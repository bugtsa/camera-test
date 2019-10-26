package com.bugtsa.camerafilters.domain.file

import android.net.Uri
import com.bugtsa.camerafilters.data.file.FileManagerRepository
import io.reactivex.Single
import java.io.File

class FileManagerInteractorImpl(
    private val fileManagerRepository: FileManagerRepository
) : FileManagerInteractor {

    override fun generateTempPhotoFile(): Single<File> =
        fileManagerRepository.generatePhotoTempFile()

    override fun generateUriForFile(file: File): Single<Uri> =
        fileManagerRepository.generateUriForFile(file)
}
