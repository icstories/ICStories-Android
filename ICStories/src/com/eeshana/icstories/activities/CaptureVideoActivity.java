package com.eeshana.icstories.activities;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eeshana.icstories.R;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.GetBitmapTask;
import com.eeshana.icstories.common.ShowCustomToast;
import com.eeshana.icstories.webservices.AssignmentWebservice;
import com.eeshana.icstories.webservices.RegistrationWebservice;

public class CaptureVideoActivity extends Activity implements OnClickListener{
	ImageView logoImageView,assignmentPictureImageView,closeImageView,zoomedImageView;
	RelativeLayout assignmentRelativeLayout,zoomedImageRelativeLayout;
	TextView dateTextView, assignmentTextView,linkTextView,assignmentTextView2,linkTextView2,txtTranslatedText,assignmentTextView2Translated;
	RelativeLayout recordRelativeLayout,uploadRelativeLayout;
	Button recordButton,selectVideoButton,uploadButton;
	int width,height;
	private String videoPath =null;
	String userid;
	SharedPreferences login_prefs;
	private static final int REQUEST_VIDEO_CAPTURED = 0;
	private static final int SELECT_VIDEO = 1;
	private static final int SELECT_VIDEO_KITKAT= 2;
	RelativeLayout homeRelativeLayout,wallRelativeLayout,settingsRelativeLayout;
	// assignment text is compulsary, image & link are optional
	RelativeLayout assignmentWithImageRelativeLayout; // When  assignment contains assignment text & image, no link
	RelativeLayout assignmentWithLinkRelativeLayout;  // When  assignment contains assignment link & image, no image
	TextView assignTextView,homeTextView,wallTextView,settingsTextView,videoDetailsTextView;//iCTextView,storiesTextView,watsStoryTextView;
	ShowCustomToast showCustomToast;
	ConnectionDetector connectionDetector;
	String assignment_id,assignment_date,assignment_img_url,assignment_img_name;
	String assignment_text="";
	String assignment_image="";
	String link="";
	String assignmentTranslated = "";
	Intent intent;
	String extension;
	long minutes;
	Dialog alertDialog;
	Button okButton;
	public static int alreadyUploading=0;
	public static Typeface stories_typeface;
	Animation anim = new AlphaAnimation(0.0f, 1.0f);
	Bitmap bm_image=null;
	//RelativeLayout.LayoutParams lp0;
	//fb 
	String  fb_email,fb_token,fb_username,fb_zip;
	String username,password,regId,status;
	ProgressDialog mProgressDialog;
	public int fb_flag;
	

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.capture_video_activity);
		intent=getIntent();
		height=SignInActivity.getScreenHeight(CaptureVideoActivity.this);
		width=SignInActivity.getScreenWidth(CaptureVideoActivity.this);
		connectionDetector=new ConnectionDetector(CaptureVideoActivity.this);
		mProgressDialog= new ProgressDialog(CaptureVideoActivity.this);
		showCustomToast=new ShowCustomToast();
		anim.setDuration(50); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.ABSOLUTE);
		anim.setRepeatCount(Animation.ABSOLUTE);

		stories_typeface=Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-lt-com-55-roman.ttf");

		zoomedImageRelativeLayout=(RelativeLayout) findViewById(R.id.rlZoomedImage);
		zoomedImageView=(ImageView) findViewById(R.id.ivZoomedImage);
		closeImageView=(ImageView) findViewById(R.id.ivClose);
		closeImageView.bringToFront();
		closeImageView.setOnClickListener(this);

		assignmentPictureImageView=(ImageView) findViewById(R.id.ivAssignment);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams((int) (width/4), (int) (width/4));
		lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		lp.addRule(RelativeLayout.BELOW,R.id.tvDate);
		lp.setMargins(10, 0, 0, 0);
		assignmentPictureImageView.setLayoutParams(lp);
		assignmentPictureImageView.setOnClickListener(this);

		login_prefs=getSharedPreferences("LOGIN",0);
		login_prefs.edit().putInt("isloggedin", 1).commit();
		SharedPreferences prefs = getSharedPreferences("DB", 0);
		userid=prefs.getString("userid", "1");
		fb_flag= prefs.getInt("fb_flag", 0);

		assignmentRelativeLayout=(RelativeLayout) findViewById(R.id.rlAssignment);
		assignmentRelativeLayout.setPadding(0, 0, 0, 10);
		RelativeLayout.LayoutParams lpAssign = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, (int) (width/1.7));
		lpAssign.addRule(RelativeLayout.CENTER_HORIZONTAL);
		lpAssign.addRule(RelativeLayout.BELOW,R.id.rlLogo);
		lpAssign.setMargins(0, 25, 0, 0);
		assignmentRelativeLayout.setLayoutParams(lpAssign);

		assignmentWithImageRelativeLayout=(RelativeLayout) findViewById(R.id.rlAssignWithImage);
		assignmentWithLinkRelativeLayout=(RelativeLayout) findViewById(R.id.rlAssignWithLink);

		txtTranslatedText = (TextView) findViewById(R.id.tvAssignmentTranslated);
		assignmentTextView=(TextView) findViewById(R.id.tvAssignment);
		assignmentTextView.setTypeface(stories_typeface);
		dateTextView=(TextView) findViewById(R.id.tvDate);
		dateTextView.setTypeface(stories_typeface,Typeface.BOLD);
		videoDetailsTextView=(TextView) findViewById(R.id.tvVideoDetails);
		videoDetailsTextView.setTypeface(stories_typeface);
		assignTextView=(TextView) findViewById(R.id.tv1);
		assignTextView.setTypeface(stories_typeface,Typeface.BOLD);
		linkTextView=(TextView) findViewById(R.id.tvLink);
		linkTextView.setTypeface(stories_typeface,Typeface.NORMAL);
		linkTextView.setOnClickListener(this);

		assignmentTextView2=(TextView) findViewById(R.id.tvOnlyAssignment);
		assignmentTextView2.setTypeface(stories_typeface);
		assignmentTextView2Translated = (TextView) findViewById(R.id.tvOnlyAssignmentTranslated);
		assignmentTextView2Translated.setTypeface(stories_typeface);

		linkTextView2=(TextView) findViewById(R.id.tvLink2);
		linkTextView2.setTypeface(stories_typeface,Typeface.NORMAL);
		linkTextView2.setOnClickListener(this);

		if(fb_flag==1){
			SharedPreferences prefs2 = getSharedPreferences("PREF",MODE_PRIVATE);
			regId=prefs2.getString("regid", "NA");
			fb_email=	prefs.getString("fb_email", "");
			fb_token=	prefs.getString("fb_token", "");
			fb_username=	prefs.getString("fb_username", "");
			fb_zip=	prefs.getString("fb_zip", "");
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
				new RegistrationTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, fb_email,fb_token,fb_username,fb_zip);
			}else{
				new RegistrationTask().execute(fb_email,fb_token,fb_username,fb_zip); 
			}
		}else {
			SharedPreferences assign_prefs = getSharedPreferences("ASSIGN", 0);
			assignment_id=assign_prefs.getString("ASSIGN_ID","");
			assignment_text=assign_prefs.getString("ASSIGN_TEXT","");
			assignmentTranslated = assign_prefs.getString("TRANSLATED_TEXT","");
			assignment_date=assign_prefs.getString("ASSIGN_DATE","");
			link=assign_prefs.getString("ASSIGN_LINK","");
			assignment_image=assign_prefs.getString("ASSIGN_IMAGE","");

			if(assignment_image.equals("")){
				assignmentWithImageRelativeLayout.setVisibility(View.GONE);
				assignmentWithLinkRelativeLayout.setVisibility(View.VISIBLE);
				if(assignment_text.equals("")){
					assignmentTextView2.setText(Html.fromHtml("&ldquo; No assignment added. &rdquo;"));
				}else{
					assignmentTextView2.setText(Html.fromHtml("&ldquo; "+assignment_text+" &rdquo;"));
					if(assignmentTranslated.length()==0){
						assignmentTextView2Translated.setVisibility(View.GONE);
					}else{
						assignmentTextView2Translated.setVisibility(View.VISIBLE);
						assignmentTextView2Translated.setText("\" "+assignmentTranslated+"\" ");
					}
				}
				if(link.equals("")){
					linkTextView2.setVisibility(View.GONE);
				}else{
					linkTextView2.setVisibility(View.VISIBLE);
					linkTextView2.setText(link);
				}
			}else{
				assignmentWithImageRelativeLayout.setVisibility(View.VISIBLE);
				assignmentWithLinkRelativeLayout.setVisibility(View.GONE);
				if(assignment_text.equals("")){
					assignmentTextView.setText(Html.fromHtml("&ldquo; No assignment added. &rdquo;"));
				}else{
					assignmentTextView.setText(Html.fromHtml("&ldquo; "+assignment_text+" &rdquo;"));
					if(assignmentTranslated.length()==0){
						txtTranslatedText.setVisibility(View.GONE);
					}else{
						txtTranslatedText.setVisibility(View.VISIBLE);
						txtTranslatedText.setText("\" "+assignmentTranslated+"\" ");
					}
				}
				if(link.equals("")){
					linkTextView.setVisibility(View.GONE);
				}else{
					linkTextView.setVisibility(View.VISIBLE);
					linkTextView.setText(link);
				}
				try {
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
						bm_image =new GetBitmapTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,assignment_image).get();
					}else{
						bm_image =new GetBitmapTask().execute(assignment_image).get(); //BitmapFactory.decodeStream(url.openConnection().getInputStream());
					}
				}catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
				if(bm_image!=null){
					assignmentPictureImageView.setVisibility(View.VISIBLE);
					assignmentPictureImageView.setImageBitmap(bm_image);
				}
			}

			dateTextView.setText(assignment_date);
		}
		recordButton=(Button) findViewById(R.id.buttonRecord);
		recordButton.setTypeface(stories_typeface,Typeface.BOLD);
		recordRelativeLayout=(RelativeLayout) findViewById(R.id.rlRecord);
		RelativeLayout.LayoutParams lp1 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp1.addRule(RelativeLayout.BELOW, R.id.rlAssignment);
		lp1.setMargins(width/7, 35, width/7, 0);
		recordRelativeLayout.setLayoutParams(lp1);
		recordButton.setOnClickListener(this);

		selectVideoButton=(Button) findViewById(R.id.buttonSelectVideo);
		selectVideoButton.setTypeface(stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp2.addRule(RelativeLayout.BELOW, R.id.rlRecord);
		lp2.setMargins(width/7, 25, width/7, 0);
		selectVideoButton.setLayoutParams(lp2);
		selectVideoButton.setOnClickListener(this);

		uploadButton=(Button) findViewById(R.id.buttonUpload);
		uploadButton.setTypeface(stories_typeface,Typeface.BOLD);
		uploadRelativeLayout=(RelativeLayout) findViewById(R.id.rlUpload);
		RelativeLayout.LayoutParams lp3 = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, height/17);
		lp3.addRule(RelativeLayout.BELOW, R.id.buttonSelectVideo);
		lp3.setMargins(width/7, 25,width/7, 0);
		uploadRelativeLayout.setLayoutParams(lp3);
		uploadButton.setOnClickListener(this);

		//bottom bar

		homeRelativeLayout=(RelativeLayout) findViewById(R.id.rlHome);
		homeRelativeLayout.setBackgroundResource(R.drawable.press_border);
		homeRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		homeTextView=(TextView) homeRelativeLayout.findViewById(R.id.tvHome);
		homeTextView.setTypeface(stories_typeface,Typeface.BOLD);
		homeTextView.setTextColor(Color.parseColor("#fffffe"));
		RelativeLayout.LayoutParams lp4 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp4.addRule(RelativeLayout.ALIGN_LEFT);
		homeRelativeLayout.setLayoutParams(lp4);
		homeRelativeLayout.setOnClickListener(this);

		wallRelativeLayout=(RelativeLayout) findViewById(R.id.rlWall);
		wallRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		wallTextView=(TextView) wallRelativeLayout.findViewById(R.id.tvWall);
		wallTextView.setTypeface(stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp5.addRule(RelativeLayout.RIGHT_OF,R.id.rlHome);
		wallRelativeLayout.setLayoutParams(lp5);
		wallRelativeLayout.setOnClickListener(this);

		settingsRelativeLayout=(RelativeLayout) findViewById(R.id.rlSettings);
		settingsRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		settingsTextView=(TextView) settingsRelativeLayout.findViewById(R.id.tvSettings);
		settingsTextView.setTypeface(stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp6.addRule(RelativeLayout.RIGHT_OF,R.id.rlWall);
		settingsRelativeLayout.setLayoutParams(lp6);
		settingsRelativeLayout.setOnClickListener(this);

		//alert dialog
		alertDialog=new Dialog(CaptureVideoActivity.this);
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.alert_dialog);
		alertDialog.setCancelable(true);
		okButton=(Button) alertDialog.findViewById(R.id.btnOk);
		okButton.setOnClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog!=null && mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
		}
		if(alertDialog!=null && alertDialog.isShowing()){
			alertDialog.dismiss();
		}
	}

	class RegistrationTask extends AsyncTask<String, Void, Void> {
		String userid,logid,fbEmail,fbToken,fbUsername,fbZip;
		JSONArray jsonArray,useridJsonArray;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			//			fbEmail="swapnil@eeshana.com";//params[0];
			//			fbToken="100004928970187";//params[1];
			fbEmail=params[0];
			fbToken=params[1];
			fbUsername=params[2];
			fbZip=params[3];
			RegistrationWebservice registrationWebservice=new RegistrationWebservice();
			String jsonResponse = registrationWebservice.userRegistration("","",fbZip, fbEmail, regId,"FB",fbToken,fbUsername);

			if(jsonResponse!=null){
				System.out.println("FB login "+jsonResponse);
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
			SignInActivity.opening_first_time=1;
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
					editor2.putString("TRANSLATED_TEXT", assignmentTranslated);
					editor2.putString("ASSIGN_DATE", assignment_date);
					editor2.putString("ASSIGN_LINK", link);
					editor2.putString("ASSIGN_IMAGE", assignment_image);
					editor2.commit(); 
					dateTextView.setText(assignment_date);

					if(assignment_image.equals("")){
						assignmentWithImageRelativeLayout.setVisibility(View.GONE);
						assignmentWithLinkRelativeLayout.setVisibility(View.VISIBLE);
						if(assignment_text.equals("")){
							assignmentTextView2.setText(Html.fromHtml("&ldquo; No assignment added. &rdquo;"));
						}else{
							assignmentTextView2.setText(Html.fromHtml("&ldquo;"+assignment_text+"&rdquo;"));
							if(assignmentTranslated.length()==0){
								assignmentTextView2Translated.setVisibility(View.GONE);
							}else{
								assignmentTextView2Translated.setVisibility(View.VISIBLE);
								assignmentTextView2Translated.setText("\" "+assignmentTranslated+"\" ");
							}
						}
						if(link.equals("")){
							linkTextView2.setVisibility(View.GONE);
						}else{
							linkTextView2.setVisibility(View.VISIBLE);
							linkTextView2.setText(link);
						}

					}else{
						assignmentWithImageRelativeLayout.setVisibility(View.VISIBLE);
						assignmentWithLinkRelativeLayout.setVisibility(View.GONE);
						if(assignment_text.equals("")){
							assignmentTextView.setText(Html.fromHtml("&ldquo; No assignment added. &rdquo;"));
						}else{
							assignmentTextView.setText(Html.fromHtml("&ldquo;"+assignment_text+"&rdquo;"));
							if(assignmentTranslated.length()==0){
								txtTranslatedText.setVisibility(View.GONE);
							}else{
								txtTranslatedText.setVisibility(View.VISIBLE);
								txtTranslatedText.setText("\" "+assignmentTranslated+"\" ");
							}
						}
						if(link.equals("")){
							linkTextView.setVisibility(View.GONE);
						}else{
							linkTextView.setVisibility(View.VISIBLE);
							linkTextView.setText(link);
						}
						try {
							bm_image =new GetBitmapTask().execute(assignment_image).get(); //BitmapFactory.decodeStream(url.openConnection().getInputStream());
						}catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						if(bm_image!=null){
							assignmentPictureImageView.setVisibility(View.VISIBLE);
							assignmentPictureImageView.setImageBitmap(bm_image);
						}
					}
					showCustomToast.showToast(CaptureVideoActivity.this, "Welcome "+fbUsername+" You are logged in with FB.");
				}else if(status.equalsIgnoreCase("0")){
					showCustomToast.showToast(CaptureVideoActivity.this, "Could not log in. Please try again later.");
					Intent i=new Intent(CaptureVideoActivity.this, SignInActivity.class);
					startActivity(i);
					CaptureVideoActivity.this.finish();
				}

			}else{
				showCustomToast.showToast(CaptureVideoActivity.this, "Could not log in. Please try again later.");
				Intent i=new Intent(CaptureVideoActivity.this, SignInActivity.class);
				startActivity(i);
				CaptureVideoActivity.this.finish();
			}
		}
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==recordButton.getId()){
			
			final Intent intent = new Intent(this,CameraActivity.class);
			startActivityForResult(intent, REQUEST_VIDEO_CAPTURED);
			
		}else if(v.getId()==selectVideoButton.getId()){
			if(alreadyUploading==0){
				if (Build.VERSION.SDK_INT <19){
					Intent intent = new Intent(); 
					intent.setType("video/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,"Complete action using"), SELECT_VIDEO);
				} else {
					Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
					intent.addCategory(Intent.CATEGORY_OPENABLE);
					intent.setType("video/*");
					startActivityForResult(intent, SELECT_VIDEO_KITKAT);
				}
			}else{
				showCustomToast.showToast(CaptureVideoActivity.this, "Uploading is in progress.");
			}
		}else if(v.getId()==uploadButton.getId()){
			if(connectionDetector.isConnectedToInternet()){
				if(videoPath!=null){
					if(alreadyUploading==0){
						if(minutes<=5){
							videoDetailsTextView.setVisibility(View.GONE);
							Intent i=new Intent(CaptureVideoActivity.this,UploadVideoActivity.class);
							i.putExtra("VIDEOPATH", videoPath);
							i.putExtra("ASSIGN_ID", assignment_id);
							i.putExtra("EXTENSION", extension);
							i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
							startActivity(i);
							finish(); 
						}else{
							showCustomToast.showToast(CaptureVideoActivity.this, "You can upload video up to 5 minutes in duration.");
						}
					}else{
						showCustomToast.showToast(CaptureVideoActivity.this, "Uploading is in progress.");
					}
				}else{
					showCustomToast.showToast(CaptureVideoActivity.this, "Please record or select a video to upload");
				}
			}else{
				showCustomToast.showToast(CaptureVideoActivity.this, "Please check your internet connection.");
			}
		}else if(v.getId()==wallRelativeLayout.getId()){
			Intent i=new Intent(CaptureVideoActivity.this,WallActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}else if(v.getId()==settingsRelativeLayout.getId()){
			Intent i=new Intent(CaptureVideoActivity.this,SettingsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}else if(v.getId()==okButton.getId()){
			Intent i=new Intent(CaptureVideoActivity.this, SignInActivity.class);
			startActivity(i);
			CaptureVideoActivity.this.finish();
		}else if(v.getId()==linkTextView.getId()){
			String url=linkTextView.getText().toString();
			linkTextView.startAnimation(anim);
			if(url!=null &&  URLUtil.isValidUrl(url)){
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}else{
				if(URLUtil.isValidUrl(url))
					showCustomToast.showToast(CaptureVideoActivity.this,"Invalid address.");
			}
		}else if(v.getId()==linkTextView2.getId()){
			String url=linkTextView2.getText().toString();
			linkTextView2.startAnimation(anim);
			if(url!=null &&  URLUtil.isValidUrl(url)){
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
				startActivity(browserIntent);
			}else{
				if(URLUtil.isValidUrl(url))
					showCustomToast.showToast(CaptureVideoActivity.this,"Invalid address.");
			}
		}else if(v.getId()==assignmentPictureImageView.getId()){
			if(bm_image!=null){
				zoomedImageView.setImageBitmap(bm_image);
			}
			zoomedImageRelativeLayout.setVisibility(View.VISIBLE);
		}else if(v.getId()==closeImageView.getId()){
			zoomedImageRelativeLayout.setVisibility(View.GONE);
		}
	}

	public static int getVideoOrientation(String videoPath){
		int rotate = 0;
		try {
			File videoFile = new File(videoPath);
			ExifInterface exif = new ExifInterface(
					videoFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_270:
				rotate = 270;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				rotate = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_90:
				rotate = 90;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rotate;
	}

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		String vid_duration=null;
		String vid_size=null;

		if(resultCode==RESULT_OK){
			if (requestCode == REQUEST_VIDEO_CAPTURED) {
			
				String path = data.getStringExtra("videopath");
				if(path != null)
				{
					videoPath = path;
					int duration = data.getIntExtra("duration", 0)*1000;
					vid_duration= String.format("%d min : %d sec", 
							TimeUnit.MILLISECONDS.toMinutes(duration),
							TimeUnit.MILLISECONDS.toSeconds(duration) - 
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
							);
					minutes=TimeUnit.MILLISECONDS.toMinutes(duration);
					
					final File f = new File(path);
					String filename = "";
					extension = "";
					long size = 0 ;
					if(f!= null)
					{
						size = f.length();
						filename = f.getName();
					}
					
					double step = size/1024/1024;
					vid_size= String.format("%3.1f %s", step, "MB");
					videoDetailsTextView.setText("Recorded video details - Path : "+videoPath+" , Duration : "+vid_duration+" , Size : "+vid_size);
					videoDetailsTextView.setVisibility(View.VISIBLE);
					
					
					
				}else{
					Toast.makeText(this, "Some Error occured while recording video!", Toast.LENGTH_SHORT).show();
				}
				
			
			}else if (requestCode == SELECT_VIDEO) {
				Uri selectedVideoUri = data.getData();
				videoPath = getRealPathFromURI(selectedVideoUri);
				Cursor cursor = MediaStore.Video.query(getContentResolver(), selectedVideoUri, new String[]{MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.SIZE,MediaStore.Video.VideoColumns.DISPLAY_NAME,MediaStore.Video.VideoColumns.MINI_THUMB_MAGIC});
				if(cursor.moveToFirst()) {
					int duration = Integer.parseInt(cursor.getString(0)); 
					vid_duration= String.format("%d min : %d sec", 
							TimeUnit.MILLISECONDS.toMinutes(duration),
							TimeUnit.MILLISECONDS.toSeconds(duration) - 
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
							);
					minutes=TimeUnit.MILLISECONDS.toMinutes(duration);
					int size=Integer.parseInt(cursor.getString(1)); 
					double step = size/1024/1024;
					vid_size= String.format("%3.1f %s", step, "MB");

					String filename = cursor.getString(2);
					String filenameArray[] = filename.split("\\.");
					extension = filenameArray[filenameArray.length-1];
					String thumb_image = (cursor.getString(3)); 
					System.out.println("THUMB IMG  == "+thumb_image);
					cursor.close();
				}
				videoDetailsTextView.setText("Selected video details :  "+videoPath+" \n  Duration : "+vid_duration+" , Size : "+vid_size);
				videoDetailsTextView.setVisibility(View.VISIBLE);
			}else if (requestCode == SELECT_VIDEO_KITKAT) {
				Uri selectedVideoUri = data.getData();
				final int takeFlags = data.getFlags()
						& (Intent.FLAG_GRANT_READ_URI_PERMISSION
								| Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
				ContentResolver cr= getContentResolver();
				cr.takePersistableUriPermission(selectedVideoUri, takeFlags);

				String id = selectedVideoUri.getLastPathSegment().split(":")[1]; 
				Uri uri = getUri();
				Uri finalSuccessfulUri = Uri.withAppendedPath(uri, ""+ id );
				videoPath=getRealPathFromURI(finalSuccessfulUri);

				Cursor cursor = MediaStore.Video.query(cr, finalSuccessfulUri, new String[]{MediaStore.Video.VideoColumns.DURATION,MediaStore.Video.VideoColumns.SIZE,MediaStore.Video.VideoColumns.DISPLAY_NAME});

				if(cursor!=null && cursor.moveToFirst()) {
					int duration = Integer.parseInt(cursor.getString(0)); 
					vid_duration= String.format("%d min : %d sec", 
							TimeUnit.MILLISECONDS.toMinutes(duration),
							TimeUnit.MILLISECONDS.toSeconds(duration) - 
							TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration))
							);
					minutes=TimeUnit.MILLISECONDS.toMinutes(duration);
					int size=Integer.parseInt(cursor.getString(1)); 
					double step = size/1024/1024;
					vid_size= String.format("%3.1f %s", step, "MB");

					String filename = cursor.getString(2);
					String filenameArray[] = filename.split("\\.");
					extension = filenameArray[filenameArray.length-1];

					cursor.close();
				}else{
				}
				videoDetailsTextView.setText("Selected video details :  "+videoPath+" \n  Duration : "+vid_duration+" , Size : "+vid_size);
				videoDetailsTextView.setVisibility(View.VISIBLE);
			}      
		}
	}
	private Uri getUri() {
		String state = Environment.getExternalStorageState();
		if(!state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
			return MediaStore.Video.Media.INTERNAL_CONTENT_URI;

		return MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
	}

	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}


	@SuppressLint("NewApi")
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			new updateAssignmentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		else
			new updateAssignmentTask().execute();
	}

	@SuppressLint("NewApi")
	@Override
	protected void onResume() {
		super.onResume();
		if(getIntent().hasExtra("From Notification")){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				new updateAssignmentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
			else
				new updateAssignmentTask().execute();
		}else{
		}
	}

	class updateAssignmentTask extends AsyncTask<String, Void, Void> {
		JSONArray jsonArray;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog.setMessage("Please wait...");
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(String... params) {
			AssignmentWebservice assignmentWebservice=new AssignmentWebservice();
			try {
				String jsonResponse = assignmentWebservice.updateAssignment();
				
				if(jsonResponse!=null){
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
					assignment_img_url=jsonObject.getString("image_url");
					if(status.equalsIgnoreCase("1")){
						jsonArray=jsonObject.getJSONArray("assignment_info");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject menuObject = jsonArray.getJSONObject(i);
							assignment_id=menuObject.getString("assignment_id");
							assignment_text=menuObject.getString("assignment_name");
							assignmentTranslated = StringEscapeUtils.unescapeXml(StringEscapeUtils.unescapeHtml(menuObject.getString("translated_name")));
							assignment_date=menuObject.getString("assignment_date");
							assignment_img_name=menuObject.getString("assignment_file");
							link=menuObject.getString("assignment_url");
						}
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mProgressDialog.hide();
			
			if(status!=null){
				if(status .equalsIgnoreCase("1")){
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
					editor2.putString("TRANSLATED_TEXT", assignmentTranslated);
					editor2.putString("ASSIGN_DATE", assignment_date);
					editor2.putString("ASSIGN_LINK", link);
					editor2.putString("ASSIGN_IMAGE", assignment_image);
					editor2.commit(); 
					dateTextView.setText(assignment_date);

					if(assignment_image.equals("")){
						assignmentWithImageRelativeLayout.setVisibility(View.GONE);
						assignmentWithLinkRelativeLayout.setVisibility(View.VISIBLE);
						if(assignment_text.equals("")){
							assignmentTextView2.setText(Html.fromHtml("&ldquo; No assignment added. &rdquo;"));
							assignmentTextView2Translated.setVisibility(View.GONE);
						}else{
							assignmentTextView2.setText(Html.fromHtml("&ldquo;"+assignment_text+"&rdquo;"));
							
							if(assignmentTranslated.length()==0){
								assignmentTextView2Translated.setVisibility(View.GONE);
								
							}else{
								assignmentTextView2Translated.setVisibility(View.VISIBLE);
								
								assignmentTextView2Translated.setText("\" "+assignmentTranslated+"\" ");
							}
							
							
							
						}
						if(link.equals("")){
							linkTextView2.setVisibility(View.GONE);
						}else{
							linkTextView2.setVisibility(View.VISIBLE);
							linkTextView2.setText(link);
						}
					}else{
						assignmentWithImageRelativeLayout.setVisibility(View.VISIBLE);
						assignmentWithLinkRelativeLayout.setVisibility(View.GONE);
						if(assignment_text.equals("")){
							assignmentTextView.setText(Html.fromHtml("&ldquo; No assignment added. &rdquo;"));
							txtTranslatedText.setVisibility(View.GONE);
						}else{
							assignmentTextView.setText(Html.fromHtml("&ldquo;"+assignment_text+"&rdquo;"));
							txtTranslatedText.setVisibility(View.VISIBLE);
							if(assignmentTranslated.length()==0){
								txtTranslatedText.setVisibility(View.GONE);
							}else{
								txtTranslatedText.setVisibility(View.VISIBLE);
								
								txtTranslatedText.setText("\" "+assignmentTranslated+"\" ");
							}
							
						}
						if(link.equals("")){
							linkTextView.setVisibility(View.GONE);
						}else{
							linkTextView.setVisibility(View.VISIBLE);
							linkTextView.setText(link);
						}
						try {
							bm_image =new GetBitmapTask().execute(assignment_image).get();
						}catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						if(bm_image!=null){
							assignmentPictureImageView.setVisibility(View.VISIBLE);
							assignmentPictureImageView.setImageBitmap(bm_image);
						}
					}
					showCustomToast.showToast(CaptureVideoActivity.this, "Your assignment is updated.");
				}else if(status.equalsIgnoreCase("0")){
					showCustomToast.showToast(CaptureVideoActivity.this, "Sorry, could not update assignment.");
				}
			}else{
			}
		}
	}
}
