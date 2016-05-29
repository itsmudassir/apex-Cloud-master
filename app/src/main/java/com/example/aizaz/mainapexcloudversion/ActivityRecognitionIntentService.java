

package com.example.aizaz.mainapexcloudversion;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * Created by Aizaz on 2/16/2016.
 */
public class ActivityRecognitionIntentService extends IntentService {
    //LogCat
    private static final String TAG = ActivityRecognitionIntentService.class.getSimpleName();

    public ActivityRecognitionIntentService() {
        super("ActivityRecognitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            //Extract the result from the Response
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = result.getMostProbableActivity();

            //Get the Confidence and Name of Activity
            int confidence = detectedActivity.getConfidence();
            String mostProbableName = getActivityName(detectedActivity.getType());

/*
            ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

            // Log each activity.
            Log.i(TAG, "activities detected");
            for (DetectedActivity da: detectedActivities) {
                Log.i(TAG, Constants.getActivityString(
                                getApplicationContext(),
                                da.getType()) + " " + da.getConfidence() + "%"
                );
            }
*/
            //Fire the intent with activity name & confidence
            Intent i = new Intent("ImActive");
            i.putExtra("activity", mostProbableName);
            i.putExtra("confidence", confidence);

            Log.d(TAG, "Most Probable Name : " + mostProbableName);
            Log.d(TAG, "Confidence : " + confidence);

            //Send Broadcast to be listen in MainActivity
            this.sendBroadcast(i);

        }else {
            Log.d(TAG, "Intent had no data returned");
        }
    }

    //Get the activity name
    private String getActivityName(int type) {
        switch (type)
        {
            case DetectedActivity.IN_VEHICLE:
                return "In Vehicle";
            case DetectedActivity.ON_BICYCLE:
                return "On Bicycle";
            case DetectedActivity.ON_FOOT:
                return "walking";
            case DetectedActivity.WALKING:
                return "Walking";
            case DetectedActivity.STILL:
                return "idle";

            case DetectedActivity.RUNNING:
                return "Running";
            case DetectedActivity.UNKNOWN:
                return "Unknown";
        }
        return "unknown";
    }
}