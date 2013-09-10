package org.nando.nearestbus;

import android.app.ActionBar;
import android.app.AlertDialog;

import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.bugsense.trace.BugSenseHandler;


import org.nando.nearestbus.adapters.SectionPagerAdapter;
import org.nando.nearestbus.utils.AlertDialogHelper;
import org.nando.nearestbus.utils.CheckConnectivityUtils;

public class MainActivity extends FragmentActivity {

    ViewPager mViewPager;



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
            SectionPagerAdapter sectionsPagerAdapter = new SectionPagerAdapter(getFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.pager);
            mViewPager.setAdapter(sectionsPagerAdapter);
        }


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
