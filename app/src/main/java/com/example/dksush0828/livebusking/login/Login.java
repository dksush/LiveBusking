package com.example.dksush0828.livebusking.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.common.RetrofitApi;
import com.example.dksush0828.livebusking.common.RetrofitInit;
import com.example.dksush0828.livebusking.login.login_http.user_info;
import com.example.dksush0828.livebusking.main.home;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Login extends AppCompatActivity {
    private String tag = "태그";
    String email, psd;
    EditText loginEmailEt,loginPwEt; // 로그인, 비번 객체.

    //쉐어드
    SharedPreferences user;
    SharedPreferences.Editor login_userinfo;

    public static Context mContext;
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_login);


        loginEmailEt = (EditText)findViewById(R.id.loginEmailEt);
        loginPwEt = (EditText)findViewById(R.id.loginPwEt);

        // 유저 정보 쉐어드.
        user = getSharedPreferences("user_info",MODE_PRIVATE);
        login_userinfo = user.edit();



        // 로그인 버튼
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = loginEmailEt.getText().toString();
                psd = loginPwEt.getText().toString();

                // 이메일 레트로핏 초기화. 서버 전송에 필요한 항목.
                RetrofitApi retrofitApi = RetrofitInit.getmAppIntroRetrofit()
                        .create(RetrofitApi.class);

                // api 선언. 서버에서 반환된 JSON 을 받아오는 모델 클래스이다.
                Call<user_info> call = retrofitApi.login(email,psd);
                call.enqueue(new Callback<user_info>() {
                    @Override
                    public void onResponse(Call<user_info> call, Response<user_info> response) {
                        Log.v(tag, "이메일 로그인 서버 연동 성공");
                        Log.v(tag, response.body().toString());


                        if(response.body().getResponse().equals("ok")){
                            login_userinfo.putString("nickname", response.body().getNickname());
                            login_userinfo.apply();
                            Intent intent2 = new Intent(Login.this, home.class);
                            startActivity(intent2);


                        }
                    }

                    @Override
                    public void onFailure(Call<user_info> call, Throwable t) {
                        Log.v(tag,"이메일 로그인 서버 연동 실패");
                        Toast.makeText(Login.this, "이메일 혹은 비밀번호를 확인해 주세요.", Toast.LENGTH_SHORT).show();

                    }
                });



            }
        });



        // 회원가입 버튼
        Button joinBtn = findViewById(R.id.joinBtn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, register.class);
                startActivity(intent);
            }
        });








    }
}
