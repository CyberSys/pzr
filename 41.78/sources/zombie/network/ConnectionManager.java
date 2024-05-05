package zombie.network;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;


public class ConnectionManager {
	private static final SimpleDateFormat s_logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");

	public static void log(String string, String string2, UdpConnection udpConnection) {
		DebugLog.Network.println("[%s] > ConnectionManager: [%s] \"%s\" connection: %s", s_logSdf.format(Calendar.getInstance().getTime()), string, string2, GameClient.bClient ? GameClient.connection : udpConnection);
	}
}
