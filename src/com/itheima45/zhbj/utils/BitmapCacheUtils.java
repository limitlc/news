package com.itheima45.zhbj.utils;

import android.graphics.Bitmap;
import android.os.Handler;

/**
 * @author andong
 * 图片缓存工具类
 */
public class BitmapCacheUtils {
	
	private NetCacheUtils mNetCacheUtils; // 网络缓存对象
	private LocalCacheUtils mLocalCacheUtils; // 本地缓存对象
	private MemoryCacheUtils mMemoryCacheUtils; // 内存缓存对象
	
	public BitmapCacheUtils(Handler handler) {
		
		mMemoryCacheUtils = new MemoryCacheUtils();
		mLocalCacheUtils = new LocalCacheUtils();
		mNetCacheUtils = new NetCacheUtils(handler, mLocalCacheUtils, mMemoryCacheUtils);
	}

	/**
	 * 根据Url取图片
	 * 
	 * 		1. 先根据Url去内存中取, 如果取到直接返回.
	 * 		2. 再根据Url去本地中取, 如果取到直接返回.
	 * 		3. 最后去网络中取, 取到之后发送给主线程显示.
	 * 			3.1 根据Url, 向内存中存一份.
	 * 			3.2 根据Url, 向本地中存一份.
	 * 
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getBitmapFromUrl(String imageUrl, int position) {
		// 1. 先根据Url去内存中取, 如果取到直接返回.
		Bitmap bm = mMemoryCacheUtils.getBitmapFromMemory(imageUrl);
		if(bm != null) {
			System.out.println("从内存中取到的");
			return bm;
		}
		
		// 2. 再根据Url去本地中取, 如果取到直接返回.
		bm = mLocalCacheUtils.getBitmapFromLocal(imageUrl);
		if(bm != null) {
			System.out.println("从本地中取到的");
			return bm;
		}
		
		// 3. 最后去网络中取, 取到之后发送给主线程显示.
		System.out.println("从网络中取到的");
		mNetCacheUtils.getBitmapFromNet(imageUrl, position);
		return null;
	}

}
