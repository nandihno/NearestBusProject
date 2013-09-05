package org.nando.nearestbus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.nando.nearestbus.R;
import org.nando.nearestbus.pojo.BusStops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fernandoMac on 20/08/13.
 */
public class RouteListAdapter extends ArrayAdapter<BusStops> {

    HashMap<BusStops,Integer> map = new HashMap<BusStops, Integer>();
    private static LayoutInflater inflater = null;
    private List<BusStops> data = new ArrayList<BusStops>();


    public RouteListAdapter(Context context,int textViewResourceId, List<BusStops> list) {
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
            vi = inflater.inflate(R.layout.stops_list_row,null);
        }
        TextView name = (TextView) vi.findViewById(R.id.nameStop);
        TextView zone = (TextView) vi.findViewById(R.id.zoneStop);
        TextView distance = (TextView) vi.findViewById(R.id.distanceStop);
        BusStops stops = data.get(position);
        name.setText(stops.getName());
        zone.setText("Zone: "+stops.getZone());
        if(stops.getDistanceFromCurrentPoint() > 0) {
          distance.setText("Distance: "+stops.getDistanceFromCurrentPoint());
        }
        else {
            distance.setText("");
        }
        return vi;
    }
}
