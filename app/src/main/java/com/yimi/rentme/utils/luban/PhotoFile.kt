package com.yimi.rentme.utils.luban

import java.io.File

class PhotoFile {
    var fileHashCode: String? = ""//哈希值
    var fileSize: Long = 0//大小
    var filePath: String? = ""//路径
    var srcFilePath: String? = ""//原图
    var photoeFile: File? = null//文件
    var uploadStatus = 0//上传状态  1 未上传  2.正在上传  3.上传成功  4.上传失败
    var webUrl: String? = ""//网络url

    constructor() : super() {}
    constructor(srcFilePath: String?, file: File) {
        this.srcFilePath = srcFilePath
        fileSize = file.length()
        fileHashCode = "" + file.hashCode()
        filePath = file.absolutePath
        photoeFile = file
        uploadStatus = 1
        webUrl = ""
    }

    constructor(url: String?) {
        srcFilePath = url
        fileSize = 0
        fileHashCode = ""
        filePath = url
        photoeFile = null
        uploadStatus = 3
        webUrl = url
    }
}