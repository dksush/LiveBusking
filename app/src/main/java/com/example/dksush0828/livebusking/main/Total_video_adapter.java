package com.example.dksush0828.livebusking.main;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.common.RetrofitApi;
import com.example.dksush0828.livebusking.common.RetrofitInit;
import com.example.dksush0828.livebusking.live.ViewVideo;
import com.example.dksush0828.livebusking.login.login_http.user_info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 토탈 방송 RecyclerView 의 Adapter
 *
 * 토탈 탭에서 실시간 + vod 를 함께 보여주는 리스트 어댑터이다.
 * 실시간일 경우 빨간 점이 붙어있다. vod의 경우 아무것도 없다.
 * 라이브 방송을 클릭할 경우 webrtc 소켓과 연결되는 ViewerActivity로 이동하고,
 * vod의 경우 vodActivity로 이동한다.
 *
 */
public class Total_video_adapter extends RecyclerView.Adapter<Total_video_adapter.ViewHolder> {

    // 생성자.
    private Context context;
    private List<Total_video_item> items;
    ArrayList<HashMap<String,String>> mArrayList; //공지사항 정보 담겨있음

    public Total_video_adapter(Context context,  ArrayList<HashMap<String,String>> mArrayList){
        this.context = context;
        this.mArrayList = mArrayList;
    }


    // 아이템 클릭 리스너구현
    // 1. 포지션 위치 선언
    public interface StreamingRecyclerViewClickListener{
        void onStreamingItemClicked(int position);  // 아이템 하나 전체 부분 클릭
    }
    // 2. 위의 인터페이스를 내부에서 사용할수 있게 선언.
    private StreamingRecyclerViewClickListener mListener;
    // 3. 2의 선언된 것이 클릭을 받을 수 있는 메소드화 시킴. 이 메소드는 "onBindViewHolder" 에서 받는다.

    public void setOnClickListener(StreamingRecyclerViewClickListener listener){

        mListener = listener;
    }






    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 리사이클러뷰의 아이템 연결.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_card,parent,false);
        return new ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HashMap<String,String> item = mArrayList.get(position);
        holder.video_title.setText(item.get("video_title")); //작성자
        holder.nickname.setText(item.get("nickname"));
        holder.view_number.setText(item.get("view_number")); //작성자

        // 밑에 ViewHolder 에서 연결한 데이터 받아오기
//        holder.video_title.setText(items.get(position).getVideo_title());
//        holder.nickname.setText(items.get(position).getNickname());
//        holder.view_number.setText(""+items.get(position).getView_number());

        if(mListener != null){// 외부에서 리스너를 연결을 했다면
            final int pos = position;

            holder.itemView.setOnClickListener(new View.OnClickListener() { // itemView :: 전체
                @Override
                public void onClick(View v) {
                    mListener.onStreamingItemClicked(pos); // 리사이클러뷰의 특정부분이 아니라, 전체 클릭 받는곳.
                }
            });
        }
        // 이미지 받아오기
//        Picasso.get().load(items.get(position).getImage1())
//                .transform(new RoundedCornersTransformation(130,0))
//                .into(holder.home_image);
//
//        if (mListener != null) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    mListener.onItemClicked(position);
//                }
//            });
//        }


    }


    @Override
    public int getItemCount() {
        return mArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView video_title, nickname, view_number;


        public ViewHolder(View itemView) {
            super(itemView);
            video_title = itemView.findViewById(R.id.video_title);
            nickname = itemView.findViewById(R.id.nickname);
            view_number = itemView.findViewById(R.id.view_number);


        }
    }
}
