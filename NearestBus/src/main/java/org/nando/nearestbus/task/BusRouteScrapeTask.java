package org.nando.nearestbus.task;

import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nando.nearestbus.JourneyPlannerFragment;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.JourneyPlannerBusInfo;
import org.nando.nearestbus.pojo.JourneyPlannerBusOption;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by fernandoMac on 14/08/13.
 */
public class BusRouteScrapeTask extends AsyncTask<Object ,Void,ArrayList<JourneyPlannerBusInfo>> {

    private Fragment mainFragment;
    ProgressDialog pd = null;

    private static final  int LIMIT_BUS_SCRAPING = 5;

    public BusRouteScrapeTask(Fragment fragment) {
        mainFragment = fragment;
    }

    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(mainFragment.getActivity());
        pd.setTitle("Finding options...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
    }


    @Override
    protected ArrayList<JourneyPlannerBusInfo> doInBackground(Object... objects) {
        ArrayList<JourneyPlannerBusInfo> list = new ArrayList<JourneyPlannerBusInfo>();

        String html = "";
        StringBuffer htmlBuff = new StringBuffer();
        try {
            HttpClient client = new DefaultHttpClient();
            String url = (String) objects[0];
            Location location = (Location) objects[1];
            String hour = String.valueOf(objects[2]);
            String minute = String.valueOf(objects[3]);
            String am_pm = (String) objects[4];
            LatLng destination = (LatLng) objects[5];
            String longLat = location.getLatitude()+","+location.getLongitude();
            Calendar todayCal = Calendar.getInstance();
            String date = todayCal.get(Calendar.DATE)+"";
            String month  = todayCal.get(Calendar.MONTH)+1+"";
            String year = todayCal.get(Calendar.YEAR)+"";



            HttpPost post = new HttpPost(url);
            ArrayList<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
            data.add(new BasicNameValuePair("Start",longLat));
            data.add(new BasicNameValuePair("End",destination.latitude+","+destination.longitude));
            data.add(new BasicNameValuePair("SearchDate",date+"-"+month+"-"+year+" 12:00 AM"));
            data.add(new BasicNameValuePair("TimeSearchMode","ArriveBefore"));
            data.add(new BasicNameValuePair("SearchHour",hour));
            data.add(new BasicNameValuePair("SearchMinute",minute));
            data.add(new BasicNameValuePair("TimeMeridiem",am_pm));
            data.add(new BasicNameValuePair("TransportModes","BUS"));
            data.add(new BasicNameValuePair("ServiceTypes","Regular"));
            data.add(new BasicNameValuePair("ServiceTypes","Express"));

            data.add(new BasicNameValuePair("FareTypes","Prepaid"));
            data.add(new BasicNameValuePair("FareTypes","Standard"));
            data.add(new BasicNameValuePair("MaximumWalkingDistance","1500"));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data,"UTF-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);

            if(response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entityResponse = response.getEntity();
                html = EntityUtils.toString(entityResponse);
                Document doc = Jsoup.parse(html);
                Elements travelOptionClass = doc.select(".travel-option");
                Elements busClass2 = travelOptionClass.select("ul.itinerary");
                for(int a=0; a < busClass2.size(); a++) {
                    Element busClassElem = busClass2.get(a);
                    Elements itineryBusClass = busClassElem.select("li.bus");

                    for(int b=0; b < itineryBusClass.size(); b++) {
                        Element busRouteCodesElem = itineryBusClass.get(b);
                        Element optionDetailsElem = itineryBusClass.get(b);
                        Elements busRouteCodes = busRouteCodesElem.select(".translink-route-code");
                        Elements optionDetails = optionDetailsElem.select("ul.option-detail");
                        for(int k = 0; k < optionDetails.size(); k++) {
                            Element busRoute = busRouteCodes.get(k);
                            Element optionDetail = optionDetails.get(k);

                            Element depart = optionDetail.child(1);
                            Element arrive = optionDetail.child(2);
                            Element travelTime = optionDetail.child(3);

                            JourneyPlannerBusInfo pojo = new JourneyPlannerBusInfo();
                            BusRoute busRoute1 = new BusRoute();
                            busRoute1.busRoute = busRoute.text();
                            pojo.busRoute= busRoute1;
                            pojo.depart = depart.text();
                            pojo.arrive = arrive.text();
                            pojo.travelTime =travelTime.text();
                            pojo.index = k;
                            list.add(pojo);
                        }
                    }

                }

            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    protected void onPostExecute(ArrayList<JourneyPlannerBusInfo> res) {
        pd.dismiss();
        pd = null;
        if(this.mainFragment instanceof JourneyPlannerFragment) {
            //((JourneyPlannerFragment)mainFragment).displayOptionList(res);
        }
       // mainActivity.fetchBusRoutes(list);

    }




}
