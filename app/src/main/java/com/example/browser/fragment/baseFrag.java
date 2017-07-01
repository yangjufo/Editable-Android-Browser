package com.example.browser.fragment;

import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Administrator on 2016/11/28.
 */
public class baseFrag extends Fragment implements View.OnTouchListener {

    protected float point_x, point_y; //手指按下的位置
    private boolean flag;

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            point_x = event.getX();
            point_y = event.getY();
            flag = false;
        }

        return true;
    }
}