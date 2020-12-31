package com.example.finals_new.ui.weather;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.List;

public class TabPagersAdapter extends FragmentStatePagerAdapter {
    public static final String TAB_TAG = "@dream@";

    private List<String> mTitles;

    public TabPagersAdapter(@NonNull FragmentManager fm, List<String> titles) {
        super(fm);
        mTitles = titles;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        ContentFragment fragment = new ContentFragment();
        String[] title = mTitles.get(position).split(TAB_TAG);
        fragment.setType(Integer.parseInt(title[1]));
        fragment.setTitle(title[0]);
        return fragment;
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }
    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position).split(TAB_TAG)[0];
    }
}
