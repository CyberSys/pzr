package zombie.chat;

import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.core.Color;
import zombie.core.network.ByteBufferWriter;
import zombie.ui.UIFont;


public class ChatSettings {
	private boolean unique;
	private Color fontColor;
	private UIFont font;
	private ChatSettings.FontSize fontSize;
	private boolean bold;
	private boolean allowImages;
	private boolean allowChatIcons;
	private boolean allowColors;
	private boolean allowFonts;
	private boolean allowBBcode;
	private boolean equalizeLineHeights;
	private boolean showAuthor;
	private boolean showTimestamp;
	private boolean showChatTitle;
	private boolean useOnlyActiveTab;
	private float range;
	private float zombieAttractionRange;
	public static final float infinityRange = -1.0F;

	public ChatSettings() {
		this.unique = true;
		this.fontColor = Color.white;
		this.font = UIFont.Dialogue;
		this.bold = true;
		this.showAuthor = true;
		this.showTimestamp = true;
		this.showChatTitle = true;
		this.range = -1.0F;
		this.zombieAttractionRange = -1.0F;
		this.useOnlyActiveTab = false;
		this.fontSize = ChatSettings.FontSize.Medium;
	}

	public ChatSettings(ByteBuffer byteBuffer) {
		this.unique = byteBuffer.get() == 1;
		this.fontColor = new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
		this.font = UIFont.FromString(GameWindow.ReadString(byteBuffer));
		this.bold = byteBuffer.get() == 1;
		this.allowImages = byteBuffer.get() == 1;
		this.allowChatIcons = byteBuffer.get() == 1;
		this.allowColors = byteBuffer.get() == 1;
		this.allowFonts = byteBuffer.get() == 1;
		this.allowBBcode = byteBuffer.get() == 1;
		this.equalizeLineHeights = byteBuffer.get() == 1;
		this.showAuthor = byteBuffer.get() == 1;
		this.showTimestamp = byteBuffer.get() == 1;
		this.showChatTitle = byteBuffer.get() == 1;
		this.range = byteBuffer.getFloat();
		if (byteBuffer.get() == 1) {
			this.zombieAttractionRange = byteBuffer.getFloat();
		} else {
			this.zombieAttractionRange = this.range;
		}

		this.fontSize = ChatSettings.FontSize.Medium;
	}

	public boolean isUnique() {
		return this.unique;
	}

	public void setUnique(boolean boolean1) {
		this.unique = boolean1;
	}

	public Color getFontColor() {
		return this.fontColor;
	}

	public void setFontColor(Color color) {
		this.fontColor = color;
	}

	public void setFontColor(float float1, float float2, float float3, float float4) {
		this.fontColor = new Color(float1, float2, float3, float4);
	}

	public UIFont getFont() {
		return this.font;
	}

	public void setFont(UIFont uIFont) {
		this.font = uIFont;
	}

	public String getFontSize() {
		return this.fontSize.toString().toLowerCase();
	}

	public void setFontSize(String string) {
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1994163307: 
			if (string.equals("Medium")) {
				byte1 = 3;
			}

			break;
		
		case -1078030475: 
			if (string.equals("medium")) {
				byte1 = 2;
			}

			break;
		
		case 73190171: 
			if (string.equals("Large")) {
				byte1 = 5;
			}

			break;
		
		case 79996135: 
			if (string.equals("Small")) {
				byte1 = 1;
			}

			break;
		
		case 102742843: 
			if (string.equals("large")) {
				byte1 = 4;
			}

			break;
		
		case 109548807: 
			if (string.equals("small")) {
				byte1 = 0;
			}

		
		}
		switch (byte1) {
		case 0: 
		
		case 1: 
			this.fontSize = ChatSettings.FontSize.Small;
			break;
		
		case 2: 
		
		case 3: 
			this.fontSize = ChatSettings.FontSize.Medium;
			break;
		
		case 4: 
		
		case 5: 
			this.fontSize = ChatSettings.FontSize.Large;
			break;
		
		default: 
			this.fontSize = ChatSettings.FontSize.NotDefine;
		
		}
	}

	public boolean isBold() {
		return this.bold;
	}

	public void setBold(boolean boolean1) {
		this.bold = boolean1;
	}

	public boolean isShowAuthor() {
		return this.showAuthor;
	}

	public void setShowAuthor(boolean boolean1) {
		this.showAuthor = boolean1;
	}

	public boolean isShowTimestamp() {
		return this.showTimestamp;
	}

	public void setShowTimestamp(boolean boolean1) {
		this.showTimestamp = boolean1;
	}

	public boolean isShowChatTitle() {
		return this.showChatTitle;
	}

	public void setShowChatTitle(boolean boolean1) {
		this.showChatTitle = boolean1;
	}

	public boolean isAllowImages() {
		return this.allowImages;
	}

	public void setAllowImages(boolean boolean1) {
		this.allowImages = boolean1;
	}

	public boolean isAllowChatIcons() {
		return this.allowChatIcons;
	}

	public void setAllowChatIcons(boolean boolean1) {
		this.allowChatIcons = boolean1;
	}

	public boolean isAllowColors() {
		return this.allowColors;
	}

	public void setAllowColors(boolean boolean1) {
		this.allowColors = boolean1;
	}

	public boolean isAllowFonts() {
		return this.allowFonts;
	}

	public void setAllowFonts(boolean boolean1) {
		this.allowFonts = boolean1;
	}

	public boolean isAllowBBcode() {
		return this.allowBBcode;
	}

	public void setAllowBBcode(boolean boolean1) {
		this.allowBBcode = boolean1;
	}

	public boolean isEqualizeLineHeights() {
		return this.equalizeLineHeights;
	}

	public void setEqualizeLineHeights(boolean boolean1) {
		this.equalizeLineHeights = boolean1;
	}

	public float getRange() {
		return this.range;
	}

	public void setRange(float float1) {
		this.range = float1;
	}

	public float getZombieAttractionRange() {
		return this.zombieAttractionRange == -1.0F ? this.range : this.zombieAttractionRange;
	}

	public void setZombieAttractionRange(float float1) {
		this.zombieAttractionRange = float1;
	}

	public boolean isUseOnlyActiveTab() {
		return this.useOnlyActiveTab;
	}

	public void setUseOnlyActiveTab(boolean boolean1) {
		this.useOnlyActiveTab = boolean1;
	}

	public void pack(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putBoolean(this.unique);
		byteBufferWriter.putFloat(this.fontColor.r);
		byteBufferWriter.putFloat(this.fontColor.g);
		byteBufferWriter.putFloat(this.fontColor.b);
		byteBufferWriter.putFloat(this.fontColor.a);
		byteBufferWriter.putUTF(this.font.toString());
		byteBufferWriter.putBoolean(this.bold);
		byteBufferWriter.putBoolean(this.allowImages);
		byteBufferWriter.putBoolean(this.allowChatIcons);
		byteBufferWriter.putBoolean(this.allowColors);
		byteBufferWriter.putBoolean(this.allowFonts);
		byteBufferWriter.putBoolean(this.allowBBcode);
		byteBufferWriter.putBoolean(this.equalizeLineHeights);
		byteBufferWriter.putBoolean(this.showAuthor);
		byteBufferWriter.putBoolean(this.showTimestamp);
		byteBufferWriter.putBoolean(this.showChatTitle);
		byteBufferWriter.putFloat(this.range);
		byteBufferWriter.putBoolean(this.range != this.zombieAttractionRange);
		if (this.range != this.zombieAttractionRange) {
			byteBufferWriter.putFloat(this.zombieAttractionRange);
		}
	}
	public static enum FontSize {

		NotDefine,
		Small,
		Medium,
		Large;
	}
}
