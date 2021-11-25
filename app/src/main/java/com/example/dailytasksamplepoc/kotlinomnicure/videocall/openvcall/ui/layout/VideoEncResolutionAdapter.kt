package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui.layout

import android.R
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.model.ConstantApp


class VideoEncResolutionAdapter(private val mContext: Context, private var mSelectedIdx: Int) :
    RecyclerView.Adapter<Any?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.video_enc_resolution_item, parent, false)
        return VideoEncResolutionViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val resolution =
            mContext.resources.getStringArray(R.array.string_array_resolutions)[position]
        (holder as VideoEncResolutionViewHolder).mTextResolution.setText(resolution)
        holder.itemView.setBackgroundResource(if (position == mSelectedIdx) R.drawable.rounded_bg_for_btn else R.drawable.rounded_bg_for_btn_normal)
        (holder as VideoEncResolutionViewHolder).mTextResolution.setTextColor(
            mContext.resources.getColor(
                if (position == mSelectedIdx) R.color.white else R.color.dark_black
            )
        )
    }

    override fun getItemCount(): Int {
        return mContext.resources.getStringArray(R.array.string_array_resolutions).size
    }

    inner class VideoEncResolutionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mTextResolution: TextView

        init {
            mTextResolution = itemView.findViewById<View>(R.id.video_enc_resolution) as TextView
            itemView.setOnClickListener {
                mSelectedIdx = getLayoutPosition()
                notifyDataSetChanged()
                val pref: SharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(mContext)
                val editor: SharedPreferences.Editor = pref.edit()
                editor.putInt(
                    ConstantApp.PrefManager.PREF_PROPERTY_VIDEO_ENC_RESOLUTION,
                    mSelectedIdx
                )
                editor.apply()
            }
        }
    }

    override fun onBindViewHolder(holder: Any, position: Int) {
        TODO("Not yet implemented")
    }
}