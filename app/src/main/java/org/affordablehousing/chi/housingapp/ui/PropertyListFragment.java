package org.affordablehousing.chi.housingapp.ui;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.adapter.PropertyListAdapter;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;
import org.affordablehousing.chi.housingapp.viewmodel.PropertyListViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class PropertyListFragment extends Fragment {

    PropertyClickListener mPropertyClickListener;

    public interface PropertyClickListener {
        void onPropertySelected();
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mPropertyClickListener = (PropertyClickListener) context;

        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement PropertyClickListener.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_property_list, container, false);

        ArrayList<String> listFilter = getArguments().getStringArrayList("LIST_FILTER");
        String current_community = getArguments().getString("CURRENT_COMMUNITY");

        PropertyListViewModel propertyListViewModel = ViewModelProviders.of(this).get(PropertyListViewModel.class);


        propertyListViewModel.getProperties().observe(this, properties -> {

            if (properties!= null) {
                final RecyclerView recyclerView = rootView.findViewById(R.id.rv_property_list);
                recyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(), 1);
                recyclerView.setLayoutManager(layoutManager);
                if (getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
                } else {
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                }

                if( !listFilter.isEmpty() || !current_community.isEmpty()){

                    filterList(properties, listFilter, current_community);
                }
                PropertyListAdapter propertyListAdapter = new PropertyListAdapter(getContext(), properties, current_community, listFilter, mPropertyClickListener);
                recyclerView.setAdapter(propertyListAdapter);
            }
        });
        return rootView;
    }

    private List<PropertyEntity> filterList(List<PropertyEntity> list , ArrayList<String> filter, String community){
        for( PropertyEntity propertyEntity : list ){
            if( !filter.isEmpty() && !community.isEmpty()
                    && !filter.contains(propertyEntity.getProperty_type())
                    && propertyEntity.getCommunity_area() != community){
                list.remove(propertyEntity);

            } else if(filter.isEmpty() && !community.isEmpty()
                    && propertyEntity.getCommunity_area() != community){
                list.remove(propertyEntity);

            } else if(!filter.isEmpty() && community.isEmpty()
                    && !filter.contains(propertyEntity.getProperty_type()) ){
                list.remove(propertyEntity);
            }
        }
        return list;
    }
}
