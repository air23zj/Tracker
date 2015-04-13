package com.research.childapp;

import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.concurrent.Callable;

public class MainActivity extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location child; //child location
    boolean BLUETOOTH_OUT_RANGE = true; //simple boolean for whether or not we have parent in range
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buildGoogleApiClient();
        createLocationRequest();
        Log.d("Location: ", "Created Location Request");
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
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

    public void updateButton(View view){
        if(BLUETOOTH_OUT_RANGE && mGoogleApiClient.isConnected()){ //continue gps polling even if app isn't focused
            startLocationUpdates();
        }else if(mGoogleApiClient.isConnecting()){
            Log.d("API CLIENT ", "Connecting");
        }else{
            mGoogleApiClient.reconnect();
            Log.d("API CLIENT ", "Reconnecting");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("Location ", "Location connected");
        if(BLUETOOTH_OUT_RANGE){ //start gps polling
            startLocationUpdates();
        }
    }
    protected void startLocationUpdates() {
        Log.d("Location", "Starting Location Services");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnectionSuspended(int i) {

    }

    public void onPause(){
        super.onPause();
        if(BLUETOOTH_OUT_RANGE){ //continue gps polling even if app isn't focused
            startLocationUpdates();
        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d("Location ","Failed to Connect to services?");
    }

    @Override
    public void onLocationChanged(Location location) {
        child = location;
        if(BLUETOOTH_OUT_RANGE){
            sendGPS(child);
        }
        updateUI();
    }

    public void updateUI(){
        TextView field = (TextView) findViewById(R.id.GPS);
        if(field != null) {
            if (child == null) {
                field.setText("No GPS Location available at the moment");
            } else {
                field.setText("Lat: " + child.getLatitude() + " Long: " + child.getLongitude());
            }
            field.append("\nBluetooth Out of Range");

        }
    }

    private void sendGPS(Location child) {
        //method to send GPS Location
    }

    private void sendBlue(double range){
        //method to send out range to parent
    }

}
