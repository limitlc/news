package com.itheima45.zhbj.base.impl.menudetail;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap.Config;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itheima45.zhbj.MainUI;
import com.itheima45.zhbj.NewsDetailUI;
import com.itheima45.zhbj.R;
import com.itheima45.zhbj.base.MenuDetailBasePager;
import com.itheima45.zhbj.domain.NewsCenterBean.NewsMenuTab;
import com.itheima45.zhbj.domain.TabDetailBean;
import com.itheima45.zhbj.domain.TabDetailBean.News;
import com.itheima45.zhbj.domain.TabDetailBean.TopNew;
import com.itheima45.zhbj.utils.CacheUtils;
import com.itheima45.zhbj.utils.ConstantUtils;
import com.itheima45.zhbj.view.HorizontalScrollViewPager;
import com.itheima45.zhbj.view.RefreshListView;
import com.itheima45.zhbj.view.RefreshListView.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;

/**
 * @author andong
 * ҳǩ��Ӧ������ҳ��
 */
public class TabDetailPager extends MenuDetailBasePager implements OnPageChangeListener, OnRefreshListener, OnItemClickListener {
	
	@ViewInject(R.id.hsvp_tab_detail_top_news)
	private HorizontalScrollViewPager mViewPager;

	@ViewInject(R.id.tv_tab_detail_description)
	private TextView tvDescription;
	
	@ViewInject(R.id.ll_tab_detail_point_group)
	private LinearLayout llPointGroup;
	
	@ViewInject(R.id.rlv_tab_detail_list_news)
	private RefreshListView mListView;
	
	private NewsMenuTab newsMenuTab;
	private String url; // ��ǰҳ�����ݵķ��ʵ�ַ
	
	private List<TopNew> topNewsList; // ������������

	private BitmapUtils bitmapUtils; // ͼƬ��������

	private int previousEnabledPointPosition; // ǰһ�����õĵ������
	
	private boolean isEnabledSlidingMenu = false; // �Ƿ����ò໬�˵�, Ĭ���ǲ�����

	private List<News> newsList; // �б����ŵ�����

	private boolean isPullDownRefresh = false; // �Ƿ���������ˢ����

	private String moreUrl; // �����url
	private boolean isLoadingMore = false; // �Ƿ����ڼ��ظ�����

	private NewsAdapter newsListAdapter;
	
	// �Ѷ����ŵ�id����
	private final String READABLE_NEWS_ID_ARRAY_KEY = "readable_news_id_array_key";

	private InternalHandler mHandler; // �ڲ���handler��Ϣ������
	
	public TabDetailPager(Activity activity, NewsMenuTab newsMenuTab, boolean isEnabledSlidingMenu) {
		super(activity);
		this.newsMenuTab = newsMenuTab;
		this.isEnabledSlidingMenu = isEnabledSlidingMenu;
		
		bitmapUtils = new BitmapUtils(mActivity);
		// ����Ĭ��ͼƬ�����ص�λ
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.tab_detail, null);
		ViewUtils.inject(this, view);
		
		View topNewsView = View.inflate(mActivity, R.layout.tab_detail_topnews, null);
		ViewUtils.inject(this, topNewsView);
		
		// ��topNewsView��ӵ�ListView��ͷ����
		// mListView.addHeaderView(topNewsView);
		
		mListView.addSecondHeaderView(topNewsView);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		url = ConstantUtils.SERVER_URL + newsMenuTab.url;
		System.out.println(newsMenuTab.title + "ҳǩ����ҳ������url: " + url);
		
		String json = CacheUtils.getString(mActivity, url, null);
		if(!TextUtils.isEmpty(json)) {
			processData(json);
		}
		
		getDataFromNet();
	}

	private void getDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, url, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				if(isPullDownRefresh) {
					isPullDownRefresh = false;
					mListView.onRefreshFinish(true);
				}
				System.out.println(newsMenuTab.title + "����������ɹ�: " + responseInfo.result);
				
				// �����ݻ�������
				CacheUtils.putString(mActivity, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println(newsMenuTab.title + "����������ʧ��: " + msg);
				if(isPullDownRefresh) {
					isPullDownRefresh = false;
					mListView.onRefreshFinish(false);
				}
			}
		});
	}

	/**
	 * ����ͽ���json����
	 * @param result
	 */
	protected void processData(String result) {
		TabDetailBean bean = parserJson(result);
		
		if(!isLoadingMore) {
			// ��ʼ���������ŵ����ݺͽ���
			topNewsList = bean.data.topnews; 
			TopNewsAdapter topNewsAdapter = new TopNewsAdapter();
			mViewPager.setAdapter(topNewsAdapter);
			mViewPager.setOnPageChangeListener(this);

			// ��ʼ���������ŵ������͵�
			llPointGroup.removeAllViews();
			View v;
			LayoutParams params;
			for (int i = 0; i < topNewsList.size(); i++) {
				v = new View(mActivity);
				v.setBackgroundResource(R.drawable.topnews_point_bg);
				params = new LayoutParams(5, 5);
				if(i != 0) {
					params.leftMargin = 10;
				}
				v.setLayoutParams(params);
				v.setEnabled(false);
				llPointGroup.addView(v);
			}
			previousEnabledPointPosition = 0;
			tvDescription.setText(topNewsList.get(previousEnabledPointPosition).title); // ����Ĭ�ϵ�����Ϊ��һ��ͼƬ
			llPointGroup.getChildAt(previousEnabledPointPosition).setEnabled(true);
			
			// ��ʼ���б����ŵ����ݺͽ���
			newsList = bean.data.news;
			newsListAdapter = new NewsAdapter();
			mListView.setAdapter(newsListAdapter);
		} else {
			isLoadingMore = false;
			
			// ���б�������ȡ����, �ټӵ���ǰ�ļ�����, ��ˢ������
			List<News> tempNewsList = bean.data.news;
			
			newsList.addAll(tempNewsList); // �����е�������ӵ������ļ�����
			newsListAdapter.notifyDataSetChanged();
		}
		
		
		// ------------��ʼ�ֲ�ͼѭ������--------------
		
		if(mHandler == null) {
			mHandler = new InternalHandler();
		} else {
			// ǰ�߿����Ѿ������������Ϣ�ڶ����еȴ�ִ��.
			
			// �Ѷ����е�������Ϣ������ȫ�����������
			mHandler.removeCallbacksAndMessages(null);
		}
		
		// ��ʱ4����, ִ��InternalRunnable�������е�run����
		mHandler.postDelayed(new InternalRunnable(), 4000);
	}

	/**
	 * ʹ��Gson������json����, ����װ��һ��TabDetailBean����.
	 * @param json
	 * @return
	 */
	private TabDetailBean parserJson(String json) {
		Gson gson = new Gson();
		TabDetailBean bean = gson.fromJson(json, TabDetailBean.class);
		
		// �Ѹ����Urlȡ����
		moreUrl = bean.data.more;
		if(TextUtils.isEmpty(moreUrl)) {
			moreUrl = null;
		} else {
			moreUrl = ConstantUtils.SERVER_URL + moreUrl;
		}
		return bean;
	}
	
	class NewsAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return newsList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NewsItemViewHolder mHolder = null;
			
			if(convertView == null) {
				convertView = View.inflate(mActivity, R.layout.news_list_item, null);
				mHolder = new NewsItemViewHolder();
				mHolder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_news_list_item_icon);
				mHolder.tvTitle = (TextView) convertView.findViewById(R.id.tv_news_list_item_title);
				mHolder.tvTime = (TextView) convertView.findViewById(R.id.tv_news_list_item_time);
				convertView.setTag(mHolder);
			} else {
				mHolder = (NewsItemViewHolder) convertView.getTag();
			}
			
			News bean = newsList.get(position);
			mHolder.tvTitle.setText(bean.title);
			mHolder.tvTime.setText(bean.pubdate);
			
			// �жϵ�ǰ�Ƿ����Ѷ�������
			String readableIDArray = CacheUtils.getString(mActivity, READABLE_NEWS_ID_ARRAY_KEY, null);
			if(!TextUtils.isEmpty(readableIDArray)
					&& readableIDArray.contains(bean.id)) {
				mHolder.tvTitle.setTextColor(Color.GRAY);
			} else {
				mHolder.tvTitle.setTextColor(Color.BLACK);
			}
			
			// ����Ĭ��ͼƬ
			mHolder.ivIcon.setImageResource(R.drawable.news_pic_default);

			// ���������е�ͼƬ
			bitmapUtils.display(mHolder.ivIcon, bean.listimage);
			return convertView;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return 0;
		}
	}
	
	public class NewsItemViewHolder {
		
		public ImageView ivIcon;
		public TextView tvTitle;
		public TextView tvTime;
	}
	
	class TopNewsAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return topNewsList.size();
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
			ImageView iv = new ImageView(mActivity);
			iv.setScaleType(ScaleType.FIT_XY); // ָ����ͼƬ����Ϊ�ؼ��Ŀ�͸�
			iv.setImageResource(R.drawable.topnews_item_default); // ����Ĭ����ʾ��ͼƬ, ����û����������֮ǰ��ʾ
			iv.setOnTouchListener(new TopNewsItemTouchListener());
			
			TopNew topNew = topNewsList.get(position);
			
			/**
			 * container �����uri��������������ͼƬ, ���ø�container��չʾ.
			 * uri ͼƬ�������ַ
			 */
			bitmapUtils.display(iv, topNew.topimage);
			
			container.addView(iv);
			return iv;
		}
	}
	
	class TopNewsItemTouchListener implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				System.out.println("��ָ����, ֹͣ������");
				mHandler.removeCallbacksAndMessages(null); // �Ƴ���Ϣ���������е��������Ϣ.
				break;
			case MotionEvent.ACTION_CANCEL: // �¼���ʧ
				System.out.println("�¼���ʧ, ��ʼ������");
				mHandler.postDelayed(new InternalRunnable(), 4000);
				break;
			case MotionEvent.ACTION_UP:
				System.out.println("��ָ̧��, ��ʼ������");
				mHandler.postDelayed(new InternalRunnable(), 4000);
				break;
			default:
				break;
			}
			
			return true;
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

	@Override
	public void onPageSelected(int position) {
		tvDescription.setText(topNewsList.get(position).title);
		llPointGroup.getChildAt(previousEnabledPointPosition).setEnabled(false);
		llPointGroup.getChildAt(position).setEnabled(true);
		previousEnabledPointPosition = position;
		
		/*if(isEnabledSlidingMenu) {
			if(position == 0) {
				System.out.println("�˵�����");
				// �Ѳ˵���Ϊ����
				((MainUI) mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			} else {
				System.out.println("�˵�����");
				// �Ѳ˵�����
				((MainUI) mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
			}
		}*/
	}

	@Override
	public void onPullDownRefresh() {
		isPullDownRefresh = true;
		getDataFromNet();
	}

	@Override
	public void onLoadingMore() {
		if(TextUtils.isEmpty(moreUrl)) {
			Toast.makeText(mActivity, "�Ѿ�û�и���������", 0).show();
			mListView.onRefreshFinish(false);
		} else {
			// �и������ݼ��ظ�������
			getMoreDataFromNet();
		}
	}

	private void getMoreDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, moreUrl, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				mListView.onRefreshFinish(false);
				System.out.println("���ظ������ݳɹ�: " + responseInfo.result);
				
				isLoadingMore = true;
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mListView.onRefreshFinish(false);
				System.out.println("���ظ�������ʧ��: " + msg);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int realPosition = position -1;
		News news = newsList.get(realPosition);
		System.out.println("����: " + news.title + ", id: " + news.id);
		
		// �Ȱ��Ѷ����ŵ�idȡ����
		String readableIDArray = CacheUtils.getString(mActivity, READABLE_NEWS_ID_ARRAY_KEY, "");
		
		if(!readableIDArray.contains(news.id)) {
			String currentID = null;
			if(TextUtils.isEmpty(readableIDArray)) {
				currentID = news.id + ", ";
			} else {
				currentID = readableIDArray + news.id + ", ";
			}
			// ���������ŵ�id�洢����
			CacheUtils.putString(mActivity, READABLE_NEWS_ID_ARRAY_KEY, currentID);
		}
		
		newsListAdapter.notifyDataSetChanged();
		
		Intent intent = new Intent(mActivity, NewsDetailUI.class);
		intent.putExtra("url", news.url);
		mActivity.startActivity(intent);
	}
	
	class InternalHandler extends Handler {
		
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			
			// ��ǰ�������߳���, ���ֲ�ͼ�л�����һҳ��
			int currentItem = (mViewPager.getCurrentItem() + 1) % topNewsList.size();
			mViewPager.setCurrentItem(currentItem);
			
			// �ݹ�: postDelayed -> InternalRunnable.run -> sendEmptyMessage -> handleMessage
			mHandler.postDelayed(new InternalRunnable(), 4000);
		}
	}
	
	class InternalRunnable implements Runnable {

		@Override
		public void run() {
			mHandler.sendEmptyMessage(0);
		}
	}
}
