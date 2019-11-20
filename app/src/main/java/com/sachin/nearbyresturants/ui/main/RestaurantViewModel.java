package com.sachin.nearbyresturants.ui.main;

import android.location.Location;
import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sachin.nearbyresturants.NearByRestaurantApplication;
import com.sachin.nearbyresturants.network.NetworkManager;
import com.sachin.nearbyresturants.network.dto.RestaurantDetail;
import com.sachin.nearbyresturants.network.dto.RestaurantDetailResponse;
import com.sachin.nearbyresturants.rx.RxBus;

import java.text.DecimalFormat;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class RestaurantViewModel extends ViewModel {

    @Inject
    public RxBus rxBus;
    @Inject
    public NetworkManager networkManager;

    private Location lastLocation;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MutableLiveData<RestaurantDetailResponse> restaurantDetailLiveData = new MutableLiveData<>();

    public RestaurantViewModel() {
        NearByRestaurantApplication.getApplication().getApplicationComponent().inject(this);
        observeForLocationUpdate();
    }

    private void observeForLocationUpdate() {
        compositeDisposable.add(rxBus.getBus().subscribe(item -> {
            if (item instanceof Location) {
                lastLocation = (Location) item;
                loadNearByRestaurantDetails();
            }
        }, error -> observeForLocationUpdate()));
    }

    @Override
    public void onCleared() {
        super.onCleared();
        if (compositeDisposable != null && !compositeDisposable.isDisposed())
            compositeDisposable.dispose();
    }

    public void loadNearByRestaurantDetails() {
        if (lastLocation == null) return;
        String nextPageToken = null;
        Double lat = lastLocation.getLatitude();
        Double lang = lastLocation.getLongitude();
        String latLang = new DecimalFormat("#0.000000").format(lat) + "," + new DecimalFormat("#0.000000").format(lang);
        RestaurantDetailResponse detailResponse = restaurantDetailLiveData.getValue();
        if (detailResponse != null && !TextUtils.isEmpty(detailResponse.getNextPageToken())) {
            nextPageToken = detailResponse.getNextPageToken();
        }
        compositeDisposable.add(networkManager.getRestaurantDetails(latLang,
                nextPageToken)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(restaurantDetailResponse -> {
                    updateDistanceInResponse(restaurantDetailResponse);
                    restaurantDetailLiveData.setValue(restaurantDetailResponse);
                     },
                        error -> restaurantDetailLiveData.setValue(null)));
    }

    private void updateDistanceInResponse(RestaurantDetailResponse restaurantDetailResponse) {
        if (restaurantDetailResponse == null || restaurantDetailResponse.getRestaurantDetails() == null ||
        restaurantDetailResponse.getRestaurantDetails().isEmpty()) return;
        for (RestaurantDetail detail : restaurantDetailResponse.getRestaurantDetails()) {
            detail.setDistance(getDistanceBetweenSourceAndDest(detail));
        }
    }

    public MutableLiveData<RestaurantDetailResponse> getRestaurantDetailLiveData() {
        return restaurantDetailLiveData;
    }

    public String getDistanceBetweenSourceAndDest(RestaurantDetail detail) {
        if (lastLocation == null || detail == null) return "";
        com.sachin.nearbyresturants.network.dto.Location location = detail.getGeometry().getLocation();
        if (location == null) return "";
        double lat1 = lastLocation.getLatitude();
        double lng1 = lastLocation.getLongitude();
        float[] result=new float[1];
        Location.distanceBetween(lat1, lng1, location.getLat(), location.getLng(), result);
        return new DecimalFormat("#0.00").format(result[0]/1000) + "km";
    }

}
