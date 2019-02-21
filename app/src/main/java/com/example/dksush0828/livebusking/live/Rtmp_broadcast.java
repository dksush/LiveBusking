package com.example.dksush0828.livebusking.live;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.common.RetrofitApi;
import com.example.dksush0828.livebusking.common.RetrofitInit;
import com.example.dksush0828.livebusking.login.login_http.user_info;
import com.example.dksush0828.livebusking.main.home;
import com.pedro.encoder.input.video.CameraOpenException;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import net.ossrs.rtmp.ConnectCheckerRtmp;

import org.json.JSONObject;

import java.nio.channels.SocketChannel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Rtmp_broadcast extends AppCompatActivity  implements ConnectCheckerRtmp, View.OnClickListener, SurfaceHolder.Callback{

    Context context;

    Button liveStartBtn, liveStopBtn;
    ImageButton switch_camera;


    // 방송정보
    SharedPreferences user;
    String nickname;
    int room_number = 1;
    String video_title; // 방제목./
    String thumbnail_path; // 섬네일 경로.
    String vod_path; // vod 경로.
    int view_number = 0; // 조회수.
    String video_type = "1"; // 비디오타입 ( 라아브 =1, vod=0)


    private RtmpCamera2 rtmpCamera2; // rtmp 카메라.
    SurfaceView surfaceView;// 방송화면 객체


    private final String[] PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };



    RecyclerView chat_recycle;
    RecyclerView.LayoutManager mLayoutManager;


    private SocketChannel socketChannel;
    private static final String HOST = "54.180.112.133"; // 접속할 네티 서버 주소
    private static final int PORT = 5001; // 포트.
    Handler handler;
    private JSONObject jsonObject; // 채팅을 주고받을 떄 사용할 제이선 객체.
    JSONObject msgObject; // 청취용 데이터.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rtmp_broadcast);
        // 방송용 권한 설정
        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }

        // 버튼 클릭 리스너.
        liveStartBtn = findViewById(R.id.liveStartBtn);
        liveStopBtn = findViewById(R.id.liveStopBtn);
        switch_camera = findViewById(R.id.switch_camera);
        setOnClickListeners();



        // 닉네임 + 방제목 = 방키.
        Intent intent = getIntent();// 방 제목 받아오기.
        video_title = intent.getStringExtra("video_title");
        user = getSharedPreferences("user_info",MODE_PRIVATE); // 닉네임 선언.
        nickname = user.getString("nickname","");
        Log.v("nickname", nickname);
        Log.v("video_title", video_title);

        // 방송화면 객체
        surfaceView = findViewById(R.id.surfaceView);
        rtmpCamera2 = new RtmpCamera2(surfaceView,  this);
        surfaceView.getHolder().addCallback( this);


    }


    /* 클릭 리스너를 설정한다. */
    private void setOnClickListeners(){
        liveStartBtn.setOnClickListener(onClickListener);
        liveStopBtn.setOnClickListener(onClickListener);
        switch_camera.setOnClickListener(onClickListener);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                //스트리밍 시작.
                // 아직 스트리밍 시작 안했을때, 녹화 혹은 비디오나 오디오를 준비중이라면.
                case R.id.liveStartBtn :
                    if(!rtmpCamera2.isStreaming()){
                        if(rtmpCamera2.isRecording() || rtmpCamera2.prepareVideo() && rtmpCamera2.prepareAudio()){


                            rtmpCamera2.startStream("rtmp://101.101.165.127/live/"+nickname+video_title); // 공인아이피.
                            Log.v("isStreaming : ", "방송시작");


                                // 방송시작하면서 디비에 데이너 넣기
                                RetrofitApi retrofitApi = RetrofitInit.getmAppIntroRetrofit()//레트로핏 초기화. 서버에 전송할 데이터(php)
                                        .create(RetrofitApi.class);

                                // api(서버에 보낼 데이터) 초기화.
                                // user_info : 서버에서 반환된 JSON 을 받아오는 모델 클래스이다.
                                Call<user_info> call = retrofitApi.make_live(nickname, video_title, view_number, thumbnail_path, video_type, vod_path);
                                call.enqueue(new Callback<user_info>() {
                                    @Override
                                    public void onResponse(Call<user_info> call, Response<user_info> response) {
                                        Log.v("디비에 데이너 넣기",response.body().getResponse());
                                        // 성공
                                        if (response.body().getResponse().equals("ok")) {
                                            Toast.makeText(Rtmp_broadcast.this, "디비는 왔다갑니다.", Toast.LENGTH_SHORT).show();
                                            Log.v("make_live_db", "방송 시작후 디비에 저장 성공");
                                        }else{
                                            Log.v("애매 : ", response.body().getResponse());
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<user_info> call, Throwable t) {
                                        Log.v("make_live", "방만들기 실패");
                                        Log.v("make_live", t.getMessage());

                                    }
                                });


                        }
                    }
                    break;

                    // 방송종료
                case R.id.liveStopBtn :
                    rtmpCamera2.stopStream();
                    update_video_type(); // live(1)에서 vod(0) 으로 전환.
                    Intent intent = new Intent(Rtmp_broadcast.this, home.class);
                    startActivity(intent);


                    break;

                    // 카메라 앞뒤 전환.
                case R.id.switch_camera :
                    try{
                        rtmpCamera2.switchCamera();
                    }catch (CameraOpenException e){
                        Toast.makeText(Rtmp_broadcast.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


            }
        }
    };



    public void update_video_type(){
        RetrofitApi retrofitApi = RetrofitInit.getmAppIntroRetrofit().create(RetrofitApi.class);
        Call<user_info>call = retrofitApi.update_video_type("0",nickname,video_title);
        call.enqueue(new Callback<user_info>() {
            @Override
            public void onResponse(Call<user_info> call, Response<user_info> response) {

            }

            @Override
            public void onFailure(Call<user_info> call, Throwable t) {

            }
        });
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // surface 의 상태가 변경될떄 호출되는 함수. surfaceview에 맞게 카메라 프리뷰도 재설정.
        // 위의 surfaceCreated 에 선언해줘도 될거같긴한데.
        rtmpCamera2.startPreview();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        //파괴되었을때 녹화중이었다면
        if (rtmpCamera2.isRecording()) {
            rtmpCamera2.stopRecord();
            liveStartBtn.setVisibility(View.VISIBLE);
            liveStopBtn.setVisibility(View.GONE);
//            Toast.makeText(this,
//                    "file " + currentDateAndTime + ".mp4 saved in " + folder.getAbsolutePath(),
//                    Toast.LENGTH_SHORT).show();
//            currentDateAndTime = "";
        }
        // 파괴되었을때, 스트리밍 중 이었다면.
        if (rtmpCamera2.isStreaming()) {
            rtmpCamera2.stopStream();
            Toast.makeText(context, "방송이 종료되었습니다.", Toast.LENGTH_SHORT).show();
            liveStartBtn.setVisibility(View.VISIBLE);
            liveStopBtn.setVisibility(View.GONE);

        }
        rtmpCamera2.stopPreview();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onConnectionSuccessRtmp() {


        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                liveStartBtn.setVisibility(View.GONE);
                liveStopBtn.setVisibility(View.VISIBLE);
                Toast.makeText(Rtmp_broadcast.this, "Connection success", Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }



    @Override
    public void onConnectionFailedRtmp(final String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Rtmp_broadcast.this, "Connection failed. " + reason,
                        Toast.LENGTH_SHORT).show();
                Log.v("Connection failed.",reason);
                rtmpCamera2.stopStream();
                liveStartBtn.setVisibility(View.VISIBLE);
                liveStopBtn.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDisconnectRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Rtmp_broadcast.this, "Disconnected", Toast.LENGTH_SHORT).show();
                Log.v("실패", "Disconnected");
            }
        });
    }

    @Override
    public void onAuthErrorRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Rtmp_broadcast.this, "Auth error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onAuthSuccessRtmp() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(Rtmp_broadcast.this, "Auth success", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


}
