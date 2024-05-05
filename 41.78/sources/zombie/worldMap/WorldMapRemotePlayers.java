package zombie.worldMap;

import java.util.ArrayList;
import java.util.HashMap;
import zombie.characters.IsoPlayer;


public final class WorldMapRemotePlayers {
	public static final WorldMapRemotePlayers instance = new WorldMapRemotePlayers();
	private final ArrayList playerList = new ArrayList();
	private final HashMap playerLookup = new HashMap();

	public WorldMapRemotePlayer getOrCreatePlayerByID(short short1) {
		WorldMapRemotePlayer worldMapRemotePlayer = (WorldMapRemotePlayer)this.playerLookup.get(short1);
		if (worldMapRemotePlayer == null) {
			worldMapRemotePlayer = new WorldMapRemotePlayer(short1);
			this.playerList.add(worldMapRemotePlayer);
			this.playerLookup.put(short1, worldMapRemotePlayer);
		}

		return worldMapRemotePlayer;
	}

	public WorldMapRemotePlayer getOrCreatePlayer(IsoPlayer player) {
		return this.getOrCreatePlayerByID(player.OnlineID);
	}

	public WorldMapRemotePlayer getPlayerByID(short short1) {
		return (WorldMapRemotePlayer)this.playerLookup.get(short1);
	}

	public ArrayList getPlayers() {
		return this.playerList;
	}

	public void removePlayerByID(short short1) {
		this.playerList.removeIf((short1x)->{
			return short1x.getOnlineID() == short1;
		});
		this.playerLookup.remove(short1);
	}

	public void Reset() {
		this.playerList.clear();
		this.playerLookup.clear();
	}
}
