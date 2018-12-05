package org.affordablehousing.chi.housingapp.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.adapter.LocationAdapter;
import org.affordablehousing.chi.housingapp.adapter.LocationListAdapter;
import org.affordablehousing.chi.housingapp.databinding.LocationListItemBinding;
import org.affordablehousing.chi.housingapp.model.Location;
import org.affordablehousing.chi.housingapp.viewmodel.LocationListViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LocationListFragment extends Fragment {

    LocationClickListener mLocationClickListener;
    LocationAdapter mLocationAdapter;
    private LocationListItemBinding mBinding;
    private final String KEY_LIST_FILTER = "list-filter";
    private final String KEY_CURRENT_COMMUNITY = "current-community";

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
        mBinding = DataBindingUtil.inflate( inflater , R.layout.fragment_location_list, container, false );

        mLocationAdapter = new LocationAdapter(mLocationListItemCallback);

        ArrayList <String> listFilter = getArguments().getStringArrayList(KEY_LIST_FILTER);
        String current_community = getArguments().getString(KEY_CURRENT_COMMUNITY);

        LocationListViewModel locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);

        locationListViewModel.getProperties().observe(this, properties -> {

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


                LocationListAdapter locationListAdapter = new LocationListAdapter(getContext(), properties, current_community, listFilter, mLocationClickListener, locationListViewModel);
                recyclerView.setAdapter(locationListAdapter);
            }
        });
        return rootView;
    }

    private final LocationListItemCallback mLocationListItemCallback = new LocationListItemCallback() {
        @Override
        public void onClick(Location location) {

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ((MapsActivity) getActivity()).show(location);
            }
        }
    };
}
