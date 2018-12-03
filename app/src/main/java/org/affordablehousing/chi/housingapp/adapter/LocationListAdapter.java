package org.affordablehousing.chi.housingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.LocationEntity;
import org.affordablehousing.chi.housingapp.ui.LocationListFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class LocationListAdapter extends RecyclerView.Adapter <LocationListAdapter.ViewHolder> {

    Context mContext;
    ArrayList <LocationEntity> mLocationEntityList;
    List <LocationEntity> mLocationEntityListMaster;
    String mCurrrentCommunity;
    ArrayList <String> mLocationTypeFilter;
    LocationListFragment.LocationClickListener mLocationClickListener;

    public LocationListAdapter(Context context, List <LocationEntity> list, String current_community, ArrayList <String> filter, LocationListFragment.LocationClickListener listener) {
        this.mContext = context;
        this.mLocationEntityListMaster = list;
        this.mLocationEntityList = new ArrayList <>();
        this.mCurrrentCommunity = current_community;
        this.mLocationTypeFilter = filter;
        this.mLocationClickListener = listener;
        copyMasterList();
        filterList();
    }

    private void copyMasterList() {
        if (mLocationEntityList != null)
            mLocationEntityList.clear();
        for (LocationEntity locationEntity : mLocationEntityListMaster) {
            mLocationEntityList.add(locationEntity);
        }
    }

    private void filterList() {

        Iterator <LocationEntity> iter = this.mLocationEntityList.iterator();
        while (iter.hasNext()) {
            LocationEntity locationEntity = iter.next();
            if ( mLocationTypeFilter.size() > 0 && !mLocationTypeFilter.contains(locationEntity.getProperty_type()) ) {
                iter.remove();
            } else if ( !mCurrrentCommunity.equals("Community") && !locationEntity.getCommunity_area().equals(mCurrrentCommunity) ){
                iter.remove();
            } else if( locationEntity.getAddress().isEmpty()){
                iter.remove();
            }
        }

    }

    @NonNull
    @Override
    public LocationListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.location_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder != null) {
            holder.mHeader.setText(mLocationEntityList.get(position).getProperty_name());
            holder.mAddress.setText(mLocationEntityList.get(position).getAddress());

            holder.mHeader.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLocationClickListener.onLocationSelected( mLocationEntityList.get(position).getLocationId());
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mLocationEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mHeader;
        TextView mAddress;

        public ViewHolder(View view) {
            super(view);
            mHeader = view.findViewById(R.id.tv_location_list_header);
            mAddress = view.findViewById(R.id.tv_location_list_address);
        }
    }

}

