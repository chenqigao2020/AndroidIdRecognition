package com.cc.camera.id.recognition;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;

public class IdRecognitionPortraitActivity extends Activity {

    private String tag = getClass().getSimpleName();

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("身份证信息确认中...");
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
                                        new BitmapUtils().save(IdRecognitionPortraitActivity.this, idBitmap, "idPortrait.jpg");
                                    }

                                    //裁出身份证号
                                    Bitmap idNumberBitmap = null;
                                    try {
                                        idNumberBitmap = Bitmap.createBitmap(
                                                idBitmap,
                                                (int)(idBitmap.getWidth() / 2.99f),
                                                (int)(idBitmap.getHeight() / 1.23f),
                                                (int)(idBitmap.getWidth() / 1.81f),
                                                (int)(idBitmap.getHeight() / 9f)
                                        );
                                    } catch (Throwable throwable){
                                        throwable.printStackTrace();
                                    }

                                    if (idNumberBitmap == null) {
                                        recognitionError();
                                        return;
                                    } else {
                                        new BitmapUtils().save(IdRecognitionPortraitActivity.this, idNumberBitmap, "idNumber.jpg");
                                    }

                                    //裁出名字
                                    Bitmap idNameBitmap = null;
                                    try {
                                        idNameBitmap = Bitmap.createBitmap(
                                                idBitmap,
                                                (int)(idBitmap.getWidth() / 5.47f),
                                                (int)(idBitmap.getHeight() / 9.18f),
                                                (int)(idBitmap.getWidth() / 2.29f),
                                                (int)(idBitmap.getHeight() / 8f)
                                        );
                                    } catch (Throwable throwable){
                                        throwable.printStackTrace();
                                    }

                                    if (idNameBitmap == null) {
                                        recognitionError();
                                        return;
                                    } else {
                                        new BitmapUtils().save(IdRecognitionPortraitActivity.this, idNameBitmap, "idName.jpg");
                                    }

                                    idBitmap.recycle();

                                    long startTime = System.currentTimeMillis();


                                    FileCopyUtils fileCopyUtils = new FileCopyUtils();
                                    String tessDataFilePath = fileCopyUtils.copyIfNot(
                                            IdRecognitionPortraitActivity.this,
                                            "tesseract/tessdata/chi_sim.traineddata",
                                            getCacheDir().getAbsolutePath() + "/tesseract/tessdata",
                                            "chi_sim.traineddata",
                                            false
                                    );

                                    File tessDataFile = new File(tessDataFilePath);
                                    String tessDataFileDriPath = tessDataFile.getParentFile() != null ? tessDataFile.getParentFile().getParent() : null;

                                    Bitmap tagIdNumberBitmap = idNumberBitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    idNumberBitmap.recycle();
                                    Bitmap tagIdNameBitmap = idNameBitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    idNameBitmap.recycle();

                                    String idNumber = null;
                                    String name = null;
                                    TessBaseAPI tessBaseAPI = null;
                                    try {
                                        tessBaseAPI = new TessBaseAPI();
                                        tessBaseAPI.init(tessDataFileDriPath + File.separator, "chi_sim");

                                        tessBaseAPI.setPageSegMode(TessBaseAPI.PageSegMode.PSM_AUTO);
                                        tessBaseAPI.setImage(tagIdNumberBitmap);
                                        idNumber = tessBaseAPI.getUTF8Text();

                                        tessBaseAPI.setImage(tagIdNameBitmap);
                                        name = tessBaseAPI.getUTF8Text();
                                    } catch (Throwable e) {
                                        e.printStackTrace();
                                    } finally {
                                        if (tessBaseAPI != null) {
                                            tessBaseAPI.clear();
                                            tessBaseAPI.end();
                                        }
                                        tagIdNumberBitmap.recycle();
                                        tagIdNameBitmap.recycle();
                                    }


                                    long time = System.currentTimeMillis() - startTime;
                                    Log.v(tag, "time = " + time);

                                    final String finalIdNumber = idNumber;
                                    final String finalName = name;
                                    final Bitmap finalIdBitmap = idBitmap;
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isDestroyed()) {
                                                return;
                                            }
                                            if (IdRecognition.PORTRAIT_LISTENER != null) {
                                                IdRecognition.PORTRAIT_LISTENER.onResult(finalIdBitmap, finalName, finalIdNumber);
                                                IdRecognition.PORTRAIT_LISTENER = null;
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
        IdRecognition.PORTRAIT_LISTENER = null;
    }

    public void recognitionError(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(IdRecognitionPortraitActivity.this, "获取失败，请重试", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

}

