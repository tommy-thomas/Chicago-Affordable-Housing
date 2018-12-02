package org.affordablehousing.chi.housingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.affordablehousing.chi.housingapp.R;
import org.affordablehousing.chi.housingapp.model.PropertyEntity;
import org.affordablehousing.chi.housingapp.ui.PropertyListFragment;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PropertyListAdapter extends RecyclerView.Adapter <PropertyListAdapter.ViewHolder> {

    Context mContext;
    ArrayList <PropertyEntity> mPropertyEntityList;
    List <PropertyEntity> mPropertyEntityListMaster;
    String mCurrrentCommunity;
    ArrayList <String> mPropertyTypeFilter;
    PropertyListFragment.PropertyClickListener mPropertyClickListener;

    public PropertyListAdapter(Context context, List <PropertyEntity> list, String current_community, ArrayList <String> filter, PropertyListFragment.PropertyClickListener listener) {
        this.mContext = context;
        this.mPropertyEntityListMaster = list;
        this.mPropertyEntityList = new ArrayList <>();
        this.mCurrrentCommunity = current_community;
        this.mPropertyTypeFilter = filter;
        this.mPropertyClickListener = listener;
        copyMasterList();
        filterList();
    }

    private void copyMasterList() {
        if (mPropertyEntityList != null)
            mPropertyEntityList.clear();
        for (PropertyEntity propertyEntity : mPropertyEntityListMaster) {
            mPropertyEntityList.add(propertyEntity);
        }
    }

    private void filterList() {

        Iterator <PropertyEntity> iter = this.mPropertyEntityList.iterator();
        while (iter.hasNext()) {
            PropertyEntity propertyEntity = iter.next();
            if ( mPropertyTypeFilter.size() > 0 && !mPropertyTypeFilter.contains(propertyEntity.getProperty_type()) ) {
                iter.remove();
            } else if ( !mCurrrentCommunity.equals("Community") && !propertyEntity.getCommunity_area().equals(mCurrrentCommunity) ){
                iter.remove();
            } else if( propertyEntity.getAddress().isEmpty()){
                iter.remove();
            }
        }

    }

    @NonNull
    @Override
    public PropertyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder != null) {
            holder.mHeader.setText(mPropertyEntityList.get(position).getProperty_name());
            holder.mAddress.setText(mPropertyEntityList.get(position).getAddress());
//
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//
//            });
        }
    }

    @Override
    public int getItemCount() {
        return mPropertyEntityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mHeader;
        TextView mAddress;

        public ViewHolder(View view) {
            super(view);
            mHeader = view.findViewById(R.id.tv_property_list_header);
            mAddress = view.findViewById(R.id.tv_property_list_address);
        }
    }

}

