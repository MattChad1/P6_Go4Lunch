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

import com.go4lunch2.R;
import com.go4lunch2.ViewModelFactory;
import com.go4lunch2.databinding.FragmentListWorkmatesBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ListWorkmatesFragment extends Fragment {

    FragmentListWorkmatesBinding binding;
    RecyclerView rv;
    List<WorkmateViewStateItem> workmates = new ArrayList<>();
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
        MaterialToolbar toolbar = (MaterialToolbar) getActivity().findViewById(R.id.topAppBar);
        toolbar.setTitle(getString(R.string.list_workmates_desc));
        toolbar.getMenu().getItem(0).setVisible(false);
        toolbar.getMenu().getItem(1).setVisible(false);

        binding = FragmentListWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        vm = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(ListWorkmatesViewModel.class);

        rv = binding.rvListWorkmates;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        WorkmatesAdapter adapter = new WorkmatesAdapter(getActivity(), workmates);
        rv.setAdapter(adapter);

        vm.getWorkmatesViewStateItemsLiveData().observe(getViewLifecycleOwner(), workmateViewStateItems -> {
            workmates.clear();
            workmates.addAll(workmateViewStateItems);
            adapter.notifyDataSetChanged();
        });

        return view;
    }
}