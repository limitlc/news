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
 * �Զ�������ˢ�ºͼ��ظ���ListView
 */
public class RefreshListView extends ListView implements OnScrollListener {

	private int downY = -1; // ���µ�y���ֵ, Ĭ��Ϊ: -1
	private int headerViewHeight; // ͷ���ֵĸ߶�
	private LinearLayout headerView; // ����ͷ���ֶ���
	private View mPullDownHeaderView; // ����ˢ��ͷ���ֶ���
	private View secondHeaderView; // �ڶ�ͷ���ֶ���
	private int mListViewYOnScreen = -1; // ListView����Ļ��y���ֵ

	private final int PULL_DOWN_REFRESH = 0; // ����ˢ��
	private final int RELEASE_REFRESH = 1; // �ͷ�ˢ��
	private final int REFRESHING = 2; // ����ˢ����
	
	private int currentState = PULL_DOWN_REFRESH; // ��ǰͷ���ֵ�״̬, Ĭ��Ϊ: ����ˢ��
	private ImageView ivArrow; // ͷ���ֵļ�ͷ
	private ProgressBar mProgressbar; // ͷ���ֵĽ�����
	private TextView tvState; // ͷ���ֵ�״̬
	private TextView tvLastUpdateTime; // ͷ���ֵ����ˢ��ʱ��
	private RotateAnimation upAnima; // ������ת�Ķ���
	private RotateAnimation downAnima; // ������ת�Ķ���
	
	private OnRefreshListener mOnRefreshListener; // �û��Ļص��¼�
	private View footerView; // �Ų��ֶ���
	private int footerViewHeight; // �Ų��ֵĸ߶�
	private boolean isLoadingMore = false; // �Ƿ����ڼ��ظ�����, Ĭ��Ϊ: false

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
	 * ��ӽŲ���
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
	 * ��ʼ��ͷ����
	 */
	private void initHeaderView() {
		headerView = (LinearLayout) View.inflate(getContext(), R.layout.refresh_listview_header, null);
		ivArrow = (ImageView) headerView.findViewById(R.id.iv_refresh_listview_header_arrow);
		mProgressbar = (ProgressBar) headerView.findViewById(R.id.pb_refresh_listview_header);
		tvState = (TextView) headerView.findViewById(R.id.tv_refresh_listview_header_state);
		tvLastUpdateTime = (TextView) headerView.findViewById(R.id.tv_refresh_listview_header_last_update_time);
		
		// ���ˢ��ʱ��
		tvLastUpdateTime.setText("���ˢ��ʱ��: " + getCurrentTime());
		
		// ����ˢ��ͷ���ֶ���
		mPullDownHeaderView = headerView.findViewById(R.id.ll_pull_down_header_view);
		
		mPullDownHeaderView.measure(0, 0);
		headerViewHeight = mPullDownHeaderView.getMeasuredHeight();
		
		// ����ͷ����
		mPullDownHeaderView.setPadding(0, -headerViewHeight, 0, 0);
		
		this.addHeaderView(headerView);
		
		initAnimation();
	}
	
	/**
	 * ��ʼ��ͷ���ֵĶ���
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
	 * ��ӵڶ�ͷ����
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
			
			// �ƶ��ļ��
			int diffY = moveY - downY;
			
			// �жϵ�ǰ�Ƿ�����ˢ����
			if(currentState == REFRESHING) {
				// ��ǰ����ˢ����, ��ִ������, ֱ������
				break;
			}
			
			// �ж��ֲ�ͼ�Ƿ���ȫ��ʾ��
			boolean isDisplay = isDisplaySecondHeaderView();
			if(!isDisplay) {
				// û����ȫ��ʾ, ��ִ������ͷ�Ĳ���, ֱ������Switch
				break;
			}
			
			if(diffY > 0) { // �������»���, �����ֲ�ͼ����ȫ��ʾ, �ſ��Խ�������ͷ�Ĳ���
				// ͷ�������ڼ���ֵ
				int paddingTop = -headerViewHeight + diffY;
				
				if(paddingTop > 0 && currentState != RELEASE_REFRESH) { // ��ȫ��ʾ, �����ɿ�ˢ��״̬
					System.out.println("�����ɿ�ˢ��״̬");
					
					currentState = RELEASE_REFRESH;
					refreshHeaderViewState();
				} else if(paddingTop < 0 && currentState != PULL_DOWN_REFRESH) { // û����ȫ��ʾ, ��������ˢ��״̬
					System.out.println("��������ˢ��״̬");
					
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
				// ��ǰ������ˢ��, ��ͷ���ֵ�����
				mPullDownHeaderView.setPadding(0, -headerViewHeight, 0, 0);
			} else if(currentState == RELEASE_REFRESH) {
				// ��ǰ���ͷ�ˢ��, ���뵽����ˢ���е�״̬
				currentState = REFRESHING;
				refreshHeaderViewState();

				mPullDownHeaderView.setPadding(0, 0, 0, 0);
				
				// �����û��Ļص��¼�, ˢ������
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
	 * ����currentState��ǰͷ���ֵ�״̬���ı�ͷ����
	 */
	private void refreshHeaderViewState() {
		switch (currentState) {
		case PULL_DOWN_REFRESH: // ����ˢ��
			ivArrow.startAnimation(downAnima);
			tvState.setText("����ˢ��");
			break;
		case RELEASE_REFRESH: // �ɿ�ˢ��
			ivArrow.startAnimation(upAnima);
			tvState.setText("�ɿ�ˢ��");
			break;
		case REFRESHING: // ����ˢ����..
			ivArrow.clearAnimation();
			ivArrow.setVisibility(View.INVISIBLE);
			mProgressbar.setVisibility(View.VISIBLE);
			tvState.setText("����ˢ����..");
			break;
		default:
			break;
		}
	}

	/**
	 * �жϵڶ�ͷ����(�ֲ�ͼ)�Ƿ���ȫ��ʾ��.
	 * @return true, ��ȫ��ʾ, false û����ʾ
	 */
	private boolean isDisplaySecondHeaderView() {
		// ��ȡListView����Ļ��y���ֵ
		
		int[] location = new int[2]; // 0λ�洢����x���ֵ, 1��y���ֵ
		
		if(mListViewYOnScreen == -1) {
			this.getLocationOnScreen(location); // ���ݸ��˷���һ������, ��������е�0,1λ����x,y���ֵ
			mListViewYOnScreen = location[1];
			System.out.println("ListView����Ļ��y���ֵ: " + mListViewYOnScreen);
		}
		
		// ��ȡ�ֲ�ͼ����Ļ��y���ֵ
		secondHeaderView.getLocationOnScreen(location);
		int mSecondHeaderViewYOnScreen = location[1];
//		System.out.println("�ֲ�ͼ����Ļ��y���ֵ: " + mSecondHeaderViewYOnScreen);
		
		return mListViewYOnScreen <= mSecondHeaderViewYOnScreen;
	}
	
	public void setOnRefreshListener(OnRefreshListener listener) {
		this.mOnRefreshListener = listener;
	}
	
	/**
	 * ��ˢ��������֮��, ���ô˷���. ��ͷ�������ػ��߽Ų�������
	 */
	public void onRefreshFinish(boolean isSuccess) {
		// �ж��Ǽ��ظ����л�������ˢ����
		if(isLoadingMore) {
			// ���ظ�����
			footerView.setPadding(0, -footerViewHeight, 0, 0);
			isLoadingMore = false;
		} else {
			mPullDownHeaderView.setPadding(0, -headerViewHeight, 0, 0);
			currentState = PULL_DOWN_REFRESH;
			mProgressbar.setVisibility(View.INVISIBLE);
			ivArrow.setVisibility(View.VISIBLE);
			tvState.setText("����ˢ��");
			
			if(isSuccess) {
				// ���ˢ��ʱ��
				tvLastUpdateTime.setText("���ˢ��ʱ��: " + getCurrentTime());
			}
		}
	}
	
	/**
	 * ��ȡ��ǰʱ��: 1990-09-09 09:09:09
	 * @return
	 */
	public String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(new Date());
	}
	
	/**
	 * @author andong
	 * ��ǰListViewˢ�µĻص��ӿ�
	 */
	public interface OnRefreshListener {

		/**
		 * ������ˢ��ʱ�����˷���
		 */
		public void onPullDownRefresh();
		
		/**
		 * �����ظ���ʱ�����˷���
		 */
		public void onLoadingMore();
		
	}

	/**
	 * ������״̬�ı�ʱ�����˷���
	 * scrollState ��ǰ������״̬
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// ������ֹͣʱ, ���߹��Ի���ʱ, ListView���һ����ʾ����Ŀ����ΪgetCount -1;
		if(scrollState == SCROLL_STATE_IDLE || 
				scrollState == SCROLL_STATE_FLING) {
			if(getLastVisiblePosition() == getCount() -1 && !isLoadingMore) {
				System.out.println("�������ײ���");
				
				isLoadingMore  = true;
				
				footerView.setPadding(0, 0, 0, 0);
				// ��ListView�������ײ�
				setSelection(getCount());
				
				// ����ʹ���ߵĻص��¼�
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
