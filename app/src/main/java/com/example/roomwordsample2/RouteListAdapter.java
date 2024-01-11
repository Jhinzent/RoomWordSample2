package com.example.roomwordsample2;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class RouteListAdapter extends ListAdapter<Route, RouteViewHolder> {

    private OnRouteClickListener onRouteClickListener;

    // Setter-Methode für den Klicklistener
    public void setOnRouteClickListener(OnRouteClickListener onRouteClickListener) {
        this.onRouteClickListener = onRouteClickListener;
    }

    public RouteListAdapter(@NonNull DiffUtil.ItemCallback<Route> diffCallback) {
        super(diffCallback);
    }

    // Interface für den Klicklistener
    public interface OnRouteClickListener {
        void onRouteClick(int routeId);
    }

    @Override
    public RouteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final RouteViewHolder viewHolder = RouteViewHolder.create(parent);

        // Hier wird der Klicklistener aufgerufen, wenn ein Element in der RecyclerView angeklickt wird
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getAdapterPosition();
                if (onRouteClickListener != null && position != RecyclerView.NO_POSITION) {
                    // changed from "onRouteClickListener.onRouteClick(getItem(position));" !!!!!!!!!!!!!!!!!!!
                    onRouteClickListener.onRouteClick(getItem(position).getRouteId());
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RouteViewHolder holder, int position) {
        Route current = getItem(position);
        holder.bind(String.valueOf(current.getRouteBezeichnung()));

        holder.itemView.setOnClickListener(view -> {
            if (onRouteClickListener != null) {
                onRouteClickListener.onRouteClick(current.getRouteId());
            }
        });
    }

    public static class RouteDiff extends DiffUtil.ItemCallback<Route>{

        @Override
        public boolean areItemsTheSame(@NonNull Route oldItem, @NonNull Route newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Route oldItem, @NonNull Route newItem) {
            if(oldItem.getRouteId() == newItem.getRouteId()) return true;
            else return false;
        }
    }
}