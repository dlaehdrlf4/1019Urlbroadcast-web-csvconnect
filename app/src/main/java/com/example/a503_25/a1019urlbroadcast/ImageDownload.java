package com.example.a503_25.a1019urlbroadcast;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageDownload extends AppCompatActivity {

    ImageView imageView;

    Handler displayHandler = new Handler(){
      public void handleMessage(Message message){
          Bitmap bitmap = (Bitmap)message.obj;
          imageView.setImageBitmap(bitmap);
      }
    };

    Handler downloadHandler = new Handler(){
        public void handleMessage(Message message){
            //파일이 존재하는 경우
            if(message.obj != null){
                //안드로이드의 Data 디렉토리 경로를 가져오기
                String path = Environment.getDataDirectory().getAbsolutePath();
                //현재 앱 내의 파일 경로 만들기
                path = path + "data/data/com.example.a503_25.a1019urlbroadcast/files/" + (String)message.obj;
                //이미지 파일을 imageView에 출력
                imageView.setImageBitmap(BitmapFactory.decodeFile(path));
            }
            // 파일이 존재하는 경우
            else {
                Toast.makeText(ImageDownload.this, "파일이 존재하지 않습니다.",Toast.LENGTH_LONG).show();

            }
        }
    };


    class displayThreadEx extends Thread{
        @Override
        public void run(){
            try{
                String addr = "http://img77.dreamwiz.net/20180809/B/s/o/Bso9saB_o.jpg";
                URL url = new URL(addr);
                //url에 연결해서 비트맵 만들기
                Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
                //비트맵을 핸들러에게 전송
                Message message = new Message();
                message.obj = bitmap;
                displayHandler.sendMessage(message);

            }catch (Exception e){
                Log.e("이미지 가져오기 스레드 예외:",e.getMessage());
            }
        }
    }
//이미지를 다운로드 받아서 파일로 저장하는 스레드
    class DownloadThreadEx extends  Thread {
    String addr;
    String filepath;

    public DownloadThreadEx(String addr, String filepath) {
        this.addr = addr;
        this.filepath = filepath;
        //Log.e("d",addr);
    }

    @Override
    public void run(){
        try{
            URL url = new URL(addr);
            HttpURLConnection con =
                    (HttpURLConnection)url.openConnection();
            con.setUseCaches(false);
            con.setConnectTimeout(20000);

            //내용을 읽을 스트림을 생성
            InputStream is = con.getInputStream();
            //기록할 스트림을 생성
            PrintStream pw = new PrintStream(
                    //기록해야하니까 outputfileoutput
                    openFileOutput(filepath, 0));
            //is에서 읽어서 pw에 기록
            Log.e("dong","읽기");
            while(true){
                byte []  b = new byte[1024];
                int read = is.read(b);
                if(read <= 0)break;
                pw.write(b, 0, read);
            }
            is.close();
            pw.close();
            con.disconnect();
            Message message =  new Message();
            message.obj = filepath;
            downloadHandler.sendMessage(message);

        }catch(Exception e){
            Log.e("이미지 다운로드 실패", e.getMessage());
        }
    }
}




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_download);

        imageView = (ImageView)findViewById(R.id.imageView);

        Button display = (Button)findViewById(R.id.display);
        Button download = (Button)findViewById(R.id.download);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.display:
                        displayThreadEx ex = new displayThreadEx();
                        ex.start();
                        break;
                    case R.id.download:
                        //이미지를 다운로드 받을 주소
                        String imageUrl = "http://img77.dreamwiz.net/20180809/B/s/o/Bso9saB_o.jpg";
                        int idx = imageUrl.lastIndexOf("/");
                        String filename = imageUrl.substring(idx +1);
                        //Log.e("파일 이름",filename);
                        //파일 경로 만들기
                        String data = Environment.getDataDirectory().getAbsolutePath();
                        String path = data+ "/data/com.example.a503_25.a1019urlbroadcast/files/" + filename;
                        //Log.e("파일경로",path);
                        //파일 존재 여부를 확인
                        if(new File(path).exists()){
                            Toast.makeText(ImageDownload.this,"파일이 존재합니다.",Toast.LENGTH_LONG).show();
                            imageView.setImageBitmap(BitmapFactory.decodeFile(path));

                        }else{
                            Toast.makeText(ImageDownload.this,"파일이 존재하지 않습니다.",Toast.LENGTH_LONG).show();
                            //넘겨준 이미지 파일의 경로와 저장할 파일 경로 확인
                            //Log.e("이미지 파일 경로",imageUrl);
                            //Log.e("저장할 파일 경로",path);
                            DownloadThreadEx th = new DownloadThreadEx(imageUrl,filename);
                            th.start();
                        }

                        break;
                }

            }
        };
        display.setOnClickListener(listener);
        download.setOnClickListener(listener);
    }
}
