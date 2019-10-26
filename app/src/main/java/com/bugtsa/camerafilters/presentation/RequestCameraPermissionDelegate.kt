package com.bugtsa.camerafilters.presentation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.bugtsa.camerafilters.global.Constants.ReqCodes.CAMERA_PERMISSION_CODE

class RequestCameraPermissionDelegate {

    fun checkRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean =
        onRequestPermissionsGranted(requestCode, permissions, grantResults)

    fun hasCameraPermission(context: Context) = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    private fun onRequestPermissionsGranted(
        requestCode: Int, permissions: Array<out String>,
        grantResults: IntArray
    ): Boolean {
        val isRequestedPermission = requestCode == CAMERA_PERMISSION_CODE &&
                permissions.contains(Manifest.permission.CAMERA)

        val isPermissionsGranted = grantResults.size == 1 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED

        return (isRequestedPermission && isPermissionsGranted)
    }
}