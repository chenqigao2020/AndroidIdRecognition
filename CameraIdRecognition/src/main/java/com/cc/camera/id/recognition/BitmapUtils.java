package com.cc.camera.id.recognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.FileOutputStream;

class BitmapUtils {

    public void save(Context context,  @NonNull Bitmap bitmap, String name) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + File.separator + "IdRecognition_" + name);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (fileOutputStream != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            try {
                fileOutputStream.close();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

}
