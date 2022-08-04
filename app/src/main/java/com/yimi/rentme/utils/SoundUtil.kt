package com.yimi.rentme.utils

import android.graphics.drawable.AnimationDrawable
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.R
import com.zb.baselibs.utils.SCToastUtil
import com.zb.baselibs.utils.getAudioFile
import java.util.*

class SoundUtil(private val activity: AppCompatActivity) {
    private var mediaRecorder: MediaRecorder? = null
    private var timer: Timer? = null
    private var resTime = 0
    private lateinit var animationDrawable: AnimationDrawable
    private var audioPath = ""
    private lateinit var audioBtn: ImageView
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var callBack: CallBack
    private val handler = Handler { msg ->
        resTime = msg.what
        val MAX_DURATION = 60
        if (msg.what == MAX_DURATION || msg.what > MAX_DURATION) {
            SCToastUtil.showToast(activity, "最长时间为60秒", 2)
            stop()
        }
        false
    }

    fun setAudioBtn(audioBtn: ImageView): SoundUtil {
        this.audioBtn = audioBtn
        return this
    }

    fun setCallBack(callBack: CallBack): SoundUtil {
        this.callBack = callBack
        return this
    }

    fun setResTime(resTime:Int){
        this.resTime = resTime
    }

    /**
     * 语音开始
     */
    fun start() {
        try {
            audioBtn.visibility = View.VISIBLE
            audioBtn.setImageResource(R.drawable.voice_record_anim)
            animationDrawable = audioBtn.drawable as AnimationDrawable
            animationDrawable.start()
            if (mediaRecorder == null) {
                mediaRecorder = MediaRecorder()
                mediaRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
                mediaRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
                mediaRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                audioPath = getAudioFile().absolutePath
                mediaRecorder!!.setOutputFile(audioPath)
                mediaRecorder!!.prepare()
                mediaRecorder!!.start()
                // 按钮按下时创建一个Timer定时器
                timer = Timer()
                // 创建一个TimerTask
                // TimerTask是个抽象类,实现了Runnable接口，所以TimerTask就是一个子线程
                val timerTask: TimerTask = object : TimerTask() {
                    // 倒数10秒
                    var i = 0
                    override fun run() {
                        // 定义一个消息传过去
                        val msg = Message()
                        msg.what = i++
                        handler.sendMessage(msg)
                    }
                }
                timer!!.scheduleAtFixedRate(timerTask, 0, 1000)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 语音结束
     */
    fun stop() {
        if (mediaRecorder == null) return
        try {
            if (timer != null) timer!!.cancel()
            if (mediaRecorder != null) {
                mediaRecorder!!.stop()
                mediaRecorder!!.release()
                mediaRecorder = null
            }
        } catch (e: java.lang.Exception) {
            mediaRecorder = null
        }
        audioBtn.visibility = View.GONE
        audioBtn.setImageResource(R.mipmap.voice_anim_1)
        animationDrawable.stop()
        callBack.soundEnd()
        if (resTime < 1) {
            SCToastUtil.showToast(activity, "语音录制不能少于1秒", 2)
            resTime = 0
        } else callBack.sendSound(resTime, audioPath)
    }

    /**
     * 语音播放
     */
    fun soundPlayer(msg: String, v: View) {
        try {
            mediaPlayer = MediaPlayer()
            mediaPlayer!!.setDataSource(msg)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
            mediaPlayer!!.setOnCompletionListener { arg0: MediaPlayer? ->
                stopPlayer()
                callBack.playEnd(v)
            }
        } catch (ignored: java.lang.Exception) {
        }
    }

    fun stopPlayer() {
        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop()
            }
            mediaPlayer!!.release()
            mediaPlayer = null
        }
    }

    interface CallBack {
        fun playEnd(view: View)
        fun sendSound(resTime: Int, audioPath: String) {}
        fun soundEnd() {}
    }
}