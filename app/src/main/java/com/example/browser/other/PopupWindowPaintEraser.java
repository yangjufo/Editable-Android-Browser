package com.example.browser.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.browser.R;

/**
 * Created by Young on 2016/12/4.
 */

public class PopupWindowPaintEraser extends PopupWindow {
    private View paintView;
    private Context context;
    private LayoutInflater paintInflater;

    public PopupWindowPaintEraser(Context context, int width) {
        super(context);
        this.context = context;
        this.paintInflater = LayoutInflater.from(this.context);
        this.paintView = this.paintInflater.inflate(R.layout.pop_window_paint_eraser, null);
        setWidth(width);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(paintView);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    public View getView(int id) {
        return this.paintView.findViewById(id);
    }
}
