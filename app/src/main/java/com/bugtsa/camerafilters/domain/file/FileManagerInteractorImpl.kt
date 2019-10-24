package com.bugtsa.camerafilters.domain.file

import android.net.Uri
import com.bugtsa.camerafilters.data.file.FileManagerUtil
import io.reactivex.Single
import java.io.File

class FileManagerInteractorImpl(
        private val fileManagerUtil: FileManagerUtil
) : FileManagerInteractor {

    override fun generateTempPhotoFile(): Single<File> = fileManagerUtil.generatePhotoTempFile()

    override fun generateUriForFile(file: File): Single<Uri> = fileManagerUtil.generateUriForFile(file)
}
