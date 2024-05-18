package zombie.core.znet;


public class GameServerDetails {
	public String address;
	public int port;
	public long steamId;
	public String name;
	public String gamedir;
	public String map;
	public String gameDescription;
	public String tags;
	public int ping;
	public int numPlayers;
	public int maxPlayers;
	public boolean passwordProtected;
	public int version;

	public GameServerDetails() {
	}

	public GameServerDetails(String string, int int1, long long1, String string2, String string3, String string4, String string5, String string6, int int2, int int3, int int4, boolean boolean1, int int5) {
		this.address = string;
		this.port = int1;
		this.steamId = long1;
		this.name = string2;
		this.gamedir = string3;
		this.map = string4;
		this.gameDescription = string5;
		this.tags = string6;
		this.ping = int2;
		this.numPlayers = int3;
		this.maxPlayers = int4;
		this.passwordProtected = boolean1;
		this.version = int5;
	}
}
