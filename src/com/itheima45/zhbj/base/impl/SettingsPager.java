package com.itheima45.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.BasePager;

/**
 * @author andong
 * ����ҳǩ��Ӧ��ҳ��
 */
public class SettingsPager extends BasePager {

	public SettingsPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("���ü���������");
		tvTitle.setText("����");
		ibMenu.setVisibility(View.GONE);
		
		TextView tv = new TextView(mActivity);
		tv.setText("����");
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		tv.setGravity(Gravity.CENTER);
		flContent.addView(tv);
	}
}
