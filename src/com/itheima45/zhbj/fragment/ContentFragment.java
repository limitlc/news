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
 * ���ĵ�Fragment
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
		// �ѵ�ǰview����ע�뵽xUtils�����
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		// ��ʼ��ViewPager������
		pagerList = new ArrayList<BasePager>();
		pagerList.add(new HomePager(mActivity));
		pagerList.add(new NewsCenterPager(mActivity));
		pagerList.add(new SmartServicePager(mActivity));
		pagerList.add(new GovaffairsPager(mActivity));
		pagerList.add(new SettingsPager(mActivity));
		
		// ������
		ContentAdapter mAdapter = new ContentAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		
		// ������ѡ��ť���еİ�ťѡ�еı仯
		mRadioGruop.setOnCheckedChangeListener(this);
		
		// ����Ĭ��ѡ�е�ҳ��Ϊ: ��ҳ
		mRadioGruop.check(R.id.rb_content_fragment_home);
		// ����ҳ�����ݼ�����
		pagerList.get(0).initData();
	}
	
	/**
	 * ��ȡ��������ҳ���ʵ������
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
			// �Ѷ�ӦpagerList�����е�positionλ�õ�ҳ��Ĳ�����ӵ�ViewPager��, ������
			BasePager pager = pagerList.get(position);
			container.addView(pager.rootView); // �Ѳ�����ӵ�ViewPager��
			// pager.initData(); // ��ʼ������: �������������, �˷�����Ԥ������һ��ҳ��, ������һ��ҳ�������, ���˷��û�������
			return pager.rootView;
		}
	}

	/**
	 * checkedId ��ǰѡ�е�RadioButton��id
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
	 * �Ƿ����ò໬�˵�
	 * @param flag true ����, false ������
	 */
	private void isEnabledSlidingMenu(boolean flag) {
		// ��������ʵ��ת����MainUI��ʵ��
		MainUI mainUI = ((MainUI) mActivity);
		// ͨ��mainUI��ȡ�˵��Ŀ���������
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		if(flag) {
			// ����
			slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		} else {
			// ������
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
	 * ��ҳ�汻ѡ��ʱ�����˷���, position���ǵ�ǰҳ�������
	 */
	@Override
	public void onPageSelected(int position) {
//		System.out.println("��ǰѡ�е�ҳ��: " + position);
		pagerList.get(position).initData(); // �ѵ�ǰѡ�е�ҳ������ݼ�����
	}
	
}
