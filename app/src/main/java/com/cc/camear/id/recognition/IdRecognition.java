package com.cc.camear.id.recognition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class IdRecognition {

    protected static OnIdRecognitionListener LISTENER;

    public static void open(Context context, OnIdRecognitionListener listener) {
        LISTENER = listener;
        context.startActivity(new Intent(context, IdRecognitionActivity.class));
    }

    public interface OnIdRecognitionListener {
        void onResult(@Nullable Bitmap idBitmap, @Nullable String name, @Nullable String id);
    }

}
