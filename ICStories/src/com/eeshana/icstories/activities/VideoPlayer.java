package com.eeshana.icstories.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.widget.Toast;

import com.eeshana.icstories.R;

public class VideoPlayer extends Activity {
	
	private WebView mVideoPlayer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video_player);
		
		mVideoPlayer = (WebView) findViewById(R.id.mwebView_vid_player);
		
		String url = getIntent().getStringExtra("videopath");
		WebSettings webViewSettings = mVideoPlayer.getSettings();
		webViewSettings.setJavaScriptCanOpenWindowsAutomatically(true);
		webViewSettings.setJavaScriptEnabled(true);
		webViewSettings.setBuiltInZoomControls(true);
		webViewSettings.setPluginState(PluginState.ON);
		
		if(url!= null)
		{
		
			initPlayer(url);
			
		}else{
			showErrorMessage();
		}
		
		
	}
	
	private void showErrorMessage()
	{
		Toast.makeText(this, "Couldnt Play Video Invalid Url!", Toast.LENGTH_SHORT).show();

		new Handler().postDelayed(new Runnable() 
		{
			
			@Override
			public void run() {
				
				finish();
				
			}
		}, 3000);
	}
	
	private void initPlayer(String url)
	{
		mVideoPlayer.loadData("<iframe src=\""+url+"\"></iframe>", "text/html",
                "utf-8");
	}

}
