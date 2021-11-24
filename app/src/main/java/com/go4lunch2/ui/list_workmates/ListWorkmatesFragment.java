package com.go4lunch2.ui.list_workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.data.Repository;
import com.go4lunch2.data.model.Workmate;
import com.go4lunch2.databinding.FragmentListWorkmatesBinding;

import java.util.List;

public class ListWorkmatesFragment extends Fragment {


    FragmentListWorkmatesBinding binding;
    RecyclerView rv;
    ListWorkmatesViewModel vm;

    public ListWorkmatesFragment() {
        // Required empty public constructor
    }


    public static ListWorkmatesFragment newInstance(String param1, String param2) {
        ListWorkmatesFragment fragment = new ListWorkmatesFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentListWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListWorkmatesViewModel.class);

        rv = binding.rvListWorkmates;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));

        vm.getWorkmatesWithChoisesLiveData().observe(getViewLifecycleOwner(), workmateViewStateItems -> {
            WorkmatesAdapter adapter = new WorkmatesAdapter(getActivity(), workmateViewStateItems);
            rv.setAdapter(adapter);


        });




        return view;
    }




}