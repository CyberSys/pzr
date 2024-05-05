package zombie.debug;

import java.io.PrintStream;
import zombie.core.Core;
import zombie.util.StringUtils;


public final class DebugLogStream extends PrintStream {
	private final PrintStream m_wrappedStream;
	private final PrintStream m_wrappedWarnStream;
	private final PrintStream m_wrappedErrStream;
	private final IDebugLogFormatter m_formatter;
	public static final String s_prefixErr = "ERROR: ";
	public static final String s_prefixWarn = "WARN : ";
	public static final String s_prefixOut = "LOG  : ";
	public static final String s_prefixDebug = "DEBUG: ";
	public static final String s_prefixTrace = "TRACE: ";

	public DebugLogStream(PrintStream printStream, PrintStream printStream2, PrintStream printStream3, IDebugLogFormatter iDebugLogFormatter) {
		super(printStream);
		this.m_wrappedStream = printStream;
		this.m_wrappedWarnStream = printStream2;
		this.m_wrappedErrStream = printStream3;
		this.m_formatter = iDebugLogFormatter;
	}

	private void write(PrintStream printStream, String string) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string);
		if (string2 != null) {
			printStream.print(string2);
		}
	}

	private void writeln(PrintStream printStream, String string) {
		this.writeln(printStream, LogSeverity.General, "LOG  : ", string);
	}

	private void writeln(PrintStream printStream, String string, Object object) {
		this.writeln(printStream, LogSeverity.General, "LOG  : ", string, object);
	}

	private void writeln(PrintStream printStream, LogSeverity logSeverity, String string, String string2) {
		String string3 = this.m_formatter.format(logSeverity, string, "", string2);
		if (string3 != null) {
			printStream.println(string3);
		}
	}

	private void writeln(PrintStream printStream, LogSeverity logSeverity, String string, String string2, Object object) {
		String string3 = this.m_formatter.format(logSeverity, string, "", string2, object);
		if (string3 != null) {
			printStream.println(string3);
		}
	}

	public static String generateCallerPrefix() {
		StackTraceElement stackTraceElement = tryGetCallerTraceElement(4);
		return stackTraceElement == null ? "(UnknownStack)" : getStackTraceElementString(stackTraceElement, false);
	}

	public static StackTraceElement tryGetCallerTraceElement(int int1) {
		StackTraceElement[] stackTraceElementArray = Thread.currentThread().getStackTrace();
		if (stackTraceElementArray.length <= int1) {
			return null;
		} else {
			StackTraceElement stackTraceElement = stackTraceElementArray[int1];
			return stackTraceElement;
		}
	}

	public static String getStackTraceElementString(StackTraceElement stackTraceElement, boolean boolean1) {
		if (stackTraceElement == null) {
			return "(UnknownStack)";
		} else {
			String string = getUnqualifiedClassName(stackTraceElement.getClassName());
			String string2 = stackTraceElement.getMethodName();
			int int1 = stackTraceElement.getLineNumber();
			String string3;
			if (stackTraceElement.isNativeMethod()) {
				string3 = " (Native Method)";
			} else if (boolean1 && int1 > -1) {
				string3 = " line:" + int1;
			} else {
				string3 = "";
			}

			String string4 = string + "." + string2 + string3;
			return string4;
		}
	}

	public static String getTopStackTraceString(Throwable throwable) {
		if (throwable == null) {
			return "Null Exception";
		} else {
			StackTraceElement[] stackTraceElementArray = throwable.getStackTrace();
			if (stackTraceElementArray != null && stackTraceElementArray.length != 0) {
				StackTraceElement stackTraceElement = stackTraceElementArray[0];
				return getStackTraceElementString(stackTraceElement, true);
			} else {
				return "No Stack Trace Available";
			}
		}
	}

	public void printStackTrace() {
		this.printStackTrace(0, (String)null);
	}

	public void printStackTrace(String string) {
		this.printStackTrace(0, string);
	}

	public void printStackTrace(int int1, String string) {
		if (string != null) {
			this.println(string);
		}

		StackTraceElement[] stackTraceElementArray = Thread.currentThread().getStackTrace();
		int int2 = int1 == 0 ? stackTraceElementArray.length : Math.min(int1, stackTraceElementArray.length);
		for (int int3 = 0; int3 < int2; ++int3) {
			StackTraceElement stackTraceElement = stackTraceElementArray[int3];
			this.println("\t" + stackTraceElement.toString());
		}
	}

	private static String getUnqualifiedClassName(String string) {
		String string2 = string;
		int int1 = string.lastIndexOf(46);
		if (int1 > -1 && int1 < string.length() - 1) {
			string2 = string.substring(int1 + 1);
		}

		return string2;
	}

	public void debugln(String string) {
		if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", "%s", string);
			this.m_wrappedStream.println(string3);
		}
	}

	public void debugln(String string, Object object) {
		if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object);
			this.m_wrappedStream.println(string3);
		}
	}

	public void debugln(String string, Object object, Object object2) {
		if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2);
			this.m_wrappedStream.println(string3);
		}
	}

	public void debugln(String string, Object object, Object object2, Object object3) {
		if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3);
			this.m_wrappedStream.println(string3);
		}
	}

	public void debugln(String string, Object object, Object object2, Object object3, Object object4) {
		if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4);
			this.m_wrappedStream.println(string3);
		}
	}

	public void debugln(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4, object5);
			this.m_wrappedStream.println(string3);
		}
	}

	public void debugln(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		if (this.m_formatter.isLogEnabled(LogSeverity.General)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.General, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4, object5, object6);
			this.m_wrappedStream.println(string3);
		}
	}

	public void print(boolean boolean1) {
		this.write(this.m_wrappedStream, boolean1 ? "true" : "false");
	}

	public void print(char char1) {
		this.write(this.m_wrappedStream, String.valueOf(char1));
	}

	public void print(int int1) {
		this.write(this.m_wrappedStream, String.valueOf(int1));
	}

	public void print(long long1) {
		this.write(this.m_wrappedStream, String.valueOf(long1));
	}

	public void print(float float1) {
		this.write(this.m_wrappedStream, String.valueOf(float1));
	}

	public void print(double double1) {
		this.write(this.m_wrappedStream, String.valueOf(double1));
	}

	public void print(String string) {
		this.write(this.m_wrappedStream, String.valueOf(string));
	}

	public void print(Object object) {
		this.write(this.m_wrappedStream, String.valueOf(object));
	}

	public PrintStream printf(String string, Object[] objectArray) {
		this.write(this.m_wrappedStream, String.format(string, objectArray));
		return this;
	}

	public void println() {
		this.writeln(this.m_wrappedStream, "");
	}

	public void println(boolean boolean1) {
		this.writeln(this.m_wrappedStream, "%s", String.valueOf(boolean1));
	}

	public void println(char char1) {
		this.writeln(this.m_wrappedStream, "%s", String.valueOf(char1));
	}

	public void println(int int1) {
		this.writeln(this.m_wrappedStream, "%s", String.valueOf(int1));
	}

	public void println(long long1) {
		this.writeln(this.m_wrappedStream, "%s", String.valueOf(long1));
	}

	public void println(float float1) {
		this.writeln(this.m_wrappedStream, "%s", String.valueOf(float1));
	}

	public void println(double double1) {
		this.writeln(this.m_wrappedStream, "%s", String.valueOf(double1));
	}

	public void println(char[] charArray) {
		this.writeln(this.m_wrappedStream, "%s", String.valueOf(charArray));
	}

	public void println(String string) {
		this.writeln(this.m_wrappedStream, "%s", string);
	}

	public void println(Object object) {
		this.writeln(this.m_wrappedStream, "%s", object);
	}

	public void println(String string, Object object) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2, Object object3) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2, object3);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2, Object object3, Object object4) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2, object3, object4);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2, object3, object4, object5);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2, object3, object4, object5, object6);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2, object3, object4, object5, object6, object7);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2, object3, object4, object5, object6, object7, object8);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void println(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6, Object object7, Object object8, Object object9) {
		String string2 = this.m_formatter.format(LogSeverity.General, "LOG  : ", "", string, object, object2, object3, object4, object5, object6, object7, object8, object9);
		if (string2 != null) {
			this.m_wrappedStream.println(string2);
		}
	}

	public void error(Object object) {
		PrintStream printStream = this.m_wrappedErrStream;
		LogSeverity logSeverity = LogSeverity.Error;
		String string = generateCallerPrefix();
		this.writeln(printStream, logSeverity, "ERROR: ", string + "> " + String.valueOf(object));
	}

	public void error(String string, Object[] objectArray) {
		PrintStream printStream = this.m_wrappedErrStream;
		LogSeverity logSeverity = LogSeverity.Error;
		String string2 = generateCallerPrefix();
		this.writeln(printStream, logSeverity, "ERROR: ", string2 + "> " + String.format(string, objectArray));
	}

	public void warn(Object object) {
		PrintStream printStream = this.m_wrappedWarnStream;
		LogSeverity logSeverity = LogSeverity.Warning;
		String string = generateCallerPrefix();
		this.writeln(printStream, logSeverity, "WARN : ", string + "> " + String.valueOf(object));
	}

	public void warn(String string, Object[] objectArray) {
		PrintStream printStream = this.m_wrappedWarnStream;
		LogSeverity logSeverity = LogSeverity.Warning;
		String string2 = generateCallerPrefix();
		this.writeln(printStream, logSeverity, "WARN : ", string2 + "> " + String.format(string, objectArray));
	}

	public void printUnitTest(String string, boolean boolean1, Object[] objectArray) {
		if (!boolean1) {
			this.error(string + ", fail", objectArray);
		} else {
			this.println(string + ", pass", objectArray);
		}
	}

	public void printException(Throwable throwable, String string, LogSeverity logSeverity) {
		this.printException(throwable, string, generateCallerPrefix(), logSeverity);
	}

	public void printException(Throwable throwable, String string, String string2, LogSeverity logSeverity) {
		if (throwable == null) {
			this.warn("Null exception passed.");
		} else {
			String string3;
			PrintStream printStream;
			boolean boolean1;
			switch (logSeverity) {
			case Trace: 
			
			case General: 
				string3 = "LOG  : ";
				printStream = this.m_wrappedStream;
				boolean1 = false;
				break;
			
			case Warning: 
				string3 = "WARN : ";
				printStream = this.m_wrappedWarnStream;
				boolean1 = false;
				break;
			
			default: 
				this.error("Unhandled LogSeverity: %s. Defaulted to Error.", String.valueOf(logSeverity));
			
			case Error: 
				string3 = "ERROR: ";
				printStream = this.m_wrappedErrStream;
				boolean1 = true;
			
			}

			if (string != null) {
				this.writeln(printStream, logSeverity, string3, String.format("%s> Exception thrown %s at %s. Message: %s", string2, throwable.toString(), getTopStackTraceString(throwable), string));
			} else {
				this.writeln(printStream, logSeverity, string3, String.format("%s> Exception thrown %s at %s.", string2, throwable.toString(), getTopStackTraceString(throwable)));
			}

			if (boolean1) {
				this.error("Stack trace:");
				throwable.printStackTrace(printStream);
			}
		}
	}

	public void noise(String string) {
		if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", "%s", string);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void noise(String string, Object object) {
		if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void noise(String string, Object object, Object object2) {
		if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void noise(String string, Object object, Object object2, Object object3) {
		if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void noise(String string, Object object, Object object2, Object object3, Object object4) {
		if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void noise(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4, object5);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void noise(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		if (Core.bDebug && this.m_formatter.isLogSeverityEnabled(LogSeverity.Debug)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Debug, "DEBUG: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4, object5, object6);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void trace(String string) {
		if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string2, 36) + "> ", "%s", string);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void trace(String string, Object object) {
		if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string2, 36) + "> ", string, object);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void trace(String string, Object object, Object object2) {
		if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void trace(String string, Object object, Object object2, Object object3) {
		if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void trace(String string, Object object, Object object2, Object object3, Object object4) {
		if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void trace(String string, Object object, Object object2, Object object3, Object object4, Object object5) {
		if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4, object5);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}

	public void trace(String string, Object object, Object object2, Object object3, Object object4, Object object5, Object object6) {
		if (this.m_formatter.isLogSeverityEnabled(LogSeverity.Trace)) {
			String string2 = generateCallerPrefix();
			String string3 = this.m_formatter.format(LogSeverity.Trace, "TRACE: ", StringUtils.leftJustify(string2, 36) + "> ", string, object, object2, object3, object4, object5, object6);
			if (string3 != null) {
				this.m_wrappedStream.println(string3);
			}
		}
	}
}
