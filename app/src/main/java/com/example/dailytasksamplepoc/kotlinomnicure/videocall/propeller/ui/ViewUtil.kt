package com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui

import android.content.Context
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui.ViewUtil
import android.view.MotionEvent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import com.example.dailytasksamplepoc.R
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.VideoInfoData
import org.slf4j.LoggerFactory

object ViewUtil {
    internal const val DEBUG_ENABLED = false
    private val log = LoggerFactory.getLogger(ViewUtil::class.java)
    private const val DEFAULT_TOUCH_TIMESTAMP = -1 // first time
    private const val TOUCH_COOL_DOWN_TIME = 500 // ms
    private var mLastTouchTime = DEFAULT_TOUCH_TIMESTAMP.toLong()

    /* package */
    @JvmStatic
    fun checkDoubleTouchEvent(event: MotionEvent, view: View): Boolean {
        if (DEBUG_ENABLED) {
            log.debug("dispatchTouchEvent " + mLastTouchTime + " " + event)
        }
        if (event.action == MotionEvent.ACTION_DOWN) { // only check touch down event
            if (mLastTouchTime == DEFAULT_TOUCH_TIMESTAMP.toLong() || SystemClock.elapsedRealtime() - mLastTouchTime >= TOUCH_COOL_DOWN_TIME) {
                mLastTouchTime = SystemClock.elapsedRealtime()
            } else {
                log.warn("too many touch events " + view + " " + MotionEvent.ACTION_DOWN)
                return true
            }
        }
        return false
    }

    /* package */
    @JvmStatic
    fun checkDoubleKeyEvent(event: KeyEvent, view: View): Boolean {
        if (DEBUG_ENABLED) {
            log.debug("dispatchKeyEvent " + mLastTouchTime + " " + event)
        }
        if (event.action == KeyEvent.ACTION_DOWN && event.keyCode == KeyEvent.KEYCODE_ENTER) {
            if (mLastTouchTime != DEFAULT_TOUCH_TIMESTAMP.toLong() && SystemClock.elapsedRealtime() - mLastTouchTime < TOUCH_COOL_DOWN_TIME) {
                log.warn("too many key events " + view + " " + KeyEvent.ACTION_DOWN)
                return true
            }
            mLastTouchTime = SystemClock.elapsedRealtime()
        }
        return false
    }

    fun setBackground(view: View, drawable: Drawable?) {
        view.background = drawable
    }

    fun composeVideoInfoString(context: Context, videoMetaData: VideoInfoData): String {
        // so far do not show delay info
        return (videoMetaData.getmWidth().toString() + "x" + videoMetaData.getmWidth() + ", "
                + context.getString(
            R.string.frame_rate_value_with_unit,
            videoMetaData.getmFrameRate()
        ) + ", "
                + context.getString(R.string.bit_rate_value_with_unit, videoMetaData.getmBitRate()))
    }
}