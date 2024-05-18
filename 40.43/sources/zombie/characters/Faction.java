package zombie.characters;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import zombie.GameWindow;
import zombie.chat.ChatSettings;
import zombie.chat.defaultChats.FactionChat;
import zombie.core.Rand;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.ColorInfo;
import zombie.network.GameClient;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatServer;


public class Faction {
	private String name;
	private String owner;
	private String tag;
	private ColorInfo tagColor;
	private ArrayList players;
	public static ArrayList factions = new ArrayList();

	public Faction() {
		this.players = new ArrayList();
	}

	public Faction(String string, String string2) {
		this.setName(string);
		this.setOwner(string2);
		this.players = new ArrayList();
		this.tagColor = new ColorInfo(Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), 1.0F);
	}

	public static Faction createFaction(String string, String string2) {
		if (!factionExist(string)) {
			Faction faction = new Faction(string, string2);
			factions.add(faction);
			if (GameClient.bClient) {
				GameClient.sendFaction(faction, false);
			}

			return faction;
		} else {
			return null;
		}
	}

	public static ArrayList getFactions() {
		return factions;
	}

	public static boolean canCreateFaction(IsoPlayer player) {
		boolean boolean1 = ServerOptions.instance.Faction.getValue();
		if (boolean1 && ServerOptions.instance.FactionDaySurvivedToCreate.getValue() > 0 && player.getHoursSurvived() / 24.0 < (double)ServerOptions.instance.FactionDaySurvivedToCreate.getValue()) {
			boolean1 = false;
		}

		return boolean1;
	}

	public boolean canCreateTag() {
		return this.players.size() + 1 >= ServerOptions.instance.FactionPlayersRequiredForTag.getValue();
	}

	public static boolean isAlreadyInFaction(String string) {
		for (int int1 = 0; int1 < factions.size(); ++int1) {
			Faction faction = (Faction)factions.get(int1);
			if (faction.getOwner().equals(string)) {
				return true;
			}

			for (int int2 = 0; int2 < faction.getPlayers().size(); ++int2) {
				if (((String)faction.getPlayers().get(int2)).equals(string)) {
					return true;
				}
			}
		}

		return false;
	}

	public static boolean isAlreadyInFaction(IsoPlayer player) {
		return isAlreadyInFaction(player.getUsername());
	}

	public void removePlayer(String string) {
		this.getPlayers().remove(string);
		if (GameClient.bClient) {
			GameClient.sendFaction(this, false);
		}
	}

	public static boolean factionExist(String string) {
		for (int int1 = 0; int1 < factions.size(); ++int1) {
			if (((Faction)factions.get(int1)).getName().equals(string)) {
				return true;
			}
		}

		return false;
	}

	public static boolean tagExist(String string) {
		for (int int1 = 0; int1 < factions.size(); ++int1) {
			if (((Faction)factions.get(int1)).getTag() != null && ((Faction)factions.get(int1)).getTag().equals(string)) {
				return true;
			}
		}

		return false;
	}

	public static Faction getPlayerFaction(IsoPlayer player) {
		for (int int1 = 0; int1 < factions.size(); ++int1) {
			Faction faction = (Faction)factions.get(int1);
			if (faction.getOwner().equals(player.getUsername())) {
				return faction;
			}

			for (int int2 = 0; int2 < faction.getPlayers().size(); ++int2) {
				if (((String)faction.getPlayers().get(int2)).equals(player.getUsername())) {
					return faction;
				}
			}
		}

		return null;
	}

	public static Faction getPlayerFaction(String string) {
		for (int int1 = 0; int1 < factions.size(); ++int1) {
			Faction faction = (Faction)factions.get(int1);
			if (faction.getOwner().equals(string)) {
				return faction;
			}

			for (int int2 = 0; int2 < faction.getPlayers().size(); ++int2) {
				if (((String)faction.getPlayers().get(int2)).equals(string)) {
					return faction;
				}
			}
		}

		return null;
	}

	public static Faction getFaction(String string) {
		for (int int1 = 0; int1 < factions.size(); ++int1) {
			if (((Faction)factions.get(int1)).getName().equals(string)) {
				return (Faction)factions.get(int1);
			}
		}

		return null;
	}

	public void removeFaction() {
		getFactions().remove(this);
		if (GameClient.bClient) {
			GameClient.sendFaction(this, true);
		}
	}

	public void syncFaction() {
		if (GameClient.bClient) {
			GameClient.sendFaction(this, false);
		}
	}

	public boolean isOwner(String string) {
		return this.getOwner().equals(string);
	}

	public boolean isPlayerMember(IsoPlayer player) {
		return this.isMember(player.getUsername());
	}

	public boolean isMember(String string) {
		for (int int1 = 0; int1 < this.getPlayers().size(); ++int1) {
			if (((String)this.getPlayers().get(int1)).equals(string)) {
				return true;
			}
		}

		return false;
	}

	public void writeToBuffer(ByteBufferWriter byteBufferWriter, boolean boolean1) {
		byteBufferWriter.putUTF(this.getName());
		byteBufferWriter.putUTF(this.getOwner());
		byteBufferWriter.putInt(this.getPlayers().size());
		if (this.getTag() != null) {
			byteBufferWriter.putByte((byte)1);
			byteBufferWriter.putUTF(this.getTag());
			byteBufferWriter.putFloat(this.getTagColor().r);
			byteBufferWriter.putFloat(this.getTagColor().g);
			byteBufferWriter.putFloat(this.getTagColor().b);
		} else {
			byteBufferWriter.putByte((byte)0);
		}

		Iterator iterator = this.getPlayers().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			byteBufferWriter.putUTF(string);
		}

		byteBufferWriter.putBoolean(boolean1);
	}

	public void save(ByteBuffer byteBuffer) {
		GameWindow.WriteString(byteBuffer, this.getName());
		GameWindow.WriteString(byteBuffer, this.getOwner());
		byteBuffer.putInt(this.getPlayers().size());
		if (this.getTag() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getTag());
			byteBuffer.putFloat(this.getTagColor().r);
			byteBuffer.putFloat(this.getTagColor().g);
			byteBuffer.putFloat(this.getTagColor().b);
		} else {
			byteBuffer.put((byte)0);
		}

		Iterator iterator = this.getPlayers().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) {
		this.setName(GameWindow.ReadString(byteBuffer));
		this.setOwner(GameWindow.ReadString(byteBuffer));
		int int2 = byteBuffer.getInt();
		if (byteBuffer.get() == 1) {
			this.setTag(GameWindow.ReadString(byteBuffer));
			this.setTagColor(new ColorInfo(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat(), 1.0F));
		} else {
			this.setTagColor(new ColorInfo(Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), Rand.Next(0.3F, 1.0F), 1.0F));
		}

		for (int int3 = 0; int3 < int2; ++int3) {
			this.getPlayers().add(GameWindow.ReadString(byteBuffer));
		}

		if (ChatServer.isInited()) {
			FactionChat factionChat = ChatServer.getInstance().createFactionChat(this.getName());
			ChatSettings chatSettings = FactionChat.getDefaultSettings();
			chatSettings.setFontColor(this.tagColor.r, this.tagColor.g, this.tagColor.b, this.tagColor.a);
			factionChat.setSettings(chatSettings);
		}
	}

	public void addPlayer(String string) {
		for (int int1 = 0; int1 < factions.size(); ++int1) {
			Faction faction = (Faction)factions.get(int1);
			if (faction.getOwner().equals(string)) {
				return;
			}

			for (int int2 = 0; int2 < faction.getPlayers().size(); ++int2) {
				if (((String)faction.getPlayers().get(int2)).equals(string)) {
					return;
				}
			}
		}

		this.players.add(string);
		if (GameClient.bClient) {
			GameClient.sendFaction(this, false);
		}
	}

	public ArrayList getPlayers() {
		return this.players;
	}

	public ColorInfo getTagColor() {
		return this.tagColor;
	}

	public void setTagColor(ColorInfo colorInfo) {
		if (colorInfo.r < 0.19F) {
			colorInfo.r = 0.19F;
		}

		if (colorInfo.g < 0.19F) {
			colorInfo.g = 0.19F;
		}

		if (colorInfo.b < 0.19F) {
			colorInfo.b = 0.19F;
		}

		this.tagColor = colorInfo;
	}

	public String getTag() {
		return this.tag;
	}

	public void setTag(String string) {
		this.tag = string;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String string) {
		this.name = string;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String string) {
		if (this.owner == null) {
			this.owner = string;
		} else {
			if (!this.isMember(this.owner)) {
				this.getPlayers().add(this.owner);
				this.getPlayers().remove(string);
			}

			this.owner = string;
		}
	}
}
