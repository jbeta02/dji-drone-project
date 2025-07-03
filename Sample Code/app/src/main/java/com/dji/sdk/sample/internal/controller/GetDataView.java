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

import dji.common.flightcontroller.FlightControllerState;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKManager;

public class GetDataView extends LinearLayout implements PresentableView {

    private static final String TAG = "DATA";

    Drone drone;
    TextView data_view;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());


    public GetDataView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        drone = new Drone(context);

        TextView title = new TextView(context);
        title.setText("Data View");
        title.setTextSize(24);
        addView(title);

        data_view = new TextView(this.getContext());

        addView(data_view);
        setupFlightControllerCallback();

        Runnable timeUpdater = new Runnable() {
            @Override
            public void run() {
                setupFlightControllerCallback();

                // repeat this cycle after delay
                uiHandler.postDelayed(this, 500);
            }
        };

        // start the time updates
        uiHandler.post(timeUpdater);
    }

    private void setupFlightControllerCallback() {
        BaseProduct product = DJISDKManager.getInstance().getProduct();
        if (product instanceof Aircraft) {
            Aircraft aircraft = (Aircraft) product;
            FlightController flightController = aircraft.getFlightController();

            if (flightController != null) {
                flightController.setStateCallback(new FlightControllerState.Callback() {
                    @Override
                    public void onUpdate(@NonNull FlightControllerState state) {
                        // build data into one big string to display in view
                        final StringBuilder data = new StringBuilder();

                        // gps location values
                        double latitude = state.getAircraftLocation().getLatitude();
                        double longitude = state.getAircraftLocation().getLongitude();
                        float altitude = state.getAircraftLocation().getAltitude();
                        // imu angles
                        double pitch = state.getAttitude().pitch;
                        double roll = state.getAttitude().roll;
                        double yaw = state.getAttitude().yaw;
                        // velocity of drone
                        double velX = state.getVelocityX();
                        double velY = state.getVelocityY();
                        double velZ = state.getVelocityZ();

                        // gps location values
                        data.append("Latitude: ").append(latitude).append("\n");
                        data.append("Longitude: ").append(longitude).append("\n");
                        data.append("Altitude: ").append(altitude).append("\n");
                        // imu angles
                        data.append("Pitch: ").append(pitch).append("\n");
                        data.append("Roll: ").append(roll).append("\n");
                        data.append("Yaw: ").append(yaw).append("\n");
                        // // velocity of drone
                        data.append("Velocity (XYZ): ")
                                .append(velX).append(", ")
                                .append(velY).append(", ")
                                .append(velZ).append("\n");

                        data_view.setText(data.toString());
                    }
                });
            }
        }
    }


    @Override
    public int getDescription() {
        return R.string.component_listview_data_view;
    }

    @NonNull
    @Override
    public String getHint() {
        return this.getClass().getSimpleName() + ".java";
    }

    private WaypointMissionOperator getWaypointMissionOperator() {
        return DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
    }
}
