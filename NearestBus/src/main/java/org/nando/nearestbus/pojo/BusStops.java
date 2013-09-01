package org.nando.nearestbus.pojo;

import java.util.List;

/**
 * Created by fernandoMac on 4/08/13.
 */
public class BusStops implements Comparable<BusStops> {

    private String id;
    private String name;
    private double longtitude;
    private double latitude;
    private String url;
    private String zone;
    private Double distanceFromCurrentPoint;

    private List<BusRoute> busRoutes;

    public List<BusRoute> getBusRoutes() {
        return busRoutes;
    }

    public void setBusRoutes(List<BusRoute> busRoutes) {
        this.busRoutes = busRoutes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public Double getDistanceFromCurrentPoint() {
        if(distanceFromCurrentPoint != null) {
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
