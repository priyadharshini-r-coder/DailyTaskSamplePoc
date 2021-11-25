package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.model;

public class CurrentUserSettings {

    private static int mEncryptionModeIndex;

    private static String mEncryptionKey;

    private static String mChannelName;

    public static int getmEncryptionModeIndex() {
        return mEncryptionModeIndex;
    }

    public static void setmEncryptionModeIndex(int mEncryptionModeIndex) {
        CurrentUserSettings.mEncryptionModeIndex = mEncryptionModeIndex;
    }

    public static String getmEncryptionKey() {
        return mEncryptionKey;
    }

    public static void setmEncryptionKey(String mEncryptionKey) {
        CurrentUserSettings.mEncryptionKey = mEncryptionKey;
    }

    public static String getmChannelName() {
        return mChannelName;
    }

    public static void setmChannelName(String mChannelName) {
        CurrentUserSettings.mChannelName = mChannelName;
    }

    public CurrentUserSettings() {
        reset();
    }

    public void reset() {
    }
}
