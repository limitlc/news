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
	 * �����϶�ʱ, ��Ԫ�ؿ������ص�ǰ���¼�
	 * �����϶�ʱ, �����������¼�.
	 * 
	 * ��ǰҳ��Ϊ��0ҳ, ���Ҵ������һ���ʱ, �������¼�.
	 * ��ǰҳ��Ϊ���һҳ, �����Ǵ������󻬶�ʱ, �������¼�.
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// ����Ԫ��, ��Ҫ�����ҵ��¼�
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
				// ���򻬶�, ��Ԫ�ز����������¼�
				if (getCurrentItem() == 0 && diffX > 0) { // ��ǰҳ��Ϊ��0ҳ, ���Ҵ������һ���ʱ, �������¼�
					getParent().requestDisallowInterceptTouchEvent(false);
				} else if (getCurrentItem() == getAdapter().getCount() -1
						&& diffX < 0) { // ��ǰҳ��Ϊ���һҳ, �����Ǵ������󻬶�ʱ, �������¼�.
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					getParent().requestDisallowInterceptTouchEvent(true);
				}
			} else {
				// ��ǰ�����Ż���, ��Ԫ�ؿ��������¼�
				getParent().requestDisallowInterceptTouchEvent(false);
			}
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}
}
