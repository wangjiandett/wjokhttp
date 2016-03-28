/*
 * Copyright (C) 20015 MaiNaEr All rights reserved
 */
package com.mainaer.wjokhttp.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mainaer.wjokhttp.R;
import com.mainaer.wjokhttp.model.LoadResponse;
import com.mainaer.wjokhttp.ui.view.GlideImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/28.
 */
public class MyAdapter extends BaseAdapter {

    List<LoadResponse.Item> mList = new ArrayList<>();

    Context mContext;

    public MyAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<LoadResponse.Item> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null){
            convertView = View.inflate(mContext,R.layout.list_item,null);
        }
        TextView text = (TextView) convertView.findViewById(R.id.text);
        GlideImageView imageview = (GlideImageView) convertView.findViewById(R.id.imageview);
        LoadResponse.Item response = (LoadResponse.Item)getItem(position);
        text.setText(response.hellname);
        imageview.setImageURL(response.thumb_path);
        return convertView;
    }

}
