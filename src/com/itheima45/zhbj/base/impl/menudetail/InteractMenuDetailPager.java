package com.itheima45.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.MenuDetailBasePager;

/**
 * @author andong
 * 左侧菜单: 互动的详情页面
 */
public class InteractMenuDetailPager extends MenuDetailBasePager {

	public InteractMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mActivity);
		tv.setText("左侧菜单: 互动的详情页面");
		tv.setTextSize(20);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

	@Override
	public void initData() {
		System.out.println("互动菜单对应的页面数据初始化了");
	}
}
