package com.lalagrass.soundview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class DrawingView extends SurfaceView  {

    private Paint _textPaint;
    private int _width;
    private int _height;

    private int MaxDb = 100;

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        _textPaint = new Paint();
        _textPaint.setARGB(255, 250, 50, 50);
    }

    public void UpdateSpectrum(double[] retX) {
        if (retX == null || retX.length == 0)
            return;
        SurfaceHolder holder = getHolder();
        if (holder != null) {
            Canvas c = holder.lockCanvas();
            if (c != null) {
                c.drawColor(0, PorterDuff.Mode.CLEAR);
                int width = c.getWidth();
                if (width != _width) {
                    int height = c.getHeight();
                    _width = width;
                    _height = height;
                    _textPaint.setTextSize(_height / 32);
                }
                for (int i = 0; i < retX.length - 1; i++) {
                    c.drawLine((float) i,
                            (float) (-retX[i] + _height * 3 / 4),
                            (float) i + 1,
                            (float) (-retX[i + 1] + _height * 3 / 4),
                            _textPaint);
                }
                holder.unlockCanvasAndPost(c);
            }
        }
    }
}
