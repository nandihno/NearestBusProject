package org.nando.nearestbus;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import com.bugsense.trace.BugSenseHandler;

import org.nando.nearestbus.adapters.SectionPagerAdapter;

public class MainActivity extends FragmentActivity {

    ViewPager mViewPager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(this,"433cb4d1");
        setContentView(R.layout.activity_main);

        SectionPagerAdapter sectionsPagerAdapter = new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(sectionsPagerAdapter);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



}
