package com.itheima45.zhbj.utils;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * @author andong
 * ͼƬ���湤����
 */
public class BitmapCacheUtils {
	
	private NetCacheUtils mNetCacheUtils; // ���绺�����
	private LocalCacheUtils mLocalCacheUtils; // ���ػ������
	private MemoryCacheUtils mMemoryCacheUtils; // �ڴ滺�����
	
	public BitmapCacheUtils(Handler handler) {
		
		mMemoryCacheUtils = new MemoryCacheUtils();
		mLocalCacheUtils = new LocalCacheUtils();
		mNetCacheUtils = new NetCacheUtils(handler, mLocalCacheUtils, mMemoryCacheUtils);
	}

	/**
	 * ����UrlȡͼƬ
	 * 
	 * 		1. �ȸ���Urlȥ�ڴ���ȡ, ���ȡ��ֱ�ӷ���.
	 * 		2. �ٸ���Urlȥ������ȡ, ���ȡ��ֱ�ӷ���.
	 * 		3. ���ȥ������ȡ, ȡ��֮���͸����߳���ʾ.
	 * 			3.1 ����Url, ���ڴ��д�һ��.
	 * 			3.2 ����Url, �򱾵��д�һ��.
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getBitmapFromUrl(String imageUrl, int position) {
		// 1. �ȸ���Urlȥ�ڴ���ȡ, ���ȡ��ֱ�ӷ���.
		Bitmap bm = mMemoryCacheUtils.getBitmapFromMemory(imageUrl);
		if(bm != null) {
			System.out.println("���ڴ���ȡ����");
			return bm;
		}
		
		// 2. �ٸ���Urlȥ������ȡ, ���ȡ��ֱ�ӷ���.
		bm = mLocalCacheUtils.getBitmapFromLocal(imageUrl);
		if(bm != null) {
			System.out.println("�ӱ�����ȡ����");
			return bm;
		}
		
		// 3. ���ȥ������ȡ, ȡ��֮���͸����߳���ʾ.
		System.out.println("��������ȡ����");
		mNetCacheUtils.getBitmapFromNet(imageUrl, position);
		return null;
	}

}
