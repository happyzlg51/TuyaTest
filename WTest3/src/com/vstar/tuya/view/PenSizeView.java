package com.vstar.tuya.view;

import com.example.wtest1.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 画笔大小view
 * 
 * @author guo
 */
public class PenSizeView extends View {
	
	public PenSizeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setColor(Color.RED);

		viewWidth = (int) getResources()
				.getDimension(R.dimen.penSizeView_Width);
		viewHeigt = (int) getResources().getDimension(
				R.dimen.penSizeView_Height);
	}

	int viewWidth;
	int viewHeigt;

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawColor(Color.GRAY);
		canvas.drawCircle(viewWidth / 2, viewHeigt / 2, size / 2, mPaint);
	}

	private int size = 10;
	private Paint mPaint;

	public void setPaintSize(int size) {
		mPaint.setStrokeWidth(size);
		this.size = size;
		invalidate();
	}

	public void setPaintColor(int color) {
		mPaint.setColor(color);
		invalidate();
	}
}
