package com.example.browser.custom;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.browser.MainActivity;
import com.example.browser.R;
import com.example.browser.file.FileActivity;
import com.example.browser.file.ImageCaptureManager;
import com.example.browser.other.PopupWindowPaintEraser;
import com.example.browser.other.PopupWindowPaintPen;

/**
 * Created by Young on 2016/12/3.
 */


//画板
public class PaintActivity extends Activity {

    private HandWrite handWrite = null; //定义画布
    public static Bitmap originalBitmap; //背景图（网页截图）
    private ImageButton pen1Button; //工具选择按钮
    private ImageButton pen2Button;
    private ImageButton pen3Button;
    private ImageButton back;
    private ImageButton forward;
    private ImageButton eraser;
    private ImageButton clear;
    private ImageButton save;
    private ImageButton quit;
    private ButtonClickListener buttonClickListener;
    private static int editedPageCount= 0; //记录编辑后网页保存编号
    private TextView choosePath; //保存路径
    private TextView imgSaveName; //保存名称
    private Dialog saveImageToChoosePath; //选择保存名称和路径对话框
    private ViewTouchListener viewTouchListener;

    //定义设置弹窗和监听器
    private SettingSeekBarChangeListener settingSeekBarChangeListener;
    private PopupWindowPaintPen pen1Settings;
    private PopupWindowPaintPen pen2Settings;
    private PopupWindowPaintPen pen3Settings;
    private PopupWindowPaintEraser eraserSettings;
    private pen pen1;
    private pen pen2;
    private pen pen3;
    private int penChoose = 0; //记录操作的画笔编号
    private int eraserWidth = 20; //橡皮擦大小
    private boolean isEraserValid = false;
    private TextView eraser_show; //展示橡皮擦大小
    private View pen1Chosen; //展示被选中的画笔
    private View pen2Chosen;
    private View pen3Chosen;
    private View eraserChosen;

    //存储画笔的属性
    private class pen {
        int red; //颜色
        int green;
        int blue;
        String mixColor;
        int width; //粗细
        SeekBar color_red_seekBar; //调整颜色和大小的拖动条
        SeekBar color_green_seekBar;
        SeekBar color_blue_seekBar;
        SeekBar width_seekBar;
        TextView pen_show;
        boolean valid;
        public pen(){ //初始化属性
            red = 0;
            green = 0;
            blue = 0;
            mixColor = "#00000";
            width = 20;
            valid = false;
        };
        public void setAttr() {  //记录属性
            red = color_red_seekBar.getProgress();
            green = color_green_seekBar.getProgress();
            blue = color_blue_seekBar.getProgress();
            width = width_seekBar.getProgress();
        }
        public void setMixColor() { //计算颜色
            StringBuffer tempRed = new StringBuffer(Integer.toHexString(red));
            if(tempRed.length() < 2){
                tempRed.insert(0, "0");
            }
            StringBuffer tempGreen = new StringBuffer(Integer.toHexString(green));
            if(tempGreen.length() < 2){
                tempGreen.insert(0, "0");
            }
            StringBuffer tempBlue = new StringBuffer(Integer.toHexString(blue));
            if(tempBlue.length() < 2){
                tempBlue.insert(0, "0");
            }
            mixColor = "#" + tempRed.toString() + tempGreen.toString() + tempBlue.toString();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_paint);
        //初始化画布
        handWrite = (HandWrite)findViewById(R.id.handWriteView);
        handWrite.setOriginalBitmap(originalBitmap);
        //设置监听器
        viewTouchListener = new ViewTouchListener();
        handWrite.setOnTouchListener(viewTouchListener);
        buttonClickListener = new ButtonClickListener();

        //初始化工具栏
        pen1Button = (ImageButton) findViewById(R.id.edit_page_pen1);
        pen2Button = (ImageButton) findViewById(R.id.edit_page_pen2);
        pen3Button = (ImageButton) findViewById(R.id.edit_page_pen3);
        back = (ImageButton) findViewById(R.id.edit_page_back);
        forward = (ImageButton) findViewById(R.id.edit_page_forward);
        eraser = (ImageButton) findViewById(R.id.edit_page_eraser);
        clear = (ImageButton) findViewById(R.id.edit_page_clear);
        save = (ImageButton) findViewById(R.id.edit_page_save);
        quit = (ImageButton) findViewById(R.id.edit_page_quit);
        pen1Chosen =  findViewById(R.id.edit_pen1_chosen);
        pen2Chosen =  findViewById(R.id.edit_pen2_chosen);
        pen3Chosen =  findViewById(R.id.edit_pen3_chosen);
        eraserChosen = findViewById(R.id.edit_eraser_chosen);

        //设置工具栏按钮监听
        pen1Button.setOnClickListener(buttonClickListener);
        pen2Button.setOnClickListener(buttonClickListener);
        pen3Button.setOnClickListener(buttonClickListener);
        back.setOnClickListener(buttonClickListener);
        forward.setOnClickListener(buttonClickListener);
        eraser.setOnClickListener(buttonClickListener);
        clear.setOnClickListener(buttonClickListener);
        save.setOnClickListener(buttonClickListener);
        quit.setOnClickListener(buttonClickListener);

        //工具栏属性弹窗
        int popWindowWidth = this.getWindowManager().getDefaultDisplay().getWidth()-30;
        pen1Settings = new PopupWindowPaintPen(PaintActivity.this, popWindowWidth, 1);
        pen2Settings = new PopupWindowPaintPen(PaintActivity.this, popWindowWidth, 2);
        pen3Settings = new PopupWindowPaintPen(PaintActivity.this, popWindowWidth, 3);
        eraserSettings = new PopupWindowPaintEraser(PaintActivity.this, popWindowWidth);

        //画笔
        pen1 = new pen();
        pen2 = new pen();
        pen3 = new pen();

        //进度条监听
        settingSeekBarChangeListener = new SettingSeekBarChangeListener();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    //工具栏按钮响应
    private class ButtonClickListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.edit_page_pen1) {
                //画笔1

                handWrite.setEraser(false); //关闭橡皮擦
                handWrite.setCap(Paint.Cap.ROUND); //设置画笔风格
                penChoose = 1; //设置选择画笔号
                setChosenShow(1); //展示选择画笔

                LayoutInflater penSettingInflater = LayoutInflater.from(getApplicationContext());
                View penSettingView = penSettingInflater.inflate(R.layout.pop_window_paint_pen1, null);
                //显示弹窗
                pen1Settings.showAtLocation(penSettingView, Gravity.TOP|Gravity.RIGHT, 20, 100);
                pen1.color_red_seekBar = (SeekBar) pen1Settings.getView(R.id.pen1_color_red);

                //绑定变量与控件
                pen1.color_green_seekBar = (SeekBar) pen1Settings.getView(R.id.pen1_color_green);
                pen1.color_blue_seekBar = (SeekBar) pen1Settings.getView(R.id.pen1_color_blue);
                pen1.width_seekBar = (SeekBar) pen1Settings.getView(R.id.pen1_width);
                pen1.pen_show = (TextView) pen1Settings.getView(R.id.pen1_show);

                //设置宽度初始值
                pen1.width_seekBar.setProgress(pen1.width);

                //设置监听
                pen1.color_red_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen1.color_green_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen1.color_blue_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen1.width_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);

                //有效位置为真
                pen1.valid = true;
            } else if (view.getId() == R.id.edit_page_pen2) {
                //画笔2

                handWrite.setEraser(false);
                handWrite.setCap(Paint.Cap.SQUARE);
                penChoose = 2;
                setChosenShow(2);
                LayoutInflater penSettingInflater = LayoutInflater.from(getApplicationContext());
                View penSettingView = penSettingInflater.inflate(R.layout.pop_window_paint_pen2, null);
                pen2Settings.showAtLocation(penSettingView, Gravity.TOP|Gravity.RIGHT, 20, 100);
                pen2.color_red_seekBar = (SeekBar) pen2Settings.getView(R.id.pen2_color_red);
                pen2.color_green_seekBar = (SeekBar) pen2Settings.getView(R.id.pen2_color_green);
                pen2.color_blue_seekBar = (SeekBar) pen2Settings.getView(R.id.pen2_color_blue);
                pen2.width_seekBar = (SeekBar) pen2Settings.getView(R.id.pen2_width);
                pen2.pen_show = (TextView) pen2Settings.getView(R.id.pen2_show);
                pen2.width_seekBar.setProgress(pen2.width);
                pen2.color_red_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen2.color_green_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen2.color_blue_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen2.width_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen2.valid = true;
            } else if (view.getId() == R.id.edit_page_pen3) {
                //画笔3

                handWrite.setEraser(false);
                handWrite.setCap(Paint.Cap.BUTT);
                penChoose = 3;
                setChosenShow(3);
                LayoutInflater penSettingInflater = LayoutInflater.from(getApplicationContext());
                View penSettingView = penSettingInflater.inflate(R.layout.pop_window_paint_pen3, null);
                pen3Settings.showAtLocation(penSettingView, Gravity.TOP|Gravity.RIGHT, 20, 100);
                pen3.color_red_seekBar = (SeekBar) pen3Settings.getView(R.id.pen3_color_red);
                pen3.color_green_seekBar = (SeekBar) pen3Settings.getView(R.id.pen3_color_green);
                pen3.color_blue_seekBar = (SeekBar) pen3Settings.getView(R.id.pen3_color_blue);
                pen3.width_seekBar = (SeekBar) pen3Settings.getView(R.id.pen3_width);
                pen3.pen_show = (TextView) pen3Settings.getView(R.id.pen3_show);
                pen3.width_seekBar.setProgress(pen3.width);
                pen3.color_red_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen3.color_green_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen3.color_blue_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen3.width_seekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                pen3.valid = true;
            } else if (view.getId() == R.id.edit_page_back) {
                //后退一步
                handWrite.setBack();
                setStatusOfBackForward();
            } else if (view.getId() == R.id.edit_page_forward) {
                //前进一步
                handWrite.setForward();
                setStatusOfBackForward();
            } else if (view.getId() == R.id.edit_page_eraser) {
                //橡皮擦，标记为画笔4

                penChoose = 4;
                setChosenShow(4);
                LayoutInflater eraserSettingInflater = LayoutInflater.from(getApplicationContext());
                View eraserSettingView = eraserSettingInflater.inflate(R.layout.pop_window_paint_eraser, null);
                eraserSettings.showAtLocation(eraserSettingView, Gravity.TOP|Gravity.RIGHT, 20, 100);

                //设置大小进度条
                SeekBar eraserSeekBar = (SeekBar) eraserSettings.getView(R.id.eraser_width);
                eraser_show = (TextView) eraserSettings.getView(R.id.eraser_show);
                eraserSeekBar.setProgress(eraserWidth);
                eraserSeekBar.setOnSeekBarChangeListener(settingSeekBarChangeListener);
                isEraserValid = true; //有效位置为真
                handWrite.setEraser(true);
            } else if (view.getId() == R.id.edit_page_clear) {
                //清空画布
                handWrite.setEraser(false);
                handWrite.clear();
            } else if (view.getId() == R.id.edit_page_save) {
                //保存当前结果

                verifyStoragePermissions(PaintActivity.this); //获取存储权限
                handWrite.setEraser(false);

                //文件名和保存路径设置弹窗
                View dialogSaveImg = LayoutInflater.from(PaintActivity.this).inflate(R.layout.dialog_saveimg, null);
                choosePath = (TextView) dialogSaveImg.findViewById(R.id.dialog_savePath_enter);
                imgSaveName = (TextView) dialogSaveImg.findViewById(R.id.dialog_fileName_input);

                //初始文件名
                final String imgName = "edited_page" + editedPageCount + ".jpg";;
                imgSaveName.setText(imgName);

                //文件保存路径选择
                choosePath.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (view.getId() == R.id.dialog_savePath_enter) {
                            //弹出文件列表
                            Intent imgSavePath = new Intent(PaintActivity.this, FileActivity.class);
                            imgSavePath.putExtra("savePath", choosePath.getText().toString());
                            startActivityForResult(imgSavePath, MainActivity.REQUEST_SAVE_IMAGE_PATH);
                        }
                    }
                });

                //保存文件
                saveImageToChoosePath = new AlertDialog.Builder(PaintActivity.this)
                        .setTitle("选择保存路径")
                        .setView(dialogSaveImg)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //写入文件
                                new ImageCaptureManager(PaintActivity.this, imgName, choosePath.getText().toString(), handWrite.new1Bitmap);
                                Toast.makeText(PaintActivity.this, "编辑网页成功保存", Toast.LENGTH_SHORT).show();
                                editedPageCount++; //已保存文件数加1
                            }
                        })
                        .setNegativeButton("取消", null)
                        .create();
                saveImageToChoosePath.show();
            } else if (view.getId() == R.id.edit_page_quit) {
                //退出编辑
                finish();
            }
        }
    }


    private class ViewTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent e) {
            //触摸时更新画笔设置
            if (e.getAction() == MotionEvent.ACTION_DOWN)
                setHandWritePen(penChoose);

            //触摸时更新后退、前进按钮状态
            if (e.getAction() != MotionEvent.ACTION_CANCEL)
                setStatusOfBackForward();
            return false;
        }
    }

    //更新前进、后退按钮状态
    private void setStatusOfBackForward() {
        //后退按钮
        if (handWrite.canBack)
            back.setEnabled(true);
        else
            back.setEnabled(false);
        //前进按钮
        if (handWrite.canForward)
            forward.setEnabled(true);
        else
            forward.setEnabled(false);

    }


    //更新画笔属性
    private void setHandWritePen(int penChoose){
        switch (penChoose) {
            case 1:
                //画笔1
                if (pen1.valid) {
                    pen1.setAttr(); //将进度条值保存
                    pen1.setMixColor(); //获取颜色
                    handWrite.setPenAttr(Color.parseColor(pen1.mixColor), pen1.width); //更新画笔属性
                    pen1.valid = false; //有效位置为假
                }
                break;
            case 2:
                //画笔2
                if (pen2.valid) {
                    pen2.setAttr();
                    pen2.setMixColor();
                    handWrite.setPenAttr(Color.parseColor(pen2.mixColor), pen2.width);
                    pen2.valid = false;
                }
                break;
            case 3:
                //画笔3
                if (pen3.valid) {
                    pen3.setAttr();
                    pen3.setMixColor();
                    handWrite.setPenAttr(Color.parseColor(pen3.mixColor), pen3.width);
                    pen3.valid = false;
                }
                break;
            case 4:
                //橡皮擦
                if(isEraserValid) {
                    handWrite.setPenAttr(Color.WHITE, eraserWidth);
                    isEraserValid = false;
                }
                break;
            default:
                break;
        }
    }


    //进度条监听
    private class SettingSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            switch (penChoose) {
                case 1:
                    //画笔1进度条
                    if(pen1.valid) {
                        pen1.setAttr();
                        pen1.setMixColor(); //保存画笔属性
                        pen1.pen_show.setBackgroundColor(Color.parseColor(pen1.mixColor)); //将当前画笔属性展示
                        pen1.pen_show.setWidth(pen1.width);
                    }
                    break;
                case 2:
                    //画笔2进度条
                    if(pen2.valid) {
                        pen2.setAttr();
                        pen2.setMixColor();
                        pen2.pen_show.setBackgroundColor(Color.parseColor(pen2.mixColor));
                        pen2.pen_show.setWidth(pen2.width);
                    }
                    break;
                case 3:
                    //画笔3进度条
                    if(pen3.valid) {
                        pen3.setAttr();
                        pen3.setMixColor();
                        pen3.pen_show.setBackgroundColor(Color.parseColor(pen3.mixColor));
                        pen3.pen_show.setWidth(pen3.width);
                    }
                    break;
                case 4:
                    //橡皮擦进度条
                    if(isEraserValid) {
                        eraserWidth = seekBar.getProgress();
                        eraser_show.setWidth(eraserWidth);
                    }
                default:
                    break;
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }

    }


    //展示所选按钮（下方显示一条绿色线）
    private void setChosenShow(int penChoose) {
        switch (penChoose) {
            case 1:
                //画笔1
                pen1Chosen.setBackgroundColor(Color.parseColor("#00FF00")); //1号画笔下方线条设置为绿色
                pen2Chosen.setBackgroundColor(Color.WHITE); //其余设置为白色
                pen3Chosen.setBackgroundColor(Color.WHITE);
                eraserChosen.setBackgroundColor(Color.WHITE);
                break;
            case 2:
                //画笔2
                pen2Chosen.setBackgroundColor(Color.parseColor("#00FF00"));
                pen1Chosen.setBackgroundColor(Color.WHITE);
                pen3Chosen.setBackgroundColor(Color.WHITE);
                eraserChosen.setBackgroundColor(Color.WHITE);
                break;
            case 3:
                //画笔3
                pen3Chosen.setBackgroundColor(Color.parseColor("#00FF00"));
                pen2Chosen.setBackgroundColor(Color.WHITE);
                pen1Chosen.setBackgroundColor(Color.WHITE);
                eraserChosen.setBackgroundColor(Color.WHITE);
                break;
            case 4:
                //画笔4
                eraserChosen.setBackgroundColor(Color.parseColor("#00FF00"));
                pen2Chosen.setBackgroundColor(Color.WHITE);
                pen3Chosen.setBackgroundColor(Color.WHITE);
                pen1Chosen.setBackgroundColor(Color.WHITE);
                break;
            default:
                break;
        }
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


