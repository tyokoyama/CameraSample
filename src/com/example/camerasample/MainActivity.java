package com.example.camerasample;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements SurfaceHolder.Callback, PictureCallback, ShutterCallback {

	private Camera mCamera;
	private SurfaceView mView;
	private FaceView mfaceView;
	
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Button btn = (Button)findViewById(R.id.button1);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCamera.takePicture(MainActivity.this, null, MainActivity.this);
			}
		});
        
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {        	
        	FrameLayout fl = (FrameLayout)findViewById(R.id.framelayout1);
        	mfaceView = new FaceView(this);
        	fl.addView(mfaceView);
        	
	        final ToggleButton btn2 = (ToggleButton)findViewById(R.id.toggleButton1);
	        btn2.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				
				@SuppressLint("NewApi") @Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if(isChecked) {
						Log.d("TEST", "startFaceDetection");
						mCamera.startFaceDetection();
					} else {
						Log.d("TEST", "stopFaceDetection");
						mCamera.stopFaceDetection();
					}
				}
			});
        }
        
        Button btn3 = (Button)findViewById(R.id.button2);
        btn3.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mCamera.autoFocus(new AutoFocusCallback() {
					
					@Override
					public void onAutoFocus(boolean success, Camera camera) {
						Log.d("TEST", "onAutoFocus " + success);
						camera.cancelAutoFocus();
					}
				});
			}
		});
        
        ToggleButton btn4 = (ToggleButton)findViewById(R.id.toggleButton2);
        btn4.setChecked(true);
        btn4.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Log.d("TEST", "onCheckedChanged: " + isChecked);
				mCamera.enableShutterSound(isChecked);
			}
		});
        
        mView = (SurfaceView)findViewById(R.id.surfaceView1);
        mView.getHolder().addCallback(this);
        
        // 3.0以降ではDeprecated
        mView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        
        CameraInfo info = new CameraInfo();
        for(int i = 0; i < Camera.getNumberOfCameras(); i++) {
        	Camera.getCameraInfo(i, info);
        	if(info.facing == CameraInfo.CAMERA_FACING_BACK) {
        		// 背面カメラを取得
                mCamera = Camera.open(i);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) { 
                	mCamera.setFaceDetectionListener(new FaceDetectionListener() {
						
						@Override
						public void onFaceDetection(Face[] faces, Camera camera) {
							Log.d("TEST", "onFaceDetection");
							mfaceView.setFaces(faces);
						}
					});
                }
                
                Camera.Parameters params = mCamera.getParameters();
//                if(params.isZoomSupported() && params.isSmoothZoomSupported()) {
                if(params.isZoomSupported()) {
                	int maxZoom = params.getMaxZoom();
                	SeekBar bar = (SeekBar)findViewById(R.id.seekBar);
                	bar.setMax(maxZoom);
                	bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
						
						@Override
						public void onStopTrackingTouch(SeekBar seekBar) {
							Log.d("TEST", String.format("seekBar = %d", seekBar.getProgress()));
//							mCamera.stopPreview();
							mCamera.startSmoothZoom(seekBar.getProgress());
//							Camera.Parameters params = mCamera.getParameters();
//							params.setZoom(seekBar.getProgress());
//							mCamera.setParameters(params);
//							mCamera.startPreview();
						}
						
						@Override
						public void onStartTrackingTouch(SeekBar seekBar) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onProgressChanged(SeekBar seekBar, int progress,
								boolean fromUser) {
							
						}
					});
                }
                
                Log.d("TEST", "Disable Shutter Sound: " + info.canDisableShutterSound);
                
                break;
        	}
        }
    }


    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mCamera.release();
	}


	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mCamera.stopPreview();
	}


	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub
		Camera.Parameters debug = mCamera.getParameters();
		Log.d("TEST", String.format("%d, %d", debug.getPreviewSize().width, debug.getPreviewSize().height));
		
		Camera.Parameters params = mCamera.getParameters();
		List<Camera.Size> sizes = params.getSupportedPreviewSizes();
		Camera.Size selected = sizes.get(0);
		params.setPreviewSize(selected.width, selected.height);
		Log.d("TEST", String.format("%d, %d", selected.width, selected.height));
		params.setRotation(90);
		mCamera.setParameters(params);
		
		mCamera.setDisplayOrientation(90);
		mCamera.startPreview();
	}


	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		try {
			mCamera.setPreviewDisplay(mView.getHolder());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onPictureTaken(byte[] data, Camera camera) {
		// 画像データができた時に呼び出される。
		
		Log.d("TEST", "onPictureTaken");
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(Environment.getExternalStorageDirectory() + "/picture.jpg");
			out.write(data);
			out.flush();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		camera.startPreview();
	}


	@Override
	public void onShutter() {
		// シャッターが閉じた後に呼び出される。
		Log.d("TEST", "onShutter");
	}
    
}
