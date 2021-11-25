package com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

//import android.support.v7.widget.LinearLayoutManager;
class RtlLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    ) {
    }

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    override fun isLayoutRTL(): Boolean {
        return true
    }
}