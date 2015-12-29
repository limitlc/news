package com.itheima45.zhbj.base;

import com.itheima45.zhbj.MainUI;
import com.itheima45.zhbj.R;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author andong
 * 主页面的五个页面的基类
 */
public class BasePager implements OnClickListener {
	
	public Activity mActivity;
	public TextView tvTitle; // 标题控件
	public ImageButton ibMenu; // 菜单按钮
	public FrameLayout flContent; // 正文内容
	public View rootView; // 当前页面的布局
	public ImageButton ibSwitchListOrGrid; // 切换单列和多列

	public BasePager(Activity activity) {
		this.mActivity = activity;
		
		rootView = initView();
	}
	
	/**
	 * 初始化当前页面的布局文件.
	 * @return
	 */
	public View initView() {
		View view = View.inflate(mActivity, R.layout.base_pager, null);
		tvTitle = (TextView) view.findViewById(R.id.tv_title_bar_title);
		ibMenu = (ImageButton) view.findViewById(R.id.ib_title_bar_menu);
		flContent = (FrameLayout) view.findViewById(R.id.fl_base_pager_content);
		ibSwitchListOrGrid = (ImageButton) view.findViewById(R.id.ib_title_bar_switch_list_or_grid);
		ibMenu.setOnClickListener(this);
		return view;
	}
	
	/**
	 * 初始化数据, 子类覆盖此方法, 实现自己的数据初始化
	 */
	public void initData() {
		
	}

	@Override
	public void onClick(View v) {
		MainUI mainUI = (MainUI) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		slidingMenu.toggle(); // 控制菜单显示还是隐藏, 如果当前菜单是显示, 就隐藏.
	}
}
