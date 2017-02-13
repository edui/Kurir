package id.co.kurindo.kurindo.map;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import java.util.Set;


public class MapUtils {
    private static String distancematrix = "https://maps.googleapis.com/maps/api/distancematrix/json?";
    private static String direction = "https://maps.googleapis.com/maps/api/directions/json?";
    private static String geocode = "https://maps.googleapis.com/maps/api/geocode/json?";


    public static String formatDistance(double distance) {
        String dist = Math.abs(distance) + " M";

        if (distance > 1000.0f) {
            distance = distance / 1000.0f;
            dist = distance + " KM";
        }
        return dist;
    }

    public static String getDistancematrixUrl(LatLng origin, LatLng destination) {
        //key = getResources().getString(R.string.google_maps_key);
        String originsStr = "origins="+origin.latitude+","+origin.longitude;
        String destinationStr = "&destinations="+destination.latitude+","+destination.longitude;

        return distancematrix + originsStr + destinationStr;
    }

    public static String getDirectionUrl(LatLng origin, LatLng destination) {
        String originsStr = "origin="+origin.latitude+","+origin.longitude;
        String destinationStr = "&destination="+destination.latitude+","+destination.longitude;
        return direction + originsStr + destinationStr;
    }
    public static String getDirectionUrl(LatLng origin, LatLng destination, Set<LatLng> waypoints) {
        String originsStr = "origin="+origin.latitude+","+origin.longitude;
        String destinationStr = "&destination="+destination.latitude+","+destination.longitude;
        String waypointStr = "&waypoints=optimize:true";

        for (LatLng point : waypoints){
            waypointStr +="|";
            waypointStr += point.latitude+","+point.longitude;
        }
        return direction + originsStr + destinationStr + waypointStr;
    }
    public static String getGeocodeUrl(LatLng latlng) {
        String latlngStr = "latlng="+latlng.latitude+","+latlng.longitude;
        return geocode + latlngStr;
    }

    private double getRadius(LatLng myLatLng, int inKm) {
        double latDistance = Math.toRadians(myLatLng.latitude - inKm);
        double lngDistance = Math.toRadians(myLatLng.longitude - inKm);
        double a = (Math.sin(latDistance / 2) * Math.sin(latDistance / 2)) +
                (Math.cos(Math.toRadians(myLatLng.latitude))) *
                        (Math.cos(Math.toRadians(inKm))) *
                        (Math.sin(lngDistance / 2)) *
                        (Math.sin(lngDistance / 2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double dist = 6371 / c;
        if (dist < 50) {
                    /* Include your code here to display your records */
        }
        return dist;

    }

    public class LocationConstants {
        public static final int SUCCESS_RESULT = 0;

        public static final int FAILURE_RESULT = 1;

        public static final String PACKAGE_NAME = "com.sample.sishin.maplocation";

        public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";

        public static final String RESULT_DATA_KEY = PACKAGE_NAME + ".RESULT_DATA_KEY";

        public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME + ".LOCATION_DATA_EXTRA";

        public static final String LOCATION_DATA_AREA = PACKAGE_NAME + ".LOCATION_DATA_AREA";
        public static final String LOCATION_DATA_CITY = PACKAGE_NAME + ".LOCATION_DATA_CITY";
        public static final String LOCATION_DATA_STREET = PACKAGE_NAME + ".LOCATION_DATA_STREET";


    }


    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

}
