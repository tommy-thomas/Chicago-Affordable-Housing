package org.affordablehousing.chi.housingapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import org.affordablehousing.chi.housingapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static org.affordablehousing.chi.housingapp.ui.PropertyTypeListFragment.PropertyTypeClickListener;


public class PropertyTypeListAdapter extends RecyclerView.Adapter<PropertyTypeListAdapter.ViewHolder> {

    private ArrayList<String> mPropertyTypes;
    private ArrayList<String> mPropertyTypeFilter;
    private Context mContext;
    private PropertyTypeClickListener propertyTypeClickListener;
    private final static String TAG = PropertyTypeListAdapter.class.getSimpleName() + "-- property list --";

    public PropertyTypeListAdapter(Context context , ArrayList<String> property_types, ArrayList<String> filter, PropertyTypeClickListener listener){
        this.mPropertyTypes = property_types;
        this.mContext = context;
        this.mPropertyTypeFilter = filter;
        propertyTypeClickListener = listener;
    }

    @NonNull
    @Override
    public PropertyTypeListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property_type_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder != null) {

            holder.cbPropertyType.setText( mPropertyTypes.get(position)  );

            holder.cbPropertyType.setContentDescription( mPropertyTypes.get(position) );

            if( mPropertyTypeFilter != null && mPropertyTypeFilter.size() > 0 ) {
                if( mPropertyTypeFilter.contains( mPropertyTypes.get(position) ) ){
                    holder.cbPropertyType.setChecked(true);
                }

            }

            holder.cbPropertyType.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    propertyTypeClickListener.onPropertyTypeSelected( mPropertyTypes.get(position) );
                }
            });


        }
    }



    @Override
    public int getItemCount() {
        return mPropertyTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cbPropertyType;

        public ViewHolder(View view) {
            super(view);
            cbPropertyType = view.findViewById(R.id.cb_property_type);
        }


    }
}
