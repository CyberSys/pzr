package zombie.debug;


public interface IDebugLogFormatter {

	boolean isLogEnabled(LogSeverity logSeverity);

	boolean isLogSeverityEnabled(LogSeverity logSeverity);

	String format(LogSeverity logSeverity, String string, String string2, String string3);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8);

	String format(LogSeverity logSeverity, String string, String string2, String string3, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9);
}
