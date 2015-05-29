package com.vstar.tuya.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;

/**
 * 
 * 小图片
 * 
 * @author guo
 * 
 */
public class MyMinPic implements Action {
	Bitmap bm;
	Matrix m;

	public MyMinPic(Matrix m, Bitmap bm) {
		this.m = m;
		this.bm = bm;
	}

	@Override
	public void down(float x, float y) {

	}

	@Override
	public void move(float x, float y) {

	}

	@Override
	public void up(float x, float y) {

	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawBitmap(bm, m, null);
	}

}
