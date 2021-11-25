package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui.layout

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import android.widget.RelativeLayout
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui.CircularImageView
import android.widget.TextView
import android.widget.LinearLayout
import com.example.dailytasksamplepoc.R

//import android.support.v7.widget.RecyclerView;
class VideoUserStatusHolder(v: View) : RecyclerView.ViewHolder(v) {
    @JvmField
    val mMaskView: RelativeLayout
    @JvmField
    val mMaskViewContainer: RelativeLayout
    @JvmField
    val mAvatar: ImageView
    @JvmField
    val mCircularAvtar: CircularImageView
    @JvmField
    val mAvtarName: TextView
    @JvmField
    val mAvtarImageText: TextView
    @JvmField
    val mIndicator: ImageView
    @JvmField
    val mVideoInfo: LinearLayout
    @JvmField
    val mMetaData: TextView

    init {
        mMaskView = v.findViewById<View>(R.id.user_control_mask) as RelativeLayout
        mMaskViewContainer = v.findViewById<View>(R.id.user_overlay) as RelativeLayout
        mAvatar = v.findViewById<View>(R.id.default_avatar) as ImageView
        mCircularAvtar = v.findViewById<View>(R.id.default_avatar_circular) as CircularImageView
        mAvtarName = v.findViewById<View>(R.id.default_avatar_name) as TextView
        mIndicator = v.findViewById<View>(R.id.indicator) as ImageView
        mVideoInfo = v.findViewById<View>(R.id.video_info_container) as LinearLayout
        mMetaData = v.findViewById<View>(R.id.video_info_metadata) as TextView
        mAvtarImageText = v.findViewById<View>(R.id.avtar_image_text) as TextView
    }
}