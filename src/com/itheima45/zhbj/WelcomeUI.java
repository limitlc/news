package com.itheima45.zhbj;

import com.itheima45.zhbj.utils.CacheUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

/**
 * @author andong
 * 欢迎界面
 */
public class WelcomeUI extends Activity {

	// 是否打开过主页面的键key
	public static final String IS_OPEN_MAIN_PAGER_KEY = "is_open_main_pager_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        init();
    }

	/**
	 * 初始化控件和动画
	 */
	private void init() {
		RelativeLayout rlRoot = (RelativeLayout) findViewById(R.id.rl_welcome_root);
		
		AnimationSet animationSet = new AnimationSet(false);
		
		// 旋转动画: 以中心点旋转, 0~360
		RotateAnimation rotateAnim = new RotateAnimation(
				0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim.setDuration(1000);
		rotateAnim.setFillAfter(true); // 设置动画执行完毕后, 控件停留在结束的状态下.
		
		// 缩放动画: 0~1 从没有到完全显示, 基于中心点进行缩放
		ScaleAnimation scaleAnim = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnim.setDuration(1000);
		scaleAnim.setFillAfter(true);
		
		// 渐变动画: 从不显示到显示
		AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
		alphaAnim.setDuration(2000);
		alphaAnim.setFillAfter(true);
		
		// 把动画添加到集合中
		animationSet.addAnimation(rotateAnim);
		animationSet.addAnimation(scaleAnim);
		animationSet.addAnimation(alphaAnim);
		animationSet.setAnimationListener(new MyAnimationListener());
		
		// 执行动画
		rlRoot.startAnimation(animationSet);
	}

	class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			// 判断当前是跳转到主页面还是引导页面
			
			boolean isOpenMainPager = CacheUtils.getBoolean(WelcomeUI.this, IS_OPEN_MAIN_PAGER_KEY, false);
			if(isOpenMainPager) {
				// 跳转到主页面
				System.out.println("跳转到主页面");

				startActivity(new Intent(WelcomeUI.this, MainUI.class));
			} else {
				// 跳转到引导页面
				System.out.println("跳转到引导页面");
				
				startActivity(new Intent(WelcomeUI.this, GuideUI.class));
			}
			
			finish();
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
	}
}
