/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjoklib.okhttp.utils;

import android.util.Log;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/30.
 */
public class WLog {
    
    private static final String TAG = "okhttp";
    
    public static void d(String tag, String msg) {
        if (isLoggable()) {
            Log.d(tag, "[" + getFileLineMethod() + "]" + msg);
        }
    }
    
    public static void d(String msg) {
        if (isLoggable()) {
            Log.d(getFileName(), "[" + getLineMethod() + "]" + msg);
        }
    }
    
    public static void i(String msg) {
        if (isLoggable()) {
            android.util.Log.i(getFileName(), "[" + getLineMethod() + "]" + msg);
        }
    }
    
    public static void i(String tag, String msg) {
        if (isLoggable()) {
            Log.i(tag, "[" + getFileLineMethod() + "]" + msg);
        }
    }
    
    public static void w(String msg) {
        Log.w(getFileName(), "[" + getLineMethod() + "]" + msg);
    }
    
    public static void w(String tag, String msg) {
        if (isLoggable()) {
            Log.w(tag, "[" + getFileLineMethod() + "]" + msg);
        }
    }
    
    public static void v(String msg) {
        if (isLoggable()) {
            Log.v(getFileName(), "[" + getLineMethod() + "]" + msg);
        }
    }
    
    public static void v(String tag, String msg) {
        if (isLoggable()) {
            Log.v(tag, "[" + getLineMethod() + "]" + msg);
        }
    }
    
    public static void e(String msg) {
        if (isLoggable()) {
            String log = getFileName() + getLineMethod() + msg;
            Log.e(TAG, log);
        }
    }
    
    public static void e(String tag, String msg) {
        if (isLoggable()) {
            String log = getFileName() + getLineMethod() + msg;
            Log.e(tag, log);
        }
    }
    
    public static boolean isLoggable() {
        return true;
    }
    
    public static String getFileLineMethod() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getFileName()).append(" | ").append(
            traceElement.getLineNumber()).append(" | ").append(traceElement.getMethodName()).append("]");
        return toStringBuffer.toString();
    }
    
    public static String getLineMethod() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        StringBuffer toStringBuffer = new StringBuffer("[").append(traceElement.getLineNumber()).append(" | ").append(
            traceElement.getMethodName()).append("]");
        return toStringBuffer.toString();
    }
    
    public static String getFileName() {
        StackTraceElement traceElement = ((new Exception()).getStackTrace())[2];
        String fileName = traceElement.getFileName();
        int start = fileName.indexOf(".java");
        if (start >= 0) {
            fileName = fileName.substring(0, start);
        }
        return fileName;
    }
    
    
}
