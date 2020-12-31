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

public class ForgotPasswordVerify extends AppCompatActivity {
    EditText et_forgotpasswordverify_username;
    EditText et_forgotpasswordverify_phonenumber;
    EditText et_forgotpasswordverify_email;
    Button btn_forgotpasswordverify_verify;

    private CourseDataBase courseDataBase;
    private UserDao userDao;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpasswordverify);


        courseDataBase = Room.databaseBuilder(this,CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();

        //隐藏顶部标题栏
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        et_forgotpasswordverify_username=findViewById(R.id.et_forgotpasswordverify_username);
        et_forgotpasswordverify_email=findViewById(R.id.et_forgotpasswordverify_email);
        et_forgotpasswordverify_phonenumber=findViewById(R.id.et_forgotpasswordverify_phonenumber);
        btn_forgotpasswordverify_verify=findViewById(R.id.btn_forgotpasswordverify_verify);

        btn_forgotpasswordverify_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        UserInfo u=userDao.findUserInfoByusername(et_forgotpasswordverify_username.getText().toString());
                        if(u==null){
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"用户不存在！",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                        else if(u.getEmail().equals(et_forgotpasswordverify_email.getText().toString()) && u.getPhonenumber().equals(et_forgotpasswordverify_phonenumber.getText().toString())){
                            Intent intent_forgotrpasswordverify_forgotpassword=new Intent();
                            intent_forgotrpasswordverify_forgotpassword.setClass(ForgotPasswordVerify.this,ForgotPassword.class);
                            intent_forgotrpasswordverify_forgotpassword.putExtra("username",et_forgotpasswordverify_username.getText().toString());
                            intent_forgotrpasswordverify_forgotpassword.putExtra("phonenumber",et_forgotpasswordverify_phonenumber.getText().toString());
                            intent_forgotrpasswordverify_forgotpassword.putExtra("email",et_forgotpasswordverify_email.getText().toString());
                            startActivity(intent_forgotrpasswordverify_forgotpassword);
                            finish();
                        }
                        else{
                            Looper.prepare();
                            Toast.makeText(getApplicationContext(),"用户信息错误！",Toast.LENGTH_SHORT).show();
                            Looper.loop();
                        }
                    }
                }).start();

            }
        });

    }
}
