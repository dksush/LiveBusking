package com.example.dksush0828.livebusking.main;

import com.google.gson.annotations.SerializedName;

public class Total_video_item {

    @SerializedName("video_idx") // 방 인덱스
    private int video_idx;
    @SerializedName("view_number") // 시청수
    private int view_number;

    @SerializedName("video_title") // 방제목
    private String video_title;
    @SerializedName("nickname") //닉네임
    private String nickname;
    @SerializedName("video_type")
    private String video_type;


    public int getVideo_idx() {
        return video_idx;
    }

    public void setVideo_idx(int video_idx) {
        this.video_idx = video_idx;
    }

    public int getView_number() {
        return view_number;
    }

    public void setView_number(int view_number) {
        this.view_number = view_number;
    }

    public String getVideo_type() {
        return video_type;
    }

    public void setVideo_type(String video_type) {
        this.video_type = video_type;
    }


    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }


}
