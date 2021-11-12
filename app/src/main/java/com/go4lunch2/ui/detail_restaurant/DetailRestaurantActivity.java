package com.go4lunch2.ui.detail_restaurant;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.model.Repository;
import com.go4lunch2.model.Restaurant;
import com.go4lunch2.model.Workmate;
import com.go4lunch2.R;
import com.go4lunch2.databinding.ActivityDetailRestaurantBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class DetailRestaurantActivity extends AppCompatActivity {

    private ActivityDetailRestaurantBinding binding;
    RecyclerView rv;
    List<Workmate> workmatesInterested = Repository.FAKE_LIST_WORKMATES; // TODO : à remplacer
    Restaurant restaurant;
    static public final String RESTAURANT_SELECTED = "restaurant_selected";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailRestaurantBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        Intent intent = getIntent();
        restaurant = intent.getParcelableExtra(RESTAURANT_SELECTED);

        binding.restaurantName.setText(restaurant.getName());
        binding.restaurantDesc1.setText(restaurant.getType() + "-" + restaurant.getAdress());
//TODO : if user has chosen this restaurant
        binding.fabRestaurantChosen.setColorFilter(this.getResources().getColor(R.color.green_select_fab));


        try {
            InputStream ims = this.getAssets().open(restaurant.getImage());
            binding.restaurantImage.setImageDrawable(Drawable.createFromStream(ims, null));
            ims.close();
        }
        catch(IOException ex) {
            return;
        }

        rv = binding.rvListWorkmatesForOneRestaurant;
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        initList();


    }

    private void initList() {
        DetailRestaurantAdapter adapter = new DetailRestaurantAdapter(this, workmatesInterested);
        rv.setAdapter(adapter);
    }
}