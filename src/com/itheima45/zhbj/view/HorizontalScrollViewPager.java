package com.itheima45.zhbj.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class HorizontalScrollViewPager extends ViewPager {

	private int downX;
	private int downY;

	public HorizontalScrollViewPager(Context context) {
		super(context);
	}

	public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 上下拖动时, 父元素可以拦截当前的事件
	 * 左右拖动时, 不可以拦截事件.
	 * 
	 * 当前页面为第0页, 并且从左向右滑动时, 不拦截事件.
	 * 当前页面为最后一页, 并且是从右向左滑动时, 不拦截事件.
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// 请求父元素, 不要拦截我的事件
			getParent().requestDisallowInterceptTouchEvent(true);

			downX = (int) ev.getX();
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			int moveX = (int) ev.getX();
			int moveY = (int) ev.getY();

			int diffX = moveX - downX;
			int diffY = moveY - downY;
			
			if(Math.abs(diffX) > Math.abs(diffY)) {
				// 横向滑动, 父元素不可以拦截事件
				if (getCurrentItem() == 0 && diffX > 0) { // 当前页面为第0页, 并且从左向右滑动时, 不拦截事件
					getParent().requestDisallowInterceptTouchEvent(false);
				} else if (getCurrentItem() == getAdapter().getCount() -1
						&& diffX < 0) { // 当前页面为最后一页, 并且是从右向左滑动时, 不拦截事件.
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			} else {
				// 当前是竖着滑动, 父元素可以拦截事件
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
}
