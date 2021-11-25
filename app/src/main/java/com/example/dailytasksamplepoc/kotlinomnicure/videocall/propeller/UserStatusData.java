package com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller;

import android.view.SurfaceView;

public class UserStatusData {
    public static final int DEFAULT_STATUS = 0;
    public static final int VIDEO_MUTED = 1;
    public static final int AUDIO_MUTED = VIDEO_MUTED << 1;

    private static final int DEFAULT_VOLUME = 0;
    private int mUid;
    private SurfaceView mView;
    private Integer mStatus; // if status is null, do nothing
    private Integer mAudioStatus;
    private int mVolume;
    private String mName;
    private String mProfilePic;
    private VideoInfoData mVideoInfo;



    public UserStatusData(int uid, SurfaceView view, Integer status, Integer audioStatus, int volume, String name, String profilePic) {
        this(uid, view, status, audioStatus, volume, null, name, profilePic);
    }

    public UserStatusData(int uid, SurfaceView view, Integer status, Integer audioStatus, int volume) {
        this(uid, view, status, audioStatus, volume, null, null, null);
    }
    public UserStatusData(int uid, SurfaceView view, Integer status, Integer audioStatus, int volume, VideoInfoData i) {
        this(uid, view, status, audioStatus, volume, i, null, null);
    }
    public UserStatusData(int uid, SurfaceView view, Integer status, Integer audioStatus, int volume, VideoInfoData i, String name, String profilePic) {
        this.mUid = uid;
        this.mView = view;
        this.mStatus = status;
        this.mVolume = volume;
        this.mVideoInfo = i;
        this.mName = name;
        this.mProfilePic = profilePic;
        this.mAudioStatus = audioStatus;
    }

    public void setVideoInfo(VideoInfoData video) {
        mVideoInfo = video;
    }

    public VideoInfoData getVideoInfoData() {
        return mVideoInfo;
    }

    @Override
    public String toString() {
        return "UserStatusData{" +
                "mUid=" + (mUid & 0XFFFFFFFFL) +
                ", mView=" + mView +
                ", mStatus=" + mStatus +
                ", mVolume=" + mVolume +
                '}';
    }


    public static int getDefaultVolume() {
        return DEFAULT_VOLUME;
    }

    public int getmUid() {
        return mUid;
    }

    public void setmUid(int mUid) {
        this.mUid = mUid;
    }

    public SurfaceView getmView() {
        return mView;
    }

    public void setmView(SurfaceView mView) {
        this.mView = mView;
    }

    public Integer getmStatus() {
        return mStatus;
    }

    public void setmStatus(Integer mStatus) {
        this.mStatus = mStatus;
    }

    public Integer getmAudioStatus() {
        return mAudioStatus;
    }

    public void setmAudioStatus(Integer mAudioStatus) {
        this.mAudioStatus = mAudioStatus;
    }

    public int getmVolume() {
        return mVolume;
    }

    public void setmVolume(int mVolume) {
        this.mVolume = mVolume;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmProfilePic() {
        return mProfilePic;
    }

    public void setmProfilePic(String mProfilePic) {
        this.mProfilePic = mProfilePic;
    }

    public VideoInfoData getmVideoInfo() {
        return mVideoInfo;
    }

    public void setmVideoInfo(VideoInfoData mVideoInfo) {
        this.mVideoInfo = mVideoInfo;
    }
}
