package com.myginee.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.model.AccServiceListModel;

import java.util.List;

public class DialogServiceOrderAdapter extends RecyclerView.Adapter<DialogServiceOrderAdapter.MyViewHolder> {

    private List<AccServiceListModel.ServiceList> mModelList;
    private Context mContext;

    public DialogServiceOrderAdapter(Context mContext, List<AccServiceListModel.ServiceList> modelList) {
        mModelList = modelList;
        mContext = mContext;
    }

    @Override
    public DialogServiceOrderAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_raw, parent, false);
        return new DialogServiceOrderAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DialogServiceOrderAdapter.MyViewHolder holder, int position) {
        final AccServiceListModel.ServiceList model = mModelList.get(position);

        holder.rbService.setVisibility(View.GONE);
        holder.tvServiceName.setVisibility(View.VISIBLE);
        holder.tvServiceName.setText(model.getServiceName());
        holder.tvServicePrice.setText("Rs. "+model.getPrice());

    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvServiceName, tvServicePrice;
        private RadioButton rbService;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            rbService = (RadioButton) itemView.findViewById(R.id.rbService);
            tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
            tvServiceName = (TextView) itemView.findViewById(R.id.tvServiceName);
        }
    }

}