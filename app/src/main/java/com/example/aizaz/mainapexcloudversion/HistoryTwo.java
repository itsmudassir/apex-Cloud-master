package com.example.aizaz.mainapexcloudversion;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.eazegraph.lib.charts.BarChart;
import org.eazegraph.lib.charts.StackedBarChart;
import org.eazegraph.lib.models.BarModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by mudassir on 3/10/2016.
 */
public class HistoryTwo extends AppCompatActivity {

    List<Tour> payloadSend = new ArrayList<Tour>();
  //
    // public AsyncDbR aDB;
    Tour tour;
    ToursDataSource datasource;
    Context mContext;

    private TextView mHistoryText;
    final String tempDate = "2652016";
    List<Tour> toursFilter;
    List<Tour> toursSend;
    float walk=0;
    float run=0;
    float upstairs=0;
    float downstairs=0;
    float idle = 0;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    StackedBarChart mStackedBarChart;
    private GoogleApiClient client;
    Button DateP;
    Button btn;
    int year_x,month_x,day_x;
    static final int DIALOG_ID=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button b1, b2;
        Button Home, History, Cloud, Maps, Plot, CloudSupport;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cloud);
        b1 = (Button) findViewById(R.id.btnPost);
        b2 = (Button) findViewById(R.id.btnRefresh);
        Plot = (Button) findViewById(R.id.btnPlot);

        mHistoryText = (TextView) findViewById(R.id.post_text);
        toursFilter = new ArrayList<Tour>();
        toursSend = new ArrayList<Tour>();

        mContext = this;
        Home = (Button) findViewById(R.id.button);
        History = (Button) findViewById(R.id.button2);
        Cloud = (Button) findViewById(R.id.button3);
        CloudSupport = (Button) findViewById(R.id.button4);

        showDialogOnButtonck();
        final Calendar cal=Calendar.getInstance();
        year_x=cal.get(Calendar.YEAR);
        month_x=cal.get(Calendar.MONTH);
        day_x=cal.get(Calendar.DAY_OF_MONTH);
/////////////////////////////////////////////////////////////////

        tour = new Tour();
        toursSend = new ArrayList<Tour>();

        tour.setread();

        tour = new Tour();

        datasource = new ToursDataSource(mContext);

        datasource.open();


        toursSend = datasource.findAll();
        mHistoryText.setText("Database size = " + toursSend.size());
//HERE QUERY IS MADE

        //////////////////////////////////////////////////////////

        BarChart mBarChart = (BarChart) findViewById(R.id.barchart);


        mBarChart.addBar(new BarModel(2.f,  Color.parseColor("#2ecc71")));
        mBarChart.addBar(new BarModel(0.1f, Color.parseColor("#e74c3c")));
        mBarChart.addBar(new BarModel(1.1f, Color.parseColor("#e6b822")));
        mBarChart.addBar(new BarModel(1.1f, Color.parseColor("#d35400")));
        mBarChart.addBar(new BarModel(3.f,  Color.parseColor("#1b6be0")));


        mBarChart.startAnimation();

      //DrawBars(1,2,1,2,1);
        Home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryTwo.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        History.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryTwo.this, History.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        ;

        CloudSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryTwo.this, PostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);

            }
        });




/*
*
*
*
*
*
* making a string here when u put a date picker in it append its day month year variable to the below string
*
*   string tempDate=day+month+year
*   no space or / in it
*
*
*
*
*
*
* */


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

/*
                for (int i = 0; i < toursSend.size(); i++) {
                    Log.d("SelfDb " + toursSend.get(i).getDate(), " longi " + toursSend.get(i).getLongitude());
                    if (toursSend.get(i).getDate().equals(tempDate)) {
                        toursFilter.add(toursSend.get(i));
                        Log.d("MatchFound", "filter");


                    }

                    // toursSend.get(i).getActLabel();


                }
*/

            }


        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datasource = new ToursDataSource(mContext);

                datasource.open();
                datasource.deleteAll();
                Toast.makeText(mContext, "db deleted", Toast.LENGTH_LONG).show();

            }
        });


        Plot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }


    public void showDialogOnButtonck(){
        DateP= (Button) findViewById(R.id.datey);
        DateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog(DIALOG_ID);
              //  Toast.makeText(HistoryTwo.this,"hello",Toast.LENGTH_LONG).show();
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
            walk=0;
            idle=0;
            upstairs=0;
            downstairs=0;
            idle=0;
            ////////////
            boolean found=false;
          String  Date=""+day_x+""+month_x+""+year_x;
            for (int i = 0; i < toursSend.size(); i++) {
             //   Log.d("SelfDb " + toursSend.get(i).getDate(), " longi " + toursSend.get(i).getLongitude());
                if (toursSend.get(i).getDate().equals(Date)) {
                  //////////////////////

                    if (toursSend.get(i).getActLabel().equals("Walking"))
                    {
                        walk++;
                        //Log.d("MatchFound walk " + toursFilter.get(i).getActLabel() + "-" + walk, "");
                    }
                    else if (toursSend.get(i).getActLabel().equals("Idle"))
                    {

                        idle++;
                     //   Log.d("MatchFound idle " + toursFilter.get(i).getActLabel() + "-" + idle, "");
                    }

                    else  if (toursSend.get(i).getActLabel().equals("Running"))
                    {

                        run++;
                       // Log.d("MatchFound runing " + toursFilter.get(i).getActLabel() + "-" + run, "");
                    }
                    else  if (toursSend.get(i).getActLabel().equals("UpStairs"))
                    {

                        upstairs++;
                        //Log.d("MatchFound upstairs " + toursFilter.get(i).getActLabel() + "-" + upstairs, "");
                    }
                    else  if (toursSend.get(i).getActLabel().equals("DownStairs"))
                    {

                        downstairs++;


                    }


                    //////////////




                    //toursFilter.add(toursSend.get(i));
               //     Log.d("MatchFound", "filter");
                    found=true;
                }
            }


            if(found==true){

                DrawBars(((walk)/100),(run)/100,(upstairs)/100,(downstairs)/100,(idle)/100);

                //Toast.makeText(HistoryTwo.this," test ",Toast.LENGTH_SHORT).show();

            }
            if(found==false){
                Toast.makeText(HistoryTwo.this,"Activity Unavailable ",Toast.LENGTH_SHORT).show();



            }


            //////////////



       //     Toast.makeText(HistoryTwo.this," test ",Toast.LENGTH_SHORT).show();




        }
    };



    @Override
    protected void onDestroy() {
        if (datasource != null) {
            datasource.close();
        }
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HistoryTwo Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.aizaz.mainapexcloudversion/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "HistoryTwo Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.aizaz.mainapexcloudversion/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }







    public void DrawBars(float walk1,float run1,float upstairs1,float downstairs1,float idle1){


        BarChart mBarChart = (BarChart) findViewById(R.id.barchart);
        mBarChart.clearChart();

        mBarChart.addBar(new BarModel(walk1,  Color.parseColor("#2ecc71")));
        mBarChart.addBar(new BarModel(run1, Color.parseColor("#e74c3c")));
        mBarChart.addBar(new BarModel(upstairs1, Color.parseColor("#e6b822")));
        mBarChart.addBar(new BarModel(downstairs1, Color.parseColor("#d35400")));
        mBarChart.addBar(new BarModel(idle1,  Color.parseColor("#1b6be0")));


        mBarChart.startAnimation();





    }

    public class AsyncDbR extends AsyncTask<String, Void, List<Tour>> {


        Context contextD;
        Tour tour;
        ToursDataSource datasource;

        public AsyncDbR(Context con) {
            this.contextD = con;

        }

        @Override
        protected void onPostExecute(List<Tour> aVoid) {
         //   Toast.makeText(contextD, "db read task done", Toast.LENGTH_LONG).show();
            payloadSend = aVoid;

        }

        @Override
        protected List<Tour> doInBackground(String... params) {


            return null;
        }

        @Override
        protected void onCancelled() {
            datasource.close();
            super.onCancelled();
        }
    }


}
