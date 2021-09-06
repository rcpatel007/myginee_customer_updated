package com.myginee.customer.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.myginee.customer.R;
import com.myginee.customer.adapter.ServiceAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CommonMethod {

    private static final String TAG = "Common";

    public static long getTimeInMilliSec(String dateTime) {
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

    public static String getDate(long milliSeconds, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public static String getDate(String dateString, String dateFormat) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        return formatter.format(calendar.getTime());
    }

    public static String getDate(Context context, Integer milliSeconds) {
        // Create a DateFormatter object for displaying date in specified format.
        SimpleDateFormat formatter = new SimpleDateFormat("dd");
// Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        calendar.get(Calendar.DAY_OF_MONTH);

//        Toast.makeText(context, getDayOfMonthSuffix(context, milliSeconds, Integer.parseInt(formatter.format(calendar.getTime()))), Toast.LENGTH_LONG).show();
        return formatter.format(calendar.getTime());
    }

    public static String getDayOfMonthSuffix(Context context, Integer milliSeconds, final int n) {
        if (n >= 1 && n <= 31) {
            Toast.makeText(context, "illegal day of month: " + n, Toast.LENGTH_LONG).show();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return new SimpleDateFormat("dd'st' -MMM-yyyy").format(calendar.getTime());
            case 2:
                return new SimpleDateFormat("dd'nd' -MMM-yyyy").format(calendar.getTime());
            case 3:
                return new SimpleDateFormat("dd'rd' -MMM-yyyy").format(calendar.getTime());
            default:
                return new SimpleDateFormat("dd'th' -MMM-yyyy").format(calendar.getTime());
        }
    }

    public static String getDeviceUniqueId(Context context) {

        return Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    public static String formateDateFromstring(String inputFormat, String outputFormat, String inputDate) {

        Date parsed = null;
        String outputDate = "";

        SimpleDateFormat df_input = new SimpleDateFormat(inputFormat, Locale.getDefault());
        SimpleDateFormat df_output = new SimpleDateFormat(outputFormat, Locale.getDefault());
        try {
            parsed = df_input.parse(inputDate);
            outputDate = df_output.format(parsed);
        } catch (ParseException e) {
            Log.e(TAG, "ParseException - dateFormat");
        }

        return outputDate;

    }

    //PopupWindow display method

    public static void showPopupWindow(Context mContext, final View view, String description) {
        final Dialog dialogView = new Dialog(view.getContext(), R.style.full_screen_dialog);
        dialogView.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogView.setContentView(R.layout.service_desc_popup);
        dialogView.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        dialogView.show();

        TextView tvDesc = dialogView.findViewById(R.id.tvDesc);
        tvDesc.setText(description);

        /*//Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.service_desc_popup, null);

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);

        //Initialize the elements of our window, install the handler

        TextView tvDesc = popupView.findViewById(R.id.tvDesc);
        tvDesc.setText(description);
        Button buttonEdit = popupView.findViewById(R.id.messageButton);

        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                //As an example, display the message
//                Toast.makeText(view.getContext(), "Wow, popup action button", Toast.LENGTH_SHORT).show();

            }
        });


        //Handler for clicking on the inactive zone of the window

        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //Close the window when clicked
                popupWindow.dismiss();
                return true;
            }
        });*/
    }

}
