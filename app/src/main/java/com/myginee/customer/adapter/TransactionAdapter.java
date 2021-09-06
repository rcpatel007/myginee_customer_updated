package com.myginee.customer.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.model.WalletTrans;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {

    private static final String TAG = "TransactionAdapter";
    Context context;
    Activity activity;
    ArrayList<WalletTrans.Datum> categories;
    LayoutInflater inflater;

    public TransactionAdapter(Context context, Activity activity, ArrayList<WalletTrans.Datum> categories) {
        this.context = context;
        this.categories = categories;
        this.activity = activity;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvCustID, tvCatPrice, tv_response_message;

        public ViewHolder(View view) {
            super(view);
            this.tvCustID = (TextView) itemView.findViewById(R.id.tvCustID);
            this.tvCatPrice = (TextView) itemView.findViewById(R.id.tvWalletPrice);
            this.tv_response_message = (TextView)itemView.findViewById(R.id.transaction_raw_tv_response_message);
        }

    }

    @Override
    public TransactionAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.transaction_raw, parent, false);
        return new TransactionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final TransactionAdapter.ViewHolder holder, final int position) {

        TextView tvOrProName = holder.tvCustID;
        TextView tvCatPrice = holder.tvCatPrice;
        TextView tv_res_sms = holder.tv_response_message;

        if(categories.get(position).getSent() == true){
            tvCatPrice.setTextColor(ContextCompat.getColor(context, R.color.signup_btn_color));
            tv_res_sms.setText("Debited");
        }else if(categories.get(position).getReceived() == true){
            tvCatPrice.setTextColor(ContextCompat.getColor(context, R.color.app_color));
            tv_res_sms.setText("Credited");
        }

//        tvOrProName.setText(categories.get(position).getCreatedAt()+"");
//        formateDateFromstring("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd, MMM yyyy", categories.get(position).getCreatedAt());
        tvOrProName.setText(formateDateFromstring("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", "dd, MMM yyyy | HH:mm:ss", categories.get(position).getCreatedAt())+"");
        tvCatPrice.setText(categories.get(position).getWalletAmount()+" Rs.");
    }
    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate){

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, java.util.Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, java.util.Locale.getDefault());

        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);

        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }
    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}

