package com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller

import kotlin.jvm.JvmOverloads

class VideoInfoData @JvmOverloads constructor(
    private var mWidth: Int,
    private var mHeight: Int,
    private var mDelay: Int,
    private var mFrameRate: Int,
    private var mBitRate: Int,
    private var mCodec: Int = 0
) {
    fun getmWidth(): Int {
        return mWidth
    }

    fun setmWidth(mWidth: Int) {
        this.mWidth = mWidth
    }

    fun getmHeight(): Int {
        return mHeight
    }

    fun setmHeight(mHeight: Int) {
        this.mHeight = mHeight
    }

    fun getmDelay(): Int {
        return mDelay
    }

    fun setmDelay(mDelay: Int) {
        this.mDelay = mDelay
    }

    fun getmFrameRate(): Int {
        return mFrameRate
    }

    fun setmFrameRate(mFrameRate: Int) {
        this.mFrameRate = mFrameRate
    }

    fun getmBitRate(): Int {
        return mBitRate
    }

    fun setmBitRate(mBitRate: Int) {
        this.mBitRate = mBitRate
    }

    fun getmCodec(): Int {
        return mCodec
    }

    fun setmCodec(mCodec: Int) {
        this.mCodec = mCodec
    }

    override fun toString(): String {
        return "VideoInfoData{" +
                "mWidth=" + mWidth +
                ", mHeight=" + mHeight +
                ", mDelay=" + mDelay +
                ", mFrameRate=" + mFrameRate +
                ", mBitRate=" + mBitRate +
                ", mCodec=" + mCodec +
                '}'
    }
}