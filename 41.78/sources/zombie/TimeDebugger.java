package zombie;

import java.util.ArrayList;
import zombie.debug.DebugLog;
import zombie.network.GameServer;


public class TimeDebugger {
	ArrayList records = new ArrayList();
	ArrayList recordStrings = new ArrayList();
	String name = "";

	public TimeDebugger(String string) {
		this.name = string;
	}

	public void clear() {
		if (GameServer.bServer) {
			this.records.clear();
			this.recordStrings.clear();
		}
	}

	public void start() {
		if (GameServer.bServer) {
			this.records.clear();
			this.recordStrings.clear();
			this.records.add(System.currentTimeMillis());
			this.recordStrings.add("Start");
		}
	}

	public void record() {
		if (GameServer.bServer) {
			this.records.add(System.currentTimeMillis());
			this.recordStrings.add(String.valueOf(this.records.size()));
		}
	}

	public void record(String string) {
		if (GameServer.bServer) {
			this.records.add(System.currentTimeMillis());
			this.recordStrings.add(string);
		}
	}

	public void recordTO(String string, int int1) {
		if (GameServer.bServer && (Long)this.records.get(this.records.size() - 1) - (Long)this.records.get(this.records.size() - 2) > (long)int1) {
			this.records.add(System.currentTimeMillis());
			this.recordStrings.add(string);
		}
	}

	public void add(TimeDebugger timeDebugger) {
		if (GameServer.bServer) {
			String string = timeDebugger.name;
			for (int int1 = 0; int1 < timeDebugger.records.size(); ++int1) {
				this.records.add((Long)timeDebugger.records.get(int1));
				this.recordStrings.add(string + "|" + (String)timeDebugger.recordStrings.get(int1));
			}

			timeDebugger.clear();
		}
	}

	public void print() {
		if (GameServer.bServer) {
			this.records.add(System.currentTimeMillis());
			this.recordStrings.add("END");
			if (this.records.size() > 1) {
				DebugLog.log("=== DBG " + this.name + " ===");
				long long1 = (Long)this.records.get(0);
				for (int int1 = 1; int1 < this.records.size(); ++int1) {
					long long2 = (Long)this.records.get(int1 - 1);
					long long3 = (Long)this.records.get(int1);
					String string = (String)this.recordStrings.get(int1);
					DebugLog.log("RECORD " + int1 + " " + string + " A:" + (long3 - long1) + " D:" + (long3 - long2));
				}

				DebugLog.log("=== END " + this.name + " (" + ((Long)this.records.get(this.records.size() - 1) - long1) + ") ===");
			} else {
				DebugLog.log("<<< DBG " + this.name + " ERROR >>>");
			}
		}
	}

	public long getExecTime() {
		return this.records.size() == 0 ? 0L : System.currentTimeMillis() - (Long)this.records.get(0);
	}
}
