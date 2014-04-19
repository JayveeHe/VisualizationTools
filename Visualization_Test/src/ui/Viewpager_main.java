package ui;

import java.util.Locale;

import jayvee.visualization_test.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class Viewpager_main extends FragmentActivity {
	// 相关参数的设置
	public static final int PAGE_NUM = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_charts);
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		MyPagerAdapter myPagerAdapter = new MyPagerAdapter(
				getSupportFragmentManager());
		pager.setAdapter(myPagerAdapter);
	}

	class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int position) {
			// TODO Auto-generated method stub
			return MyFragment.newInstance(position);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return PAGE_NUM;
		}
		
		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
//				return getString(R.string.title_section1).toUpperCase(l);
			return "好友性别分布";
			case 1:
//				return getString(R.string.title_section2).toUpperCase(l);
			return "转发频次";
			case 2:
//				return getString(R.string.title_section3).toUpperCase(l);
			return "用户属性";
			}
			return null;
		}
	}

}
