package org.nando.nearestbus.task;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.nando.nearestbus.MainActivity;
import org.nando.nearestbus.pojo.BusStops;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fernandoMac on 14/08/13.
 */
public class BusRouteScrapeTask extends AsyncTask<List<BusStops>,Void,List<BusStops>> {

    private MainActivity mainActivity;

    private static final  int LIMIT_BUS_SCRAPING = 5;

    public BusRouteScrapeTask(MainActivity activity) {
        mainActivity = activity;
    }


    @Override
    protected List<BusStops> doInBackground(List<BusStops>... lists) {
        List<BusStops> list = new ArrayList<BusStops>();
        HttpClient client = new DefaultHttpClient();
        int sizeOfBusStops = lists[0].size();
        if (sizeOfBusStops < LIMIT_BUS_SCRAPING) {

            for (BusStops stop : lists[0]) {
                try {
                    HttpGet get = new HttpGet(stop.getUrl());
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        String html = EntityUtils.toString(entity);
                        Document doc = Jsoup.parse(html);
                        Elements links = doc.select("table.content-table th:contains(Routes departing) + td");
                        String routes = links.get(0).text();
                        //stop.setBusRoutes(routes);
                        list.add(stop);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            for(int i =0; i < LIMIT_BUS_SCRAPING; i++) {
                BusStops stop = lists[0].get(i);
                try {
                    HttpGet get = new HttpGet(stop.getUrl());
                    HttpResponse response = client.execute(get);
                    if (response.getStatusLine().getStatusCode() == 200) {
                        HttpEntity entity = response.getEntity();
                        String html = EntityUtils.toString(entity);
                        Document doc = Jsoup.parse(html);
                        Elements links = doc.select("table.content-table th:contains(Routes departing) + td");
                        String routes = links.get(0).text();
                        //stop.setBusRoutes(routes);
                        list.add(stop);
                    }

                } catch(Exception e) {

                }
            }
        }
        return list;
    }

    protected void onPostExecute(List<BusStops> list) {
       // mainActivity.fetchBusRoutes(list);

    }




}
