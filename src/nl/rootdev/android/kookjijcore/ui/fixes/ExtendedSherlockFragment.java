package nl.rootdev.android.kookjijcore.ui.fixes;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragment;

public abstract class ExtendedSherlockFragment extends SherlockFragment {
	protected Bundle getBundle(Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			return savedInstanceState;
		} else {
			return getArguments();
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (getArguments() != null && getArguments().size() > 0) {
			outState.putAll(getArguments());
		}
	}	
}
