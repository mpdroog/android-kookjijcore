package nl.rootdev.android.kookjijcore.ui.fragments;

import nl.rootdev.android.kookjijcore.R;
import nl.rootdev.android.kookjijcore.ui.fixes.ExtendedSherlockFragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ColumnFragment extends ExtendedSherlockFragment {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Bundle bundle = getBundle(savedInstanceState);
		final TextView text = (TextView) getView().findViewById(R.id.column_message);
		final TextView title = (TextView) getView().findViewById(R.id.column_title);

		title.setText(bundle.getString("name"));
		text.setText(Html.fromHtml(bundle.getString("text")));		
	}
			
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.column, container, false);
	}		
}
