package com.example.dksush0828.livebusking.common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInit {
    /***
     *초기화를 쉽게 하기 위해 Retrofit Init 이라는 이름으로
     *싱글톤 패턴을 사용해서 이 클래스에서 레트로핏을 정의해 주었다.
     *사용할 클래스에서는 그냥 불러오기만한다. 모든 클래스에서 재사용이 가능 하다.
     *
     *json으로 파일을 받아오기 때문에 json을 자동 변환 해주는
     *gson을 선언해주고 , 레트로핏 라이브러리를 정의 = 오류나는 경우가 있어서 gson을 GsonConverterFactory.create() 안에 선언하는 경우가 있음.
     *레트로핏을 빌드하기위해서는 인터페이스의 BASE_URL(서버 공인 아이피)와
     *JSON을 변화해주는 addconvertFactory - 필수이다. 없으면 오류가 난다.
     *빌드 후 레트로핏을 리턴해준다.
     */
    private static Retrofit mAppIntroRetrofit = null;



    public static Retrofit getmAppIntroRetrofit() {
        OkHttpClient client = new OkHttpClient();

        if (mAppIntroRetrofit == null) {
            Gson gson = new GsonBuilder().setLenient().create();
            mAppIntroRetrofit = new Retrofit.Builder()
                    .baseUrl(RetrofitApi.BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return mAppIntroRetrofit;
    }
}
