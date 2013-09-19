package org.nando.nearestbus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.nando.nearestbus.R;
import org.nando.nearestbus.pojo.JourneyPlannerDisplayInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fernandoMac on 19/09/13.
 */
public class JourneyPlannerListAdapter extends ArrayAdapter<JourneyPlannerDisplayInfo> {

    HashMap<JourneyPlannerDisplayInfo,Integer> map = new HashMap();
    private static LayoutInflater inflater = null;
    private List<JourneyPlannerDisplayInfo> data = new ArrayList();

    public JourneyPlannerListAdapter(Context ctx,int textViewResourceId, List<JourneyPlannerDisplayInfo> list) {
        super(ctx,textViewResourceId,list);
        data = list;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i =0; i < list.size(); i++) {
            map.put(list.get(i),i);
        }
    }

    public long getItemId(int position) {
        JourneyPlannerDisplayInfo pojo = getItem(position);
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
            vi = inflater.inflate(R.layout.journey_planner_options_row,null);
        }
        TextView heading = (TextView) vi.findViewById(R.id.headingElementTxt);
        TextView walkingElement = (TextView) vi.findViewById(R.id.walkingElementTxt);
        TextView busElement = (TextView) vi.findViewById(R.id.busElement);
        TextView destinationElement = (TextView) vi.findViewById(R.id.destinationElement);
        JourneyPlannerDisplayInfo pojo = data.get(position);
        int optionNo = position + 1;
        heading.setText(optionNo + " take bus no "+pojo.getBusRoute().busRoute);
        walkingElement.setText("From / to "+pojo.getWalkingElement());
        busElement.setText("Bus no "+pojo.getBusRoute().busRoute+" departs / arrives "+pojo.getBusRouteLegDuration());
        destinationElement.setText("Finally from / to "+pojo.getDestinationElement());
        return vi;
    }



}
