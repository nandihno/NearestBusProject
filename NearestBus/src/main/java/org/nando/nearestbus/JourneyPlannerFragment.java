package org.nando.nearestbus;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.location.Location;
import android.os.AsyncTask;
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
import android.widget.ToggleButton;

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

import java.util.ArrayList;
import java.util.Calendar;


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

    private boolean isLeaveAfterTimeSearchMode = true;
    private ToggleButton toggleBtn;
    private Button jpPicker;
    private TextView beThereTxt;

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
        jpPicker = (Button) rootView.findViewById(R.id.jpTimePicker);
        jpPicker.setOnClickListener(this);
        Button chooseDestinationJp = (Button) rootView.findViewById(R.id.chooseDestinationJp);
        chooseDestinationJp.setOnClickListener(this);
        toggleBtn = (ToggleButton) rootView.findViewById(R.id.toggleButtonJp);
        toggleBtn.setOnClickListener(this);

        //listViewJp = (ListView) rootView.findViewById(R.id.listViewJp);
        webView = (WebView) rootView.findViewById(R.id.webViewJp);

        jpTextView = (TextView) rootView.findViewById(R.id.jpTextView);
        jpTextView.setText("");
        fromToJpTxt = (TextView) rootView.findViewById(R.id.fromToJPTxt);
        fromToJpTxt.setText("From current location to ?");
        beThereTxt = (TextView) rootView.findViewById(R.id.beThereTxt);

        dialogHelper = new AlertDialogHelper(getActivity());
        String html="<html><head></head><body background='#004B88'></body></html>";
        webView.loadData(html,"text/html",null);
        webView.setVisibility(View.INVISIBLE);




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
                    /**
                     * Uncomment when ready

                    webView.loadData("","text/html",null);
                    JourneyPlannerWebScrapeTask task = new JourneyPlannerWebScrapeTask(this);
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,url,location,hour,minute,am_pm,destination,isLeaveAfterTimeSearchMode);
                     */
                    final ProgressDialog pd = new ProgressDialog(getActivity());
                    webView.clearFormData();
                    webView.clearHistory();
                    webView.clearCache(true);

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
                    if(isLeaveAfterTimeSearchMode) {
                        sbuff.append("TimeSearchMode=LeaveAfter&");
                    }
                    else {
                        sbuff.append("TimeSearchMode=ArriveBefore&");
                    }
                    sbuff.append("SearchHour="+hour+"&");
                    sbuff.append("SearchMinute="+minute+"&");
                    sbuff.append("TimeMeridiem="+am_pm+"&");
                    sbuff.append("TransportModes=Bus&");
                    sbuff.append("TransportModes=Train&");
                    sbuff.append("TransportModes=Ferry&");
                    sbuff.append("ServiceTypes=Regular&");
                    sbuff.append("ServiceTypes=Express&");
                    sbuff.append("ServiceTypes=NightLink&");
                    sbuff.append("FareTypes=Prepaid&");
                    sbuff.append("FareTypes=Standard&");
                    sbuff.append("MaximumWalkingDistance=1500");
                    System.out.println(url+sbuff.toString());
                    webView.getSettings().setJavaScriptEnabled(true);
                    webView.setWebViewClient(new WebViewClient(){
                        public void onPageFinished(WebView view, String url) {
                            if(pd.isShowing()) {
                                pd.dismiss();
                            }
                        }
                    });
                    webView.setBackgroundColor(0x00000000);
                    webView.setVisibility(View.VISIBLE);
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
        if(view.getId() == R.id.toggleButtonJp) {
            if(toggleBtn.isChecked()) {
                this.isLeaveAfterTimeSearchMode = true;
                jpPicker.setText("Leave after");
                beThereTxt.setText("Need to leave after");

            }
            else {
                this.isLeaveAfterTimeSearchMode = false;
                jpPicker.setText("Arrive before");
                beThereTxt.setText("Need to arrive before");
            }
        }

    }

    public void displayWebResults(String resultsHtml) {
        webView.setVisibility(View.VISIBLE);
        webView.loadData(resultsHtml,"text/html","UTF-8");
    }

    public void displayOptionList(ArrayList<JourneyPlannerBusInfo> list) {
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
            if(minute < 10) {
                jpTextView.setText(hour + ":0" + minute + "" + am_pm);
            }
            else {
                jpTextView.setText(hour + ":" + minute + "" + am_pm);
            }
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

    private void setupLocationClientIfNeeded() {
        if(locationClient == null) {
            locationClient = new LocationClient(getActivity(),this,this);
        }
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
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setDestination(LatLng aDestination,String locality) {
        this.destination = aDestination;
        this.fromToJpTxt.setText("From current location to "+locality);

    }


}
