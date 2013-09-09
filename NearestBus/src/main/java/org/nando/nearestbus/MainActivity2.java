package org.nando.nearestbus;

import android.app.ActionBar;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

import android.app.Fragment;
import android.view.Menu;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        ActionBar ab = getActionBar();
        setListNavigation(ab);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity2, menu);
        return true;
    }

    private void setListNavigation( ActionBar actionBar )
    {
        actionBar.setNavigationMode( ActionBar.NAVIGATION_MODE_LIST );
        actionBar.setTitle( "" );
        final List<Map<String, Object>> data =
                new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put( "title", "Test1" );
        map.put( "fragment", Fragment.instantiate(this, AllBusRouteFragment.class.getName()));
        data.add( map );
        map = new HashMap<String, Object>();
        map.put( "title", "Test2" );
        map.put( "fragment", Fragment.instantiate( this, NearestStopsFragment.class.getName() ));
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
    
}
