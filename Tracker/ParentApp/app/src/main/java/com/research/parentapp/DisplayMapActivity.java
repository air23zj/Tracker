package com.research.parentapp;

import android.content.Intent;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;


public class DisplayMapActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public double direction;
    public double arc;
    public LatLng child;
    public double range =.005;
    GoogleApiClient mGoogleApiClient;
    Location my_loc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_map);
        Intent intent = getIntent();
        buildGoogleApiClient();
        direction = intent.getDoubleExtra(MainActivity.DIR,0);
        arc = intent.getDoubleExtra(MainActivity.ARC,Math.PI/4);
        child = new LatLng(intent.getDoubleExtra(MainActivity.LAT,50),intent.getDoubleExtra(MainActivity.LONG,50));

        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_map, menu);
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

    protected synchronized void buildGoogleApiClient() {
         mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        if(mapFragment!=null){
            mapFragment.getMapAsync(this);
            GoogleMap map = mapFragment.getMap(); //update and get the current map
            map.setMyLocationEnabled(true);
        }

    }
    public void updated(View view){
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        GoogleMap map = mapFragment.getMap(); //update and get the current map

        map.addMarker(new MarkerOptions().position(child));
        LatLng parent_loc;
        Location my_loc = map.getMyLocation();
        if(my_loc == null){
            Log.d("Location", "null");
            parent_loc = new LatLng(25,35);
        }else{
            parent_loc = new LatLng(my_loc.getLatitude(),my_loc.getLongitude()); //parent location
        }
        LatLng[] cone_shape = new LatLng[5];

        LatLng tip_cone = new LatLng(parent_loc.latitude+range*Math.cos(direction),parent_loc.longitude+range*Math.sin(direction)); //this would be the farthest tip of the cone
        LatLng bottom_cone = new LatLng(parent_loc.latitude+range*Math.cos(direction-arc/2), parent_loc.longitude+range*Math.sin(direction-arc/2)); //bottom end of the cone
        LatLng top_cone = new LatLng(parent_loc.latitude+range*Math.cos(direction+arc/2), parent_loc.longitude+range*Math.sin(direction+arc/2)); //top end of the cone
        Polygon cone = map.addPolygon(new PolygonOptions()
                .add(parent_loc)
                .add(bottom_cone) //creates a somewhat cone thingie
                .add(tip_cone)
                .add(top_cone));
        map.moveCamera(CameraUpdateFactory.newLatLng(parent_loc)); //center map on person
    }
    @Override
    public void onConnected(Bundle bundle) {
        my_loc = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
