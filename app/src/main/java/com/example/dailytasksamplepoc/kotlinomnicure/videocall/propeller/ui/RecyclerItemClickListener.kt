package com.example.dailytasksamplepoc.kotlinomnicure.videocall.propeller.ui

import android.content.Context
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import android.view.GestureDetector
import androidx.recyclerview.widget.RecyclerView
import android.view.MotionEvent
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.View

//import android.support.v7.widget.RecyclerView;
class RecyclerItemClickListener(context: Context?, private val mListener: OnItemClickListener?) :
    OnItemTouchListener {
    interface OnItemClickListener {
        fun onItemClick(view: View?, position: Int)
        fun onItemLongClick(view: View?, position: Int)
        fun onItemDoubleClick(view: View?, position: Int)
    }

    private val mGestureDetector: GestureDetector
    private var mRv: RecyclerView? = null
    override fun onInterceptTouchEvent(view: RecyclerView, e: MotionEvent): Boolean {
        val childView = view.findChildViewUnder(e.x, e.y)
        mRv = view
        if (childView != null && mListener != null) {
            mGestureDetector.onTouchEvent(e)
        }
        return false
    }

    override fun onTouchEvent(view: RecyclerView, motionEvent: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(b: Boolean) {}

    init {
        mGestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                if (mRv == null) {
                    return false
                }
                val child = mRv!!.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener.onItemClick(child, mRv!!.getChildAdapterPosition(child))
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                if (mRv == null) {
                    return
                }
                val child = mRv!!.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener.onItemLongClick(child, mRv!!.getChildAdapterPosition(child))
                }
            }

            override fun onDoubleTap(e: MotionEvent): Boolean {
                if (mRv == null) {
                    return false
                }
                val child = mRv!!.findChildViewUnder(e.x, e.y)
                if (child != null && mListener != null) {
                    mListener.onItemDoubleClick(child, mRv!!.getChildAdapterPosition(child))
                }
                return true
            }
        })
    }
}