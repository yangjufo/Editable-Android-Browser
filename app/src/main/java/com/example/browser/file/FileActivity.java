package com.example.browser.file;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.browser.MainActivity;
import com.example.browser.R;
import com.example.browser.other.ItemLongClickedPopWindow;

import java.io.File;

/**
 * Created by Young on 2016/11/30.
 */

public class FileActivity extends Activity {

    public static final int RESULT_FILEMANAGER = 1;
    public static final String SDCARD_HOME = Environment.getExternalStorageDirectory().toString();

    //文件列表
    private ListView fileList;

    //确定取消按钮
    private TextView sure;
    private TextView cancel;

    //新建目录按钮
    private Button createNewFolder;

    //监听器
    private FileManagerOnItemListener fileManagerOnItemListener;
    private FileManagerOnClickListener fileManagerOnClickListener;
    private FileManagerOnItemLongListener fileManagerOnItemLongListener;
    private FileListPopWindowMenu fileListPopWindowMenu;

    //长按文件列表单项弹出菜单
    private ItemLongClickedPopWindow fileListItemLongClickPopWindow;

    //文件管理线程类
    private FileShowManager fileManager;

    //当前路径
    private String currentPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filemanager);

        //初始化
        this.fileList = (ListView) this.findViewById(R.id.fileManager_list);
        this.sure = (TextView) this.findViewById(R.id.fileManager_toolbar_sure);
        this.cancel = (TextView) this.findViewById(R.id.fileManager_toolbar_cancel);

        this.createNewFolder = (Button) this.findViewById(R.id.fileManager_title_newDirectory);

        this.fileManager = new FileShowManager(this, this.fileList);
        this.fileManagerOnClickListener = new FileManagerOnClickListener();
        this.fileManagerOnItemListener = new FileManagerOnItemListener();
        this.fileManagerOnItemLongListener = new FileManagerOnItemLongListener();

        //注册监听
        this.fileList.setOnItemClickListener(this.fileManagerOnItemListener);
        this.fileList.setOnItemLongClickListener(this.fileManagerOnItemLongListener);

        this.sure.setOnClickListener(this.fileManagerOnClickListener);
        this.cancel.setOnClickListener(this.fileManagerOnClickListener);
        this.createNewFolder.setOnClickListener(this.fileManagerOnClickListener);

        //启动文件查找线程
        currentPath = getIntent().getStringExtra("savePath");
        this.fileManager.execute(currentPath);
    }


    //单击事件
    private class FileManagerOnItemListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view,
                                int position, long id) {
            TextView path = (TextView) view.findViewById(R.id.filemanager_item_filePath);
            currentPath = path.getText().toString();
            fileManager = new FileShowManager(FileActivity.this, fileList);
            fileManager.execute(currentPath);
        }
    }

    //长按事件
    private class FileManagerOnItemLongListener implements AdapterView.OnItemLongClickListener {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                int position, long id) {
            TextView date = (TextView) view.findViewById(R.id.filemanager_item_info_numsAndDate_date);
            if (date.getText().toString().equals("")) {
                return false;
            }
            fileListItemLongClickPopWindow = new ItemLongClickedPopWindow(FileActivity.this, ItemLongClickedPopWindow.FILEMANAGER_ITEM_POPUPWINDOW);
            fileListItemLongClickPopWindow.showAsDropDown(view, view.getWidth()/2, -view.getHeight()/2);
            TextView deleteFolder = (TextView) fileListItemLongClickPopWindow.getView(R.id.item_longclicked_deleteFloder);
            TextView newNameForFolder = (TextView) fileListItemLongClickPopWindow.getView(R.id.item_longclicked_newNameForFolder);
            fileListPopWindowMenu = new FileListPopWindowMenu(view);
            deleteFolder.setOnClickListener(fileListPopWindowMenu);
            newNameForFolder.setOnClickListener(fileListPopWindowMenu);
            return true;
        }
    }

    //长按弹窗按钮事件
    private class FileListPopWindowMenu implements View.OnClickListener {

        private View beLongClickedView;

        public FileListPopWindowMenu(View beLongClickedView) {
            this.beLongClickedView = beLongClickedView;
        }

        @Override
        public void onClick(View v) {
            fileListItemLongClickPopWindow.dismiss();
            TextView folderPath = (TextView) beLongClickedView.findViewById(R.id.filemanager_item_filePath);
            TextView oldFolderName = (TextView) beLongClickedView.findViewById(R.id.filemanager_item_info_name);
            final String folderPathStr = folderPath.getText().toString();
            if (v.getId() == R.id.item_longclicked_deleteFloder) {
                //删除文件夹
                new AlertDialog.Builder(FileActivity.this)
                        .setTitle("Delete directory")
                        .setMessage("Delete\""+folderPathStr+"\"?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                File deleteDictory = new File(folderPathStr);
                                if (deleteDictory.exists()) {
                                    deleteDictory.delete();
                                    fileManager = new FileShowManager(FileActivity.this, fileList);
                                    fileManager.execute(currentPath);
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            } else if (v.getId() == R.id.item_longclicked_newNameForFolder) {
                //文件夹重命名
                View newNameForFolderView = LayoutInflater.from(FileActivity.this).inflate(R.layout.dialog_newnameforfolder, null);
                final TextView folderName = (TextView) newNameForFolderView.findViewById(R.id.dialog_newNameForFloder_floderName);
                folderName.setText(oldFolderName.getText().toString());
                new AlertDialog.Builder(FileActivity.this)
                        .setTitle("Rename directory")
                        .setView(newNameForFolderView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String newFolderName = folderName.getText().toString();
                                File newNameFolder = new File(folderPathStr);
                                newNameFolder.renameTo(new java.io.File(currentPath+"/"+newFolderName));
                                fileManager = new FileShowManager(FileActivity.this, fileList);
                                fileManager.execute(currentPath);
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        }
    }

    //单击事件
    private class FileManagerOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.fileManager_toolbar_sure) {
                //确定操作，返回确定的url
                Intent intentExtraUrl = new Intent();
                intentExtraUrl.putExtra("savePath", currentPath);
                setResult(RESULT_FILEMANAGER, intentExtraUrl);
                finish();
            } else if (v.getId() == R.id.fileManager_toolbar_cancel) {
                //取消操作，不更改url
                setResult(MainActivity.REQUEST_DEFAULT);
                finish();
            } else if (v.getId() == R.id.fileManager_title_newDirectory) {
                //新建目录
                View createNewFolderView = LayoutInflater.from(FileActivity.this).inflate(R.layout.dialog_createnewfolder, null);
                final EditText newFolderName = (EditText) createNewFolderView.findViewById(R.id.dialog_createNewFloder_floderName);
                new AlertDialog.Builder(FileActivity.this)
                        .setTitle("Create new directory")
                        .setView(createNewFolderView)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String createNewPath = currentPath+"/"+newFolderName.getText().toString();
                                File file = new File(createNewPath);
                                if (!file.exists()) {
                                    file.mkdir();
                                    fileManager = new FileShowManager(FileActivity.this, fileList);
                                    fileManager.execute(currentPath);
                                } else {
                                    Toast.makeText(FileActivity.this, "Name exists", Toast.LENGTH_SHORT).show();

                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create()
                        .show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!currentPath.equals(SDCARD_HOME)) {
            int endIndex = currentPath.lastIndexOf("/");
            currentPath = currentPath.substring(0, endIndex);
            fileManager = new FileShowManager(FileActivity.this, fileList);
            fileManager.execute(currentPath);
        } else {
            setResult(MainActivity.REQUEST_DEFAULT);
            super.onBackPressed();
        }

    }


}
