package org.nando.nearestbus;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.bugsense.trace.BugSenseHandler;
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
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.task.BusStopInfoTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fernandoMac on 24/08/13.
 */
public class NearestStopsMapActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener,GoogleMap.OnInfoWindowClickListener  {

    private Location location;
    private LocationClient locationClient;
    private Switch aSwitch;
    private Button nearestStopBtnMap;
    private GoogleMap map;
    private Map<Marker,BusStops> markerPojoMap = new HashMap<Marker, BusStops>();

    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nearest_stops_map);
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        try {
            dbHelper.createDataBase();
        } catch(IOException ioe) {
            throw new Error("Unable to create db");
        }
        //dbHelper.openDataBase();
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapStop))
                .getMap();

        map.setMyLocationEnabled(true);
        map.setOnInfoWindowClickListener(this);
        aSwitch = (Switch) findViewById(R.id.switchToBusStop);
        nearestStopBtnMap = (Button) findViewById(R.id.nearestStopMapBtn);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    Intent intent = new Intent(NearestStopsMapActivity.this,MainActivity.class);
                    startActivity(intent);
                    aSwitch.setChecked(false);
                }
            }
        });
        nearestStopBtnMap.setOnClickListener(this);
    }

    public void nearestStop() {
        markerPojoMap.clear();
        location = locationClient.getLastLocation();
        LocationPojo pojo = new LocationPojo();
        pojo.latitude = location.getLatitude();
        pojo.longtitude = location.getLongitude();
        findNearestInDB(pojo);
    }

    public void findNearestInDB(LocationPojo locationPojo) {
        BusStopDataSource dsource = new BusStopDataSource(this);
        BugSenseHandler.setLogging("in findNearestInDB: the datasource has been set:" + dsource);
        BusStopInfoTask task = new BusStopInfoTask(this,location,locationPojo);
        task.execute(dsource);
    }

    public void displayBusStops(List<BusStops> list) {
        if(list != null) {


            for(BusStops stops:list) {
                LatLng latLng = new LatLng(stops.getLatitude(),stops.getLongtitude());
                Marker marker = map.addMarker(createMarkerOptions(latLng,"Zone:"+stops.getZone()+" ",stops.getName(), BitmapDescriptorFactory.HUE_AZURE));
                markerPojoMap.put(marker,stops);

            }
            map.setInfoWindowAdapter(new MapStopsInfoWindowAdapter(this,markerPojoMap));
            location = locationClient.getLastLocation();
            LatLng current = new LatLng(location.getLatitude(),location.getLongitude());
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 14));
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
    public void onClick(View view) {
        if(view.getId() == R.id.nearestStopMapBtn) {
            this.nearestStop();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        BusStops stops = markerPojoMap.get(marker);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if(stops.getUrl() != null || stops.getUrl().isEmpty()) {
            intent.setData(Uri.parse(stops.getUrl()));
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
