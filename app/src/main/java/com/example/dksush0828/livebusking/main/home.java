package com.example.dksush0828.livebusking.main;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.view.Menu;
import android.widget.Toast;


import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.common.RetrofitApi;
import com.example.dksush0828.livebusking.common.RetrofitInit;
import com.example.dksush0828.livebusking.live.Rtmp_broadcast;
import com.example.dksush0828.livebusking.live.ViewVideo;
import com.example.dksush0828.livebusking.login.login_http.user_info;
import com.example.dksush0828.livebusking.wallet.wallet_start;
import com.google.gson.JsonArray;
import com.pedro.rtplibrary.rtmp.RtmpCamera2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class home extends AppCompatActivity implements Total_video_adapter.StreamingRecyclerViewClickListener{

    private int page = 1; // 페이징 페이지
    private RtmpCamera2 rtmpCamera2;

    SharedPreferences user;
    String nickname;
    String video_title; // 방제목./
    String thumbnail_path; // 섬네일 경로.
    String vod_path; // vod 경로.
    int view_number = 0; // 조회수.
    String video_type = "1"; // 비디오타입 ( 라아브 =1, vod=0)


    Toolbar toolbar;

    // 리사이클러뷰
    private RecyclerView recyclerview;
    private RecyclerView.LayoutManager mLayoutManager;
    private Total_video_adapter adapter;
    private List<Total_video_item> items;
    ProgressBar progressBar;                        // 데이터 로딩중을 표시할 프로그레스바--> 프로그래스바를 안쓰면 페이징시 화면이 상단으로 이동 : 다른 해결책으 아직 모르겟.
    private boolean mLockListView = false;          // 데이터 불러올때 중복안되게 하기위한 변수
    private boolean lastItemVisibleFlag = false;    // 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수

  JSONObject jsonObject;
    ArrayList<HashMap<String, String>> mArrayList; //디비에서 가져온 데이터를 해쉬맵에 넣는다. 그걸 리사이클러뷰 어댑터에 넣기 위해 다시 여기에 넣는다.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        progressBar = findViewById(R.id.progress);
        progressBar.setVisibility(View.GONE);

        // 툴바
        toolbar = findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("LiveBusking");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // 닉네임 선언.
        user = getSharedPreferences("user_info",MODE_PRIVATE);
        nickname = user.getString("nickname","");
        Log.v("nickname", nickname);


        // 텝 설정.
        TabHost tabHost = findViewById(R.id.tabHost);
        tabHost.setup();


        /**
         * 첫번째 Tab.(탭 표시 텍스트 : Tab1 , 페이지뷰 : content1)
         */
        TabHost.TabSpec ts1 = tabHost.newTabSpec("Tab Spec 1");
        ts1.setContent(R.id.content1);
        ts1.setIndicator("Total");
        tabHost.addTab(ts1);

        // 디비에서 데이터 가져오기
        getitem_fromdb(page); // 페이지(키값) 없음.

        // 리사이클러뷰 선언.
        mArrayList = new ArrayList<>();
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        recyclerview.setLayoutManager(mLayoutManager);



        Scroll_Paging(); // 스크롤 페이징.


        /**
         * 두번째 Tab. (탭 표시 텍스트 : Tab2 , 페이지뷰 : content2)
         */
        TabHost.TabSpec ts2 = tabHost.newTabSpec("Tab Spec 2");
        ts2.setContent(R.id.content2);
        ts2.setIndicator("HOT");
        tabHost.addTab(ts2);




    }


    //// 리사이클러뷰 온클릭리스너
    @Override
    public void onStreamingItemClicked(int position) {
        Log.v("아이템 클릭 : ","ok");
        Log.v("아이템 클릭 : ",mArrayList.get(position).get("video_title"));


        Intent intent = new Intent(home.this, ViewVideo.class);
        intent.putExtra("video_title",mArrayList.get(position).get("video_title")); // 제목
        intent.putExtra("bj_nickname",mArrayList.get(position).get("nickname")); // bj 닉네임
        intent.putExtra("video_type",mArrayList.get(position).get("video_type")); // 방송타입(라이브/ vod)
        intent.putExtra("video_idx",mArrayList.get(position).get("video_idx")); // 방번호.

        startActivity(intent);


//        // 조회수 갱신
//        RetrofitApi retrofitApi = RetrofitInit.getmAppIntroRetrofit()
//                .create(RetrofitApi.class);
//
//        Call<user_info> call = retrofitApi.update_view_number(Integer.parseInt(mArrayList.get(position).get("view_number"))+1, Integer.parseInt(mArrayList.get(position).get("video_idx")));
//        call.enqueue(new Callback<user_info>() {
//            @Override
//            public void onResponse(Call<user_info> call, Response<user_info> response) {
//                if (response.body().equals("ok")){
//                    Log.v("조회수 갱신 : ", "성공");
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<user_info> call, Throwable t) {
//                Log.v("error", t.getMessage());
//            }
//        });

    }





    public void Scroll_Paging(){
        // 페이징 스크롤 : 리사이클러뷰의 마지막 아이템에 스크롤이 도달했을때 반응.
        recyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int itemTotalCount = recyclerView.getAdapter().getItemCount()-1; // 총 아이템개수


                if(lastVisibleItemPosition == itemTotalCount && mLockListView == false){
                    progressBar.setVisibility(View.VISIBLE);
                    getitem_more_fromdb();
                }
//                        if(lastVisibleItemPosition == -1 ){ // 리사이클러뷰 최상단 끌올 시.
//                            getitem_fromdb(1); // page = 1 로 데이터를 다시 디비에서 가져온다.
//                        }
            }
        });

    }



    // 디비에서 데이터 가져오기
    private void getitem_fromdb(int Page) {

        Log.v("getitem_fromdb : ", "디비에서 데이터 가져오기. ");

              //디비 접속해서 아이템 가져오기. 서버 전송에 필요한 항목.
        RetrofitApi retrofitApi = RetrofitInit.getmAppIntroRetrofit()
                .create(RetrofitApi.class);

        // api 선언. 위에 선언된 retrofitApi로 서버에 요청을 보내고,  반환된 JSON 을 받아오는 모델 클래스이다.
        Call<List<Total_video_item>> call= retrofitApi.gettotalitem(Page);
        Log.v("page 확인 : ", String.valueOf(Page));
        call.enqueue(new Callback<List<Total_video_item>>() {
            @Override
            public void onResponse(Call<List<Total_video_item>> call, Response<List<Total_video_item>> response) {


                for(int i=0;i<response.body().size();i++){

                    // 디비 데이터 가져오기.
                    // 이렇게 쪼개는 이유는 페이징을 위해서.
                    // 페이징에 넣기 위해선 초기화가 아니라 계속 add 를 해 주어야 해서.
                    String nickname = response.body().get(i).getNickname();
                    String video_title = response.body().get(i).getVideo_title();
                    int view_number = response.body().get(i).getView_number();

                    Log.v("test",video_title);

                    // 해쉬앱에 키/밸류값 넣기
                    final HashMap<String,String> hashMap = new HashMap<String, String>();

                    hashMap.put("nickname", nickname);
                    hashMap.put("video_title", video_title);
                    hashMap.put("view_number", String.valueOf(view_number));


                    mArrayList.add(hashMap);

                }

                if(page == 1){
                    adapter = new Total_video_adapter(home.this, mArrayList);
                    recyclerview.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    mLockListView = false;
                }else if(page > 1){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            progressBar.setVisibility(View.GONE);
                            mLockListView = false;
                        }
                    },100);
                }
                page++;
                adapter.setOnClickListener(home.this::onStreamingItemClicked);


            }

            @Override
            public void onFailure(Call<List<Total_video_item>> call, Throwable t) {
               Log.v("onFailure", t.getMessage());
            }
        });

    }

    // 스크롤시 추가로 디비에서 데이터를 가져온다.
    public void getitem_more_fromdb(){
        getitem_fromdb(page);
        Log.v("page : ", String.valueOf(page));
        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
        mLockListView = true;
    }



    // 방송하기
    public void live_btn(View view){
        make_live();
    }

    public void wallet(View view){
        Intent wallet_intent = new Intent(home.this, wallet_start.class);
        startActivity(wallet_intent);
    }


    // 라이브 방송하기.
    void make_live(){
        final EditText edittext = new EditText(this);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("방 제목");
        builder.setView(edittext);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // 방만들기 : 방제목을 정하고 방만들기를 누른다(디비에 저장 : 디비 리스트 (썸네일, 방제목, 만든디, 라이브중 여부)
                        // 그럼 방이름을이 "키값"으로 uri에 들어가서 스트리밍 방을 생성한다.
                        // 그렇게 생성된 방 정도는 "스트리밍디비"에 들어가고, 라이브 엑티비티에 방이 생성된다.(끌올을 하면 새로고침되게끔)
                        // 방은 썸네일과 방제목, 스트리밍하는사람 아이디표시( + 라이브중 표시?)
                        // 다른 유저가 그 방을 누르면 방제목을 키값으로  해당 방에 들어갈수 있다.
                        // 레트로핏 세팅
                        video_title = edittext.getText().toString();//방제목.
                        Log.v("video_title",video_title);
                        Intent intent = new Intent(home.this, Rtmp_broadcast.class);
                        intent.putExtra("video_title",video_title);
                        startActivity(intent);


                    }
                });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
