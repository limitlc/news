package com.itheima45.zhbj.base.impl;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.BasePager;

/**
 * @author andong
 * ��ҳҳǩ��Ӧ��ҳ��
 */
public class HomePager extends BasePager {

	public HomePager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("��ҳ����������");
		tvTitle.setText("�ǻ۱���");
		ibMenu.setVisibility(View.GONE);
		
		TextView tv = new TextView(mActivity);
		tv.setText("��ҳ");
		tv.setTextColor(Color.RED);
		tv.setTextSize(25);
		tv.setGravity(Gravity.CENTER);
		flContent.addView(tv);
	}
}
