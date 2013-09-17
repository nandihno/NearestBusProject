package org.nando.nearestbus;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;

import org.nando.nearestbus.task.BusRouteScrapeTask;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

import java.util.Calendar;

/**
 * Created by fernandoMac on 13/09/13.
 */
public class JourneyPlannerFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener{


    private WebView webView;
    private TextView textView;

    private Location location;
    private LocationClient locationClient;

    int hour = 0;
    int minute = 0;
    String am_pm = "";




    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(10000)         // 10 seconds
            .setFastestInterval(5000)    // 5 sec
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.journey_planner,container,false);

        webView = (WebView) rootView.findViewById(R.id.webView);
        Button button = (Button) rootView.findViewById(R.id.scrapeButton);
        button.setOnClickListener(this);
        Button jpPicker = (Button) rootView.findViewById(R.id.jpTimePicker);
        textView = (TextView) rootView.findViewById(R.id.jpTextView);
        jpPicker.setOnClickListener(this);


        return rootView;
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.scrapeButton) {
            location = locationClient.getLastLocation();
            if(location == null) {
                CheckConnectivityUtils.showGPSSettingsAlert(getActivity());
            }
            else {
                String url = "http://mobile.jp.translink.com.au/travel-information/journey-planner";
                BusRouteScrapeTask task = new BusRouteScrapeTask(this);
                task.execute(url,location,hour,minute,am_pm);



            }
        }
        if(view.getId() == R.id.jpTimePicker) {
            Calendar now = Calendar.getInstance();
             hour =  now.get(Calendar.HOUR_OF_DAY);
             minute = now.get(Calendar.MINUTE);
            Dialog timeDialog = new TimePickerDialog(getActivity(),timePickerListener,hour,minute,false);
            timeDialog.show();
        }

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int selectedHour, int selectedMinute) {
            hour = selectedHour;
            minute = selectedMinute;
            Calendar dateTime = Calendar.getInstance();
            dateTime.set(Calendar.HOUR_OF_DAY,hour);
            dateTime.set(Calendar.MINUTE,minute);
            if(dateTime.get(Calendar.AM_PM) == Calendar.AM) {
                am_pm = "AM";
            }
            else {
                am_pm = "PM";
            }
            hour = dateTime.get(Calendar.HOUR);
            textView.setText(hour+" - "+minute+" "+am_pm);


            System.out.println("hour:"+hour+" minute:"+minute+" "+am_pm);

        }
    };

    public void loadWebView(String html) {

        webView.loadData(html,"text/html",null);

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


}
