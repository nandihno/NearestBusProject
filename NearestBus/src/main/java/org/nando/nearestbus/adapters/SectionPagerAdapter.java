package org.nando.nearestbus.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import org.nando.nearestbus.AllBusRouteFragment;
import org.nando.nearestbus.FromHereToThereFragment;
import org.nando.nearestbus.NearestBusRouteActivity;
import org.nando.nearestbus.NearestBusRouteMapActivity;
import org.nando.nearestbus.NearestStopsActivity;

import java.util.Locale;

/**
 * Created by fernandoMac on 20/08/13.
 */
public class SectionPagerAdapter extends FragmentPagerAdapter {

    public SectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                return new NearestStopsActivity();
            case 1:
                return new NearestBusRouteActivity();
            case 2:
                return new FromHereToThereFragment();
            case 3:
                return new AllBusRouteFragment();
        }
        return  null;
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
