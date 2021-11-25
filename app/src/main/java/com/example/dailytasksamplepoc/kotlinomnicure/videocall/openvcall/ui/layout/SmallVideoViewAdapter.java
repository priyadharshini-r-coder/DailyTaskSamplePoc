package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui.layout;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.mvp.omnicure.videocall.propeller.UserStatusData;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;

import omnicure.mvp.com.providerEndpoints.model.Provider;

public class SmallVideoViewAdapter extends VideoViewAdapter {

    private final static Logger log = LoggerFactory.getLogger(SmallVideoViewAdapter.class);

    private int mExceptedUid;

    public SmallVideoViewAdapter(Activity activity, int localUid, int localStatus, int localAudioStatus, int exceptedUid, HashMap<Integer, SurfaceView> uids, ArrayList<Provider> providerList) {
        super(activity, localUid, localStatus, localAudioStatus, uids, providerList);
        mExceptedUid = exceptedUid;
        log.debug("SmallVideoViewAdapter " + (mLocalUid & 0xFFFFFFFFL) + " " + (mExceptedUid & 0xFFFFFFFFL));
    }

    @Override
    protected void customizedInit(HashMap<Integer, SurfaceView> uids, boolean force) {
        HashMap<Integer, Integer> status = new HashMap<>();
        status.put(mLocalUid, mLocalStatus);
        HashMap<Integer, Integer> audioStatus = new HashMap<>();
        audioStatus.put(mLocalUid, mLocalAudioStatus);
        VideoViewAdapterUtil.composeDataItem(mUsers, uids, mLocalUid, status, audioStatus, null, mVideoInfo, mExceptedUid);
        setProfileInUsers();
        if (force || mItemWidth == 0 || mItemHeight == 0) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            mItemWidth = (outMetrics.widthPixels / 4);
            mItemHeight = mItemWidth;//outMetrics.heightPixels / 4;
        }
    }

    @Override
    public void notifyUiChanged(HashMap<Integer, SurfaceView> uids, int uidExcepted, HashMap<Integer, Integer> status, HashMap<Integer, Integer> audioStatus, HashMap<Integer, Integer> volume) {
        if (status == null) {
            status = new HashMap<Integer, Integer>();
            if (mUsers != null) {
                for (UserStatusData userStatusData : mUsers) {
                    status.put(userStatusData.getmUid(), userStatusData.getmStatus());
                }
            }
        }
        if (audioStatus == null) {
            audioStatus = new HashMap<>();
            if (mUsers != null) {
                for (UserStatusData userStatusData : mUsers) {
                    audioStatus.put(userStatusData.getmUid(), userStatusData.getmAudioStatus());
                }
            }
        }
//        mUsers.clear();
        mExceptedUid = uidExcepted;

        log.debug("notifyUiChanged " + (mLocalUid & 0xFFFFFFFFL) + " " + (uidExcepted & 0xFFFFFFFFL) + " " + uids + " " + status + " " + volume);
        VideoViewAdapterUtil.composeDataItem(mUsers, uids, mLocalUid, status, audioStatus, volume, mVideoInfo, uidExcepted);
        setProfileInUsers();
        notifyDataSetChanged();
    }

    public int getExceptedUid() {
        return mExceptedUid;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoUserStatusHolder myHolder = ((VideoUserStatusHolder) holder);

        final UserStatusData user = mUsers.get(position);
        Log.d("TAG", "onBindViewHolder user: " + user.getmName() + " status: " + user.getmStatus());

        if (DEBUG) {
            log.debug("onBindViewHolder " + position + " " + user + " " + myHolder + " " + myHolder.itemView + " " + mDefaultChildItem);
        }

        FrameLayout holderView = (FrameLayout) myHolder.itemView;

        if (holderView.getChildCount() == mDefaultChildItem) {
            Log.d("TAG", "stip view called");
            SurfaceView target = user.getmView();
            VideoViewAdapterUtil.stripView(target);
            holderView.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        VideoViewAdapterUtil.renderExtraData(mContext, user, myHolder, mExceptedUid, false);
    }

}
