package com.dji.sdk.sample.internal.controller;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

//TODO: add take off
// TODO: add emergence landing code (stop mission or stop virtual sticks)
public class Drone {
    private static final String TAG = "Drone Class";

    public void takeOff() {
        Aircraft aircraft = (Aircraft) DJISDKManager.getInstance().getProduct();
        if (aircraft != null && aircraft.getFlightController() != null) {

            if (aircraft.getFlightController() != null) {
                aircraft.getFlightController().startTakeoff(djiError -> {
                    if (djiError == null) {
                        // takeoff succeeded, drone is hovering at takeoff altitude.
                        Log.d(TAG, "Takeoff successful, waiting to start mission.");
                        // let drone sit a bit
                        new android.os.Handler().postDelayed(() -> {
                            Log.d(TAG, "Delay before letting anything other drone movements to start");
                        }, 5000);
                    } else {
                        Log.e(TAG, "Takeoff failed: " + djiError.getDescription());
                    }
                });
            }
        }
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
