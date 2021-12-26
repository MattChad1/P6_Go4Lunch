package com.go4lunch2.ui.main_activity;

import static com.go4lunch2.MyApplication.PREFS_CENTER;
import static com.go4lunch2.MyApplication.PREFS_CENTER_GPS;
import static com.go4lunch2.MyApplication.PREFS_NOTIFS;

import static java.lang.Math.abs;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.go4lunch2.BaseActivity;
import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.data.repositories.SortRepository;
import com.go4lunch2.databinding.ActivityMainBinding;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsFragment;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsViewModel;
import com.go4lunch2.ui.list_workmates.ListWorkmatesFragment;
import com.go4lunch2.ui.login.LogInActivity;
import com.go4lunch2.ui.map.MapsFragment;
import com.go4lunch2.ui.settings.SettingsFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements LocationListener {

    String TAG = "MyLog MainActivity";
    private ActivityMainBinding binding;
    DrawerLayout drawer;
    FirebaseUser user;
    CustomUser currentCustomUser;

    RecyclerView rv;
    List<SearchViewStateItem> searchResults = new ArrayList<>();
    SearchAdapter adapter;
    MainActivityViewModel vm;

    LocationManager locationManager;
    Location userLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(MainActivityViewModel.class);

        user = FirebaseAuth.getInstance().getCurrentUser();
        vm.getCurrentCustomUser(user.getUid()).observe(this, value -> currentCustomUser = value);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        MaterialToolbar toolbar = binding.topAppBar;
        setSupportActionBar(toolbar);

        drawer = binding.drawerLayout;
        toolbar.setNavigationOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.START);
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment, ListRestaurantsFragment.class, null)
                    .commit();
        }


        // Set up the location manager if user wants to use its GPS
        if (MyApplication.settings.getString(PREFS_CENTER, "").equals(PREFS_CENTER_GPS)) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, this);
        }




        // Set up the content of the navigation drawer
        if (user != null) { //TODO : supprimer le if car user ne peut pas être null ici (connecté)
            NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_drawer);
            View headerView = navigationView.getHeaderView(0);
            TextView tvMailUser = headerView.findViewById(R.id.nav_drawer_tv_email);
            TextView tvNameUser = headerView.findViewById(R.id.nav_drawer_name);
            ImageView ivAvatarUser = headerView.findViewById(R.id.nav_drawer_avatar);

            for (UserInfo profile : user.getProviderData()) {
                tvMailUser.setText(profile.getDisplayName());
                tvNameUser.setText(profile.getEmail());
                Glide.with(this).load(profile.getPhotoUrl())
                        .transform(new CircleCrop())
                        .into(ivAvatarUser);
            }
        }

        binding.navigationDrawer.setNavigationItemSelectedListener(menuItem -> {
           if (menuItem.getItemId() == R.id.menu_drawer_logout) {
                signOut();
            }

            else if (menuItem.getItemId() == R.id.menu_drawer_yourlunch) {
                if (currentCustomUser.getIdRestaurantChosen() != null) {
                    Intent i = new Intent(this, DetailRestaurantActivity.class);
                    i.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, currentCustomUser.getIdRestaurantChosen());
                    startActivity(i);
                }
                else Toast.makeText(this, R.string.no_restaurant, Toast.LENGTH_SHORT).show();
            }

            else if (menuItem.getItemId() == R.id.menu_drawer_settings) {
                getSupportFragmentManager().beginTransaction()
                        .setReorderingAllowed(true)
                        .replace(R.id.main_fragment, SettingsFragment.class, null)
                        .commit();
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        });

        // Bottom Navigation
        BottomNavigationView bottombar = binding.bottomNavigation;
        bottombar.setSelectedItemId(R.id.menu_bb_listview);
        bottombar.setOnItemSelectedListener(view1 -> {
            Class linkTo;
            switch (view1.getItemId()) {
                case R.id.menu_bb_mapview:
                    linkTo = MapsFragment.class;
                    break;
                case R.id.menu_bb_listview:
                    linkTo = ListRestaurantsFragment.class;
                    break;
                case R.id.menu_bb_workmates:
                    linkTo = ListWorkmatesFragment.class;
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + view1.getItemId());
            }

            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.main_fragment, linkTo, null)
                    .commit();

            return true;
        });


        // Display list of restaurants in RecyclerView
        rv = binding.lvSearchResults;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SearchAdapter(this, searchResults);
        rv.setAdapter(adapter);

        vm.getSearchResultsLiveData().observe(this, values -> {
            searchResults.clear();
            searchResults.addAll(values);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // For searching restaurants in the toolbar
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_restaurant));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() == 0) {
                    rv.setVisibility(View.GONE);
                    binding.mainFragment.setVisibility(View.VISIBLE);
                }

                else if (s.length() > 2) {
                    rv.setVisibility(View.VISIBLE);
                    binding.mainFragment.setVisibility(View.GONE);
                    vm.getSearchResults(s);
//                    if (s.length() >= 3) {
//                        vm.getSearchResults(s);
//                    }
//                    else adapter.getFilter().filter(s);
                }

                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // function for the list to be sorted
        switch (item.getItemId()) {
                case R.id.menu_order_name:
                vm.updateOrderLiveData(SortRepository.OrderBy.NAME);
                break;
                case R.id.menu_order_distance:
                    vm.updateOrderLiveData(SortRepository.OrderBy.DISTANCE);
                    break;
                case R.id.menu_order_rating:
                    vm.updateOrderLiveData(SortRepository.OrderBy.RATING);
                    break;

                default:
                    Log.i(TAG, "Clic error on toolbar menu : id " + item.getItemId());;
            }
            return true;
        }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        // if the position of the user has changed enough, we call updateCenter, so the list or map can update.
        if (userLocation== null || abs(userLocation.getLatitude() - location.getLatitude()) > 0.01  || abs(userLocation.getLongitude()- location.getLongitude()) > 0.01) {
            vm.updateCenter(location);
            Log.i(TAG, "onLocationChanged: update center");
        }
        userLocation = location;
    }

    @Override
    public void onLocationChanged(@NonNull List<Location> locations) {

    }

    @Override
    public void onFlushComplete(int requestCode) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }




}