package com.eeshana.icstories.activities;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eeshana.icstories.R;
import com.eeshana.icstories.adapters.ArrayAdapter_for_wall;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.MyVideos;
import com.eeshana.icstories.common.ShowCustomToast;
import com.eeshana.icstories.common.CustomProgressDialog;
import com.eeshana.icstories.webservices.WallWebservice;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class WallActivity extends Activity implements OnClickListener {
	int height,width;
	RelativeLayout homeRelativeLayout,wallRelativeLayout,settingsRelativeLayout;
	TextView homeTextView,wallTextView,settingsTextView;
	ListView wallListView;
	ArrayAdapter_for_wall arrayAdapter_for_wall;
	ArrayList<MyVideos> list=new ArrayList<MyVideos>();
	MyVideos myVideos;
	ProgressDialog mProgressDialog;
	ShowCustomToast showCustomToast;
	ConnectionDetector connectionDetector;
	String userid,username,thumb_url,video_url,video_title,video_description,date,location,video_id;
	SharedPreferences prefs;
	protected ImageLoader imageLoader;
	String start="0";
	int prog;
	int mScrollState = OnScrollListener.SCROLL_STATE_IDLE;
	CustomProgressDialog customProgressDialog;
	ImageView logoImageView;
	//TextView iCTextView,storiesTextView,watsStoryTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.wall_activity);
		height=SignInActivity.getScreenHeight(WallActivity.this);
		width=SignInActivity.getScreenWidth(WallActivity.this); 
		mProgressDialog=new ProgressDialog(WallActivity.this);
		customProgressDialog = new CustomProgressDialog(this, R.drawable.spinner);
		showCustomToast=new ShowCustomToast();
		connectionDetector=new ConnectionDetector(WallActivity.this);
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		prefs = getSharedPreferences("DB",0);
		userid=prefs.getString("userid", "1");

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

		wallListView=(ListView) findViewById(R.id.lvVideos);

		//@TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
		if(connectionDetector.isConnectedToInternet()){
			if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				new WallTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, start);
			else
				new WallTask().execute(start);

		}else{
			showCustomToast.showToast(WallActivity.this, "Please check your internet connection.");
		}

		wallListView.setOnScrollListener(new OnScrollListener() {
			private int currentFirstVisibleItem;
			private int currentVisibleItemCount;
			private int currentScrollState;
			private int currentLastVisibleItem;
			@Override
			public void onScrollStateChanged(final AbsListView view, final int scrollState) {
				mScrollState = scrollState;
				this.currentScrollState = scrollState;
				this.isScrollCompleted();
			}

			@Override
			public void onScroll(final AbsListView view, final int firstVisibleItem,
					final int visibleItemCount, final int totalItemCount) {
				this.currentFirstVisibleItem = firstVisibleItem;
				this.currentVisibleItemCount = visibleItemCount;
			}
			private void isScrollCompleted() {
				prog=0;
				if (this.currentVisibleItemCount > 0 && this.currentScrollState == SCROLL_STATE_IDLE) {
					/*** In this way I detect if there's been a scroll which has completed ***/
					/*** do the work for load more date! ***/
					currentLastVisibleItem = currentFirstVisibleItem+currentVisibleItemCount;  // this will be start count to be set
					if(currentLastVisibleItem==1){
					}else if(currentLastVisibleItem==list.size() && list.size()==2){
						//when only 2 videos are listed, show both but don't take any action on scroll
					}else if(currentLastVisibleItem==list.size() && list.size()>1){
						start=String.valueOf(currentLastVisibleItem);
						loadMoreData();
					}
				}
			}

			private void loadMoreData() {
				prog=1;
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					new WallTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, start);
				else
					new WallTask().execute(start);
			}
		});

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
		wallRelativeLayout.setBackgroundResource(R.drawable.press_border);
		wallRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		wallTextView=(TextView) wallRelativeLayout.findViewById(R.id.tvWall);
		wallTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp5 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp5.addRule(RelativeLayout.RIGHT_OF,R.id.rlHome);
		wallRelativeLayout.setLayoutParams(lp5);
		//wallRelativeLayout.setOnClickListener(this);

		settingsRelativeLayout=(RelativeLayout) findViewById(R.id.rlSettings);
		settingsRelativeLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
		settingsTextView=(TextView) settingsRelativeLayout.findViewById(R.id.tvSettings);
		settingsTextView.setTypeface(CaptureVideoActivity.stories_typeface,Typeface.BOLD);
		RelativeLayout.LayoutParams lp6 = new RelativeLayout.LayoutParams(width/3, width/5);
		lp6.addRule(RelativeLayout.RIGHT_OF,R.id.rlWall);
		settingsRelativeLayout.setLayoutParams(lp6);
		settingsRelativeLayout.setOnClickListener(this);

	}
	@Override
	public void onClick(View v) {
		if(v.getId()==settingsRelativeLayout.getId()){
			Intent i=new Intent(WallActivity.this,SettingsActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}else if(v.getId()==homeRelativeLayout.getId()){
			Intent i=new Intent(WallActivity.this,CaptureVideoActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
			startActivity(i);
			finish();
		}
	}
	class WallTask extends AsyncTask<String, Void, Void> {
		String status,thumb_u,video_u;
		JSONArray jsonArray;
		String start="0";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if(prog==1)
			{
				customProgressDialog.show();
			}else{
				mProgressDialog.setMessage("Please wait...");
				mProgressDialog.show();
			}
		}

		@Override
		protected Void doInBackground(String... params) {

			start=params[0];
			WallWebservice wallWebservice=new WallWebservice();
			String jsonResponse = wallWebservice.userWall(userid,start);
			//http://www.bizmoapps.com/icstories/uploads/video 
			//	www.bizmoapps.com/icstories/uploads/video/thumb  
			if(jsonResponse!=null){
				System.out.println("jsonResponse Wall :: "+jsonResponse);
				Log.e("json", jsonResponse);
				//on fail - jsonResponse Login :: {"status":"0","statusInfo":"fail","userid":null}
				//on success : {"status":"1","statusInfo":"success","wall_info":[{"user_id":"120","username":"aarti","video_thumb":"52d3cfba81b57thumbnail.png","video_data":"52d3cfba7ffddvid_20140109_132150.3gp","video_title":"kk","video_description":"kkk","assignment_date":"2014-01-13 05:06:26","location":"Pune"},{"user_id":"120","username":"aarti","video_thumb":"52d3d19fc96b1thumbnail.png","video_data":"52d3d19fc8419vid_20140106_152333.3gp","video_title":"oo","video_description":"ooo","assignment_date":"2014-01-13 05:14:31","location":"Pune"}]}
				//"video_url":"http:\/\/bizmoapps.com\/icstories\/uploads\/video","video_thumb":"http:\/\/bizmoapps.com\/icstories\/uploads\/video\/thumb"
				try {
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
					jsonArray=jsonObject.getJSONArray("wall_info");
					thumb_u=jsonObject.getString("video_thumb_url");
					video_u=jsonObject.getString("video_url");
					if(jsonArray.length()>0){
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject menuObject = jsonArray.getJSONObject(i);
							//	userid=menuObject.getString("user_id");
							//	username=menuObject.getString("username");
							thumb_url=thumb_u+"/"+menuObject.getString("video_thumb");
							video_url=video_u+"/"+menuObject.getString("video_data");
							video_title=menuObject.getString("video_title");
							video_description=menuObject.getString("video_description");
							video_id=menuObject.getString("video_id");
							date=menuObject.getString("assignment_date"); 
							//2014-01-13 05:06:26
							// first split with " " & then with "-" 
							//						String aDate[] = date.split(" ");  
							//						String bDate=aDate[0];
							//						String splittedDate[] = bDate.split("-");  
							//						String Day=splittedDate[2];
							//						String Month=splittedDate[1];
							//						String Year=splittedDate[0];
							//
							//						String final_date = Month+"/"+Day+"/"+Year;

							location=menuObject.getString("location");
							myVideos=new MyVideos(video_title, video_url, thumb_url, date, location, video_description,video_id);
							list.add(myVideos);
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
			customProgressDialog.hide();
			if(status!=null){
				if(status .equalsIgnoreCase("1")){
					if(jsonArray.length()>0){
						arrayAdapter_for_wall=new ArrayAdapter_for_wall(WallActivity.this,0,list);
						scrollMyListViewToBottom();
						arrayAdapter_for_wall.notifyDataSetChanged();
						wallListView.setAdapter(arrayAdapter_for_wall);
					}else{
						if(start.equalsIgnoreCase("0")){
							showCustomToast.showToast(WallActivity.this,"You've not uploaded any video.");
						}else{
							showCustomToast.showToast(WallActivity.this,"No more videos.");
						}
					}
				}else if(status.equalsIgnoreCase("0")){
					//Toast.makeText(getApplicationContext(), "Invalid username or password ", Toast.LENGTH_LONG).show();
					showCustomToast.showToast(WallActivity.this,"Please try again.");
				}
			}else{
			}
		}
	}

	private void scrollMyListViewToBottom() {
		wallListView.post(new Runnable() { 
			@Override
			public void run() {
				// Select the last row so it will scroll into view...
				wallListView.setSelection(Integer.parseInt(start));
			}
		});
	}
	@Override
	protected void onPause() {
		super.onPause();
		if(mProgressDialog.isShowing())
			mProgressDialog.dismiss();
		if(customProgressDialog.isShowing())
			customProgressDialog.dismiss();
	}

}