package com.bugtsa.camerafilters.di

import com.bugtsa.camerafilters.global.Constants
import com.bugtsa.camerafilters.presentation.media.TakePhotoFlowScopeType
import org.koin.core.KoinComponent
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.core.scope.ScopeID

// Used to provide more convenient access to the koin scope
/** Koin [Scope] wrapper */
abstract class ScopeWrapper : KoinComponent {
    abstract val scopeID: ScopeID
    abstract val qualifier: Qualifier

    fun close() {
        getKoin().getScope(scopeID).close()
    }

    fun get(): Scope {
        return getKoin().getScope(scopeID)
    }

    fun getOrCreate(): Scope {
        return getKoin().getOrCreateScope(scopeID, qualifier)
    }

    fun create() {
        getKoin().createScope(scopeID, qualifier)
    }
}

class TakePhotoScopeWrapper : ScopeWrapper() {
    override val scopeID = Constants.TAKE_PHOTO.KEY_SCOPE_ID
    override val qualifier = named<TakePhotoFlowScopeType>()
}