package com.example.yiska.project_part2.model.datasource;

import android.text.TextUtils;
import android.util.Patterns;

import com.example.yiska.project_part2.model.entities.Driver;
import com.example.yiska.project_part2.model.entities.Trip;

import java.util.ArrayList;
import java.util.List;

public class DatabaseList {//implements Backend {
    static public int DriverCounter=0;
    ArrayList<Driver> drivers = new ArrayList<Driver>();
    ArrayList<Trip> trips = new ArrayList<Trip>();
   /* public  void addDriver(Driver driver) throws Exception{
        if (!isValidEmail(driver.getEMail()))
            throw new Exception("your Email is invalid!");
        if (!isValidMobile(driver.getPhoneNumber()))
            throw new Exception("your phone number is invalid!");
        driver.setId(DriverCounter++);
        for (Driver d: drivers) {
            if (driver.equals(d))
                throw new Exception("this driver is already exists!");
        }
        drivers.add(driver);
    }*/

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    private boolean isValidMobile(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }


    public List<Trip> getAvailableTrips () throws Exception{


        return trips;
    }


    public List<Trip> getTrips(){
        return trips;
    }


    public List<Trip> getEndedTrips() throws Exception{ return trips;}


    public List<Trip> getTripsOfDriver(Driver driver) throws Exception{ return trips;}


    public List<Trip> getTripsByCity(Driver driver) throws Exception {
        return null;
    }


    public List<Trip> getAvailableTripsByCity(String city) throws Exception{ return trips;}


    public List<Trip> getAvailableTripsByDis(String distance) throws Exception{ return trips;}


    public List<Driver> getDriversEndedTrips() {
        return null;
    }


    public ArrayList<Trip> getTripsByDate(ArrayList<Trip> trips, String hour) throws Exception {
        return null;
    }


    public ArrayList<Trip> getTripsByPayment(ArrayList<Trip> trips) throws Exception{
        return trips;}
}
