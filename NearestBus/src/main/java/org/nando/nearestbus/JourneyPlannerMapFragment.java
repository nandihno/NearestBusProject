package org.nando.nearestbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ads.ac;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.task.GeoCodingTask;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;
import org.nando.nearestbus.utils.GeoUtils;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * Created by fernandoMac on 17/09/13.
 */
public class JourneyPlannerMapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener, GoogleMap.OnMapClickListener,GoogleMap.OnInfoWindowClickListener {

    private MapView mapView;
    private GoogleMap map;

    private TextView textView;
    private EditText searchLocationTextView;

    private Location location;
    private LocationClient locationClient;
    private LatLng destinationLatLng;
    private AlertDialogHelper dialogHelper;

    private HashMap<Marker,String> locMap = new HashMap<Marker,String>();

    JourneyPlannerMapListener jpCallback;





    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            MapsInitializer.initialize(getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }

        View rootView = inflater.inflate(R.layout.journey_planner_map,container,false);
        Button button = (Button) rootView.findViewById(R.id.setLocationButtonJp);
        Button searchBtn = (Button) rootView.findViewById(R.id.searchMapBtn);
        mapView = (MapView) rootView.findViewById(R.id.mapViewJp);
        mapView.onCreate(savedInstanceState);

        textView = (TextView) rootView.findViewById(R.id.destinationTextJP);
        searchLocationTextView = (EditText) rootView.findViewById(R.id.searchLocationTextJp);

        dialogHelper = new AlertDialogHelper(getActivity());

        map = mapView.getMap();
        map.setMyLocationEnabled(true);
        map.setOnMapClickListener(this);
        map.setOnInfoWindowClickListener(this);
        GeoUtils.loadBrisbaneArea(map);
        button.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        searchLocationTextView.setOnClickListener(this);
        return rootView;
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
        locationClient.disconnect();

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onClick(View view) {
        String searchVal = searchLocationTextView.getText().toString();
        if(view.getId() == R.id.setLocationButtonJp) {
            if(destinationLatLng != null) {
               jpCallback.clickSetLocation(destinationLatLng,textView.getText().toString());
                //Toast.makeText(getActivity(),"coords lat:"+destinationLatLng.latitude+" coords long:"+destinationLatLng.longitude+" ",Toast.LENGTH_LONG).show();
            }
            else {
                if(searchVal == null || searchVal.isEmpty()) {
                    AlertDialog dialog = dialogHelper.createAlertDialog("Warning","Please make sure to click on any location on the map to register your destination",false);
                    dialog.show();
                }
                else {
                    AlertDialog dialog = dialogHelper.createAlertDialog("Warning","Please click on any map marker then on its information tag to register your destination",false);
                    dialog.show();
                }
            }
        }
        if(view.getId() == R.id.searchMapBtn) {
            map.clear();

            if(searchVal == null || searchVal.isEmpty()) {
                AlertDialog dialog = dialogHelper.createAlertDialog("Warning","Please add a search value",false);
                dialog.show();
            }
            else {
                GeoCodingTask task = new GeoCodingTask(this,false);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,searchVal);
            }
        }
        if(view.getId() == R.id.searchLocationTextJp) {
           searchLocationTextView.setText("");
            destinationLatLng = null;
            textView.setText("");
        }

    }

    public void displaySearchedMarkers(ArrayList<LocationPojo> list) {
        if(!list.isEmpty()) {
            for(LocationPojo pojo:list) {

                Marker marker = map.addMarker(createMarkerOptions(new LatLng(pojo.latitude,pojo.longtitude),pojo.addressName,""));
                locMap.put(marker,pojo.addressName);
            }
            GeoUtils.loadBrisbaneArea(map);
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

    public void onStart() {
        super.onStart();
        setupLocationClientIfNeeded();
        locationClient.connect();

    }



    @Override
    public void onStop() {

        if (locationClient.isConnected()) {
            locationClient.removeLocationUpdates(this);
        }
        locationClient.disconnect();
        super.onStop();
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

    @Override
    public void onMapClick(LatLng latLng) {
        destinationLatLng = null;
        textView.setText("");
        map.clear();
        map.addMarker(createMarkerOptions(latLng,"",""));
        destinationLatLng = latLng;
        GeoCodingTask task = new GeoCodingTask(this,true);
        task.execute(latLng);


    }

    public void setDestinationText(String text) {
        this.textView.setText(text);

    }

    private MarkerOptions createMarkerOptions(LatLng position,String title,String snippet) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        markerOptions.title(title);
        //markerOptions.snippet("");
        return markerOptions;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        destinationLatLng = null;
        destinationLatLng = marker.getPosition();
        textView.setText("");

        GeoCodingTask task = new GeoCodingTask(this,true);
        task.execute(marker.getPosition());
    }

    public interface JourneyPlannerMapListener {
        public void clickSetLocation(LatLng destination,String locality);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
           jpCallback = (JourneyPlannerMapListener) activity;
        } catch(ClassCastException e) {
            throw new ClassCastException(activity.toString()+" must implement JourneyPlannerMapListener");
        }
    }
}
