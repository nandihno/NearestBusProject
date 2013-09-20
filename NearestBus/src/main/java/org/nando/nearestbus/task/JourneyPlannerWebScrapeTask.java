package org.nando.nearestbus.task;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.location.Location;
import android.os.AsyncTask;
import android.webkit.WebView;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.nando.nearestbus.JourneyPlannerFragment;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.JourneyPlannerBusInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by fernandoMac on 20/09/13.
 */
public class JourneyPlannerWebScrapeTask extends AsyncTask<Object ,Void,WebView> {

    private Fragment mainFragment;
    ProgressDialog pd = null;

    public JourneyPlannerWebScrapeTask(Fragment fragment) {
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
    protected WebView doInBackground(Object... objects) {



        WebView webView = (WebView) objects[6];
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
            webView.postUrl(url, EncodingUtils.getBytes(sbuff.toString(),"BASE64"));

            /*
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
                Elements travelOptionClass = doc.select(".travel-option");
                Elements busClass2 = travelOptionClass.select("ul.itinerary");
                for(int a=0; a < busClass2.size(); a++) {
                    Element busClassElem = busClass2.get(a);
                    Elements itineryBusClass = busClassElem.select("li.bus");
                    htmlBuff.append("<p style='color:green'>Option "+(a+1)+"</p>");


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
                            pojo.setBusRoute(busRoute1);
                            pojo.setDepart(depart.text());
                            pojo.setArrive(arrive.text());
                            pojo.setTravelTime(travelTime.text());
                            pojo.setIndex(k);
                            htmlBuff.append( "<ul><ol>"+pojo.getBusRoute()+"</ol><ol>"+pojo.getDepart()+"</ol><ol>"+pojo.getArrive()+"</ol><ol>"+pojo.getTravelTime()+"</ol></ul>");
                            pojo.setHtml(htmlBuff.toString());

                            list.add(pojo);
                        }
                    }

                }

            }
            */

        } catch(Exception e) {
            e.printStackTrace();
        }
        //return list;

            return webView;
    }

    protected void onPostExecute(WebView res) {
        pd.dismiss();
        pd = null;



    }

}
