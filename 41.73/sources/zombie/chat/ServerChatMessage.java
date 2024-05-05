package zombie.chat;


public class ServerChatMessage extends ChatMessage {

	public ServerChatMessage(ChatBase chatBase, String string) {
		super(chatBase, string);
		super.setAuthor("Server");
		this.setServerAuthor(true);
	}

	public String getAuthor() {
		return super.getAuthor();
	}

	public void setAuthor(String string) {
		throw new UnsupportedOperationException();
	}
}
