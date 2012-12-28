package nl.rootdev.android.kookjijcore.ui.frames;

import nl.rootdev.android.kookjijcore.R;
import nl.rootdev.android.kookjijcore.ui.tasks.AsyncDownload;
import nl.rootdev.android.kookjijcore.utils.AndroidUtilities;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * The master of a group of fragments responsible in
 * coordinating the download and showing fragments based
 * on it's results.
 * 
 * It simply tries to download a Recipie.
 * - During downloading show the LoadingFragment
 * - In case of an 'Error' open up the ErrorFragment
 * - In case of success open the HomeFragment
 * 
 * @author mark
 */
public abstract class AbstractLoadingFrame extends SherlockFragment implements IConnectionHandle  {	
	/** Reference in case we need to intervene (UI requests) */
	protected AsyncDownload _download;

	/** Interval used to check download delta */
	private final static long DOWNLOAD_INTERVAL_CHECK = 3000L;
	/** Timer for updating progress-screen */
	private Handler _timer;
	private int _lastPercentage;

	public AbstractLoadingFrame() {
		super();
		_timer = new Handler();
	}
	
	private Runnable _updateTask = new Runnable() {
		
		@Override
		public void run() {
			final TextView percentage = (TextView)getView().findViewById(R.id.percentage);

			int currentPercentage = getLoadingPercentage();
			if(percentage != null) {
				if(currentPercentage - _lastPercentage >= 10) {
					// Normal speed
					percentage.setText(currentPercentage + "%");
				} else {
					// SLOOOOW, 3sec and less than 10 percent
					percentage.setText(currentPercentage + "%");
				}
			}
			_lastPercentage = currentPercentage;
			_timer.postDelayed(this, DOWNLOAD_INTERVAL_CHECK);
		}
	};
	
	/**
	 * Stop downloading the data from the web.
	 */
	public void stopDownload() {
		_timer.removeCallbacks(_updateTask);
		_download.cancel(true);
	}
	
	/**
	 * Stop the download and spawn a new one.
	 * Useful for 'hanging' connections so
	 * the user can say stop and try again.
	 */
	public void retryDownload() {
		_timer.removeCallbacks(_updateTask);
		_download.cancel(true);
		openLoading();
		
		try {
			startAsyncDownload(getArguments());
		} catch (Exception e) {
			openError(e);
		}
	}
		
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}
	
	@Override
	public void onDestroyView() {
		if (_download != null) {
			_download.cancel(true);
			_timer.removeCallbacks(_updateTask);
		}
		super.onDestroyView();
	}
	
	@Override
	public int getLoadingPercentage() {
		return _download.getDownloadProgress();
	}
	
	private void openLoading() {
		_timer.postDelayed(_updateTask, DOWNLOAD_INTERVAL_CHECK);		

		LinearLayout loading = (LinearLayout) getView().findViewById(R.id.loading);
		LinearLayout error = (LinearLayout) getView().findViewById(R.id.error);

		if (error.getVisibility() == View.VISIBLE) {
			loading.setVisibility(View.VISIBLE);
			error.setVisibility(View.INVISIBLE);
		}
	}
	
	protected void stopAbstractLoadingFrame() {
		if (getView() != null) {
			LinearLayout loading = (LinearLayout) getView().findViewById(R.id.loading);
			LinearLayout error = (LinearLayout) getView().findViewById(R.id.error);		
			loading.setVisibility(View.GONE);
			error.setVisibility(View.GONE);			
		}
		_timer.removeCallbacks(_updateTask);		
	}
	
	protected void openError(Exception e) {
		_timer.removeCallbacks(_updateTask);		

		LinearLayout loading = (LinearLayout) getView().findViewById(R.id.loading);
		LinearLayout error = (LinearLayout) getView().findViewById(R.id.error);

		EditText stacktrace = (EditText) getView().findViewById(R.id.stacktrace);
		TextView reason = (TextView) getView().findViewById(R.id.reason);
		
		reason.setText(e.getMessage());
		stacktrace.setText(AndroidUtilities.getInstance().getStacktrace(e));
		
		if (loading.getVisibility() == View.VISIBLE) {
			loading.setVisibility(View.INVISIBLE);
			error.setVisibility(View.VISIBLE);
		}
	}
	
	protected abstract void startAsyncDownload(Bundle savedInstanceState) throws Exception;
	protected abstract int getFragmentId();
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("loaded", true);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		if (savedInstanceState != null) {
			// Already loaded, do nothing
			((LinearLayout) getView().findViewById(R.id.loading)).setVisibility(View.GONE);
			return;
		}
		
		// Loading bindings
		{
			final Button stop = (Button) getView().findViewById(R.id.button_download_stop);
			final Button retry = (Button) getView().findViewById(R.id.button_retry);
			final ProgressBar progress = (ProgressBar) getView().findViewById(R.id.progressBar1);
			
			stop.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					stop.setEnabled(false);
					TextView text = (TextView) getView().findViewById(R.id.textView1);
					text.setText(R.string.stopped_download);
					stopDownload();
					progress.setVisibility(View.INVISIBLE);
				}
			});
			retry.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					stop.setEnabled(true);
					progress.setVisibility(View.VISIBLE);
					retryDownload();
				}
			});
		}
		
		// Error bindings
		{
			final Button expand = (Button) getView().findViewById(R.id.openStacktrace);
			final Button retry = (Button) getView().findViewById(R.id.retry);
			
			retry.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					openLoading();
					retryDownload();
				}
			});
			expand.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					EditText stacktrace = (EditText) getView().findViewById(R.id.stacktrace);
					stacktrace.setVisibility(View.VISIBLE);
					expand.setEnabled(false);
				}
			});
		}

		openLoading();
		try {
			startAsyncDownload(savedInstanceState);
		}
		catch(Exception e) {
			openError(e);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setRetainInstance(true);
		View item = inflater.inflate(R.layout.loadingframe, container, false);
		item.setId(getFragmentId());
		return item;
	}
}
