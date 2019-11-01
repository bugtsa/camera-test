package com.bugtsa.camerafilters.data.media

import android.app.Application
import android.content.Context
import android.os.Environment
import com.bugtsa.camerafilters.global.SchedulersProvider
import com.bumptech.glide.Glide
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class ExternalFilesProviderImpl(val application: Application) : ExternalFilesProvider {
    private var exportImage: File? = null

    override fun getImageFile(fileUri: String, forExport: Boolean): Single<File> =
        Single.fromCallable {
            val image = exportImage
                ?: File("${application.cacheDir.path}/export/image/${getShareImageFileName()}.${ExternalFilesProvider.EXT}").apply {
                    deleteOnExit()
                }

            exportImage = image

            if (image.exists()) {
                exportImage
            } else {
                getCachedFile(application, fileUri)
                    .let { if (forExport) it.copyTo(image, true) else it }
            }
        }

    override fun getExternalImageFile(): Single<File> {
        return Single.fromCallable {
            val fileName = getShareImageFileName()
            val filePath = getPublicAlbumStorageDir().path

            var copy = 1
            var file = File(filePath, "$fileName.${ExternalFilesProvider.EXT}")

            while (file.exists()) {
                file = File(filePath, "$fileName($copy).${ExternalFilesProvider.EXT}")
                copy++
            }

            file
        }
    }

    override fun getExternalDocumentFile(fileName: String): Single<File> {
        return Single.fromCallable {
            val filePath = getPublicDocumentsStorageDir().path

            File(filePath, fileName)
        }
    }

    override fun clearImageExport() {
        // Don't keep disposable pointer because file clearing
        // procedure not tied to any lifecycle
        Completable.fromCallable {
            exportImage?.delete()
        }
            .onErrorComplete()
            .doOnComplete { exportImage = null }
            .subscribeOn(SchedulersProvider.io())
            .subscribe()
    }

    private fun getPublicAlbumStorageDir(): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), GALLERY_NAME
        )

        if (file.mkdirs()) {
            Timber.i("New image album created")
        }

        return file
    }

    private fun getCachedFile(context: Context, imageUri: String): File = Glide.with(context)
        .asFile()
        .load(imageUri)
        .submit()
        .get()

    private fun getPublicDocumentsStorageDir(): File {
        val file = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
            GALLERY_NAME
        )

        if (file.mkdirs()) {
            Timber.i("New document album created")
        }

        return file
    }

    private fun getShareImageFileName(): String {
        TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())

        return "IMG_${TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) % DATE_OFFSET}"
    }

    companion object {
        const val GALLERY_NAME = "Bugtsa"
        const val DATE_OFFSET = 1000000L
    }
}