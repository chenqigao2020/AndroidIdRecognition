package com.cc.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cc.camera.id.recognition.IdRecognition;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.portraitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdRecognition.portrait(MainActivity.this, new IdRecognition.OnIdRecognitionPortraitListener() {
                    @Override
                    public void onResult(@Nullable Bitmap idBitmap, @Nullable String name, @Nullable String id) {
                        // code //
                        Toast.makeText(MainActivity.this, "name = " + name + ", id = " + id, Toast.LENGTH_LONG).show();
                        //释放
                        if (idBitmap != null) {
                            idBitmap.recycle();
                        }
                    }
                });
            }
        });
        findViewById(R.id.nationalEmblemButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdRecognition.nationalEmblem(MainActivity.this, new IdRecognition.OnIdRecognitionNationalEmblemListener() {
                    @Override
                    public void onResult(@Nullable Bitmap idBitmap) {
                        //释放
                        if (idBitmap != null) {
                            idBitmap.recycle();
                        }
                    }
                });
            }
        });
    }
}
