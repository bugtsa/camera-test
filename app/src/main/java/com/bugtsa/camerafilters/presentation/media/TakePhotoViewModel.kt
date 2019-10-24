package com.bugtsa.camerafilters.presentation.media

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.global.ErrorHandler
import com.bugtsa.camerafilters.global.ErrorHandler.handle
import com.bugtsa.camerafilters.global.SchedulersProvider
import com.bugtsa.camerafilters.presentation.RequestCameraPermissionDelegate
import com.bugtsa.camerafilters.presentation.RxAndroidViewModel
import com.hadilq.liveevent.LiveEvent
import im.dlg.platform.R
import im.dlg.platform.di.ScopeHost
import im.dlg.platform.di.ScopedInstanceProvider
import im.dlg.platform.domain.file.FileManagerInteractor
import im.dlg.platform.presentation.RequestCameraPermissionDelegate
import im.dlg.platform.presentation.RxAndroidViewModel
import im.dlg.platform.ui.Views
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import java.io.File


open class TakePhotoData(open val file: File, open val uri: Uri)
data class TakePhotoIntentData(val intent: Intent, override val file: File, override val uri: Uri) : TakePhotoData(file, uri)

class TakePhotoViewModel(application: Application,
                         private val fileManagerInteractor: FileManagerInteractor
) : RxAndroidViewModel(application), KoinComponent{

    private val takeDataHolder = takePhotoProvider.provide()
    private val takePhotoEventLiveData = LiveEvent<TakePhotoIntentData>()
    private val requestPermissions = LiveEvent<Unit>()

    private val defaultErrorMessage = application.getString(R.string.error_undefined)

    private var sourcePhotoFile: File? = null
    private var croppedPhotoFile: File? = null
    private var cropSuccessDisposable: Disposable? = null
    private var cropErrorDisposable: Disposable? = null

    private val requestCameraPermissionDelegate = RequestCameraPermissionDelegate()

    fun takePhotoEventLiveData(): LiveData<TakePhotoIntentData> = takePhotoEventLiveData
    fun observeRequestPermissions(): LiveEvent<Unit> = requestPermissions

    fun requestTakePhoto() {
        requestTakePhoto(requestCameraPermissionDelegate.hasCameraPermission(getApplication()))
    }

    fun photoTaken(sourcePhotoUri: Uri, tempFile: File? = null) {
        sourcePhotoFile = tempFile
        fileManagerInteractor.generateTempPhotoFile()
                .flatMap { file ->
                    fileManagerInteractor.generateUriForFile(file).map { Pair(file, it) }
                }
                .subscribeOn(SchedulersProvider.io())
                .observeOn(SchedulersProvider.ui())
                .subscribe({ (file, destinationPhotoUri) ->
                    croppedPhotoFile = file
                    takeDataHolder.sourceUri = sourcePhotoUri
                    takeDataHolder.destinationUri = destinationPhotoUri
//                    startOtherScreen()
                }, ErrorHandler::handle)
                .also { addDispose(it) }
    }

    fun onRequestPermissionsResult(requestCode: Int,
                                   permissions: Array<out String>,
                                   grantResults: IntArray) {
        if (requestCameraPermissionDelegate.checkRequestPermissionsResult(
                        requestCode,
                        permissions,
                        grantResults)) {
            cameraPermissionGranted()
        }
    }

    private fun cameraPermissionGranted() {
        requestTakePhoto(true)
    }

    private fun requestTakePhoto(hasPermission: Boolean) {
        if (hasPermission) {
            getApplication<Application>().packageManager.apply {
                if (hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.resolveActivity(this)?.let {
                        fileManagerInteractor.generateTempPhotoFile()
                                .flatMap { file ->
                                    fileManagerInteractor.generateUriForFile(file).map { Pair(file, it) }
                                }.subscribeOn(SchedulersProvider.io())
                                .observeOn(SchedulersProvider.ui())
                                .subscribe({ (file, uri) ->
                                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                                    takePhotoEventLiveData.value = TakePhotoIntentData(intent, file, uri)
                                }, ErrorHandler::handle)
                                .also { addDispose(it) }
                    }
                }
            }
        } else {
            requestPermissions.postValue(Unit)
        }
    }
}