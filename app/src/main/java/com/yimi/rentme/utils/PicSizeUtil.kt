package com.yimi.rentme.utils

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import com.zb.baselibs.app.BaseApp
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

object PicSizeUtil {

    fun getPicSize(activity: AppCompatActivity, url: String, onPicListener: OnPicListener) {
        BaseApp.fixedThreadPool.execute {
            val connection: HttpURLConnection
            try {
                connection = URL(url).openConnection() as HttpURLConnection
                val inputStream = connection.inputStream
                val imageSize = getImageSize(inputStream)
                activity.runOnUiThread {
                    onPicListener.onImageSize(imageSize[0]!!, imageSize[1]!!)
                }
                inputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getImageSize(input: InputStream): Array<Int?> {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeStream(input, null, options)
        val size = arrayOfNulls<Int>(2)
        size[0] = options.outWidth
        size[1] = options.outHeight
        return size
    }

    interface OnPicListener {
        fun onImageSize(width: Int, height: Int)
    }
}