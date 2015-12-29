package com.itheima45.zhbj.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;

/**
 * @author andong
 * ���ػ��湤����
 */
public class LocalCacheUtils {
	
	// �����Ŀ¼
	private final String CACHE_DIR = "/mnt/sdcard/itheima_zhbj";

	/**
	 * ����Url�洢��ǰͼƬ
	 * @param imageUrl
	 * @param bm
	 */
	public void putBitmap2Local(String imageUrl, Bitmap bm) {
		try {
			String fileName = MD5Encoder.encode(imageUrl);
			File file = new File(CACHE_DIR, fileName); // /mnt/sdcard/itheima_zhbj/aslkjdlhk124312423lkj
			File parentFile = file.getParentFile();
			if(!parentFile.exists()) {
				parentFile.mkdirs();
			}
			
			FileOutputStream fos = new FileOutputStream(file);
			// ��ͼƬͨ����д������
			bm.compress(CompressFormat.JPEG, 100, fos);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * �ӱ����и���urlȡ��ͼƬ
	 * @param imageUrl
	 * @return
	 */
	public Bitmap getBitmapFromLocal(String imageUrl) {
		try {
			String fileName = MD5Encoder.encode(imageUrl);
			File file = new File(CACHE_DIR, fileName); // /mnt/sdcard/itheima_zhbj/aslkjdlhk124312423lkj
			if(file.exists()) {
				// �ļ�����, �л���, ��ȡ����, ������
				
				FileInputStream is = new FileInputStream(file);
				Bitmap bm = BitmapFactory.decodeStream(is);
				return bm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
