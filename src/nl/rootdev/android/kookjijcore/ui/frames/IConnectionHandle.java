package nl.rootdev.android.kookjijcore.ui.frames;

/**
 * Methods for intervening from the UI on network actions.
 * 
 * This class was initially added to the RecipieFrame so it's children
 * can communicate back to it in case the user changed his mind.
 * 
 * @author mark
 */
public interface IConnectionHandle {
	/**
	 * Stop the current download.
	 */
	public void stopDownload();
	
	/**
	 * Start a new download connection dropping
	 * the current.
	 */
	public void retryDownload();
	
	/**
	 * Get the amount of percentage loaded.
	 * @return int Number from 0 to 100 indicating the percentual completeness.
	 */
	public int getLoadingPercentage();
}
