package com.bugtsa.camerafilters.global

object Constants {

    object ReqCodes {
        const val REQUEST_TAKE_PHOTO = 6
        const val REQUEST_PICK_PHOTO = 7
        const val CAMERA_PERMISSION_CODE = 8
    }

    object PICK_IMAGE {
        const val DEFAULT_MIME = "*/*"
        val IMAGE_MIMES = arrayOf(
            "image/bmp",
            "image/png",
            "image/jpeg",
            "image/gif"
        )
    }

    object TAKE_PHOTO {
        const val KEY_SCOPE_ID = "take_photo_scope_id"
    }
}