package org.nando.nearestbus;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

/**
 * Created by fernandoMac on 17/09/13.
 */
public class JourneyPlannerMapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener {

    private MapView mapView;
    private GoogleMap map;
    private EditText editText;

    private Location location;
    private LocationClient locationClient;



    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.journey_planner_map,container,false);
        Button button = (Button) rootView.findViewById(R.id.setLocationButtonJp);
        mapView = (MapView) rootView.findViewById(R.id.mapViewJp);
        mapView.onCreate(savedInstanceState);
        editText = (EditText) rootView.findViewById(R.id.searchLocationTextJp);

        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        button.setOnClickListener(this);
        return rootView;
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
        if(view.getId() == R.id.setLocationButtonJp) {
            Toast.makeText(getActivity(),"Clicking button",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onResume() {
        super.onResume();
        setupLocationClientIfNeeded();
        locationClient.connect();
        mapView.onResume();
    }

    public void onPause() {
        super.onPause();
        locationClient.disconnect();
        mapView.onPause();
    }

    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }



    private void setupLocationClientIfNeeded() {
        if(locationClient == null) {
            locationClient = new LocationClient(getActivity(),this,this);
        }
    }
}
