package com.example.dksush0828.livebusking.live.netty_chat;

import com.google.gson.annotations.SerializedName;

public class chat_item {


    private String nickname;
    private String msg;



    public chat_item(String nickname, String msg){
        this.nickname = nickname;
        this.msg = msg;

    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
