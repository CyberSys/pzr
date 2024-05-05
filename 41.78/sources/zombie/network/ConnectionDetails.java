package zombie.network;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.SteamUtils;
import zombie.debug.DebugLog;
import zombie.erosion.ErosionMain;
import zombie.gameStates.ChooseGameInfo;
import zombie.gameStates.ConnectToServerState;
import zombie.gameStates.MainScreenState;
import zombie.globalObjects.SGlobalObjects;
import zombie.iso.Vector3;
import zombie.world.WorldDictionary;


public class ConnectionDetails {

	public static void write(UdpConnection udpConnection, ServerWorldDatabase.LogonResult logonResult, ByteBuffer byteBuffer) {
		try {
			writeServerDetails(byteBuffer, udpConnection, logonResult);
			writeGameMap(byteBuffer);
			if (SteamUtils.isSteamModeEnabled()) {
				writeWorkshopItems(byteBuffer);
			}

			writeMods(byteBuffer);
			writeStartLocation(byteBuffer);
			writeServerOptions(byteBuffer);
			writeSandboxOptions(byteBuffer);
			writeGameTime(byteBuffer);
			writeErosionMain(byteBuffer);
			writeGlobalObjects(byteBuffer);
			writeResetID(byteBuffer);
			writeBerries(byteBuffer);
			writeWorldDictionary(byteBuffer);
		} catch (Throwable throwable) {
			throw new RuntimeException(throwable);
		}
	}

	public static void parse(ByteBuffer byteBuffer) {
		ConnectionManager.log("receive-packet", "connection-details", (UdpConnection)null);
		Calendar calendar = Calendar.getInstance();
		ConnectToServerState connectToServerState = new ConnectToServerState(byteBuffer);
		connectToServerState.enter();
		MainScreenState.getInstance().setConnectToServerState(connectToServerState);
		DebugLog.General.println("LOGGED INTO : %d millisecond", calendar.getTimeInMillis() - GameClient.startAuth.getTimeInMillis());
	}

	private static void writeServerDetails(ByteBuffer byteBuffer, UdpConnection udpConnection, ServerWorldDatabase.LogonResult logonResult) {
		byteBuffer.put((byte)(udpConnection.isCoopHost ? 1 : 0));
		byteBuffer.putInt(ServerOptions.getInstance().getMaxPlayers());
		if (SteamUtils.isSteamModeEnabled() && CoopSlave.instance != null && !udpConnection.isCoopHost) {
			byteBuffer.put((byte)1);
			byteBuffer.putLong(CoopSlave.instance.hostSteamID);
			GameWindow.WriteString(byteBuffer, GameServer.ServerName);
		} else {
			byteBuffer.put((byte)0);
		}

		int int1 = udpConnection.playerIDs[0] / 4;
		byteBuffer.put((byte)int1);
		GameWindow.WriteString(byteBuffer, logonResult.accessLevel);
	}

	private static void writeGameMap(ByteBuffer byteBuffer) {
		GameWindow.WriteString(byteBuffer, GameServer.GameMap);
	}

	private static void writeWorkshopItems(ByteBuffer byteBuffer) {
		byteBuffer.putShort((short)GameServer.WorkshopItems.size());
		for (int int1 = 0; int1 < GameServer.WorkshopItems.size(); ++int1) {
			byteBuffer.putLong((Long)GameServer.WorkshopItems.get(int1));
			byteBuffer.putLong(GameServer.WorkshopTimeStamps[int1]);
		}
	}

	private static void writeMods(ByteBuffer byteBuffer) {
		ArrayList arrayList = new ArrayList();
		ChooseGameInfo.Mod mod;
		Iterator iterator;
		for (iterator = GameServer.ServerMods.iterator(); iterator.hasNext(); arrayList.add(mod)) {
			String string = (String)iterator.next();
			String string2 = ZomboidFileSystem.instance.getModDir(string);
			if (string2 != null) {
				try {
					mod = ChooseGameInfo.readModInfo(string2);
				} catch (Exception exception) {
					ExceptionLogger.logException(exception);
					mod = new ChooseGameInfo.Mod(string);
					mod.setId(string);
					mod.setName(string);
				}
			} else {
				mod = new ChooseGameInfo.Mod(string);
				mod.setId(string);
				mod.setName(string);
			}
		}

		byteBuffer.putInt(arrayList.size());
		iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			ChooseGameInfo.Mod mod2 = (ChooseGameInfo.Mod)iterator.next();
			GameWindow.WriteString(byteBuffer, mod2.getId());
			GameWindow.WriteString(byteBuffer, mod2.getUrl());
			GameWindow.WriteString(byteBuffer, mod2.getName());
		}
	}

	private static void writeStartLocation(ByteBuffer byteBuffer) {
		Object object = null;
		Vector3 vector3 = ServerMap.instance.getStartLocation((ServerWorldDatabase.LogonResult)object);
		byteBuffer.putInt((int)vector3.x);
		byteBuffer.putInt((int)vector3.y);
		byteBuffer.putInt((int)vector3.z);
	}

	private static void writeServerOptions(ByteBuffer byteBuffer) {
		byteBuffer.putInt(ServerOptions.instance.getPublicOptions().size());
		Iterator iterator = ServerOptions.instance.getPublicOptions().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			GameWindow.WriteString(byteBuffer, string);
			GameWindow.WriteString(byteBuffer, ServerOptions.instance.getOption(string));
		}
	}

	private static void writeSandboxOptions(ByteBuffer byteBuffer) throws IOException {
		SandboxOptions.instance.save(byteBuffer);
	}

	private static void writeGameTime(ByteBuffer byteBuffer) throws IOException {
		GameTime.getInstance().saveToPacket(byteBuffer);
	}

	private static void writeErosionMain(ByteBuffer byteBuffer) {
		ErosionMain.getInstance().getConfig().save(byteBuffer);
	}

	private static void writeGlobalObjects(ByteBuffer byteBuffer) throws IOException {
		SGlobalObjects.saveInitialStateForClient(byteBuffer);
	}

	private static void writeResetID(ByteBuffer byteBuffer) {
		byteBuffer.putInt(GameServer.ResetID);
	}

	private static void writeBerries(ByteBuffer byteBuffer) {
		GameWindow.WriteString(byteBuffer, Core.getInstance().getPoisonousBerry());
		GameWindow.WriteString(byteBuffer, Core.getInstance().getPoisonousMushroom());
	}

	private static void writeWorldDictionary(ByteBuffer byteBuffer) throws IOException {
		WorldDictionary.saveDataForClient(byteBuffer);
	}
}
