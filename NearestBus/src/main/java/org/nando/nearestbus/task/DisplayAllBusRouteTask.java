package org.nando.nearestbus.task;

import android.os.AsyncTask;

import org.nando.nearestbus.AllBusRouteActivityMap;
import org.nando.nearestbus.AllBusRouteFragment;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;

import java.util.List;

/**
 * Created by fernandoMac on 28/08/13.
 */
public class DisplayAllBusRouteTask extends AsyncTask<Object,Void,BusRoute> {

    AllBusRouteActivityMap activity = null;
    AllBusRouteFragment fragment = null;

    public DisplayAllBusRouteTask(AllBusRouteActivityMap anActivity) {
        activity = anActivity;
    }

    public DisplayAllBusRouteTask(AllBusRouteFragment aFragment) {
        fragment = aFragment;
    }


    @Override
    protected BusRoute doInBackground(Object... objects) {
        BusStopDataSource dataSource = (BusStopDataSource) objects[0];
        BusRoute route = (BusRoute) objects[1];
        dataSource.open();
        dataSource.setAllStopsForBusNumber(route);
        dataSource.close();
        return route;
    }

    protected void onPostExecute(BusRoute route) {
        if(activity != null) {
            activity.displayBusStops(route);
        }
        else {
           fragment.displayBusStops(route);
        }


    }


}
