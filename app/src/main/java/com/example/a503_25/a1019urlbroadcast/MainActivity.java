package com.example.a503_25.a1019urlbroadcast;

import android.app.ProgressDialog;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity {

    EditText url;
    Button download;
    TextView html;

    //진행 상황을 출력할 진행 대화상자
    ProgressDialog progressDialog;

    //데이터를 텍스트뷰에 출력할 핸들러 만들기 // 핸들러는 1개만 만들면 된다고 메인스레드에 1개만 있으니까
    //메시지.what으로 구분한다.
    Handler handler = new Handler(){
        @Override
      public void handleMessage(Message message){
            Log.e("핸들러 호출","핸들러호출되었습니다.");

            //스레드가 넘겨준 데이터를 텍스트 뷰에 출력
            //문자열로 변환하기 위해 toString으로 해준다.
            html.setText(message.obj.toString());
            progressDialog.dismiss();

      }
    };
    //클래스를 안만들고 객체를 바로 만든것이고
   /* //인스턴스 객체가 한개가 만들어진다 딱 한번만 하면 1개만
    Thread th = new Thread(){

    };*/
    //객체를 따로 만든다. 여러번 사용할 때는 이것을 사용한다.
    // 여러번 호출해야하므로 클래스를 만들고 객체를 생성한다.
    class ThreadEx extends Thread{
        @Override
        public void run(){
            try{
                Log.e("스레드","시작");
                // 다운로드 받을 주소 가져오기
                String addr = url.getText().toString();
                Log.e("다운로드 받을 주소",addr);
                //문자열 주소로 URL 객체 생성
                URL downloadURL = new URL(addr);
                //연결 객체 생성
                HttpURLConnection con = (HttpURLConnection)downloadURL.openConnection();

                //옵션 설정
                con.setConnectTimeout(20000);
                con.setUseCaches(false);
                Log.e("connection",con.toString());

                //문자열 다운로드 받기 위한 스트림 생성
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                //줄 단위로 문자열을 읽어서 sb에 추가
                StringBuilder sb = new StringBuilder();
                while (true){
                    String line = br.readLine();
                    //Log.e("읽은 내용",line);
                    if(line == null){
                        break;
                    }else {
                        sb.append(line + "\n");
                    }
                    //전부 가져왔으면 닫기
                    br.close();
                    con.disconnect();
                    //Message에 저장해서 handler에게 메시지 전송
                    Message message = new Message();
                    message.obj = sb.toString();
                    handler.sendMessage(message);
                }






            }catch (Exception e){
                Log.e("스레드 예외 발생:" ,e.getMessage());
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url = (EditText)findViewById(R.id.url);
        download = (Button)findViewById(R.id.download);
        html = (TextView)findViewById(R.id.html);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.download:
                        progressDialog = ProgressDialog.show(MainActivity.this,"","다운로드 중...");
                        ThreadEx th = new ThreadEx();
                        // 스타틀를 하면 run으로 간다.
                        Log.e("스레드","만듬");
                        th.start();
                        break;
                }
            }
        };
        download.setOnClickListener(listener);
    }
}
