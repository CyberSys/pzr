package zombie.core.znet;

import zombie.core.textures.Texture;


public class SteamFriend {
	private String name = "";
	private long steamID;
	private String steamIDString;

	public SteamFriend() {
	}

	public SteamFriend(String string, long long1) {
		this.steamID = long1;
		this.steamIDString = SteamUtils.convertSteamIDToString(long1);
		this.name = string;
	}

	public String getName() {
		return this.name;
	}

	public String getSteamID() {
		return this.steamIDString;
	}

	public Texture getAvatar() {
		return Texture.getSteamAvatar(this.steamID);
	}

	public String getState() {
		int int1 = SteamFriends.GetFriendPersonaState(this.steamID);
		switch (int1) {
		case 0: 
			return "Offline";
		
		case 1: 
			return "Online";
		
		case 2: 
			return "Busy";
		
		case 3: 
			return "Away";
		
		case 4: 
			return "Snooze";
		
		case 5: 
			return "LookingToTrade";
		
		case 6: 
			return "LookingToPlay";
		
		default: 
			return "Unknown";
		
		}
	}
}
