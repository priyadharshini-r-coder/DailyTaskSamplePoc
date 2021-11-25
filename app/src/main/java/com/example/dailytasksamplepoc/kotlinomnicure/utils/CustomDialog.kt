package com.example.dailytasksamplepoc.kotlinomnicure.utils

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.example.dailytasksamplepoc.R
import android.app.Dialog

class CustomDialog() : Dialog(){
    private var positive: Button? = null
    private  var negative: Button? = null
    private var titleMsgView: TextView? = null
    private  var msgView:TextView? = null
    private var mTitleId = -1
    private var title: String? = null
    private var msg: String? = null
    private var positivetextId = -1
    private var positiveListener: View.OnClickListener? = null
    private var negativeTextId = -1
    private var negetiveListener: View.OnClickListener? = null
    private var positiveButtonColor = -1
    var showTitle = true

    constructor(parcel: Parcel,context: Context?) : this() {
        mTitleId = parcel.readInt()
        title = parcel.readString()
        msg = parcel.readString()
        positivetextId = parcel.readInt()
        negativeTextId = parcel.readInt()
        positiveButtonColor = parcel.readInt()
        showTitle = parcel.readByte() != 0.toByte()
    }

    fun CustomDialog(context: Context?) {
        //super(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.custom_dialog)
        window!!.setBackgroundDrawable(ColorDrawable(Color.BLACK))
        window!!.setDimAmount(1f)

//        WindowManager.LayoutParams lp = getWindow().getAttributes();
//        lp.dimAmount=0.7f;
//        getWindow().setAttributes(lp);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        titleMsgView = findViewById(R.id.id_custom_dialog_title_text)
        msgView = findViewById<TextView>(R.id.id_custom_dialog_msg_text)
        val width = (context.resources.displayMetrics.widthPixels * 0.75).toInt()
        msgView!!.getLayoutParams().width = width
        positive = findViewById(R.id.id_custom_dialog_positive_btn)
        negative = findViewById<Button>(R.id.id_custom_dialog_negative_btn)
        val line = findViewById<View>(R.id.id_line)
        if (mTitleId != -1) {
            titleMsgView!!.setText(mTitleId)
        }
        if (title != null) {
            titleMsgView!!.setText(title)
        }
        if (!showTitle) {
            titleMsgView!!.setVisibility(View.GONE)
        } else {
            titleMsgView!!.setVisibility(View.VISIBLE)
        }
        if (msg != null) {
            msgView!!.setText(msg)
        }
        if (positivetextId != -1) {
            positive!!.setText(positivetextId)
            positive!!.setOnClickListener(positiveListener)
            positive!!.setVisibility(View.VISIBLE)
        }
        if (negativeTextId != -1) {
            negative!!.setText(negativeTextId)
            negative!!.setOnClickListener(negetiveListener)
            negative!!.setVisibility(View.VISIBLE)
        }
        if (positivetextId == -1 || negativeTextId == -1) {
            //line.setVisibility(View.GONE);
            positive!!.setBackgroundResource(R.drawable.signin_selector_btn_bg)
            negative!!.setBackgroundResource(R.drawable.signin_selector_btn_bg)
        }
        if (positiveButtonColor != -1) {
            positive!!.setTextColor(positiveButtonColor)
        }
        //customDialogView.setVisibility(View.VISIBLE);
    }

    fun setMessage(msg: String?) {
        this.msg = msg
    }

    @JvmName("setShowTitle1")
    fun setShowTitle(flag: Boolean) {
        showTitle = flag
    }

    override fun setTitle(id: Int) {
        mTitleId = id
    }

    fun setTitle(title: String?) {
        this.title = title
    }

    fun setPositiveButton(textId: Int, positiveListener: View.OnClickListener?) {
        positivetextId = textId
        this.positiveListener = positiveListener
    }

    fun setNegativeButton(textId: Int, positiveListener: View.OnClickListener?) {
        negetiveListener = positiveListener
        negativeTextId = textId
    }

    fun setPositiveButtonDrawable(drawable: Int) {
        positive!!.background = context.resources.getDrawable(drawable)
    }

    fun getPositiveButtonColor(): Int {
        return positiveButtonColor
    }

    fun setPositiveButtonColor(positiveButtonColor: Int) {
        this.positiveButtonColor = positiveButtonColor
    }

    fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(mTitleId)
        parcel.writeString(title)
        parcel.writeString(msg)
        parcel.writeInt(positivetextId)
        parcel.writeInt(negativeTextId)
        parcel.writeInt(positiveButtonColor)
        parcel.writeByte(if (showTitle) 1 else 0)
    }

    fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CustomDialog> {
        override fun createFromParcel(parcel: Parcel): CustomDialog {
            return CustomDialog(parcel)
        }

        override fun newArray(size: Int): Array<CustomDialog?> {
            return arrayOfNulls(size)
        }
    }

}