package com.bugtsa.camerafilters.data.file

import android.app.Application
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import io.reactivex.Single
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FileManagerRepositoryImpl(private val application: Application) : FileManagerRepository {
    companion object {
        private const val FILE_PREFIX = "Bugtsa_"
        private const val FILE_JPG_SUFFIX = ".jpg"
        private val DATE_TIME_FORMAT = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
    }

    override fun generatePhotoTempFile(): Single<File> = Single.fromCallable {
        createTempFile(
            FILE_PREFIX + DATE_TIME_FORMAT.format(Date()),
            FILE_JPG_SUFFIX,
            application.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        )
    }

    override fun generateUriForFile(file: File): Single<Uri> = Single.fromCallable {
        FileProvider.getUriForFile(
            application,
            application.packageName + ".provider",
            file
        )
    }

    override fun notifyGalleryAboutNewImage(filePath: String, ext: String) {
        MediaScannerConnection.scanFile(
            application,
            arrayOf(filePath),
            arrayOf(
                MimeTypeMap.getSingleton()
                    .getMimeTypeFromExtension(ext)
            ),
            null
        )
    }
}
