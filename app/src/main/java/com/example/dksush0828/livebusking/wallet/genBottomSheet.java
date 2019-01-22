package com.example.dksush0828.livebusking.wallet;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.login.Login;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class genBottomSheet extends BottomSheetDialogFragment{

    Context context;
    Bitmap bitmap = null;

    public static genBottomSheet getInstance(){
        return new genBottomSheet();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         // 지갑 주소 가져오기
        context = Login.mContext;

        // 로그인 유저 닉네임.
        SharedPreferences user = context.getSharedPreferences("user_info",Context.MODE_PRIVATE);
        String nickname = user.getString("nickname","");

        // 닉네임을 기반으로 만들어진 지갑 주소와 이름을 가져온다.
        SharedPreferences now_wallet = context.getSharedPreferences(nickname,Context.MODE_PRIVATE);
        String address = now_wallet.getString("address","");
        String walletName = now_wallet.getString("wallet_name","");
        Log.v("BS_address : ", address);
        Log.v("BS_wallet_name : ", walletName);






        // 객체 선언.
        View view = inflater.inflate(R.layout.activity_gen_wallet_bottm_seet,container,false);
        ImageView wallet_qr_cord = view.findViewById(R.id.wallet_qr_cord);
        TextView wallet_name = view.findViewById(R.id.wallet_name);
        TextView wallet_address = view.findViewById(R.id.wallet_address);
        Button genWallet_btn = view.findViewById(R.id.genWallet_btn);



        wallet_name.setText(walletName); // 지갑이름
        wallet_address.setText(address); // 지갑주소
        wallet_qr_cord.setImageBitmap(generateRQCode(address)); // 큐알코드 생성.



        // 확인버튼
        genWallet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent();
                startActivity(new Intent(getActivity(), main_wallet.class));
            }
        });
        return view;

    }



    //   1) QRcodeWriter 객체를 생성합니다.
    //   2) QR코드에 들어갈 문자정보, 바코드포맷, QR코드 가로사이즈, QR코드 세로사이즈 셋팅
    //   3) Bitmap 생성

    public Bitmap generateRQCode(String contents) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        try {
             bitmap = toBitmap(qrCodeWriter.encode(contents, BarcodeFormat.QR_CODE, 100, 100));
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    public static Bitmap toBitmap(BitMatrix matrix) {
        int height = matrix.getHeight();
        int width = matrix.getWidth();
        Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bmp.setPixel(x, y, matrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bmp;
    }




}