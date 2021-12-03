package com.go4lunch2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.auth.AuthUI;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsFragment;
import com.go4lunch2.ui.list_workmates.ListWorkmatesFragment;
import com.go4lunch2.ui.login.LogInActivity;
import com.go4lunch2.ui.map.MapsFragment;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.go4lunch2.R;
import com.go4lunch2.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String TAG = "MyLog MainActivity";
    int AUTOCOMPLETE_REQUEST_CODE = 1221;
    private ActivityMainBinding binding;
    DrawerLayout drawer;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // TODO : supprimer avec la fonction
        user = FirebaseAuth.getInstance().getCurrentUser();
        testFirebase(user);


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

        if (user!=null) { //TODO : supprimer le if car user ne peut pas être null ici (connecté)
            TextView tvMailUser = binding.navigationDrawer.findViewById(R.id.nav_drawer_tv_email);
            //tvMailUser.setText(user.getEmail());
            Toast.makeText(this, "Vous êtes connecté !!!!", Toast.LENGTH_SHORT).show();
        }

        binding.navigationDrawer.setNavigationItemSelectedListener ( menuItem -> {
                // Handle menu item selected
                //menuItem.isChecked = true;
            if (menuItem.getItemId() == R.id.menu_drawer_connexion) {
                startActivity(new Intent(this, LogInActivity.class));
            }

            else if (menuItem.getItemId()== R.id.menu_drawer_logout) {
                signOut();
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
                //toolbar.getMenu().clear();
                //toolbar.inflateMenu(R.menu.menu_toolbar);
                linkTo = MapsFragment.class;
                break;
                case R.id.menu_bb_listview:
                    toolbar.setTitle(getString(R.string.list_restaurants_desc));
//                    toolbar.getMenu().clear();
//                    toolbar.inflateMenu(R.menu.menu_toolbar);
                    linkTo = ListRestaurantsFragment.class;
                    break;
                case R.id.menu_bb_workmates:
                    toolbar.setTitle(getString(R.string.list_workmates_desc));
//                    toolbar.getMenu().clear();
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



        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key22));
        }

// Create a new Places client instance.
        PlacesClient placesClient = Places.createClient(this);




    }


    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();

        searchView.setQueryHint("Search restaurants");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length()>2) Log.i(TAG, "onQueryTextChange: "+s);

                return false;
            }
        });





        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            Toast.makeText(MainActivity.this, "Action clicked", Toast.LENGTH_LONG).show();
            //onSearchCalled();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onSearchCalled() {
        // Set the fields to specify which types of place data to return.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields).setCountry("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());

            }
            else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            }
            else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }




    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void testFirebase(FirebaseUser user) {
        if (user != null) {
            Snackbar.make(binding.drawerLayout, "Vous êtes connecté : " + user.getEmail() + user.getUid(), Snackbar.LENGTH_SHORT).show();
        }
    }

}