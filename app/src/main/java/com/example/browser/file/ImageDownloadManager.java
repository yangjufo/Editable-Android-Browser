package com.example.browser.file;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Young on 2016/11/30.
 */

public class ImageDownloadManager extends AsyncTask<String, String, String> {

    private File file;
    private Context context;

    public ImageDownloadManager(Context cotnext) {
        this.context = cotnext;
    }

    @Override
    protected String doInBackground(String... params) {
        if (params[2].startsWith("/sdcard")) {
            //如果是以/sdcard/为开头，则应保存为sdcard中
            params[2] = Environment.getExternalStorageDirectory()+params[2].substring(8);
        }
        try{
            URL url = new URL(params[1]);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            Bitmap imgSave = BitmapFactory.decodeStream(inputStream);
            this.writeFile(params[0], params[2], imgSave);
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        Toast.makeText(context, "成功下载", Toast.LENGTH_SHORT).show();
        super.onPostExecute(result);
    }

    //将图片写入
    public void writeFile(String fileName, String dirPath, Bitmap imgSave) {
        try{
            File directory = new File(dirPath);
            if ((directory.exists()) && (directory.isFile())) {
                directory.delete();
            } else {
                directory.mkdirs();
            }
            this.file = new File(dirPath, fileName);
            if (this.file.exists()) {
                this.file.delete();
            }
            this.file.createNewFile();
            FileOutputStream fo = new FileOutputStream(this.file);
            imgSave.compress(Bitmap.CompressFormat.PNG, 100, fo);
            fo.flush();
            fo.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
    }

}
