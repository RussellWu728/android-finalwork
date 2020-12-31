package com.example.finals_new;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.room.Room;

import com.example.finals_new.ui.setting.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;


public class Login extends AppCompatActivity {
    Button btn_login_login;
    EditText et_login_username;
    EditText et_login_password;
    Button btn_login_register;
    Button btn_login_forgatpassword;
    private CourseDataBase courseDataBase;
    private UserDao userDao;

//    Button btn_test;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        courseDataBase = Room.databaseBuilder(this,CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();

        //隐藏顶部标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        btn_login_login=findViewById(R.id.btn_login_login);
        btn_login_forgatpassword=findViewById(R.id.btn_login_forgotpassword);
        btn_login_register=findViewById(R.id.btn_login_register);
        et_login_username=findViewById(R.id.et_login_username);
        et_login_password=findViewById(R.id.et_login_password);


//        btn_test=findViewById(R.id.btn_test);

        Intent i=getIntent();
        if(i!=null){
            et_login_username.setText(i.getStringExtra("username"));
        }

        btn_login_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UserInfo u=userDao.findUserInfoByusername(et_login_username.getText().toString());
                            if (u==null){
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(),"用户名不存在或密码错误！",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }
                            else if (u.getPassword().equals(et_login_password.getText().toString())){
                                Intent intent_login_main=new Intent();
                                intent_login_main.setClass(Login.this,MainActivity.class);
                                intent_login_main.putExtra("username",et_login_username.getText().toString());
                                startActivity(intent_login_main);

                                finish();
                            }
                            else{
                                Looper.prepare();
                                Toast.makeText(getApplicationContext(),"用户名不存在或密码错误！",Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        } catch (Exception e) {

                        }
                    }
                }).start();

            }
        });


        btn_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_register=new Intent();
                intent_register.setClass(Login.this,RegisterVerify.class);
                startActivityForResult(intent_register,0);
            }
        });

        btn_login_forgatpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_forgotpassword=new Intent();
                intent_forgotpassword.setClass(Login.this,ForgotPasswordVerify.class);
                startActivityForResult(intent_forgotpassword,1);
            }
        });

//        btn_test.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        List<UserInfo> l=userDao.findAll();
//                        System.out.println(l);
//                    }
//                }).start();
//                System.out.println(et_login_username.getText());
//                System.out.println(et_login_username.getText()==null);
//
//            }
//        });

    }


}
