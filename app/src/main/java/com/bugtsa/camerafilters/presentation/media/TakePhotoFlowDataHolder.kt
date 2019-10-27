package com.bugtsa.camerafilters.presentation.media

import java.io.File

class TakePhotoFlowDataHolder {
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
        sourcePhotoTempFile = null
        filteredPhotoTempFile = null
    }
}

object TakePhotoFlowScopeType