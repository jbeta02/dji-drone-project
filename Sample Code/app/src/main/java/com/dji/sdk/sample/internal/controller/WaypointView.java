package com.dji.sdk.sample.internal.controller;

import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.dji.sdk.sample.R;
import com.dji.sdk.sample.internal.model.ViewWrapper;
import com.dji.sdk.sample.internal.view.PresentableView;

import dji.common.error.DJIError;
import dji.common.mission.waypoint.Waypoint;
import dji.common.mission.waypoint.WaypointMission;
import dji.common.mission.waypoint.WaypointMissionFinishedAction;
import dji.common.mission.waypoint.WaypointMissionFlightPathMode;
import dji.common.mission.waypoint.WaypointMissionGotoWaypointMode;
import dji.sdk.mission.waypoint.WaypointMissionOperator;
import dji.sdk.sdkmanager.DJISDKManager;


public class WaypointView extends LinearLayout implements PresentableView {

    private static final String TAG = "WAYPOINT";

//    Drone drone;


    public WaypointView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        // TODO: below is likely how I will get to create proper independent views (will need to add view to R.layout.xxx res folder and to layoutInflater.inflate(yyy))
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
//        layoutInflater.inflate(R.layout.demo_list_view, this, true);

//        drone = new Drone();

        TextView title = new TextView(context);
        title.setText("Waypoint View");
        title.setTextSize(24);
        addView(title);

//        Button launchButton = new Button(this.getContext());
//        launchButton.setText("launchButton");
//        launchButton.setOnClickListener(v -> {
//            drone.takeOff();
//            uploadAndStartWaypointMission();
//        });
//
//        Button stopButton = new Button(this.getContext());
//        stopButton.setText("stopButton");
//        stopButton.setOnClickListener(v -> {
//            WaypointMissionOperator operator = DJISDKManager.getInstance().getMissionControl().getWaypointMissionOperator();
//            operator.stopMission(error -> {
//                if (error == null) {
//                    Toast.makeText(context, "Waypoint Mission Stopped", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(context, "Stop Failed: " + error.getDescription(), Toast.LENGTH_SHORT).show();
//                }
//            });
//        });
//
//        addView(launchButton);
//        addView(stopButton);

    }

    private void uploadAndStartWaypointMission() {
//        // define waypoints (lat, lon, alt)
//        // TODO: fly done and find good points before running !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//        Waypoint wp1 = new Waypoint(34.0219, -118.4814, 20f);  // altitude in meters
//        Waypoint wp2 = new Waypoint(34.0220, -118.4810, 25f);
//        Waypoint wp3 = new Waypoint(34.0221, -118.4806, 20f);           //TODO:  change waypoint values !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//
//        // build mission
//        WaypointMission.Builder builder = new WaypointMission.Builder()
//                .finishedAction(WaypointMissionFinishedAction.AUTO_LAND) // Auto land at the end of mission
//                .flightPathMode(WaypointMissionFlightPathMode.NORMAL) // normal refers to going to waypoint in straight lines instead of curves
//                .autoFlightSpeed(5.0f) // m/s
//                .maxFlightSpeed(10.0f) // m/s
//                .gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY); // rise to altitude then go to lat and lon
//
//        builder.addWaypoint(wp1);
//        builder.addWaypoint(wp2);
//        builder.addWaypoint(wp3);
//
//        WaypointMission mission = builder.build();
//
//        // load mission
//        getWaypointMissionOperator().loadMission(mission);
//
//        // upload mission
//        getWaypointMissionOperator().uploadMission((DJIError uploadError) -> {
//            if (uploadError == null) {
//                String msg = "Mission uploaded successfully!";
//                Log.d(TAG, msg);
//                Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
//
//
//                // start mission
//                getWaypointMissionOperator().startMission((DJIError startError) -> {
//                    if (startError == null) {
//                        String msgInner = "Mission started!";
//                        Log.d(TAG, msgInner);
//                        Toast.makeText(this.getContext(), msgInner, Toast.LENGTH_SHORT).show();
//                    } else {
//                        String msgInner = "Start mission error: ";
//                        Log.d(TAG, msgInner);
//                        Toast.makeText(this.getContext(), msgInner, Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//            } else {
//                String msg = "Upload failed: " + uploadError.getDescription();
//                Log.d(TAG, msg);
//                Toast.makeText(this.getContext(), msg, Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    @Override
    public int getDescription() {
        return R.string.component_listview_waypoint_view;
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
