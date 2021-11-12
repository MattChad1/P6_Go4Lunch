package com.go4lunch2.ui.list_restaurants;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.model.Repository;
import com.go4lunch2.model.Restaurant;
import com.go4lunch2.databinding.FragmentListRestaurantsBinding;

import java.util.List;

public class ListRestaurantsFragment extends Fragment {

    FragmentListRestaurantsBinding binding;
    List<Restaurant> listRestaurants = Repository.FAKE_LIST_RESTAURANTS;
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
        binding = FragmentListRestaurantsBinding.inflate(inflater, container, false);
        rv = binding.rvListRestaurants;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        initList();
        View view = binding.getRoot();
        return view;
    }

    public void initList() {
        RestaurantsAdapter adapter = new RestaurantsAdapter(getActivity(), listRestaurants);
        rv.setAdapter(adapter);

    }
}