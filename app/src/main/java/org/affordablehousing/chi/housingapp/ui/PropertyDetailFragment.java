package org.affordablehousing.chi.housingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.viewmodel.PropertyViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;


public class PropertyDetailFragment extends Fragment {

    private static final String KEY_PROPERTY_ID = "property_id";
    private TextView mPropertyName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_property_detail, container, false);

        mPropertyName = rootView.findViewById(R.id.tv_property_name);

        PropertyViewModel.Factory factory = new PropertyViewModel.Factory(
                getActivity().getApplication(), getArguments().getInt(KEY_PROPERTY_ID));

        final PropertyViewModel model = ViewModelProviders.of(this, factory)
                .get(PropertyViewModel.class);

        model.getObservableProperty().observe( this, propertyEntity -> {

            mPropertyName.setText(propertyEntity.getProperty_name());

            Toast toast = Toast.makeText(getActivity(), propertyEntity.getProperty_name(), Toast.LENGTH_LONG);
            toast.show();

        });


        return rootView;
    }

    /** Creates product fragment for specific product ID */
    public static PropertyDetailFragment forProperty(int propertyId) {
        PropertyDetailFragment fragment = new PropertyDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_PROPERTY_ID, propertyId);
        fragment.setArguments(args);
        return fragment;
    }
}
