package com.example.dksush0828.livebusking;

import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        




//        String fileName = WalletUtils.generateNewWalletFile(
//                "your password",
//                new File("/path/to/destination"));

        // mp4로 녹화??
//        exec ffmpeg -i rtmp://localhost:1935/hls/$name -c:v libx264 -vprofile baseline -acodec aac -strict -2 -f mp4  /usr/local/nginx/html/flv_video/$name.mp4;
//
//
//
//
//        exec ffmpeg -i rtmp://localhost/live/$name
//        -c:a libfdk_aac -b:a 32k  -c:v libx264 -b:v 128K -f flv rtmp://localhost/hls/$name_low
//        -c:a libfdk_aac -b:a 64k  -c:v libx264 -b:v 256k -f flv rtmp://localhost/hls/$name_mid;
//
//
//    }
//
//
//    application hls{
//        live on;
//        hls on;
//        hls_path /usr/local/nginx/html/hls;
//        hls_nested on;
//
//
//        hls_variant _low BANDWIDTH=160000;
//        hls_variant _mid BANDWIDTH=320000;
//
//


  }


    public String[] createWallet(final String password) {
        String[] result = new String[2];
        try {
            File file_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); //다운로드 path 가져오기
            // getExternalStoragePublicDirectory : 외부 저장경로를 얻어온다.

            if (!file_path.exists()) {
                file_path.mkdir();
            }

            Log.v("파일경로 : ", String.valueOf(file_path));

            //지갑파일 생성 : 패스워드와 path 를 주면 지갑을 생성해주는 메소드.
            String fileName = WalletUtils.generateLightNewWalletFile(password, new File(String.valueOf(file_path)));
            result[0] = file_path+"/"+fileName;

            Log.v("파일이름 : ", result[0]);

            // 위에서 만든 지갑 파일로부터 인증서? 를 가져온다.
            Credentials credentials = WalletUtils.loadCredentials(password,result[0]);
            result[1] = credentials.getAddress();
            Log.v("인증서 (Credentials) : ", result[1]);
            Log.v("지갑생성 최종 result : ", String.valueOf(result));

            return result;
        } catch (NoSuchAlgorithmException
                | NoSuchProviderException
                | InvalidAlgorithmParameterException
                | IOException
                | CipherException e) {
            e.printStackTrace();
            return null;
        }
    }







    }
