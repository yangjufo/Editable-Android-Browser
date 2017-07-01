package com.example.browser.file;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Young on 2016/12/3.
 */

public class ImageCaptureManager{

    private File file;
    private Context context;

    public ImageCaptureManager(Context cotnext, String fileName, String dirPath, Bitmap imgSave) {
        writeFile(fileName, dirPath, imgSave);
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
