package com.example.fender;

import java.util.ArrayList;
import java.util.List;

import donghua.ZS_RadioButton;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Event;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends Activity{
	
	private List<View>viewList;
	private ViewPager pager;
	private ImageButton imageButton;
	private List<ImageView> imageViewList = new ArrayList<ImageView>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guidepager);
        /**
         * 通过View对象做viewPager的数据远
         */
        viewList = new ArrayList<View>();
        View view1=View.inflate(this, R.layout.view1, null);
        View view2=View.inflate(this, R.layout.view2, null);
        View view3=View.inflate(this, R.layout.view3, null);
        viewList.add(view1);
        viewList.add(view2);
        viewList.add(view3);
        //初始化viewpager
        pager = (ViewPager) findViewById(R.id.pager);
        //创建适配器
        MyPagerAdapter adapter = new MyPagerAdapter(viewList);
        //Viewpager 加载适配器
        pager.setAdapter(adapter);
        
        imageButton = (ImageButton)view3.findViewById(R.id.tiyan);
        imageButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent event) {
				// TODO Auto-generated method stub
				if(event.getAction()==MotionEvent.ACTION_DOWN)
				{
					//按下事件
					imageButton.setImageResource(R.drawable.tiyan_down);
					
				}
				if(event.getAction()==MotionEvent.ACTION_UP)
				{
					//放开事件
					imageButton.setImageResource(R.drawable.tiyan);
				}
				
				return false;
			}
        	
        });
        imageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				Intent intent = new Intent();
				intent.setClass(MainActivity.this, Main.class);
				startActivity(intent);
				MainActivity.this.finish();
			}
		});
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }



}
