package com.itheima45.zhbj.base.impl.menudetail;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.itheima45.zhbj.base.MenuDetailBasePager;

/**
 * @author andong
 * ���˵�: ר�������ҳ��
 */
public class TopicMenuDetailPager extends MenuDetailBasePager {

	public TopicMenuDetailPager(Activity activity) {
		super(activity);
	}

	@Override
	public View initView() {
		TextView tv = new TextView(mActivity);
		tv.setText("���˵�: ר�������ҳ��");
		tv.setTextSize(20);
		tv.setTextColor(Color.RED);
		tv.setGravity(Gravity.CENTER);
		return tv;
	}

	@Override
	public void initData() {
		System.out.println("ר��˵���Ӧ��ҳ�����ݳ�ʼ����");
	}
}
