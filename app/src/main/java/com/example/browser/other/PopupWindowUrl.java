package com.example.browser.other;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.browser.QrcodeActivity;
import com.example.browser.R;

/**
 * Created by Administrator on 2016/11/24.
 */
public class PopupWindowUrl extends PopupWindow {

    private View contentView;
    private ImageButton copyUrl;
    private ImageButton qrCode;
    private ClipboardManager clipboardManager;
    public PopupWindowUrl(final Activity context, final String url, final Bitmap bitmap) {
        final LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        contentView = inflater.inflate(R.layout.pop_window_url, null);
        copyUrl = (ImageButton) contentView.findViewById(R.id.copy_url);
        qrCode = (ImageButton) contentView.findViewById(R.id.qr_code);

        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(false);
        this.setOutsideTouchable(true);
        copyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clipboardManager = (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(ClipData.newPlainText(null, url));
                Toast.makeText(context, "网址复制成功", Toast.LENGTH_SHORT).show();
                PopupWindowUrl.this.dismiss();
            }
        });

        qrCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupWindowUrl.this.dismiss();
                Intent intent = new Intent(context, QrcodeActivity.class);
                intent.putExtra("URL", url);
                intent.putExtra("ICON", bitmap);
                context.startActivity(intent);
            }
        });
        this.update();
    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAsDropDown(parent, parent.getLayoutParams().width, 0);
        } else {
            this.dismiss();
        }
    }
}
