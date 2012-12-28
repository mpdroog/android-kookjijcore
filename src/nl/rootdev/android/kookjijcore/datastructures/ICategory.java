package nl.rootdev.android.kookjijcore.datastructures;

import java.util.List;

import nl.rootdev.android.kookjijcore.datastructures.pb.CategoryItem;

public interface ICategory {
	public List<CategoryItem> getItemsList();
    public Integer getSetCurpage();
    public Integer getSetResultpages();	
}
