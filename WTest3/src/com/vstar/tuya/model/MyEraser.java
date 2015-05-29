package com.vstar.tuya.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

/**
 * 橡皮擦
 * 
 * @author guo
 * 
 */
public class MyEraser implements Action {
	public Path path;
	public Paint paint;

	public MyEraser(float x, float y, Paint p) {
		path = new Path();
		paint = p;
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStyle(Paint.Style.STROKE);
		// paint.setStrokeWidth(15);
		paint.setColor(Color.RED);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
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
