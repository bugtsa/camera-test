package com.bugtsa.camerafilters.domain.file

import android.net.Uri
import io.reactivex.Single
import java.io.File

interface FileManagerInteractor {

    fun generateTempPhotoFile(): Single<File>

    fun generateUriForFile(file: File): Single<Uri>
}
