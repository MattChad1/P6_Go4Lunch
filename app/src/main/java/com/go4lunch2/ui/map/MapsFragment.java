package com.go4lunch2.ui.map;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.databinding.FragmentMapsBinding;
import com.go4lunch2.databinding.InfoWindowBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements GoogleMap.InfoWindowAdapter {
    FragmentMapsBinding binding;
    MapsViewModel vm;
    List<MapsStateItem> allMarkers = new ArrayList<>();

    private OnMapReadyCallback callback;

    {
        callback = new OnMapReadyCallback() {

            @Override
            public void onMapReady(GoogleMap googleMap) {
                LatLng paris = new LatLng(48.856614, 2.3522219);
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 13f));
                //googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setZoomControlsEnabled(true);

                vm.getMarkersLiveData().observe(MapsFragment.this, mapsStateItems -> {
                    allMarkers = mapsStateItems;
                    for (MapsStateItem marker : allMarkers) {

                        int iconeMarker = marker.getWorkmatesCount() > 0 ? R.drawable.marker_restaurant_orange : R.drawable.marker_restaurant_green;

                        googleMap.addMarker(new MarkerOptions()
                                                    .position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                                                    .title(marker.getName())
                                                    .icon(BitmapDescriptorFactory.fromResource(iconeMarker))

                                           ).setTag(allMarkers.indexOf(marker));
                    }
                });

                googleMap.setInfoWindowAdapter(MapsFragment.this);
            }
        };
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapsViewModel.class);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        int position = (int) marker.getTag();
        MapsStateItem item = allMarkers.get(position);

        InfoWindowBinding bindingWindow = InfoWindowBinding.inflate(MapsFragment.this.getLayoutInflater());
        bindingWindow.tvMapwindowTitle.setText(item.name);
        bindingWindow.tvMapwindowSubtitle.setText(""); //TODO : mettre type de cuisine?

        if (item.getStarsCount() == null) {
            bindingWindow.mapwindowNumStars1.setVisibility(View.INVISIBLE);
            bindingWindow.mapwindowNumStars2.setVisibility(View.INVISIBLE);
            bindingWindow.mapwindowNumStars3.setVisibility(View.INVISIBLE);
        }
        else {
            if (item.getStarsCount() == 0.5) bindingWindow.mapwindowNumStars1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half));
            else if (item.getStarsCount() > 0.5)
                bindingWindow.mapwindowNumStars1.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_filled));
            if (item.getStarsCount() == 1.5) bindingWindow.mapwindowNumStars2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half));
            else if (item.getStarsCount() > 1.5)
                bindingWindow.mapwindowNumStars2.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_filled));
            if (item.getStarsCount() == 2.5) bindingWindow.mapwindowNumStars3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_half));
            else if (item.getStarsCount() > 2.5)
                bindingWindow.mapwindowNumStars3.setImageDrawable(getResources().getDrawable(R.drawable.ic_star_filled));
        }

        bindingWindow.tvMapwindowWorkmates.setText(getString(R.string.num_workmates, item.workmatesCount));

        if (item.image != null && item.image != "") {
            try {
                InputStream ims = getResources().getAssets().open(item.getImage());
                bindingWindow.ivMapwindow.setImageDrawable(Drawable.createFromStream(ims, null));
                ims.close();
            } catch (IOException ex) {
                Log.i("MapsFragment", "Image not found : " + item.getImage());
            }
        }
        View view = bindingWindow.getRoot();
        return view;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }
}