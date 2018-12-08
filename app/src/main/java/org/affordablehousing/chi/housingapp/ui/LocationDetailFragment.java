package org.affordablehousing.chi.housingapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.databinding.FragmentLocationDetailBinding;
import org.affordablehousing.chi.housingapp.model.Location;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.viewmodel.LocationViewModel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


public class LocationDetailFragment extends Fragment {

    private static final String KEY_LOCATION_ID = "location-id";
    private FragmentLocationDetailBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate( inflater , R.layout.fragment_location_detail, container, false );

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

        ToggleButton fav = mBinding.getRoot().findViewById(R.id.tb_favorite);

        fav.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                int locationId = locationViewModel.getObservableLocation().getValue().getLocationId();
                locationViewModel.setFavorite( locationId , isChecked);
            }
        });

        (mBinding.getRoot().findViewById(R.id.btn_add_note)).setVisibility(View.VISIBLE);

        mBinding.setLocationViewModel(locationViewModel);


       // mBinding.setCallback(mLocationClickCallback);

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

    private final LocationClickCallback mLocationClickCallback = new LocationClickCallback() {

        @Override
        public void onClick(Location location) {

            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                ((MapsActivity) getActivity()).show(location);
            }
        }

        @Override
        public void onFavoriteChecked(Location location) {
            if (getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                boolean isFavorite = location.getIs_favorite() ? false : true;
                ((MapsActivity) getActivity()).favorite(location, isFavorite);
            }
        }


    };

    /** Creates location fragment for specific product ID */
    public static LocationDetailFragment forLocation(int propertyId) {
        LocationDetailFragment fragment = new LocationDetailFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_LOCATION_ID, propertyId);
        fragment.setArguments(args);
        return fragment;
    }
}
