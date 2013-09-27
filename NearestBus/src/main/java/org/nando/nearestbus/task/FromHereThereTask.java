package org.nando.nearestbus.task;

import android.os.AsyncTask;

import org.nando.nearestbus.FromHereToThereFragment;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.LocationPojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernandoMac on 27/08/13.
 */
public class FromHereThereTask extends AsyncTask<Object,Void,ArrayList<BusRoute>> {

    FromHereToThereFragment myFragment;

    public FromHereThereTask(FromHereToThereFragment fragment) {
        myFragment = fragment;
    }


    @Override
    protected ArrayList<BusRoute> doInBackground(Object... objects) {
        BusStopDataSource dataSource = (BusStopDataSource) objects[0];
        String suburbName = (String) objects[1];
        LocationPojo myLocation = (LocationPojo) objects[2];
        dataSource.open();
        ArrayList<BusRoute> list = new ArrayList<BusRoute>();
        list = dataSource.findBusesThatGoTo(suburbName,myLocation);
        dataSource.close();
        return list;
    }

    protected void onPostExecute(ArrayList<BusRoute> list) {
        myFragment.displayList(list);

    }
}
