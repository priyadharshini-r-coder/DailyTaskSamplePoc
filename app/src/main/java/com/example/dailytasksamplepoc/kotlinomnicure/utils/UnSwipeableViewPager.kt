package com.example.dailytasksamplepoc.kotlinomnicure.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class UnSwipeableViewPager  :ViewPager(){
    fun UnSwipeableViewPager(context: Context?) {

    }

    fun UnSwipeableViewPager(context: Context?, attrs: AttributeSet?) {

    }

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        // Never allow swiping to switch between pages
        return false
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        // Never allow swiping to switch between pages
        return false
    }
}