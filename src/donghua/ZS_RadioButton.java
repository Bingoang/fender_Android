package donghua;

import java.util.ArrayList;
import java.util.List;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;

public class ZS_RadioButton {
	
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
	private boolean flag=true;
	int length=0;
	
	public void addListView( List<ImageView> imageViewList )
	{
		this.imageViewList = imageViewList;
		length=imageViewList.size();
	}
	
	
	private void closeAnim() {
		
		for(int i =1;i<length;i++)
		{
			ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewList.get(i), "translationY",i*100,0F);
			animator.setDuration(500);
			animator.setStartDelay(i*300);
			animator.start();
			//Log.i("AL", "关闭关闭");
		}
	}


	private void startAnim() {
		
		for(int i =1;i<length;i++)
		{
			ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewList.get(i), "translationY", 0F,i*100);
			animator.setDuration(500);
			animator.setInterpolator(new BounceInterpolator());
			animator.setStartDelay(i*300);
			animator.start();
		}
	}
	private void closeAnim1() {//自身用
		
		for(int i =1;i<length;i++)
		{
			ObjectAnimator animator = ObjectAnimator.ofFloat(imageViewList.get(i), "translationY",i*100,0F);
			animator.setDuration(100);
			animator.setStartDelay(i*100+300);
			animator.start();
			//Log.i("AL", "关闭关闭");
		}
	}
	public void start() {
		if(flag==true)
		{
			 startAnim();
			flag=false;
		}else {
			closeAnim();
			flag=true;
		}
	}
	public void clickOne(View view) {
		ObjectAnimator animator=ObjectAnimator.ofFloat(view, "alpha", 1F,0F);
		animator.setDuration(300);
		animator.start();
		animator=ObjectAnimator.ofFloat(view, "alpha", 1F,1F);
		animator.setStartDelay(300);
		animator.setDuration(1);
		animator.start();
		flag = true;
		closeAnim1();

	}
}
