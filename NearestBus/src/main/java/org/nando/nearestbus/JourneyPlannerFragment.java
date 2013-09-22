package org.nando.nearestbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.util.EncodingUtils;
import org.nando.nearestbus.adapters.JourneyPlannerListAdapter;
import org.nando.nearestbus.pojo.JourneyPlannerBusInfo;
import org.nando.nearestbus.pojo.JourneyPlannerBusOption;
import org.nando.nearestbus.task.BusRouteScrapeTask;
import org.nando.nearestbus.task.JourneyPlannerWebScrapeTask;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

import java.util.Calendar;
import java.util.List;

/**
 * Created by fernandoMac on 13/09/13.
 */
public class JourneyPlannerFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener, View.OnClickListener{



    private TextView jpTextView;
    private TextView fromToJpTxt;
    //private ListView listViewJp;

    private WebView webView;

    private Location location;
    private LocationClient locationClient;

    private LatLng destination;

    private JourneyPlannerListener activityCallBack;

    private AlertDialogHelper dialogHelper;

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


        Button button = (Button) rootView.findViewById(R.id.scrapeButton);
        button.setOnClickListener(this);
        Button jpPicker = (Button) rootView.findViewById(R.id.jpTimePicker);
        jpPicker.setOnClickListener(this);
        Button chooseDestinationJp = (Button) rootView.findViewById(R.id.chooseDestinationJp);
        chooseDestinationJp.setOnClickListener(this);

        //listViewJp = (ListView) rootView.findViewById(R.id.listViewJp);
        webView = (WebView) rootView.findViewById(R.id.webViewJp);

        jpTextView = (TextView) rootView.findViewById(R.id.jpTextView);
        jpTextView.setText("");
        fromToJpTxt = (TextView) rootView.findViewById(R.id.fromToJPTxt);
        fromToJpTxt.setText("");

        dialogHelper = new AlertDialogHelper(getActivity());




        return rootView;
    }

    public interface JourneyPlannerListener {
        public void openJPMap();
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            activityCallBack = (JourneyPlannerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement JourneyPlannerFragment.JourneyPlannerListener ");
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.scrapeButton) {
            //listViewJp.setAdapter(null);
            location = locationClient.getLastLocation();
            if(location == null) {
                CheckConnectivityUtils.showGPSSettingsAlert(getActivity());
            }
            else {
                String url = "http://mobile.jp.translink.com.au/travel-information/journey-planner";
                //JourneyPlannerWebScrapeTask task = new JourneyPlannerWebScrapeTask(this);
                //check if destination is null here
                if(destination == null) {
                    Toast.makeText(getActivity(),"Choose destination",Toast.LENGTH_LONG).show();
                }
                else {
                  //task.execute(url,location,hour,minute,am_pm,destination,webView);
                    final ProgressDialog pd = new ProgressDialog(getActivity());

                    pd.setTitle("Finding options...");
                    pd.setMessage("Please wait.");
                    pd.setCancelable(false);
                    pd.setIndeterminate(true);
                    pd.show();

                    String longLat = location.getLatitude()+","+location.getLongitude();
                    Calendar todayCal = Calendar.getInstance();
                    String date = todayCal.get(Calendar.DATE)+"";
                    String month  = todayCal.get(Calendar.MONTH)+1+"";
                    String year = todayCal.get(Calendar.YEAR)+"";
                    StringBuffer sbuff = new StringBuffer();
                    sbuff.append("Start="+longLat+"&");
                    sbuff.append("End="+destination.latitude+","+destination.longitude+"&");
                    sbuff.append("SearchDate="+date+"-"+month+"-"+year+" 12:00 AM&");
                    sbuff.append("TimeSearchMode=ArriveBefore&");
                    sbuff.append("SearchHour="+hour+"&");
                    sbuff.append("SearchMinute="+minute+"&");
                    sbuff.append("TimeMeridiem="+am_pm+"&");
                    sbuff.append("TransportModes=BUS&");
                    sbuff.append("ServiceTypes=Regular&");
                    sbuff.append("ServiceTypes=Express&");
                    sbuff.append("FareTypes=Prepaid&");
                    sbuff.append("FareTypes=Standard&");
                    sbuff.append("MaximumWalkingDistance=1500");
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url) {
                            if(pd.isShowing()) {
                                pd.dismiss();
                            }
                        }
                    });
                    webView.setBackgroundColor(0x00000000);
                    webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
                    webView.postUrl(url, EncodingUtils.getBytes(sbuff.toString(), "BASE64"));

                }
            }
        }
        if(view.getId() == R.id.jpTimePicker) {
            Calendar now = Calendar.getInstance();
             hour =  now.get(Calendar.HOUR_OF_DAY);
             minute = now.get(Calendar.MINUTE);
            Dialog timeDialog = new TimePickerDialog(getActivity(),timePickerListener,hour,minute,false);
            timeDialog.show();
        }
        if(view.getId() == R.id.chooseDestinationJp) {
            activityCallBack.openJPMap();
        }

    }

    public void displayOptionList(List<JourneyPlannerBusInfo> list) {
        if(list != null && list.size() > 0) {
            JourneyPlannerListAdapter adapter = new JourneyPlannerListAdapter(getActivity(),android.R.layout.simple_list_item_1,list);
            //listViewJp.setAdapter(adapter);
        }
        else {
                AlertDialog dialog = dialogHelper.createAlertDialog("Sorry","Cant find any options",false);
                dialog.show();
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
            if(hour == 0) {
                hour = 12;
            }
            jpTextView.setText(hour + ":" + minute + "" + am_pm);


            System.out.println("hour:"+hour+" minute:"+minute+" "+am_pm);

        }
    };



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

    public void setDestination(LatLng aDestination,String locality) {
        this.destination = aDestination;
        this.fromToJpTxt.setText("From current location to "+locality);

    }


}
