package com.example.yiska.project_part2.controller;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yiska.project_part2.model.backend.Backend;
import com.example.yiska.project_part2.model.backend.BackendFactory;
import com.example.yiska.project_part2.model.entities.Trip;
import com.example.yiska.project_part2.R;
import com.example.yiska.project_part2.model.entities.Driver;
import java.util.List;
import java.util.ArrayList;

import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.SearchView;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Handles the search option
 */
public class SearchFragment extends android.app.Fragment {
    private static final String TAG = "DatabaseFb";
    View view;
    RecyclerView recyclerView;
    static Context context;
    SearchView searchView;
    ExpendableAdapter adapter;
    List<Trip> tripList = new ArrayList<>();
    List<Driver> driverList = new ArrayList<>();
    Backend instance;
    Trip tmpTrip;
    Driver mDriver;


    /**
     * this function calls when the activity is opened
     * @param savedInstanceState contains more information
     */
  /*  @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }
    */


    /**
     * this function calls when the activity is opened, after the function "onCreate" is called
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's UI should be attached to. The fragment
     *                           should not add the view itself, but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this is the parent view that the fragment's UI should be attached to.
     *                           The fragment should not add the view itself, but this can be used to generate the LayoutParams of the view.
     */
    @NonNull
    @Override
    public View onCreateView(LayoutInflater inflater, @NonNull ViewGroup container,
                             Bundle savedInstanceState) {
        mDriver = (Driver) getArguments().getSerializable("myDriver");
        view = inflater.inflate(R.layout.fragment_search, container, false);
        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findViews();
        activeSearchView();
    }


    /**
     * Find the Views in the layout
     */
    private void findViews() {
        long ms = System.currentTimeMillis();
        instance = BackendFactory.getInstance(context);
        recyclerView = (RecyclerView) getView().findViewById(R.id.myRecyclerView);
        String m = "After creating recycle view" + (ms - System.currentTimeMillis());
        Log.i(TAG, m);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        m = "After set layout mangager" + (ms - System.currentTimeMillis());
        Log.i(TAG, m);
        adapter = new ExpendableAdapter(initItems(), new ListItemClickListener() {
            @Override
            public void onItemClicked(ExpendableItem item, int position) {
                //showDialog(item, position);
            }
        });
        m = "After create adapter" + (ms - System.currentTimeMillis());
        Log.i(TAG, m);
        recyclerView.setAdapter(adapter);
        m = "After set adapter" + (ms - System.currentTimeMillis());
        Log.i(TAG, m);
        searchView = (SearchView) getView().findViewById(R.id.simpleSearchView);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    /**
     * The function init the items for the recycle view
     *
     * @return list of the items
     */
    private List<Trip> initItems() {
        driverList = instance.getDrivers();
        tripList = instance.getTrips();
        List<Trip> result = new ArrayList<>(1000);

        for (int i = 0; i < tripList.size(); i++) {
            ExpendableItem item = new ExpendableItem();
            LocationClass locationClass = new LocationClass(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
                // has no permission it will crash if we will try to access it
            }
            item.setDestination(tripList.get(i).getDestination());
            Location driverLocation = locationClass.getMyLocation();
            Location passengerLocation = locationClass.addressToLocation(tripList.get(i).getPlaceBegin());
            float distance = (float) (Math.round(locationClass.calculateDistance(driverLocation, passengerLocation) * 100)) / 100;
            item.setDistance(distance);
            ChildItem child = new ChildItem();
            child.setPlaceBegin(tripList.get(i).getPlaceBegin());
            child.setHourBegin(tripList.get(i).getHourBegin());
            child.setCostumerTel(tripList.get(i).getCostumerTel());
            item.children.add(child);
            result.add(item);
        }
        return result;
    }

    /**
     * The function show notification after the driver picked a ride
     */
    private void showNotification() {

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "My Notifications", NotificationManager.IMPORTANCE_MAX);

            // Configure the notification channel.
            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.CYAN);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder b = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("New Ride")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("You just added a new ride to " + tmpTrip.getDestination())) //expand
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");

        notificationManager.notify(1, b.build());
    }

    /**
     * set listener for the search view
     */
    public void activeSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /**
             * When the text change in the search view
             *
             * @param newText the text the user typed into the search view
             * @return false
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
    }


    /**
     * The class handles the adapter for the view
     */
    private static class ExpendableAdapter extends RecyclerView.Adapter implements Filterable {
        List<Trip> data;
        List<Trip> dataFull;
        ListItemClickListener listener;

        /**
         * ctor
         *
         * @param data     the data to present on recycle view
         * @param listener listener for the button click on the recycle view
         */
        public ExpendableAdapter(List<Trip> data, ListItemClickListener listener) {
            this.data = data;
            this.listener = listener;
            dataFull = new ArrayList<>(this.data);
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == ExpendableAdapter.TYPES.EXPENDABLE.value) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expendable1, parent, false);
                return new ExpendableAdapter.ExpendableViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_child, parent, false);
                return new ExpendableAdapter.ChildViewHolder(view);
            }
        }

        /**
         * bind the view holder - if is expandable takes care of the relevant fields
         * connects between the layout and how its supposed to look
         *
         * @param holder   view to bind
         * @param position where in the view the holder should be bind
         */
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Trip item = data.get(position);
            if (item instanceof ExpendableItem) {
                //set view holder text for the recycle view
                ExpendableAdapter.ExpendableViewHolder exHolder = (ExpendableAdapter.ExpendableViewHolder) holder;
                exHolder.location_txt.setText(item.getDestination());
                exHolder.distance_txt.setText(Float.toString(((ExpendableItem) item).getDistance()) + " km away");

            } else {
                //set child view holder text for the recycle view
                ExpendableAdapter.ChildViewHolder chHolder = (ExpendableAdapter.ChildViewHolder) holder;
                chHolder.origin.setText(item.getPlaceBegin());
                chHolder.phoneNumber.setText(item.getCostumerTel());
                chHolder.time.setText(item.getHourBegin());
            }
        }

        /**
         * returns the size of the recycle view
         *
         * @return size
         */
        @Override
        public int getItemCount() {
            return data.size();
        }

        @Override
        public Filter getFilter() {
            return dataFilter;
        }

        /**
         * implements the data filter for the recycle view
         */
        private Filter dataFilter = new Filter() {
            /**
             * filters the data if needed
             * @param constraint
             * @return
             */
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Trip> filteredList = new ArrayList<>();
                //if there is no constraint or the constraint wa deleted
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(dataFull);
                } else {
                    //filter the list
                    String filterPattern = constraint.toString().toLowerCase().trim(); //make sure the search is not case sensitivity
                    for (Trip t : dataFull) {
                        if (t.getDestination().toLowerCase().contains(filterPattern)
                                || Float.toString(t.getDistance()).contains(filterPattern)) {
                            filteredList.add(t);
                        }

                    }
                }
                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            /**
             * shows to the custumer the wanted items
             * @param constraint
             * @param results
             */
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                data.clear();
                data.addAll((List) results.values);
                notifyDataSetChanged();
            }
        };

        /**
         * returns if the item is expandable or child
         *
         * @param position
         * @return enum TYPES value
         */
        @Override
        public int getItemViewType(int position) {
            return data.get(position) instanceof ExpendableItem ? SearchFragment.ExpendableAdapter.TYPES.EXPENDABLE.value
                    : SearchFragment.ExpendableAdapter.TYPES.CHILD.value;
        }

        //The types of the recycle view
        enum TYPES {
            EXPENDABLE(1), CHILD(2);

            private final int value;

            TYPES(int i) {
                value = i;
            }
        }

        /**
         * Handles the expandable items
         */
        class ExpendableViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            TextView location_txt;
            TextView distance_txt;
            ImageView arrow;
            ImageView plus;

            /**
             * ctor
             *
             * @param itemView
             */
            public ExpendableViewHolder(View itemView) {
                super(itemView);
                location_txt = itemView.findViewById(R.id.parent_location);
                distance_txt = itemView.findViewById(R.id.parent_dist);
                arrow = itemView.findViewById(R.id.expendable_btn);
                arrow.setOnClickListener(this);
                plus = itemView.findViewById(R.id.getRide_btn);
                plus.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                //the button for expend the list view
                if (v == arrow) {
                    SearchFragment.ExpendableItem item = ((SearchFragment.ExpendableItem) data.get(getAdapterPosition()));
                    int numOfChildes = item.children.size();
                    if (item.isOpen) {
                        data.removeAll(item.children);
                        notifyItemRangeRemoved(getAdapterPosition() + 1, numOfChildes);
                        item.isOpen = false;
                    } else {
                        data.addAll(getAdapterPosition() + 1, item.children);
                        notifyItemRangeInserted(getAdapterPosition() + 1, numOfChildes);
                        item.isOpen = true;
                    }
                }
                //the button to get the ride
                if (v == plus) {
                    listener.onItemClicked((ExpendableItem) data.get(getAdapterPosition()), getAdapterPosition());
                }
            }
        }

        /**
         * the additional fields of the trip if the item was opened
         */
        class ChildViewHolder extends RecyclerView.ViewHolder {
            TextView origin;
            TextView time;
            TextView phoneNumber;

            /**
             * ctor
             *
             * @param itemView
             */
            public ChildViewHolder(View itemView) {
                super(itemView);
                origin = itemView.findViewById(R.id.dest_textView);
                time = itemView.findViewById(R.id.time_textView);
                phoneNumber = itemView.findViewById(R.id.phoneNumber_textView);
            }
        }
    }

    /**
     * interface that handles the chosen item
     */
    interface ListItemClickListener {
        void onItemClicked(ExpendableItem item, int position);
    }

    /**
     * Extends trips in the view for more details
     */
    private class ExpendableItem extends Trip {
        public boolean isOpen;//if the item is expand or not
        List<ChildItem> children = new ArrayList<>();
    }

    /**
     * child item class, extends the trips in the view
     */
    private class ChildItem extends Trip {
    }


}
