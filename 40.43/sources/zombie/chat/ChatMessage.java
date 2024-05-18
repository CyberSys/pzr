package zombie.chat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import zombie.core.Color;
import zombie.core.network.ByteBufferWriter;


public class ChatMessage implements Cloneable {
	private ChatBase chat;
	private LocalDateTime datetime;
	private String author;
	private String text;
	private boolean scramble;
	private String customTag;
	private Color textColor;
	private boolean customColor;
	private boolean overHeadSpeech;
	private boolean showInChat;
	private boolean fromDiscord;
	private boolean serverAlert;
	private int radioChannel;
	private boolean local;
	private boolean shouldAttractZombies;
	private boolean serverAuthor;

	public ChatMessage(ChatBase chatBase, String string) {
		this(chatBase, LocalDateTime.now(), string);
	}

	public ChatMessage(ChatBase chatBase, LocalDateTime localDateTime, String string) {
		this.scramble = false;
		this.overHeadSpeech = true;
		this.showInChat = true;
		this.fromDiscord = false;
		this.serverAlert = false;
		this.radioChannel = -1;
		this.local = false;
		this.shouldAttractZombies = false;
		this.serverAuthor = false;
		this.chat = chatBase;
		this.datetime = localDateTime;
		this.text = string;
		this.textColor = chatBase.getColor();
		this.customColor = false;
	}

	public boolean isShouldAttractZombies() {
		return this.shouldAttractZombies;
	}

	public void setShouldAttractZombies(boolean boolean1) {
		this.shouldAttractZombies = boolean1;
	}

	public boolean isLocal() {
		return this.local;
	}

	public void setLocal(boolean boolean1) {
		this.local = boolean1;
	}

	public String getTextWithReplacedParentheses() {
		return this.text != null ? this.text.replaceAll("<", "&lt;").replaceAll(">", "&gt;") : null;
	}

	public void setScrambledText(String string) {
		this.scramble = true;
		this.text = string;
	}

	public int getRadioChannel() {
		return this.radioChannel;
	}

	public void setRadioChannel(int int1) {
		this.radioChannel = int1;
	}

	public boolean isServerAuthor() {
		return this.serverAuthor;
	}

	public void setServerAuthor(boolean boolean1) {
		this.serverAuthor = boolean1;
	}

	public boolean isFromDiscord() {
		return this.fromDiscord;
	}

	public void makeFromDiscord() {
		this.fromDiscord = true;
	}

	public boolean isOverHeadSpeech() {
		return this.overHeadSpeech;
	}

	public void setOverHeadSpeech(boolean boolean1) {
		this.overHeadSpeech = boolean1;
	}

	public boolean isShowInChat() {
		return this.showInChat;
	}

	public void setShowInChat(boolean boolean1) {
		this.showInChat = boolean1;
	}

	public LocalDateTime getDatetime() {
		return this.datetime;
	}

	public String getDatetimeStr() {
		return this.datetime.format(DateTimeFormatter.ofPattern("h:m"));
	}

	public void setDatetime(LocalDateTime localDateTime) {
		this.datetime = localDateTime;
	}

	public boolean isShowAuthor() {
		return this.getChat().isShowAuthor();
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String string) {
		this.author = string;
	}

	public ChatBase getChat() {
		return this.chat;
	}

	public int getChatID() {
		return this.chat.getID();
	}

	public String getText() {
		return this.text;
	}

	public void setText(String string) {
		this.text = string;
	}

	public String getTextWithPrefix() {
		return this.chat.getMessageTextWithPrefix(this);
	}

	public boolean isScramble() {
		return this.scramble;
	}

	public String getCustomTag() {
		return this.customTag;
	}

	public void setCustomTag(String string) {
		this.customTag = string;
	}

	public Color getTextColor() {
		return this.textColor;
	}

	public void setTextColor(Color color) {
		this.customColor = true;
		this.textColor = color;
	}

	public boolean isCustomColor() {
		return this.customColor;
	}

	public void pack(ByteBufferWriter byteBufferWriter) {
		this.chat.packMessage(byteBufferWriter, this);
	}

	public ChatMessage clone() {
		ChatMessage chatMessage;
		try {
			chatMessage = (ChatMessage)super.clone();
		} catch (CloneNotSupportedException cloneNotSupportedException) {
			throw new RuntimeException();
		}

		chatMessage.datetime = this.datetime;
		chatMessage.chat = this.chat;
		chatMessage.author = this.author;
		chatMessage.text = this.text;
		chatMessage.scramble = this.scramble;
		chatMessage.customTag = this.customTag;
		chatMessage.textColor = this.textColor;
		chatMessage.customColor = this.customColor;
		chatMessage.overHeadSpeech = this.overHeadSpeech;
		return chatMessage;
	}

	public boolean isServerAlert() {
		return this.serverAlert;
	}

	public void setServerAlert(boolean boolean1) {
		this.serverAlert = boolean1;
	}

	public String toString() {
		return "ChatMessage{chat=" + this.chat.getTitle() + ", author=\'" + this.author + '\'' + ", text=\'" + this.text + '\'' + '}';
	}
}
