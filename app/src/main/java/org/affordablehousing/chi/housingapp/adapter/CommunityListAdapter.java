package org.affordablehousing.chi.housingapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.affordablehousing.chi.housingapp.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class CommunityListAdapter extends RecyclerView.Adapter<CommunityListAdapter.ViewHolder> {

    private ArrayList<String> communities;
    private Context context;
    private final static String TAG = CommunityListAdapter.class.getSimpleName() + "-- community list --";

    public CommunityListAdapter( Context context , ArrayList<String> communities){
        this.communities = communities;
        this.context = context;
    }

    @NonNull
    @Override
    public CommunityListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.community_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder != null) {

            holder.tvCommunity.setText( communities.get(position)  );
            Log.d(TAG , communities.get(position)  );

//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Step currentStep = steps.get(position);
//                    int previousStepPos = position != 0 ? position - 1 : -1;
//                    int nextStepPos = ( position < steps.size()-1 ) ? position + 1 : -1;
//                    stepClickListener.onStepSelected(currentStep , previousStepPos, nextStepPos);
//                }
//            });

        }
    }



    @Override
    public int getItemCount() {
        return communities.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvCommunity;

        public ViewHolder(View view) {
            super(view);
            tvCommunity = view.findViewById(R.id.tv_community);
        }


    }
}
