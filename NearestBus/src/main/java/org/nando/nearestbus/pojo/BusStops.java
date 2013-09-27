package org.nando.nearestbus.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernandoMac on 4/08/13.
 */
public class BusStops implements Comparable<BusStops> {

    public String id;
    public String name;
    public double longtitude;
    public double latitude;
    public String url;
    public String zone;
    public Double distanceFromCurrentPoint = new Double(0);

    public ArrayList<BusRoute> busRoutes;




    public Double getDistanceFromCurrentPoint() {
        if(distanceFromCurrentPoint != null || distanceFromCurrentPoint.equals(0.0)) {
            long dLong = Math.round(distanceFromCurrentPoint);

            return (double)dLong;
        }
        else {
            return null;
        }
    }

    public void setDistanceFromCurrentPoint(Double distanceFromCurrentPoint) {
        this.distanceFromCurrentPoint = distanceFromCurrentPoint;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BusStops busStops = (BusStops) o;

        if (distanceFromCurrentPoint != busStops.distanceFromCurrentPoint) return false;
        if (!name.equals(busStops.name)) return false;
        if (!zone.equals(busStops.zone)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name.hashCode();
        result = 31 * result + zone.hashCode();
        temp = Double.doubleToLongBits(distanceFromCurrentPoint);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public String toString() {
        return name + "\n";
    }

    @Override
    public int compareTo(BusStops busStops) {
        return this.distanceFromCurrentPoint.compareTo(busStops.distanceFromCurrentPoint);
    }
}
