package com.even.sample;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.even.mricheditor.ActionType;
import com.even.mricheditor.RichEditorAction;
import com.even.mricheditor.RichEditorCallback;
import com.even.sample.fragment.EditHyperlinkFragment;
import com.even.sample.fragment.EditTableFragment;
import com.even.sample.fragment.EditorMenuFragment;
import com.even.sample.interfaces.OnActionPerformListener;
import com.even.sample.keyboard.KeyboardHeightObserver;
import com.even.sample.keyboard.KeyboardHeightProvider;
import com.even.sample.keyboard.KeyboardUtils;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import java.util.ArrayList;

@SuppressLint("SetJavaScriptEnabled") public class RichEditorActivity extends AppCompatActivity
    implements KeyboardHeightObserver {
    @BindView(R.id.wv_container) WebView mWebView;
    @BindView(R.id.fl_action) FrameLayout flAction;

    /** The keyboard height provider */
    private KeyboardHeightProvider keyboardHeightProvider;
    private boolean isKeyboardShowing;

    private RichEditorAction mRichEditorAction;
    private RichEditorCallback mRichEditorCallback;

    private EditorMenuFragment mEditorMenuFragment;

    @BindView(R.id.iv_action_bold) ImageView ivBold;
    @BindView(R.id.iv_action_italic) ImageView ivItalic;
    @BindView(R.id.iv_action_underline) ImageView ivUnderline;
    @BindView(R.id.iv_action_strikethrough) ImageView ivStrikethrough;

    @BindView(R.id.iv_action_justify_left) ImageView ivJustifyLeft;
    @BindView(R.id.iv_action_justify_center) ImageView ivJustifyCenter;
    @BindView(R.id.iv_action_justify_right) ImageView ivJustifyRight;
    @BindView(R.id.iv_action_justify_full) ImageView ivJustifyFull;

    @BindView(R.id.iv_action_insert_numbers) ImageView ivOrdered;
    @BindView(R.id.iv_action_insert_bullets) ImageView ivUnOrdered;

    @BindView(R.id.iv_action_indent) ImageView ivIndent;
    @BindView(R.id.iv_action_outdent) ImageView ivOutdent;

    @BindView(R.id.iv_action_subscript) ImageView ivSubScript;
    @BindView(R.id.iv_action_superscript) ImageView ivSuperScript;

    @BindView(R.id.iv_action_insert_image) ImageView ivImage;
    @BindView(R.id.iv_action_insert_link) ImageView ivLink;
    @BindView(R.id.iv_action_table) ImageView ivTable;
    @BindView(R.id.iv_action_line) ImageView ivLine;

    @BindView(R.id.iv_action_blockquote) ImageView ivBlockQuote;
    @BindView(R.id.iv_action_code_block) ImageView ivCodeBlock;

    @BindView(R.id.iv_action_code_view) ImageView ivCodeView;

    private static final int REQUEST_CODE_CHOOSE = 0;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initImageLoader();
        initView();

        mEditorMenuFragment = new EditorMenuFragment();
        mEditorMenuFragment.setActionClickListener(new MOnActionPerformListener(mRichEditorAction));
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction()
            .add(R.id.fl_action, mEditorMenuFragment, EditorMenuFragment.class.getName())
            .commit();
    }

    /**
     * ImageLoader for insert Image
     */
    private void initImageLoader() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());
        imagePicker.setShowCamera(true);
        imagePicker.setCrop(false);
        imagePicker.setMultiMode(false);
        imagePicker.setSaveRectangle(true);
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);
        imagePicker.setFocusWidth(800);
        imagePicker.setFocusHeight(800);
        imagePicker.setOutPutX(256);
        imagePicker.setOutPutY(256);
    }

    private void initView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDomStorageEnabled(true);
        mRichEditorCallback = new MRichEditorCallback();
        mWebView.addJavascriptInterface(mRichEditorCallback, "MRichEditor");
        mWebView.loadUrl("file:///android_asset/richEditor.html");
        mRichEditorAction = new RichEditorAction(mWebView);

        keyboardHeightProvider = new KeyboardHeightProvider(this);
        findViewById(R.id.fl_container).post(new Runnable() {
            @Override public void run() {
                keyboardHeightProvider.start();
            }
        });
    }

    @OnClick(R.id.iv_action) void onClickAction() {
        if (flAction.getVisibility() == View.VISIBLE) {
            flAction.setVisibility(View.GONE);
        } else {
            if (isKeyboardShowing) {
                KeyboardUtils.hideSoftInput(RichEditorActivity.this);
            }
            flAction.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.iv_action_undo) void onClickUndo() {
        mRichEditorAction.undo();
    }

    @OnClick(R.id.iv_action_redo) void onClickRedo() {
        mRichEditorAction.redo();
    }

    @OnClick(R.id.iv_action_bold) void onClickBold() {
        mRichEditorAction.bold();
    }

    @OnClick(R.id.iv_action_italic) void onClickItalic() {
        mRichEditorAction.italic();
    }

    @OnClick(R.id.iv_action_subscript) void onClickSubscript() {
        mRichEditorAction.subscript();
    }

    @OnClick(R.id.iv_action_superscript) void onClickSuperscript() {
        mRichEditorAction.superscript();
    }

    @OnClick(R.id.iv_action_strikethrough) void onClickStrikethrough() {
        mRichEditorAction.strikethrough();
    }

    @OnClick(R.id.iv_action_underline) void onClickUnderline() {
        mRichEditorAction.underline();
    }

    @OnClick(R.id.iv_action_heading1) void onClickH1() {
        mRichEditorAction.formatH1();
    }

    @OnClick(R.id.iv_action_heading2) void onClickH2() {
        mRichEditorAction.formatH2();
    }

    @OnClick(R.id.iv_action_heading3) void onClickH3() {
        mRichEditorAction.formatH3();
    }

    @OnClick(R.id.iv_action_heading4) void onClickH4() {
        mRichEditorAction.formatH4();
    }

    @OnClick(R.id.iv_action_heading5) void onClickH5() {
        mRichEditorAction.formatH5();
    }

    @OnClick(R.id.iv_action_heading6) void onClickH6() {
        mRichEditorAction.formatH6();
    }

    @OnClick(R.id.iv_action_txt_color) void onClickTextColor() {
        mRichEditorAction.foreColor("blue");
    }

    @OnClick(R.id.iv_action_txt_bg_color) void onClickHighlight() {
        mRichEditorAction.backColor("red");
    }

    @OnClick(R.id.iv_action_indent) void onClickIndent() {
        mRichEditorAction.indent();
    }

    @OnClick(R.id.iv_action_outdent) void onClickOutdent() {
        mRichEditorAction.outdent();
    }

    @OnClick(R.id.iv_action_justify_left) void onClickJustifyLeft() {
        mRichEditorAction.justifyLeft();
    }

    @OnClick(R.id.iv_action_justify_center) void onClickJustifyCenter() {
        mRichEditorAction.justifyCenter();
    }

    @OnClick(R.id.iv_action_justify_right) void onClickJustifyRight() {
        mRichEditorAction.justifyRight();
    }

    @OnClick(R.id.iv_action_justify_full) void onClickJustifyFull() {
        mRichEditorAction.justifyFull();
    }

    @OnClick(R.id.iv_action_insert_bullets) void onClickUnOrdered() {
        mRichEditorAction.insertUnorderedList();
    }

    @OnClick(R.id.iv_action_insert_numbers) void onClickOrdered() {
        mRichEditorAction.insertOrderedList();
    }

    @OnClick(R.id.iv_action_code_view) void onClickCodeView() {
        mRichEditorAction.codeReview();
    }

    @OnClick(R.id.iv_action_line_height) void onClickLineHeight() {
        mRichEditorAction.lineHeight(20);
    }

    @OnClick(R.id.iv_action_insert_image) void onClickInsertImage() {
        Intent intent = new Intent(this, ImageGridActivity.class);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }

    @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS
            && data != null
            && requestCode == REQUEST_CODE_CHOOSE) {
            ArrayList<ImageItem> images =
                (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
            if (images != null && !images.isEmpty()) {
                //Upload the image,and return the URL
                mRichEditorAction.insertImage(
                    "https://avatars0.githubusercontent.com/u/5581118?v=4&u=b7ea903e397678b3675e2a15b0b6d0944f6f129e&s=400");
            }
        }
    }

    @OnClick(R.id.iv_action_insert_link) void onClickInsertLink() {
        KeyboardUtils.hideSoftInput(RichEditorActivity.this);
        EditHyperlinkFragment fragment = new EditHyperlinkFragment();
        fragment.setOnHyperlinkListener(new EditHyperlinkFragment.OnHyperlinkListener() {
            @Override public void onHyperlinkOK(String address, String text) {
                mRichEditorAction.createLink(text, address);
            }
        });
        getSupportFragmentManager().beginTransaction()
            .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
            .commit();
    }

    @OnClick(R.id.iv_action_table) void onClickInsertTable() {
        KeyboardUtils.hideSoftInput(RichEditorActivity.this);
        EditTableFragment fragment = new EditTableFragment();
        fragment.setOnTableListener(new EditTableFragment.OnTableListener() {
            @Override public void onTableOK(int rows, int cols) {
                mRichEditorAction.insertTable(rows, cols);
            }
        });
        getSupportFragmentManager().beginTransaction()
            .add(R.id.fl_container, fragment, EditHyperlinkFragment.class.getName())
            .commit();
    }

    @OnClick(R.id.iv_action_line) void onClickInsertLine() {
        mRichEditorAction.insertHorizontalRule();
    }

    @OnClick(R.id.iv_action_blockquote) void onClickBlockQuote() {
        mRichEditorAction.formatBlockquote();
    }

    @OnClick(R.id.iv_action_code_block) void onClickCodeBlock() {
        mRichEditorAction.formatBlockCode();
    }

    @Override public void onResume() {
        super.onResume();
        keyboardHeightProvider.setKeyboardHeightObserver(this);
    }

    @Override public void onPause() {
        super.onPause();
        keyboardHeightProvider.setKeyboardHeightObserver(null);
        if (flAction.getVisibility() == View.INVISIBLE) {
            flAction.setVisibility(View.GONE);
        }
    }

    @Override public void onDestroy() {
        super.onDestroy();
        keyboardHeightProvider.close();
    }

    @Override public void onKeyboardHeightChanged(int height, int orientation) {
        isKeyboardShowing = height > 0;
        if (height != 0) {
            flAction.setVisibility(View.INVISIBLE);
            ViewGroup.LayoutParams params = flAction.getLayoutParams();
            params.height = height;
            flAction.setLayoutParams(params);
        } else if (flAction.getVisibility() != View.VISIBLE) {
            flAction.setVisibility(View.GONE);
        }
    }

    class MRichEditorCallback extends RichEditorCallback {

        @Override public void notifyFontStyleChange(ActionType type, final String value) {
            switch (type) {
                case FAMILY:
                    mEditorMenuFragment.updateFontFamilyStates(value);
                    break;
                case SIZE:
                    mEditorMenuFragment.updateFontStates(ActionType.SIZE, Double.valueOf(value));
                    break;
                case FORE_COLOR:
                case BACK_COLOR:
                    mEditorMenuFragment.updateFontColorStates(type, value);
                    break;
                case LINE_HEIGHT:
                    mEditorMenuFragment.updateFontStates(ActionType.LINE_HEIGHT,
                        Double.valueOf(value));
                    break;
                case JUSTIFY_LEFT:
                case JUSTIFY_CENTER:
                case JUSTIFY_RIGHT:
                case JUSTIFY_FULL:
                    updateJustifyStates(type);
                    break;
                case BOLD:
                case ITALIC:
                case UNDERLINE:
                case SUBSCRIPT:
                case SUPERSCRIPT:
                case STRIKETHROUGH:
                    updateButtonStates(type, Boolean.valueOf(value));
                    break;
                case NORMAL:
                case H1:
                case H2:
                case H3:
                case H4:
                case H5:
                case H6:
                case STYLE_NONE:
                    mEditorMenuFragment.updateStyleStates(type);
                    break;
                case ORDERED:
                case UNORDERED:
                case LIST_STYLE_NONE:
                    updateListStyleStates(type);
                    break;
                default:
                    break;
            }
        }
    }

    private void updateJustifyStates(ActionType type) {
        mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_LEFT,
            type == ActionType.JUSTIFY_LEFT);
        mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_CENTER,
            type == ActionType.JUSTIFY_CENTER);
        mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_RIGHT,
            type == ActionType.JUSTIFY_RIGHT);
        mEditorMenuFragment.updateActionStates(ActionType.JUSTIFY_FULL,
            type == ActionType.JUSTIFY_FULL);

        updateActionItemUI(ivJustifyLeft, type == ActionType.JUSTIFY_LEFT);
        updateActionItemUI(ivJustifyCenter, type == ActionType.JUSTIFY_CENTER);
        updateActionItemUI(ivJustifyRight, type == ActionType.JUSTIFY_RIGHT);
        updateActionItemUI(ivJustifyFull, type == ActionType.JUSTIFY_FULL);
    }

    private void updateListStyleStates(ActionType type) {
        mEditorMenuFragment.updateActionStates(ActionType.UNORDERED, type == ActionType.UNORDERED);
        mEditorMenuFragment.updateActionStates(ActionType.ORDERED, type == ActionType.ORDERED);

        updateActionItemUI(ivUnOrdered, type == ActionType.UNORDERED);
        updateActionItemUI(ivOrdered, type == ActionType.ORDERED);
    }

    private void updateButtonStates(ActionType type, boolean isActive) {
        mEditorMenuFragment.updateActionStates(type, isActive);

        switch (type) {
            case BOLD:
                updateActionItemUI(ivBold, isActive);
                break;
            case ITALIC:
                updateActionItemUI(ivItalic, isActive);
                break;
            case UNDERLINE:
                updateActionItemUI(ivUnderline, isActive);
                break;
            case SUBSCRIPT:
                updateActionItemUI(ivSubScript, isActive);
                break;
            case SUPERSCRIPT:
                updateActionItemUI(ivSuperScript, isActive);
                break;
            case STRIKETHROUGH:
                updateActionItemUI(ivStrikethrough, isActive);
                break;
            case CODEVIEW:
                updateActionItemUI(ivCodeView, isActive);
                break;
            default:
                break;
        }
    }

    private void updateActionItemUI(final ImageView iv, final boolean isActive) {
        mWebView.post(new Runnable() {
            @Override public void run() {
                iv.setColorFilter(ContextCompat.getColor(RichEditorActivity.this,
                    isActive ? R.color.colorAccent : R.color.tintColor));
            }
        });
    }

    public class MOnActionPerformListener implements OnActionPerformListener {
        private RichEditorAction mRichEditorAction;

        public MOnActionPerformListener(RichEditorAction mRichEditorAction) {
            this.mRichEditorAction = mRichEditorAction;
        }

        @Override public void onActionPerform(ActionType type, Object... values) {
            if (mRichEditorAction == null) {
                return;
            }

            String value = null;
            if (values != null && values.length > 0) {
                value = (String) values[0];
            }

            switch (type) {
                case SIZE:
                    mRichEditorAction.fontSize(Double.valueOf(value));
                    break;
                case LINE_HEIGHT:
                    mRichEditorAction.lineHeight(Double.valueOf(value));
                    break;
                case TEXT_COLOR:
                    mRichEditorAction.foreColor(value);
                    break;
                case HIGHLIGHT:
                    mRichEditorAction.backColor(value);
                    break;
                case FAMILY:
                    mRichEditorAction.fontName(value);
                    break;
                case BOLD:
                    mRichEditorAction.bold();
                    break;
                case ITALIC:
                    mRichEditorAction.italic();
                    break;
                case UNDERLINE:
                    mRichEditorAction.underline();
                    break;
                case SUBSCRIPT:
                    mRichEditorAction.subscript();
                    break;
                case SUPERSCRIPT:
                    mRichEditorAction.superscript();
                    break;
                case STRIKETHROUGH:
                    mRichEditorAction.strikethrough();
                    break;
                case JUSTIFY_LEFT:
                    mRichEditorAction.justifyLeft();
                    break;
                case JUSTIFY_CENTER:
                    mRichEditorAction.justifyCenter();
                    break;
                case JUSTIFY_RIGHT:
                    mRichEditorAction.justifyRight();
                    break;
                case JUSTIFY_FULL:
                    mRichEditorAction.justifyFull();
                    break;
                case CODEVIEW:
                    mRichEditorAction.codeReview();
                    break;
                case ORDERED:
                    mRichEditorAction.insertOrderedList();
                    break;
                case UNORDERED:
                    mRichEditorAction.insertUnorderedList();
                    break;
                case INDENT:
                    mRichEditorAction.indent();
                    break;
                case OUTDENT:
                    mRichEditorAction.outdent();
                    break;
                case IMAGE:
                    ivImage.performClick();
                    break;
                case LINK:
                    ivLink.performClick();
                    break;
                case TABLE:
                    ivTable.performClick();
                    break;
                case LINE:
                    ivLine.performClick();
                    break;
                case BLOCKQUOTE:
                    mRichEditorAction.formatBlockquote();
                    break;
                case CODE_BLOCK:
                    mRichEditorAction.formatBlockCode();
                    break;
                case NORMAL:
                    mRichEditorAction.formatPara();
                    break;
                case H1:
                    mRichEditorAction.formatH1();
                    break;
                case H2:
                    mRichEditorAction.formatH2();
                    break;
                case H3:
                    mRichEditorAction.formatH3();
                    break;
                case H4:
                    mRichEditorAction.formatH4();
                    break;
                case H5:
                    mRichEditorAction.formatH5();
                    break;
                case H6:
                    mRichEditorAction.formatH6();
                    break;
                default:
                    break;
            }
        }
    }
}
