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
 * ��ӭ����
 */
public class WelcomeUI extends Activity {

	// �Ƿ�򿪹���ҳ��ļ�key
	public static final String IS_OPEN_MAIN_PAGER_KEY = "is_open_main_pager_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome);
        
        init();
    }

	/**
	 * ��ʼ���ؼ��Ͷ���
	 */
	private void init() {
		RelativeLayout rlRoot = (RelativeLayout) findViewById(R.id.rl_welcome_root);
		
		AnimationSet animationSet = new AnimationSet(false);
		
		// ��ת����: �����ĵ���ת, 0~360
		RotateAnimation rotateAnim = new RotateAnimation(
				0, 360, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		rotateAnim.setDuration(1000);
		rotateAnim.setFillAfter(true); // ���ö���ִ����Ϻ�, �ؼ�ͣ���ڽ�����״̬��.
		
		// ���Ŷ���: 0~1 ��û�е���ȫ��ʾ, �������ĵ��������
		ScaleAnimation scaleAnim = new ScaleAnimation(
				0, 1, 
				0, 1, 
				Animation.RELATIVE_TO_SELF, 0.5f, 
				Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnim.setDuration(1000);
		scaleAnim.setFillAfter(true);
		
		// ���䶯��: �Ӳ���ʾ����ʾ
		AlphaAnimation alphaAnim = new AlphaAnimation(0, 1);
		alphaAnim.setDuration(2000);
		alphaAnim.setFillAfter(true);
		
		// �Ѷ�����ӵ�������
		animationSet.addAnimation(rotateAnim);
		animationSet.addAnimation(scaleAnim);
		animationSet.addAnimation(alphaAnim);
		animationSet.setAnimationListener(new MyAnimationListener());
		
		// ִ�ж���
		rlRoot.startAnimation(animationSet);
	}

	class MyAnimationListener implements AnimationListener {

		@Override
		public void onAnimationEnd(Animation animation) {
			// �жϵ�ǰ����ת����ҳ�滹������ҳ��
			
			boolean isOpenMainPager = CacheUtils.getBoolean(WelcomeUI.this, IS_OPEN_MAIN_PAGER_KEY, false);
			if(isOpenMainPager) {
				// ��ת����ҳ��
				System.out.println("��ת����ҳ��");

				startActivity(new Intent(WelcomeUI.this, MainUI.class));
			} else {
				// ��ת������ҳ��
				System.out.println("��ת������ҳ��");
				
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
