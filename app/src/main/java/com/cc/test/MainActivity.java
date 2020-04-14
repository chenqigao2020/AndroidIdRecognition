package com.cc.test;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.cc.camear.id.recognition.IdRecognition;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.startButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdRecognition.open(MainActivity.this, new IdRecognition.OnIdRecognitionListener() {
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
    }
}
