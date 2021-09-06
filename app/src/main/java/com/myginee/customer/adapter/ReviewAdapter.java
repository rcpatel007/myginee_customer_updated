package com.myginee.customer.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.fragment.DetailFragment;
import com.myginee.customer.model.ServiceListModel;
import com.myginee.customer.utils.CommonMethod;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.MyViewHolder> {

    private List<ServiceListModel.ServiceList> mModelList;
    private Context context;
    DetailFragment detailFragment;


    public ReviewAdapter(Context mContext, List<ServiceListModel.ServiceList> modelList,
                         DetailFragment fragment) {
        mModelList = modelList;
        context = mContext;
        detailFragment = fragment;
    }

    @Override
    public ReviewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_service_raw, parent, false);
        return new ReviewAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ReviewAdapter.MyViewHolder holder, int position) {
        final ServiceListModel.ServiceList model = mModelList.get(position);
        holder.tvServiceName.setText(model.getServiceName());
        holder.tvServicePrice.setText(model.getPrice() + " Rs. ");
        holder.tvPriceMrpService.setText(model.getMrp() + "");
        if (Long.parseLong(model.getMrp()) > 0) {
            holder.tvServiceDisc.setText(((Long.parseLong(model.getMrp()) - Long.parseLong(model.getPrice())) / Long.parseLong(model.getMrp()) * 100) + " % off");
        }
        holder.tvPriceMrpService.setPaintFlags(holder.tvPriceMrpService.getPaintFlags()
                | Paint.STRIKE_THRU_TEXT_FLAG);

        holder.rvDetailService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonMethod.showPopupWindow(context, holder.rvDetailService, model.getDescription());
            }
        });

        if (model.getIs_added_to_cart().equals(true)) {
            holder.addServiceStatus.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            holder.addServiceStatus.setText("Added");
        } else {
            holder.addServiceStatus.setText("Add");
        }
        if (!model.getImage_url().equals("")) {
            Picasso.with(context)
                    .load(model.getImage_url())
                    .placeholder(R.drawable.ic_plumber)
                    .error(R.drawable.ic_plumber)
                    .into(holder.imgService);
        }
        holder.addServiceStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!model.getIs_added_to_cart().equals(true)) {
                    detailFragment.callAPIForAddServiceToCart(model);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mModelList == null ? 0 : mModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView tvServicePrice, tvServiceName, tvPriceMrpService, tvServiceDisc,
                rvDetailService, addServiceStatus;
        private RadioButton rbService;
        private ImageView imgService;

        private MyViewHolder(View itemView) {
            super(itemView);
            view = itemView;
            tvServicePrice = (TextView) itemView.findViewById(R.id.tvServicePrice);
            tvServiceName = (TextView) itemView.findViewById(R.id.tvServiceName);
            tvPriceMrpService = (TextView) itemView.findViewById(R.id.tvPriceMrpService);
            tvServiceDisc = (TextView) itemView.findViewById(R.id.tvServiceDisc);
            rvDetailService = (TextView) itemView.findViewById(R.id.rvDetailService);
            addServiceStatus = (TextView) itemView.findViewById(R.id.addServiceStatus);
            imgService = (ImageView) itemView.findViewById(R.id.imgService);
        }
    }

}