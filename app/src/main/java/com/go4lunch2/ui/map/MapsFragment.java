package com.go4lunch2.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.databinding.FragmentMapsBinding;
import com.go4lunch2.databinding.InfoWindowBinding;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements GoogleMap.InfoWindowAdapter,
                                                      GoogleMap.OnMyLocationButtonClickListener,
                                                      GoogleMap.OnMyLocationClickListener,
                                                      GoogleMap.OnInfoWindowClickListener,
                                                      OnMapReadyCallback,
                                                      ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    FragmentMapsBinding binding;
    MapsViewModel vm;
    List<MapsStateItem> allMarkers = new ArrayList<>();
    SupportMapFragment mapFragment;
    private GoogleMap map;
    private boolean permissionDenied = false;
    private Double centerLatitude;
    private Double centerLongitude;

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();

        if (MyApplication.settings.getString(MyApplication.PREFS_CENTER, "").equals(MyApplication.PREFS_CENTER_COMPANY)) {
            LatLng paris = new LatLng(48.856614, 2.3522219);
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 13f));
        }
        else map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(centerLatitude, centerLongitude), 13f));

        map.getUiSettings().setZoomControlsEnabled(true);

        vm.getMarkersLiveData().observe(MapsFragment.this, mapsStateItems -> {
            allMarkers = mapsStateItems;
            for (MapsStateItem marker : allMarkers) {

                int iconeMarker = marker.getWorkmatesCount() > 0 ? R.drawable.marker_restaurant_orange : R.drawable.marker_restaurant_green;

                Marker m = map.addMarker(new MarkerOptions()
                                                 .position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                                                 .title(marker.getName())
                                                 .icon(BitmapDescriptorFactory.fromResource(iconeMarker))
                                        );
                if (m != null) m.setTag(allMarkers.indexOf(marker));
            }
        });

        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle(getString(R.string.map_view_desc));
        toolbar.getMenu().getItem(0).setVisible(true);
        toolbar.getMenu().getItem(1).setVisible(false);

        binding = FragmentMapsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MapsViewModel.class);

        if (this.getArguments() != null) {
            centerLatitude = this.getArguments().getDouble("centerLatitude");
            centerLongitude = this.getArguments().getDouble("centerLongitude");
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        InfoWindowBinding bindingWindow = InfoWindowBinding.inflate(MapsFragment.this.getLayoutInflater());
        Integer position = (Integer) marker.getTag();

        if (position != null) {
            MapsStateItem item = allMarkers.get(position);

            bindingWindow.tvMapwindowTitle.setText(item.getName());
            bindingWindow.tvMapwindowSubtitle.setText("");
            if (item.getStarsCount() == null) {
                bindingWindow.mapwindowNumStars1.setVisibility(View.INVISIBLE);
                bindingWindow.mapwindowNumStars2.setVisibility(View.INVISIBLE);
                bindingWindow.mapwindowNumStars3.setVisibility(View.INVISIBLE);
            }
            else {
                if (item.getStarsCount() == 0.5)
                    bindingWindow.mapwindowNumStars1.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_star_half));
                else if (item.getStarsCount() > 0.5)
                    bindingWindow.mapwindowNumStars1.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_star_filled));
                if (item.getStarsCount() == 1.5)
                    bindingWindow.mapwindowNumStars2.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_star_half));
                else if (item.getStarsCount() > 1.5)
                    bindingWindow.mapwindowNumStars2.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_star_filled));
                if (item.getStarsCount() == 2.5)
                    bindingWindow.mapwindowNumStars3.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_star_half));
                else if (item.getStarsCount() > 2.5)
                    bindingWindow.mapwindowNumStars3.setImageDrawable(ContextCompat.getDrawable(requireActivity(), R.drawable.ic_star_filled));
            }

            bindingWindow.tvMapwindowWorkmates.setText(getString(R.string.num_workmates, item.workmatesCount));

            if (item.getImage() != null && !item.getImage().isEmpty()) {
                String url = item.getImage().replace("maxwidth=150", "maxwidth=90");
                Glide.with(requireActivity()).load(url)
                        .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                        .placeholder(R.drawable.ic_downloading_24)
                        .error(R.drawable.ic_search)
                        .centerCrop()
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                if (!dataSource.equals(DataSource.MEMORY_CACHE)) marker.showInfoWindow();
                                return false;
                            }
                        })
                        .into(bindingWindow.ivMapwindow);
            }
        }

        return bindingWindow.getRoot();
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        if (marker.getTag() != null) {
            int position = (int) marker.getTag();
            MapsStateItem item = allMarkers.get(position);
            Intent i = new Intent(mapFragment.getContext(), DetailRestaurantActivity.class);
            i.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, item.getId());
            startActivity(i);
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        }
        else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission((AppCompatActivity) requireActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                                              Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        }
        else {
            // Permission was denied. Display an error message
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(mapFragment.getChildFragmentManager(), "dialog");
    }
}




