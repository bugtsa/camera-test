package com.bugtsa.camerafilters.di


/** Inherit your class from that interface for ability to inject the scoped instance */
interface ScopeHolder<T> {
    val provider: ScopedInstanceProvider<T>
}