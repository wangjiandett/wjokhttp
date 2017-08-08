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
import android.text.TextUtils;
import android.widget.Toast;

/**
 * 单例Toast，防止重复显示
 */
public class WToast {
    
    private static Toast toast;
    
    private static void getToast(Context context, CharSequence text, int res, int duration) {
        if (toast == null) {
            if (!TextUtils.isEmpty(text)) {
                toast = Toast.makeText(context.getApplicationContext(), text, duration);
            }
            else {
                toast = Toast.makeText(context.getApplicationContext(), res, duration);
            }
        }
        else {
            if (!TextUtils.isEmpty(text)) {
                toast.setText(text);
            }
            else {
                toast.setText(res);
            }
        }
        toast.show();
    }
    
    public static void showToast(Context context, CharSequence msg) {
        getToast(context, msg, 0, Toast.LENGTH_SHORT);
    }
    
    public static void showToast(Context context, int resId) {
        getToast(context, null, resId, Toast.LENGTH_SHORT);
    }
    
    public static void showLongToast(Context context, CharSequence msg) {
        getToast(context, msg, 0, Toast.LENGTH_LONG);
    }
    
    public static void showLongToast(Context context, int resId) {
        getToast(context, null, resId, Toast.LENGTH_LONG);
    }
    
    public static void cancelToast() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
