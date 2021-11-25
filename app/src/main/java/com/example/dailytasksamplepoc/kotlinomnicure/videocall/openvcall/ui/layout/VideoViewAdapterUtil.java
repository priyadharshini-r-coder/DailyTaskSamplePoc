package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui.layout;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.UserStatusData;
import com.mvp.omnicure.R;
import com.mvp.omnicure.utils.UtilityMethods;
import com.mvp.omnicure.videocall.propeller.Constant;
import com.mvp.omnicure.videocall.propeller.UserStatusData;
import com.mvp.omnicure.videocall.propeller.VideoInfoData;
import com.mvp.omnicure.videocall.propeller.ui.ViewUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class VideoViewAdapterUtil {

    private final static Logger log = LoggerFactory.getLogger(VideoViewAdapterUtil.class);

    private static final boolean DEBUG = false;

    public static void composeDataItem1(final ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids, int localUid, int localStatus, int localAudioStatus) {
//        Log.d("TAG", "composeDataItem1: "+localUid+" status: "+localStatus);

        for (HashMap.Entry<Integer, SurfaceView> entry : uids.entrySet()) {
            if (DEBUG) {
                log.debug("composeDataItem1 " + (entry.getKey() & 0xFFFFFFFFL) + " " + (localUid & 0xFFFFFFFFL) + " " + users.size() + " " + entry.getValue());
            }
            SurfaceView surfaceV = entry.getValue();
            surfaceV.setZOrderOnTop(false);
            surfaceV.setZOrderMediaOverlay(false);
//            searchUidsAndAppend(users, entry, localUid, localStatus,localAudioStatus, UserStatusData.DEFAULT_VOLUME, null);
            searchUidsAndAppend(users, entry, localUid, localStatus,localAudioStatus, UserStatusData.getDefaultVolume(), null);
        }

        removeNotExisted(users, uids, localUid);
    }

    private static void removeNotExisted(ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids, int localUid) {
        if (DEBUG) {
            log.debug("removeNotExisted all " + uids + " " + users.size());
        }
        Iterator<UserStatusData> it = users.iterator();
        while (it.hasNext()) {
            UserStatusData user = it.next();
            if (DEBUG) {
                log.debug("removeNotExisted " + user + " " + localUid);
            }
            if (uids.get(user.getmUid()) == null && user.getmUid() != localUid) {
                it.remove();
            }
        }
    }

    private static void searchUidsAndAppend(ArrayList<UserStatusData> users, HashMap.Entry<Integer, SurfaceView> entry,
                                            int localUid, Integer status,Integer audioStatus, int volume, VideoInfoData i) {
        if (entry.getKey() == 0 || entry.getKey() == localUid) {
            boolean found = false;
            for (UserStatusData user : users) {
                if ((user.getmUid() == entry.getKey() && user.getmUid() == 0) || user.getmUid() == localUid) { // first time
                    user.setmUid(localUid);
                    if (status != null) {
//                        user.getmStatus() = status;
                        user.setmStatus(status);
                    }
                    if(audioStatus!=null){
//                        user.getmAudioStatus() = audioStatus;
                        user.setmAudioStatus(audioStatus);
                    }
//                    user.getmVolume() = volume;
                    user.setmVolume(volume);
                    user.setVideoInfo(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
//                Log.d("TAG", "searchUidsAndAppend: "+localUid+" status: "+status);
                users.add(0, new UserStatusData(localUid, entry.getValue(), status,audioStatus, volume, i));
            }
        } else {
            boolean found = false;
            for (UserStatusData user : users) {
                if (user.getmUid() == entry.getKey()) {
                    if (status != null) {
                        user.setmStatus(status);
                    }
                    if(audioStatus!=null){
                        user.setmAudioStatus(audioStatus);
                    }
                    user.setmVolume(volume);
                    user.setVideoInfo(i);
                    found = true;
                    break;
                }
            }
            if (!found) {
                users.add(new UserStatusData(entry.getKey(), entry.getValue(), status,audioStatus, volume, i));
            }
        }
    }

    public static void composeDataItem(final ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids,
                                       int localUid,
                                       HashMap<Integer, Integer> status,
                                       HashMap<Integer, Integer> audioStatus,
                                       HashMap<Integer, Integer> volume,
                                       HashMap<Integer, VideoInfoData> video) {
        composeDataItem(users, uids, localUid, status,audioStatus, volume, video, 0);
//        Log.d("TAG", "composeDataItem: "+localUid+" status: "+status.get(localUid));
    }

    public static void composeDataItem(final ArrayList<UserStatusData> users, HashMap<Integer, SurfaceView> uids,
                                       int localUid,
                                       HashMap<Integer, Integer> status,
                                       HashMap<Integer, Integer> audioStatusMap,
                                       HashMap<Integer, Integer> volume,
                                       HashMap<Integer, VideoInfoData> video, int uidExcepted) {
//        Log.d("TAG", "composeDataItem: "+localUid+" status: "+status.get(localUid)+ " uidExcepted: "+uidExcepted+" audiostatus: "+audioStatusMap.get(localUid));
        for (HashMap.Entry<Integer, SurfaceView> entry : uids.entrySet()) {
            int uid = entry.getKey();

//            if (uid == uidExcepted && uidExcepted != 0) {
//                continue;
//            }

            boolean local = uid == 0 || uid == localUid;

            Integer s = null;
            if (status != null) {
                s = status.get(uid);
                if (local && s == null) { // check again
                    s = status.get(uid == 0 ? localUid : 0);
                }
            }
            Integer audioStatus = null;
            if (audioStatusMap != null) {
                audioStatus = audioStatusMap.get(uid);
                if (audioStatus == null) { // check again
                    audioStatus = UserStatusData.DEFAULT_STATUS;
                }
            }

            Integer v = null;
            if (volume != null) {
                v = volume.get(uid);
                if (local && v == null) { // check again
                    v = volume.get(uid == 0 ? localUid : 0);
                }
            }
            if (v == null) {
//                v = UserStatusData.DEFAULT_VOLUME;
                v = UserStatusData.getDefaultVolume();
            }
            VideoInfoData i;
            if (video != null) {
                i = video.get(uid);
                if (local && i == null) { // check again
                    i = video.get(uid == 0 ? localUid : 0);
                }
            } else {
                i = null;
            }
            if (DEBUG) {
                log.debug("composeDataItem " + users + " " + entry + " " + (localUid & 0XFFFFFFFFL) + " " + s + " " + v + " " + i + " " + local + " " + (uid & 0XFFFFFFFFL) + " " + (uidExcepted & 0XFFFFFFFFL));
            }
            searchUidsAndAppend(users, entry, localUid, s,audioStatus, v, i);
        }

        removeNotExisted(users, uids, localUid);
    }
    public static void renderExtraData(Context context, UserStatusData user, VideoUserStatusHolder myHolder) {
        renderExtraData(context,user,myHolder,-1,false);
    }
    public static void renderExtraData(Context context, UserStatusData user, VideoUserStatusHolder myHolder,boolean fromBigGrid) {
        renderExtraData(context,user,myHolder,-1,fromBigGrid);
    }
    public static void renderExtraData(Context context, UserStatusData user, VideoUserStatusHolder myHolder,int uidExcepted,boolean fromBigGrid) {
        if (DEBUG) {
            log.debug("renderExtraData " + user + " " + myHolder);
        }
//        Log.d("TAG", "renderExtraData name" + user.mName + " status" + user.getmStatus() );

        if (user.getmStatus() != null) {
            if ((user.getmStatus() & UserStatusData.VIDEO_MUTED) != 0) {

//                if(!TextUtils.isEmpty(user.mProfilePic)) {
                if(!TextUtils.isEmpty(user.getmProfilePic())) {
                    myHolder.mAvtarImageText.setVisibility(View.GONE);
                    if(fromBigGrid){
                        myHolder.mAvatar.setVisibility(View.GONE);
                        myHolder.mCircularAvtar.setVisibility(View.VISIBLE);
//                        Glide.with(context).load(user.mProfilePic).into(myHolder.mCircularAvtar);
                        Glide.with(context).load(user.getmProfilePic()).into(myHolder.mCircularAvtar);
                    }else {
                        myHolder.mAvatar.setVisibility(View.VISIBLE);
                        myHolder.mCircularAvtar.setVisibility(View.GONE);
//                        Glide.with(context).load(user.mProfilePic).into(myHolder.mAvatar);
                        Glide.with(context).load(user.getmProfilePic()).into(myHolder.mAvatar);
                    }
                }else{
                    myHolder.mAvtarImageText.setVisibility(View.VISIBLE);
                    myHolder.mAvatar.setVisibility(View.GONE);
                    myHolder.mCircularAvtar.setVisibility(View.GONE);
                    myHolder.mAvtarImageText.setText(UtilityMethods.getNameText(user.getmName()));
                    if(fromBigGrid){
                        myHolder.mAvtarImageText.setBackgroundResource(R.drawable.text_image_drawable_white);
                    }else{
                        myHolder.mAvtarImageText.setBackgroundColor(ContextCompat.getColor(context, R.color.gray_500));
                    }
                }
                myHolder.mMaskView.setBackgroundResource(R.drawable.login_bg_gradient);
            } else {
                myHolder.mAvatar.setVisibility(View.GONE);
                myHolder.mCircularAvtar.setVisibility(View.GONE);
                myHolder.mAvtarImageText.setVisibility(View.GONE);
                myHolder.mMaskView.setBackgroundColor(Color.TRANSPARENT);
            }
            myHolder.mAvtarName.setText(getTrimmedText(user.getmName(),12));

            if (user.getmAudioStatus()!=null && user.getmAudioStatus()!= 0) {
//                Log.i("TAG","audiosStatus: "+user.getmAudioStatus()+" :::::::UserStatusData.AUDIO_MUTED: "+UserStatusData.AUDIO_MUTED);
                myHolder.mIndicator.setImageResource(R.drawable.icon_muted);
                myHolder.mIndicator.setVisibility(View.VISIBLE);
                myHolder.mIndicator.setTag(System.currentTimeMillis());
                //Log.d("Volume","is muted");
                return;
            } else {
//                Log.i("TAG","audiosStatus: "+user.getmAudioStatus()+" :::::::UserStatusData.AUDIO_MUTED: "+UserStatusData.AUDIO_MUTED);
                myHolder.mIndicator.setTag(null);
                myHolder.mIndicator.setVisibility(View.INVISIBLE);
            }
        }
        //set border frame
        if(uidExcepted==user.getmUid() && uidExcepted != -1){
//            Log.d("TAG", "renderExtraData border name" + user.mName + " uidExcepted " + uidExcepted+" UID: " +user.getmUid());
            myHolder.mMaskViewContainer.setBackgroundResource(R.drawable.boarder_bg_white);
        }else {
//            Log.d("TAG", "renderExtraData no border name" + user.mName + " uidExcepted " + uidExcepted+" UID: " +user.getmUid());
            myHolder.mMaskViewContainer.setBackgroundColor(Color.TRANSPARENT);
        }
        if(fromBigGrid){
            myHolder.mAvtarName.setVisibility(View.GONE);
        }else{
            myHolder.mAvtarName.setVisibility(View.VISIBLE);
        }
        //Log.d("Volume","user.getmStatus()=" + user.getmStatus());
        //Log.d("Volume","UserStatusData.AUDIO_MUTED" + UserStatusData.AUDIO_MUTED);


        Object tag = myHolder.mIndicator.getTag();
        if (tag != null && System.currentTimeMillis() - (Long) tag < 1500) { // workaround for audio volume comes just later than mute
            return;
        }

        int volume = user.getmVolume();
        //Log.d("Volume","volume=" + volume + "; uid=" + user.getmUid());
        //Log.d("user data:","user data:" + user.toString());

//        if (volume > 0) {
//            myHolder.mIndicator.setImageResource(R.drawable.icon_speaker);
//            myHolder.mIndicator.setVisibility(View.VISIBLE);
//        } else {
//            myHolder.mIndicator.setVisibility(View.INVISIBLE);
//        }

        if (Constant.SHOW_VIDEO_INFO && user.getVideoInfoData() != null) {
            VideoInfoData videoInfo = user.getVideoInfoData();
            myHolder.mMetaData.setText(ViewUtil.composeVideoInfoString(context, videoInfo));
//            if(videoInfo.mWidth==0 && videoInfo.mHeight==0) {
//                Log.d("Volume", "Video Muted by " + "uid=" + user.getmUid());
//                myHolder.mAvatar.setVisibility(View.VISIBLE);
//                myHolder.mMaskView.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
//            } else {
//                myHolder.mAvatar.setVisibility(View.GONE);
//                myHolder.mMaskView.setBackgroundColor(Color.TRANSPARENT);
//            }

            myHolder.mVideoInfo.setVisibility(View.VISIBLE);
        } else {
            myHolder.mVideoInfo.setVisibility(View.GONE);
            //Log.d("Volume","Video gone=" + "; uid=" + user.getmUid());

        }
    }

    public static String getTrimmedText(String str,int length){
        if(TextUtils.isEmpty(str)){
            return "";
        }
        if(str.length()<(length+3)){
            return str;
        }else{
            return str.substring(0,length)+"...";
        }
    }

    public static void stripView(SurfaceView view) {
        ViewParent parent = view.getParent();
        if (parent != null) {
            ((FrameLayout) parent).removeView(view);
        }
    }
}
