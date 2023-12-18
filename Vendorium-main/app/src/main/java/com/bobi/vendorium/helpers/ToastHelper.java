package com.bobi.vendorium.helpers;

import android.app.Activity;
import android.widget.Toast;

public class ToastHelper {
    public static void createShortToast(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
