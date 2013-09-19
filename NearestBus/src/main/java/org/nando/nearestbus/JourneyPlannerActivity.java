package org.nando.nearestbus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fernandoMac on 17/09/13.
 */
public class JourneyPlannerActivity extends Activity implements JourneyPlannerMapFragment.JourneyPlannerMapListener, JourneyPlannerFragment.JourneyPlannerListener {

    private SlidingPaneLayout mSlidingLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_planner_activity);
        setSlidingLayoutIfNeeded();


    }

    protected void onStop() {
        super.onStop();
        System.out.println("stopping!");
        mSlidingLayout = null;
    }

    protected void onStart() {
        super.onStart();
        setSlidingLayoutIfNeeded();
    }


    private void setSlidingLayoutIfNeeded() {
        if(mSlidingLayout == null) {
            mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);

        }
    }

    @Override
    public void clickSetLocation(LatLng destination,String locality) {
        JourneyPlannerFragment jp = (JourneyPlannerFragment) getFragmentManager().findFragmentById(R.id.jp);
        jp.setDestination(destination,locality);
        mSlidingLayout.closePane();

    }

    @Override
    public void openJPMap() {
        mSlidingLayout.openPane();
    }
}
