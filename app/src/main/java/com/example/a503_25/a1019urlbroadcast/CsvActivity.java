package com.example.a503_25.a1019urlbroadcast;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class CsvActivity extends AppCompatActivity {

    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    ListView listView;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message message) {
            adapter.notifyDataSetChanged();
        }
    };

    class ThreadEx extends Thread{
        public void run(){
            try{
                String addr = "http://192.168.0.235:8080/android/data.csv";
                URL url = new URL(addr);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setUseCaches(false);
                con.setConnectTimeout(20000);

                //문자열 읽기
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                list.clear();
                while(true){
                    String line = br.readLine();
                    if(line == null){
                        break;
                    }else {
                        //,로 구분한 것은 컨트롤러 리턴값을 ,로 구분했기 때문이다.
                        String [] ar = line.split(",");
                        for(String temp : ar){
                            list.add(temp);
                        }
                    }
                }
                br.close();
                con.disconnect();
                handler.sendEmptyMessage(0);

            }catch (Exception e){
                Log.e("예외발생",e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_csv);

        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(CsvActivity.this,android.R.layout.simple_list_item_1,list);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        ThreadEx ex = new ThreadEx();
        ex.start();


    }
}
