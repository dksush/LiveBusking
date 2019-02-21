package com.example.dksush0828.livebusking.login;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.common.RetrofitApi;
import com.example.dksush0828.livebusking.common.RetrofitInit;
import com.example.dksush0828.livebusking.login.login_http.user_info;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class register extends AppCompatActivity {

    TextInputEditText joinNameEt,joinGenderEt,joinEmailEt,joinPwEt;
    String nickname, gender, email, psd;
    Button login_btn;
    private String TAG = "회원가입";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        joinNameEt = findViewById(R.id.joinNameEt);
        joinGenderEt = findViewById(R.id.joinGenderEt);
        joinEmailEt = findViewById(R.id.joinEmailEt);
        joinPwEt = findViewById(R.id.joinPwEt);
        login_btn = findViewById(R.id.joinNextBtn);


        // 가입하기 버튼.
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 입력값
                nickname = joinNameEt.getText().toString();
                email = joinEmailEt.getText().toString();
                psd = joinPwEt.getText().toString();
                gender = joinGenderEt.getText().toString();

                Log.v("register", gender);

                if(nickname.length() != 0 && gender.length() != 0 && email.length() != 0 && psd.length() != 0 ){

                    RetrofitApi retrofitApi = RetrofitInit.getmAppIntroRetrofit()
                            .create(RetrofitApi.class);
                    Call<user_info> call = retrofitApi.register(nickname,email,psd,gender);
                    call.enqueue(new Callback<user_info>() {
                        @Override
                        public void onResponse(Call<user_info> call, Response<user_info> response) {
                            Log.v(TAG,"회원가입 서버응답으로 들어옴1111111111");
                            Log.v(TAG,"회원가입 성공");
                                Toast.makeText(register.this,"회원가입이 완료 되었습니다.",Toast.LENGTH_SHORT).show();
                                //finish();
                                Intent intent = new Intent(register.this, Login.class);
                                startActivity(intent);
                        }

                        @Override
                        public void onFailure(Call<user_info> call, Throwable t) {
                            Log.v(TAG,"회원가입 서버응답으로 들어옴22222222");
                            Log.v(TAG,t.toString());
                            Log.v("뭔데 : ",t.getMessage());
                        }
                    });

//                    call.enqueue(new Callback<user_info>() {
//                        @Override
//                        public void onResponse(Call<user_info> call, Response<user_info> response) {
//                            Log.v(TAG,"회원가입 서버응답으로 들어옴");
//
//                            if (response.body().getResponse().equals("ok")) {
//                                Log.v(TAG,"회원가입 성공");
//                                Toast.makeText(register.this,"회원가입이 완료 되었습니다.",Toast.LENGTH_SHORT).show();
//                                //finish();
//                                Intent intent = new Intent(register.this, Login.class);
//                                startActivity(intent);
//
//                            } else if (response.body().getResponse().equals("exist")){
//                                Log.v(TAG,"서버연동은 됐지만 회원가입 실패");
//                                Toast.makeText(register.this,"존재하는 이메일 입니다.",Toast.LENGTH_SHORT).show();
//
//                            } else {
//                                Log.v(TAG,"서버연동실패 및 회원가입 실패");
//                                Toast.makeText(register.this,"회원가입에 실패 했습니다. 관리자에게 문의해주세요.",Toast.LENGTH_SHORT).show();
//
//                            }
//
//                        }
//
//                        @Override
//                        public void onFailure(Call<user_info> call, Throwable t) {
//
//                        }
//                    });


                }else{
                    Toast.makeText(register.this, "??", Toast.LENGTH_SHORT).show();
                }


            }
        });






    }
}
