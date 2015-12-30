package com.gu.test.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import com.gu.test.R;


/**
 * 
 * @desc 带删除按钮的输入框
 * @author Nate
 * @date 2015-12-18
 */
public class ClearEditText extends EditText {

	// 回调函数
	private TextWatcherCallBack mCallback;
	// 右侧删除图标
	private Drawable mDrawable;
	private Context mContext;

	public void setCallBack(TextWatcherCallBack mCallback) {
		this.mCallback = mCallback;
	}

	public ClearEditText(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.mContext = context;
		init();
	}

	public void init() {
		mDrawable = mContext.getResources().getDrawable(
				R.mipmap.clear_normal_list);
		mCallback = null;
		// 重写了TextWatcher，在具体实现时就不用每个方法都实现，减少代码量
		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				// 更新状态，检查是否显示删除按钮
				updateCleanable(length(), true);
				// 如果有在activity中设置回调，则此处可以触发
				if (mCallback != null)
					mCallback.handleMoreTextChanged();
			}
		};
		this.addTextChangedListener(textWatcher);
		this.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// 更新状态，检查是否显示删除按钮
				updateCleanable(length(), hasFocus);
			}
		});
	}

	// 当内容不为空，而且获得焦点，才显示右侧删除按钮
	public void updateCleanable(int length, boolean hasFocus) {
		if (length() > 0 && hasFocus)
			setCompoundDrawablesWithIntrinsicBounds(null, null, mDrawable, null);
		else
			setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final int DRAWABLE_RIGHT = 2;
		// 可以获得上下左右四个drawable，右侧排第二。图标没有设置则为空。
		Drawable rightIcon = getCompoundDrawables()[DRAWABLE_RIGHT];
		if (rightIcon != null && event.getAction() == MotionEvent.ACTION_UP) {
			// 检查点击的位置是否是右侧的删除图标
			// 注意，使用getRwwX()是获取相对屏幕的位置，getX()可能获取相对父组件的位置
			int leftEdgeOfRightDrawable = getRight() - getPaddingRight()
					- rightIcon.getBounds().width();
			if (event.getRawX() >= leftEdgeOfRightDrawable) {
				setText("");
			}
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void finalize() throws Throwable {
		mDrawable = null;
		super.finalize();
	}

	public interface TextWatcherCallBack {
		void handleMoreTextChanged();
	}
}
