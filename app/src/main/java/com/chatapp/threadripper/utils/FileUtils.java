package com.chatapp.threadripper.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

public class FileUtils {

    public static String getExtension(File file) {
        String fileAbsolutePath = file.getAbsolutePath();
        String fileExtension = fileAbsolutePath.substring(fileAbsolutePath.lastIndexOf("."));
        return fileExtension;
    }

    public static File bitmap2File(Context context, Bitmap bitmap) throws IOException {
        // Create a file to write bitmap data
        String filename = "IMG_" + new Date().getTime() + ".png";
        File f = new File(context.getCacheDir(), filename);
        f.createNewFile();

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);
        byte[] bitmapData = bos.toByteArray();

        // Write the bytes in file
        FileOutputStream fos = new FileOutputStream(f);
        fos.write(bitmapData);
        fos.flush();
        fos.close();

        return f;
    }
}
