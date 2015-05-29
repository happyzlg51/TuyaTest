package com.vstar.tuya;

import java.io.File;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

import com.example.wtest1.R;
import com.vstar.tuya.util.BitmapUtil;
import com.vstar.tuya.view.ColorPickerDialog;
import com.vstar.tuya.view.MyView;
import com.vstar.tuya.view.PenSizeView;
import com.vstar.tuya.view.ColorPickerDialog.OnColorChangedListener;

@SuppressLint("SdCardPath")
public class MainActivity extends Activity implements OnClickListener {

	private MyView sf;
	public static int width;
	public static int height;
	public static int ScreenHeight;
	public static int ScreenWidth;
	// 保存路径
	private static final String path = "/mnt/sdcard/test.png";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		WindowManager wm = (WindowManager) this
				.getSystemService(Context.WINDOW_SERVICE);
		ScreenWidth = wm.getDefaultDisplay().getWidth();
		ScreenHeight = wm.getDefaultDisplay().getHeight();
		setContentView(R.layout.activity_main);
		initView();
	}

	Button btn_open, btn_undo, btn_redo, btn_save, btn_pen, btn_clear,
			btn_penColor, btn_penStyle, btn_minpic;

	CheckBox cb_eraser;
	// ---画笔大小布局
	LinearLayout ll_penSize;
	PenSizeView pv;
	SeekBar penSizeSb;

	// ---
	// ---shader样式
	LinearLayout ll_shader;
	ImageView iv_sh1;
	ImageView iv_sh2;
	// ------

	// ------小图标布局---
	LinearLayout ll_minpic;
	ImageView iv_minpic1;

	// ---
	// 显示小图标的Imageview和 两个确定，取消按钮
	ImageView iv_show;
	Button btn_minPicConfirm, btn_minPicCancel;
	LinearLayout ll_minPicBtn;

	// ---背景图片选择

	LinearLayout ll_bgPic;
	Button btn_selectBg;
	ImageView ivBg1, ivBg2, ivBg3, ivBg4, ivBg5;

	// ---
	private void initView() {
		sf = (MyView) findViewById(R.id.myView);
		ViewTreeObserver vto = sf.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressLint("NewApi")
			@Override
			public void onGlobalLayout() {
				sf.getViewTreeObserver().removeOnGlobalLayoutListener(this);
				width = sf.getWidth();
				height = sf.getHeight();
			}
		});
		btn_open = (Button) findViewById(R.id.openPic);
		btn_undo = (Button) findViewById(R.id.undo);
		btn_redo = (Button) findViewById(R.id.redo);
		btn_save = (Button) findViewById(R.id.savePic);
		btn_pen = (Button) findViewById(R.id.pen);
		btn_penColor = (Button) findViewById(R.id.penColor);
		btn_clear = (Button) findViewById(R.id.clear);
		btn_penStyle = (Button) findViewById(R.id.penStyle);
		btn_minpic = (Button) findViewById(R.id.minPic);
		btn_minpic.setOnClickListener(this);
		btn_penStyle.setOnClickListener(this);
		btn_open.setOnClickListener(this);
		btn_undo.setOnClickListener(this);
		btn_redo.setOnClickListener(this);
		btn_save.setOnClickListener(this);
		btn_pen.setOnClickListener(this);
		btn_clear.setOnClickListener(this);
		btn_penColor.setOnClickListener(this);

		cb_eraser = (CheckBox) findViewById(R.id.cb_eraser);
		cb_eraser.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {

				if (isChecked) {
					sf.setEraser();
				} else {
					sf.setPen();
				}

			}
		});
		//
		ll_penSize = (LinearLayout) findViewById(R.id.ll_pen);
		pv = (PenSizeView) findViewById(R.id.penSizeIv);
		penSizeSb = (SeekBar) findViewById(R.id.penSizeSb);
		penSizeSb.setMax(80);
		penSizeSb.setProgress(10);
		penSizeSb.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				pv.setPaintSize(progress);
				sf.setSize(progress);
			}
		});
		//

		ll_shader = (LinearLayout) findViewById(R.id.ll_sharder);
		iv_sh1 = (ImageView) findViewById(R.id.shader1);
		iv_sh2 = (ImageView) findViewById(R.id.shader2);
		iv_sh1.setOnClickListener(this);
		iv_sh2.setOnClickListener(this);

		//

		ll_minpic = (LinearLayout) findViewById(R.id.ll_minpic);
		iv_minpic1 = (ImageView) findViewById(R.id.minpic1);
		iv_minpic1.setOnClickListener(this);
		iv_show = (ImageView) findViewById(R.id.showIv);

		iv_show.setOnTouchListener(new OnTouchListener() {
			@SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int action = event.getAction();

				int x = (int) event.getRawX();
				int y = (int) event.getRawY();
				switch (action) {
				case MotionEvent.ACTION_DOWN:
					tempX = iv_show.getWidth() / 2;
					tempY = iv_show.getHeight() / 2;
					break;
				case MotionEvent.ACTION_MOVE:
					int top = x - tempX;
					int left = y - tempY;
					int right = x - tempX + v.getWidth();
					int bottom = y - tempY + v.getHeight();
					iv_show.layout(top, left, right, bottom);
					iv_show.postInvalidate();
					break;
				case MotionEvent.ACTION_UP:

					tempX = x;
					tempX = y;
					break;
				}
				return true;
			}
		});

		// 小图标确定取消布局
		ll_minPicBtn = (LinearLayout) findViewById(R.id.ll_btn);
		btn_minPicConfirm = (Button) findViewById(R.id.btn_picConfirm);
		btn_minPicCancel = (Button) findViewById(R.id.btn_picCancel);
		btn_minPicConfirm.setOnClickListener(this);
		btn_minPicCancel.setOnClickListener(this);

		// 背景图片选择
		ll_bgPic = (LinearLayout) findViewById(R.id.ll_bgPic);
		btn_selectBg = (Button) findViewById(R.id.selectBg);
		ivBg1 = (ImageView) findViewById(R.id.bgpic1);
		ivBg2 = (ImageView) findViewById(R.id.bgpic2);
		ivBg3 = (ImageView) findViewById(R.id.bgpic3);
		ivBg4 = (ImageView) findViewById(R.id.bgpic4);
		ivBg5 = (ImageView) findViewById(R.id.bgpic5);
		btn_selectBg.setOnClickListener(this);
		ivBg1.setOnClickListener(this);
		ivBg2.setOnClickListener(this);
		ivBg3.setOnClickListener(this);
		ivBg4.setOnClickListener(this);
		ivBg5.setOnClickListener(this);
	}

	int tempX;
	int tempY;
	int[] location;

	@SuppressLint("NewApi")
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.openPic:
			// 打开图片
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			intent.setType("image/*");
			startActivityForResult(intent, 10);
			break;
		case R.id.undo:
			// 撤销
			sf.undo();
			break;
		case R.id.redo:
			// 前进
			sf.redo();
			break;
		case R.id.clear:
			// 清除
			sf.clear();
			break;
		case R.id.savePic:
			// 保存
			savePic();
			break;
		case R.id.pen:
			// 大小
			checkVisitable();
			break;
		case R.id.penColor:
			// 颜色
			openColorPickDialog();
			break;
		case R.id.shader1:
			sf.setPen();
			break;
		case R.id.shader2:
			sf.setStyleBm(R.drawable.stamp0heart, 0xfffdf600);
			break;
		case R.id.penStyle:
			// 样式
			if (ll_shader.getVisibility() == View.GONE) {
				ll_shader.setVisibility(View.VISIBLE);
			} else {
				ll_shader.setVisibility(View.GONE);
			}
			if (ll_penSize.getVisibility() == View.VISIBLE) {
				ll_penSize.setVisibility(View.GONE);
			}
			if (ll_minpic.getVisibility() == View.VISIBLE) {
				ll_minpic.setVisibility(View.GONE);
			}
			if (ll_bgPic.getVisibility() == View.VISIBLE) {
				ll_bgPic.setVisibility(View.GONE);
			}
			break;

		case R.id.minPic:
			// 小图片
			ll_minpic.bringToFront();
			if (ll_minpic.getVisibility() == View.GONE) {
				ll_minpic.setVisibility(View.VISIBLE);
			} else {
				ll_minpic.setVisibility(View.GONE);
			}
			if (ll_shader.getVisibility() == View.VISIBLE) {
				ll_shader.setVisibility(View.GONE);
			}
			if (ll_penSize.getVisibility() == View.VISIBLE) {
				ll_penSize.setVisibility(View.GONE);
			}
			if (ll_bgPic.getVisibility() == View.VISIBLE) {
				ll_bgPic.setVisibility(View.GONE);
			}
			break;

		case R.id.minpic1:

			tempDr = iv_minpic1.getBackground();
			iv_show.setImageDrawable(tempDr);
			iv_show.setVisibility(View.VISIBLE);
			iv_show.bringToFront();
			if (ll_minPicBtn.getVisibility() == View.GONE) {
				ll_minPicBtn.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.btn_picConfirm:
			checkllBtnVisable();
			Matrix m = new Matrix();
			location = new int[2];
			iv_show.getLocationOnScreen(location);// 获取图片在屏幕上面的绝对位置
			m.postTranslate(location[0], location[1]);
			if (tempDr != null) {
				Bitmap b = BitmapUtil.drawableToBitmap(tempDr);
				sf.drawMinPic(m, b);
			}
			break;
		case R.id.btn_picCancel:
			checkllBtnVisable();
			break;

		case R.id.selectBg:
			// 背景
			ll_bgPic.bringToFront();
			if (ll_bgPic.getVisibility() == View.GONE) {
				ll_bgPic.setVisibility(View.VISIBLE);
			} else {
				ll_bgPic.setVisibility(View.GONE);
			}
			if (ll_shader.getVisibility() == View.VISIBLE) {
				ll_shader.setVisibility(View.GONE);
			}
			if (ll_penSize.getVisibility() == View.VISIBLE) {
				ll_penSize.setVisibility(View.GONE);
			}
			if (ll_minpic.getVisibility() == View.VISIBLE) {
				ll_minpic.setVisibility(View.GONE);
			}
			break;
		case R.id.bgpic1:
			sf.setBgBitmap(BitmapUtil.drawableToBitmap(ivBg1.getBackground()));
			break;
		case R.id.bgpic2:
			sf.setBgBitmap(BitmapUtil.drawableToBitmap(ivBg2.getBackground()));
			break;
		case R.id.bgpic3:
			sf.setBgBitmap(BitmapUtil.drawableToBitmap(ivBg3.getBackground()));
			break;
		case R.id.bgpic4:
			sf.setBgBitmap(BitmapUtil.drawableToBitmap(ivBg4.getBackground()));
			break;
		case R.id.bgpic5:
			sf.setBgBitmap(BitmapUtil.drawableToBitmap(ivBg5.getBackground()));
			break;
		}

	}

	Drawable tempDr;

	private void checkllBtnVisable() {
		if (ll_minPicBtn.getVisibility() == View.VISIBLE) {
			ll_minPicBtn.setVisibility(View.GONE);
		}
		if (iv_show.getVisibility() == View.VISIBLE) {
			iv_show.setVisibility(View.GONE);
		}

	}

	private void checkVisitable() {

		if (ll_penSize.getVisibility() == View.GONE) {
			ll_penSize.bringToFront();
			ll_penSize.setVisibility(View.VISIBLE);
		} else {
			ll_penSize.setVisibility(View.GONE);
		}

		if (ll_shader.getVisibility() == View.VISIBLE) {
			ll_shader.setVisibility(View.GONE);
		}
		if (ll_minpic.getVisibility() == View.VISIBLE) {
			ll_minpic.setVisibility(View.GONE);
		}
		if (ll_bgPic.getVisibility() == View.VISIBLE) {
			ll_bgPic.setVisibility(View.GONE);
		}

	}

	private void openColorPickDialog() {
		new ColorPickerDialog(MainActivity.this, new OnColorChangedListener() {
			@Override
			public void colorChanged(int color) {
				pv.setPaintColor(color);
				sf.setColor(color);
			}
		}, Color.BLUE).show();
	}

	private void savePic() {

		Bitmap bm = sf.getBitmap();
		boolean b = BitmapUtil.saveBitmap(bm, path);
		if (b) {
			Toast.makeText(getApplicationContext(), "保存成功", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(getApplicationContext(), "保存失败", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case 10:
			if (resultCode == Activity.RESULT_CANCELED)
				return;
			if (data == null)
				return;
			Uri uri = data.getData();
			if (uri == null)
				return;
			getBm(uri);
			break;

		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getBm(final Uri uri) {

		new AsyncTask<Void, Void, Bitmap>() {
			@Override
			protected Bitmap doInBackground(Void... params) {
				String[] proj = { MediaStore.Images.Media.DATA };
				@SuppressWarnings("deprecation")
				Cursor actualimagecursor = managedQuery(uri, proj, null, null,
						null);
				int actual_image_column_index = actualimagecursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualimagecursor.moveToFirst();
				String img_path = actualimagecursor
						.getString(actual_image_column_index);
				File file = new File(img_path);
				if (file.exists()) {
					// System.out.println("图片路径为：" + img_path);
					Bitmap bm = BitmapUtil.loadFromSdCard(img_path);
					bm = BitmapUtil.zoomBitmap(bm, width, height);
					return bm;
				}
				return null;
			}

			@Override
			protected void onPostExecute(Bitmap result) {
				if (result != null)
					sf.setBitmap(result);
			}
		}.execute();

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {

			exit();

			return false;

		} else {

			return super.onKeyDown(keyCode, event);

		}

	}

	boolean isExit = false;

	public void exit() {

		if (!isExit) {

			isExit = true;

			Toast.makeText(getApplicationContext(), "再按一次退出",
					Toast.LENGTH_SHORT).show();

			mHandler.sendEmptyMessageDelayed(0, 2000);

		} else {

			Intent intent = new Intent(Intent.ACTION_MAIN);

			intent.addCategory(Intent.CATEGORY_HOME);

			startActivity(intent);

			System.exit(0);

		}

	}

	@SuppressLint("HandlerLeak")
	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			isExit = false;

		}
	};
}
