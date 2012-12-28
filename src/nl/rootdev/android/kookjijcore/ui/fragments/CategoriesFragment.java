package nl.rootdev.android.kookjijcore.ui.fragments;

import nl.rootdev.android.kookjijcore.CategoryMain;
import nl.rootdev.android.kookjijcore.R;
import nl.rootdev.android.kookjijcore.ui.fixes.ExtendedSherlockFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class CategoriesFragment extends ExtendedSherlockFragment implements OnItemClickListener {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.categories, container, false);
		ListView list = (ListView) view.findViewById(R.id.list);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(container.getContext(), android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.categories));
		list.setAdapter(adapter);
		list.setOnItemClickListener(this);
		return view;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String category = (String) parent.getItemAtPosition(position);
		Intent intent = new Intent(getSherlockActivity(), CategoryMain.class);
		Bundle bundle = new Bundle();
		bundle.putString("title", category);
		bundle.putInt("categoryIndex", getResources().getIdentifier("category" + position, "array", getSherlockActivity().getPackageName()));
		intent.putExtras(bundle);
		getSherlockActivity().startActivity(intent);
	}
}
