package com.example.browser;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.browser.other.FavAndHisManager;
import com.example.browser.other.ItemLongClickedPopWindow;

/**
 * Created by Young on 2016/11/29.
 */

public class FavAndHisActivity extends Activity {

    public static final int RESULT_FAV_HIS = 0;
    private static final int RESULT_DEFAULT = -1;

    //初始界面书签/历史选择
    private String type;

    private View favoriteView;
    private View historyView;


    //书签历史内容
    private ListView favoriteContent;
    private ListView historyContent;

    //长按弹窗
    private ItemLongClickedPopWindow itemLongClickedPopWindow;

    //书签历史管理
    private FavAndHisManager favAndHisManager;

    //Cursor
    private Cursor favAndHisCursor;

    //Adapter
    private ListAdapter favAndHisAdapter;

    private ListViewOnItemLongListener itemLongListener;
    private ListViewOnItemClickedListener itemClickedListener;

    private boolean itemLongClickedFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //初始化
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        type = bundle.getString("type");

        LayoutInflater favAndHisInflater = LayoutInflater.from(getApplicationContext());
        favoriteView = favAndHisInflater.inflate(R.layout.view_favorite, null);
        historyView = favAndHisInflater.inflate(R.layout.view_history, null);


        //显示初始界面
        if (type.equals("favorite")) {
            setContentView(favoriteView);
            this.favoriteContent = (ListView) this.findViewById(R.id.favorites_item_content);
            this.historyContent = (ListView) historyView.findViewById(R.id.histories_item_content);
        } else if (type.equals("history")) {
            setContentView(historyView);
            this.favoriteContent = (ListView) favoriteView.findViewById(R.id.favorites_item_content);
            this.historyContent = (ListView) this.findViewById(R.id.histories_item_content);
        }

        this.itemLongListener = new ListViewOnItemLongListener();
        this.itemClickedListener = new ListViewOnItemClickedListener();

        //添加监听
        this.favoriteContent.setOnItemLongClickListener(this.itemLongListener);
        this.historyContent.setOnItemLongClickListener(this.itemLongListener);

        this.favoriteContent.setOnItemClickListener(this.itemClickedListener);
        this.historyContent.setOnItemClickListener(this.itemClickedListener);

        this.itemLongClickedFlag = false;

        //初始化数据
        this.initDataFavorites();
        this.initDataHistory();

        //添加默认返回值
        setResult(RESULT_DEFAULT);
    }

    //初始化ListView中的数据
    @SuppressWarnings("deprecation")
    private void initDataFavorites() {
        //获取书签管理
        this.favAndHisManager = new FavAndHisManager(this);
        this.favAndHisCursor = this.favAndHisManager.getAllFavorites();
        this.favAndHisAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.list_item, this.favAndHisCursor,
                new String[]{"_id", "name", "url"},
                new int[]{R.id.item_id, R.id.item_name, R.id.item_url});
        this.favoriteContent.setAdapter(this.favAndHisAdapter);
    }

    //初始化ListView中History的数据
    @SuppressWarnings("deprecation")
    private void initDataHistory() {
        //获取历史管理
        this.favAndHisManager = new FavAndHisManager(this);
        this.favAndHisCursor = this.favAndHisManager.getAllHistory();
        this.favAndHisAdapter = new SimpleCursorAdapter(getApplicationContext(),
                R.layout.list_item, this.favAndHisCursor,
                new String[]{"_id", "name", "url", "date"},
                new int[]{R.id.item_id, R.id.item_name, R.id.item_url, R.id.item_date});
        this.historyContent.setAdapter(this.favAndHisAdapter);
    }

    //长按单项事件
    private class ListViewOnItemLongListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {
            if (parent.getId() == R.id.favorites_item_content) {
                itemLongClickedFlag = true;
                itemLongClickedPopWindow = new ItemLongClickedPopWindow(FavAndHisActivity.this,
                        ItemLongClickedPopWindow.FAVORITES_ITEM_POPUPWINDOW);
                itemLongClickedPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.favandhis_activity));
                itemLongClickedPopWindow.showAsDropDown(view, view.getWidth() / 2, -view.getHeight() / 2);
                TextView modifyFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_modifyFavorites);
                TextView deleteFavorite = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteFavorites);
                ItemClickedListener itemClickedListener = new ItemClickedListener(view);
                modifyFavorite.setOnClickListener(itemClickedListener);
                deleteFavorite.setOnClickListener(itemClickedListener);
            } else if (parent.getId() == R.id.histories_item_content) {
                itemLongClickedPopWindow = new ItemLongClickedPopWindow(FavAndHisActivity.this,
                        ItemLongClickedPopWindow.HISTORY_ITEM_POPUPWINDOW);
                itemLongClickedPopWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.favandhis_activity));
                itemLongClickedPopWindow.showAsDropDown(view, view.getWidth()/2, -view.getHeight()/2 );
                TextView deleteHistory = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteHistory);
                TextView deleteAllHistories = (TextView) itemLongClickedPopWindow.getView(R.id.item_longclicked_deleteAllHistories);
                ItemClickedListener itemClickedListener = new ItemClickedListener(view);
                deleteHistory.setOnClickListener(itemClickedListener);
                deleteAllHistories.setOnClickListener(itemClickedListener);
                itemLongClickedFlag = true;
            }
            return false;
        }
    }

    //ListView单击事件
    private class ListViewOnItemClickedListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            if (arg0.getId() == R.id.favorites_item_content && (!itemLongClickedFlag ||
                    (itemLongClickedFlag && !itemLongClickedPopWindow.isShowing()))) {
                itemLongClickedFlag = false;
                Intent intent = new Intent();
                intent.putExtra("url", ((TextView) arg1.findViewById(R.id.item_url)).getText().toString());
                setResult(FavAndHisActivity.RESULT_FAV_HIS, intent);
                finish();
            } else if (arg0.getId() == R.id.histories_item_content && (!itemLongClickedFlag ||
                    (itemLongClickedFlag && !itemLongClickedPopWindow.isShowing()))) {
                itemLongClickedFlag = false;
                Intent intent = new Intent();
                intent.putExtra("url", ((TextView) arg1.findViewById(R.id.item_url)).getText().toString());
                setResult(FavAndHisActivity.RESULT_FAV_HIS, intent);
                finish();
            }
        }
    }


    //popupwindow按钮事件
    private class ItemClickedListener implements View.OnClickListener {

        private String item_id;
        private String item_name;
        private String item_url;

        public ItemClickedListener(View item) {
            this.item_id = ((TextView) item.findViewById(R.id.item_id)).getText().toString();
            this.item_name = ((TextView) item.findViewById(R.id.item_name)).getText().toString();
            this.item_url = ((TextView) item.findViewById(R.id.item_url)).getText().toString();
        }

        @Override
        public void onClick(View view) {
            //取消弹窗
            itemLongClickedPopWindow.dismiss();
            itemLongClickedFlag = false;
            if (view.getId() == R.id.item_longclicked_modifyFavorites) {
                //弹出修改窗口
                LayoutInflater modifyFavoritesInflater = LayoutInflater.from(FavAndHisActivity.this);
                View modifyFavoritesView = modifyFavoritesInflater.inflate(R.layout.dialog_modify, null);
                final TextView item_name_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_name_input);
                final TextView item_url_input = (TextView) modifyFavoritesView.findViewById(R.id.dialog_url_input);
                item_name_input.setText(item_name);
                item_url_input.setText(item_url);
                new AlertDialog.Builder(FavAndHisActivity.this)
                        .setTitle("编辑书签")
                        .setView(modifyFavoritesView)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int width) {
                                if (favAndHisManager.modifyFavorite(item_id, item_name_input.getText().toString(),
                                        item_url_input.getText().toString())) {
                                    Toast.makeText(FavAndHisActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                                    initDataFavorites();
                                    favoriteContent.invalidate();
                                } else {
                                    Toast.makeText(FavAndHisActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            } else if (view.getId() == R.id.item_longclicked_deleteFavorites) {
                new AlertDialog.Builder(FavAndHisActivity.this)
                        .setTitle("删除书签")
                        .setMessage("是否要删除\"" + item_name + "\"这个书签？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int width) {
                                if (favAndHisManager.deleteFavorite(item_id)) {
                                    //删除成功
                                    Toast.makeText(FavAndHisActivity.this, "删除成功", Toast.LENGTH_SHORT);
                                    initDataFavorites();
                                    favoriteContent.invalidate();
                                } else {
                                    Toast.makeText(FavAndHisActivity.this, "删除失败", Toast.LENGTH_SHORT);
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            } else if (view.getId() == R.id.item_longclicked_deleteHistory) {
                new AlertDialog.Builder(FavAndHisActivity.this)
                        .setTitle("删除历史")
                        .setMessage("是否要删除\"" + item_name + "\"这个历史？")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (favAndHisManager.deleteHistory(item_id)) {
                                    //删除成功
                                    Toast.makeText(FavAndHisActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    initDataHistory();
                                    historyContent.invalidate();
                                } else {
                                    Toast.makeText(FavAndHisActivity.this, "删除失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            } else if (view.getId() == R.id.item_longclicked_deleteAllHistories) {
                new AlertDialog.Builder(FavAndHisActivity.this)
                        .setTitle("清空历史")
                        .setMessage("是否要清空历史？")
                        .setPositiveButton("清空", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (favAndHisManager.deleteAllHistories()) {
                                    //删除成功
                                    Toast.makeText(FavAndHisActivity.this, "成功清空", Toast.LENGTH_SHORT).show();
                                    initDataHistory();
                                    historyContent.invalidate();
                                } else {
                                    Toast.makeText(FavAndHisActivity.this, "清空失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create()
                        .show();
            }
        }
    }

    @Override
    public void finish() {
        if (this.favAndHisCursor != null) {
            this.favAndHisCursor.close();
        }
        super.finish();
    }

    @Override
    protected  void onDestroy() {
        if (this.favAndHisCursor != null) {
            this.favAndHisCursor.close();
        }
        super.onDestroy();
    }
}

