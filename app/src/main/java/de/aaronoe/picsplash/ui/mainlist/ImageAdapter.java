package de.aaronoe.picsplash.ui.mainlist;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.picsplash.R;
import de.aaronoe.picsplash.data.model.PhotosReply;

/**
 * Created by aaron on 30.05.17.
 *
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private static final String TAG = "ImageAdapter";
    private List<PhotosReply> photosReplyList;
    private int itemHeight;

    public ImageAdapter() {}

    public void setPhotosReplyList(List<PhotosReply> photosReplyList) {
        this.photosReplyList = photosReplyList;
        notifyDataSetChanged();
    }

    public void addMoreItemsToList(List<PhotosReply> otherList) {
        int oldSize = photosReplyList.size();
        int newSize = otherList.size();
        photosReplyList.addAll(otherList);
        notifyItemRangeChanged(oldSize, newSize);
        Log.d(TAG, "addMoreItemsToList() called with: otherList = [" + otherList + "]");
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                itemHeight);

        viewHolder.setLayoutParams(params);

        return new ImageViewHolder(viewHolder);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        Activity context = (Activity) recyclerView.getContext();
        Point windowDimensions = new Point();
        context.getWindowManager().getDefaultDisplay().getSize(windowDimensions);
        itemHeight = Math.round(windowDimensions.y * 0.6f);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        PhotosReply photo = photosReplyList.get(position);
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");
        Log.e(TAG, "onBindViewHolder: " + photo.getUrls().getRegular());
        Glide.with(holder.itemView.getContext())
                .load(photo.getUrls().getFull())
                .placeholder(new ColorDrawable(Color.parseColor(photo.getColor())))
                .crossFade()
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return photosReplyList == null ? 0 : photosReplyList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_item_iv)
        ImageView imageView;

        @BindView(R.id.image_item_overlay)
        View imageOverlay;

        ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setOverlayColor(@ColorInt int color) {
            imageOverlay.setBackgroundColor(color);
        }
    }

}
