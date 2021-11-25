package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mvp.omnicure.R;
import com.mvp.omnicure.apiRetrofit.ApiClient;
import com.mvp.omnicure.apiRetrofit.RequestBodys.SendMsgNotifyRequestBody;
import com.mvp.omnicure.utils.CustomSnackBar;
import com.mvp.omnicure.utils.ErrorMessages;
import com.mvp.omnicure.utils.PrefUtility;
import com.mvp.omnicure.utils.UtilityMethods;
import com.mvp.omnicure.videocall.openvcall.model.AGEventHandler;
import com.mvp.omnicure.videocall.openvcall.model.ConstantApp;
import com.mvp.omnicure.videocall.openvcall.model.DuringCallEventHandler;
import com.mvp.omnicure.videocall.openvcall.model.Message;
import com.mvp.omnicure.videocall.openvcall.model.User;
import com.mvp.omnicure.videocall.openvcall.ui.layout.GridVideoViewContainer;
import com.mvp.omnicure.videocall.openvcall.ui.layout.InChannelMessageListAdapter;
import com.mvp.omnicure.videocall.openvcall.ui.layout.SmallVideoViewAdapter;
import com.mvp.omnicure.videocall.openvcall.ui.layout.SmallVideoViewDecoration;
import com.mvp.omnicure.videocall.propeller.Constant;
import com.mvp.omnicure.videocall.propeller.UserStatusData;
import com.mvp.omnicure.videocall.propeller.VideoInfoData;
import com.mvp.omnicure.videocall.propeller.ui.RecyclerItemClickListener;
import com.mvp.omnicure.videocall.propeller.ui.RtlLinearLayoutManager;
import com.mvp.omnicure.viewmodel.CallActivityViewModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;

import io.agora.rtc.Constants;
import io.agora.rtc.IRtcEngineEventHandler;
import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.VideoCanvas;
import io.agora.rtc.video.VideoEncoderConfiguration;
import omnicure.mvp.com.providerEndpoints.model.CommonResponse;
import omnicure.mvp.com.providerEndpoints.model.Provider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.support.v7.app.ActionBar;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;

public class CallActivity extends BaseActivity implements DuringCallEventHandler {

    public static final int LAYOUT_TYPE_DEFAULT = 0;
    //    public static final int LAYOUT_TYPE_DEFAULT = 1;
    public static final int LAYOUT_TYPE_SMALL = 1;
    private static final String TAG = "CallActivity";
    private final static Logger log = LoggerFactory.getLogger(CallActivity.class);
    private static final int CALL_OPTIONS_REQUEST = 3222;
    // should only be modified under UI thread
    private final HashMap<Integer, SurfaceView> mUidsList = new HashMap<>(); // uid = 0 || uid == EngineConfig.getmUid()
    private final Handler mUIHandler = new Handler();
    private int mLayoutType = LAYOUT_TYPE_SMALL;//LAYOUT_TYPE_DEFAULT;
    BroadcastReceiver mBroadcastReceiver;
    MediaPlayer player = null;
    com.mvp.omnicure.customview.CircularImageView connecting_profile_pic = null;
    TextView connecting_image_text = null;
    Handler mHandlerCall = null;
    Runnable mNoAnswerRunnable = null;
    ArrayList<Provider> mProviderList = new ArrayList<>();
    boolean sosCall = false;
    private GridVideoViewContainer mGridVideoViewContainer;
    private RelativeLayout mSmallVideoViewDock;
    private volatile boolean mVideoMuted = false;
    private volatile boolean mAudioMuted = false;
    private volatile boolean mMixingAudio = false;
    private volatile int mAudioRouting = Constants.AUDIO_ROUTE_DEFAULT;
    private volatile boolean mFullScreen = false;
    private boolean mIsLandscape = false;
    private boolean mIsAudioCall = true;
    private InChannelMessageListAdapter mMsgAdapter;
    private ArrayList<Message> mMsgList;
    private SmallVideoViewAdapter mSmallVideoViewAdapter;
    private CallActivityViewModel callViewModel;
    private Chronometer chronometer;
    private boolean isCallDurationStarted = false;
    private Timer timer;
    private int seconds = 0, minutes = 0, hour = 0;

//    @Override
//    public boolean onCreateOptionsMenu(final Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.menu_call, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle presses on the action bar items
//        switch (item.getItemId()) {
//            case R.id.action_options:
//                showCallOptions();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
    private String auditId = "";
    private boolean outgoing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeActivityContentShownUnderStatusBar();
        setContentView(R.layout.activity_call);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        callViewModel = ViewModelProviders.of(this).get(CallActivityViewModel.class);

        registerCallerBusyBroadCast();
        showOrHideStatusBar(true);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ab.setCustomView(R.layout.ard_agora_actionbar_with_title);
        }
        TextView textView = (TextView) findViewById(R.id.connection_message);
        connecting_profile_pic = (com.mvp.omnicure.customview.CircularImageView) findViewById(R.id.connecting_profile_pic);
        connecting_image_text = (TextView) findViewById(R.id.connecting_image_text);
        chronometer = (Chronometer) findViewById(R.id.txt_call_duration);

        if (getIntent().hasExtra("sos")) {
            textView.setText(R.string.finding_a_doctor);
            sosCall = true;
        } else {
            textView.setText(R.string.connecting);
        }
        boolean notAnswerWatchDog = false;
        String receiverName = getIntent().getStringExtra("providerName");
        String providerHospitalName = getIntent().getStringExtra("providerHospitalName");
        String profilePicUrl = getIntent().getStringExtra("profilePicUrl");
        auditId = getIntent().getStringExtra(com.mvp.omnicure.utils.Constants.IntentKeyConstants.AUDIT_ID);
        auditId = auditId==null?"":auditId ;

        if (getIntent().hasExtra("providerList")) {
            Gson gson = new Gson();
            String providerListStr = getIntent().getStringExtra("providerList");
            try {
                JSONArray providerArray = new JSONArray(providerListStr);
                for (int i = 0; i < providerArray.length(); i++) {
                    JSONObject providerObj = (JSONObject) providerArray.get(i);
                    Provider provider = new Provider();
                    provider.setId(providerObj.getLong("id"));
                    provider.setName(providerObj.getString("name"));
                    if (providerObj.has("profilePicUrl")) {
                        provider.setProfilePicUrl(providerObj.getString("profilePicUrl"));
                    }
                    if (providerObj.has("hospital")) {
                        provider.setHospital(providerObj.getString("hospital"));
                    }
                    if(providerObj.has("role")) {
                        provider.setRole(providerObj.getString("role"));
                    }
                    mProviderList.add(provider);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception:", e.getCause());
            }
            System.out.println("providerlistval "+mProviderList);
            if (mProviderList != null) {
                if (mProviderList.size() == 2 && !sosCall) {
                    notAnswerWatchDog = true;
                    if (TextUtils.isEmpty(receiverName)) {
                        Long selfId = PrefUtility.getProviderId(this);
                        for (Provider provider : mProviderList) {
                            if (selfId.longValue() != provider.getId().longValue()) {
                                receiverName = provider.getName();
                                providerHospitalName = provider.getHospital();
                                profilePicUrl = provider.getProfilePicUrl();
                                break;
                            }
                        }
                    }
                    TextView connectingProfileName = (TextView) CallActivity.this.findViewById(R.id.connecting_profile_name);
                    connectingProfileName.setText(receiverName);
                    TextView connectingProfileHospital = (TextView) CallActivity.this.findViewById(R.id.connecting_hospital_name);
                    connectingProfileHospital.setText(providerHospitalName);
                    connecting_profile_pic.setVisibility(View.GONE);
                    connecting_image_text.setVisibility(View.VISIBLE);
                    connecting_image_text.setText(UtilityMethods.getNameText(receiverName));
                    if (!TextUtils.isEmpty(profilePicUrl)) {
                        connecting_profile_pic.setVisibility(View.VISIBLE);
                        connecting_image_text.setVisibility(View.GONE);
                        Glide.with(this)
                                .load(profilePicUrl)
                                .into(connecting_profile_pic);
                    }

                } else {
                    //group call
                    TextView connectingProfileName = (TextView) CallActivity.this.findViewById(R.id.connecting_profile_name);
                    if (sosCall) {
                        connectingProfileName.setText(R.string.sos_call);
                    } else {
                        connectingProfileName.setText(R.string.group_call);
                    }
                    TextView connectingProfileHospital = (TextView) CallActivity.this.findViewById(R.id.connecting_hospital_name);
                    connectingProfileHospital.setText("");
                    Drawable drawable = null;
                    if (sosCall) {
                        drawable = getResources().getDrawable(R.drawable.ic_sos_big_red);
                    } else {
                        drawable = getResources().getDrawable(R.drawable.ic_conference);
                    }
                    try {
                        connecting_profile_pic.setVisibility(View.VISIBLE);
                        connecting_image_text.setVisibility(View.GONE);
                        connecting_profile_pic.setBorderWidth(5);
                        Bitmap bitmap;

                        bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicWidth(), Bitmap.Config.ARGB_8888);

                        Canvas canvas = new Canvas(bitmap);
                        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                        drawable.draw(canvas);

                        connecting_profile_pic.setImageBitmap(bitmap);

                        connecting_profile_pic.setFillColor(Color.WHITE);
                    } catch (OutOfMemoryError e) {
                        // Handle the error
                        return;
                    }

                }
            }
        } else {
            // handle directory call
            notAnswerWatchDog = true;
            TextView connectingProfileName = (TextView) CallActivity.this.findViewById(R.id.connecting_profile_name);
            connectingProfileName.setText(receiverName);
            TextView connectingProfileHospital = (TextView) CallActivity.this.findViewById(R.id.connecting_hospital_name);
            connectingProfileHospital.setText(providerHospitalName);
            if (getIntent().hasExtra("profilePicUrl") && !TextUtils.isEmpty(getIntent().getStringExtra("profilePicUrl"))) {
                connecting_profile_pic.setVisibility(View.VISIBLE);
                connecting_image_text.setVisibility(View.GONE);
                Glide.with(this)
                        .load(getIntent().getStringExtra("profilePicUrl"))
                        .into(connecting_profile_pic);
            } else {
                connecting_profile_pic.setVisibility(View.GONE);
                connecting_image_text.setVisibility(View.VISIBLE);
                connecting_image_text.setText(UtilityMethods.getNameText(receiverName));
            }
        }

        if (getIntent().hasExtra("callType") && getIntent().getStringExtra("callType").equalsIgnoreCase("outgoing")) {
            outgoing = true;
            AssetFileDescriptor afd = null;
            try {
                afd = getAssets().openFd("outgoing.mp3");
                if (player == null) {
                    player = new MediaPlayer();
                }
                player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                player.prepare();
                player.setLooping(true);
                player.start();
            } catch (Exception e) {
                Log.e(TAG, "Exception:", e.getCause());
            }finally {
                if (afd != null) {
                    try {
                        afd.close();
                    } catch (IOException e) {
                        Log.e("error", e.getMessage());
                    }
                }
            }
//            if(notAnswerWatchDog){
            mNoAnswerRunnable = new Runnable() {
                @Override
                public void run() {
                    TextView connectingError = (TextView) findViewById(R.id.connection_error);
                    if (sosCall) {
                        connectingError.setText(getString(R.string.did_not_find_doctor));
                    } else {
                        connectingError.setText(getIntent().getStringExtra("providerName") + " " + getString(R.string.did_not_answer));
                    }
                    AssetFileDescriptor afd = null;
                    try {
                        if (player != null) {
                            player.stop();
                            player.stop();
                            player.reset();
                            player = null;
                        }
                        afd = getAssets().openFd("busytone.mp3");
                        if (player == null) {
                            player = new MediaPlayer();
                        }
                        player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        player.prepare();
                        player.setLooping(false);
                        player.start();
                    } catch (Exception e) {
                        Log.e(TAG, "Exception:", e.getCause());
                    }finally {
                        if (afd != null) {
                            try {
                                afd.close();
                            } catch (IOException e) {
                                Log.e("error", e.getMessage());
                            }
                        }
                    }
                    mHandlerCall.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 2000);

                }
            };
            mHandlerCall = new Handler();
            mHandlerCall.postDelayed(mNoAnswerRunnable, 60000);
//            }
        }

    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.stop();
            player.stop();
            player.reset();
            player = null;
        }
        if (mHandlerCall != null && mNoAnswerRunnable != null) {
            mHandlerCall.removeCallbacks(mNoAnswerRunnable);
        }
        unregisterCallerBusyBroadCast();
        super.onDestroy();
    }

    @Override
    protected void initUIandEvent() {

        getSupportActionBar().hide();
        addEventHandler(this);
        String channelName = getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            TextView channelNameView = ((TextView) findViewById(R.id.ovc_page_title));
//            channelNameView.setText(channelName);
        }

        // programmatically layout ui below of status bar/action bar
        LinearLayout eopsContainer = findViewById(R.id.extra_ops_container);
        RelativeLayout.MarginLayoutParams eofmp = (RelativeLayout.MarginLayoutParams) eopsContainer.getLayoutParams();
        eofmp.topMargin = getStatusBarHeight() + getActionBarHeight() + getResources().getDimensionPixelOffset(R.dimen.activity_vertical_margin) / 2; // status bar + action bar + divider

        final String encryptionKey = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY);
        final String encryptionMode = getIntent().getStringExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE);

        doConfigEngine(encryptionKey, encryptionMode);

        mGridVideoViewContainer = (GridVideoViewContainer) findViewById(R.id.grid_video_view_container);
        mGridVideoViewContainer.setItemEventHandler(new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                onBigVideoViewClicked(view, position);
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }

            @Override
            public void onItemDoubleClick(View view, int position) {
//                onBigVideoViewDoubleClicked(view, position);
            }
        });

        if (getIntent().hasExtra("call") && getIntent().getStringExtra("call").equalsIgnoreCase(com.mvp.omnicure.utils.Constants.FCMMessageType.AUDIO_CALL)) {
            mVideoMuted = true;
        } else {
            mVideoMuted = false;

        }
        ImageView videoMuteView = (ImageView) findViewById(R.id.btn_camera);
        rtcEngine().enableLocalVideo(!mVideoMuted);//rtcEngine.enableVideo();
        //handelVideoIcon(videoMuteView,false);
        videoMuteView.setImageResource(mVideoMuted ? R.drawable.ic_video_mute : R.drawable.ic_video);
        videoMuteView.setBackground(mVideoMuted ? getResources().getDrawable(R.drawable.ic_call_icon_bg) : getResources().getDrawable(R.drawable.transparent_bg));

        SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
        preview(true, surfaceV, 0);
        surfaceV.setZOrderOnTop(false);
        surfaceV.setZOrderMediaOverlay(false);
        Long selfId = PrefUtility.getProviderId(CallActivity.this);
        System.out.println("selfid "+selfId);
        if (getProviderFromList(selfId.intValue()) == null) {
            Provider tmpProvider = new Provider();
            tmpProvider.setId(selfId);
            String name = PrefUtility.getStringInPref(this, com.mvp.omnicure.utils.Constants.SharedPrefConstants.NAME, "");
            tmpProvider.setName(name);
            String profilePic = PrefUtility.getStringInPref(this, com.mvp.omnicure.utils.Constants.SharedPrefConstants.PROFILE_IMG_URL, "");
            tmpProvider.setProfilePicUrl(profilePic);
            mProviderList.add(tmpProvider);
        }
        mUidsList.put(selfId.intValue(), surfaceV); // get first surface view

//        if(getIntent().hasExtra("call") && getIntent().getStringExtra("call").equalsIgnoreCase(com.mvp.omnicure.utils.Constants.FCMMessageType.AUDIO_CALL)) {
//
//        }else{
        mGridVideoViewContainer.initViewContainer(this, selfId.intValue(), mVideoMuted ? 1 : 0, mAudioMuted ? 1 : 0, mUidsList, mIsLandscape, mProviderList); // first is now full view
//        }


        initMessageList();
        notifyMessageChanged(new Message(new User(selfId.intValue(), null), "start join " + channelName + " as " + (config().getmUid() & 0xFFFFFFFFL)));


        joinChannel(channelName, PrefUtility.getProviderId(this).intValue());
//        hideLocalView(mVideoMuted);

        rtcEngine().addHandler(new IRtcEngineEventHandler() {
            @Override
            public void onUserJoined(int uid, int elapsed) {
                super.onUserJoined(uid, elapsed);

            }
        });

        optional();

    }


    @Override
    public void onRemoteAudioStateChanged(int uid, int state, int reason, int elapsed) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateAudioMuteView(uid, state == 0);
            }
        });
    }

    @Override
    public void onRemoteVideoStateChanged(int uid, int state, int reason, int elapsed) {
        if (state == 0 && reason == 5) {
            //remote video muted
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doHideTargetView(uid, true);

                }
            });
        } else if (state == 2 && reason == 6) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doHideTargetView(uid, false);
                }
            });
        }
    }

    private void onBigVideoViewClicked(View view, int position) {
        log.debug("onItemClick " + view + " " + position + " " + mLayoutType);
        toggleFullscreen();
    }

    private void onBigVideoViewDoubleClicked(View view, int position) {
        log.debug("onItemDoubleClick " + view + " " + position + " " + mLayoutType);

        if (mUidsList.size() < 2) {
            return;
        }

        UserStatusData user = mGridVideoViewContainer.getItem(position);
        int uid = (user.getmUid() == 0) ? config().getmUid() : user.getmUid();
        Log.d("TAG", "user.getmUid()= " + user.getmUid() + "; config().getmUid()=" + config().getmUid());
        if (mLayoutType == LAYOUT_TYPE_DEFAULT && mUidsList.size() != 1) {
            switchToSmallVideoView(uid, -1);
        } else {
            switchToDefaultVideoView();
        }
    }

    private void onSmallVideoViewDoubleClicked(View view, int position) {
        log.debug("onItemDoubleClick small " + view + " " + position + " " + mLayoutType);

        switchToDefaultVideoView();
    }

    private void makeActivityContentShownUnderStatusBar() {
        // https://developer.android.com/training/system-ui/status
        // May fail on some kinds of devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;

            decorView.setSystemUiVisibility(uiOptions);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(getResources().getColor(R.color.agora_blue));
            }
        }
    }

    private void showOrHideStatusBar(boolean hide) {
        // May fail on some kinds of devices
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            View decorView = getWindow().getDecorView();
            int uiOptions = decorView.getSystemUiVisibility();

//            if (hide) {
//                uiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
//            } else {
//                uiOptions ^= View.SYSTEM_UI_FLAG_FULLSCREEN;
//            }

            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void toggleFullscreen() {
//        mFullScreen = !mFullScreen;
        Log.d(TAG, "toggleFullscreen: actual value "+mFullScreen);
        togglemFullScreen();
        Log.d(TAG, "toggleFullscreen: after toggle value "+mFullScreen);

        showOrHideCtrlViews(mFullScreen);

        mUIHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showOrHideStatusBar(true);
            }
        }, 200); // action bar fade duration
    }
    public synchronized void togglemFullScreen() {
        mFullScreen = !mFullScreen;
    }
    private void showOrHideCtrlViews(boolean hide) {
//        ActionBar ab = getSupportActionBar();
//        if (ab != null) {
//            if (hide) {
//                ab.hide();
//            } else {
//                ab.show();
//            }
//        }

        findViewById(R.id.extra_ops_container).setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
        findViewById(R.id.bottom_action_container).setVisibility(hide ? View.INVISIBLE : View.VISIBLE);
//        findViewById(R.id.msg_list).setVisibility(hide ? View.INVISIBLE : (Constant.DEBUG_INFO_ENABLED ? View.VISIBLE : View.INVISIBLE));
        findViewById(R.id.msg_list).setVisibility(hide ? View.INVISIBLE : (Constant.isDebugInfoEnabled() ? View.VISIBLE : View.INVISIBLE));
    }

    private void relayoutForVirtualKeyPad(int orientation) {
        int virtualKeyHeight = virtualKeyHeight();

        LinearLayout eopsContainer = findViewById(R.id.extra_ops_container);
        FrameLayout.MarginLayoutParams eofmp = (FrameLayout.MarginLayoutParams) eopsContainer.getLayoutParams();

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            eofmp.rightMargin = virtualKeyHeight;
            eofmp.leftMargin = 0;
        } else {
            eofmp.leftMargin = 0;
            eofmp.rightMargin = 0;
        }

        LinearLayout bottomContainer = findViewById(R.id.bottom_container);
        FrameLayout.MarginLayoutParams fmp = (FrameLayout.MarginLayoutParams) bottomContainer.getLayoutParams();

        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            fmp.bottomMargin = 0;
            fmp.rightMargin = virtualKeyHeight;
            fmp.leftMargin = 0;
        } else {
            fmp.bottomMargin = virtualKeyHeight;
            fmp.leftMargin = 0;
            fmp.rightMargin = 0;
        }
    }

    public synchronized void showCallOptions() {
        Intent i = new Intent(this, CallOptionsActivity.class);
        startActivityForResult(i, CALL_OPTIONS_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CALL_OPTIONS_REQUEST) {
            RecyclerView msgListView = (RecyclerView) findViewById(R.id.msg_list);
            msgListView.setVisibility(Constant.isDebugInfoEnabled() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    public void onClickHideIME(View view) {
        log.debug("onClickHideIME " + view);

        closeIME(findViewById(R.id.msg_content));
        findViewById(R.id.msg_input_container).setVisibility(View.GONE);
        findViewById(R.id.bottom_action_container).setVisibility(View.VISIBLE);
    }

    private void initMessageList() {
        mMsgList = new ArrayList<>();
        RecyclerView msgListView = (RecyclerView) findViewById(R.id.msg_list);
        //msgListView.setVisibility(View.GONE);
        mMsgAdapter = new InChannelMessageListAdapter(this, mMsgList);
        mMsgAdapter.setHasStableIds(true);
        //msgListView.setAdapter(mMsgAdapter);
        //msgListView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false));
        //msgListView.addItemDecoration(new MessageListDecoration());
    }

    private void notifyMessageChanged(Message msg) {
        mMsgList.add(msg);
        System.out.println("messageValue "+msg.toString());
        System.out.println("messageList "+mMsgList.size());
        int MAX_MESSAGE_COUNT = 16;
        if (mMsgList.size() > MAX_MESSAGE_COUNT) {
            int toRemove = mMsgList.size() - MAX_MESSAGE_COUNT;
            for (int i = 0; i < toRemove; i++) {
                mMsgList.remove(i);
            }
        }

        mMsgAdapter.notifyDataSetChanged();
    }

    private void optional() {
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);
    }

    private void optionalDestroy() {
    }

    private int getVideoEncResolutionIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int videoEncResolutionIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX);
//        if (videoEncResolutionIndex > ConstantApp.VIDEO_DIMENSIONS.length - 1) {
        if (videoEncResolutionIndex > ConstantApp.getVideoDimensions().length - 1) {
            videoEncResolutionIndex = ConstantApp.DEFAULT_VIDEO_ENC_RESOLUTION_IDX;

            // save the new value
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION, videoEncResolutionIndex);
            editor.apply();
        }
        return videoEncResolutionIndex;
    }

    private int getVideoEncFpsIndex() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int videoEncFpsIndex = pref.getInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX);
//        if (videoEncFpsIndex > ConstantApp.VIDEO_FPS.length - 1) {
        if (videoEncFpsIndex > ConstantApp.getVideoFps().length - 1) {
            videoEncFpsIndex = ConstantApp.DEFAULT_VIDEO_ENC_FPS_IDX;

            // save the new value
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt(ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_FPS, videoEncFpsIndex);
            editor.apply();
        }
        return videoEncFpsIndex;
    }

    private void doConfigEngine(String encryptionKey, String encryptionMode) {
        /*VideoEncoderConfiguration.VideoDimensions videoDimension = ConstantApp.VIDEO_DIMENSIONS[getVideoEncResolutionIndex()];
        VideoEncoderConfiguration.FRAME_RATE videoFps = ConstantApp.VIDEO_FPS[getVideoEncFpsIndex()];*/
        VideoEncoderConfiguration.VideoDimensions videoDimension = ConstantApp.getVideoDimensions()[getVideoEncResolutionIndex()];
        VideoEncoderConfiguration.FRAME_RATE videoFps = ConstantApp.getVideoFps()[getVideoEncFpsIndex()];
        configEngine(videoDimension, videoFps, encryptionKey, encryptionMode);
    }

    public void onSwitchCameraClicked(View view) {
        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.switchCamera();
        ImageView iv = (ImageView) findViewById(R.id.switch_camera_id);
        if (iv.getTag() == null || ((String) iv.getTag()).equalsIgnoreCase("front")) {
            iv.setBackground(getResources().getDrawable(R.drawable.ic_call_icon_bg));
            iv.setTag("back");
        } else {
            iv.setBackground(getResources().getDrawable(R.drawable.transparent_bg));
            iv.setTag("front");
        }
    }

    public void onSwitchSpeakerClicked(View view) {
        if (mAudioRouting == Constants.AUDIO_ROUTE_DEFAULT) {
            mAudioRouting = Constants.AUDIO_ROUTE_SPEAKERPHONE;
        }
        RtcEngine rtcEngine = rtcEngine();
        rtcEngine.setEnableSpeakerphone(mAudioRouting != Constants.AUDIO_ROUTE_SPEAKERPHONE);

    }

    public void onFilterClicked(View view) {
//        Constant.BEAUTY_EFFECT_ENABLED = !Constant.BEAUTY_EFFECT_ENABLED;
        Constant.setBeautyEffectEnabled(!Constant.isBeautyEffectEnabled());
        if (Constant.isBeautyEffectEnabled()) {
            setBeautyEffectParameters(Constant.BEAUTY_EFFECT_DEFAULT_LIGHTNESS, Constant.BEAUTY_EFFECT_DEFAULT_SMOOTHNESS, Constant.BEAUTY_EFFECT_DEFAULT_REDNESS);
            enablePreProcessor();
        } else {
            disablePreProcessor();
        }

        ImageView iv = (ImageView) view;

//        iv.setImageResource(Constant.BEAUTY_EFFECT_ENABLED ? R.drawable.btn_filter : R.drawable.btn_filter_off);
        iv.setImageResource(Constant.isBeautyEffectEnabled() ? R.drawable.btn_filter : R.drawable.btn_filter_off);
    }

    @Override
    protected void deInitUIandEvent() {
        optionalDestroy();
        doLeaveChannel();
        removeEventHandler(this);
        mUidsList.clear();
    }

    private void doLeaveChannel() {
        leaveChannel(config().getmChannel());
        preview(false, null, 0);
    }

    public void onHangupClicked(View view) {
        log.info("onHangupClicked " + view + " " + mUidsList.size());

        chronometer.stop();
        if (getIntent().hasExtra("sos")) {
            sosDismiss(false);
        } else {
            sendCallResponseMessage(com.mvp.omnicure.utils.Constants.FCMMessageType.CALLER_REJECT);
        }
    }

    private void sendCallResponseMessage(String messageType) {
        showProgressBar(getString(R.string.disconnecting));

        /*String token = PrefUtility.getStringInPref(context, com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN, "");

        Call<CommonResponse> call = ApiClient.getApiProviderEndpoints(false, false).sendCallResponseMessage(PrefUtility.getProviderId(context), token, receiverId, channelName, messageType);
        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse> call, Response<CommonResponse> response) {

                if (response.isSuccessful()) {

                }
            }

            @Override
            public void onFailure(Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse> call, Throwable t) {

            }
        });*/

        String errMsg[] = new String[1];
        String token = PrefUtility.getStringInPref(CallActivity.this, com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN, "");

        SendMsgNotifyRequestBody requestBody = new SendMsgNotifyRequestBody();
        requestBody.setProviderId(PrefUtility.getProviderId(CallActivity.this));//id key
        requestBody.setToken(token);
        requestBody.setReceiverId(getIntent().getLongExtra("providerId", 0l));
        requestBody.setMessage(getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME));
        requestBody.setType(messageType);
        if (getIntent().hasExtra("patientId")) {
            requestBody.setPatientId(getIntent().getLongExtra("patientId", 0l));
        }

        ApiClient.getApiProviderEndpoints(true,true).sendMessageNotification(requestBody).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Log.e(TAG, "onResponse: sendMessageNotification "+ response.toString());
                System.out.println("callrespon_se "+response.body());
                if (response.body() != null) {
                    Log.e("response", "response for busy notification : "+ response.body().getStatus());
                }
//                Log.i("response", "response for busy notification : " + response.body().getStatus());
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        sendAuditId(false, messageType);
                    }
                });
            }

            @Override
            public void onFailure(Call<omnicure.mvp.com.providerEndpoints.model.CommonResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException)
                    errMsg[0] = com.mvp.omnicure.utils.Constants.APIErrorType.SocketTimeoutException.toString();
                else if (t instanceof Exception)
                    errMsg[0] = com.mvp.omnicure.utils.Constants.API_ERROR.toString();

                finish();
            }
        });

        /*new Thread(new Runnable() {
            String errMsg = "";

            @Override
            public void run() {
                try {
                    String token = PrefUtility.getStringInPref(CallActivity.this, com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN, "");
                    final ProviderEndpoints.SendMessageNotification apiCall = EndPointBuilder.getProviderEndpoints()
                            .sendMessageNotification(PrefUtility.getProviderId(CallActivity.this),
                                    token,
                                    getIntent().getLongExtra("providerId", 0l),
                                    getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME),
                                    messageType,
                                    auditId);

                    if (getIntent().hasExtra("patientId")) {
                        apiCall.setPatientsId(getIntent().getLongExtra("patientId", 0l));
                    }
                    Log.i("CallActivity", "values for api " + PrefUtility.getProviderId(CallActivity.this) + " " + getIntent().getLongExtra("providerId", 0l));
                    CommonResponse commonResponse = apiCall.execute();

                    finish();
                    System.out.println("callrespon_se "+commonResponse);

                    Log.i("response", "response for busy notification : " + commonResponse.getStatus());
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        public void run() {
                            sendAuditId(false, messageType);
                        }
                    });
                } catch (SocketTimeoutException e) {
                    errMsg = com.mvp.omnicure.utils.Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = com.mvp.omnicure.utils.Constants.APIErrorType.Exception.toString();
                    Log.e(TAG, "Exception:", e.getCause());
                    errMsg = com.mvp.omnicure.utils.Constants.API_ERROR.toString();

                }

                finish();
            }
        }).start();*/
    }

    /*
         result = {RtcEngineImpl@14564}
         lastOrientationTs = 0
         mChannelProfile = 1
         mClientRole = 2
         mContext = {WeakReference@14579}
         mDefaultRtcChannel = {RtcChannelImpl@14580}
         mExAudioChannels = 0
         mExAudioSampleRate = 0
         mLocalVideoEnabled = false
         mNativeHandle = 482113016960
         mRtcChannels = {LinkedList@14581}  size = 0
         mRtcHandlers = {ConcurrentHashMap@14584}  size = 1
         mRtcStats = {IRtcEngineEventHandler$RtcStats@14587}
         mTotalRotation = 1000
         mUseLocalView = true
         mVideoSourceType = 1
         mWifiLock = null
         shadow$_klass_ = {Class@12399} "class io.agora.rtc.internal.RtcEngineImpl"
         shadow$_monitor_ = 0
         */

    public synchronized void togglemVideoMuted(){
        mVideoMuted = !mVideoMuted;
    }

    public void onVideoMuteClicked(View view) {
        /*if(mUidsList.size() == 1){
            return;
        }*/
        log.info("onVoiceChatClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size() == 0) {
            return;
        }

        SurfaceView surfaceV = getLocalView();
        ViewParent parent;
        if (surfaceV == null || (parent = surfaceV.getParent()) == null) {
            log.warn("onVoiceChatClicked " + view + " " + surfaceV);
            return;
        }

        RtcEngine rtcEngine = rtcEngine();
        togglemVideoMuted();
//        mVideoMuted = !mVideoMuted;

//        rtcEngine().stopPreview();
//        rtcEngine().muteLocalVideoStream(mVideoMuted);
//        preview(false,null,config().getmUid());

//
//        if (mVideoMuted) {
//            rtcEngine.disableVideo();
//        } else {
//            rtcEngine.enableVideo();
//        }

        if (mVideoMuted) {
            rtcEngine.enableLocalVideo(false);
        } else {
            rtcEngine.enableLocalVideo(true);//rtcEngine.enableVideo();
        }

        ImageView iv = (ImageView) view;

        //handelVideoIcon(iv,true);
        iv.setImageResource(mVideoMuted ? R.drawable.ic_video_mute : R.drawable.ic_video);
        iv.setBackground(mVideoMuted ? getResources().getDrawable(R.drawable.ic_call_icon_bg) : getResources().getDrawable(R.drawable.transparent_bg));

        hideLocalView(mVideoMuted);
    }

    private SurfaceView getLocalView() {
        for (HashMap.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
            if (entry.getKey() == 0 || entry.getKey() == config().getmUid()) {
                return entry.getValue();
            }
        }

        return null;
    }

    private void hideLocalView(boolean hide) {
        Log.d("TAG", "hideLocalView hide: " + hide);
        int uid = config().getmUid();
        doHideTargetView(uid, hide);
    }

    private void updateAudioMuteView(int targetUid, boolean audioMute) {
        Log.d("TAG", "updateAUdioMuteView targetUid:" + targetUid + " audioMute: " + audioMute);
        if (mSmallVideoViewAdapter == null) {
            return;
        }
        List<UserStatusData> userList = mSmallVideoViewAdapter.getUserList();
        HashMap<Integer, Integer> status = new HashMap<>();
        HashMap<Integer, Integer> audioStatus = new HashMap<>();
        if (userList != null) {
            for (UserStatusData userStatusData : userList) {
                if (userStatusData.getmUid() == targetUid) {
                    audioStatus.put(targetUid, audioMute ? UserStatusData.AUDIO_MUTED : UserStatusData.DEFAULT_STATUS);
                } else {
                    audioStatus.put(userStatusData.getmUid(), userStatusData.getmAudioStatus());
                }
                status.put(userStatusData.getmUid(), userStatusData.getmStatus());
            }
        }
        UserStatusData bigBgUser = mGridVideoViewContainer.getItem(0);
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(bigBgUser.getmUid(), mUidsList.get(bigBgUser.getmUid()));
        Iterator<SurfaceView> iterator = mUidsList.values().iterator();
        while (iterator.hasNext()) {
            SurfaceView s = iterator.next();
            if (s != null) {
                s.setZOrderOnTop(true);
                s.setZOrderMediaOverlay(true);
            }
        }
        if (slice.get(bigBgUser.getmUid()) != null) {
            slice.get(bigBgUser.getmUid()).setZOrderOnTop(false);
            slice.get(bigBgUser.getmUid()).setZOrderMediaOverlay(false);
        }
        mGridVideoViewContainer.notifyUiChanged(slice, bigBgUser.getmUid(), status, audioStatus, null);
        mSmallVideoViewAdapter.setLocalUid(config().getmUid());
        { // find target view in small video view list
            log.warn("SmallVideoViewAdapter call notifyUiChanged " + mUidsList + " " + (bigBgUser.getmUid() & 0xFFFFFFFFL) + " target: " + (targetUid & 0xFFFFFFFFL) + "==" + targetUid + " " + status + " " + mUidsList.size());

            if (mSmallVideoViewAdapter != null)
                mSmallVideoViewAdapter.notifyUiChanged(mUidsList, bigBgUser.getmUid(), status, audioStatus, null);
//            if (mUidsList.size() < 2) {
//                finish();
//            }
        }
//        }
    }


    private void doHideTargetView(int targetUid, boolean hide) {
        try {
            Log.d("TAG", "doHideTargetView targetUid:" + targetUid + " hide: " + hide);
            List<UserStatusData> userList = null;
            if (mSmallVideoViewAdapter != null) {
                userList = mSmallVideoViewAdapter.getUserList();
            }
            HashMap<Integer, Integer> status = new HashMap<>();
            HashMap<Integer, Integer> audioStatus = new HashMap<>();
            if (userList != null) {
                for (UserStatusData userStatusData : userList) {
                    if (userStatusData.getmUid() == targetUid) {
                        status.put(targetUid, hide ? UserStatusData.VIDEO_MUTED : UserStatusData.DEFAULT_STATUS);
                    } else {
                        status.put(userStatusData.getmUid(), userStatusData.getmStatus());
                    }
                    audioStatus.put(userStatusData.getmUid(), userStatusData.getmAudioStatus());
                }
            }
//        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
//            mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
//        } else if (mLayoutType == LAYOUT_TYPE_SMALL) {
            UserStatusData bigBgUser = mGridVideoViewContainer.getItem(0);
            if (bigBgUser.getmUid() == targetUid) { // big background is target view
                HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
                slice.put(targetUid, mUidsList.get(targetUid));
                mGridVideoViewContainer.notifyUiChanged(slice, targetUid, status, audioStatus, null);
//                mGridVideoViewContainer.notifyUiChanged(mUidsList, targetUid, status, null);
            } else {
                HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
                slice.put(bigBgUser.getmUid(), mUidsList.get(bigBgUser.getmUid()));
                mGridVideoViewContainer.notifyUiChanged(slice, bigBgUser.getmUid(), status, audioStatus, null);
            }
            { // find target view in small video view list
                log.warn("SmallVideoViewAdapter call notifyUiChanged " + mUidsList + " " + (bigBgUser.getmUid() & 0xFFFFFFFFL) + " target: " + (targetUid & 0xFFFFFFFFL) + "==" + targetUid + " " + status);
                if (mSmallVideoViewAdapter != null)
                    mSmallVideoViewAdapter.notifyUiChanged(mUidsList, bigBgUser.getmUid(), status, audioStatus, null);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception:", e.getCause());
        }
//        }
    }

    public void onVoiceMuteClicked(View view) {
        log.info("onVoiceMuteClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted);
        if (mUidsList.size() == 0) {
            return;
        }

        RtcEngine rtcEngine = rtcEngine();
//        rtcEngine.muteLocalAudioStream(mAudioMuted = !mAudioMuted);
        toggleAudioMuted();
        rtcEngine.muteLocalAudioStream(mAudioMuted);

        ImageView iv = (ImageView) view;

        iv.setImageResource(mAudioMuted ? R.drawable.ic_mic_off : R.drawable.ic_mic);
        iv.setBackground(mAudioMuted ? getResources().getDrawable(R.drawable.ic_call_icon_bg) : getResources().getDrawable(R.drawable.transparent_bg));
        updateAudioMuteView(config().getmUid(), mAudioMuted);
    }

    public synchronized void toggleAudioMuted(){
        mAudioMuted = !mAudioMuted;
    }

    public void onMixingAudioClicked(View view) {
        log.info("onMixingAudioClicked " + view + " " + mUidsList.size() + " video_status: " + mVideoMuted + " audio_status: " + mAudioMuted + " mixing_audio: " + mMixingAudio);

        if (mUidsList.size() == 0) {
            return;
        }

//        mMixingAudio = !mMixingAudio;
        toggleMixingAudio();
        RtcEngine rtcEngine = rtcEngine();
        if (mMixingAudio) {
            rtcEngine.startAudioMixing(Constant.MIX_FILE_PATH, false, false, -1);
        } else {
            rtcEngine.stopAudioMixing();
        }

        ImageView iv = (ImageView) view;
        iv.setImageResource(mMixingAudio ? R.drawable.btn_audio_mixing : R.drawable.btn_audio_mixing_off);
    }

    public synchronized void toggleMixingAudio(){
        mMixingAudio = !mMixingAudio;
    }

    @Override
    public void onUserJoined(int uid) {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                handelVideoIcon(((ImageView)findViewById(R.id.btn_camera)),true);
            }
        });*/
        Log.d("TAG", "onUser-Joined " + mUidsList.size() + " " + config().getmChannel()+" "+uid);
        if (!isCallDurationStarted) {
            chronometer.setBase(SystemClock.elapsedRealtime());
            chronometer.start();
            chronometer.setFormat("%s");
            isCallDurationStarted = true;
        }
        if (player != null) {
            player.stop();
            player.stop();
            player.reset();
            player = null;
        }
        if (mHandlerCall != null && mNoAnswerRunnable != null) {
            mHandlerCall.removeCallbacks(mNoAnswerRunnable);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.connecting_ui).setVisibility(View.INVISIBLE);
                notifyMessageChanged(new Message(new User(0, null), "user " + (uid & 0xFFFFFFFFL) + " joined"));
            }
        });

//        if(mVideoMuted) {
        doRenderRemoteUi(uid);
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    switchToSmallVideoView(uid);
//                }
//            },3000);

//        }
        if (getIntent().hasExtra("sos") && getIntent().hasExtra("callType") && getIntent().getStringExtra("callType").equalsIgnoreCase("outgoing")) {
            sosDismiss(true);
        }

    }

    private void sosDismiss(Boolean isAttend) {
        long providerID = PrefUtility.getProviderId(CallActivity.this);
        String token = PrefUtility.getStringInPref(CallActivity.this, com.mvp.omnicure.utils.Constants.SharedPrefConstants.TOKEN, "");
        System.out.println("channel name " + config().getmChannel()+" "+mUidsList);

        String receiverIds = "";
        if(!isAttend) {
            ArrayList<String> receiverArr = new ArrayList<String>();
            for (Map.Entry<Integer, SurfaceView> entry : mUidsList.entrySet()) {
                System.out.println("userjoined values " + entry.getKey());
                receiverArr.add(String.valueOf(entry.getKey()));
            }
            if(receiverArr.size() >= 2) {
                receiverIds = TextUtils.join(",", receiverArr);
            }
        }
        System.out.println("receiverids "+receiverIds);
        callViewModel.sendSOSDismiss(providerID, token, config().getmChannel(), com.mvp.omnicure.utils.Constants.FCMMessageType.SOS_DISMISS, receiverIds, auditId).observe(CallActivity.this, commonResponse -> {
            dismissProgressBar();
            Log.i(TAG, "sosDismiss: response "+commonResponse);

            if (commonResponse != null && commonResponse.getStatus()) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    public void run() {
                        sendAuditId(false, com.mvp.omnicure.utils.Constants.FCMMessageType.SOS_DISMISS);
                    }
                });

                //Do nothing
            } else {
                finish();
                log.info("sos dismiss message: " + commonResponse.getErrorMessage());
            }

        });
    }

    private void sendAuditId(Boolean callAttend, String type){
        if(outgoing && callAttend){
            return;
        }
        String providerID = String.valueOf( PrefUtility.getProviderId(CallActivity.this) );
        System.out.println("coming here "+callAttend);
        callViewModel.sendAuditId(auditId, callAttend, providerID, type).observe(CallActivity.this, commonResponse -> {
            System.out.println("callauditResponse "+auditId+" "+commonResponse);
            dismissProgressBar();
            if (commonResponse != null && commonResponse.getStatus()) {
                //Do nothing
            } else {
                log.info("sos dismiss message: " + commonResponse.getErrorMessage());
            }
            finish();
        });
    }

    @Override
    public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
        Log.d("TAG", "onFirstRemoteVideoDecoded");
        log.debug("onFirstRemoteVideoDecoded " + (uid & 0xFFFFFFFFL) + " " + width + " " + height + " " + elapsed);
        findViewById(R.id.connecting_ui).setVisibility(View.INVISIBLE);
        if (player != null) {
            player.stop();
            player.stop();
            player.reset();
            player = null;
        }
        if (mHandlerCall != null && mNoAnswerRunnable != null) {
            mHandlerCall.removeCallbacks(mNoAnswerRunnable);
        }
        if (mUidsList.containsKey(uid)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    doHideTargetView(uid, false);
                }
            });

        } else {
            doRenderRemoteUi(uid);
        }
    }

    private Provider getProviderFromList(int uid) {
        for (Provider provider : mProviderList) {
            if (provider != null && provider.getId().intValue() == uid) {
                return provider;
            }
        }
        return null;
    }

    private void doRenderRemoteUi(final int uid) {
        Log.d("TAG", "doRenderRemoteUi_: " + uid);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("isfinishing "+isFinishing());
                if (isFinishing()) {
                    return;
                }
                if (getProviderFromList(uid) == null) {
                    Log.d("TAG", "provider not found so first get provider from server "+uid);
                    Long id = PrefUtility.getProviderId(CallActivity.this);
                    String token = PrefUtility.getToken(CallActivity.this);
                    callViewModel.getProviderById(id, token, (long) uid).observe(CallActivity.this, commonResponse -> {
                        Gson gson = new Gson();
                        Log.d("TAG", "provider_response " + gson.toJson(commonResponse));
                        if (commonResponse != null && commonResponse.getStatus() != null && commonResponse.getStatus() && commonResponse.getProvider() != null) {
                            mProviderList.add(commonResponse.getProvider());
                            doRenderRemoteUi(commonResponse.getProvider().getId().intValue());

                        } else {
                            String errMsg = ErrorMessages.getErrorMessage(CallActivity.this, commonResponse.getErrorMessage(), com.mvp.omnicure.utils.Constants.API.getProviderById);
//                            UtilityMethods.showErrorSnackBar(mGridVideoViewContainer, errMsg, Snackbar.LENGTH_LONG);
                            CustomSnackBar.make(mGridVideoViewContainer, CallActivity.this, CustomSnackBar.WARNING, errMsg, CustomSnackBar.TOP, 3000, 0).show();


                        }
                    });

                } else {

                    if (mUidsList.containsKey(uid)) {
                        return;
                    }

                    System.out.println("addid "+uid);
                    System.out.println("providerlist_call "+mProviderList);

                    SurfaceView surfaceV = RtcEngine.CreateRendererView(getApplicationContext());
                    mUidsList.put(uid, surfaceV);

                    int count=0;
                    for (int uid : mUidsList.keySet()) {
                        count++;
                        System.out.println("mUidsList_call "+uid);
                        if (count>0)
                            break;
                    }

//                boolean useDefaultLayout = mLayoutType == LAYOUT_TYPE_DEFAULT;

                    surfaceV.setZOrderOnTop(true);
                    surfaceV.setZOrderMediaOverlay(true);

                    rtcEngine().setupRemoteVideo(new VideoCanvas(surfaceV, VideoCanvas.RENDER_MODE_HIDDEN, uid));

//                if (useDefaultLayout) {
//                    log.debug("doRenderRemoteUi LAYOUT_TYPE_DEFAULT " + (uid & 0xFFFFFFFFL));
//                    switchToDefaultVideoView();
//                } else {
                    int bigBgUid = mSmallVideoViewAdapter == null ? uid : mSmallVideoViewAdapter.getExceptedUid();
                    log.debug("doRenderRemoteUi LAYOUT_TYPE_SMALL " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL));
                    switchToSmallVideoView(bigBgUid, uid);
//                }

                    notifyMessageChanged(new Message(new User(0, null), "video from user " + (uid & 0xFFFFFFFFL) + " decoded"));
                }
            }
        });
    }


    @Override
    public void onJoinChannelSuccess(String channel, final int uid, int elapsed) {
        log.debug("onJoinChannelSuccess " + channel + " " + (uid & 0xFFFFFFFFL) + " " + elapsed);
    }

    @Override
    public void onUserOffline(int uid, int reason) {
        log.debug("onUserOffline " + (uid & 0xFFFFFFFFL) + " " + reason);
        doRemoveRemoteUi(uid);
    }

    @Override
    public void onExtraCallback(final int type, final Object... data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                doHandleExtraCallback(type, data);
            }
        });
    }

    private void doHandleExtraCallback(int type, Object... data) {
        int peerUid;
        boolean muted;

        switch (type) {
            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_MUTED:

                peerUid = (Integer) data[0];
                muted = (boolean) data[1];
                Log.d("TAG", "onExtraCallback EVENT_TYPE_ON_USER_VIDEO_MUTED: " + muted + " UID: " + peerUid);
                doHideTargetView(peerUid, muted);

                break;

            case AGEventHandler.EVENT_TYPE_ON_USER_VIDEO_STATS:
                IRtcEngineEventHandler.RemoteVideoStats stats = (IRtcEngineEventHandler.RemoteVideoStats) data[0];

                if (Constant.SHOW_VIDEO_INFO) {
                    if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                        mGridVideoViewContainer.addVideoInfo(stats.uid, new VideoInfoData(stats.width, stats.height, stats.delay, stats.rendererOutputFrameRate, stats.receivedBitrate));
                        int uid = config().getmUid();
                        int profileIndex = getVideoEncResolutionIndex();
                        String resolution = getResources().getStringArray(R.array.string_array_resolutions)[profileIndex];
                        String fps = getResources().getStringArray(R.array.string_array_frame_rate)[profileIndex];

                        String[] rwh = resolution.split("x");
                        int width = Integer.valueOf(rwh[0]);
                        int height = Integer.valueOf(rwh[1]);

                        mGridVideoViewContainer.addVideoInfo(uid, new VideoInfoData(width > height ? width : height,
                                width > height ? height : width,
                                0, Integer.valueOf(fps), Integer.valueOf(0)));
                    }
                } else {
                    mGridVideoViewContainer.cleanVideoInfo();
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_SPEAKER_STATS:
                IRtcEngineEventHandler.AudioVolumeInfo[] infos = (IRtcEngineEventHandler.AudioVolumeInfo[]) data[0];

                if (infos.length == 1 && infos[0].uid == 0) { // local guy, ignore it
                    break;
                }

                if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
                    HashMap<Integer, Integer> volume = new HashMap<>();

                    for (IRtcEngineEventHandler.AudioVolumeInfo each : infos) {
                        peerUid = each.uid;
                        int peerVolume = each.volume;

                        if (peerUid == 0) {
                            continue;
                        }
                        volume.put(peerUid, peerVolume);
                    }
                    List<UserStatusData> userList = mSmallVideoViewAdapter.getUserList();
                    HashMap<Integer, Integer> status = new HashMap<>();
                    HashMap<Integer, Integer> audioStatus = new HashMap<>();
                    if (userList != null) {
                        for (UserStatusData userStatusData : userList) {
                            status.put(userStatusData.getmUid(), userStatusData.getmStatus());
                            audioStatus.put(userStatusData.getmUid(), userStatusData.getmAudioStatus());
                        }
                    }
                    mGridVideoViewContainer.notifyUiChanged(mUidsList, config().getmUid(), status, audioStatus, volume);
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_APP_ERROR:
                int subType = (int) data[0];

                if (subType == ConstantApp.AppError.NO_CONNECTION_ERROR) {
                    String msg = getString(R.string.msg_connection_error);
                    notifyMessageChanged(new Message(new User(0, null), msg));
                    showLongToast(msg);
                }

                break;

            case AGEventHandler.EVENT_TYPE_ON_DATA_CHANNEL_MSG:
                peerUid = (Integer) data[0];
                final byte[] content = (byte[]) data[1];
                notifyMessageChanged(new Message(new User(peerUid, String.valueOf(peerUid)), new String(content)));
                Log.d("TAG", "EVENT_TYPE_ON_DATA_CHANNEL_MSG UID: " + peerUid);

                break;

            case AGEventHandler.EVENT_TYPE_ON_AGORA_MEDIA_ERROR: {
                int error = (int) data[0];
                String description = (String) data[1];

                notifyMessageChanged(new Message(new User(0, null), error + " " + description));

                break;
            }

            case AGEventHandler.EVENT_TYPE_ON_AUDIO_ROUTE_CHANGED:
                notifyHeadsetPlugged((int) data[0]);

                break;

        }
    }

    private void requestRemoteStreamType(final int currentHostCount) {
        log.debug("requestRemoteStreamType " + currentHostCount);
    }

    private void doRemoveRemoteUi(final int uid) {
        Log.d("TAG", "doRemoveRemoteUi");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isFinishing()) {
                    return;
                }

                Object target = mUidsList.remove(uid);
                if (target == null) {
                    return;
                }

                log.warn("SmallVideoViewAdapter notifyUiChanged-1 " + mUidsList.size());
                if (mUidsList.size() < 2) {
                    finish();
                }

                int bigBgUid = -1;
                if (mSmallVideoViewAdapter != null) {
                    bigBgUid = mSmallVideoViewAdapter.getExceptedUid();
                }
                if (mUidsList.get(bigBgUid) == null) {
                    int count = 0;
                    for (int uid : mUidsList.keySet()) {
                        count++;
                        bigBgUid = uid;
                        if (count>0)
                            break;
                    }
                }

                log.debug("doRemoveRemoteUi " + (uid & 0xFFFFFFFFL) + " " + (bigBgUid & 0xFFFFFFFFL) + " " + mLayoutType);

//                if (mLayoutType == LAYOUT_TYPE_DEFAULT || uid == bigBgUid) {
//                    switchToDefaultVideoView();
//                } else {
                switchToSmallVideoView(bigBgUid, -1);
//                }

                notifyMessageChanged(new Message(new User(0, null), "user " + (uid & 0xFFFFFFFFL) + " left"));
            }
        });
    }

    private void switchToDefaultVideoView() {
        Log.d("TAG", "switchToDefaultVideoView");
        if (mSmallVideoViewDock != null) {
            mSmallVideoViewDock.setVisibility(View.GONE);
        }
        mGridVideoViewContainer.initViewContainer(this, config().getmUid(), mVideoMuted ? 1 : 0, mAudioMuted ? 1 : 0, mUidsList, mIsLandscape, mProviderList);

        mLayoutType = LAYOUT_TYPE_DEFAULT;
        boolean setRemoteUserPriorityFlag = false;
        int sizeLimit = mUidsList.size();
        if (sizeLimit > ConstantApp.MAX_PEER_COUNT + 1) {
            sizeLimit = ConstantApp.MAX_PEER_COUNT + 1;
        }
        for (int i = 0; i < sizeLimit; i++) {
            int uid = mGridVideoViewContainer.getItem(i).getmUid();
            if (config().getmUid() != uid) {
                if (!setRemoteUserPriorityFlag) {
                    setRemoteUserPriorityFlag = true;
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_HIGH);
                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                } else {
                    rtcEngine().setRemoteUserPriority(uid, Constants.USER_PRIORITY_NORMAL);
                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (uid & 0xFFFFFFFFL));
                }
            }
        }
    }

    private void switchToSmallVideoView(int bigBgUid, int uid) {
        Log.d("TAG", "switchToSmallVideoView");
        HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
        slice.put(bigBgUid, mUidsList.get(bigBgUid));
        Iterator<SurfaceView> iterator = mUidsList.values().iterator();
        while (iterator.hasNext()) {
            SurfaceView s = iterator.next();
            s.setZOrderOnTop(true);
            s.setZOrderMediaOverlay(true);
        }

        mUidsList.get(bigBgUid).setZOrderOnTop(false);
        mUidsList.get(bigBgUid).setZOrderMediaOverlay(false);

        mGridVideoViewContainer.initViewContainer(this, bigBgUid, mVideoMuted ? 1 : 0, mAudioMuted ? 1 : 0, slice, mIsLandscape, mProviderList);

        bindToSmallVideoView(bigBgUid, uid);

        mLayoutType = LAYOUT_TYPE_SMALL;

        requestRemoteStreamType(mUidsList.size());
    }

    private void bindToSmallVideoView(int exceptUid, int uid) {
        Log.d("TAG", "bindToSmallVideoView "+uid);
        if (mSmallVideoViewDock == null) {
            ViewStub stub = (ViewStub) findViewById(R.id.small_video_view_dock);
            mSmallVideoViewDock = (RelativeLayout) stub.inflate();
        }

        boolean twoWayVideoCall = mUidsList.size() == 2;

        RecyclerView recycler = (RecyclerView) findViewById(R.id.small_video_view_container);

        boolean create = false;

        if (mSmallVideoViewAdapter == null) {
            Log.d("TAG", "smallview adapter created");
            create = true;
            mSmallVideoViewAdapter = new SmallVideoViewAdapter(this, config().getmUid(), mVideoMuted ? 1 : 0, mAudioMuted ? 1 : 0, exceptUid, mUidsList, mProviderList);
            mSmallVideoViewAdapter.setHasStableIds(true);

            recycler.setHasFixedSize(true);

            log.debug("bindToSmallVideoView " + twoWayVideoCall + " " + (exceptUid & 0xFFFFFFFFL));

            if (twoWayVideoCall) {
                recycler.setLayoutManager(new RtlLinearLayoutManager(getApplicationContext(), RtlLinearLayoutManager.HORIZONTAL, false));
            } else {
                recycler.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
            }
            recycler.addItemDecoration(new SmallVideoViewDecoration());
            recycler.setAdapter(mSmallVideoViewAdapter);
            recycler.addOnItemTouchListener(new RecyclerItemClickListener(getBaseContext(), new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    onSmallVideoViewClicked(view, position);
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }

                @Override
                public void onItemDoubleClick(View view, int position) {
//                onSmallVideoViewDoubleClicked(view, position);
                }
            }));

            recycler.setDrawingCacheEnabled(true);
            recycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_AUTO);
        }
        boolean needToNotify = false;
        if (!create) {
            needToNotify = true;
        } else if (create && mUidsList.size() > 1) {
            needToNotify = true;
        }
        if (needToNotify) {
            Log.d("TAG", "smallview adapter not created");
            mSmallVideoViewAdapter.setLocalUid(config().getmUid());
            HashMap<Integer, Integer> status = new HashMap<>();
            status.put(exceptUid, mVideoMuted ? 1 : 0);
            status.put(uid, mVideoMuted ? 1 : 0);
            status.put(config().getmUid(), mVideoMuted ? 1 : 0);
            HashMap<Integer, Integer> audioStatus = new HashMap<>();
            audioStatus.put(exceptUid, mAudioMuted ? 1 : 0);
            audioStatus.put(uid, mAudioMuted ? 1 : 0);
            audioStatus.put(config().getmUid(), mAudioMuted ? 1 : 0);
            System.out.println("providerlist_call "+mProviderList);
            mSmallVideoViewAdapter.setmProviderList(mProviderList);
            mSmallVideoViewAdapter.notifyUiChanged(mUidsList, exceptUid, status, audioStatus, null);
        }
        for (Integer tempUid : mUidsList.keySet()) {
            if (config().getmUid() != tempUid) {
                if (tempUid == exceptUid) {
                    rtcEngine().setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_HIGH);
                    log.debug("setRemoteUserPriority USER_PRIORITY_HIGH " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                } else {
                    rtcEngine().setRemoteUserPriority(tempUid, Constants.USER_PRIORITY_NORMAL);
                    log.debug("setRemoteUserPriority USER_PRIORITY_NORANL " + mUidsList.size() + " " + (tempUid & 0xFFFFFFFFL));
                }
            }
        }
        recycler.setVisibility(View.VISIBLE);
        mSmallVideoViewDock.setVisibility(View.VISIBLE);
    }

    private void onSmallVideoViewClicked(View view, int position) {
        //TODO: handle onSmallVideoViewClicked
        int bigBgUid = mSmallVideoViewAdapter.getItemUid(position);
        if (mSmallVideoViewAdapter != null && mSmallVideoViewAdapter.getExceptedUid() != bigBgUid) {
            HashMap<Integer, SurfaceView> slice = new HashMap<>(1);
            slice.put(bigBgUid, mUidsList.get(bigBgUid));
            Iterator<SurfaceView> iterator = mUidsList.values().iterator();
            while (iterator.hasNext()) {
                SurfaceView s = iterator.next();
                s.setZOrderOnTop(true);
                s.setZOrderMediaOverlay(true);
            }

            List<UserStatusData> userList = mSmallVideoViewAdapter.getUserList();
            HashMap<Integer, Integer> status = new HashMap<>();
            HashMap<Integer, Integer> audioStatus = new HashMap<>();
            if (userList != null) {
                for (UserStatusData userStatusData : userList) {
                    status.put(userStatusData.getmUid(), userStatusData.getmStatus());
                    audioStatus.put(userStatusData.getmUid(), userStatusData.getmAudioStatus());
                }
            }
            slice.get(bigBgUid).setZOrderOnTop(false);
            slice.get(bigBgUid).setZOrderMediaOverlay(false);
            mGridVideoViewContainer.notifyUiChanged(slice, bigBgUid, status, audioStatus, null);
//            mGridVideoViewContainer.initViewContainer(this, bigBgUid,status.get(bigBgUid), slice, mIsLandscape,mProviderList);
            if (mSmallVideoViewAdapter != null) {
                mSmallVideoViewAdapter.setLocalUid(config().getmUid());
                mSmallVideoViewAdapter.notifyUiChanged(mUidsList, bigBgUid, status, audioStatus, null);
            }
        }
    }

    public void notifyHeadsetPlugged(final int routing) {
        log.info("notifyHeadsetPlugged " + routing + " " + mVideoMuted);

        mAudioRouting = routing;

        ImageView iv = (ImageView) findViewById(R.id.switch_speaker_id);
        if (mAudioRouting == Constants.AUDIO_ROUTE_SPEAKERPHONE) {
            iv.setImageResource(R.drawable.ic_speaker);
            iv.setBackground(getResources().getDrawable(R.drawable.transparent_bg));
        } else {
            iv.setImageResource(R.drawable.ic_phone_speaker);
            iv.setBackground(getResources().getDrawable(R.drawable.ic_call_icon_bg));
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mIsLandscape = newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE;

//        if (mLayoutType == LAYOUT_TYPE_DEFAULT) {
//            switchToDefaultVideoView();
//        } else if (mSmallVideoViewAdapter != null) {
        switchToSmallVideoView(mSmallVideoViewAdapter.getExceptedUid(), -1);
//        }
    }


    private void registerCallerBusyBroadCast() {
        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    TextView connectingError = (TextView) CallActivity.this.findViewById(R.id.connection_error);
                    String providerName = intent.getStringExtra("providerName");
                    String providerObjString = intent.getStringExtra("providerList");
                    ArrayList<Provider> providerListNotification = new ArrayList<>();
                    try {
                        JSONArray providerArray = new JSONArray(providerObjString);
                        for (int i = 0; i < providerArray.length(); i++) {
                            JSONObject providerObj = (JSONObject) providerArray.get(i);
                            Provider provider = new Provider();
                            provider.setId(providerObj.getLong("id"));
                            provider.setName(providerObj.getString("name"));
                            if (providerObj.has("profilePicUrl")) {
                                provider.setProfilePicUrl(providerObj.getString("profilePicUrl"));
                            }
                            if (providerObj.has("hospital")) {
                                provider.setHospital(providerObj.getString("hospital"));
                            }
                            if(providerObj.has("role")) {
                                provider.setRole(providerObj.getString("role"));
                            }
                            providerListNotification.add(provider);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Exception:", e.getCause());
                    }
                    boolean groupCall = false;
                    if ((providerListNotification != null && providerListNotification.size() > 2) || sosCall) {
                        groupCall = true;
                    }

                    System.out.println("provider_list "+groupCall+" "+providerListNotification);
                    switch (action) {
                        case "caller-busy": {
                            if (TextUtils.isEmpty(connectingError.getText())) {
                                connectingError.setText(providerName + " " + getString(R.string.is_busy));
                            } else {
                                if (groupCall) {
                                    connectingError.setText(connectingError.getText() + "\n" + providerName + " " + getString(R.string.is_busy));
                                } else {
                                    connectingError.setText(providerName + " " + getString(R.string.is_busy));
                                }
                            }
                            connectingError.setVisibility(View.VISIBLE);
                            if (!groupCall) {
                                AssetFileDescriptor afd = null;
                                try {
                                    if (player != null) {
                                        player.stop();
                                        player.reset();
                                        player = null;
                                    }
                                    afd = getAssets().openFd("busytone.mp3");
                                    if (player == null) {
                                        player = new MediaPlayer();
                                    }
                                    player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                    player.prepare();
                                    player.setLooping(false);
                                    player.start();
                                } catch (Exception e) {
                                    Log.e(TAG, "Exception:", e.getCause());
                                }finally {
                                    if (afd != null) {
                                        try {
                                            afd.close();
                                        } catch (IOException e) {
                                            Log.e("error", e.getMessage());
                                        }
                                    }
                                }
                                TextView connection_message = (TextView) findViewById(R.id.connection_message);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            }
                        }
                        break;
                        case "caller-reject": {
                            if (TextUtils.isEmpty(connectingError.getText())) {
                                connectingError.setText(providerName + " " + getString(R.string.declined));
                            } else {
                                if (groupCall) {
                                    connectingError.setText(connectingError.getText() + "\n" + providerName + " " + getString(R.string.declined));
                                } else {
                                    connectingError.setText(providerName + " " + getString(R.string.declined));
                                }

                            }
                            connectingError.setVisibility(View.VISIBLE);

                            if (!groupCall) {
                                AssetFileDescriptor afd = null;
                                try {
                                    if (player != null) {
                                        player.stop();
                                        player.stop();
                                        player.reset();
                                        player = null;
                                    }
                                    afd = getAssets().openFd("busytone.mp3");
                                    if (player == null) {
                                        player = new MediaPlayer();
                                    }
                                    player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                    player.prepare();
                                    player.setLooping(false);
                                    player.start();
                                } catch (Exception e) {
                                    Log.e(TAG, "Exception:", e.getCause());
                                }finally {
                                    if (afd != null) {
                                        try {
                                            afd.close();
                                        } catch (IOException e) {
                                            Log.e("error", e.getMessage());
                                        }
                                    }
                                }
                                TextView connection_message = (TextView) findViewById(R.id.connection_message);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            }
                        }
                        break;
                        case "caller-not-answer": {
                            if (TextUtils.isEmpty(connectingError.getText())) {
                                connectingError.setText(providerName + " " + getString(R.string.did_not_answer));
                            } else {
                                if (groupCall) {
                                    connectingError.setText(connectingError.getText() + "\n" + providerName + " " + getString(R.string.did_not_answer));
                                } else {
                                    connectingError.setText(providerName + " " + getString(R.string.did_not_answer));
                                }

                            }
                            connectingError.setVisibility(View.VISIBLE);
                            if (!groupCall) {
                                AssetFileDescriptor afd = null;
                                try {
                                    if (player != null) {
                                        player.stop();
                                        player.stop();
                                        player.reset();
                                        player = null;
                                    }
                                    afd = getAssets().openFd("busytone.mp3");
                                    if (player == null) {
                                        player = new MediaPlayer();
                                    }
                                    player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                                    player.prepare();
                                    player.setLooping(false);
                                    player.start();
                                } catch (Exception e) {
                                    Log.e(TAG, "Exception:", e.getCause());
                                }finally {
                                    if (afd != null) {
                                        try {
                                            afd.close();
                                        } catch (IOException e) {
                                            Log.e("error", e.getMessage());
                                        }
                                    }
                                }
                                TextView connection_message = (TextView) findViewById(R.id.connection_message);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 2000);
                            }
                        }
                        break;
                    }
                }

            };

            IntentFilter filter = new IntentFilter("caller-busy");
            filter.addAction("caller-reject");
            filter.addAction("caller-not-answer");
            registerReceiver(mBroadcastReceiver, filter);
        }
    }

    private void unregisterCallerBusyBroadCast() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void handelVideoIcon(ImageView videoMuteView, boolean isEnable) {
        videoMuteView.setEnabled(isEnable);
        if (isEnable) {
            videoMuteView.setImageResource(mVideoMuted ? R.drawable.ic_video_mute : R.drawable.ic_video);
            videoMuteView.setBackground(mVideoMuted ? getResources().getDrawable(R.drawable.ic_call_icon_bg) : getResources().getDrawable(R.drawable.transparent_bg));
        } else {
            videoMuteView.setImageResource(mVideoMuted ? R.drawable.ic_video_mute_disabled : R.drawable.ic_video_disabled);
            videoMuteView.setBackground(mVideoMuted ? getResources().getDrawable(R.drawable.ic_call_icon_bg_disabled) : getResources().getDrawable(R.drawable.transparent_bg));
        }
    }
}
