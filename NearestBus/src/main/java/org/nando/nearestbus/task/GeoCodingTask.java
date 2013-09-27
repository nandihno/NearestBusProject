package org.nando.nearestbus.task;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.nando.nearestbus.JourneyPlannerMapFragment;
import org.nando.nearestbus.pojo.LocationPojo;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernandoMac on 19/09/13.
 */
public class GeoCodingTask extends AsyncTask<Object,Void,Object> {

    Fragment myFragment = null;
    ProgressDialog pd = null;
    boolean passLatLng = false;
    private String url = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true";

     public GeoCodingTask(Fragment fragment,boolean isPassLatLng) {
         myFragment = fragment;
         passLatLng = isPassLatLng;
     }


    @Override
    protected void onPreExecute() {
        pd = new ProgressDialog(myFragment.getActivity());
        pd.setTitle("Processing...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.setIndeterminate(true);
        pd.show();
    }

    private String  doLatLngSearch(Object... objects) {
        StringBuffer sbuff = new StringBuffer();
        LatLng latLng = (LatLng) objects[0];
        String latLngSt = latLng.latitude +","+latLng.longitude;
        System.out.println(latLngSt);
        try {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url+"&latlng="+latLngSt);
            HttpResponse response = client.execute(get);
            if(response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String jsonResponse = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonResponse);
                JSONArray jArray = json.getJSONArray("results");
                for(int i=0; i < 1; i++) {
                    JSONObject obj = jArray.getJSONObject(i);
                    String s = obj.getString("formatted_address");
                    sbuff.append(s +"");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbuff.toString();
    }

    @Override
    protected Object doInBackground(Object... objects) {
        if(passLatLng) {
            return doLatLngSearch(objects);
        }
        else {
            return doAddressSearch(objects);
        }


    }

    private ArrayList<LocationPojo> doAddressSearch(Object... objects) {
        ArrayList<LocationPojo> list = new ArrayList<LocationPojo>();

        try {
            String encoded = URLEncoder.encode((String)objects[0],"UTF-8");
            String componentsEncoded = URLEncoder.encode("country:AU|administrative_area:QLD","UTF-8");
            String boundsEncoded = URLEncoder.encode("-28.1204,152.501220,-26.555,153.726196","UTF-8");

            HttpClient client = new DefaultHttpClient();
            String qldOnlyQryStr = "&region=au&components="+componentsEncoded+"&bounds="+boundsEncoded;
            System.out.println("url is:"+url+"&address="+encoded+qldOnlyQryStr);
            HttpGet get = new HttpGet(url+"&address="+encoded+qldOnlyQryStr);
            HttpResponse response = client.execute(get);
            if(response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                String jsonResponse = EntityUtils.toString(entity);
                JSONObject json = new JSONObject(jsonResponse);
                JSONArray jArray = json.getJSONArray("results");
                for(int i =0; i < jArray.length(); i++) {
                    JSONObject jObject = jArray.getJSONObject(i);
                    String address = jObject.getString("formatted_address");
                    JSONObject jObject2 = jObject.getJSONObject("geometry").getJSONObject("location");
                    double lat = jObject2.getDouble("lat");
                    double lng = jObject2.getDouble("lng");
                    LocationPojo latLng = new LocationPojo(lng,lat,address);
                    list.add(latLng);
                }
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    protected void onPostExecute(Object s) {
        pd.dismiss();
        pd = null;
        if(passLatLng) {
            String st = (String) s;
            ((JourneyPlannerMapFragment)myFragment).setDestinationText(st);
        }
        else {
            ArrayList<LocationPojo> list = (ArrayList<LocationPojo>) s;
            ((JourneyPlannerMapFragment)myFragment).displaySearchedMarkers(list);

        }

    }

}

