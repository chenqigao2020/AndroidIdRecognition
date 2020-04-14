package com.cc.camera.id.recognition;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;

import androidx.annotation.Nullable;

public class IdRecognition {

    static OnIdRecognitionPortraitListener PORTRAIT_LISTENER;
    static OnIdRecognitionNationalEmblemListener NATIONAL_EMBLEM_LISTENER;

    //人像面
    public static void portrait(Context context, OnIdRecognitionPortraitListener listener) {
        PORTRAIT_LISTENER = listener;
        context.startActivity(new Intent(context, IdRecognitionPortraitActivity.class));
    }

    //国徽面
    public static void nationalEmblem(Context context, OnIdRecognitionNationalEmblemListener listener) {
        NATIONAL_EMBLEM_LISTENER = listener;
        context.startActivity(new Intent(context, IdRecognitionNationalEmblemActivity.class));
    }

    public interface OnIdRecognitionPortraitListener {
        void onResult(@Nullable Bitmap idBitmap, @Nullable String name, @Nullable String id);
    }

    public interface OnIdRecognitionNationalEmblemListener {
        void onResult(@Nullable Bitmap idBitmap);
    }

}
