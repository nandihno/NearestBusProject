package org.nando.nearestbus.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fernandoMac on 28/08/13.
 */
public class BusRoute implements Serializable {

    public List<BusStops> locations;
    public String busRoute;
    public String stopId;
    public String busRouteId;
    public String routeLongName;
    public String routeType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusRoute busRoute = (BusRoute) o;

        if (busRouteId != null ? !busRouteId.equals(busRoute.busRouteId) : busRoute.busRouteId != null)
            return false;
        if (locations != null ? !locations.equals(busRoute.locations) : busRoute.locations != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = locations != null ? locations.hashCode() : 0;
        result = 31 * result + (busRouteId != null ? busRouteId.hashCode() : 0);
        return result;
    }
}
