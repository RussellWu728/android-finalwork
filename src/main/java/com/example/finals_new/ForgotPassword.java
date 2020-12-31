package com.example.finals_new;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ForgotPassword extends AppCompatActivity {
    EditText et_forgotpassword_username;
    EditText et_forgotpassword_password;
    EditText et_forgotpassword_phonenumber;
    EditText et_forgotpassword_email;
    EditText et_forgotpassword_repeatpassword;
    Button btn_forgotpassword_reset;
    LinearLayout ll_forgotpassword_repeatpassword;
    LinearLayout ll_forgotpassword_password;

    private CourseDataBase courseDataBase;
    private UserDao userDao;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);


        courseDataBase = Room.databaseBuilder(this,CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();

        //隐藏顶部标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        Intent intent_get=getIntent();

        ll_forgotpassword_password=findViewById(R.id.ll_forgotpassword_password);
        ll_forgotpassword_repeatpassword=findViewById(R.id.ll_forgotpassword_repeatpassword);
        et_forgotpassword_username=findViewById(R.id.et_forgotpassword_username);
        et_forgotpassword_password=findViewById(R.id.et_forgotpassword_password);
        et_forgotpassword_email=findViewById(R.id.et_forgotpassword_email);
        et_forgotpassword_phonenumber=findViewById(R.id.et_forgotpassword_phonenumber);
        et_forgotpassword_repeatpassword=findViewById(R.id.et_forgotpassword_repeatpassword);
        btn_forgotpassword_reset=findViewById(R.id.btn_forgotpassword_reset);


        et_forgotpassword_email.setText(intent_get.getStringExtra("email"));
        et_forgotpassword_username.setText(intent_get.getStringExtra("username"));
        et_forgotpassword_phonenumber.setText(intent_get.getStringExtra("phonenumber"));


        btn_forgotpassword_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread t_reset=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UserInfo newUser=userDao.findUserInfoByusername(et_forgotpassword_username.getText().toString());
                            newUser.setEmail(et_forgotpassword_email.getText().toString());
                            newUser.setPassword(et_forgotpassword_password.getText().toString());
                            newUser.setPhonenumber(et_forgotpassword_phonenumber.getText().toString());
                            userDao.updateUserInfoByusername(newUser);
                            Intent intent_forgotpassword_login=new Intent();
                            intent_forgotpassword_login.putExtra("username",et_forgotpassword_username.getText().toString());
                            intent_forgotpassword_login.setClass(ForgotPassword.this,Login.class);
                            startActivity(intent_forgotpassword_login);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"更新信息格式错误！",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
                if(et_forgotpassword_password.getText().toString().trim().isEmpty()||et_forgotpassword_repeatpassword.getText().toString().trim().isEmpty()||et_forgotpassword_email.getText().toString().trim().isEmpty()||et_forgotpassword_phonenumber.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"用户信息不能为空！",Toast.LENGTH_SHORT).show();
                }
                else if(et_forgotpassword_password.getText().toString().equals(et_forgotpassword_repeatpassword.getText().toString())){
                   t_reset.start();
                }
                else {
                    Toast.makeText(getApplicationContext(),"两次密码不同！",Toast.LENGTH_SHORT).show();
                }
            }

        });





    }

}
