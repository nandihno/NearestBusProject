package org.nando.nearestbus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import org.nando.nearestbus.R;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fernandodeleon on 27/08/13.
 */
public class HereThereListAdapter extends ArrayAdapter<BusRoute> {

    private static LayoutInflater inflater = null;
    private ArrayList<BusRoute> data = new ArrayList<BusRoute>();
    private HashMap<BusRoute,Integer> map = new HashMap<BusRoute, Integer>();

    public HereThereListAdapter(Context context,int textViewResourceId, ArrayList<BusRoute> list) {
        super(context,textViewResourceId,list);
        data = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i=0; i< list.size(); i++) {
            map.put(list.get(i),i);
        }
    }

    public long getItemId(int position) {
        BusRoute element = getItem(position);
        return map.get(element);
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
            vi = inflater.inflate(R.layout.here_there_row,null);
        }
         TextView busRoute = (TextView) vi.findViewById(R.id.busRouteText);
         BusRoute busStr = data.get(position);
         busRoute.setText(busStr.busRoute);
        return vi;
    }
}
