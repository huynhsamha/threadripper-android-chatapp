package com.chatapp.threadripper.utils;

import java.io.File;

public class FileUtils {

    public static String getExtension(File file) {
        String fileAbsolutePath = file.getAbsolutePath();
        String fileExtension = fileAbsolutePath.substring(fileAbsolutePath.lastIndexOf("."));
        return fileExtension;
    }
}
