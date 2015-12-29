package com.itheima45.zhbj.domain;

import java.util.List;

/**
 * @author andong
 * 新闻中心页面数据实体类
 */
public class NewsCenterBean {
	
	public List<NewsCenterMenu> data;
	public List<String> extend;
	public int retcode;
	
	/**
	 * @author andong
	 * 新闻中心左侧菜单数据类
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
	 * 新闻菜单对应的数据
	 */
	public class NewsMenuTab {
		
		public int id;
		public String title;
		public int type;
		public String url;
	}
}
