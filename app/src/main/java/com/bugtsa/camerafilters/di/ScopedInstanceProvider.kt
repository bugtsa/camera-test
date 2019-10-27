package com.bugtsa.camerafilters.di

/** Wrap your scoped instance into such provider and inject it into [ScopeHolder] instance */
interface ScopedInstanceProvider<T> {
    fun provide(vararg arguments: Any?): T
}