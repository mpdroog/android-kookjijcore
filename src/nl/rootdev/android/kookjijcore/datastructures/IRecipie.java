package nl.rootdev.android.kookjijcore.datastructures;

/**
 * Interface abstraction to keep using the Google
 * Protocol Buffer without any dependency including.
 * @author mark
 *
 */
public interface IRecipie {
	public Long getId();
	public String getName();
	public Integer getPreparationTime();
	public String getDescription();
	public Long getLastedit();
	public Integer getRating();
	public String getOrigin();
	public Integer getServes();
	public String getImage();
	
    public String getIngredients();
	public String getComment();
	public String getIntroduction();
}
