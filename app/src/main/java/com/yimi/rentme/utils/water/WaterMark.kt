package com.yimi.rentme.utils.water

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Environment
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.yimi.rentme.R
import com.yimi.rentme.utils.water.helper.MagicFilterType
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.utils.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

object WaterMark {

    lateinit var waterBitmap: Bitmap
    private var downloadPath = ""
    private var outPutUrl = ""
    private var imageUrl = ""
    private var videoWidth = 0
    private var videoHeight = 0

    fun createWater(
        activity: AppCompatActivity, downloadPath: String, otherUserId: Long, videoWidth: Int,
        videoHeight: Int, duration: Long, callBack: CallBack
    ) {
        this.downloadPath = downloadPath
        this.videoWidth = videoWidth
        this.videoHeight = videoHeight
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        outPutUrl = file.absolutePath + "/Camera/xg_" + randomString(15) + ".mp4"
        imageUrl = getImageFile().absolutePath
        waterBitmap = textToBitmap(activity, "虾菇号：$otherUserId", null)
        val clipper = VideoClipper()
        clipper.setInputVideoPath(downloadPath)
        clipper.setFilterType(MagicFilterType.WARM)
        clipper.setOutputVideoPath(outPutUrl)
        clipper.setOnVideoCutFinishListener {
            BaseApp.context.uploadFile(outPutUrl)
            SCToastUtil.showToast(activity, "下载成功", 2)
            callBack.sure()
        }
        try {
            clipper.clipVideo(0, duration)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveImage(
        activity: AppCompatActivity, otherUserId: Long,
        downloadPath: String, callBack: CallBack
    ) {
        this.downloadPath = downloadPath
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        outPutUrl = file.absolutePath + "/Camera/xg_" + randomString(15) + ".jpg"
        Glide.with(activity).asBitmap().load(downloadPath).apply(RequestOptions().centerCrop())
            .into(object : SimpleTarget<Bitmap?>() {

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    videoWidth = resource.width
                    videoHeight = resource.height
                    activity.runOnUiThread {
                        saveFile(textToBitmap(activity, "虾菇号：$otherUserId", resource), callBack)
                    }
                }
            })
    }

    private fun saveFile(bitmap: Bitmap, callBack: CallBack) {
        val file = File(outPutUrl)
        try {
            val os = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
            os.flush()
            os.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        SCToastUtil.showToast(null, "保存成功", 2)
        BaseApp.context.uploadFile(outPutUrl)
        callBack.sure()
    }

    /**
     * 文本转成Bitmap
     *
     * @param text 文本内容
     * @return 图片的bitmap
     */
    private fun textToBitmap(activity: AppCompatActivity, text: String, resource: Bitmap?): Bitmap {
        val ra =
            if (videoHeight > videoWidth) videoWidth.toFloat() / BaseApp.W else videoHeight.toFloat() / BaseApp.W
        val w = (87f * 1.8f * ra).toInt()
        val h = (39f * 1.8f * ra).toInt()
        val size = 7f * 2 * ra
        val layout = LinearLayout(activity)
        layout.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(videoWidth, videoHeight)
        layout.layoutParams = layoutParams
        if (resource == null) layout.setBackgroundColor(Color.TRANSPARENT) else {
            layout.setBackgroundDrawable(BitmapDrawable(resource))
        }
        val iv = ImageView(activity)
        val params = LinearLayout.LayoutParams(w, h)
        params.leftMargin = 0
        params.rightMargin = BaseApp.W
        params.gravity = Gravity.START
        iv.layoutParams = params
        iv.setImageResource(R.mipmap.water_icon)
        layout.addView(iv)
        val tv = TextView(activity)
        tv.text = text
        tv.textSize = size
        tv.setTextColor(Color.WHITE)
        tv.setShadowLayer(1f, 2f, 2f, R.color.black)
        tv.setBackgroundColor(Color.TRANSPARENT)
        layout.addView(tv)
        if (resource != null) {
            val width: Int = videoWidth - BaseApp.context.dip2px(size) * text.length
            val height = videoHeight - 2 * h
            layout.setPadding(width, height, 0, 0)
        }
        layout.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        return if (resource == null) {
            layout.layout(0, 0, layout.measuredWidth, layout.measuredHeight)
            layout.buildDrawingCache()
            val bitmap = layout.drawingCache
            Bitmap.createScaledBitmap(bitmap, bitmap.width, bitmap.height, false)
        } else {
            layout.layout(0, 0, videoWidth, videoHeight)
            val bitmap = Bitmap.createBitmap(videoWidth, videoHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            layout.draw(canvas)
            bitmap
        }
    }

    interface CallBack {
        fun sure()
    }
}