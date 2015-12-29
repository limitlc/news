package com.itheima45.zhbj;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.TextSize;
import android.widget.ProgressBar;

/**
 * @author andong
 * 新闻详情页面
 */
public class NewsDetailUI extends Activity implements OnClickListener {

	private String url;
	private int currentSelectTextSize = 2; // 当前默认选中的字体
	private int tempSelectTextsize; // 临时的选择字体变量
	private WebSettings settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.news_detail);
		
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		
//		url = "http://www.itheima.com";
		
		initView();
	}

	private void initView() {
		findViewById(R.id.ib_title_bar_menu).setVisibility(View.GONE);
		findViewById(R.id.tv_title_bar_title).setVisibility(View.GONE);

		findViewById(R.id.ib_title_bar_back).setVisibility(View.VISIBLE);
		findViewById(R.id.ib_title_bar_textsize).setVisibility(View.VISIBLE);
		findViewById(R.id.ib_title_bar_share).setVisibility(View.VISIBLE);

		findViewById(R.id.ib_title_bar_back).setOnClickListener(this);
		findViewById(R.id.ib_title_bar_textsize).setOnClickListener(this);
		findViewById(R.id.ib_title_bar_share).setOnClickListener(this);
		
		final ProgressBar mProgressbar = (ProgressBar) findViewById(R.id.pb_news_detail);
		WebView mWebView = (WebView) findViewById(R.id.wv_news_detail_content);
		mWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				mProgressbar.setVisibility(View.GONE);
			}
		});

		// 获取WebView的辅助类
		settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true); // 启用javascript脚本
		settings.setBuiltInZoomControls(true); // 启用界面上放大和缩小按钮
		settings.setUseWideViewPort(true); // 启用双击放大, 双击缩小功能
		mWebView.loadUrl(url);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_title_bar_back:
			finish();
			break;
		case R.id.ib_title_bar_textsize:
			showChangeTextSizeDialog();
			break;
		case R.id.ib_title_bar_share:
			showShare();
			break;
		default:
			break;
		}
	}
	
	private void showShare() {
		 ShareSDK.initSDK(this);
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 

		 // 分享时Notification的图标和文字
		 oks.setNotification(R.drawable.icon_150, getString(R.string.app_name));
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("天王盖地虎!!!");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 oks.setImagePath("/mnt/sdcard/xinlang.jpg");//确保SDcard下面存在此张图片

		 // 启动分享GUI
		 oks.show(this);
	}

	/**
	 * 弹出改变字体大小的对话框
	 */
	private void showChangeTextSizeDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("选择字体大小");
		
		final String[] items = {"超大号字体", "大号字体", "正常字体", "小号字体", "超小号字体"};
		builder.setSingleChoiceItems(items, currentSelectTextSize, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("字体: " + items[which]);
				tempSelectTextsize = which;
			}});
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentSelectTextSize = tempSelectTextsize;
				changeTextSize();
			}});
		builder.setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 根据currentSelectTextSize来改变字体大小
	 */
	protected void changeTextSize() {
		switch (currentSelectTextSize) {
		case 0:
			settings.setTextSize(TextSize.LARGEST);
			break;
		case 1:
			settings.setTextSize(TextSize.LARGER);
			break;
		case 2:
			settings.setTextSize(TextSize.NORMAL);
			break;
		case 3:
			settings.setTextSize(TextSize.SMALLER);
			break;
		case 4:
			settings.setTextSize(TextSize.SMALLEST);
			break;
		default:
			break;
		}
	}
}
