package com.example.kotlinomnicure.customview

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.widget.TextView
import com.example.dailytasksamplepoc.R


class CustomProgressDialog: Dialog{
    private var activity: Activity? = null

    constructor(context: Activity):super(context) {
        //super(context, R.style.TransparentProgressDialog);
        val wlmp = window!!.attributes
        activity = context
        wlmp.gravity = Gravity.CENTER
        window!!.attributes = wlmp
        setTitle(null)
        setCancelable(false)
        setContentView(R.layout.custom_progress)
        //getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }


    override fun show() {
        try {
            val isDestroyed: Boolean = activity!!.isDestroyed
            if (!activity!!.isFinishing && !isDestroyed && !this.isShowing) {
                super.show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setText(text: String?) {
        val textView: TextView = findViewById(R.id.id_pb_txt)
        textView.text = text
    }
}


