package com.example.aizaz.mainapexcloudversion;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.example.aizaz.mainapexcloudversion.cloud.PostActivity;
import com.example.aizaz.mainapexcloudversion.db.ToursDataSource;
import com.example.aizaz.mainapexcloudversion.model.Tour;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class MainActivity extends AppCompatActivity implements ServiceConnection {




    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(TAG, "C:onServiceConnected()");
        mServiceMessenger = new Messenger(service);
//        textStatus.setText("Attached.");
        try {
            Message msg = Message.obtain(null, SensorsService.MSG_REGISTER_CLIENT);
            msg.replyTo = mMessenger;
            Log.d(TAG, "C: TX MSG_REGISTER_CLIENT");
            mServiceMessenger.send(msg);
        } catch (RemoteException e) {
            // In this case the service has crashed before we could even do
            // anything with it
        }

    }

    public void onServiceDisconnected(ComponentName name) {

        Log.d(TAG, "C:onServiceDisconnected()");
        // This is called when the connection with the service has been
        // unexpectedly disconnected - process crashed.
        mServiceMessenger = null;
        textStatus.setText("Disconnected.");
    }

    private enum State {
        IDLE, COLLECTING, TRAINING, CLASSIFYING
    };


    private final String[] mLabels = { Globals.CLASS_LABEL_STANDING,
            Globals.CLASS_LABEL_WALKING, Globals.CLASS_LABEL_RUNNING,Globals.CLASS_LABEL_SITTING,Globals.CLASS_LABEL_LAYING,
            Globals.CLASS_LABEL_IDLE,Globals.CLASS_LABEL_UPSTAIRS,Globals.CLASS_LABEL_DOWNSTAIRS,Globals.CLASS_LABEL_OTHER };
    private TextView  textStatus,textIntValue, textStrValue;

    private RadioGroup radioGroup;
    private final RadioButton[] radioBtns = new RadioButton[4];
    private Intent mServiceIntent;
    private File mFeatureFile;

    private State mState;
    private Button Home,History,Cloud, CloudSupport;
    GPSTracker gps;
boolean gpsNotSetFlag= true;
    int value,countWalk,countRun,countUps,countDowns,countVehicle,countBicycle=0;
    int countIdle=10;
    private Messenger mServiceMessenger = null;
    boolean mIsBound;

    private static final String TAG = "CS65";

    private final Messenger mMessenger = new Messenger(
            new IncomingMessageHandler());
    private Handler mHandler;

    private ServiceConnection mConnection = this;
    int year_x,month_x,day_x;
    Tour tour;
    ToursDataSource datasource;
    List<Tour> toursSendMaps = new ArrayList<Tour>();

    static final int DIALOG_ID=0;
    PieChart mPieChart;
    RoundCornerProgressBar progress1,progress2,progress3,progress4,progress5,progress6,progress7,progress8;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHandler=new Handler();
        gps = new GPSTracker(this);
        contextD = this;

        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);
///////////////////////////////////////////////////////





        ///////////////////////////////////////////////
        mPieChart = (PieChart) findViewById(R.id.piechart);


        setpie();


        mPieChart.startAnimation();


        progress1 = (RoundCornerProgressBar) findViewById(R.id.progress_1);
        progress2 = (RoundCornerProgressBar) findViewById(R.id.progress_2);
        progress3 = (RoundCornerProgressBar) findViewById(R.id.progress_3);
        progress4 = (RoundCornerProgressBar) findViewById(R.id.progress_4);

        progress7 = (RoundCornerProgressBar) findViewById(R.id.progress_7);
        progress1.setProgressColor(Color.parseColor("#2ecc71"));
        progress1.getProgressColor();
        progress2.setProgressColor(Color.parseColor("#e74c3c"));
        progress2.getProgressColor();
        progress3.setProgressColor(Color.parseColor("#e6b822"));
        progress3.getProgressColor();
        progress4.setProgressColor(Color.parseColor("#d35400"));
        progress4.getProgressColor();

        progress7.setProgressColor(Color.parseColor("#1b6be0"));
        progress7.getProgressColor();








        progress1.setMax(150);
        progress1.getMax();
        //progress1.setProgress(120);
        //progress1.getProgress();
        progress2.setMax(150);
        progress2.getMax();
        progress3.setMax(150);
        progress3.getMax();
        progress4.setMax(150);
        progress4.getMax();

        progress7.setMax(150);
        progress7.getMax();

        //progress2.setProgress(80);
        //progress2.getProgress();
        //textStatus = (TextView) findViewById(R.id.t1);
        textIntValue = (TextView) findViewById(R.id.t2);
        textStrValue = (TextView) findViewById(R.id.t3);
        Home= (Button) findViewById(R.id.button);
        History= (Button) findViewById(R.id.button2);
        //SetDay= (Button) findViewById(R.id.button7);
        Cloud= (Button) findViewById(R.id.button3);
        CloudSupport= (Button) findViewById(R.id.button4);

        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
       History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(MainActivity.this,History.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });
        Cloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,HistoryTwo.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        CloudSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this,PostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                doUnbindService();

                stopService(mServiceIntent);

                finish();
            }
        });


        mFeatureFile = new File(getExternalFilesDir(null),
                Globals.FEATURE_FILE_NAME);
        mState = State.IDLE;
        mServiceIntent = new Intent(this, SensorsService.class);
        startService(mServiceIntent);
        mIsBound = false; // by default set this to unbound

        Log.d(TAG, "C:Bind");
        //doBindService();



        //doBindService();
        if(gps.canGetLocation()){
            automaticBind();

            //  Toast.makeText(getApplicationContext(), "MainonCreate", Toast.LENGTH_LONG).show();
        }else{

            gps.showSettingsAlert();

        }



        //   datasource.deleteAll();





    }
    int pieWalk=0;
    int  pieRun=0;
    int  pieUpstairs=0;
    int pieDownstairs=0;
    int pieVehicle=0;
    int pieBicycle=0;
    int pieIdle=0;



    public void setpie(){


        mPieChart.addPieSlice(new PieModel("Walking", pieWalk, Color.parseColor("#2ecc71")));
        mPieChart.addPieSlice(new PieModel("Running", pieRun, Color.parseColor("#e74c3c")));

        mPieChart.addPieSlice(new PieModel("Upstairs", pieUpstairs, Color.parseColor("#e6b822")));
        mPieChart.addPieSlice(new PieModel("Downstairs", pieDownstairs, Color.parseColor("#d35400")));

        mPieChart.addPieSlice(new PieModel("Idle", pieIdle, Color.parseColor("#666666")));
    }

    Context contextD;



String tempDate;

    private void automaticBind() {
        if (SensorsService.isRunning()) {
            Log.d(TAG, "C:MyService.isRunning: doBindService()");
            doBindService();
        }
    }

    private void doBindService() {
        Log.d(TAG, "C:doBindService()");
        bindService(new Intent(this, SensorsService.class), mConnection,
                Context.BIND_AUTO_CREATE);
        mIsBound = true;
        //	textStatus.setText("Binding.");
    }

    /**
     * Un-bind this Activity to TimerService
     */
///////////////////////////////////////

    ////////////////////////////////////////

    private void doUnbindService() {
        Log.d(TAG, "C:doUnBindService()");
        if (mIsBound) {
            // If we have received the service, and hence registered with it,
            // then now is the time to unregister.
            if (mServiceMessenger != null) {
                try {
                    Message msg = Message.obtain(null,
                            SensorsService.MSG_UNREGISTER_CLIENT);
                    Log.d(TAG, "C: TX MSG_UNREGISTER_CLIENT");
                    msg.replyTo = mMessenger;
                    mServiceMessenger.send(msg);
                } catch (RemoteException e) {
                    // There is nothing special we need to do if the service has
                    // crashed.
                }
            }
        }
    }



    @Override
    public void onBackPressed() {

        if (mState == State.TRAINING) {
            return;
        } else if (mState == State.COLLECTING || mState == State.CLASSIFYING) {
            doUnbindService();

            stopService(mServiceIntent);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .cancel(Globals.NOTIFICATION_ID);
        }
        super.onBackPressed();
    }

    public void printt(){


    }
    @Override
    public void onDestroy() {
        // Stop the service and the notification.
        // Need to check whether the mSensorService is null or not.
        if (mState == State.TRAINING) {
            return;
        } else if (mState == State.COLLECTING || mState == State.CLASSIFYING) {
            doUnbindService();

            stopService(mServiceIntent);
            ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                    .cancel(Globals.NOTIFICATION_ID);
        }
        finish();



        super.onDestroy();

        Log.d(TAG, "C:onDestroy()");
        try {
            doUnbindService();
        } catch (Throwable t) {
            Log.e(TAG, "Failed to unbind from the service", t);
        }

    }



    /**
     * Handle incoming messages from TimerService
     */
    private class IncomingMessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {


            Log.d(TAG, "C:IncomingHandler:handleMessage");
            switch (msg.what) {
                case SensorsService.MSG_SET_INT_VALUE:
                    Log.d(TAG, "C: RX MSG_SET_INT_VALUE");
                    textIntValue.setText("" + msg.arg1);
                    value=msg.arg1;





                    /////



                    break;
                case SensorsService.MSG_SET_STRING_VALUE:
                    String str1 = msg.getData().getString("str1");
                    uiUpdate(str1);
                    textStrValue.setText(str1);
                    break;
                default:
                    super.handleMessage(msg);


            }






        }}


    public void uiUpdate(String v){
        String i="idle";
        String vi="Vehicle";
        String bi="Bicycle";
        String ru="Running";
        String w="walking";
        String us="UpStairs";
        String ds="DownStairs";

        if(v.equals(us)){
            countUps++;

            progress3.setProgress(countUps);
            pieUpstairs++;
        }

        if(v.equals(ds)){
            countDowns++;

            progress4.setProgress(countUps);
            pieDownstairs++;
        }



        if(v.equals(i)){
            countIdle++;
            if(countIdle%5==0){
                progress7.setProgress(countIdle);
                pieIdle++;}
        }





        if(v.equals(bi)){
            countBicycle++;

           // progress6.setProgress(countBicycle);

            pieBicycle++;
        }

        if(v.equals(vi)){
            countVehicle++;
            //progress5.setProgress(countVehicle);
            pieVehicle++;
        }
        if(v.equals(w)){
            countWalk++;
            progress1.setProgress(countWalk);
            pieWalk++;

        }

        if(v.equals(ru)) {
            countRun++;
            progress2.setProgress(countRun);
            pieRun++;

        }


        setpie();



    }

}