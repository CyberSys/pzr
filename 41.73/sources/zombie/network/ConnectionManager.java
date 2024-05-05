package zombie.network;

import zombie.core.logger.LoggerManager;
import zombie.core.raknet.UdpConnection;
import zombie.core.znet.ZNetSessionState;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;


public class ConnectionManager {

	public static void log(String string, UdpConnection udpConnection) {
		if (GameServer.bServer) {
			try {
				LoggerManager.getLogger("connection").write(string + ": " + LoginQueue.getDescription() + " " + GameServer.udpEngine.getDescription() + " " + GameServer.getDescription());
				if (udpConnection != null) {
					LoggerManager.getLogger("connection").write(string + ": " + udpConnection.getDescription());
					LoggerManager.getLogger("connection").write(string + ": " + udpConnection.getPlayerDescription());
					ZNetSessionState zNetSessionState = udpConnection.getP2PSessionState();
					if (zNetSessionState != null) {
						LoggerManager.getLogger("connection").write(string + ": " + zNetSessionState.getDescription());
					}
				}
			} catch (Exception exception) {
				DebugLog.Multiplayer.printException(exception, "ConnectionManager.log", LogSeverity.Error);
			}
		} else if (GameClient.bClient) {
			DebugLog.General.warn(string);
		}
	}
}
