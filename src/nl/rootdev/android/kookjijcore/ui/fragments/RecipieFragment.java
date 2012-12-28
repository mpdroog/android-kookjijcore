package nl.rootdev.android.kookjijcore.ui.fragments;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import nl.rootdev.android.kookjijcore.R;
import nl.rootdev.android.kookjijcore.ui.fixes.ExtendedSherlockFragment;
import nl.rootdev.android.kookjijcore.ui.tasks.AsyncDownload;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment for showing a Recipie.
 * 
 * @author mark
 */
public class RecipieFragment extends ExtendedSherlockFragment implements OnClickListener {
	/** Downloaded image from the webserver */
	private Bitmap _image;
	private boolean _calledSetImage;
	private Bundle _savedInstanceState;
	
	public void setImage(Bitmap image) {
		_image = image;
		_calledSetImage = true;		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}
	
    protected Dialog openPopin() {
    	final ImageView image = new ImageView(getSherlockActivity());
    	image.setImageResource(R.drawable.logo);
    	
        // We have only one dialog.
    	final AlertDialog dialog = new AlertDialog.Builder(getSherlockActivity())
	        .setTitle(getString(R.string.loading_image))
	        .setView(image)
	        .setPositiveButton(R.string.dialoghide, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                dialog.dismiss();
	            }
	        })
	        .create();
    	dialog.show();
    	
    	dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});

        new AsyncTask<Void, Void, Void>() {
        	private Bitmap _image;
        	
        	@Override
        	protected void onPostExecute(Void result) {
        		super.onPostExecute(result);
        		if (_image == null) {
	        		dialog.setTitle(R.string.dialogerror);        			
        		} else {
	        		dialog.setTitle(R.string.dialogsuccess);
	        		image.setImageBitmap(_image);
        		}
        	}
        	
        	@Override
        	protected Void doInBackground(Void... params) {
				URLConnection img;
				try {
					img = new URL(AsyncDownload.URL_BASE + "api.php?f=d&i=" + _savedInstanceState.getLong("id")).openConnection();
					_image = BitmapFactory.decodeStream(img.getInputStream());

				} catch (Exception e) {
					// Ignore
				}
        		return null;
        	}
        }.execute();
        
        return null;
    }
	
	/**
	 * Fill the view with actual data.
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Bundle bundle = getBundle(savedInstanceState);
		_savedInstanceState = bundle;

		final TextView introduction = (TextView) getView().findViewById(R.id.introduction);
		final TextView description = (TextView) getView().findViewById(R.id.description);
		final TextView title = (TextView) getView().findViewById(R.id.title);
		final ImageView preview = (ImageView) getView().findViewById(R.id.preview);
		final TextView titleMeta = (TextView) getView().findViewById(R.id.title_meta);
		final RatingBar rating = (RatingBar) getView().findViewById(R.id.ratingBar1);
		
		rating.setRating( ((float)bundle.getInt("rating")) / 100);
		title.setText(bundle.getString("name"));
		introduction.setText(Html.fromHtml(bundle.getString("ingredients")), TextView.BufferType.SPANNABLE);
		description.setText(Html.fromHtml(bundle.getString("description")), TextView.BufferType.SPANNABLE);
		titleMeta.setText(Html.fromHtml(
			"Personen: " + bundle.getInt("serves") +
			"<br />Bereidingstijd: " + bundle.getInt("preparationTime") + "minuten"
		), TextView.BufferType.SPANNABLE);
		
		if (bundle.getString("comment") != null) {
			final TextView commentTitle = (TextView) getView().findViewById(R.id.comment_title);
			final TextView comment = (TextView) getView().findViewById(R.id.comment);
			commentTitle.setVisibility(View.VISIBLE);
			comment.setText(Html.fromHtml(bundle.getString("comment")), TextView.BufferType.SPANNABLE);
		}
		
		if (_image == null && _calledSetImage) {
			// No image available
			preview.setImageResource(R.drawable.app_icon);
		} else if (_image != null) {
			// Set loaded image
			preview.setImageBitmap(_image);
			preview.setTag("");
		}
		else {
			// Set 'click to load' image
			preview.setImageResource(R.drawable.logo_gray);
		}
		preview.setOnClickListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.recipie, container, false);
	}

	/**
	 * When on a slow internet connection the image is
	 * never download automatically, so added onclick-listener
	 * here to download it if the user want's.
	 */
	@Override
	public void onClick(View v) {
		final ImageView preview = (ImageView) getView().findViewById(R.id.preview);		
		if (preview.getTag() instanceof String) {
			openPopin();
			return;
		}

		if (_image != null || _calledSetImage) {
			// No need to 'download image' if already loaded or 'no available'
	    	Toast.makeText(getSherlockActivity(), R.string.dialog_nopicture, Toast.LENGTH_SHORT).show();			
			return;
		}
				
		AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
			private Bitmap _bitmap;

			@Override
			protected void onPostExecute(Void result) {
				_calledSetImage = true;				
				if(_bitmap != null) {
					try {
						preview.setTag(new URL(AsyncDownload.URL_BASE + "api.php?f=d&i=" + _savedInstanceState.getLong("id")));
					} catch (MalformedURLException e) {
						// Ignore malformed URL's won't happen (A)
					}
					preview.setImageBitmap(_bitmap);
					preview.setTag("loaded");
				} else {
					// Set no image picture
					preview.setImageResource(R.drawable.app_icon);
				}
			}
			
			@Override
			protected Void doInBackground(Void... params) {
				URLConnection img;
				try {
					img = new URL(AsyncDownload.URL_BASE + "api.php?f=p&i=" + _savedInstanceState.getLong("id")).openConnection();
					_bitmap = BitmapFactory.decodeStream(img.getInputStream());

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return params[0];
			}			
		};
		task.execute(new Void[] {null});
	}	
}
