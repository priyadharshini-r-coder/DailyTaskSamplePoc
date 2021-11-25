package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.model;

public class EngineConfig {
    private int mUid;

    private String mChannel;

    public int getmUid() {
        return mUid;
    }

    public void setmUid(int mUid) {
        this.mUid = mUid;
    }

    public String getmChannel() {
        return mChannel;
    }

    public void setmChannel(String mChannel) {
        this.mChannel = mChannel;
    }

    public void reset() {
        mChannel = null;
    }

    public EngineConfig() {
    }
}
