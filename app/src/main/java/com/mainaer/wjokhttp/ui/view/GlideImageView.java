/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.ui.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bumptech.glide.GenericRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.mainaer.wjokhttp.R;


/**
 * 类/接口描述
 *
 * @author Harry
 * @date 2016/3/28.
 */
public class GlideImageView extends ImageView {
    int placeholderId = 0;
    int failureImageId = 0;
    boolean roundAsCircle = false;
    int roundedCornerRadius = 0;

    boolean failure = false;
    RequestManager manager;
    GenericRequestBuilder builder;
    GlideRoundTransform mRoundTrans;

    public GlideImageView(Context context) {
        super(context);
    }

    public GlideImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public GlideImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }
    
    private void init(Context context, AttributeSet attrs) {
        if (isInEditMode()) {
            return;
        }
        manager = Glide.with(context);
        mRoundTrans = new GlideRoundTransform(context);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.GlideImageView);
        try {
            placeholderId = a.getResourceId(R.styleable.GlideImageView_placeholderImage, placeholderId);
            failureImageId = a.getResourceId(R.styleable.GlideImageView_placeholderImage, failureImageId);
            roundAsCircle = a.getBoolean(R.styleable.GlideImageView_roundAsCircle, roundAsCircle);
            roundedCornerRadius = a.getDimensionPixelSize(R.styleable.GlideImageView_roundedCornerRadius,
                roundedCornerRadius);
        } finally {
            a.recycle();
        }

        if (failureImageId > 0) {
            this.setFailureImage(failureImageId);
        }
    }

    /**
     * Displays an fail image
     */
    public void setFailureImage(int id) {
        if (failure) {
            this.setImageResource(id);
        }
    }

    /**
     * Displays an image given by the url
     *
     * @param url url of the image
     */
    public void setImageURL(String url) {
        if (url == null) {
            return;
        }
        builder = manager.load(url).centerCrop().placeholder(placeholderId);
        builder.error(failureImageId);
        if (roundAsCircle) {
            builder.transform(mRoundTrans);
        }
        builder.into(this);
    }

    public class GlideRoundTransform extends BitmapTransformation {
        private float radius = 0f;

        public GlideRoundTransform(Context context) {
            this(context, roundedCornerRadius);
        }

        public GlideRoundTransform(Context context, int dp) {
            super(context);
            this.radius = Resources.getSystem().getDisplayMetrics().density * dp;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            return roundCrop(pool, toTransform);
        }

        private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
            if (source == null) {
                return null;
            }
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            if (result == null) {
                result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
            }
            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            return result;
        }

        @Override
        public String getId() {
            return getClass().getName() + Math.round(radius);
        }
    }
}
