package zombie.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import zombie.core.Core;
import zombie.core.ThreadGroups;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.network.GameServer;
import zombie.network.ServerOptions;


public final class PublicServerUtil {
	public static String webSite = "https://www.projectzomboid.com/server_browser/";
	private static long timestampForUpdate = 0L;
	private static long timestampForPlayerUpdate = 0L;
	private static long updateTick = 600000L;
	private static long updatePlayerTick = 300000L;
	private static int sentPlayerCount = 0;
	private static boolean isEnabled = false;

	public static void init() {
		isEnabled = false;
		if (DebugOptions.instance.Network.PublicServerUtil.Enabled.getValue()) {
			try {
				if (GameServer.bServer) {
					ServerOptions.instance.changeOption("PublicName", checkHacking(ServerOptions.instance.getOption("PublicName")));
					ServerOptions.instance.changeOption("PublicDescription", checkHacking(ServerOptions.instance.getOption("PublicDescription")));
				}

				if (GameServer.bServer && !isPublic()) {
					return;
				}

				DebugLog.log("connecting to public server list");
				URL url = new URL(webSite + "serverVar.php");
				URLConnection urlConnection = url.openConnection();
				urlConnection.setConnectTimeout(3000);
				urlConnection.connect();
				InputStreamReader inputStreamReader = new InputStreamReader(urlConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String string = null;
				StringBuilder stringBuilder = new StringBuilder();
				while ((string = bufferedReader.readLine()) != null) {
					stringBuilder.append(string).append('\n');
				}

				bufferedReader.close();
				String[] stringArray = stringBuilder.toString().split("<br>");
				String[] stringArray2 = stringArray;
				int int1 = stringArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					String string2 = stringArray2[int2];
					if (string2.contains("allowed") && string2.contains("true")) {
						isEnabled = true;
					}

					if (string2.contains("updateTick")) {
						updateTick = Long.parseLong(string2.split("=")[1].trim());
					}

					if (string2.contains("updatePlayerTick")) {
						updatePlayerTick = Long.parseLong(string2.split("=")[1].trim());
					}

					if (string2.contains("ip")) {
						GameServer.ip = string2.split("=")[1].trim();
					}
				}
			} catch (SocketTimeoutException socketTimeoutException) {
				isEnabled = false;
				DebugLog.log("timeout trying to connect to public server list");
			} catch (Exception exception) {
				isEnabled = false;
				exception.printStackTrace();
			}
		}
	}

	private static String checkHacking(String string) {
		return string == null ? "" : string.replaceAll("--", "").replaceAll("->", "").replaceAll("(?i)select union", "").replaceAll("(?i)select join", "").replaceAll("1=1", "").replaceAll("(?i)delete from", "");
	}

	public static void insertOrUpdate() {
		if (isEnabled) {
			if (isPublic()) {
				try {
					insertDatas();
				} catch (Exception exception) {
					System.out.println("Can\'t reach PZ.com");
				}
			}
		}
	}

	private static boolean isPublic() {
		String string = checkHacking(ServerOptions.instance.PublicName.getValue());
		return ServerOptions.instance.Public.getValue() && !string.isEmpty();
	}

	public static void update() {
		if (System.currentTimeMillis() - timestampForUpdate > updateTick) {
			timestampForUpdate = System.currentTimeMillis();
			init();
			if (!isEnabled) {
				return;
			}

			if (isPublic()) {
				try {
					insertDatas();
				} catch (Exception exception) {
					System.out.println("Can\'t reach PZ.com");
				}
			}
		}
	}

	private static void insertDatas() throws Exception {
		if (isEnabled) {
			String string = "";
			String string2;
			if (!ServerOptions.instance.PublicDescription.getValue().isEmpty()) {
				string2 = ServerOptions.instance.PublicDescription.getValue();
				string = "&desc=" + string2.replaceAll(" ", "%20");
			}

			String string3 = "";
			String string4;
			for (Iterator iterator = GameServer.ServerMods.iterator(); iterator.hasNext(); string3 = string3 + string4 + ",") {
				string4 = (String)iterator.next();
			}

			if (!"".equals(string3)) {
				string3 = string3.substring(0, string3.length() - 1);
				string3 = "&mods=" + string3.replaceAll(" ", "%20");
			}

			String string5 = GameServer.ip;
			if (!ServerOptions.instance.server_browser_announced_ip.getValue().isEmpty()) {
				string5 = ServerOptions.instance.server_browser_announced_ip.getValue();
			}

			timestampForUpdate = System.currentTimeMillis();
			int int1 = GameServer.getPlayerCount();
			string2 = webSite;
			callUrl(string2 + "write.php?name=" + ServerOptions.instance.PublicName.getValue().replaceAll(" ", "%20") + string + "&port=" + ServerOptions.instance.DefaultPort.getValue() + "&players=" + int1 + "&ip=" + string5 + "&open=" + (ServerOptions.instance.Open.getValue() ? "1" : "0") + "&password=" + ("".equals(ServerOptions.instance.Password.getValue()) ? "0" : "1") + "&maxPlayers=" + ServerOptions.instance.MaxPlayers.getValue() + "&version=" + Core.getInstance().getVersionNumber().replaceAll(" ", "%20") + string3 + "&mac=" + getMacAddress());
			sentPlayerCount = int1;
		}
	}

	public static void updatePlayers() {
		if (System.currentTimeMillis() - timestampForPlayerUpdate > updatePlayerTick) {
			timestampForPlayerUpdate = System.currentTimeMillis();
			if (!isEnabled) {
				return;
			}

			try {
				String string = GameServer.ip;
				if (!ServerOptions.instance.server_browser_announced_ip.getValue().isEmpty()) {
					string = ServerOptions.instance.server_browser_announced_ip.getValue();
				}

				int int1 = GameServer.getPlayerCount();
				String string2 = webSite;
				callUrl(string2 + "updatePlayers.php?port=" + ServerOptions.instance.DefaultPort.getValue() + "&players=" + int1 + "&ip=" + string);
				sentPlayerCount = GameServer.getPlayerCount();
			} catch (Exception exception) {
				System.out.println("Can\'t reach PZ.com");
			}
		}
	}

	public static void updatePlayerCountIfChanged() {
		if (isEnabled && sentPlayerCount != GameServer.getPlayerCount()) {
			updatePlayers();
		}
	}

	public static boolean isEnabled() {
		return isEnabled;
	}

	private static String getMacAddress() {
		try {
			InetAddress inetAddress = InetAddress.getLocalHost();
			NetworkInterface networkInterface = NetworkInterface.getByInetAddress(inetAddress);
			if (networkInterface != null) {
				byte[] byteArray = networkInterface.getHardwareAddress();
				StringBuilder stringBuilder = new StringBuilder();
				for (int int1 = 0; int1 < byteArray.length; ++int1) {
					stringBuilder.append(String.format("%02X%s", byteArray[int1], int1 < byteArray.length - 1 ? "-" : ""));
				}

				return stringBuilder.toString();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		return "";
	}

	private static void callUrl(String string) {
		(new Thread(ThreadGroups.Workers, Lambda.invoker(string, (stringx)->{
			try {
				URL url = new URL(stringx);
				URLConnection urlConnection = url.openConnection();
				urlConnection.getInputStream();
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		}), "openUrl")).start();
	}
}
