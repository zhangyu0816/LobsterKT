package com.yimi.rentme.adapter

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.yimi.rentme.MineApp
import com.yimi.rentme.R
import com.yimi.rentme.views.*
import com.zb.baselibs.utils.SCToastUtil

@BindingAdapter("videoUrl")
fun FullScreenVideoView.setVideoUrl(videoUrl: String) {
    this.setOnPreparedListener { mp -> }
    //异常回调
    this.setOnErrorListener { mp, what, extra ->
        true //如果方法处理了错误，则为true；否则为false。返回false或根本没有OnErrorListener，将导致调用OnCompletionListener。
    }
    this.setOnPreparedListener { mp ->
        mp.isLooping = true //让电影循环播放
    }
    //信息回调
    this.setOnInfoListener { mp, what, extra ->
        when (what) {
            MediaPlayer.MEDIA_INFO_UNKNOWN, MediaPlayer.MEDIA_INFO_NOT_SEEKABLE -> {
                SCToastUtil.showToast(MineApp.videoPlayActivity, "视频播放失败", 2)
                this.stopPlayback() //停止播放视频,并且释放
                this.suspend() //在任何状态下释放媒体播放器
                return@setOnInfoListener true
            }
            MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                // 缓冲结束,此接口每次回调完START就回调END,若不加上判断就会出现缓冲图标一闪一闪的卡顿现象
                mp.isPlaying
                return@setOnInfoListener true
            }
            MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START -> {
                this.setBackgroundColor(Color.TRANSPARENT)
            }
        }
        false //如果方法处理了信息，则为true；如果没有，则为false。返回false或根本没有OnInfoListener，将导致丢弃该信息。
    }
    this.setVideoURI(Uri.parse(videoUrl))
    this.start()
}

@BindingAdapter("isBig")
fun TextView.tabTextSize(isBig: Boolean) {
    this.textSize = if (isBig) 16f else 14f
}

@BindingAdapter(value = ["tabTitle", "tabSelect"], requireAll = false)
fun TabView.setTab(tabTitle: String, tabSelect: Boolean) {
    this.selectTab(tabTitle, tabSelect)
}

@BindingAdapter(value = ["noDataRes", "noWifi"], requireAll = false)
fun NoDataView.setNoDataInfo(noDataRes: Int, noWifi: Boolean) {
    this.setNoDataInfo(noDataRes, noWifi)
}

@BindingAdapter("bottleTitleIsPlay")
fun BottleTitleView.setBottleTitle(bottleTitleIsPlay: Boolean) {
    if (bottleTitleIsPlay)
        this.start()
    else
        this.stop()
}

@BindingAdapter("dpValue")
fun RoundRelativeLayout.setDpValue(dpValue: Float) {
    this.setDpValue(dpValue)
}

@BindingAdapter(value = ["isLike", "isGrey", "isLightGrey"], requireAll = false)
fun GoodView.likeStatus(isLike: Boolean, isGrey: Boolean, isLightGrey: Boolean) {
    if (isGrey) {
        this.findViewById<View>(R.id.iv_unLike)
            .setBackgroundResource(R.drawable.like_unselect_grey_icon)
    } else if (isLightGrey) {
        this.findViewById<View>(R.id.iv_unLike).setBackgroundResource(R.drawable.icon_like_gray_big)
    } else {
        this.findViewById<View>(R.id.iv_unLike).setBackgroundResource(R.drawable.like_unselect_icon)
    }
    if (isLike) {
        this.findViewById<View>(R.id.iv_like).visibility = View.VISIBLE
        this.findViewById<View>(R.id.iv_unLike).visibility = View.GONE
    } else {
        this.findViewById<View>(R.id.iv_like).visibility = View.GONE
        this.findViewById<View>(R.id.iv_unLike).visibility = View.VISIBLE
    }
}

@BindingAdapter(value = ["bigSuperLikeInterface", "isPlay"], requireAll = false)
fun SuperLikeBigView.superLike(superLikeInterface: SuperLikeInterface, isPlay: Boolean) {
    this.setSuperLikeInterface(superLikeInterface)
    if (isPlay) this.play() else this.stop()
}