package com.itheima45.zhbj.base.impl;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.itheima45.zhbj.MainUI;
import com.itheima45.zhbj.base.BasePager;
import com.itheima45.zhbj.base.MenuDetailBasePager;
import com.itheima45.zhbj.base.impl.menudetail.InteractMenuDetailPager;
import com.itheima45.zhbj.base.impl.menudetail.NewsMenuDetailPager;
import com.itheima45.zhbj.base.impl.menudetail.PhotosMenuDetailPager;
import com.itheima45.zhbj.base.impl.menudetail.TopicMenuDetailPager;
import com.itheima45.zhbj.domain.NewsCenterBean;
import com.itheima45.zhbj.domain.NewsCenterBean.NewsCenterMenu;
import com.itheima45.zhbj.fragment.LeftMenuFragment;
import com.itheima45.zhbj.utils.CacheUtils;
import com.itheima45.zhbj.utils.ConstantUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;

/**
 * @author andong
 * 新闻中心页签对应的页面
 */
public class NewsCenterPager extends BasePager {
	
	private List<MenuDetailBasePager> pagerList; // 左侧菜单对应的详情页面
	private List<NewsCenterMenu> leftMenuDataList; // 左侧菜单的数据

	public NewsCenterPager(Activity activity) {
		super(activity);
	}

	@Override
	public void initData() {
		System.out.println("新闻中心加载数据了");
		tvTitle.setText("新闻");
		ibMenu.setVisibility(View.VISIBLE);
		
		// 在请求网路之前, 把以前的数据取出来, 展示在界面上.
		String json = CacheUtils.getString(mActivity, ConstantUtils.NEWSCENTER_URL, null);
		if(!TextUtils.isEmpty(json)) {
			// 本地数据不为null, 展示到界面上
			processData(json);
		}
		
		getDataFromNet();
	}

	/**
	 * 从网络获取数据
	 */
	private void getDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		// RequestCallBack<T> 中的泛型是定义请求网络成功后, 返回回来的数据的类型
		httpUtils.send(HttpMethod.GET, ConstantUtils.NEWSCENTER_URL, new RequestCallBack<String>() {

			/**
			 * 当请求成功时回调此方法
			 * responseInfo 请求结果信息
			 */
			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				System.out.println("新闻中心页面数据请求成功, 数据: " + responseInfo.result);
				
				// 把数据缓存到本地, 为了下次打开程序去显示历史的数据
				CacheUtils.putString(mActivity, ConstantUtils.NEWSCENTER_URL, responseInfo.result);
				
				processData(responseInfo.result);
			}

			/**
			 * 当请求失败时回调此方法
			 * msg 错误信息
			 */
			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("新闻中心页面数据请求失败, 错误: " + msg);
			}
		});
	}

	/**
	 * 处理和解析数据
	 * @param result
	 */
	protected void processData(String json) {
		Gson gson = new Gson();
		NewsCenterBean bean = gson.fromJson(json, NewsCenterBean.class);

		// 初始化左侧菜单的数据
		leftMenuDataList = bean.data; // 左侧菜单的数据

		// 初始化左侧菜单对应的页面, 共四个页面: 新闻, 专题, 组图, 互动
		pagerList = new ArrayList<MenuDetailBasePager>();
		pagerList.add(new NewsMenuDetailPager(mActivity, leftMenuDataList.get(0)));
		pagerList.add(new TopicMenuDetailPager(mActivity));
		pagerList.add(new PhotosMenuDetailPager(mActivity));
		pagerList.add(new InteractMenuDetailPager(mActivity));
		
		// 把左侧菜单数据传递给LeftMenuFragment来处理
		MainUI mainUI = ((MainUI) mActivity);
		LeftMenuFragment leftMenuFragment = mainUI.getLeftMenuFragment();
		leftMenuFragment.setMenuDataList(leftMenuDataList);
	}
	
	/**
	 * 根据索引切换当前的页面
	 * @param position
	 */
	public void switchCurrentPager(int position) {
		// 设置当前的标题
		String title = leftMenuDataList.get(position).title;
		tvTitle.setText(title);
		
		MenuDetailBasePager pager = pagerList.get(position);
		flContent.removeAllViews(); // 清除所有的子布局
		flContent.addView(pager.rootView);
		pager.initData();
		
		if(position == 2) {
			// 当前是组图
			ibSwitchListOrGrid.setVisibility(View.VISIBLE);
			ibSwitchListOrGrid.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					PhotosMenuDetailPager pager = (PhotosMenuDetailPager) pagerList.get(2);
					pager.switchListOrGrid(ibSwitchListOrGrid); // 切换列表或者网格页面
				}
			});
		} else {
			ibSwitchListOrGrid.setVisibility(View.GONE);
		}
	}
}
