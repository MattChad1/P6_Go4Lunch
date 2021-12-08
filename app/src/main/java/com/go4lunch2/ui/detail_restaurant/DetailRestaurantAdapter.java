package com.go4lunch2.ui.detail_restaurant;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.go4lunch2.data.model.User;
import com.go4lunch2.R;

import java.util.List;

public class DetailRestaurantAdapter extends RecyclerView.Adapter<DetailRestaurantAdapter.ViewHolder> {

    Context ctx;
    private List<User> listUsers;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivAvatar;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_workmate_choise);
            ivAvatar = view.findViewById(R.id.iv_workmate_avatar);
        }
    }

    public DetailRestaurantAdapter(Context ctx, List<User> listUsers) {
        this.ctx = ctx;
        this.listUsers = listUsers;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_workmate, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        User user = listUsers.get(position);
        viewHolder.tvName.setText(ctx.getString(R.string.restaurant_chosen2, user.getName())); //TODO:  mettre les fonctions du repo
        Glide.with(viewHolder.ivAvatar.getContext())
                .load(user.getAvatar())
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.ivAvatar);
        Log.i("Glide", user.getAvatar().substring(0 , user.getAvatar().indexOf('.')));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listUsers.size();
    }
}

