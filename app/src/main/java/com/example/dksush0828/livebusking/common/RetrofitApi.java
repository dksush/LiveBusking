package com.example.dksush0828.livebusking.common;

import com.example.dksush0828.livebusking.login.login_http.user_info;
import com.example.dksush0828.livebusking.main.Total_video_item;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RetrofitApi {

    /**
     * 서버 전송에 필요한 항목.
     *인터페이스를 지정하는부분
     *자기가 사용하는 서버의 공인 아이피와
     *php (편집기 - 아톰) 어떤 파일에 데이터를 전송할지 보내는 함수
     *call에 전송결과를 받을 모델을 명시하고,
     *login 등 필요 목적의 함수 이름을 정의.
     *php에서 전달 받는 $_...['x']와 같은 키값을 적어서 보낸다.
     *
     * 서버에서 데이터를 json 으로 받아와서 리사이클러뷰에 뿌리는 서버연결은
     * 해당 인터페이스가 클래스에 선언이 되서 다른곳에서 재사용이 안된다.
     * 그래서 같은 목적일지라도 새롭게 만들어서 써야지 작동했다.
     */
    String BASE_URL = "http://101.101.165.127";

    //이메일 회원가입
    @FormUrlEncoded
    @POST("/Register.php")
    Call<user_info> register(@Field("nickname") String nickname,
                             @Field("email") String email,
                             @Field("psd") String psd,
                             @Field("gender") String gender);

    //이메일 로그인
    @FormUrlEncoded
    @POST("/login.php")
    Call<user_info> login(@Field("email") String email,
                          @Field("psd") String psd);

    // 라이브 방송데이터 디비에 저장하기.
    @FormUrlEncoded
    @POST("/Make_live.php")
    Call<user_info> make_live(
                              @Field("nickname") String nickname,
                              @Field("video_title") String video_title,         // 비디오 제목.
                              @Field("view_number") int view_number,               // 죄회수.
                              @Field("thumbnail_path") String thumbnail_path,     // 섬네일 경로.
                              @Field("video_type") String video_type, // 비디오 타입(라이브 = 1 , vod = 0)
                              @Field("vod_path") String vod_path); //vod 경로

    //total 페이지정보 db에서 가져오기.
    @FormUrlEncoded
    @POST("/GetTotalVideoItem.php")
    Call<List<Total_video_item>> gettotalitem(@Field("page") int page);




    // 조회수 갱신( 어떤걸 갱신할지 알리기 위해
    // 조회수, video_idx를 함께 올린다.
    @FormUrlEncoded
    @POST("/Update_view_number.php")
    Call<user_info> update_view_number(
            @Field("view_number") int view_number,
            @Field("view_idx") int view_idx // 비디오 고유번호

    );

    // 비디오 타입 변경. 라이브가 종료되면 디비에 라이브(1) => vod(0) 라고 변경된다 ; string이다. 왜 그랬을까ㅏ.
    @FormUrlEncoded
    @POST("/Update_video_type.php")
    Call<user_info> update_video_type(
        @Field("video_type") String video_type,
        @Field("nickname") String nickname,
        @Field("video_title") String video_title

    );








}
