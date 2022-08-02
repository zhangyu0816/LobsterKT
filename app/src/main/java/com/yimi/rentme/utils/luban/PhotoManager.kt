package com.yimi.rentme.utils.luban

import android.os.SystemClock
import android.text.TextUtils
import androidx.appcompat.app.AppCompatActivity
import com.yimi.rentme.ApiService
import com.yimi.rentme.MineApp
import com.zb.baselibs.app.BaseApp
import com.zb.baselibs.http.MainDataSource
import com.zb.baselibs.utils.SCToastUtil
import me.shaohui.advancedluban.Luban
import me.shaohui.advancedluban.OnCompressListener
import me.shaohui.advancedluban.OnMultiCompressListener
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.CopyOnWriteArrayList


class PhotoManager(
    private val context: AppCompatActivity,
    private val mainDataSource: MainDataSource<ApiService>,
    private val listener: OnUpLoadImageListener
) {

    private val photos: MutableList<PhotoFile> = CopyOnWriteArrayList()
    private var instantUpload = false //是否立即上传
    private val maxSize = 30 //最大几张图
    private val maxReUpload = 3 //最少几张图
    private var unUploadSize = 0 //未上传
    private var currentUploadSize = 0 //正在上传的
    private var failSize = 0 //失败的
    private var successSize = 0 //成功的
    private var reUploadCount = 0

    private var isChat = false
    private var isGPU = false

    val imageSize: Int
        get() = photos.size

    fun addNotUpLoadFile(url: String?): PhotoManager {
        if (photos.size >= maxSize) {
            return this //不能继续添加
            //throw new Exception("不能继续添加");
        }
        photos.add(PhotoFile(url))
        statisticsUploadStatus()
        return this
    }

    fun getWebUrl(srcFilePath: String?): String {
        var webUrl = ""
        for (photoFile in photos) {
            if (TextUtils.equals(photoFile.srcFilePath, srcFilePath)) {
                webUrl = photoFile.webUrl.toString()
                break
            }
        }
        return webUrl
    }

    /**
     * 添加 文件  压缩 并上传
     *
     * @param file
     */
    fun addFile(file: File?) {
        addFile(-1, file)
    }

    var compressCount = 0
        private set
    var isCompress = false
        private set

    /**
     * 添加 文件 并上传
     *
     * @param index
     * @param file
     */
    fun addFile(index: Int, file: File?) {
        if (null == file) {
            return
        }
        if (photos.size >= maxSize) {
            return  //不能继续添加
        }
        val srcFilePath = file.absolutePath
        compressCount++
        Luban.compress(context, file).putGear(Luban.THIRD_GEAR).setMaxSize(10 * 1024)
            .launch(object : OnCompressListener {
                override fun onStart() {
                    isCompress = true
                }

                override fun onSuccess(file: File) {
                    val photoFile = PhotoFile(srcFilePath, file)
                    if (index >= 0) {
                        photos.add(index, photoFile)
                    } else {
                        photos.add(photoFile)
                    }
                    if (instantUpload) {
                        uploadImage(photoFile)
                    }
                    compressCount--
                    isCompress = false
                }

                override fun onError(e: Throwable) {
                    SCToastUtil.showToast(context, "压缩失败", 2)
                }
            })
    }

    /**
     * 添加文件 并立即上传
     *
     * @param index
     * @param file
     */
    fun addFileUpload(index: Int, file: File?) {
        if (null == file) {
            return
        }
        val srcFilePath = file.absolutePath
        compressCount = 0
        Luban.compress(context, file).putGear(Luban.CUSTOM_GEAR).setMaxSize(10 * 1024)
            .launch(object : OnCompressListener {
                override fun onStart() {
                    isCompress = true
                }

                override fun onSuccess(file: File) {
                    val photoFile = PhotoFile(srcFilePath, file)
                    if (index >= 0) {
                        photos.add(index, photoFile)
                    } else {
                        photos.add(photoFile)
                    }
                    isCompress = false
                    uploadImage(photoFile)
                }

                override fun onError(e: Throwable) {}
            })
    }

    fun addFiles(filePaths: List<String>, compressOver: CompressOver) {
        if (filePaths.isEmpty()) {
            return
        }
        if (photos.size + filePaths.size >= maxSize) {
            return  //不能继续添加
        }
        compressOverBack = compressOver
        compressCount += filePaths.size
        val files: MutableList<File> = ArrayList()
        for (i in filePaths.indices) {
            files.add(File(filePaths[i]))
        }

        Luban.compress(context, files).putGear(Luban.CUSTOM_GEAR).setMaxSize(10 * 1024)
            .launch(object : OnMultiCompressListener {
                override fun onStart() {
                    isCompress = true
                }

                override fun onSuccess(fileList: List<File>) {
                    for (i in fileList.indices) {
                        val photoFile = PhotoFile(files[i].absolutePath, fileList[i])
                        photos.add(photoFile)
                        if (instantUpload) {
                            uploadImage(photoFile)
                        }
                        compressCount--
                        isCompress = false
                        if (i == fileList.size - 1) {
                            val ra = Runnable {
                                SystemClock.sleep(100L)
                                context.runOnUiThread {
                                    if (compressOverBack != null) {
                                        compressOverBack!!.success()
                                    }
                                }
                            }
                            BaseApp.fixedThreadPool.execute(ra)
                        }
                    }
                }

                override fun onError(e: Throwable) {
                    SCToastUtil.showToast(context, "压缩失败", 2)
                }
            })
    }

    private var compressOverBack: CompressOver? = null

    interface CompressOver {
        fun success()
    }

    fun addFiles(filePaths: List<String?>?) {
        if (null == filePaths || filePaths.isEmpty()) {
            return
        }
        if (photos.size + filePaths.size >= maxSize) {
            return  //不能继续添加
        }
        compressCount += filePaths.size
        val files: MutableList<File> = ArrayList()
        for (i in filePaths.indices) {
            files.add(File(filePaths[i]))
        }
        Luban.compress(context, files).putGear(Luban.CUSTOM_GEAR).setMaxSize(10 * 1024)
            .launch(object : OnMultiCompressListener {
                override fun onStart() {
                    isCompress = true
                }

                override fun onSuccess(fileList: List<File>) {
                    for (i in fileList.indices) {
                        val photoFile = PhotoFile(files[i].absolutePath, fileList[i])
                        photos.add(photoFile)
                        if (instantUpload) {
                            uploadImage(photoFile)
                        }
                        compressCount--
                        isCompress = false
                    }
                }

                override fun onError(e: Throwable) {
                    SCToastUtil.showToast(context, "压缩失败", 2)
                }
            })
    }

    /**
     * move 图片可以移动到某个位置
     *
     * @param index
     * @param file
     * @param isCopy
     * @return
     */
    fun moveFile(index: Int, file: File, isCopy: Boolean): PhotoManager {
        for (i in photos.indices) {
            if (photos[i].filePath.equals(file.absolutePath)) {
                photos.add(index, photos[i])
                if (!isCopy) { //如果不是复制，需要删掉原来位置上的信息
                    photos.removeAt(i)
                }
            }
        }
        return this
    }

    /**
     * 删除文件
     *
     * @param file
     * @return
     */
    fun deleteFile(file: File?): PhotoManager {
        if (null == file) {
            return this
        }
        if (photos.size <= 0) {
            return this //不能删除了
            //throw new Exception("不能删除了");
        }
        for (i in photos.indices) {
            if (photos[i].filePath.equals(file.absolutePath)) {
                photos.removeAt(i)
            }
        }
        return this
    }

    /**
     * 删除文件
     *
     * @param file
     */
    fun deleteSrcFile(file: String?) {
        if (null == file) {
            return
        }
        if (photos.size <= 0) {
            return  //不能删除了
            //throw new Exception("不能删除了");
        }
        for (i in photos.indices) {
            if (photos[i].srcFilePath.equals(file)) {
                if (photos[i].photoeFile != null) photos[i].photoeFile?.deleteOnExit()
                photos.removeAt(i)
            }
        }
        statisticsUploadStatus()
    }

    fun deleteAllFile() {
        for (file in photos) {
            if (file.photoeFile != null) file.photoeFile?.deleteOnExit()
        }
        photos.clear()
        statisticsUploadStatus()
    }

    /**
     * 拼接 全部的 webUrl
     *
     * @param separator 分隔符
     * @return
     */
    fun jointWebUrl(separator: String): String {
        var allWebUrl = StringBuilder()
        for (i in photos.indices) {
            val photoFile = photos[i]
            if (photoFile.uploadStatus == 3) {
                allWebUrl.append("").append(photoFile.webUrl).append(separator)
            }
        }
        if (allWebUrl.length > 1) {
            allWebUrl = StringBuilder(allWebUrl.substring(0, allWebUrl.length - separator.length))
        }
        return allWebUrl.toString()
    }
    //上传之前检查一次，图片上传的情况。(未成功的再上传一次,还不成功。返回出去。) 可根据方法自己写
    /**
     * 统计状态
     */
    @Synchronized
    fun statisticsUploadStatus() {
        ////1 未上传  2.正在上传  3.上传成功  4.上传失败
        clearSize()
        for (i in photos.indices) {
            val photoFile = photos[i]
            when (photoFile.uploadStatus) {
                1 -> unUploadSize++
                2 -> currentUploadSize++
                3 -> successSize++
                4 -> failSize++
                else -> {
                }
            }
        }
    }

    private fun clearSize() {
        failSize = 0
        successSize = 0
        unUploadSize = 0
        currentUploadSize = 0
    }

    val photoFiles: List<PhotoFile>
        get() = photos

    /**
     * 得到状态
     *
     * @param uploadStatus
     * @return
     */
    fun getPhotoUploadStatus(uploadStatus: Int): Int {
        when (uploadStatus) {
            1 -> return unUploadSize
            2 -> return currentUploadSize
            3 -> return successSize
            4 -> return failSize
            else -> {
            }
        }
        return 0
    }

    /**
     * 失败的重新上传。
     */
    fun reUploadByFail() {
        for (i in photos.indices) {
            val photoFile = photos[i]
            if (photoFile.uploadStatus == 4) {
                reUploadCount++
                uploadImage(photoFile)
            }
        }
    }

    /**
     * 所有非成功的，强制重新上传
     */
    fun reUploadByUnSuccess() {
        if (isCompress) {
            SCToastUtil.showToast(context, "正在压缩图片...", 2)
            return
        }
        for (i in photos.indices) {
            val photoFile = photos[i]
            if (photoFile.uploadStatus != 3) {
                uploadImage(photoFile)
            }
        }
        if (getPhotoUploadStatus(3) == photos.size) {
            listener.onSuccess()
        }
    }

    /**
     * 聊天图片
     */
    fun isChat(isChat: Boolean): PhotoManager {
        this.isChat = isChat
        return this
    }

    /**
     * 自动上传
     */
    fun isGPU(isGPU: Boolean): PhotoManager {
        this.isGPU = isGPU
        return this
    }

    fun uploadImage(photoFile: PhotoFile) {
        photoFile.uploadStatus = 2
        val file: File? = photoFile.photoeFile
        val requestFile: RequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file!!)
        val body: MultipartBody.Part =
            MultipartBody.Part.createFormData("file", file.name, requestFile)
        mainDataSource.enqueue(
            {
                uploadImages(
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "2"),
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "1"),
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), file.name),
                    RequestBody.create("multipart/form-data".toMediaTypeOrNull(), "image/jpeg"),
                    body
                )
            }, false, "", if (isChat) MineApp.CHAT_URL else BaseApp.imageUrl
        ) {
            onSuccess {
                photoFile.webUrl = it.url
                photoFile.uploadStatus = 3
                photoFile.photoeFile!!.deleteOnExit()
                statisticsUploadStatus()
                if (getPhotoUploadStatus(3) == photos.size) {
                    listener.onSuccess()
                }
            }
            onFailed {
                photoFile.uploadStatus = 4
                statisticsUploadStatus()
                if (reUploadCount <= maxReUpload)
                    reUploadByFail()
                else listener.onError(photoFile, it.errorMessage)
            }
        }
    }

    fun interface OnUpLoadImageListener {
        fun onSuccess()
        fun onError(file: PhotoFile?, errorMsg: String) {}
    }
}