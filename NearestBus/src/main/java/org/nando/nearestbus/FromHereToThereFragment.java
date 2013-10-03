package org.nando.nearestbus;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.bugsense.trace.BugSenseHandler;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.nando.nearestbus.adapters.HereThereListAdapter;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.task.FromHereThereTask;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by fernandoMac on 26/08/13.
 */
public class FromHereToThereFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private Location location;
    private LocationClient locationClient;
    private ListView listView;
    private Button button;
    private AutoCompleteTextView editText;
    private AlertDialogHelper dialogHelper;




    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);



    public  View  onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.here_to_there,container,false);

        dialogHelper = new AlertDialogHelper(getActivity());
        button = (Button) rootView.findViewById(R.id.searchToThereButton);
        editText = (AutoCompleteTextView) rootView.findViewById(R.id.suburbNameText);
        listView = (ListView) rootView.findViewById(R.id.hereTothereList);
        button.setOnClickListener(this);
        editText.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_dropdown_item_1line,fetchAllSuburbsInQld()));


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BusRoute route = (BusRoute) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(getActivity(),AllBusRouteActivityMap.class);
                intent.putExtra("busRoute",route);
                startActivity(intent);

            }
        });



        return rootView;

    }

    private ArrayList<String> fetchAllSuburbsInQld() {
        BusStopDataSource dsource = new BusStopDataSource(getActivity());
        dsource.open();
        ArrayList<String> list =  dsource.fetchAllSuburbsInQld();
        dsource.close();
        return list;
    }


    public void onResume() {
        super.onResume();
        setupLocationClientIfNeeded();
        locationClient.connect();
    }

    public void onPause() {
        super.onPause();
        locationClient.disconnect();
    }



    @Override
    public void onConnected(Bundle bundle) {
        locationClient.requestLocationUpdates(REQUEST,this);
        location = locationClient.getLastLocation();
        if(location == null) {
            CheckConnectivityUtils.showGPSSettingsAlert(getActivity());
        }
    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    private void searchForBusToThere() {
        listView.setAdapter(null);
        BusStopDataSource dsource = new BusStopDataSource(getActivity());
        
        if(location == null) {
            CheckConnectivityUtils.showGPSSettingsAlert(getActivity());
        }
        else {
            FromHereThereTask task = new FromHereThereTask(this);
            String suburbName = editText.getText().toString();
            if(suburbName == null || suburbName.isEmpty()) {
                AlertDialog dialog = dialogHelper.createAlertDialog("Sorry","No suburb name found",false);
                dialog.show();
            }
            else {
              LocationPojo myLocation = new LocationPojo(location.getLongitude(),location.getLatitude());
              task.execute(dsource,suburbName,myLocation);
            }
        }
    }

    public void displayList(ArrayList<BusRoute> busRoutes) {
        if(busRoutes != null && busRoutes.size() > 0) {
            HereThereListAdapter adapter = new HereThereListAdapter(getActivity(),android.R.layout.simple_list_item_1,busRoutes);
            listView.setAdapter(adapter);
        }
        else {
            AlertDialog dialog = dialogHelper.createAlertDialog("Sorry","No nearby bus travelling to  "+editText.getText().toString()+" found",false);
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        searchForBusToThere();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private void setupLocationClientIfNeeded() {

        if(locationClient == null) {
            locationClient = new LocationClient(getActivity(),this,this);

        }
    }
}
