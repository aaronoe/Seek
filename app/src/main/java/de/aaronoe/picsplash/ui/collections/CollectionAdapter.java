package de.aaronoe.picsplash.ui.collections;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.aaronoe.picsplash.R;
import de.aaronoe.picsplash.data.model.collections.Collection;
import de.aaronoe.picsplash.util.DisplayUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

/**
 * Created by aaron on 05.06.17.
 *
 */

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.CollectionViewHolder> {

    private int itemHeight;
    private List<Collection> collectionList;
    private onCollectionClickListener clickListener;

    public CollectionAdapter(onCollectionClickListener clickListener) {
        this.clickListener = clickListener;
    }

    interface onCollectionClickListener {
        void onClickCollection(Collection collection, ImageView target);
    }

    public void setCollectionList(List<Collection> collectionList) {
        this.collectionList = collectionList;
        notifyDataSetChanged();
    }

    public void addMoreItemsToList(List<Collection> otherList) {
        int oldSize = collectionList.size();
        int newSize = otherList.size();
        collectionList.addAll(otherList);
        notifyItemRangeChanged(oldSize, newSize);
        Log.d(TAG, "addMoreItemsToList() called with: otherList = [" + otherList + "]");
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
    public CollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder = LayoutInflater.from(parent.getContext()).inflate(R.layout.collection_item, parent, false);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                itemHeight);

        viewHolder.setLayoutParams(params);

        return new CollectionViewHolder(viewHolder);
    }

    @Override
    public void onBindViewHolder(CollectionViewHolder holder, int position) {

        final Collection collection = collectionList.get(position);
        final Context context = holder.imageView.getContext();
        final ImageView targetView = holder.imageView;

        holder.captionNameTv.setText(collection.getTitle());
        holder.captionPhotoTv.setText(context.getString(R.string.number_of_photos, (int) collection.getTotalPhotos()));
        holder.authorNameTv.setText(collection.getUser().getName());

        Glide.with(context)
                .load(collection.getUser().getProfileImage().getMedium())
                .into(holder.authorImageView);

        int imageColor = Color.parseColor(collection.getCoverPhoto().getColor());
        int complementaryColor = getComplementaryColor(imageColor);
        holder.captionNameTv.setTextColor(complementaryColor);
        holder.captionPhotoTv.setTextColor(complementaryColor);
        holder.authorNameTv.setTextColor(complementaryColor);

        Glide.with(holder.itemView.getContext())
                .load(collection.getCoverPhoto().getUrls().getRegular())
                .asBitmap()
                .centerCrop()
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .placeholder(new ColorDrawable(Color.parseColor(collection.getCoverPhoto().getColor())))
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
        return collectionList == null ? 0 : collectionList.size();
    }

    public class CollectionViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.image_item_iv)
        ImageView imageView;
        @BindView(R.id.image_item_overlay)
        View imageOverlay;
        @BindView(R.id.caption_author_iv)
        CircleImageView authorImageView;
        @BindView(R.id.caption_author_tv)
        TextView authorNameTv;
        @BindView(R.id.caption_photos_tv)
        TextView captionPhotoTv;
        @BindView(R.id.caption_name_tv)
        TextView captionNameTv;

        // Test

        CollectionViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        public void setOverlayColor(@ColorInt int color) {
            imageOverlay.setBackgroundColor(color);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            clickListener.onClickCollection(collectionList.get(adapterPosition), imageView);
        }
    }

    private static int getComplementaryColor(int colorToInvert) {
        float[] hsv = new float[3];
        Color.RGBToHSV(Color.red(colorToInvert), Color.green(colorToInvert),
                Color.blue(colorToInvert), hsv);
        hsv[0] = (hsv[0] + 180) % 360;
        return Color.HSVToColor(hsv);
    }

}
