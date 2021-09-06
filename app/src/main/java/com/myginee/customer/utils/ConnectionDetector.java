package com.myginee.customer.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.appcompat.app.AlertDialog;

import com.myginee.customer.R;

public class ConnectionDetector {


    private static ConnectionDetector mConnectionDetector = new ConnectionDetector();

    /* A private Constructor prevents any other
     * class from instantiating.
     */
    private ConnectionDetector() {
    }

    /* Static 'instance' method */
    public static ConnectionDetector getInstance() {
        if (mConnectionDetector == null) {
            mConnectionDetector = new ConnectionDetector();
        }
        return mConnectionDetector;
    }

    /**
     * To check that user has enabled internet connection or not
     */
    public boolean isConnectingToInternet(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    /**
     * To show the alert dialog if user don't have internet connection with custom title and message
     *
     * @param title title to be displayed in alert dialog
     * @param msg   message to be displayed in alert dialog
     * @param activity   current context in which dialog will display
     */
    public void show_alert(String title, String msg, Activity activity) {

        final Activity mActivity = activity;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(msg)
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.alert_ok_button_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }

    /**
     * To show the alert dialog if user don't have internet connection
     *
     * @param activity current context in which dialog will display
     */
    public void showNoInternetAlert(Activity activity) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        // set title
        alertDialogBuilder.setTitle(activity.getResources().getString(R.string.no_internet_alert_title));
        // set dialog message
        alertDialogBuilder
                .setMessage(activity.getResources().getString(R.string.no_internet_alert_message))
                .setCancelable(false)
                .setPositiveButton(activity.getResources().getString(R.string.alert_ok_button_text), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }
}
