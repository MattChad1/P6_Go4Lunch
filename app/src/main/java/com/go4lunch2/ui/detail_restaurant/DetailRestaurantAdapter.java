package com.go4lunch2.ui.detail_restaurant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.go4lunch2.R;
import com.go4lunch2.data.model.CustomUser;

import java.util.List;

public class DetailRestaurantAdapter extends RecyclerView.Adapter<DetailRestaurantAdapter.ViewHolder> {

    private final List<CustomUser> listCustomUsers;
    private final Context ctx;

    public DetailRestaurantAdapter(Context ctx, List<CustomUser> listCustomUsers) {
        this.ctx = ctx;
        this.listCustomUsers = listCustomUsers;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_workmate, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        CustomUser customUser = listCustomUsers.get(position);
        viewHolder.tvName.setText(ctx.getString(R.string.restaurant_chosen2, customUser.getName()));
        Glide.with(viewHolder.ivAvatar.getContext())
                .load(customUser.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.ivAvatar);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listCustomUsers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final ImageView ivAvatar;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_workmate_choise);
            ivAvatar = view.findViewById(R.id.iv_workmate_avatar);
        }
    }
}

