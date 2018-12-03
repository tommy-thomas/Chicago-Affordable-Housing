package org.affordablehousing.chi.housingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.viewmodel.LoactionViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


public class LocationDetailFragment extends Fragment {

    private static final String KEY_LOCATION_ID = "location_id";
    private TextView mLocationName;
    private TextView mLocationAddress;
    private ToggleButton mToggleButtonFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_location_detail, container, false);

        mLocationName = rootView.findViewById(R.id.tv_property_name);
        mLocationAddress = rootView.findViewById(R.id.tv_location_address);

        LoactionViewModel.Factory factory = new LoactionViewModel.Factory(
                getActivity().getApplication(), getArguments().getInt(KEY_LOCATION_ID));

        final LoactionViewModel model = ViewModelProviders.of(this, factory)
                .get(LoactionViewModel.class);

        model.getObservableProperty().observe( this, propertyEntity -> {

            mLocationName.setText(propertyEntity.getProperty_name());

            mLocationAddress.setText(propertyEntity.getAddress());


        });


        return rootView;
    }

    /** Creates product fragment for specific product ID */
    public static LocationDetailFragment forProperty(int propertyId) {
        LocationDetailFragment fragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LOCATION_ID, propertyId);
        fragment.setArguments(args);
        return fragment;
    }
}
