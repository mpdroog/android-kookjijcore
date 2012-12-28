package nl.rootdev.android.kookjijcore.ui.frames;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nl.rootdev.android.kookjijcore.datastructures.ISearchRecipie;
import nl.rootdev.android.kookjijcore.datastructures.pb.Recipie;
import nl.rootdev.android.kookjijcore.datastructures.pb.SearchRecipie;
import nl.rootdev.android.kookjijcore.ui.fragments.CategoryFragment;
import nl.rootdev.android.kookjijcore.ui.tasks.AsyncDownload;
import nl.rootdev.android.kookjijcore.utils.AndroidUtilities;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

/**
 * The master of a group of fragments responsible in
 * coordinating the download and showing fragments based
 * on it's results.
 * 
 * It simply tries to download a Category.
 * - During downloading show the LoadingFragment
 * - In case of an 'Error' open up the ErrorFragment
 * - In case of success open the HomeFragment
 * 
 * @author mark
 */
public class SearchFrame extends AbstractLoadingFrame {
	private int _fragmentId;
	
	@Override
	protected void startAsyncDownload(Bundle savedInstanceState)
			throws MalformedURLException {
		_download = new AsyncDownload(getSherlockActivity().getCacheDir().toString()) {
			private ISearchRecipie _results;
			
			@Override
			protected void onPostExecute(String result) {
				if(getException() == null) {
					stopAbstractLoadingFrame();
					final CategoryFragment home = new CategoryFragment();
					Bundle bundle = new Bundle();
					{
						if (_results.getRecipiesList() != null) {
							String[] names = new String[_results.getRecipiesList().size()];
							long[] lastEdits = new long[_results.getRecipiesList().size()];
							long[] ids = new long[_results.getRecipiesList().size()];
							
							int i = 0;
							for (Recipie item : _results.getRecipiesList()) {
								names[i] = item.getName();
								lastEdits[i] = item.getLastedit();
								ids[i] = item.getId();
								i++;
							}
						
							bundle.putStringArray("names", names);
							bundle.putLongArray("lastEdits", lastEdits);
							bundle.putLongArray("ids", ids);
							bundle.putString("query", getArguments().getString("query"));
						}
					}
					home.setArguments(bundle);
					
					final FragmentTransaction action2 = getFragmentManager().beginTransaction();
					action2.replace(getFragmentId(), home).commitAllowingStateLoss();
				} else {
					openError(getException());
				}
			}
			
			@Override
			protected void startTextDownload(InputStream link) {
				try {
					link = AndroidUtilities.getInstance().encapsulateGZipOnNeed(link);
					_results = new SearchRecipie();
					Schema<SearchRecipie> schema = RuntimeSchema.getSchema(SearchRecipie.class);
					ProtobufIOUtil.mergeFrom(link, (SearchRecipie) _results, schema);
				} catch (Exception e) {
					setException(e);
				}
				
			}
			
			@Override
			protected void getImageDownload(Bitmap image) {
			}
		};

		_download.execute(new URL[] {
			new URL(AsyncDownload.URL_BASE + "api.php?f=s&i=" + getArguments().getString("query") + "&date=" + AndroidUtilities.getInstance().getDate()),
		});
	}

	/**
	 * Using dirty trick here to ensure 'always unique'.
	 */
	@Override
	protected int getFragmentId() {
		if (_fragmentId == 0) {
			_fragmentId = AndroidUtilities.getInstance().getUniqueNumber();
		}
		return _fragmentId;
	}
}
