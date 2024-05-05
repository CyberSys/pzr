package zombie.core.znet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;


public class ZNet {
	private static final SimpleDateFormat s_logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");

	public static native void init();

	private static native void setLogLevel(int int1);

	public static void SetLogLevel(int int1) {
		LogSeverity logSeverity;
		switch (int1) {
		case 0: 
			logSeverity = LogSeverity.Warning;
			break;
		
		case 1: 
			logSeverity = LogSeverity.General;
			break;
		
		case 2: 
			logSeverity = LogSeverity.Debug;
			break;
		
		default: 
			logSeverity = LogSeverity.Error;
		
		}
		DebugLog.enableLog(DebugType.Network, logSeverity);
	}

	public static void SetLogLevel(LogSeverity logSeverity) {
		setLogLevel(logSeverity.ordinal());
	}

	private static void logPutsCallback(String string) {
		String string2 = s_logSdf.format(Calendar.getInstance().getTime());
		DebugLog.Network.print("[" + string2 + "] > " + string);
		System.out.flush();
	}
}
