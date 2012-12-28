package nl.rootdev.android.kookjijcore.ui.tasks;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import nl.rootdev.android.kookjijcore.ui.tasks.utils.ProgressInputStream;
import nl.rootdev.android.kookjijcore.utils.AndroidUtilities;
import nl.rootdev.android.kookjijcore.utils.ConnectionTypes;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/**
 * Base class for downloading data from the webserver.
 * 
 * What this class does for you:
 * - Return how much % is finished
 * - Downloading only text if internet connection is slow
 *   (on fast connection download text+additional)
 * - Start HTTP caching on Android's supporting this
 * 
 * @author mark
 *
 */
public abstract class AsyncDownload extends AsyncTask<URL, String, String> {
	/** Stream to webserver with text */
	protected ProgressInputStream _textStream;
	/** Stream to webserver with image */
	protected ProgressInputStream _imageStream;
	/** Exception that occured during I/O with webserver */
	private Exception _exception;
	
	public final static String URL_BASE = "http://android.kookjij.mobi/";
	
	/**
	 * @param tempFolder Use getSherlockActivity().getCacheDir()
	 */
	public AsyncDownload(String tempFolder) {
		enableHttpResponseCache(tempFolder);
	}
	
	@Override
	protected void onCancelled() {
		super.onCancelled();
		try {
			if (_textStream != null) { _textStream.close(); }
			if (_textStream != null) { _imageStream.close(); }
		}
		catch(Exception e) {
			// Ignore closing errors
		}
	}
	
	/**
	 * Getting the bytecount is done through a synchronised action.
	 * 
	 * Watch out for blocking behaviour and download-speed slowdown
	 * because of this.
	 * @return
	 */
	public int getDownloadProgress() {
		if (_textStream == null) {
			return 0;
		}
		int percentage = _textStream.getProgress();
		percentage += _imageStream != null ? _imageStream.getProgress() : 100;
		return percentage / 200 * 100;
	}
	
	/**
	 * Cache the HTTP-response if this Android feature is possible.
	 * http://developer.android.com/training/efficient-downloads/
	 * redundant_redundant.html
	 */
	private void enableHttpResponseCache(String tempFolder) {
		try {
			long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
			File httpCacheDir = new File(tempFolder,
					"http");
			Class.forName("android.net.http.HttpResponseCache")
					.getMethod("install", File.class, long.class)
					.invoke(null, httpCacheDir, httpCacheSize);
		} catch (Exception httpResponseCacheNotAvailable) {
			System.out.println("HTTP response cache is NOT available");
		}
	}
	
	protected abstract void startTextDownload(InputStream connection);
	protected abstract void getImageDownload(Bitmap image);

	protected String doInBackground(URL... params) {		
		try {
			ConnectionTypes connection = AndroidUtilities.getInstance().getConnectionSpeed();
			// Always read text
			{
				URLConnection link = params[0].openConnection();
				if (isCancelled()) {
					return "";
				}
				_textStream = new ProgressInputStream(link.getInputStream(), link.getContentLength());
				startTextDownload(_textStream);
				_textStream.close();
			}
			if (params.length == 2) {
				// Only image on speedy connection
				if(connection == ConnectionTypes.TYPE_WIFI || connection == ConnectionTypes.TYPE_MOBILE_FAST) {
					URLConnection img = params[1].openConnection();
					if (isCancelled()) {
						return "";
					}
					_imageStream = new ProgressInputStream(img.getInputStream(), img.getContentLength());
					getImageDownload(BitmapFactory.decodeStream(_imageStream));
					_imageStream.close();
				}
			}
		}
		catch(Exception e) {
			_exception = e;
		}
		return "";
	}
	
	public void setException(Exception e) {
		_exception = e;
	}

	public Exception getException() {
		return _exception;
	}
}