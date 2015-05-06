package com.eeshana.icstories.common;



import com.eeshana.icstories.R;

import android.R.color;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class  CustomProgressDialog extends Dialog {

	private ImageView iv;

	public CustomProgressDialog(Context context, int resourceIdOfImage) {
		super(context, R.style.TransparentProgressDialog);
		
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		 try {
		        display.getSize(size);
		    } catch (java.lang.NoSuchMethodError ignore) { // Older device
		        size.x = display.getWidth();
		        size.y = display.getHeight();
		    }
		int height = size.y;
		
		WindowManager.LayoutParams wlmp =getWindow().getAttributes();

		wlmp.gravity = Gravity.BOTTOM;
		if(height>800 && height!=800){
		wlmp.y=137;
		}else{
			wlmp.y=105;
		}
		getWindow().setAttributes(wlmp);
		setTitle(null);
		setCancelable(false);
		setOnCancelListener(null);
		LinearLayout layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		layout.setBackgroundColor(Color.argb(000,000, 000,000));
		//layout.setBackgroundColor(Color.argb(255,255, 255,255));
		iv = new ImageView(context);
		//iv.setPadding(0,0,0,5);
		//iv.setBackgroundColor(Color.argb(255,255, 255,255));
		iv.setImageResource(resourceIdOfImage);
		layout.addView(iv, params);
		addContentView(layout, params);
	}

	@Override
	public void show() {
		super.show();
		RotateAnimation anim = new RotateAnimation(0.0f, 360.0f , Animation.RELATIVE_TO_SELF, .5f, Animation.RELATIVE_TO_SELF, .5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(3000);
		iv.setAnimation(anim);
		iv.startAnimation(anim);
	}
}
