package com.bugtsa.camerafilters.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.presentation.FilterPhotoViewModel
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.android.synthetic.main.fragment_photo_filter.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.viewmodel.ext.android.viewModel

class FiltersListFragment : BaseFragment(R.layout.fragment_photo_filter),
    ThumbnailsAdapter.ThumbnailsAdapterListener {

    var mAdapter: ThumbnailsAdapter? = null

    var thumbnailItemList = arrayListOf<ThumbnailItem>()

    private val filterPhotoVIewModel by viewModel<FilterPhotoViewModel>()

    private lateinit var recyclerView: RecyclerView

    private var filterListener: FiltersListFragmentListener? = null

    fun setListener(listener: FiltersListFragmentListener) {
        this.filterListener = listener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = vFilterList
        mAdapter = ThumbnailsAdapter(activity, thumbnailItemList, this)

        val mLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        vFilterList.layoutManager = mLayoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        val space = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 8f,
            resources.displayMetrics
        ).toInt()
        recyclerView.addItemDecoration(SpacesItemDecoration(space))
        recyclerView.adapter = mAdapter
    }

    private var sourceBitmap: Bitmap? = null
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        filterPhotoVIewModel.getBitmap()
        filterPhotoVIewModel.observeShowBitmap()
            .observe(viewLifecycleOwner, Observer { imageUri ->
                image_preview.setImageURI(imageUri)
                sourceBitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
                sourceBitmap?.also { bitmap ->
                    GlobalScope.launch(Dispatchers.Main) {
                        prepareThumbnail(bitmap)
                    }
                }
            })
    }


    override fun onFilterSelected(filter: Filter?) {
        filter?.also {
            val brightnessFinal = 0
            val saturationFinal = 1.0f
            val contrastFinal = 1.0f
            filter.addSubFilter(BrightnessSubFilter(brightnessFinal))
            filter.addSubFilter(ContrastSubFilter(contrastFinal))
            filter.addSubFilter(SaturationSubfilter(saturationFinal))

            GlobalScope.launch(Dispatchers.Main) {
                flowTest(filter)
            }
        }
    }

    private suspend fun flowTest(filter: Filter) {
        val flowFilter: Flow<Bitmap> = flow {
            val filteredImage = sourceBitmap?.copy(Bitmap.Config.ARGB_8888, true)
            emit(filter.processFilter(filteredImage))
        }
        val bitmap = flowFilter
            .flowOn(Dispatchers.Main)
            .single()

        withContext(Dispatchers.Main) {
            image_preview.setImageBitmap(bitmap)
        }
    }

    private suspend fun prepareThumbnail(bitmap: Bitmap) {
        val flowThumbManager : Flow<MutableList<ThumbnailItem>> = flow {

            val thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false)

            ThumbnailsManager.clearThumbs()
            thumbnailItemList.clear()

            // add normal bitmap first
            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImage
            thumbnailItem.filterName = getString(R.string.filter_normal)
            ThumbnailsManager.addThumb(thumbnailItem)

            val filters = FilterPack.getFilterPack(requireActivity())

            for (filter in filters) {
                val tI = ThumbnailItem()
                tI.image = thumbImage
                tI.filter = filter
                tI.filterName = filter.name
                ThumbnailsManager.addThumb(tI)
            }
            val thumbs = ThumbnailsManager.processThumbs(activity)
            emit(thumbs)
        }
        val result = flowThumbManager
            .flowOn(Dispatchers.Main)
            .single()

        thumbnailItemList.addAll(result)
        mAdapter?.notifyDataSetChanged()
    }

    companion object {
        fun newInstance(listener: FiltersListFragmentListener): FiltersListFragment {
            val filtersListFragment = FiltersListFragment()
            filtersListFragment.setListener(listener)
            return filtersListFragment
        }
    }
}

interface FiltersListFragmentListener {
    fun onFilterSelected(filter: Filter)
}