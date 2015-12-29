package com.itheima45.zhbj;

import com.itheima45.zhbj.fragment.ContentFragment;
import com.itheima45.zhbj.fragment.LeftMenuFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Window;

public class MainUI extends SlidingFragmentActivity {
	
	private final String LEFT_MENU_TAG = "left_menu"; // 左侧菜单Fragment的标记, 相当于控件的id, 后期可以使用tag标记找到他
	private final String MAIN_CONTENT_TAG = "main_content"; // 主界面正文的Fragment的标记

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 去除标题
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 配置主界面布局
		setContentView(R.layout.main);
		
		// 配置左侧菜单布局
		setBehindContentView(R.layout.left_menu);
		
		// 配置菜单可用的模式: 左侧菜单可用
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		
		// 配置菜单可以拖拽的区域: 整个屏幕都可以拖拽
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		// 配置主界面留在屏幕上的宽度: 200
		slidingMenu.setBehindOffset(200);
		
		initFragment();
	}

	/**
	 * 初始化Fragment对象
	 */
	private void initFragment() {
		// 获取FragmentManager管理器对象
		FragmentManager fm = getSupportFragmentManager();
		
		// 开启事务
		FragmentTransaction ft = fm.beginTransaction(); // 获取事务操作对象
		
		// 替换Fragment
		ft.replace(R.id.fl_left_menu, new LeftMenuFragment(), LEFT_MENU_TAG); // 把帧布局替换成左侧菜单Fragment对象
		ft.replace(R.id.fl_main, new ContentFragment(), MAIN_CONTENT_TAG); // 把帧布局替换成主界面正文Fragment对象
		
		// 提交事务
		ft.commit();
	}
	
	/**
	 * 获取左侧菜单的Fragment的对象实例
	 * @return
	 */
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager fm = getSupportFragmentManager();
		LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(LEFT_MENU_TAG);
		return fragment;
	}
	
	/**
	 * 获取右侧正文的Fragment的对象实例
	 * @return
	 */
	public ContentFragment getContentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		ContentFragment fragment = (ContentFragment) fm.findFragmentByTag(MAIN_CONTENT_TAG);
		return fragment;
	}
}
