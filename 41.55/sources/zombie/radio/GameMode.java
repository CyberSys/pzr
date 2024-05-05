package zombie.radio;



public enum GameMode {

	SinglePlayer,
	Server,
	Client;

	private static GameMode[] $values() {
		return new GameMode[]{SinglePlayer, Server, Client};
	}
}
