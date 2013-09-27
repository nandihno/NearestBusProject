package org.nando.nearestbus.task;

import android.location.Location;
import android.os.AsyncTask;

import org.nando.nearestbus.NearestStopsFragment;
import org.nando.nearestbus.NearestStopsMapActivity;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.utils.GeoUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by fernandoMac on 19/08/13.
 */
public class BusStopInfoTask extends AsyncTask<BusStopDataSource,Void,ArrayList<BusStops>> {

    private Location location;
    private NearestStopsFragment mainActivity = null;
    private NearestStopsMapActivity mapActivity = null;
    private LocationPojo locationPojo;

    public BusStopInfoTask(Object anActivity, Location aLocation, LocationPojo locPojo) {
        if(anActivity instanceof NearestStopsFragment) {
            mainActivity = (NearestStopsFragment) anActivity;
        }
        else if(anActivity instanceof NearestStopsMapActivity) {
            mapActivity = (NearestStopsMapActivity) anActivity;
        }

        location = aLocation;
        locationPojo = locPojo;

    }


    @Override
    protected ArrayList<BusStops> doInBackground(BusStopDataSource... busStopDataSources) {
        busStopDataSources[0].open();
        LocationPojo p1 = GeoUtils.calculateDerivedPosition(locationPojo, GeoUtils.RANGE_IN_METERS, 0);
        LocationPojo p2 = GeoUtils.calculateDerivedPosition(locationPojo, GeoUtils.RANGE_IN_METERS, 90);
        LocationPojo p3 = GeoUtils.calculateDerivedPosition(locationPojo, GeoUtils.RANGE_IN_METERS, 180);
        LocationPojo p4 = GeoUtils.calculateDerivedPosition(locationPojo, GeoUtils.RANGE_IN_METERS, 270);
        ArrayList <BusStops> list = busStopDataSources[0].getNearestBusStop(p1,p2,p3,p4);
        busStopDataSources[0].close();
        decorateList(list);
        return list;
    }

    protected void onPostExecute(ArrayList<BusStops> list) {
        if(mainActivity != null) {
            mainActivity.displayBusStops(list);
        }
        else {
            mapActivity.displayBusStops(list);
        }

    }

    private void decorateList(ArrayList<BusStops> list) {
        LocationPojo currentLocation = new LocationPojo();
        currentLocation.latitude = location.getLatitude();
        currentLocation.longtitude = location.getLongitude();
        for(BusStops stop:list) {
            LocationPojo stopLocation = new LocationPojo();
            stopLocation.latitude = stop.latitude;
            stopLocation.longtitude = stop.longtitude;
            double distance = GeoUtils.getDistanceBetweenTwoPoints(currentLocation, stopLocation);
            stop.setDistanceFromCurrentPoint(distance);
        }
        Collections.sort(list);
    }
}
