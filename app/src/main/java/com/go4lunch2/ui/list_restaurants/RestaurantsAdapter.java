package com.go4lunch2.ui.list_restaurants;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.go4lunch2.R;
import com.go4lunch2.Utils.Utils;
import com.go4lunch2.databinding.ItemRestaurantBinding;
import com.go4lunch2.ui.ItemClickListener;

import java.util.List;

public class RestaurantsAdapter extends RecyclerView.Adapter<RestaurantsAdapter.ViewHolder> {

    private final List<RestaurantViewState> listRestaurants;
    final Context ctx;
    ItemRestaurantBinding binding;
    private ItemClickListener clickListener;

    public RestaurantsAdapter(Context ctx, List<RestaurantViewState> listRestaurants, ItemClickListener clickListener) {
        this.ctx = ctx;
        this.listRestaurants = listRestaurants;
        this.clickListener = clickListener;
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        binding = ItemRestaurantBinding.inflate(LayoutInflater.from(viewGroup.getContext()), viewGroup, false);
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        View v = holder.itemView;
        RestaurantViewState restaurant = listRestaurants.get(position);
        ((TextView) v.findViewById(R.id.item_restaurant_name)).setText(restaurant.getName());
        ((TextView) v.findViewById(R.id.item_restaurant_desc1)).setText(restaurant.getAdress());
        ((TextView) v.findViewById(R.id.item_restaurant_desc2)).setText(restaurant.getOpeningHours());
        if (restaurant.getOpeningHours().equals(ctx.getString(R.string.open)))
            ((TextView) v.findViewById(R.id.item_restaurant_desc2)).setTextColor(ctx.getResources().getColor(R.color.quantum_googgreen));
        else ((TextView) v.findViewById(R.id.item_restaurant_desc2)).setTextColor(ctx.getResources().getColor(R.color.red_dark));

        if (restaurant.getDistance() != null)
            ((TextView) v.findViewById(R.id.item_restaurant_distance)).setText(Utils.distanceConversion(restaurant.getDistance()));
        ((TextView) v.findViewById(R.id.item_restaurant_num_workmates)).setText(
                ctx.getString(R.string.num_workmates, restaurant.getWorkmatesCount()));

        if (restaurant.getStarsCount() == null) {
            v.findViewById(R.id.item_restaurant_num_stars1).setVisibility(View.GONE);
            v.findViewById(R.id.item_restaurant_num_stars2).setVisibility(View.GONE);
            v.findViewById(R.id.item_restaurant_num_stars3).setVisibility(View.GONE);
        }
        else {
            v.findViewById(R.id.item_restaurant_num_stars1).setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_restaurant_num_stars2).setVisibility(View.VISIBLE);
            v.findViewById(R.id.item_restaurant_num_stars3).setVisibility(View.VISIBLE);
            if (restaurant.getStarsCount() == 0.5)
                ((ImageView) v.findViewById(R.id.item_restaurant_num_stars1)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_half));
            else if (restaurant.getStarsCount() > 0.5)
                ((ImageView) v.findViewById(R.id.item_restaurant_num_stars1)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_filled));
            if (restaurant.getStarsCount() < 1.5)
                ((ImageView) v.findViewById(R.id.item_restaurant_num_stars2)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_empty));
            else if (restaurant.getStarsCount() == 1.5)
                ((ImageView) v.findViewById(R.id.item_restaurant_num_stars2)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_half));
            else ((ImageView) v.findViewById(R.id.item_restaurant_num_stars2)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_filled));
            if (restaurant.getStarsCount() < 2.5)
                ((ImageView) v.findViewById(R.id.item_restaurant_num_stars3)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_empty));
            else if (restaurant.getStarsCount() == 2.5)
                ((ImageView) v.findViewById(R.id.item_restaurant_num_stars3)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_half));
            else ((ImageView) v.findViewById(R.id.item_restaurant_num_stars3)).setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.ic_star_filled));
        }

        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            Glide.with(v.getContext()).load(restaurant.getImage())
                    .placeholder(R.drawable.ic_downloading_24)
                    .error(R.drawable.ic_search)
                    .centerCrop()
                    .into((ImageView) v.findViewById(R.id.item_restaurant_image));
        }

        v.setOnClickListener(view1 -> clickListener.onClick(v, position));

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listRestaurants.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}