package com.itheima45.zhbj.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.itheima45.zhbj.MainUI;
import com.itheima45.zhbj.R;
import com.itheima45.zhbj.base.BaseFragment;
import com.itheima45.zhbj.base.BasePager;
import com.itheima45.zhbj.base.impl.GovaffairsPager;
import com.itheima45.zhbj.base.impl.HomePager;
import com.itheima45.zhbj.base.impl.NewsCenterPager;
import com.itheima45.zhbj.base.impl.SettingsPager;
import com.itheima45.zhbj.base.impl.SmartServicePager;
import com.itheima45.zhbj.view.NoScrollViewPager;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @author andong
 * 正文的Fragment
 */
public class ContentFragment extends BaseFragment implements OnCheckedChangeListener, OnPageChangeListener {
	
	@ViewInject(R.id.vp_content_fragment)
	private NoScrollViewPager mViewPager;
	
	@ViewInject(R.id.rg_content_fragment)
	private RadioGroup mRadioGruop;
	
	private List<BasePager> pagerList;

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.content_fragment, null);
		// 把当前view对象注入到xUtils框架中
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		// 初始化ViewPager的数据
		pagerList = new ArrayList<BasePager>();
		pagerList.add(new HomePager(mActivity));
		pagerList.add(new NewsCenterPager(mActivity));
		pagerList.add(new SmartServicePager(mActivity));
		pagerList.add(new GovaffairsPager(mActivity));
		pagerList.add(new SettingsPager(mActivity));
		
		// 绑定数据
		ContentAdapter mAdapter = new ContentAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		
		// 监听单选按钮组中的按钮选中的变化
		mRadioGruop.setOnCheckedChangeListener(this);
		
		// 设置默认选中的页面为: 首页
		mRadioGruop.check(R.id.rb_content_fragment_home);
		// 把首页的数据加载了
		pagerList.get(0).initData();
	}
	
	/**
	 * 获取新闻中心页面的实例对象
	 */
	public NewsCenterPager getNewsCenterPagerInstance() {
		NewsCenterPager newsCenterPager = (NewsCenterPager) pagerList.get(1);
		return newsCenterPager;
	}
	
	class ContentAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return pagerList.size();
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
			// 把对应pagerList集合中的position位置的页面的布局添加到ViewPager中, 并返回
			BasePager pager = pagerList.get(position);
			container.addView(pager.rootView); // 把布局添加到ViewPager中
			// pager.initData(); // 初始化数据: 不能在这里加载, 此方法会预加载下一个页面, 加载下一个页面的数据, 会浪费用户的流量
			return pager.rootView;
		}
	}

	/**
	 * checkedId 当前选中的RadioButton的id
	 */
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_content_fragment_home:
			mViewPager.setCurrentItem(0);
			isEnabledSlidingMenu(false);
			break;
		case R.id.rb_content_fragment_newscenter:
			mViewPager.setCurrentItem(1);
			isEnabledSlidingMenu(true);
			break;
		case R.id.rb_content_fragment_smartservice:
			mViewPager.setCurrentItem(2);
			isEnabledSlidingMenu(true);
			break;
		case R.id.rb_content_fragment_govaffairs:
			mViewPager.setCurrentItem(3);
			isEnabledSlidingMenu(true);
			break;
		case R.id.rb_content_fragment_settings:
			mViewPager.setCurrentItem(4);
			isEnabledSlidingMenu(false);
			break;
		default:
			break;
		}
	}
	
	/**
	 * 是否启用侧滑菜单
	 * @param flag true 启用, false 不启用
	 */
	private void isEnabledSlidingMenu(boolean flag) {
		// 把上下文实例转换成MainUI的实例
		MainUI mainUI = ((MainUI) mActivity);
		// 通过mainUI获取菜单的控制器对象
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		if(flag) {
			// 可用
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// 不可用
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 当页面被选中时触发此方法, position就是当前页面的索引
	 */
	@Override
	public void onPageSelected(int position) {
//		System.out.println("当前选中的页面: " + position);
		pagerList.get(position).initData(); // 把当前选中的页面的数据加载了
	}
	
}
