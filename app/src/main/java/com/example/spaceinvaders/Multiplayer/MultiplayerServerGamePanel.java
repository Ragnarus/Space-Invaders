package com.example.spaceinvaders.Multiplayer;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;
import java.util.Objects;


public class MultiplayerServerGamePanel extends SurfaceView {
    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private Paint mPaint;
    MultiplayerServerScreenFragment fragment;
    MultiplayerClientScreenFragment fragment2;
    String Fragment;

    public MultiplayerServerGamePanel(Context context) {
        super(context);
        init(context);
        mContext = context;
    }

    public MultiplayerServerGamePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }

    public MultiplayerServerGamePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        mContext = context;
    }

    public MultiplayerServerGamePanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
        mContext = context;
    }

    public void setComTool(MultiplayerServerScreenFragment fragment) {
        this.fragment = fragment;
        Fragment = "fragment";
    }
    public void setComTool2(MultiplayerClientScreenFragment fragment2) {
        this.fragment2 = fragment2;
        Fragment = "fragment2";
    }
    private void init(Context context) {
        mSurfaceHolder = getHolder();
        mPaint = new Paint();
        mPaint.setColor(Color.DKGRAY);
    }

    public void draw(List<Float> x, List<Float> y, List<Bitmap> bitmap){
        //Draws the objects which are supplied in the gamescreenfragment in the run method
        Canvas canvas = mSurfaceHolder.lockCanvas();
        if (canvas != null) {
            canvas.drawColor(Color.BLACK);
            for (int i = 0; i < x.size(); i++){
                canvas.drawBitmap(bitmap.get(i), x.get(i), y.get(i), mPaint);
            }
            mSurfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(Objects.equals(Fragment, "fragment2")){
            return fragment2.onGamePanelTouchEvent(event);
        }else {
            return fragment.onGamePanelTouchEvent(event);
        }
    }


}