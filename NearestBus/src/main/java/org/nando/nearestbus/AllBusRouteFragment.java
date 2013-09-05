package org.nando.nearestbus;

import android.app.AlertDialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;

import com.bugsense.trace.BugSenseHandler;


import org.nando.nearestbus.adapters.RouteListAdapter;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.task.DisplayAllBusRouteTask;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by fernandoMac on 3/09/13.
 */
public class AllBusRouteFragment extends Fragment implements View.OnClickListener {


    private ListView listView;
    private Button button;
    private EditText editText;
    private AlertDialogHelper dialogHelper;
    private Switch aSwitch;




    public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.all_bus_routes,container,false);
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.createDataBase();
        } catch(IOException ioe) {
            BugSenseHandler.sendException(ioe);
            throw new Error("Unable to create db");
        }
        dialogHelper = new AlertDialogHelper(getActivity());
        button = (Button) rootView.findViewById(R.id.allBusRouteFindBtn);
        editText = (EditText) rootView.findViewById(R.id.allBusRouteText);
        listView = (ListView) rootView.findViewById(R.id.allBusRouteListView);
        aSwitch = (Switch) rootView.findViewById(R.id.allBusRouteSwitch);
        aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if(!CheckConnectivityUtils.weHaveGoogleServices(getActivity())) {
                        CheckConnectivityUtils.downloadGooglePlayServices(getActivity());
                    }
                    if(editText.getText().toString() == null || editText.getText().toString().isEmpty()) {
                        AlertDialog dialog = dialogHelper.createAlertDialog("Warning","Please put a bus number",true);
                        dialog.show();
                        aSwitch.setChecked(false);
                    }
                    else {
                        aSwitch.setChecked(false);
                        BusRoute route = new BusRoute();
                        route.busRoute = editText.getText().toString();
                        Intent intent = new Intent(getActivity(),AllBusRouteActivityMap.class);
                        intent.putExtra("busRoute",route);
                        startActivity(intent);
                    }
                }
            }
        });
        button.setOnClickListener(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BusStops stop = (BusStops) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(stop.getUrl()));
                startActivity(intent);
            }
        });
        return rootView;

    }

    private void findBusStops() {
        listView.setAdapter(null);
        BusStopDataSource dataSource = new BusStopDataSource(getActivity());
        if(editText.getText().toString() == null || editText.getText().toString().isEmpty()) {
            AlertDialog dialog = dialogHelper.createAlertDialog("Warning","Please put a bus number",true);
            dialog.show();
        }
        else {
            DisplayAllBusRouteTask task = new DisplayAllBusRouteTask(this);
            BusRoute busRoute = new BusRoute();
            busRoute.busRoute = editText.getText().toString();
            task.execute(dataSource,busRoute);
        }

    }

    public void displayBusStops(BusRoute route) {
        if(route != null) {

            if(route.locations != null && route.locations.size() > 0) {
                List<BusStops> stops = route.locations;
                RouteListAdapter adapter = new RouteListAdapter(getActivity(),android.R.layout.simple_list_item_1,stops);
                listView.setAdapter(adapter);
            }
            else {
                AlertDialog dialog = dialogHelper.createAlertDialog("Sorry","Sorry no bus "+editText.getText().toString()+"",false);
                dialog.show();
            }
        }


    }


    public void onResume() {
        super.onResume();

    }

    public void onPause() {
        super.onPause();

    }






    @Override
    public void onClick(View view) {
        findBusStops();

    }




}
