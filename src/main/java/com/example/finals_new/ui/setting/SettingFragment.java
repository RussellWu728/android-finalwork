package com.example.finals_new.ui.setting;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;

import com.example.finals_new.ChangeDefaultcity;
import com.example.finals_new.ChangePassword;
import com.example.finals_new.CourseDataBase;
import com.example.finals_new.Login;
import com.example.finals_new.MainActivity;
import com.example.finals_new.R;
import com.example.finals_new.UserDao;

public class SettingFragment extends Fragment {


    private SettingViewModel settingViewModel;
    Button btn_setting_changepassword;
    public TextView tv_setting_username;
    Bundle bundle;
    Button btn_setting_quitid;
    private CourseDataBase courseDataBase;
    private UserDao userDao;
    TextView tv_setting_defaultcity;
    Button btn_setting_changedefaultcity;

    Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bd=msg.getData();
            tv_setting_defaultcity.setText(bd.getString("defaultcity"));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // tv_setting_username.setText(bundle.getString("username"));
    }

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel = ViewModelProviders.of(this).get(SettingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_setting, container, false);
        courseDataBase = Room.databaseBuilder(getActivity(), CourseDataBase.class,"word_database")
                .build();
        userDao=courseDataBase.getUserDao();


        tv_setting_defaultcity=root.findViewById(R.id.tv_setting_defaultcity);

        tv_setting_username=root.findViewById(R.id.tv_setting_username);
        btn_setting_changepassword=root.findViewById(R.id.btn_setting_changepassword);
        final MainActivity mainActivity= (MainActivity) getActivity();
        final String username=mainActivity.getUsername();
        tv_setting_username.setText(username);
        btn_setting_changedefaultcity=root.findViewById(R.id.btn_setting_changedefaultcity);
        new Thread(new Runnable() {
            @Override
            public void run() {
                userDao.findDefaultcityByusername(username);
                Bundle bd=new Bundle();
                Message msg=new Message();
                bd.putString("defaultcity",userDao.findDefaultcityByusername(username));
                msg.setData(bd);
                myHandler.sendMessage(msg);
            }
        }).start();


        btn_setting_quitid=root.findViewById(R.id.btn_setting_quitid);
        btn_setting_changepassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String username=tv_setting_username.getText().toString();
                Intent intent_setting_changepassword=new Intent();
                intent_setting_changepassword.putExtra("username",username);
                intent_setting_changepassword.setClass(getActivity(), ChangePassword.class);
                startActivity(intent_setting_changepassword);

            }
        });
        btn_setting_quitid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), Login.class);
                intent.putExtra("username",mainActivity.getUsername());
                startActivity(intent);

                mainActivity.finish();
            }
        });
        btn_setting_changedefaultcity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setClass(getActivity(), ChangeDefaultcity.class);
                intent.putExtra("username",username);
                String location=mainActivity.getLocation();
                String city;
                int start;
                int end;
                if(location.indexOf("县")!=-1) {
                    start = location.indexOf("省");
                    end = location.indexOf("市");

                }
                else if(location.indexOf("区")!=-1){
                    start = location.indexOf("市");
                    end = location.indexOf("区");
                }else {
                    start = location.indexOf("市");
                    end = location.indexOf("县");
                }
                city = location.substring(start + 1, end);
                intent.putExtra("nowcity",city);
                Log.i("location",mainActivity.getLocation());
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }
}