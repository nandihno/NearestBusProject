package org.nando.nearestbus.task;

import android.app.Fragment;

import android.location.Location;
import android.os.AsyncTask;

import com.bugsense.trace.BugSenseHandler;


import org.nando.nearestbus.NearestBusRouteFragment;
import org.nando.nearestbus.NearestStopsFragment;
import org.nando.nearestbus.pojo.LocationPojo;


/**
 * Created by fernandoMac on 4/08/13.
 */
public class LocationTask extends AsyncTask<Location,Void,LocationPojo> {

    private Fragment myActivity;

    public LocationTask(Fragment activity) {
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
        //myActivity.findNearestInDB(pojo);
        if(myActivity instanceof NearestStopsFragment) {
          ((NearestStopsFragment)myActivity).findNearestInDB(pojo);
        }
        if(myActivity instanceof NearestBusRouteFragment) {
            ((NearestBusRouteFragment)myActivity).findNearestInDB(pojo);
        }

    }


}
