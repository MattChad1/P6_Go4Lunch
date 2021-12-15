package com.go4lunch2.ui.main_activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.go4lunch2.BaseActivity;
import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.data.model.CustomUser;
import com.go4lunch2.databinding.ActivityMainBinding;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsFragment;
import com.go4lunch2.ui.list_workmates.ListWorkmatesFragment;
import com.go4lunch2.ui.login.LogInActivity;
import com.go4lunch2.ui.map.MapsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    String TAG = "MyLog MainActivity";
    private ActivityMainBinding binding;
    DrawerLayout drawer;
    FirebaseUser user;
    CustomUser currentCustomUser;

    RecyclerView rv;
    List<SearchViewStateItem> searchResults = new ArrayList<>();
    SearchAdapter adapter;
    MainActivityViewModel vm;

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
                    .add(R.id.main_fragment, MapsFragment.class, null)
                    .commit();
        }



        // Navigation drawer
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
            Toast.makeText(this, "Vous êtes connecté !!!!", Toast.LENGTH_SHORT).show();
        }



        binding.navigationDrawer.setNavigationItemSelectedListener(menuItem -> {
            // Handle menu item selected
            // menuItem.isChecked = true;
            if (menuItem.getItemId() == R.id.menu_drawer_connexion) {
                startActivity(new Intent(this, LogInActivity.class));
            }

            else if (menuItem.getItemId() == R.id.menu_drawer_logout) {
                signOut();
            }

            else if (menuItem.getItemId() == R.id.menu_drawer_yourlunch) {
                if (currentCustomUser.getIdRestaurantChosen()!=null) {
                    Intent i = new Intent(this, DetailRestaurantActivity.class);
                    i.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, currentCustomUser.getIdRestaurantChosen());
                    startActivity(i);
                }
                else Toast.makeText(this, R.string.no_restaurant, Toast.LENGTH_SHORT);
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
                    toolbar.setTitle(getString(R.string.map_view_desc));
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_toolbar);
                    linkTo = MapsFragment.class;
                    break;
                case R.id.menu_bb_listview:
                    toolbar.setTitle(getString(R.string.list_restaurants_desc));
                    toolbar.getMenu().clear();
                    toolbar.inflateMenu(R.menu.menu_toolbar);
                    linkTo = ListRestaurantsFragment.class;
                    break;
                case R.id.menu_bb_workmates:
                    toolbar.setTitle(getString(R.string.list_workmates_desc));
                    toolbar.getMenu().clear();
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

        rv = binding.lvSearchResults;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new SearchAdapter(this, searchResults);
        rv.setAdapter(adapter);

        vm.getSearchResultsLiveData().observe(this, values -> {
            Log.i(TAG, "observer: " + values.get(0).getName());
            searchResults.clear();
            searchResults.addAll(values);
            adapter.notifyDataSetChanged();
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setQueryHint("Search restaurants");

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
                    Log.i(TAG, "onQueryTextChange: " + s);
                }

                else if (s.length() > 2) {
                    Log.i(TAG, "onQueryTextChange: " +s);
                    rv.setVisibility(View.VISIBLE);
                    binding.mainFragment.setVisibility(View.GONE);
                    if (s.length() == 3) {
                        vm.getSearchResults(s);
                        Log.i(TAG, "onQueryTextChange: " +s);
                    }
                    else adapter.getFilter().filter(s);
                }

                return false;
            }
        });
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
}