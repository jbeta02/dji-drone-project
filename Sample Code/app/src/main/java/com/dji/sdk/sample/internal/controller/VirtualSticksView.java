package com.dji.sdk.sample.internal.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.view.PresentableView;


import dji.common.error.DJIError;
import dji.common.flightcontroller.virtualstick.FlightControlData;
import dji.common.flightcontroller.virtualstick.RollPitchControlMode;
import dji.common.flightcontroller.virtualstick.VerticalControlMode;
import dji.common.flightcontroller.virtualstick.YawControlMode;
import dji.common.util.CommonCallbacks;

public class VirtualSticksView extends LinearLayout implements PresentableView {

    private static final String TAG = "VIRTUAL_STICKS";

    Drone drone;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable virtualStickRunnable;

    public VirtualSticksView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        // TODO: below is likely how I will get to create proper independent views (will need to add view to R.layout.xxx res folder and to layoutInflater.inflate(yyy))
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
//        layoutInflater.inflate(R.layout.demo_list_view, this, true);

        drone = new Drone(context);

        TextView title = new TextView(context);
        title.setText("Virutal Sticks View");
        title.setTextSize(24);
        addView(title);

        Button launchButton = new Button(this.getContext());
        launchButton.setText("Launch Drone");
        launchButton.setOnClickListener(v -> {
            drone.takeOff();
        });

        Button statMovingButton = new Button(this.getContext());
        statMovingButton.setText("Start Moving");
        statMovingButton.setOnClickListener(v -> {
            enableVirtualStick();
        });

        Button stopButton = new Button(this.getContext());
        stopButton.setText("Stop Moving");
        stopButton.setOnClickListener(v -> {
            stopSendingVirtualStickData();
//            drone.forceLanding();
        });

        addView(launchButton);
        addView(statMovingButton);
        addView(stopButton);

        drone.flightController.setRollPitchControlMode(RollPitchControlMode.VELOCITY); // can be angle or velocity
        drone.flightController.setYawControlMode(YawControlMode.ANGULAR_VELOCITY); // can be angle or angular velocity
        drone.flightController.setVerticalControlMode(VerticalControlMode.VELOCITY); // can be velocity or position

    }

    private void enableVirtualStick() {
        if (drone.isSetupProperly()) {
            drone.flightController.setVirtualStickModeEnabled(true, new CommonCallbacks.CompletionCallback() {
                @Override
                public void onResult(DJIError error) {
                    if (error == null) {
                        drone.log(TAG, "Virtual Stick Enabled", getContext());
                        startSendingVirtualStickData();
                    } else {
                        drone.log(TAG, "Failed to enable virtual stick: " + error.getDescription(), getContext());
                    }
                }
            });
        }
    }

    private void startSendingVirtualStickData() {
        drone.isSendingVirtualSticks = true;

        virtualStickRunnable = new Runnable() {
            @Override
            public void run() {
                if (!drone.isSendingVirtualSticks || !drone.isSetupProperly()) return; // don't run below

                float pitch = 0.25f; // forward/backward velocity (m/s)
                float roll = 0f; // left/right velocity (m/s)
                float yaw = 0f; // angular velocity (°/s)
                float throttle = 0f; // vertical velocity (m/s)

                FlightControlData controlData = new FlightControlData(pitch, roll, yaw, throttle);

                drone.flightController.sendVirtualStickFlightControlData(controlData, error -> {
                    if (error != null) {
                        drone.log(TAG, "Send Virtual Stick error: " + error.getDescription());
                    }
                });

                // repeat after 50ms (20Hz) (max is around 50Hz according to ChatGPT (20ms))
                handler.postDelayed(this, 50);
            }
        };

        handler.post(virtualStickRunnable);
        handler.postDelayed(new Runnable() { // TODO: remove and try to get drone to stop with stop button
            @Override
            public void run() {
                stopSendingVirtualStickData();
            }
        }, 4000); // stop after given delay
    }


    private void stopSendingVirtualStickData() {
        drone.isSendingVirtualSticks = false;
        FlightControlData controlData = new FlightControlData(0f, 0f, 0f, 0f);
        drone.flightController.sendVirtualStickFlightControlData(controlData,
                djiError -> {
                    if (djiError != null) {
                        drone.log(TAG, "Set all virtual stick values to 0: " + djiError.getDescription());
                    }
                }
        );

        handler.removeCallbacks(virtualStickRunnable);
        virtualStickRunnable = null;

        drone.flightController.setVirtualStickModeEnabled(false, error -> {
            if (error == null) {
                drone.log(TAG, "Virtual stick disabled", getContext());
            } else {
                drone.log(TAG,"Failed to disable virtual stick: " + error.getDescription(), getContext());
            }
        });
    }


    @Override
    public int getDescription() {
        return R.string.component_listview_virtual_sticks_view;
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }

}
