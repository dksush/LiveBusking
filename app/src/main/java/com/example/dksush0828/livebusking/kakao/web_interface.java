package com.example.dksush0828.livebusking.kakao;

import android.app.Activity;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public  class web_interface {
//    public abstract void success();
//    public abstract void failed();



    private WebView mAppView;
    private Activity mContext;

    public web_interface(Activity mContext, WebView mAppView) {
        this.mContext = mContext;
        this.mAppView = mAppView;

    }



}
