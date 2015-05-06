package com.eeshana.icstories.adapters;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.eeshana.icstories.R;
import com.eeshana.icstories.activities.CaptureVideoActivity;
import com.eeshana.icstories.activities.WallActivity;
import com.eeshana.icstories.common.ConnectionDetector;
import com.eeshana.icstories.common.MyVideos;
import com.eeshana.icstories.common.ShowCustomToast;
import com.eeshana.icstories.webservices.WallWebservice;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ArrayAdapter_for_wall extends ArrayAdapter<MyVideos>{
	Activity context;
	ArrayList<MyVideos> list=new ArrayList<MyVideos>();
	DisplayImageOptions options;
	ConnectionDetector connectionDetector;
	ShowCustomToast showCustomToast;
	Animation anim = new AlphaAnimation(0.0f, 1.0f);
	Dialog alertDialog;
	Button yesButton,noButton;
	int pos;

	public ArrayAdapter_for_wall(Activity context, int resource, ArrayList<MyVideos> list) {
		super(context, 0);
		this.context=context;
		this.list=list;
		options = new DisplayImageOptions.Builder()
		.showImageForEmptyUri(R.drawable.empty)
		.showImageOnFail(R.drawable.empty)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.bitmapConfig(Bitmap.Config.RGB_565)					
		.build();
		connectionDetector=new ConnectionDetector(context);
		showCustomToast=new ShowCustomToast();
		anim.setDuration(50); //You can manage the time of the blink with this parameter
		anim.setStartOffset(20);
		anim.setRepeatMode(Animation.ABSOLUTE);
		anim.setRepeatCount(Animation.ABSOLUTE);
		alertDialog=new Dialog(context);
		alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialog.setContentView(R.layout.delete_dialog);
		alertDialog.setCancelable(true);
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public MyVideos getItem(int position) {
		return list.get(position);
	}

	@SuppressLint("NewApi") @Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ViewContainer viewContainer;
		View rowView = view;

		if (rowView == null) {
			LayoutInflater inflater = context.getLayoutInflater();
			rowView = inflater.inflate(R.layout.wall_list_item, parent, false);

			viewContainer = new ViewContainer();

			viewContainer.titleTextView = (TextView) rowView.findViewById(R.id.tvTitle);
			viewContainer.dateTextView = (TextView) rowView.findViewById(R.id.tvDate);
			viewContainer.locationTextView = (TextView) rowView.findViewById(R.id.tvLocation);
			viewContainer.descriptionTextView = (TextView) rowView.findViewById(R.id.tvDescription);
			viewContainer.videoImageView = (ImageView) rowView.findViewById(R.id.ivVideo);
			viewContainer.playButton= (Button) rowView.findViewById(R.id.btnPlay);
			viewContainer.deleteImageView= (ImageView) rowView.findViewById(R.id.ivDelete);
			rowView.setTag(viewContainer);
		} else {
			viewContainer = (ViewContainer) rowView.getTag();
		}

		viewContainer.titleTextView.setTypeface(CaptureVideoActivity.stories_typeface);
		viewContainer.dateTextView.setTypeface(CaptureVideoActivity.stories_typeface);
		viewContainer.locationTextView.setTypeface(CaptureVideoActivity.stories_typeface);
		viewContainer.descriptionTextView.setTypeface(CaptureVideoActivity.stories_typeface);
		
		viewContainer.titleTextView.setText(list.get(position).getTitle());
		viewContainer.dateTextView.setText(list.get(position).getDate());
		viewContainer.locationTextView.setText(list.get(position).getLocation());
		viewContainer.descriptionTextView.setText(list.get(position).getDescription());
		com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(list.get(position).getThumbPath(),viewContainer.videoImageView, options);

		viewContainer.playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Intent intent = new Intent(Intent.ACTION_VIEW );
				intent.setDataAndType(Uri.parse(list.get(position).getVideoPath()), "video/*");
				context.startActivity(intent);
				
			}
		});
		viewContainer.deleteImageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				viewContainer.deleteImageView.startAnimation(anim);
				pos=position;
				alertDialog.show();
			}
		});

		yesButton=(Button) alertDialog.findViewById(R.id.btnYes);
		yesButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(connectionDetector.isConnectedToInternet()){
					if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
						new DeleteVideoTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list.get(pos).getVideoId());
					else
						new DeleteVideoTask().execute(list.get(pos).getVideoId());
				}else{
					showCustomToast.showToast(context, "Please check your internet connection.");
				}
			}
		});

		noButton=(Button) alertDialog.findViewById(R.id.btnNo);
		noButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}
		});
		return rowView;
	}
	static class ViewContainer {
		public ImageView videoImageView,deleteImageView;
		public TextView titleTextView,dateTextView,locationTextView,descriptionTextView;
		public Button playButton;
	}

	class DeleteVideoTask extends AsyncTask<String, Void, Void> {
		String status,vid_id;
		JSONArray jsonArray;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(String... params) {

			vid_id=params[0];
			WallWebservice wallWebservice=new WallWebservice();
			String jsonResponse = wallWebservice.deleteVideoFromWall(vid_id);
			if(jsonResponse!=null){
				try {
					JSONObject jsonObject = new JSONObject(jsonResponse);
					status = jsonObject.getString("status");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if(status!=null){
				if(status .equalsIgnoreCase("1")){
					showCustomToast.showToast(context,"Video is deleted from your wall.");
					Intent i=new Intent(context, WallActivity.class);
					context.startActivity(i);
					context.finish();
				}else if(status.equalsIgnoreCase("0")){
					showCustomToast.showToast(context,"Please try again.");
				}
			}else{
			}
		}
	}
}
