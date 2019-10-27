package com.bugtsa.camerafilters.presentation.media

import android.net.Uri
import java.io.File

class TakePhotoFlowDataHolder {
    var sourceUri: Uri? = null
    var destinationUri: Uri? = null
    var sourcePhotoTempFile: File? = null
        set(value) {
            field?.delete()
            field = value
        }
    var filteredPhotoTempFile: File? = null
        set(value) {
            field?.delete()
            field = value
        }

    fun clear() {
        sourceUri = null
        destinationUri = null
        sourcePhotoTempFile = null
        filteredPhotoTempFile = null
    }
}

object TakePhotoFlowScopeType