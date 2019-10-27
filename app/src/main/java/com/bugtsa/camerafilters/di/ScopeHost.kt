package com.bugtsa.camerafilters.di

// Main purpose of the interface is to keep models independent of Koin framework
/** This interface allows to DI subscribe on object destroying and perform scope closing */
interface ScopeHost<T> : ScopeHolder<T> {
    var closeObserver: (() -> Unit)?

    fun scopeClosed()
    fun subscribeOnCleared(closeObserver: () -> Unit)

    class Delegate<T>(override val provider: ScopedInstanceProvider<T>) : ScopeHost<T> {
        override var closeObserver: (() -> Unit)? = null

        override fun scopeClosed() {
            closeObserver?.invoke()
        }

        override fun subscribeOnCleared(closeObserver: () -> Unit) {
            this.closeObserver = closeObserver
        }
    }
}
