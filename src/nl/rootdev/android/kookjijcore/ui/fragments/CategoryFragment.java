package nl.rootdev.android.kookjijcore.ui.fragments;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nl.rootdev.android.kookjijcore.R;
import nl.rootdev.android.kookjijcore.RecipieActivity;
import nl.rootdev.android.kookjijcore.datastructures.ICategory;
import nl.rootdev.android.kookjijcore.datastructures.pb.Category;
import nl.rootdev.android.kookjijcore.datastructures.pb.CategoryItem;
import nl.rootdev.android.kookjijcore.ui.EndlessScrollListener;
import nl.rootdev.android.kookjijcore.ui.ImageTextTuple;
import nl.rootdev.android.kookjijcore.ui.ListImageTextAdapter;
import nl.rootdev.android.kookjijcore.ui.fixes.ExtendedSherlockFragment;
import nl.rootdev.android.kookjijcore.ui.tasks.AsyncDownload;
import nl.rootdev.android.kookjijcore.utils.AndroidUtilities;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.dyuproject.protostuff.ProtobufIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class CategoryFragment extends ExtendedSherlockFragment implements OnItemClickListener {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		GridView grid = (GridView) getView().findViewById(R.id.gridView1);
		if (grid != null) {
			if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
				grid.setColumnWidth(320);
			} else {
				grid.setColumnWidth(240);
			}
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view;
		
		final Bundle bundle = getBundle(savedInstanceState);
		if (bundle == null) {
			return inflater.inflate(R.layout.category_empty, container, false);
		}

		String[] names = bundle.getStringArray("names");
		if (names != null) {
			GridView grid = (GridView) inflater.inflate(R.layout.grid, container, false);
			final ListImageTextAdapter listItems = new ListImageTextAdapter();
			long[] lastEdits = bundle.getLongArray("lastEdits");
			long[] ids = bundle.getLongArray("ids");
			
			for (int i = 0; i < names.length; i++) {
				listItems.addImageText(new ImageTextTuple(
					0,
					names[i],
					AndroidUtilities.getInstance().getDate(lastEdits[i]),
					ids[i]
				));
				grid.setAdapter(listItems);
				grid.setOnItemClickListener(this);
			}
			
			grid.setOnScrollListener(new EndlessScrollListener(5) {
				@Override
				protected void loadInBackground () {
					// TODO: Async download..
					AsyncTask<URL, String, String> dl = new AsyncTask<URL, String, String>() {
						private ICategory _category;
						private Exception _exception;
						
						@Override
						protected void onPostExecute(String result) {
							// TODO: Needs to get into bundle?
							if (_exception != null) {
								AndroidUtilities.getInstance().makeToast("Fout bij ophalen categorie items", true);
								return;
							}
							if (_category.getItemsList() != null && _category.getItemsList().size() > 0) {
								for (CategoryItem item : _category.getItemsList()) {
									listItems.addImageText(new ImageTextTuple(
										0,
										item.getName(),
										AndroidUtilities.getInstance().getDate(item.getLastedit()),
										item.getId()
									));
									listItems.notifyDataSetChanged();
								}
								AndroidUtilities.getInstance().makeToast(_category.getItemsList().size() + " recepten opgehaald", true);								
							} else {
								AndroidUtilities.getInstance().makeToast("Geen categorie-items meer", true);
							}
						}
						
						@Override
						protected String doInBackground(URL... params) {
							try {
								InputStream link = AndroidUtilities.getInstance().encapsulateGZipOnNeed(params[0].openStream());
								_category = new Category();
								Schema<Category> schema = RuntimeSchema.getSchema(Category.class);
								ProtobufIOUtil.mergeFrom(link, (Category)_category, schema);
								link.close();
							} catch (Exception e) {
								_exception = e;
							}
							return "";
						}
					};
					
					try {
						AndroidUtilities.getInstance().makeToast("Ophalen van meer recepten...", true);
						bundle.putInt("page", bundle.getInt("page")+1);
						if (bundle.getString("query") != null) {
							dl.execute(new URL[] {
								new URL(AsyncDownload.URL_BASE + "api.php?f=s&i=" + bundle.getString("query") + "&k=" + bundle.getInt("page") + "&date=" + AndroidUtilities.getInstance().getDate()),
							});							
						} else {
							dl.execute(new URL[] {
								new URL(AsyncDownload.URL_BASE + "api.php?f=b&c=" + bundle.getString("categoryName") + "&n=" + bundle.getInt("page") + "&date=" + AndroidUtilities.getInstance().getDate()),
							});
						}
					} catch (MalformedURLException e) {
						// Ignore not happening
					}
				}
			});
			view = grid;
		} else {
			view = inflater.inflate(R.layout.category_empty, container, false);
		}
		
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ImageTextTuple item = (ImageTextTuple) parent.getItemAtPosition(position);
		Intent i = new Intent(getSherlockActivity().getApplicationContext(), RecipieActivity.class);
		Bundle bundle = new Bundle();
		bundle.putLong("index", item.getRecipieIndex());
		i.putExtras(bundle);
		startActivity(i);
	}	
}
