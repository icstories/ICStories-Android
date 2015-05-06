package com.eeshana.icstories.activities;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eeshana.icstories.R;
import com.eeshana.icstories.activities.RegistrationActivity.RegistrationTask;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.MD5Convertor;
import com.eeshana.icstories.common.ShowCustomToast;
import com.eeshana.icstories.webservices.LoginWebservice;
import com.eeshana.icstories.webservices.RegistrationWebservice;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ActionBar.LayoutParams;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Point;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PaintDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi") public class SignInActivity extends Activity implements OnClickListener{
	int width,height;
	Button signinButton,okButton,cancelButton;
	EditText usernameEditText,passwordEditText,emailEditText,forgotPassEmailEditText;
	Animation anim = new AlphaAnimation(0.0f, 1.0f);
	String username,password,regId,status;
	StringBuffer md5Password;
	SharedPreferences prefs;
	ProgressDialog mProgressDialog;
	TextView signupTextView,fbloginTextView,forgotPasswordTextView,dialog_forgotPasswordTextView,termsOfUseTextView,privacyPolicyTextView;
	ImageView closeImageView;
	WebView termsWebView;
	RelativeLayout termsRelativeLayout;
	ShowCustomToast showCustomToast;
	public static int opening_first_time=0; //  for calling reg ws for fb login once only used in capture video activity
	ConnectionDetector connectionDetector;
	String assignment_id,assignment_text,assignment_date,assignment_img_url,assignment_img_name,assignment_image,link;
	public static Typeface ic_typeface,stories_typeface;
	//TextView iCTextView,storiesTextView,watsStoryTextView;
	TextView loginTextView,dontHaveTextView;
	ImageView logoImageView;
	Dialog forgotPassDialog;

	//fb variables
	private static List<String> permissions;
	Session.StatusCallback statusCallback = new SessionStatusCallback();
	ProgressDialog dialog;
	SharedPreferences fb_prefs,login_prefs;
	public String fb_zip = null;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signin_activity);
		login_prefs = getSharedPreferences("LOGIN", 0);
		login_prefs.edit().putInt("isloggedin", 0).commit();
		ic_typeface=Typeface.createFromAsset(getAssets(), "fonts/MYRIADPRO-BOLD.OTF");
		stories_typeface=Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-lt-com-55-roman.ttf");
		fb_prefs = getSharedPreferences("DB", 0);
		connectionDetector=new ConnectionDetector(SignInActivity.this);
		SharedPreferences prefs = getSharedPreferences("PREF",MODE_PRIVATE);
		regId=prefs.getString("regid", "NA");
		mProgressDialog= new ProgressDialog(SignInActivity.this);
		anim.setDuration(50); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.ABSOLUTE);
		anim.setRepeatCount(Animation.ABSOLUTE);
		showCustomToast=new ShowCustomToast();
		height=getScreenHeight(SignInActivity.this);
		width=getScreenWidth(SignInActivity.this);

		//		iCTextView=(TextView) findViewById(R.id.tvIC);
		//		iCTextView.setTypeface(SignInActivity.ic_typeface,Typeface.BOLD);
		//		storiesTextView=(TextView) findViewById(R.id.tvStories);
		//		storiesTextView.setTypeface(SignInActivity.stories_typeface,Typeface.BOLD);
		//		watsStoryTextView=(TextView) findViewById(R.id.tvWhatsStory);
		//		watsStoryTextView.setTypeface(SignInActivity.stories_typeface,Typeface.ITALIC);

		//		logoImageView=(ImageView) findViewById(R.id.ivLogo);
		//		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (width/1.9), (int) (height/9.5));
		//		lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
		//		lp.setMargins(0, 0, 0, 0);
		//		logoImageView.setLayoutParams(lp);

		loginTextView=(TextView) findViewById(R.id.tvLogin);
		loginTextView.setTypeface(SignInActivity.stories_typeface,Typeface.BOLD);

		usernameEditText=(EditText) findViewById(R.id.etUsername);
		usernameEditText.setTypeface(SignInActivity.stories_typeface);
		usernameEditText.setPadding(15, 0, 0, 0);
		usernameEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/16);
		lp2.addRule(RelativeLayout.BELOW, R.id.rlLoginfb);
		lp2.setMargins(10,30, 10, 0);
		usernameEditText.setLayoutParams(lp2);

		passwordEditText=(EditText) findViewById(R.id.etPassword);
		passwordEditText.setTypeface(SignInActivity.stories_typeface);
		passwordEditText.setPadding(15, 0, 0, 0);
		passwordEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/16);
		lp3.addRule(RelativeLayout.BELOW, R.id.etUsername);
		lp3.setMargins(10,20, 10, 0);
		passwordEditText.setLayoutParams(lp3);

		signinButton=(Button) findViewById(R.id.buttonSignin);
		signinButton.setTypeface(SignInActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp4.addRule(RelativeLayout.BELOW, R.id.etPassword);
		lp4.setMargins(width/7,40,width/7,0);
		signinButton.setLayoutParams(lp4);

		signinButton.setOnClickListener(this);

		dontHaveTextView=(TextView) findViewById(R.id.tvDontHave);
		dontHaveTextView.setTypeface(SignInActivity.stories_typeface);
		signupTextView=(TextView) findViewById(R.id.tvSignup);
		signupTextView.setTypeface(SignInActivity.stories_typeface);
		signupTextView.setOnClickListener(this);
		forgotPasswordTextView=(TextView) findViewById(R.id.tvForgotPassword);
		forgotPasswordTextView.setTypeface(SignInActivity.stories_typeface);
		//		RelativeLayout.LayoutParams lp04 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		//		lp04.addRule(RelativeLayout.BELOW, R.id.rlSignup);
		//		forgotPasswordTextView.setLayoutParams(lp04);
		forgotPasswordTextView.setOnClickListener(this);
		termsOfUseTextView=(TextView) findViewById(R.id.tvTermsOfUse);
		termsOfUseTextView.setTypeface(SignInActivity.stories_typeface);
		termsOfUseTextView.setOnClickListener(this);
		privacyPolicyTextView=(TextView) findViewById(R.id.tvPrivacyPolicy);
		privacyPolicyTextView.setTypeface(SignInActivity.stories_typeface);
		privacyPolicyTextView.setOnClickListener(this);

		termsRelativeLayout=(RelativeLayout) findViewById(R.id.rlTerms);
		termsWebView=(WebView) findViewById(R.id.wvTerms);
		closeImageView=(ImageView) findViewById(R.id.ivCloseTerms);
		closeImageView.setOnClickListener(this);

		forgotPassDialog=new Dialog(SignInActivity.this);
		forgotPassDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		forgotPassDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		forgotPassDialog.setContentView(R.layout.forgotpassword_dialog);
		forgotPassEmailEditText=(EditText)forgotPassDialog.findViewById(R.id.etMail);
		okButton=(Button)forgotPassDialog.findViewById(R.id.OkBtn);
		okButton.setTag("");
		cancelButton=(Button)forgotPassDialog.findViewById(R.id.CancelBtn);
		dialog_forgotPasswordTextView=(TextView) forgotPassDialog.findViewById(R.id.tvForgotPassword);
		forgotPassDialog.setCancelable(true);
		okButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if(okButton.getTag().toString().equals("OK")){
					String Zip=forgotPassEmailEditText.getText().toString();
					if(Zip.equals("")){
						forgotPassEmailEditText.setError("Required");
					}else{
						if(Zip.length()==5){
							fb_prefs.edit().putString("fb_zip", Zip).commit();
							Intent i=new Intent(SignInActivity.this, CaptureVideoActivity.class);
							i.putExtra("FB_FLAG", "YES");
							startActivity(i);
							SignInActivity.this.finish();
							fb_prefs.edit().putInt("fb_flag", 1).commit();
						}else{
							forgotPassEmailEditText.setError("Zip-code must be 5 digits.");
						}
					}
				}else{
					String email=forgotPassEmailEditText.getText().toString();
					if(!email.equals("")){
						if(connectionDetector.isConnectedToInternet()==true){
							if(emailValidator(email)){
								new ForgotPasswordTask().execute(email);
							}else{
								forgotPassEmailEditText.setError("Invalid email (name@domain.com).");
							}
						}else{
							showCustomToast.showToast(SignInActivity.this,"Please check your internet connection");
						}
					}else{
						forgotPassEmailEditText.setError("Required");
					}
				}
			}
		});

		cancelButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				forgotPassDialog.dismiss();
				forgotPassEmailEditText.setText("");
			}
		});

		//fb

		//get hashkey
		try {
			PackageInfo info = getPackageManager().getPackageInfo(
					"com.eeshana.icstories", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				//Toast.makeText(SignInActivity.this, " "+Base64.encodeToString(md.digest(), Base64.DEFAULT), Toast.LENGTH_SHORT).show();
				Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
		} catch (NameNotFoundException e) {

		} catch (NoSuchAlgorithmException e) {

		}

		//permissions
		permissions = new ArrayList<String>();
		permissions.add("email");

		fbloginTextView=(TextView) findViewById(R.id.tvLoginfb);
		fbloginTextView.setTypeface(SignInActivity.stories_typeface);
		fbloginTextView.setOnClickListener(this);

		Session session = Session.getActiveSession();
		if(session == null) {
			if(savedInstanceState != null) {
				session = Session.restoreSession(this, null, statusCallback, savedInstanceState);
			}
			if(session == null) {
				session = new Session(SignInActivity.this);
			}
			Session.setActiveSession(session);
			//			session.addCallback(statusCallback);
			//			if(session.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
			//				session.openForRead(new Session.OpenRequest(this).setCallback(statusCallback).setPermissions(permissions));
			//			} 
		}
	}

	private class SessionStatusCallback implements Session.StatusCallback {

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			//Check if Session is Opened or not
			processSessionStatus(session, state, exception);
		}

	}
	public void processSessionStatus(Session session, SessionState state, Exception exception) {

		//in processSessionStatus....{Session state:CLOSED_LOGIN_FAILED, token:{AccessToken token:ACCESS_TOKEN_REMOVED permissions:[]}, appId:390611384407544}

		if(session != null && session.isOpened()) {

			if(session.getPermissions().contains("email")) {
				//Show Progress Dialog 
				dialog = new ProgressDialog(SignInActivity.this);
				dialog.setMessage("Loggin in..");
				dialog.show();

				Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response) {

						if (dialog!=null && dialog.isShowing()) {
							dialog.dismiss();
						}
						if(user != null) {
							Map<String, Object> responseMap = new HashMap<String, Object>();
							GraphObject graphObject = response.getGraphObject();
							responseMap = graphObject.asMap();
							//Log.i("FbLogin", "Response Map KeySet - " + responseMap.keySet());
							// TODO : Get Email responseMap.get("email"); 

							String fb_id = user.getId();
							//String fb_username=user.getUsername();
							String email = null;
							String fb_username = (String) responseMap.get("name");

							if (responseMap.get("email")!=null) {
								email = responseMap.get("email").toString();
								fb_prefs.edit().putString("fb_email", email).commit();
								fb_prefs.edit().putString("fb_token", fb_id).commit();
								fb_prefs.edit().putString("fb_username", fb_username).commit();
								//1
								//	startActivity(new Intent(SignInActivity.this, CaptureVideoActivity.class));
								//	SignInActivity.this.finish();
								//2

								//for getting city,state,country
								if(getZipFromFB(fb_id)){
									forgotPassEmailEditText.setText("");
									if(fb_zip.equals("")){
										dialog_forgotPasswordTextView.setVisibility(View.GONE);
										okButton.setText("Ok");
										okButton.setTag("OK");
										forgotPassEmailEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
										forgotPassEmailEditText.setHint("Please enter zip code.");
										forgotPassDialog.show();
									}else{
										fb_prefs.edit().putString("fb_zip", fb_zip).commit();
										Intent i=new Intent(SignInActivity.this, CaptureVideoActivity.class);
										i.putExtra("FB_FLAG", "YES");
										startActivity(i);
										SignInActivity.this.finish();
										fb_prefs.edit().putInt("fb_flag", 1).commit();
									}
								}else{
									dialog_forgotPasswordTextView.setVisibility(View.GONE);
									okButton.setText("Ok");
									okButton.setTag("OK");
									forgotPassEmailEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
									forgotPassEmailEditText.setHint("Please enter zip code.");
									forgotPassDialog.show();
								}
							}else {
								//Clear all session info & ask user to login again
								Session session = Session.getActiveSession();
								if(session != null) {
									session.closeAndClearTokenInformation();
								}
							}
						}else{
							fb_prefs.edit().putInt("fb_flag", 0).commit();
						}
					}
				});
				//3
				//new RegistrationTask().execute(fb_email,fb_token);
			} else {
				session.requestNewReadPermissions(new Session.NewPermissionsRequest(SignInActivity.this, permissions));
			}
		}
	}

	private boolean getZipFromFB(String fb_id) {

		String fqlQuery = "SELECT current_location FROM user WHERE uid="+fb_id;
		Bundle params = new Bundle();
		params.putString("q", fqlQuery);
		Session session = Session.getActiveSession();
		Request request = new Request(session, 
				"/fql", 
				params, 
				HttpMethod.GET, 
				new Request.Callback(){ 
			@SuppressLint("NewApi")
			public void onCompleted(Response response) {
				Log.i("Fb FQL ", "Got results: " + response.toString());
				//{Response:  responseCode: 200, graphObject: GraphObject{graphObjectClass=GraphObject,
				//state={"data":[{"current_location":{"id":"106442706060302","zip":"","name":"Pune, Maharashtra","state":"Maharashtra","longitude":73.8567,"latitude":18.5203,"country":"India","city":"Pune"}}]}},
				//error: null, isFromCache:false}
				GraphObject graphObject=response.getGraphObject();
				JSONObject dataJsonObject=graphObject.getInnerJSONObject();
				System.out.println("dataJsonObject ==   "+dataJsonObject);
				//{"data":[{"current_location":{"id":"106442706060302","zip":"","name":"Pune, Maharashtra","state":"Maharashtra",
				//"longitude":73.8567,"latitude":18.5203,"country":"India","city":"Pune"},
				//"pic_square":"https:\/\/scontent-a.xx.fbcdn.net\/hprofile-frc3\/l\/t1.0-1\/p50x50\/1966954_646760378731577_3872711505609795767_t.jpg"}]}
				JSONArray dataJsonArray = null;
				try {
					dataJsonArray = dataJsonObject.getJSONArray("data");
					JSONObject locationJsonObject=dataJsonArray.getJSONObject(0).getJSONObject("current_location");
					fb_zip=locationJsonObject.getString("zip");
					System.out.println("FB ZIP !!!!! "+fb_zip);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
		Request.executeBatchAsync(request);
		if(fb_zip!=null){
			return true;
		}else{
			return false;
		}
	}

	public boolean emailValidator(String email) {
		Pattern pattern;
		Matcher matcher;
		final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==signinButton.getId()){
			if(connectionDetector.isConnectedToInternet()){
				signIn();
			}else{
				showCustomToast.showToast(SignInActivity.this, "Please check your internet connection.");
			}
		}else if(v.getId()==signupTextView.getId()){
			signupTextView.startAnimation(anim);
			if(connectionDetector.isConnectedToInternet()){
				fb_prefs.edit().putInt("fb_flag", 0).commit();
				Intent i=new Intent(SignInActivity.this, RegistrationActivity.class);
				startActivity(i);
				SignInActivity.this.finish();
			}else{
				showCustomToast.showToast(SignInActivity.this, "Please check your internet connection.");
			}
		}else if(v.getId()==fbloginTextView.getId()){
			fbloginTextView.startAnimation(anim);
			if(connectionDetector.isConnectedToInternet()){
				//showCustomToast.showToast(SignInActivity.this, "Working on facebook session maintenance.");
				Session session = Session.getActiveSession();
				if (session == null) {
					Session.openActiveSession(SignInActivity.this, true, statusCallback);
				} else if (!session.isOpened()) {
					//session.addCallback(statusCallback);
					if(session.getState().equals(SessionState.CLOSED_LOGIN_FAILED)) {
						//session.openForRead(new Session.OpenRequest(SignInActivity.this).setCallback(statusCallback).setPermissions(permissions));
						//showCustomToast.showToast(SignInActivity.this, "Working on facebook session maintenance.");
						//Session session2 = Session.getActiveSession();
						if (session != null){
							session.closeAndClearTokenInformation();
							Session.setActiveSession(null);
						}
						Session.openActiveSession(SignInActivity.this, true, statusCallback);
					}else{
						session.openForRead(new Session.OpenRequest(SignInActivity.this).setCallback(statusCallback).setPermissions(permissions));
					}
				}else{
					session.closeAndClearTokenInformation();
					Session.setActiveSession(null);
					Session.openActiveSession(SignInActivity.this, true, statusCallback);
				}
			}else{
				showCustomToast.showToast(SignInActivity.this, "Please check your internet connection.");
			}
			// TODO Check if there is any Active Session, otherwise Open New Session
			//			Session session = Session.getActiveSession();
			//			System.out.println("fb clicked...   "+session);
			//			if(!session.isOpened()) {
			//				session.openForRead(new Session.OpenRequest(SignInActivity.this).setCallback(statusCallback).setPermissions(permissions));
			//			} else {
			//				Session.openActiveSession(SignInActivity.this, true, statusCallback);
			//			}
		}else if(v.getId()==forgotPasswordTextView.getId()){
			dialog_forgotPasswordTextView.setVisibility(View.VISIBLE);
			okButton.setText("Send");
			okButton.setTag("");
			forgotPassEmailEditText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
			forgotPassEmailEditText.setHint("Email (name@domain.com)");
			forgotPassDialog.show();
			forgotPassDialog.show();
		}else if(v.getId()==termsOfUseTextView.getId()){
			termsOfUseTextView.startAnimation(anim);
			termsWebView.loadUrl("http://www.icstories.com/icstories/home/termsofuse");
			//termsWebView.loadUrl("http://bizmoapps.com/icstories/home/termsofuse");
			termsRelativeLayout.setVisibility(View.VISIBLE);
		}else if(v.getId()==privacyPolicyTextView.getId()){
			privacyPolicyTextView.startAnimation(anim);
			termsWebView.loadUrl("http://www.icstories.com/icstories/home/policy");
			//termsWebView.loadUrl("http://bizmoapps.com/icstories/home/policy");
			termsRelativeLayout.setVisibility(View.VISIBLE);
		}else if(v.getId()==closeImageView.getId()){
			closeImageView.startAnimation(anim);
			termsRelativeLayout.setVisibility(View.GONE);
		}
	}
	private void signIn() {
		username=usernameEditText.getText().toString();
		password=passwordEditText.getText().toString();
		MD5Convertor md5Convertor=new MD5Convertor();
		md5Password=md5Convertor.convertToMd5(password);
		if(username.equalsIgnoreCase("")||password.equalsIgnoreCase("")){
			showCustomToast.showToast(SignInActivity.this, "Both fields are required.");
		}else{
			if(username.length()<=20 && password.length()<=10 && username.length()>=4 && password.length()>=4 && !password.contains(" ") &&  isAlphaNumeric(username) && isAlphaNumeric(password)){
				new SigninTask().execute();
			}else{
				if((username.length()<4) || (username.length()>20)){
					usernameEditText.setError("Username should not be less than 4 & more than 20 characters.");
				}
				if((password.length()<4) || (password.length()>10)){
					passwordEditText.setError("Password should not be less than 4 & more than 10 characters.");
				}
				if((password.contains(" "))){
					passwordEditText.setError("Password should not contain spaces.");
				}
				if(!isAlphaNumeric(username)){
					//usernameEditText.setError("Enter alphabets or numericals or both. Special characters are not allowed.");
					usernameEditText.setError("Invalid username.");
				}
				if(!isAlphaNumeric(password)){
					//passwordEditText.setError("Enter alphabets or numericals or both. Special characters are not allowed.");
					passwordEditText.setError("Invalid password.");
				}
			}
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		if(forgotPassDialog.isShowing())
			forgotPassDialog.dismiss();

	}

	public boolean isAlphaNumeric(String s){
		String pattern= "^[a-zA-Z0-9 ]*$"; 
		if(s.matches(pattern)){
			return true;
		}
		return false;   
	}

	class SigninTask extends AsyncTask<Void, Void, Void> {
		JSONArray jsonArray,useridJsonArray;
		String userid,logid;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			LoginWebservice loginWebservice=new LoginWebservice();
			String jsonResponse = loginWebservice.userLogin(username,md5Password.toString(),regId);

			if(jsonResponse!=null){
				//System.out.println("jsonResponse Login :: "+jsonResponse);
				//on fail - jsonResponse Login :: {"status":"0","statusInfo":"fail","userid":null}
				//on success : jsonResponse Login :: {"status":"1","statusInfo":"success","ids":[{"user_id":"45","log_id":"72"}],"image_url":"http:\/\/bizmoapps.com\/icstories\/uploads\/assignments","assignment_info":[{"assignment_id":"65","assignment_name":"new data","assignment_date":"02\/18\/2014","assignment_file":"content-box-right-bg.png","assignment_url":""}]}
				try {
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
					if(status.equalsIgnoreCase("1")){
						useridJsonArray =jsonObject.getJSONArray("ids");
						assignment_img_url=jsonObject.getString("image_url");
						for (int i = 0; i < useridJsonArray.length(); i++) {
							JSONObject menuObject = useridJsonArray.getJSONObject(i);
							userid=menuObject.getString("user_id");
							logid=menuObject.getString("log_id");
						}
						jsonArray=jsonObject.getJSONArray("assignment_info");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject menuObject = jsonArray.getJSONObject(i);
							assignment_id=menuObject.getString("assignment_id");
							assignment_text=menuObject.getString("assignment_name");
							assignment_date=menuObject.getString("assignment_date");
							assignment_img_name=menuObject.getString("assignment_file");
							link=menuObject.getString("assignment_url");
						}
					}
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
					SharedPreferences settings = getSharedPreferences("DB", 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("userid", userid);
					editor.putString("logid", logid);
					editor.putString("username", username);
					editor.putString("password", password);
					editor.commit(); 
					fb_prefs.edit().putInt("fb_flag", 0).commit();
					SharedPreferences assign_prefs = getSharedPreferences("ASSIGN", 0);
					SharedPreferences.Editor editor2 = assign_prefs.edit(); 
					if( jsonArray.length()>0){
						if(assignment_img_name.equals("")){
							assignment_image="";
						}else{
							assignment_image=assignment_img_url+"/"+assignment_img_name;
						}
					}
					editor2.putString("ASSIGN_ID", assignment_id);
					editor2.putString("ASSIGN_TEXT", assignment_text);
					editor2.putString("ASSIGN_DATE", assignment_date);
					editor2.putString("ASSIGN_LINK", link);
					editor2.putString("ASSIGN_IMAGE", assignment_image);
					editor2.commit(); 

					Intent i=new Intent(SignInActivity.this, CaptureVideoActivity.class);
					startActivity(i);
					finish();
					//Toast.makeText(getApplicationContext(), "You are now logged in. ", Toast.LENGTH_LONG).show();
					showCustomToast.showToast(SignInActivity.this,"You are now logged in.");
				}else if(status.equalsIgnoreCase("0")){
					//Toast.makeText(getApplicationContext(), "Invalid username or password ", Toast.LENGTH_LONG).show();
					showCustomToast.showToast(SignInActivity.this,"Invalid username or password.");
				}
			}else{
			}
		}
	}
	public static int getScreenHeight(Context c) {
		WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		try {
			
			if(Build.VERSION.SDK_INT >= 13)
			{
				display.getSize(size);
			}else{
				size.x = display.getWidth();
				size.y = display.getHeight();
			}
			
			
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			size.x = display.getWidth();
			size.y = display.getHeight();
		}
		return size.y;
	}
	public static int getScreenWidth(Context c) {
		WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		try {
			display.getSize(size);
		} catch (java.lang.NoSuchMethodError ignore) { // Older device
			size.x = display.getWidth();
			size.y = display.getHeight();
		}
		return  size.x;
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		//Log.d("FbLogin", "Result Code is - " + resultCode +"");
		Session.getActiveSession().onActivityResult(SignInActivity.this, requestCode, resultCode, data);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Save current session
		super.onSaveInstanceState(outState);
		Session session = Session.getActiveSession();
		Session.saveSession(session, outState);
	}

	@Override
	protected void onStart() {
		// TODO Add status callback
		super.onStart();
		Session.getActiveSession().addCallback(statusCallback);
	}

	@Override
	protected void onStop() {
		// TODO Remove callback
		super.onStop();
		Session.getActiveSession().removeCallback(statusCallback);
	}

	class ForgotPasswordTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			String email=params[0];
			LoginWebservice loginWebservice=new LoginWebservice();
			String jsonResponse = loginWebservice.forgotPassword(email);
			if(jsonResponse!=null)
				//	System.out.println("jsonResponse forgot pass :: "+jsonResponse);
				//	jsonResponse forgot pass :: {"status":"1","statusInfo":"success","message":"mail sent"}
				try {
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			return status;
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mProgressDialog.dismiss();
			
			if(result!=null){
				if(result.equalsIgnoreCase("1")){
					showCustomToast.showToast(SignInActivity.this, "Please check your mail");
					forgotPassEmailEditText.setText("");
					forgotPassDialog.dismiss();
				}else{
					showCustomToast.showToast(SignInActivity.this, "This email is not registered. Please try another.");
				}
			}else{
				forgotPassEmailEditText.setText("");
				forgotPassDialog.dismiss();
			}
		}
	}

}
