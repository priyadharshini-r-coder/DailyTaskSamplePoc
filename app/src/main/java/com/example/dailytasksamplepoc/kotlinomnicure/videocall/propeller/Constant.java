package com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller;

import io.agora.rtc.RtcEngine;
import io.agora.rtc.video.BeautyOptions;

public class Constant {

    public static final String MEDIA_SDK_VERSION;

    static {
        String sdk = "undefined";
        try {
            sdk = RtcEngine.getSdkVersion();
        } catch (Throwable e) {
        }
        MEDIA_SDK_VERSION = sdk;
    }

    public static final String MIX_FILE_PATH = "/assets/ringtone.mp3"; // in assets folder

    public static final boolean SHOW_VIDEO_INFO = false;

    private static boolean DEBUG_INFO_ENABLED = false; // Show debug/log info on screen

    private static boolean BEAUTY_EFFECT_ENABLED = false; // Built-in face beautification

    public static final int BEAUTY_EFFECT_DEFAULT_CONTRAST = 1;
    public static final float BEAUTY_EFFECT_DEFAULT_LIGHTNESS = .7f;
    public static final float BEAUTY_EFFECT_DEFAULT_SMOOTHNESS = .5f;
    public static final float BEAUTY_EFFECT_DEFAULT_REDNESS = .1f;

    public static final BeautyOptions BEAUTY_OPTIONS = new BeautyOptions(BEAUTY_EFFECT_DEFAULT_CONTRAST, BEAUTY_EFFECT_DEFAULT_LIGHTNESS, BEAUTY_EFFECT_DEFAULT_SMOOTHNESS, BEAUTY_EFFECT_DEFAULT_REDNESS);

    public static final float BEAUTY_EFFECT_MAX_LIGHTNESS = 1.0f;
    public static final float BEAUTY_EFFECT_MAX_SMOOTHNESS = 1.0f;
    public static final float BEAUTY_EFFECT_MAX_REDNESS = 1.0f;

    public static boolean isDebugInfoEnabled() {
        return DEBUG_INFO_ENABLED;
    }

    public static void setDebugInfoEnabled(boolean debugInfoEnabled) {
        DEBUG_INFO_ENABLED = debugInfoEnabled;
    }

    public static boolean isBeautyEffectEnabled() {
        return BEAUTY_EFFECT_ENABLED;
    }

    public static void setBeautyEffectEnabled(boolean beautyEffectEnabled) {
        BEAUTY_EFFECT_ENABLED = beautyEffectEnabled;
    }
}
