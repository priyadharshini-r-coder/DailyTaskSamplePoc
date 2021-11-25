package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.model;

public interface DuringCallEventHandler extends AGEventHandler {

    void onUserJoined(int uid);

    void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed);

    void onJoinChannelSuccess(String channel, int uid, int elapsed);

    void onUserOffline(int uid, int reason);
    void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) ;
    void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) ;

    void onExtraCallback(int type, Object... data);
}
