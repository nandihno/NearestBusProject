package org.nando.nearestbus;

import android.support.v4.app.Fragment;

import org.nando.nearestbus.pojo.LocationPojo;

/**
 * Created by fernandoMac on 20/08/13.
 */
public abstract class BaseActivityFragment extends Fragment {

    public abstract void findNearestInDB(LocationPojo locationPojo);
}
