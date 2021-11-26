package com.go4lunch2.ui.map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MapsFragment extends Fragment implements GoogleMap.InfoWindowAdapter,
                                                      GoogleMap.OnMyLocationButtonClickListener,
                                                      GoogleMap.OnMyLocationClickListener,
                                                      GoogleMap.OnInfoWindowClickListener,
                                                      OnMapReadyCallback,
                                                      ActivityCompat.OnRequestPermissionsResultCallback {

    FragmentMapsBinding binding;
    MapsViewModel vm;
    List<MapsStateItem> allMarkers = new ArrayList<>();
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap map;
    private boolean permissionDenied = false;
    SupportMapFragment mapFragment;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        enableMyLocation();

        LatLng paris = new LatLng(48.856614, 2.3522219);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(paris, 13f));
        map.getUiSettings().setZoomControlsEnabled(true);

        vm.getMarkersLiveData().observe(MapsFragment.this, mapsStateItems -> {
            allMarkers = mapsStateItems;
            for (MapsStateItem marker : allMarkers) {

                int iconeMarker = marker.getWorkmatesCount() > 0 ? R.drawable.marker_restaurant_orange : R.drawable.marker_restaurant_green;

                map.addMarker(new MarkerOptions()
                                      .position(new LatLng(marker.getLatitude(), marker.getLongitude()))
                                      .title(marker.getName())
                                      .icon(BitmapDescriptorFactory.fromResource(iconeMarker))

                             ).setTag(allMarkers.indexOf(marker));
            }
        });

        map.setInfoWindowAdapter(this);
        map.setOnInfoWindowClickListener(this);
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
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
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

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        Intent i = new Intent (mapFragment.getContext(), DetailRestaurantActivity.class);
        int position = (int) marker.getTag();
        MapsStateItem item = allMarkers.get(position);
        i.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, item.getId());
        startActivity(i);


    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(mapFragment.getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
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
        //Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        //Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();
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




