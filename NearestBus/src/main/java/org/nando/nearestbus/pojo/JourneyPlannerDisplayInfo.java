package org.nando.nearestbus.pojo;

/**
 * Created by fernandoMac on 19/09/13.
 */
public class JourneyPlannerDisplayInfo {

    private BusRoute busRoute;
    private String walkingElement;
    private String busRouteLegDuration;
    private String destinationElement;


    public BusRoute getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(BusRoute busRoute) {
        this.busRoute = busRoute;
    }

    public String getBusRouteLegDuration() {
        return busRouteLegDuration;
    }

    public void setBusRouteLegDuration(String busRouteLegDuration) {
        this.busRouteLegDuration = busRouteLegDuration;
    }

    public String getDestinationElement() {
        return destinationElement;
    }

    public void setDestinationElement(String destinationElement) {
        this.destinationElement = destinationElement;
    }

    public String getWalkingElement() {
        return walkingElement;
    }

    public void setWalkingElement(String walkingElement) {
        this.walkingElement = walkingElement;
    }


}
