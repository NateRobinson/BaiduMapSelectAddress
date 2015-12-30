package com.gu.test.adapter.base;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author Nate
 * @ClassName: BaseViewHolder
 * @Description: ViewHolder封装类
 * @date 2015年4月26日 上午10:03:34
 */
public class BaseViewHolder {
	/**
	 * 视图容器
	 */
	private SparseArray<View> mViews;

	/**
	 * 位置标识
	 */
	private int mPosition;

	/**
	 * 视图
	 */
	private View mConvertView;

	/**
	 * 私有化构造方法，不让外界进行使用
	 * 
	 * @param context
	 * @param parent
	 * @param layoutId
	 * @param position
	 */
	private BaseViewHolder(Context context, ViewGroup parent, int layoutId,
			int position) {
		this.mViews = new SparseArray<View>();
		this.mConvertView = LayoutInflater.from(context).inflate(layoutId,
				parent, false);
		this.mPosition = position;
		mConvertView.setTag(this);
	}

	/**
	 * @param context
	 * @param parent
	 * @param layoutId
	 * @param position
	 * @param convertView
	 * @return 静态方法获取到viewholder类实例
	 */
	public static BaseViewHolder get(Context context, ViewGroup parent,
			int layoutId, int position, View convertView) {
		if (convertView == null) {
			return new BaseViewHolder(context, parent, layoutId, position);
		} else {
			BaseViewHolder holder = (BaseViewHolder) convertView.getTag();
			// 复用视图，但是position要更新
			holder.mPosition = position;
			return holder;
		}
	}

	/**
	 * @param viewId
	 *            控件id
	 * @return 根据控件id获取到控件
	 */
	@SuppressWarnings("unchecked")
	public <T extends View> T getView(int viewId) {
		View view = mViews.get(viewId);
		if (view == null) {
			view = mConvertView.findViewById(viewId);
			mViews.put(viewId, view);
		}
		return (T) view;
	}

	/**
	 * @return 返回视图类
	 */
	public View getConvertView() {
		return mConvertView;
	}

	/**
	 * 返回当前的position
	 */
	public int getPosition() {
		return mPosition;
	}

	/**
	 * @return 设置textview相关
	 */
	public BaseViewHolder setTextView(int viewId, String content) {
		TextView tv = getView(viewId);
		tv.setText(content);
		return this;
	}

	public BaseViewHolder setTextView(int viewId, Spanned content) {
		TextView tv = getView(viewId);
		tv.setText(content);
		return this;
	}

	/**
	 * 设置textview相关
	 * 
	 * @param viewId
	 * @param content
	 * @return
	 */
	public BaseViewHolder setTextView(int viewId, SpannableString content) {
		TextView tv = getView(viewId);
		tv.setText(content);
		return this;
	}

	/**
	 * 设置textview相关
	 * 
	 * @param viewId
	 * @param content
	 * @return
	 */
	public BaseViewHolder setTextView(int viewId, int content) {
		TextView tv = getView(viewId);
		tv.setText(content);
		return this;
	}

	/**
	 * 展示资源图片
	 */
	public BaseViewHolder setResNorImg(int viewId, int resourceId) {
		ImageView iv = getView(viewId);
		iv.setImageResource(resourceId);
		return this;
	}
	// TODO 可以根据自己的需要编写更多适用的方法。。。
}
