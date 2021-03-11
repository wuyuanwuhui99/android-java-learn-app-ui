package com.player.learn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.player.learn.R;
import com.player.learn.config.Api;
import com.player.learn.view.RoundImageView;

import java.util.List;
import java.util.Map;

public class CategoryAdapter extends BaseAdapter {

    private List<Map>courseList;
    Context context;
    ViewHolder  viewHolder;

    public CategoryAdapter(Context context, List<Map> courseList){
        this.courseList = courseList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return courseList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        viewHolder = new ViewHolder();
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(R.layout.course_item, null);

            viewHolder.tv=(TextView) convertView.findViewById(R.id.course_name);
            viewHolder.tv.setText((String) courseList.get(position).get("name"));

            viewHolder.iv = (RoundImageView)convertView.findViewById(R.id.course_img);
            String localImg = (String) courseList.get(position).get("localImg");
            viewHolder.iv.setImageURL(Api.HOST + localImg);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }

    private static class ViewHolder{
        TextView tv;
        RoundImageView iv;
    }
}
