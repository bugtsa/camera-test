package com.bugtsa.camerafilters.global

import timber.log.Timber

object ErrorHandler {
    fun handle(throwable: Throwable) {
        Timber.e(throwable)
    }

    fun handleCallback(throwable: Throwable, callback: () -> Unit) {
        Timber.e(throwable)
        callback()
    }
}