package nl.rootdev.android.kookjijcore;

import nl.rootdev.android.kookjijcore.ui.TabsAdapter;
import nl.rootdev.android.kookjijcore.ui.frames.SearchFrame;
import nl.rootdev.android.kookjijcore.utils.AndroidUtilities;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

public class SearchActivity extends SherlockFragmentActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        AndroidUtilities.instantiate(this);
    	
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getExtras().getString("query"));
    	AndroidUtilities.getInstance().setChristmasTheme(bar, getApplicationContext());

		ViewPager pager = new ViewPager(this);
		pager.setId(R.id.normal);
		TabsAdapter tabAdapter = new TabsAdapter(this, pager);
    	tabAdapter.addTab(bar.newTab().setText(""), SearchFrame.class, getIntent().getExtras());
    	setContentView(AndroidUtilities.getInstance().injectAdvertisement(this, pager));        
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
}
