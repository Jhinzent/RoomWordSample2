/* package com.example.roomwordsample2;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

public class PoiListAdapter extends ListAdapter<Poi, PoiViewHolder> {

    public PoiListAdapter(@NonNull DiffUtil.ItemCallback<Poi> diffCallback) {
        super(diffCallback);
    }

    @Override
    public PoiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return PoiViewHolder.create(parent);
    }


    @Override
    public void onBindViewHolder(PoiViewHolder holder, int position) {
        Poi current = getItem(position);
        holder.bind(String.valueOf(current.getOrt()));
    }

    public static class PoiDiff extends DiffUtil.ItemCallback<Poi>{

        @Override
        public boolean areItemsTheSame(@NonNull Poi oldItem, @NonNull Poi newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Poi oldItem, @NonNull Poi newItem) {
            if(oldItem.getPoiId() == newItem.getPoiId()) return true;
            else return false;
        }
    }
}

 */