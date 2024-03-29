package com.bugtsa.camerafilters.presentation.media

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.di.ScopeHost
import com.bugtsa.camerafilters.di.ScopedInstanceProvider
import com.bugtsa.camerafilters.domain.file.FileManagerInteractor
import com.bugtsa.camerafilters.global.ErrorHandler
import com.bugtsa.camerafilters.global.SchedulersProvider
import com.bugtsa.camerafilters.presentation.RequestCameraPermissionDelegate
import com.bugtsa.camerafilters.presentation.RxAndroidViewModel
import com.hadilq.liveevent.LiveEvent
import io.reactivex.disposables.Disposable
import org.koin.core.KoinComponent
import java.io.File


open class TakePhotoData(open val file: File, open val uri: Uri)
data class TakePhotoIntentData(val intent: Intent, override val file: File, override val uri: Uri) :
    TakePhotoData(file, uri)

class TakePhotoViewModel(
    application: Application,
    private val fileManagerInteractor: FileManagerInteractor,
    private val takePhotoProvider: ScopedInstanceProvider<TakePhotoFlowDataHolder>
) : RxAndroidViewModel(application),
    ScopeHost<TakePhotoFlowDataHolder> by ScopeHost.Delegate(takePhotoProvider) {

    private val takeDataHolder = takePhotoProvider.provide()
    private val takePhotoEventLiveData = LiveEvent<TakePhotoIntentData>()
    private val requestPermissions = LiveEvent<Unit>()
    private val startFilterScreeLiveData = LiveEvent<Unit>()

    private var sourcePhotoFile: File? = null
    private var filteredPhotoFile: File? = null

    private val requestCameraPermissionDelegate = RequestCameraPermissionDelegate()

    fun takePhotoEventLiveData(): LiveData<TakePhotoIntentData> = takePhotoEventLiveData
    fun observeRequestPermissions(): LiveEvent<Unit> = requestPermissions
    fun observeStartFilterScreen(): LiveEvent<Unit> = startFilterScreeLiveData

    fun requestTakePhoto() {
        requestTakePhoto(requestCameraPermissionDelegate.hasCameraPermission(getApplication()))
    }

    fun photoTaken(sourcePhotoUri: Uri, tempFile: File? = null) {
        sourcePhotoFile = tempFile
        takeDataHolder.sourceUri = sourcePhotoUri
        takeDataHolder.sourcePhotoTempFile = tempFile
        fileManagerInteractor.generateTempPhotoFile()
            .flatMap { file ->
                fileManagerInteractor.generateUriForFile(file).map { Pair(file, it) }
            }
            .subscribeOn(SchedulersProvider.io())
            .observeOn(SchedulersProvider.ui())
            .subscribe({ (file, photoUri) ->
                filteredPhotoFile = file
                takeDataHolder.destinationUri = photoUri
                takeDataHolder.filteredPhotoTempFile = file
                startFilterScreeLiveData.postValue(Unit)
            }, ErrorHandler::handle)
            .also { addDispose(it) }
    }

    fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCameraPermissionDelegate.checkRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
            )
        ) {
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
                                fileManagerInteractor.generateUriForFile(file)
                                    .map { Pair(file, it) }
                            }.subscribeOn(SchedulersProvider.io())
                            .observeOn(SchedulersProvider.ui())
                            .subscribe({ (file, uri) ->
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                                takePhotoEventLiveData.value =
                                    TakePhotoIntentData(intent, file, uri)
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