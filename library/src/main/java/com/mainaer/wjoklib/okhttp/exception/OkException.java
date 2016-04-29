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
package com.mainaer.wjoklib.okhttp.exception;

import com.google.gson.JsonSyntaxException;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * 对异常进行统一处理
 *
 * @author wangjian
 * @date 2015年11月9日
 */
public class OkException extends Exception {
    public static final int TYPE_NO_CONNECTION = 0x01;//网络错误
    public static final int TYPE_TIMEOUT = 0x02;//连接超时
    public static final int TYPE_NETWORK = 0x03;//网络未连接
    public static final int TYPE_AUTH = 0x04;//身份验证失败
    public static final int TYPE_SERVER = 0x05;//服务暂时不可用, 请稍后再试
    public static final int TYPE_PARSE = 0x10;// 数据解析错误
    public static final int TYPE_CLIENT = 0x11;


    public int mErrorType;

    public OkException(Exception error) {
        super(error);
        initType();
    }
    
    private void initType() {
        Throwable error = getCause();
        if (error == null) {
            return;
        }
        if (error instanceof UnknownHostException) {
            mErrorType = TYPE_NO_CONNECTION;
        }
        else if (error instanceof SocketTimeoutException) {
            mErrorType = TYPE_TIMEOUT;
        }
        else if (error instanceof JsonSyntaxException) {
            mErrorType = TYPE_PARSE;
        }
    }
    
    public int getType() {
        return mErrorType;
    }
}
