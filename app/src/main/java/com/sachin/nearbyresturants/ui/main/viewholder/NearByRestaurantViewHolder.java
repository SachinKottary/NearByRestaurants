package com.sachin.nearbyresturants.ui.main.viewholder;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sachin.nearbyresturants.databinding.ReastaurantListItemBinding;

public class NearByRestaurantViewHolder extends RecyclerView.ViewHolder {

    public ReastaurantListItemBinding itemBinding;

    public NearByRestaurantViewHolder(@NonNull ReastaurantListItemBinding itemView) {
        super(itemView.getRoot());
        itemBinding = itemView;
    }

}
