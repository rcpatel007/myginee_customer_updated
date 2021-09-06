package com.myginee.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;

import java.util.List;

public class DetailDescriptionAdapter extends RecyclerView.Adapter<DetailDescriptionAdapter.MyViewHolder> {

    private List<String> mModelList;
    private Context mContext;

    public DetailDescriptionAdapter(Context context, List<String> modelList) {
        mModelList = modelList;
        mContext = context;
    }

    @Override
    public DetailDescriptionAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_desc_raw, parent, false);
        return new DetailDescriptionAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.tvDetailDesc.setText(mModelList.get(position));
        holder.tvIndex.setText(position+1+"");
    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvDetailDesc, tvIndex;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvDetailDesc = (TextView) itemView.findViewById(R.id.tvDetailDesc);
            tvIndex = (TextView) itemView.findViewById(R.id.tvIndex);
        }

    }

}