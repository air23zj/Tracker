package com.research.parentapp;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {
    public final static String LAT = "com.research.parentapp.LAT";
    public final static String LONG = "com.research.parentapp.LONG";
    public final static String DIR = "com.research.parentapp.DIR";
    public final static String ARC = "com.research.parentapp.ARC";
    public Location child;
    public double theta; //the angle w.r.t. east
    public double phi; //the arc length of the cone
    public double OUT_OF_RANGE = -1; //out of range distance for bluetooth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        child = new Location("H");

    }

    public void UpdateChild(View view){
        //get info from child, either GPS or Bluetooth
        double blue_dist = 24;
        if(blue_dist != OUT_OF_RANGE){
            TextView blue = (TextView) findViewById(R.id.ChildDistance);
            blue.setText(blue_dist + " m away");
        }else if (child.getLongitude() != OUT_OF_RANGE){
            TextView gps = (TextView) findViewById(R.id.ChildDistance);
            gps.setText("Lat: " + child.getLatitude() + " Long: " + child.getLongitude());
        }else{
            TextView notifier = (TextView) findViewById(R.id.ChildDistance);
            notifier.setText("Child out of Range");
        }

    }

    public void MapSend(View view){
        Intent intent = new Intent(this, DisplayMapActivity.class);
        //update child location from message
        child.setLatitude(25);
        child.setLongitude(25);
        theta = Math.PI/2;//east
        phi = Math.PI/4; //45 degree cone
        //Add the angles to intent to send to map
        intent.putExtra(DIR,theta);
        intent.putExtra(ARC,phi);
        intent.putExtra(LAT,child.getLatitude());
        intent.putExtra(LONG,child.getLongitude());
        startActivity(intent); //Start map view
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
