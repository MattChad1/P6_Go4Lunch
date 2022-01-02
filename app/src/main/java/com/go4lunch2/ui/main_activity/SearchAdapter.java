package com.go4lunch2.ui.main_activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.R;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final Context ctx;
    private final List<SearchViewStateItem> searchResults;

    public SearchAdapter(Context ctx, List<SearchViewStateItem> searchResults) {
        this.ctx = ctx;
        this.searchResults = searchResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvTitle.setText(searchResults.get(position).getName());
        holder.tvSubtitle.setText(searchResults.get(position).getAdress());
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.itemView.getContext(), DetailRestaurantActivity.class);
            String test = searchResults.get(position).getId();
            intent.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, test);
            holder.itemView.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSubtitle;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.item_search_tv_title);
            tvSubtitle = itemView.findViewById(R.id.item_search_tv_subtitle);
        }
    }
}




