package com.go4lunch2.ui.list_restaurants;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.model.Restaurant;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;
import com.go4lunch2.databinding.ItemRestaurantBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    Context ctx;
    private List<Restaurant> listRestaurants;
    ItemRestaurantBinding binding;

    public class ViewHolder extends RecyclerView.ViewHolder {



        public ViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }

    public RestaurantsAdapter(Context ctx, List<Restaurant> listRestaurants) {
        this.ctx = ctx;
        this.listRestaurants = listRestaurants;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        binding = ItemRestaurantBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
    View view = binding.getRoot();

    //View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_restaurant, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = listRestaurants.get(position);
        binding.itemRestaurantName.setText(restaurant.getName());
        binding.itemRestaurantDesc1.setText(restaurant.getType() + "-" + restaurant.getAdress());
        binding.itemRestaurantDesc2.setText(restaurant.getOpeningTime());


        try {
            InputStream ims = ctx.getAssets().open(restaurant.getImage());
            binding.itemRestaurantImage.setImageDrawable(Drawable.createFromStream(ims, null));
            ims .close();
        }
        catch(IOException ex) {
            return;
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, DetailRestaurantActivity.class);
            intent.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, restaurant);
            ctx.startActivity(intent);
        });


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listRestaurants.size();
    }
}