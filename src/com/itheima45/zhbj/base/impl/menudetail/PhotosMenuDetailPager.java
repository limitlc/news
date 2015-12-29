package com.itheima45.zhbj.base.impl.menudetail;

import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.itheima45.zhbj.R;
import com.itheima45.zhbj.base.MenuDetailBasePager;
import com.itheima45.zhbj.domain.PhotosBean;
import com.itheima45.zhbj.domain.PhotosBean.PhotoItemBean;
import com.itheima45.zhbj.utils.BitmapCacheUtils;
import com.itheima45.zhbj.utils.CacheUtils;
import com.itheima45.zhbj.utils.ConstantUtils;
import com.itheima45.zhbj.utils.NetCacheUtils;
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
 * 左侧菜单: 组图的详情页面
 */
public class PhotosMenuDetailPager extends MenuDetailBasePager {
	
	@ViewInject(R.id.lv_photos)
	private ListView mListView;

	@ViewInject(R.id.gv_photos)
	private GridView mGridView;

	private List<PhotoItemBean> photoItemList;

	private BitmapUtils bitmapUtils;
	
	private boolean isSingleColumns = true; // 当前是否是单列数据, 默认是单列

	private BitmapCacheUtils mBitmapCacheUtils; // 图片缓存工具类
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NetCacheUtils.SUCCESS: // 网络缓存请求成功.
				Bitmap bm = (Bitmap) msg.obj;
				int position = msg.arg1;
				
				ImageView mImageView = (ImageView) mListView.findViewWithTag(position);
				if(mImageView != null) {
					// System.out.println("当前是第" + position + "张, 图片请求成功");
					mImageView.setImageBitmap(bm);
				}
				break;
			case NetCacheUtils.FAILED:
				Toast.makeText(mActivity, "第" + msg.arg1 + "张图片请求失败", 0).show();
				break;
			default:
				break;
			}
			
		}
	};

	public PhotosMenuDetailPager(Activity activity) {
		super(activity);
		
//		bitmapUtils = new BitmapUtils(mActivity);
//		bitmapUtils.configDefaultBitmapConfig(Config.ARGB_4444);
		

		mBitmapCacheUtils = new BitmapCacheUtils(handler);
	}

	@Override
	public View initView() {
		View view = View.inflate(mActivity, R.layout.photos, null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void initData() {
		// 先显示缓存起来的数据
		String json = CacheUtils.getString(mActivity, ConstantUtils.PHOTOS_URL, null);
		if(!TextUtils.isEmpty(json)) {
			processData(json);
		}
		
		// 去网络中取图片数据
		getDataFromNet();
	}

	private void getDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, ConstantUtils.PHOTOS_URL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// System.out.println("组图数据请求成功: " + responseInfo.result);
				
				CacheUtils.putString(mActivity, ConstantUtils.PHOTOS_URL, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("组图数据请求失败: " + msg);
			}
		});
		
	}

	/**
	 * 处理和显示数据
	 * @param result
	 */
	protected void processData(String json) {
		PhotosBean bean = parseJson(json);
		
		photoItemList = bean.data.news;
		PhotosAdapter mAdapter = new PhotosAdapter();
		mListView.setAdapter(mAdapter);
	}

	/**
	 * 解析json数据
	 * @param json
	 * @return
	 */
	private PhotosBean parseJson(String json) {
		Gson gson = new Gson();
		return gson.fromJson(json, PhotosBean.class);
	}
	
	class PhotosAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return photoItemList.size();
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			PhotosViewHolder mHolder = null;
			if(convertView == null) {
				convertView = View.inflate(mActivity, R.layout.photos_item, null);
				mHolder = new PhotosViewHolder();
				mHolder.ivImage = (ImageView) convertView.findViewById(R.id.iv_photos_item_iamge);
				mHolder.tvDescription = (TextView) convertView.findViewById(R.id.tv_photos_item_description);
				convertView.setTag(mHolder);
			} else {
				mHolder = (PhotosViewHolder) convertView.getTag();
			}
			
			PhotoItemBean bean = photoItemList.get(position);
			
			// 根据mholder的两个对象进行赋值
			mHolder.tvDescription.setText(bean.title);
			
			// 设置默认图片
			mHolder.ivImage.setTag(position);
			mHolder.ivImage.setImageResource(R.drawable.pic_item_list_default);
			
			// 设置最新图片
			// bitmapUtils.display(mHolder.ivImage, bean.listimage);
			
			Bitmap bm = mBitmapCacheUtils.getBitmapFromUrl(bean.listimage, position);
			
			if(bm != null) {
				// 当前图片不等于null, 是从内存或者本地中取到的, 立刻显示.
				mHolder.ivImage.setImageBitmap(bm);
			}
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
	
	public class PhotosViewHolder {
		
		public ImageView ivImage;
		public TextView tvDescription;
	}

	/**
	 * 切换当前的页面显示.
	 */
	public void switchListOrGrid(ImageButton ib) {
		if(isSingleColumns) {
			isSingleColumns = false;
			// 当前是单列, 切换成两列
			mListView.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			
			mGridView.setAdapter(new PhotosAdapter());
			
			// 切换图片
			ib.setImageResource(R.drawable.icon_pic_list_type);
		} else {
			isSingleColumns = true;
			// 当前是两列, 切换成单列
			mGridView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			
			mListView.setAdapter(new PhotosAdapter());
			
			// 切换图片
			ib.setImageResource(R.drawable.icon_pic_grid_type);
		}
	}
}
