package com.vstar.tuya.model;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 光滑曲线
 * 
 * @author guo
 * 
 */
public class MyPath implements Action {
	public Path path;
	public Paint paint;

	public MyPath(float x, float y, Paint p) {
		path = new Path();
		paint = p;
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStyle(Paint.Style.STROKE);
		path.moveTo(x, y);
		tempX = x;
		tempY = y;
	}

	float tempX;
	float tempY;

	@Override
	public void down(float x, float y) {

	}

	@Override
	public void move(float x, float y) {
		float dx = Math.abs(x - tempX);
		float dy = Math.abs(tempY - y);
		if (dx >= 4 || dy >= 4) {
			path.quadTo(tempX, tempY, (x + tempX) / 2, (y + tempY) / 2);
			tempX = x;
			tempY = y;
		}
	}

	@Override
	public void up(float x, float y) {

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}

}
