import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GPSListener implements LocationListener{
    private static final String TAG = "Location_Debug";

    private LocationManager locationManager;
    String provider;
    Context mContext;

    private double latitude;
    private double longitude;
    private double altitude;

    GPSListener(Context context){
        locationManager=(LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        provider = LocationManager.GPS_PROVIDER;
        mContext = context;
    }

    public void onResume(){
        if(!requestLocation()){requestLocation();}
    }

    public void onPause(){
        LocationManager locationManager=(LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        if(locationManager != null) {
            try{
                locationManager.removeUpdates(this);
            } catch (RuntimeException e){
                Log.i("GPS_Error","Error among GPSListener onPause");
            }
        }
    }

    private boolean requestLocation(){
        if(locationManager.getProvider(provider)!=null){
            try{
                locationManager.requestLocationUpdates(provider,0,0,this);
                return true;
            }
            catch(SecurityException e){
                Log.i(TAG,"GPS request failure");
                return false;
            }
        }
        return false;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        altitude = location.getAltitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        if(status==LocationProvider.OUT_OF_SERVICE) {
            Toast.makeText(mContext,"[GPS]out of service",Toast.LENGTH_SHORT).show();
        } else if(status==LocationProvider.TEMPORARILY_UNAVAILABLE) {
            Toast.makeText(mContext,"[GPS]unavailiable for now",Toast.LENGTH_SHORT).show();
        } else if(status==LocationProvider.AVAILABLE) {
            Toast.makeText(mContext,"[GPS]is availiable!",Toast.LENGTH_SHORT).show();
        }
        requestLocation();
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
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