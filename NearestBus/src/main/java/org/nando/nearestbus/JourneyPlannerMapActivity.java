package org.nando.nearestbus;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fernandoMac on 24/09/13.
 */
public class JourneyPlannerMapActivity extends Activity implements JourneyPlannerMapFragment.JourneyPlannerMapListener {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_planner_map_activity);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    protected void onStop() {
        super.onStop();
        System.out.println("stopping!");

    }

    protected void onStart() {
        super.onStart();

    }

    @Override
    public void clickSetLocation(LatLng destination, String locality) {
        //send to jp activity
    }
}
