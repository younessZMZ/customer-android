package com.example.bxlargeimageviewer.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.bxlargeimageviewer.R;
import com.example.bxlargeimageviewer.ViewHolder.PagerViewHolder;

import java.util.List;


public class ImageViewAdapter extends RecyclingPagerAdapter<PagerViewHolder> {

    private List<String> imageURIs;

    @Override
    public int getItemCount() {
        if (imageURIs != null)
            return imageURIs.size();
        else {
            return 0;
        }
    }

    @Override
    public void onBindViewHolder(PagerViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public PagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        PagerViewHolder holder = new PagerViewHolder(view, imageURIs);
        return holder;
    }

    public void addDataSet(List<String> imageURIs) {
        this.imageURIs = imageURIs;
    }
}
