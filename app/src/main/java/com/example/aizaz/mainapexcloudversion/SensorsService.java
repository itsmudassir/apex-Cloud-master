/**
 * LocationService.java
 *
 * Created by Xiaochao Yang on Sep 11, 2011 4:50:19 PM
 *
 */

package com.example.aizaz.mainapexcloudversion;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;


import com.example.aizaz.mainapexcloudversion.db.ToursDataSource;
import com.example.aizaz.mainapexcloudversion.model.Tour;
import com.example.aizaz.mainapexcloudversion.threads.AsyncDb;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.ConverterUtils.DataSource;

public class SensorsService extends Service implements SensorEventListener {

	private static final int mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 8;
	private static final String TAG = "CS65";
	private Context mContext;
	private GoogleApiClient mGApiClient;
	private BroadcastReceiver receiver;
	//private OnSensorChangedTask mAsyncTask;
	//  public AsyncDb aDB;
	private List<Tour> tours;






	private File mFeatureFile;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private int mServiceTaskType;
	private String mLabel;
	private Instances mDataset;
	private Attribute mClassAttribute;
	private OnSensorChangedTask mAsyncTask;
	Classifier cls,cls2;
	Instances instance;

	ObjectInputStream ois;



	//////////////////////////////////////


	private static boolean isRunning = true;

	private List<Messenger> mClients = new ArrayList<Messenger>(); // Keeps
	// track of
	// all
	// current
	// registered
	// clients.
	public static final int MSG_REGISTER_CLIENT = 1;
	public static final int MSG_UNREGISTER_CLIENT = 2;
	public static final int MSG_SET_INT_VALUE = 3;
	public static final int MSG_SET_STRING_VALUE = 4;
	int z=0;
	// Reference to a Handler, which others can use to send messages to it. This
	// allows for the implementation of message-based communication across
	// processes, by creating a Messenger pointing to a Handler in one process,
	// and handing that Messenger to another process.

	private final Messenger mMessenger = new Messenger(
			new IncomingMessageHandler()); // Target we publish for clients to
	// send messages to IncomingHandler.

	public AsyncDb aDB;
	public AsyncDb aDBclassy;
	public AsyncFileReader aDbFile;

	/////////////////////////////////////

	private static ArrayBlockingQueue<Sample> mAccBuffer;
	public static final DecimalFormat mdf = new DecimalFormat("#.##");
	GPSTracker gps;
	@Override
	public void onCreate() {
		super.onCreate();
		///////////////////////////////////
		///file


		cls = new RandomForest();

		// train

		//////////////////////////////////
		mContext = this;

		gps = new GPSTracker(this);
		// check if GPS enabled
		if(gps.canGetLocation()){

			double latitude = gps.getLatitude();
			double longitude = gps.getLongitude();

			// \n is for new line
			//Toast.makeText(getApplicationContext(), "Loc is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
		}else{
			// can't get location
			// GPS or Network is not enabled
			// Ask user to enable GPS/network in settings
			gps.showSettingsAlert();
		}
		//datasource = new ToursDataSource(mContext);
		//datasource.open();
		//////////////////////////////////////////////

		//Check Google Play Service Available





		/////////////////////////////////

		mAccBuffer = new ArrayBlockingQueue<Sample>(
				Globals.ACCELEROMETER_BUFFER_CAPACITY);
		isRunning = true;

	}





	public static boolean isRunning() {
		return isRunning;
	}
	private boolean isPlayServiceAvailable() {
		return GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS;
	}
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		aDbFile=new AsyncFileReader(mContext);
		aDbFile.execute();

		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		mAccelerometer = mSensorManager
				.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

		mSensorManager.registerListener(this, mAccelerometer,
				SensorManager.SENSOR_DELAY_FASTEST);

		//	Bundle extras = intent.getExtras();
		mLabel = "others";

		mFeatureFile = new File(getExternalFilesDir(null), Globals.FEATURE_FILE_NAME);
		Log.d(Globals.TAG, mFeatureFile.getAbsolutePath());

		mServiceTaskType = Globals.SERVICE_TASK_TYPE_COLLECT;

		// Create the container for attributes
		ArrayList<Attribute> allAttr = new ArrayList<Attribute>();

		// Adding FFT coefficient attributes
		DecimalFormat df = new DecimalFormat("0000");

		for (int i = 0; i < Globals.ACCELEROMETER_BLOCK_CAPACITY; i++) {
			allAttr.add(new Attribute(Globals.FEAT_FFT_COEF_LABEL + df.format(i)));
		}
		// Adding the max feature
		allAttr.add(new Attribute(Globals.FEAT_MAX_LABEL));
		allAttr.add(new Attribute(Globals.FEAT_X_MEAN_LABEL));
		allAttr.add(new Attribute(Globals.FEAT_Y_MEAN_LABEL));

		allAttr.add(new Attribute(Globals.FEAT_Z_MEAN_LABEL));
		allAttr.add(new Attribute(Globals.FEAT_X_STD_LABEL));
		allAttr.add(new Attribute(Globals.FEAT_Y_STD_LABEL));

		allAttr.add(new Attribute(Globals.FEAT_Z_STD_LABEL));

		// Declare a nominal attribute along with its candidate values
		ArrayList<String> labelItems = new ArrayList<String>(10);
		labelItems.add(Globals.CLASS_LABEL_STANDING);
		labelItems.add(Globals.CLASS_LABEL_WALKING);
		labelItems.add(Globals.CLASS_LABEL_RUNNING);
		labelItems.add(Globals.CLASS_LABEL_SITTING);
		labelItems.add(Globals.CLASS_LABEL_LAYING);
		labelItems.add(Globals.CLASS_LABEL_IDLE);
		labelItems.add(Globals.CLASS_LABEL_UPSTAIRS);
		labelItems.add(Globals.CLASS_LABEL_DOWNSTAIRS);
		labelItems.add(Globals.CLASS_LABEL_OTHER);
		mClassAttribute = new Attribute(Globals.CLASS_LABEL_KEY, labelItems);
		allAttr.add(mClassAttribute);

		// Construct the dataset with the attributes specified as allAttr and
		// capacity 10000
		mDataset = new Instances(Globals.FEAT_SET_NAME, allAttr, Globals.FEATURE_SET_CAPACITY);

		// Set the last column/attribute (standing/walking/running) as the class
		// index for classification
		mDataset.setClassIndex(mDataset.numAttributes() - 1);

		Intent i = new Intent(this, MainActivity.class);
		// Read:
		// http://developer.android.com/guide/topics/manifest/activity-element.html#lmode
		// IMPORTANT!. no re-create activity
		i.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);



		Notification notification = new Notification.Builder(this)
				.setContentTitle(
						getApplicationContext().getString(
								R.string.ui_sensor_service_notification_title))
				.setContentText(
						getResources()
								.getString(
										R.string.ui_sensor_service_notification_content))
				.setContentIntent(pi).build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification.flags = notification.flags
				| Notification.FLAG_ONGOING_EVENT;
		notificationManager.notify(0, notification);
		mAsyncTask = new OnSensorChangedTask(mContext);
		mAsyncTask.execute();

		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		//aDbFile.cancel(true);
		//aDB.cancel(true);
		//aDBclassy.cancel(true);
		mAsyncTask.cancel(true);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		mSensorManager.unregisterListener(this);
		Log.i("", "");
		isRunning = false;
		super.onDestroy();

	}


	private class OnSensorChangedTask extends AsyncTask<Void, Void, Void> {
		Context contextD;
		public OnSensorChangedTask(Context con){
			this.contextD=con;

		}


		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		public Gesture sampleSignal(Gesture signal) {
			ArrayList<double[]> sampledValues = new ArrayList<double[]>(signal.length());
			Gesture sampledSignal = new Gesture(sampledValues, signal.getLabel());

			double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
			for (int i = 0; i < signal.length(); i++) {
				for (int j = 0; j < 3; j++) {
					if (signal.getValue(i, j) > max) {
						max = signal.getValue(i, j);
					}
					if (signal.getValue(i, j) < min) {
						min = signal.getValue(i, j);
					}
				}
			}
			for (int i = 0; i < signal.length(); ++i) {
				sampledValues.add(new double[3]);
				for (int j = 0; j < 3; ++j) {
					sampledSignal.setValue(i, j, (signal.getValue(i, j) - min) / (max - min));
				}
			}
			return sampledSignal;

		}


		private double computeMean(double values[]){
			double mean = 0.0;
			for(int i=0;i<values.length;i++)
				mean += values[i];
			return mean/values.length;
		}

		private double computeStdDev(double values[],double mean){
			double dev = 0.0;
			double diff = 0.0;
			for(int i=0;i<values.length;i++){
				diff = values[i]-mean;
				dev += diff*diff;
			}
			return Math.sqrt(dev/values.length);
		}
		@Override
		protected Void doInBackground(Void... arg0) {

			Instance inst = new DenseInstance(mFeatLen);
			inst.setDataset(mDataset);
			int blockSize = 0;
			FFT fft = new FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY);
			double[] accBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] xBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] yBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] zBlock = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] x = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] y = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			double[] z = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];

			int count=0;

			double[] re = accBlock;
			double[] im = new double[Globals.ACCELEROMETER_BLOCK_CAPACITY];
			ArrayList<double[]> gestureValues = new ArrayList<double[]>();
			double max = Double.MIN_VALUE;
			double meanX,meanY,meanZ,stdDevX,stdDevY,stdDevZ;

			while (true) {
				try {
					// need to check if the AsyncTask is cancelled or not in the while loop
					if (isCancelled () == true)
					{
						return null;
					}

					// Dumping buffer
					count= blockSize++;
					// Dumping buffer
					xBlock[count] = mAccBuffer.take().getX();
					yBlock[count] = mAccBuffer.take().getY();
					zBlock[count] = mAccBuffer.take().getZ();

					double[] value = {xBlock[count],yBlock[count] ,zBlock[count] };
					gestureValues.add(value);
					if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
						blockSize = 0;
						count=0;
						meanX=0;
						stdDevX=0;
						meanY=0;
						stdDevY=0;
						meanZ=0;
						stdDevZ=0;

						Gesture normSignal = sampleSignal(new Gesture(gestureValues, null));

						//double[] x = normSignal.getValue(1,0);
						for (int p = 0; p < normSignal.length(); ++p) {

							int a = 0;
							x[p] = normSignal.getValue(p, a);
							y[p] = normSignal.getValue(p, a++);
							z[p] = normSignal.getValue(p, a++);
							accBlock[p] = Math.sqrt(x[p] * x[p] + y[p] * y[p] + z[p] * z[p]);
						//	Log.d("Norm Count"," "+p);
						}
					//	Log.d("X64 ", "" + x.length+" MAg"+accBlock.length);
						//double[] y= xyz.get(1);
						//double[] z= xyz.get(2);
						///Log.d("X length = " + xyz.get(0).length+"");

						gestureValues.clear();


						meanX = computeMean(x);
						stdDevX = computeStdDev(x, meanX);
						//Log.d("Mean%%%%%> = " + meanX, " StdvX " + stdDevX + " len" + x.length);

						meanY = computeMean(y);
						stdDevY = computeStdDev(y, meanY);
						//Log.d("Mean%%%%%> = " + meanY, " StdvX " + stdDevY);

						meanZ = computeMean(z);
						stdDevZ = computeStdDev(z, meanZ);
					//	Log.d("Mean%%%%%> = " + meanZ, " StdvX " + stdDevZ);


					//	Log.d("Normalized =", " " + normSignal.getValue(0, 1) + "-- RAW x " + xBlock[1] + "*" + normSignal.length());

					/*	for(int i=0; i<normSignal.length();i++) {
 						}
					*/    // time = System.currentTimeMillis();
						max = .0;
						for (double val : accBlock) {
							if (max < val) {
								max = val;
							}
						}

						fft.fft(re, im);

						for (int i = 0; i < re.length; i++) {
							double mag = Math.sqrt(re[i] * re[i] + im[i]
									* im[i]);
							inst.setValue(i, mag);
							im[i] = .0; // Clear the field
						}

						// Append max after frequency component
						inst.setValue(Globals.ACCELEROMETER_BLOCK_CAPACITY, max);

						inst.setValue(65, meanX);
						inst.setValue(66,meanY);

						inst.setValue(67,meanZ);
						inst.setValue(68,stdDevX);
						inst.setValue(69,stdDevY);
						inst.setValue(70,stdDevZ);
						inst.setValue(mClassAttribute, mLabel);
						mDataset.add(inst);
					//	Log.i("new instance", inst + "");
						Log.d("Classifiy ", " &&&&&&-> " + cls.classifyInstance(inst));
						double v =cls.classifyInstance(inst);

						sendMessageToUI((int)v);
						Tour tour = new Tour();
						Tour tourLog = new Tour();

						tour=createDbBlog((int)v);
					//	Log.i("speakXX ", " " + (int) v);
						datasourceClassy = new ToursDataSource(contextD);
						datasourceClassy.open();
						if(tour!=null){      tourLog = datasourceClassy.create(tour);
							//Log.d("DBwriteClassy", "Tour created with id " + tourLog.getId());
						}
						//z++;
						//	sendToDbClassy(v);
					//	Log.i("speakXX ", " " + (int) v);



					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		ToursDataSource datasourceClassy;



		public Tour createDbBlog(int v){
			Tour tour = new Tour();

			String actLabel = null;
			Log.d("createBlob","1");
			actLabel="";

			if(v==1){
				actLabel= "Walking";}
			else if(v==2){
				actLabel="Running";}


			else if(v==5){
				actLabel="Idle";}
			else if(v==6){
				actLabel="UpStairs";}

			else if(v==7){
				actLabel="DownStairs";}

			else if(v==7){
				actLabel="Unknown";}

			Calendar rightNow = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
			SimpleDateFormat sdfDate = new SimpleDateFormat("ddMyyyy");
			String date = sdfDate.format(new Date());
			String time = sdf.format(rightNow.getTime());
			Log.d("sendtodbClassy","2"+date);

			if(gps.canGetLocation()){
				Log.d("sendtodbClassy","4");

				double latitude = gps.getLatitude();
				double longitude = gps.getLongitude();
				//Toast.makeText(getApplicationContext(), "b4IF"+latitude+"-"+latitude , Toast.LENGTH_LONG).show();
				Log.d("sendtodbTest",""+latitude);

				if(latitude>0.0 && longitude>0.0) {
					Log.d("sendtodbTestIF",""+latitude);

					tour = createDataClassy(date, time, actLabel, latitude, longitude);
					//Toast.makeText(getApplicationContext(), "latlong"+latitude+"-"+latitude , Toast.LENGTH_LONG).show();

				}
				//Toast.makeText(getApplicationContext(), "Your ClassyLoc sendtodb - \nLat: " + latitude + "\nLong: "+actLabel , Toast.LENGTH_LONG).show();
				return tour;
			}else{
				// can't get location
				// GPS or Network is not enabled
				// Ask user to enable GPS/network in settings
				gps.showSettingsAlert();
			}




			return null;
		}

		public Tour createDataClassy(String date,String time,String actLabel,double latitude,double longitude){

			Tour tour = new Tour();
			tour.setDate(date);
			tour.setTime(time);
			tour.setActLabel(actLabel);
			tour.setLatitude(latitude);
			tour.setLongitude(longitude);
			//aDBclassy=new AsyncDb(this);

			//aDBclassy.execute(tour);

			return tour;

		}

		@Override
		protected void onCancelled() {

		//	Log.e("123", mDataset.size() + "");

			if(datasourceClassy!=null){
				datasourceClassy.close();
			}


			if (mServiceTaskType == Globals.SERVICE_TASK_TYPE_CLASSIFY) {
				super.onCancelled();
				return;
			}
			Log.i("in the loop", "still in the loop cancelled");
			String toastDisp;

			if (mFeatureFile.exists()) {

				// merge existing and delete the old dataset
				DataSource source;
				try {
					// Create a datasource from mFeatureFile where
					// mFeatureFile = new File(getExternalFilesDir(null),
					// "features.arff");
					source = new DataSource(new FileInputStream(mFeatureFile));
					// Read the dataset set out of this datasource
					Instances oldDataset = source.getDataSet();
					oldDataset.setClassIndex(mDataset.numAttributes() - 1);
					// Sanity checking if the dataset format matches.
					if (!oldDataset.equalHeaders(mDataset)) {
						// Log.d(Globals.TAG,
						// oldDataset.equalHeadersMsg(mDataset));
						throw new Exception(
								"The two datasets have different headers:\n");
					}

					// Move all items over manually
					for (int i = 0; i < mDataset.size(); i++) {
						oldDataset.add(mDataset.get(i));
					}

					mDataset = oldDataset;
					// Delete the existing old file.
					mFeatureFile.delete();
					Log.i("delete", "delete the file");
				} catch (Exception e) {
					e.printStackTrace();
				}
				toastDisp = getString(R.string.ui_sensor_service_toast_success_file_updated);

			} else {
				toastDisp = getString(R.string.ui_sensor_service_toast_success_file_created)   ;
			}
			Log.i("save", "create saver here");
			// create new Arff file
			ArffSaver saver = new ArffSaver();
			// Set the data source of the file content
			saver.setInstances(mDataset);
			Log.e("1234", mDataset.size() + "");
			try {
				// Set the destination of the file.
				// mFeatureFile = new File(getExternalFilesDir(null),
				// "features.arff");
				saver.setFile(mFeatureFile);
				// Write into the file
				saver.writeBatch();
				Log.i("batch", "write batch here");
				Toast.makeText(getApplicationContext(), toastDisp,
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				toastDisp = getString(R.string.ui_sensor_service_toast_error_file_saving_failed);
				e.printStackTrace();
			}

			//Log.i("toast", "toast here");
			super.onCancelled();
		}

	}

	private double[] gravity = new double[3];
	private double[] linear_acceleration = new double[3];

	public void onSensorChanged(SensorEvent event) {
		final double alpha= 0.8;

		if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

		/*	double m = Math.sqrt(event.values[0] * event.values[0]
					+ event.values[1] * event.values[1] + event.values[2]
					* event.values[2]);  */



			gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
			gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
			gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

			linear_acceleration[0] = event.values[0] - gravity[0];
			linear_acceleration[1] = event.values[1] - gravity[1];
			linear_acceleration[2] = event.values[2] - gravity[2];



			Sample s=new Sample(linear_acceleration[0],linear_acceleration[1],linear_acceleration[2]);
			// Inserts the specified element into this queue if it is possible
			// to do so immediately without violating capacity restrictions,
			// returning true upon success and throwing an IllegalStateException
			// if no space is currently available. When using a
			// capacity-restricted queue, it is generally preferable to use
			// offer.

			try {
				mAccBuffer.add(s);
				//	Log.d("Gesture ###########> "," "+s.getX());
			} catch (IllegalStateException e) {

				// Exception happens when reach the capacity.
				// Doubling the buffer. ListBlockingQueue has no such issue,
				// But generally has worse performance
				ArrayBlockingQueue<Sample> newBuf = new ArrayBlockingQueue<Sample>(
						mAccBuffer.size() * 2);

				mAccBuffer.drainTo(newBuf);
				mAccBuffer = newBuf;
				mAccBuffer.add(s);
				Log.d("Gesture after catch #> ", " " + s.getX());

			}
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "S:onBind() - return mMessenger.getBinder()");

		// getBinder()
		// Return the IBinder that this Messenger is using to communicate with
		// its associated Handler; that is, IncomingMessageHandler().

		return mMessenger.getBinder();	}


	private void sendMessageToUI(int intvaluetosend) {
		Log.d(TAG, "S:sendMessageToUI");
		Iterator<Messenger> messengerIterator = mClients.iterator();
		while (messengerIterator.hasNext()) {
			Messenger messenger = messengerIterator.next();
			try {
				// Send data as an Integer
				Log.d(TAG, "S:TX MSG_SET_INT_VALUE");
				messenger.send(Message.obtain(null, MSG_SET_INT_VALUE,
						intvaluetosend, 0));

				// Send data as a String
				Bundle bundle = new Bundle();
				if(intvaluetosend==0){
					bundle.putString("str1", "Standing");}
				else if(intvaluetosend==1){
					bundle.putString("str1", "walking");}
				else if(intvaluetosend==2){
					bundle.putString("str1", "Running");}
				else if(intvaluetosend==3){
					bundle.putString("str1", "Sitting");}
				else if(intvaluetosend==4){
					bundle.putString("str1", "Laying");}
				else if(intvaluetosend==5){
					bundle.putString("str1", "idle");}
				else if(intvaluetosend==6){
					bundle.putString("str1", "UpStairs");}

				else if(intvaluetosend==7){
					bundle.putString("str1", "DownStairs");}
				else if(intvaluetosend==8){
					bundle.putString("str1", "unknown");}



				else {
					bundle.putString("str1", "ERROR" + "");

				}
				Message msg = Message.obtain(null, MSG_SET_STRING_VALUE);
				msg.setData(bundle);
				Log.d(TAG, "S:TX MSG_SET_STRING_VALUE");
				messenger.send(msg);

			} catch (RemoteException e) {
				// The client is dead. Remove it from the list.
				Log.d("send to ui ", " " + e.getMessage());
				mClients.remove(messenger);
			}
		}
	}

	private class IncomingMessageHandler extends Handler { // Handler of
		// incoming messages
		// from clients.
		@Override
		public void handleMessage(Message msg) {
			Log.d(TAG, "S:handleMessage: " + msg.what);
			switch (msg.what) {
				case MSG_REGISTER_CLIENT:
					Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.add(msg.replyTo) ");
					mClients.add(msg.replyTo);
					break;
				case MSG_UNREGISTER_CLIENT:
					Log.d(TAG, "S: RX MSG_REGISTER_CLIENT:mClients.remove(msg.replyTo) ");
					mClients.remove(msg.replyTo);
					break;
				//	case MSG_SET_INT_VALUE:
				//incrementBy = msg.arg1;
				//	break;
				default:
					super.handleMessage(msg);
			}
		}
	}



	//////////////////////////
	//file reader thread

	public class AsyncFileReader extends AsyncTask<Void,Void,Void>  {




		Context contextD;


		public AsyncFileReader(Context con){
			this.contextD=con;

		}
		@Override
		protected void onPostExecute(Void aVoid) {
			Toast.makeText(contextD, "Model Imported", Toast.LENGTH_LONG).show();

		}

		@Override
		protected Void doInBackground(Void... params) {
// train
			try {

				instance = new Instances(
						new BufferedReader(
								new FileReader("/sdcard/features.arff")));
				instance.setClassIndex(instance.numAttributes() - 1);
				try {
					cls.buildClassifier(instance);
					Log.d("FileTest","Works");
					//Log.d("###############Classifiy "," &&&&&&-> "+cls.classifyInstance(inst));
					// serialize model
					//	ObjectOutputStream oos = new ObjectOutputStream(
					//	new FileOutputStream("/sdcard/try.model"));
					//	oos.writeObject(cls);
					//	oos.flush();
					//	oos.close();



				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}





			return null;
		}

		@Override
		protected void onCancelled() {
			/*try {
				if(oos!=null){
					oos.flush();
					oos.close();}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   */
			super.onCancelled();
		}
	}





	/////////////

}
