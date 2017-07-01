package com.example.browser.file;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.browser.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Young on 2016/11/30.
 */

public class RequestShowImageOnline extends AsyncTask<String, String, Bitmap> {

    private Dialog dialog;
    private Context context;

    public RequestShowImageOnline(Context context) {
        this.context = context;
    }

    //在子线程执行前调用
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.dialog = new AlertDialog.Builder(this.context)
                .setMessage("正在加载...")
                .create();
        this.dialog.show();
    }

    //执行子线程
    @Override
    protected Bitmap doInBackground(String... params) {
        Bitmap imgShow = null;
        try{
            URL url = new URL(params[0]);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream inputStream = conn.getInputStream();
            imgShow = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        return imgShow;
    }

    //执行完子线程，进行完结操作
    @Override
    protected void onPostExecute(Bitmap result) {
        super.onPostExecute(result);
        View popupImgMenu = LayoutInflater.from(context).inflate(R.layout.activity_imgsview, null);
        ImageView showImg = (ImageView) popupImgMenu.findViewById(R.id.imgsview);
        showImg.setImageBitmap(result);
        this.dialog.dismiss();
        new AlertDialog.Builder(context).setView(popupImgMenu).create().show();
    }
}
