package com.itheima45.zhbj.base;

import android.app.Activity;
import android.view.View;

/**
 * @author andong
 * ���˵�����ҳ��Ļ���
 */
public abstract class MenuDetailBasePager {
	
	public Activity mActivity;
	public View rootView; // ��ǰҳ��Ĳ���

	public MenuDetailBasePager(Activity activity) {
		this.mActivity = activity;
		
		rootView = initView();
	}
	
	/**
	 * ��ǰҳ��Ĳ���, ���ڳ�ȡ��ҳ�沼�ֶ���һ��, �������ʵ�ִ˷���, �����Լ��Ĳ���, ������.
	 * @return
	 */
	public abstract View initView();
	
	/**
	 * ���า�Ǵ˷���, ʵ���Լ������ݳ�ʼ��
	 */
	public void initData() {
		
	}
}
