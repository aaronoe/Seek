package de.aaronoe.seek.ui.collectiondetail;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import static android.content.ContentValues.TAG;

/**
 * Created by aaron on 07.06.17.
 *
 */

public class CollectionDetailAdapter extends RecyclerView.Adapter<CollectionDetailAdapter.CollectionPhotoViewHolder> {

    private List<PhotosReply> photoReplyList;
    private onImageClickListener clickListener;

    public CollectionDetailAdapter(onImageClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setImages(List<PhotosReply> photoReplyList) {
        this.photoReplyList = photoReplyList;
        notifyDataSetChanged();
    }

    public void addMoreItemsToList(List<PhotosReply> otherList) {
        int oldSize = photoReplyList.size();
        int newSize = otherList.size();
        photoReplyList.addAll(otherList);
        notifyItemRangeChanged(oldSize, newSize);
        Log.d(TAG, "addMoreItemsToList() called with: otherList = [" + otherList + "]");
    }

    interface onImageClickListener {
        void onClickImage(PhotosReply photo, ImageView target);
    }

    @Override
    public CollectionPhotoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item_collection, parent, false);
        Log.d(TAG, "onCreateViewHolder() called with: parent = [" + parent + "], viewType = [" + viewType + "]");
        return new CollectionPhotoViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CollectionPhotoViewHolder holder, int position) {
        PhotosReply photo = photoReplyList.get(position);
        final ImageView target = holder.imageView;
        final Context context = holder.itemView.getContext();
        Log.d(TAG, "onBindViewHolder() called with: holder = [" + holder + "], position = [" + position + "]");

        Glide.with(holder.itemView.getContext())
                .load(photo.getUrls().getRegular())
                .asBitmap()
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(new ColorDrawable(Color.parseColor(photo.getColor())))
                .into(new BitmapImageViewTarget(target) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        // Do bitmap magic here
                        target.setImageBitmap(resource);
                        DisplayUtils.startSaturationAnimation(context, target, 2000);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return photoReplyList == null ? 0 : photoReplyList.size();
    }

    class CollectionPhotoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_item_iv)
        ImageView imageView;

        CollectionPhotoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            clickListener.onClickImage(photoReplyList.get(getAdapterPosition()), imageView);
        }
    }

}
