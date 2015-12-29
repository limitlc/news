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
 * 页签对应的详情页面
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
	private String url; // 当前页面数据的访问地址
	
	private List<TopNew> topNewsList; // 顶部新闻数据

	private BitmapUtils bitmapUtils; // 图片请求框架类

	private int previousEnabledPointPosition; // 前一个可用的点的索引
	
	private boolean isEnabledSlidingMenu = false; // 是否启用侧滑菜单, 默认是不启用

	private List<News> newsList; // 列表新闻的数据

	private boolean isPullDownRefresh = false; // 是否正在下拉刷新中

	private String moreUrl; // 更多的url
	private boolean isLoadingMore = false; // 是否正在加载更多中

	private NewsAdapter newsListAdapter;
	
	// 已读新闻的id数组
	private final String READABLE_NEWS_ID_ARRAY_KEY = "readable_news_id_array_key";

	private InternalHandler mHandler; // 内部的handler消息处理器
	
	public TabDetailPager(Activity activity, NewsMenuTab newsMenuTab, boolean isEnabledSlidingMenu) {
		super(activity);
		this.newsMenuTab = newsMenuTab;
		this.isEnabledSlidingMenu = isEnabledSlidingMenu;
		
		bitmapUtils = new BitmapUtils(mActivity);
		// 配置默认图片的像素单位
		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.tab_detail, null);
		ViewUtils.inject(this, view);
		
		View topNewsView = View.inflate(mActivity, R.layout.tab_detail_topnews, null);
		ViewUtils.inject(this, topNewsView);
		
		// 把topNewsView添加到ListView的头部中
		// mListView.addHeaderView(topNewsView);
		
		mListView.addSecondHeaderView(topNewsView);
		mListView.setOnRefreshListener(this);
		mListView.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void initData() {
		url = ConstantUtils.SERVER_URL + newsMenuTab.url;
		System.out.println(newsMenuTab.title + "页签详情页面数据url: " + url);
		
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
				System.out.println(newsMenuTab.title + "的数据请求成功: " + responseInfo.result);
				
				// 把数据缓存起来
				CacheUtils.putString(mActivity, url, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println(newsMenuTab.title + "的数据请求失败: " + msg);
				if(isPullDownRefresh) {
					isPullDownRefresh = false;
					mListView.onRefreshFinish(false);
				}
			}
		});
	}

	/**
	 * 处理和解析json数据
	 * @param result
	 */
	protected void processData(String result) {
		TabDetailBean bean = parserJson(result);
		
		if(!isLoadingMore) {
			// 初始化顶部新闻的数据和界面
			topNewsList = bean.data.topnews; 
			TopNewsAdapter topNewsAdapter = new TopNewsAdapter();
			mViewPager.setAdapter(topNewsAdapter);
			mViewPager.setOnPageChangeListener(this);

			// 初始化顶部新闻的描述和点
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
			tvDescription.setText(topNewsList.get(previousEnabledPointPosition).title); // 设置默认的描述为第一张图片
			llPointGroup.getChildAt(previousEnabledPointPosition).setEnabled(true);
			
			// 初始化列表新闻的数据和界面
			newsList = bean.data.news;
			newsListAdapter = new NewsAdapter();
			mListView.setAdapter(newsListAdapter);
		} else {
			isLoadingMore = false;
			
			// 把列表新闻先取出来, 再加到以前的集合中, 再刷新数据
			List<News> tempNewsList = bean.data.news;
			
			newsList.addAll(tempNewsList); // 把所有的数据添加到以往的集合中
			newsListAdapter.notifyDataSetChanged();
		}
		
		
		// ------------开始轮播图循环播放--------------
		
		if(mHandler == null) {
			mHandler = new InternalHandler();
		} else {
			// 前边可能已经有任务或者消息在队列中等待执行.
			
			// 把队列中的所有消息和任务全部清除出队列
			mHandler.removeCallbacksAndMessages(null);
		}
		
		// 延时4秒钟, 执行InternalRunnable任务类中的run方法
		mHandler.postDelayed(new InternalRunnable(), 4000);
	}

	/**
	 * 使用Gson来解析json数据, 并封装成一个TabDetailBean返回.
	 * @param json
	 * @return
	 */
	private TabDetailBean parserJson(String json) {
		Gson gson = new Gson();
		TabDetailBean bean = gson.fromJson(json, TabDetailBean.class);
		
		// 把更多的Url取出来
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
			
			// 判断当前是否是已读的新闻
			String readableIDArray = CacheUtils.getString(mActivity, READABLE_NEWS_ID_ARRAY_KEY, null);
			if(!TextUtils.isEmpty(readableIDArray)
					&& readableIDArray.contains(bean.id)) {
				mHolder.tvTitle.setTextColor(Color.GRAY);
			} else {
				mHolder.tvTitle.setTextColor(Color.BLACK);
			}
			
			// 设置默认图片
			mHolder.ivIcon.setImageResource(R.drawable.news_pic_default);

			// 请求网络中的图片
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
			iv.setScaleType(ScaleType.FIT_XY); // 指定把图片拉伸为控件的宽和高
			iv.setImageResource(R.drawable.topnews_item_default); // 设置默认显示的图片, 会在没加载完数据之前显示
			iv.setOnTouchListener(new TopNewsItemTouchListener());
			
			TopNew topNew = topNewsList.get(position);
			
			/**
			 * container 下面的uri参数请求下来的图片, 设置给container来展示.
			 * uri 图片的请求地址
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
				System.out.println("手指按下, 停止播放了");
				mHandler.removeCallbacksAndMessages(null); // 移除消息队列中所有的任务和消息.
				break;
			case MotionEvent.ACTION_CANCEL: // 事件丢失
				System.out.println("事件丢失, 开始播放了");
				mHandler.postDelayed(new InternalRunnable(), 4000);
				break;
			case MotionEvent.ACTION_UP:
				System.out.println("手指抬起, 开始播放了");
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
				System.out.println("菜单可用");
				// 把菜单置为可用
				((MainUI) mActivity).getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
			} else {
				System.out.println("菜单屏蔽");
				// 把菜单屏蔽
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
			Toast.makeText(mActivity, "已经没有更多数据了", 0).show();
			mListView.onRefreshFinish(false);
		} else {
			// 有更多数据加载更多数据
			getMoreDataFromNet();
		}
	}

	private void getMoreDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, moreUrl, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				mListView.onRefreshFinish(false);
				System.out.println("加载更多数据成功: " + responseInfo.result);
				
				isLoadingMore = true;
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				mListView.onRefreshFinish(false);
				System.out.println("加载更多数据失败: " + msg);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		int realPosition = position -1;
		News news = newsList.get(realPosition);
		System.out.println("标题: " + news.title + ", id: " + news.id);
		
		// 先把已读新闻的id取出来
		String readableIDArray = CacheUtils.getString(mActivity, READABLE_NEWS_ID_ARRAY_KEY, "");
		
		if(!readableIDArray.contains(news.id)) {
			String currentID = null;
			if(TextUtils.isEmpty(readableIDArray)) {
				currentID = news.id + ", ";
			} else {
				currentID = readableIDArray + news.id + ", ";
			}
			// 把这条新闻的id存储起来
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
			
			// 当前是在主线程中, 把轮播图切换到下一页面
			int currentItem = (mViewPager.getCurrentItem() + 1) % topNewsList.size();
			mViewPager.setCurrentItem(currentItem);
			
			// 递归: postDelayed -> InternalRunnable.run -> sendEmptyMessage -> handleMessage
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
