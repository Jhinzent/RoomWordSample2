/* package com.example.roomwordsample2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class PoiViewHolder extends RecyclerView.ViewHolder {

    private final TextView poiItemView;

    private PoiViewHolder(View itemView) {
        super(itemView);
        poiItemView = itemView.findViewById(R.id.textView);
    }

    public void bind(String text) {
        poiItemView.setText(text);
    }

    static PoiViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new PoiViewHolder(view);
    }
}


 */