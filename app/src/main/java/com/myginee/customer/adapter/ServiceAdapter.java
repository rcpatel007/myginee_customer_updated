package com.myginee.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.model.ServiceListModel;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.MyViewHolder> {

    private List<ServiceListModel.ServiceList> mModelList;
    private Context mContext;

    public ServiceAdapter(Context mContext, List<ServiceListModel.ServiceList> modelList) {
        mModelList = modelList;
        mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_raw, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ServiceListModel.ServiceList model = mModelList.get(position);
        holder.rbService.setText(model.getServiceName());
        holder.tvServicePrice.setText(model.getPrice());
//        holder.textView.setText(model.getText());
        if(model.getIs_selected()){
            holder.rbService.setChecked(true);
        }else {
            holder.rbService.setChecked(false);
        }
//        holder.view.setBackgroundColor(model.getIs_selected() ? Color.CYAN : Color.WHITE);
        holder.rbService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.setIs_selected(!model.getIs_selected());
                holder.rbService.setChecked(model.getIs_selected() ? true : false);
            }
        });

       /* holder.rbService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                model.setIs_selected(!model.getIs_selected());
                holder.rbService.setChecked(model.getIs_selected() ? true : false);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView textView, tvServicePrice;
        private RadioButton rbService;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
//            textView = (TextView) itemView.findViewById(R.id.tvService);
            rbService = (RadioButton) itemView.findViewById(R.id.rbService);
            tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
        }
    }

}