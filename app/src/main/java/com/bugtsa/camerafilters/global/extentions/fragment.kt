package com.bugtsa.camerafilters.global.extentions

import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.global.Constants

@RequiresApi(Build.VERSION_CODES.KITKAT)
fun Fragment.pickPhoto(requestCode: Int) {
    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
    intent.type = Constants.PICK_IMAGE.DEFAULT_MIME
    intent.putExtra(Intent.EXTRA_MIME_TYPES, Constants.PICK_IMAGE.IMAGE_MIMES)
    intent.addCategory(Intent.CATEGORY_OPENABLE)
    intent.resolveActivity(requireContext().packageManager)?.let {
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.pick_photo_select_app)),
            requestCode
        )
    }
}