package com.example.fender;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;

public class SettingActivity extends Activity implements OnClickListener{
	private boolean flag1,flag2,flag3,flag4,flag5,flag6,flag7,flag8,flag9,flag10,flag11;
	private ImageButton backImageButton;
	private ImageButton settingImageButton1;
	private ImageButton settingImageButton2;
	private ImageButton settingImageButton3;
	private ImageButton settingImageButton4;
	private ImageButton settingImageButton5;
	private ImageButton settingImageButton6;
	private ImageButton settingImageButton7;
	private ImageButton settingImageButton8;
	private ImageButton settingImageButton9;
	private ImageButton settingImageButton10;
	private ImageButton settingImageButton11;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.setting);
	    initialButton();
	}

	private void initialButton()
	{
		settingImageButton1=(ImageButton) findViewById(R.id.idSettingButton1);
		settingImageButton2=(ImageButton) findViewById(R.id.idSettingButton2);
		settingImageButton3=(ImageButton) findViewById(R.id.idSettingButton3);
		settingImageButton4=(ImageButton) findViewById(R.id.idSettingButton4);
		settingImageButton5=(ImageButton) findViewById(R.id.idSettingButton5);
		settingImageButton6=(ImageButton) findViewById(R.id.idSettingButton6);
		settingImageButton7=(ImageButton) findViewById(R.id.idSettingButton7);
		settingImageButton8=(ImageButton) findViewById(R.id.idSettingButton8);
		settingImageButton9=(ImageButton) findViewById(R.id.idSettingButton9);
		settingImageButton10=(ImageButton) findViewById(R.id.idSettingButton10);
		settingImageButton11=(ImageButton) findViewById(R.id.idSettingButton11);
		settingImageButton1.setOnClickListener(this);
		settingImageButton2.setOnClickListener(this);
		settingImageButton3.setOnClickListener(this);
		settingImageButton4.setOnClickListener(this);
		settingImageButton5.setOnClickListener(this);
		settingImageButton6.setOnClickListener(this);
		settingImageButton7.setOnClickListener(this);
		settingImageButton8.setOnClickListener(this);
		settingImageButton9.setOnClickListener(this);
		settingImageButton10.setOnClickListener(this);
		settingImageButton11.setOnClickListener(this);
		flag1=flag2=flag3=flag4=flag5=flag6=flag7=flag8=flag9=flag10=flag11=true;
		backImageButton = (ImageButton) findViewById(R.id.idSettingBack);
		backImageButton.setOnClickListener(this);
		backImageButton.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View arg0, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction() == MotionEvent.ACTION_DOWN)
					{
						backImageButton.setBackgroundResource(R.drawable.settingbackdown);
					}
					if(event.getAction() == MotionEvent.ACTION_UP)
					{
						backImageButton.setBackgroundResource(R.drawable.settingbackup);
					}
					return false;
				}
			});
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.idSettingBack:
			finish();
			break;
		case R.id.idSettingButton1:
			donghuaButton(1);
			break;
		case R.id.idSettingButton2:
			donghuaButton(2);
			break;
		case R.id.idSettingButton3:
			donghuaButton(3);
			break;
		case R.id.idSettingButton4:
			donghuaButton(4);
			break;
		case R.id.idSettingButton5:
			donghuaButton(5);
			break;
		case R.id.idSettingButton6:
			donghuaButton(6);
			break;
		case R.id.idSettingButton7:
			donghuaButton(7);
			break;
		case R.id.idSettingButton8:
			donghuaButton(8);
			break;
		case R.id.idSettingButton9:
			donghuaButton(9);
			break;
		case R.id.idSettingButton10:
			donghuaButton(10);
			break;
		case R.id.idSettingButton11:
			donghuaButton(11);
			break;
			
		default:
			break;
		}
		
	}
	
	private void donghuaButton(int i)
	{
		switch (i) {
		case 1:
			if(flag1)
			{
				settingImageButton1.setBackgroundResource(R.drawable.settingoff);
				flag1=false;
			}else {
				settingImageButton1.setBackgroundResource(R.drawable.settingon);
				flag1=true;
			}
			break;
		case 2:
			if(flag2)
			{
				settingImageButton2.setBackgroundResource(R.drawable.settingoff);
				flag2=false;
			}else {
				settingImageButton2.setBackgroundResource(R.drawable.settingon);
				flag2=true;
			}
			break;
		case 3:
			if(flag3)
			{
				settingImageButton3.setBackgroundResource(R.drawable.settingoff);
				flag3=false;
			}else {
				settingImageButton3.setBackgroundResource(R.drawable.settingon);
				flag3=true;
			}
			break;
		case 4:
			if(flag4)
			{
				settingImageButton4.setBackgroundResource(R.drawable.settingoff);
				flag4=false;
			}else {
				settingImageButton4.setBackgroundResource(R.drawable.settingon);
				flag4=true;
			}
			break;
		case 5:
			if(flag5)
			{
				settingImageButton5.setBackgroundResource(R.drawable.settingoff);
				flag5=false;
			}else {
				settingImageButton5.setBackgroundResource(R.drawable.settingon);
				flag5=true;
			}
			break;
		case 6:
			if(flag6)
			{
				settingImageButton6.setBackgroundResource(R.drawable.settingoff);
				flag6=false;
			}else {
				settingImageButton6.setBackgroundResource(R.drawable.settingon);
				flag6=true;
			}
			break;
		case 7:
			if(flag7)
			{
				settingImageButton7.setBackgroundResource(R.drawable.settingoff);
				flag7=false;
			}else {
				settingImageButton7.setBackgroundResource(R.drawable.settingon);
				flag7=true;
			}
			break;
		case 8:
			if(flag8)
			{
				settingImageButton8.setBackgroundResource(R.drawable.settingoff);
				flag8=false;
			}else {
				settingImageButton8.setBackgroundResource(R.drawable.settingon);
				flag8=true;
			}
			break;
		case 9:
			if(flag9)
			{
				settingImageButton9.setBackgroundResource(R.drawable.settingoff);
				flag9=false;
			}else {
				settingImageButton9.setBackgroundResource(R.drawable.settingon);
				flag9=true;
			}
			break;
		case 10:
			if(flag10)
			{
				settingImageButton10.setBackgroundResource(R.drawable.settingoff);
				flag10=false;
			}else {
				settingImageButton10.setBackgroundResource(R.drawable.settingon);
				flag10=true;
			}
			break;
		case 11:
			if(flag11)
			{
				settingImageButton11.setBackgroundResource(R.drawable.settingoff);
				flag11=false;
			}else {
				settingImageButton11.setBackgroundResource(R.drawable.settingon);
				flag11=true;
			}
			break;

		default:
			break;
		}
		
	}
	
	
}
