package com.eeshana.icstories.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.eeshana.icstories.R;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.GetBitmapTask;
import com.eeshana.icstories.common.NotificationHelper;
import com.eeshana.icstories.common.ShowCustomToast;
import com.eeshana.icstories.webservices.MyMultipartEntity;
import com.eeshana.icstories.webservices.RegistrationWebservice;
import com.eeshana.icstories.webservices.UploadWebservice;
import com.eeshana.icstories.webservices.MyMultipartEntity.ProgressListener;
import com.loopj.android.http.AsyncHttpResponseHandler;

import android.R.id;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UploadVideoActivity extends Activity implements OnClickListener, LocationListener {
	int width,height;
	ImageView logoImageView;
	ProgressDialog mProgressDialog;
	String userid, videoPath, title, description, flag, regId,status;
	Button uploadButton,isAssignmentButton;
	String isAssignment = "NO";
	EditText titleEditText,descriptionEditText,locationEditText;
	SharedPreferences prefs;
	RelativeLayout homeRelativeLayout,wallRelativeLayout,settingsRelativeLayout;
	TextView homeTextView,wallTextView,settingsTextView;
	String my_location="";
	LocationManager locationManager;
	public double LAT,LONG;
	public Location location;
	ProgressDialog pDialog;
	Dialog locationDialog;
	Button okButton,cancelButton;
	private static final int REQUEST_CODE = 0;
	private static final String TAG = null;
	ShowCustomToast showCustomToast;
	Bitmap thumb;
	ConnectionDetector connectionDetector;
	String assignment_id;
	TextView uploadtTextView,dailyAssignTextView;//iCTextView,storiesTextView,watsStoryTextView
	//String pattern="[a-zA-Z0-9.@!_:;&*+=-? ]*";
	String folderPath;
	String extension;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this will copy the license file and the demo video file.
		// to the videokit work folder location.
		// without the license file the library will not work.
		//copyLicenseAndDemoFilesFromAssetsToSDIfNeeded();
		setContentView(R.layout.upload_video_activity);

		height=SignInActivity.getScreenHeight(UploadVideoActivity.this);
		width=SignInActivity.getScreenWidth(UploadVideoActivity.this);
		mProgressDialog=new ProgressDialog(UploadVideoActivity.this);
		pDialog=new ProgressDialog(UploadVideoActivity.this);
		showCustomToast=new ShowCustomToast();
		connectionDetector=new ConnectionDetector(UploadVideoActivity.this);
		//		mProgressDialog=new ProgressDialog(UploadVideoActivity.this,AlertDialog.THEME_HOLO_LIGHT);
		//		mProgressDialog.setFeatureDrawableResource(height,R.drawable.rounded_toast);
		prefs = getSharedPreferences("PREF",MODE_PRIVATE);
		regId=prefs.getString("regid", "NA");
		SharedPreferences pref = getSharedPreferences("DB", 0);
		userid=pref.getString("userid", "1");
		SharedPreferences assign_prefs = getSharedPreferences("ASSIGN", 0);
		assignment_id=assign_prefs.getString("ASSIGN_ID","");
		videoPath=getIntent().getStringExtra("VIDEOPATH");

		//		iCTextView=(TextView) findViewById(R.id.tvIC);
		//		iCTextView.setTypeface(CaptureVideoActivity.ic_typeface,Typeface.BOLD);
		//		storiesTextView=(TextView) findViewById(R.id.tvStories);
		//		storiesTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		//		watsStoryTextView=(TextView) findViewById(R.id.tvWhatsStory);
		//		watsStoryTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.ITALIC);

		//		logoImageView=(ImageView) findViewById(R.id.ivLogo);
		//		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (width/1.9), (int) (height/9.5));
		//		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		//		lp.setMargins(0, 0, 0, 0);
		//		logoImageView.setLayoutParams(lp);

		uploadtTextView=(TextView) findViewById(R.id.tvUpload);
		uploadtTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		//		extension=getIntent().getStringExtra("EXTENSION");
		//		folderPath = Environment.getExternalStorageDirectory().getPath()+"/ICStoriesFolder";
		//		if (!FileUtils.checkIfFolderExists(folderPath)) {
		//			boolean isFolderCreated = FileUtils.createFolder(folderPath);
		//			Log.i(Prefs.TAG, folderPath + " created? " + isFolderCreated);
		//			if (isFolderCreated) {
		//
		//			}
		//		}
		//command for rotating video 90 degree
		//String commandStr="ffmpeg -y -i "+videoPath+" -strict experimental -vf transpose=1 -s 160x120 -r 30 -aspect 4:3 -ab 48000 -ac 2 -ar 22050 -b 2097k "+folderPath+"/out."+extension;

		//Change Video Resolution:
		//String commandStr="ffmpeg -y -i "+videoPath+" -strict experimental -vf transpose=3 -s 320x240 -r 15 -aspect 3:4 -ab 12288 -vcodec mpeg4 -b 2097152 -sample_fmt s16 "+folderPath+"/res."+extension;

		//command for compressing video 
		//		String commandStr="ffmpeg -y -i "+videoPath+" -strict experimental -s 320x240 -r 15 -aspect 3:4 -ab 12288 -vcodec mpeg4 -b 2097152 -sample_fmt s16 "+folderPath+"/test."+extension;
		//		setCommand(commandStr);
		//		runTranscoing();

		thumb = ThumbnailUtils.createVideoThumbnail(videoPath,MediaStore.Images.Thumbnails.MINI_KIND);
		System.out.println("THUMB IMG  in uplaod== "+thumb);

		titleEditText=(EditText) findViewById(R.id.etTitle);
		titleEditText.setTypeface(CaptureVideoActivity.stories_typeface);
		descriptionEditText=(EditText) findViewById(R.id.etDescripton);
		descriptionEditText.setTypeface(CaptureVideoActivity.stories_typeface);
		locationEditText=(EditText) findViewById(R.id.etLocation);
		locationEditText.setTypeface(CaptureVideoActivity.stories_typeface);
		isAssignmentButton=(Button) findViewById(R.id.buttonCheck);
		isAssignmentButton.setTypeface(CaptureVideoActivity.stories_typeface);
		isAssignmentButton.setOnClickListener(this);
		dailyAssignTextView=(TextView) findViewById(R.id.tvDailyAssignment);
		dailyAssignTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		uploadButton=(Button) findViewById(R.id.buttonUpload);
		uploadButton.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout uploadRelativeLayout=(RelativeLayout) findViewById(R.id.rlUpload);
		RelativeLayout.LayoutParams lp9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp9.addRule(RelativeLayout.BELOW, R.id.rlDailyAssignment);
		lp9.setMargins(width/7,30, width/7, 0);
		uploadRelativeLayout.setLayoutParams(lp9);
		uploadButton.setOnClickListener(this);

		//bottom bar

		homeRelativeLayout=(RelativeLayout) findViewById(R.id.rlHome);
		homeRelativeLayout.setBackgroundResource(R.drawable.press_border);
		homeRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		homeTextView=(TextView) homeRelativeLayout.findViewById(R.id.tvHome);
		homeTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		homeTextView.setTextColor(Color.parseColor("#fffffe"));
		RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp4.addRule(RelativeLayout.ALIGN_LEFT);
		homeRelativeLayout.setLayoutParams(lp4);
		homeRelativeLayout.setOnClickListener(this);

		wallRelativeLayout=(RelativeLayout) findViewById(R.id.rlWall);
		wallRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		wallTextView=(TextView) wallRelativeLayout.findViewById(R.id.tvWall);
		wallTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp5.addRule(RelativeLayout.RIGHT_OF,R.id.rlHome);
		wallRelativeLayout.setLayoutParams(lp5);
		wallRelativeLayout.setOnClickListener(this);

		settingsRelativeLayout=(RelativeLayout) findViewById(R.id.rlSettings);
		settingsRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		settingsTextView=(TextView) settingsRelativeLayout.findViewById(R.id.tvSettings);
		settingsTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp6.addRule(RelativeLayout.RIGHT_OF,R.id.rlWall);
		settingsRelativeLayout.setLayoutParams(lp6);
		settingsRelativeLayout.setOnClickListener(this);

		// current location
		locationDialog=new Dialog(UploadVideoActivity.this);
		locationDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		locationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		locationDialog.setContentView(R.layout.location_dialog );
		locationDialog.setCancelable(true);
		LinearLayout dialogLayout=(LinearLayout) locationDialog.findViewById(R.id.ll1);
		okButton = (Button)dialogLayout.findViewById(R.id.OkBtn);
		okButton.setOnClickListener(this);
		cancelButton = (Button)dialogLayout.findViewById(R.id.cancelBtn);
		cancelButton.setOnClickListener(this);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		boolean network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		if(network_enabled){
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
		}else{
			showCustomToast.showToast(UploadVideoActivity.this, "Could not find current location. Please enable location services of your device.");
		}
		//		else if(gps_enabled == false){
		//			locationDialog.show();
		//		}
	}

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if(v.getId()==uploadButton.getId()){
			if(connectionDetector.isConnectedToInternet()){
				title=titleEditText.getText().toString();
				description=descriptionEditText.getText().toString();
				flag=isAssignment;
				my_location=locationEditText.getText().toString();
				if(title.equalsIgnoreCase("") || description.equalsIgnoreCase("")){
					showCustomToast.showToast(UploadVideoActivity.this, "Title and Description both are required");
				}else{
					
					Log.e("location2", my_location);
					
					//new UplodTask(getApplicationContext()).execute();
					if(title.length()<=30 && title.length()>=4 && description.length()>=4 && description.length()<=150 && my_location.length()<=30){//&& my_location.matches(pattern) && title.matches(pattern) && description.matches(pattern)){
						if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
							new UplodTask(getApplicationContext()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
						}else{
							new UplodTask(getApplicationContext()).execute();						}

					}else{
						//						if(!title.matches(pattern)){
						//							titleEditText.setError("Title contains one or more invalid characters. Please check.");
						//						}
						//						if(!description.matches(pattern)){
						//							descriptionEditText.setError("Description contains one or more invalid characters. Please check");
						//						}
						//						if(!my_location.matches(pattern)){
						//							locationEditText.setError("Location contains one or more invalid characters. Please check");
						//						}
						if(title.length()<4){
							titleEditText.setError("Title must be min 4 characters.");
						}
						if(description.length()<4){
							descriptionEditText.setError("Description must be min 4 characters.");
						}
						if(title.length()>30){
							titleEditText.setError("Title must be max 30 characters.");
						}
						if(description.length()>150){
							descriptionEditText.setError("Description must be max 150 characters.");
						}
						if(my_location.length()>30){
							descriptionEditText.setError("Location must be max 30 characters.");
						}
					}
				}
			}else{
				showCustomToast.showToast(UploadVideoActivity.this, "Please check your internet connection.");
			}
		}else if(v.getId()==isAssignmentButton.getId()){
			if(isAssignment.equalsIgnoreCase("NO")){
				isAssignment="YES";
				isAssignmentButton.setText("YES");
			}else{
				isAssignment="NO";
				isAssignmentButton.setText("NO");
			}
		}else if(v.getId()==wallRelativeLayout.getId()){
			Intent i=new Intent(UploadVideoActivity.this,WallActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}else if(v.getId()==settingsRelativeLayout.getId()){
			Intent i=new Intent(UploadVideoActivity.this,SettingsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}else if(v.getId()==okButton.getId()){
			//go to settings page to enable GPS
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, REQUEST_CODE);
			locationDialog.dismiss();
		}else if(v.getId()==cancelButton.getId()){
			locationDialog.dismiss();
		}

	}
	class UplodTask extends AsyncTask<Void, Integer, Void> {
		String jsonResponse;
		ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
		HttpClient httpClient = new DefaultHttpClient();
		private long totalSize;
		//Context mContext;
		private NotificationHelper mNotificationHelper;

		public UplodTask(Context applicationContext) {
			//mContext=applicationContext;
			mNotificationHelper = new NotificationHelper(applicationContext);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			//			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			//			mProgressDialog.setMessage("Uploading Video...");
			//			mProgressDialog.setCancelable(false);
			//			mProgressDialog.show();
			CaptureVideoActivity.alreadyUploading=1;
			uploadButton.setBackgroundColor(Color.parseColor("#c5c5c5"));
			uploadButton.setClickable(false);
			mNotificationHelper.createNotification();
		}

		@Override
		protected Void doInBackground(Void... params) {

			HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/upload_video");
			
			//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/upload_video");
			
			//FileBody filebodyVideo = new FileBody(new File(folderPath+"/test."+extension));
			FileBody filebodyVideo = new FileBody(new File(videoPath));
			MultipartEntity reqEntity = new MyMultipartEntity(new ProgressListener() {
				@Override
				public void transferred(long num){
					publishProgress((int) ((num / (float) totalSize) * 100));  //total size==content (entity's) length
				}
			});
			try {
				reqEntity.addPart("userid",  new StringBody(userid));
				reqEntity.addPart("video", filebodyVideo);
				if(thumb==null){
					reqEntity.addPart("video_thumb", new StringBody(""));
				}
				else{
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					thumb.compress(Bitmap.CompressFormat.JPEG,90, stream); //compress to which format you want.
					byte [] byte_arr = stream.toByteArray();
					reqEntity.addPart("video_thumb", new ByteArrayBody(byte_arr,"thumbnail.png"));	
				}

				reqEntity.addPart("video_title", new StringBody(title, Charset.defaultCharset()));
				reqEntity.addPart("video_description",  new StringBody(description, Charset.defaultCharset()));
				reqEntity.addPart("assignment_flag", new StringBody(flag));
				reqEntity.addPart("assignment_id", new StringBody(assignment_id));
				reqEntity.addPart("location",  new StringBody(my_location, Charset.defaultCharset()));
				reqEntity.addPart("device_token",  new StringBody(regId));
				//After adding everything we get the content's lenght
				
				Log.e("tag", my_location);
				
				totalSize = reqEntity.getContentLength();
				httpPost.setEntity(reqEntity);

				ResponseHandler<String> mHandler = new BasicResponseHandler();
				jsonResponse = httpClient.execute(httpPost,mHandler);
				if(jsonResponse!=null){
					System.out.println("video uploading resp == "+jsonResponse);
					//on fail - jsonResponse  :: ["status"]="0";["statusInfo"] = "fail"; ["msg"] = "Not Uploaded";
					//success -jsonResponse  ::  ["status"]="1";["statusInfo"] = "success"; ["msg"] = "Video Uploaded"
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
					if(status.equalsIgnoreCase("1")){
						//publishProgress(i);
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		protected void onProgressUpdate(Integer... progress) {
			//This method runs on the UI thread, it receives progress updates
			//from the background thread and publishes them to the status bar
			mNotificationHelper.progressUpdate(progress[0]);
		}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			CaptureVideoActivity.alreadyUploading=0;
			mNotificationHelper.completed();
			uploadButton.setBackgroundColor(R.drawable.blue_button_pressed);
			uploadButton.setClickable(true);
			//	mProgressDialog.hide();
			if(status!=null){
				if(status .equalsIgnoreCase("1")){
					showCustomToast.showToast(UploadVideoActivity.this, "Video uploaded successfully.");
					//Toast.makeText(getApplicationContext(), "Video uploaded successfully.", Toast.LENGTH_SHORT).show();
					Intent i=new Intent(UploadVideoActivity.this, CaptureVideoActivity.class);
					startActivity(i);
					UploadVideoActivity.this.finish();
				}else if(status.equalsIgnoreCase("0")){
					showCustomToast.showToast(UploadVideoActivity.this, "Could not upload video. Please try again.");
					//Toast.makeText(getApplicationContext(), "Could not upload video. Please try again.", Toast.LENGTH_SHORT).show();
				}
			}else{
			}
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		if(pDialog.isShowing())
			pDialog.dismiss();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i=new Intent(UploadVideoActivity.this, CaptureVideoActivity.class);
			startActivity(i);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onLocationChanged(Location location2) {
		location = location2; 
		if(location == null){
			LAT = 0.0;
			LONG = 0.0;
		}else{
			LAT = location.getLatitude();
			LONG = location.getLongitude();
			locationManager.removeUpdates(this);
			new ReverseGeocodingTask(this).execute(location);
		}
	}
	// AsyncTask encapsulating the reverse-geocoding API.  Since the geocoder API is blocked,
	// we do not want to invoke it from the UI thread.
	private class ReverseGeocodingTask extends AsyncTask<Location, Void, Void> {

		String _city,_state,_zip;

		public ReverseGeocodingTask(Context context) {
			super();
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}
		@Override
		protected Void doInBackground(Location... params) {	    	

			Geocoder geocoder = new Geocoder(UploadVideoActivity.this, Locale.getDefault());
			try {
				List<Address> addresses = geocoder.getFromLocation(LAT, LONG, 1);
				_city = addresses.get(0).getLocality();
				//_state = addresses.get(0).getAdminArea();
				//_zip = addresses.get(0).getPostalCode();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if(_city!=null){
				locationEditText.setText(_city);
				//showCustomToast.showToast(UploadVideoActivity.this, "Your current location is - "+my_location);
				//Toast.makeText(UploadVideoActivity.this, "Your current location is - "+my_location, Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == REQUEST_CODE && resultCode == 0){
			String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
			if(provider != null){
				//Log.v(TAG, " Location providers: "+provider);
				//Start searching for location and update the location text when update available. 
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
			}else{
				//Users did not switch on the GPS
				//	showCustomToast.showToast(SharePhotoActivity.this, "  ");
				locationDialog.show();
			}
		}
	}
}