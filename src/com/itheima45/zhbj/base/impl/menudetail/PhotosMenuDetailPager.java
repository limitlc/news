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
 * ���˵�: ��ͼ������ҳ��
 */
public class PhotosMenuDetailPager extends MenuDetailBasePager {
	
	@ViewInject(R.id.lv_photos)
	private ListView mListView;

	@ViewInject(R.id.gv_photos)
	private GridView mGridView;

	private List<PhotoItemBean> photoItemList;

	private BitmapUtils bitmapUtils;
	
	private boolean isSingleColumns = true; // ��ǰ�Ƿ��ǵ�������, Ĭ���ǵ���

	private BitmapCacheUtils mBitmapCacheUtils; // ͼƬ���湤����
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NetCacheUtils.SUCCESS: // ���绺������ɹ�.
				Bitmap bm = (Bitmap) msg.obj;
				int position = msg.arg1;
				
				ImageView mImageView = (ImageView) mListView.findViewWithTag(position);
				if(mImageView != null) {
					// System.out.println("��ǰ�ǵ�" + position + "��, ͼƬ����ɹ�");
					mImageView.setImageBitmap(bm);
				}
				break;
			case NetCacheUtils.FAILED:
				Toast.makeText(mActivity, "��" + msg.arg1 + "��ͼƬ����ʧ��", 0).show();
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
		// ����ʾ��������������
		String json = CacheUtils.getString(mActivity, ConstantUtils.PHOTOS_URL, null);
		if(!TextUtils.isEmpty(json)) {
			processData(json);
		}
		
		// ȥ������ȡͼƬ����
		getDataFromNet();
	}

	private void getDataFromNet() {
		HttpUtils httpUtils = new HttpUtils();
		httpUtils.send(HttpMethod.GET, ConstantUtils.PHOTOS_URL, new RequestCallBack<String>() {

			@Override
			public void onSuccess(ResponseInfo<String> responseInfo) {
				// System.out.println("��ͼ��������ɹ�: " + responseInfo.result);
				
				CacheUtils.putString(mActivity, ConstantUtils.PHOTOS_URL, responseInfo.result);
				
				processData(responseInfo.result);
			}

			@Override
			public void onFailure(HttpException error, String msg) {
				System.out.println("��ͼ��������ʧ��: " + msg);
			}
		});
		
	}

	/**
	 * �������ʾ����
	 * @param result
	 */
	protected void processData(String json) {
		PhotosBean bean = parseJson(json);
		
		photoItemList = bean.data.news;
		PhotosAdapter mAdapter = new PhotosAdapter();
		mListView.setAdapter(mAdapter);
	}

	/**
	 * ����json����
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
			
			// ����mholder������������и�ֵ
			mHolder.tvDescription.setText(bean.title);
			
			// ����Ĭ��ͼƬ
			mHolder.ivImage.setTag(position);
			mHolder.ivImage.setImageResource(R.drawable.pic_item_list_default);
			
			// ��������ͼƬ
			// bitmapUtils.display(mHolder.ivImage, bean.listimage);
			
			Bitmap bm = mBitmapCacheUtils.getBitmapFromUrl(bean.listimage, position);
			
			if(bm != null) {
				// ��ǰͼƬ������null, �Ǵ��ڴ���߱�����ȡ����, ������ʾ.
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
	 * �л���ǰ��ҳ����ʾ.
	 */
	public void switchListOrGrid(ImageButton ib) {
		if(isSingleColumns) {
			isSingleColumns = false;
			// ��ǰ�ǵ���, �л�������
			mListView.setVisibility(View.GONE);
			mGridView.setVisibility(View.VISIBLE);
			
			mGridView.setAdapter(new PhotosAdapter());
			
			// �л�ͼƬ
			ib.setImageResource(R.drawable.icon_pic_list_type);
		} else {
			isSingleColumns = true;
			// ��ǰ������, �л��ɵ���
			mGridView.setVisibility(View.GONE);
			mListView.setVisibility(View.VISIBLE);
			
			mListView.setAdapter(new PhotosAdapter());
			
			// �л�ͼƬ
			ib.setImageResource(R.drawable.icon_pic_grid_type);
		}
	}
}
