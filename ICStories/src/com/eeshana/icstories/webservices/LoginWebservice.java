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

public class LoginWebservice {
	String response;
	ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
	HttpClient httpClient = new DefaultHttpClient();

	public String userLogin(String uname,String md5password,String regId) {
		//System.out.println("-- "+uname+" -- "+md5password+" -- "+regId);
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/login");
		
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/login");
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", uname);		
			jsonObject.put("password", md5password); // md5password
			jsonObject.put("dev_token",regId);
			jsonObject.put("input_from_dev","ANDROID");
			jsonObject.put("login_type","APP");
			postParams.add(new BasicNameValuePair("login",jsonObject.toString()));
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
	
	public String forgotPassword(String email) {
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/forgot_password");
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/forgot_password");
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("email", email);		
			postParams.add(new BasicNameValuePair("forgot_password",jsonObject.toString()));
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
