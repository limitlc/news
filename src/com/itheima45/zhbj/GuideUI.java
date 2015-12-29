package com.itheima45.zhbj;

import java.util.ArrayList;
import java.util.List;

import com.itheima45.zhbj.utils.CacheUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

/**
 * @author andong
 * ����ҳ��
 */
public class GuideUI extends Activity implements OnClickListener {

	private List<ImageView> imageViewList;
	private LinearLayout llPointGroup; // �����
	private View redPointView; // ��ɫ�ĵ�
	private int basicWidth;// ��ļ��
	private Button btnStartExperience; // ��ʼ����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // ȥ������, ������setContentView֮ǰ����
		setContentView(R.layout.guide);
		
		init();
	}

	/**
	 * ��ʼ���ؼ�
	 */
	private void init() {
		ViewPager mViewPager = (ViewPager) findViewById(R.id.vp_guide);
		btnStartExperience = (Button) findViewById(R.id.btn_start_experience);
		llPointGroup = (LinearLayout) findViewById(R.id.ll_guide_point_group);
		redPointView = findViewById(R.id.view_guide_red_point);
		
		btnStartExperience.setOnClickListener(this);
		
		// ׼������
		initData();
		
		GuidePagerAdapter mAdapter = new GuidePagerAdapter();
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
		
		// measure -> layout -> draw
		
		// �����ͼ���۲���, �۲쵱�������ֵ�layoutʱ���¼�
		redPointView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			
			/**
			 * ��ȫ�ֿ�ʼ����layoutʱ�ص��˷���
			 */
			@Override
			public void onGlobalLayout() {
				// �˷���ֻ��Ҫִ��һ�ξͿ���: �ѵ�ǰ�ļ����¼�����ͼ�����Ƴ���, �Ժ�Ͳ����ڻص����¼���.
				redPointView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				
				// ��ļ�� = ��1�������� - ��0��������;
				basicWidth = llPointGroup.getChildAt(1).getLeft() - llPointGroup.getChildAt(0).getLeft();
				System.out.println("��ļ��: " + basicWidth);
			}
		});
	}
	
	/**
	 * ��ʼ������
	 */
	private void initData() {
		int[] imageResIDs = {
				R.drawable.guide_1,
				R.drawable.guide_2,
				R.drawable.guide_3
		};
		
		imageViewList = new ArrayList<ImageView>();
		ImageView iv;
		for (int i = 0; i < imageResIDs.length; i++) {
			iv = new ImageView(this);
			iv.setBackgroundResource(imageResIDs[i]);
			imageViewList.add(iv);
			
			// �����Բ��������һ����ɫ�ĵ�
			View view = new View(this);
			view.setBackgroundResource(R.drawable.point_normal);
			LayoutParams params = new LayoutParams(10, 10);
			if(i != 0) {
				params.leftMargin = 10;
			}
			view.setLayoutParams(params);
			llPointGroup.addView(view);
		}
	}

	class GuidePagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return imageViewList.size();
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
			// ��ViewPager�����һ��ImageView
			ImageView iv = imageViewList.get(position);
			container.addView(iv);
			
			// ����ӵ�ImageView���ػ�ȥ
			return iv;
		}
	}
	
	class MyOnPageChangeListener implements OnPageChangeListener {

		/**
		 * ��ҳ�����ʱ�����˷���
		 * @param position ��ǰ���ڱ�ѡ�е�position
		 * @param positionOffset ҳ���ƶ��ı�ֵ
		 * @param positionOffsetPixels ҳ���ƶ�������
		 */
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
			
			// �������ƶ��ľ��� = ��� * ��ֵ;
			int leftMargin = (int) (basicWidth * (position + positionOffset));
//			System.out.println("��ɫ�ĵ��ƶ��ľ���: " + leftMargin + ", ��ǰҳ�������: " + position + ", ��ֵ: " + positionOffset);
			
			RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) redPointView.getLayoutParams();
			lp.leftMargin = leftMargin;
			redPointView.setLayoutParams(lp);
		}

		/**
		 * ��ҳ��ѡ��ʱ�����˷���
		 */
		@Override
		public void onPageSelected(int position) {
			if(position == imageViewList.size() -1) {
				btnStartExperience.setVisibility(View.VISIBLE);
			} else {
				btnStartExperience.setVisibility(View.GONE);
			}
		}

		/**
		 * ��ҳ�����״̬�ı�ʱ�����˷���
		 */
		@Override
		public void onPageScrollStateChanged(int state) {
			
		}
	}

	@Override
	public void onClick(View v) {
		// ��SharedPreferences�д洢һ���򿪹��ı��
		CacheUtils.putBoolean(this, WelcomeUI.IS_OPEN_MAIN_PAGER_KEY, true);
		
		// ��ת����ҳ��
		startActivity(new Intent(this, MainUI.class));
		
		// ���Լ��ص�
		finish();
	}
}
