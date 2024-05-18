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

   public ZLogger(String var1) {
      this.name = null;
      this.outputStreams = new HashMap();
      this.file = null;
      this._logSdf = new SimpleDateFormat("dd-MM-yy HH:mm:ss.SSS");
      this.maxSizeKo = 10000L;

      try {
         this.name = var1;
         this.file = new File(LoggerManager.getLogsDir() + File.separator + this.getLoggerName(var1) + ".txt");
         this.outputStreams.put(ZLogger.LoggerOutput.file, new PrintStream(this.file));
      } catch (FileNotFoundException var3) {
         var3.printStackTrace();
      }

   }

   public ZLogger(String var1, boolean var2) {
      this(var1);
      if (var2) {
         this.outputStreams.put(ZLogger.LoggerOutput.console, System.out);
      }

   }

   private String getLoggerName(String var1) {
      return _fileNameSdf.format(Calendar.getInstance().getTime()) + "_" + var1;
   }

   public synchronized void write(String var1) {
      this.write(var1, (String)null);
   }

   public synchronized void write(String var1, String var2) {
      try {
         Iterator var3 = this.outputStreams.values().iterator();

         while(var3.hasNext()) {
            PrintStream var4 = (PrintStream)var3.next();
            var4.print("[" + this._logSdf.format(Calendar.getInstance().getTime()) + "]");
            if (var2 != null && !"".equals(var2)) {
               var4.print("[" + var2 + "]");
            }

            var4.print(" " + var1 + ".\r\n");
            var4.flush();
            this.checkSize();
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public synchronized void write(Exception var1) {
      var1.printStackTrace((PrintStream)this.outputStreams.get(ZLogger.LoggerOutput.file));
      this.checkSize();
   }

   private void checkSize() {
      long var1 = this.file.length() / 1024L;

      try {
         if (var1 > this.maxSizeKo) {
            ((PrintStream)this.outputStreams.get(ZLogger.LoggerOutput.file)).close();
            this.file = new File(LoggerManager.getLogsDir() + File.separator + this.getLoggerName(this.name) + ".txt");
            this.outputStreams.replace(ZLogger.LoggerOutput.file, new PrintStream(this.file));
         }
      } catch (IOException var4) {
         var4.printStackTrace();
      }

   }

   private static enum LoggerOutput {
      file,
      console;
   }
}
