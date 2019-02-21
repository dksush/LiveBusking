package com.example.dksush0828.livebusking.login.login_http;

import com.google.gson.annotations.SerializedName;

public class user_info {

    /**
     * 서버에서 반환된 JSON 을 받아오는 모델 클래스이다.
     * Response 는 json 을 받아오는 필수로 들어가는 키 값.
     * Nickname 은 db에서 로그인한 사용자의 닉네임을 받아온다.(프로필사진 추가 예정)
     */

    @SerializedName("response")
    private String response;

    @SerializedName("nickname")
    private String nickname;

    @SerializedName("email")
    private String email;
    @SerializedName("psd")
    private String psd;
    @SerializedName("gender")
    private String gender;

    public int getVideo_idx() {
        return video_idx;
    }

    public void setVideo_idx(int video_idx) {
        this.video_idx = video_idx;
    }

    @SerializedName("video_title")
    private String video_title;

    @SerializedName("thumbnail_path")
    private String thumbnail_path;

    @SerializedName("vod_path")
    private String vod_path;

    @SerializedName("view_number")
    private int view_number;
    @SerializedName("video_idx")
    private int video_idx;
    @SerializedName("video_type")
    private String video_type;









    public String getVideo_title() {
        return video_title;
    }

    public void setVideo_title(String video_title) {
        this.video_title = video_title;
    }

    public String getThumbnail_path() {
        return thumbnail_path;
    }

    public void setThumbnail_path(String thumbnail_path) {
        this.thumbnail_path = thumbnail_path;
    }

    public String getVod_path() {
        return vod_path;
    }

    public void setVod_path(String vod_path) {
        this.vod_path = vod_path;
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

    // 디비에서 데이터 가져오기





    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPsd() {
        return psd;
    }

    public void setPsd(String psd) {
        this.psd = psd;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }




}
