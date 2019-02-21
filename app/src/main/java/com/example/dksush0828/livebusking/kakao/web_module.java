
package com.example.dksush0828.livebusking.kakao;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.example.dksush0828.livebusking.R;

public class web_module extends AppCompatActivity {


    private WebView mainWebView;
    private final String APP_SCHEME = "iamportkakao://";
    private web_interface web_interface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_module);


        mainWebView = (WebView) findViewById(R.id.web);
        WebSettings settings = mainWebView.getSettings();
        settings.setJavaScriptEnabled(true); //자바스크립트를 사용가능하게 세팅.
        mainWebView.setWebViewClient(new KakaoWebViewClient(this));
        //--> 이게 없으면 앱 외부의 브라우져를 킬수있다.
        //쉽게 말해서 loadUrl 로 호출하던 방법을 WebViewClient 에서 대신 호출한다고 생각하면 된다


        web_interface = new web_interface(web_module.this, mainWebView);
       // mainWebView.addJavascriptInterface(web_interface,"Android");

        mainWebView.loadUrl("http://106.10.44.11/test.html");


        // Bridge 인스턴스 등록
        // mWebView.addJavascriptInterface(new AndroidBridge(mWebView, dbHelper, newtwork), "HybridApp");


    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        Log.v("이건감7", "이건감7");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = getIntent();
        if ( intent != null ) {
            Uri intentData = intent.getData();



            if ( intentData != null ) {
                //카카오페이 인증 후 복귀했을 때 결제 후속조치
                String url = intentData.toString();


               // Intent intent1 = new Intent(web_module.this, Streaming.class);
                Log.v("1번","1번");
               // startActivity(intent1);
                // 음 여기서 인텐트 때리면 될래나.





                if ( url.startsWith(APP_SCHEME) ) {
                    String path = url.substring(APP_SCHEME.length());
                    if ( "process".equalsIgnoreCase(path) ) {
                        mainWebView.loadUrl("javascript:IMP.communicate({result:'process'})");



                    } else {


                        mainWebView.loadUrl("javascript:IMP.communicate({result:'cancel'})");


                    }
                }
            }
        }

    }
}

