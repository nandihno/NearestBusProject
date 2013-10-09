package org.nando.nearestbus.task;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.AsyncTask;
import android.webkit.WebView;
import android.widget.Toast;

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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by fernandoMac on 20/09/13.
 */
public class JourneyPlannerWebScrapeTask extends AsyncTask<Object ,Void,String> {

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

    private String readHtmlAsset(String assetName,AssetManager assetManager) throws IOException {
        InputStream input = assetManager.open(assetName);
        int size = input.available();
        byte[] buffer = new byte[size];
        input.read(buffer);
        input.close();
        return new String(buffer);
    }

    @Override
    protected String doInBackground(Object... objects) {
        String results = "";
        try {
            HttpClient client = new DefaultHttpClient();
            String url = (String) objects[0];
            Location location = (Location) objects[1];
            String hour = String.valueOf(objects[2]);
            String minute = String.valueOf(objects[3]);
            String am_pm = (String) objects[4];
            LatLng destination = (LatLng) objects[5];
            Boolean isLeaveAfter = (Boolean) objects[6];


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
            if(isLeaveAfter) {
                data.add(new BasicNameValuePair("TimeSearchMode","LeaveAfter"));
            }
            else {
                data.add(new BasicNameValuePair("TimeSearchMode","ArriveBefore"));
            }

            data.add(new BasicNameValuePair("SearchHour",hour));
            data.add(new BasicNameValuePair("SearchMinute",minute));
            data.add(new BasicNameValuePair("TimeMeridiem",am_pm));
            data.add(new BasicNameValuePair("TransportModes","Bus"));
            data.add(new BasicNameValuePair("TransportModes","Train"));
            data.add(new BasicNameValuePair("TransportModes","Ferry"));
            data.add(new BasicNameValuePair("ServiceTypes","Regular"));
            data.add(new BasicNameValuePair("ServiceTypes","Express"));

            data.add(new BasicNameValuePair("FareTypes","Prepaid"));
            data.add(new BasicNameValuePair("FareTypes","Standard"));
            data.add(new BasicNameValuePair("MaximumWalkingDistance","1500"));
            data.add(new BasicNameValuePair("randomDate",new Date().getTime()+""));
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(data,"UTF-8");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            System.out.println(response.getStatusLine().getStatusCode()+ " <----------- status code!");
            if(response.getStatusLine().getStatusCode() == 200) {
                results = populateResultsToHtml(response);
                //results = populateResultsToHtml2(response);
            }
            else {
                results = "<p style='color:red'>Please try again there has been some error at Translink's end.  Error "+response.getStatusLine().getStatusCode()+"</p>";
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return results;
    }

    private String populateResultsToHtml2(HttpResponse response) throws IOException {
        String html = "";
        String results = "";
        AssetManager assetManager = mainFragment.getActivity().getAssets();
        HttpEntity entityResponse = response.getEntity();
        html = EntityUtils.toString(entityResponse);

        Document doc = Jsoup.parse(html);
        //Elements travelOptionClass = doc.select(".travel-option");

        //Elements optionSummaryClass = travelOptionClass.select(".travel-option-summary");
        //System.out.println("travelOptionClass-->"+travelOptionClass.html());
        //Elements busClass2 = travelOptionClass.select("ul.itinerary");
        Elements contentClass = doc.select("div.content");
        results = this.readHtmlAsset("headHtml.html",assetManager);
        results += this.readHtmlAsset("bodyHtml.html",assetManager);
        //results += busClass2.html();
        results += contentClass.html();
        return results;

    }

    private String populateResultsToHtml(HttpResponse response) throws IOException {
        AssetManager assetManager = mainFragment.getActivity().getAssets();

        String html = "";
        String results = "";
        HttpEntity entityResponse = response.getEntity();
        html = EntityUtils.toString(entityResponse);

        Document doc = Jsoup.parse(html);
        Elements travelOptionClass = doc.select(".travel-option");
        Elements optionSummaryClass = travelOptionClass.select(".travel-option-summary");
        //System.out.println("travelOptionClass-->"+travelOptionClass.html());
        Elements busClass2 = travelOptionClass.select("ul.itinerary");
        //results ="<html><head><meta name='viewport' content='width=device-width' /><style>p {color:#F79429;font-weight:bold;} ol {color:white;} ol.bolder{font-weight:bold;color:white;font-size:15pt;}</style></head><body style='background-color:#004B88'>";
        if(busClass2.size() > 0) {
        results = this.readHtmlAsset("headHtml.html",assetManager);
        results += this.readHtmlAsset("bodyHtml.html",assetManager);


        System.out.println("busClass2 size is:"+busClass2.size());
        for(int a=0; a < busClass2.size(); a++) {
            Element busClassElem = busClass2.get(a);

            Elements itineryBusClass = busClassElem.select("li");

            // System.out.println("itineraryBysClass size ------>"+itineryBusClass.size() +"html: "+itineryBusClass.html());
            results += "<span class='whiteBold'>OPTION "+(a+1)+"</span> ";

            Element optionSummElem = optionSummaryClass.get(a);
            results += optionSummElem.html();
            for(int b=0; b < itineryBusClass.size(); b++) {
                boolean hasBusClass = false;
                boolean hasTrainClass = false;
                boolean hasFerryClass = false;
                boolean hasWalkClass = false;
                Element busRouteCodesElem = itineryBusClass.get(b);
                if(busRouteCodesElem.hasClass("bus")) {
                    hasBusClass = true;
                }
                if(busRouteCodesElem.hasClass("train")) {
                    hasTrainClass = true;
                }
                if(busClassElem.hasClass("ferry")) {
                    hasFerryClass = true;
                }
                if(busClassElem.hasClass("walk")) {
                    hasWalkClass = true;
                }
                Element optionDetailsElem = itineryBusClass.get(b);
                Elements busRouteCodes = busRouteCodesElem.select(".translink-route-code");
                Elements optionDetails = optionDetailsElem.select("ul.option-detail");
                for(int k = 0; k < optionDetails.size(); k++) {
                    Element busRoute = busRouteCodes.get(k);
                    Element optionDetail = optionDetails.get(k);

                    Element depart = optionDetail.child(1);
                    Element arrive = optionDetail.child(2);
                    Element travelTime = optionDetail.child(3);

                           /*
                            JourneyPlannerDisplayInfo pojo = new JourneyPlannerDisplayInfo();
                            pojo.setBusRoute(busRoute.text());
                            pojo.setDepart(depart.text());
                            pojo.setArrive(arrive.text());
                            pojo.setTravelTime(travelTime.text());
                            */
                    results += "<ul><ol class='bolder'>"+writeTransportHtml(busRoute.text(),hasBusClass,hasTrainClass,hasFerryClass,hasWalkClass)+"</ol><ol>"+depart.text()+"</ol><ol>"+arrive.text()+"</ol><ol>"+travelTime.text()+"</ol></ul>";


                }
            }
        }
        }
        else  {
            results += this.readHtmlAsset("noInformationFound.html",assetManager);

        }
        //results += "</body></html>";
        results += this.readHtmlAsset("footerHtml.html",assetManager);
        System.out.println(results);
        return results;

    }

    private  String writeTransportHtml(String routeNo,boolean hasBusclass,boolean hasTrainclass,boolean hasFerryClass,boolean hasWalkclass) {
        if(hasBusclass) {
            return "Take <b>bus</b> "+routeNo;
        }
        else if(hasTrainclass) {
            return "Catch the <b>train</b>";
        }
        else if(hasFerryClass) {
            return "Take the <b>Ferry</b> "+routeNo;
        }
        else if (hasWalkclass) {
            return "Walk ";
        }
        else {
            return routeNo;
        }

    }


    protected void onPostExecute(String res) {
        pd.dismiss();
        pd = null;
        ((JourneyPlannerFragment)mainFragment).displayWebResults(res);
    }

}
