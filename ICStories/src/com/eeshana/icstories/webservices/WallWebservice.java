package com.eeshana.icstories.webservices;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

public class WallWebservice {
	String response;
	ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
	HttpClient httpClient = new DefaultHttpClient();

	public String userWall(String uid, String start) {
		//System.out.println("-- "+uid+"  start -- "+start);
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/user_wall//");
		
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/user_wall");
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("userid", uid);	
			jsonObject.put("start", start);	
			postParams.add(new BasicNameValuePair("wall",jsonObject.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(postParams));
			ResponseHandler<String> mHandler = new BasicResponseHandler();
			response = httpClient.execute(httpPost,mHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return response;
	}

	public String deleteVideoFromWall(String vid_id) {
		//System.out.println("-- "+vid_id);
		
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/delete");
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/delete");
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("video_id", vid_id);	
			postParams.add(new BasicNameValuePair("delete",jsonObject.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(postParams));
			ResponseHandler<String> mHandler = new BasicResponseHandler();
			response = httpClient.execute(httpPost,mHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} 
		return response;
	}

}
