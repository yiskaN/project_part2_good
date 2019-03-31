package com.example.yiska.project_part2.controller;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.yiska.project_part2.model.backend.BackendFactory;
import com.example.yiska.project_part2.model.datasource.DatabaseFb;
import com.example.yiska.project_part2.model.entities.Trip;

import java.util.List;

public class NotificationService extends Service {
    private int lastCount = 0;
    Context context;
    DatabaseFb dbManager;

    /**
     * This method is called when another component (such as an activity) requests that the service be started
     * @param intent The Intent supplied to Context.startService(Intent).
     * @param flags Additional data about this start request.
     * @param startId A unique integer representing this specific request to start
     * @return value indicates what semantics the system should use for the service's current started state.
     */
    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        dbManager = (DatabaseFb) BackendFactory.getInstance(getApplicationContext());
        context = getApplicationContext();
        dbManager.NotifyToTripList(new DatabaseFb.NotifyDataChange<List<Trip>>() {

            @Override
            public void onDataChanged(List<Trip> obj) {//sending a broadcast
                try {
                    Intent intent = new Intent(context, MyBroadcastReceiver.class);
                    sendBroadcast(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Exception exception) {
            }
        });
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
