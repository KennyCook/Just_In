/*
 * File Name: MapViewActivity.java
 * Author: Kenneth Cook
 * Description: This class contains logic for the map view. It is responsible
 *      for obtaining the user's location and displaying it on the map, pulling
 *      existing pin data from the server, copying it to the app-side DB,
  *     and displaying the pins on the map.
 *      MapView is the default view when opening Just In
 * Associated Files:
 *      PinView.java
 *      PinCreate.java
 *      AccountSettings.java
 *      activity_map_view.xml
 */

package sledgehammerlabs.just_in;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    PinTable pinTable;

    private static double userLat, userLong;
    private int filterID, viewDistance;

    public static final int GPS_ERROR_DIALOG_REQUEST = 1960,
            CONNECTION_FAILED_RESOLUTION_REQUEST = 4441;
    //Zoom level range: 0-19
    private static final float DEFAULT_ZOOM = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(360 * 1000)    //5 minutes in milliseconds
                .setFastestInterval(60 * 1000);     //TODO Adjust appropriately

        // TODO: delete debugging Toasts
        if (CheckServicesStatus()) {
            Toast.makeText(this, "CheckServicesStatus returned TRUE", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_map_view);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } else {
            Toast.makeText(this, "CheckServicesStatus returned FALSE", Toast.LENGTH_SHORT).show();
            // TODO: Add an error screen layout?
        }

        createDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    // TODO: Delete debugging toasts
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Toast.makeText(this, "Location connected.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(this, "Connection to Google API suspended.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //If the connection failure can be mended by user action then
        //  attempt to have the API prompt the user to do so with
        //  the appropriate action(s)
        if (connectionResult.hasResolution()) {
            try {
                connectionResult.startResolutionForResult(this, CONNECTION_FAILED_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Connection to Google API failed.", Toast.LENGTH_LONG).show();
            //TODO Make an error layout for this fail state
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        //Check to see if the user is moving
        //  Compare the stored lat and long with knew lat long?
        //  Change the frequency of updates?
        Toast.makeText(this, "In onLocationChanged()", Toast.LENGTH_SHORT).show();
        GetLocation(location);
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLatLng, DEFAULT_ZOOM));
    }

    /**
     * Default listener, custom one defined in OnRecenterButtonPress()
     */
    @Override
    public boolean onMyLocationButtonClick() {
        return false;
    }

    /**
     * Default listener, custom one defined when marker is dropped
     */
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mGoogleApiClient.disconnect();
/*        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(false);*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
/*        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);*/
    }

    // TODO: remove debugging toasts
    private void GetLocation(Location location) {

        Toast.makeText(this, "In GetLocation()", Toast.LENGTH_SHORT).show();
        userLat = location.getLatitude();
        userLong = location.getLongitude();

        // TODO: move to method that calls a pin list from DB, delete debugging Toasts
        LatLng pos = new LatLng(userLat, userLong);
        mMap.addMarker(new MarkerOptions().position(pos));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            // Custom marker press method.
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null) {
                    Toast.makeText(MapViewActivity.this, "Marker != null", Toast.LENGTH_SHORT).show();
                    // TODO: replace putExtras value with int pinID from database
                    double[] pinPos = {marker.getPosition().latitude, marker.getPosition().longitude};
                    Intent pinViewIntent = new Intent(MapViewActivity.this, PinView.class).putExtra("pinPos", pinPos);
                    startActivity(pinViewIntent);
                    return true;
                }
                Toast.makeText(MapViewActivity.this, "Marker == null", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }

    private void GetPins() {
    }

    private void DropPins() {
    }

    private void ConsolidatePins() {
    }

    /*************** BUTTON METHODS ***************/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.acct_settings:
                OnAccountSettingsMenuButtonPress();
                return true;
            case R.id.about:
                OnAboutMenuButtonPress();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void OnAccountSettingsMenuButtonPress() {
        Intent intent = new Intent(MapViewActivity.this, AccountSettings.class);
        startActivity(intent);
    }

    public void OnAboutMenuButtonPress() {
    }

    /**
     * Move camera to last known user location
     */
    public void OnRecenterButtonPress(View view) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        LatLng myLatLng = new LatLng(myLocation.getLatitude(), myLocation.getLongitude());
        // TODO: .newLatLngZoom?
        mMap.moveCamera(CameraUpdateFactory.newLatLng(myLatLng));
    }

    /**
     * Takes user to PinCreate screen
     */
    public void OnCreatePinButtonPress(View view) {
        // TODO: call GetLocation()?
        double[] userPos = {userLat, userLong};
        Intent createNewPin = new Intent(MapViewActivity.this, PinCreate.class).putExtra("userPos", userPos);
        startActivity(createNewPin);
    }

    /**
     * Checks if the user has Google Play services installed,
     * If not then the Google API error dialog prompts the user with the actions
     *  needed to install or update
     */
    public boolean CheckServicesStatus() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int isAvailable = googleAPI.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        } else if (googleAPI.isUserResolvableError(isAvailable)) {
            Dialog errorDialog = googleAPI.getErrorDialog(this, isAvailable, GPS_ERROR_DIALOG_REQUEST);
            errorDialog.show();
        } else {
            Toast.makeText(this, "Cannot connect to Google Play Services.", Toast.LENGTH_LONG).show();
        }
        return false;
    }

    //DB DEBUGGING CODE
    public void createDB() {
        pinTable = new PinTable(MapViewActivity.this, "Just_In_DB", null, 1);
        PinModel test = new PinModel(42, 1, 1, 0, 42);
        if( pinTable.deletePin(42) )
        {
            Toast.makeText(this, "Deleted successful", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this, "Delete unsuccessful", Toast.LENGTH_SHORT).show();
        }
        pinTable.addPin(test);
        test = null;

        PinModel test2;
        try
        {
            test2 = pinTable.findPin(42);
            Toast.makeText(this, " " + test2.getScore(), Toast.LENGTH_SHORT).show();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(this, "Exception caught while trying .findPin()", Toast.LENGTH_SHORT).show();
        }
    }
}

// TODO: ensure that location services stops or at least goes into low battery mode when not on MAP, LIST, or HYBRID
