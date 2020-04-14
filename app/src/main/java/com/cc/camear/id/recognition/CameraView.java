package com.cc.camear.id.recognition;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import java.util.List;

public class CameraView extends RelativeLayout {

    private String tag = getClass().getSimpleName();

    @Nullable
    private Camera camera;

    private SurfaceView surfaceView;

    private RelativeLayout.LayoutParams surfaceLayoutParams;

    private OnSizeChangeListener onSizeChangeListener;

    private boolean isCreated = false;

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        surfaceView = new SurfaceView(context);
        surfaceLayoutParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        surfaceLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        surfaceView.setLayoutParams(surfaceLayoutParams);
        addView(surfaceView);

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                isCreated = true;
                Log.v(tag, "surfaceCreated");
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.v(tag, "surfaceChanged width = " + width + ", height = " + height);
                startPreview(width, height);
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                isCreated = false;
                Log.v(tag, "surfaceDestroyed");
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        });
    }

    public void startPreview(){
        startPreview(surfaceView.getWidth(), surfaceView.getHeight());
    }

    //调用即开始预览
    private void startPreview(int width, int height){
        if (camera != null) {
            camera.stopPreview();
            camera.release();
        }
        if (!isCreated) {//创建了才能使用
            return;
        }
        try {
            camera = Camera.open(0);
            camera.setDisplayOrientation(0);
        } catch (Throwable e){
            e.printStackTrace();
        }
        if (camera != null) {
            Camera.Size cameraSize = getCameraSize(camera, width, height);
            if (cameraSize == null) {
                return;
            }
            Log.v(tag, "select CameraSize, width = " + cameraSize.width + ", height = " + cameraSize.height);

            surfaceLayoutParams.width = cameraSize.width;
            surfaceLayoutParams.height = cameraSize.height;
            surfaceView.setLayoutParams(surfaceLayoutParams);
            surfaceView.requestLayout();

            if(onSizeChangeListener != null){
                onSizeChangeListener.onSizeChange(width, height);
            }

            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(cameraSize.width, cameraSize.height);
            parameters.setPictureSize(cameraSize.width, cameraSize.height);
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            camera.setParameters(parameters);
            try {
                camera.setPreviewDisplay(surfaceView.getHolder());
            } catch (Throwable e) {
                e.printStackTrace();
            }
            camera.startPreview();
        }
    }

    @Nullable
    public Camera.Size getCameraSize(Camera camera, int viewWidth, int viewHeight){
        List<Camera.Size> previewSizeList = camera.getParameters().getSupportedPreviewSizes();
        List<Camera.Size> pictureSizeList = camera.getParameters().getSupportedPictureSizes();

        Camera.Size selectSize = null;

        for(Camera.Size newSize : previewSizeList){
            if (newSize.width > viewWidth || newSize.height > viewHeight) {//限制最大尺寸不得超过view的高宽
                continue;
            }
            Camera.Size tempSize = null;
            if (selectSize == null) {
                tempSize = newSize;
            }else if (viewWidth - selectSize.width >= viewWidth - newSize.width && viewHeight - selectSize.height >= viewHeight - newSize.height) { //如果newSize的高宽都比selectSize更接近viewWidth、viewHeight，则选择
                tempSize = newSize;
            }
            if (tempSize == null) {
                continue;
            }
            for (Camera.Size pictureSize : pictureSizeList) {
                if (pictureSize.width == tempSize.width && pictureSize.height == tempSize.height) { //需要预览和拍摄尺寸一致
                    selectSize = tempSize;
                    break;
                }
            }
        }

        return selectSize;
    }

    @Nullable
    public Camera getCamera() {
        return camera;
    }

    public SurfaceView getSurfaceView() {
        return surfaceView;
    }

    public interface OnSizeChangeListener {
        void onSizeChange(int width, int height);
    }

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.onSizeChangeListener = onSizeChangeListener;
    }
}
