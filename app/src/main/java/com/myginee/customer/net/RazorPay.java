package com.myginee.customer.net;

import android.content.Context;

import com.google.gson.GsonBuilder;
import com.myginee.customer.GineeAppApplication;
import com.myginee.customer.utils.URLHelper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RazorPay {
    private static GineeAppAPIInterface api;

    static {
        initialize(GineeAppApplication.getApplicationContext);
    }

    protected RazorPay() {
    }

    public static void initialize(Context context) {

        // set your desired log level
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build();
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Request.Builder builder = originalRequest.newBuilder().header("Content-Type",
                        "application/x-www-form-urlencoded");

                Request newRequest = builder.build();
                return chain.proceed(newRequest);
            }
        }).build();

        final OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .readTimeout(90, TimeUnit.SECONDS)
                .connectTimeout(90, TimeUnit.SECONDS)
                .build();


        // Build the API interface
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URLHelper.RAZOR_PAY_ORDER_API)
                .client(client)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create
                        (new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create()))
                .build();

        api = retrofit.create(GineeAppAPIInterface.class);
    }

    public static GineeAppAPIInterface api() {
        return api;
    }



}