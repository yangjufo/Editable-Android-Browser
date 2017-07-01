package com.example.browser.custom;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * Created by Young on 2016/12/4.
 */

//画线和使用过橡皮擦
public class Action {
        public int color;

        Action() {
            color = Color.BLACK;
        }

        Action(int color) {
            this.color = color;
        }

        public void draw(Canvas canvas) {
        }

        public void move(float mx, float my) {

        }
    }


//画线
class MyPath extends Action {
    Path path; //路线
    float size; //笔迹粗细
    Paint.Cap cap; //笔迹风格

    MyPath() { //构造函数
        path=new Path();
        size=1;
    }

    MyPath(float x, float y, float size, int color, Paint.Cap cap) {
        super(color); //设置相关属性
        path=new Path();
        this.size=size;
        this.cap = cap;
        path.moveTo(x, y);
        path.lineTo(x, y);
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint(); //设置画笔属性
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(cap);
        canvas.drawPath(path, paint);
    }

    //记录路径
    public void move(float mx,float my){
        path.lineTo(mx, my);
    }
}

//橡皮擦
class MyEraser extends Action {
    Path path; //路线
    float size; //大小

    MyEraser() {
        path=new Path();
        size=1;
    }

    MyEraser(float x,float y, float size, int color) {
        super(color); //设置相关属性
        path=new Path();
        this.size=size;
        path.moveTo(x, y);
        path.lineTo(x, y);
    }

    public void draw(Canvas canvas) {
        Paint paint = new Paint(); //设置画笔属性
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(size);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
        canvas.drawPath(path, paint);
    }


    //记录路线
    public void move(float mx,float my){
        path.lineTo(mx, my);
    }
}
