package zombie.radio.scripting;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
import zombie.core.Rand;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;


public final class RadioScript {
	private final ArrayList broadcasts;
	private final ArrayList exitOptions;
	private String GUID;
	private String name;
	private int startDay;
	private int startDayStamp;
	private int loopMin;
	private int loopMax;
	private int internalStamp;
	private RadioBroadCast currentBroadcast;
	private boolean currentHasAired;

	public RadioScript(String string, int int1, int int2) {
		this(string, int1, int2, UUID.randomUUID().toString());
	}

	public RadioScript(String string, int int1, int int2, String string2) {
		this.broadcasts = new ArrayList();
		this.exitOptions = new ArrayList();
		this.name = "Unnamed radioscript";
		this.startDay = 0;
		this.startDayStamp = 0;
		this.loopMin = 1;
		this.loopMax = 1;
		this.internalStamp = 0;
		this.currentBroadcast = null;
		this.currentHasAired = false;
		this.name = string;
		this.loopMin = int1;
		this.loopMax = int2;
		this.GUID = string2;
	}

	public String GetGUID() {
		return this.GUID;
	}

	public String GetName() {
		return this.name;
	}

	public int getStartDayStamp() {
		return this.startDayStamp;
	}

	public int getStartDay() {
		return this.startDay;
	}

	public int getLoopMin() {
		return this.loopMin;
	}

	public int getLoopMax() {
		return this.loopMax;
	}

	public RadioBroadCast getCurrentBroadcast() {
		return this.currentBroadcast;
	}

	public ArrayList getBroadcastList() {
		return this.broadcasts;
	}

	public void clearExitOptions() {
		this.exitOptions.clear();
	}

	public void setStartDayStamp(int int1) {
		this.startDay = int1;
		this.startDayStamp = int1 * 24 * 60;
	}

	public RadioBroadCast getValidAirBroadcast() {
		if (!this.currentHasAired && this.currentBroadcast != null && this.internalStamp >= this.currentBroadcast.getStartStamp() && this.internalStamp < this.currentBroadcast.getEndStamp()) {
			this.currentHasAired = true;
			return this.currentBroadcast;
		} else {
			return null;
		}
	}

	public void Reset() {
		this.currentBroadcast = null;
		this.currentHasAired = false;
	}

	private RadioBroadCast getNextBroadcast() {
		if (this.currentBroadcast != null && this.currentBroadcast.getEndStamp() > this.internalStamp) {
			return this.currentBroadcast;
		} else {
			for (int int1 = 0; int1 < this.broadcasts.size(); ++int1) {
				RadioBroadCast radioBroadCast = (RadioBroadCast)this.broadcasts.get(int1);
				if (radioBroadCast.getEndStamp() > this.internalStamp) {
					this.currentHasAired = false;
					return radioBroadCast;
				}
			}

			return null;
		}
	}

	public RadioBroadCast getBroadcastWithID(String string) {
		for (int int1 = 0; int1 < this.broadcasts.size(); ++int1) {
			RadioBroadCast radioBroadCast = (RadioBroadCast)this.broadcasts.get(int1);
			if (radioBroadCast.getID().equals(string)) {
				this.currentBroadcast = radioBroadCast;
				this.currentHasAired = true;
				return radioBroadCast;
			}
		}

		return null;
	}

	public boolean UpdateScript(int int1) {
		this.internalStamp = int1 - this.startDayStamp;
		this.currentBroadcast = this.getNextBroadcast();
		return this.currentBroadcast != null;
	}

	public RadioScript.ExitOption getNextScript() {
		int int1 = 0;
		int int2 = Rand.Next(100);
		RadioScript.ExitOption exitOption;
		for (Iterator iterator = this.exitOptions.iterator(); iterator.hasNext(); int1 += exitOption.getChance()) {
			exitOption = (RadioScript.ExitOption)iterator.next();
			if (int2 >= int1 && int2 < int1 + exitOption.getChance()) {
				return exitOption;
			}
		}

		return null;
	}

	public void AddBroadcast(RadioBroadCast radioBroadCast) {
		this.AddBroadcast(radioBroadCast, false);
	}

	public void AddBroadcast(RadioBroadCast radioBroadCast, boolean boolean1) {
		boolean boolean2 = false;
		if (radioBroadCast != null && radioBroadCast.getID() != null) {
			if (boolean1) {
				this.broadcasts.add(radioBroadCast);
				boolean2 = true;
			} else {
				DebugType debugType;
				int int1;
				if (radioBroadCast.getStartStamp() >= 0 && radioBroadCast.getEndStamp() > radioBroadCast.getStartStamp()) {
					if (this.broadcasts.size() != 0 && ((RadioBroadCast)this.broadcasts.get(this.broadcasts.size() - 1)).getEndStamp() > radioBroadCast.getStartStamp()) {
						if (this.broadcasts.size() > 0) {
							debugType = DebugType.Radio;
							int1 = radioBroadCast.getStartStamp();
							DebugLog.log(debugType, "startstamp = \'" + int1 + "\', endstamp = \'" + radioBroadCast.getEndStamp() + "\', previous endstamp = \'" + ((RadioBroadCast)this.broadcasts.get(this.broadcasts.size() - 1)).getEndStamp() + "\'.");
						}
					} else {
						this.broadcasts.add(radioBroadCast);
						boolean2 = true;
					}
				} else {
					debugType = DebugType.Radio;
					int1 = radioBroadCast.getStartStamp();
					DebugLog.log(debugType, "startstamp = \'" + int1 + "\', endstamp = \'" + radioBroadCast.getEndStamp() + "\'.");
				}
			}
		}

		if (!boolean2) {
			String string = radioBroadCast != null ? radioBroadCast.getID() : "null";
			DebugLog.log(DebugType.Radio, "Error cannot add broadcast ID: \'" + string + "\' to script \'" + this.name + "\', null or timestamp error");
		}
	}

	public void AddExitOption(String string, int int1, int int2) {
		int int3 = int1;
		RadioScript.ExitOption exitOption;
		for (Iterator iterator = this.exitOptions.iterator(); iterator.hasNext(); int3 += exitOption.getChance()) {
			exitOption = (RadioScript.ExitOption)iterator.next();
		}

		if (int3 <= 100) {
			this.exitOptions.add(new RadioScript.ExitOption(string, int1, int2));
		} else {
			DebugLog.log(DebugType.Radio, "Error cannot add exitoption with scriptname \'" + string + "\' to script \'" + this.name + "\', total chance exceeding 100");
		}
	}

	public RadioBroadCast getValidAirBroadcastDebug() {
		if (this.currentBroadcast != null && this.currentBroadcast.getEndStamp() > this.internalStamp) {
			return this.currentBroadcast;
		} else {
			for (int int1 = 0; int1 < this.broadcasts.size(); ++int1) {
				RadioBroadCast radioBroadCast = (RadioBroadCast)this.broadcasts.get(int1);
				if (radioBroadCast.getEndStamp() > this.internalStamp) {
					return radioBroadCast;
				}
			}

			return null;
		}
	}

	public ArrayList getExitOptions() {
		return this.exitOptions;
	}

	public static final class ExitOption {
		private String scriptname = "";
		private int chance = 0;
		private int startDelay = 0;

		public ExitOption(String string, int int1, int int2) {
			this.scriptname = string;
			this.chance = int1;
			this.startDelay = int2;
		}

		public String getScriptname() {
			return this.scriptname;
		}

		public int getChance() {
			return this.chance;
		}

		public int getStartDelay() {
			return this.startDelay;
		}
	}
}
