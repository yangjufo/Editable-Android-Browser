package com.example.browser.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.browser.R;

/**
 * Created by Young on 2016/11/28.
 */

public class PopupWindowTools extends PopupWindow {
    private View toolsTabView;
    private Context context;
    private LayoutInflater toolsTabInflater;

    public PopupWindowTools(Context context) {
        super(context);
        this.context = context;
        this.toolsTabInflater = LayoutInflater.from(this.context);
        this.toolsTabView = this.toolsTabInflater.inflate(R.layout.pop_window_tools, null);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(toolsTabView);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    public View getView(int id) {
        return this.toolsTabView.findViewById(id);
    }
}
