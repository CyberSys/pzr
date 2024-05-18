package zombie.network;


public class Server {
	private String name = "My Server";
	private String ip = "127.0.0.1";
	private String localIP = "";
	private String port = "16262";
	private String serverpwd = "";
	private String description = "";
	private String userName = "";
	private String pwd = "";
	private int lastUpdate = 0;
	private String players = null;
	private String maxPlayers = null;
	private boolean open = false;
	private boolean bPublic = true;
	private String version = null;
	private String mods = null;
	private boolean passwordProtected;
	private String steamId = null;
	private String ping = null;
	private boolean hosted = false;

	public String getPort() {
		return this.port;
	}

	public void setPort(String string) {
		this.port = string;
	}

	public String getIp() {
		return this.ip;
	}

	public void setIp(String string) {
		this.ip = string;
	}

	public String getLocalIP() {
		return this.localIP;
	}

	public void setLocalIP(String string) {
		this.localIP = string;
	}

	public String getServerPassword() {
		return this.serverpwd;
	}

	public void setServerPassword(String string) {
		this.serverpwd = string == null ? "" : string;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String string) {
		this.description = string;
	}

	public String getUserName() {
		return this.userName;
	}

	public void setUserName(String string) {
		this.userName = string;
	}

	public String getPwd() {
		return this.pwd;
	}

	public void setPwd(String string) {
		this.pwd = string;
	}

	public int getLastUpdate() {
		return this.lastUpdate;
	}

	public void setLastUpdate(int int1) {
		this.lastUpdate = int1;
	}

	public String getPlayers() {
		return this.players;
	}

	public void setPlayers(String string) {
		this.players = string;
	}

	public boolean isOpen() {
		return this.open;
	}

	public void setOpen(boolean boolean1) {
		this.open = boolean1;
	}

	public boolean isPublic() {
		return this.bPublic;
	}

	public void setPublic(boolean boolean1) {
		this.bPublic = boolean1;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String string) {
		this.version = string;
	}

	public String getMaxPlayers() {
		return this.maxPlayers;
	}

	public void setMaxPlayers(String string) {
		this.maxPlayers = string;
	}

	public String getMods() {
		return this.mods;
	}

	public void setMods(String string) {
		this.mods = string;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getPing() {
		return this.ping;
	}

	public void setPing(String string) {
		this.ping = string;
	}

	public boolean isPasswordProtected() {
		return this.passwordProtected;
	}

	public void setPasswordProtected(boolean boolean1) {
		this.passwordProtected = boolean1;
	}

	public String getSteamId() {
		return this.steamId;
	}

	public void setSteamId(String string) {
		this.steamId = string;
	}

	public boolean isHosted() {
		return this.hosted;
	}

	public void setHosted(boolean boolean1) {
		this.hosted = boolean1;
	}
}
