package com.vstar.tuya.view;

import java.util.ArrayList;
import java.util.List;

import com.example.wtest1.R;
import com.vstar.tuya.MainActivity;
import com.vstar.tuya.model.Action;
import com.vstar.tuya.model.MyEraser;
import com.vstar.tuya.model.MyMinPic;
import com.vstar.tuya.model.MyPath;
import com.vstar.tuya.model.MyStylePic;
import com.vstar.tuya.util.BitmapUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * 涂鸦View
 * 
 * @author guo
 * 
 */
@SuppressLint("ClickableViewAccessibility")
public class MyView extends View {
	private static final int MAX_UNDO_COUNTS = 6;
	int defaultColor = Color.parseColor("#C9DDFE");
	// 当前的Action实例
	private Action curAction = null;
	// 当前的Action类型
	private int currentActionType = 0;
	// 当前画笔颜色和大小
	private int currentColor = Color.RED;
	private int currentSize = 10;

	int width;
	int height;

	// 背景图片
	Bitmap bgBitmap = null;

	// action栈类,用于控制撤销次数和实现undo,redo,clear
	ActionStack actionStack;

	// 临时画布
	Canvas tmpCanvas;
	Bitmap mBitmap;
	Paint mBitmaPaint;
	Paint paint;
	Bitmap b;
	//打开相册的图片
	Bitmap picBitmap = null;
	
	//打开相册的图片在view中显示的位置
	Matrix m;
	// 原始图片
	private Bitmap mOrgBitmap;

	// 小图片bitmap
	Bitmap minBm;
	// 手势是否是抬起
	private boolean isTouncUp = false;
	// 小图片最终位置
	Matrix minPicMatrix;

	public static String TAG = "info";

	public MyView(Context context) {
		super(context);
	}

	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		width = MainActivity.ScreenWidth;
		height = MainActivity.ScreenHeight;
		this.setFocusable(true);
		init();
	}

	void init() {
		Bitmap bm = BitmapFactory.decodeResource(getResources(),
				R.drawable.smallpaper00);
		bgBitmap = BitmapUtil.FitTheScreenSizeImage(bm, width, height);
		mBitmaPaint = new Paint(Paint.DITHER_FLAG);
		tmpCanvas = new Canvas();
		tmpCanvas.setDrawFilter(new PaintFlagsDrawFilter(0,
				Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG));
		actionStack = new ActionStack(this, MAX_UNDO_COUNTS);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		float mX = event.getX();
		float mY = event.getY();
		isTouncUp = false;
		if (action == MotionEvent.ACTION_DOWN) {
			tmpCanvas.setBitmap(mBitmap);
			setCurAction(mX, mY);
			actionStack.clearRedo();
			invalidate();
		}
		if (action == MotionEvent.ACTION_MOVE) {
			// 滑动时候
			if (curAction != null) {
				curAction.move(mX, mY);
				if (curAction instanceof MyEraser) {
					curAction.draw(tmpCanvas);
				}
				invalidate();
			}
		}
		if (action == MotionEvent.ACTION_UP) {
			if (curAction != null) {
				// curAction.up(mX, mY);
				actionStack.push(curAction);
				curAction.draw(tmpCanvas);
				// curAction = null;
				invalidate();
			}
			isTouncUp = true;
		}

		return true;
	}

	private void setCurAction(float mX, float mY) {

		switch (currentActionType) {
		case 0:
			paint = new Paint();
			paint.setColor(currentColor);
			paint.setStrokeWidth(currentSize);
			curAction = new MyPath(mX, mY, paint);
			// 光滑曲线
			break;
		case 1:
			paint = new Paint();
			paint.setStrokeWidth(currentSize);
			// 橡皮擦
			curAction = new MyEraser(mX, mY, paint);
			break;
		case 2:
			// 图片
			curAction = new MyMinPic(minPicMatrix, minBm);
			curAction.draw(tmpCanvas);
			break;
		case 3:
			//
			if (b != null)
				curAction = new MyStylePic(b, mX, mY);
			break;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		drawView(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		this.width = w;
		this.height = h;
		creatCanvasBitmap(width, height);
	}

	void creatCanvasBitmap(int w, int h) {
		mBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 临时图片，用于tmpCanvas画布
		tmpCanvas.setBitmap(mBitmap);
	}

	public void drawView(Canvas mCanvas) {
		// 画背景
		mCanvas.drawBitmap(bgBitmap, 0, 0, null);
		// 画打开的图片--如，相册的照片。
		mCanvas.drawBitmap(mBitmap, 0, 0, mBitmaPaint);
//		if (picBitmap != null)
//			mCanvas.drawBitmap(picBitmap, m, null);
		if (!isTouncUp) {
			if (curAction != null && !(curAction instanceof MyEraser)) {
				curAction.draw(mCanvas);
			}
		}
	}

	public void drawMinPic(Matrix m, Bitmap b) {
		if (b != null) {
			minBm = b;
			minPicMatrix = m;
			currentActionType = 2;
			setCurAction(0, 0);
			actionStack.push(curAction);
			invalidate();
			// curAction = null;
			currentActionType = 0;
		}
	}

	public void setStyleBm(int drawableId, int color) {
		b = BitmapUtil.casualStroke(getContext(), drawableId, color);
		currentActionType = 3;
	}

	/*
	 * 设置背景图片
	 */
	public void setBgBitmap(Bitmap bm) {
		if (bm != null) {
			bgBitmap = BitmapUtil.FitTheScreenSizeImage(bm, width, height);
			invalidate();
		}
	}

	public void setBitmap(Bitmap bm) {
		m = new Matrix();
		int trX = 0;
		if (bm.getWidth() < width) {
			trX = width / 2 - bm.getWidth() / 2;
		}
		m.postTranslate(trX, height / 2 - bm.getHeight() / 2);
		// m 是图片居中显示
		picBitmap = bm;
		minBm = bm;
		minPicMatrix = m;
		currentActionType = 2;
		setCurAction(0, 0);
		actionStack.push(curAction);
		invalidate();
		invalidate();
		currentActionType = 0;
	}

	public void setEraser() {
		currentActionType = 1;
	}

	public void setPen() {
		currentActionType = 0;
	}

	public void setSize(int size) {
		this.currentSize = size;
	}

	public void setColor(int color) {
		this.currentColor = color;
	}

	// 撤销
	public void undo() {
		if (null != actionStack)
			actionStack.undo();
	}

	public void redo() {
		// 前进
		if (null != actionStack)
			actionStack.redo();
	}

	/*
	 * 清屏操作
	 */
	public void clear() {

	    mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		tmpCanvas.setBitmap(mBitmap);
		actionStack.clearAll();
		picBitmap = null;
		invalidate();
	}

	/*
	 * 用于保存涂鸦完，按了保存按钮返回的图片
	 */
	public Bitmap getBitmap() {
		Bitmap bm = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bm);
		if (bgBitmap != null)
			canvas.drawBitmap(bgBitmap, 0, 0, null);
		if (picBitmap != null)
			canvas.drawBitmap(picBitmap, m, null);
		canvas.drawBitmap(mBitmap, 0, 0, null);
		return bm;
	}

	/**
	 * 创建临时背景，没有备份
	 */
	protected void setTempForeBitmap(Bitmap tempForeBitmap) {
		if (null != tempForeBitmap) {
			recycleMBitmap();
			mBitmap = BitmapUtil.duplicateBitmap(tempForeBitmap);
			if (null != mBitmap && null != tmpCanvas) {
				tmpCanvas.setBitmap(mBitmap);
				invalidate();
			}
		}
	}

	/**
	 * 回收图片
	 */
	private void recycleMBitmap() {
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}

	class ActionStack {

		private int m_stackSize = 0;
		private MyView myView;
		private List<Action> mUndoStack = new ArrayList<Action>();
		private List<Action> mRedoStack = new ArrayList<Action>();
		private List<Action> mOldStack = new ArrayList<Action>();

		public ActionStack(MyView myView, int stackSize) {
			this.myView = myView;
			m_stackSize = stackSize;
			Log.e(TAG, "Create ActionStack");
		}

		/*
		 * 添加一个Action
		 */
		public void push(Action action) {
			if (null != action) {
				// 已满
				if (mUndoStack.size() == m_stackSize && m_stackSize > 0) {
					Action removeAction = mUndoStack.get(0);
					mOldStack.add(removeAction);
					mUndoStack.remove(0);
				}
				//
				mUndoStack.add(action);
			}

		}

		/*
		 * 清空所有
		 */
		public void clearAll() {
			mUndoStack.clear();
			mRedoStack.clear();
			mOldStack.clear();
		}

		/*
		 * 撤销
		 */
		public void undo() {

			if (canUndo() && null != myView) {

				// 把最后一个取出，放入到redostack集合中，然后删除它，再重新绘画undostack中的
				Action removeAction = mUndoStack.get(mUndoStack.size() - 1);
				mRedoStack.add(removeAction);
				mUndoStack.remove(mUndoStack.size() - 1);

				if (null != mOrgBitmap) {
					myView.setTempForeBitmap(myView.mOrgBitmap);
				} else {
					myView.creatCanvasBitmap(width, height);
				}
				Canvas canvas = myView.tmpCanvas;
				for (Action action : mOldStack) {
					action.draw(canvas);
				}
				//
				for (Action action : mUndoStack) {
					action.draw(canvas);
				}
				invalidate();
			}

		}

		/*
		 * redo
		 */

		public void redo() {

			if (canRedo() && null != myView) {

				// 把最后一个取出，放入到redostack集合中，然后删除它，再重新绘画undostack中的
				Action removeAction = mRedoStack.get(mRedoStack.size() - 1);
				mUndoStack.add(removeAction);
				mRedoStack.remove(mRedoStack.size() - 1);

				if (null != mOrgBitmap) {
					myView.setTempForeBitmap(myView.mOrgBitmap);
				} else {
					myView.creatCanvasBitmap(width, height);
				}
				Canvas canvas = myView.tmpCanvas;
				for (Action action : mOldStack) {
					action.draw(canvas);
				}
				//
				for (Action action : mUndoStack) {
					action.draw(canvas);

				}
				invalidate();
			}

		}

		public boolean canUndo() {
			return (mUndoStack.size() > 0);
		}

		public boolean canRedo() {

			return (mRedoStack.size() > 0);
		}

		public void clearRedo() {
			mRedoStack.clear();
		}
	}
}
