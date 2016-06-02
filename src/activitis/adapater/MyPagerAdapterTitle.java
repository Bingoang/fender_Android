package activitis.adapater;

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
	 * ���ص���ҳ������
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return viewList.size();
	}
	/**
	 * View�Ƿ��������ڶ���
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	
	/**
	 * ʵ����һ��Ҷ��
	 */
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		container.addView(viewList.get(position));
		return viewList.get(position);
		
	}
	/**
	 * ����һ��Ҷ��
	 */
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		container.removeView(viewList.get(position));
	}
	/**
	 * ����Ҷ������
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		// TODO Auto-generated method stub
		return titleList.get(position);
	}

}