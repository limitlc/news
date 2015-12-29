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
	
	private final String LEFT_MENU_TAG = "left_menu"; // ���˵�Fragment�ı��, �൱�ڿؼ���id, ���ڿ���ʹ��tag����ҵ���
	private final String MAIN_CONTENT_TAG = "main_content"; // ���������ĵ�Fragment�ı��

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// ȥ������
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// ���������沼��
		setContentView(R.layout.main);
		
		// �������˵�����
		setBehindContentView(R.layout.left_menu);
		
		// ���ò˵����õ�ģʽ: ���˵�����
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT);
		
		// ���ò˵�������ק������: ������Ļ��������ק
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		// ����������������Ļ�ϵĿ��: 200
		slidingMenu.setBehindOffset(200);
		
		initFragment();
	}

	/**
	 * ��ʼ��Fragment����
	 */
	private void initFragment() {
		// ��ȡFragmentManager����������
		FragmentManager fm = getSupportFragmentManager();
		
		// ��������
		FragmentTransaction ft = fm.beginTransaction(); // ��ȡ�����������
		
		// �滻Fragment
		ft.replace(R.id.fl_left_menu, new LeftMenuFragment(), LEFT_MENU_TAG); // ��֡�����滻�����˵�Fragment����
		ft.replace(R.id.fl_main, new ContentFragment(), MAIN_CONTENT_TAG); // ��֡�����滻������������Fragment����
		
		// �ύ����
		ft.commit();
	}
	
	/**
	 * ��ȡ���˵���Fragment�Ķ���ʵ��
	 * @return
	 */
	public LeftMenuFragment getLeftMenuFragment() {
		FragmentManager fm = getSupportFragmentManager();
		LeftMenuFragment fragment = (LeftMenuFragment) fm.findFragmentByTag(LEFT_MENU_TAG);
		return fragment;
	}
	
	/**
	 * ��ȡ�Ҳ����ĵ�Fragment�Ķ���ʵ��
	 * @return
	 */
	public ContentFragment getContentFragment() {
		FragmentManager fm = getSupportFragmentManager();
		ContentFragment fragment = (ContentFragment) fm.findFragmentByTag(MAIN_CONTENT_TAG);
		return fragment;
	}
}
