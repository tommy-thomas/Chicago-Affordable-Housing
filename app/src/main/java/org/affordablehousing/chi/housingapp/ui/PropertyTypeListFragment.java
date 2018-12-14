package org.affordablehousing.chi.housingapp.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.adapter.PropertyTypeListAdapter;
import org.affordablehousing.chi.housingapp.viewmodel.LocationListViewModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PropertyTypeListFragment extends Fragment {

    PropertyTypeClickListener propertyTypeClickListener;

    PropertyTypeListAdapter mPropertyTypeListAdapter;

    private final String KEY_PROPERTY_LIST_FILTER = "property-filter-list";

    public interface PropertyTypeClickListener {

        void onPropertyTypeSelected(String propertyType);

    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            propertyTypeClickListener = (PropertyTypeClickListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PropertyTypeClickListener.");
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        final View rootView = inflater.inflate(R.layout.fragment_property_type_list, container, false);


        ArrayList <String> listFilter = getArguments().getStringArrayList(KEY_PROPERTY_LIST_FILTER);


        LocationListViewModel locationListViewModel = ViewModelProviders.of(this).get(LocationListViewModel.class);


        locationListViewModel.getPropertyTypes().observe(this, propertyTypes -> {

            if (propertyTypes != null) {
                final RecyclerView recyclerView = rootView.findViewById(R.id.rv_property_type);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
                recyclerView.setLayoutManager(layoutManager);
                DisplayMetrics metrics = new DisplayMetrics();
                if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                } else if  (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT && metrics.widthPixels < 600 ){
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                }

                mPropertyTypeListAdapter = new PropertyTypeListAdapter(getContext(), (ArrayList <String>) propertyTypes, listFilter, propertyTypeClickListener);
                recyclerView.setAdapter(mPropertyTypeListAdapter);
            }


        });



        return rootView;
    }
}
