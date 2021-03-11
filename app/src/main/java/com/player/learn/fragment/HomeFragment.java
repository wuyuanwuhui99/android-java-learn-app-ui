package com.player.learn.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.player.learn.R;
import com.player.learn.adapter.HomePagerAdapter;
import com.player.learn.config.Api;
import com.player.learn.entity.EventEntity;
import com.player.learn.http.HttpUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private String token;
    FragmentManager fragmentManager;
    private View view;
    private String tabSelectedTag = "推荐";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.home_fragment,container,false);
        fragmentManager = getFragmentManager();
        onScrollview();//监听滚动事件，滚动懒加载
        return view;
    }

    //事件订阅者
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEvent(EventEntity eventEntity){
        if( "getToken".equals(eventEntity.getWhat())){//getToken表示从MainActivity派发过来的获取用户信息的标识
            token = (String) eventEntity.getData();
            getAllClassify();
        }
    }

    private void onScrollview(){
        ScrollView myScrollView = (ScrollView)view.findViewById(R.id.myScrollView);
        myScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    View view = ((ScrollView) v).getChildAt(0);
                    if (view.getMeasuredHeight() <= v.getScrollY() + v.getHeight()) {
                        //加载数据代码
                        EventBus.getDefault().postSticky(new EventEntity(tabSelectedTag,null));
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * @author: wuwenqiang
     * @description: 获取所有分类
     * @date: 2021-01-16 12:00
     */
    private void getAllClassify(){
        HttpUtil.doGetAsyn(Api.FINDALLBYCLASSIFYGROUP,token,new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){//请求成功
                    String data = JSON.parseObject(msg.obj.toString()).get("data").toString();
                    List<Map> classifyList = JSON.parseArray(data,Map.class);
                    //渲染导航条
                    initNavView(classifyList);
                }
            }
        });
    }

    /**
     * @author: wuwenqiang
     * @description: 渲染导航条
     * @date: 2021-01-16 12:00
     */
    private void initNavView(List<Map> classifyList) {
        HorizontalScrollView scrollView = (HorizontalScrollView) view.findViewById(R.id.scrollView);
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vp);

        HomePagerAdapter adapter = new HomePagerAdapter(fragmentManager,classifyList);



        viewPager.setAdapter(adapter);
        List<View> navViews = new ArrayList<>();
        int index = 0;
        for (Map map:classifyList){
            View view = getLayoutInflater().inflate(R.layout.nav_item, null);
            TextView textView = (TextView)view.findViewById(R.id.tvbutn);
            textView.setText((CharSequence) map.get("classsify"));
            View  underline = view.findViewById(R.id.underline);
            if(index == 0){
                underline.setVisibility(View.VISIBLE);
            }else {
                underline.setVisibility(View.INVISIBLE);
            }
            navViews.add(view);
            scrollView.addView(view);
            view.setTag(index);
            index++;
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (View navView :navViews){
                        navView.findViewById(R.id.underline).setVisibility(View.INVISIBLE);
                    }
                    v.findViewById(R.id.underline).setVisibility(View.VISIBLE);
                    viewPager.setCurrentItem((Integer) v.getTag());
                }
            });
        }

//        tabLayout.setupWithViewPager(viewPager);
//        tabLayout.addOnTabSelectedListener(new TabLayout.BaseOnTabSelectedListener(){
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                CharSequence text = tab.getText();
//                tabSelectedTag = (String) text;
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }
}
