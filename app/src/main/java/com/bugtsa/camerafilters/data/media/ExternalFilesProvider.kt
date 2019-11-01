package com.bugtsa.camerafilters.data.media

import io.reactivex.Single
import java.io.File

interface ExternalFilesProvider {
    /** Provides image source file
     * if [forExport] is true method returns file what FileProvider allowed to share.
     * For optimization set [forExport] to false */
    fun getImageFile(fileUri: String, forExport: Boolean = false): Single<File>

    /** Provides unique image file from external storage.*/
    fun getExternalImageFile(): Single<File>

    /** Clear current export image file */
    fun clearImageExport()

    /** Provides the file in which the documents will be saved. */
    fun getExternalDocumentFile(fileName: String): Single<File>

    companion object {
        /** All images file extension. (From server images comes in jpeg format) */
        const val EXT = "jpg"
    }
}
