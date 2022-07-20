package com.yimi.rentme.bean

import android.graphics.Bitmap
import java.io.Serializable

class SelectImage : Serializable {
    var imageUrl = ""
    var videoUrl = ""
    var index = 0
    var bitmap: Bitmap? = null
}