package com.vstar.tuya.model;

import android.graphics.Canvas;

/**
 * @author guo
 * 
 */
public interface Action {

	public void down(float x, float y);

	public void move(float x, float y);

	public void up(float x, float y);

	public void draw(Canvas canvas);

}
