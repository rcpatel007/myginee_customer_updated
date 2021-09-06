package com.myginee.customer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;

import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {
    Context context;
    List<String> sliders;
    LayoutInflater inflater;

    private static final String TAG = "ImageListAdapter";

    public ImageListAdapter(Context context, List<String> sliders) {
        this.context = context;
        this.sliders = sliders;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvServiceName;

        public ViewHolder(View view) {
            super(view);
            this.tvServiceName = (TextView) view.findViewById(R.id.tvServiceName);
        }

    }

    @Override
    public ImageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.service_rv_raw, parent, false);
        return new ImageListAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ImageListAdapter.ViewHolder holder, final int position) {

        TextView tvServiceName = holder.tvServiceName;
       tvServiceName.setText(sliders.get(position));

    }

    @Override
    public int getItemCount() {
        return sliders.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}
