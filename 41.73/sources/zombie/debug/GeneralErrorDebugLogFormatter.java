package zombie.debug;


class GeneralErrorDebugLogFormatter implements IDebugLogFormatter {
	public boolean isLogEnabled(LogSeverity logSeverity) {
		return DebugLog.isLogEnabled(logSeverity, DebugType.General);
	}

	public boolean isLogSeverityEnabled(LogSeverity logSeverity) {
		return this.isLogEnabled(logSeverity);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2, object3);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2, object3, object4);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2, object3, object4, object5);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2, object3, object4, object5, object6);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2, object3, object4, object5, object6, object7);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2, object3, object4, object5, object6, object7, object8);
	}

	public String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9) {
		return DebugLog.formatString(DebugType.General, logSeverity, "ERROR: ", string2, string3, object, object2, object3, object4, object5, object6, object7, object8, object9);
	}
}
