package org.nando.nearestbus.pojo;

/**
 * Created by fernandoMac on 19/09/13.
 */
public class JourneyPlannerBusInfo {

    private BusRoute busRoute;
    private String depart;
    private String arrive;
    private String travelTime;
    private int index = 0;
    private String html;

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public BusRoute getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(BusRoute busRoute) {
        this.busRoute = busRoute;
    }

    public String getArrive() {
        return arrive;
    }

    public void setArrive(String arrive) {
        this.arrive = arrive;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public String getDepart() {
        return depart;
    }

    public void setDepart(String depart) {
        this.depart = depart;
    }


}
