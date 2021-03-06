package org.nando.nearestbus.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by fernandoMac on 24/08/13.
 */
public  class CheckConnectivityUtils {

    public static boolean weHaveGoogleServices(Context ctx) {
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(ctx);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason
        } else {
            return false;

        }
    }

    public static void downloadGooglePlayServices(Activity activity) {
        Intent marketIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.gms"));
        activity.startActivity(marketIntent);
    }


}
