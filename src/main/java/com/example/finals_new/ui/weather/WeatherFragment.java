package com.example.finals_new.ui.weather;

import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.finals_new.MainActivity;
import com.example.finals_new.R;
import com.google.android.material.tabs.TabLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherFragment extends Fragment {

    private WeatherViewModel weatherViewModel;
    TabLayout tl_weather;
    TabHost th;
    ViewPager vp_weather;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //weatherViewModel = ViewModelProviders.of(this).get(WeatherViewModel.class);
        View root = inflater.inflate(R.layout.fragment_weather, container, false);



        tl_weather=root.findViewById(R.id.tl_weather);
        tl_weather.setTabMode(TabLayout.MODE_FIXED);


        //th=root.findViewById(R.id.th);



        vp_weather=root.findViewById(R.id.vp_weather);
        List<View> listView = new ArrayList<>() ;
        View vPagerToday = inflater.inflate(R.layout.pager_weather_today, null);
        View vPagerRecommend = inflater.inflate(R.layout.pager_weather_recommend, null);
        listView.add (vPagerToday) ;
        listView.add (vPagerRecommend) ;
        String[] titles = getResources().getStringArray(R.array.home_video_tab);
        vp_weather.setAdapter(new TabPagersAdapter(getChildFragmentManager(), Arrays.asList(titles))) ;
        tl_weather.setupWithViewPager(vp_weather) ;



        return root;
    }
}