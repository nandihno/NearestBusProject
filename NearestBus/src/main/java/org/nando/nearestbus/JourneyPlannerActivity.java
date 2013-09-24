package org.nando.nearestbus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fernandoMac on 17/09/13.
 */
public class JourneyPlannerActivity extends Activity implements JourneyPlannerMapFragment.JourneyPlannerMapListener, JourneyPlannerFragment.JourneyPlannerListener {

    static JourneyPlannerMapFragment jpMapFragment;
    static JourneyPlannerFragment jpFragment;




    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_planner_activity);
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        jpFragment = (JourneyPlannerFragment) getFragmentManager().findFragmentById(R.id.jp);
        jpMapFragment = (JourneyPlannerMapFragment) getFragmentManager().findFragmentById(R.id.jpMap);
        ft.show(jpFragment);
        //ft.hide(jpMapFragment);
        ft.commit();
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
    public void clickSetLocation(LatLng destination,String locality) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        jpFragment.setDestination(destination,locality);
        ft.hide(jpMapFragment);
        ft.show(jpFragment);
        ft.commit();

        //show jpfragment
    }



    @Override
    public void openJPMap() {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.animator.fade_in,android.R.animator.fade_out);
        ft.hide(jpFragment);
        ft.show(jpMapFragment);
        ft.commit();



    }
}
