package zombie.worldMap;

import zombie.characters.IsoPlayer;
import zombie.network.GameClient;
import zombie.network.ServerOptions;


public final class WorldMapRemotePlayer {
	private short changeCount = 0;
	private final short OnlineID;
	private String username = "???";
	private String forename = "???";
	private String surname = "???";
	private String accessLevel = "None";
	private float x;
	private float y;
	private boolean invisible = false;
	private boolean bHasFullData = false;

	public WorldMapRemotePlayer(short short1) {
		this.OnlineID = short1;
	}

	public void setPlayer(IsoPlayer player) {
		boolean boolean1 = false;
		if (!this.username.equals(player.username)) {
			this.username = player.username;
			boolean1 = true;
		}

		if (!this.forename.equals(player.getDescriptor().getForename())) {
			this.forename = player.getDescriptor().getForename();
			boolean1 = true;
		}

		if (!this.surname.equals(player.getDescriptor().getSurname())) {
			this.surname = player.getDescriptor().getSurname();
			boolean1 = true;
		}

		if (!this.accessLevel.equals(player.accessLevel)) {
			this.accessLevel = player.accessLevel;
			boolean1 = true;
		}

		this.x = player.x;
		this.y = player.y;
		if (this.invisible != player.isInvisible()) {
			this.invisible = player.isInvisible();
			boolean1 = true;
		}

		if (boolean1) {
			++this.changeCount;
		}
	}

	public void setFullData(short short1, String string, String string2, String string3, String string4, float float1, float float2, boolean boolean1) {
		this.changeCount = short1;
		this.username = string;
		this.forename = string2;
		this.surname = string3;
		this.accessLevel = string4;
		this.x = float1;
		this.y = float2;
		this.invisible = boolean1;
		this.bHasFullData = true;
	}

	public void setPosition(float float1, float float2) {
		this.x = float1;
		this.y = float2;
	}

	public short getOnlineID() {
		return this.OnlineID;
	}

	public String getForename() {
		return this.forename;
	}

	public String getSurname() {
		return this.surname;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public short getChangeCount() {
		return this.changeCount;
	}

	public boolean isInvisible() {
		return this.invisible;
	}

	public boolean hasFullData() {
		return this.bHasFullData;
	}

	public String getUsername(Boolean Boolean1) {
		String string = this.username;
		if (Boolean1 && GameClient.bClient && ServerOptions.instance.ShowFirstAndLastName.getValue() && this.isAccessLevel("None")) {
			string = this.forename + " " + this.surname;
			if (ServerOptions.instance.DisplayUserName.getValue()) {
				string = string + " (" + this.username + ")";
			}
		}

		return string;
	}

	public String getUsername() {
		return this.getUsername(false);
	}

	public String getAccessLevel() {
		String string = this.accessLevel;
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -2004703995: 
			if (string.equals("moderator")) {
				byte1 = 1;
			}

			break;
		
		case 3302: 
			if (string.equals("gm")) {
				byte1 = 3;
			}

			break;
		
		case 92668751: 
			if (string.equals("admin")) {
				byte1 = 0;
			}

			break;
		
		case 348607190: 
			if (string.equals("observer")) {
				byte1 = 4;
			}

			break;
		
		case 530022739: 
			if (string.equals("overseer")) {
				byte1 = 2;
			}

		
		}
		String string2;
		switch (byte1) {
		case 0: 
			string2 = "Admin";
			break;
		
		case 1: 
			string2 = "Moderator";
			break;
		
		case 2: 
			string2 = "Overseer";
			break;
		
		case 3: 
			string2 = "GM";
			break;
		
		case 4: 
			string2 = "Observer";
			break;
		
		default: 
			string2 = "None";
		
		}
		return string2;
	}

	public boolean isAccessLevel(String string) {
		return this.getAccessLevel().equalsIgnoreCase(string);
	}
}
