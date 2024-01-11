package com.example.roomwordsample2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RouteViewHolder extends RecyclerView.ViewHolder {

    private final TextView routeItemView;

    private RouteViewHolder(View itemView) {
        super(itemView);
        routeItemView = itemView.findViewById(R.id.textView);
    }

    public void bind(String text) {
        routeItemView.setText(text);
    }

    static RouteViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new RouteViewHolder(view);
    }
}
