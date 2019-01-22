package com.example.dksush0828.livebusking.rtc_peer.kurento;

import android.util.Log;

import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;
import com.nhancv.webrtcpeer.rtc_peer.RTCClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import timber.log.Timber;

/**
 * /**
 * Kurento-Media-Server 에 방송 송출자 (BJ) 로 연결할 때 사용하는 Presenter Client 객체
 *
 * RTCClient 를 Implement 한다.
 * RTCClient 에서 sendOfferSdp 나 sendAnswerSdp 등을 오버라이딩하여 Presenter 가 방송을 시작할 때 필요한 정보를 넘기도록 한다.
 * 서버와 연결하고자할 때 sendOfferSdp 메소드를 오버라이딩하여 presenter 로 연결한다.
 *
 * Created by charlie on 2018. 5. 28
 */


public class KurentoPresenterRTCClient implements RTCClient {
    private static final String TAG = KurentoPresenterRTCClient.class.getSimpleName();

    private SocketService socketService; // 서버에 RTCpeer 로 연결된 소켓에 접근하기 위한 소켓 서비스.

    private String nickname;      // 방송 송출자의 nicknae
    private String roomName;        // 방송의 제


    // Presenter 클라이언트 객체를 생성할 때 SocketService 를 초기화한다.
    public KurentoPresenterRTCClient(SocketService socketService) {
        this.socketService = socketService;
    }

    // 방송 송출자의 정보와 방의 정보를 설정하는 메소드.
    // 소켓에 연결한 다음 현재 연결된 소켓에 정보를 입력해줘야하는데 이 때 필요한 정보들을 설정한다.
    public void setInfo(String nickname, String roomName){
        this.nickname = nickname;   // 닉네임 설정
        this.roomName = roomName;       // 방 이름 설정

    }



    // 방에 Presenter 로 연결한다. WebRTC의 Room 은 '채팅방' 과는 좀 다른듯하다. 방송 하나하나의 방이 아니라, 그냥 영상을 교환할 쿠렌토 서버 그 자체를 지칭하는듯?
    // 방송의 송출자로 소켓에 연결함으로 내 미디어 데이터를 송출할 수 있도록 한다.
    public void connectToRoom(String host, BaseSocketCallback socketCallback) {
        socketService.connect(host, socketCallback);
    }


    // 이미 연결된 소켓을 통해 서버에 Peer 연결을 요청한다.
    // Offer 를 생성하여 나의 Session 정보를 넘겨준다. 보내는 이 고유의 "Session Id" 가 만들어진다.
    @Override
    public void sendOfferSdp(SessionDescription sdp) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "presenter");// 방송 송출자용 ID, id 를 presenter 로 함으로써 현재 전송하는 메시지가 방송 송출자에게서 온 것을 알려준다.
            obj.put("sdpOffer", sdp.description); // 나의 세션 정보 (미디어, 네트워크 정보) 를 JSONObject 에 담는다.


            JSONObject infoJson = new JSONObject(); // 방송에 필요한 추가 정보를 담는 JSONObject
            infoJson.put("nickname", nickname);    // 방송 송출자의 닉네임.
            infoJson.put("room_name",roomName);         // 방의 이름


            obj.put("info",infoJson);        // 추가 정보를 info Object 에 담는다.

            socketService.sendMessage(obj.toString()); // 웹소켓을 통해 서버에 '메세지' 형태로 전달한다.
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp) {
        Log.e(TAG, "sendAnswerSdp: ");
        Timber.v("sendAnswerSdp : "+sdp.description);
    }


    // sdp 를 통해 세션정보(나의 세션 id는 무엇이고, 어떤 비디오 파라미터를 사용하는지 등등) 을 교환헀다.
    // 하지만 여전히 '미디어 데이터 자체' 를 어떻게 교환해야하는 지는 알수 없다.

    // 그래서 방송 시청자가 나의 Peer 와 연결할 수 있도록 나의 네트워크 커넥션에 대한 정보를 보낸다.(ICE 후보군 교환)
    @Override
    public void sendLocalIceCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject obj = new JSONObject();
            obj.put("id", "onIceCandidate");

            JSONObject candidate = new JSONObject();
            candidate.put("candidate", iceCandidate.sdp);
            candidate.put("sdpMid", iceCandidate.sdpMid);
            candidate.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            obj.put("candidate", candidate);

            socketService.sendMessage(obj.toString()); // 웹소켓을 통해 서버로 보낸다.

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {
        Log.e(TAG, "sendLocalIceCandidateRemovals: ");
    }

}
