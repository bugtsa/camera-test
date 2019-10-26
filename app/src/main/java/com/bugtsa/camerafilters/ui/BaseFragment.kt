package com.bugtsa.camerafilters.ui

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.ViewPropertyAnimator
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*

abstract class BaseFragment(layout: Int) : Fragment(layout) {

    private val compositeDisposable = CompositeDisposable()

    private val viewAnimators = Collections.newSetFromMap(
        WeakHashMap<ViewPropertyAnimator, Boolean>()
    )
    private val animators = Collections.newSetFromMap(
        WeakHashMap<Animator, Boolean>()
    )

    private var dialogProgress: AlertDialog? = null

    private var showCancelDialogProgress: Boolean = true
    private var cancelDialogProgress: AlertDialog? = null

    private var creationDialogDisposable: Disposable? = null

    protected lateinit var homeActivity: AppCompatActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onDestroy() {
        super.onDestroy()
        homeActivity.setSupportActionBar(null)
        compositeDisposable.clear()
        creationDialogDisposable?.dispose()
    }

    override fun onStop() {
        super.onStop()
        viewAnimators.forEach {
            it.cancel()
        }
        animators.forEach {
            it.cancel()
        }
    }

    fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun addToAnimators(animator: ViewPropertyAnimator?) {
        animator?.let {
            viewAnimators.add(it)
        }
    }

    fun addToAnimators(animator: Animator?) {
        animator?.let {
            animators.add(it)
        }
    }

    fun addDisposables(vararg disposables: Disposable) = disposables.forEach(::addDisposable)

    fun toast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            homeActivity = context
        }
    }

}