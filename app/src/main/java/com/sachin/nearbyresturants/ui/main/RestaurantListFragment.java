package com.sachin.nearbyresturants.ui.main;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sachin.nearbyresturants.R;
import com.sachin.nearbyresturants.base.BaseFragment;
import com.sachin.nearbyresturants.databinding.MainFragmentBinding;
import com.sachin.nearbyresturants.rx.RxBus;
import com.sachin.nearbyresturants.ui.main.adapter.NearByRestaurantAdapter;
import com.sachin.nearbyresturants.widgets.EndlessRecyclerViewOnScrollListener;

public class RestaurantListFragment extends BaseFragment {

    private RestaurantViewModel mViewModel;
    private RecyclerView recyclerView;
    private MainFragmentBinding binding;
    private NearByRestaurantAdapter adapter;
    private ProgressBar progressBar;
    private TextView networkErrorText;

    public static RestaurantListFragment newInstance() {
        return new RestaurantListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, container, false);
        adapter = new NearByRestaurantAdapter();
        binding.restaurantList.setAdapter(adapter);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(getActivity()).get(RestaurantViewModel.class);
        binding.setRestaurantDetailViewModel(mViewModel);
        initLiveDataObservers();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view == null) return;
        recyclerView = view.findViewById(R.id.restaurant_list);
        progressBar = view.findViewById(R.id.progress_bar);
        networkErrorText = view.findViewById(R.id.network_error_text);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(manager);
        recyclerView.addOnScrollListener(new EndlessRecyclerViewOnScrollListener(manager) {
            @Override
            public void onLoadMore(int totalItemsCount, RecyclerView view) {
                mViewModel.loadNearByRestaurantDetails();
            }
        });
    }

    @Override
    public boolean handleNetworkState() {
        return true;
    }

    @Override
    public void onNetworkDisConnected() {
        if (adapter == null || adapter.getItemCount() <= 0) {
            networkErrorText.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNetworkConnected() {
        if (adapter != null && adapter.getItemCount() <= 0) {
            progressBar.setVisibility(View.VISIBLE);
            networkErrorText.setVisibility(View.GONE);
            mViewModel.loadNearByRestaurantDetails();
        }
    }

    private void initLiveDataObservers() {
        mViewModel.getRestaurantDetailLiveData().observe(this, restaurantDetail -> {
            if (restaurantDetail == null || restaurantDetail.getRestaurantDetails() == null ||
            restaurantDetail.getRestaurantDetails().isEmpty()) {
                onNetworkDisConnected();
                return;
            }
            if (adapter == null || progressBar == null) return;
            progressBar.setVisibility(View.GONE);
            adapter.setRestaurantDetailList(restaurantDetail.getRestaurantDetails());
            adapter.notifyDataSetChanged();
        });
    }
}
