package com.bugtsa.camerafilters.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bugtsa.camerafilters.R
import com.bugtsa.camerafilters.global.BitmapUtils
import com.zomato.photofilters.FilterPack
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.utils.ThumbnailItem
import com.zomato.photofilters.utils.ThumbnailsManager
import kotlinx.android.synthetic.main.fragment_photo_filter.*

class FiltersListFragment : BaseFragment(R.layout.fragment_photo_filter),
    ThumbnailsAdapter.ThumbnailsAdapterListener {

    var mAdapter: ThumbnailsAdapter? = null

    var thumbnailItemList = arrayListOf<ThumbnailItem>()

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

        prepareThumbnail(null)
    }

    override fun onFilterSelected(filter: Filter?) {
        filter?.also {
            filterListener?.onFilterSelected(it)
        }
    }

    private fun prepareThumbnail(bitmap: Bitmap?) {
        val r = Runnable {
            val thumbImage: Bitmap?

            if (bitmap == null) {
                thumbImage =
                    BitmapUtils.getBitmapFromAssets(activity, IMAGE_NAME, 100, 100)
            } else {
                thumbImage = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
            }

            if (thumbImage == null)
                return@Runnable

            ThumbnailsManager.clearThumbs()
            thumbnailItemList.clear()

            // add normal bitmap first
            val thumbnailItem = ThumbnailItem()
            thumbnailItem.image = thumbImage
            thumbnailItem.filterName = getString(R.string.filter_normal)
            ThumbnailsManager.addThumb(thumbnailItem)

            val filters = FilterPack.getFilterPack(activity!!)

            for (filter in filters) {
                val tI = ThumbnailItem()
                tI.image = thumbImage
                tI.filter = filter
                tI.filterName = filter.name
                ThumbnailsManager.addThumb(tI)
            }

            thumbnailItemList.addAll(ThumbnailsManager.processThumbs(activity))

            activity!!.runOnUiThread { mAdapter?.notifyDataSetChanged() }
        }

        Thread(r).start()
    }

    companion object {
        private const val IMAGE_NAME = "dog.jpg"

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