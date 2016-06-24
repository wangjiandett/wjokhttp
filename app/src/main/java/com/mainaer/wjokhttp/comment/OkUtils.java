/*
 * Copyright 2014-2015 ieclipse.cn.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mainaer.wjokhttp.comment;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.mainaer.wjokhttp.R;
import com.mainaer.wjoklib.okhttp.exception.OkHttpError;

/**
 * Okhttp Utils
 *
 * @author wangjian
 * @date 2015年11月16日
 */
public final class OkUtils {

    /**
     * private constructor
     */
    private OkUtils() {
    }
    
    public static void toastError(Context context, OkHttpError error) {
        String msg = getError(context, error);
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    
    public static void showError(TextView tv, OkHttpError error) {
        tv.setText(getError(tv.getContext(), error));
    }

    // 自定义异常
    private static String getError(Context context, OkHttpError error) {
        int type = error.getType();
        int resId;
        if (type == OkHttpError.TYPE_NO_CONNECTION) {
            resId = R.string.error_type_no_network;
        }
        else if (type == OkHttpError.TYPE_TIMEOUT) {
            resId = R.string.error_type_timeout;
        }
        else if (type == OkHttpError.TYPE_PARSE) {
            resId = R.string.error_type_parse;
        }
        else {
            resId = R.string.error_type_unknown;
        }
        return context.getString(resId);
    }
}
