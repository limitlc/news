package com.itheima45.zhbj.domain;

import java.util.List;

/**
 * @author andong
 * 组图数据实体类
 */
public class PhotosBean {

	public PhotoData data;
	public int retcode;
	
	public class PhotoData {

		public String countcommenturl;
		public String more;
		public List<PhotoItemBean> news;
		public String title;
		public List topic;
	}
	
	public class PhotoItemBean {
		
		public String comment;
		public String commentlist;
		public String commenturl;
		public String id;
		public String largeimage;
		public String listimage;
		public String pubdate;
		public String smallimage;
		public String title;
		public String type;
		public String url;
		
	}
}
