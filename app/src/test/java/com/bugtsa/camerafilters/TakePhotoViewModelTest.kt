package com.bugtsa.camerafilters

import android.app.Application
import android.net.Uri
import com.bugtsa.camerafilters.di.ScopedInstanceProvider
import com.bugtsa.camerafilters.domain.file.FileManagerInteractor
import com.bugtsa.camerafilters.presentation.media.TakePhotoFlowDataHolder
import com.bugtsa.camerafilters.presentation.media.TakePhotoViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import java.io.File


class TakePhotoViewModelTest {

    private val application: Application = mockk()
    private val fileManagerInteractor: FileManagerInteractor = mockk()
    private val scopeInstanceProvider: ScopedInstanceProvider<TakePhotoFlowDataHolder> = mockk()
    private lateinit var takePhotoFlowDataHolder: TakePhotoFlowDataHolder
    private lateinit var takePhotoViewModel: TakePhotoViewModel

    @Before
    fun setup() {
        takePhotoFlowDataHolder = TakePhotoFlowDataHolder()
        every { scopeInstanceProvider.provide() } returns takePhotoFlowDataHolder

        takePhotoViewModel =
            TakePhotoViewModel(application, fileManagerInteractor, scopeInstanceProvider)
    }

    @Test
    fun takePhoto() {
        mockkStatic(Uri::class)
        val builder = Uri.Builder()
        builder.path("//foo")
        builder.query("dsfa")
        builder.scheme("dfasdf")
        val uri = builder.build()
        val file: File = mockk()
        every { Uri.parse("http://test/path") } returns uri
        every { fileManagerInteractor.generateTempPhotoFile() } returns Single.just(file)

        takePhotoViewModel.photoTaken(sourcePhotoUri = uri)

        verify { fileManagerInteractor.generateTempPhotoFile() }
    }
}