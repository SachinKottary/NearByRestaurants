package com.sachin.nearbyresturants.ui.main.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.sachin.nearbyresturants.R;
import com.sachin.nearbyresturants.databinding.ReastaurantListItemBinding;
import com.sachin.nearbyresturants.network.dto.RestaurantDetail;
import com.sachin.nearbyresturants.ui.main.viewholder.NearByRestaurantViewHolder;

import java.util.ArrayList;
import java.util.List;

public class NearByRestaurantAdapter extends RecyclerView.Adapter<NearByRestaurantViewHolder> {

    private List<RestaurantDetail> restaurantDetailList = new ArrayList<>();

    @NonNull
    @Override
    public NearByRestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReastaurantListItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.reastaurant_list_item,
                        parent, false);
        return new NearByRestaurantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NearByRestaurantViewHolder holder, int position) {
        RestaurantDetail restaurantDetail = restaurantDetailList.get(position);
        holder.itemBinding.setRestaurantDetails(restaurantDetail);
    }

    @Override
    public int getItemCount() {
        return restaurantDetailList.size();
    }

    public void setRestaurantDetailList(List<RestaurantDetail> restaurantDetailList) {
        this.restaurantDetailList.addAll(restaurantDetailList);
    }

}

