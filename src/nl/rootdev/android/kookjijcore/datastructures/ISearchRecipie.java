package nl.rootdev.android.kookjijcore.datastructures;

import java.util.List;

import nl.rootdev.android.kookjijcore.datastructures.pb.Recipie;

public interface ISearchRecipie {
    public Integer getSetResultpages();
    public List<Recipie> getRecipiesList();
}
