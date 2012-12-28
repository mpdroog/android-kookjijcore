package nl.rootdev.android.kookjijcore.ui;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

/**
 * 
 * @see http://benjii.me/2010/08/endless-scrolling-listview-in-android/
 * @author mark
 */
public abstract class EndlessScrollListener implements OnScrollListener {

    private int visibleThreshold = 5;
    private int currentPage = 0;
    private int previousTotal = 0;
    private boolean loading = false;

    public EndlessScrollListener() {
    }
    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
            int visibleItemCount, int totalItemCount) {
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
                currentPage++;
            }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // I load the next page of gigs using a background task,
            // but you can call any function here.
        	loadInBackground();
            loading = true;
        }
    }
    
    protected abstract void loadInBackground();

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }
}