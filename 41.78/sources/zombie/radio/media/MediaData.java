package zombie.radio.media;

import java.util.ArrayList;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Translator;
import zombie.debug.DebugLog;


public final class MediaData {
	private final String id;
	private final String itemDisplayName;
	private String title;
	private String subtitle;
	private String author;
	private String extra;
	private short index;
	private String category;
	private final int spawning;
	private final ArrayList lines = new ArrayList();

	public MediaData(String string, String string2, int int1) {
		this.itemDisplayName = string2;
		this.id = string;
		this.spawning = int1;
		if (Core.bDebug) {
			if (string2 == null) {
				throw new RuntimeException("ItemDisplayName may not be null.");
			}

			if (string == null) {
				throw new RuntimeException("Id may not be null.");
			}
		}
	}

	public void addLine(String string, float float1, float float2, float float3, String string2) {
		MediaData.MediaLineData mediaLineData = new MediaData.MediaLineData(string, float1, float2, float3, string2);
		this.lines.add(mediaLineData);
	}

	public int getLineCount() {
		return this.lines.size();
	}

	public String getTranslatedItemDisplayName() {
		return Translator.getText(this.itemDisplayName);
	}

	public boolean hasTitle() {
		return this.title != null;
	}

	public void setTitle(String string) {
		this.title = string;
	}

	public String getTitleEN() {
		return this.title != null ? Translator.getTextMediaEN(this.title) : null;
	}

	public String getTranslatedTitle() {
		return this.title != null ? Translator.getText(this.title) : null;
	}

	public boolean hasSubTitle() {
		return this.subtitle != null;
	}

	public void setSubtitle(String string) {
		this.subtitle = string;
	}

	public String getSubtitleEN() {
		return this.subtitle != null ? Translator.getTextMediaEN(this.subtitle) : null;
	}

	public String getTranslatedSubTitle() {
		return this.subtitle != null ? Translator.getText(this.subtitle) : null;
	}

	public boolean hasAuthor() {
		return this.author != null;
	}

	public void setAuthor(String string) {
		this.author = string;
	}

	public String getAuthorEN() {
		return this.author != null ? Translator.getTextMediaEN(this.author) : null;
	}

	public String getTranslatedAuthor() {
		return this.author != null ? Translator.getText(this.author) : null;
	}

	public boolean hasExtra() {
		return this.extra != null;
	}

	public void setExtra(String string) {
		this.extra = string;
	}

	public String getExtraEN() {
		return this.extra != null ? Translator.getTextMediaEN(this.extra) : null;
	}

	public String getTranslatedExtra() {
		return this.extra != null ? Translator.getText(this.extra) : null;
	}

	public String getId() {
		return this.id;
	}

	public short getIndex() {
		return this.index;
	}

	protected void setIndex(short short1) {
		this.index = short1;
	}

	public String getCategory() {
		return this.category;
	}

	protected void setCategory(String string) {
		this.category = string;
	}

	public int getSpawning() {
		return this.spawning;
	}

	public byte getMediaType() {
		if (this.category == null) {
			String string = this.itemDisplayName != null ? this.itemDisplayName : "unknown";
			DebugLog.log("Warning MediaData has no category set, mediadata = " + string);
		}

		return RecordedMedia.getMediaTypeForCategory(this.category);
	}

	public MediaData.MediaLineData getLine(int int1) {
		return int1 >= 0 && int1 < this.lines.size() ? (MediaData.MediaLineData)this.lines.get(int1) : null;
	}

	public static final class MediaLineData {
		private final String text;
		private final Color color;
		private final String codes;

		public MediaLineData(String string, float float1, float float2, float float3, String string2) {
			this.text = string;
			this.codes = string2;
			if (float1 == 0.0F && float2 == 0.0F && float3 == 0.0F) {
				float1 = 1.0F;
				float2 = 1.0F;
				float3 = 1.0F;
			}

			this.color = new Color(float1, float2, float3);
		}

		public String getTranslatedText() {
			return Translator.getText(this.text);
		}

		public Color getColor() {
			return this.color;
		}

		public float getR() {
			return this.color.r;
		}

		public float getG() {
			return this.color.g;
		}

		public float getB() {
			return this.color.b;
		}

		public String getCodes() {
			return this.codes;
		}

		public String getTextGuid() {
			return this.text;
		}
	}
}
