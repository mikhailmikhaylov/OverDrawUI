package org.redblaq.overdrawui.ui.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.redblaq.overdrawui.R;
import org.redblaq.overdrawui.model.ImagePreview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ImagePreviewViewHolder> {

    private List<ImagePreview> imagePreviews;

    public ImagePreviewAdapter() {

    }

    @Override
    public ImagePreviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_image_preview, parent, false);
        return new ImagePreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ImagePreviewViewHolder holder, int position) {
        ImagePreview imagePreview = getItem(position);
        if (imagePreview != null) {
            holder.bind(imagePreview);
        }
    }

    public void setItems(List<ImagePreview> items) {
        addItems(items, true);
    }

    public void addItems(List<ImagePreview> items) {
        addItems(items, false);
    }

    private void addItems(List<ImagePreview> items, boolean clearDestBefore) {
        if (imagePreviews == null) {
            imagePreviews = new ArrayList<>(items);
        } else {
            if (clearDestBefore) {
                imagePreviews.clear();
            }
            imagePreviews.addAll(items);
        }
        notifyDataSetChanged();
    }

    @Nullable
    public ImagePreview getItem(int index) {
        if (imagePreviews != null) {
            return imagePreviews.get(index);
        }
        return null;
    }

    @Override
    public int getItemCount() {
        return imagePreviews == null ? 0 : imagePreviews.size();
    }

    static class ImagePreviewViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image_preview)
        ImageView imagePreview;

        public ImagePreviewViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ImagePreview imagePreview) {
            Picasso.with(itemView.getContext())
                    .load(imagePreview.getUri())
                    .into(this.imagePreview);
        }
    }
}
