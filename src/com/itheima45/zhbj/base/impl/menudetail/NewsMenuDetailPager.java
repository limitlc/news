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
 * 左侧菜单: 新闻的详情页面
 */
public class NewsMenuDetailPager extends MenuDetailBasePager implements OnPageChangeListener {
	
	@ViewInject(R.id.tpi_news_menu_detail)
	private TabPageIndicator mIndicator;
	
	@ViewInject(R.id.vp_news_menu_detail)
	private ViewPager mViewPager;

	private List<NewsMenuTab> newsMenuList; // 页签对应的数据
	private List<TabDetailPager> mTabDetailPagerList; // 页签对应的页面

	public NewsMenuDetailPager(Activity activity, NewsCenterMenu menuItemBean) {
		super(activity);
		
		newsMenuList = menuItemBean.children;
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.news_menu_detail, null);
		ViewUtils.inject(this, view); // 把当前view对象注入到xutils框架中
		return view;
	}

	@Override
	public void initData() {
		System.out.println("新闻菜单对应的页面数据初始化了");
		
		// 准备数据
		mTabDetailPagerList = new ArrayList<TabDetailPager>();
		for (int i = 0; i < newsMenuList.size(); i++) {
			mTabDetailPagerList.add(new TabDetailPager(mActivity, newsMenuList.get(i), i == 0));
		}
		
		NewsMenuAdapter mAdapter = new NewsMenuAdapter();
		mViewPager.setAdapter(mAdapter);
		// 当把ViewPager设置给Indicator时, 自己的OnPageChangeListener事件不可用了, 会被Indicator来响应.
		// mViewPager.setOnPageChangeListener(this);
		
		// 给TabPageIndicator关联ViewPager
		// 当mIndicator和ViewPager关联起来之后, mIndicator的数据就由关联上的ViewPager的adapter来提供
		mIndicator.setViewPager(mViewPager);
		mIndicator.setOnPageChangeListener(this);
	}
	
	class NewsMenuAdapter extends PagerAdapter {
		
		/**
		 * 此方法是给mIndicator提供的页签数据
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
			pager.initData(); // 加载数据
			return pager.rootView;
		}
	}
	
	@OnClick(R.id.btn_news_menu_detail_next_tab)
	public void nextTab(View v) {
		// 向下移动一页
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
			// 当前是北京页签, 可以把左侧菜单拖拽出来
			((MainUI) mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// 当前不是北京页签, 不可以把左侧菜单拖拽出来, 应该响应ViewPager页面切换的事件
			((MainUI) mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}
}
