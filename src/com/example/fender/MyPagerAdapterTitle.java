package com.example.fender;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

public class MyPagerAdapterTitle extends PagerAdapter{
	
	private List<View>viewList;
	private List<String>titleList;
	public MyPagerAdapterTitle(List<View> viewList,List<String>titleList) {
		this.viewList = viewList;
		this.titleList=titleList;
	}
	
	/**
	 * 返回的是页卡数量
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return viewList.size();
	}
	/**
	 * View是否来来自于对象
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	
	/**
	 * 实例化一个叶卡
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(viewList.get(position));
		return viewList.get(position);
		
	}
	/**
	 * 销毁一个叶卡
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView(viewList.get(position));
	}
	/**
	 * 设置叶卡标题
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titleList.get(position);
	}

}
