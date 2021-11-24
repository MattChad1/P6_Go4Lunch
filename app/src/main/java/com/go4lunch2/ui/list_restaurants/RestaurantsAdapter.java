package com.go4lunch2.ui.list_restaurants;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.R;
import com.go4lunch2.databinding.ItemRestaurantBinding;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

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
    View view = binding.getRoot();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RestaurantViewState restaurant = listRestaurants.get(position);
        binding.itemRestaurantName.setText(restaurant.getName());
        binding.itemRestaurantDesc1.setText(restaurant.getType() + "-" + restaurant.getAdress());
        binding.itemRestaurantDesc2.setText(restaurant.getOpeningHours());

        if (restaurant.getStarsCount()==null) {
            binding.itemRestaurantNumStars1.setVisibility(View.GONE);
            binding.itemRestaurantNumStars2.setVisibility(View.GONE);
            binding.itemRestaurantNumStars3.setVisibility(View.GONE);

        }
        else{
            if (restaurant.getStarsCount() == 0.5) binding.itemRestaurantNumStars1.setImageDrawable(ctx.getDrawable(R.drawable.ic_star_half));
            else if (restaurant.getStarsCount() > 0.5) binding.itemRestaurantNumStars1.setImageDrawable(ctx.getDrawable(R.drawable.ic_star_filled));
            if (restaurant.getStarsCount() == 1.5) binding.itemRestaurantNumStars2.setImageDrawable(ctx.getDrawable(R.drawable.ic_star_half));
            else if (restaurant.getStarsCount() > 1.5) binding.itemRestaurantNumStars2.setImageDrawable(ctx.getDrawable(R.drawable.ic_star_filled));
            if (restaurant.getStarsCount() == 2.5) binding.itemRestaurantNumStars3.setImageDrawable(ctx.getDrawable(R.drawable.ic_star_half));
            else if (restaurant.getStarsCount() > 2.5) binding.itemRestaurantNumStars3.setImageDrawable(ctx.getDrawable(R.drawable.ic_star_filled));
        }

//        try {
//            InputStream ims = ctx.getAssets().open(restaurant.getImage());
//            binding.itemRestaurantImage.setImageDrawable(Drawable.createFromStream(ims, null));
//            ims.close();
//        }
//        catch(IOException ex) {
//            return; // TODO:ajouter image par défaut
//        }

        holder.itemView.setOnClickListener(v -> {
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