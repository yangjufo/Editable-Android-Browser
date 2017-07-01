package com.example.browser.file;

import android.app.Activity;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.browser.R;

/**
 * Created by Young on 2016/11/30.
 */

public class PageAttributesActivity extends Activity{

    //网站标题
    private TextView webPageTitle;
    //网页URL
    private TextView currentPageUrl;
    //网页大小
    private TextView webPageSize;
    //网页编码
    private TextView webPageEncoding;
    //特殊显示区域
    private RelativeLayout chooseURLlayout;

    //网页信息
    private String title;
    private String url;
    private String size;
    private String encoding;

    //属性类型
    private int type;

    @Override
    protected void onCreate(Bundle savadInstanceState) {
        super.onCreate(savadInstanceState);
        setContentView(R.layout.activity_pageattr);

        Bundle bundle = getIntent().getExtras();
        this.title = bundle.getString("title");
        this.url = bundle.getString("url");
        this.size = bundle.getString("size");
        this.encoding = bundle.getString("encoding");
        this.type = bundle.getInt("type");

        this.init(bundle);
    }

    private void init(Bundle bundle) {
        this.webPageTitle = (TextView) this.findViewById(R.id.activity_pageattr_title_value);
        this.currentPageUrl = (TextView) this.findViewById(R.id.activity_pageattr_currentPageUrl_value);
        this.webPageSize = (TextView) this.findViewById(R.id.activity_pageattr_pageOtherAttr_size_value);
        this.webPageEncoding = (TextView) this.findViewById(R.id.activity_pageattr_pageOtherAttr_currentPageEncoding_value);

        Button pageurlCopy = (Button) this.findViewById(R.id.activity_pageattr_currentPageUrl_copy);

        this.webPageTitle.setText(this.title);
        this.currentPageUrl.setText(this.url);
        this.webPageSize.setText(this.size);
        this.webPageEncoding.setText(this.encoding);

        //设置监听
        pageurlCopy.setOnClickListener(new ButtonOnClick(this.url));

         //判断特殊情况
        if (this.type != WebView.HitTestResult.UNKNOWN_TYPE) {
            //类型已知
            this.chooseURLlayout = (RelativeLayout) this.findViewById(R.id.activity_pageattr_resultShowURL);
            this.chooseURLlayout.setVisibility(View.VISIBLE);

            Button choosedURLCopy = (Button) this.findViewById(R.id.activity_pageattr_resultShowURL_copy);

            //设置值
            if ((this.type == WebView.HitTestResult.IMAGE_TYPE) || (this.type == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE)) {
                ((TextView) this.chooseURLlayout.findViewById(R.id.activity_pageattr_resultShowURL_key))
                        .setText(getResources().getString(R.string.choosedPictureURL));
            } else if (this.type == WebView.HitTestResult.SRC_ANCHOR_TYPE) {
                ((TextView) this.chooseURLlayout.findViewById(R.id.activity_pageattr_resultShowURL_key))
                        .setText(getResources().getString(R.string.choosedLinkedURL));
            }
            ((TextView) this.chooseURLlayout.findViewById(R.id.activity_pageattr_resultShowURL_value))
                    .setText(bundle.getString("typeUrl"));
            choosedURLCopy.setOnClickListener(new ButtonOnClick(bundle.getString("typeUrl")));
        }
    }

    //按钮事件
    private class ButtonOnClick implements View.OnClickListener {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        private String typeUrl;

        public ButtonOnClick(String typeUrl) {
            this.typeUrl = typeUrl;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.activity_pageattr_currentPageUrl_copy) {
                //复制当前页面URL
                clipboardManager.setText(typeUrl);
            } else if (v.getId() == R.id.activity_pageattr_resultShowURL_copy) {
                clipboardManager.setText(typeUrl);
            }
        }
    }
}
