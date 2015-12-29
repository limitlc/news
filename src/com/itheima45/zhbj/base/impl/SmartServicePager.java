package com.itheima45.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.BasePager;

/**
 * @author andong
 * �ǻ۷���ҳǩ��Ӧ��ҳ��
 */
public class SmartServicePager extends BasePager {

	public SmartServicePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("�ǻ۷������������");
		tvTitle.setText("����");
		ibMenu.setVisibility(View.VISIBLE);
		
		TextView tv = new TextView(mActivity);
		tv.setText("�ǻ۷���");
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		tv.setGravity(Gravity.CENTER);
		flContent.addView(tv);
	}
}
