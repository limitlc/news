package com.itheima45.zhbj.view;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.itheima45.zhbj.R;

/**
 * @author andong
 * 自定义下拉刷新和加载更多ListView
 */
public class RefreshListView extends ListView implements OnScrollListener {

	private int downY = -1; // 按下的y轴的值, 默认为: -1
	private int headerViewHeight; // 头布局的高度
	private LinearLayout headerView; // 整个头布局对象
	private View mPullDownHeaderView; // 下拉刷新头布局对象
	private View secondHeaderView; // 第二头布局对象
	private int mListViewYOnScreen = -1; // ListView在屏幕中y轴的值

	private final int PULL_DOWN_REFRESH = 0; // 下拉刷新
	private final int RELEASE_REFRESH = 1; // 释放刷新
	private final int REFRESHING = 2; // 正在刷新中
	
	private int currentState = PULL_DOWN_REFRESH; // 当前头布局的状态, 默认为: 下拉刷新
	private ImageView ivArrow; // 头布局的箭头
	private ProgressBar mProgressbar; // 头布局的进度条
	private TextView tvState; // 头布局的状态
	private TextView tvLastUpdateTime; // 头布局的最后刷新时间
	private RotateAnimation upAnima; // 向上旋转的动画
	private RotateAnimation downAnima; // 向下旋转的动画
	
	private OnRefreshListener mOnRefreshListener; // 用户的回调事件
	private View footerView; // 脚布局对象
	private int footerViewHeight; // 脚布局的高度
	private boolean isLoadingMore = false; // 是否正在加载更多中, 默认为: false

	public RefreshListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initHeaderView();
		initFooterView();
	}

	public RefreshListView(Context context) {
		super(context);
		initHeaderView();
		initFooterView();
	}
	
	/**
	 * 添加脚布局
	 */
	private void initFooterView() {
		footerView = View.inflate(getContext(), R.layout.refresh_listview_footer, null);
		
		footerView.measure(0, 0);
		footerViewHeight = footerView.getMeasuredHeight();
		
		footerView.setPadding(0, -footerViewHeight, 0, 0);
		this.addFooterView(footerView);
		
		this.setOnScrollListener(this);
	}

	/**
	 * 初始化头布局
	 */
	private void initHeaderView() {
		headerView = (LinearLayout) View.inflate(getContext(), R.layout.refresh_listview_header, null);
		ivArrow = (ImageView) headerView.findViewById(R.id.iv_refresh_listview_header_arrow);
		mProgressbar = (ProgressBar) headerView.findViewById(R.id.pb_refresh_listview_header);
		tvState = (TextView) headerView.findViewById(R.id.tv_refresh_listview_header_state);
		tvLastUpdateTime = (TextView) headerView.findViewById(R.id.tv_refresh_listview_header_last_update_time);
		
		// 最后刷新时间
		tvLastUpdateTime.setText("最后刷新时间: " + getCurrentTime());
		
		// 下拉刷新头布局对象
		mPullDownHeaderView = headerView.findViewById(R.id.ll_pull_down_header_view);
		
		mPullDownHeaderView.measure(0, 0);
		headerViewHeight = mPullDownHeaderView.getMeasuredHeight();
		
		// 隐藏头布局
		mPullDownHeaderView.setPadding(0, -headerViewHeight, 0, 0);
		
		this.addHeaderView(headerView);
		
		initAnimation();
	}
	
	/**
	 * 初始化头布局的动画
	 */
	private void initAnimation() {
		upAnima = new RotateAnimation(
				0, -180, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		upAnima.setFillAfter(true);
		upAnima.setDuration(500);
		
		downAnima = new RotateAnimation(
				-180, -360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		downAnima.setFillAfter(true);
		downAnima.setDuration(500);
	}

	/**
	 * 添加第二头布局
	 * @param secondHeaderView
	 */
	public void addSecondHeaderView(View secondHeaderView) {
		this.secondHeaderView = secondHeaderView;
		headerView.addView(secondHeaderView);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			downY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			if(downY == -1) {
				downY = (int) ev.getY();
			}
			
			int moveY = (int) ev.getY();
			
			// 移动的间距
			int diffY = moveY - downY;
			
			// 判断当前是否正在刷新中
			if(currentState == REFRESHING) {
				// 当前正在刷新中, 不执行下拉, 直接跳出
				break;
			}
			
			// 判断轮播图是否完全显示了
			boolean isDisplay = isDisplaySecondHeaderView();
			if(!isDisplay) {
				// 没有完全显示, 不执行下拉头的操作, 直接跳出Switch
				break;
			}
			
			if(diffY > 0) { // 从上向下滑动, 并且轮播图得完全显示, 才可以进行下拉头的操作
				// 头布局上内间距的值
				int paddingTop = -headerViewHeight + diffY;
				
				if(paddingTop > 0 && currentState != RELEASE_REFRESH) { // 完全显示, 进入松开刷新状态
					System.out.println("进入松开刷新状态");
					
					currentState = RELEASE_REFRESH;
					refreshHeaderViewState();
				} else if(paddingTop < 0 && currentState != PULL_DOWN_REFRESH) { // 没有完全显示, 进入下拉刷新状态
					System.out.println("进入下拉刷新状态");
					
					currentState = PULL_DOWN_REFRESH;
					refreshHeaderViewState();
				}
				
				mPullDownHeaderView.setPadding(0, paddingTop, 0, 0);
				return true;
			}
			break;
		case MotionEvent.ACTION_UP:
			downY = -1;
			
			if(currentState == PULL_DOWN_REFRESH) {
				// 当前是下拉刷新, 把头布局的隐藏
				mPullDownHeaderView.setPadding(0, -headerViewHeight, 0, 0);
			} else if(currentState == RELEASE_REFRESH) {
				// 当前是释放刷新, 进入到正在刷新中的状态
				currentState = REFRESHING;
				refreshHeaderViewState();

				mPullDownHeaderView.setPadding(0, 0, 0, 0);
				
				// 调用用户的回调事件, 刷新数据
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onPullDownRefresh();
				}
			}
			break;
		default:
			break;
		}
		return super.onTouchEvent(ev);
	}
	
	/**
	 * 根据currentState当前头布局的状态来改变头布局
	 */
	private void refreshHeaderViewState() {
		switch (currentState) {
		case PULL_DOWN_REFRESH: // 下拉刷新
			ivArrow.startAnimation(downAnima);
			tvState.setText("下拉刷新");
			break;
		case RELEASE_REFRESH: // 松开刷新
			ivArrow.startAnimation(upAnima);
			tvState.setText("松开刷新");
			break;
		case REFRESHING: // 正在刷新中..
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.INVISIBLE);
			mProgressbar.setVisibility(View.VISIBLE);
			tvState.setText("正在刷新中..");
			break;
		default:
			break;
		}
	}

	/**
	 * 判断第二头布局(轮播图)是否完全显示了.
	 * @return true, 完全显示, false 没有显示
	 */
	private boolean isDisplaySecondHeaderView() {
		// 获取ListView在屏幕中y轴的值
		
		int[] location = new int[2]; // 0位存储的是x轴的值, 1是y轴的值
		
		if(mListViewYOnScreen == -1) {
			this.getLocationOnScreen(location); // 传递给此方法一个数组, 会把数组中的0,1位填上x,y轴的值
			mListViewYOnScreen = location[1];
			System.out.println("ListView在屏幕中y轴的值: " + mListViewYOnScreen);
		}
		
		// 获取轮播图在屏幕中y轴的值
		secondHeaderView.getLocationOnScreen(location);
		int mSecondHeaderViewYOnScreen = location[1];
//		System.out.println("轮播图在屏幕中y轴的值: " + mSecondHeaderViewYOnScreen);
		
		return mListViewYOnScreen <= mSecondHeaderViewYOnScreen;
	}
	
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}
	
	/**
	 * 当刷新完数据之后, 调用此方法. 把头布局隐藏或者脚布局隐藏
	 */
	public void onRefreshFinish(boolean isSuccess) {
		// 判断是加载更多中还是下拉刷新中
		if(isLoadingMore) {
			// 加载更多中
			footerView.setPadding(0, -footerViewHeight, 0, 0);
			isLoadingMore = false;
		} else {
			mPullDownHeaderView.setPadding(0, -headerViewHeight, 0, 0);
			currentState = PULL_DOWN_REFRESH;
			mProgressbar.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.VISIBLE);
			tvState.setText("下拉刷新");
			
			if(isSuccess) {
				// 最后刷新时间
				tvLastUpdateTime.setText("最后刷新时间: " + getCurrentTime());
			}
		}
	}
	
	/**
	 * 获取当前时间: 1990-09-09 09:09:09
	 * @return
	 */
	public String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	/**
	 * @author andong
	 * 当前ListView刷新的回调接口
	 */
	public interface OnRefreshListener {

		/**
		 * 当下拉刷新时触发此方法
		 */
		public void onPullDownRefresh();
		
		/**
		 * 当加载更多时触发此方法
		 */
		public void onLoadingMore();
		
	}

	/**
	 * 当滚动状态改变时触发此方法
	 * scrollState 当前滚动的状态
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// 当滚动停止时, 或者惯性滑动时, ListView最后一个显示的条目索引为getCount -1;
		if(scrollState == SCROLL_STATE_IDLE || 
				scrollState == SCROLL_STATE_FLING) {
			if(getLastVisiblePosition() == getCount() -1 && !isLoadingMore) {
				System.out.println("滚动到底部了");
				
				isLoadingMore  = true;
				
				footerView.setPadding(0, 0, 0, 0);
				// 让ListView滚动到底部
				setSelection(getCount());
				
				// 调用使用者的回调事件
				if(mOnRefreshListener != null) {
					mOnRefreshListener.onLoadingMore();
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		
	}
}
