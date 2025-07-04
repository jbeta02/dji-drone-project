package com.dji.sdk.sample.internal.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.util.CommonCallbacks;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.MissionControl;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class Drone {
    private static final String TAG = "Drone Class";

    Context context;
    boolean isSendingVirtualSticks = false;

    Aircraft aircraft;
    FlightController flightController;
    WaypointMissionOperator operator;

    public Drone(Context context) {
        this.context = context;

//        aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
//        flightController = aircraft.getFlightController();
//        operator =  DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
    }

    public void initDrone() {
        aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        flightController = aircraft.getFlightController();
        operator =  DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
    }

    public void takeOff() {
        if (isSetupProperly()) { // prevent take off if not setup properly
            aircraft.getFlightController().startTakeoff(new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError djiError) {
                    if (djiError == null) {
                        // takeoff succeeded, drone is hovering at takeoff altitude.
                        log(TAG, "Takeoff successful", context);
                    } else {
                        log(TAG, "Takeoff failed: " + djiError.getDescription(), context);
                    }
                }
            });

            // old method of launching (does wait for launch completion
//            aircraft.getFlightController().startTakeoff(djiError -> {
//                if (djiError == null) {
//                    // takeoff succeeded, drone is hovering at takeoff altitude.
//                    log(TAG, "Takeoff successful", context);
//                } else {
//                    log(TAG, "Takeoff failed: " + djiError.getDescription(), context);
//                }
//            });
        }
    }

    // stops mission if in a mission then lands drone
    public void forceLanding() { //TODO: add for virtual sticks

        log(TAG, "Landing drone", context);

        // stop mission
        if (operator.getLoadedMission() != null) { // in a mission so stop
            operator.stopMission(null);
            log(TAG, "mission stopped", context);
        }

        // stop virtual stick mode
        if (isSendingVirtualSticks) {
            isSendingVirtualSticks = false;

            flightController.setVirtualStickModeEnabled(false, error -> {
                if (error == null) {
                    log(TAG, "Virtual stick disabled", context);
                } else {
                    log(TAG,"Failed to disable virtual stick: " + error.getDescription(), context);
                }
            });

        }

        // starting landing
        flightController.startLanding(null);
        log(TAG, "landing drone", context);
    }


    // use this to check before starting anything that initiates a movement
    public boolean isSetupProperly() {
        return aircraft != null && aircraft.getFlightController() != null;
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
