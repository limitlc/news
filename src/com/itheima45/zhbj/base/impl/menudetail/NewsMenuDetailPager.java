package com.itheima45.zhbj.base.impl.menudetail;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;

import com.itheima45.zhbj.MainUI;
import com.itheima45.zhbj.R;
import com.itheima45.zhbj.base.MenuDetailBasePager;
import com.itheima45.zhbj.domain.NewsCenterBean.NewsCenterMenu;
import com.itheima45.zhbj.domain.NewsCenterBean.NewsMenuTab;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.viewpagerindicator.TabPageIndicator;

/**
 * @author andong
 * ���˵�: ���ŵ�����ҳ��
 */
public class NewsMenuDetailPager extends MenuDetailBasePager implements OnPageChangeListener {
	
	@ViewInject(R.id.tpi_news_menu_detail)
	private TabPageIndicator mIndicator;
	
	@ViewInject(R.id.vp_news_menu_detail)
	private ViewPager mViewPager;

	private List<NewsMenuTab> newsMenuList; // ҳǩ��Ӧ������
	private List<TabDetailPager> mTabDetailPagerList; // ҳǩ��Ӧ��ҳ��

	public NewsMenuDetailPager(Activity activity, NewsCenterMenu menuItemBean) {
		super(activity);
		
		newsMenuList = menuItemBean.children;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.news_menu_detail, null);
		ViewUtils.inject(this, view); // �ѵ�ǰview����ע�뵽xutils�����
		return view;
	}

	@Override
	public void initData() {
		System.out.println("���Ų˵���Ӧ��ҳ�����ݳ�ʼ����");
		
		// ׼������
		mTabDetailPagerList = new ArrayList<TabDetailPager>();
		for (int i = 0; i < newsMenuList.size(); i++) {
			mTabDetailPagerList.add(new TabDetailPager(mActivity, newsMenuList.get(i), i == 0));
		}
		
		NewsMenuAdapter mAdapter = new NewsMenuAdapter();
		mViewPager.setAdapter(mAdapter);
		// ����ViewPager���ø�Indicatorʱ, �Լ���OnPageChangeListener�¼���������, �ᱻIndicator����Ӧ.
		// mViewPager.setOnPageChangeListener(this);
		
		// ��TabPageIndicator����ViewPager
		// ��mIndicator��ViewPager��������֮��, mIndicator�����ݾ��ɹ����ϵ�ViewPager��adapter���ṩ
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);
	}
	
	class NewsMenuAdapter extends PagerAdapter {
		
		/**
		 * �˷����Ǹ�mIndicator�ṩ��ҳǩ����
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			return newsMenuList.get(position).title;
		}

		@Override
		public int getCount() {
			return mTabDetailPagerList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			TabDetailPager pager = mTabDetailPagerList.get(position);
			container.addView(pager.rootView);
			pager.initData(); // ��������
			return pager.rootView;
		}
	}
	
	@OnClick(R.id.btn_news_menu_detail_next_tab)
	public void nextTab(View v) {
		// �����ƶ�һҳ
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int position) {
		System.out.println("onPageSelected, position: " + position);
		if(position == 0) {
			// ��ǰ�Ǳ���ҳǩ, ���԰����˵���ק����
			((MainUI) mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// ��ǰ���Ǳ���ҳǩ, �����԰����˵���ק����, Ӧ����ӦViewPagerҳ���л����¼�
			((MainUI) mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
}
