package com.player.learn.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.player.learn.R;
import com.player.learn.adapter.CategoryAdapter;
import com.player.learn.common.Common;
import com.player.learn.config.Api;
import com.player.learn.entity.EventEntity;
import com.player.learn.http.HttpUtil;
import com.player.learn.view.MyGridView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Map;

public class TabFragment extends Fragment {
    private boolean isFirstLoad = false;//初始化为false
    private String classify;//当前容器的标题
    private int pageNum=0;//分页页码
    private int pageSize = 8;//每页数量
    private int total = 0;//总数
    private View view;
    private List<Map>courseList;
    private MyGridView myGridView;
    private boolean loading=false;//是否正在加载数据

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        isFirstLoad = true;//视图创建完成，将变量置为true
        if (getUserVisibleHint()) {//如果Fragment可见进行数据加载
            onLazyLoad();
            isFirstLoad = false;
        }
        view = inflater.inflate(R.layout.tab_fragment,container,false);
        Bundle bundle = getArguments();
        classify = bundle.getString("classify");
        myGridView = (MyGridView) view.findViewById(R.id.course_grid);
        return view;
    }

    /**
     * @author: wuwenqiang
     * @description: 容器滚动懒加载数据
     * @date: 2021-01-25 23:19
     */
    private void onLazyLoad(){
        HttpUtil.doGetAsyn(Api.FINDALLBYCLASSIFY+"?classify="+classify+"&pageNum="+pageNum+"&pageSize="+pageSize, Common.token,new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1){//请求成功
                    JSONObject jsonObject = JSON.parseObject(msg.obj.toString());
                    String data =jsonObject.get("data").toString();
                    total = (int) jsonObject.get("total");
                    courseList = JSON.parseArray(data,Map.class);
                    initCourseView();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isFirstLoad = false;//视图销毁将变量置为false
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isFirstLoad && isVisibleToUser) {//视图变为可见并且是第一次加载
            onLazyLoad();
            isFirstLoad = false;
        }
    }

    /**
     * @author: wuwenqiang
     * @description: 渲染列表
     * @date: 2021-01-25 23:19
     */
    private void initCourseView(){
        CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(),courseList);
        myGridView.setAdapter(categoryAdapter);
    }

    //事件订阅者
    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onEvent(EventEntity eventEntity){
        if(classify.equals(eventEntity.getWhat())){//classify表示从HomeFragment派发过来的获取用户信息的名称
            loadMoreData();//加载更多数据
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


    private void loadMoreData(){
        if(total > courseList.size() && loading == false){
            loading = true;
            HttpUtil.doGetAsyn(Api.FINDALLBYCLASSIFY+"?classify="+classify+"&pageNum="+pageNum+"&pageSize="+pageSize, Common.token,new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1){//请求成功
                        String data = JSON.parseObject(msg.obj.toString()).get("data").toString();
                        List<Map> myCourseList = JSON.parseArray(data,Map.class);
                        courseList.addAll(myCourseList);
                        initCourseView();
                        //渲染导航条
                    }
                    loading = false;
                }
            });
        }

    }
}
