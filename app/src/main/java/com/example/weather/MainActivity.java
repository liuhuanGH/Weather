package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//默认是将当前城市的全部三天信息先全部缓存到本地，然后左右切换天数的时候更新显示相应的数据；在切换城市时，读取切换后城市三天的天气信息
public class MainActivity extends Activity implements View.OnTouchListener,GestureDetector.OnGestureListener{
    public static int city=0;
    static final String[] weatherUrl={"http://sky.tvos.skysrt.com/Framework/tvos/index.php?_r=weather/weatherAction/GetWeather&city=%E6%B7%B1%E5%9C%B3",
            "http://sky.tvos.skysrt.com/Framework/tvos/index.php?_r=weather/weatherAction/GetWeather&city=%E5%8C%97%E4%BA%AC",
            "http://sky.tvos.skysrt.com/Framework/tvos/index.php?_r=weather/weatherAction/GetWeather&city=%E4%B8%8A%E6%B5%B7",
            "http://sky.tvos.skysrt.com/Framework/tvos/index.php?_r=weather/weatherAction/GetWeather&city=%E8%A5%BF%E5%AE%89"};//深圳0，北京1，上海2，西安3
    public static int day = 0;//记录是哪天，0今天，1明天，2后天
    //记录当前界面需要信息，日期、天气，最高温度、最低温度
    static String[] date=new String[3];
    static String[] weather=new String[3];
    static int[] maxDegree=new int[3];
    static int[] minDegree=new int[3];

    TextView textView_city;
    TextView textView_date;
    TextView textView_weather;
    TextView textView_maxD;
    TextView textView_mimD;
    LinearLayout layout;
    Button change_city;
    GestureDetector mGestureDetector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_city=(TextView) findViewById(R.id.city);
        textView_date=(TextView) findViewById(R.id.date);
        textView_weather=(TextView) findViewById(R.id.weather);
        textView_maxD=(TextView) findViewById(R.id.maxDegree);
        textView_mimD=(TextView) findViewById(R.id.minDegree);
        change_city=(Button) findViewById(R.id.button_city);
        change_city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Main2Activity.class);
                startActivity(intent);
            }
        });

        mGestureDetector=new GestureDetector((GestureDetector.OnGestureListener)this);
        layout=(LinearLayout) findViewById(R.id.activity_main);
        layout.setOnTouchListener(this);
        layout.setLongClickable(true);
        requestWeather();
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float minMove=120;
        float minV=0;
        float startX=e1.getX();
        float stopX=e2.getX();

        if(stopX-startX>minMove && Math.abs(velocityX)>minV){//向右滑动
            if(day>0){
                day-=1;
                refreshActivity();
            }else {
                day=0;
                Toast.makeText(MainActivity.this,"没有更早的数据了",Toast.LENGTH_SHORT).show();
            }
        }else  if(startX-stopX>minMove && Math.abs(velocityX)>minV){//向左滑动
            if(day<2){
                day+=1;
                refreshActivity();
            }else {
                day=2;
                Toast.makeText(MainActivity.this,"没有后面的数据了",Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    /**
     * 请求某个城市三天的天气数据
     */
    public void requestWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(weatherUrl[city]);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.connect();
                    InputStream in = connection.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(in));
                    StringBuilder builder = new StringBuilder();
                    String s;
                    if ((s = buffer.readLine()) != null)
                        builder.append(s);
                    parseData(builder.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 将请求回的天气JSON数据进行转换
     * @param s JSON数据的String格式
     */
    private void parseData(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            //JSONObject baseinfo = jsonObject.getJSONObject("baseinfo");
            JSONArray array = jsonObject.getJSONArray("days");
            for (int i = 0; i < 3; i++) {
                JSONObject day = array.getJSONObject(i);
                date[i]=day.getString("date");
                weather[i]= day.getString("Weather");
                maxDegree[i]=day.getInt("MaxDegree");
                minDegree[i]=day.getInt("MinDegree");
                if(i==0)
                    refreshcity();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     在主线程更新UI天气*/
    private void refreshActivity() {
        //
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView_date.setText(date[day].toString());
                textView_weather.setText(weather[day].toString());
                textView_maxD.setText(String.valueOf(maxDegree[day]));
                textView_mimD.setText(String.valueOf(minDegree[day]));
            }
        });
    }

    /**
      更新UI的城市和天气
     */
    public void refreshcity(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (city){
                    case 0:
                        textView_city.setText("深圳");
                        break;
                    case 1:
                        textView_city.setText("北京");
                        break;
                    case 2:
                        textView_city.setText("上海");
                        break;
                    case 3:
                        textView_city.setText("西安");
                        break;
                }
            }
        });
        refreshActivity();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }
}
