package com.eeshana.icstories;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.eeshana.icstories.activities.CaptureVideoActivity;
import com.eeshana.icstories.activities.SignInActivity;
import com.eeshana.icstories.activities.SplashActivity;
import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

public class GCMIntentService extends GCMBaseIntentService {
	SharedPreferences prefs;
	private static final String TAG = "GCMIntentService";
	String regId;

	public GCMIntentService() {
		super(CommonUtilities.SENDER_ID);
	}
	@Override
	protected void onError(Context context, String errorId) {
		//Log.i(TAG, "Received error: " + errorId);
		CommonUtilities.displayMessage(context, "GCM error occured");
	}
	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		//Log.i(TAG, "Received recoverable error: " + errorId);
		CommonUtilities.displayMessage(context, "GCM recoverable error occured");
		return super.onRecoverableError(context, errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		String newMessage = intent.getExtras().getString(CommonUtilities.EXTRA_MESSAGE);
		CommonUtilities.displayMessage(context, newMessage);
		generateNotification(context, newMessage);
	}
	/**
	 * Method called on device registered
	 **/
	@Override
	protected void onRegistered(Context context, String registrationId){
		//Log.i(TAG, "Device registered: regId = " + registrationId);
		regId=registrationId;
		prefs = getSharedPreferences("PREF",MODE_PRIVATE);
		prefs.edit().putString("regid",regId).commit();
		CommonUtilities.displayMessage(context,"Device Registered");

		//new mAlreadyRegisterTask().execute();
	}
	//	class mAlreadyRegisterTask extends AsyncTask<Void, Void, Void> {
	//		@Override
	//		protected Void doInBackground(Void... params) {
	//			SendCredentialsWebservice_for_GCM sendCredentialsWebservice=new SendCredentialsWebservice_for_GCM();
	//			String resp = null;
	//			try {
	//				resp = sendCredentialsWebservice.sendData(MainActivity.SENDER_ID, MainActivity.appId, regId);
	//			} catch (JSONException e) {
	//				// TODO Auto-generated catch block
	//				e.printStackTrace();
	//			}
	//			if(resp!=null)
	//				System.out.println(resp);
	//			return null;
	//		}
	//
	//		@Override
	//		protected void onPostExecute(Void result) {
	//			System.out.println("Already registered with GCM");
	//			//Toast.makeText(getApplicationContext(), "Already registered with GCM", Toast.LENGTH_LONG).show();
	//		}
	//	}
	@Override
	protected void onUnregistered(Context context, String registrationId) {
		//Log.i(TAG, "Device unregistered");
		CommonUtilities.displayMessage(context, "Device unregistered");
		if (GCMRegistrar.isRegisteredOnServer(context)) {
			unregister(context, registrationId);
		} else {
			// This callback results from the call to unregister made on
			// ServerUtilities when the registration to the server failed.
			//Log.i(TAG, "Ignoring unregister callback");
		}
	}
	/**
	 * Unregister this account/device pair within the server.
	 */
	static void unregister(final Context context, final String regId) {
		//		Log.i(TAG, "unregistering device (regId = " + regId + ")");
		//		String serverUrl =MainActivity.SERVER_URL + "/unregister";
		//		Map<String, String> params = new HashMap<String, String>();
		//		params.put("regId", regId);
		//		try {
		//			post(serverUrl, params);
		//			GCMRegistrar.setRegisteredOnServer(context, false);
		//			String message = context.getString(R.string.server_unregistered);
		//			MainActivity.displayMessage(context, message);
		//		} catch (IOException e) {
		//			// At this point the device is unregistered from GCM, but still
		//			// registered in the server.
		//			// We could try to unregister again, but it is not necessary:
		//			// if the server tries to send a message to the device, it will get
		//			// a "NotRegistered" error message and should unregister the device.
		//			String message = context.getString(R.string.server_unregister_error,
		//					e.getMessage());
		//			MainActivity.displayMessage(context, message);
		//		}
	}
	/**
	 * Issue a POST request to the server.
	 *
	 * @param endpoint POST address.
	 * @param params request parameters.
	 *
	 * @throws IOException propagated from POST.
	 */
	private static void post(String endpoint, Map<String, String> params)  throws IOException {   	

		URL url;
		try {
			url = new URL(endpoint);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException("invalid url: " + endpoint);
		}
		StringBuilder bodyBuilder = new StringBuilder();
		Iterator<Entry<String, String>> iterator = params.entrySet().iterator();
		// constructs the POST body using the parameters
		while (iterator.hasNext()) {
			Entry<String, String> param = iterator.next();
			bodyBuilder.append(param.getKey()).append('=')
			.append(param.getValue());
			if (iterator.hasNext()) {
				bodyBuilder.append('&');
			}
		}
		String body = bodyBuilder.toString();
		//Log.v(TAG, "Posting '" + body + "' to " + url);
		byte[] bytes = body.getBytes();
		HttpURLConnection conn = null;
		try {
			//Log.e("URL", "> " + url);
			conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setUseCaches(false);
			conn.setFixedLengthStreamingMode(bytes.length);
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded;charset=UTF-8");
			// post the request
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			// handle the response
			int status = conn.getResponseCode();
			if (status != 200) {
				throw new IOException("Post failed with error code " + status);
			}
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}
	}
	/**
	 * Issues a notification to inform the user that server has sent a message.
	 */
	private static Void generateNotification(Context context, String message) {
		int icon = R.drawable.app_icon;
		Intent notificationIntent;
		SharedPreferences login_prefs;

		long when = System.currentTimeMillis();
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(icon, message, when);
		String title = context.getString(R.string.app_name);
		login_prefs = context.getSharedPreferences("LOGIN", 0);
		int isloggedin=login_prefs.getInt("isloggedin", 0);

		if(isloggedin==1){
			notificationIntent = new Intent(context, CaptureVideoActivity.class);
		}else{
			notificationIntent = new Intent(context, SignInActivity.class);
		}
		notificationIntent.putExtra("From Notification", 1);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent intent = PendingIntent.getActivity(context, 1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT );
		notification.setLatestEventInfo(context, title, message, intent);
		notification.defaults |= Notification.DEFAULT_SOUND;
		//notification.sound = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notificationsound.mp3);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(0, notification);

		return null;
	}
}
