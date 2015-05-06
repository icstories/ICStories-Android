package com.eeshana.icstories.webservices;

import java.io.ByteArrayOutputStream;
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

public class RegistrationWebservice {
	String response;
	ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();
	HttpClient httpClient = new DefaultHttpClient();

	public String userRegistration(String uname,String md5password,String zipcode,String email,String regId,String login_type,String fb_token, String fbUsername) {
		System.out.println("-- "+uname+" -- "+md5password+" -- "+regId+" -- "+zipcode+" -- "+email+" -- "+login_type+" -- "+fb_token+"fb username -- "+fbUsername);
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/registration");
		
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/registration");
		
		try {
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("username", uname);		
			jsonObject.put("password", md5password); // md5password
			jsonObject.put("email",email);
			jsonObject.put("zipcode", zipcode);		
			jsonObject.put("dev_token",regId);
			jsonObject.put("input_from_dev","ANDROID");
			jsonObject.put("fb_token",fb_token);
			jsonObject.put("login_type",login_type);
			jsonObject.put("fb_username",fbUsername);
			//from device - input_from_dev
			//fb token -fb_token
			//login_type  - fb/app
			postParams.add(new BasicNameValuePair("register",jsonObject.toString()));
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
