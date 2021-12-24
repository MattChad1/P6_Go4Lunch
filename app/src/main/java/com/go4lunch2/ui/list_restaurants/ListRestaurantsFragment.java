package com.go4lunch2.ui.list_restaurants;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.databinding.FragmentListRestaurantsBinding;
import com.go4lunch2.ui.main_activity.MainActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ListRestaurantsFragment extends Fragment {

    String TAG = "MyLog ListRestaurantsFragment";
    FragmentListRestaurantsBinding binding;
    ListRestaurantsViewModel vm;

    List<RestaurantViewState> datas = new ArrayList<>();
    RecyclerView rv;

    public ListRestaurantsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ListRestaurantsFragment newInstance() {
        ListRestaurantsFragment fragment = new ListRestaurantsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle(getString(R.string.list_restaurants_desc));
        toolbar.getMenu().getItem(0).setVisible(true);
        toolbar.getMenu().getItem(1).setVisible(true);

        binding = FragmentListRestaurantsBinding.inflate(inflater, container, false);

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListRestaurantsViewModel.class);

        rv = binding.rvListRestaurants;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        RestaurantsAdapter adapter = new RestaurantsAdapter(getActivity(), datas);
        rv.setAdapter(adapter);

        vm.getAllRestaurantsWithOrderMediatorLD().observe(getViewLifecycleOwner(), listRestaurants -> {
//        vm.getAllRestaurantsViewStateLD().observe(getViewLifecycleOwner(), listRestaurants -> {
            if (listRestaurants != null) {
                Log.i(TAG, "onCreateView: getAllRestaurantsWithOrderMediatorLD()");
                datas.clear();
                datas.addAll(listRestaurants);
                adapter.notifyDataSetChanged();
            }
        });

        View view = binding.getRoot();
        return view;
    }


}