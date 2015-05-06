package com.eeshana.icstories.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Convertor {

  public StringBuffer convertToMd5(String password) {

		MessageDigest md;
		byte[] digest = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(password.getBytes());
			digest = md.digest();
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < digest.length; i++)
	        sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1));
		return sb;
	}

}
