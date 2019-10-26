package com.bugtsa.camerafilters

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bugtsa.camerafilters.ui.BaseFragment
import com.bugtsa.camerafilters.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    private val hostedFragment
        get() = supportFragmentManager.findFragmentById(R.id.fragment) as BaseFragment?

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment, MainFragment.newInstance())
                    .commitNow()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        hostedFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}
