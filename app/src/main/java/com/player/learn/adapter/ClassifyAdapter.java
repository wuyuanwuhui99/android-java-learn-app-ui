package com.player.learn.adapter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.player.learn.fragment.ClassifyFragment;

import java.util.List;
import java.util.Map;

public class ClassifyAdapter extends FragmentPagerAdapter {

    private List<Map> list;

    public ClassifyAdapter(@NonNull FragmentManager fm, List <Map>list) {
        super(fm);
        this.list = list;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new ClassifyFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", (String) list.get(position).get("title"));
        bundle.putString("list",(String) list.get(position).get("title"));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return list.size();
    }

}
