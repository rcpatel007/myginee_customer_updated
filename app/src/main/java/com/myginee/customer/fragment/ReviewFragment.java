package com.myginee.customer.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.GineeAppApplication;
import com.myginee.customer.R;
import com.myginee.customer.adapter.AdapterReview;
import com.myginee.customer.model.PaymentModel;
import com.myginee.customer.model.ServiceListModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.payumoney.AppEnvironment;
import com.myginee.customer.payumoney.AppPreference;
import com.myginee.customer.utils.GineePref;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.razorpay.Checkout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import static android.app.Activity.RESULT_OK;

public class ReviewFragment extends Fragment {

    TextView tvConfirmBooking, tvTotalPrice, tvDateTime, tvDesc, tvCategoryDesc, walletPrice, tvPrice, tvBackToHome;
    public  static  TextView tvReview;
    public  static LinearLayout llDetail, llThanks;
    public static RelativeLayout rlBooking, rlBackToHome;
    View root;
    Toolbar toolbar;
    private ProgressDialog mProgressDialog;
    JSONArray serviceArray = new JSONArray();
    String sub_category_name = "", sub_category_id = "", service_id = "", date = "", time = "",
            description = "";


    int total_price = 0;
    RecyclerView mReviewServiceRecyclerView;
    AdapterReview mAdapter;
    private List<ServiceListModel.ServiceList> mModelList;
    //    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
//    final private String serverKey = "key=" + "AAAAUitlQeE:APA91bEMahPvHK8nqEbTtYTT4uDWpDE-tKnc-3SFMMJ60743rSdh_MhHOXZ5rFu6dr4OY5g4mPq87V0AA4DrjszD8ABex10ajS49TALouoGCN-WjxwvjRiLBV8rokSmS2UP2E_W0GsoF";
    final private String contentType = "application/json";

    String NOTIFICATION_TITLE;
    String NOTIFICATION_MESSAGE;
    String TOPIC;

    public static final String TAG = "CartProceedToPay : ";
    private boolean isDisableExitConfirmation = false;
    private String userMobile;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    ImageView imgBack;
    PaymentModel orderModel = new PaymentModel();

    public ReviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.activity_review, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        Bundle bundle = getArguments();
        sub_category_name = bundle.getString("sub_category_name");
        sub_category_id = bundle.getString("sub_category_id");
        service_id = bundle.getString("service_id");
        date = bundle.getString("date");
        time = bundle.getString("time");
        description = bundle.getString("description");
        mModelList = new ArrayList<ServiceListModel.ServiceList>();

        try {
            serviceArray = new JSONArray(bundle.getString("service_array"));
            if (serviceArray != null) {
                for (int i = 0; i < serviceArray.length(); i++) {
                    ServiceListModel.ServiceList serviceList = new Gson().fromJson(serviceArray.get(i).toString(),
                            ServiceListModel.ServiceList.class);
                    total_price = total_price + Integer.parseInt(serviceList.getPrice());
                    mModelList.add(serviceList);
//                    mModelList.add( serviceArray.getJSONObject(i));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        tvConfirmBooking = root.findViewById(R.id.tvConfirmBooking);
        imgBack = (ImageView) root.findViewById(R.id.imgBack);
        tvBackToHome = root.findViewById(R.id.tvBackToHome);
        tvCategoryDesc = root.findViewById(R.id.tvCategoryDesc);
        tvTotalPrice = root.findViewById(R.id.tvTotalPrice);
        tvPrice = root.findViewById(R.id.tvPrice);
        tvDateTime = root.findViewById(R.id.tvDateTime);
        tvDesc = root.findViewById(R.id.tvDesc);
        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        tvReview = root.findViewById(R.id.tvReview);
        llDetail = (LinearLayout) root.findViewById(R.id.llDetail);
        rlBooking = (RelativeLayout) root.findViewById(R.id.rlBooking);
        rlBackToHome = (RelativeLayout) root.findViewById(R.id.rlBackToHome);
        mReviewServiceRecyclerView = root.findViewById(R.id.rvReviewService);
        llThanks = root.findViewById(R.id.llThanks);
        walletPrice = root.findViewById(R.id.walletPrice);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        //Set Up SharedPref
        setUpUserDetails();

//        ((GineeAppApplication) getActivity().getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);

//        setupCitrusConfigs();
        Checkout.preload(getActivity());

        /*if (GineePref.getIsOrdered(getActivity()) == true) {
            imgLoader.setVisibility(View.VISIBLE);
            llDetail.setVisibility(View.GONE);
            rlBooking.setVisibility(View.GONE);
            Glide.with(this).asGif()
                    .load(R.raw.order_loader).into(imgLoader);
        } else {
            imgLoader.setVisibility(View.GONE);
            llDetail.setVisibility(View.VISIBLE);
            rlBooking.setVisibility(View.VISIBLE);
        }*/


        mAdapter = new AdapterReview(getActivity(), mModelList);
        mModelList = new ArrayList<ServiceListModel.ServiceList>();

        tvDateTime.setText(date + " | " + time);
        tvCategoryDesc.setText(sub_category_name);
        tvTotalPrice.setText(total_price + " Rs. ");
        tvPrice.setText((total_price - GineePref.getWalletPrice(getActivity())) + " Rs. ");
        tvDesc.setText(description);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mReviewServiceRecyclerView.setHasFixedSize(true);
        mReviewServiceRecyclerView.setLayoutManager(manager);
        mReviewServiceRecyclerView.setAdapter(mAdapter);
        tvBackToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHome();
                /*Intent otpIntent = new Intent(getActivity(), HomeActivity.class);
                startActivity(otpIntent);*/
            }
        });
        tvConfirmBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialogView = new Dialog(getActivity(), R.style.full_screen_dialog);
                dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogView.setContentView(R.layout.dialog_select_payment);
                dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextView tvSelectOnline = (TextView) dialogView.findViewById(R.id.tvSelectOnline);
                TextView tvSelectCash = (TextView) dialogView.findViewById(R.id.tvSelectCash);

                tvSelectCash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogView.dismiss();
                        if (!GineePref.getAddress(getActivity()).equals("")) {
                            if (!GineePref.getAccessToken(getActivity()).equals("")) {
                                callAPIForAddServiceToCart("1");
                            }

                        } else {
                            Toast.makeText(getActivity(), "Please verify your address ", Toast.LENGTH_LONG).show();

                        }
                    }
                });
                tvSelectOnline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogView.dismiss();
                        if (total_price > GineePref.getWalletPrice(getActivity())) {
                            if (!GineePref.getAddress(getActivity()).equals("")) {
                                clickToPay();
                                /*if (!GineePref.getEmail(getActivity()).equals("")) {
                                    clickToPay();
//                                payUsingPay();
                                } else {
                                    Toast.makeText(getActivity(), "Please add your email address ", Toast.LENGTH_LONG).show();
                                }*/
                            }else {
                                Toast.makeText(getActivity(), "Please verify your address ", Toast.LENGTH_LONG).show();
                            }


                        } else {
                            if (!GineePref.getAddress(getActivity()).equals("")) {
                                callAPIForAddServiceToCart("0");
                            } else {
                                Toast.makeText(getActivity(), "Please verify your address ", Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
                dialogView.show();

            }
        });


        return root;
    }
    public void startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
        final Activity activity = getActivity();

        final Checkout co = new Checkout();

        try {
            JSONObject options = new JSONObject();
            options.put("name", "Razorpay Corp");
            options.put("description", "Demoing Charges");
            options.put("send_sms_hash",true);
            options.put("allow_rotation", true);
            //You can omit the image option to fetch the image from dashboard
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
            options.put("currency", "INR");
            options.put("amount", "100");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "test@razorpay.com");
            preFill.put("contact", "9876543210");

            options.put("prefill", preFill);

            co.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();
        }
    }

    private void payUsingPay() {


        /**
         * Instantiate Checkout
         */
        Checkout checkout = new Checkout();
//            checkout.setKeyID("od5NL44u5Ypd8WnPghJVyt0w");
//        checkout.setKeyID("xjix3hNi4aoeiwG2krjsIZBy");//secret key
        /**
         * Set your logo here
         */
//        checkout.setImage(R.mipmap.ic_logo);

        /**
         * Reference to current activity
         */
            final Activity activity = getActivity();

        double amount = 0;
        try {
            amount = Double.parseDouble(String.valueOf(total_price - GineePref.getWalletPrice(getActivity())));

        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * Pass your payment options to the Razorpay Checkout as a JSONObject
         */
        try {
            JSONObject options = new JSONObject();

            options.put("name", "" + GineePref.getUSERName(getActivity()));
            options.put("description", "My Ginee");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", true);
            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png");
//                options.put("order_id", orderModel.getId());//from response of step 3.
            options.put("theme.color", "#FF0505");
            options.put("currency", "INR");
            options.put("amount", amount * 100);//pass amount in currency subunits

            JSONObject preFill = new JSONObject();
            preFill.put("email", "" + GineePref.getEmail(getActivity()));
            preFill.put("contact", "" + GineePref.getPhoneStringValue(getActivity()).replace("+91", ""));
            options.put("prefill", preFill);

            checkout.open(activity, options);


        } catch (Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }

    }

    /*private void createOrderToPay() {

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("amount", total_price);
            paramObject.put("currency", "INR");
            paramObject.put("receipt", "");
//            paramObject.put("payment_capture", 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body =
                RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

        String uName = "rzp_test_hjcIIjeMPEuJY4";
        String uPass = "od5NL44u5Ypd8WnPghJVyt0w";
        String uBase = uName + ":" + uPass;

        String authHeader = "Basic " + Base64.encodeToString(uBase.getBytes(), Base64.NO_WRAP);

        Call<PaymentModel> call = RazorPay.api().generateOrder(getAuthToken(), body);

        call.enqueue(new Callback<PaymentModel>() {
            @Override
            public void onResponse(Call<PaymentModel> call, Response<PaymentModel> response) {
                if (response.isSuccessful()) {
//                    progressDialog.dismiss();
                    orderModel = response.body();
                    payUsingPay(orderModel);
                } else {
//                    progressDialog.dismiss();
                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PaymentModel> call, Throwable t) {
//                progressDialog.dismiss();
                Toast.makeText(getActivity(), "Fail....", Toast.LENGTH_SHORT).show();
            }

        });

    }*/
    public static String getAuthToken() {
        byte[] data = new byte[0];
        try {
            data = ("rzp_test_36tiIManoGr1Zm" + ":" + "aPYijhhItOpFPxJLkhufXyp0").getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
    }

    public void showHome() {
        final FragmentManager fm = getActivity().getSupportFragmentManager();
        while (fm.getBackStackEntryCount() > 2) {
            fm.popBackStackImmediate();
        }
                /*for(int i = 0; i< fm.getBackStackEntryCount() - 1 ; i++){
                    fm.popBackStackImmediate();
                }*/

    }

    private void clickToPay() {
        tvConfirmBooking.setEnabled(false);
//        launchPayUMoneyFlow();
//        createOrderToPay();
        GineePref.getSharedPreferences(getActivity());
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("category_id", service_id);
            paramObject.put("subcategory_id", sub_category_id);
            paramObject.put("service_list", serviceArray);
            paramObject.put("description", sub_category_name);
            paramObject.put("service_provision_date", getTimeInMilliSec(date + " " + time));
            paramObject.put("total_price", total_price);
            paramObject.put("wallet_price", GineePref.getWalletPrice(getActivity()));
            paramObject.put("address", GineePref.getAddress(getActivity()));
            paramObject.put("agent_id", "");
            paramObject.put("latitude", GineePref.getLat(getActivity()));
            paramObject.put("longitude", GineePref.getLng(getActivity()));
            paramObject.put("payment_mode", "0");
            GineePref.storeDataToPref(getActivity(), paramObject);
        }catch (JSONException e){
            Log.e(TAG, "clickToPay: "+ e.getMessage());
        }

        payUsingPay();
//        startPayment();
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }


    public static void setErrorInputLayout(EditText editText, String msg, TextInputLayout textInputLayout) {
        textInputLayout.setError(msg);
        editText.requestFocus();
    }

    public static boolean isValidEmail(String strEmail) {
        return strEmail != null && android.util.Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

    public static boolean isValidPhone(String phone) {
        Pattern pattern = Pattern.compile(AppPreference.PHONE_PATTERN);

        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private void setUpUserDetails() {

        userMobile = GineePref.getPhoneStringValue(getActivity());

    }


    private void setupCitrusConfigs() {
        AppEnvironment appEnvironment = ((GineeAppApplication) getActivity().getApplication()).getAppEnvironment();
        if (appEnvironment == AppEnvironment.PRODUCTION) {
            Toast.makeText(getActivity(), "Environment Set to Production", Toast.LENGTH_SHORT);
        } else {
            Toast.makeText(getActivity(), "Environment Set to SandBox", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {

                } else {
                    Toast.makeText(getActivity(), "" + "Process Failed, Try again later", Toast.LENGTH_LONG).show();

                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                /*new AlertDialog.Builder(getActivity())
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();*/

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d(TAG, "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d(TAG, "Both objects are null!");
            }
        } else {
            Log.d(TAG, "Data object is null!");
        }
    }

    /**
     * This function prepares the data for payment and launches payumoney plug n play sdk
     */
    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        //Use this to set your custom text on result screen button
//        payUmoneyConfig.setDoneButtonText("DONE");

        //Use this to set your custom title for the activity
//        payUmoneyConfig.setPayUmoneyActivityTitle("Payment");

        payUmoneyConfig.disableExitConfirmation(isDisableExitConfirmation);

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = 0;
        try {
            amount = Double.parseDouble(String.valueOf(total_price - GineePref.getWalletPrice(getActivity())));

        } catch (Exception e) {
            e.printStackTrace();
        }
        String txnId = System.currentTimeMillis() + "";
        //String txnId = "TXNID720431525261327973";
        String phone = GineePref.getPhoneStringValue(getActivity()).replace("+91", "");
        String productName = "product";
        String firstName = GineePref.getUSERName(getActivity());
//        String email = email_til.getEditText().getText().toString().trim();
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        AppEnvironment appEnvironment = ((GineeAppApplication) getActivity().getApplication()).getAppEnvironment();
        builder.setAmount(String.valueOf(amount))
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(GineePref.getEmail(getActivity()))
//                .setEmail("kinjal@gmail.com")
                .setsUrl(appEnvironment.surl())
                .setfUrl(appEnvironment.furl())
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(appEnvironment.debug())
                .setKey(appEnvironment.merchant_Key())
                .setMerchantId(appEnvironment.merchant_ID());

        try {
            mPaymentParams = builder.build();

            /*
             * Hash should always be generated from your server side.
             * */
            //    generateHashFromServer(mPaymentParams);

            /*            *//**
             * Do not use below code when going live
             * Below code is provided to generate hash from sdk.
             * It is recommended to generate hash from server side only.
             * */
            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, getActivity(),
                    R.style.AppTheme_default, true);


        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            tvConfirmBooking.setEnabled(true);
        }
    }

    /**
     * Thus function calculates the hash for transaction
     *
     * @param paymentParam payment params of transaction
     * @return payment params along with calculated merchant hash
     */
    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        AppEnvironment appEnvironment = ((GineeAppApplication) getActivity().getApplication()).getAppEnvironment();
        stringBuilder.append(appEnvironment.salt());

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    /**
     * This method generates hash from server.
     *
     * @param paymentParam payments params used for hash generation
     */
    public void generateHashFromServer(PayUmoneySdkInitializer.PaymentParam paymentParam) {
        //nextButton.setEnabled(false); // lets not allow the user to click the button again and again.

        HashMap<String, String> params = paymentParam.getParams();

        // lets create the post params
        StringBuffer postParamsBuffer = new StringBuffer();
        postParamsBuffer.append(concatParams(PayUmoneyConstants.KEY, params.get(PayUmoneyConstants.KEY)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.AMOUNT, params.get(PayUmoneyConstants.AMOUNT)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.TXNID, params.get(PayUmoneyConstants.TXNID)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.EMAIL, params.get(PayUmoneyConstants.EMAIL)));
        postParamsBuffer.append(concatParams("productinfo", params.get(PayUmoneyConstants.PRODUCT_INFO)));
        postParamsBuffer.append(concatParams("firstname", params.get(PayUmoneyConstants.FIRSTNAME)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF1, params.get(PayUmoneyConstants.UDF1)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF2, params.get(PayUmoneyConstants.UDF2)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF3, params.get(PayUmoneyConstants.UDF3)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF4, params.get(PayUmoneyConstants.UDF4)));
        postParamsBuffer.append(concatParams(PayUmoneyConstants.UDF5, params.get(PayUmoneyConstants.UDF5)));

        String postParams = postParamsBuffer.charAt(postParamsBuffer.length() - 1) == '&' ? postParamsBuffer.substring(0, postParamsBuffer.length() - 1).toString() : postParamsBuffer.toString();

        // lets make an api call
        GetHashesFromServerTask getHashesFromServerTask = new GetHashesFromServerTask();
        getHashesFromServerTask.execute(postParams);
    }


    protected String concatParams(String key, String value) {
        return key + "=" + value + "&";
    }

/*
    @Override
    public void onPaymentSuccess(String s, PaymentData paymentData) {
        //Success Transaction
        GineePref.setIsOrdered(getActivity(), true);
        if (!GineePref.getAddress(getActivity()).equals("")) {
            callAPIForAddServiceToCart("0");
        } else {
            Toast.makeText(getActivity(), "Please verify your address ", Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Toast.makeText(getActivity(), "" + "Process Failed, Try again later", Toast.LENGTH_LONG).show();
    }
*/

    /**
     * This AsyncTask generates hash from server.
     */
    private class GetHashesFromServerTask extends AsyncTask<String, String, String> {
//        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog = new ProgressDialog(getActivity());
//            progressDialog.setMessage("Please wait...");
            mProgressDialog.show();
        }

        @Override
        protected String doInBackground(String... postParams) {

            String merchantHash = "";
            try {
                //TODO Below url is just for testing purpose, merchant needs to replace this with their server side hash generation url
                URL url = new URL("https://payu.herokuapp.com/get_hash");

                String postParam = postParams[0];

                byte[] postParamsByte = postParam.getBytes("UTF-8");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Content-Length", String.valueOf(postParamsByte.length));
                conn.setDoOutput(true);
                conn.getOutputStream().write(postParamsByte);

                InputStream responseInputStream = conn.getInputStream();
                StringBuffer responseStringBuffer = new StringBuffer();
                byte[] byteContainer = new byte[1024];
                for (int i; (i = responseInputStream.read(byteContainer)) != -1; ) {
                    responseStringBuffer.append(new String(byteContainer, 0, i));
                }

                JSONObject response = new JSONObject(responseStringBuffer.toString());

                Iterator<String> payuHashIterator = response.keys();
                while (payuHashIterator.hasNext()) {
                    String key = payuHashIterator.next();
                    switch (key) {
                        /**
                         * This hash is mandatory and needs to be generated from merchant's server side
                         *
                         */
                        case "payment_hash":
                            merchantHash = response.getString(key);
                            break;
                        default:
                            break;
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return merchantHash;
        }

        @Override
        protected void onPostExecute(String merchantHash) {
            super.onPostExecute(merchantHash);

            mProgressDialog.dismiss();
            tvConfirmBooking.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(getActivity(), "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);
                PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, getActivity(), R.style.AppTheme_default,
                        true);

            }
        }
    }

    public static class EditTextInputWatcher implements TextWatcher {

        private TextInputLayout textInputLayout;

        EditTextInputWatcher(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() > 0) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }
        }
    }

    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    private void callAPIForAddServiceToCart(String paymentMode) {
        mProgressDialog.show();

        GineePref.getSharedPreferences(getActivity());
        try {
            JSONObject paramObject = new JSONObject();
            paramObject.put("category_id", service_id);
            paramObject.put("subcategory_id", sub_category_id);
            paramObject.put("service_list", serviceArray);
            paramObject.put("description", sub_category_name);
            paramObject.put("service_provision_date", getTimeInMilliSec(date + " " + time));
            paramObject.put("total_price", total_price);
            paramObject.put("wallet_price", GineePref.getWalletPrice(getActivity()));
            paramObject.put("address", GineePref.getAddress(getActivity()));
            paramObject.put("agent_id", "");
            paramObject.put("latitude", GineePref.getLat(getActivity()));
            paramObject.put("longitude", GineePref.getLng(getActivity()));
            paramObject.put("payment_mode", paymentMode);

            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            final Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().saveServiceOrder(GineePref.getAccessToken(getActivity()),
                    body);

            getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    mProgressDialog.dismiss();
                    if (response.code() == 200) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().string());
                            if (jsonObject.getBoolean("success") == true) {
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
                                Toast.makeText(getActivity(), "HEY, Thanks for booking service with us", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private long getTimeInMilliSec(String dateTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MMM/yy HH:mm a");
        long timeInMilliseconds = 0;
        try {
            Date mDate = sdf.parse(dateTime);
            timeInMilliseconds = mDate.getTime();
            System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeInMilliseconds;
    }

    @Override
    public void onResume() {
        super.onResume();
        walletPrice.setText(GineePref.getWalletPrice(getActivity()) + " Rs.");
        tvConfirmBooking.setEnabled(true);
        if (GineePref.getIsOrdered(getActivity()) == true) {
            llThanks.setVisibility(View.VISIBLE);
            rlBackToHome.setVisibility(View.VISIBLE);
            llDetail.setVisibility(View.GONE);
            rlBooking.setVisibility(View.GONE);
            tvReview.setText("THANK YOU");
            GineePref.setIsOrdered(getActivity(), false);
        } else {
            llThanks.setVisibility(View.GONE);
            rlBackToHome.setVisibility(View.GONE);
            llDetail.setVisibility(View.VISIBLE);
            rlBooking.setVisibility(View.VISIBLE);
            tvReview.setText("REVIEW");
        }
    }
}
