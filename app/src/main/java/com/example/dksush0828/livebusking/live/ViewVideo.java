package com.example.dksush0828.livebusking.live;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.live.netty_chat.chat_item;
import com.example.dksush0828.livebusking.live.netty_chat.viewer_chat_adapter;
import com.example.dksush0828.livebusking.main.Total_video_adapter;
import com.example.dksush0828.livebusking.main.Total_video_item;
import com.example.dksush0828.livebusking.main.home;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;


import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;


import com.nhancv.webrtcpeer.rtc_peer.PeerConnectionClient;
import com.nhancv.webrtcpeer.rtc_peer.SignalingEvents;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;
import com.pedro.rtplibrary.view.LightOpenGlView;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.mongodb.client.model.Filters.gt;


public class ViewVideo extends AppCompatActivity {

    private RtmpCamera2 rtmpCamera2;
    SharedPreferences user; // 로그인 유저정보 받아오기
    String nickname; // 로그인유저의 닉네임.
    String video_title;
    String bj_nickname;
    String video_type; // 라이브 = 1,  vod = 0
    ImageView live_icon;
    int video_idx;


    // netty 채팅 리사이클러뷰
    private RecyclerView chat_recycle;
    private RecyclerView.LayoutManager mLayoutManager;
    private viewer_chat_adapter chat_adapter;
    private List<chat_item> items;
    EditText message_edit; // 채팅 입력하는곳
    Button ChatSendBtn; // 채팅 보내기 버튼.


    // netty 통신용 값.
    private SocketChannel socketChannel;
    private static final String HOST = "54.180.112.133"; // 접속할 네티 서버 주소
    private static final int PORT = 5001; // 포트.
    Handler handler;
    private JSONObject jsonObject; // 채팅을 주고받을 떄 사용할 제이선 객체.
    JSONObject msgObject; // 청취용 데이터.

    String return_msg; // 보내는 메세지
    String data; // netty 청취소멧으로 부터 받은 메세지 데이터 덩어리.
    String Sender_nickname; // 소켓을 타고 넘어온 / 몽고디비에  => 있는 닉네임 추출
    String Sender_Msg; // 소켓을 타고 넘어온 / 몽고디비에 있는 => 메세지 추출



    // 몽고디비
    String MongoDB_IP = "54.180.112.133"; // 몽고디비 서버ip.
    int MongoDB_PORT = 27017; // 몽고디비 포트.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_video);


        handler = new Handler();// 핸들러 선언
        message_edit = findViewById(R.id.message_edit); // 채팅창 객체 선언.


        // 유저 닉네임
        user = getSharedPreferences("user_info",MODE_PRIVATE);
        nickname = user.getString("nickname","");
        Log.v("nickname", nickname);

        // 방 정보 받기
        Intent intent = getIntent();
        video_title = intent.getStringExtra("video_title");          // 방 제목
        bj_nickname = intent.getStringExtra("bj_nickname");          // 방송하는 bj
        video_type = intent.getStringExtra("video_type");            // 라이브(1) or vod(2)
        video_idx = intent.getIntExtra("video_idx",0);    // 비디오 고유 번호
        Log.v("video_title",video_title);
        Log.v("bj_nickname",bj_nickname);
        Log.v("video_type", String.valueOf(video_type));
        Log.v("video_idx", String.valueOf(video_idx));

       // live_icon = findViewById(R.id.live_icon); // 라이브 표시 빨간동그라미 아이콘.

        if(video_type.equals("1")){ // 라이브라면.

            Enter_Live_room();
        }else {
            // 1. vod : vod 영상경로를 연결하고,
            // 2. 몽고에서 채팅 내용을 가져온다.
            // 3. live 알려주는 아이콘을 비활성.
            Enter_Vod_room(); // 1.
            find_chat_mongo();// 2.
            live_icon.setVisibility(View.GONE);// 3.


        }


        // Netty Chat #####
        setSocket(); // 소켓 연결.
        chat_recycle = findViewById(R.id.chat_recycle);
        chat_recycle.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        chat_recycle.setLayoutManager(mLayoutManager);
        items = new ArrayList<>();




    }

    ///##### onCreate 끝 #######################


  /*   채팅서버와 소켓 연결하기(netty에선 소켓을 채널이라 부른다)
        - 네티에서 새로운 소켓을 연결하고, 소켓연결에 성공하면 청취소켓 스레드도 열어준다.
    1. 소켓채널이 없는 경우 IP, PORT 를 통해 소켓 연.
    2. 서버와 연결되면 현 사용자 정보를 서버에 보낸다.
    3. 소켓 연결 성공하면 청취스레드 오픈*/
  public void setSocket(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(socketChannel == null) {
                        Log.v("setSocket : ", "start");
                        socketChannel =  SocketChannel.open(); // 1. 새로운 소켓채널 오픈
                        socketChannel.configureBlocking(true);
                        socketChannel.connect(new InetSocketAddress(HOST, PORT)); // 서버와의 연결.

                       // 2. 서버와 연결되면 현 사용자 정보를 서버에 보낸다.
                        if(socketChannel.isConnected()){
                            JSONObject mjsonObject = new JSONObject(); // 소켓에 보낼 정보를 담을 JSONObject.
                            mjsonObject.put("type" ,"set"); // 설정 파일이라고 서버에게 알려준다.
                            mjsonObject.put("video_idx",String.valueOf(video_idx)); // 비디오 인덱스 : 채팅에서 방을 나누는 키로 쓸 것이다.
                            Log.v("json오브젝트란 : ", mjsonObject.toString());
                            Log.v("json오브젝트란 : ", mjsonObject.get("type").toString());
                            Log.v("json오브젝트란 : ", mjsonObject.get("video_idx").toString());

                            socketChannel
                                    .socket()                           // 소켓의
                                    .getOutputStream()                  // 아웃풋 스트림에
                                    .write(mjsonObject.toString().      // mjsonObject 의 값을 넣고
                                             getBytes("EUC-KR"));  // 인코딩 타입을 EUC-KR 로 설정한 다음 서버에 소켓 채널을 통해 전송한다.

                            // 3. 소켓에서 새로운 메시지를 청취하는 스레드를 시작한다.
                            listen_socket.start();

                        }

                    }else{
                        Log.e("ChatService::setSocket","socket already connected");
                    }


                }catch (Exception io){

                }

            }
        }).start();
    }

    // 메세지 보내기 버튼.
    public void ChatSendBtn(View view) throws JSONException {
        return_msg = message_edit.getText().toString();
        jsonObject = new JSONObject(); // 서버로 보낼 제이선객체.
        jsonObject.put("type","msg"); // 서버에 설정데이터가 아닌, 메세지 데이터임을 알려준다.
        jsonObject.put("sender_nickname",nickname); // 메세지 보낸이의 닉네임
        jsonObject.put("msg",return_msg); // 메세지
        jsonObject.put("video_idx",String.valueOf(video_idx)); // 방 구분키

        Log.v("ChatSendBtn_json : ", jsonObject.toString());


        try {
            if(!TextUtils.isEmpty(return_msg)){
                new SendmsgTask().execute(jsonObject);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    // AsyncTask 를 이용해 스레드로 서버에 메시지 전달 + ui 변경.
    private class SendmsgTask extends AsyncTask<JSONObject, Void, Void> {

        @Override // 서버로 메세지를 보낸다.
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                Log.v("doInBackground_ MSG : ", jsonObject.toString());
                socketChannel
                        .socket()
                        .getOutputStream()
                        .write(jsonObject.toString().getBytes("EUC-KR")); // 서버로
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override // UI 를 변경한다.
        protected void onPreExecute() {
            super.onPreExecute();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    message_edit.setText(""); // 메시지 창을 비운다.

                    items.add(new chat_item(nickname,return_msg));
                    Log.v("item : ", items.toString());
                    chat_adapter = new viewer_chat_adapter(ViewVideo.this,items); // 어댑터 선언
                    chat_recycle.setAdapter(chat_adapter);
                    chat_adapter.notifyDataSetChanged();


                }
            });
        }
    }


    // 스레드를 이용해 채팅 서버로 메세지 제이선 객체 보내기. : 이런 방법도 있다는것.
//   public void SendMsg(String sender_nickname, String msg){
//        jsonObject = new JSONObject; // 서버로 보낼 제이선객체.
//      new Thread(()->{
//          try {
//
//              jsonObject.put("sender_nickname",nickname);
//              jsonObject.put("msg",msg);
//              socketChannel
//                      .socket() // 해당 socketChannel 소켓에 있는
//                      .getOutputStream() // getOutputStream 에 보낼 데이터를 담는다.
//                      .write(jsonObject.toString().getBytes("EUC-KR")); // 인코딩 타입을 "EUC-KR"로 해서 데이터를 사버로 보낸다.
//
//          }catch (IOException | JSONException e) {
//              e.printStackTrace();
//          }
//
//      }).start();
//   }



    /*
     * 네티 소켓 채널에서 새로운 메시지를 청취하는 스레드
     * 1. 네티 소켓 채널에서 새로운 메시지가 올 때 까지 기다린다.
     * 2. 새로운 메시지가 도착하면 byteBuffer 에 담은뒤 String 형태로 메시지를 가져온다.
     */
    private Thread listen_socket = new Thread() {
            @Override
            public void run() {
                Log.v("listen_socket : ", "listen_socket 시작");
                while (true){

                    try {
                        // NIO 에서는 데이터 입출력을 위해 버퍼를 사용한다.
                        // 버퍼는 읽기/쓰기가 가능한 메모리 배열이다.
                        ByteBuffer byteBuffer = ByteBuffer.allocate(256); // 문자열로 반환. ByteBuffer 에 힙 메모리 256 Byte 를 할당한다.버퍼는 운영체제의 커널이 관리하는 시스템 메모리를 직접 사용 할 수있다.
                        int readByteCount = socketChannel.read(byteBuffer); // 데이 받기  : 소켓에서 데이터를 읽어서 byteBuffer 에 저장한다.
                        Log.v("readByteCount", readByteCount + "");
                        if (readByteCount == -1) {// 소켓채널에서 들어온 바이트의 길이가 -1 이라면 에러를 발생시킨다.
                            throw new IOException();
                        }

                        // 버퍼에 있는 데이터 읽기.
                        byteBuffer.flip(); // 문자열로 변환. 바이트 버퍼의 position을 0으로. limit을 현재 position으로 설정 ==> 읽기/쓰기 작업 병향.
                        Charset charset = Charset.forName("EUC-KR"); // 캐릭터셋을 EUC-KR 로 설정한다.(한국어 코드)
                        data = charset.decode(byteBuffer).toString();
                        Log.v("receive", "msg :" + data);
//                        Toast.makeText(ViewVideo.this, "data", Toast.LENGTH_SHORT).show();

                       // msgObject = new JSONObject(data); // 가져온 data 를 JSONObject 로 파싱한다.
                       // Log.v("수신데이터", msgObject.toString());
                        // 채팅 리사이클러뷰 ui 갱신
                        handler.post(showUpdate);

                    } catch (IOException e) {
                        e.printStackTrace();
                        try {
                            socketChannel.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }

                    }
//                    catch (JSONException e) {
//                        e.printStackTrace();
//                    }

              }

            }
        };

    // 리슨소케세서 들어온 채팅 리사이클러뷰 ui 갱신
    private Runnable showUpdate = new Runnable() {

        public void run() {

            Sender_nickname = data.split(">##<")[0];
            Sender_Msg = data.split(">##<")[1];
            Log.v("Sender_nickname : ", Sender_nickname);
            Log.v("Sender_Msg : ", Sender_Msg);
            items.add(new chat_item(Sender_nickname,Sender_Msg));
            chat_adapter = new viewer_chat_adapter(ViewVideo.this,items); // 어댑터 선언
            chat_recycle.setAdapter(chat_adapter);
            chat_adapter.notifyDataSetChanged();


        }

    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }







    // Live 방에 접속.
    public void Enter_Live_room(){

        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        //Create the player
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        PlayerView playerView = findViewById(R.id.simple_player);
        playerView.setPlayer(player);


        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(rtmpDataSourceFactory)
                .createMediaSource(Uri.parse("rtmp://101.101.165.127/live/"+bj_nickname+video_title));


        // Prepare the player with the source.
        player.prepare(videoSource);

        //auto start playing
        player.setPlayWhenReady(true);


    }

    public void Enter_Vod_room(){
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        //Create the player
        SimpleExoPlayer player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
        PlayerView playerView = findViewById(R.id.simple_player);
        playerView.setPlayer(player);


        RtmpDataSourceFactory rtmpDataSourceFactory = new RtmpDataSourceFactory();

        // This is the MediaSource representing the media to be played.
        MediaSource videoSource = new ExtractorMediaSource.Factory(rtmpDataSourceFactory)
                .createMediaSource(Uri.parse("rtmp://101.101.165.127/vod/atest2.flv"));

        // Prepare the player with the source.
        player.prepare(videoSource);

        //auto start playing
        player.setPlayWhenReady(true);
    }







    // 몽고디비에서 채팅 내역 가져오기.
    public void find_chat_mongo(){

        Log.v("mongo : ", "start");

            new Thread(new Runnable() {
                @Override
                public void run() {

                    try {

                        MongoClient mongo = new MongoClient( MongoDB_IP , 27017 );               //몽고디비 서버 연결.
                        MongoDatabase db =  mongo.getDatabase("chat");                   //몽고 database 연결.
                        MongoCollection<Document> Chat_col = db.getCollection("chat");  // 콜렉션(테이블) 연결.

                        // 필터 걸기.
                        BasicDBObject query = new BasicDBObject();
                        query.put("video_idx",String.valueOf(video_idx));               // "video_idx" 가 특정 값인 애들만 불러온다.
                        MongoCursor<Document> cursor = Chat_col.find(query).iterator(); // cursor = find().
                        try {
                            while (cursor.hasNext()) {
                                // Log.v("test", String.valueOf(cursor.next().toJson())); 해당 도큐먼트 가로줄 json 형태로 전체보기.
                                Sender_nickname = String.valueOf(cursor.next().get("nickname"));
                                Sender_Msg = String.valueOf(cursor.next().get("msg"));
                                items.add(new chat_item(Sender_nickname,Sender_Msg));

                                // UI 갱신.리사이클러뷰 어댑터 : 해당 vod 로 들어왔을때 해방방( 키값 : video_idx 으로 구분)에 채팅 내역이 있다면 불러온다.
                                handler.post(Mongo_Chat_Data);

                            }
                        } finally {
                            cursor.close();
                        }

                    }catch (ClassCastException e){
                        Log.v("error : ", e.getMessage(),e);
                    }

                }
            }).start();

    }

    // 몽고디비에서 받은 채팅데이터를 가지고 리사이클러뷰 갱신
    // 서브 스레드로 유아이 갱신을 넘김. 안이래도 될거같기도?? 한데 혹시 모르니 그냥 바로 이케 만듬.
    private Runnable Mongo_Chat_Data = new Runnable() {
        @Override
        public void run() {
            chat_adapter = new viewer_chat_adapter(ViewVideo.this,items); // 어댑터 선언
            chat_recycle.setAdapter(chat_adapter);
            chat_adapter.notifyDataSetChanged();


        }
    };


    // 방송보기 종료 후 홈으로 돌아가기.
    public void back_btn(View view){

        if(rtmpCamera2.isStreaming()){
            rtmpCamera2.stopStream();
            Intent intent = new Intent(ViewVideo.this, home.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(ViewVideo.this, home.class);
            startActivity(intent);
        }

    }
}
