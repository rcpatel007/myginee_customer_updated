package com.myginee.customer.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.HomeActivity;
import com.myginee.customer.R;
import com.myginee.customer.model.CartModel;
import com.myginee.customer.utils.GineePref;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartListAdpater extends RecyclerView.Adapter<CartListAdpater.ViewHolder> {

    private static final String TAG = "CartListAdpater";
    Context context;
    Fragment fragment;
    FragmentManager fragmentManager;
    ArrayList<CartModel.Datum> categories;
    LayoutInflater inflater;
    private ProgressDialog mProgressDialog;
    private OnClickListner onClickListner;

    public CartListAdpater(Context context, Fragment fragment,
                           FragmentManager fragmentManager, ArrayList<CartModel.Datum> categories) {
        this.context = context;
        this.fragment = fragment;
        this.fragmentManager = fragmentManager;
        this.categories = categories;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        onClickListner = (OnClickListner) fragment;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageCartProduct, imageCartAddProduct, imageCartRemoveProduct;

        TextView tvCartProductDetail, tvCartProductPrice, txtDeleteFromCart, txtCartQuantity;

        public ViewHolder(View view) {
            super(view);

            this.tvCartProductDetail = (TextView) itemView.findViewById(R.id.tvCartProductDetail);
            this.tvCartProductPrice = (TextView) itemView.findViewById(R.id.tvCartProductPrice);
            imageCartProduct = (ImageView) view.findViewById(R.id.imageCartProduct);
            imageCartAddProduct = (ImageView) view.findViewById(R.id.imageCartAddProduct);
            imageCartRemoveProduct = (ImageView) view.findViewById(R.id.imageCartRemoveProduct);
            txtDeleteFromCart = (TextView) view.findViewById(R.id.txtDeleteFromCart);
            txtCartQuantity = (TextView) view.findViewById(R.id.txtCartQuantity);
//            imgProductIcon.setOnClickListener(this);
            txtDeleteFromCart.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.txtDeleteFromCart) {
                onClickListner.onClickResponse(categories.get(getAdapterPosition()).getProduct_id(), getAdapterPosition());
            }
        }
    }

    @Override
    public CartListAdpater.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cart_raw, parent, false);
        return new CartListAdpater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CartListAdpater.ViewHolder holder, final int position) {

        TextView tvCartProductPrice = holder.tvCartProductPrice;
        TextView tvCartProductDetail = holder.tvCartProductDetail;
        TextView txtDeleteFromCart = holder.txtDeleteFromCart;
        final TextView txtCartQuantity = holder.txtCartQuantity;
        ImageView imageCartAddProduct = holder.imageCartAddProduct;
        ImageView imageCartRemoveProduct = holder.imageCartRemoveProduct;
        ImageView imageCartProduct = holder.imageCartProduct;

        tvCartProductPrice.setText("Rs. " + categories.get(position).getPrice());
        tvCartProductDetail.setText(categories.get(position).getName());
        txtCartQuantity.setText(String.valueOf(categories.get(position).getQuantity()));

        Picasso.with(context)
                .load(categories.get(position).getImageUrl())
                .placeholder(R.drawable.ic_plumber)
                .error(R.drawable.ic_plumber)
                .into(imageCartProduct);

        imageCartAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = categories.get(position).getQuantity();
                if (qty > 0) {
                    qty++;
//                    txtCartQuantity.setText(String.valueOf(qty));
                    onClickListner.onCartResponse(categories.get(position).getProduct_id(), categories.get(position).getId(), qty, position);
//                    updateCart(holder, position, qty);
                }

            }
        });

        imageCartRemoveProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int qty = categories.get(position).getQuantity();
                if (qty > 1) {

                    qty--;
                    onClickListner.onCartResponse(categories.get(position).getProduct_id(), categories.get(position).getId(), qty, position);

//                    updateCart(holder, position, qty);
                }

            }
        });

        Log.e("cart", "" + position);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public interface OnClickListner {
        void onClickResponse(String product_id, int position);

        void onCartResponse(String product_id, String cart_id, int qty, int position);
    }

    private void deleteItem(int position) {
        categories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, categories.size());
        if (!GineePref.getAccessToken(context).equals("")) {
            HomeActivity.getAllCartProductsAPICall(context);
        }


    }

}
