package com.example.aizaz.mainapexcloudversion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aizaz.mainapexcloudversion.cloud.PostActivity;
import com.example.aizaz.mainapexcloudversion.db.ToursDataSource;
import com.example.aizaz.mainapexcloudversion.model.Tour;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Aizaz on 3/3/2016.
 */
public class History extends AppCompatActivity {
    static LatLng abc=new LatLng(0,0);
    private ArrayList<LatLng> arrayPoints = null;
    List<Tour> toursSendMaps = new ArrayList<Tour>();
    List<Tour> toursFilter;

    //  public AsyncDbMaps aDB;
    int year_x,month_x,day_x;

    static final int DIALOG_ID=0;
    private GoogleMap googleMap;
    Button Home,History,Cloud,Maps,SetDay,CloudSupport;
    TextView txt;
    GPSTracker gps;
    private Context mContext;
    PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
    double latitude;
    double longitude;
    Tour tour;
    ToursDataSource datasource;
    List<Tour> toursSend;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);

        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);
        Home= (Button) findViewById(R.id.button);
        History= (Button) findViewById(R.id.button2);
        Cloud= (Button) findViewById(R.id.button3);
        Maps= (Button) findViewById(R.id.button4);
        CloudSupport= (Button) findViewById(R.id.buttonx);


        mContext = this;
        showDialogOnButtonck();
        toursFilter = new ArrayList<Tour>();

        gps = new GPSTracker(this);
        // check if GPS enabled




        if(gps.canGetLocation()){

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

            Home.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(com.example.aizaz.mainapexcloudversion.History.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            CloudSupport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(com.example.aizaz.mainapexcloudversion.History.this,PostActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }
            });
            Cloud.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(com.example.aizaz.mainapexcloudversion.History.this,HistoryTwo.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            });
            arrayPoints = new ArrayList<LatLng>();
            Maps.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng newPoint3 = new LatLng(33.6667, 73.1667);//Peshawar


                    Polyline line = googleMap.addPolyline(new PolylineOptions()
                            .addAll(arrayPoints)
                            .width(16)
                            .color(Color.BLUE)
                            .geodesic(true));

                    //  googleMap.moveCamera(point);
// animates camera to coordinates
                    //  googleMap.animateCamera(point,15.0F);

                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint3, 7.0f));
                }
            });
            try {
                // Loading map
                initilizeMap();


            } catch (Exception e) {
                e.printStackTrace();
            }

///////////////////////////////////draw the all db cordinates onCreate

            Tour tour = new Tour();
            toursSend = new ArrayList<Tour>();

            tour.setread();


            tour = new Tour();

            datasource = new ToursDataSource(mContext);

            datasource.open();

       //     Log.d("historytwo", "in bgthread");


            toursSend=  datasource.findAll();
            for(int i=0; i<toursSend.size();i++){
                //Log.d("SelfDb " + toursSend.get(i).getLatitude(), " longi " + toursSend.get(i).getLongitude());

                //  toursSend.get(i).getActLabel();
                LatLng newPoint = new LatLng(toursSend.get(i).getLatitude(), toursSend.get(i).getLongitude());
                arrayPoints.add(newPoint);
            }






            LatLng newPoint3 = new LatLng(33.6667, 73.1667);//Peshawar
            LatLng newPoint1 = new LatLng(33.9805, 71.4272);//fastpwr
            arrayPoints.add(newPoint1);




            Polyline line = googleMap.addPolyline(new PolylineOptions()
                    .addAll(arrayPoints)
                    .width(16)
                    .color(Color.BLUE)
                    .geodesic(true));

            //  googleMap.moveCamera(point);
// animates camera to coordinates
            //  googleMap.animateCamera(point,15.0F);

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint3, 7.0f));

///////////////////////////////////////////////////

        }




    }
    public void showDialogOnButtonck(){
        SetDay= (Button) findViewById(R.id.button6);
        SetDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(DIALOG_ID);
              //  Toast.makeText(History.this,"hello",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id){

        if(id==DIALOG_ID)
            return new DatePickerDialog(this,dpickerListener,year_x,month_x,day_x);

        return null;
    }

    private DatePickerDialog.OnDateSetListener dpickerListener=new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x=year;
            month_x=monthOfYear+1;
            day_x=dayOfMonth;
            ////////////
            boolean found=false;
            String  Date=""+day_x+""+month_x+""+year_x;
            arrayPoints.clear();
            for (int i = 0; i < toursSend.size(); i++) {
                //  Log.d("SelfDb " + toursSend.get(i).getDate(), " longi " + toursSend.get(i).getLongitude());
                if (toursSend.get(i).getDate().equals(Date)) {

                    LatLng newPoint = new LatLng(toursSend.get(i).getLatitude(), toursSend.get(i).getLongitude());
                    arrayPoints.add(newPoint);


                    //  toursFilter.add(toursSend.get(i));
                  //  Log.d("MatchMaps", "filter");
                    found=true;
                }
            }
            if(found==true) {
                LatLng newPoint3 = new LatLng(33.6667, 73.1667);//Peshawar
                LatLng newPoint1 = new LatLng(33.9805, 71.4272);//fastpwr
                arrayPoints.add(newPoint1);


                Polyline line = googleMap.addPolyline(new PolylineOptions()
                        .addAll(arrayPoints)
                        .width(16)
                        .color(Color.BLUE)
                        .geodesic(true));


                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint3, 7.0f));
               // Toast.makeText(History.this,"Test "+arrayPoints.size(),Toast.LENGTH_SHORT).show();

            }
            if(found==false){
                Toast.makeText(History.this,"Location Unavailable ",Toast.LENGTH_SHORT).show();



            }
        }
    };
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
            googleMap.setMyLocationEnabled(true);

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
    @Override
    public void onBackPressed() {
        if (aDB != null) {
            aDB.cancel(true);
        }


        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        if (aDB != null) {
            aDB.cancel(true);
        }
        finish();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }




    List<Tour> payloadSend = new ArrayList<Tour>();

    public AsyncDbCloud aDB;
    ////////////
    public class AsyncDbCloud extends AsyncTask<Tour,Void,List<Tour>>  {




        Context contextD;
        Tour tour;
        ToursDataSource datasource;
        public AsyncDbCloud(Context con){
            this.contextD=con;

        }
        @Override
        protected void onPostExecute(List<Tour> aVoid) {
            //	Toast.makeText(contextD, "db task done", Toast.LENGTH_LONG).show();
            if(aVoid!=null){
            //    Toast.makeText(contextD, "payload here"+aVoid.size(), Toast.LENGTH_LONG).show();
                payloadSend=aVoid;


            }

        }

        @Override
        protected List<Tour> doInBackground(Tour... params) {

            tour = new Tour();

            datasource = new ToursDataSource(contextD);
            datasource.open();


            if(params[0].getread()){
                List<Tour> toursSend = new ArrayList<Tour>();
                toursSend=  datasource.findAll();
                datasource.deleteAll();
                return toursSend;

            }
            if(!params[0].getread()) {

                //	tour = datasource.create(params[0]);
                //Log.d("DBwrite", "Tour created with id " + tour.getId());
                //  sendMessageToUI(1, "thread");
            }
            return null;
        }

        @Override
        protected void onCancelled() {
            if(datasource!=null){
                datasource.close();}
            super.onCancelled();
        }
    }


    //////////







}
