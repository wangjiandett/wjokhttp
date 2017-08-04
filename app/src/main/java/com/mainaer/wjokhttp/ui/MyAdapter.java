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
import com.mainaer.wjokhttp.model.WeatherResponse;

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
    
    List<WeatherResponse.ForecastBean> mList = new ArrayList<>();
    
    Context mContext;
    
    public MyAdapter(Context context) {
        this.mContext = context;
    }
    
    public void setList(List<WeatherResponse.ForecastBean> list) {
        this.mList = list;
    }
    
    @Override
    public int getCount() {
        return mList.size();
    }
    
    @Override
    public WeatherResponse.ForecastBean getItem(int position) {
        return mList.get(position);
    }
    
    @Override
    public long getItemId(int position) {
        return 0;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.list_item, null);
        }
        
        TextView tvDateTitle = (TextView) convertView.findViewById(R.id.tv_date_title);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tv_date);
        TextView tvWenduTitle = (TextView) convertView.findViewById(R.id.tv_wendu_title);
        TextView tvWendu = (TextView) convertView.findViewById(R.id.tv_wendu);
        TextView tvWindTitle = (TextView) convertView.findViewById(R.id.tv_wind_title);
        TextView tvWind = (TextView) convertView.findViewById(R.id.tv_wind);
        TextView tvTypeTitle = (TextView) convertView.findViewById(R.id.tv_type_title);
        TextView tvType = (TextView) convertView.findViewById(R.id.tv_type);
    
        WeatherResponse.ForecastBean bean = getItem(position);
        
        tvDate.setText(bean.date);
        tvWendu.setText(bean.high+"-"+bean.low);
        tvWind.setText(bean.fengli+"-"+bean.fengxiang);
        tvType.setText(bean.type);
        
        return convertView;
    }
    
    
    private String format(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return format.format(time);
    }
    
}
