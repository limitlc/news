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
 * 左侧菜单的Fragment
 */
public class LeftMenuFragment extends BaseFragment implements OnItemClickListener {
	
	private List<NewsCenterMenu> mDataList;
	private ListView mListView;
	private int currentSelectItem; // 当前被选中菜单的变量
	private LeftMenuAdapter mAdapter;

	@Override
	public View initView() {
		mListView = new ListView(mActivity);
		mListView.setBackgroundColor(Color.BLACK);
		mListView.setCacheColorHint(Color.TRANSPARENT); // 处理按住拖动时, 变白的效果
		mListView.setDividerHeight(0); // 去除分割线
		mListView.setPadding(0, 40, 0, 0);
		mListView.setSelector(android.R.color.transparent); // 给ListView的item设置一个点击的选择器: 透明
		return mListView;
	}
	
	/**
	 * 设置菜单的数据
	 * @param dataList
	 */
	public void setMenuDataList(List<NewsCenterMenu> dataList) {
		this.mDataList = dataList;
		
		// 初始化默认选中的菜单: 0
		currentSelectItem = 0;
		// 初始化默认选中菜单对应的页面
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
			
			// 当前获取的条目的位置和被选中的条目的位置一样, 应该把当前的条目置为红色
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
		
		// 把菜单缩回去
		MainUI mainUI = (MainUI) mActivity;
		SlidingMenu slidingMenu = mainUI.getSlidingMenu();
		slidingMenu.toggle(); // 控制菜单显示还是隐藏, 如果当前菜单是显示, 就隐藏.
		
		// 把当前选中的菜单对应的页面展示出来
		switchMenuDetailPager(position);
	}
	
	/**
	 * 根据索引来切换菜单对应的详情页面
	 * @param position
	 */
	private void switchMenuDetailPager(int position) {
		MainUI mainUI = (MainUI) mActivity;
		ContentFragment contentFragment = mainUI.getContentFragment();
		NewsCenterPager pager = contentFragment.getNewsCenterPagerInstance();
		pager.switchCurrentPager(position);
	}
}
