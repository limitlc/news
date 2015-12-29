package com.itheima45.zhbj.base;

import android.app.Activity;
import android.view.View;

/**
 * @author andong
 * 左侧菜单详情页面的基类
 */
public abstract class MenuDetailBasePager {
	
	public Activity mActivity;
	public View rootView; // 当前页面的布局

	public MenuDetailBasePager(Activity activity) {
		this.mActivity = activity;
		
		rootView = initView();
	}
	
	/**
	 * 当前页面的布局, 由于抽取的页面布局都不一样, 子类必须实现此方法, 创建自己的布局, 并返回.
	 * @return
	 */
	public abstract View initView();
	
	/**
	 * 子类覆盖此方法, 实现自己的数据初始化
	 */
	public void initData() {
		
	}
}
