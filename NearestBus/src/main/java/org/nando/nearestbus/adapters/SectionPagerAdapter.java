package org.nando.nearestbus.adapters;

/**
 * Created by fernandoMac on 10/09/13.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;

import org.nando.nearestbus.AllBusRouteFragment;
import org.nando.nearestbus.FromHereToThereFragment;
import org.nando.nearestbus.JourneyPlannerFragment;
import org.nando.nearestbus.NearestBusRouteFragment;
import org.nando.nearestbus.NearestStopsFragment;


public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new NearestStopsFragment();
            case 1:
                return new NearestBusRouteFragment();
            case 2:
                return new FromHereToThereFragment();
            case 3:
                return new AllBusRouteFragment();

        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Find nearest bus stops";
            case 1:
                return "Find nearest bus routes";
            case 2:
                return "Which bus goes there";
            case 3:
                return "Stops for any bus";


        }
        return null;
    }


}
