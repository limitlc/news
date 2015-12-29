package com.itheima45.zhbj.utils;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryCacheUtils {

	private LruCache<String, Bitmap> mMemoryCache;

	public MemoryCacheUtils() {
		// ������ڴ��8��֮һ������ͼƬ
		int maxMemorySize = (int) (Runtime.getRuntime().maxMemory() / 8);
		
		mMemoryCache = new LruCache<String, Bitmap>(maxMemorySize) {

			/**
			 * ��ͼƬ�Ĵ�С����
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}
	
	/**
	 * ���ڴ��д�һ��ͼƬ
	 * @param imageUrl
	 * @param bm
	 */
	public void putBitmap2Memory(String imageUrl, Bitmap bm) {
		mMemoryCache.put(imageUrl, bm);
	}
	
	/**
	 * ���ڴ���ȡһ��ͼƬ
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getBitmapFromMemory(String imageUrl) {
		return mMemoryCache.get(imageUrl);
	}
}
