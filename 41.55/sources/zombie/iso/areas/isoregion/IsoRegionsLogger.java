package zombie.iso.areas.isoregion;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import zombie.core.Color;
import zombie.core.Core;
import zombie.debug.DebugLog;


public class IsoRegionsLogger {
	private final ConcurrentLinkedQueue pool = new ConcurrentLinkedQueue();
	private final ConcurrentLinkedQueue loggerQueue = new ConcurrentLinkedQueue();
	private final boolean consolePrint;
	private final ArrayList logs = new ArrayList();
	private final int maxLogs = 100;
	private boolean isDirtyUI = false;

	public IsoRegionsLogger(boolean boolean1) {
		this.consolePrint = boolean1;
	}

	public ArrayList getLogs() {
		return this.logs;
	}

	public boolean isDirtyUI() {
		return this.isDirtyUI;
	}

	public void unsetDirtyUI() {
		this.isDirtyUI = false;
	}

	private IsoRegionsLogger.IsoRegionLog getLog() {
		IsoRegionsLogger.IsoRegionLog regionLog = (IsoRegionsLogger.IsoRegionLog)this.pool.poll();
		if (regionLog == null) {
			regionLog = new IsoRegionsLogger.IsoRegionLog();
		}

		return regionLog;
	}

	protected void log(String string) {
		this.log(string, (Color)null);
	}

	protected void log(String string, Color color) {
		if (Core.bDebug) {
			if (this.consolePrint) {
				DebugLog.IsoRegion.println(string);
			}

			IsoRegionsLogger.IsoRegionLog regionLog = this.getLog();
			regionLog.str = string;
			regionLog.type = IsoRegionLogType.Normal;
			regionLog.col = color;
			this.loggerQueue.offer(regionLog);
		}
	}

	protected void warn(String string) {
		DebugLog.IsoRegion.warn(string);
		if (Core.bDebug) {
			IsoRegionsLogger.IsoRegionLog regionLog = this.getLog();
			regionLog.str = string;
			regionLog.type = IsoRegionLogType.Warn;
			this.loggerQueue.offer(regionLog);
		}
	}

	protected void update() {
		if (Core.bDebug) {
			for (IsoRegionsLogger.IsoRegionLog regionLog = (IsoRegionsLogger.IsoRegionLog)this.loggerQueue.poll(); regionLog != null; regionLog = (IsoRegionsLogger.IsoRegionLog)this.loggerQueue.poll()) {
				if (this.logs.size() >= 100) {
					IsoRegionsLogger.IsoRegionLog regionLog2 = (IsoRegionsLogger.IsoRegionLog)this.logs.remove(0);
					regionLog2.col = null;
					this.pool.offer(regionLog2);
				}

				this.logs.add(regionLog);
				this.isDirtyUI = true;
			}
		}
	}

	public static class IsoRegionLog {
		private String str;
		private IsoRegionLogType type;
		private Color col;

		public String getStr() {
			return this.str;
		}

		public IsoRegionLogType getType() {
			return this.type;
		}

		public Color getColor() {
			if (this.col != null) {
				return this.col;
			} else {
				return this.type == IsoRegionLogType.Warn ? Color.red : Color.white;
			}
		}
	}
}
