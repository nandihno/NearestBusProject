package org.nando.nearestbus;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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

import java.io.IOException;
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
    private Switch aSwitch;
    private GoogleMap map;
    private Map<Marker,BusStops> markerPojoMap = new HashMap<Marker, BusStops>();
    private TextView busRouteText;

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_bus_routes_map);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch(IOException ioe) {
            throw new Error("Unable to create db");
        }
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapBusRoute))
                .getMap();
        aSwitch = (Switch) findViewById(R.id.switchToBusRouteList);
        busRouteText = (TextView) findViewById(R.id.busRouteNoText);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    Intent intent = new Intent(AllBusRouteActivityMap.this,MainActivity.class);
                    startActivity(intent);
                    aSwitch.setChecked(false);
                }
            }
        });
        BusRoute route = (BusRoute) getIntent().getSerializableExtra("busRoute");


        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        displayMapMarkers(route);
        displayBusRouteText(route);

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
                List<BusStops> locations = route.locations;
                for(BusStops location:locations) {
                    LatLng latLng = new LatLng(location.getLatitude(),location.getLongtitude());
                    Marker marker = map.addMarker(createMarkerOptions(latLng,route.busRoute,"",BitmapDescriptorFactory.HUE_ORANGE));
                    markerPojoMap.put(marker,location);

                }
            }
        }
        location = locationClient.getLastLocation();
        LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
        map.setInfoWindowAdapter(new MapStopsInfoWindowAdapter(this,markerPojoMap));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 13));

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
    public void onInfoWindowClick(Marker marker) {
        BusStops stops = markerPojoMap.get(marker);
        if(stops != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            if(stops.getUrl() != null || stops.getUrl().isEmpty()) {
                intent.setData(Uri.parse(stops.getUrl()));
                startActivity(intent);
            }
        }

    }
}
