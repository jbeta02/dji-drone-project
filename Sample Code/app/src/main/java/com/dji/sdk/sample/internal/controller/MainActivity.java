package com.dji.sdk.sample.internal.controller;

import android.os.Bundle;

import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.dji.sdk.sample.R;

import dji.common.error.DJIError;
import dji.common.error.DJISDKError;
import dji.common.flightcontroller.FlightControllerState;
import dji.sdk.base.BaseComponent;
import dji.sdk.base.BaseProduct;
import dji.sdk.flightcontroller.FlightController;
import dji.sdk.products.Aircraft;
import dji.sdk.sdkmanager.DJISDKInitEvent;
import dji.sdk.sdkmanager.DJISDKManager;

public class MainActivity extends AppCompatActivity {

    TextView data_view;

    //TODO: app builds but doesn't fails at runtime. Problem likely caused by drastic change of MainActivity which is referenced in many other classes.
    //TODO: Solution: revert MainActivity back to original state. Add button to MainActivity that leads to data_display_view as Activity then put this code in that activity

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // main view setup
        setContentView(R.layout.data_display_view);
        LinearLayout displayLayout = findViewById(R.id.display_data_view_id);
        displayLayout.setOrientation(LinearLayout.VERTICAL);
        data_view = new TextView(this);
        displayLayout.addView(data_view);

        // register SDK
        DJISDKManager.getInstance().registerApp(this, new DJISDKManager.SDKManagerCallback() {
            @Override
            public void onRegister(DJIError djiError) {
                if (djiError == DJISDKError.REGISTRATION_SUCCESS) {
                    DJISDKManager.getInstance().startConnectionToProduct();
                } else {
                    updateUI("Registration failed: " + djiError.getDescription());
                }
            }

//            @Override
//            public void onRegister(DJIError djiError) {
//
//            }

            @Override
            public void onProductDisconnect() {
                updateUI("Product disconnected");
            }

            @Override
            public void onProductConnect(BaseProduct baseProduct) {
                updateUI("Product connected");
                setupFlightControllerCallback();
            }

            @Override
            public void onProductChanged(BaseProduct baseProduct) {

            }

            @Override
            public void onComponentChange(BaseProduct.ComponentKey componentKey, BaseComponent baseComponent, BaseComponent baseComponent1) {

            }

            @Override
            public void onInitProcess(DJISDKInitEvent djisdkInitEvent, int i) {

            }

            @Override
            public void onDatabaseDownloadProgress(long l, long l1) {

            }
        });
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

                        updateUI(data.toString());
                    }
                });
            }
        }
    }


    private void updateUI(final String text) {
        runOnUiThread(() -> data_view.setText(text));
    }



    public static class SearchQueryEvent {
        private final String query;

        public SearchQueryEvent(String query) {
            this.query = query;
        }

        public String getQuery() {
            return query;
        }
    }


    // below are functions from original MainActivity that are referenced by other classes
    public static class RequestStartFullScreenEvent {
    }

    public static class RequestEndFullScreenEvent {
    }

    public static class ConnectivityChangeEvent {
    }

}