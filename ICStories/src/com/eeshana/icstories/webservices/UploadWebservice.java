package com.eeshana.icstories.webservices;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

import com.eeshana.icstories.webservices.MyMultipartEntity.ProgressListener;

public class UploadWebservice {
	String response;
	ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
	HttpClient httpClient = new DefaultHttpClient();
	private long totalSize;

	public String uploadVideo(String userid,String videoPath,String title,String description,String flag,String location,String regid,String assign_id) throws ParseException, IOException {
		//url : http://bizmoapps.com/icstories/webservices/users/upload_video
		//i/p : {"userid":"12","video":"video_data","video_title":"fcDGTRBzTjva","video_description":"sdsd","assignment_flag":"YES","device_token":"sdsad"}';

		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/upload_video");
		
		
		//current url
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/upload_video");
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/upload_video");

		FileBody filebodyVideo = new FileBody(new File(videoPath));

		//MultipartEntity reqEntity = new MultipartEntity();
		MultipartEntity reqEntity = new MyMultipartEntity(new ProgressListener() {
			@Override
			public void transferred(long num){
				//Call the onProgressUpdate method with the percent completed
				//  publishProgress((int) ((num / (float) totalSize) * 100));
				Log.d("DEBUG", num + " - " + totalSize);
			}
		});
		reqEntity.addPart("userid",  new StringBody(userid));
		reqEntity.addPart("video", filebodyVideo);
		//	reqEntity.addPart("video_thumb", thumbnail);
		reqEntity.addPart("video_title", new StringBody(title));
		reqEntity.addPart("video_description",  new StringBody(description));
		reqEntity.addPart("assignment_flag", new StringBody(flag));
		reqEntity.addPart("assignment_id", new StringBody(assign_id));
		reqEntity.addPart("location",  new StringBody(location));
		reqEntity.addPart("device_token",  new StringBody(regid));
		//After adding everything we get the content's lenght
		totalSize = reqEntity.getContentLength();
		httpPost.setEntity(reqEntity);

		ResponseHandler<String> mHandler = new BasicResponseHandler();
		//AsyncHttpResponseHandler httpResponseHandler = UploadVideoActivity.createHTTPResponseHandler();
		response = httpClient.execute(httpPost,mHandler);

		return response;
	} // end of uploadVideo( )
}
