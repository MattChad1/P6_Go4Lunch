package com.go4lunch2.ui.list_restaurants;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.MyApplication;
import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.databinding.FragmentListRestaurantsBinding;
import com.go4lunch2.ui.ItemClickListener;
import com.go4lunch2.ui.detail_restaurant.DetailRestaurantActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ListRestaurantsFragment extends Fragment implements ItemClickListener {

    FragmentListRestaurantsBinding binding;
    ListRestaurantsViewModel vm;

    List<RestaurantViewState> datas = new ArrayList<>();
    RecyclerView rv;

    public ListRestaurantsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MaterialToolbar toolbar = requireActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle(getString(R.string.list_restaurants_desc));
        toolbar.getMenu().getItem(0).setVisible(true);
        toolbar.getMenu().getItem(1).setVisible(true);

        binding = FragmentListRestaurantsBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListRestaurantsViewModel.class);

        rv = binding.rvListRestaurants;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(MyApplication.getInstance(), DividerItemDecoration.VERTICAL));
        RestaurantsAdapter adapter = new RestaurantsAdapter(getActivity(), datas, this);
        rv.setAdapter(adapter);

        vm.getAllRestaurantsWithOrderMediatorLD().observe(getViewLifecycleOwner(), listRestaurants -> {
            if (listRestaurants != null) {
                datas.clear();
                datas.addAll(listRestaurants);
                adapter.notifyDataSetChanged();
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onClick(View view, int position) {
        Intent intent = new Intent(MyApplication.getInstance(), DetailRestaurantActivity.class);
        intent.putExtra(DetailRestaurantActivity.RESTAURANT_SELECTED, datas.get(position).getId());
        startActivity(intent);
    }
}