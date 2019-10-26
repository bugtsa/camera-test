package com.bugtsa.camerafilters

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bugtsa.camerafilters.ui.BaseFragment
import com.bugtsa.camerafilters.ui.ChoosePhotoTypeFragment
import com.bugtsa.camerafilters.ui.FiltersListFragment
import com.bugtsa.camerafilters.ui.FiltersListFragmentListener
import com.zomato.photofilters.imageprocessors.Filter

class MainActivity : AppCompatActivity(),
    OpenFilterListFragmentListener,
    FiltersListFragmentListener {

    private val hostedFragment
        get() = supportFragmentManager.findFragmentById(R.id.fragment) as BaseFragment?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        loadLibrary()
        if (savedInstanceState == null) {
            openChoosePhotoScreen()
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

    override fun readyToOpenScreen() {
        openFilterListScreen()
    }

    override fun onFilterSelected(filter: Filter) {

    }

    private fun openChoosePhotoScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, ChoosePhotoTypeFragment.newInstance(this))
            .commitNow()
    }

    private fun openFilterListScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, FiltersListFragment.newInstance(this))
            .commitNow()
    }

    companion object {
        fun loadLibrary() {
            System.loadLibrary("NativeImageProcessor")
        }
    }
}

interface OpenFilterListFragmentListener {
    fun readyToOpenScreen()
}
