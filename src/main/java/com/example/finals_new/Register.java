package com.example.finals_new;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Register extends AppCompatActivity {
    EditText et_register_username;
    EditText et_register_password;
    EditText et_register_repeatpassword;
    EditText et_register_phonenumber;
    EditText et_register_email;
    Button btn_register;
    EditText et_register_defaultcity;
    private CourseDataBase courseDataBase;
    private UserDao userDao;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //隐藏顶部标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        final Intent intent_get=getIntent();

        courseDataBase = Room.databaseBuilder(this,CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();

        et_register_username=findViewById(R.id.et_register_username);
        et_register_password=findViewById(R.id.et_register_password);
        et_register_email=findViewById(R.id.et_register_email);
        et_register_phonenumber=findViewById(R.id.et_register_phonenumber);
        et_register_repeatpassword=findViewById(R.id.et_register_repeatpassword);
        btn_register=findViewById(R.id.btn_register_register);
        et_register_username.setText(intent_get.getStringExtra("username"));
        et_register_defaultcity=findViewById(R.id.et_register_defaultcity);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            Log.i("thread","start!");
                            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                    .connectTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(10,TimeUnit.SECONDS)
                                    .writeTimeout(10,TimeUnit.SECONDS)
                                    .build();
                            final Request request=new Request.Builder()
                                    .url("https://geoapi.qweather.com/v2/city/lookup?key=8b86256edc3d4893afc47ce359eca1fa&location="+et_register_defaultcity.getText().toString())
                                    .build();
                            Call call=okHttpClient.newCall(request);
                            call.enqueue(new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    throw new RuntimeException();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {

                                        String str=response.body().string();
                                        Log.i("response",str);
                                        JSONObject jsonObject = new JSONObject(str);
                                        JSONArray jsonArray = jsonObject.getJSONArray("location");
                                        JSONObject jsonCityId = jsonArray.getJSONObject(0);

                                        String cityid=jsonCityId.getString("id");
                                        Log.i("response",cityid);
                                        UserInfo u=new UserInfo(et_register_username.getText().toString(),et_register_password.getText().toString(),et_register_phonenumber.getText().toString(),et_register_email.getText().toString(),et_register_defaultcity.getText().toString());
                                        userDao.insertUserInfo(u);

                                        Intent intent_register_login=new Intent();
                                        intent_register_login.setClass(Register.this,Login.class);
                                        intent_register_login.putExtra("username",et_register_username.getText().toString());
                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(),"注册成功！",Toast.LENGTH_SHORT).show();

                                        startActivity(intent_register_login);
                                        finish();
                                        Looper.loop();

                                    }catch (Exception e){
                                        e.printStackTrace();
                                        Log.e("response","error");
                                        Looper.prepare();
                                        Toast.makeText(getApplicationContext(),"城市名不存在！",Toast.LENGTH_SHORT).show();
                                        Looper.loop();
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"城市名不存在！",Toast.LENGTH_SHORT).show();
                            Looper.loop();

                        }
                    }
                });
                if(et_register_password.getText().toString().equals(et_register_repeatpassword.getText().toString())){
                    t.start();
                }
                else{
                    Toast.makeText(getApplicationContext(),"两次密码不同！",Toast.LENGTH_SHORT).show();
                    et_register_password.setText("");
                    et_register_repeatpassword.setText("");
                }

            }
        });






    }
}
