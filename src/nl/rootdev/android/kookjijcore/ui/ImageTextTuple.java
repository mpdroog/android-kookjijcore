package nl.rootdev.android.kookjijcore.ui;


public class ImageTextTuple {
	private String text_;
	private String smallText_;
	private int imageResId_;
	private long recipieIndex_;
	
	public ImageTextTuple(int imageResId, String text, String smallText, long recipieIndex) {
		imageResId_ = imageResId;
		text_ = text;
		smallText_ = smallText;
		recipieIndex_ = recipieIndex;
	}
	
	public String getText_() {
		return text_;
	}
	public void setText(String text) {
		text_ = text;
	}

	public int getImageResId() {
		return imageResId_;
	}

	public void setImageResId(int imageResId) {
		imageResId_ = imageResId;
	}

	public String getSmallText() {
		return smallText_;
	}

	public void setSmallText(String smallText) {
		smallText_ = smallText;
	}

	public long getRecipieIndex() {
		return recipieIndex_;
	}

	public void setRecipieIndex(long recipieIndex) {
		recipieIndex_ = recipieIndex;
	}
}
