package com.itheima45.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.BasePager;

/**
 * @author andong
 * 首页页签对应的页面
 */
public class HomePager extends BasePager {

	public HomePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("首页加载数据了");
		tvTitle.setText("智慧北京");
		ibMenu.setVisibility(View.GONE);
		
		TextView tv = new TextView(mActivity);
		tv.setText("首页");
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		tv.setGravity(Gravity.CENTER);
		flContent.addView(tv);
	}
}
