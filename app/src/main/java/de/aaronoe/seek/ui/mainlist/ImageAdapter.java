package de.aaronoe.seek.ui.mainlist;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.seek.R;
import de.aaronoe.seek.data.model.photos.PhotosReply;
import de.aaronoe.seek.util.DisplayUtils;
import de.aaronoe.seek.util.PhotoDownloadUtils;

/**
 * Created by aaron on 30.05.17.
 *
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private List<PhotosReply> photosReplyList;
    private int itemHeight;
    private onImageClickListener clickListener;
    private SharedPreferences sharedPrefs;

    public ImageAdapter(onImageClickListener onImageClickListener, SharedPreferences sharedPrefs) {
        clickListener = onImageClickListener;
        this.sharedPrefs = sharedPrefs;
    }

    public void setPhotosReplyList(List<PhotosReply> photosReplyList) {
        this.photosReplyList = photosReplyList;
        notifyDataSetChanged();
    }

    public interface onImageClickListener {
        void onClickImage(PhotosReply photo, ImageView target);
    }

    public void addMoreItemsToList(List<PhotosReply> otherList) {
        int oldSize = photosReplyList.size();
        int newSize = otherList.size();
        photosReplyList.addAll(otherList);
        notifyItemRangeChanged(oldSize, newSize);
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
        final PhotosReply photo = photosReplyList.get(position);

        final Context context = holder.imageView.getContext();
        final ImageView targetView = holder.imageView;

        String photoUrl = PhotoDownloadUtils.Companion.getPhotoLinkForQuality(photo,
                sharedPrefs.getString(context.getString(R.string.pref_key_display_quality),
                        context.getString(R.string.quality_regular_const)));

        Glide.with(holder.itemView.getContext())
                .load(photoUrl)
                .asBitmap()
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(new ColorDrawable(Color.parseColor(photo.getColor())))
                .into(new BitmapImageViewTarget(holder.imageView) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        // Do bitmap magic here
                        targetView.setImageBitmap(resource);
                        DisplayUtils.startSaturationAnimation(context, targetView, 2000);
                    }
                });
    }



    @Override
    public int getItemCount() {
        return photosReplyList == null ? 0 : photosReplyList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_item_iv)
        ImageView imageView;

        @BindView(R.id.image_item_overlay)
        View imageOverlay;

        ImageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            clickListener.onClickImage(photosReplyList.get(adapterPosition), imageView);
        }

        public void setOverlayColor(@ColorInt int color) {
            imageOverlay.setBackgroundColor(color);
        }
    }

}