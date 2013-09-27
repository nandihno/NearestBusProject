package org.nando.nearestbus.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernandoMac on 20/09/13.
 */
public class JourneyPlannerBusOption {

    public List<JourneyPlannerBusInfo> busInfo = new ArrayList<JourneyPlannerBusInfo>();
    public int totalTimeTravel;




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
