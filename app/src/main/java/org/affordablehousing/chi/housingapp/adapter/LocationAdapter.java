package org.affordablehousing.chi.housingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.databinding.LocationItemBinding;
import org.affordablehousing.chi.housingapp.model.Location;
import org.affordablehousing.chi.housingapp.ui.LocationClickCallback;

import java.util.List;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

public class LocationAdapter extends RecyclerView.Adapter <LocationAdapter.LocationViewHolder> {

    List<? extends Location> mLocationList;

    private Context mContext;

    @Nullable
    private final LocationClickCallback mLocationClickCallback;

    public LocationAdapter(LocationClickCallback mLocationClickCallback, Context context) {
        this.mLocationClickCallback = mLocationClickCallback;
        mContext = context;

        setHasStableIds(true);
    }

    public void setLocationList(final List<? extends Location> locationList) {
        if (mLocationList == null) {
           mLocationList = locationList;
            notifyItemRangeInserted(0, locationList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mLocationList.size();
                }

                @Override
                public int getNewListSize() {
                    return mLocationList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mLocationList.get(oldItemPosition).getLocationId() ==
                            locationList.get(newItemPosition).getLocationId();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Location newLocation = locationList.get(newItemPosition);
                    Location oldLocation = mLocationList.get(oldItemPosition);
                    return newLocation.getLocationId() == oldLocation.getLocationId()
                            && Objects.equals(newLocation.getProperty_name(), oldLocation.getProperty_name());
                }
            });
            mLocationList = locationList;
            result.dispatchUpdatesTo(this);
        }
    }


    @Override
    public LocationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LocationItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.location_item,
                        parent, false);
        binding.setCallback(mLocationClickCallback);
        return new LocationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(LocationViewHolder holder, int position) {

        holder.binding.setLocation(mLocationList.get(position));
        holder.binding.executePendingBindings();

        holder.binding.tbFavorite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if ( isChecked ) {
                    holder.binding.tbFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_outline_favorite_24px));
                    Toast toast = Toast.makeText(mContext,
                            mLocationList.get(position).getProperty_name() + " added to favorites.",
                            Toast.LENGTH_SHORT);
                    toast.show();
                } else {
                    holder.binding.tbFavorite.setBackground(ContextCompat.getDrawable(mContext, R.drawable.ic_round_favorite_border_24px));
                    Toast toast = Toast.makeText(mContext,
                            mLocationList.get(position).getProperty_name() + " removed from favorites.",
                            Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        holder.binding.btnAddNote.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mLocationList == null ? 0 : mLocationList.size();
    }

    static class LocationViewHolder extends RecyclerView.ViewHolder {

        final LocationItemBinding binding;

        public LocationViewHolder(LocationItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
