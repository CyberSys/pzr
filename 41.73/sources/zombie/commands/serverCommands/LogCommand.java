package zombie.commands.serverCommands;

import java.util.ArrayList;
import zombie.commands.CommandArgs;
import zombie.commands.CommandBase;
import zombie.commands.CommandHelp;
import zombie.commands.CommandName;
import zombie.commands.RequiredRight;
import zombie.core.Translator;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.debug.LogSeverity;


@CommandName(name = "log")
@CommandArgs(required = {"(.+)", "(.+)"})
@CommandHelp(helpText = "UI_ServerOptionDesc_SetLogLevel")
@RequiredRight(requiredRights = 32)
public class LogCommand extends CommandBase {

	public LogCommand(String string, String string2, String string3, UdpConnection udpConnection) {
		super(string, string2, string3, udpConnection);
	}

	public static DebugType getDebugType(String string) {
		ArrayList arrayList = new ArrayList();
		DebugType[] debugTypeArray = DebugType.values();
		int int1 = debugTypeArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			DebugType debugType = debugTypeArray[int2];
			if (debugType.name().toLowerCase().startsWith(string.toLowerCase())) {
				arrayList.add(debugType);
			}
		}

		return arrayList.size() == 1 ? (DebugType)arrayList.get(0) : null;
	}

	public static LogSeverity getLogSeverity(String string) {
		ArrayList arrayList = new ArrayList();
		LogSeverity[] logSeverityArray = LogSeverity.values();
		int int1 = logSeverityArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			LogSeverity logSeverity = logSeverityArray[int2];
			if (logSeverity.name().toLowerCase().startsWith(string.toLowerCase())) {
				arrayList.add(logSeverity);
			}
		}

		return arrayList.size() == 1 ? (LogSeverity)arrayList.get(0) : null;
	}

	protected String Command() {
		DebugType debugType = getDebugType(this.getCommandArg(0));
		LogSeverity logSeverity = getLogSeverity(this.getCommandArg(1));
		if (debugType != null && logSeverity != null) {
			DebugLog.enableLog(debugType, logSeverity);
			return String.format("Server \"%s\" log level is \"%s\"", debugType.name().toLowerCase(), logSeverity.name().toLowerCase());
		} else {
			return Translator.getText("UI_ServerOptionDesc_SetLogLevel", debugType == null ? "\"type\"" : debugType.name().toLowerCase(), logSeverity == null ? "\"severity\"" : logSeverity.name().toLowerCase());
		}
	}
}
