package com.example.browser.custom;

/**
 * Created by Young on 2016/12/3.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

//编辑画布
public class HandWrite extends SurfaceView implements Runnable, SurfaceHolder.Callback
{
    private Bitmap originalBitmap = null; //原图
    Bitmap new1Bitmap = null; //编辑图
    public boolean isClear = false; //是否清空
    private int color = Color.WHITE; //画笔颜色
    private float strokeWidth = 3.0f; //画笔粗细
    private Paint.Cap strokeCap = Paint.Cap.ROUND; //画笔风格
    private boolean isEraser = false; //是否橡皮擦
    private ArrayList<Action> actionList = null; //动作容器，记录画和擦的笔迹
    private Action curAction = null; //当前动作
    private int currentPaintIndex = 0; //当前动作号
    private boolean mLoop = true; //是否循环
    private SurfaceHolder mSurfaceHolder;
    public boolean canBack = false; //是否可以后退
    public boolean canForward = false; //是否可以前进


    public HandWrite(Context context, AttributeSet attrs)
    {
        //初始化变量
        super(context, attrs);
        actionList = new ArrayList<Action>();
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        new Thread(this).start();
    }


    public void setOriginalBitmap(Bitmap bitmap) {
        //设置原图和编辑图
        originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        new1Bitmap = Bitmap.createBitmap(originalBitmap);
    }

    public void setPenAttr(int color, float strokeWidth) {
        //设置画笔属性（颜色和粗细）
        this.color = color;
        this.strokeWidth = strokeWidth;
    }

    public void setBack() {
        //后退一步
        if (actionList.size() > 0 && currentPaintIndex > 0) {
            synchronized (mSurfaceHolder) {
                new1Bitmap = Bitmap.createBitmap(this.originalBitmap);
                currentPaintIndex--; //当前动作号减1
            }
        }
    }

    //前进一步
    public void setForward() {
        if (currentPaintIndex < actionList.size()) {
            synchronized (mSurfaceHolder) {
                new1Bitmap = Bitmap.createBitmap(this.originalBitmap);
                currentPaintIndex++; //当前动作号加1
            }
        }
    }

    //清空
    public void clear(){
        synchronized (mSurfaceHolder) {
            isClear = true;
            actionList.clear();
            currentPaintIndex = 0;
        }
    }


    //设置风格
    public void setCap(Paint.Cap strokeCap){
        this.strokeCap = strokeCap;
    }

    //设置橡皮擦
    public void setEraser(boolean isEraser) {
        this.isEraser = isEraser;
    }

    //绘制笔记
    protected void Draw()
    {
        //获取当前画布
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (mSurfaceHolder == null || canvas == null) {
            return;
        }

        //载入背景图
        canvas.drawBitmap(this.originalBitmap, 0, 0, null);
        if (isClear) {
            //如果清空，更新编辑图为原图
            new1Bitmap = Bitmap.createBitmap(this.originalBitmap);
        }
        //绘制编辑图
        canvas.drawBitmap(HandWriting(new1Bitmap), 0, 0, null);
        mSurfaceHolder.unlockCanvasAndPost(canvas);
    }

    //绘制编辑图
    public Bitmap HandWriting(Bitmap originalBitmap)
    {
        Canvas canvas = null;
        //创建临时画布
        canvas = new Canvas(originalBitmap);
        canvas.drawColor(Color.TRANSPARENT);

        //将动作容器中未被撤销的动作画出
        for(int i=0; i<currentPaintIndex; i++){
            actionList.get(i).draw(canvas);
        }

        //绘制当前动作
        if (curAction != null)
            curAction.draw(canvas);

        //返回编辑图
        return originalBitmap;
    }

    //触摸事件，记录绘画笔迹
    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        //触摸点坐标
        float clickX, clickY;
        clickX = event.getX();
        clickY = event.getY();

        //按下
        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            //初始化当前动作为画笔或橡皮擦
            if (!isEraser) {
                curAction = new MyPath(clickX, clickY, this.strokeWidth, this.color, this.strokeCap);
            } else {
                curAction = new MyEraser(clickX, clickY, this.strokeWidth, Color.WHITE);
            }
            //删去被撤销的动作
            clearSpareAction();
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

            //记录绘画笔迹
            if (curAction != null)
                curAction.move(clickX, clickY);
            return true;
        } else if (event.getAction() == MotionEvent.ACTION_UP) {

            //将当前动作加入动作容器
            if (curAction != null) {
                curAction.move(clickX, clickY);
                actionList.add(curAction);
                currentPaintIndex++;
                curAction = null;
            }
            return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void run() {
        //线程不断循环绘制
        while(mLoop) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (mSurfaceHolder) {
                //绘制并检测是否可以前进、后退
                if (originalBitmap != null)
                    Draw();
                if (actionList.size() > 0 && currentPaintIndex != 0)
                    canBack = true;
                else
                    canBack = false;
                if (currentPaintIndex < actionList.size())
                    canForward = true;
                else
                    canForward = false;

            }
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        //画布创建时线程开始循环
        mLoop = true;
        new Thread(this).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //画布销毁时停止线程循环
        mLoop = false;
    }

    private void clearSpareAction() {
        //删除被撤销的动作
        for (int i = actionList.size() - 1; i > currentPaintIndex - 1; i--) {
            actionList.remove(i);
        }
    }
}

