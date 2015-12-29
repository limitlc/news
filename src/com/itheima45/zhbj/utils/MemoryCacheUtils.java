package com.itheima45.zhbj.utils;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class MemoryCacheUtils {

	private LruCache<String, Bitmap> mMemoryCache;

	public MemoryCacheUtils() {
		// 虚拟机内存的8分之一来缓存图片
		int maxMemorySize = (int) (Runtime.getRuntime().maxMemory() / 8);
		
		mMemoryCache = new LruCache<String, Bitmap>(maxMemorySize) {

			/**
			 * 把图片的大小返回
			 */
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes() * value.getHeight();
			}
		};
	}
	
	/**
	 * 向内存中存一张图片
	 * @param imageUrl
	 * @param bm
	 */
	public void putBitmap2Memory(String imageUrl, Bitmap bm) {
		mMemoryCache.put(imageUrl, bm);
	}
	
	/**
	 * 从内存中取一张图片
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getBitmapFromMemory(String imageUrl) {
		return mMemoryCache.get(imageUrl);
	}
}
