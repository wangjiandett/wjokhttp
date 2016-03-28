/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjoklib.okhttp;

import okhttp3.Cache;

/**
 * OKHttpConfig 自定义配置
 *
 * @author wangjian
 * @date 2016/3/23.
 */
public class OKHttpConfig {

    private long mConnectTimeout;
    private long mReadTimeout;
    private long mWriteTimeout;
    private Cache mCache;
    private Class<? extends OKBaseResponse> mBaseResponseClass;

    private OKHttpConfig(Builder builder) {
        this.mBaseResponseClass = builder.baseResponseClass;
        this.mConnectTimeout = builder.connectTimeout;
        this.mWriteTimeout = builder.writeTimeout;
        this.mReadTimeout = builder.readTimeout;
        this.mCache = builder.cache;
    }

    public long getConnectTimeout() {
        return mConnectTimeout;
    }

    public long getReadTimeout() {
        return mReadTimeout;
    }

    public long getWriteTimeout() {
        return mWriteTimeout;
    }

    public Cache getCache() {
        return mCache;
    }

    public Class<? extends OKBaseResponse> getBaseResponseClass() {
        return mBaseResponseClass;
    }

    public static class Builder {
        private Class<? extends OKBaseResponse> baseResponseClass;
        private long connectTimeout;
        private long readTimeout;
        private long writeTimeout;
        private Cache cache;

        public Builder setBaseResponseClass(Class<? extends OKBaseResponse> baseResponseClass) {
            this.baseResponseClass = baseResponseClass;
            return this;
        }

        public Builder setConnectTimeout(long connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(long readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public Builder setWriteTimeout(long writeTimeout) {
            this.writeTimeout = writeTimeout;
            return this;
        }

        public Builder setCache(Cache cache) {
            this.cache = cache;
            return this;
        }

        public OKHttpConfig build() {
            return new OKHttpConfig(this);
        }
    }
}
