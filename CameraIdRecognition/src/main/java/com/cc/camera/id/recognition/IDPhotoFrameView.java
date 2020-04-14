package com.cc.camera.id.recognition;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class IDPhotoFrameView extends View {

    private int windowWidth;

    private int windowHeight;

    private Paint paint = new Paint();

    private Rect fullRect = new Rect();

    private Rect windowRect = new Rect();

    private PorterDuffXfermode xorMode = new PorterDuffXfermode(PorterDuff.Mode.XOR);

    public IDPhotoFrameView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setXfermode(null);
        paint.setColor(Color.parseColor("#CC000000"));
        fullRect.left = 0;
        fullRect.right = getWidth();
        fullRect.top = 0;
        fullRect.bottom = getHeight();
        canvas.drawRect(fullRect, paint);

        paint.setXfermode(xorMode);

        windowRect.left = fullRect.right / 2 - windowWidth / 2;
        windowRect.right = windowRect.left + windowWidth;
        windowRect.top = fullRect.bottom / 2 - windowHeight / 2;
        windowRect.bottom = windowRect.top + windowHeight;
        canvas.drawRect(windowRect, paint);
    }

    public int getWindowWidth() {
        return windowWidth;
    }

    public void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    public int getWindowHeight() {
        return windowHeight;
    }

    public void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }
}
