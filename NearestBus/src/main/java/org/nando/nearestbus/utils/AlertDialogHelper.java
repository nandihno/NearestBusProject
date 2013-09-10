package org.nando.nearestbus.utils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import org.nando.nearestbus.MainActivity;
import org.nando.nearestbus.R;

/**
 * Created by fernandoMac on 24/08/13.
 */
public class AlertDialogHelper {

    private Activity activity;

    public AlertDialogHelper(Activity anActivity) {
        activity = anActivity;
    }



    public AlertDialog createAlertDialog(String title,String message, boolean isClosableMessage) {
        AlertDialog.Builder alertDialogBuilder = null;
        if(isClosableMessage) {
            alertDialogBuilder = new AlertDialog.Builder(activity, R.style.AboutDialogWarning);
        }
        else {
            alertDialogBuilder = new AlertDialog.Builder(activity, R.style.AboutDialogInfo);
        }
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder.setMessage(message);
        if(isClosableMessage) {
            alertDialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    activity.finish();
                }
            });
        }
        else {
            alertDialogBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
        }

        AlertDialog dialog = alertDialogBuilder.create();
        return dialog;
    }
}
