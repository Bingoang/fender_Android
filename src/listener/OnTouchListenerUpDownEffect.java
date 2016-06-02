package listener;

import com.example.fender.R;

import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class OnTouchListenerUpDownEffect implements OnTouchListener{

	protected ImageButton imageBut;

	public OnTouchListenerUpDownEffect(ImageButton data) {
		imageBut = data;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		setClickAnimation(event, imageBut);
		return false;
	}

	public void setClickAnimation(MotionEvent event, ImageButton but) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			but.setBackgroundResource(R.drawable.touchdown_bg);
		} else {
			but.setBackgroundResource(R.drawable.touchup_bg);
		}
	}
}
