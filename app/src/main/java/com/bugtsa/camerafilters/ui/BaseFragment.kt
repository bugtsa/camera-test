package com.bugtsa.camerafilters.ui

import android.animation.Animator
import android.content.Context
import android.os.Bundle
import android.view.ViewPropertyAnimator
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.R.dimen
import com.bugtsa.camerafilters.R.style
import com.bugtsa.camerafilters.global.SchedulersProvider
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

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

    protected open fun showProgress(
        cancelAction: (() -> Unit)? = null,
        cancelable: Boolean = true,
        delayToShow: Long = 0) {
        if (creationDialogDisposable?.isDisposed == false) return

        creationDialogDisposable = Completable.complete()
            .delay(delayToShow, TimeUnit.MILLISECONDS)
            .observeOn(SchedulersProvider.ui())
            .subscribe {
                context?.also { context ->
                    dialogProgress = AlertDialog.Builder(context, style.ProgressDialog)
                        .setOnCancelListener {
                            showCancelProgressDialog(cancelAction)
                        }
                        .setCancelable(cancelable)
                        .create()

                    dialogProgress?.show()
                    dialogProgress?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
                    dialogProgress?.window?.setLayout(
                        context.resources.getDimensionPixelSize(dimen.dialog_progress_load_size),
                        context.resources.getDimensionPixelSize(dimen.dialog_progress_load_size))
                    dialogProgress?.window?.setContentView(R.layout.view_progress_dialog)
                }
            }
    }

    protected fun hideProgress() {
        creationDialogDisposable?.dispose()
        dialogProgress?.dismiss()
        dialogProgress = null
    }

    protected fun processCancelDialogProgress(state: Boolean) {
        showCancelDialogProgress = state
        if (!showCancelDialogProgress) {
            cancelDialogProgress?.dismiss()
        }
    }

    protected fun hideCancelProgress() {
        cancelDialogProgress?.dismiss()
        cancelDialogProgress = null
    }

    protected open fun showCancelProgressDialog(cancelAction: (() -> Unit)? = null) {
        context?.also { context ->
            cancelDialogProgress = AlertDialog.Builder(context)
                .setTitle(R.string.load_data_dialog_title)
                .setMessage(R.string.load_data_dialog_content)
                .setPositiveButton(R.string.load_data_dialog_positive_button) { dialog, _ ->
                    dialog.dismiss()
                    showProgress(cancelAction)
                }
                .setNegativeButton(R.string.load_data_dialog_negative_button) { dialog, _ ->
                    hideProgress()
                    dialog.dismiss()
                    cancelAction?.invoke()
                }
                .setOnCancelListener {
                    showProgress(cancelAction)
                }
                .create()
            if (showCancelDialogProgress) cancelDialogProgress?.show()
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

    fun longToast(message: String?) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AppCompatActivity) {
            homeActivity = context
        }
    }

}