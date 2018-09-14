package com.example.weather;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import static com.example.weather.MainActivity.city;
import static com.example.weather.MainActivity.day;

public class Main2Activity extends Activity {
    private String[] cityAll={"深圳","北京","上海","西安"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(Main2Activity.this,android.R.layout.simple_list_item_1,cityAll);
        final ListView listView_city=(ListView) findViewById(R.id.LV_changeCity);
        listView_city.setAdapter(adapter);
        listView_city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                city=(int)id;
                day=0;
                Intent intent=new Intent(Main2Activity.this,MainActivity.class);
                startActivity(intent);//启动Activity
            }
        });
    }
}
