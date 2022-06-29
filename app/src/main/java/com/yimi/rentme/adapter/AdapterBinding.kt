package com.yimi.rentme.adapter

import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import androidx.databinding.BindingAdapter
import com.yimi.rentme.MineApp
import com.yimi.rentme.views.FullScreenVideoView
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