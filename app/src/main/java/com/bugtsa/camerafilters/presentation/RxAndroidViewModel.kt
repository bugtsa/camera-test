package com.bugtsa.camerafilters.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.bugtsa.camerafilters.global.ErrorHandler
import com.hadilq.liveevent.LiveEvent
import im.dlg.global.contracts.ErrorObservableOwner
import im.dlg.global.contracts.OnBackPressable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class RxAndroidViewModel(application: Application) : AndroidViewModel(application), OnBackPressable, ErrorObservableOwner {

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()
    private val errorLiveData = LiveEvent<String>()
    protected val keyboardVisibilityEvent = LiveEvent<Boolean>()

    fun addDispose(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    fun addDisposes(vararg disposables: Disposable) = disposables.forEach(::addDispose)

    fun unsubscribe() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        super.onCleared()
        unsubscribe()
    }

    override fun onBackPressed() {}

    override fun observeErrorLiveData() = errorLiveData

    fun observeKeyboardVisibilityEvent() = keyboardVisibilityEvent as LiveData<Boolean>

    protected open fun handleError(
            throwable: Throwable,
            showError: Boolean = true,
            defaultErrorMessage: String? = null
    ) {
        ErrorHandler.handle(throwable)
        if (showError) (throwable.message ?: defaultErrorMessage)?.let(errorLiveData::postValue)
    }
}