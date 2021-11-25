package com.example.dailytasksamplepoc.kotlinomnicure.videocall.openvcall.ui.layout

import android.content.Context
import com.mvp.omnicure.R
import org.slf4j.LoggerFactory
import java.lang.NullPointerException
import java.lang.String
import java.util.ArrayList
import java.util.HashMap

//import android.support.v7.widget.RecyclerView;
abstract class VideoViewAdapter(
    activity: Activity,
    localUid: Int,
    localStatus: Int,
    localAudioStatus: Int,
    uids: HashMap<Int?, SurfaceView?>,
    providerList: ArrayList<Provider>?
) : RecyclerView.Adapter<RecyclerView.ViewHolder?>() {
    protected val mInflater: LayoutInflater
    @JvmField
    protected val mContext: Context
    @JvmField
    protected val mUsers: ArrayList<UserStatusData>
    protected var mProviderList: ArrayList<Provider>
    @JvmField
    protected var mLocalUid: Int
    @JvmField
    protected var mLocalStatus: Int
    @JvmField
    protected var mLocalAudioStatus: Int
    @JvmField
    protected var mItemWidth = 0
    @JvmField
    protected var mItemHeight = 0
    @JvmField
    protected var mDefaultChildItem = 0
    private fun init(uids: HashMap<Int?, SurfaceView?>) {
        mUsers.clear()
        customizedInit(uids, true)
    }

    protected abstract fun customizedInit(uids: HashMap<Int?, SurfaceView?>?, force: Boolean)
    abstract fun notifyUiChanged(
        uids: HashMap<Int?, SurfaceView?>?,
        uidExtra: Int,
        status: HashMap<Int?, Int?>?,
        audioStatus: HashMap<Int?, Int?>?,
        volume: HashMap<Int?, Int?>?
    )

    @JvmField
    protected var mVideoInfo // left user should removed from this HashMap
            : HashMap<Int, VideoInfoData>? = null

    fun addVideoInfo(uid: Int, video: VideoInfoData) {
        if (mVideoInfo == null) {
            mVideoInfo = HashMap<Int, VideoInfoData>()
        }
        mVideoInfo!![uid] = video
    }

    fun cleanVideoInfo() {
        mVideoInfo = null
    }

    fun setLocalUid(uid: Int) {
        mLocalUid = uid
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: ViewGroup =
            mInflater.inflate(R.layout.video_view_container, parent, false) as ViewGroup
        v.getLayoutParams().width = mItemWidth
        v.getLayoutParams().height = mItemHeight
        mDefaultChildItem = v.getChildCount()
        return VideoUserStatusHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val myHolder = holder as VideoUserStatusHolder
        val user: UserStatusData = mUsers[position]
        if (DEBUG) {
            log.debug("onBindViewHolder " + position + " " + user + " " + myHolder + " " + myHolder.itemView + " " + mDefaultChildItem)
        }
        val holderView: FrameLayout = myHolder.itemView as FrameLayout
        if (holderView.getChildCount() == mDefaultChildItem) {
            val target: SurfaceView = user.getmView()
            VideoViewAdapterUtil.stripView(target)
            holderView.addView(
                target,
                0,
                FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            )
        }
        VideoViewAdapterUtil.renderExtraData(mContext, user, myHolder)
    }

    val itemCount: Int
        get() {
            if (DEBUG) {
                log.debug("getItemCount " + mUsers.size)
            }
            return mUsers.size
        }

    override fun getItemId(position: Int): Long {
        val user: UserStatusData = mUsers[position]
        val view: SurfaceView = user.getmView()
            ?: throw NullPointerException(
                "SurfaceView destroyed for user " + user.getmUid()
                    .toString() + " " + user.getmStatus()
                    .toString() + " " + user.getmVolume()
            )
        return (String.valueOf(user.getmUid()) + System.identityHashCode(view)).hashCode()
    }

    fun getItemUid(position: Int): Int {
        val user: UserStatusData = mUsers[position]
        return user.getmUid()
    }

    open fun getItem(position: Int): UserStatusData? {
        return mUsers[position]
    }

    val userList: List<Any>
        get() = mUsers

    protected fun setProfileInUsers() {
        for (i in mUsers.indices) {
            val provider: Provider? = getProviderFromList(mUsers[i].getmUid())
            if (provider != null) {
                mUsers[i].setmName(provider.getName())
                mUsers[i].setmProfilePic(provider.getProfilePicUrl())
            }
        }
    }

    private fun getProviderFromList(uid: Int): Provider? {
        for (provider in mProviderList) {
            if (provider.getId().intValue() === uid) {
                return provider
            }
        }
        return null
    }

    fun getmProviderList(): ArrayList<Provider> {
        return mProviderList
    }

    fun setmProviderList(mProviderList: ArrayList<Provider>) {
        this.mProviderList = mProviderList
    }

    companion object {
        private val log = LoggerFactory.getLogger(VideoViewAdapter::class.java)
        const val DEBUG = false
    }

    init {
        mInflater = (activity as Activity).getLayoutInflater()
        mContext = (activity as Activity).getApplicationContext()
        mLocalUid = localUid
        mLocalStatus = localStatus
        mLocalAudioStatus = localAudioStatus
        mUsers = ArrayList<UserStatusData>()
        mProviderList = ArrayList<Provider>()
        if (providerList != null) {
            mProviderList.addAll(providerList)
        }
        init(uids)
    }
}