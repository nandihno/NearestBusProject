package org.nando.nearestbus.datasource;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import org.nando.nearestbus.DatabaseHelper;
import org.nando.nearestbus.pojo.BusRoute;
import org.nando.nearestbus.pojo.BusStops;
import org.nando.nearestbus.pojo.LocationPojo;
import org.nando.nearestbus.utils.GeoUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by fernandoMac on 4/08/13.
 */
public class BusStopDataSource {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;

    private String[] allColumns = {"field1","field2","field3","field4","field5","field6","field7","field8","field9","field10","field11"};

    public BusStopDataSource(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getReadableDatabase();
    }

    public void close() throws SQLException {
        if(database.isOpen()) {
            dbHelper.close();
        }
    }

    public List<BusStops> getAllBusStops() {
        List<BusStops> stops = new ArrayList<BusStops>();
        Cursor cursor = database.query("STOPS",allColumns,null,null,null,null,null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            BusStops stop = new BusStops();
            stop.setName(cursor.getString(2));
            stops.add(stop);
            cursor.moveToNext();
        }
        cursor.close();
        return stops;
    }

    public List<BusStops> getNearestBusStop(LocationPojo p1, LocationPojo p2, LocationPojo p3, LocationPojo p4) {
        List<BusStops> list = new ArrayList<BusStops>();
        Cursor cursor = database.rawQuery("select DISTINCT * from STOPS where stop_lat > ? AND " +
                                          "stop_lat < ? AND " +
                                          "stop_lon < ? AND " +
                                          "stop_lon > ?",new String[]{String.valueOf(p3.latitude),String.valueOf(p1.latitude),String.valueOf(p2.longtitude),String.valueOf(p4.longtitude)});
        cursor.moveToFirst();
        while(!cursor.isAfterLast()) {
            BusStops stop = new BusStops();
            stop.setId(cursor.getString(0));
            stop.setName(cursor.getString(2));
            stop.setLatitude(cursor.getDouble(4));
            stop.setLongtitude(cursor.getDouble(5));
            stop.setZone(cursor.getString(6));
            stop.setUrl(cursor.getString(7));
            stop.setBusRoutes(fetchBusRoutesForEachStop(cursor.getString(0)));
            list.add(stop);
            cursor.moveToNext();
        }
        cursor.close();

        return list;
    }

    public List<BusStops> fetchBusStopsForABusRoute(String busRoute, LocationPojo p1, LocationPojo p2, LocationPojo p3, LocationPojo p4) {
        String table1 = "stop_times_thursday_1";
        String table2 = "stop_times_thursday2";
        String table3 = "stop_times_thursday_3";
        String table4 = "stop_times_thursday_4";
        List<BusStops> list  = new ArrayList();
        String sqlp1 = "select distinct stp.stop_id, stp.stop_name,stp.stop_lat,stp.stop_lon,stp.stop_url,stp.zone_id from ";
        String sqlp2 = " thurs, trips trp, routes rts, stops stp where thurs.trip_id = trp.trip_id "+
                "and trp.route_id = rts.route_id "+
                "and stp.stop_id = thurs.stop_id "+
                "and (rts.route_short_name = ?  OR rts.route_short_name = ?) "+
                "and stp.stop_lat > ? and stp.stop_lat < ? and stp.stop_lon < ? and stp.stop_lon > ?";
        busRoute = busRoute.trim();
        busRoute = busRoute.toUpperCase();
        String [] arrParams = new String[]{busRoute,"P"+busRoute, String.valueOf(p3.latitude),String.valueOf(p1.latitude),String.valueOf(p2.longtitude),String.valueOf(p4.longtitude)};
        Cursor cursor = database.rawQuery(sqlp1+table1+sqlp2,arrParams);
        cursor.moveToNext();
        if(cursor.getCount() != 0) {
            list = populateBusStopListFrom(cursor);
        }
        else {
            Cursor cursor2 = database.rawQuery(sqlp1+table2+sqlp2,arrParams);
            cursor2.moveToNext();
            if(cursor2.getCount() != 0) {
                list = populateBusStopListFrom(cursor2);
            }
            else {
                Cursor cursor3 = database.rawQuery(sqlp1+table3+sqlp2,arrParams);
                cursor3.moveToNext();
                if(cursor3.getCount() != 0) {
                    list = populateBusStopListFrom(cursor3);
                }
                else {
                    Cursor cursor4 = database.rawQuery(sqlp1+table4+sqlp2,arrParams);
                    cursor4.moveToNext();
                    if(cursor4.getCount() != 0) {
                        list = populateBusStopListFrom(cursor4);
                    }
                }
            }
        }
        return list;
    }

    private List<BusStops> populateBusStopListFrom(Cursor cursor) {
        List<BusStops> list = new ArrayList<BusStops>();
        while(!cursor.isAfterLast()) {
            BusStops stops = new BusStops();
            stops.setId(cursor.getString(0));
            stops.setName(cursor.getString(1));
            stops.setLongtitude(cursor.getDouble(3));
            stops.setLatitude(cursor.getDouble(2));
            stops.setUrl(cursor.getString(4));
            stops.setZone(cursor.getString(5));
            list.add(stops);
            cursor.moveToNext();
        }
        cursor.close();
        return list;
    }

    public List<BusRoute> fetchBusRoutesForEachStop(String stopId) {
        String table1 = "stop_times_thursday_1";
        String table2 = "stop_times_thursday2";
        String table3 = "stop_times_thursday_3";
        String table4 = "stop_times_thursday_4";
        List<BusRoute> list = null;
        String sqlp1 = "select distinct rts.route_short_name,rts.route_id from ";
        String sqlp2 = " thurs, trips trp, routes rts where thurs.stop_id = ?" +
                " and trp.trip_id = thurs.trip_id and rts.route_id = trp.route_id ";
        String [] arrParams = new String[] {stopId};
        Cursor cursor = database.rawQuery(sqlp1+table1+sqlp2,arrParams);
        list = performCursorFunctionOnStopTimesTables(cursor);
        if(list.size() == 0) {
            Cursor cursor2 = database.rawQuery(sqlp1+table2+sqlp2,arrParams);
            list = performCursorFunctionOnStopTimesTables(cursor2);
            if(list.size() == 0) {
                Cursor cursor3 = database.rawQuery(sqlp1+table3+sqlp2,arrParams);
                list = performCursorFunctionOnStopTimesTables(cursor3);
                if(list.size() == 0) {
                    Cursor cursor4 = database.rawQuery(sqlp1+table4+sqlp2,arrParams);
                    list = performCursorFunctionOnStopTimesTables(cursor4);
                }
            }
        }
        if(list != null && list.size() > 0) {
            return list;
        }
        else {
            return new ArrayList<BusRoute>();
        }

    }

    public List<String> fetchAllSuburbsInQld() {
        String sql = "select locality from suburbs where state = 'QLD' ";
        List<String> suburbs = new ArrayList<String>();
        Cursor cursor = database.rawQuery(sql,null);
        cursor.moveToNext();
        if(cursor.getCount() !=0) {
            while(!cursor.isAfterLast()) {
                suburbs.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        return suburbs;
    }

    public List<BusRoute> findBusesThatGoTo(String suburbName, LocationPojo currentLocation) {
        List<BusRoute> list = new ArrayList<BusRoute>();
        suburbName = suburbName.toUpperCase();
        suburbName = suburbName.trim();
        LocationPojo suburbLatLng = new LocationPojo();
        String sql = "select Lat, Long from suburbs where state = 'QLD' and locality = ? LIMIT 1";
        Cursor cursor = database.rawQuery(sql,new String[] {suburbName});
        cursor.moveToNext();
        if(cursor.getCount() != 0) {
            while (!cursor.isAfterLast()) {
                suburbLatLng.latitude = Double.valueOf(cursor.getString(0));
                suburbLatLng.longtitude = Double.valueOf(cursor.getString(1));
                cursor.moveToNext();
            }
        }
        cursor.close();
        LocationPojo p1 = GeoUtils.calculateDerivedPosition(suburbLatLng,1600,0);
        LocationPojo p2 = GeoUtils.calculateDerivedPosition(suburbLatLng,1600,90);
        LocationPojo p3 = GeoUtils.calculateDerivedPosition(suburbLatLng,1600,180);
        LocationPojo p4 = GeoUtils.calculateDerivedPosition(suburbLatLng,1600,270);
        List<BusStops> allbusesInThatSuburb = this.getNearestBusStop(p1,p2,p3,p4);
        List<BusRoute> allRoutesInThatSuburb = this.listOfAllBusRoutes(allbusesInThatSuburb);

        p1 = GeoUtils.calculateDerivedPosition(currentLocation,GeoUtils.RANGE_IN_METERS,0);
        p2 = GeoUtils.calculateDerivedPosition(currentLocation,GeoUtils.RANGE_IN_METERS,90);
        p3 = GeoUtils.calculateDerivedPosition(currentLocation,GeoUtils.RANGE_IN_METERS,180);
        p4 = GeoUtils.calculateDerivedPosition(currentLocation,GeoUtils.RANGE_IN_METERS,270);
        List<BusStops> allBusesNearMe = this.getNearestBusStop(p1,p2,p3,p4);
        List<BusRoute> allRoutesNearMe = this.listOfAllBusRoutes(allBusesNearMe);

        if(allRoutesNearMe.size() > allRoutesInThatSuburb.size()) {
           for(BusRoute route:allRoutesInThatSuburb) {
               if(allRoutesNearMe.contains(route)) {
                   list.add(route);
               }
           }
        }
        else {
            for(BusRoute route:allRoutesNearMe) {
                if(allRoutesInThatSuburb.contains(route)) {
                    list.add(route);
                }
            }
        }
        Set<BusRoute> set = new HashSet<BusRoute>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;

    }

    public void setAllStopsForBusNumber(BusRoute busRoute) {
        List<LocationPojo> list = new ArrayList<LocationPojo>();
        String thurs1 = "stop_times_thursday_1 ";
        String thurs2 = "stop_times_thursday2 ";
        String thurs3 = "stop_times_thursday_3 ";
        String thurs4 = "stop_times_thursday_4 ";
        String sql = "select stp.stop_id, stp.stop_lat, stp.stop_lon from trips trp, Stops stp, ";
        String sql2 = " thurs where trp.trip_id = thurs.trip_id and thurs.stop_id = stp.stop_id and "+
                "trp.route_id = ?";
        Cursor cursor = database.rawQuery(sql+thurs1+sql2,new String[]{busRoute.busRouteId});
        performCursorDisplayAllStopsFunction(cursor, list);
        Cursor cursor2 = database.rawQuery(sql+thurs2+sql2,new String[]{busRoute.busRouteId});
        performCursorDisplayAllStopsFunction(cursor2,list);
        Cursor cursor3 = database.rawQuery(sql+thurs3+sql2,new String[]{busRoute.busRouteId});
        performCursorDisplayAllStopsFunction(cursor3,list);
        Cursor cursor4 = database.rawQuery(sql+thurs4+sql2,new String[]{busRoute.busRouteId});
        performCursorDisplayAllStopsFunction(cursor4,list);
        busRoute.locations = list;


    }

    private void performCursorDisplayAllStopsFunction(Cursor cursor,List<LocationPojo>list) {
        cursor.moveToNext();
        if(cursor.getCount() != 0) {
            while(!cursor.isAfterLast()) {
                LocationPojo location = new LocationPojo(cursor.getDouble(2),cursor.getDouble(1));
                list.add(location);
                cursor.moveToNext();
            }
        }
        cursor.close();
    }



    List<BusRoute> listOfAllBusRoutes(List<BusStops> busStops) {
        List<BusRoute> routes = new ArrayList<BusRoute>();
        for(BusStops stop:busStops) {
            List<BusRoute> busRoute = stop.getBusRoutes();
            routes.addAll(busRoute);
        }
        return routes;
    }



    private List<BusRoute> performCursorFunctionOnStopTimesTables(Cursor cursor) {
        List<BusRoute> list =new ArrayList<BusRoute>();
        cursor.moveToNext();
        if(cursor.getCount() != 0) {
            while(!cursor.isAfterLast()) {
                BusRoute route = new BusRoute();
                route.busRoute = cursor.getString(0);
                route.busRouteId = cursor.getString(1);
                list.add(route);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return  list;
    }


}
