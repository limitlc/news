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
 * ��ҳ������ҳ��Ļ���
 */
public class BasePager implements OnClickListener {
	
	public Activity mActivity;
	public TextView tvTitle; // ����ؼ�
	public ImageButton ibMenu; // �˵���ť
	public FrameLayout flContent; // ��������
	public View rootView; // ��ǰҳ��Ĳ���
	public ImageButton ibSwitchListOrGrid; // �л����кͶ���

	public BasePager(Activity activity) {
		this.mActivity = activity;
		
		rootView = initView();
	}
	
	/**
	 * ��ʼ����ǰҳ��Ĳ����ļ�.
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
	 * ��ʼ������, ���า�Ǵ˷���, ʵ���Լ������ݳ�ʼ��
	 */
	public void initData() {
		
	}

	@Override
	public void onClick(View v) {
		MainUI mainUI = (MainUI) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		slidingMenu.toggle(); // ���Ʋ˵���ʾ��������, �����ǰ�˵�����ʾ, ������.
	}
}
