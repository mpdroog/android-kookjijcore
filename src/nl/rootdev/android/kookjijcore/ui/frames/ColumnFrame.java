package nl.rootdev.android.kookjijcore.ui.frames;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nl.rootdev.android.kookjijcore.datastructures.IColumn;
import nl.rootdev.android.kookjijcore.datastructures.pb.Column;
import nl.rootdev.android.kookjijcore.ui.fragments.ColumnFragment;
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
 * It simply tries to download a Column.
 * - During downloading show the LoadingFragment
 * - In case of an 'Error' open up the ErrorFragment
 * - In case of success open the HomeFragment
 * 
 * @author mark
 */
public class ColumnFrame extends AbstractLoadingFrame  {	

	@Override
	protected void startAsyncDownload(Bundle savedInstanceState)
			throws MalformedURLException {
		_download = new AsyncDownload(getSherlockActivity().getCacheDir().toString()) {
			private IColumn _column;
			
			@Override
			protected void onPostExecute(String result) {
				if(getException() == null) {
					stopAbstractLoadingFrame();
					final ColumnFragment home = new ColumnFragment();
					
					Bundle bundle = new Bundle();
					{
						/** Warning, not ALL arguments are passed!! */
						bundle.putString("name", _column.getName());
						bundle.putString("text", _column.getText());
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
					_column = new Column();
					Schema<Column> schema = RuntimeSchema.getSchema(Column.class);
					ProtobufIOUtil.mergeFrom(link, (Column)_column, schema);
				} catch (Exception e) {
					setException(e);
				}
				
			}
			
			@Override
			protected void getImageDownload(Bitmap image) {
			}
		};
		
		_download.execute(new URL[] {
			new URL(AsyncDownload.URL_BASE + "api.php?f=a&date=" + AndroidUtilities.getInstance().getDate())
		});		
	}
	@Override
	protected int getFragmentId() {
		return 2;
	}	
}
