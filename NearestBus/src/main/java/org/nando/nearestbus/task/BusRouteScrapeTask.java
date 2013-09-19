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
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nando.nearestbus.JourneyPlannerFragment;
import org.nando.nearestbus.MainActivity;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.JourneyPlannerDisplayInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fernandoMac on 14/08/13.
 */
public class BusRouteScrapeTask extends AsyncTask<Object ,Void,List<JourneyPlannerDisplayInfo>> {

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
    protected List<JourneyPlannerDisplayInfo> doInBackground(Object... objects) {
        List<JourneyPlannerDisplayInfo> list = new ArrayList<JourneyPlannerDisplayInfo>();
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
            List<BasicNameValuePair> data = new ArrayList<BasicNameValuePair>();
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
                Elements busClass = doc.select("ul.itinerary li:eq(1)");
                Elements walkClass = doc.select("ul.itinerary li:eq(0)");
                Elements walkFinalDestination = doc.select("ul.itinerary li:eq(2)");

                for(int k = 0; k < busClass.size(); k++) {
                    Element busElem = busClass.get(k);
                    Element walkElem = walkClass.get(k);
                    Element walkFinalDestinationElem = walkFinalDestination.get(k);

                    Elements legDurations = busElem.select(".leg-duration");

                    Elements routeCodes = busElem.select(".translink-route-code");
                    //this is not part of li.bus!
                    //Elements fairInfos = elem.select("div.fare-information p:eq(0)");
                    for(int i =0; i < legDurations.size(); i++) {
                        Element legDuration = legDurations.get(i);
                        Element routeCode = routeCodes.get(i);
                        JourneyPlannerDisplayInfo pojo = new JourneyPlannerDisplayInfo();
                        BusRoute busRoute = new BusRoute();
                        busRoute.busRoute = routeCode.text();
                        pojo.setBusRoute(busRoute);
                        pojo.setWalkingElement(walkElem.text());
                        pojo.setBusRouteLegDuration(legDuration.text());
                        pojo.setDestinationElement(walkFinalDestinationElem.text());

                        //Element fairInfo = fairInfos.get(i);
                        //htmlBuff.append("<ul><li>From current destination leave and walk:"+walkElem.text()+"</li>");
                        //htmlBuff.append("<li> Bus : "+routeCode.text()+ " leaves and arrives  "+legDuration.text()+"</li>");

                        //htmlBuff.append("<li>Then Walk :"+walkFinalDestinationElem.text()+"</li></ul><hr/>");
                        list.add(pojo);
                    }
                }

            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    protected void onPostExecute(List<JourneyPlannerDisplayInfo> res) {
        pd.dismiss();
        pd = null;
        if(this.mainFragment instanceof JourneyPlannerFragment) {
            ((JourneyPlannerFragment)mainFragment).displayOptionList(res);
        }
       // mainActivity.fetchBusRoutes(list);

    }




}
