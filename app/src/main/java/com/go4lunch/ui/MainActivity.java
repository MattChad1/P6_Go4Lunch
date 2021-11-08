package com.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.go4lunch.ui.list_restaurants.ListRestaurantsFragment;
import com.go4lunch.ui.list_workmates.ListWorkmatesFragment;
import com.go4lunch.ui.map.MapsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.go4lunch.R;
import com.go4lunch.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.main_fragment, ListRestaurantsFragment.class, null)
                    .commit();
        }

        MaterialToolbar toolbar = binding.topAppBar;
        setSupportActionBar(toolbar);
        drawer = binding.drawerLayout;
        toolbar.setNavigationOnClickListener(v -> {
            drawer.openDrawer(GravityCompat.START);
        });

        binding.navigationDrawer.setNavigationItemSelectedListener ( menuItem -> {
                // Handle menu item selected
                //menuItem.isChecked = true;
            if (menuItem.getItemId() == R.id.menu_drawer_connexion) {
                startActivity(new Intent(this, LogInActivity.class));
            }

        drawer.closeDrawer(GravityCompat.START);
           return true;
        });

        BottomNavigationView bottombar = binding.bottomNavigation;
        bottombar.setSelectedItemId(R.id.menu_bb_listview);
        bottombar.setOnItemSelectedListener(view1 -> {
            Class linkTo;
            switch (view1.getItemId()) {
                case R.id.menu_bb_mapview:
                toolbar.setTitle(getString(R.string.map_view_desc));
                linkTo = MapsFragment.class;
                break;
                case R.id.menu_bb_listview:
                    toolbar.setTitle(getString(R.string.list_restaurants_desc));
                    linkTo = ListRestaurantsFragment.class;
                    break;
                case R.id.menu_bb_workmates:
                    toolbar.setTitle(getString(R.string.list_workmates_desc));
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




    }


    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}