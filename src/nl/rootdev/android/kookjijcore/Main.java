package nl.rootdev.android.kookjijcore;

import nl.rootdev.android.kookjijcore.ui.TabsAdapter;
import nl.rootdev.android.kookjijcore.ui.fixes.MenuItemSearchAction;
import nl.rootdev.android.kookjijcore.ui.fixes.SearchPerformListener;
import nl.rootdev.android.kookjijcore.ui.fragments.CategoriesFragment;
import nl.rootdev.android.kookjijcore.ui.frames.ColumnFrame;
import nl.rootdev.android.kookjijcore.ui.frames.RecipieFrame;
import nl.rootdev.android.kookjijcore.utils.AndroidUtilities;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;

public class Main extends SherlockFragmentActivity implements SearchPerformListener {
	private TabsAdapter tabAdapter;
	private ViewPager pager;

	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        AndroidUtilities.instantiate(this);
    	
        ActionBar bar = getSupportActionBar();
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        bar.setDisplayShowTitleEnabled(true);
        bar.setDisplayShowHomeEnabled(true);
    	AndroidUtilities.getInstance().setChristmasTheme(bar, getApplicationContext());
        
		pager = new ViewPager(this);
		pager.setId(R.id.normal);
		tabAdapter = new TabsAdapter(this, pager);
    	tabAdapter.addTab(bar.newTab().setText(R.string.tab_category), CategoriesFragment.class, null);
		tabAdapter.addTab(bar.newTab().setText(R.string.tab_recipie_day), RecipieFrame.class, null, true);
    	tabAdapter.addTab(bar.newTab().setText("Column"), ColumnFrame.class, null);
    	//tabAdapter.addTab(bar.newTab().setText("Favorieten"), FavoriteGridFragment.class, null);

    	setContentView(AndroidUtilities.getInstance().injectAdvertisement(this, pager));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	Context context = getSupportActionBar().getThemedContext();
    	//MenuItem item = menu.add(0,4,0,"H");
    	//item.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
    	new MenuItemSearchAction(context, menu, this);
        return true;
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
