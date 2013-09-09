package org.nando.nearestbus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.view.Menu;

import com.bugsense.trace.BugSenseHandler;


import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

public class MainActivity extends Activity {





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this,"433cb4d1");
        if(!CheckConnectivityUtils.weHaveGoogleServices(this)) {
            AlertDialogHelper helper = new AlertDialogHelper(this);
            AlertDialog dialog = helper.createAlertDialog("Warning","You dont have Google play services please download",false);
            dialog.show();
            CheckConnectivityUtils.downloadGooglePlayServices(this);
        }
        else {
            setContentView(R.layout.activity_main);

            ActionBar actionBar = getActionBar();
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            ActionBar.Tab tab = actionBar.newTab();
            tab.setText(R.string.tab_nearestBusStops);
            tab.setTabListener(new MyTabListener(this,NearestStopsFragment.class.getName()));
            actionBar.addTab(tab);
            ActionBar.Tab tab2 = actionBar.newTab();
            tab2.setText(R.string.tab_nearestBusRoutes);
            tab2.setTabListener(new MyTabListener(this,NearestBusRouteFragment.class.getName()));
            actionBar.addTab(tab2);
            ActionBar.Tab tab3 = actionBar.newTab();
            tab3.setText(R.string.tab_whichBus);
            tab3.setTabListener(new MyTabListener(this,FromHereToThereFragment.class.getName()));
            actionBar.addTab(tab3);
            ActionBar.Tab tab4 = actionBar.newTab();
            tab4.setText(R.string.tab_anyBus);
            tab4.setTabListener(new MyTabListener(this,AllBusRouteFragment.class.getName()));
            actionBar.addTab(tab4);

        }


    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private class MyTabListener
            implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mFragName;

        public MyTabListener(Activity activity,
                             String fragName) {
            mActivity = activity;
            mFragName = fragName;
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab,
                                    FragmentTransaction ft) {
            // TODO Auto-generated method stub
        }

        @Override
        public void onTabSelected(ActionBar.Tab tab,
                                  FragmentTransaction ft) {
            mFragment = Fragment.instantiate(mActivity,
                    mFragName);
            ft.add(android.R.id.content, mFragment);
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab,
                                    FragmentTransaction ft) {
            ft.remove(mFragment);
            mFragment = null;
        }
    }



}
