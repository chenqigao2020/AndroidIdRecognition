package com.cc.camear.id.recognition;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class FileCopyUtils {

    public String copyIfNot(Context context, String assetsFilePath, String dir, String name, boolean isForceCopy){
        String filePath = dir + "/" + name;
        if(!isForceCopy && new File(filePath).exists()){
            return filePath;
        }
        new File(dir).mkdirs();
        String tempFilePath = dir + "/" + "temp";
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open(assetsFilePath);
        } catch (Throwable throwable){
            throwable.printStackTrace();
        }
        FileOutputStream fileOutputStream = null;
        byte[] readBytes = new byte[1024];
        int readLength = 0;
        try {
            fileOutputStream = new FileOutputStream(tempFilePath);
            while ((readLength = inputStream.read(readBytes)) > 0){
                fileOutputStream.write(readBytes, 0, readLength);
            }
        } catch (Throwable ignored){
            filePath = null;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Throwable ignored){}
            }
            if(fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (Throwable ignored){}
            }
        }
        boolean isSuccess = false;
        if (filePath != null) {
            isSuccess = new File(tempFilePath).renameTo(new File(filePath));
        }
        if(!isSuccess){
            filePath = null;
        }
        return filePath;
    }

    /**
     * 复制到缓存文件夹
     * isForceCopy true执行复制 false如果文件已经存在则不复制
     * return null异常 str文件路径
     * **/
    public String copyToCache(Context context, String assetsFilePath, String name, boolean isForceCopy){
        return copyIfNot(context, assetsFilePath, context.getCacheDir().toString(), name, isForceCopy);
    }

}
