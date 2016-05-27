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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 类/接口描述
 *
 * @author wangjian
 * @date 2016/3/28.
 */
public class MyAdapter extends BaseAdapter {

    List<LoadResponse> mList = new ArrayList<>();

    Context mContext;

    public MyAdapter(Context context) {
        this.mContext = context;
    }

    public void setList(List<LoadResponse> list) {
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
        TextView tvSources = (TextView) convertView.findViewById(R.id.tv_sources);
        TextView tvTime = (TextView) convertView.findViewById(R.id.tv_time);
        LoadResponse response = (LoadResponse)getItem(position);
        text.setText(response.title);

        tvSources.setText(response.source);
        tvTime.setText(format(response.behot_time));
        return convertView;
    }

    private String format(long time){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(time);
    }

}
