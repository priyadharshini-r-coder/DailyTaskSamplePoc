package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.mvp.omnicure.R;
import com.mvp.omnicure.apiRetrofit.ApiClient;
import com.mvp.omnicure.apiRetrofit.RequestBodys.SendMsgNotifyRequestBody;
import com.mvp.omnicure.utils.Buildconfic;
import com.mvp.omnicure.utils.Constants;
import com.mvp.omnicure.utils.PrefUtility;
import com.mvp.omnicure.utils.UtilityMethods;
import com.mvp.omnicure.videocall.openvcall.model.ConstantApp;
import com.mvp.omnicure.videocall.openvcall.model.CurrentUserSettings;
import com.mvp.omnicure.viewmodel.CallActivityViewModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import omnicure.mvp.com.providerEndpoints.model.CommonResponse;
import omnicure.mvp.com.providerEndpoints.model.Provider;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.support.v7.app.ActionBar;

public class RingingActivity extends BaseActivity {
    private final static Logger log = LoggerFactory.getLogger(RingingActivity.class);
    private static final String TAG = "RingingAct";
    private boolean screenOn = false;
    MediaPlayer player = null;
    Handler mHandlerRinging = null;
    Runnable mRunnable = null;
    BroadcastReceiver mBroadcastReceiver;
    ScreenReceiver mReceiver;
    ArrayList<Provider> mProviderList = new ArrayList<>();
    private Long mAcceptClickTime = System.currentTimeMillis();
    String auditId;

    @SuppressLint("StringFormatInvalid")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing);

        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        getSupportActionBar().hide();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ab.setCustomView(R.layout.ard_agora_actionbar);
        }
        ImageView sosImageView = (ImageView) findViewById(R.id.sos_icon);
        TextView patient_title = (TextView) findViewById(R.id.patient_title);

        //appname
        TextView header = (TextView) findViewById(R.id.header);
        header.setText(String.format(getString(R.string.omnicure_now), Buildconfic.value()));

        LinearLayout patientInfoLayout = (LinearLayout) findViewById(R.id.patient_info_layout);
        if (getIntent().hasExtra("sos")) {
            sosImageView.setVisibility(View.VISIBLE);
            patientInfoLayout.setBackgroundResource(R.color.sos_red);
            patient_title.setText(R.string.sos_alert);

        } else {
            sosImageView.setVisibility(View.GONE);
            patientInfoLayout.setBackgroundResource(R.drawable.blue_gradient);
            patient_title.setText(R.string.patient_information);
        }
        ImageButton imageButton = (ImageButton) findViewById(R.id.ringing_call_icon);
        if (getIntent().hasExtra("call") && getIntent().getStringExtra("call").equalsIgnoreCase(Constants.FCMMessageType.AUDIO_CALL)) {
            imageButton.setImageResource(R.drawable.ic_audio_call_white);
        } else {
            imageButton.setImageResource(R.drawable.video_icon);
        }

        com.mvp.omnicure.customview.CircularImageView caller_image_view = (com.mvp.omnicure.customview.CircularImageView) findViewById(R.id.caller_image_view);
        caller_image_view.setVisibility(View.GONE);
        TextView caller_image_text = (TextView) findViewById(R.id.caller_image_text);

        TextView caller_name = (TextView) findViewById(R.id.caller_name);
        String providerName = getIntent().getStringExtra("providerName");
        String providerType = getIntent().getStringExtra("providerType");

        String callType = "Incoming call";
        if (getIntent().hasExtra("call")) {
            if (getIntent().getStringExtra("call").equalsIgnoreCase(Constants.FCMMessageType.AUDIO_CALL)) {
                callType = "Audio call";
            } else if (getIntent().getStringExtra("call").equalsIgnoreCase(Constants.FCMMessageType.VIDEO_CALL)) {
                callType = "Video call";
            }
        }
        if (getIntent().hasExtra(Constants.IntentKeyConstants.AUDIT_ID) ) {
            auditId = getIntent().getStringExtra(Constants.IntentKeyConstants.AUDIT_ID) ;
            auditId = auditId==null?"":auditId ;
        }

        String pt = "";
        if (!TextUtils.isEmpty(providerType)) {
            pt = ", " + providerType;
        }
        String callerTitle = (getIntent().hasExtra("sos") ? "SOS " + callType.toLowerCase() + " " : callType) + " from \n" + providerName + pt;

//        caller_name.setText(getIntent().getStringExtra("providerName"));
        caller_name.setText(callerTitle);
        caller_image_text.setVisibility(View.VISIBLE);
        caller_image_text.setText(UtilityMethods.getNameText(getIntent().getStringExtra("providerName")));

        TextView caller_hospital = (TextView) findViewById(R.id.caller_hospital);
        caller_hospital.setText(getIntent().getStringExtra("hospitalName"));
        caller_hospital.setVisibility(View.GONE);
        TextView patient = (TextView) findViewById(R.id.id_patient_name);
        LinearLayout patient_layout = (LinearLayout) findViewById(R.id.patient_layout);
        if (getIntent().hasExtra("patientAge")) {
            patient_layout.setVisibility(View.VISIBLE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeZone(TimeZone.getDefault());
            calendar.setTimeInMillis(getIntent().getLongExtra("patientAge", 0));
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.setTimeZone(TimeZone.getDefault());
            currentCalendar.setTimeInMillis(System.currentTimeMillis());
            int age = currentCalendar.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
            String gender = getIntent().getStringExtra("gender");
            patient.setText(getIntent().getStringExtra("patientName") + " " + "\u2022" + " " + age + " " + gender);
        } else {
            patient_layout.setVisibility(View.INVISIBLE);
        }
        AssetFileDescriptor afd=null;
        try {
            afd = getAssets().openFd("ringtone.mp3");
            player = new MediaPlayer();
            player.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            player.prepare();
            player.setLooping(true);
            player.start();
        } catch (Exception e) {
            Log.e("error", e.getMessage());
        }finally {
            if (afd != null) {
                try {
                    afd.close();
                } catch (IOException e) {
                    Log.e("error", e.getMessage());
                }
            }
        }
        if (getIntent().hasExtra("profilePicUrl") && !TextUtils.isEmpty(getIntent().getStringExtra("profilePicUrl"))) {
            String imageUrl = getIntent().getStringExtra("profilePicUrl");
            caller_image_view.setVisibility(View.VISIBLE);
            caller_image_text.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(imageUrl)) {
                Glide.with(this)
                        .load(imageUrl)
                        .into(caller_image_view);
//                new RingingActivity.ImageLoader(RingingActivity.this, imageUrl).execute();
            }
        }
       mHandlerRinging = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                sendCallResponseMessage(Constants.FCMMessageType.CALLER_NOT_ANSWER);
                sendAuditId(Constants.FCMMessageType.CALLER_NOT_ANSWER, false);
            }
        };
        mHandlerRinging.postDelayed(mRunnable, 60 * 1000L);
        // INITIALIZE RECEIVER
        registerScreenBroadCast();
        registerBroadCast();
    }

    private void registerBroadCast() {
        if (mReceiver == null) {
            mReceiver = new ScreenReceiver();
        }
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction("caller-reject");
        filter.addAction("caller-not-answer");
        filter.addAction("sos-dismiss");

        registerReceiver(mReceiver, filter);
    }

    private void registerScreenBroadCast() {
//        if (mBroadcastReceiver == null) {
//            mBroadcastReceiver = new ScreenReceiver();
//        }
//        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
//        filter.addAction("caller-reject");
//        filter.addAction("caller-not-answer");
//        filter.addAction("sos-dismiss");
//
//        registerReceiver(mBroadcastReceiver, filter);

        if (mBroadcastReceiver == null) {
            mBroadcastReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    String action = intent.getAction();
                    String providerName = intent.getStringExtra("providerName");

                    if (intent.hasExtra("providerList")) {
                        Gson gson = new Gson();
                        String providerListStr = intent.getStringExtra("providerList");
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
                                provider.setRole(providerObj.getString("role"));
                                mProviderList.add(provider);
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "Exception:", e.getCause());
                        }
                    }

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
                    Log.i("RingingActivity", "provider_list " + mProviderList);
                    if (providerListNotification != null && providerListNotification.size() > 2) {
                        groupCall = true;
                    } else if (getIntent().hasExtra("sos")) {
                        groupCall = true;
                    }

                    System.out.println("provider_list_ringing "+groupCall+" "+providerListNotification);
                    switch (action) {

                        case "caller-busy": {

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
                                sendAuditId(Constants.FCMMessageType.CALLER_REJECT, false);
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

            IntentFilter filter1 = new IntentFilter("caller-busy");
            filter1.addAction("caller-reject");
            filter1.addAction("caller-not-answer");
            registerReceiver(mBroadcastReceiver, filter1);
        }
    }

    private void unregisterScreenBroadCast() {
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
            mBroadcastReceiver = null;
        }
    }

    private void unregisterBroadCast() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void sendAuditId(String type, Boolean flag){
        System.out.println("coming hereee");
        String providerID = String.valueOf(PrefUtility.getProviderId(RingingActivity.this) );
        new CallActivityViewModel().sendAuditId(auditId, flag, providerID, type).observe(RingingActivity.this, commonResponse -> {
            System.out.println("callauditResponseRinging "+auditId+" "+commonResponse);
            dismissProgressBar();
            if (commonResponse != null && commonResponse.getStatus()) {
                //Do nothing
            } else {
                log.info("sos dismiss message: " + commonResponse.getErrorMessage());
            }
        } );
    }

    @Override
    protected void onStart() {
        screenOn = true;
        super.onStart();
    }

    @Override
    protected void initUIandEvent() {

    }

    @Override
    protected void deInitUIandEvent() {

    }

    public void onClickAccept(View view) {
        if ((System.currentTimeMillis() - mAcceptClickTime) < 1000) {
            return;
        }
        mAcceptClickTime = System.currentTimeMillis();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                sendAuditId("", true);
            }
        });
        forwardToRoom();
    }

    public void onClickDecline(View view) {
        sendCallResponseMessage(Constants.FCMMessageType.CALLER_REJECT);
        sendAuditId(Constants.FCMMessageType.CALLER_REJECT, false);
    }

    private void sendCallResponseMessage(String messageType) {
        showProgressBar(getString(R.string.disconnecting));

       /* String token = PrefUtility.getStringInPref(context, Constants.SharedPrefConstants.TOKEN, "");

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

        String token = PrefUtility.getStringInPref(RingingActivity.this, Constants.SharedPrefConstants.TOKEN, "");
        String errMsg[] = new String[1];

        SendMsgNotifyRequestBody requestBody = new SendMsgNotifyRequestBody();
        requestBody.setProviderId(PrefUtility.getProviderId(RingingActivity.this));//id key
        requestBody.setToken(token);
        requestBody.setReceiverId(getIntent().getLongExtra("providerId", 0L));
        requestBody.setMessage(getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME));
        requestBody.setType(messageType);

        if (getIntent().hasExtra("patientId")) {
            requestBody.setPatientId(getIntent().getLongExtra("patientId", 0L));
        }

        ApiClient.getApiProviderEndpoints(true,true).sendMessageNotification(requestBody).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Log.e(TAG, "onResponse: sendMessageNotification "+ response.toString());
                if (response.body() != null) {
                    Log.e(TAG, "onResponse: sendMessageNotification status "+ response.body().getStatus());
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (t instanceof SocketTimeoutException)
                    errMsg[0] = Constants.APIErrorType.SocketTimeoutException.toString();
                else if (t instanceof Exception)
                    errMsg[0] = Constants.API_ERROR;

            }
        });

        /*new Thread(new Runnable() {
            String errMsg = "";

            @Override
            public void run() {
                try {
                    String token = PrefUtility.getStringInPref(RingingActivity.this, Constants.SharedPrefConstants.TOKEN, "");
                    final ProviderEndpoints.SendMessageNotification apiCall = EndPointBuilder.getProviderEndpoints()
                            .sendMessageNotification(PrefUtility.getProviderId(RingingActivity.this), token, getIntent().getLongExtra("providerId", 0l), getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME), messageType, auditId);

                    if (getIntent().hasExtra("patientId")) {
                        apiCall.setPatientsId(getIntent().getLongExtra("patientId", 0l));
                    }
                    CommonResponse commonResponse = apiCall.execute();
                    Log.i("response", "response for busy notification : " + commonResponse.getStatus());
                } catch (SocketTimeoutException e) {
                    errMsg = Constants.APIErrorType.SocketTimeoutException.toString();
                } catch (Exception e) {
//                    errMsg = Constants.APIErrorType.Exception.toString();
                    errMsg = Constants.API_ERROR;

                }
                finish();
            }
        }).start();*/
    }

    public void onClickSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }
//    AssetFileDescriptor afd = getAssets().openFd("ringtone.mp3");

    public void forwardToRoom() {
        /*CurrentUserSettings.mChannelName = getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);//"omnicuretest";

        CurrentUserSettings.mEncryptionKey = "";*/
        //changed fields to private
        CurrentUserSettings.setmChannelName(getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME));//"omnicuretest";

        CurrentUserSettings.setmEncryptionKey("");
        Intent intent = new Intent(RingingActivity.this, CallActivity.class);
        intent.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME));
        intent.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
        intent.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, getResources().getStringArray(R.array.encryption_mode_values)[CurrentUserSettings.getmEncryptionModeIndex()]);
        intent.putExtra("providerName", getIntent().getStringExtra("providerName"));
        intent.putExtra(Constants.IntentKeyConstants.AUDIT_ID, getIntent().getStringExtra(Constants.IntentKeyConstants.AUDIT_ID) );
        intent.putExtra("providerHospitalName", getIntent().getStringExtra("hospitalName"));
        intent.putExtra("call", getIntent().getStringExtra("call"));
        intent.putExtra("providerList", getIntent().getStringExtra("providerList"));

        if (getIntent().hasExtra("patientId")) {
            intent.putExtra("patientId", getIntent().getLongExtra("patientId", 0l));
        }
        if (getIntent().hasExtra("sos")) {
            intent.putExtra("sos", getIntent().hasExtra("sos"));
        }
        if (getIntent().hasExtra("profilePicUrl")) {
            intent.putExtra("profilePicUrl", getIntent().getStringExtra("profilePicUrl"));
        }
//        if(getIntent().hasExtra("sos")) {
//            i.putExtra("sos", getIntent().getBooleanExtra("sos",false));
//        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (player != null) {
            player.stop();
        }
        unregisterScreenBroadCast();
        unregisterBroadCast();
        if (mHandlerRinging != null && mRunnable != null) {
            mHandlerRinging.removeCallbacks(mRunnable);
        }
        super.onDestroy();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP) {
            Log.i("", "Dispath event power");
            if (player != null) {
                player.stop();
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            Log.i("", "Dispath event power");
            if (player != null) {
                player.stop();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            Log.i("", "Dispath event power");
            if (player != null) {
                player.stop();
            }
        }
        return super.onKeyMultiple(keyCode, repeatCount, event);
    }

    @Override
    protected void onStop() {
        screenOn = false;
        super.onStop();
    }


    class ScreenReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // do whatever you need to do here
                if (player != null) {
                    player.stop();
                }
            } else if (intent.getAction().equalsIgnoreCase("sos-dismiss")) {
                String channel = intent.getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);
                if (getIntent().hasExtra("sos") && channel.equalsIgnoreCase(getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME))) {
                    sendAuditId(Constants.FCMMessageType.CALLER_NOT_ANSWER, false);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    }, 1000);

                }
            }
        }

    }

}
