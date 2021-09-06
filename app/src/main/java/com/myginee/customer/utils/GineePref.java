package com.myginee.customer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.myginee.customer.model.Notification;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GineePref {
    private static final String APP_SETTINGS = "APP_SETTINGS";

    // other properties...

    private GineePref() {
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getAccessToken(Context context) {
        return getSharedPreferences(context).getString("access_token", "");
    }

    public static void setAccessToken(Context context, String accessToken) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("access_token", accessToken);
        editor.commit();
    }

    public static String getUSERID(Context context) {
        return getSharedPreferences(context).getString("user_id", "");
    }

    public static void setUSERID(Context context, String user_id) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("user_id", user_id);
        editor.commit();
    }

    public static Integer getWalletPrice(Context context) {
        return getSharedPreferences(context).getInt("wallet_amount", 0);
    }

    public static void setWalletPrice(Context context, int wallet_amount) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("wallet_amount", wallet_amount);
        editor.commit();
    }


    public static String getUSERName(Context context) {
        return getSharedPreferences(context).getString("user_name", "");
    }

    public static void setUSERName(Context context, String user_name) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("user_name", user_name);
        editor.commit();
    }

    public static String getRefreshToken(Context context) {
        return getSharedPreferences(context).getString("refresh_token", "");
    }

    public static void setRefreshToken(Context context, String accessToken) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("refresh_token", accessToken);
        editor.commit();
    }

    public static String getAddress(Context context) {
        return getSharedPreferences(context).getString("address", "");
    }

    public static void setAddress(Context context, String address) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("address", address);
        editor.commit();
    }

    public static String getAddressLoc(Context context) {
        return getSharedPreferences(context).getString("address_loc", "");
    }

    public static void setAddressLoc(Context context, String address) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("address_loc", address);
        editor.commit();
    }


    public static String getPhoneStringValue(Context context) {
        return getSharedPreferences(context).getString("phone", "");
    }

    public static void setStringValue(Context context, String phone) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("phone", phone);
        editor.commit();
    }

    public static String getEmail(Context context) {
        return getSharedPreferences(context).getString("email", "");
    }

    public static void setEmail(Context context, String email) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("email", email);
        editor.commit();
    }

    public static String getFCMToken(Context context) {
        return getSharedPreferences(context).getString("fcm_token", "");
    }

    public static void setFCMToken(Context context, String fcmToken) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("fcm_token", fcmToken);
        editor.commit();
    }

    public static Boolean getIsOrdered(Context context) {
        return getSharedPreferences(context).getBoolean("is_ordered", false);
    }

    public static void setIsOrdered(Context context, Boolean is_ordered) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("is_ordered", is_ordered);
        editor.commit();
    }

    public static String getLat(Context context) {
        return getSharedPreferences(context).getString("latitude", "");
    }

    public static void setLat(Context context, String latitude) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("latitude", latitude);
        editor.commit();
    }

    public static String getLng(Context context) {
        return getSharedPreferences(context).getString("longitude", "");
    }

    public static void setLng(Context context, String longitude) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("longitude", longitude);
        editor.commit();
    }

    public static void saveArrayList(Context context, ArrayList<Notification> list) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("notify", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<Notification> getArrayList(Context context) {
        Gson gson = new Gson();
        String json = getSharedPreferences(context).getString("notify", null);
        Type type = new TypeToken<ArrayList<Notification>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public static String getAgent(Context context) {
        return getSharedPreferences(context).getString("agent", "");
    }

    public static void setAgent(Context context, String agent) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("agent", agent);
        editor.commit();
    }

    public static String getOrderID(Context context) {
        return getSharedPreferences(context).getString("orderId", "");
    }

    public static void setOrderID(Context context, String orderId) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("orderId", orderId);
        editor.commit();
    }

    public static boolean isOverrideResultScreen(Context context) {
        return getSharedPreferences(context).getBoolean("override_result_screen", true);
    }

    public static void setOverrideResultScreen(Context context, boolean overrideResultScreen) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("overrideResultScreen", overrideResultScreen);
        editor.commit();
    }

    public static void storeDataToPref(Context context, JSONObject jsonObject) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();

        Log.d("response", jsonObject.toString());
        editor.putString("objPayment", jsonObject.toString()).commit();

    }

    public static JSONObject  getDataFromPref(Context context) {
        JSONObject jsonObject = null;
        String value = getSharedPreferences(context).getString("objPayment", "");
        try {
            jsonObject = new JSONObject(value);
            String name = jsonObject.getString("name");
            String number = jsonObject.getString("number");
            Log.d("result", name + "...." + number);
        } catch (Exception e) {
            Log.d("exception", e.getMessage());
        }

        return jsonObject;
    }

}
