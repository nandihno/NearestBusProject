package org.nando.nearestbus;

import org.nando.nearestbus.pojo.BusStops;

import java.util.List;

/**
 * Created by fernandoMac on 23/08/13.
 */
public abstract class NearestBusRouteFragmentAbstract extends BaseActivityFragment {

    public abstract void displayBusStops(List<BusStops> list);


}
