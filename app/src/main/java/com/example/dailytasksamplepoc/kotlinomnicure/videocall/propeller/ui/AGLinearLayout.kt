package com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui.ViewUtil.checkDoubleTouchEvent
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui.ViewUtil.checkDoubleKeyEvent
import android.widget.LinearLayout
import android.view.MotionEvent
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui.ViewUtil

//import android.support.annotation.NonNull;
class AGLinearLayout : LinearLayout {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        return checkDoubleTouchEvent(event, this) || super.dispatchTouchEvent(event)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return checkDoubleKeyEvent(event, this) || super.dispatchKeyEvent(event)
    }
}