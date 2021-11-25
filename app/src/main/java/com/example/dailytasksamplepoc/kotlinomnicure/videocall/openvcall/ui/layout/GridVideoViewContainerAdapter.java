package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui.layout;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
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

//import android.support.v7.widget.RecyclerView;
//import androidx.recyclerview.widget.RecyclerView;

public class GridVideoViewContainerAdapter extends VideoViewAdapter {

    private final static Logger log = LoggerFactory.getLogger(GridVideoViewContainerAdapter.class);

    public GridVideoViewContainerAdapter(Activity activity, int localUid,int localStatus,int localAudioStatus, HashMap<Integer, SurfaceView> uids, ArrayList<Provider> providerList) {
        super(activity, localUid,localStatus,localAudioStatus, uids,providerList);
        log.debug("GridVideoViewContainerAdapter " + (mLocalUid & 0xFFFFFFFFL));
    }

    @Override
    protected void customizedInit(HashMap<Integer, SurfaceView> uids, boolean force) {
        VideoViewAdapterUtil.composeDataItem1(mUsers, uids, mLocalUid,mLocalStatus,mLocalAudioStatus); // local uid
        setProfileInUsers();
        if (force || mItemWidth == 0 || mItemHeight == 0) {
            WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);

            int count = uids.size();
            int DividerX = 1;
            int DividerY = 1;

            if (count == 2) {
                DividerY = 2;
            } else if (count == 3) {// FIX for 3 users = 1 + 2 peer
                DividerX = getNearestSqrt(4);
                DividerY = (int) Math.ceil(4 * 1.f / DividerX);
            } else if (count > 3) {
                DividerX = getNearestSqrt(count);
                DividerY = (int) Math.ceil(count * 1.f / DividerX);
            }

            int width = outMetrics.widthPixels;
            int height = outMetrics.heightPixels;

            if (width > height) {
                mItemWidth = width / DividerY;
                mItemHeight = height / DividerX;
            } else {
                mItemWidth = width / DividerX;
                mItemHeight = height / DividerY;
            }
        }
    }


    private int getNearestSqrt(int n) {
        return (int) Math.sqrt(n);
    }

    @Override
    public void notifyUiChanged(HashMap<Integer, SurfaceView> uids, int localUid, HashMap<Integer, Integer> status,HashMap<Integer, Integer> audioStatus, HashMap<Integer, Integer> volume) {
        if(status==null) {
            status = new HashMap<>();
            if (mUsers != null) {
                for (UserStatusData userStatusData : mUsers) {
                    status.put(userStatusData.getmUid(), userStatusData.getmStatus());
                }
            }
        }
        if(audioStatus==null) {
            audioStatus = new HashMap<>();
            if (mUsers != null) {
                for (UserStatusData userStatusData : mUsers) {
                    audioStatus.put(userStatusData.getmUid(), userStatusData.getmAudioStatus());
                }
            }
        }
        setLocalUid(localUid);

        VideoViewAdapterUtil.composeDataItem(mUsers, uids, localUid, status,audioStatus, volume, mVideoInfo);
        setProfileInUsers();
        notifyDataSetChanged();
        if (DEBUG) {
            log.debug("notifyUiChanged " + (mLocalUid & 0xFFFFFFFFL) + " " + (localUid & 0xFFFFFFFFL) + " " + uids + " " + status + " " + volume);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return super.onCreateViewHolder(parent, viewType);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoUserStatusHolder myHolder = ((VideoUserStatusHolder) holder);

        final UserStatusData user = mUsers.get(position);

        if (DEBUG) {
            log.debug("onBindViewHolder " + position + " " + user + " " + myHolder + " " + myHolder.itemView + " " + mDefaultChildItem);
        }

        FrameLayout holderView = (FrameLayout) myHolder.itemView;

        if (holderView.getChildCount() == mDefaultChildItem) {
            SurfaceView target = user.getmView();
            VideoViewAdapterUtil.stripView(target);
            holderView.addView(target, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        VideoViewAdapterUtil.renderExtraData(mContext, user, myHolder,true);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public UserStatusData getItem(int position) {
        return mUsers.get(position);
    }

    @Override
    public long getItemId(int position) {
        UserStatusData user = mUsers.get(position);

        SurfaceView view = user.getmView();
        if (view == null) {
            throw new NullPointerException("SurfaceView destroyed for user " + (user.getmUid() & 0xFFFFFFFFL) + " " + user.getmStatus() + " " + user.getmVolume());
        }

        return (String.valueOf(user.getmUid()) + System.identityHashCode(view)).hashCode();
    }
}
