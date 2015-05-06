package com.eeshana.icstories.common;


import com.eeshana.icstories.R;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ShowCustomToast {
	
	public void showToast(Context c,String message){
		LayoutInflater inflater = (LayoutInflater) c.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

		View layout = inflater.inflate(R.layout.custom_toast, null);
		
		TextView text = (TextView) layout.findViewById(R.id.tvMessage);
		text.setText(message);

		Toast toast = new Toast(c);
		toast.setGravity(Gravity.CENTER_HORIZONTAL| Gravity.BOTTOM,0,150);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}
}
