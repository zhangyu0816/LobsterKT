package com.yimi.rentme.bean

class FileModel {
    var fileName = ""
    var image = ""
    var size = 0

    constructor()
    constructor(fileName: String, image: String, size: Int) {
        this.fileName = fileName
        this.image = image
        this.size = size
    }
}