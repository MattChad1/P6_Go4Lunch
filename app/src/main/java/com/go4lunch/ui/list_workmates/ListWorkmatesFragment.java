package com.go4lunch.ui.list_workmates;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.go4lunch.model.Repository;
import com.go4lunch.model.Workmate;
import com.go4lunch.databinding.FragmentListWorkmatesBinding;

import java.util.List;

public class ListWorkmatesFragment extends Fragment {


    FragmentListWorkmatesBinding binding;
    List<Workmate>  listWorkmates = Repository.FAKE_LIST_WORKMATES;
    RecyclerView rv;

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
        rv = binding.rvListWorkmates;
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        initList();
        return view;
    }

    private void initList() {
        WorkmatesAdapter adapter = new WorkmatesAdapter(getActivity(), listWorkmates);
        rv.setAdapter(adapter);
    }



}