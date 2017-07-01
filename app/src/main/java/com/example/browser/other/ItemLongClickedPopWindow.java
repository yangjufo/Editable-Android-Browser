package com.example.browser.other;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.example.browser.R;

/**
 * Created by Young on 2016/11/29.
 */

public class ItemLongClickedPopWindow extends PopupWindow {

    //书签条目弹出菜单
    public static final int FAVORITES_ITEM_POPUPWINDOW = 0;

    //书签页面弹出菜单
    public static final int FAVORITES_VIEW_POPUPWINDOW = 1;

    //历史条目弹出菜单
    public static final int HISTORY_ITEM_POPUPWINDOW = 3;

    //历史页面弹出菜单
    public static final int HISTORY_VIEW_POPUPWINDOW = 4;

    // 图片项目弹出菜单
    public static final int IMAGE_VIEW_POPUPWINDOW = 5;

    //超链接项目弹出菜单
    public static final int ACHOR_VIEW_POPUPWINDOW = 6;

    //文件夹管理项目弹出菜单
    public static final int FILEMANAGER_ITEM_POPUPWINDOW = 7;

    private LayoutInflater itemLongClickedPopWindowInflater;
    private View itemLongClickedPopWindowView;
    private Context context;

    private int type;

    //构造函数
    public ItemLongClickedPopWindow(Context context, int type) {
        super(context);
        this.context = context;
        this.type = type;

        this.initTab();

        //设置默认选项
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setContentView(this.itemLongClickedPopWindowView);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    //实例化
    private void initTab() {
        this.itemLongClickedPopWindowInflater = LayoutInflater.from(this.context);
        switch (type) {
            case FAVORITES_ITEM_POPUPWINDOW:
                this.itemLongClickedPopWindowView = this.itemLongClickedPopWindowInflater.inflate(R.layout.list_item_longclicked_favoirtes, null);
                break;
            case FAVORITES_VIEW_POPUPWINDOW:
                //TODO:add favorites view popwindow
                break;
            case HISTORY_ITEM_POPUPWINDOW:
                this.itemLongClickedPopWindowView = this.itemLongClickedPopWindowInflater.inflate(R.layout.list_item_longclicked_history, null);
                break;
            case HISTORY_VIEW_POPUPWINDOW:
                //TODO:add history view popupwindow
                break;
            case ACHOR_VIEW_POPUPWINDOW:
                this.itemLongClickedPopWindowView = this.itemLongClickedPopWindowInflater.inflate(R.layout.list_item_longclicked_achor, null);
                break;
            case IMAGE_VIEW_POPUPWINDOW:
                this.itemLongClickedPopWindowView = this.itemLongClickedPopWindowInflater.inflate(R.layout.list_item_longclicked_img, null);
                break;
            case FILEMANAGER_ITEM_POPUPWINDOW:
                this.itemLongClickedPopWindowView = this.itemLongClickedPopWindowInflater.inflate(R.layout.list_item_longclicked_filelist, null);
                break;
        }
    }
     public View getView(int id) {
         return this.itemLongClickedPopWindowView.findViewById(id);

     }
}
