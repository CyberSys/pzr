package zombie.iso.areas;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import se.krka.kahlua.j2se.KahluaTableImpl;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.Translator;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.iso.BuildingDef;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.chat.ChatServer;
import zombie.network.packets.SyncSafehousePacket;


public class SafeHouse {
	private int x = 0;
	private int y = 0;
	private int w = 0;
	private int h = 0;
	private static int diffError = 2;
	private String owner = null;
	private ArrayList players = new ArrayList();
	private long lastVisited = 0L;
	private String title = "Safehouse";
	private int playerConnected = 0;
	private int openTimer = 0;
	private final String id;
	public ArrayList playersRespawn = new ArrayList();
	private static final ArrayList safehouseList = new ArrayList();
	private static final ArrayList tempPlayers = new ArrayList();

	public static void init() {
		safehouseList.clear();
	}

	public static SafeHouse addSafeHouse(int int1, int int2, int int3, int int4, String string, boolean boolean1) {
		SafeHouse safeHouse = new SafeHouse(int1, int2, int3, int4, string);
		safeHouse.setOwner(string);
		safeHouse.setLastVisited(Calendar.getInstance().getTimeInMillis());
		safeHouse.addPlayer(string);
		safehouseList.add(safeHouse);
		if (GameServer.bServer) {
			DebugLog.log("safehouse: added " + int1 + "," + int2 + "," + int3 + "," + int4 + " owner=" + string);
		}

		if (GameClient.bClient && !boolean1) {
			GameClient.sendSafehouse(safeHouse, false);
		}

		updateSafehousePlayersConnected();
		if (GameClient.bClient) {
			LuaEventManager.triggerEvent("OnSafehousesChanged");
		}

		return safeHouse;
	}

	public static SafeHouse addSafeHouse(IsoGridSquare square, IsoPlayer player) {
		String string = canBeSafehouse(square, player);
		return string != null && !"".equals(string) ? null : addSafeHouse(square.getBuilding().def.getX() - diffError, square.getBuilding().def.getY() - diffError, square.getBuilding().def.getW() + diffError * 2, square.getBuilding().def.getH() + diffError * 2, player.getUsername(), false);
	}

	public static SafeHouse hasSafehouse(String string) {
		for (int int1 = 0; int1 < safehouseList.size(); ++int1) {
			SafeHouse safeHouse = (SafeHouse)safehouseList.get(int1);
			if (safeHouse.getPlayers().contains(string) || safeHouse.getOwner().equals(string)) {
				return safeHouse;
			}
		}

		return null;
	}

	public static SafeHouse hasSafehouse(IsoPlayer player) {
		return hasSafehouse(player.getUsername());
	}

	public static void updateSafehousePlayersConnected() {
		SafeHouse safeHouse = null;
		label28: for (int int1 = 0; int1 < safehouseList.size(); ++int1) {
			safeHouse = (SafeHouse)safehouseList.get(int1);
			safeHouse.setPlayerConnected(0);
			Iterator iterator = GameClient.IDToPlayerMap.values().iterator();
			while (true) {
				IsoPlayer player;
				do {
					if (!iterator.hasNext()) {
						continue label28;
					}

					player = (IsoPlayer)iterator.next();
				}		 while (!safeHouse.getPlayers().contains(player.getUsername()) && !safeHouse.getOwner().equals(player.getUsername()));

				safeHouse.setPlayerConnected(safeHouse.getPlayerConnected() + 1);
			}
		}
	}

	public static SafeHouse getSafeHouse(IsoGridSquare square) {
		return isSafeHouse(square, (String)null, false);
	}

	public static SafeHouse getSafeHouse(int int1, int int2, int int3, int int4) {
		SafeHouse safeHouse = null;
		for (int int5 = 0; int5 < safehouseList.size(); ++int5) {
			safeHouse = (SafeHouse)safehouseList.get(int5);
			if (int1 == safeHouse.getX() && int3 == safeHouse.getW() && int2 == safeHouse.getY() && int4 == safeHouse.getH()) {
				return safeHouse;
			}
		}

		return null;
	}

	public static SafeHouse isSafeHouse(IsoGridSquare square, String string, boolean boolean1) {
		if (square == null) {
			return null;
		} else {
			if (GameClient.bClient) {
				IsoPlayer player = GameClient.instance.getPlayerFromUsername(string);
				if (player != null && !player.accessLevel.equals("")) {
					return null;
				}
			}

			SafeHouse safeHouse = null;
			boolean boolean2 = false;
			for (int int1 = 0; int1 < safehouseList.size(); ++int1) {
				safeHouse = (SafeHouse)safehouseList.get(int1);
				if (square.getX() >= safeHouse.getX() && square.getX() < safeHouse.getX2() && square.getY() >= safeHouse.getY() && square.getY() < safeHouse.getY2()) {
					boolean2 = true;
					break;
				}
			}

			if (boolean2 && boolean1 && ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue() && (safeHouse.getPlayerConnected() > 0 || safeHouse.getOpenTimer() > 0)) {
				return null;
			} else {
				return !boolean2 || (string == null || safeHouse == null || safeHouse.getPlayers().contains(string) || safeHouse.getOwner().equals(string)) && string != null ? null : safeHouse;
			}
		}
	}

	public static void clearSafehouseList() {
		safehouseList.clear();
	}

	public boolean playerAllowed(IsoPlayer player) {
		return this.players.contains(player.getUsername()) || this.owner.equals(player.getUsername()) || !player.accessLevel.equals("");
	}

	public boolean playerAllowed(String string) {
		return this.players.contains(string) || this.owner.equals(string);
	}

	public void addPlayer(String string) {
		if (!this.players.contains(string)) {
			this.players.add(string);
			updateSafehousePlayersConnected();
		}
	}

	public void removePlayer(String string) {
		if (this.players.contains(string)) {
			this.players.remove(string);
			this.playersRespawn.remove(string);
			if (GameClient.bClient) {
				GameClient.sendSafehouse(this, false);
			}
		}
	}

	public void syncSafehouse() {
		if (GameClient.bClient) {
			GameClient.sendSafehouse(this, false);
		}
	}

	public void removeSafeHouse(IsoPlayer player) {
		this.removeSafeHouse(player, false);
	}

	public void removeSafeHouse(IsoPlayer player, boolean boolean1) {
		if (player == null || player.getUsername().equals(this.getOwner()) || !player.accessLevel.equals("admin") && !player.accessLevel.equals("moderator") || boolean1) {
			if (GameClient.bClient) {
				GameClient.sendSafehouse(this, true);
			}

			if (GameServer.bServer) {
				SyncSafehousePacket syncSafehousePacket = new SyncSafehousePacket();
				syncSafehousePacket.set(this, true);
				GameServer.sendSafehouse(syncSafehousePacket, (UdpConnection)null);
			}

			getSafehouseList().remove(this);
			int int1 = this.x;
			DebugLog.log("safehouse: removed " + int1 + "," + this.y + "," + this.w + "," + this.h + " owner=" + this.getOwner());
			if (GameClient.bClient) {
				LuaEventManager.triggerEvent("OnSafehousesChanged");
			}
		}
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.putInt(this.getX());
		byteBuffer.putInt(this.getY());
		byteBuffer.putInt(this.getW());
		byteBuffer.putInt(this.getH());
		GameWindow.WriteString(byteBuffer, this.getOwner());
		byteBuffer.putInt(this.getPlayers().size());
		Iterator iterator = this.getPlayers().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
		}

		byteBuffer.putLong(this.getLastVisited());
		GameWindow.WriteString(byteBuffer, this.getTitle());
		byteBuffer.putInt(this.playersRespawn.size());
		for (int int1 = 0; int1 < this.playersRespawn.size(); ++int1) {
			GameWindow.WriteString(byteBuffer, (String)this.playersRespawn.get(int1));
		}
	}

	public static SafeHouse load(ByteBuffer byteBuffer, int int1) {
		SafeHouse safeHouse = new SafeHouse(byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt(), byteBuffer.getInt(), GameWindow.ReadString(byteBuffer));
		int int2 = byteBuffer.getInt();
		int int3;
		for (int3 = 0; int3 < int2; ++int3) {
			safeHouse.addPlayer(GameWindow.ReadString(byteBuffer));
		}

		safeHouse.setLastVisited(byteBuffer.getLong());
		if (int1 >= 101) {
			safeHouse.setTitle(GameWindow.ReadString(byteBuffer));
		}

		if (ChatServer.isInited()) {
			ChatServer.getInstance().createSafehouseChat(safeHouse.getId());
		}

		safehouseList.add(safeHouse);
		if (int1 >= 177) {
			int3 = byteBuffer.getInt();
			for (int int4 = 0; int4 < int3; ++int4) {
				safeHouse.playersRespawn.add(GameWindow.ReadString(byteBuffer));
			}
		}

		return safeHouse;
	}

	public static String canBeSafehouse(IsoGridSquare square, IsoPlayer player) {
		if (!GameClient.bClient && !GameServer.bServer) {
			return null;
		} else if (!ServerOptions.instance.PlayerSafehouse.getValue() && !ServerOptions.instance.AdminSafehouse.getValue()) {
			return null;
		} else if (ServerOptions.instance.PlayerSafehouse.getValue() && hasSafehouse(player) != null) {
			return Translator.getText("IGUI_Safehouse_AlreadyHaveSafehouse");
		} else {
			int int1 = ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue();
			if (!ServerOptions.instance.PlayerSafehouse.getValue() && ServerOptions.instance.AdminSafehouse.getValue() && GameClient.bClient) {
				if (!player.accessLevel.equals("admin") && !player.accessLevel.equals("moderator")) {
					return null;
				}

				int1 = 0;
			}

			if (int1 > 0 && player.getHoursSurvived() < (double)(int1 * 24)) {
				return Translator.getText("IGUI_Safehouse_DaysSurvivedToClaim", int1);
			} else {
				KahluaTable kahluaTable;
				if (GameClient.bClient) {
					KahluaTableIterator kahluaTableIterator = GameClient.instance.getServerSpawnRegions().iterator();
					IsoGridSquare square2 = null;
					while (kahluaTableIterator.advance()) {
						KahluaTable kahluaTable2 = (KahluaTable)kahluaTableIterator.getValue();
						KahluaTableIterator kahluaTableIterator2 = ((KahluaTableImpl)kahluaTable2.rawget("points")).iterator();
						while (kahluaTableIterator2.advance()) {
							KahluaTable kahluaTable3 = (KahluaTable)kahluaTableIterator2.getValue();
							KahluaTableIterator kahluaTableIterator3 = kahluaTable3.iterator();
							while (kahluaTableIterator3.advance()) {
								kahluaTable = (KahluaTable)kahluaTableIterator3.getValue();
								Double Double1 = (Double)kahluaTable.rawget("worldX");
								Double Double2 = (Double)kahluaTable.rawget("worldY");
								Double Double3 = (Double)kahluaTable.rawget("posX");
								Double Double4 = (Double)kahluaTable.rawget("posY");
								square2 = IsoWorld.instance.getCell().getGridSquare(Double3 + Double1 * 300.0, Double4 + Double2 * 300.0, 0.0);
								if (square2 != null && square2.getBuilding() != null && square2.getBuilding().getDef() != null) {
									BuildingDef buildingDef = square2.getBuilding().getDef();
									if (square.getX() >= buildingDef.getX() && square.getX() < buildingDef.getX2() && square.getY() >= buildingDef.getY() && square.getY() < buildingDef.getY2()) {
										return Translator.getText("IGUI_Safehouse_IsSpawnPoint");
									}
								}
							}
						}
					}
				}

				boolean boolean1 = true;
				boolean boolean2 = false;
				boolean boolean3 = false;
				boolean boolean4 = false;
				boolean boolean5 = false;
				BuildingDef buildingDef2 = square.getBuilding().getDef();
				if (square.getBuilding().Rooms != null) {
					Iterator iterator = square.getBuilding().Rooms.iterator();
					while (iterator.hasNext()) {
						IsoRoom room = (IsoRoom)iterator.next();
						if (room.getName().equals("kitchen")) {
							boolean3 = true;
						}

						if (room.getName().equals("bedroom") || room.getName().equals("livingroom")) {
							boolean4 = true;
						}

						if (room.getName().equals("bathroom")) {
							boolean5 = true;
						}
					}
				}

				kahluaTable = null;
				for (int int2 = buildingDef2.getX() - diffError; int2 < buildingDef2.getX2() + diffError; ++int2) {
					for (int int3 = buildingDef2.getY() - diffError; int3 < buildingDef2.getY2() + diffError; ++int3) {
						IsoGridSquare square3 = square.getCell().getGridSquare(int2, int3, 0);
						if (square3 != null) {
							for (int int4 = 0; int4 < square3.getMovingObjects().size(); ++int4) {
								IsoMovingObject movingObject = (IsoMovingObject)square3.getMovingObjects().get(int4);
								if (movingObject != player) {
									boolean1 = false;
									break;
								}

								if (!movingObject.getSquare().Is(IsoFlagType.exterior)) {
									boolean2 = true;
								}
							}
						}
					}

					if (!boolean1) {
						break;
					}
				}

				if (boolean1 && boolean2) {
					return !boolean4 ? Translator.getText("IGUI_Safehouse_NotHouse") : "";
				} else {
					return Translator.getText("IGUI_Safehouse_SomeoneInside");
				}
			}
		}
	}

	public void kickOutOfSafehouse(IsoPlayer player) {
		if (player.getAccessLevel().equals("None")) {
			GameClient.sendKickOutOfSafehouse(player);
		}
	}

	public void checkTrespass(IsoPlayer player) {
		if (GameServer.bServer && !ServerOptions.instance.SafehouseAllowTrepass.getValue() && (!ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue() || this.getPlayerConnected() <= 0 && this.getOpenTimer() <= 0) && !this.playerAllowed(player) && player.getVehicle() == null && player.getX() >= (float)this.getX() && player.getY() >= (float)this.getY() && player.getX() <= (float)this.getX2() && player.getY() <= (float)this.getY2()) {
			GameServer.sendTeleport(player, (float)(this.x - 1), (float)(this.y - 1), 0.0F);
		}
	}

	public SafeHouse alreadyHaveSafehouse(String string) {
		return ServerOptions.instance.PlayerSafehouse.getValue() ? hasSafehouse(string) : null;
	}

	public SafeHouse alreadyHaveSafehouse(IsoPlayer player) {
		return ServerOptions.instance.PlayerSafehouse.getValue() ? hasSafehouse(player) : null;
	}

	public static boolean allowSafeHouse(IsoPlayer player) {
		boolean boolean1 = false;
		boolean boolean2 = (GameClient.bClient || GameServer.bServer) && (ServerOptions.instance.PlayerSafehouse.getValue() || ServerOptions.instance.AdminSafehouse.getValue());
		if (boolean2) {
			if (ServerOptions.instance.PlayerSafehouse.getValue()) {
				boolean1 = hasSafehouse(player) == null;
			}

			if (boolean1 && ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue() > 0 && player.getHoursSurvived() / 24.0 < (double)ServerOptions.instance.SafehouseDaySurvivedToClaim.getValue()) {
				boolean1 = false;
			}

			if (ServerOptions.instance.AdminSafehouse.getValue() && GameClient.bClient) {
				boolean1 = player.accessLevel.equals("admin") || player.accessLevel.equals("moderator");
			}
		}

		return boolean1;
	}

	public void updateSafehouse(IsoPlayer player) {
		if (player == null || !this.getPlayers().contains(player.getUsername()) && !this.getOwner().equals(player.getUsername())) {
			if (ServerOptions.instance.SafeHouseRemovalTime.getValue() > 0 && System.currentTimeMillis() - this.getLastVisited() > 3600000L * (long)ServerOptions.instance.SafeHouseRemovalTime.getValue()) {
				boolean boolean1 = false;
				ArrayList arrayList = GameServer.getPlayers(tempPlayers);
				for (int int1 = 0; int1 < arrayList.size(); ++int1) {
					IsoPlayer player2 = (IsoPlayer)arrayList.get(int1);
					if (this.containsLocation(player2.x, player2.y) && (this.getPlayers().contains(player2.getUsername()) || this.getOwner().equals(player2.getUsername()))) {
						boolean1 = true;
						break;
					}
				}

				if (boolean1) {
					this.setLastVisited(System.currentTimeMillis());
					return;
				}

				this.removeSafeHouse(player, true);
			}
		} else {
			this.setLastVisited(System.currentTimeMillis());
		}
	}

	public SafeHouse(int int1, int int2, int int3, int int4, String string) {
		this.x = int1;
		this.y = int2;
		this.w = int3;
		this.h = int4;
		this.players.add(string);
		this.owner = string;
		this.id = int1 + "," + int2 + " at " + Calendar.getInstance().getTimeInMillis();
	}

	public String getId() {
		return this.id;
	}

	public int getX() {
		return this.x;
	}

	public void setX(int int1) {
		this.x = int1;
	}

	public int getY() {
		return this.y;
	}

	public void setY(int int1) {
		this.y = int1;
	}

	public int getW() {
		return this.w;
	}

	public void setW(int int1) {
		this.w = int1;
	}

	public int getH() {
		return this.h;
	}

	public void setH(int int1) {
		this.h = int1;
	}

	public int getX2() {
		return this.x + this.w;
	}

	public int getY2() {
		return this.y + this.h;
	}

	public boolean containsLocation(float float1, float float2) {
		return float1 >= (float)this.getX() && float1 < (float)this.getX2() && float2 >= (float)this.getY() && float2 < (float)this.getY2();
	}

	public ArrayList getPlayers() {
		return this.players;
	}

	public void setPlayers(ArrayList arrayList) {
		this.players = arrayList;
	}

	public static ArrayList getSafehouseList() {
		return safehouseList;
	}

	public String getOwner() {
		return this.owner;
	}

	public void setOwner(String string) {
		this.owner = string;
		if (this.players.contains(string)) {
			this.players.remove(string);
		}
	}

	public boolean isOwner(IsoPlayer player) {
		return this.getOwner().equals(player.getUsername());
	}

	public long getLastVisited() {
		return this.lastVisited;
	}

	public void setLastVisited(long long1) {
		this.lastVisited = long1;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String string) {
		this.title = string;
	}

	public int getPlayerConnected() {
		return this.playerConnected;
	}

	public void setPlayerConnected(int int1) {
		this.playerConnected = int1;
	}

	public int getOpenTimer() {
		return this.openTimer;
	}

	public void setOpenTimer(int int1) {
		this.openTimer = int1;
	}

	public void setRespawnInSafehouse(boolean boolean1, String string) {
		if (boolean1) {
			this.playersRespawn.add(string);
		} else {
			this.playersRespawn.remove(string);
		}

		if (GameClient.bClient) {
			GameClient.sendSafehouse(this, false);
		}
	}

	public boolean isRespawnInSafehouse(String string) {
		return this.playersRespawn.contains(string);
	}

	public static boolean isPlayerAllowedOnSquare(IsoPlayer player, IsoGridSquare square) {
		if (!ServerOptions.instance.SafehouseAllowTrepass.getValue()) {
			SafeHouse safeHouse = isSafeHouse(square, (String)null, false);
			if (safeHouse != null && (!ServerOptions.instance.DisableSafehouseWhenPlayerConnected.getValue() || safeHouse.getPlayerConnected() <= 0 && safeHouse.getOpenTimer() <= 0)) {
				return safeHouse.playerAllowed(player);
			}
		}

		return true;
	}
}
