package com.vstar.tuya.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

/**
 * 
 * 样式图片
 * 
 * @author guo
 * 
 */
public class MyStylePic implements Action {
	Bitmap bm;
	Path path;
	Paint paint;
	List<PicCoordinate> cos;

	public MyStylePic(Bitmap b, float x, float y) {
		bm = b;
		cos = new ArrayList<PicCoordinate>();
		tmpX = x;
		tmpY = y;
	}

	static float tmpX;
	static float tmpY;
	private static final float TOUCH_TOLERANCE = 4.0f;

	@Override
	public void down(float x, float y) {

	}

	@Override
	public void move(float x, float y) {
		if (isMove(x, y)) {
			cos.add(new PicCoordinate(x, y));
			tmpX = x;
			tmpY = y;
		}
	}

	@Override
	public void up(float x, float y) {

	}

	@Override
	public void draw(Canvas canvas) {
		Iterator<PicCoordinate> ite = cos.iterator();
		while (ite.hasNext()) {
			PicCoordinate p = ite.next();
			float x = p.x;
			float y = p.y;
			canvas.drawBitmap(bm, x, y, null);
		}
	}

	private static boolean isMove(float x, float y) {
		float dx = Math.abs(x - tmpX);
		float dy = Math.abs(y - tmpY);
		boolean isMoved = dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE;
		return isMoved;
	}
}
