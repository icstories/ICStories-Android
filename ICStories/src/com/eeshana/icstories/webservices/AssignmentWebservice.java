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

public class AssignmentWebservice {
	String response;
	ArrayList<NameValuePair> postParams = new ArrayList<NameValuePair>();

	public String updateAssignment() throws JSONException {
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost("http://www.icstories.com/icstories/webservices/users/get_assignment");
		//HttpPost httpPost = new HttpPost("");
		//HttpPost httpPost = new HttpPost("http://bizmoapps.com/icstories/webservices/users/get_assignment");
		try {
			JSONObject jsonObject = new JSONObject();
			postParams.add(new BasicNameValuePair("assignment",jsonObject.toString()));
			httpPost.setEntity(new UrlEncodedFormEntity(postParams));
			ResponseHandler<String> mHandler = new BasicResponseHandler();
			response = httpClient.execute(httpPost,mHandler);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		return response;
	}
}
