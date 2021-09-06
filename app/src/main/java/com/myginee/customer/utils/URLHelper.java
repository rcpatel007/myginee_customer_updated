package com.myginee.customer.utils;

public class URLHelper {
//    final public static String URL_PREFIX_FOR_API = "https://myginee-app.herokuapp.com/api/v1/";
//final public static String URL_PREFIX_FOR_API = "http://3.138.208.107:3000/api/v1/";
    final public static String URL_PREFIX_FOR_API = "http://apis.myginee.com:3000/api/v1/";
    final public static String RAZOR_PAY_ORDER_API = "https://api.razorpay.com/v1/";
    final public static String DEVICE_TYPE = "Android";

    public static int REQUEST_CODE_REGISTER = 2000;
    public static String STR_EXTRA_ACTION_LOGIN = "login";
    public static String STR_EXTRA_ACTION_RESET = "resetpass";
    public static String STR_EXTRA_ACTION = "action";
    public static String STR_EXTRA_USERNAME = "username";
    public static String STR_EXTRA_PASSWORD = "password";
    public static String STR_DEFAULT_BASE64 = "default";
    public static String UID = "";
    //TODO only use this UID for debug mode
//    public static String UID = "6kU0SbJPF5QJKZTfvW1BqKolrx22";
    public static String INTENT_KEY_CHAT_FRIEND = "friendname";
    public static String INTENT_KEY_CHAT_AVATA = "friendavata";
    public static String INTENT_KEY_CHAT_ID = "friendid";
    public static String INTENT_KEY_CHAT_ROOM_ID = "roomid";
    public static long TIME_TO_REFRESH = 10 * 1000;
    public static long TIME_TO_OFFLINE = 2 * 60 * 1000;
}
