package org.affordablehousing.chi.housingapp.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.adapter.LocationListAdapter;
import org.affordablehousing.chi.housingapp.viewmodel.LoactionListViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LocationListFragment extends Fragment {

    LocationClickListener mLocationClickListener;

    public interface LocationClickListener {
        void onLocationSelected(int id );
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mLocationClickListener = (LocationClickListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement LocationClickListener.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_location_list, container, false);

        ArrayList <String> listFilter = getArguments().getStringArrayList("LIST_FILTER");
        String current_community = getArguments().getString("CURRENT_COMMUNITY");

        LoactionListViewModel loactionListViewModel = ViewModelProviders.of(this).get(LoactionListViewModel.class);

        loactionListViewModel.getProperties().observe(this, properties -> {

            if (properties != null) {
                final RecyclerView recyclerView = rootView.findViewById(R.id.rv_location_list);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                recyclerView.setLayoutManager(layoutManager);
                if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                }


                LocationListAdapter locationListAdapter = new LocationListAdapter(getContext(), properties, current_community, listFilter, mLocationClickListener,loactionListViewModel);
                recyclerView.setAdapter(locationListAdapter);
            }
        });
        return rootView;
    }
}
