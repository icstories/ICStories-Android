package com.eeshana.icstories.activities;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eeshana.icstories.R;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.MD5Convertor;
import com.eeshana.icstories.common.ShowCustomToast;
import com.eeshana.icstories.webservices.ChangePasswordWebservice;
import com.eeshana.icstories.webservices.LogoutWebservice;
import com.facebook.Session;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebook.OnLogoutListener;

public class SettingsActivity extends Activity implements OnClickListener{
	protected static final String TAG = "FB LOGOUT";
	EditText oldPasswordEditText,newPasswordEditText;
	ImageView logoImageView;
	Button changePasswordButton,logoutButton;
	int height,width;
	RelativeLayout homeRelativeLayout,wallRelativeLayout,settingsRelativeLayout;
	TextView homeTextView,wallTextView,settingsTextView;
	SharedPreferences prefs,login_prefs;
	ProgressDialog mProgressDialog;
	ShowCustomToast showCustomToast;
	String oldPassword,newPassword,userid,regId;
	StringBuffer md5OldPassword,md5NewPassword;
	int fb_flag;
	ConnectionDetector connectionDetector;
	private SimpleFacebook mSimpleFacebook;
	//TextView iCTextView,storiesTextView,watsStoryTextView;
	TextView changePassTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.change_password_activity);

		mSimpleFacebook = SimpleFacebook.getInstance(this);

		height=SignInActivity.getScreenHeight(SettingsActivity.this);
		width=SignInActivity.getScreenWidth(SettingsActivity.this);
		prefs = getSharedPreferences("DB",0);
		userid=prefs.getString("userid", "1");
		fb_flag=prefs.getInt("fb_flag", 0);
		login_prefs = getSharedPreferences("LOGIN", 0);
		SharedPreferences pref = getSharedPreferences("PREF",MODE_PRIVATE);
		regId=pref.getString("regid", "NA");
		mProgressDialog=new ProgressDialog(SettingsActivity.this);
		showCustomToast=new ShowCustomToast();
		connectionDetector= new ConnectionDetector(SettingsActivity.this);
		
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
		
		changePassTextView=(TextView) findViewById(R.id.tvChange);
		changePassTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		
		oldPasswordEditText=(EditText) findViewById(R.id.etOldPassword);
		oldPasswordEditText.setTypeface(CaptureVideoActivity.stories_typeface);
		oldPasswordEditText.setPadding(10, 0, 0, 0);
		oldPasswordEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp1.addRule(RelativeLayout.BELOW, R.id.tvLine);
		lp1.setMargins(20,30, 20, 0);
		oldPasswordEditText.setLayoutParams(lp1);

		newPasswordEditText=(EditText) findViewById(R.id.etNewPassword);
		newPasswordEditText.setTypeface(CaptureVideoActivity.stories_typeface);
		newPasswordEditText.setPadding(10, 0, 0, 0);
		newPasswordEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp2.addRule(RelativeLayout.BELOW, R.id.etOldPassword);
		lp2.setMargins(20,30, 20, 0);
		newPasswordEditText.setLayoutParams(lp2);

		changePasswordButton=(Button) findViewById(R.id.buttonChangePassword);
		changePasswordButton.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp3.addRule(RelativeLayout.BELOW, R.id.etNewPassword);
		lp3.setMargins(width/7,30,width/7, 0);
		changePasswordButton.setLayoutParams(lp3);
		changePasswordButton.setOnClickListener(this);

		logoutButton=(Button) findViewById(R.id.buttonLogout);
		logoutButton.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp0 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp0.addRule(RelativeLayout.BELOW, R.id.buttonChangePassword);
		lp0.setMargins(width/7,30,width/7, 0);
		logoutButton.setLayoutParams(lp0);

		logoutButton.setOnClickListener(this);

		//bottom bar

		homeRelativeLayout=(RelativeLayout) findViewById(R.id.rlHome);

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
		homeTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp5.addRule(RelativeLayout.RIGHT_OF,R.id.rlHome);
		wallRelativeLayout.setLayoutParams(lp5);
		wallRelativeLayout.setOnClickListener(this);

		settingsRelativeLayout=(RelativeLayout) findViewById(R.id.rlSettings);
		settingsRelativeLayout.setBackgroundResource(R.drawable.press_border);
		settingsRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		settingsTextView=(TextView) settingsRelativeLayout.findViewById(R.id.tvSettings);
		homeTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp6.addRule(RelativeLayout.RIGHT_OF,R.id.rlWall);
		settingsRelativeLayout.setLayoutParams(lp6);
		settingsRelativeLayout.setOnClickListener(this);
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
	}
	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {
		if(v.getId()==wallRelativeLayout.getId()){
			Intent i=new Intent(SettingsActivity.this,WallActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}else if(v.getId()==homeRelativeLayout.getId()){
			Intent i=new Intent(SettingsActivity.this,CaptureVideoActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}else if(v.getId()==logoutButton.getId()){
			if(CaptureVideoActivity.alreadyUploading==0){
				if(fb_flag==1){
					mSimpleFacebook.logout(mOnLogoutListener);
				}else{
					//new  LogoutTask().execute();
					showCustomToast.showToast(SettingsActivity.this,"You are now logged out.");
					SharedPreferences.Editor editor = prefs.edit();
					editor.remove("userid");
					editor.remove("password");
					editor.remove("username");
					editor.remove("fb_flag");
					editor.clear();
					editor.commit();
					SharedPreferences.Editor editor2 = login_prefs.edit();
					editor2.remove("isloggedin");
					editor2.clear();
					editor2.commit();
					Intent i=new Intent(SettingsActivity.this,SignInActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(i);
					finish();
				}
			}else{
				showCustomToast.showToast(SettingsActivity.this,"You cannot logout. Your video is getting uploaded. Please try later.");
			}

		}else if(v.getId()==changePasswordButton.getId()){
			if(connectionDetector.isConnectedToInternet()){
				if(fb_flag==1){
					showCustomToast.showToast(SettingsActivity.this, "You are logged in with FB. Cannot change password");
				}else{
					oldPassword=oldPasswordEditText.getText().toString();
					newPassword=newPasswordEditText.getText().toString();
					if(oldPassword.equalsIgnoreCase("")|| newPassword.equalsIgnoreCase("")){
						showCustomToast.showToast(SettingsActivity.this,"Both fields are required.");
					}else{
						if(oldPassword.length()<=10 && !oldPassword.contains(" ") && isAlphaNumeric(oldPassword) && newPassword.length()<=10 && !newPassword.contains(" ") && isAlphaNumeric(newPassword) && newPassword.length()>=4 && oldPassword.length()>=4 ){
							MD5Convertor md5Convertor=new MD5Convertor();
							md5OldPassword=md5Convertor.convertToMd5(oldPassword);
							md5NewPassword=md5Convertor.convertToMd5(newPassword);
							if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
								new ChangePasswordTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
							else
							new ChangePasswordTask().execute();
						}else{
							if((oldPassword.length()>10) || oldPassword.length()<4){
								oldPasswordEditText.setError("Password should not be less than 4 & more than 10 characters.");
							}
							if((oldPassword.contains(" "))){
								oldPasswordEditText.setError("Password should not contain spaces.");
							}
							if(!isAlphaNumeric(oldPassword)){
								oldPasswordEditText.setError("Enter alphabets or numericals or both. Special characters are not allowed.");
							}
							if((newPassword.length()>10) || (newPassword.length()<4)){
								newPasswordEditText.setError("Password should not be less than 4 & more than 6 characters.");
							}
							if((newPassword.contains(" "))){
								newPasswordEditText.setError("Password should not contain spaces.");
							}
							if(!isAlphaNumeric(newPassword)){
								newPasswordEditText.setError("Enter alphabets or numericals or both. Special characters are not allowed.");
							}
						}
					}
				}
			}else{
				showCustomToast.showToast(SettingsActivity.this, "Please check your internet connection.");
			}
		}
	}

	// Logout listener
	private OnLogoutListener mOnLogoutListener = new OnLogoutListener()
	{

		@Override
		public void onFail(String reason)
		{
			//Log.w(TAG, "Failed to login");
		}

		@Override
		public void onException(Throwable throwable)
		{
			//Log.e(TAG, "Bad thing happened", throwable);
		}

		@Override
		public void onThinking()
		{
			// show progress bar or something to the user while login is happening
		}

		@Override
		public void onLogout()
		{
			Session session = Session.getActiveSession();
			if (session != null){
				session.closeAndClearTokenInformation();
				Session.setActiveSession(null);
			}

			showCustomToast.showToast(SettingsActivity.this,"You are now logged out.");
			SharedPreferences.Editor editor = prefs.edit();
			editor.remove("userid");
			editor.remove("password");
			editor.remove("username");
			editor.remove("fb_flag");
			editor.clear();
			editor.commit();
			SharedPreferences.Editor editor2 = login_prefs.edit();
			editor2.remove("isloggedin");
			editor2.clear();
			editor2.commit();
			Intent i=new Intent(SettingsActivity.this,SignInActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}

	};

	public boolean isAlphaNumeric(String s){
		String pattern= "^[a-zA-Z0-9 ]*$"; 
		if(s.matches(pattern)){
			return true;
		}
		return false;   
	}
	class ChangePasswordTask extends AsyncTask<Void, Void, Void> {
		String status;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			ChangePasswordWebservice changePasswordWebservice=new ChangePasswordWebservice();
			String jsonResponse = changePasswordWebservice.changePassword(userid, md5OldPassword.toString(), md5NewPassword.toString());

			if(jsonResponse!=null){
				//System.out.println("jsonResponse change pass :: "+jsonResponse);
				//on fail - jsonResponse Login :: {"status":"0","statusInfo":"fail","userid":null}
				//on success : {"status":"1","statusInfo":"success","userid":"19"}
				try {
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressDialog.hide();
			if(status!=null){
				if(status .equalsIgnoreCase("1")){
					showCustomToast.showToast(SettingsActivity.this,"Password changed successfully.");
				}else if(status.equalsIgnoreCase("0")){
					//Toast.makeText(getApplicationContext(), "Invalid username or password ", Toast.LENGTH_LONG).show();
					showCustomToast.showToast(SettingsActivity.this,"Could not change password. Please try again.");
				}
			}else{
			}
		}
	}
	class LogoutTask extends AsyncTask<Void, Void, Void> {
		String status;
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			LogoutWebservice logoutWebservice=new LogoutWebservice();
			String jsonResponse = logoutWebservice.userLogout(userid,regId);

			if(jsonResponse!=null){
				//System.out.println("jsonResponse Login :: "+jsonResponse);

				//on fail - jsonResponse Login :: {"status":"0","statusInfo":"fail","userid":null}
				//on success : {"status":"1","statusInfo":"success","userid":"19"}
				try {
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressDialog.hide();
			if(status!=null){
				if(status .equalsIgnoreCase("1")){
					showCustomToast.showToast(SettingsActivity.this,"You are now logged out.");
					SharedPreferences.Editor editor = prefs.edit();
					editor.remove("userid");
					editor.remove("password");
					editor.remove("username");
					editor.remove("fb_flag");
					editor.clear();
					editor.commit();
					Intent i=new Intent(SettingsActivity.this,SignInActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
					startActivity(i);
					finish();
				}else if(status.equalsIgnoreCase("0")){
					//Toast.makeText(getApplicationContext(), "Invalid username or password ", Toast.LENGTH_LONG).show();
					showCustomToast.showToast(SettingsActivity.this,"Could not log out. Please try again");
				}
			}else{
			}
		}
	}
}
