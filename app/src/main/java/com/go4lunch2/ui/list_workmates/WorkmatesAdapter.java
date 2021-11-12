package com.go4lunch2.ui.list_workmates;

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
import com.go4lunch2.model.Workmate;
import com.go4lunch2.R;

import java.util.List;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    Context ctx;
    private List<Workmate> listWorkmates;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        ImageView ivAvatar;

        public ViewHolder(View view) {
            super(view);
            tvName = view.findViewById(R.id.tv_workmate_choise);
            ivAvatar = view.findViewById(R.id.iv_workmate_avatar);
        }
    }

    public WorkmatesAdapter(Context ctx, List<Workmate> listWorkmates) {
        this.ctx = ctx;
        this.listWorkmates = listWorkmates;
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
        Workmate workmate = listWorkmates.get(position);
        viewHolder.tvName.setText(ctx.getString(R.string.restaurant_chosen, workmate.getName(), "", "")); //TODO:  mettre les fonctions du repo
        //TODO : remplacer par adresse avatar
        String name = workmate.getAvatar().substring(0 , workmate.getAvatar().indexOf('.'));
        int resourceId = ctx.getResources().getIdentifier(name, "drawable",
                ctx.getPackageName());
        Glide.with(viewHolder.ivAvatar.getContext())
                .load(resourceId)
                .apply(RequestOptions.circleCropTransform())
                .into(viewHolder.ivAvatar);
        Log.i("Glide", workmate.getAvatar().substring(0 , workmate.getAvatar().indexOf('.')));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return listWorkmates.size();
    }
}

