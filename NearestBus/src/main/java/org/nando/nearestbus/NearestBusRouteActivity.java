package org.nando.nearestbus;

import android.app.AlertDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import org.nando.nearestbus.adapters.BusListAdapter;
import org.nando.nearestbus.adapters.RouteListAdapter;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.task.BusRouteInfoTask;
import org.nando.nearestbus.task.BusStopInfoTask;
import org.nando.nearestbus.task.LocationTask;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by fernandoMac on 20/08/13.
 */
public class NearestBusRouteActivity extends NearestBusRouteFragmentAbstract implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private Location location;
    private LocationClient locationClient;
    private ListView listView;
    private BusStops busStopPojo;
    private Button searchBtn;
    private EditText editText;
    private Switch aSwitch;
    private AlertDialogHelper dialogHelper;



    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public  View  onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.nearest_route,container,false);
        DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
        try {
            dbHelper.createDataBase();
        } catch(IOException ioe) {
            throw new Error("Unable to create db");
        }
        //dbHelper.openDataBase();

        listView = (ListView) rootView.findViewById(R.id.listViewStops);
        searchBtn = (Button) rootView.findViewById(R.id.findBusStop);
        editText = (EditText) rootView.findViewById(R.id.editText);
        aSwitch = (Switch) rootView.findViewById(R.id.switchToMap);
        aSwitch.setChecked(false);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    if(!CheckConnectivityUtils.weHaveGoogleServices(getActivity())) {
                        CheckConnectivityUtils.downloadGooglePlayServices(getActivity());
                    }

                    aSwitch.setChecked(false);
                    Intent intent = new Intent(getActivity(),NearestBusRouteMapActivity.class);
                    intent.putExtra("busRoute",editText.getText().toString());
                    startActivity(intent);
                }
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                busStopPojo = (BusStops) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(busStopPojo.getUrl()));
                startActivity(intent);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                busStopPojo = (BusStops) adapterView.getItemAtPosition(i);
                String sUri = "geo:"+busStopPojo.getLatitude()+","+busStopPojo.getLongtitude()+
                        "?q="+busStopPojo.getLatitude()+","+busStopPojo.getLongtitude();

                Uri uri = Uri.parse(sUri);
                Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(intent);
                return true;
            }
        });

        dialogHelper = new AlertDialogHelper(getActivity());

        searchBtn.setOnClickListener(this);
        return rootView;
    }

    /*
      call when button is pressed
     */

    public void nearestStop(View view) {
        listView.setAdapter(null);
        location = locationClient.getLastLocation();
        if(location == null) {
            AlertDialog dialog = dialogHelper.createAlertDialog("Warning","Make sure your device has gps, google location available",true);
            dialog.show();
        }
        LocationTask task = new LocationTask(this);
        task.execute(location);

    }
    /*
     returns from LocationTask.java
     */
    public void findNearestInDB(LocationPojo locationPojo) {
        BusStopDataSource dsource = new BusStopDataSource(getActivity());
        String busroute = editText.getText().toString();
        if(busroute == null || busroute.isEmpty()) {
            AlertDialog dialog = dialogHelper.createAlertDialog("Warning","Please add a bus number",false);
            dialog.show();
        }
        else {
            BusRouteInfoTask task = new BusRouteInfoTask(this,location,locationPojo,busroute);
            task.execute(dsource);
        }

    }

    /*
    returns from BusStopInfoTask and displays to UI
     */
    public void displayBusStops(List<BusStops> list) {
        if(list != null || list.size() > 0) {
            RouteListAdapter adapter = new RouteListAdapter(getActivity(),android.R.layout.simple_list_item_1,list);
            listView.setAdapter(adapter);
        }
        else {
            AlertDialog dialog = dialogHelper.createAlertDialog("Sorry","Sorry no bus "+editText.getText().toString()+" in around 500 meters",false);
            dialog.show();
        }
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



    private void setupLocationClientIfNeeded() {
        if(locationClient == null) {
            locationClient = new LocationClient(getActivity(),this,this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationClient.requestLocationUpdates(REQUEST,this);

    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onClick(View view) {
        if(R.id.findBusStop == view.getId()) {
            if(CheckConnectivityUtils.weHaveGoogleServices(getActivity())) {
                this.nearestStop(view);
            }
            else {
                AlertDialog dialog = dialogHelper.createAlertDialog("Warning","You dont have google play services, please download from playstore",true);
                dialog.show();
                CheckConnectivityUtils.downloadGooglePlayServices(getActivity());


            }

        }

    }
}
