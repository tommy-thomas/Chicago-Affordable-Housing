package org.affordablehousing.chi.housingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.databinding.FragmentLocationDetailBinding;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.viewmodel.LocationViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


public class LocationDetailFragment extends Fragment {

    private static final String KEY_LOCATION_ID = "location_id";
    private final String KEY_SHOW_LOCATION = "show-location";
    private FragmentLocationDetailBinding mBinding;

    private TextView mLocationName;
    private TextView mLocationAddress;
    private ToggleButton mToggleButtonFavorite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate( inflater , R.layout.fragment_location_detail, container, false );


        //final View rootView = inflater.inflate(R.layout.fragment_location_detail, container, false);

//        mLocationName = rootView.findViewById(R.id.tv_property_name);
//        mLocationAddress = rootView.findViewById(R.id.tv_location_address);
//
//        LocationViewModel.Factory factory = new LocationViewModel.Factory(
//                getActivity().getApplication(), getArguments().getInt(KEY_LOCATION_ID));
//
//        final LocationViewModel model = ViewModelProviders.of(this, factory)
//                .get(LocationViewModel.class);
//
//        model.getObservableLocation().observe( this, propertyEntity -> {
//
//            mLocationName.setText(propertyEntity.getProperty_name());
//
//            mLocationAddress.setText(propertyEntity.getAddress());
//
//
//        });


        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        LocationViewModel.Factory factory = new LocationViewModel.Factory(
                getActivity().getApplication(), getArguments().getInt(KEY_LOCATION_ID));

        final LocationViewModel locationViewModel =
                ViewModelProviders.of(this, factory)
                        .get(LocationViewModel.class);

        mBinding.setLocationViewModel(locationViewModel);

        subscribeToModel(locationViewModel);


    }

    private void subscribeToModel(final LocationViewModel model) {

        // Observe product data
        model.getObservableLocation().observe(this, new Observer<LocationEntity>() {
            @Override
            public void onChanged(@Nullable LocationEntity locationEntity) {
                model.setLocation(locationEntity);
            }
        });

//        // Observe comments
//        model.getComments().observe(this, new Observer<List<CommentEntity>>() {
//            @Override
//            public void onChanged(@Nullable List<CommentEntity> commentEntities) {
//                if (commentEntities != null) {
//                    mBinding.setIsLoading(false);
//                    mCommentAdapter.setCommentList(commentEntities);
//                } else {
//                    mBinding.setIsLoading(true);
//                }
//            }
//        });
    }

    /** Creates location fragment for specific product ID */
    public static LocationDetailFragment forLocation(int propertyId) {
        LocationDetailFragment fragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LOCATION_ID, propertyId);
        fragment.setArguments(args);
        return fragment;
    }
}
