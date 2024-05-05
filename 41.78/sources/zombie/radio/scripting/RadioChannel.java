package zombie.radio.scripting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import zombie.GameTime;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.radio.ChannelCategory;
import zombie.radio.RadioData;
import zombie.radio.ZomboidRadio;


public class RadioChannel {
	private String GUID;
	private RadioData radioData;
	private boolean isTimeSynced;
	private Map scripts;
	private int frequency;
	private String name;
	private boolean isTv;
	private ChannelCategory category;
	private boolean playerIsListening;
	private RadioScript currentScript;
	private int currentScriptLoop;
	private int currentScriptMaxLoops;
	private RadioBroadCast airingBroadcast;
	private float airCounter;
	private String lastAiredLine;
	private String lastBroadcastID;
	private float airCounterMultiplier;
	private boolean louisvilleObfuscate;
	float minmod;
	float maxmod;

	public RadioChannel(String string, int int1, ChannelCategory channelCategory) {
		this(string, int1, channelCategory, UUID.randomUUID().toString());
	}

	public RadioChannel(String string, int int1, ChannelCategory channelCategory, String string2) {
		this.isTimeSynced = false;
		this.scripts = new HashMap();
		this.frequency = -1;
		this.name = "Unnamed channel";
		this.isTv = false;
		this.category = ChannelCategory.Undefined;
		this.playerIsListening = false;
		this.currentScript = null;
		this.currentScriptLoop = 1;
		this.currentScriptMaxLoops = 1;
		this.airingBroadcast = null;
		this.airCounter = 0.0F;
		this.lastAiredLine = "";
		this.lastBroadcastID = null;
		this.airCounterMultiplier = 1.0F;
		this.louisvilleObfuscate = false;
		this.minmod = 1.5F;
		this.maxmod = 5.0F;
		this.name = string;
		this.frequency = int1;
		this.category = channelCategory;
		this.isTv = this.category == ChannelCategory.Television;
		this.GUID = string2;
	}

	public String getGUID() {
		return this.GUID;
	}

	public int GetFrequency() {
		return this.frequency;
	}

	public String GetName() {
		return this.name;
	}

	public boolean IsTv() {
		return this.isTv;
	}

	public ChannelCategory GetCategory() {
		return this.category;
	}

	public RadioScript getCurrentScript() {
		return this.currentScript;
	}

	public RadioBroadCast getAiringBroadcast() {
		return this.airingBroadcast;
	}

	public String getLastAiredLine() {
		return this.lastAiredLine;
	}

	public int getCurrentScriptLoop() {
		return this.currentScriptLoop;
	}

	public int getCurrentScriptMaxLoops() {
		return this.currentScriptMaxLoops;
	}

	public String getLastBroadcastID() {
		return this.lastBroadcastID;
	}

	public RadioData getRadioData() {
		return this.radioData;
	}

	public void setRadioData(RadioData radioData) {
		this.radioData = radioData;
	}

	public boolean isTimeSynced() {
		return this.isTimeSynced;
	}

	public void setTimeSynced(boolean boolean1) {
		this.isTimeSynced = boolean1;
	}

	public boolean isVanilla() {
		return this.radioData == null || this.radioData.isVanilla();
	}

	public void setLouisvilleObfuscate(boolean boolean1) {
		this.louisvilleObfuscate = boolean1;
	}

	public void LoadAiringBroadcast(String string, int int1) {
		if (this.currentScript != null) {
			this.airingBroadcast = this.currentScript.getBroadcastWithID(string);
			if (int1 < 0) {
				this.airingBroadcast = null;
			}

			if (this.airingBroadcast != null && int1 >= 0) {
				this.airingBroadcast.resetLineCounter();
				this.airingBroadcast.setCurrentLineNumber(int1);
				this.airCounter = 120.0F;
				this.playerIsListening = true;
			}
		}
	}

	public void SetPlayerIsListening(boolean boolean1) {
		this.playerIsListening = boolean1;
		if (this.playerIsListening && this.airingBroadcast == null && this.currentScript != null) {
			this.airingBroadcast = this.currentScript.getValidAirBroadcast();
			if (this.airingBroadcast != null) {
				this.airingBroadcast.resetLineCounter();
			}

			this.airCounter = 0.0F;
		}
	}

	public boolean GetPlayerIsListening() {
		return this.playerIsListening;
	}

	public void setActiveScriptNull() {
		this.currentScript = null;
		this.airingBroadcast = null;
	}

	public void setActiveScript(String string, int int1) {
		this.setActiveScript(string, int1, 1, -1);
	}

	public void setActiveScript(String string, int int1, int int2, int int3) {
		if (string != null && this.scripts.containsKey(string)) {
			this.currentScript = (RadioScript)this.scripts.get(string);
			if (this.currentScript != null) {
				this.currentScript.Reset();
				this.currentScript.setStartDayStamp(int1);
				this.currentScriptLoop = int2;
				if (int3 == -1) {
					int int4 = this.currentScript.getLoopMin();
					int int5 = this.currentScript.getLoopMax();
					if (int4 != int5 && int4 <= int5) {
						int3 = Rand.Next(int4, int5);
					} else {
						int3 = int4;
					}
				}

				this.currentScriptMaxLoops = int3;
				if (DebugLog.isEnabled(DebugType.Radio)) {
					DebugLog.Radio.println("Script: " + string + ", day = " + int1 + ", minloops = " + this.currentScript.getLoopMin() + ", maxloops = " + this.currentScriptMaxLoops);
				}
			}
		}
	}

	private void getNextScript(int int1) {
		if (this.currentScript != null) {
			if (this.currentScriptLoop < this.currentScriptMaxLoops) {
				++this.currentScriptLoop;
				this.currentScript.Reset();
				this.currentScript.setStartDayStamp(int1);
			} else {
				RadioScript.ExitOption exitOption = this.currentScript.getNextScript();
				this.currentScript = null;
				if (exitOption != null) {
					this.setActiveScript(exitOption.getScriptname(), int1 + exitOption.getStartDelay());
				}
			}
		}
	}

	public void UpdateScripts(int int1, int int2) {
		this.playerIsListening = false;
		if (this.currentScript != null && !this.currentScript.UpdateScript(int1)) {
			this.getNextScript(int2 + 1);
		}
	}

	public void update() {
		if (this.airingBroadcast != null) {
			this.airCounter -= 1.25F * GameTime.getInstance().getMultiplier();
			if (this.airCounter < 0.0F) {
				RadioLine radioLine = this.airingBroadcast.getNextLine();
				if (radioLine == null) {
					this.lastBroadcastID = this.airingBroadcast.getID();
					this.airingBroadcast = null;
					this.playerIsListening = false;
				} else {
					this.lastAiredLine = radioLine.getText();
					if (!ZomboidRadio.DISABLE_BROADCASTING) {
						String string = radioLine.getText();
						if (this.louisvilleObfuscate && ZomboidRadio.LOUISVILLE_OBFUSCATION) {
							string = ZomboidRadio.getInstance().scrambleString(string, 85, true, (String)null);
							ZomboidRadio.getInstance().SendTransmission(0, 0, this.frequency, string, (String)null, "", 0.7F, 0.5F, 0.5F, -1, this.isTv);
						} else {
							ZomboidRadio.getInstance().SendTransmission(0, 0, this.frequency, string, (String)null, radioLine.getEffectsString(), radioLine.getR(), radioLine.getG(), radioLine.getB(), -1, this.isTv);
						}
					}

					if (radioLine.isCustomAirTime()) {
						this.airCounter = radioLine.getAirTime() * 60.0F;
					} else {
						this.airCounter = (float)radioLine.getText().length() / 10.0F * 60.0F;
						if (this.airCounter < 60.0F * this.minmod) {
							this.airCounter = 60.0F * this.minmod;
						} else if (this.airCounter > 60.0F * this.maxmod) {
							this.airCounter = 60.0F * this.maxmod;
						}

						this.airCounter *= this.airCounterMultiplier;
					}
				}
			}
		}
	}

	public void AddRadioScript(RadioScript radioScript) {
		if (radioScript != null && !this.scripts.containsKey(radioScript.GetName())) {
			this.scripts.put(radioScript.GetName(), radioScript);
		} else {
			String string = radioScript != null ? radioScript.GetName() : "null";
			DebugLog.log(DebugType.Radio, "Error while attempting to add script (" + string + "), null or name already exists.");
		}
	}

	public RadioScript getRadioScript(String string) {
		return string != null && this.scripts.containsKey(string) ? (RadioScript)this.scripts.get(string) : null;
	}

	public void setAiringBroadcast(RadioBroadCast radioBroadCast) {
		this.airingBroadcast = radioBroadCast;
	}

	public float getAirCounterMultiplier() {
		return this.airCounterMultiplier;
	}

	public void setAirCounterMultiplier(float float1) {
		this.airCounterMultiplier = PZMath.clamp(float1, 0.1F, 10.0F);
	}
}
