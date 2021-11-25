package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.ActionBar;

import com.mvp.omnicure.R;
import com.mvp.omnicure.videocall.openvcall.model.ConstantApp;
import com.mvp.omnicure.videocall.openvcall.model.CurrentUserSettings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import android.support.v7.app.ActionBar;

public class InitVideoCallActivity extends BaseActivity {

    private final static Logger log = LoggerFactory.getLogger(InitVideoCallActivity.class);
    String mProviderName,mProviderHospitalName;
    String mChannelName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init_video_call);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        getSupportActionBar().hide();
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            ab.setCustomView(R.layout.ard_agora_actionbar);
        }
//        mProviderName = getIntent().getStringExtra("providerName");
//        mProviderHospitalName = getIntent().getStringExtra("providerHospitalName");
//        mChannelName = getIntent().getStringExtra("channelName");
        forwardToRoom();
    }

    @Override
    protected void initUIandEvent() {

    }

    @Override
    protected void deInitUIandEvent() {

    }

    public void onClickAccept(View view) {
        forwardToRoom();
    }

    public void onClickDecline(View view) {
        finish();
    }
    public void onClickSettings(View view) {
        Intent i = new Intent(this, SettingsActivity.class);
        startActivity(i);
    }

    public void forwardToRoom() {
        /*CurrentUserSettings.mChannelName = getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME);//"omnicuretest";
        CurrentUserSettings.mEncryptionKey = "";*/
        //changed to private fields
        CurrentUserSettings.setmChannelName(getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME));//"omnicuretest";
        CurrentUserSettings.setmEncryptionKey("");
        Intent i = new Intent(InitVideoCallActivity.this, CallActivity.class);
        i.putExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME, getIntent().getStringExtra(ConstantApp.ACTION_KEY_CHANNEL_NAME));
        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_KEY, "");
//        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, getResources().getStringArray(R.array.encryption_mode_values)[CurrentUserSettings.mEncryptionModeIndex]);
        i.putExtra(ConstantApp.ACTION_KEY_ENCRYPTION_MODE, getResources().getStringArray(R.array.encryption_mode_values)[CurrentUserSettings.getmEncryptionModeIndex()]);
       if(getIntent().hasExtra("providerList")) {
           i.putStringArrayListExtra("providerList", getIntent().getStringArrayListExtra("providerList"));
       }
        i.putExtra("providerName", getIntent().getStringExtra("providerName"));
        i.putExtra("providerHospitalName", getIntent().getStringExtra("hospitalName"));
        if(getIntent().hasExtra("patientId")) {
           i.putExtra("patientId", getIntent().getStringExtra("patientId"));
       }
        if(getIntent().hasExtra("profilePicUrl")) {
            i.putExtra("profilePicUrl", getIntent().getStringExtra("profilePicUrl"));
        }
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        finish();
    }


}
