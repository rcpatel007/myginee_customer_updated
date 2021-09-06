package com.myginee.customer.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.activity.SignInActivity;
import com.myginee.customer.fragment.ProductDetailFragment;
import com.myginee.customer.model.ProductSubCatSubModel;
import com.myginee.customer.model.WishListModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class ProductSubCatSubAdapter extends RecyclerView.Adapter<ProductSubCatSubAdapter.ViewHolder> {

    private static final String TAG = "ProductSubCatSubAdapter";
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<ProductSubCatSubModel.Data> categories;
    LayoutInflater inflater;
    ArrayList<WishListModel.Datum> wishListModelArrayList;
    private ProgressDialog mProgressDialog;

    public ProductSubCatSubAdapter(Context context, Fragment fragment,
                                   FragmentManager fragmentManager, ArrayList<ProductSubCatSubModel.Data> categories,
                                   ArrayList<WishListModel.Datum> wishListModelArrayList) {
        this.context = context;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
        this.categories = categories;
        this.wishListModelArrayList = wishListModelArrayList;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgProductIcon;

        TextView txtViewMore, tvCatPrice, tvDetail;
        ImageView imageProduct, imgLike;

        public ViewHolder(View view) {
            super(view);
            this.txtViewMore = (TextView) itemView.findViewById(R.id.txtViewMore);
            this.tvCatPrice = (TextView) itemView.findViewById(R.id.tvCatPrice);
            this.tvDetail = (TextView) itemView.findViewById(R.id.tvDetail);
            this.imageProduct = (ImageView) itemView.findViewById(R.id.imageProduct);
            imgLike = (ImageView) view.findViewById(R.id.imgLike);

//            imgProductIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
         /*   if(v.getId() == R.id.imgProductIcon){
                CategoryProductFragment categoryProductFragment = new CategoryProductFragment();
                Bundle bundles = new Bundle();
                HomeModel.Categories category = categories.get(getAdapterPosition());
                bundles.putString("category_id", category.category_id);
                bundles.putString("name", category.name);
                bundles.putBoolean("category", true);
                categoryProductFragment.setArguments(bundles);

                fragmentManager.beginTransaction().replace(R.id.frameContainer, categoryProductFragment).addToBackStack(categoryProductFragment.getClass().getName()).commit();

            }*/
        }
    }

    @Override
    public ProductSubCatSubAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.product_raw, parent, false);
        return new ProductSubCatSubAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ProductSubCatSubAdapter.ViewHolder holder, final int position) {

        TextView txtViewMore = holder.txtViewMore;
        TextView tvDetail = holder.tvDetail;
        TextView tvCatPrice = holder.tvCatPrice;
        ImageView imageProduct = holder.imageProduct;
        final ImageView imgLike = holder.imgLike;


        for (int j = 0; j < wishListModelArrayList.size(); j++) {
            if (wishListModelArrayList.get(j).getId().equals(categories.get(position).getId())) {
                imgLike.setTag("like");
                imgLike.setImageResource(R.drawable.ic_fav);
            } else {
                imgLike.setTag("dislike");
                imgLike.setImageResource(R.drawable.ic_unfav);
            }
        }

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GineePref.getAccessToken(context).equals("")) {
                    if (imgLike.getTag().toString().equals("like")) {
                        imgLike.setTag("dislike");
                        imgLike.setImageResource(R.drawable.ic_unfav);
                        deleteProductFromWishList(position);

                    } else {
                        imgLike.setTag("like");
                        imgLike.setImageResource(R.drawable.ic_fav);
                        addProductToWishList(position);
                    }
                } else {
                    Intent signOutIntent = new Intent(context, SignInActivity.class);
                    context.startActivity(signOutIntent);
//                    ((Activity)context).finish();
                }

            }
        });

        tvDetail.setText(categories.get(position).getName());
        tvCatPrice.setText("Rs. " + categories.get(position).getPrice());
        Picasso.with(context)
                .load(categories.get(position).getImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imageProduct);

        holder.txtViewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProductDetailFragment productDetailFragment = new ProductDetailFragment();
                Bundle bundles = new Bundle();
                bundles.putSerializable("sub_category_name", categories.get(position).getName());
                bundles.putSerializable("sub_category_id", categories.get(position).getProductSubCategoryId());
                bundles.putSerializable("product_id", categories.get(position).getProductCategoryId());
                bundles.putSerializable("_id", categories.get(position).getId());
                productDetailFragment.setArguments(bundles);
                fragmentManager.beginTransaction().replace(R.id.frame_container, productDetailFragment)
                        .addToBackStack(productDetailFragment.getClass().getName()).commit();
//                fragmentManager.beginTransaction().addToBackStack(productDetailFragment.getClass().getName());

            }
        });

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void addProductToWishList(int position) {

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().addProductToWishList(GineePref.getAccessToken(context),
                categories.get(position).getId());
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {

                            JSONObject item = jsonObject.getJSONObject("data");
                            if (item != null) {
                                Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                notifyDataSetChanged();
                            }

                        } else {
                            Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void deleteProductFromWishList(final int adapterPosition) {

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();

        Call<ResponseBody> removeProductFromWishListCall = GineeAppApi.api().removeFromWishListProducts(GineePref.getAccessToken(context),
                categories.get(adapterPosition).getId());
        removeProductFromWishListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {

                            JSONObject item = jsonObject.getJSONObject("data");
                            if (item != null) {
                                notifyDataSetChanged();
                                Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }

                        } else {
                            Toast.makeText(context, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(context, context.getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, context.getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

}
