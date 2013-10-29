package org.nando.nearestbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.bugsense.trace.BugSenseHandler;

import org.nando.nearestbus.task.InstallDatabaseTask;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

/**
 * Created by fernandoMac on 23/09/13.
 */
public class EntryActivity extends Activity {

    private boolean dbInstall = false;
    private static AlertDialogHelper dialogHelper;

    boolean gpsEnabled = false;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "433cb4d1");
        setContentView(R.layout.entry_activity);
        dialogHelper = new AlertDialogHelper(this);
        InstallDatabaseTask task = new InstallDatabaseTask(this);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);




    }

    public void successOnDabase(boolean success) {
        dbInstall = success;

    }

    public void clickOnLinkBus(View v) {

        if(CheckConnectivityUtils.weHaveGoogleServices(this) && gpsEnabled && dbInstall) {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        else {
            performChecks();
        }

    }

    public void clickOnJP(View v) {

        if(CheckConnectivityUtils.weHaveGoogleServices(this) && gpsEnabled && dbInstall) {
            Intent intent = new Intent(this,JourneyPlannerActivity.class);
            startActivity(intent);
        }
        else {
            performChecks();
        }
    }

    private  void performChecks() {
        if(!CheckConnectivityUtils.weHaveGoogleServices(this)) {
            processCheckGoogleServices();
        }
        if(!dbInstall) {
            AlertDialog dialog =  dialogHelper.createAlertDialog("Sorry","The database was not install properly please re-install app again ",true);
            dialog.show();
        }
        if(!gpsEnabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Please enable GPS settings");
            alertDialog.setPositiveButton("Ok",new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.show();
        }
    }



    public void onBackPressed() {
        this.finish();
    }




    private void processCheckGoogleServices() {

            AlertDialogHelper helper = new AlertDialogHelper(this);
            AlertDialog dialog = helper.createAlertDialog("Warning","You dont have Google play services please download",false);
            dialog.show();
            CheckConnectivityUtils.downloadGooglePlayServices(this);

    }

    protected void onStop() {
        super.onStop();
        System.out.println("stopping!");

    }
}
