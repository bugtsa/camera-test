package com.bugtsa.camerafilters.di

import org.koin.core.module.Module

interface DependencyGraph {
    fun assemble(): List<Module>
}