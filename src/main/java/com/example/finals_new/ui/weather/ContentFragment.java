package com.example.finals_new.ui.weather;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.baidu.location.BDLocation;
import com.example.finals_new.CourseDataBase;
import com.example.finals_new.MainActivity;
import com.example.finals_new.R;
import com.example.finals_new.UserDao;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ContentFragment extends Fragment {
    ContentFragment cf=this;
    private View viewContent;
    private int mType = 0;
    private String mTitle;
    private BDLocation bdLocation;			//百度地图定位信息
    private Bitmap weatherBitmap;			//天气缩略图bitmap
    String location;
    String city;
    private CourseDataBase courseDataBase;
    private UserDao userDao;
    public String getCity() {
        return city;
    }

    public void setType(int mType) {
        this.mType = mType;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
    private final int CAMERA_REQUEST = 7777;
    EditText et_weather_searchcity;
    ListView lv_weather;
    Button btn_weather_search;
    Map dic_weatherinfo;
    ImageView iv_QRcode;
    Button btn_camera;
    ImageView iv_camera_show;
    Button btn_weather_dingwei;


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

                        JSONObject json_nowweather = jsonObject.getJSONObject("now");
                        Log.i("response",json_nowweather.getString("obsTime"));
                        Iterator<?> keys = json_nowweather.keys();
                        while (keys.hasNext()) {
                            String k = keys.next().toString();
                            if(dic_weatherinfo.get(k)!=null) {
                                String v = json_nowweather.getString(k);
                                kv = Arrays.copyOf(kv, kv.length + 1);
                                kv[kv.length - 1] = dic_weatherinfo.get(k) + " : " + v;
                            }

                        }
                        final String[] item = Arrays.copyOf(kv, kv.length);
                        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, item);
                        lv_weather.setAdapter(mArrayAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("getcity","GG");
//                        Toast.makeText(getActivity().getApplicationContext(), "获取城市ID失败！", Toast.LENGTH_SHORT).show();
                        //break;
                    }

                case 2:
                    try {
                        Bundle bd = msg.getData();
                        String str = bd.getString("weatherinfo");
                        String cityname=bd.getString("location");
                        String[] kv = {"地名："+cityname};

                        JSONObject jsonObject = new JSONObject(str);

                        JSONObject json_nowweather = jsonObject.getJSONObject("now");
                        Log.i("response",json_nowweather.getString("obsTime"));
                        Iterator<?> keys = json_nowweather.keys();
                        while (keys.hasNext()) {
                            String k = keys.next().toString();
                            if(dic_weatherinfo.get(k)!=null) {
                                String v = json_nowweather.getString(k);
                                kv = Arrays.copyOf(kv, kv.length + 1);
                                kv[kv.length - 1] = dic_weatherinfo.get(k) + " : " + v;
                            }

                        }
                        final String[] item = Arrays.copyOf(kv, kv.length);
                        ArrayAdapter<String> mArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, item);
                        lv_weather.setAdapter(mArrayAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.i("getcity","GG");
//                        Toast.makeText(getActivity().getApplicationContext(), "获取城市ID失败！", Toast.LENGTH_SHORT).show();
                        //break;
                    }

                default:
                    break;
            }
        }
    };
    private void initdic(){
        dic_weatherinfo=new HashMap();
        dic_weatherinfo.put("obsTime","观测时间");
        dic_weatherinfo.put("temp","温度(℃)");
        dic_weatherinfo.put("feelsLike","体感温度(℃)");
        dic_weatherinfo.put("text","天气状况");
        dic_weatherinfo.put("windDir","风向");
        dic_weatherinfo.put("windScale","风力等级");
        dic_weatherinfo.put("windSpeed","风速(km/h)");
        dic_weatherinfo.put("humidity","相对湿度(%)");
        dic_weatherinfo.put("precip","降水量(mm)");
        dic_weatherinfo.put("vis","能见度(km)");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == -1) {
            iv_camera_show=viewContent.findViewById(R.id.iv_camera_show);
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            iv_camera_show.setImageBitmap(photo);
        }
    }


    public static Bitmap createQRCodeBitmap(String content, int width,int height,
                                            String character_set,String error_correction_level,
                                            String margin,int color_black, int color_white) {
        // 字符串内容判空
        if (TextUtils.isEmpty(content)) {
            return null;
        }
        // 宽和高>=0
        if (width < 0 || height < 0) {
            return null;
        }
        try {
            /** 1.设置二维码相关配置 */
            Hashtable<EncodeHintType, String> hints = new Hashtable<>();
            // 字符转码格式设置
            if (!TextUtils.isEmpty(character_set)) {
                hints.put(EncodeHintType.CHARACTER_SET, character_set);
            }
            // 容错率设置
            if (!TextUtils.isEmpty(error_correction_level)) {
                hints.put(EncodeHintType.ERROR_CORRECTION, error_correction_level);
            }
            // 空白边距设置
            if (!TextUtils.isEmpty(margin)) {
                hints.put(EncodeHintType.MARGIN, margin);
            }
            /** 2.将配置参数传入到QRCodeWriter的encode方法生成BitMatrix(位矩阵)对象 */
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            /** 3.创建像素数组,并根据BitMatrix(位矩阵)对象为数组元素赋颜色值 */
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    //bitMatrix.get(x,y)方法返回true是黑色色块，false是白色色块
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = color_black;//黑色色块像素设置
                    } else {
                        pixels[y * width + x] = color_white;// 白色色块像素设置
                    }
                }
            }
            /** 4.创建Bitmap对象,根据像素数组设置Bitmap每个像素点的颜色值,并返回Bitmap对象 */
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if(mType==0) {
            viewContent = inflater.inflate(R.layout.pager_weather_today, container, false);
            lv_weather=viewContent.findViewById(R.id.lv_weather);
            et_weather_searchcity=viewContent.findViewById(R.id.et_weather_searchcity);
            initdic();
            btn_weather_search=viewContent.findViewById(R.id.btn_weather_search);
            btn_weather_dingwei=viewContent.findViewById(R.id.btn_weather_dingwei);
            courseDataBase = Room.databaseBuilder(getActivity(),CourseDataBase.class,"word_database")
                    .build();
            userDao=courseDataBase.getUserDao();
            MainActivity mainActivity= (MainActivity) getActivity();
            final String username=mainActivity.getUsername();

            Thread t=new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.i("thread","start!");
                    city=userDao.findDefaultcityByusername(username);
                    OkHttpClient okHttpClient=new OkHttpClient.Builder()
                            .connectTimeout(10, TimeUnit.SECONDS)
                            .readTimeout(10,TimeUnit.SECONDS)
                            .writeTimeout(10,TimeUnit.SECONDS)
                            .build();
                    final Request request=new Request.Builder()
                            .url("https://geoapi.qweather.com/v2/city/lookup?key=8b86256edc3d4893afc47ce359eca1fa&location="+city)
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
                                Log.i("response",city);
                                String str=response.body().string();
                                Log.i("response",str);
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
                                        .url("https://devapi.qweather.com/v7/weather/now?key=8b86256edc3d4893afc47ce359eca1fa&location="+cityid)
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
                                            bd.putString("location",city);
                                            Message msg=new Message();
                                            msg.setData(bd);
                                            msg.what=2;
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
            btn_weather_dingwei.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MainActivity ma=(MainActivity)getActivity();
                    try {
                        location = ma.getLocation();
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

                    }catch (Exception e){
                        Log.i("getcity","error");
                    }
                    Thread t=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("thread","start!");
                            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                    .connectTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(10,TimeUnit.SECONDS)
                                    .writeTimeout(10,TimeUnit.SECONDS)
                                    .build();
                            final Request request=new Request.Builder()
                                    .url("https://geoapi.qweather.com/v2/city/lookup?key=8b86256edc3d4893afc47ce359eca1fa&location="+city)
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
                                        Log.i("response",city);
                                        String str=response.body().string();
                                        Log.i("response",str);
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
                                                .url("https://devapi.qweather.com/v7/weather/now?key=8b86256edc3d4893afc47ce359eca1fa&location="+cityid)
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
                                                    bd.putString("location",location);
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
                }
            });


            btn_weather_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("click!");
                    Log.i("btn","click!");

                    Thread t=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("thread","start!");
                            OkHttpClient okHttpClient=new OkHttpClient.Builder()
                                    .connectTimeout(10, TimeUnit.SECONDS)
                                    .readTimeout(10,TimeUnit.SECONDS)
                                    .writeTimeout(10,TimeUnit.SECONDS)
                                    .build();
                            final Request request=new Request.Builder()
                                    .url("https://geoapi.qweather.com/v2/city/lookup?key=8b86256edc3d4893afc47ce359eca1fa&location="+et_weather_searchcity.getText().toString())
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
                                                .url("https://devapi.qweather.com/v7/weather/now?key=8b86256edc3d4893afc47ce359eca1fa&location="+cityid)
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
                                                    bd.putString("location",et_weather_searchcity.getText().toString());
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
                }
            });






        }else{
            viewContent = inflater.inflate(R.layout.pager_weather_recommend, container, false);
            iv_QRcode=viewContent.findViewById(R.id.iv_QRcode);
            btn_camera=viewContent.findViewById(R.id.btn_camera);
            btn_camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(openCameraIntent, CAMERA_REQUEST);
                }
            });
            Bitmap bm=createQRCodeBitmap("https://www.hznu.edu.cn", 800, 800,"UTF-8","H", "1", Color.BLACK, Color.WHITE);
            iv_QRcode.setImageBitmap(bm);
        }
        return viewContent;
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