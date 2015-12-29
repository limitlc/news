package com.itheima45.zhbj.domain;

import java.util.List;

/**
 * @author andong
 * ��������ҳ������ʵ����
 */
public class NewsCenterBean {
	
	public List<NewsCenterMenu> data;
	public List<String> extend;
	public int retcode;
	
	/**
	 * @author andong
	 * �����������˵�������
	 */
	public class NewsCenterMenu {
		
		public List<NewsMenuTab> children;
		public int id;
		public String title;
		public int type;
		public String url;
		public String url1;
		public String dayurl;
		public String excurl;
		public String weekurl;
		
		@Override
		public String toString() {
			return "NewsCenterMenu [children=" + children + ", id=" + id
					+ ", title=" + title + ", type=" + type + ", url=" + url
					+ ", url1=" + url1 + ", dayurl=" + dayurl + ", excurl="
					+ excurl + ", weekurl=" + weekurl + "]";
		}
	}
	
	/**
	 * @author andong
	 * ���Ų˵���Ӧ������
	 */
	public class NewsMenuTab {
		
		public int id;
		public String title;
		public int type;
		public String url;
	}
}
