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
 * ��������ҳ��
 */
public class NewsDetailUI extends Activity implements OnClickListener {

	private String url;
	private int currentSelectTextSize = 2; // ��ǰĬ��ѡ�е�����
	private int tempSelectTextsize; // ��ʱ��ѡ���������
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

		// ��ȡWebView�ĸ�����
		settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true); // ����javascript�ű�
		settings.setBuiltInZoomControls(true); // ���ý����ϷŴ����С��ť
		settings.setUseWideViewPort(true); // ����˫���Ŵ�, ˫����С����
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
		 //�ر�sso��Ȩ
		 oks.disableSSOWhenAuthorize(); 

		 // ����ʱNotification��ͼ�������
		 oks.setNotification(R.drawable.icon_150, getString(R.string.app_name));
		 // text�Ƿ����ı�������ƽ̨����Ҫ����ֶ�
		 oks.setText("�����ǵػ�!!!");
		 // imagePath��ͼƬ�ı���·����Linked-In�����ƽ̨��֧�ִ˲���
		 oks.setImagePath("/mnt/sdcard/xinlang.jpg");//ȷ��SDcard������ڴ���ͼƬ

		 // ��������GUI
		 oks.show(this);
	}

	/**
	 * �����ı������С�ĶԻ���
	 */
	private void showChangeTextSizeDialog() {
		Builder builder = new Builder(this);
		builder.setTitle("ѡ�������С");
		
		final String[] items = {"���������", "�������", "��������", "С������", "��С������"};
		builder.setSingleChoiceItems(items, currentSelectTextSize, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				System.out.println("����: " + items[which]);
				tempSelectTextsize = which;
			}});
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				currentSelectTextSize = tempSelectTextsize;
				changeTextSize();
			}});
		builder.setNegativeButton("ȡ��", null);
		builder.show();
	}

	/**
	 * ����currentSelectTextSize���ı������С
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
