package com.example.dksush0828.livebusking.rtc_peer.kurento;

import android.util.Log;

import com.nhancv.webrtcpeer.rtc_comm.ws.BaseSocketCallback;
import com.nhancv.webrtcpeer.rtc_comm.ws.SocketService;
import com.nhancv.webrtcpeer.rtc_peer.RTCClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

/**
 * Kurento-Media-Server 에 방송 시청자로 연결할 때 사용하는 Viewer Client 객체
 *
 * RTCClient 를 Implement 한다.
 * RTCClient 에서 sendOfferSdp 나 sendAnswerSdp 등을 오버라이딩하여 Viewer 가 라이브 방송을 시청을 시작할 때 필요한 정보를 넘기도록 한다.
 * 서버와 연결하고자할 때 sendOfferSdp 메소드를 오버라이딩하여 presenter 로 연결한다.
 *
 * Created by charlie on 2018. 5. 28
 */

public class KurentoViewerRTCClient implements RTCClient {
    private static final String TAG = KurentoViewerRTCClient.class.getSimpleName();
    private int presenterSessionId;     // 서버에 Node.js 상 방송 송출자가 가지고 있는 SessionId 값을 저장한다.
    private SocketService socketService;

    public KurentoViewerRTCClient(SocketService socketService) {
        this.socketService = socketService;
    }

    public void connectToRoom(String host, BaseSocketCallback socketCallback) {
        socketService.connect(host, socketCallback);
    }

    // 라이브 방송에 연결하기 전, 시청하고자 하는 방의 서버상 세션 아이디를 설정한다.
    public void setPresenterSID(int presenterSessionId){
        this.presenterSessionId = presenterSessionId;   // 세션 아이디 설정
    }

    @Override
    public void sendOfferSdp(SessionDescription sdp) {
        try {
            JSONObject obj = new JSONObject();  // SDP 를 담을 JSONObject
            obj.put("id", "viewer");    // Offer 를 보낸 사람이 시청자임을 알리는 id
            obj.put("sdpOffer", sdp.description);   // 시청자의 SDP 정보, 네트워크 정보와 미디어 정보 등이 들어있다.
            obj.put("presenterSessionId", presenterSessionId);  // 연결하고자 하는 방송의 세션 아이디
            socketService.sendMessage(obj.toString());  // 소켓으로 전달한다.
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendAnswerSdp(SessionDescription sdp) {
        Log.e(TAG, "sendAnswerSdp: ");
    }

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

            socketService.sendMessage(obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendLocalIceCandidateRemovals(IceCandidate[] candidates) {
        Log.e(TAG, "sendLocalIceCandidateRemovals: ");
    }

}
