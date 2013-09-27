package org.nando.nearestbus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import org.nando.nearestbus.R;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fernandoMac on 24/08/13.
 */
public class MapStopsInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private static LayoutInflater inflater = null;
    private HashMap<Marker,BusStops> map = new HashMap();

    public MapStopsInfoWindowAdapter(Context ctx, HashMap<Marker,BusStops> aMap) {
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        map = aMap;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;

    }

    @Override
    public View getInfoContents(Marker marker) {
        View vi = inflater.inflate(R.layout.list_row,null);
        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView zone = (TextView) vi.findViewById(R.id.zone);
        TextView busRoute = (TextView) vi.findViewById(R.id.busRoute);
        TextView distance = (TextView) vi.findViewById(R.id.distance);
        BusStops stops = map.get(marker);
        name.setText(stops.name);
        zone.setText("Zone: "+stops.zone);
        if(stops.getDistanceFromCurrentPoint() != null && stops.getDistanceFromCurrentPoint() > 0) {
          distance.setText("Distance: "+stops.getDistanceFromCurrentPoint());
        }
        else {
            distance.setText("");
        }
        if(stops.busRoutes != null) {
            List<BusRoute> routes = stops.busRoutes;
            StringBuffer buff = new StringBuffer();
            for(BusRoute route:routes) {
                buff.append(route.busRoute+" ");
            }
            busRoute.setText("Bus routes: "+buff.toString());
        }
        else {
            busRoute.setText("");
        }

        return vi;
    }
}
