package com.itheima45.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.MenuDetailBasePager;

/**
 * @author andong
 * ���˵�: ����������ҳ��
 */
public class InteractMenuDetailPager extends MenuDetailBasePager {

	public InteractMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mActivity);
		tv.setText("���˵�: ����������ҳ��");
		tv.setTextSize(20);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

	@Override
	public void initData() {
		System.out.println("�����˵���Ӧ��ҳ�����ݳ�ʼ����");
	}
}
