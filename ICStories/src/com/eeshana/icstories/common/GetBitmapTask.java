package com.eeshana.icstories.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;


public class GetBitmapTask extends AsyncTask<String, Void, Bitmap>{

	public GetBitmapTask() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		Bitmap myBitmap=null;
		 try {
		        URL url = new URL(params[0]);
		        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		        connection.setDoInput(true);
		        connection.connect();
		        InputStream input = connection.getInputStream();
		        myBitmap = BitmapFactory.decodeStream(input);
		        return myBitmap;
		    } catch (IOException e) {
		        e.printStackTrace();
		        return myBitmap;
		    }
	}

	protected void onPostExecute(Bitmap result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
}
