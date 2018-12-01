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
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PropertyListAdapter extends RecyclerView.Adapter<PropertyListAdapter.ViewHolder> {

    Context mContext;
    List <PropertyEntity> mPropertyEntityList;
    String mCurrrentCommunity;
    ArrayList <String> mPropertyTypeFilter;
    PropertyListFragment.PropertyClickListener mPropertyClickListener;

    public PropertyListAdapter(Context context, List <PropertyEntity> list, String current_community, ArrayList <String> filter, PropertyListFragment.PropertyClickListener listener) {
        this.mContext = context;
        this.mPropertyEntityList = list;
        this.mCurrrentCommunity = current_community;
        this.mPropertyTypeFilter = filter;
        this.mPropertyClickListener = listener;
    }


    @NonNull
    @Override
    public PropertyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder != null
                && (mPropertyTypeFilter.contains(mPropertyEntityList.get(position).getProperty_type()) || mPropertyTypeFilter.isEmpty())) {
            holder.mHeader.setText(mPropertyEntityList.get(position).getProperty_name() );
            holder.mAddress.setText(mPropertyEntityList.get(position).getAddress());
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

