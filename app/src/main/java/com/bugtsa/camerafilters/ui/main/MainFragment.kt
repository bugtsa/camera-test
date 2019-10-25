package com.bugtsa.camerafilters.ui.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.global.Constants.ReqCodes.CAMERA_PERMISSION_CODE
import com.bugtsa.camerafilters.global.Constants.ReqCodes.REQUEST_PICK_PHOTO
import com.bugtsa.camerafilters.global.Constants.ReqCodes.REQUEST_TAKE_PHOTO
import com.bugtsa.camerafilters.presentation.media.TakePhotoIntentData
import com.bugtsa.camerafilters.presentation.media.TakePhotoViewModel
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment(R.layout.main_fragment) {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var takePhotoViewModel: TakePhotoViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        takePhotoViewModel = ViewModelProviders.of( this).get(TakePhotoViewModel::class.java)
        setClickListeners()
    }

    override fun onStart() {
        super.onStart()
        takePhotoViewModel.takePhotoEventLiveData().observe(viewLifecycleOwner, Observer(::takePhoto))
        takePhotoViewModel.observeRequestPermissions().observe(this, Observer {
            ActivityCompat.requestPermissions(
                    activity ?: throw RuntimeException("Fragment not attached error!"),
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE)
        })
//        observeErrors()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> takePhotoViewModel.takePhotoEventLiveData().value?.also {
                if (resultCode == Activity.RESULT_OK)
                    takePhotoViewModel.photoTaken(it.uri)
                else it.file.delete()
            }
            REQUEST_PICK_PHOTO -> if (resultCode == Activity.RESULT_OK)
                data?.data?.also { takePhotoViewModel.photoTaken(it) }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        takePhotoViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setClickListeners() {
        pick_from_gallery.setOnClickListener { viewModel.pickFromGallery() }
        take_from_camera.setOnClickListener { takePhotoViewModel.requestTakePhoto() }
    }

    private fun takePhoto(intentData: TakePhotoIntentData?) {
        intentData?.let {
            startActivityForResult(intentData.intent, REQUEST_TAKE_PHOTO)
        }
    }

//    private fun observeErrors() {
//        viewModel.observeErrorLiveData().observe(viewLifecycleOwner, Observer { error ->
//            error?.let { longToast(it) }
//        })
//    }
}
