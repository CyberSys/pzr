package zombie.chat;



public enum ChatMode {

	ServerMultiPlayer,
	ClientMultiPlayer,
	SinglePlayer;

	private static ChatMode[] $values() {
		return new ChatMode[]{ServerMultiPlayer, ClientMultiPlayer, SinglePlayer};
	}
}
