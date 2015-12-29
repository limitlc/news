package com.itheima45.zhbj.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itheima45.zhbj.MainUI;
import com.itheima45.zhbj.base.BasePager;
import com.itheima45.zhbj.base.MenuDetailBasePager;
import com.itheima45.zhbj.base.impl.menudetail.InteractMenuDetailPager;
import com.itheima45.zhbj.base.impl.menudetail.NewsMenuDetailPager;
import com.itheima45.zhbj.base.impl.menudetail.PhotosMenuDetailPager;
import com.itheima45.zhbj.base.impl.menudetail.TopicMenuDetailPager;
import com.itheima45.zhbj.domain.NewsCenterBean;
import com.itheima45.zhbj.domain.NewsCenterBean.NewsCenterMenu;
import com.itheima45.zhbj.fragment.LeftMenuFragment;
import com.itheima45.zhbj.utils.CacheUtils;
import com.itheima45.zhbj.utils.ConstantUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * @author andong
 * ��������ҳǩ��Ӧ��ҳ��
 */
public class NewsCenterPager extends BasePager {
	
	private List<MenuDetailBasePager> pagerList; // ���˵���Ӧ������ҳ��
	private List<NewsCenterMenu> leftMenuDataList; // ���˵�������

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("�������ļ���������");
		tvTitle.setText("����");
		ibMenu.setVisibility(View.VISIBLE);
		
		// ��������·֮ǰ, ����ǰ������ȡ����, չʾ�ڽ�����.
		String json = CacheUtils.getString(mActivity, ConstantUtils.NEWSCENTER_URL, null);
		if(!TextUtils.isEmpty(json)) {
			// �������ݲ�Ϊnull, չʾ��������
			processData(json);
		}
		
		getDataFromNet();
	}

	/**
	 * �������ȡ����
	 */
	private void getDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		// RequestCallBack<T> �еķ����Ƕ�����������ɹ���, ���ػ��������ݵ�����
		httpUtils.send(HttpMethod.GET, ConstantUtils.NEWSCENTER_URL, new RequestCallBack<String>() {

			/**
			 * ������ɹ�ʱ�ص��˷���
			 * responseInfo ��������Ϣ
			 */
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("��������ҳ����������ɹ�, ����: " + responseInfo.result);
				
				// �����ݻ��浽����, Ϊ���´δ򿪳���ȥ��ʾ��ʷ������
				CacheUtils.putString(mActivity, ConstantUtils.NEWSCENTER_URL, responseInfo.result);
				
				processData(responseInfo.result);
			}

			/**
			 * ������ʧ��ʱ�ص��˷���
			 * msg ������Ϣ
			 */
			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("��������ҳ����������ʧ��, ����: " + msg);
			}
		});
	}

	/**
	 * ����ͽ�������
	 * @param result
	 */
	protected void processData(String json) {
		Gson gson = new Gson();
		NewsCenterBean bean = gson.fromJson(json, NewsCenterBean.class);

		// ��ʼ�����˵�������
		leftMenuDataList = bean.data; // ���˵�������

		// ��ʼ�����˵���Ӧ��ҳ��, ���ĸ�ҳ��: ����, ר��, ��ͼ, ����
		pagerList = new ArrayList<MenuDetailBasePager>();
		pagerList.add(new NewsMenuDetailPager(mActivity, leftMenuDataList.get(0)));
		pagerList.add(new TopicMenuDetailPager(mActivity));
		pagerList.add(new PhotosMenuDetailPager(mActivity));
		pagerList.add(new InteractMenuDetailPager(mActivity));
		
		// �����˵����ݴ��ݸ�LeftMenuFragment������
		MainUI mainUI = ((MainUI) mActivity);
		LeftMenuFragment leftMenuFragment = mainUI.getLeftMenuFragment();
		leftMenuFragment.setMenuDataList(leftMenuDataList);
	}
	
	/**
	 * ���������л���ǰ��ҳ��
	 * @param position
	 */
	public void switchCurrentPager(int position) {
		// ���õ�ǰ�ı���
		String title = leftMenuDataList.get(position).title;
		tvTitle.setText(title);
		
		MenuDetailBasePager pager = pagerList.get(position);
		flContent.removeAllViews(); // ������е��Ӳ���
		flContent.addView(pager.rootView);
		pager.initData();
		
		if(position == 2) {
			// ��ǰ����ͼ
			ibSwitchListOrGrid.setVisibility(View.VISIBLE);
			ibSwitchListOrGrid.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PhotosMenuDetailPager pager = (PhotosMenuDetailPager) pagerList.get(2);
					pager.switchListOrGrid(ibSwitchListOrGrid); // �л��б��������ҳ��
				}
			});
		} else {
			ibSwitchListOrGrid.setVisibility(View.GONE);
		}
	}
}
