package com.itheima45.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.BasePager;

/**
 * @author andong
 * 政务页签对应的页面
 */
public class GovaffairsPager extends BasePager {

	public GovaffairsPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("政务加载数据了");
		tvTitle.setText("人口管理");
		ibMenu.setVisibility(View.VISIBLE);
		
		TextView tv = new TextView(mActivity);
		tv.setText("政务");
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		tv.setGravity(Gravity.CENTER);
		flContent.addView(tv);
	}
}
