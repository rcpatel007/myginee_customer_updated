package com.myginee.customer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.myginee.customer.fragment.AccountFragment;
import com.myginee.customer.fragment.AlertFragment;
import com.myginee.customer.fragment.CartFragment;
import com.myginee.customer.fragment.MyGineeFragment;
import com.myginee.customer.fragment.MyGineeServiceFragment;
import com.myginee.customer.fragment.RatingFragment;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.utils.GineePref;
import com.myginee.customer.utils.IFragmentToActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static com.myginee.customer.fragment.ReviewFragment.llDetail;
import static com.myginee.customer.fragment.ReviewFragment.llThanks;
import static com.myginee.customer.fragment.ReviewFragment.rlBackToHome;
import static com.myginee.customer.fragment.ReviewFragment.rlBooking;
import static com.myginee.customer.fragment.ReviewFragment.tvReview;

public class HomeActivity extends AppCompatActivity implements IFragmentToActivity, PaymentResultWithDataListener {

    private ActionBar toolbar;
    public static BottomNavigationView navigation;
    public static View badge;
    private ProgressDialog mProgressDialog;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Checkout.preload(getApplicationContext());


        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        mAuth = FirebaseAuth.getInstance();
// active listen to user logged in or not.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }

            }
        };
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCustomToken:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(HomeActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
//        toolbar = getSupportActionBar();
        if (getIntent().getStringExtra("orderId") != null) {

            RatingFragment ratingFragment = new RatingFragment();

            Bundle bundles = new Bundle();
            bundles.putSerializable("orderId", getIntent().getStringExtra("orderId") + "");
            ratingFragment.setArguments(bundles);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, ratingFragment)
                    .addToBackStack(ratingFragment.getClass().getName()).commit();

        } else {
            loadFragment(new MyGineeFragment());
        }
        if (!GineePref.getAccessToken(this).equals("")) {
            getAllCartProductsAPICall(HomeActivity.this);
        }

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

//        toolbar.setTitle("Shop");
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_myginee:
//                    toolbar.setTitle(getString(R.string.my_ginee_txt));
                    fragment = new MyGineeFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_cart:
//                    toolbar.setTitle(getString(R.string.cart_txt));
                    fragment = new CartFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_alert:
//                    toolbar.setTitle(getString(R.string.alert_txt));
                    fragment = new AlertFragment();
                    loadFragment(fragment);
                    return true;

                case R.id.navigation_acc:
//                    toolbar.setTitle(getString(R.string.acc_txt));
                    fragment = new AccountFragment();
                    loadFragment(fragment);
                    return true;
            }
            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void showBadge(Context context, BottomNavigationView
            bottomNavigationView, @IdRes int itemId, String value) {
//        removeBadge(bottomNavigationView, itemId);

        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
        badge = LayoutInflater.from(context).inflate(R.layout.view_notification_badge, bottomNavigationView, false);

        TextView text = badge.findViewById(R.id.badge_text_view);
        text.setText(value);
        itemView.addView(badge);

    }

    public static void removeBadge(BottomNavigationView bottomNavigationView, @IdRes int itemId) {
        BottomNavigationItemView itemView = bottomNavigationView.findViewById(itemId);
//        if (itemView.getChildCount() == 3) {
        itemView.removeViewAt(2);
//        }
    }

    public static void getAllCartProductsAPICall(final Context context) {
        GineePref.getSharedPreferences(context);
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getCartProducts(GineePref.getAccessToken(context));
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray serviceArray = jsonObject.getJSONArray("data");
                            showBadge(context, navigation, R.id.navigation_cart, String.valueOf(serviceArray.length()));
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
                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
       /* Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragMyGinee);
        fragment.onActivityResult(requestCode, resultCode, data);*/
    }

    @Override
    public void showToast(String msg) {
        // Get Fragment B
        MyGineeServiceFragment frag = (MyGineeServiceFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragMyGinee);
        frag.youGotMsg(msg);
    }

    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        Toast.makeText(HomeActivity.this, "" + "Payment success", Toast.LENGTH_LONG).show();

        try {
            GineePref.getDataFromPref(this).put("transaction_id", "" + paymentData.getOrderId());

            GineePref.storeDataToPref(this, GineePref.getDataFromPref(this));
            if (GineePref.getDataFromPref(this).has("product_list")) {
                saveOrder();
            } else {
                callAPIForAddServiceToCart();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void saveOrder() {
        GineePref.getSharedPreferences(this);
        RequestBody body =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), GineePref.getDataFromPref(this).toString());

        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().saveOrder(GineePref.getAccessToken(HomeActivity.this),
                body);
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {

                            GineePref.getSharedPreferences(HomeActivity.this).edit().remove("objPayment").apply();
                            JSONObject item = jsonObject.getJSONObject("data");
                            if (item != null) {
                                Toast.makeText(HomeActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                if (!GineePref.getAccessToken(HomeActivity.this).equals("")) {
                                    CartFragment.getAllCartProductsAPICall(HomeActivity.this);
                                    getAllCartProductsAPICall(HomeActivity.this);
                                }

                            }


                        } else {
                            Toast.makeText(HomeActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Something went wrong!", Toast.LENGTH_LONG).show();
            }
        });


    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        GineePref.getSharedPreferences(HomeActivity.this).edit().remove("objPayment").apply();
        Toast.makeText(HomeActivity.this, "" + "Process Failed, Try again later", Toast.LENGTH_LONG).show();

    }

    private void callAPIForAddServiceToCart() {
        mProgressDialog.show();

        GineePref.getSharedPreferences(this);

        RequestBody body =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), GineePref.getDataFromPref(this).toString());

        final Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().saveServiceOrder(
                GineePref.getAccessToken(HomeActivity.this), body);

        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {

                            GineePref.getSharedPreferences(HomeActivity.this).edit().remove("objPayment").apply();

//                                GineePref.setIsOrdered(getActivity(), false);
//                                if (GineePref.getIsOrdered(getActivity()) == true) {
                            llThanks.setVisibility(View.VISIBLE);
                            rlBackToHome.setVisibility(View.VISIBLE);
                            llDetail.setVisibility(View.GONE);
                            rlBooking.setVisibility(View.GONE);
                            tvReview.setText("THANK YOU");
//                                    Glide.with(getActivity()).asGif()
//                                            .load(R.raw.order_loader).into(imgLoader);
//                                } else {
//                                    llThanks.setVisibility(View.GONE);
//                                    rlBackToHome.setVisibility(View.GONE);
//                                    llDetail.setVisibility(View.VISIBLE);
//                                    rlBooking.setVisibility(View.VISIBLE);
//                                }
                            Toast.makeText(HomeActivity.this, "HEY, Thanks for booking service with us", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(HomeActivity.this, getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
            }
        });

    }


    /*@Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        }
        else {
            getSupportFragmentManager().popBackStack();
        }
    }*/

}