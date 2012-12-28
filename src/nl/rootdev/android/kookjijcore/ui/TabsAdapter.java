package nl.rootdev.android.kookjijcore.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;

/**
 * Tab adapter for making horizontal scrolling (swiping)
 * possible.
 * 
 * @author mark
 *
 */
public class TabsAdapter extends FragmentPagerAdapter implements ActionBar.TabListener, ViewPager.OnPageChangeListener {
	private List<TabInfo> tabs_;
	private SherlockFragmentActivity activity_;
	private ViewPager pager_;
	
	private static final class TabInfo {
        private final Class<?> clss;
        private final Bundle args;

        public TabInfo(Class<?> _class, Bundle _args)
        {
                clss = _class;
                args = _args;
        }
	}
	
	public TabsAdapter(SherlockFragmentActivity activity, ViewPager pager) {
		super(activity.getSupportFragmentManager());
		tabs_ = new ArrayList<TabInfo>();
		
		pager.setAdapter(this);
		pager.setOnPageChangeListener(this);
		pager_ = pager;
		activity_ = activity;
	}

	public void addTab(Tab tab, Class<?> instance, Bundle args) {
		addTab(tab, instance, args, false);
	}
	
	public void addTab(Tab tab, Class<?> instance, Bundle args, boolean setSelected) {
		TabInfo info = new TabInfo(instance, args);
		tab.setTag(info);
		tab.setTabListener(this);
		tabs_.add(info);
		activity_.getSupportActionBar().addTab(tab, setSelected);
		notifyDataSetChanged();
	}

	@Override
	public Fragment getItem(int position) {
		TabInfo info = tabs_.get(position);
		return Fragment.instantiate(activity_, info.clss.getName(), info.args);
	}

	@Override
	public int getCount() {
		return tabs_.size();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		TabInfo meta = (TabInfo) tab.getTag();
		int i = 0;
		for (; i < tabs_.size(); i++) {
			if (tabs_.get(i) == meta) {
				pager_.setCurrentItem(i, true);
			}
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		activity_.getSupportActionBar().setSelectedNavigationItem(arg0);
	}

}