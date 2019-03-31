package com.example.yiska.project_part2.model.datasource;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.yiska.project_part2.model.backend.Backend;
import com.example.yiska.project_part2.model.entities.Driver;
import com.example.yiska.project_part2.model.entities.Trip;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseFb implements Backend
{

    private static final String TAG = "Firebase_DBManager";
    static ArrayList<Driver> driverList = new ArrayList<>();
    static ArrayList<Trip> tripList = new ArrayList<>();
    private static ChildEventListener driverRefChildEventListener;
    private static ChildEventListener tripRefChildEventListener;
    private static ChildEventListener serviceListener;
    public List<Driver> tmp = new ArrayList<>();
    static Context mContext;
    Boolean isComplete = false;

    static DatabaseReference driverRef;
    static DatabaseReference tripRef;

    static {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        driverRef = database.getReference("drivers");
        tripRef = database.getReference("trips");
    }

    public List<Driver> getDriverList() {
        return driverList;
    }

    public static List<Trip> getTripList() {
        return tripList;
    }

    /**
     * interface NotifyDataChange. For update the list from the firebase.
     * @param <T>
     */
    public interface NotifyDataChange<T>{
        void onDataChanged(T obj);
        void onFailure(Exception exp);
    }

    /**
     * ctor
     * @param context
     */
    public DatabaseFb(Context context) {
        mContext = context;

        NotifyToDriverList(new NotifyDataChange<List<Driver>>() {
            @Override
            public void onDataChanged(List<Driver> obj) {
                Log.d(TAG, "OnDataChanged() called with: obj = [" + obj.size() + "]");
                isComplete = true;

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d(TAG, "onFailure() called with: exception = [" + exception + "]");

            }
        });

        NotifyToTripList(new NotifyDataChange<List<Trip>>() {
            @Override
            public void onDataChanged(List<Trip> obj) {
                Log.d(TAG, "OnDataChanged() called with: obj = [" + obj.size() + "]");

            }

            @Override
            public void onFailure(Exception exception) {
                Log.d(TAG, "onFailure() called with: exception = [" + exception + "]");

            }
        });


    }


    //BACKEND FUNC
    @Override
    public void addDriver(final Driver driver, final Action<String> action) throws Exception {
        driverRef.push().setValue(driver).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(" insert Driver");
                action.onProgress("upload driver data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload driver data", 100);

            }
        });
    }

    @Override
    public ArrayList<Driver> getDrivers() {
        return driverList;
    }


    public ArrayList<Trip> getTrips() {
        return tripList;
    }

    /**
     * The function return all availble trips
     */
    @Override
    public ArrayList<Trip> getAvailableTrips() {
        ArrayList<Trip> availableTrips = new ArrayList<>();
        for (Trip t : tripList) {
            if (t.getStatus().toString().equals("AVAILABLE")) {
                availableTrips.add(t);
            }
        }
        return availableTrips;
    }

    /**
     * The function return all rides by driver name
     */
    @Override
    public ArrayList<String> getDriversNames() {
        ArrayList<String> driversNames = new ArrayList<>();
        for (Driver d : driverList) {
            driversNames.add(d.getFullName());
        }
        return driversNames;
    }

    @Override
    public ArrayList<Trip> getUnhandledTrips() {
        ArrayList<Trip> availableTrips = new ArrayList<>();
        for (Trip t : tripList) {
            if (t.getStatus().equals("AVAILABLE")) {
                availableTrips.add(t);
            }
        }
        return availableTrips;
    }

    @Override
    public ArrayList<Trip> getFinishedTrips() {

        ArrayList<Trip> finishedTrips = new ArrayList<>();
        for (Trip t : tripList) {
            if (t.getStatus().toString().equals("DONE")) {
                finishedTrips.add(t);
            }
        }
        return finishedTrips;
    }

    @Override
    public ArrayList<Trip> getTripsByDriver(String driverName) {
        ArrayList<Trip> tripsByName = new ArrayList<>();
        for(Trip t:tripList)
        {
            if(t.getDriverName().equals(driverName))
            {
                tripsByName.add(t);
            }
        }
        return tripsByName;    }

    @Override
    public ArrayList<Trip> getTripsByCity(String city) {
        ArrayList<Trip> tripsByCity = new ArrayList<>();
        Geocoder gc = new Geocoder(mContext);
        try
        {
            if (gc.isPresent()) {
                for(Trip t: tripList) {
                    //try to convert string of address to location
                    List<Address> addresses = gc.getFromLocationName(t.getDestination(), 1);
                    String cityName = addresses.get(0).getAddressLine(0);
                    if(city.equals(cityName) && t.getStatus().equals("AVAILABLE"))
                    {
                        tripsByCity.add(t);
                    }
                } }
        }
        catch (Exception e)
        {
            Toast.makeText(mContext, "error", Toast.LENGTH_SHORT).show();
        }
        return tripsByCity;
    }




    @Override
    public ArrayList<Trip> getTripsByDistance(float distance) {
        LocationClass lc = new LocationClass(mContext);
        ArrayList<Trip> ridesByDistance = new ArrayList<>();
        float mDistance;
        //driver location
        Location driverLocation = lc.getMyLocation();
        for(Trip t : tripList) {
            Location distLocation = lc.addressToLocation(t.getDestination());
            mDistance = lc.calculateDistance(driverLocation, distLocation);
            if(mDistance <= distance)
            {
                ridesByDistance.add(t);
            }
        }
        return ridesByDistance;    }

    @Override
    public ArrayList<Trip> getTripsByDate(Date date) {
        ArrayList<Trip> tripsByDate = new ArrayList<>();
        for(Trip t : tripList)
        {
            if(t.getTripDate().equals(date.toString()))
            {
                tripsByDate.add(t);
            }
        }
        return tripsByDate;    }

    @Override
    public ArrayList<Trip> getTripsByPayment(float payment) {
        ArrayList<Trip> tripsByPayment = new ArrayList<>();
        String a,b;
        Location dest, origin;
        LocationClass lc = new LocationClass(mContext);
        float distance, mPayment;
        for(Trip t : tripList)
        {
            //calculate only for dine rides
            if(t.getStatus().toString().equals("DONE"))
            {
                a = t.getDestination();
                b = t.getPlaceBegin();
                dest = lc.addressToLocation(a);
                origin = lc.addressToLocation(b);
                distance = lc.calculateDistance(dest , origin);
                mPayment = distance*10;
                if(mPayment <= payment)
                {
                    tripsByPayment.add(t);
                }
            }
        }
        return tripsByPayment;    }

    @Override
    public void updateTrips(String id, String key, String value) {
        try {
            tripRef.child(id).child(key).setValue(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isComplete() {
        return isComplete;
    }


    /**
     * stop the listener from notifying to Firebase
     */
    public static void stopNotifyToRidetripList() {
        if (tripRefChildEventListener != null) {
            tripRef.removeEventListener(tripRefChildEventListener);
            tripRefChildEventListener = null;


        }
    }
    /**
     * stop the listener from notifying to Firebase
     */
    public static void stopNotifyToDriverList() {
        if (driverRefChildEventListener != null) {
            driverRef.removeEventListener(driverRefChildEventListener);
            driverRefChildEventListener = null;
        }
    }
    public static void NotifyToDriverList(final NotifyDataChange<List<Driver>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (driverRefChildEventListener != null) {
                notifyDataChange.onFailure(new Exception("first unNotify student list"));
                return;
            }
            driverList.clear();
            driverRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    String id = dataSnapshot.getKey();
                    driver.setId(id);
                    driverList.add(driver);
                    notifyDataChange.onDataChanged(driverList);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    String id = dataSnapshot.getKey();
                    for (int i = 0; i < driverList.size(); i++) {
                        if (driverList.get(i).getId().equals(id)) {
                            driverList.set(i, driver);
                            break;
                        }
                    }
                    notifyDataChange.onDataChanged(driverList);
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Driver driver = dataSnapshot.getValue(Driver.class);
                    String id = dataSnapshot.getKey();
                    for (int i = 0; i < driverList.size(); i++) {
                        if (driverList.get(i).getId().equals(id)) {
                            driverList.remove(i);
                            break;
                        }
                    }
                    notifyDataChange.onDataChanged(driverList);
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            driverRef.addChildEventListener(driverRefChildEventListener);
        }
    }

    public static void NotifyToTripList(final NotifyDataChange<List<Trip>> notifyDataChange) {
        if (notifyDataChange != null) {
            if (tripRefChildEventListener != null) {
                if (serviceListener != null) {
                    notifyDataChange.onFailure(new Exception("first unNotify student list"));
                    return;
                }
                else {
                    serviceListener = new ChildEventListener() {//create new listener to service- when drive change
                        @Override
                        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                            notifyDataChange.onDataChanged(tripList);//notification
                        }

                        @Override
                        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onChildRemoved(DataSnapshot dataSnapshot) {
                        }

                        @Override
                        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    tripRef.addChildEventListener(serviceListener);//listener to drive list
                    return;
                }
            }
            tripList.clear();
            tripRefChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    trip.setId(id);
                    tripList.add(trip);
                    notifyDataChange.onDataChanged(tripList);
                }
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    trip.setId(id);
                    for (int i = 0; i < tripList.size(); i++) {
                        if (tripList.get(i).getId().equals(id)) {
                            tripList.set(i, trip);
                            break;
                        }
                    }
                    notifyDataChange.onDataChanged(tripList);
                }
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Trip trip = dataSnapshot.getValue(Trip.class);
                    String id = dataSnapshot.getKey();
                    trip.setId(id);
                    for (int i = 0; i < tripList.size(); i++) {
                        if (tripList.get(i).getId().equals(id)) {
                            tripList.set(i, trip);
                            break;
                        }
                    }
                    notifyDataChange.onDataChanged(tripList);
                }
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    notifyDataChange.onFailure(databaseError.toException());
                }
            };
            tripRef.addChildEventListener(tripRefChildEventListener);
        }
    }

    /**
     * The function add ride tofirebase
     * @param trip
     * @param action interface
     * @throws Exception
     */
    public void addTrip(final Trip trip, final Action<String> action) throws Exception
    {
        tripRef.push().setValue(trip).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                action.onSuccess(" insert trip");
                action.onProgress("upload trip data", 100);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                action.onFailure(e);
                action.onProgress("error upload trip data", 100);

            }
        });
    }






}
