package com.go4lunch2.ui.list_workmates;

import android.content.Context;
import android.content.Intent;
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
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    private final List<WorkmateViewStateItem> listWorkmates;
    private final Context ctx;

    public WorkmatesAdapter(Context ctx, List<WorkmateViewStateItem> listWorkmates) {
        this.ctx = ctx;
        this.listWorkmates = listWorkmates;
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
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        if (listWorkmates != null && !listWorkmates.isEmpty()) {
            WorkmateViewStateItem workmate = listWorkmates.get(position);
            viewHolder.tvName.setText(ctx.getString(R.string.restaurant_chosen, workmate.getNameWorkmate(), workmate.getNameRestaurant()));
            Glide.with(viewHolder.ivAvatar.getContext())
                    .load(workmate.getAvatar())
                    .apply(RequestOptions.circleCropTransform())
                    .into(viewHolder.ivAvatar);
            viewHolder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(ctx, DetailRestaurantActivity.class);
                i.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, listWorkmates.get(position).getIdRestaurant());
                ctx.startActivity(i);
            });
        }
    }

    @Override
    public int getItemCount() {
        return listWorkmates.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final ImageView ivAvatar;
        private final View itemView;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_workmate_choise);
            ivAvatar = view.findViewById(R.id.iv_workmate_avatar);
            itemView = view;
        }
    }
}

