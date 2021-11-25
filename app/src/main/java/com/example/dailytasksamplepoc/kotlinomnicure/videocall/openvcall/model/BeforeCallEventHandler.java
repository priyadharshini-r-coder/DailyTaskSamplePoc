package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.model;

import io.agora.rtc.IRtcEngineEventHandler;

public interface BeforeCallEventHandler extends AGEventHandler {
    void onLastmileQuality(int quality);

    void onLastmileProbeResult(IRtcEngineEventHandler.LastmileProbeResult result);
}
