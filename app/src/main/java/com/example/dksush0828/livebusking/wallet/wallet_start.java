package com.example.dksush0828.livebusking.wallet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.dksush0828.livebusking.R;

import jnr.ffi.annotations.In;

public class wallet_start extends AppCompatActivity {

    Button Create_wallet_btn,Get_wallet_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_start);

        Create_wallet_btn = findViewById(R.id.Create_wallet_btn); // 지갑 새로 만들기 버튼.
        Get_wallet_btn = findViewById(R.id.Get_wallet_btn); // 이미 생성된 지갑 가져오기



       // 지갑 새로 만들기.
        Create_wallet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(wallet_start.this, Generate_Wallet.class);
                startActivity(intent);
            }
        });


        Get_wallet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main_intent = new Intent(wallet_start.this, main_wallet.class);
                startActivity(main_intent);
            }
        });


    }
}
