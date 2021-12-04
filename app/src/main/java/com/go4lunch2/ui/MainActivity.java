package com.go4lunch2.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.firebase.ui.auth.AuthUI;
import com.go4lunch2.R;
import com.go4lunch2.data.PlaceAutocompleteAPI;
import com.go4lunch2.data.model.model_gmap.place_autocomplete.Prediction;
import com.go4lunch2.data.model.model_gmap.place_autocomplete.Root;
import com.go4lunch2.databinding.ActivityMainBinding;
import com.go4lunch2.ui.list_restaurants.ListRestaurantsFragment;
import com.go4lunch2.ui.list_workmates.ListWorkmatesFragment;
import com.go4lunch2.ui.login.LogInActivity;
import com.go4lunch2.ui.map.MapsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    String TAG = "MyLog MainActivity";
    //int AUTOCOMPLETE_REQUEST_CODE = 1221;
    private ActivityMainBinding binding;
    DrawerLayout drawer;
    FirebaseUser user;

    ListView listView;
    List<String> searchResults = new ArrayList<>();
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        // TODO : supprimer avec la fonction
        user = FirebaseAuth.getInstance().getCurrentUser();

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

        if (user != null) { //TODO : supprimer le if car user ne peut pas être null ici (connecté)
            TextView tvMailUser = binding.navigationDrawer.findViewById(R.id.nav_drawer_tv_email);
            //tvMailUser.setText(user.getEmail());
            Toast.makeText(this, "Vous êtes connecté !!!!", Toast.LENGTH_SHORT).show();
        }

        binding.navigationDrawer.setNavigationItemSelectedListener(menuItem -> {
            // Handle menu item selected
            //menuItem.isChecked = true;
            if (menuItem.getItemId() == R.id.menu_drawer_connexion) {
                startActivity(new Intent(this, LogInActivity.class));
            }

            else if (menuItem.getItemId() == R.id.menu_drawer_logout) {
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

        listView = binding.lvSearchResults;
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, searchResults);
        listView.setAdapter(arrayAdapter);

        //listView.setOnItemClickListener();

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
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (s.length() > 2) {
                    Log.i(TAG, "onQueryTextChange: " + s);

                    if (s.length() == 3) {
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                        OkHttpClient client = new OkHttpClient.Builder()
                                .addInterceptor(logging)
                                .build();

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("https://maps.googleapis.com/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(client)
                                .build();

                        PlaceAutocompleteAPI service = retrofit.create(PlaceAutocompleteAPI.class);
                        Call<Root> callAsync = service.getResults("restaurant+" + s, "48.856614, 2.3522219", "1500", "establishment",
                                                                  getString(R.string.google_maps_key22));

                        callAsync.enqueue(new Callback<Root>() {
                            @Override
                            public void onResponse(Call<Root> call, Response<Root> response) {

                                for (Prediction p : response.body().getPredictions()) {
                                    updateSearch(p.getDescription());
                                }

                                Log.i(TAG, "onResponse: " + searchResults.toString());
                            }

                            @Override
                            public void onFailure(Call<Root> call, Throwable t) {
                                Log.i(TAG, "onFailure: " + t);
                                System.out.println(t);
                            }
                        });
                    }

                    else {
                        arrayAdapter.getFilter().filter(s);

                    }

                    listView.setVisibility(View.VISIBLE);
                    binding.mainFragment.setVisibility(View.GONE);
                }

                return false;
            }
        });

        return true;
    }

    public void updateSearch(String str) {
        searchResults.add(str);
        Log.i(TAG, "updateSearch: " + searchResults.toString());
        arrayAdapter.notifyDataSetChanged();
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