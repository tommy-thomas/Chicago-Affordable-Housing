package org.affordablehousing.chi.housingapp.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.adapter.CommunityListAdapter;
import org.affordablehousing.chi.housingapp.viewmodel.PropertyListViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    public static BottomSheetFragment newInstance() {
        return new BottomSheetFragment();
    }

    private ArrayList <String> mCommunities;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_bottomsheet, container, false);

        PropertyListViewModel mPropertyListViewModel =
                ViewModelProviders.of(this).get(PropertyListViewModel.class);

        mCommunities = new ArrayList <>();

        mPropertyListViewModel.getCommunities().observe(this, communities -> {
            if (communities != null) {
                for (String community : communities) {
                    mCommunities.add(community);
                }

                final RecyclerView recyclerView = rootView.findViewById(R.id.rv_communities);
                recyclerView.setHasFixedSize(false);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                recyclerView.setLayoutManager(layoutManager);
                if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                }

                CommunityListAdapter communityListAdapter = new CommunityListAdapter(getContext(), (ArrayList <String>) mCommunities);
                recyclerView.setAdapter(communityListAdapter);
            }
        });


        return rootView;
    }
}
