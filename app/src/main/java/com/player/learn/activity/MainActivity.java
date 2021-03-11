package com.player.learn.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.player.learn.R;
import com.player.learn.common.Common;
import com.player.learn.config.Api;
import com.player.learn.entity.EventEntity;
import com.player.learn.fragment.HomeFragment;
import com.player.learn.fragment.MyCourseFragment;
import com.player.learn.fragment.StudyFragment;
import com.player.learn.fragment.UserFragment;
import com.player.learn.http.HttpCallBackListener;
import com.player.learn.http.HttpUtil;
import com.player.learn.http.LoadImagesTask;
import com.player.learn.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//extends AppCompatActivity
public class MainActivity  extends FragmentActivity implements View.OnClickListener{

    private ViewPager mViewPager;//容器
    private List<Fragment> listFragment = new ArrayList<Fragment>();

    //导航栏布局栏
    LinearLayout homeLinearLayout;
    LinearLayout studyLinearLayout;
    LinearLayout myCourseLinearLayout;
    LinearLayout userLinearLayout;

    //导航栏图标
    ImageView homeImg;
    ImageView studyImg;
    ImageView  myCourseImg;
    ImageView userImg;

    //导航栏文字
    TextView homeText;
    TextView studyText;
    TextView myCourseText;
    TextView userText;

    private SharedPreferences sp;//缓存
    private JSONObject userData;//用户数据
    private String token;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();//初始化视图
        initEvent();//绑定底部导航栏事件
        setSelect(0);//设置默认选中的tab

        getToken();//获取缓存中的token
        getUserData();//获取用户数据

    }


    private void initEvent() {
        homeLinearLayout.setOnClickListener(this);
        studyLinearLayout.setOnClickListener(this);
        myCourseLinearLayout.setOnClickListener(this);
        userLinearLayout.setOnClickListener(this);
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);

        //导航栏对应内容区布局
        Fragment homeFragment = new HomeFragment();
        Fragment studyFragment = new StudyFragment();
        Fragment myCourseFragment = new MyCourseFragment();
        Fragment userFragment = new UserFragment();

        //导航栏布局栏
        homeLinearLayout = (LinearLayout)findViewById(R.id.home);
        studyLinearLayout = (LinearLayout)findViewById(R.id.study);
        myCourseLinearLayout = (LinearLayout)findViewById(R.id.my_course);
        userLinearLayout = (LinearLayout)findViewById(R.id.user_center);

        //导航栏图标
        homeImg = (ImageView)findViewById(R.id.home_img);
        studyImg = (ImageView)findViewById(R.id.study_img);
        myCourseImg = (ImageView)findViewById(R.id.my_course_img);
        userImg = (ImageView)findViewById(R.id.user_img);

        //导航栏文字
        homeText = (TextView)findViewById(R.id.home_text);
        studyText = (TextView)findViewById(R.id.study_text);
        myCourseText = (TextView)findViewById(R.id.my_course_text);
        userText = (TextView)findViewById(R.id.user_text);


        listFragment.add(homeFragment);
        listFragment.add(studyFragment);
        listFragment.add(myCourseFragment);
        listFragment.add(userFragment);

        FragmentPagerAdapter mAdapter =new FragmentPagerAdapter(getSupportFragmentManager()) {  //适配器直接new出来
            @Override
            public Fragment getItem(int position) {
                return listFragment.get(position);//直接返回
            }

            @Override
            public int getCount() {
                return listFragment.size(); //放回tab数量
            }
        };

        mViewPager.setAdapter(mAdapter);  //加载适配器
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {  //监听界面拖动
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                int currentItem=mViewPager.getCurrentItem(); //获取当前界面
                resetImg();  //将所有图标变暗
                tab(currentItem); //切换图标亮度
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void tab(int i){  //用于屏幕脱拖动时切换底下图标，只在监听屏幕拖动中调用
        int color = this.getResources().getColor(R.color.navigate_active);
        switch (i){
            case 0:{
                homeImg.setImageResource(R.mipmap.icon_home_active);
                homeText.setTextColor(color);
                break;
            }
            case 1:
            {
                studyImg.setImageResource(R.mipmap.icon_study_active);
                studyText.setTextColor(color);
                break;
            }
            case 2:
            {
                myCourseImg.setImageResource(R.mipmap.icon_my_course_active);
                myCourseText.setTextColor(color);
                break;
            }
            case 3:
            {
                userImg.setImageResource(R.mipmap.icon_user_active);
                userText.setTextColor(color);
                break;
            }
        }
    }

    //自定义一个方法
    private void setSelect(int i){
        mViewPager.setCurrentItem(i);//切换界面
    }

    @Override
    public void onClick(View view) {  //设置点击的为；亮色
        resetImg();
        switch (view.getId()){
            case R.id.home:{
                setSelect(0);
                homeImg.setImageResource(R.mipmap.icon_home_active);
                break;
            }
            case R.id.study:
            {
                setSelect(1);
                studyImg.setImageResource(R.mipmap.icon_study_active);
                break;
            }
            case R.id.my_course:
            {
                setSelect(2);
                myCourseImg.setImageResource(R.mipmap.icon_my_course_active);
                break;
            }
            case R.id.user_center:
            {
                setSelect(3);
                userImg.setImageResource(R.mipmap.icon_user_active);
                break;
            }

        }
    }

    //设置暗色
    private void resetImg() {
        homeImg.setImageResource(R.mipmap.icon_home);
        studyImg.setImageResource(R.mipmap.icon_study);
        myCourseImg.setImageResource(R.mipmap.icon_my_course);
        userImg.setImageResource(R.mipmap.icon_user);

        int color = this.getResources().getColor(R.color.navigate);
        homeText.setTextColor(color);
        studyText.setTextColor(color);
        myCourseText.setTextColor(color);
        userText.setTextColor(color);
    }


    /**
     * @author: wuwenqiang
     * @description: 设置缓存中的token
     * @date: 2021-01-16 11:59
     */
    private void getToken(){
        sp = getSharedPreferences("user", 0);
        token = sp != null ? sp.getString("token",null):null;
    }

    /**
     * @author: wuwenqiang
     * @description: 设置头像
     * @date: 2021-01-13 22:24
     */
    private void setAvater() throws JSONException {
        if(userData!=null){
            String img = Api.HOST + userData.getString("avater");
            CircleImageView circleImageView = (CircleImageView)findViewById(R.id.avater);
            new LoadImagesTask(circleImageView).execute(img);
        }
    }

    /**
     * @author: wuwenqiang
     * @description: 获取用户信息，从缓存中获取token，如果没有token，随机从数据库中查询一个公共的账号
     * @date: 2021-01-13 22:24
     */
    public void getUserData(){
        sp = getSharedPreferences("user", 0);
        String token = sp != null ? sp.getString("token",null):null;
        HttpUtil.doGetAsyn(Api.GETUSERDATA,token, new HttpCallBackListener() {
            @Override
            public void onFinish(JSONObject jsonObject) throws JSONException {
                userData = jsonObject.getJSONObject("data");
                String token = jsonObject.getString("token");
                Common.token = token;
                //设置缓存
                sp.edit().putString("token", token).commit();
                setAvater();//设置用户头像
                EventBus.getDefault().postSticky(new EventEntity("getToken",token));
            }

            @Override
            public void onError(Exception e) {
                System.out.println(e);
            }
        });
    }

}
