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

/**
 * Created by fernandoMac on 19/09/13.
 */
public class GeoCodingTask extends AsyncTask<Object,Void,String> {

    Fragment myFragment = null;
    ProgressDialog pd = null;

     public GeoCodingTask(Fragment fragment) {
         myFragment = fragment;
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

    @Override
    protected String doInBackground(Object... objects) {

        StringBuffer sbuff = new StringBuffer();
        String url = "http://maps.googleapis.com/maps/api/geocode/json?sensor=true";
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
                    sbuff.append(s +" \n ");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        return sbuff.toString();
    }

    protected void onPostExecute(String s) {
        pd.dismiss();
        pd = null;
        ((JourneyPlannerMapFragment)myFragment).setDestinationText(s);
    }

}

