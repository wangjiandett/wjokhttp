/*
 *
 *  Copyright 2014-2016 wjokhttp.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.mainaer.wjoklib.okhttp.utils;

import android.content.Context;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

/**
 * 类或文件描述
 * <p>
 * Author：Administrator on 2017/7/17 17:54
 */
public class FileUtil {
    
    private static final String EX_DOWNLOAD = "WJOkhttp/download/";
    
    private static final String DOWNLOAD = "download";
    
    public static File getExternalFileDir(String dirName) {
        String status = android.os.Environment.getExternalStorageState();
        if (status.equals(android.os.Environment.MEDIA_MOUNTED)) {
            String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + dirName;
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
            return file;
        }
        return null;
    }
    
    public static File getInternalFileDir(Context context, String dirName) {
        File dir = context.getExternalFilesDir(dirName);
        if (dir != null) {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
        
        return dir;
    }
    
    public static File getDownLoadDir() {
        File file = getExternalFileDir(EX_DOWNLOAD);
        return file;
    }
    
    public static void close(Closeable closeable) {
        try {
            if(closeable != null){
                closeable.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
