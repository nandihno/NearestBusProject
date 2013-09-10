package org.nando.nearestbus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.nando.nearestbus.adapters.RouteListAdapter;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.task.BusRouteInfoTask;
import org.nando.nearestbus.task.LocationTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fernandoMac on 23/08/13.
 *
 */
public class NearestBusRouteMapActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener,GoogleMap.OnInfoWindowClickListener {

    private Location location;
    private LocationClient locationClient;

    private BusStops busStopPojo;
    private Button searchBtn;
    private EditText editText;


    private GoogleMap map;

    private String busRouteFromPreviousActivity = "-1";

    private Map<Marker,String> markerUrlMap = new HashMap<Marker, String>();




    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearest_route_map);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch(IOException ioe) {
            throw new Error("Unable to create db");
        }

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap();

        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);

        searchBtn = (Button) findViewById(R.id.findBusStop);
        editText = (EditText) findViewById(R.id.editText);

        searchBtn.setOnClickListener(this);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            busRouteFromPreviousActivity = extras.getString("busRoute");
            editText.setText(busRouteFromPreviousActivity);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /*
      call when button is pressed
     */

    public void nearestStop() {

        location = locationClient.getLastLocation();
        map.clear();
        markerUrlMap.clear();
        LocationPojo pojo = new LocationPojo();
        pojo.latitude = location.getLatitude();
        pojo.longtitude = location.getLongitude();
        findNearestInDB(pojo);

    }
    /*
     returns from LocationTask.java
     */
    public void findNearestInDB(LocationPojo locationPojo) {
        BusStopDataSource dsource = new BusStopDataSource(this);
        String busroute = editText.getText().toString();
        if(busroute == null || busroute.isEmpty()) {
            Toast toast = Toast.makeText(this,"Please add a bus route",Toast.LENGTH_SHORT);

            toast.show();

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
        if(list != null) {
            for(BusStops stops:list) {
                LatLng latLng = new LatLng(stops.getLatitude(),stops.getLongtitude());

                Marker marker = map.addMarker(createMarkerOptions(latLng,"Zone:"+stops.getZone()+" ",stops.getName(),BitmapDescriptorFactory.HUE_GREEN));
                markerUrlMap.put(marker,stops.getUrl());


            }
           location = locationClient.getLastLocation();
           LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
           map.moveCamera(CameraUpdateFactory.newLatLngZoom(current,14));
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

    public void onDestroy() {
        super.onDestroy();
        markerUrlMap.clear();
        map.clear();
    }



    private void setupLocationClientIfNeeded() {
        if(locationClient == null) {
            locationClient = new LocationClient(this,this,this);
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
            this.nearestStop();
        }

    }


    @Override
    public void onInfoWindowClick(Marker marker) {
        String url = markerUrlMap.get(marker);
        if(url != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    private MarkerOptions createMarkerOptions(LatLng position,String title,String snippet,float hueValue) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        markerOptions.snippet(snippet);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(hueValue));
        return markerOptions;
    }


}
