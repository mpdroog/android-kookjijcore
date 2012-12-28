package nl.rootdev.android.kookjijcore;

import nl.rootdev.android.kookjijcore.ui.TabsAdapter;
import nl.rootdev.android.kookjijcore.ui.fixes.MenuItemSearchAction;
import nl.rootdev.android.kookjijcore.ui.fixes.SearchPerformListener;
import nl.rootdev.android.kookjijcore.ui.frames.CategoryFrame;
import nl.rootdev.android.kookjijcore.utils.AndroidUtilities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class CategoryMain extends SherlockFragmentActivity implements SearchPerformListener
{
	private TabsAdapter tabAdapter;
	private ViewPager pager;
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onRestoreInstanceState(savedInstanceState);
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);    	
        AndroidUtilities.instantiate(this);
        Bundle bundle = getIntent().getExtras();
    	
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);        
        bar.setTitle(bundle.getString("title"));
    	AndroidUtilities.getInstance().setChristmasTheme(bar, getApplicationContext());

		pager = new ViewPager(this);
		pager.setId(R.id.normal);
		tabAdapter = new TabsAdapter(this, pager);
		String[] tabNames = getResources().getStringArray(bundle.getInt("categoryIndex"));
		for(String tabName : tabNames) {
			Bundle child = new Bundle();
			child.putString("name", tabName);
	    	tabAdapter.addTab(bar.newTab().setText(tabName), CategoryFrame.class, child);
		}
    	setContentView(AndroidUtilities.getInstance().injectAdvertisement(this, pager));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	Context context = getSupportActionBar().getThemedContext();
    	new MenuItemSearchAction(context, menu, this); // not working??
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
            	finish();
            	return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void performSearch(String query) {
		Intent intent = new Intent(this, SearchActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("query", query);
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
