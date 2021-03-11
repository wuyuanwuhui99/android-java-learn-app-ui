package com.player.learn.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.player.learn.R;
import com.player.learn.common.Common;
import com.player.learn.config.Api;
import com.player.learn.entity.CourseEntity;
import com.player.learn.entity.EventEntity;
import com.player.learn.http.HttpUtil;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

public class RecommonFragment extends Fragment{

    private View view;
    private List<Map> classifyList;
    private int count = 2;//表示初始化只加载两个分类，后面的做滚动懒加载
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    private Boolean loading = false;//是否正在加载数据
    private String classify;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.recommon_fragment,container,false);
        getBannerData();
        Bundle bundle = getArguments();
        classifyList = JSON.parseArray(bundle.getString("classifyList"),Map.class);
        classify = bundle.getString("classify");
        fragmentManager =  getFragmentManager();
        initClassifyView();
        return view;
    }

    /**
     * @author: wuwenqiang
     * @description: 获取轮播数据
     * @date: 2021-01-23 14:16
     */
    public void getBannerData(){
        HttpUtil.doGetAsyn(Api.FINDALLBYCLASSIFY+"?classify=%E8%BD%AE%E6%92%AD&pageNum=0&pageSize=5", Common.token,new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){//请求成功
                    String data =  JSON.parseObject(msg.obj.toString()).get("data").toString();
                    List<CourseEntity> courseEntities= JSON.parseArray(data, CourseEntity.class);
                    initBannerView(courseEntities);
                }
            }
        });
    }

    //事件订阅者
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEvent(EventEntity eventEntity){
        if(classify.equals(eventEntity.getWhat())){//classify表示从HomeFragment派发过来的获取用户信息的名称
            loadMoreData();//加载更多数据
        }
    }

    /**
     * @author: wuwenqiang
     * @description: 初始化轮播图
     * @date: 2021-01-23 14:16
     */
    private void initBannerView(List list) {

        //方法二：使用自带的图片适配器
        Banner banner = (Banner)view.findViewById(R.id.banner);

        banner.setAdapter(new BannerImageAdapter<CourseEntity>(list) {
            @Override
            public void onBindView(BannerImageHolder holder, CourseEntity courseEntity, int position, int size) {
                //图片加载自己实现
                Glide.with(holder.imageView)
                        .load(Api.HOST+courseEntity.getLocalImg())
                        .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                        .into(holder.imageView);
            }
        }).setIndicator(new CircleIndicator(getContext())).setBannerRound(20.0f);
    }

    /**
     * @author: wuwenqiang
     * @description: 初始化加载fragment
     * @date: 2021-01-25 21:24
     */
    private void initClassifyView(){
        fragmentTransaction = fragmentManager.beginTransaction();
        for(int i = 1; i < count; i++){
            this.addFragment(i);
        }
        fragmentTransaction.commit();
    }

    /**
     * @author: wuwenqiang
     * @description: 逐个添加fragment
     * @date: 2021-01-25 21:24
     */
    private void addFragment(int index){
        ClassifyFragment classifyFragment = new ClassifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("classify", (String) classifyList.get(index).get("classify"));
        classifyFragment.setArguments(bundle);
        fragmentTransaction.add(R.id.course_category, classifyFragment);
    }

    /**
     * @author: wuwenqiang
     * @description: 容器滚动时做懒加载
     * @date: 2021-01-25 21:24
     */
    private void loadMoreData(){
        if(count < classifyList.size() && loading == false){
            loading = true;
            fragmentTransaction = fragmentManager.beginTransaction();
            this.addFragment(count);
            fragmentTransaction.commit();
            count++;
            loading = false;
        }
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
}
