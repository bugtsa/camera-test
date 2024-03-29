package com.bugtsa.camerafilters.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bugtsa.camerafilters.R;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.MyViewHolder> {

    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailsAdapterListener listener;
    private Context mContext;
    private int selectedIndex = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private TextView filterName;

        private View row;

        public TextView getTitle() {
            if (filterName == null) {
                filterName = row.findViewById(R.id.vFilterName);
            }
            return filterName;
        }

        public ImageView getThumbnail() {
            if (thumbnail == null) {
                thumbnail = row.findViewById(R.id.vThumbnail);
            }
            return thumbnail;
        }

        public MyViewHolder(View view) {
            super(view);
            this.row = view;
        }
    }


    public ThumbnailsAdapter(Context context, List<ThumbnailItem> thumbnailItemList, ThumbnailsAdapterListener listener) {
        mContext = context;
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.thumbnail_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);

        holder.getThumbnail().setImageBitmap(thumbnailItem.image);

        holder.getThumbnail().setOnClickListener(view -> {
            listener.onFilterSelected(thumbnailItem.filter);
            selectedIndex = position;
            notifyDataSetChanged();
        });

        holder.getTitle().setText(thumbnailItem.filterName);

        if (selectedIndex == position) {
            holder.getTitle().setTextColor(ContextCompat.getColor(mContext, R.color.colorTextPrimary));
        } else {
            holder.getTitle().setTextColor(ContextCompat.getColor(mContext, R.color.colorTextSecondary));
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public interface ThumbnailsAdapterListener {
        void onFilterSelected(Filter filter);
    }

}
