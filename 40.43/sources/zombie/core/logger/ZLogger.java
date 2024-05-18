package zombie.core.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;


public class ZLogger {
	private String name;
	private final HashMap outputStreams;
	private File file;
	static SimpleDateFormat _fileNameSdf = new SimpleDateFormat("dd-MM-yy_HH-mm");
	SimpleDateFormat _logSdf;
	private long maxSizeKo;

	public ZLogger(String string) {
		this.name = null;
		this.outputStreams = new HashMap();
		this.file = null;
		this._logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");
		this.maxSizeKo = 10000L;
		try {
			this.name = string;
			this.file = new File(LoggerManager.getLogsDir() + File.separator + this.getLoggerName(string) + ".txt");
			this.outputStreams.put(ZLogger.LoggerOutput.file, new PrintStream(this.file));
		} catch (FileNotFoundException fileNotFoundException) {
			fileNotFoundException.printStackTrace();
		}
	}

	public ZLogger(String string, boolean boolean1) {
		this(string);
		if (boolean1) {
			this.outputStreams.put(ZLogger.LoggerOutput.console, System.out);
		}
	}

	private String getLoggerName(String string) {
		return _fileNameSdf.format(Calendar.getInstance().getTime()) + "_" + string;
	}

	public synchronized void write(String string) {
		this.write(string, (String)null);
	}

	public synchronized void write(String string, String string2) {
		try {
			Iterator iterator = this.outputStreams.values().iterator();
			while (iterator.hasNext()) {
				PrintStream printStream = (PrintStream)iterator.next();
				printStream.print("[" + this._logSdf.format(Calendar.getInstance().getTime()) + "]");
				if (string2 != null && !"".equals(string2)) {
					printStream.print("[" + string2 + "]");
				}

				printStream.print(" " + string + ".\r\n");
				printStream.flush();
				this.checkSize();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	public synchronized void write(Exception exception) {
		exception.printStackTrace((PrintStream)this.outputStreams.get(ZLogger.LoggerOutput.file));
		this.checkSize();
	}

	private void checkSize() {
		long long1 = this.file.length() / 1024L;
		try {
			if (long1 > this.maxSizeKo) {
				((PrintStream)this.outputStreams.get(ZLogger.LoggerOutput.file)).close();
				this.file = new File(LoggerManager.getLogsDir() + File.separator + this.getLoggerName(this.name) + ".txt");
				this.outputStreams.replace(ZLogger.LoggerOutput.file, new PrintStream(this.file));
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	private static enum LoggerOutput {

		file,
		console;
	}
}
