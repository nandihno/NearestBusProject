package org.nando.nearestbus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.nando.nearestbus.adapters.MapStopsInfoWindowAdapter;
import org.nando.nearestbus.datasource.BusStopDataSource;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.task.DisplayAllBusRouteTask;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fernandoMac on 28/08/13.
 */
public class AllBusRouteActivityMap extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener,GoogleMap.OnInfoWindowClickListener{

    private Location location;
    private LocationClient locationClient;

    private GoogleMap map;
    private HashMap<Marker,BusStops> markerPojoMap = new HashMap<Marker, BusStops>();
    private TextView busRouteText;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_bus_routes_map);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapBusRoute))
                .getMap();

        busRouteText = (TextView) findViewById(R.id.busRouteNoText);

        BusRoute route = (BusRoute) getIntent().getSerializableExtra("busRoute");


        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        displayMapMarkers(route);
        displayBusRouteText(route);

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

    private void displayBusRouteText(BusRoute route) {
        if(route.busRoute == null || route.busRoute.isEmpty()) {
            busRouteText.setText("Bus number unavailable");
        }
        else {
            busRouteText.setText("Stops for bus "+route.busRoute+" ");
        }
    }

    private void displayMapMarkers(BusRoute route) {
        map.clear();
        markerPojoMap.clear();
        if(route != null ) {
            BusStopDataSource dataSource = new BusStopDataSource(this);
            DisplayAllBusRouteTask task = new DisplayAllBusRouteTask(this);
            task.execute(dataSource,route);
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

    public void displayBusStops(BusRoute route) {
        if(route != null ) {
            if(route.locations != null) {
                ArrayList<BusStops> locations = route.locations;
                for(BusStops location:locations) {
                    LatLng latLng = new LatLng(location.latitude,location.longtitude);
                    Marker marker = map.addMarker(createMarkerOptions(latLng,route.busRoute,"",BitmapDescriptorFactory.HUE_ORANGE));
                    markerPojoMap.put(marker,location);

                }
            }
        }
        
        if(location == null) {
            CheckConnectivityUtils.showGPSSettingsAlert(this);
        }
        else {
            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
            map.setInfoWindowAdapter(new MapStopsInfoWindowAdapter(this,markerPojoMap));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 13));
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
        markerPojoMap.clear();
        map.clear();
    }

    public void onStart() {
        super.onStart();
        setupLocationClientIfNeeded();
        locationClient.connect();
    }

    public void onRestart() {
        super.onRestart();
        setupLocationClientIfNeeded();
        locationClient.connect();

    }

    @Override
    protected void onStop() {

        if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
        locationClient.disconnect();
        super.onStop();
    }


    private void setupLocationClientIfNeeded() {
        if(locationClient == null) {
            locationClient = new LocationClient(this,this,this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        locationClient.requestLocationUpdates(REQUEST,this);
        location = locationClient.getLastLocation();
        if(location == null) {
            CheckConnectivityUtils.showGPSSettingsAlert(this);
        }

    }

    @Override
    public void onDisconnected() {
        locationClient.disconnect();

    }

    @Override
    public void onLocationChanged(Location location) {

    }



    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        BusStops stops = markerPojoMap.get(marker);
        if(stops != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(stops.url != null || stops.url.isEmpty()) {
                intent.setData(Uri.parse(stops.url));
                startActivity(intent);
            }
        }

    }
}
