package com.example.camerasample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.hardware.Camera.Face;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class FaceView extends View {
	private Face[] faces;
	private Paint mPaint;
	
	public void setFaces(Face[] faces) {
		this.faces = faces;
		invalidate();
	}

	public FaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initialize();
	}

	public FaceView(Context context) {
		super(context);
		initialize();
	}

	private void initialize() {
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setAlpha(64);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if(faces == null) return;
		
		for(Face face : faces) {
			if(face == null) {
				Log.d("TEST", "face is null");
				continue;
			}
			
			Log.d("TEST", String.format("%d %d %d %d", face.rect.top, face.rect.left, face.rect.bottom, face.rect.right));
			Log.d("TEST", String.format("%d, %d", face.rect.width(), face.rect.height()));
			
			Matrix matrix = new Matrix();
			matrix.postScale(getWidth() / 2000f, getHeight() / 2000f);
			matrix.postTranslate(getWidth() / 2f, getHeight() / 2f);
			int count = canvas.save();
			canvas.concat(matrix);
			mPaint.setColor(Color.BLUE);
			mPaint.setAlpha(64);
			canvas.drawRect(face.rect, mPaint);
			mPaint.setColor(Color.WHITE);
			mPaint.setAlpha(255);
			mPaint.setTextSize(48.0f);
			canvas.drawText(String.format("score = %d", face.score), face.rect.left, face.rect.top, mPaint);
			canvas.restoreToCount(count);
		}
	}

}
