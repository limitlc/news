package com.itheima45.zhbj.utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

/**
 * @author andong
 * 网络缓存工具类
 */
public class NetCacheUtils {

	public static final int SUCCESS = 0;
	public static final int FAILED = 1;
	
	private Handler handler;
	private ExecutorService mExecutorService;
	private LocalCacheUtils localCacheUtils; // 本地缓存对象
	private MemoryCacheUtils memoryCacheUtils; // 内存缓存对象
	
	public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
		this.handler = handler;
		this.localCacheUtils = localCacheUtils;
		this.memoryCacheUtils = memoryCacheUtils;

		// 创建一个线程池
		mExecutorService = Executors.newFixedThreadPool(10);
	}

	/**
	 * 使用子线程去请求网络, 把图片抓取回来, 再发送给主线程
	 * @param imageUrl
	 */
	public void getBitmapFromNet(String imageUrl, int position) {
		// new Thread(new InternalRunnable(imageUrl, position)).start();
		
		// 让线程池执行一个任务.
		mExecutorService.execute(new InternalRunnable(imageUrl, position));
	}

	class InternalRunnable implements Runnable {
		
		private String imageUrl;
		private int position;
		
		public InternalRunnable(String imageUrl, int position) {
			this.imageUrl = imageUrl;
			this.position = position;
		}

		@Override
		public void run() {
			HttpURLConnection conn = null;
			try {
				conn = (HttpURLConnection) new URL(imageUrl).openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				conn.setReadTimeout(5000);
				conn.connect();
				
				int responseCode = conn.getResponseCode();
				if(responseCode == 200) {
					InputStream is = conn.getInputStream();
					Bitmap bm = BitmapFactory.decodeStream(is);
					
					// 发送到主线程中显示
					Message msg = handler.obtainMessage();
					msg.obj = bm;
					msg.what = SUCCESS;
					msg.arg1 = position;
					handler.sendMessage(msg);
					
					// 向内存存一份
					memoryCacheUtils.putBitmap2Memory(imageUrl, bm);
					// 向本地存一份
					localCacheUtils.putBitmap2Local(imageUrl, bm);
				}
			} catch (Exception e) {
				e.printStackTrace();
				Message msg = handler.obtainMessage();
				msg.what = FAILED;
				msg.arg1 = position;
				handler.sendMessage(msg);
			} finally {
				if(conn != null) {
					conn.disconnect(); // 断开连接
				}
			}
		}
	}
}
