package com.myginee.customer.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.GineeAppApplication;
import com.myginee.customer.HomeActivity;
import com.myginee.customer.MapsActivity;
import com.myginee.customer.R;
import com.myginee.customer.activity.SignInActivity;
import com.myginee.customer.adapter.CartListAdpater;
import com.myginee.customer.model.CartModel;
import com.myginee.customer.net.GineeAppApi;
import com.myginee.customer.payumoney.AppEnvironment;
import com.myginee.customer.payumoney.AppPreference;
import com.myginee.customer.utils.ConnectionDetector;
import com.myginee.customer.utils.GineePref;
import com.google.android.material.textfield.TextInputLayout;
import com.payu.india.Model.PaymentParams;
import com.payu.india.Model.PayuConfig;
import com.payu.india.Model.PayuHashes;
import com.payu.india.Payu.PayuConstants;
import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;
import com.razorpay.Checkout;
import com.sasidhar.smaps.payu.PaymentActivity;
import com.sasidhar.smaps.payu.PaymentOptions;
import com.sasidhar.smaps.payu.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CartFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CartFragment extends Fragment implements CartListAdpater.OnClickListner {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CartModel productViewModel;
    public static ArrayList<CartModel.Datum> CartModelArrayList;
    public static CartListAdpater cartListAdapter;
    RecyclerView cartListCategory;
    TextView tvProceedToPay, tvShowAddress, txtAddAddress;
    public static TextView cartTotalPrice;
    public static TextView tvNoData;
    View root;
    public static ProgressDialog mProgressDialog;
    public static ArrayList<String> imgSliderList;
    public static JSONArray productListArray = new JSONArray();
    public static int total_price = 0;
    Toolbar toolbar;
    ImageView imgBack;
    public static RelativeLayout llDetail;
    public static final String TAG = "CartProceedToPay : ";
    private boolean isDisableExitConfirmation = false;
    private String userMobile;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;
    private PaymentParams paymentParams = new PaymentParams();
    private PayuConfig payuConfig = new PayuConfig();

    public CartFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CartFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CartFragment newInstance(String param1, String param2) {
        CartFragment fragment = new CartFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_cart, container, false);

        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setMessage(getResources().getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);

        toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        imgBack = root.findViewById(R.id.imgBack);
        tvProceedToPay = root.findViewById(R.id.tvProceedToPay);
        tvShowAddress = root.findViewById(R.id.tvShowAddress);
        txtAddAddress = root.findViewById(R.id.txtAddAddress);
        llDetail = root.findViewById(R.id.llDetail);
        tvNoData = root.findViewById(R.id.tvNoData);

        setUpUserDetails();
//        payuConfig.setEnvironment(PayuConstants.MOBILE_STAGING_ENV);

//        ((GineeAppApplication) getActivity().getApplication()).setAppEnvironment(AppEnvironment.SANDBOX);

//        setupCitrusConfigs();
        Checkout.preload(getActivity());
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        tvProceedToPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                saveOrder();
                if (total_price > GineePref.getWalletPrice(getActivity())) {
                    if (!GineePref.getAddress(getActivity()).equals("")) {
                        if (!GineePref.getEmail(getActivity()).equals("")) {
                            clickToPay();
//                        clickToPayPayUMoney();
                        } else {
                            Toast.makeText(getActivity(), "Please add your email address ", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Please verify your address ", Toast.LENGTH_LONG).show();
                    }

                } else {

                    if (!GineePref.getAccessToken(getActivity()).equals("")) {
                        if (!GineePref.getAddress(getActivity()).equals("")) {
                            saveOrder();
                        } else {
                            Toast.makeText(getActivity(), "Please verify your address ", Toast.LENGTH_LONG).show();
                        }

                    }


                }

            }
        });
//        if(!GineePref.getAddress(getActivity()).equals("")){
        tvShowAddress.setVisibility(View.VISIBLE);
        txtAddAddress.setVisibility(View.VISIBLE);

        tvShowAddress.setText(GineePref.getAddress(getActivity()));
//        }else {
//            txtAddAddress.setVisibility(View.VISIBLE);
//            tvShowAddress.setVisibility(View.GONE);
//        }
        txtAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), MapsActivity.class));
            }
        });
        cartListCategory = (RecyclerView) root.findViewById(R.id.rvCartList);
        cartTotalPrice = (TextView) root.findViewById(R.id.totalPrice);
        cartListCategory.setHasFixedSize(true);
        cartListCategory.setNestedScrollingEnabled(false);
        CartModelArrayList = new ArrayList<CartModel.Datum>();
        imgSliderList = new ArrayList<>();

        LinearLayoutManager productLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        cartListCategory.setLayoutManager(productLayoutManager);
        cartListAdapter = new CartListAdpater(getActivity(),
                CartFragment.this, getActivity().getSupportFragmentManager(), CartModelArrayList);
        cartListCategory.setAdapter(cartListAdapter);

        if (!GineePref.getAccessToken(getActivity()).equals("")) {
            if (ConnectionDetector.getInstance().isConnectingToInternet(getActivity())) {
                //Call Login Api
                getAllCartProductsAPICall(getActivity());
            } else {
                ConnectionDetector.getInstance().showNoInternetAlert(getActivity());
            }
        } else {
            llDetail.setVisibility(View.GONE);
            tvNoData.setVisibility(View.VISIBLE);
        }


//        cartTotalPrice.setText(total_price +" Rs. ");
        return root;
    }

    private void clickToPayPayUMoney() {
        paymentParams.setKey("ALtdY4eX");
        paymentParams.setFirstName("test");
        paymentParams.setEmail("test@gmail.com");
        paymentParams.setPhone("7405595993");
        paymentParams.setProductInfo("test product");
        paymentParams.setAmount("20");
        paymentParams.setTxnId("" + System.currentTimeMillis());
        paymentParams.setSurl("https://dl.dropboxusercontent.com/s/rsyajmdygg50ug8/success.html");
        paymentParams.setFurl("https://dl.dropboxusercontent.com/s/haywukd51v4bqlg/failure.html");
        paymentParams.setUdf1("");
        paymentParams.setUdf2("");
        paymentParams.setUdf3("");
        paymentParams.setUdf4("");
        paymentParams.setUdf5("");

        PayuHashes payuHashes = Utils.generateHashFromSDK(paymentParams, "Me3O3BVomL");
        paymentParams.setHash(payuHashes.getPaymentHash());

        PaymentOptions.isEMIEnabled = false;

        Intent intent = new Intent(getActivity(), PaymentActivity.class);
        intent.putExtra(PayuConstants.PAYU_CONFIG, payuConfig);
        intent.putExtra(PayuConstants.PAYMENT_PARAMS, paymentParams);
        intent.putExtra(PayuConstants.PAYU_HASHES, payuHashes);

        startActivityForResult(intent, PayuConstants.PAYU_REQUEST_CODE);
    }

    private void clickToPay() {
        tvProceedToPay.setEnabled(false);
//        launchPayUMoneyFlow();

        GineePref.getSharedPreferences(getActivity());


        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("product_list", productListArray);
            paramObject.put("total_price", total_price);
            paramObject.put("wallet_price", GineePref.getWalletPrice(getActivity()));
            paramObject.put("address", GineePref.getAddress(getActivity()));
            paramObject.put("payment_mode", "0");
            GineePref.storeDataToPref(getActivity(), paramObject);
        } catch (JSONException e) {
            Log.e(TAG, "clickToPay: cart " + e.getMessage());
        }

        payUsingPay();
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
//        userDetailsPreference = getSharedPreferences(AppPreference.USER_DETAILS, MODE_PRIVATE);
//        userEmail = userDetailsPreference.getString(AppPreference.USER_EMAIL, mAppPreference.getDummyEmail());

        userMobile = GineePref.getPhoneStringValue(getActivity());

//        email_et.setText(userEmail);
//        mobile_et.setText(userMobile);
//        amount_et.setText(mAppPreference.getDummyAmount());
//        restoreAppPref();
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
                    //Success Transaction
                    if (!GineePref.getAccessToken(getActivity()).equals("")) {
                        saveOrder();
                    }
                } else {
                    Toast.makeText(getActivity(), "" + "Process Failed, Try again later", Toast.LENGTH_LONG).show();

                    //Failure Transaction
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

               /* new AlertDialog.Builder(getActivity())
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
        payUmoneyConfig.setDoneButtonText("DONE");

        //Use this to set your custom title for the activity
        payUmoneyConfig.setPayUmoneyActivityTitle("Payment");

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
                    R.style.AppTheme_default, false);

        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            tvProceedToPay.setEnabled(true);
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
            tvProceedToPay.setEnabled(true);

            if (merchantHash.isEmpty() || merchantHash.equals("")) {
                Toast.makeText(getActivity(), "Could not generate hash", Toast.LENGTH_SHORT).show();
            } else {
                mPaymentParams.setMerchantHash(merchantHash);

                if (AppPreference.selectedTheme != -1) {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,
                            getActivity(), AppPreference.selectedTheme, GineePref.isOverrideResultScreen(getActivity()));
                } else {
                    PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams, getActivity(), R.style.AppTheme_default,
                            GineePref.isOverrideResultScreen(getActivity()));
                }
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

    public static void getAllCartProductsAPICall(final Context context) {

        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(context.getString(R.string.loading_message));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().getCartProducts(GineePref.getAccessToken(context));
        getAllServiceListCall.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mProgressDialog.dismiss();
                if (response.code() == 200) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if (jsonObject.getBoolean("success") == true) {
                            JSONArray serviceArray = jsonObject.getJSONArray("data");
                            if (serviceArray.length() > 0) {
                                llDetail.setVisibility(View.VISIBLE);
                                tvNoData.setVisibility(View.GONE);
                                total_price = 0;
                                for (int i = 0; i < serviceArray.length(); i++) {
                                    JSONObject item = serviceArray.getJSONObject(i);

                                    imgSliderList = new ArrayList<>();
                                    JSONArray array = item.getJSONObject("product").getJSONArray("image_urls");
                                    if (array.length() > 0) {
                                        for (int j = 0; j < array.length(); j++) {
                                            imgSliderList.add(array.get(j).toString());
                                        }
                                    }
                                    CartModelArrayList.add(new CartModel.Datum(
                                            imgSliderList,
                                            item.getString("_id"),
                                            item.getString("product_id"),
                                            item.getJSONObject("product").getInt("quantity"),
                                            item.getJSONObject("product").getString("name"),
                                            item.getJSONObject("product").getString("image_url"),
                                            item.getJSONObject("product").getString("description"),
                                            item.getJSONObject("product").getInt("model_no"),
                                            item.getJSONObject("product").getInt("price"),
                                            item.getJSONObject("product").getInt("ratings"),
                                            item.getJSONObject("product").getString("created_at"),
                                            item.getJSONObject("product").getString("updated_at"),
                                            item.getJSONObject("product").getString("product_category_id"),
                                            item.getJSONObject("product").getString("product_sub_category_id")
                                    ));
                                    createProductJsonArray(item.getJSONObject("product").getString("_id"),
                                            item.getJSONObject("product").getInt("price"),
                                            item.getJSONObject("product").getInt("quantity")
                                    );
                                }
                                cartTotalPrice.setText(String.valueOf(calculateTotal()) + " Rs. ");
                                cartListAdapter.notifyDataSetChanged();
                            } else {
                                llDetail.setVisibility(View.GONE);
                                tvNoData.setVisibility(View.VISIBLE);
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

    public static int calculateTotal() {
        total_price = 0;
        for (CartModel.Datum p : CartModelArrayList) {
            int qty = p.getQuantity();
            total_price += p.getPrice() * qty;
        }
        return total_price;
    }

    private void saveOrder() {
        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("product_list", productListArray);
            paramObject.put("total_price", total_price);
            paramObject.put("wallet_price", GineePref.getWalletPrice(getActivity()));
            paramObject.put("address", GineePref.getAddress(getActivity()));
            paramObject.put("payment_mode", "0");
//            paramObject.put("transaction_id", GineePref.getAddress(getActivity()));
            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().saveOrder(GineePref.getAccessToken(getActivity()),
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
                                    Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();

                                    if (!GineePref.getAccessToken(getActivity()).equals("")) {
                                        getAllCartProductsAPICall(getActivity());
                                        HomeActivity.getAllCartProductsAPICall(getActivity());
                                    }

                                }


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
                    Toast.makeText(getActivity(), "Something went wrong!", Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static JSONArray createProductJsonArray(String product_id, long price, Integer quantity) {
        JSONObject jsonService = new JSONObject();
        try {
            jsonService.put("product_id", product_id);
            jsonService.put("price", price);
            jsonService.put("quantity", quantity);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return productListArray.put(jsonService);
    }

    @Override
    public void onResume() {
        super.onResume();
//        if(!GineePref.getAddress(getActivity()).equals("")){
        tvShowAddress.setVisibility(View.VISIBLE);
        txtAddAddress.setVisibility(View.VISIBLE);

        tvShowAddress.setText(GineePref.getAddress(getActivity()));
//        }else {
//            txtAddAddress.setVisibility(View.VISIBLE);
//            tvShowAddress.setVisibility(View.GONE);
//        }
        tvProceedToPay.setEnabled(true);
        cartListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClickResponse(String product_id, int position) {
        if (!GineePref.getAccessToken(getActivity()).equals("")) {
            deleteProductFromCart(product_id, position);
        }


    }

    private void deleteProductFromCart(String product_id, final int adapterPosition) {

        mProgressDialog.show();

        Call<ResponseBody> removeProductFromWishListCall = GineeAppApi.api().removeFromCartListProducts(GineePref.getAccessToken(getActivity()),
                product_id);
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

                                total_price = total_price - (CartModelArrayList.get(adapterPosition).getQuantity() * CartModelArrayList.get(adapterPosition).getPrice());
                                CartModelArrayList.get(adapterPosition).setPrice(total_price);
                                cartTotalPrice.setText(String.valueOf(total_price) + " Rs. ");

                                CartModelArrayList.remove(adapterPosition);
                                cartListAdapter.notifyDataSetChanged();

                                if (CartModelArrayList.size() > 0) {
                                    llDetail.setVisibility(View.VISIBLE);
                                    tvNoData.setVisibility(View.GONE);
                                } else {
                                    llDetail.setVisibility(View.GONE);
                                    tvNoData.setVisibility(View.VISIBLE);
                                }
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                if (!GineePref.getAccessToken(getActivity()).equals("")) {
                                    HomeActivity.getAllCartProductsAPICall(getActivity());
                                }

                            }

                        } else {
                            Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onCartResponse(String product_id, String cart_id, int qty, int position) {
        if (!GineePref.getAccessToken(getActivity()).equals("")) {
            updateCart(product_id, cart_id, qty, position);
        }


    }

    private void updateCart(String product_id, String cart_id, final int qty, final int position) {

        mProgressDialog.show();

        JSONObject paramObject = new JSONObject();
        try {
            paramObject.put("product_id", product_id);
            paramObject.put("cart_id", cart_id);
            paramObject.put("quantity", qty);
            RequestBody body =
                    RequestBody.create(MediaType.parse("application/json; charset=utf-8"), paramObject.toString());

            Call<ResponseBody> getAllServiceListCall = GineeAppApi.api().updateCart(GineePref.getAccessToken(getActivity()),
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

                                    CartModelArrayList.get(position).setQuantity(item.getInt("quantity"));
                                    cartListAdapter.notifyDataSetChanged();

                                    cartTotalPrice.setText(String.valueOf(calculateTotal()) + " Rs. ");
                                }

                            } else {
                                Toast.makeText(getActivity(), "" + jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    mProgressDialog.dismiss();
                    Toast.makeText(getActivity(), getActivity().getResources().getString(R.string.server_error_message), Toast.LENGTH_LONG).show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
