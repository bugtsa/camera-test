package com.bugtsa.camerafilters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bugtsa.camerafilters.ui.BaseFragment
import com.bugtsa.camerafilters.ui.ChoosePhotoTypeFragment

class MainActivity : AppCompatActivity() {

    private val hostedFragment
        get() = supportFragmentManager.findFragmentById(R.id.fragment) as BaseFragment?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        loadLibrary()
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment, ChoosePhotoTypeFragment.newInstance())
                .commitNow()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        hostedFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    companion object {
        fun loadLibrary() {
            System.loadLibrary("NativeImageProcessor")
        }
    }
}
