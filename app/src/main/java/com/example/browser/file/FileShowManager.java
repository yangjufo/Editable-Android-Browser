package com.example.browser.file;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.example.browser.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Created by Young on 2016/11/30.
 */

public class FileShowManager extends AsyncTask<String, String, List<HashMap<String, Object>>> {

    public static final String SDCARD_HOME = Environment.getExternalStorageDirectory().toString();
    public static final String DEFAULT_PATH = SDCARD_HOME + "/webbrowserX/download/";

    public enum SROTTYPE {
        LETTER, DATE, CHILDNUMS
    }

    private Activity activity;
    private ListView fileList;
    private Dialog waitDialog;

    public FileShowManager(Activity activity, ListView fileListView) {
        this.activity = activity;
        this.fileList = fileListView;
    }

    @Override
    protected void onPreExecute() {
        //初始化控件
        this.waitDialog = new AlertDialog.Builder(this.activity)
                .setMessage("waiting...")
                .create();
        this.waitDialog.setCancelable(false);
        this.waitDialog.setCanceledOnTouchOutside(false);
        this.waitDialog.show();
        super.onPreExecute();
    }

    @Override
    protected List<HashMap<String, Object>> doInBackground(String... params) {
        List<HashMap<String, Object>> fileLists = buildListForAdapter(params[0]);
        //默认首字母排序
        this.sortByKey(fileLists, SROTTYPE.LETTER);
        return fileLists;
    }

    @Override
    protected void onPostExecute(List<HashMap<String, Object>> result) {
        //初始化数据
        this.initData(result);
        fileList.invalidate();
        this.waitDialog.dismiss();
        super.onPostExecute(result);
    }

    //初始化数据
    public void initData(List<HashMap<String, Object>> fileLists) {
        SimpleAdapter adapter = new SimpleAdapter(this.activity.getApplicationContext(), fileLists,
                R.layout.filemanager_list_item,
                new String[]{"name", "path", "childnums", "date", "img"},
                new int[]{R.id.filemanager_item_info_name, R.id.filemanager_item_filePath,
                R.id.filemanager_item_info_numsAndDate_nums, R.id.filemanager_item_info_numsAndDate_date,
                R.id.filemanager_item_icon});
        this.fileList.setAdapter(adapter);
    }

    //构建文件列表适配器
    public List<HashMap<String, Object>> buildListForAdapter(String path) {
        verifyStoragePermissions(this.activity);
        List<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
        File rootFile = new File(path);
        if (!rootFile.exists()) {
            //以默认位置打开
            rootFile = new File(DEFAULT_PATH);
            if (!rootFile.exists()) {
                //默认位置不存在，进行创建
                rootFile.mkdir();
            }
        }
        File[] currentPathFiles = rootFile.listFiles();
        if (!path.equals(SDCARD_HOME)) {
            HashMap<String, Object> root = new HashMap<String, Object>();
            root.put("name", "/");
            root.put("img", R.drawable.folder_home_back);
            root.put("path", SDCARD_HOME);
            root.put("childnums", "返回根目录");
            root.put("date", "");
            list.add(root);
            HashMap<String, Object> pmap = new HashMap<String, Object>();
            pmap.put("name", "..");
            pmap.put("img", R.drawable.folder_up_back);
            int endIndex = path.lastIndexOf("/");
            String parentPath = path.substring(0, endIndex);
            pmap.put("path", parentPath);
            pmap.put("childnums", "返回上一级");
            pmap.put("date", "");
            list.add(pmap);
        }
        if (currentPathFiles != null) {
            //如果存在子文件则进行遍历
            for (File file : currentPathFiles) {
                //设置文件夹图标
                if (file.isDirectory()) {
                    HashMap<String, Object> item = new HashMap<String, Object>();
                    item.put("img", R.drawable.folder_back);
                    item.put("name", file.getName());
                    item.put("path", file.getPath());
                    item.put("childnums", "共有"+this.getDirectoryNums(file)+"项");
                    item.put("date", new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA).format(file.lastModified()));
                    list.add(item);
                }
            }
        }
        return list;
    }

    //统计文件夹中文件夹数目
    public int getDirectoryNums(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            return this.getDirectoryNums(files);
        }
        return -1;
    }

    //统计文件夹中的文件夹数量
    public int getDirectoryNums(File[] files) {
        int nums = 0;
        if (files != null) {
            for (File file : files) {
                if(file.isDirectory()) {
                    nums++;
                }
            }
        }
        return nums;
    }

    //List排序
    public List<HashMap<String, Object>> sortByKey(List<HashMap<String, Object>> lists, SROTTYPE sortType) {
        Collections.sort(lists, new SortComparator(sortType));
        return lists;
    }

    //获取存储权限
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE ={
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};
    public static void verifyStoragePermissions(Activity activity){
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
    }
}
