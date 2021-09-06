package com.myginee.customer.net;


import com.google.gson.JsonElement;
import com.myginee.customer.model.OrderModel;
import com.myginee.customer.model.PaymentModel;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GineeAppAPIInterface {

    @FormUrlEncoded
    @POST("customer/login")
    Call<ResponseBody> userLogin(@Field("phone") String phone,
                                 @Field("password") String password,
                                 @Field("device_id") String device_id,
                                 @Field("device_token") String device_token);

    @FormUrlEncoded
    @POST("customer/register")
    Call<ResponseBody> userRegister(@Field("name") String name,
                                    @Field("phone") String phone,
                                    @Field("device_id") String device_id,
                                    @Field("device_token") String device_token);

    @GET("customer/verify-otp")
    Call<ResponseBody> getOTPOnRegister(@Header("access-token") String access_token, @Query("otp") String otp);

    @GET("customer/access-token")
    Call<ResponseBody> accessTokenRefresh(@Header("refresh-token ") String refresh_token);

    @POST("customer/forgot/password")
    Call<ResponseBody> forgetPassword(@Body RequestBody body);

    @POST("customer/reset-password")
    Call<ResponseBody> resetPassword(@Body RequestBody body);

    @POST("customer/update/{user_id}")
    Call<ResponseBody> updateCustomer(@Header("access-token") String access_token, @Path("user_id") String user_id,
                                      @Body RequestBody body);

    @POST("customer/update/address/{user_id}")
    Call<ResponseBody> updateCustomerAddress(@Header("access-token") String access_token, @Path("user_id") String user_id,
                                      @Body RequestBody body);

    @GET("customer/{user_id}")
    Call<ResponseBody> getUserByID(@Header("access-token") String access_token, @Path("user_id") String user_id);

    @POST("customer/change-password/{user_id}")
    Call<ResponseBody> changePassByID(@Header("access-token") String access_token, @Path("user_id") String user_id,
                                      @Body RequestBody body);

    @FormUrlEncoded
    @POST("customer/save-password")
    Call<ResponseBody> savePassword(@Header("access-token") String access_token, @Field("password") String password);

    @POST("customer/sign-out")
    Call<ResponseBody> signOut(@Header("refresh-token") String refresh_token);

    @GET("service/category/all")
    Call<ResponseBody> getAllService();

    /*@GET("service/category/all")
    Call<ResponseBody> getAllService(@Header("access-token") String access_token);*/

    @GET("service/category/sub/{category_id}")
    Call<ResponseBody> getServiceSubCatByID(/*@Header("access-token") String access_token, */@Path("category_id") String category_id);

    @GET("service/category/{subcategory_id}")
    Call<ResponseBody> getSubCatServiceByID(@Header("access-token") String access_token, @Path("subcategory_id") String subcategory_id);

    @Headers("Content-Type: application/json")
    @POST("service/category/save-service-order")
    Call<ResponseBody> saveServiceOrder(@Header("access-token") String access_token, @Body RequestBody body);

    @Headers("Content-Type: application/json")
    @POST("service/category/save-cart-detail")
    Call<ResponseBody> saveCartDetail(@Header("access-token") String access_token, @Body RequestBody body);

    @GET("service/category/all/orders")
    Call<ResponseBody> getAllServiceList(@Header("access-token") String access_token);

    @GET("product/category/all")
    Call<ResponseBody> getAllProducts();

    /*@GET("product/category/all")
    Call<ResponseBody> getAllProducts(@Header("access-token") String access_token);*/

    @GET("product/category/sub/{category_id}")
    Call<ResponseBody> getProductSubCatByID(/*@Header("access-token") String access_token,*/ @Path("category_id") String category_id);

    @GET("product/all/{subcategory_id}")
    Call<ResponseBody> getProductSubCatSubBySubID(/*@Header("access-token") String access_token,*/ @Path("subcategory_id") String subcategory_id);

    @GET("product/by/{id}")
    Call<ResponseBody> getProductSubID(/*@Header("access-token") String access_token,*/ @Path("id") String id);

    @GET("product/search")
    Call<ResponseBody> getSearchProductByStr(@Header("access-token") String access_token, @Query("searchStr") String searchStr);

    @GET("product/get-cart-products")
    Call<ResponseBody> getCartProducts(@Header("access-token") String access_token);

    @Headers("Content-Type: application/json")
    @POST("product/add-to-cart")
    Call<ResponseBody> addProductToCart(@Header("access-token") String access_token, @Body RequestBody body);

    @POST("product/save-to-wishlist/{product_id}")
    Call<ResponseBody> addProductToWishList(@Header("access-token") String access_token, @Path("product_id") String product_id);

    @Headers("Content-Type: application/json")
    @POST("product/save-order")
    Call<ResponseBody> saveOrder(@Header("access-token") String access_token,  @Body RequestBody body);

    @GET("product/get-wishlist-products")
    Call<ResponseBody> getWishListProducts(@Header("access-token") String access_token);

    @GET("product/orders/all")
    Call<ResponseBody> getAllOrdersList(@Header("access-token") String access_token);

    @DELETE("product/remove-from-wishlist/{product_id}")
    Call<ResponseBody> removeFromWishListProducts(@Header("access-token") String access_token, @Path("product_id") String product_id);

    @DELETE("product/remove-from-cart/{product_id}")
    Call<ResponseBody> removeFromCartListProducts(@Header("access-token") String access_token, @Path("product_id") String product_id);

    @POST("product/update/cart")
    Call<ResponseBody> updateCart(@Header("access-token") String access_token,
                                      @Body RequestBody body);

    @POST("customer/save/chat")
    Call<ResponseBody> saveChat(@Header("access-token") String access_token,
                                      @Body RequestBody body);

    @POST("customer/reviews")
    Call<ResponseBody> sendRating(@Header("access-token") String access_token,
                                      @Body RequestBody body);

    @GET("customer/retrieve/chat/{order_id}")
    Call<ResponseBody> retrieveChatByOrderId(@Header("access-token") String access_token,
                                             @Path("order_id") String order_id);

    @GET("banner-images/all")
    Call<ResponseBody> getBannerImages();

    @GET("customer/retrieve/terms-and-conditions")
    Call<ResponseBody> termsNconditionAPI(/*@Header("access-token") String access_token*/);

    @GET("customer/retrieve/about-us")
    Call<ResponseBody> aboutUs(/*@Header("access-token") String access_token*/);

    @GET("customer/retrieve/alerts")
    Call<JsonElement> alerts(@Header("access-token") String access_token);

    @GET("customer/wallet/transactions")
    Call<ResponseBody> getWalletTransaction(@Header("access-token") String access_token);

    @Headers("Content-Type: application/json")
    @POST("orders")
    Call<PaymentModel> generateOrder(@Header("Authorization") String Authorization, @Body RequestBody body);
}
