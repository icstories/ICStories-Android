package com.eeshana.icstories.activities;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eeshana.icstories.R;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.MD5Convertor;
import com.eeshana.icstories.common.ShowCustomToast;
import com.eeshana.icstories.webservices.RegistrationWebservice;

public class RegistrationActivity extends Activity implements OnClickListener{
	int width,height;
	RelativeLayout fieldsRelativeLayout;
	ImageView logoImageView,uname_icon,pass_icon,fb_icon,Zip_icon,email_icon,state_icon;
	EditText usernameEditText,passwordEditText,ZipEditText,emailEditText;
	Animation anim = new AlphaAnimation(0.0f, 1.0f);
	Button registerButton;
	String username,password,Zip,email,regId,status;
	StringBuffer md5Password;
	SharedPreferences prefs;
	ProgressDialog mProgressDialog;
	ShowCustomToast showCustomToast;
	ConnectionDetector connectionDetector;
	String assignment_id,assignment_text,assignment_date,assignment_img_url,assignment_img_name,assignment_image,link;;
	//TextView iCTextView,storiesTextView,watsStoryTextView;
	TextView regTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_activity);
		anim.setDuration(50); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.ABSOLUTE);
		anim.setRepeatCount(Animation.ABSOLUTE);

		height=SignInActivity.getScreenHeight(RegistrationActivity.this);
		width=SignInActivity.getScreenWidth(RegistrationActivity.this);
		connectionDetector=new ConnectionDetector(RegistrationActivity.this);
		prefs = getSharedPreferences("PREF",MODE_PRIVATE);
		regId=prefs.getString("regid", "NA");
		mProgressDialog= new ProgressDialog(RegistrationActivity.this);
		showCustomToast=new ShowCustomToast();

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

		regTextView=(TextView) findViewById(R.id.tvReg);
		regTextView.setTypeface(SignInActivity.stories_typeface);

		//		fieldsRelativeLayout=(RelativeLayout) findViewById(R.id.rlFields);
		//		RelativeLayout.LayoutParams lp0 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		//		lp0.addRule(RelativeLayout.CENTER_IN_PARENT);
		//		fieldsRelativeLayout.setLayoutParams(lp0);

		RelativeLayout usernameLayout=(RelativeLayout) findViewById(R.id.rlUsername);
		uname_icon=(ImageView) findViewById(R.id.uname_icon);
		uname_icon.bringToFront();
		//uname_icon.setImageResource(R.drawable.username);
		RelativeLayout.LayoutParams lp02 = new RelativeLayout.LayoutParams(width/9, height/18);
		lp02.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp02.setMargins(4, 10, 10, 10);
		uname_icon.setLayoutParams(lp02);	
		usernameEditText=(EditText) findViewById(R.id.etUsername);
		usernameEditText.setTypeface(SignInActivity.stories_typeface);
		usernameEditText.setPadding(15, 0, 0, 0);
		usernameEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/16);
		lp2.addRule(RelativeLayout.BELOW, R.id.tvLine);
		lp2.setMargins(0,20, 0, 0);
		usernameLayout.setLayoutParams(lp2);

		RelativeLayout passwordLayout=(RelativeLayout) findViewById(R.id.rlPassword);
		pass_icon=(ImageView) findViewById(R.id.pass_icon);
		pass_icon.bringToFront();
		//pass_icon.setImageResource(R.drawable.password);
		pass_icon.setLayoutParams(lp02);			
		passwordEditText=(EditText) findViewById(R.id.etPassword);
		usernameEditText.setTypeface(SignInActivity.stories_typeface);
		passwordEditText.setPadding(15, 0, 0, 0);
		passwordEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/16);
		lp3.addRule(RelativeLayout.BELOW, R.id.rlUsername);
		lp3.setMargins(0, 20, 0, 0);
		passwordLayout.setLayoutParams(lp3);

		RelativeLayout ZipRelativeLayout=(RelativeLayout) findViewById(R.id.rlZip);
		Zip_icon=(ImageView) findViewById(R.id.Zip_icon);
		Zip_icon.bringToFront();
		//pass_icon.setImageResource(R.drawable.password);
		Zip_icon.setLayoutParams(lp02);			
		ZipEditText=(EditText) findViewById(R.id.etZip);
		ZipEditText.setTypeface(SignInActivity.stories_typeface);
		ZipEditText.setPadding(15, 0, 0, 0);
		ZipEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/16);
		lp4.addRule(RelativeLayout.BELOW, R.id.rlPassword);
		lp4.setMargins(0, 20, 0, 0);
		ZipRelativeLayout.setLayoutParams(lp4);

		//		RelativeLayout stateRelativeLayout=(RelativeLayout) findViewById(R.id.rlState);
		//		state_icon=(ImageView) findViewById(R.id.state_icon);
		//		state_icon.bringToFront();
		//		//pass_icon.setImageResource(R.drawable.password);
		//		state_icon.setLayoutParams(lp02);			
		//		stateEditText=(EditText) findViewById(R.id.etState);
		//		stateEditText.setPadding(10, 0, 0, 0);
		//		stateEditText.setGravity(Gravity.CENTER_VERTICAL);
		//		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/16);
		//		lp5.addRule(RelativeLayout.BELOW, R.id.rlZip);
		//		lp5.setMargins(0, 20, 0, 0);
		//		stateRelativeLayout.setLayoutParams(lp5);

		RelativeLayout emailRelativeLayout=(RelativeLayout) findViewById(R.id.rlEmail);
		email_icon=(ImageView) findViewById(R.id.email_icon);
		email_icon.bringToFront();
		//pass_icon.setImageResource(R.drawable.password);
		email_icon.setLayoutParams(lp02);			
		emailEditText=(EditText) findViewById(R.id.etEmail);
		emailEditText.setTypeface(SignInActivity.stories_typeface);
		emailEditText.setPadding(15, 0, 0, 0);
		emailEditText.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/16);
		lp6.addRule(RelativeLayout.BELOW, R.id.rlZip);
		lp6.setMargins(0, 20, 0, 0);
		emailRelativeLayout.setLayoutParams(lp6);

		registerButton=(Button) findViewById(R.id.buttonRegister);
		registerButton.setTypeface(SignInActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp9 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp9.addRule(RelativeLayout.BELOW, R.id.rlEmail);
		lp9.setMargins(width/7, 30, width/7, 0);
		registerButton.setLayoutParams(lp9);
		registerButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==registerButton.getId()){
			if(connectionDetector.isConnectedToInternet()){
				register();
			}else{
				showCustomToast.showToast(RegistrationActivity.this, "Please check your internet connection.");
			}
		}
	}

	private void register() {
		username=usernameEditText.getText().toString();
		password=passwordEditText.getText().toString();
		MD5Convertor md5Convertor=new MD5Convertor();
		md5Password=md5Convertor.convertToMd5(password);
		Zip=ZipEditText.getText().toString();
		email=emailEditText.getText().toString();
		if(username.equalsIgnoreCase("")||password.equalsIgnoreCase("")||Zip.equalsIgnoreCase("")||email.equalsIgnoreCase("")){
			showCustomToast.showToast(RegistrationActivity.this, "All fields are required.");
		}else{
			if(emailValidator(email) &&!(email.contains(".com.com")) && Zip.length()==5 && username.length()<=20 && password.length()<=10 &&  username.length()>=4 && password.length()>=4 && !password.contains(" ") && isAlphaNumeric(username) && isAlphaNumeric(password)){
				new RegistrationTask().execute();
			}else{
				if(!emailValidator(email)){
					emailEditText.setError("Please enter a valid email address. name@domain.com");
				}
				if((Zip.length()!=5)){
					ZipEditText.setError("Zip-code must be 5 digits.");
				}
				if((username.length()<4) || (username.length()>20)){
					usernameEditText.setError("Username should not be less than 4 & more than 20 characters.");
				}
				if((password.length()<4) || (password.length()>10)){
					passwordEditText.setError("Password should  not be less than 4 & more than 10 characters.");
				}
				if((password.contains(" "))){
					passwordEditText.setError("Password should not contain spaces.");
				}
				if(!isAlphaNumeric(username)){
					usernameEditText.setError("Enter alphabets or numericals or both. Special characters are not allowed.");
				}
				if(!isAlphaNumeric(password)){
					passwordEditText.setError("Enter alphabets or numericals or both. Special characters are not allowed.");
				}
				if((email.contains(".com.com"))){
					emailEditText.setError("Please enter a valid email address. name@domain.com");
				}
			}
		}
	}

	public boolean isAlphaNumeric(String s){
		String pattern= "^[a-zA-Z0-9 ]*$"; 
		if(s.matches(pattern)){
			return true;
		}
		return false;   
	}

	public boolean emailValidator(String email){
		Pattern pattern;
		Matcher matcher;
		final String EMAIL_PATTERN = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(email);
		return matcher.matches();
	}
	class RegistrationTask extends AsyncTask<Void, Void, Void> {
		String userid,logid;
		JSONArray jsonArray,useridJsonArray;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			RegistrationWebservice registrationWebservice=new RegistrationWebservice();
			String jsonResponse = registrationWebservice.userRegistration(username, md5Password.toString(), Zip, email, regId,"APP","","");

			if(jsonResponse!=null){
				//System.out.println("jsonResponse register :: "+jsonResponse);

				//on fail - jsonResponse register :: {"status":"0","statusInfo":"fail","message":"user exists"}
				//success -jsonResponse register :: {"status":"1","statusInfo":"success","userid":149,"assignment_info":[{"assignment_id":"1","assignment_name":"Assignment 1","assignment_date":"01\/15\/2014"}]}
				try {
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
					assignment_img_url=jsonObject.getString("image_url");
					if(status.equalsIgnoreCase("1")){
						useridJsonArray =jsonObject.getJSONArray("ids");
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
					Intent i=new Intent(RegistrationActivity.this, CaptureVideoActivity.class);
					startActivity(i);
					finish();
					showCustomToast.showToast(RegistrationActivity.this,"Registration Successful.");
				}else if(status.equalsIgnoreCase("0")){
					showCustomToast.showToast(RegistrationActivity.this,"Email already exists.");
				}
			}
		}
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog.isShowing())
			mProgressDialog.dismiss();
	}
}
