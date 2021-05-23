package com.swami.whiteboardwithocr;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;

public class Canvas_View extends View {

    Paint paint;
    Path path;
    Bitmap bitmap;
    Canvas pCanvas;

    int currentColor;
    ArrayList<PathModel> list = new ArrayList<>();
    Paint pBitmapPaint = new Paint(paint.DITHER_FLAG);

    float xAxis, yAxis, startX, startY;
    boolean flg;

    public Canvas_View(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();

        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setXfermode(null);
        paint.setAlpha(0xff);
        //paint.setStrokeWidth(6f);
    }

    public void init(DisplayMetrics metrics){
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        bitmap = Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        pCanvas = new Canvas(bitmap);

        currentColor = Color.BLACK;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        pCanvas.drawColor(Color.WHITE);

        for (PathModel model: list){
            paint.setColor(model.getColor());
            paint.setStrokeWidth(model.getStrokeWidth());
            paint.setMaskFilter(null);

            pCanvas.drawPath((Path) model.getPath(), paint);
        }

        canvas.drawBitmap(bitmap, 0, 0, pBitmapPaint);
        canvas.restore();
    }

    public void pen(){
        flg = false;
        currentColor = Color.BLACK;
    }

    public void erase(){
        flg = true;
        currentColor = Color.WHITE;
    }

    public void TouchStart(float x, float y){
        path = new Path();
        PathModel model = new PathModel(currentColor, flg?20:10, path);
        list.add(model);

        path.reset();
        path.moveTo(x, y);
        startX = x;
        startY = y;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        xAxis = event.getX();
        yAxis = event.getY();

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE:
                path.lineTo(xAxis,yAxis);
                invalidate();
                break;

            case MotionEvent.ACTION_DOWN:
                TouchStart(xAxis,yAxis);
                invalidate();
                return true;

            case MotionEvent.ACTION_UP:
                invalidate();
                break;

            default:
                return false;
        }
        return true;
    }





}
