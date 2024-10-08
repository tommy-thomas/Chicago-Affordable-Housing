package org.affordablehousing.chi.housing.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.affordablehousing.chi.housing.R;
import org.affordablehousing.chi.housing.adapter.LocationAdapter;
import org.affordablehousing.chi.housing.databinding.FragmentLocationListBinding;
import org.affordablehousing.chi.housing.model.Location;
import org.affordablehousing.chi.housing.model.LocationEntity;
import org.affordablehousing.chi.housing.service.LocationWidgetService;
import org.affordablehousing.chi.housing.viewmodel.LocationListViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LocationListFragment extends Fragment {

    LocationAdapter mLocationAdapter;

    private FragmentLocationListBinding mBinding;

    private final String KEY_PROPERTY_LIST_FILTER = "property-filter-list";
    private final String KEY_CURRENT_COMMUNITY = "current-community";
    private String CURRENT_COMMUNITY = "";
    private List <String> LIST_FILTER;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_location_list, container, false);

        LIST_FILTER = getArguments().getStringArrayList(KEY_PROPERTY_LIST_FILTER);
        CURRENT_COMMUNITY = getArguments().getString(KEY_CURRENT_COMMUNITY);

        if (CURRENT_COMMUNITY.equals("showFavorites")){
            mBinding.rvLocationList.setContentDescription("Favorites List");
        } else {
            mBinding.rvLocationList.setContentDescription("Location List");
        }

        mLocationAdapter = new LocationAdapter(mLocationClickCallback, getContext());
        mBinding.rvLocationList.setAdapter(mLocationAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final LocationListViewModel viewModel =
                ViewModelProviders.of(this).get(LocationListViewModel.class);

        if ((CURRENT_COMMUNITY.equals("") || CURRENT_COMMUNITY.equals("Community")) && (LIST_FILTER == null || LIST_FILTER.size() == 0)) {
            subscribeUi(viewModel.getLocations());
        } else if ((!CURRENT_COMMUNITY.equals("") && !CURRENT_COMMUNITY.matches("Community|showFavorites")) && (LIST_FILTER == null || LIST_FILTER.size() == 0)) {
            subscribeUi(viewModel.loadLocationsByCommunity(CURRENT_COMMUNITY));
        } else if ((!CURRENT_COMMUNITY.equals("") && !CURRENT_COMMUNITY.matches("Community|showFavorites")) && (LIST_FILTER != null && LIST_FILTER.size() > 0)) {
            subscribeUi(viewModel.loadLocationsByCommunityAndPropertyType(CURRENT_COMMUNITY, LIST_FILTER));
        } else if ((CURRENT_COMMUNITY.equals("") || CURRENT_COMMUNITY.equals("Community")) && (LIST_FILTER != null && LIST_FILTER.size() > 0)) {
            subscribeUi(viewModel.loadLocationsByPropertyType(LIST_FILTER));
        } else if (CURRENT_COMMUNITY.equals("showFavorites")) {
            subscribeUi(viewModel.getFavorites());
        }
    }

    private void subscribeUi(LiveData <List <LocationEntity>> liveData) {
        // Update the list when the data changes
        liveData.observe(this, new Observer <List <LocationEntity>>() {
            @Override
            public void onChanged(@Nullable List <LocationEntity> locationEntities) {
                if (locationEntities != null) {
                    mBinding.setIsLoading(false);
                    mLocationAdapter.setLocationList(locationEntities);
                } else {
                    mBinding.setIsLoading(true);
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.

                /* hide no results view */
                if (locationEntities == null || locationEntities.size() == 0) {
                    mBinding.tvNoResults.setVisibility(View.VISIBLE);
                } else {
                    mBinding.tvNoResults.setVisibility(View.GONE);
                }
                mBinding.executePendingBindings();
            }
        });
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
                LocationWidgetService.notifyServiceUpdateLocationWidget(getContext());
            }
        }


    };
}
