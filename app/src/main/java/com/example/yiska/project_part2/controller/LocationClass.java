package com.example.yiska.project_part2.controller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import java.util.List;

/**
 * This class handles all the function related to the location
 */
public class LocationClass extends Activity
    {
        double longitude;
        double latitude;
        private Location destLocation;
        private Location originLocation;
        private Location myLocation;
        View view;
        static Context context;
        protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public LocationClass(Context context) {
        this.context = context;
    }

        public Location getDestLocation() {
        return destLocation;
    }

        public Location getMyLocation() {
        getLocation();
        return myLocation;
    }
        public Location getOriginLocation() {
        return originLocation;
    }

        public void setOriginLocation(Location originLocation) {
        this.originLocation = originLocation;
    }

        public void setDestLocation(Location destLocation) {
        this.destLocation = destLocation;
    }

        /**
         * The function sighed to location listener, ask for permission for location,
         * and try to get the phone's location.
         */
        private void getLocation() {
        //location listener
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                if(location != null) {
                    longitude = location.getLongitude();
                    latitude = location.getLatitude();
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        android.location.LocationManager lm = (android.location.LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if(lm != null) {
            try {
                lm.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                myLocation = lm.getLastKnownLocation(android.location.LocationManager.GPS_PROVIDER);
                if (myLocation != null) {
                    longitude = myLocation.getLongitude();
                    latitude = myLocation.getLatitude();
                }
            }
            catch (SecurityException se)
            {

            }
        }
    }


        /**
         * the function check if the given premission is enough to try to get phone's location
         * @param requestCode
         * @param permissions
         * @param grantResults
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 5){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLocation();
            }
        }
    }

        /**
         * the function convert address to location
         * @param my_address - the address string to convert
         * @return
         */
        public Location addressToLocation(String my_address)
        {
            Location tempLocation = null;
            try {
                Geocoder gc = new Geocoder(context);
                if (gc.isPresent()) {
                    List<Address> list = gc.getFromLocationName(my_address, 1);
                    Address address = list.get(0);
                    double lat = address.getLatitude();
                    double lng = address.getLongitude();
                    tempLocation = new Location("A");
                    tempLocation.setLatitude(lat);
                    tempLocation.setLongitude(lng);


                }
            } catch (Exception e)
            {
                Toast.makeText(view.getContext(), "can not convert to location", Toast.LENGTH_LONG).show();

            }
            return tempLocation;
        }

        /**
         * the function calculates distance between 2 given address
         * @param a first address
         * @param b second address
         * @return
         */
        public float calculateDistance(Location a, Location b) {

        if (a == null) return Float.MAX_VALUE;
        float distance = a.distanceTo(b);
        //return in km
        distance = distance/1000;

        return distance;

    }

        /**
         * The function call async task class which check if the device location is on
         * @return true if the location is on
         */
        public  boolean canGetLocation() {
        Boolean result = false;
        try {
            result = new isLocationEnabled().execute(1, 2, 3).get();
        }
        catch (Exception e)
        {

        }
        return result;
    }

        /**
         * async task class'check if the device location is on
         */
        private class isLocationEnabled extends AsyncTask<Integer, Integer, Boolean> {
            protected Boolean doInBackground(Integer...mInt) {
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
                    locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                    return !TextUtils.isEmpty(locationProviders);
                }
            }
            protected void onProgressUpdate(Integer... progress) { }

            protected void onPostExecute(Long result) { }

        }
}
