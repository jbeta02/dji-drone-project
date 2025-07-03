package com.dji.sdk.sample.internal.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class Drone {
    private static final String TAG = "Drone Class";

    Context context;

    public Drone(Context context) {
        this.context = context;
    }

    public void takeOff() {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        if (aircraft != null && aircraft.getFlightController() != null) {

            if (aircraft.getFlightController() != null) {
                aircraft.getFlightController().startTakeoff(djiError -> {
                    if (djiError == null) {
                        // takeoff succeeded, drone is hovering at takeoff altitude.
                        log(TAG, "Takeoff successful, waiting to start mission.", context);
                        // let drone sit a bit
                        new android.os.Handler().postDelayed(() -> {
                            log(TAG, "Delay before letting anything other drone movements to start", context);
                        }, 5000);
                    } else {
                        log(TAG, "Takeoff failed: " + djiError.getDescription(), context);
                    }
                });
            }
        }
    }

    // stops mission if in a mission then lands drone
    public void forceLanding() { //TODO: add for virtual sticks

        WaypointMissionOperator operator =  DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();

        if (operator.getLoadedMission() != null) { // in a mission so stop
            operator.stopMission(null);
            log(TAG, "mission stopped", context);
        }

        FlightController fc = ((Aircraft) DJISDKManager.getInstance().getProduct()).getFlightController();
        fc.startLanding(null);
        log(TAG, "landing drone", context);
    }


    public void log(String tag, String msg) {
       log(tag, msg, null);
    }


    // if context included, will also produce toast msg
    public void log(String tag, String msg, Context context) {
        Log.d(tag, msg);
        if (context != null) {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
