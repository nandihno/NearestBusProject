package org.nando.nearestbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
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
    private AlertDialogHelper dialogHelper;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this, "433cb4d1");
        setContentView(R.layout.entry_activity);
        InstallDatabaseTask task = new InstallDatabaseTask(this);
        task.execute();

    }

    public void successOnDabase(boolean success) {
        dbInstall = success;

    }

    public void clickOnLinkBus(View v) {
        if(!CheckConnectivityUtils.weHaveGoogleServices(this)) {
            processCheckGoogleServices();
        }
        if(!dbInstall) {
            AlertDialog dialog =  dialogHelper.createAlertDialog("Sorry","The database was not install properly please re-install app again ",true);
            dialog.show();
        }
        else {
            Intent intent = new Intent(this,MainActivity.class);
            startActivity(intent);
        }
    }

    public void clickOnJP(View v) {
        if(!CheckConnectivityUtils.weHaveGoogleServices(this)) {
            processCheckGoogleServices();
        }
        if(!dbInstall) {
            AlertDialog dialog =  dialogHelper.createAlertDialog("Sorry","The database was not install properly please re-install app again ",true);
            dialog.show();
        }
        else {
            Intent intent = new Intent(this,JourneyPlannerActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Toast.makeText(this,"Pleasure service you",Toast.LENGTH_LONG);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }




    private void processCheckGoogleServices() {

            AlertDialogHelper helper = new AlertDialogHelper(this);
            AlertDialog dialog = helper.createAlertDialog("Warning","You dont have Google play services please download",false);
            dialog.show();
            CheckConnectivityUtils.downloadGooglePlayServices(this);

    }
}
