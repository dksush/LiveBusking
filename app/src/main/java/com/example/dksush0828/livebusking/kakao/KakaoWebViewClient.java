package com.example.dksush0828.livebusking.kakao;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.net.URISyntaxException;


public class KakaoWebViewClient  extends WebViewClient {

    // 웹뷰 클라이언트를 상속한다.
    private Activity activity;

    public KakaoWebViewClient(Activity activity) {
        this.activity = activity;
    }

    @Override //현카등 외부 앱카드(앱)을 가져오는 경우 필요.
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if (!url.startsWith("http://") && !url.startsWith("https://") && !url.startsWith("javascript:")) {
            Intent intent = null;

            try {
                intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME); //IntentURI처리
                Uri uri = Uri.parse(intent.getDataString());
                activity.startActivity(new Intent(Intent.ACTION_VIEW, uri));//해당되는 Activity 실행

                return true;


            } catch (URISyntaxException ex) {
                return false;

            } catch (ActivityNotFoundException e) {

                if ( intent == null )	return false;

                String packageName = intent.getPackage(); //packageName should be com.kakao.talk
                if (packageName != null) {

                    activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + packageName)));
                    return true;
                }

                return false;
            }
        }

        return false;
    }

}
