package zombie.core.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import zombie.debug.DebugLog;
import zombie.util.StringUtils;


public final class ZLogger {
	private final String name;
	private final ZLogger.OutputStreams outputStreams = new ZLogger.OutputStreams();
	private File file = null;
	private static final SimpleDateFormat s_fileNameSdf = new SimpleDateFormat("dd-MM-yy_HH-mm-ss");
	private static final SimpleDateFormat s_logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");
	private static final long s_maxSizeKo = 10000L;

	public ZLogger(String string, boolean boolean1) {
		this.name = string;
		try {
			String string2 = LoggerManager.getLogsDir();
			this.file = new File(string2 + File.separator + getLoggerName(string) + ".txt");
			this.outputStreams.file = new PrintStream(this.file);
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		}

		if (boolean1) {
			this.outputStreams.console = System.out;
		}
	}

	private static String getLoggerName(String string) {
		String string2 = s_fileNameSdf.format(Calendar.getInstance().getTime());
		return string2 + "_" + string;
	}

	public void write(String string) {
		this.write(string, (String)null);
	}

	public void write(String string, String string2) {
		try {
			this.writeUnsafe(string, string2);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public synchronized void writeUnsafe(String string, String string2) throws Exception {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.setLength(0);
		stringBuilder.append("[").append(s_logSdf.format(Calendar.getInstance().getTime())).append("]");
		if (!StringUtils.isNullOrEmpty(string2)) {
			stringBuilder.append("[").append(string2).append("]");
		}

		int int1 = string.length();
		if (string.lastIndexOf(10) == string.length() - 1) {
			--int1;
		}

		stringBuilder.append(" ").append(string, 0, int1).append(".");
		this.outputStreams.println(stringBuilder.toString());
		this.checkSizeUnsafe();
	}

	public synchronized void write(Exception exception) {
		exception.printStackTrace(this.outputStreams.file);
		this.checkSize();
	}

	private synchronized void checkSize() {
		try {
			this.checkSizeUnsafe();
		} catch (Exception exception) {
			DebugLog.General.error("Exception thrown checking log file size.");
			DebugLog.General.error(exception);
			exception.printStackTrace();
		}
	}

	private synchronized void checkSizeUnsafe() throws Exception {
		long long1 = this.file.length() / 1024L;
		if (long1 > 10000L) {
			this.outputStreams.file.close();
			String string = LoggerManager.getLogsDir();
			this.file = new File(string + File.separator + getLoggerName(this.name) + ".txt");
			this.outputStreams.file = new PrintStream(this.file);
		}
	}

	private static class OutputStreams {
		public PrintStream file;
		public PrintStream console;

		public void println(String string) {
			if (this.file != null) {
				this.file.println(string);
				this.file.flush();
			}

			if (this.console != null) {
				this.console.println(string);
			}
		}
	}
}
