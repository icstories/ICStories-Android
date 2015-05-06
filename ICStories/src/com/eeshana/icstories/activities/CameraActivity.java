package com.eeshana.icstories.activities;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.microedition.khronos.opengles.GL10;

import com.eeshana.icstories.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.opengl.EGLConfig;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CameraActivity extends Activity {
	private GLSurfaceView mGLView;
    CameraSurfaceView surface_view;
    FrameLayout camera_preview;
    int noOfCameras= -1;
    static String VideoPath="";
    byte[] arrByteVideo;
    ImageView iv_capture,iv_stop,iv_orientation,iv_camera_Switch;
    ImageView ivCancel,ivOk;
    LinearLayout llCameraAction1,llCameraAction;
    MediaRecorder mMediaRecorder;
    TextView tv_time;
    Camera mCamera;
    Timer timer = new Timer();
    private boolean isRecording = false;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    int videoTimer = 0;
	int totalSuccess = 0;
	final int MAX_ATTEMPT = 3;
	int attempt = 1;
	ProgressDialog dialog;
	int orintationAngle=90;
	boolean isFrontCam=false,isFirstTime=true;
	boolean frontCamAvailable = false, backCameraAvailable = false;
	static SharedPreferences sharedPreferences;
	Editor editor;
	Bundle bundle;
	String timeStamp;
	File mediaStorageDir;
	private final int MAX_DURATION = 5*60*1000-1;
	//private final int TIMER_DURATION_IN_MIN = 5;
	
	/*private final int WIDTH_16_9 = 640;
	private final int HEIGHT_16_9= 360;*/
	
	private final int WIDTH_16_9 = 1280;
	private final int HEIGHT_16_9= 720;
	
	/*private final int WIDTH_16_9 = 320;
	private final int HEIGHT_16_9= 240;*/
	
	private final int WIDTH_4_3 = 640;
	private final int HEIGHT_4_3 = 480;
	
	private static final String TAG = "CameraActivity";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "icstories"+"/asignments/videos");
		
		sharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);
        editor = sharedPreferences.edit();
		bundle = new Bundle();
		bundle = savedInstanceState;
		
        surface_view = new CameraSurfaceView(this);
        mGLView = new MySurfaceView(this);
        setContentView(R.layout.activity_camera);
       
        orintationAngle = sharedPreferences.getInt("orientation", 90);
        llCameraAction = (LinearLayout)findViewById(R.id.llCameraAction);
        llCameraAction1 = (LinearLayout)findViewById(R.id.llCameraAction1);
        ivCancel = (ImageView)findViewById(R.id.ivCancel);
        ivOk = (ImageView)findViewById(R.id.ivOk);
        camera_preview =(FrameLayout)findViewById(R.id.camera_preview);
        iv_capture = (ImageView)findViewById(R.id.iv_capture);
        iv_stop = (ImageView)findViewById(R.id.iv_stop);
        iv_orientation = (ImageView)findViewById(R.id.iv_orientation);
        tv_time = (TextView)findViewById(R.id.tv_time);
        mMediaRecorder = new MediaRecorder();
        llCameraAction.setVisibility(View.GONE);
        llCameraAction1.setVisibility(View.VISIBLE);
        iv_camera_Switch = (ImageView)findViewById(R.id.iv_camera_Switch);
		
        iv_stop.setEnabled(false);
        
        camera_preview.addView(surface_view);
        
        PackageManager pm = getPackageManager();
        frontCamAvailable = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
        backCameraAvailable = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
        
        if(!backCameraAvailable && frontCamAvailable){
        	
        	isFrontCam = true;
        	
        }
        
        if(Camera.getNumberOfCameras()>1)
        {
        	iv_camera_Switch.setVisibility(View.VISIBLE);
        }
        else
        {
        	iv_camera_Switch.setVisibility(View.GONE);
        }
        
        dialog = new ProgressDialog(CameraActivity.this);
		dialog.setMessage("Loading");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

		iv_orientation.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

					orintationAngle = sharedPreferences.getInt("orientation", 0) + 90;
					if(orintationAngle == 360)
					{
						editor.putInt("orientation", 0);
						editor.commit();
					}else
					{
						editor.putInt("orientation", orintationAngle);
						editor.commit();
					}
					
					releaseCamera();
					//releaseMediaRecorder();
					onCreate(bundle);

				
			}
		});
		
		
		iv_camera_Switch.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isFrontCam)
				{
					noOfCameras = getBackCameraId();
					if(noOfCameras != -1)
					{
						editor.putInt("CameraID", noOfCameras);
						editor.commit();
						releaseCamera();
						onCreate(bundle);
						isFrontCam = false;
					}
					else{
						Toast.makeText(getApplicationContext(), "Sorry...", Toast.LENGTH_LONG).show();
					}
					
				}
				else
				{
					noOfCameras = getFrontCameraId();
					if(noOfCameras != -1)
					{
						editor.putInt("CameraID", noOfCameras);
						editor.commit();
						releaseCamera();
						onCreate(bundle);
						isFrontCam = true;
					}
					else
					{
						Toast.makeText(getApplicationContext(), "Sorry...", Toast.LENGTH_LONG).show();
					}
					
				}
			}
		});
		
        iv_capture.setOnClickListener(new OnClickListener() {
			
			@SuppressWarnings("unused")
			@SuppressLint("SimpleDateFormat")
			@Override
			public void onClick(View v) {
	            if (isRecording) {
	                // stop recording and release camera
	                mMediaRecorder.stop();  // stop the recording
	                releaseMediaRecorder(); // release the MediaRecorder object
	                surface_view.mCamera.lock();         // take camera access back from MediaRecorder

	                // inform the user that recording has stopped
	                llCameraAction1.setVisibility(View.VISIBLE);
					 llCameraAction.setVisibility(View.GONE);
					 iv_capture.setVisibility(View.VISIBLE);
	   				 iv_stop.setVisibility(View.GONE);
	                isRecording = false;
	            } else {
	                // initialize video camera
	                if (prepareVideoRecorder()) {
	                    // Camera is available and unlocked, MediaRecorder is prepared,
	                    // now you can start recording
	                	try
	                	{
	                		mMediaRecorder.start();
		                    stopVideo();
	                	}
	                	catch(Exception e)
	                	{
	                		Log.e(TAG, "exception occurred");
	                		e.printStackTrace();
	                		try {
								releaseMediaRecorder();
							} catch (Exception e2) {
							}
	                		if(prepareVideoRecorder(WIDTH_4_3, HEIGHT_4_3))
	                		{
	                			try
	    	                	{
	    	                		mMediaRecorder.start();
	    		                    stopVideo();
	    	                	}catch(Exception ee){
	    	                		ee.printStackTrace();
	    	                	}
	                		}
	                		
	                		e.printStackTrace();
	        				try 
	        				{
	        					File imagesFolder = new File(Environment.getExternalStorageDirectory(), "icstory_log");
	        					imagesFolder.mkdirs(); // <----
	        					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss");
	        					String currentDateTimeString = sdf.format(new Date());
	        					
	        					File image = new File(imagesFolder,"error" + ".txt");
	        			
	        					Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(image), "utf-8"));
	        				    writer.write(e.toString());
	        				    writer.close();
	        				} 
	        				catch (IOException we1) 
	        				{
	        					we1.printStackTrace();
	        				}
	                	}
	                    
	                    // inform the user that recording has started
	                     llCameraAction1.setVisibility(View.VISIBLE);
		   				 llCameraAction.setVisibility(View.GONE);
		   				 
		   				 iv_capture.setVisibility(View.GONE);
		   				 iv_stop.setVisibility(View.VISIBLE);
		   				iv_stop.setEnabled(false);
	                    isRecording = true;
	                } else {
	                    // prepare didn't work, release the camera
	                    releaseMediaRecorder();
	                }
	            }
	        }
        
			
		});
        
       
       iv_stop.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (isRecording) {
				
                // stop recording and release camera

					mMediaRecorder.stop();

                  // stop the recording
                releaseMediaRecorder(); // release the MediaRecorder object
                timer.cancel();
                
                surface_view.mCamera.lock();         // take camera access back from MediaRecorder

                // inform the user that recording has stopped
                llCameraAction1.setVisibility(View.GONE);
				 llCameraAction.setVisibility(View.VISIBLE);
               // button_capture.setText("Capture");
                isRecording = false;
                iv_camera_Switch.setVisibility(View.GONE);
                
                RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
                LayoutParams layoutParamsRelativeLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                relativeLayout.setLayoutParams(layoutParamsRelativeLayout);
                
                ImageView view = new ImageView(getApplicationContext());
                LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                view.setLayoutParams(layoutParams);
                Bitmap bm = ThumbnailUtils.createVideoThumbnail(VideoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                view.setImageBitmap(bm);
                view.setScaleType(ScaleType.FIT_XY);
                relativeLayout.addView(view, layoutParams);
                
                ImageView imgPrev = new ImageView(getApplicationContext());
                RelativeLayout.LayoutParams layoutParamsimgPrev = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
                		LayoutParams.WRAP_CONTENT);
                layoutParamsimgPrev.addRule(RelativeLayout.CENTER_IN_PARENT);
                imgPrev.setLayoutParams(layoutParamsimgPrev);
                
                imgPrev.setBackgroundResource(R.drawable.play);
                
                
                relativeLayout.addView(imgPrev, layoutParamsimgPrev);
                
                camera_preview.addView(relativeLayout, layoutParamsRelativeLayout);
                
                Log.e("TAG", "video path:"+VideoPath);
                
                zip(VideoPath, mediaStorageDir.getPath() + File.separator + "ZIP_"+ timeStamp + ".zip");
                
                view.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						
						Intent intent = new Intent();  
			            intent.setAction(android.content.Intent.ACTION_VIEW);
			            System.out.println(".......................... "+VideoPath);
			            File file = new File(VideoPath);  
			            intent.setDataAndType(Uri.fromFile(file), "video/*");  
			            startActivity(intent);
					}
				});
                
                //playing video is not required
                //view.setVisibility(View.INVISIBLE);
				
            }
			
		}
	});
       
       ivCancel.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			releaseCamera();
			releaseMediaRecorder();
			System.out.println(VideoPath);
			File file = new File(VideoPath);
			if(file.delete())
			{
				//Toast.makeText(getApplicationContext(), "Delete Done.....", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(CameraActivity.this,CaptureVideoActivity.class);
				//intent.putExtra("videopath", VideoPath);
				setResult(RESULT_CANCELED, intent);
				finish();
			}
			else
			{
				Toast.makeText(getApplicationContext(), "Failed.....", Toast.LENGTH_SHORT).show();
			}
			
		}
	});
       
       ivOk.setOnClickListener(new OnClickListener() {
   		
		@Override
		public void onClick(View v) {
			/*
			releaseCamera();
			releaseMediaRecorder();
			Intent intent = new Intent(CameraActivity.this,CaptureVideoActivity.class);
			intent.putExtra("videopath", VideoPath);
			setResult(RESULT_OK, intent);
			finish();
		*/
			

			releaseCamera();
			releaseMediaRecorder();
			Intent intent = new Intent(CameraActivity.this,CaptureVideoActivity.class);
			intent.setData(Uri.parse(VideoPath));
			intent.putExtra("videopath", VideoPath);
			intent.putExtra("duration", videoTimer);
			setResult(RESULT_OK, intent);
			finish();
		
		
		}
	});
       
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		releaseCamera();
		//releaseMediaRecorder();
		System.out.println(VideoPath);
		File file = new File(VideoPath);
		file.delete();
		Intent intent = new Intent(CameraActivity.this,CaptureVideoActivity.class);
		//intent.putExtra("videopath", VideoPath);
		setResult(RESULT_CANCELED, intent);
		finish();
	}

	void stopVideo()
	{
	 /*timer for automatic close recording after 30 sec*/
	 new Timer().schedule(new TimerTask() {

           @Override
           public void run() {
               runOnUiThread(new Runnable() {

                   @Override
                   public void run() 
                   {
                   	System.out.println("in timerstop");
                   		
                       	try
           				{
                       		if (isRecording) {
                				
                       		// stop recording and release camera

            					mMediaRecorder.stop();

                              // stop the recording
                            releaseMediaRecorder(); // release the MediaRecorder object
                            timer.cancel();
                            surface_view.mCamera.lock();         // take camera access back from MediaRecorder

                            // inform the user that recording has stopped
                            llCameraAction1.setVisibility(View.GONE);
            				 llCameraAction.setVisibility(View.VISIBLE);
                           // button_capture.setText("Capture");
                            isRecording = false;
                            iv_camera_Switch.setVisibility(View.GONE);
                            
                            RelativeLayout relativeLayout = new RelativeLayout(getApplicationContext());
                            LayoutParams layoutParamsRelativeLayout = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                            relativeLayout.setLayoutParams(layoutParamsRelativeLayout);
                            
                            ImageView view = new ImageView(getApplicationContext());
                            LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                            view.setLayoutParams(layoutParams);
                            Bitmap bm = ThumbnailUtils.createVideoThumbnail(VideoPath, MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                            view.setImageBitmap(bm);
                            view.setScaleType(ScaleType.FIT_XY);
                           // camera_preview.removeAllViews();
                            relativeLayout.addView(view, layoutParams);
                            
                            ImageView imgPrev = new ImageView(getApplicationContext());
                            RelativeLayout.LayoutParams layoutParamsimgPrev = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                            layoutParamsimgPrev.addRule(RelativeLayout.CENTER_IN_PARENT);
                            imgPrev.setLayoutParams(layoutParamsimgPrev);
                            
                            imgPrev.setBackgroundResource(R.drawable.play);
                            
                            
                            relativeLayout.addView(imgPrev, layoutParamsimgPrev);
                            
                            
                            
                            camera_preview.addView(relativeLayout, layoutParamsRelativeLayout);
                            
                            view.setOnClickListener(new OnClickListener() {
            					
            					@Override
            					public void onClick(View v) {
            						Intent intent = new Intent();  
            			            intent.setAction(android.content.Intent.ACTION_VIEW);
            			            System.out.println(".......................... "+VideoPath);
            			            File file = new File(VideoPath);  
            			            intent.setDataAndType(Uri.fromFile(file), "video/*");  
            			            startActivity(intent);
            					}
            				});
                            }
                           	timer.cancel();
           					//audioPath = getRealAudioPathFromUri(mCapturedAudioURI);
           					System.out.println(VideoPath);

           				}
           				catch(Exception e)
           				{
           					e.printStackTrace();
           					Toast.makeText(getApplicationContext(), "Invalid Audio", Toast.LENGTH_SHORT).show();
           				}
                   }
               });

           }
       }, MAX_DURATION);
	 /*timer for count down display while recording*/
	 //timer  = new Timer();
	 timer.scheduleAtFixedRate(new TimerTask() {
		
		@Override
		public void run() {
			videoTimer++;
			
			runOnUiThread(new Runnable() {
		        @Override
		        public void run() {
		        	if(videoTimer<=MAX_DURATION)
		        	{
		        		
		        		//tv_time.setText("00:0" + videoTimer);
		        		tv_time.setText(getTime(videoTimer));
		        		System.out.println("VideoTimer :- "+videoTimer);
		        	}
		        	if(videoTimer>1)
		        	{
		        		iv_stop.setEnabled(true);
		        	}
		        }
		    });
			//tvMessageAudio.setText("Recording Audio Note..."+audioTimer);
		}
	}, 0, 1000);
	
	}
	
	@SuppressWarnings("unused")
	private String getTime(int totalSec)
	{
		
		int hr  = 0 , min = 0 , sec  = 0  ;
		boolean leadHr = false, leadMin = false, leadSec = false;
		min = totalSec / 60;
		hr = min / 60;
		sec = totalSec % 60;
		
		leadSec = (sec <= 9) ? true : false;
		leadMin = (min <= 9) ? true : false;
		leadHr = (hr <= 9 ) ? true : false;
		return ""/*+(leadHr?("0"+hr):hr)+":"*/+(leadMin ? ("0"+min):min)+":"+(leadSec ? ("0"+sec):sec);
	}
	
	class MySurfaceView extends GLSurfaceView{

        public MySurfaceView(CameraActivity context){
            super(context);
            // Create an OpenGL ES 2.0 context.
            System.out.println("Mysurfaceview Welcome");
            //Debug.out("Mysurfaceview welcome");
            setEGLContextClientVersion(2);
            // Set the Renderer for drawing on the GLSurfaceView
            MyRenderer renderer = new MyRenderer();
            renderer.takeContext(context);
           // context.surface_view.renderer = renderer;
            setRenderer(renderer);
        }
    }
    
    int getFrontCameraId() {
        CameraInfo ci = new CameraInfo();
        for (int i = 0 ; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
            {
            	System.out.println(i);
                return i;
            }
            	
        }
        return -1; // No front-facing camera found
    }
    
    int getBackCameraId() {
        CameraInfo ci = new CameraInfo();
        for (int i = 0 ; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
            {
            	System.out.println(i);
                return i;
            }
            	
        }
        return -1; // No front-facing camera found
    }
	
    @Override
	protected void onDestroy() {
		super.onDestroy();
		System.out.println("in on destroy");
		editor.remove("CameraID");
		editor.commit();
	}

	public class CameraSurfaceView extends ViewGroup implements SurfaceHolder.Callback
    {

    private Size mPreviewSize;
    private List<Size> mSupportedPreviewSizes;        
    private Context mContext;
    private SurfaceView mSurfaceView;
    private SurfaceHolder mHolder;
    private final String TAG = "CameraSurfaceView";
    private Camera mCamera;
    private List<String> mSupportedFlashModes;

    @SuppressWarnings("deprecation")
	public CameraSurfaceView(Context context)
    {
        super(context);
        mContext = context;
        noOfCameras = sharedPreferences.getInt("CameraID", -1);
        if(noOfCameras == -1)
        {
        	noOfCameras = getBackCameraId();
        	if(noOfCameras == -1)
        	{
        		noOfCameras = getFrontCameraId();
        	}
        }
        	mCamera = Camera.open(noOfCameras);
            setCamera(mCamera);
            mSurfaceView = new SurfaceView(context);
            addView(mSurfaceView, 0);
            mHolder = mSurfaceView.getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mHolder.setKeepScreenOn(true);

    }

    public CameraSurfaceView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        mContext = context;            
    }

    public void setSupportedPreviewSizes(List<Size> supportedPreviewSizes)
    {
        mSupportedPreviewSizes = supportedPreviewSizes;
    }

    public Size getPreviewSize()
    {
        return mPreviewSize;
    }

    public void setCamera(Camera camera)
    {
        mCamera = camera;
        if (mCamera != null)
        {
        	
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();                
            mSupportedFlashModes = mCamera.getParameters().getSupportedFlashModes();
            // Set the camera to Auto Flash mode.
            //System.out.println(mSupportedFlashModes.toString());
            if(mSupportedFlashModes!=null)
            {
            	if (mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO))
                {
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);             
                    mCamera.setParameters(parameters);
                }
            }
                               
        }
        requestLayout();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null)
        {
            mCamera.stopPreview();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
    {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        if (mCamera != null)
        {
            Camera.Parameters parameters = mCamera.getParameters();  
            System.out.println(parameters.getHorizontalViewAngle());
            System.out.println(parameters.getVerticalViewAngle());
           // System.out.println(parameters);
            Size previewSize = getPreviewSize();
            
            if(previewSize!=null)
            {
            	parameters.setPreviewSize(previewSize.width, previewSize.height);                
            	//mCamera.setDisplayOrientation(90);
                
            	mCamera.setParameters(parameters);
            	
            }
            else
            {
            	Camera.getCameraInfo(0,new CameraInfo());
            		mCamera.setDisplayOrientation(orintationAngle);
               	 mCamera.setParameters(parameters);
            }
            mCamera.startPreview();
        }
        
        

    }
    
    @Override
    public void surfaceCreated(SurfaceHolder holder)
    {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try
        {
            if (mCamera != null)
            {
                mCamera.setPreviewDisplay(holder);
            }
        }
        catch (IOException exception)
        {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {        
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null)
        {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        if (changed)
        {                            
            final View cameraView = getChildAt(0);

            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null)
            {
                Display display = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

                switch (display.getRotation())
                {
                    case Surface.ROTATION_0:
                        previewWidth = mPreviewSize.height;
                        previewHeight = mPreviewSize.width;
                        mCamera.setDisplayOrientation(90);
                        break;
                    case Surface.ROTATION_90:
                        previewWidth = mPreviewSize.width;
                        previewHeight = mPreviewSize.height;
                        break;
                    case Surface.ROTATION_180:
                        previewWidth = mPreviewSize.height;
                        previewHeight = mPreviewSize.width;
                        break;
                    case Surface.ROTATION_270:
                        previewWidth = mPreviewSize.width;
                        previewHeight = mPreviewSize.height;
                        mCamera.setDisplayOrientation(180);
                        break;
                }
            }

            final int scaledChildHeight = previewHeight * width / previewWidth;

            cameraView.layout(0, height - scaledChildHeight, width, height);

        }
    }
    
    


    /*private Size getOptimalPreviewSize(List<Size> sizes, int width, int height)
    {           
        Size optimalSize = null;                                

        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) height / width;

        // Try to find a size match which suits the whole screen minus the menu on the left.
        for (Size size : sizes)
        {
            if (size.height != width) continue;
            double ratio = (double) size.width / size.height;
            if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE)
            {
                optimalSize = size;
            }               
        }

        // If we cannot find the one that matches the aspect ratio, ignore the requirement.
        if (optimalSize == null)
        {
        }

        return optimalSize;
    }*/
    
    
    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void previewCamera()
    {        
        try 
        {           
            mCamera.setPreviewDisplay(mHolder);         
            mCamera.startPreview();                 
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview.", e);    
        }
    }

    void decodeYUV420SP(byte[] rgb, byte[] yuv420sp, int width, int height) {   

        final int frameSize = width * height;   

        for (int j = 0, yp = 0; j < height; j++) {        
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;   
            for (int i = 0; i < width; i++, yp++) {   
                int y = (0xff & ((int) yuv420sp[yp])) - 16;   
                if (y < 0){   
                    y = 0;  
                } 
                if ((i & 1) == 0) {   
                    v = (0xff & yuv420sp[uvp++]) - 128;   
                    u = (0xff & yuv420sp[uvp++]) - 128;   
                }   

                int y1192 = 1192 * y;   
                int r = (y1192 + 1634 * v);   
                int g = (y1192 - 833 * v - 400 * u);   
                int b = (y1192 + 2066 * u);   

                if (r < 0){ 
                    r = 0;                
                }else if (r > 262143){   
                    r = 262143;  
                } 
                if (g < 0){                   
                    g = 0;                
                }else if (g > 262143){ 
                    g = 262143;  
                } 
                if (b < 0){                   
                    b = 0;                
                }else if (b > 262143){ 
                    b = 262143;  
                } 
                rgb[yp*3] = (byte) (b << 6); 
                rgb[yp*3 + 1] = (byte) (b >> 2); 
                rgb[yp*3 + 2] = (byte) (b >> 10); 
            }   
        }   
    }   
    }
    
    public class MyRenderer implements GLSurfaceView.Renderer{
        private FloatBuffer vertices;
        private FloatBuffer texcoords;
        private int mProgram;
        private int maPositionHandle;
        private int gvTexCoordHandle;
        private int gvSamplerHandle;
        private Context context=null;
        int[] camera_texture;
        public void onSurfaceCreated(GL10 unused, EGLConfig config) {
            initShapes();
            GLES20.glClearColor(0.0f, 1.0f, 0.2f, 1.0f);
            //Debug.out("Hello init.");
            System.out.println("Hello init.");
            //Shaders
            int vertexShader = 0;
            int fragmentShader = 0;
            try {
                vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, readFile("vertex.vsh"));
                fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, readFile("fragment.fsh"));
            } catch (IOException e) {
                //Debug.out("The shaders could not be found.");
            	System.out.println("The shaders could not be found.");
                e.printStackTrace();
            }
            mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
            GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
            GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
            GLES20.glLinkProgram(mProgram);                  // creates OpenGL program executables
            // get handles
            maPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
            gvTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "a_texCoord");
            gvSamplerHandle = GLES20.glGetAttribLocation(mProgram, "s_texture");
            GLES20.glPixelStorei(GLES20.GL_UNPACK_ALIGNMENT, 1);
            camera_texture = null;
        }


        private void initShapes(){
            float triangleCoords[] = {
                // X, Y, Z
                -1.0f, -1.0f, 0.0f,
                 1.0f, -1.0f, 0.0f,
                 -1.0f, 1.0f, 0.0f,
                 1.0f,  1.0f, 0.0f,
            }; 
            float texcoordf[] = {
                // X, Y, Z
                -1.0f,-1.0f,
                1.0f,-1.0f,
                -1.0f,1.0f,
                1.0f,1.0f,
            };

            // initialize vertex Buffer for vertices
            ByteBuffer vbb = ByteBuffer.allocateDirect(triangleCoords.length * 4); 
            vbb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
            vertices = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
            vertices.put(triangleCoords);    // add the coordinates to the FloatBuffer
            vertices.position(0);            // set the buffer to read the first coordinate
            // initialize vertex Buffer for texcoords 
            vbb = ByteBuffer.allocateDirect(texcoordf.length * 4); 
            vbb.order(ByteOrder.nativeOrder());// use the device hardware's native byte order
            texcoords = vbb.asFloatBuffer();  // create a floating point buffer from the ByteBuffer
            texcoords.put(texcoordf);    // add the coordinates to the FloatBuffer
            texcoords.position(0);            // set the buffer to read the first coordinate
        }

        private String readFile(String path) throws IOException {
            AssetManager assetManager = context.getAssets();
            InputStream stream = assetManager.open(path);
            try {
                return new Scanner(stream).useDelimiter("\\A").next();
            }
            finally {
                stream.close();
            }
        }

        private int loadShader(int type, String shaderCode){
            // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
            // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
            int shader = GLES20.glCreateShader(type); 
            // add the source code to the shader and compile it
            GLES20.glShaderSource(shader, shaderCode);
            GLES20.glCompileShader(shader);
            return shader;
        }

        public void onDrawFrame(GL10 unused) {
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
            if(camera_texture == null){
                return;
            }
            // Add program to OpenGL environment
            GLES20.glUseProgram(mProgram);
            // Prepare the triangle data
            GLES20.glVertexAttribPointer(maPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertices);
            GLES20.glVertexAttribPointer(gvTexCoordHandle, 2, GLES20.GL_FLOAT, false, 0, texcoords);
            GLES20.glEnableVertexAttribArray(maPositionHandle);
            GLES20.glEnableVertexAttribArray(gvTexCoordHandle);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, camera_texture[0]);
            GLES20.glUniform1i(gvSamplerHandle, 0);
            // Draw the triangle
            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
            GLES20.glDisableVertexAttribArray(maPositionHandle);
            GLES20.glDisableVertexAttribArray(gvTexCoordHandle);
        }

        public void onSurfaceChanged(GL10 unused, int width, int height) {
            GLES20.glViewport(0, 0, width, height);
        }

        public void takeContext(Context ocontext) {
            //Debug.out("Take context");
        	System.out.println("Take context");
            context = ocontext;
        }

        void bindCameraTexture(byte[] data,int w,int h) {
            byte[] pixels = new byte[256*256*3];
            for(int x = 0;x < 256;x++){
                for(int y = 0;x < 256;x++){
                    pixels[x*256+y] = data[x*w+y];
                }
            }
            if (camera_texture==null){
                camera_texture=new int[1];
            }else{
                GLES20.glDeleteTextures(1, camera_texture, 0);
            }   
            GLES20.glGenTextures(1, camera_texture, 0);
            int tex = camera_texture[0];
            GLES20.glBindTexture(GL10.GL_TEXTURE_2D, tex);
            GLES20.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGB, 256, 256, 0, GL10.GL_RGB, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(pixels));
            GLES20.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
        }


		@Override
		public void onSurfaceCreated(GL10 gl,
				javax.microedition.khronos.egl.EGLConfig config) {
			
		}
    }
	
    @Override
    protected void onPause() {
        super.onPause();
        //releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        //releaseCamera();              // release the camera immediately on pause event
        mGLView.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        mGLView.onResume();
    }
	
    
    private boolean prepareVideoRecorder(int w , int h ){

        //mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        surface_view.mCamera.unlock();
        mMediaRecorder.setCamera(surface_view.mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        
        /*if(frontCamAvailable && isFrontCam){
        	
        	mMediaRecorder.setOrientationHint(270);
        	
        }else{
        	
        	mMediaRecorder.setOrientationHint(90);
        	
        }*/

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
     //   mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        
        mMediaRecorder.setVideoEncodingBitRate(900000);
        mMediaRecorder.setVideoSize(w, h);//16*9 aspect ratio
        
        mMediaRecorder.setVideoFrameRate(15);
        mMediaRecorder.setMaxDuration(MAX_DURATION);
        mMediaRecorder.setAudioEncodingBitRate(160);
        mMediaRecorder.setAudioChannels(2);
        mMediaRecorder.setAudioSamplingRate(48);

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(surface_view.mHolder.getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("video", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("video", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    
    
    private boolean prepareVideoRecorder(){

        //mCamera = getCameraInstance();
        mMediaRecorder = new MediaRecorder();

        // Step 1: Unlock and set camera to MediaRecorder
        surface_view.mCamera.unlock();
        mMediaRecorder.setCamera(surface_view.mCamera);

        // Step 2: Set sources
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        
        Log.e(TAG, "frontCamAvailable : "+frontCamAvailable+",isFrontCam : " + isFrontCam);
        
        /*if(frontCamAvailable && isFrontCam){
        	
        	mMediaRecorder.setOrientationHint(270);
        	
        }else{
        	
        	mMediaRecorder.setOrientationHint(90);
        	
        }*/
        
    	

        // Step 3: Set a CamcorderProfile (requires API Level 8 or higher)
     //   mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH));
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        
        mMediaRecorder.setVideoEncodingBitRate(900000);
        mMediaRecorder.setVideoSize(WIDTH_16_9, HEIGHT_16_9);//16*9 aspect ratio
        
        mMediaRecorder.setVideoFrameRate(15);
        mMediaRecorder.setMaxDuration(MAX_DURATION);
        mMediaRecorder.setAudioEncodingBitRate(160);
        mMediaRecorder.setAudioChannels(2);
        mMediaRecorder.setAudioSamplingRate(48);

        // Step 4: Set output file
        mMediaRecorder.setOutputFile(getOutputMediaFile(MEDIA_TYPE_VIDEO).toString());

        // Step 5: Set the preview output
        mMediaRecorder.setPreviewDisplay(surface_view.mHolder.getSurface());

        // Step 6: Prepare configured MediaRecorder
        try {
            mMediaRecorder.prepare();
        } catch (IllegalStateException e) {
            Log.d("video", "IllegalStateException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            Log.d("video", "IOException preparing MediaRecorder: " + e.getMessage());
            releaseMediaRecorder();
            return false;
        }
        return true;
    }
    
    /** Create a file Uri for saving an image or video */
    /*private static Uri getOutputMediaFileUri(int type){
          return Uri.fromFile(getOutputMediaFile(type));
    }*/

    /** Create a File for saving an image or video */
    @SuppressLint("SimpleDateFormat")
	@SuppressWarnings("unused")
	private File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
    	String tripname = "";
		 tripname = sharedPreferences.getString("tripName", "MyTrip");

        /*File mediaStorageDir = new File(Environment.getExternalStorageDirectory(),
				"icstories"+"/asignments/videos");*/
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
            "VID_"+ timeStamp + ".mp4");
            
            /*zip(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4",mediaStorageDir.getPath() + File.separator +
                    "ZIP_"+ timeStamp + ".zip");*/
            
        } else {
            return null;
        }
        VideoPath = mediaFile.toString();
        return mediaFile;
    }
    
    private void zip(String file, String zipFileName){
    	
    	int BUFFER = 80000;
    	
    	try {
			FileOutputStream dest = new FileOutputStream(zipFileName);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];
			
			FileInputStream fi = new FileInputStream(file);
			BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
			
			ZipEntry entry = new ZipEntry(file.substring(file.lastIndexOf("/") + 1));
			out.putNextEntry(entry);
			
			int count;
			 
            while ((count = origin.read(data, 0, BUFFER)) != -1) {
                out.write(data, 0, count);
            }
            origin.close();
			
            out.close();
            
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	        // Camera is not available (in use or does not exist)
	    }
	    return c; // returns null if camera is unavailable
	}
	
	 private void releaseMediaRecorder(){
	        if (mMediaRecorder != null) {
	            mMediaRecorder.reset();   // clear recorder configuration
	            mMediaRecorder.release(); // release the recorder object
	            mMediaRecorder = null;
	            surface_view.mCamera.lock();           // lock camera for later use
	        }
	    }

	    private void releaseCamera(){
	        if (surface_view.mCamera != null){
	        	surface_view.mCamera.release();        // release the camera for other applications
	        	surface_view.mCamera = null;
	        }
	    }
	
}

