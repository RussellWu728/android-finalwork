package com.example.finals_new;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class RegisterVerify extends AppCompatActivity {
    EditText et_registerverify_username;
    Button btn_registerverify_verify;
    private CourseDataBase courseDataBase;
    private UserDao userDao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registerverify);
        //隐藏顶部标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        et_registerverify_username=findViewById(R.id.et_registerverify_username);
        btn_registerverify_verify=findViewById(R.id.btn_registerverify_verify);


        courseDataBase = Room.databaseBuilder(this,CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();



        btn_registerverify_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        try {
                            UserInfo u=userDao.findUserInfoByusername(et_registerverify_username.getText().toString());
                            if(u==null){
                                Intent intent_registerverify_register=new Intent();
                                intent_registerverify_register.setClass(RegisterVerify.this,Register.class);
                                intent_registerverify_register.putExtra("username",et_registerverify_username.getText().toString());
                                startActivity(intent_registerverify_register);
                                finish();
                            }
                            else{
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(),"用户名已被注册！",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });






    }
}
