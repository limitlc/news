package com.itheima45.zhbj.fragment;

import java.util.List;

import com.itheima45.zhbj.MainUI;
import com.itheima45.zhbj.R;
import com.itheima45.zhbj.base.BaseFragment;
import com.itheima45.zhbj.base.impl.NewsCenterPager;
import com.itheima45.zhbj.domain.NewsCenterBean.NewsCenterMenu;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * @author andong
 * ���˵���Fragment
 */
public class LeftMenuFragment extends BaseFragment implements OnItemClickListener {
	
	private List<NewsCenterMenu> mDataList;
	private ListView mListView;
	private int currentSelectItem; // ��ǰ��ѡ�в˵��ı���
	private LeftMenuAdapter mAdapter;

	@Override
	public View initView() {
		mListView = new ListView(mActivity);
		mListView.setBackgroundColor(Color.BLACK);
		mListView.setCacheColorHint(Color.TRANSPARENT); // ����ס�϶�ʱ, ��׵�Ч��
		mListView.setDividerHeight(0); // ȥ���ָ���
		mListView.setPadding(0, 40, 0, 0);
		mListView.setSelector(android.R.color.transparent); // ��ListView��item����һ�������ѡ����: ͸��
		return mListView;
	}
	
	/**
	 * ���ò˵�������
	 * @param dataList
	 */
	public void setMenuDataList(List<NewsCenterMenu> dataList) {
		this.mDataList = dataList;
		
		// ��ʼ��Ĭ��ѡ�еĲ˵�: 0
		currentSelectItem = 0;
		// ��ʼ��Ĭ��ѡ�в˵���Ӧ��ҳ��
		switchMenuDetailPager(0);
		
		mAdapter = new LeftMenuAdapter();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}
	
	class LeftMenuAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mDataList.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView tv = (TextView) View.inflate(mActivity, R.layout.left_menu_item, null);
			NewsCenterMenu menu = mDataList.get(position);
			tv.setText(menu.title);
			
			// ��ǰ��ȡ����Ŀ��λ�úͱ�ѡ�е���Ŀ��λ��һ��, Ӧ�ðѵ�ǰ����Ŀ��Ϊ��ɫ
			tv.setEnabled(position == currentSelectItem);
			return tv;
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		currentSelectItem = position;
		mAdapter.notifyDataSetChanged();
		
		// �Ѳ˵�����ȥ
		MainUI mainUI = (MainUI) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		slidingMenu.toggle(); // ���Ʋ˵���ʾ��������, �����ǰ�˵�����ʾ, ������.
		
		// �ѵ�ǰѡ�еĲ˵���Ӧ��ҳ��չʾ����
		switchMenuDetailPager(position);
	}
	
	/**
	 * �����������л��˵���Ӧ������ҳ��
	 * @param position
	 */
	private void switchMenuDetailPager(int position) {
		MainUI mainUI = (MainUI) mActivity;
		ContentFragment contentFragment = mainUI.getContentFragment();
		NewsCenterPager pager = contentFragment.getNewsCenterPagerInstance();
		pager.switchCurrentPager(position);
	}
}
