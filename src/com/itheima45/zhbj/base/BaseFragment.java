package com.itheima45.zhbj.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * @author andong
 * ���е�Fragment�Ļ���
 * 			�����ĳ�ȡ
 * 			��ʼ�����ֵķ�����ȡ: ����
 * 			��ʼ�����ݵķ�����ȡ: ��ѡ
 */
public abstract class BaseFragment extends Fragment {
	
	public Activity mActivity; // �����Ķ���

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mActivity = getActivity();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = initView();
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		// Activity�Ѿ���ʼ�������, ��ǰ��Ҫ��ʼ��Fragment��������
		initData();
	}
	
	/**
	 * ��ʼ��Fragment�Ĳ���
	 * @return
	 */
	public abstract View initView();
	
	/**
	 * ��ʼ������, ���า�Ǵ˷���, ��ʵ���Լ������ݳ�ʼ������
	 */
	public void initData() {
		
	}
}
