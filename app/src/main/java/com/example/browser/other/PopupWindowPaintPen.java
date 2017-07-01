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

public class PopupWindowPaintPen extends PopupWindow {
    private View paintView;
    private Context context;
    private LayoutInflater paintInflater;

    public PopupWindowPaintPen(Context context, int width, int penChoose) {
        super(context);
        this.context = context;
        this.paintInflater = LayoutInflater.from(this.context);
        switch (penChoose) {
            case 1:
                this.paintView = this.paintInflater.inflate(R.layout.pop_window_paint_pen1, null);
                break;
            case 2:
                this.paintView = this.paintInflater.inflate(R.layout.pop_window_paint_pen2, null);
                break;
            case 3:
                this.paintView = this.paintInflater.inflate(R.layout.pop_window_paint_pen3, null);
                break;
            default:
                break;
        }
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
