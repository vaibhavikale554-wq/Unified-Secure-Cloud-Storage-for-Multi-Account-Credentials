package com.rao.multiaccountcredentials.comman;



import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.rao.multiaccountcredentials.R;


public class CustomSnackbar {

    public static final int TYPE_SUCCESS = 0;
    public static final int TYPE_ERROR = 1;

    public static void showSnackbar(Activity activity, View rootView, String message, int iconResId, int messageType) {
        Snackbar snackbar = Snackbar.make(rootView, "", Snackbar.LENGTH_LONG);
        View customView = LayoutInflater.from(activity).inflate(R.layout.show_sanckbar, null);

        ImageView iconImageView = customView.findViewById(R.id.snackbar_icon);
        iconImageView.setImageResource(iconResId);

        TextView messageTextView = customView.findViewById(R.id.snackbar_message);
        messageTextView.setText(message);

        // Set the background color based on the message type
        int backgroundColorResId = messageType == TYPE_SUCCESS ? R.drawable.snackbar_background_green : R.drawable.snackbar_background_red;
        customView.setBackgroundResource(backgroundColorResId);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        snackbar.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                // Handle dismiss event if needed
            }
        });
        closeKeyboard(activity,rootView);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.addView(customView, 0);
        snackbar.show();
    }


    private static void closeKeyboard(Context context, View view) {
        // Get the InputMethodManager
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);

        // Check if the keyboard is open
        if (imm != null && imm.isAcceptingText()) {
            // Hide the keyboard
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
