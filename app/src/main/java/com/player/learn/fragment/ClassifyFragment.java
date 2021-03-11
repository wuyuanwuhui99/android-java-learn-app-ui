package com.player.learn.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.player.learn.R;
import com.player.learn.adapter.CategoryAdapter;
import com.player.learn.common.Common;
import com.player.learn.config.Api;
import com.player.learn.http.HttpUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ClassifyFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.classify_item, container, false);
        TextView textView = (TextView)view.findViewById(R.id.classify_name);
        ImageView imageView = view.findViewById(R.id.icon_course);
        Bundle arguments = getArguments();
        Map<String, Integer> map = new HashMap<>();
        map.put("会计课程",R.mipmap.icon_kuaiji);
        map.put("室内设计",R.mipmap.icon_shineisheji);
        map.put("室外设计",R.mipmap.icon_shiwaisheji);
        map.put("工业自动化",R.mipmap.icon_gongyezidonghua);
        map.put("平面设计",R.mipmap.icon_pingmiansheji);
        map.put("影视动画",R.mipmap.icon_yingshidonghua);
        map.put("机械设计",R.mipmap.icon_jixiesheji);
        map.put("电脑办公",R.mipmap.icon_diannaobangong);
        map.put("程序开发",R.mipmap.icon_chengxukaifa);
        map.put("网页设计",R.mipmap.icon_wangyesheji);
        String classify = arguments.getString("classify");
        textView.setText(classify);
        Integer resourceId = map.get(classify);
        if(resourceId != null){
            imageView.setImageResource(resourceId);
        }
        getCourseData(classify);
        return view;
    }

    private void getCourseData(String classify){
        HttpUtil.doGetAsyn(Api.FINDALLBYCLASSIFY+"?classify="+classify+"&pageNum=0&pageSize=4", Common.token,new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){//请求成功
                    String data = JSON.parseObject(msg.obj.toString()).get("data").toString();
                    List<Map> courseList = JSON.parseArray(data,Map.class);
                    //渲染导航条
                    initCourseView(courseList);
                }
            }
        });
    }

    private void initCourseView(List<Map>courseList){
        GridView gridView = view.findViewById(R.id.course_grid);
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(),courseList);
        gridView.setAdapter(categoryAdapter);
    }
}
