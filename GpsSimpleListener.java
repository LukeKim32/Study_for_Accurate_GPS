import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.legday.sundal.helpers.GPSPermissionHelper;

import java.util.List;

import static android.content.Context.LOCATION_SERVICE;
import static com.legday.sundal.helpers.PermissionUtil.checkSelfPermission;


public class GpsSimpleListener {
    private static final String TAG = GpsSimpleListener.class.getSimpleName();

    private final int LOCATION_REQUEST_INTERVAl = 1000;
    private final int LOCATION_REQUEST_FASTEST_INTERVAL = 500;

    String provider;
    Activity mActivity;

    private FusedLocationProviderClient fusedLocationClient;

    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    private double latitude;
    private double longitude;
    private double altitude;

    GpsSimpleListener(Activity activity) {
        provider = LocationManager.GPS_PROVIDER;
        mActivity = activity;

        //Location request 설정
        createLocationRequest();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(mActivity);
        mLocationCallback = setUpLocationCallback();
    }



    public void onResume() {
        getLocationData();
        Log.d(TAG, "Location update resumed .....................");
    }

    public void onPause(){
        //stop location updates
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }


    /** Location data 요청 */
    @SuppressLint("MissingPermission")
    private void getLocationData() {

        if (checkSelfPermission(mActivity,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            GPSPermissionHelper.requestGPSPermission(mActivity);
            return;
        }
            //Get last location
            getLastLocation();
            //request location update
            fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null/*Looper.myLooper()*/);
    }

    /**Location request 설정*/
    private void createLocationRequest() {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(LOCATION_REQUEST_INTERVAl);
        mLocationRequest.setFastestInterval(LOCATION_REQUEST_FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    /**처음 location 요청 전 마지막 location을 먼저 가져온다.*/
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        OnCompleteListener onCompleteListener = new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.isSuccessful() && task.getResult() != null){
                    Location location = task.getResult();
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    altitude = location.getAltitude();
                    Log.i(TAG,"Location last cached : lat = "+latitude+", long = "+longitude+", alt = "+altitude);
                } else {
                    Log.e(TAG,"Location Last listener : onCompleteListener not properly acting");
                }
            }
        };
        fusedLocationClient.getLastLocation().addOnCompleteListener(mActivity, onCompleteListener);
    }


    private LocationCallback setUpLocationCallback() {
        return new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);

                List<Location> locationList = locationResult.getLocations();

                if (locationList.size() > 0) {
                    Location location = locationList.get(locationList.size() - 1);
                    //location = locationList.get(0);

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    altitude = location.getAltitude();
                    Log.i(TAG, "Location updated : lat = " + latitude + ", long = " + longitude + ", alt = " + altitude);

                }

            }

        };
    }

    EcefCoords getCoordsofEcef() {
        return new EcefCoords((float)latitude,(float)longitude,(float)altitude);
    }

    /**
     * Overloading getCoordsOfEcef(), 임의로 위/경/고도를 넣으면 그에 해당하는 ecefcoord 값으로 convert 된 object를 얻을 수 있다.
     */
    EcefCoords getCoordsofEcef(double latitude, double longitude, double altitude) {
        return new EcefCoords(latitude,longitude,altitude);
    }

    public class EcefCoords {
        double x, y, z; // unit 'm'
        double lambda, phi;
        double rho; //rho is radian between vector of our location and ecef x-y

        EcefCoords(double lat, double longt, double alt) {
            double semimajoraxis = 6378137;
            double semiminoraxis = 6356752.3142;
            double flatness = (semimajoraxis - semiminoraxis) / semimajoraxis;
            double eccent_sq = flatness * (2 - flatness);

            lambda = Math.toRadians(lat);
            phi = Math.toRadians(longt);

            double sin_lambda = Math.sin(lambda);
            double cos_lambda = Math.cos(lambda);
            double sin_phi = Math.sin(phi);
            double cos_phi = Math.cos(phi);

            double N = semimajoraxis / Math.sqrt(1 - eccent_sq * sin_lambda * sin_lambda);

            z = (alt + (1 - eccent_sq) * N) * sin_lambda;
            x = (alt + N) * cos_lambda * cos_phi;
            y = (alt + N) * cos_lambda * sin_phi;

            double norm_ecef = Math.sqrt(x * x + y * y + z * z);

            double vector_x = (2 * x) / (semimajoraxis * semimajoraxis);
            double vector_y = (2 * y) / (semimajoraxis * semimajoraxis);
            double vector_z = (2 * z) / (semiminoraxis * semiminoraxis);
            double norm_vector = Math.sqrt(vector_x * vector_x + vector_y * vector_y + vector_z * vector_z);

            rho = Math.acos((x / norm_ecef) * (vector_x / norm_vector) + (y / norm_ecef) * (vector_y / norm_vector) + (z / norm_ecef) * (vector_z / norm_vector)) + lambda;
        }
    }
}