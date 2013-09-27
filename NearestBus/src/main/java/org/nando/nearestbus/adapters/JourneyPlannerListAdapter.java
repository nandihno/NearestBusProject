package org.nando.nearestbus.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.nando.nearestbus.R;
import org.nando.nearestbus.pojo.JourneyPlannerBusInfo;
import org.nando.nearestbus.pojo.JourneyPlannerBusOption;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by fernandoMac on 19/09/13.
 */
public class JourneyPlannerListAdapter extends ArrayAdapter<JourneyPlannerBusInfo> {

    HashMap<JourneyPlannerBusInfo,Integer> map = new HashMap();
    private static LayoutInflater inflater = null;
    private ArrayList<JourneyPlannerBusInfo> data = new ArrayList();

    public JourneyPlannerListAdapter(Context ctx,int textViewResourceId, ArrayList<JourneyPlannerBusInfo> list) {
        super(ctx,textViewResourceId,list);
        data = list;
        inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(int i =0; i < list.size(); i++) {
            map.put(list.get(i),i);
        }
    }

    public long getItemId(int position) {
        JourneyPlannerBusInfo pojo = getItem(position);
        return map.get(pojo);
    }

    public int getCount() {
        return data.size();
    }

    public boolean hasStableIds() {
        return true;
    }

    public View getAView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null) {
            vi = inflater.inflate(R.layout.journey_planner_options_row,null);
        }
        TextView heading = (TextView) vi.findViewById(R.id.headingElementTxt);
        TextView arriveTxt = (TextView) vi.findViewById(R.id.busArrive);
        TextView departTxt = (TextView) vi.findViewById(R.id.busDepart);
        TextView travelTime = (TextView) vi.findViewById(R.id.busTravelTime);
        JourneyPlannerBusInfo pojo = data.get(position);
        if(pojo.index == 0) {
            heading.setPadding(0,20,0,10);
            heading.setTextSize(20f);
            heading.setText("Option "+(pojo.index +1) +" Bus:"+pojo.busRoute.busRoute);
        }
        else {
            heading.setPadding(0,10,0,10);
            heading.setTextSize(10f);
            heading.setText("Then ");
        }
        arriveTxt.setText(pojo.arrive);
        departTxt.setText(pojo.depart);
        travelTime.setText(pojo.travelTime);
        return vi;
    }

    public View getView(int position, View view, ViewGroup viewGroup) {
        View vi = view;
        if(view == null) {
            vi = inflater.inflate(R.layout.journey_planner_options_webview_row,null);
        }
        WebView webView = (WebView) vi.findViewById(R.id.webViewRowJP);
        webView.getSettings().setJavaScriptEnabled(true);
        JourneyPlannerBusInfo pojo = data.get(position);
        System.out.println(pojo.html);
        webView.loadData(pojo.html,"text/html","UTF-8");

        return vi;
    }



}
