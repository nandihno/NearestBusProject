package org.nando.nearestbus;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SlidingPaneLayout;

/**
 * Created by fernandoMac on 17/09/13.
 */
public class JourneyPlannerActivity extends Activity {

    private SlidingPaneLayout mSlidingLayout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.journey_planner_activity);
        mSlidingLayout = (SlidingPaneLayout) findViewById(R.id.sliding_pane_layout);


    }

}
