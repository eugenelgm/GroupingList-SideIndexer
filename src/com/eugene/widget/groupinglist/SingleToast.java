package com.eugene.widget.groupinglist;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.BadTokenException;
import android.widget.LinearLayout.LayoutParams;

public abstract class SingleToast {

	private Context context;
	private WindowManager windowManager;
	private View dialogPosition;
	private Handler handler = new Handler();
	private RemoveWindow removeWindow = new RemoveWindow();
	private int centerX = 0;
	private int centerY = 0;

	/**
	 * 
	 * @param context
	 * @param resId
	 *            - resource id for inflating
	 */
	public void initailizeToast(Context context, int resId) {
		initailizeToast(context, resId, 0, 0);
	}

	/**
	 * 
	 * @param context
	 * @param resId
	 * @param centerX
	 *            - default : center
	 * @param centerY
	 *            - default : center
	 */
	public void initailizeToast(Context context, int resId, int centerX, int centerY) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		dialogPosition = inflater.inflate(resId, null);
		dialogPosition.setVisibility(View.INVISIBLE);
		this.centerX = centerX;
		this.centerY = centerY;
		initailize();
	}

	private void initailize() {
		windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		handler.post(new Runnable() {
			public void run() {
				WindowManager.LayoutParams lp = new WindowManager.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, centerX, centerY,
						WindowManager.LayoutParams.TYPE_APPLICATION, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
								| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
				try {
					windowManager.addView(dialogPosition, lp);
				} catch (IllegalStateException ie) {
					windowManager.updateViewLayout(dialogPosition, lp);
				} catch (BadTokenException be) {
					Log.e("GROUPPINGLIST", "error", be);
					return;
				}
			}
		});
	}

	public View getView() {
		return dialogPosition;
	}

	public void setVisibility(int visibility) {
		dialogPosition.setVisibility(visibility);
	}

	public void removeToast(int delay) {
		handler.removeCallbacks(removeWindow);
		handler.postDelayed(removeWindow, 1500);
	}

	public void destory() {
		try {
			windowManager.removeView(dialogPosition);
		} catch (Exception e) {
			Log.d("GROUPPINGLIST", "dupliation remove : " + dialogPosition.hashCode());
		}
	}

	abstract public void removeWindow();

	private final class RemoveWindow implements Runnable {
		public void run() {
			removeWindow();
		}
	}
}
