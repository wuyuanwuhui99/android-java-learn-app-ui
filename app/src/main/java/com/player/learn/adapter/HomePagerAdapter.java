package com.player.learn.adapter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.alibaba.fastjson.JSON;
import com.player.learn.fragment.RecommonFragment;
import com.player.learn.fragment.TabFragment;

import java.util.List;
import java.util.Map;

public class HomePagerAdapter extends FragmentPagerAdapter {
    private List<Map> classifyList;

    public HomePagerAdapter(FragmentManager fm,List<Map>classifyList){
        super(fm);
        this.classifyList = classifyList;

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (CharSequence) classifyList.get(position).get("classify");
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == 0){
            fragment = new RecommonFragment();
            Bundle bundle = new Bundle();
            bundle.putString("classifyList", JSON.toJSONString(classifyList));
            bundle.putString("classify",(String) classifyList.get(position).get("classify"));
            fragment.setArguments(bundle);
        }else{
            fragment = new TabFragment();
            Bundle bundle2 = new Bundle();
            bundle2.putString("classify", (String) classifyList.get(position).get("classify"));
            fragment.setArguments(bundle2);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return classifyList.size();
    }
}
