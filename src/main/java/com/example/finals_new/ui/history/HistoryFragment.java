package com.example.finals_new.ui.history;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.example.finals_new.ChangeDefaultcity;
import com.example.finals_new.CourseDataBase;
import com.example.finals_new.MainActivity;
import com.example.finals_new.R;
import com.example.finals_new.UserDao;
import com.example.finals_new.UserInfo;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HistoryFragment extends Fragment {
    ListView lv_history;
    private CourseDataBase courseDataBase;
    private UserDao userDao;
    private HistoryViewModel HistoryViewModel;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    try {
                        Bundle bd = msg.getData();
                        String str = bd.getString("weatherinfo");
                        String cityname=bd.getString("location");
                        String[] kv = {"地名："+cityname};

                        JSONObject jsonObject = new JSONObject(str);

                        JSONArray json_weather = jsonObject.getJSONArray("daily");
                        int i;
                        for(i=0;i<7;i++){
                            JSONObject json_day=json_weather.getJSONObject(i);
                            String date=json_day.getString("fxDate");
                            String maxtemp=json_day.getString("tempMax");
                            String mintemp=json_day.getString("tempMin");
                            kv = Arrays.copyOf(kv, kv.length + 1);
                            kv[kv.length - 1] = date + " : " + maxtemp+"℃/"+mintemp+"℃";
                        }


                        final String[] item = Arrays.copyOf(kv, kv.length);
                        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, item);
                        lv_history.setAdapter(mArrayAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("getcity","GG");

                        //break;
                    }


                default:
                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        HistoryViewModel =
                ViewModelProviders.of(this).get(HistoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        courseDataBase = Room.databaseBuilder(getActivity(),CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();


        MainActivity ma=(MainActivity)getActivity();
        final String username=ma.getUsername();
        lv_history=root.findViewById(R.id.lv_history);


        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("thread","start!");
                OkHttpClient okHttpClient=new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(10,TimeUnit.SECONDS)
                        .writeTimeout(10,TimeUnit.SECONDS)
                        .build();
                UserInfo newUser=userDao.findUserInfoByusername(username);
                final String defaultcity=newUser.getDefaultcity();
                final Request request=new Request.Builder()
                        .url("https://geoapi.qweather.com/v2/city/lookup?key=8b86256edc3d4893afc47ce359eca1fa&location="+defaultcity)
                        .build();
                Call call=okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.i("response","GG1");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {

                            String str=response.body().string();

                            Bundle bd=new Bundle();
                            bd.putString("cityinfo",str);
                            JSONObject jsonObject = new JSONObject(str);

                            JSONArray jsonArray = jsonObject.getJSONArray("location");
                            JSONObject jsonCityId = jsonArray.getJSONObject(0);

                            String cityid=jsonCityId.getString("id");
                            Log.i("response",cityid);
                            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                    .connectTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(10,TimeUnit.SECONDS)
                                    .writeTimeout(10,TimeUnit.SECONDS)
                                    .build();
                            final Request request=new Request.Builder()
                                    .url("https://devapi.qweather.com/v7/weather/7d?key=8b86256edc3d4893afc47ce359eca1fa&location="+cityid)
                                    .build();
                            Call call_wether=okHttpClient.newCall(request);
                            call_wether.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Log.i("response", "GG");
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {

                                        String str = response.body().string();
                                        Log.i("response", str);
                                        Bundle bd = new Bundle();
                                        bd.putString("weatherinfo", str);
                                        bd.putString("location",defaultcity);
                                        Message msg=new Message();
                                        msg.setData(bd);
                                        msg.what=1;
                                        mHandler.sendMessage(msg);


                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("response", "error2");
                                        Looper.prepare();
                                        Toast.makeText(getActivity().getApplicationContext(), "城市名不存在！", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }

                                }
                            });



                        }catch (Exception e){
                            e.printStackTrace();
                            Log.e("response","error");
                            Looper.prepare();
                            Toast.makeText(getActivity().getApplicationContext(),"城市名不存在！",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }

                    }
                });
            }
        });
        t.start();
        return root;
    }
}