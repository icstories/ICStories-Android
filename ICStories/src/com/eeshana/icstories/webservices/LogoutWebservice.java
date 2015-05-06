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

public class LogoutWebservice {
	String response;
	ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
	HttpClient httpClient = new DefaultHttpClient();

	public String userLogout(String uid,String regId) {
		//System.out.println("-- "+uid+" -- "+regId);
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/logout");
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/logout");
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("userid", uid);		
			jsonObject.put("dev_token",regId);
			postParams.add(new BasicNameValuePair("logout",jsonObject.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(postParams));
			ResponseHandler<String> mHandler = new BasicResponseHandler();
			response = httpClient.execute(httpPost,mHandler);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		} 
		return response;
	}
}
