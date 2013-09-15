package org.nando.nearestbus;

import android.app.ActionBar;
import android.app.AlertDialog;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.widget.SimpleAdapter;

import com.bugsense.trace.BugSenseHandler;


import org.nando.nearestbus.adapters.SectionPagerAdapter;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    ViewPager mViewPager;
    SectionPagerAdapter sectionsPagerAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this,"433cb4d1");
        setContentView(R.layout.activity_main);
        if(!CheckConnectivityUtils.weHaveGoogleServices(this)) {
            AlertDialogHelper helper = new AlertDialogHelper(this);
            AlertDialog dialog = helper.createAlertDialog("Warning","You dont have Google play services please download",false);
            dialog.show();
            CheckConnectivityUtils.downloadGooglePlayServices(this);
        }
        else {
            sectionsPagerAdapter = new SectionPagerAdapter(getFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.pager);
            setAdapterOnViewPagerIfNeeded();
           // ActionBar ab = getActionBar();
            //setListNavigation(ab);
        }
    }

    protected void onStart() {
        super.onStart();
        setAdapterOnViewPagerIfNeeded();

    }

    private void setAdapterOnViewPagerIfNeeded() {
        if(mViewPager == null) {
            mViewPager = (ViewPager) findViewById(R.id.pager);
        }
        if(mViewPager.getAdapter() == null) {
            mViewPager.setAdapter(sectionsPagerAdapter);
        }
    }

    protected void onStop() {
        super.onStop();
        System.out.println("stopping!");
        mViewPager.setAdapter(null);
    }

    private void setListNavigation( ActionBar actionBar )
    {
        actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_LIST );
        actionBar.setTitle( "" );
        final List<Map<String, Object>> data =
                new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "title", "Test1" );
        map.put( "fragment", Fragment.instantiate(this, JourneyPlannerFragment.class.getName()));
        data.add( map );

        SimpleAdapter adapter = new SimpleAdapter( this, data,
                android.R.layout.simple_spinner_dropdown_item,
                new String[] { "title" }, new int[] { android.R.id.text1 } );
        actionBar.setListNavigationCallbacks( adapter,
                new ActionBar.OnNavigationListener()
                {

                    @Override
                    public boolean onNavigationItemSelected( int itemPosition,
                                                             long itemId )
                    {
                        Map<String, Object> map = data.get( itemPosition );
                        Object o = map.get( "fragment" );
                        if( o instanceof Fragment )
                        {
                            FragmentTransaction tx = getFragmentManager().beginTransaction();

                            tx.replace( android.R.id.content, (Fragment)o );
                            tx.commit();
                        }
                        return true;
                    }
                }
        );
    }





/*
  //Not needed yet
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    */





}
