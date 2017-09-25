package com.exercise.eugene.pixabay.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.exercise.eugene.pixabay.R;

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<CategoryRecyclerAdapter.ViewHolder> {
    private String[] categoryList;
    private CategoryAdapterListener mCategoryAdapterListener;

    public interface CategoryAdapterListener {
        void onItemClicked(String categoryName);
    }

    public void setListener(CategoryAdapterListener categoryAdapterListener) {
        mCategoryAdapterListener = categoryAdapterListener;
    }

    public CategoryRecyclerAdapter(Activity host) {
        Resources res = host.getResources();
        categoryList = res.getStringArray(R.array.string_array_categories);
    }

    @Override
    public CategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new CategoryRecyclerAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_categories, parent, false));
    }

    @Override
    public void onBindViewHolder(CategoryRecyclerAdapter.ViewHolder holder, int position) {
        holder.bindView();
    }

    @Override
    public int getItemCount() {
        return categoryList.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTitle;

        private ViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.title);
            mTitle.setOnClickListener(this);
        }

        private void bindView() {
            mTitle.setText(categoryList[getAdapterPosition()]);
        }

        @Override
        public void onClick(View view) {
            if (view == mTitle) {
                if (mCategoryAdapterListener != null) {
                    mCategoryAdapterListener.onItemClicked(categoryList[getAdapterPosition()]);
                }
            }
        }
    }
}