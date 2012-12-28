package nl.rootdev.android.kookjijcore.ui.frames;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nl.rootdev.android.kookjijcore.datastructures.IRecipie;
import nl.rootdev.android.kookjijcore.datastructures.pb.Recipie;
import nl.rootdev.android.kookjijcore.ui.fragments.RecipieFragment;
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
 * It simply tries to download a Recipie.
 * - During downloading show the LoadingFragment
 * - In case of an 'Error' open up the ErrorFragment
 * - In case of success open the HomeFragment
 * 
 * @author mark
 */
public class RecipieFrame extends AbstractLoadingFrame  {	
	protected void startAsyncDownload(Bundle savedInstanceState) throws MalformedURLException {
		_download = new AsyncDownload(getSherlockActivity().getCacheDir().toString()) {
			private IRecipie _recipie;
			private Bitmap _image;
			
			@Override
			protected void onPostExecute(String result) {
				if(getException() == null) {
					stopAbstractLoadingFrame();
					final RecipieFragment home = new RecipieFragment();
					Bundle bundle = new Bundle();
					{
						/** Warning, not ALL arguments are passed!! */
						bundle.putLong("id", _recipie.getId());
						bundle.putString("name", _recipie.getName());
						bundle.putInt("preparationTime", _recipie.getPreparationTime());
						bundle.putString("description", _recipie.getDescription());
						bundle.putInt("rating", _recipie.getRating());
						bundle.putInt("serves", _recipie.getServes());
						bundle.putString("ingredients", _recipie.getIngredients());
						bundle.putString("comment", _recipie.getComment());
						bundle.putString("introduction", _recipie.getIntroduction());
						
					}
					home.setArguments(bundle);
					if (_image != null) {
						home.setImage(_image);
					}
					
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
					_recipie = new Recipie();
					Schema<Recipie> schema = RuntimeSchema.getSchema(Recipie.class);
					ProtobufIOUtil.mergeFrom(link, (Recipie)_recipie, schema);

				} catch (Exception e) {
					setException(e);
				}
				
			}
			
			@Override
			protected void getImageDownload(Bitmap image) {
				_image = image;
			}
		};
		
		long index = 0;
		if (getArguments() != null) {
			index = getArguments().getLong("index");
		}
		// TODO: Remove this crap
		if (index == 0L) {
			// No index, get recipie of the day
			_download.execute(new URL[] {
				new URL(AsyncDownload.URL_BASE + "api.php?f=h&date=" + AndroidUtilities.getInstance().getDate()),
				new URL(AsyncDownload.URL_BASE + "api.php?f=p&i=h&date=" + AndroidUtilities.getInstance().getDate())
			});
		} else {
			_download.execute(new URL[] {
				new URL(AsyncDownload.URL_BASE + "api.php?f=h&i=" + index + "&date=" + AndroidUtilities.getInstance().getDate()),
				new URL(AsyncDownload.URL_BASE + "api.php?f=p&i=" + index + "&date=" + AndroidUtilities.getInstance().getDate())
			});
		}
	}

	@Override
	protected int getFragmentId() {
		return 1;
	}
}
