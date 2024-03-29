package com.bugtsa.camerafilters.ui

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import com.bugtsa.camerafilters.OpenFilterListFragmentListener
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.global.Constants.ReqCodes.CAMERA_PERMISSION_CODE
import com.bugtsa.camerafilters.global.Constants.ReqCodes.REQUEST_PICK_PHOTO
import com.bugtsa.camerafilters.global.Constants.ReqCodes.REQUEST_TAKE_PHOTO
import com.bugtsa.camerafilters.global.extentions.pickPhoto
import com.bugtsa.camerafilters.presentation.media.TakePhotoIntentData
import com.bugtsa.camerafilters.presentation.media.TakePhotoViewModel
import kotlinx.android.synthetic.main.fragment_choose_photo_type.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChoosePhotoTypeFragment : BaseFragment(R.layout.fragment_choose_photo_type) {

    private lateinit var openFilterFragmentListener: OpenFilterListFragmentListener
    private val takePhotoViewModel by viewModel<TakePhotoViewModel>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setClickListeners()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        takePhotoViewModel.takePhotoEventLiveData()
            .observe(viewLifecycleOwner, Observer(::takePhoto))
        takePhotoViewModel.observeRequestPermissions().observe(this, Observer {
            ActivityCompat.requestPermissions(
                activity ?: throw RuntimeException("Fragment not attached error!"),
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        })
        takePhotoViewModel.observeStartFilterScreen()
            .observe(
                viewLifecycleOwner,
                Observer { openFilterFragmentListener.readyToOpenScreen() })
        observeErrors()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        takePhotoViewModel.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setClickListeners() {
        vPickFromGalleryButton.setOnClickListener { pickPhoto(REQUEST_PICK_PHOTO) }
        vTakeFromCameraButton.setOnClickListener { takePhotoViewModel.requestTakePhoto() }
    }

    private fun setOpenFilterFragmentListener(openFilterListFragmentListener: OpenFilterListFragmentListener) {
        this.openFilterFragmentListener = openFilterListFragmentListener
    }

    private fun takePhoto(intentData: TakePhotoIntentData?) {
        intentData?.let {
            startActivityForResult(intentData.intent, REQUEST_TAKE_PHOTO)
        }
    }

    private fun observeErrors() {
        takePhotoViewModel.observeErrorLiveData().observe(viewLifecycleOwner, Observer { error ->
            error?.let { longToast(it) }
        })
    }

    companion object {
        fun newInstance(openFilterListFragmentListener: OpenFilterListFragmentListener): ChoosePhotoTypeFragment {
            val choosePhotoTypeFragment = ChoosePhotoTypeFragment()
            choosePhotoTypeFragment.setOpenFilterFragmentListener(openFilterListFragmentListener)
            return choosePhotoTypeFragment

        }
    }
}
