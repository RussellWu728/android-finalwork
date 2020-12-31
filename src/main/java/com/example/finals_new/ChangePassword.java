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

public class ChangePassword extends AppCompatActivity {
    EditText et_changepassword_ori_password;
    EditText et_changepassword_new_password;
    EditText et_changepassword_repeat_new_password;
    Button btn_changepassword_change;

    private CourseDataBase courseDataBase;
    private UserDao userDao;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        //隐藏顶部标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        Intent intent=getIntent();
        final String username=intent.getStringExtra("username");
        et_changepassword_ori_password=findViewById(R.id.et_changepassword_ori_password);
        et_changepassword_new_password=findViewById(R.id.et_changepassword_new_password);
        et_changepassword_repeat_new_password=findViewById(R.id.et_changepassword_repeat_new_password);
        btn_changepassword_change=findViewById(R.id.btn_changepassword_change);

        courseDataBase = Room.databaseBuilder(this,CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();

        btn_changepassword_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread t_change=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UserInfo newUser=userDao.findUserInfoByusername(username);
                            if(newUser.getPassword().equals(et_changepassword_ori_password.getText().toString())){
                                newUser.setPassword(et_changepassword_new_password.getText().toString());
                            }
                            else{
                                throw new Exception("ORI_PASSWORD WRONG！");
                            }
                            userDao.updateUserInfoByusername(newUser);
                            Intent intent_forgotpassword_login=new Intent();
                            intent_forgotpassword_login.putExtra("username",username);
                            intent_forgotpassword_login.setClass(ChangePassword.this,Login.class);
                            startActivity(intent_forgotpassword_login);
                            MainActivity.main.finish();
                            finish();

                        } catch (Exception e) {
                            e.printStackTrace();
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"原密码错误！",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                });
                if(et_changepassword_ori_password.getText().toString().trim().isEmpty()||et_changepassword_new_password.getText().toString().trim().isEmpty()||et_changepassword_repeat_new_password.getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"原密码、新密码均不能为空！",Toast.LENGTH_SHORT).show();
                }
                else if(et_changepassword_new_password.getText().toString().equals(et_changepassword_repeat_new_password.getText().toString())){
                    t_change.start();
                }
                else {
                    Toast.makeText(getApplicationContext(),"两次密码不同！",Toast.LENGTH_SHORT).show();
                }
            }
        });




    }
}
