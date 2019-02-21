package com.example.dksush0828.livebusking.wallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.os.Environment;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.dksush0828.livebusking.R;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Numeric;


import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class Generate_Wallet extends AppCompatActivity  {

    /**
     * 토큰 지갑 생성하는 액티비티
     *
     * 토큰 생성 요청시 비밀번호를 입력하게 한다.
     * 디바이스에 키 파일을 생성한다. 생성된 파일의 이름 중 -- 뒤에 있는 주소를 SharedPreference 와 SQLite DB 에 저장한다.
     */

    TextInputEditText Wallet_Psd_ed; // 비번란.
    Button Wallet_create_btn;
    ImageView WalletBackIv;
    String password;


    Context context;


    // 방금 만든 지갑.
    SharedPreferences now_wallet;
    SharedPreferences.Editor now_wallet_edit;

    SharedPreferences user; // 로그인 아이디.

    KeyDBHelper keyDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate__wallet);




        Wallet_Psd_ed = findViewById(R.id.Wallet_Psd_ed); // 비번입력란.
        Wallet_create_btn = findViewById(R.id.Wallet_create_btn);
        WalletBackIv = findViewById(R.id.WalletBackIv); // 백버튼


        // 비번 입력 후 => 지갑 생성하기 버튼.
        Wallet_create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                password = Wallet_Psd_ed.getText().toString();
                genWallet(); // 지갑 생성.
            }
        });


        // 뒤로가기 버튼.
        WalletBackIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Generate_Wallet.this, wallet_start.class);
                startActivity(intent);

            }
        });



        //SQLite
        keyDBHelper = new KeyDBHelper(getApplicationContext(),"key_list",null,1);
        keyDBHelper.keyDB();



    }


    public void genWallet(){

        if (Wallet_Psd_ed.length()==0){
            Wallet_Psd_ed.setError("비밀번호를 입력해주세요");
            return;
        }

        try {

            // 앱 외부 메모리 저장(SD 카드) : sd가 없거나, 용량이 없다면 내부 저장소를 연결할 수 도 있다.(getExternalStorageDirectory)
            // 외부 네트워크와 교류할 "(종이)지갑"을 저장하는 곳
            // :: 밑에 유저 식별정보인 "keystore 파일"은 내부 저장소에 저장한다(getFilesDir())
            File fileDir = new File(Environment.getExternalStorageDirectory().getPath() + "/LightWallet"); // 다운로드 경로? : KeyStore 저장경로같다.


            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            // 개인키, 공개키를 생성?
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            Log.v("fileDir : ", String.valueOf(fileDir));
            Log.v("ecKeyPair : ", String.valueOf(ecKeyPair));


            // generateWalletFile ; 패스워드와 KeyStore경로? 를 입력하면
            // 지갑파일(KeyStore파일), 지갑주소, PrivateKey, PublicKey 가 생성된다.
            String filename = WalletUtils.generateWalletFile(password, ecKeyPair, fileDir, false); // 지갑파일 생성(KeyStore 생성? 아마..?)
            String address = KeyStoreUtils.genKeyStoreToFiles(ecKeyPair,getApplicationContext());  // 지갑 주소를 가져온다.


            String privateKey = Numeric.encodeQuantity(ecKeyPair.getPrivateKey()); // 개인키
            String publicKey = Numeric.encodeQuantity(ecKeyPair.getPublicKey()); // 공개키


            Log.v("fileName",filename); // 지갑 파일.
            if(!address.equals("")){
                Log.v("address",address);  // 지갑주소
            }


            Log.v("privateKey",privateKey);
            Log.v("publicKey",publicKey);


            // 지갑 번호 저장.
            SharedPreferences wallet_num  = getSharedPreferences("wallet_num",MODE_PRIVATE);
            SharedPreferences.Editor wallet_num_edit = wallet_num.edit();
            wallet_num_edit.putInt("wallet_num",1);

            // 지갑 이름 만들기
            int lastest_wallet_num = wallet_num.getInt("wallet_num",0);
            Log.v("lastest_wallet_num : ", String.valueOf(lastest_wallet_num));
            String wallet_name = "wallet_"+ String.valueOf(lastest_wallet_num); // 지갑 이름 ex) "wallet_1"


            // 방금 만든 지갑은 로그인 nickname을 전체 키값으로 쉐어드에 저장한다.
            user = getSharedPreferences("user_info",MODE_PRIVATE);
            String nickname = user.getString("nickname","");

            now_wallet = getSharedPreferences(nickname, MODE_PRIVATE);
            now_wallet_edit = now_wallet.edit();
            now_wallet_edit.putString("wallet_name",wallet_name); // 지갑이름(자체부여)
            now_wallet_edit.putString("address",address); // 지갑주소.
            now_wallet_edit.putString("fileName",filename); // 파일이름
            now_wallet_edit.putString("privateKey",privateKey);
            now_wallet_edit.putString("publicKey",publicKey);

            now_wallet_edit.apply();

            // 지갑 번호 갱신 후 저장.
            wallet_num_edit.putInt("wallet_num",lastest_wallet_num+1);
            wallet_num_edit.apply();



            ///// 지갑은 sqlite에 집어넣는다. 폰 자체가 정말 지갑인 샘.
            /// 근데 만약 폰을 바꾸면? 음. 고민의 여지가 있지만, 그냥 오래나만에 sqlite 연습하는 샘 치지 뭐.

            if(keyDBHelper.insertNewKey(wallet_name,address)){
                Log.v("sqlite : "," 데이터 잘 들어감.");
            }else{
                Log.v("sqlite : ","실패!");
            }

            // 바텀시트
            genBottomSheet BS = genBottomSheet.getInstance();
            BS.show(getSupportFragmentManager(),"bottomSheet");


        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CipherException e) {
            e.printStackTrace();
        }


    }

}
