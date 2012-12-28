package nl.rootdev.android.kookjijcore.ui.frames;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nl.rootdev.android.kookjijcore.datastructures.ICategory;
import nl.rootdev.android.kookjijcore.datastructures.pb.Category;
import nl.rootdev.android.kookjijcore.datastructures.pb.CategoryItem;
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
public class CategoryFrame extends AbstractLoadingFrame {
	private int _fragmentId;
	
	@Override
	protected void startAsyncDownload(Bundle savedInstanceState)
			throws MalformedURLException {
		_download = new AsyncDownload(getSherlockActivity().getCacheDir().toString()) {
			private ICategory _category;
			
			@Override
			protected void onPostExecute(String result) {
				if(getException() == null) {
					stopAbstractLoadingFrame();
					final CategoryFragment home = new CategoryFragment();
					Bundle bundle = new Bundle();
					{
						if (_category.getItemsList() != null) {
							String[] names = new String[_category.getItemsList().size()];
							long[] lastEdits = new long[_category.getItemsList().size()];
							long[] ids = new long[_category.getItemsList().size()];
							
							int i = 0;
							for (CategoryItem item : _category.getItemsList()) {
								names[i] = item.getName();
								lastEdits[i] = item.getLastedit();
								ids[i] = item.getId();
								i++;
							}
						
							bundle.putStringArray("names", names);
							bundle.putLongArray("lastEdits", lastEdits);
							bundle.putLongArray("ids", ids);
							bundle.putString("categoryName", getArguments().getString("name"));
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
					_category = new Category();
					Schema<Category> schema = RuntimeSchema.getSchema(Category.class);
					ProtobufIOUtil.mergeFrom(link, (Category)_category, schema);
				} catch (Exception e) {
					setException(e);
				}
				
			}
			
			@Override
			protected void getImageDownload(Bitmap image) {
			}
		};
		
		_download.execute(new URL[] {
			new URL(AsyncDownload.URL_BASE + "api.php?f=b&c=" + getArguments().getString("name") + "&date=" + AndroidUtilities.getInstance().getDate()),
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
