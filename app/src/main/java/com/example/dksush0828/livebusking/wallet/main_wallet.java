package com.example.dksush0828.livebusking.wallet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dksush0828.livebusking.R;
import com.example.dksush0828.livebusking.kakao.KakaoWebViewClient;
import com.example.dksush0828.livebusking.live.Rtmp_broadcast;
import com.example.dksush0828.livebusking.main.home;
import com.example.dksush0828.livebusking.wallet.Web3jService;
import com.example.dksush0828.livebusking.wallet.contract.TokenContract;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthLog;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;

public class main_wallet extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener,  View.OnClickListener{


    private SwipeRefreshLayout walletSwipe;   // 당겨서 새로고침 레이아웃

    TextView charge; // 카카오로 충전하기.
    TextView wallet_name; // 지갑 이름.
    TextView wallet_address; // 지갑주소소.
    TextView token_num; // 잔액 : 토큰 보유개수.
    ImageView WalletBackIv,walletRefresh; // 뒤로가기, 리프레쉬 아이콘.
    Button walletSendBtn,walletReceiveBtn; // 상대에게 보내기, 받기 버튼.

    // 지갑이름과 주소 가져오귀.
    SharedPreferences now_wallet;
    SharedPreferences.Editor now_wallet_edit;

    //리사이클러뷰.
    RecyclerView wallet_Recycle;
    private RecyclerView.LayoutManager mLayoutManager;

    private Web3j web3j;
    private TokenContract Token;
    private Boolean isInitWallet = false;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0"); // 숫자 "," 패턴 : string으로 뱉는다.
    String name, address; // 지갑 이름과 주소.

    KeyDBHelper keyDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_wallet);

        // 현재 사용할 지갑 이름과 주소를 가져온다.
        SharedPreferences user = getSharedPreferences("user_info",MODE_PRIVATE);
        String nickname = user.getString("nickname","");

        now_wallet = getSharedPreferences(nickname,MODE_PRIVATE);
        name =  now_wallet.getString("wallet_name",""); // 지갑이름.
        address = now_wallet.getString("address","");  // 지갑주소.
        Log.v("개인키 ; ", now_wallet.getString("privateKey",""));

        bindViews(); // 뷰를 연결한다.
        initWallet(); // 지갑 초기화.
        walletSwipe.setOnRefreshListener(this::bindViews); // 리사이클러뷰 상단 끌어당길시 갱신. onRefresh() 에 작동할 내용을 입력한다.




    }

    @Override
    public void onRefresh() {
        // 새로고침 코드
        // 디비서 다시 꺼내오기.

    }



    /*
     * 지갑을 초기화하는 메소드
     */
    @SuppressLint("CheckResult")
    private void initWallet(){
        if(web3j == null){
            web3j = Web3jService.getInstance();
        }
        try{
            // Observable : 데이터 스트리. 특정 신호가 들어올때 데이터를 처리하고 다른 구성요소에 전달한다.
            // Observers : Observable 에서 나온 데이터를 소비.
            // -> subscribeOn() 메소드를 통해 subscribeOn() 를 구독, 방출된 데이터 수신.
            // -> 등록된 모든 observer는 onNext() 콜백으로 데이터를 수신한다.
            // -> 여기에서 JSON 응답 파싱이나 UI 업데이트와 같은 다양한 작업을 수행. observable에서 에러가 발생하면, observer는 onError()에서 에러를 수신.
            io.reactivex.Observable.create((ObservableOnSubscribe<TokenContract>) emitter -> {

                TokenContract Token = new TokenContract(TokenContract.CONTRACT_ADDRESS,
                        Web3jService.getInstance(), // 이더리움 네트워크와 연결?
                        KeyStoreUtils.getCredentials(address,getApplicationContext()), // 보증서? 가져오기.
                        BigInteger.valueOf(41), // gasPrice : Gas Limit에서 1 Gas 당 가격, 높을 수록 빨리 실행된다.
                        BigInteger.valueOf(3000000)); //gasLimit
                emitter.onNext(Token); // 값을 하나씩 넣어 에러 여부를 확인다.
                emitter.onComplete(); // 에러가 없다면 onComplete 를 호출해 작업이 끝났음을 알린다.

            })
                    // Schedulers : 비동기 작업을 위한 스케줄러.
                    .subscribeOn(Schedulers.io()) // 실행하라는 소리.
                    .observeOn(AndroidSchedulers.mainThread()) // observers 에게 관찰해야 할 스레드를 알려줌 : RxJava에서 제공 된 메인 기본 스레드는 새로운 백그라운드를 생성.
                    .subscribe(Token -> {
                        this.Token = Token;
                        Log.d("Contract","OK");
                        isInitWallet = true;    // 지갑 초기화가 완료 되면 true 를 저장한다.
                        getWalletInfo();    //  지갑 정보를 가져온다.
                     //   getTransactionLogs();     // 트랜잭션 리스트를 가져온다.
                    }, Throwable::printStackTrace);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /*
     * 지갑의 현재 토큰 개수를 확인한다.
     */
    @SuppressLint({"CheckResult", "SetTextI18n"})
    private void getWalletInfo() {
        io.reactivex.Observable.create((ObservableOnSubscribe<BigInteger>) e -> {
            Log.v("현재지갑토큰 개수 확인주소 : ",address+"..");


            BigInteger send = Token.balanceOf(address).send();  // 이더리움 네트워크에 지갑 잔액을 가져오는 메소드를 호출함.
            e.onNext(send); // balanceOf 메소드 호출이 성공 한다면 send 를 파라미터로 넘겨준다.
            e.onComplete();
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(value -> {

                    Log.v("잔액 : ",value.toString()+"..");
                    token_num.setText(decimalFormat.format(value));
                    Snackbar.make(walletReceiveBtn, "지갑을 갱신하였습니다.",Snackbar.LENGTH_SHORT).show();
                }, Throwable::printStackTrace);
    }


    // 자바버전 ???? 시발?? 너 가독성 좋은 버전이 있었자나??????
//    public String getBalance(String address)
//    {
//        //통신할 노드의 주소를 지정해준다.
//        Web3j web3 = Web3jFactory.build(new HttpService("https://ropsten.infura.io/ABCDEFGHIJK"));
//        String result = null;
//        EthGetBalance ethGetBalance = null;
//        try {
//
//            //이더리움 노드에게 지정한 Address 의 잔액을 조회한다.
//            ethGetBalance = web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).sendAsync().get();
//            BigInteger wei = ethGetBalance.getBalance();
//
//            //잔액조회를 하게되면 wei 단위로 받기때문에 보기좋게 wei 단위에서 ETH 단위로 변환한다.
//
//            result = Convert.fromWei(wei.toString() , Convert.Unit.ETHER).toString();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
//        return result;
//    }


 private void getTransactionLogs(){
        new Thread(){
            @Override
            public void run() {
                // EthFilter ; 이더리움 네트워크에서 발생하는 어떤 이벤트의 세부사항을 가져올 수 있다.
                EthFilter filter = new EthFilter(DefaultBlockParameterName.EARLIEST,
                        DefaultBlockParameterName.LATEST, TokenContract.CONTRACT_ADDRESS); // 필터링 하고자 하는 스마트 계약서의 주소를 집어넣는다.
                try {

                    // 트랜잭션 로그 데이터를 가져온다.
                    // 리사이클러뷰에 넣을것이기에 List 형태로 가져옴.
                    List<EthLog.LogResult> list = web3j.ethGetLogs(filter).send().getResult();
                    for(EthLog.LogResult logResult : list){
                        String fullLog = logResult.get().toString();

                        String logSplit[] = fullLog.split("',");
                        String hash = logSplit[2].replace(" transactionHash='","");
                        Long quantity = Long.parseLong(logSplit[6].replace("data='","").replace("0x","").replace(" ",""),16);

                        String topics[] = logSplit[8].replace("topics=[","").replace("]}","").split(", ");

                        String from = "0x"+topics[1].substring(26);
                        String to = "0x"+topics[2].substring(26);

                        Log.v("fullLog : ",fullLog);
                        Log.v("hash : ",hash);
                        Log.v("quantity : ", String.valueOf(quantity));
                        Log.v("topics : ", String.valueOf(topics));
                        Log.v("from : ",from);
                        Log.v("to : ",to);

                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }.start();
 }





    private void bindViews(){
        walletSwipe = findViewById(R.id.walletSwipe); //끌어당겨 갱신.
        charge = findViewById(R.id.charge);
        wallet_name = findViewById(R.id.wallet_name);
        wallet_address = findViewById(R.id.wallet_address);
        token_num = findViewById(R.id.token_num); // 현재 지갑의 잔액.
        WalletBackIv = findViewById(R.id.WalletBackIv);
        walletRefresh = findViewById(R.id.walletRefresh);
        walletSendBtn = findViewById(R.id.walletSendBtn);
        walletReceiveBtn = findViewById(R.id.walletReceiveBtn);

        wallet_name.setText(name);
        wallet_address.setText(address);
        setClickListeners(); // 클릭 리스너 연결.

    }

    private void setClickListeners(){
        WalletBackIv.setOnClickListener((View.OnClickListener) this);
        charge.setOnClickListener((View.OnClickListener) this);
        walletRefresh.setOnClickListener((View.OnClickListener) this);
        walletSendBtn.setOnClickListener((View.OnClickListener) this);
        walletReceiveBtn.setOnClickListener((View.OnClickListener) this);
    }


    @Override // 버튼 클릭 이밴트.
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.charge : // 충전하기
//                Intent intent = new Intent(getApplicationContext(), KakaoWebViewClient.class);
//                startActivityForResult(intent,3001);
                final EditText edittext = new EditText(this);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("충전금액");
                builder.setView(edittext);
                builder.setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                int request_token = Integer.parseInt(edittext.getText().toString());//방제목.
                                requestToken(request_token);


                            }
                        });
                builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.show();

            case R.id.walletSendBtn:
                IntentIntegrator integrator = new IntentIntegrator(main_wallet.this); // zxing 을 불러온다.
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES); // qr 타입 지정.
                integrator.setOrientationLocked(true); // 세로모.
                integrator.setCameraId(0); // 디바이스의 특정 카메라를 사용한다 ??
                integrator.setBeepEnabled(false); // 스캔을 할때 폰에서 "삐" 소리가 나는걸 막는다.
                integrator.setPrompt(""); // 화면 하단에 "QR_Scan" 란 글자를 띄운다.
                integrator.setRequestCode(5000); // 화면을 찍고 "onActivityResult" 로 돌아온다.
                integrator.initiateScan();// 초기화시킨다.

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 5000){
            IntentResult result = IntentIntegrator.parseActivityResult(resultCode,data); // 스캔해온 qr코드 파싱.
            if(result.getContents() ==null){
                Log.v("qr_scan : ", "fail");
            }else{
                genBottomSheet BS = genBottomSheet.getInstance();
                BS.show(getSupportFragmentManager(),"bottomSheet");
            }
        }
    }

    /*
     * 토큰 오너에게 토큰을 요청하는 메소드
     */
    @SuppressLint({"CheckResult", "SetTextI18n"})
    public void requestToken(int value){

        token_num.setText(decimalFormat.format(value));
        Log.v("request address",address);
        Log.v("request value",value+"..");
        io.reactivex.Observable  // 토큰 요청 시작
                .create((ObservableOnSubscribe<TransactionReceipt>) e -> {
                    TransactionReceipt send  = Token.requestToken(address,BigInteger.valueOf(value)).send();
                    e.onNext(send);
                    e.onComplete();
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(respons -> {
                    String result = respons.getBlockHash();
                    if (result != null) {
                        Log.v("transaction OK", result + "..");
                        Toast.makeText(getApplicationContext(),"요청이 성공했습니다.",Toast.LENGTH_SHORT).show();
                        getWalletInfo();
                    } else {
                        Log.v("transaction NO",  "..");

                        Toast.makeText(getApplicationContext(),"요청이 실패했습니다.",Toast.LENGTH_SHORT).show();
                    }

                }, Throwable::printStackTrace);
        //mkLoader.setVisibility(View.GONE);  // 프로그레스 바를 숨긴다.
    }
}
