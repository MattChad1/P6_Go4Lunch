package com.go4lunch2.ui.list_restaurants;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.R;
import com.go4lunch2.databinding.ItemRestaurantBinding;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    String TAG = "MyLog RestaurantsAdapter";
    Context ctx;
    private final List<RestaurantViewState> listRestaurants;
    ItemRestaurantBinding binding;

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public RestaurantsAdapter(Context ctx, List<RestaurantViewState> listRestaurants) {
        this.ctx = ctx;
        this.listRestaurants = listRestaurants;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        binding = ItemRestaurantBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.i(TAG, "onBindViewHolder: ");
        View v = holder.itemView;
        RestaurantViewState restaurant = listRestaurants.get(position);
        ((TextView) v.findViewById(R.id.item_restaurant_name)).setText(restaurant.getName());
        ((TextView) v.findViewById(R.id.item_restaurant_desc1)).setText(restaurant.getType() + "-" + restaurant.getAdress());
        ((TextView) v.findViewById(R.id.item_restaurant_desc2)).setText(restaurant.getOpeningHours());
        ((TextView) v.findViewById(R.id.item_restaurant_distance)).setText(restaurant.getDistance());

        if (restaurant.getStarsCount() == null) {
            v.findViewById(R.id.item_restaurant_num_stars1).setVisibility(View.GONE);
            v.findViewById(R.id.item_restaurant_num_stars2).setVisibility(View.GONE);
            v.findViewById(R.id.item_restaurant_num_stars3).setVisibility(View.GONE);
        }
        else {
            if (restaurant.getStarsCount() == 0.5) ((ImageView) v.findViewById(R.id.item_restaurant_num_stars1)).setImageDrawable(ctx.getDrawable(R.drawable.ic_star_half));
            else if (restaurant.getStarsCount() > 0.5) ((ImageView) v.findViewById(R.id.item_restaurant_num_stars1)).setImageDrawable(ctx.getDrawable(R.drawable.ic_star_filled));
            if (restaurant.getStarsCount() == 1.5) ((ImageView) v.findViewById(R.id.item_restaurant_num_stars2)).setImageDrawable(ctx.getDrawable(R.drawable.ic_star_half));
            else if (restaurant.getStarsCount() > 1.5) ((ImageView) v.findViewById(R.id.item_restaurant_num_stars2)).setImageDrawable(ctx.getDrawable(R.drawable.ic_star_filled));
            if (restaurant.getStarsCount() == 2.5) ((ImageView) v.findViewById(R.id.item_restaurant_num_stars3)).setImageDrawable(ctx.getDrawable(R.drawable.ic_star_half));
            else if (restaurant.getStarsCount() > 2.5) ((ImageView) v.findViewById(R.id.item_restaurant_num_stars3)).setImageDrawable(ctx.getDrawable(R.drawable.ic_star_filled));
        }

//        try {
//            InputStream ims = ctx.getAssets().open(restaurant.getImage());
//            binding.itemRestaurantImage.setImageDrawable(Drawable.createFromStream(ims, null));
//            ims.close();
//        }
//        catch(IOException ex) {
//            return; // TODO:ajouter image par défaut
//        }

        v.setOnClickListener(view -> {
            Intent intent = new Intent(ctx, DetailRestaurantActivity.class);
            intent.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, restaurant.getId());
            ctx.startActivity(intent);
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listRestaurants.size();
    }
}