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

    Drone drone;

    public WaypointView(Context context) {
        super(context);

        setOrientation(VERTICAL);

        // TODO: below is likely how I will get to create proper independent views (will need to add view to R.layout.xxx res folder and to layoutInflater.inflate(yyy))
//        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Service.LAYOUT_INFLATER_SERVICE);
//        layoutInflater.inflate(R.layout.demo_list_view, this, true);

        drone = new Drone(context);

        TextView title = new TextView(context);
        title.setText("Waypoint View");
        title.setTextSize(24);
        addView(title);

        Button launchButton = new Button(this.getContext());
        launchButton.setText("Launch Button");
        launchButton.setOnClickListener(v -> {
            drone.takeOff();
            uploadAndStartWaypointMission();
        });

        Button statMissionButton = new Button(this.getContext());
        statMissionButton.setText("Start Mission Button");
        statMissionButton.setOnClickListener(v -> {
            uploadAndStartWaypointMission();
        });

        Button stopButton = new Button(this.getContext());
        stopButton.setText("Stop Button");
        stopButton.setOnClickListener(v -> {
            getWaypointMissionOperator().stopMission(error -> {
                if (error == null) {
                    drone.log(TAG, "Waypoint Mission stopped", getContext());
                } else {
                    drone.log(TAG, "Failed to stop Mission: " + error.getDescription(), getContext());
                }
            });
            drone.forceLanding(); //TODO: can remove above since taken care of by this method
        });

        addView(launchButton);
        addView(statMissionButton);
        addView(stopButton);

    }

    private void uploadAndStartWaypointMission() {
        // define waypoints (lat, lon, alt)
        Waypoint wp1 = new Waypoint(38.562303, -121.421518, 20f);  // altitude in meters
        Waypoint wp2 = new Waypoint(38.562330, -121.421530, 25f);

        // build mission
        WaypointMission.Builder builder = new WaypointMission.Builder()
                .finishedAction(WaypointMissionFinishedAction.AUTO_LAND) // Auto land at the end of mission
                .flightPathMode(WaypointMissionFlightPathMode.NORMAL) // normal refers to going to waypoint in straight lines instead of curves
                .autoFlightSpeed(5.0f) // m/s
                .maxFlightSpeed(10.0f) // m/s
                .gotoFirstWaypointMode(WaypointMissionGotoWaypointMode.SAFELY); // rise to altitude then go to lat and lon

        builder.addWaypoint(wp1);
        builder.addWaypoint(wp2);

        WaypointMission mission = builder.build();

        // load mission
        getWaypointMissionOperator().loadMission(mission);

        // upload mission
        getWaypointMissionOperator().uploadMission((DJIError uploadError) -> {
            if (uploadError == null) {
                drone.log(TAG, "Mission uploaded successfully!", getContext());

                // start mission
                getWaypointMissionOperator().startMission((DJIError startError) -> {
                    if (startError == null) {
                        drone.log(TAG, "Mission started!", getContext());
                    } else {
                        drone.log(TAG, "Start mission error: " + startError.getDescription(), getContext());
                    }
                });

            } else {
                drone.log(TAG, "Upload failed: " + uploadError.getDescription(), getContext());
            }
        });
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
