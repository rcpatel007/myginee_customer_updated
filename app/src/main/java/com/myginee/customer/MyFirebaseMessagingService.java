package com.myginee.customer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.myginee.customer.activity.NotificationActivity;
import com.myginee.customer.utils.GineePref;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FireBaseMessagingService";
    public static String NOTIFICATION_CHANNEL_ID = "com.myginee.customer";
    public static final int NOTIFICATION_ID = 1;
    ArrayList<com.myginee.customer.model.Notification> list = new ArrayList<>();
    GineePref mStorePref;
    /*RapidCommDatabase mRapidCommDatabase;*/

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    @Override
    public void onNewToken(String token) {
        Log.e(TAG,  token);
        GineePref.setFCMToken(this, token);
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
//            mStorePref = GineePref.setUSERID();
//            mStorePref.set_GCM_RegistrationID(token);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.e(TAG, "From: " + remoteMessage.getFrom());
//            mStorePref = Store_pref.getInstance();
//            mRapidCommDatabase = RapidCommDatabase.getInstance(getBaseContext());
        // Check if message contains a notification payload.
        if (remoteMessage.getData() != null) {
            list = new ArrayList<>();
            Map<String, String> data = remoteMessage.getData();
            GineePref.getSharedPreferences(this);
            try {
                if(remoteMessage.getData().get("type") == null) {
                    if (remoteMessage.getNotification().getTitle().contains("accepted")) {
                        GineePref.setIsOrdered(this, false);
                        GineePref.setAgent(this, new JSONObject(data.get("order")).getString("agent_id")+"");
                        GineePref.setOrderID(this,
                                new JSONObject(data.get("order")).getString("_id")+"");

                        com.myginee.customer.model.Notification notification = new
                                com.myginee.customer.model.Notification(remoteMessage.getNotification().getTitle(),
                                new JSONObject(data.get("order")).getString("description"),
                                new JSONObject(data.get("order")).getString("service_provision_date"),
                                new JSONObject(data.get("order")).getString("sub_category_image_url"),
                                new JSONObject(data.get("order")).getString("_id"), "accepted"
                        );

                        if(GineePref.getArrayList(this) !=  null){
                            list = GineePref.getArrayList(this);
                        }

                        list.add(notification);
                        GineePref.saveArrayList(this, list);


                    } else if (remoteMessage.getNotification().getTitle().contains("completed")) {
                        GineePref.setAgent(this, "");

                        com.myginee.customer.model.Notification notification = new
                                com.myginee.customer.model.Notification(remoteMessage.getNotification().getTitle(),
                                new JSONObject(data.get("order")).getString("description"),
                                new JSONObject(data.get("order")).getString("service_provision_date"),
                                new JSONObject(data.get("order")).getString("sub_category_image_url"),
                                new JSONObject(data.get("order")).getString("_id"), "completed"
                        );


                        if(GineePref.getArrayList(this) !=  null){
                            list = GineePref.getArrayList(this);
                        }

                        list.add(notification);
                        GineePref.saveArrayList(this, list);
                    } else if (remoteMessage.getNotification().getTitle().contains("dispatched")) {
                        GineePref.setAgent(this, "");
                        com.myginee.customer.model.Notification notification = new
                                com.myginee.customer.model.Notification(remoteMessage.getNotification().getTitle(),
                                new JSONObject(data.get("order")).getString("description"),
                                new JSONObject(data.get("order")).getString("service_provision_date"),
                                new JSONObject(data.get("order")).getString("sub_category_image_url"),
                                new JSONObject(data.get("order")).getString("_id"), "dispatched"
                        );


                        if(GineePref.getArrayList(this) !=  null){
                            list = GineePref.getArrayList(this);
                        }

                        list.add(notification);
                        GineePref.saveArrayList(this, list);
                    }
                    showNotification(this, remoteMessage.getNotification().getTitle(), data.get("description"),
                            data.get("type"), remoteMessage.getData().get("order"), remoteMessage.getData().get("agent_id"), data.get("orderId"));
                }else {
                    showChtNotification(this,
                            data.get("type").toString(), data.get("order").toString(),
                            data.get("agent_id").toString(), data.get("agent_name").toString(),
                            data.get("message").toString());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

                        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    // [END receive_message]

    private void showNotification(Context context, String title, String message,
                                  String notificationType, String id, String agent_id, String orderId) {
        // Create remote view and set bigContentView.
            /*RemoteViews expandedView = new RemoteViews(this.getPackageName(),
                    R.layout.alert_raw);
            expandedView.setTextViewText(R.id.tvOrderTitle, title);
            expandedView.setTextViewText(R.id.tvOrderDesc, message);*/
//            expandedView.setImageViewBitmap(R.id.imgOrder, remote_picture);
        Intent ii;
        ii = new Intent(context, NotificationActivity.class);
        ii.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        ii.setAction("actionstring" + System.currentTimeMillis());
        ii.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ii.putExtra("type", notificationType);
        ii.putExtra("agent_id", agent_id+"");
        ii.putExtra("title", title);
        ii.putExtra("message", message);
        ii.putExtra("order", id+"");

        PendingIntent pi = PendingIntent.getActivity(context, 0, ii,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("Notification", "Created in up to orieo OS device");
            notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setWhen(System.currentTimeMillis())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, title, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_notification : R.mipmap.ic_launcher;
    }

    private void showNewProductLunchNotification(Context context, String title, String message,
                                                 String notificationType, String orderId, String imageUrl) {
            /*Bitmap remote_picture = null;
            String url = getResources().getString(R.string.STORE_URL) + getResources().getString(R.string.IMAGE_PREFIX_URL) + imageUrl;
            Log.e("Notification Image URL ", url);
            url = url.replaceAll(" ", "%20");
            try {
                remote_picture = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }*/

        // Create remote view and set bigContentView.
        RemoteViews expandedView = new RemoteViews(this.getPackageName(),
                R.layout.alert_raw);
        expandedView.setTextViewText(R.id.tvOrderTitle, title);
        expandedView.setTextViewText(R.id.tvOrderDesc, message);
//            expandedView.setImageViewBitmap(R.id.imgOrder, remote_picture);
        Intent ii;

//            if (!GineePref.getUSERID(context).equals("")) {
        ii = new Intent(context, SplashActivity.class);
//            } else {
//                ii = new Intent(context, LoginActivity.class);
//            }
        ii.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        ii.setAction("actionstring" + System.currentTimeMillis());
        ii.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ii.putExtra("type", notificationType);
        ii.putExtra("title", title);
        ii.putExtra("message", message);
        ii.putExtra("orderId", orderId);

        PendingIntent pi = PendingIntent.getActivity(context, 0, ii,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("Notification", "Created in up to orio OS device");
            notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setWhen(System.currentTimeMillis())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title)
                    .setContent(expandedView).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "Order", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {

            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(title).build();
            notification.bigContentView = expandedView;
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }


    private void play_sound(int id) {
        MediaPlayer mp1 = MediaPlayer.create(this, id);
        if (mp1.isPlaying()) {
            mp1.reset();
            mp1 = MediaPlayer.create(this, id);
        }
        mp1.start();
    }

    static void updateMyActivity(Context context, String message) {
        Intent intent = new Intent("updateList");
        //put whatever data you want to send, if any
        intent.putExtra("message", message);
        //send broadcast
        context.sendBroadcast(intent);
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
        if(intent.getExtras() != null && intent.getExtras().get("type") != null){

            showChtNotification(this,
                    intent.getExtras().get("type").toString(), intent.getExtras().get("order").toString(),
                    intent.getExtras().get("agent_id").toString(), intent.getExtras().get("agent_name").toString(),
                    intent.getExtras().get("message").toString());

        }
    }

    private void showChtNotification(Context context, String notificationType, String id, String agent_id,
                                     String agent_name, String message) {
        // Create remote view and set bigContentView.
            /*RemoteViews expandedView = new RemoteViews(this.getPackageName(),
                    R.layout.alert_raw);
            expandedView.setTextViewText(R.id.tvOrderTitle, title);
            expandedView.setTextViewText(R.id.tvOrderDesc, message);*/
//            expandedView.setImageViewBitmap(R.id.imgOrder, remote_picture);
        Intent ii;
        ii = new Intent(context, SplashActivity.class);
        ii.setData((Uri.parse("custom://" + System.currentTimeMillis())));
        ii.setAction("actionstring" + System.currentTimeMillis());
        ii.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ii.putExtra("type", notificationType);
        ii.putExtra("agent_id", agent_id+"");
        ii.putExtra("agent_name", agent_name);
        ii.putExtra("message", message);
        ii.putExtra("order", id+"");

        PendingIntent pi = PendingIntent.getActivity(context, 0, ii,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("Notification", "Created in up to orieo OS device");
            notification = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                    .setOngoing(false)
                    .setSmallIcon(getNotificationIcon())
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setContentIntent(pi)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setWhen(System.currentTimeMillis())
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(agent_name).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, agent_name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(NOTIFICATION_ID, notification);
        } else {
            notification = new NotificationCompat.Builder(this)
                    .setSmallIcon(getNotificationIcon())
                    .setAutoCancel(true)
                    .setOngoing(false)
                    .setContentText(message)
                    .setContentIntent(pi)
                    .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    .setContentTitle(agent_name).build();
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                    Context.NOTIFICATION_SERVICE);
            notificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
