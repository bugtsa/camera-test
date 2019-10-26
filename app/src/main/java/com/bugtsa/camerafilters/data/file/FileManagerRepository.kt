package com.bugtsa.camerafilters.data.file

import android.net.Uri
import io.reactivex.Single
import java.io.File

interface FileManagerRepository {
    fun generatePhotoTempFile(): Single<File>
    fun generateUriForFile(file: File): Single<Uri>
    fun notifyGalleryAboutNewImage(filePath: String, ext: String)
}