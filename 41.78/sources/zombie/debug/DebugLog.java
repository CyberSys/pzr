package zombie.debug;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import zombie.GameTime;
import zombie.ZomboidFileSystem;
import zombie.config.BooleanConfigOption;
import zombie.config.ConfigFile;
import zombie.config.ConfigOption;
import zombie.core.Core;
import zombie.core.logger.LoggerManager;
import zombie.core.logger.ZLogger;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.ui.UIDebugConsole;
import zombie.util.StringUtils;


public final class DebugLog {
	private static final boolean[] m_enabledDebugTypes = new boolean[DebugType.values().length];
	private static final HashMap logLevels = new HashMap();
	private static boolean s_initialized = false;
	public static boolean printServerTime = false;
	private static final DebugLog.OutputStreamWrapper s_stdout;
	private static final DebugLog.OutputStreamWrapper s_stderr;
	private static final PrintStream m_originalOut;
	private static final PrintStream m_originalErr;
	private static final PrintStream GeneralErr;
	private static ZLogger s_logFileLogger;
	public static final DebugLogStream ActionSystem;
	public static final DebugLogStream Animation;
	public static final DebugLogStream Asset;
	public static final DebugLogStream Clothing;
	public static final DebugLogStream Combat;
	public static final DebugLogStream Damage;
	public static final DebugLogStream Death;
	public static final DebugLogStream FileIO;
	public static final DebugLogStream Fireplace;
	public static final DebugLogStream General;
	public static final DebugLogStream Input;
	public static final DebugLogStream IsoRegion;
	public static final DebugLogStream Lua;
	public static final DebugLogStream MapLoading;
	public static final DebugLogStream Mod;
	public static final DebugLogStream Multiplayer;
	public static final DebugLogStream Network;
	public static final DebugLogStream NetworkFileDebug;
	public static final DebugLogStream Packet;
	public static final DebugLogStream Objects;
	public static final DebugLogStream Radio;
	public static final DebugLogStream Recipe;
	public static final DebugLogStream Script;
	public static final DebugLogStream Shader;
	public static final DebugLogStream Sound;
	public static final DebugLogStream Statistic;
	public static final DebugLogStream UnitTests;
	public static final DebugLogStream Vehicle;
	public static final DebugLogStream Voice;
	public static final DebugLogStream Zombie;
	public static final int VERSION = 1;

	private static DebugLogStream createDebugLogStream(DebugType debugType) {
		return new DebugLogStream(m_originalOut, m_originalOut, m_originalErr, new GenericDebugLogFormatter(debugType));
	}

	public static void enableLog(DebugType debugType, LogSeverity logSeverity) {
		setLogEnabled(debugType, true);
		logLevels.put(debugType, logSeverity);
	}

	public static LogSeverity getLogLevel(DebugType debugType) {
		return (LogSeverity)logLevels.get(debugType);
	}

	public static boolean isLogEnabled(DebugType debugType, LogSeverity logSeverity) {
		return logSeverity.ordinal() >= ((LogSeverity)logLevels.getOrDefault(debugType, LogSeverity.Warning)).ordinal();
	}

	public static boolean isLogEnabled(LogSeverity logSeverity, DebugType debugType) {
		return logSeverity.ordinal() >= LogSeverity.Warning.ordinal() || isEnabled(debugType);
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, "%s", string2) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3, Object object4) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3, object4) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3, Object object4, Object object5) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3, object4, object5) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3, Object object4, Object object5, Object object6) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3, object4, object5, object6) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3, object4, object5, object6, object7) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3, object4, object5, object6, object7, object8) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3, object4, object5, object6, object7, object8, object9) : null;
	}

	public static String formatString(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9, Object object10) {
		return isLogEnabled(logSeverity, debugType) ? formatStringVarArgs(debugType, logSeverity, string, object, string2, object2, object3, object4, object5, object6, object7, object8, object9, object10) : null;
	}

	public static String formatStringVarArgs(DebugType debugType, LogSeverity logSeverity, String string, Object object, String string2, Object[] objectArray) {
		if (!isLogEnabled(logSeverity, debugType)) {
			return null;
		} else {
			String string3 = String.valueOf(System.currentTimeMillis());
			if (GameServer.bServer || GameClient.bClient || printServerTime) {
				string3 = string3 + "> " + NumberFormat.getNumberInstance().format(TimeUnit.NANOSECONDS.toMillis(GameTime.getServerTime()));
			}

			String string4 = string + StringUtils.leftJustify(debugType.toString(), 12) + ", " + string3 + "> " + object + String.format(string2, objectArray);
			echoToLogFile(string4);
			return string4;
		}
	}

	private static void echoToLogFile(String string) {
		if (s_logFileLogger == null) {
			if (s_initialized) {
				return;
			}

			s_logFileLogger = new ZLogger(GameServer.bServer ? "DebugLog-server" : "DebugLog", false);
		}

		try {
			s_logFileLogger.writeUnsafe(string, (String)null, false);
		} catch (Exception exception) {
			m_originalErr.println("Exception thrown writing to log file.");
			m_originalErr.println(exception);
			exception.printStackTrace(m_originalErr);
		}
	}

	public static boolean isEnabled(DebugType debugType) {
		return m_enabledDebugTypes[debugType.ordinal()];
	}

	public static void log(DebugType debugType, String string) {
		String string2 = formatString(debugType, LogSeverity.General, "LOG  : ", "", "%s", string);
		if (string2 != null) {
			m_originalOut.println(string2);
		}
	}

	public static void setLogEnabled(DebugType debugType, boolean boolean1) {
		m_enabledDebugTypes[debugType.ordinal()] = boolean1;
	}

	public static void log(Object object) {
		log(DebugType.General, String.valueOf(object));
	}

	public static void log(String string) {
		log(DebugType.General, string);
	}

	public static ArrayList getDebugTypes() {
		ArrayList arrayList = new ArrayList(Arrays.asList(DebugType.values()));
		arrayList.sort((arrayListx,var1)->{
			return String.CASE_INSENSITIVE_ORDER.compare(arrayListx.name(), var1.name());
		});
		return arrayList;
	}

	public static void save() {
		ArrayList arrayList = new ArrayList();
		DebugType[] debugTypeArray = DebugType.values();
		int int1 = debugTypeArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			DebugType debugType = debugTypeArray[int2];
			BooleanConfigOption booleanConfigOption = new BooleanConfigOption(debugType.name(), false);
			booleanConfigOption.setValue(isEnabled(debugType));
			arrayList.add(booleanConfigOption);
		}

		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "debuglog.ini";
		ConfigFile configFile = new ConfigFile();
		configFile.write(string2, 1, arrayList);
	}

	public static void load() {
		String string = ZomboidFileSystem.instance.getCacheDir();
		String string2 = string + File.separator + "debuglog.ini";
		ConfigFile configFile = new ConfigFile();
		if (configFile.read(string2)) {
			for (int int1 = 0; int1 < configFile.getOptions().size(); ++int1) {
				ConfigOption configOption = (ConfigOption)configFile.getOptions().get(int1);
				try {
					setLogEnabled(DebugType.valueOf(configOption.getName()), StringUtils.tryParseBoolean(configOption.getValueAsString()));
				} catch (Exception exception) {
				}
			}
		}
	}

	public static void setStdOut(OutputStream outputStream) {
		s_stdout.setStream(outputStream);
	}

	public static void setStdErr(OutputStream outputStream) {
		s_stderr.setStream(outputStream);
	}

	public static void init() {
		if (!s_initialized) {
			s_initialized = true;
			setStdOut(System.out);
			setStdErr(System.err);
			System.setOut(General);
			System.setErr(GeneralErr);
			if (!GameServer.bServer) {
				load();
			}

			s_logFileLogger = LoggerManager.getLogger(GameServer.bServer ? "DebugLog-server" : "DebugLog");
		}
	}

	static  {
		s_stdout = new DebugLog.OutputStreamWrapper(System.out);
		s_stderr = new DebugLog.OutputStreamWrapper(System.err);
		m_originalOut = new PrintStream(s_stdout, true);
		m_originalErr = new PrintStream(s_stderr, true);
		GeneralErr = new DebugLogStream(m_originalErr, m_originalErr, m_originalErr, new GeneralErrorDebugLogFormatter());
		ActionSystem = createDebugLogStream(DebugType.ActionSystem);
		Animation = createDebugLogStream(DebugType.Animation);
		Asset = createDebugLogStream(DebugType.Asset);
		Clothing = createDebugLogStream(DebugType.Clothing);
		Combat = createDebugLogStream(DebugType.Combat);
		Damage = createDebugLogStream(DebugType.Damage);
		Death = createDebugLogStream(DebugType.Death);
		FileIO = createDebugLogStream(DebugType.FileIO);
		Fireplace = createDebugLogStream(DebugType.Fireplace);
		General = createDebugLogStream(DebugType.General);
		Input = createDebugLogStream(DebugType.Input);
		IsoRegion = createDebugLogStream(DebugType.IsoRegion);
		Lua = createDebugLogStream(DebugType.Lua);
		MapLoading = createDebugLogStream(DebugType.MapLoading);
		Mod = createDebugLogStream(DebugType.Mod);
		Multiplayer = createDebugLogStream(DebugType.Multiplayer);
		Network = createDebugLogStream(DebugType.Network);
		NetworkFileDebug = createDebugLogStream(DebugType.NetworkFileDebug);
		Packet = createDebugLogStream(DebugType.Packet);
		Objects = createDebugLogStream(DebugType.Objects);
		Radio = createDebugLogStream(DebugType.Radio);
		Recipe = createDebugLogStream(DebugType.Recipe);
		Script = createDebugLogStream(DebugType.Script);
		Shader = createDebugLogStream(DebugType.Shader);
		Sound = createDebugLogStream(DebugType.Sound);
		Statistic = createDebugLogStream(DebugType.Statistic);
		UnitTests = createDebugLogStream(DebugType.UnitTests);
		Vehicle = createDebugLogStream(DebugType.Vehicle);
		Voice = createDebugLogStream(DebugType.Voice);
		Zombie = createDebugLogStream(DebugType.Zombie);
		enableLog(DebugType.Checksum, LogSeverity.Debug);
		enableLog(DebugType.Combat, LogSeverity.Debug);
		enableLog(DebugType.General, LogSeverity.Debug);
		enableLog(DebugType.IsoRegion, LogSeverity.Debug);
		enableLog(DebugType.Lua, LogSeverity.Debug);
		enableLog(DebugType.Mod, LogSeverity.Debug);
		enableLog(DebugType.Multiplayer, LogSeverity.Debug);
		enableLog(DebugType.Network, LogSeverity.Debug);
		enableLog(DebugType.Vehicle, LogSeverity.Debug);
		enableLog(DebugType.Voice, LogSeverity.Debug);
	if (GameServer.bServer) {
		enableLog(DebugType.Damage, LogSeverity.Debug);
		enableLog(DebugType.Death, LogSeverity.Debug);
		enableLog(DebugType.Statistic, LogSeverity.Debug);
	}
	}

	private static final class OutputStreamWrapper extends FilterOutputStream {

		public OutputStreamWrapper(OutputStream outputStream) {
			super(outputStream);
		}

		public void write(byte[] byteArray, int int1, int int2) throws IOException {
			this.out.write(byteArray, int1, int2);
			if (Core.bDebug && UIDebugConsole.instance != null && DebugOptions.instance.UIDebugConsoleDebugLog.getValue()) {
				UIDebugConsole.instance.addOutput(byteArray, int1, int2);
			}
		}

		public void setStream(OutputStream outputStream) {
			this.out = outputStream;
		}
	}
}
