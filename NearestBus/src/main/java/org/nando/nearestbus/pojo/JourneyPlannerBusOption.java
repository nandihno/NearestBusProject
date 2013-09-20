package org.nando.nearestbus.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernandoMac on 20/09/13.
 */
public class JourneyPlannerBusOption {

    private List<JourneyPlannerBusInfo> busInfo = new ArrayList<JourneyPlannerBusInfo>();
    private int totalTimeTravel;


    public List<JourneyPlannerBusInfo> getBusInfo() {
        return busInfo;
    }

    public void setBusInfo(List<JourneyPlannerBusInfo> busInfo) {
        this.busInfo = busInfo;
    }

    public int getTotalTimeTravel() {
        return totalTimeTravel;
    }

    public void setTotalTimeTravel(int totalTimeTravel) {
        this.totalTimeTravel = totalTimeTravel;
    }

    public void addBusInfo(JourneyPlannerBusInfo busInfo) {
        this.busInfo.add(busInfo);
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JourneyPlannerBusOption that = (JourneyPlannerBusOption) o;

        if (totalTimeTravel != that.totalTimeTravel) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return totalTimeTravel;
    }
}
