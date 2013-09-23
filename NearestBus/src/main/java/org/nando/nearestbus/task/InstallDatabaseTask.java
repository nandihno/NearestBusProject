package org.nando.nearestbus.task;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.nando.nearestbus.DatabaseHelper;
import org.nando.nearestbus.EntryActivity;

import java.io.IOException;

/**
 * Created by fernandoMac on 23/09/13.
 */
public class InstallDatabaseTask extends AsyncTask<Void, Void, Boolean> {

    ProgressDialog pd = null;

    Activity activity = null;


    public InstallDatabaseTask(Activity anActivity) {
        activity = anActivity;
    }




    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(activity);
        pd.setTitle("Preparing database ");
        pd.setMessage("Preparing database please wait...");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
    }


    @Override
    protected Boolean doInBackground(Void... voids) {

        DatabaseHelper dbHelper = new DatabaseHelper(activity);

        if (dbHelper.checkDataBase()) {
            return true;
        } else {
            try {
                dbHelper.createDataBase();
            } catch (IOException ioE) {
                return false;
            }
        }
        return true;
    }

    protected void onPostExecute(Boolean dbInstall) {
        pd.dismiss();
        pd = null;
        if(activity instanceof EntryActivity) {
            ((EntryActivity)activity).successOnDabase(dbInstall);
        }
    }
}
