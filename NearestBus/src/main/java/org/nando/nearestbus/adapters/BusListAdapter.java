package org.nando.nearestbus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.nando.nearestbus.R;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fernandoMac on 5/08/13.
 */
public class BusListAdapter extends ArrayAdapter<BusStops> {

    HashMap<BusStops,Integer> map = new HashMap<BusStops, Integer>();
    private static LayoutInflater inflater = null;
    private ArrayList<BusStops> data = new ArrayList<BusStops>();


    public BusListAdapter(Context context,int textViewResourceId, ArrayList<BusStops> list) {
        super(context,textViewResourceId,list);
        data = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i =0; i < list.size(); i++) {
            map.put(list.get(i),i);
        }
    }

    public long getItemId(int position) {
        BusStops pojo = getItem(position);
        return map.get(pojo);
    }

    public int getCount() {
        return data.size();
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null) {
            vi = inflater.inflate(R.layout.list_row,null);
        }
        TextView name = (TextView) vi.findViewById(R.id.name);
        TextView zone = (TextView) vi.findViewById(R.id.zone);
        TextView busRoute = (TextView) vi.findViewById(R.id.busRoute);
        TextView distance = (TextView) vi.findViewById(R.id.distance);
        BusStops stops = data.get(position);
        name.setText(stops.name);
        zone.setText("Zone: "+stops.zone);
        distance.setText("Distance: "+stops.getDistanceFromCurrentPoint());
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
