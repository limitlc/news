package com.itheima45.zhbj.domain;

import java.util.List;

/**
 * @author andong
 * ҳǩ����ҳ������ʵ����
 */
public class TabDetailBean {

	public int retcode;
	public TabDetailData data;
	
	public class TabDetailData {
		
		public String countcommenturl;
		public String more;
		public List<News> news;
		public String title;
		public List<Topic> topic;
		public List<TopNew> topnews;
	}
	
	/**
	 * @author andong
	 * �б���������
	 */
	public class News {

		public String comment;
		public String commentlist;
		public String commenturl;
		public String id;
		public String listimage;
		public String pubdate;
		public String title;
		public String type;
		public String url;
	}
	
	public class Topic {

		public String description;
		public String id;
		public String listimage;
		public String sort;
		public String title;
		public String url;
	}
	
	/**
	 * @author andong
	 * �������ŵ�����
	 */
	public class TopNew {

		public String comment;
		public String commentlist;
		public String commenturl;
		public String id;
		public String pubdate;
		public String title;
		public String topimage;
		public String type;
		public String url;
	}
}
