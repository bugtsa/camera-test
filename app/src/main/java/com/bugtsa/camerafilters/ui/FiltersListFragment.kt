package com.bugtsa.camerafilters.ui

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.data.media.ExternalFilesProvider
import com.bugtsa.camerafilters.presentation.FilterPhotoViewModel
import com.bugtsa.camerafilters.presentation.ShowPhotoState
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import kotlinx.android.synthetic.main.fragment_photo_filter.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FiltersListFragment : BaseFragment(R.layout.fragment_photo_filter),
    ThumbnailsAdapter.ThumbnailsAdapterListener {

    private val filterPhotoViewModel by viewModel<FilterPhotoViewModel>()

    private var mAdapter: ThumbnailsAdapter? = null
    private var thumbnailItemList = arrayListOf<ThumbnailItem>()
    private lateinit var recyclerView: RecyclerView

    private var filterListener: FiltersListFragmentListener? = null
    private var sourceBitmap: Bitmap? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).setSupportActionBar(vToolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        vToolbar.navigationIcon = resources.getDrawable(R.drawable.ic_arrow_back_black_24dp)

        vShare.setOnClickListener { filterPhotoViewModel.shareClick() }

        showProgress()
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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        filterPhotoViewModel.getBitmap()
        observeShowPhotoStates()
        observeShareIntent()
    }

    override fun onFilterSelected(filter: Filter?) {
        filter?.also { selectedFilter ->
            sourceBitmap?.also { bitmap ->
                showProgress()
                filterPhotoViewModel.processFiltersList(selectedFilter, bitmap)
            }
        }
    }

    fun setListener(listener: FiltersListFragmentListener) {
        this.filterListener = listener
    }

    private fun observeShowPhotoStates() {
        filterPhotoViewModel.observeShowPhotoStates()
            .observe(viewLifecycleOwner, Observer { photoShowState ->
                when (photoShowState) {
                    is ShowPhotoState.SourceImagePhotoState -> {
                        image_preview.setImageURI(photoShowState.uri)
                        sourceBitmap =
                            MediaStore.Images.Media.getBitmap(
                                requireContext().contentResolver,
                                photoShowState.uri
                            )
                        sourceBitmap?.also { bitmap ->
                            GlobalScope.launch(Dispatchers.Main) {
                                filterPhotoViewModel.prepareThumbnail(bitmap)
                            }
                        }
                    }
                    is ShowPhotoState.FilteredImagePhotoState -> {
                        image_preview.setImageBitmap(photoShowState.bitmap)
                        hideProgress()
                    }
                    is ShowPhotoState.ClearFiltersListPhotoState -> {
                        thumbnailItemList.clear()
                    }
                    is ShowPhotoState.FiltersListPhotoState -> {
                        thumbnailItemList.addAll(photoShowState.filtersList)
                        mAdapter?.notifyDataSetChanged()
                        hideProgress()
                    }
                }
            })
    }

    private fun observeShareIntent() {
        filterPhotoViewModel.observeSendShareIntent().observe(this, Observer {
            var mimeType = MimeTypeMap.getSingleton()
                .getMimeTypeFromExtension(ExternalFilesProvider.EXT)
            if (mimeType == null) {
                mimeType = "*/*"
            }

            val intent = Intent(Intent.ACTION_SEND)
                .putExtra(Intent.EXTRA_STREAM, it)
                .setType(mimeType)

            startActivity(intent)
        })
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