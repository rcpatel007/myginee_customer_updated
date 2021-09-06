package com.myginee.customer.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.activity.SignInActivity;
import com.myginee.customer.model.WishListModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public class WishListAdapter extends RecyclerView.Adapter<WishListAdapter.ViewHolder> {

    private static final String TAG = "WishListAdapter";
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<WishListModel.Datum> categories;
    LayoutInflater inflater;
    private ProgressDialog mProgressDialog;


    public WishListAdapter(Context context, Fragment fragment,
                           FragmentManager fragmentManager, ArrayList<WishListModel.Datum> categories) {
        this.context = context;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
        this.categories = categories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageWishListProduct;

        TextView txtViewMore, txtAddToCart, txtDeleteFromWishLsit, tvNameWishListProduct, tvPriceWishListProduct;

        public ViewHolder(View view) {
            super(view);
            this.txtAddToCart = (TextView) itemView.findViewById(R.id.txtAddToCart);
            this.tvNameWishListProduct = (TextView) itemView.findViewById(R.id.tvNameWishListProduct);
            this.tvPriceWishListProduct = (TextView) itemView.findViewById(R.id.tvPriceWishListProduct);
            this.txtDeleteFromWishLsit = (TextView) itemView.findViewById(R.id.txtDeleteFromWishLsit);

            txtAddToCart.setOnClickListener(this);
            txtDeleteFromWishLsit.setOnClickListener(this);
            imageWishListProduct = (ImageView) view.findViewById(R.id.imageWishListProduct);

//            imgProductIcon.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.txtDeleteFromWishLsit){
                if (!GineePref.getAccessToken(context).equals("")) {
                    deleteProductFromWishList(getAdapterPosition());
                }

            }else if(v.getId() == R.id.txtAddToCart){

                if (!GineePref.getAccessToken(context).equals("")) {
                    addProductToCart(getAdapterPosition());
                }else {
                    Intent signOutIntent = new Intent(context, SignInActivity.class);
                    context.startActivity(signOutIntent);
//                    ((Activity)context).finish();
                }

            }
        }
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
                                    deleteItem(adapterPosition);
                                    Toast.makeText(context, ""+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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

    @Override
    public WishListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.wishlist_raw, parent, false);
        return new WishListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final WishListAdapter.ViewHolder holder, final int position) {

        TextView textViewName = holder.txtViewMore;
        TextView tvNameWishListProduct = holder.tvNameWishListProduct;
        TextView tvPriceWishListProduct = holder.tvPriceWishListProduct;
        ImageView imageWishListProduct = holder.imageWishListProduct;

        tvPriceWishListProduct.setText("Rs. "+categories.get(position).getPrice());
        tvNameWishListProduct.setText(categories.get(position).getName());
        Picasso.with(context)
                .load(categories.get(position).getImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imageWishListProduct);
        Log.e("wishlist", ""+position);

    }

    private void addProductToCart(final int adapterPosition) {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("product_id", categories.get(adapterPosition).getId());
            paramObject.put("quantity", 1);
            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().addProductToCart(GineePref.getAccessToken(context),
                    body);
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
                                    if (!GineePref.getAccessToken(context).equals("")) {
                                        HomeActivity.getAllCartProductsAPICall(context);
                                    }
                                    Toast.makeText(context, ""+jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private void deleteItem(int position) {
        categories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, categories.size());
    }
}