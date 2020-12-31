package com.example.finals_new;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChangeDefaultcity extends AppCompatActivity {
    private CourseDataBase courseDataBase;
    private UserDao userDao;

    TextView tv_changedefaultcity_ori_defaultcity;
    TextView tv_changedefaultcity_nowcity;
    EditText et_changedefaultcity_new_defaultcity;
    Button btn_changedefaultcity_change;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changedefaultcity);
        //隐藏顶部标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        courseDataBase = Room.databaseBuilder(this, CourseDataBase.class, "word_database")
                .build();
        userDao = courseDataBase.getUserDao();


        Intent intent = getIntent();
        final String username = intent.getStringExtra("username");
        String nowcity = intent.getStringExtra("nowcity");
        Log.i("defaultcity",nowcity);
        tv_changedefaultcity_ori_defaultcity = findViewById(R.id.tv_changedefaultcity_ori_defaultcity);
        tv_changedefaultcity_nowcity = findViewById(R.id.tv_changedefaultcity_nowcity);

        et_changedefaultcity_new_defaultcity = findViewById(R.id.et_changedefaultcity_new_defaultcity);
        btn_changedefaultcity_change = findViewById(R.id.btn_changedefaultcity_change);


        new Thread(new Runnable() {
            @Override
            public void run() {
                tv_changedefaultcity_ori_defaultcity.setText(userDao.findDefaultcityByusername(username));
            }
        }).start();
        tv_changedefaultcity_nowcity.setText(nowcity);

        btn_changedefaultcity_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                                    .connectTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(10, TimeUnit.SECONDS)
                                    .writeTimeout(10, TimeUnit.SECONDS)
                                    .build();
                            final Request request = new Request.Builder()
                                    .url("https://geoapi.qweather.com/v2/city/lookup?key=8b86256edc3d4893afc47ce359eca1fa&location=" + et_changedefaultcity_new_defaultcity.getText().toString())
                                    .build();
                            Call call = okHttpClient.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    throw new RuntimeException();
                                }

                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {

                                        String str = response.body().string();
                                        Log.i("response", str);
                                        JSONObject jsonObject = new JSONObject(str);
                                        JSONArray jsonArray = jsonObject.getJSONArray("location");
                                        JSONObject jsonCityId = jsonArray.getJSONObject(0);

                                        String cityid = jsonCityId.getString("id");
                                        Log.i("response", cityid);
                                        UserInfo newUser = userDao.findUserInfoByusername(username);
                                        newUser.setDefaultcity(et_changedefaultcity_new_defaultcity.getText().toString());
                                        userDao.updateUserInfoByusername(newUser);
                                        Log.i("dao",newUser.getDefaultcity());



                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(), "修改默认城市成功！", Toast.LENGTH_SHORT).show();
                                        NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                                        NotificationChannel mChannel = new NotificationChannel("channel_1", "notice", NotificationManager.IMPORTANCE_LOW);

                                        nm.createNotificationChannel(mChannel);
                                        Notification.Builder builder = new Notification.Builder(getApplicationContext());
                                        //设置属性
                                        builder.setSmallIcon(R.drawable.position);


                                        builder.setContentTitle("默认地址已被更改");
                                        builder.setContentText("用户"+username+"的默认地址已更改为"+et_changedefaultcity_new_defaultcity.getText().toString());
                                        //这个要和创建通道的ID一致
                                        builder.setChannelId("channel_1");

                                        //创建对象,发送的就是这个对象
                                        Notification build = builder.build();
                                        nm.notify(1,build);
                                        finish();
                                        Looper.loop();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Log.e("response", "error");
                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(), "城市名不存在！", Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(), "城市名不存在！", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();

            }
        });
    }
}
