package com.eeshana.icstories.activities;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;
import com.eeshana.icstories.CommonUtilities;
import com.eeshana.icstories.R;
import com.eeshana.icstories.WakeLocker;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.ShowCustomToast;
import com.google.android.gcm.GCMRegistrar;

public class SplashActivity extends Activity{
	ConnectionDetector connectionDetector;
	ShowCustomToast showCustomToast;
	// Splash screen timer
	private static int SPLASH_TIME_OUT = 5000;
	String flag="";
	SharedPreferences prefs;
	public static String regId="";
	//	TextView iCTextView,storiesTextView,watsStoryTextView;
	public static Typeface ic_typeface,stories_typeface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Crashlytics.start(this);
		connectionDetector=new ConnectionDetector(SplashActivity.this);
		showCustomToast= new  ShowCustomToast();
		prefs = getSharedPreferences("com.mycompany.myAppName", MODE_PRIVATE);
		ic_typeface=Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
		stories_typeface=Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-lt-com-55-roman.ttf");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.splash_activity);

		//		iCTextView=(TextView) findViewById(R.id.tvIC);
		//		iCTextView.setTypeface(ic_typeface,Typeface.BOLD);
		//		storiesTextView=(TextView) findViewById(R.id.tvStories);
		//		storiesTextView.setTypeface(stories_typeface,Typeface.BOLD);
		//		watsStoryTextView=(TextView) findViewById(R.id.tvWhatsStory);
		//		watsStoryTextView.setTypeface(stories_typeface,Typeface.ITALIC);

	}
	@Override
	protected void onResume() {
		super.onResume();
		if (prefs.getBoolean("firstrun", true)) {
			if(!connectionDetector.isConnectedToInternet()){
				//Toast.makeText(MainActivity.this, "Please connect to the working internet connection", Toast.LENGTH_SHORT).show();
				showCustomToast.showToast(SplashActivity.this, "Please check your internet connection.");
			}else{
				GCMRegistrar.checkDevice(getApplicationContext());
				GCMRegistrar.checkManifest(getApplicationContext());
				registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
				final Context context = this;
				regId=GCMRegistrar.getRegistrationId(context);
				if (regId.equals("")) {
					GCMRegistrar.register(getApplicationContext(), CommonUtilities.SENDER_ID);
				} else {
					if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
					} else {
						GCMRegistrar.register(getApplicationContext(), CommonUtilities.SENDER_ID);
					}
				}
			}
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent();
					intent.setClass(SplashActivity.this, SignInActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(intent);
					finish();
				}
			}, SPLASH_TIME_OUT);
			prefs.edit().putBoolean("firstrun", false).commit();
		}else{
			prefs = getSharedPreferences("PREF",MODE_PRIVATE);
			regId=prefs.getString("regid", "NA");
			flag="in else";															// to handle crash at on destroy
			//System.out.println(" in else "+regId);
			if(regId.equalsIgnoreCase("NA")){
				GCMRegistrar.checkDevice(getApplicationContext());
				GCMRegistrar.checkManifest(getApplicationContext());
				registerReceiver(mHandleMessageReceiver, new IntentFilter(CommonUtilities.DISPLAY_MESSAGE_ACTION));
				final Context context = this;
				regId=GCMRegistrar.getRegistrationId(context);
				if (regId.equals("")) {
					GCMRegistrar.register(getApplicationContext(), CommonUtilities.SENDER_ID);
				} else {
					if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {
					} else {
						GCMRegistrar.register(getApplicationContext(), CommonUtilities.SENDER_ID);
					}
				}
				regId=prefs.getString("regid", "NA");
				//System.out.println("REGID Recovered :: "+regId);
			}
			prefs = getSharedPreferences("DB",0);
			String userId=prefs.getString("userid", "NA");

			if(userId.equalsIgnoreCase("NA")) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent();
						intent.setClass(SplashActivity.this, SignInActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
						startActivity(intent);
						finish();
					}
				}, SPLASH_TIME_OUT);
			}else{
				Intent intent=new Intent(SplashActivity.this,CaptureVideoActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
				overridePendingTransition(0,0);                    // to avoid animation successfully
				finish();
			}
		}
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(connectionDetector.isConnectedToInternet()){
			if(flag.equalsIgnoreCase("in else")){

			}else{
				unregisterReceiver(mHandleMessageReceiver);
				GCMRegistrar.onDestroy(getApplicationContext());
			}
		}else{

		}
	}
	/**
	 * Receiving push messages
	 * */
	private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String regId_from_intentservice=GCMRegistrar.getRegistrationId(getApplicationContext());
			prefs.edit().putString("regid", regId_from_intentservice).commit();

			String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
			// Waking up mobile if it is sleeping
			WakeLocker.acquire(getApplicationContext());

			/**
			 * Take appropriate action on this message
			 * depending upon your app requirement
			 * For now i am just displaying it on the screen
			 * */

			// Showing received message
			//Toast.makeText(getApplicationContext(), "New Message:  " + newMessage, Toast.LENGTH_LONG).show();

			// Releasing wake lock
			WakeLocker.release();
		}
	};
}
