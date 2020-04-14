package com.cc.camera.id.recognition;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class IdRecognitionNationalEmblemActivity extends Activity {

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("请稍后...");
        progressDialog.setCancelable(false);

        View decorView = getWindow().getDecorView();
        int options = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        decorView.setSystemUiVisibility(options);

        requestPermissions(new String[]{
                Manifest.permission.CAMERA
        }, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        initView();
    }

    private void initView(){
        setContentView(R.layout.id_recognition_portrait);
        final CameraView cameraView = findViewById(R.id.cameraView);
        final IDPhotoFrameView idPhotoFrameView = findViewById(R.id.idPhotoFrameView);
        cameraView.setOnSizeChangeListener(new CameraView.OnSizeChangeListener() {
            @Override
            public void onSizeChange(int width, int height) {
                int windowWidth = width / 2;
                int windowHeight = (int) (windowWidth * 0.63f);
                idPhotoFrameView.setWindowWidth(windowWidth);
                idPhotoFrameView.setWindowHeight(windowHeight);
                idPhotoFrameView.invalidate();
            }
        });
        findViewById(R.id.confirmButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                final Camera camera = cameraView.getCamera();
                if (camera != null) {
                    camera.takePicture(null, null, null, new Camera.PictureCallback() {
                        @Override
                        public void onPictureTaken(final byte[] data, Camera camera) {
                            if (data == null) {
                                recognitionError();
                                return;
                            }
                            new Thread() {
                                @Override
                                public void run() {
                                    super.run();
                                    Bitmap fullBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                    if (fullBitmap == null) {
                                        recognitionError();
                                        return;
                                    }

                                    //裁出身份证
                                    Bitmap idBitmap = null;
                                    try {
                                        idBitmap = Bitmap.createBitmap(
                                                fullBitmap,
                                                cameraView.getSurfaceView().getWidth() / 2 - idPhotoFrameView.getWindowWidth() / 2,
                                                cameraView.getSurfaceView().getHeight() / 2 - idPhotoFrameView.getWindowHeight() / 2,
                                                idPhotoFrameView.getWindowWidth(),
                                                idPhotoFrameView.getWindowHeight()
                                        );
                                    } catch (Throwable throwable){
                                        throwable.printStackTrace();
                                    }

                                    fullBitmap.recycle();
                                    if (idBitmap == null) {
                                        recognitionError();
                                        return;
                                    } else {
                                        new BitmapUtils().save(IdRecognitionNationalEmblemActivity.this, idBitmap, "idNationalEmblem.jpg");
                                    }

                                    final Bitmap finalIdBitmap = idBitmap;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isDestroyed()) {
                                                return;
                                            }
                                            if (IdRecognition.NATIONAL_EMBLEM_LISTENER != null) {
                                                IdRecognition.NATIONAL_EMBLEM_LISTENER.onResult(finalIdBitmap);
                                                IdRecognition.NATIONAL_EMBLEM_LISTENER = null;
                                            } else {
                                                finalIdBitmap.recycle();
                                            }
                                            finish();
                                        }
                                    });
                                }
                            }.start();
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        progressDialog.dismiss();
        IdRecognition.NATIONAL_EMBLEM_LISTENER = null;
    }

    public void recognitionError(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IdRecognitionNationalEmblemActivity.this, "获取失败，请重试", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }
}
