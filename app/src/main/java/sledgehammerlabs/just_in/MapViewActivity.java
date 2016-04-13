package sledgehammerlabs.just_in;

import java.lang.Object;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapViewActivity extends FragmentActivity implements
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
    //private com.google.android.gms.location.LocationListener mLocationListener;

    private static double userLat, userLong;
    private int filterID, viewDistance;

    public static final int GPS_ERROR_DIALOG_REQUEST = 1960;
    //Zoom level range: 0-19
    private static final float DEFAULT_ZOOM = 16;
    private static final int CONNECTION_FAILED_RESOLUTION_REQUEST = 4441;


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

/*        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null) {
                    Toast.makeText(MapViewActivity.this, "Marker != null", Toast.LENGTH_SHORT).show();
                    Intent pinViewIntent = new Intent(MapViewActivity.this, Test.class);
                    startActivity(pinViewIntent);
                    return true;
                }
                Toast.makeText(MapViewActivity.this, "Marker == null", Toast.LENGTH_SHORT).show();
                return false;
            }
        });*/

        if (CheckServicesStatus()) {
            //DEBUGGING TOAST
            Toast.makeText(this, "CheckServicesStatus returned TRUE", Toast.LENGTH_SHORT).show();
            setContentView(R.layout.activity_map_view);
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
            //If difficulty is encountered using getMapAsync, deprecated function
            //  getMap still works as intended
            mapFragment.getMapAsync(this);
        } else {
            //DEBUGGING TOAST
            Toast.makeText(this, "CheckServicesStatus returned FALSE", Toast.LENGTH_SHORT).show();
            //TODO Add an error screen layout?
        }
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
    }

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
        Location myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        Toast.makeText(this, "Location connected.", Toast.LENGTH_SHORT).show();
        //Center camera here (first location?)

        //Is this if block needed?
//        if (myLocation == null)
//        {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        }
//        else
//        {
//            Toast.makeText(this, "Getting new location.", Toast.LENGTH_SHORT).show();
//        }
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
    }

    //Disable this and define custom one
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "My Location Button clicked.", Toast.LENGTH_SHORT).show();
        return false;
    }

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

    private void GetLocation(Location location)
    {
        final SendToServer sender = new SendToServer();

        Toast.makeText(this, "In GetLocation()", Toast.LENGTH_SHORT).show();
        userLat = location.getLatitude();
        userLong = location.getLongitude();
        LatLng userLatLng = new LatLng(userLat, userLong);
        //Probably shouldn't recenter camera here
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, DEFAULT_ZOOM));

        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    sender.SendOut();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        LatLng pos = new LatLng(userLat, userLong);
        mMap.addMarker(new MarkerOptions().position(pos));
        //Implement code to send user to a new activity when clicked
        //  true returns new behavior
        //  false returns default behavior
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null) {
                    Toast.makeText(MapViewActivity.this, "Marker != null", Toast.LENGTH_SHORT).show();
                    Intent pinViewIntent = new Intent(MapViewActivity.this, Test.class);
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

    private void ConsolidatePins() {
    }

    private void DropPins() {
    }

    //Assign to a button in the activity_map_view
    //  Call GetLocation()
    //  Animate camera to location
    private void OnRecenterButtonPress() {
    }

    private void OnPinMarkerPress() {
    }

    //Checks if the user has Google Play services installed,
    //  If not then the Google API error dialog prompts the user with the actions
    //      needed to install or update
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
}
