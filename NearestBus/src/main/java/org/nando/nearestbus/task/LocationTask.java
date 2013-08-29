package org.nando.nearestbus.task;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;

import com.bugsense.trace.BugSenseHandler;

import org.nando.nearestbus.BaseActivityFragment;
import org.nando.nearestbus.MainActivity;
import org.nando.nearestbus.pojo.LocationPojo;

import java.util.List;
import java.util.Locale;

/**
 * Created by fernandoMac on 4/08/13.
 */
public class LocationTask extends AsyncTask<Location,Void,LocationPojo> {

    private BaseActivityFragment myActivity;

    public LocationTask(BaseActivityFragment activity) {
        this.myActivity = activity;
    }

    @Override
    protected LocationPojo doInBackground(Location... locations) {
        LocationPojo pojo = new LocationPojo();
        pojo.latitude = locations[0].getLatitude();
        pojo.longtitude = locations[0].getLongitude();
        BugSenseHandler.setLogging("in LocationTask:doInBackgroun pojo:"+pojo);
        return  pojo;
    }

    protected void onPostExecute(LocationPojo pojo) {
        myActivity.findNearestInDB(pojo);

    }


}
