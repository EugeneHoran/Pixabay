package com.exercise.eugene.pixabay.adapters;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exercise.eugene.pixabay.R;
import com.exercise.eugene.pixabay.model.Hit;
import com.exercise.eugene.pixabay.util.PixabayImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class PixabayAdapter extends RecyclerView.Adapter<PixabayAdapter.ViewHolder> {
    private final Activity host;
    private List<Hit> mHitList;
    private ColorDrawable[] shotLoadingPlaceholders;

    public PixabayAdapter(Activity hostActivity) {
        this.host = hostActivity;
        this.mHitList = new ArrayList<>();
        this.shotLoadingPlaceholders = new ColorDrawable[]{new ColorDrawable(ContextCompat.getColor(this.host, R.color.background_light))};
    }

    public void setHitList(List<Hit> hitList) {
        final int positionStart = mHitList.size() + 1;
        mHitList.addAll(hitList);
        notifyItemRangeInserted(positionStart, hitList.size());
    }

    public void setFreshList() {
        final int listSize = mHitList.size();
        mHitList.clear();
        notifyItemRangeRemoved(0, listSize);
    }

    @Override
    public PixabayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new PixabayAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_pixabay, parent, false));
    }

    @Override
    public void onBindViewHolder(PixabayAdapter.ViewHolder holder, int position) {
        holder.bindView();
        holder.mImage.requestLayout();
    }

    @Override
    public int getItemCount() {
        return mHitList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private PixabayImageView mImage;
        private TextView mTitle;

        private ViewHolder(View view) {
            super(view);
            mImage = view.findViewById(R.id.image);
            mTitle = view.findViewById(R.id.title);
        }

        private void bindView() {
            final Hit hit = mHitList.get(getAdapterPosition());
            mImage.setAspectRatio(hit.getWebImageFormatRatio());
            mTitle.setText(hit.getUser());
            Picasso.with(host)
                    .load(hit.getWebformatURL())
                    .placeholder(shotLoadingPlaceholders[0])
                    .resize(hit.getWebformatWidth(), hit.getWebformatHeight())
                    .priority(Picasso.Priority.HIGH)
                    .onlyScaleDown()
                    .into(mImage);
        }
    }
}