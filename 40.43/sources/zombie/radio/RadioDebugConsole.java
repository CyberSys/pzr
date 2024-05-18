package zombie.radio;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.lwjgl.input.Keyboard;
import zombie.core.Color;
import zombie.radio.scripting.RadioBroadCast;
import zombie.radio.scripting.RadioChannel;
import zombie.radio.scripting.RadioScript;
import zombie.radio.scripting.RadioScriptManager;
import zombie.ui.TextManager;
import zombie.ui.UIFont;


public class RadioDebugConsole {
	private HashMap state = new HashMap();
	private int channelIndex = 0;
	private int testcounter = 0;
	private Color colRed = new Color(255, 0, 0, 255);
	private Color colGreen = new Color(0, 255, 0, 255);
	private Color colWhite = new Color(255, 255, 255, 255);
	private Color colGrey = new Color(150, 150, 150, 255);
	private Color colDyn = new Color(255, 255, 255, 255);
	private int drawY = 0;
	private int drawX = 0;
	private int drawYLine = 20;

	public RadioDebugConsole() {
		this.state.put(12, false);
		this.state.put(13, false);
		this.state.put(53, false);
		this.state.put(26, false);
	}

	public void update() {
		Map map = RadioScriptManager.getInstance().getChannels();
		boolean boolean1;
		Entry entry;
		for (Iterator iterator = this.state.entrySet().iterator(); iterator.hasNext(); entry.setValue(boolean1)) {
			entry = (Entry)iterator.next();
			boolean1 = Keyboard.isKeyDown((Integer)entry.getKey());
			if (boolean1 && (Boolean)entry.getValue() != boolean1) {
				switch ((Integer)entry.getKey()) {
				case 12: 
					--this.channelIndex;
					if (this.channelIndex < 0 && map != null) {
						this.channelIndex = map.size() - 1;
					}

					break;
				
				case 13: 
					++this.channelIndex;
					if (map != null && this.channelIndex >= map.size()) {
						this.channelIndex = 0;
					}

				
				case 26: 
				
				case 53: 
				
				}
			}
		}
	}

	public void render() {
		Map map = RadioScriptManager.getInstance().getChannels();
		if (map != null && map.size() != 0) {
			if (this.channelIndex < 0) {
				this.channelIndex = 0;
			}

			if (this.channelIndex >= map.size()) {
				this.channelIndex = map.size() - 1;
			}

			this.drawYLine = 20;
			this.drawX = 20;
			this.drawY = 200;
			short short1 = 150;
			this.DrawLine("Scamble once: ", 0, false, this.colGrey);
			this.AddBlancLine();
			this.DrawLine("Radio Script Manager Debug.", 0, true);
			this.DrawLine("Real Time: ", 0, false, this.colGrey);
			this.DrawLine(timeStampToString(RadioScriptManager.getInstance().getCurrentTimeStamp()), short1, true);
			this.AddBlancLine();
			this.AddBlancLine();
			this.DrawLine("Index: " + (this.channelIndex + 1) + " of " + map.size() + " total channels.", 0, true);
			RadioChannel radioChannel = (RadioChannel)map.values().toArray()[this.channelIndex];
			if (radioChannel != null) {
				this.DrawLine("Selected channel: ", 0, false, this.colGrey);
				this.DrawLine(radioChannel.GetName(), short1, true);
				this.DrawLine("Type: ", 0, false, this.colGrey);
				this.DrawLine(radioChannel.IsTv() ? "Television" : "Radio", short1, true);
				this.DrawLine("Frequency: ", 0, false, this.colGrey);
				this.DrawLine(Integer.toString(radioChannel.GetFrequency()), short1, true);
				this.DrawLine("Category: ", 0, false, this.colGrey);
				this.DrawLine(radioChannel.GetCategory().toString(), short1, true);
				this.DrawLine("PlayerListening: ", 0, false, this.colGrey);
				if (radioChannel.GetPlayerIsListening()) {
					this.DrawLine("Yes", short1, true, this.colGreen);
				} else {
					this.DrawLine("No", short1, true, this.colRed);
				}

				RadioBroadCast radioBroadCast = radioChannel.getAiringBroadcast();
				if (radioBroadCast != null) {
					this.AddBlancLine();
					this.DrawLine("Is airing a broadcast:", 0, true, this.colGreen);
					this.DrawLine("ID: ", 0, false, this.colGrey);
					this.DrawLine(radioBroadCast.getID(), short1, true);
					this.DrawLine("StartStamp: ", 0, false, this.colGrey);
					this.DrawLine(timeStampToString(radioBroadCast.getStartStamp()), short1, true);
					this.DrawLine("EndStamp: ", 0, false, this.colGrey);
					this.DrawLine(timeStampToString(radioBroadCast.getEndStamp()), short1, true);
					if (radioBroadCast.getCurrentLine() != null) {
						this.colDyn.r = radioBroadCast.getCurrentLine().getR();
						this.colDyn.g = radioBroadCast.getCurrentLine().getG();
						this.colDyn.b = radioBroadCast.getCurrentLine().getB();
						if (radioBroadCast.getCurrentLine().getText() != null) {
							this.DrawLine("Next line to be aired: ", 0, false, this.colGrey);
							this.DrawLine(radioBroadCast.PeekNextLineText(), short1, true, this.colDyn);
						}
					}
				}

				this.AddBlancLine();
				RadioScript radioScript = radioChannel.getCurrentScript();
				if (radioScript != null) {
					this.DrawLine("Currently working on RadioScript: ", 0, true);
					this.DrawLine("Name: ", 0, false, this.colGrey);
					this.DrawLine(radioScript.GetName(), short1, true);
					this.DrawLine("Start day: ", 0, false, this.colGrey);
					this.DrawLine(timeStampToString(radioScript.getStartDayStamp()), short1, true);
					this.DrawLine("Current loop: ", 0, false, this.colGrey);
					this.DrawLine(Integer.toString(radioChannel.getCurrentScriptLoop()), short1, true);
					this.DrawLine("Total loops: ", 0, false, this.colGrey);
					this.DrawLine(Integer.toString(radioChannel.getCurrentScriptMaxLoops()), short1, true);
					radioBroadCast = radioScript.getCurrentBroadcast();
					if (radioBroadCast != null) {
						this.AddBlancLine();
						this.DrawLine("Currently active broadcast:", 0, true);
						this.DrawLine("ID: ", 0, false, this.colGrey);
						this.DrawLine(radioBroadCast.getID(), short1, true);
						this.DrawLine("Real StartStamp: ", 0, false, this.colGrey);
						this.DrawLine(timeStampToString(radioBroadCast.getStartStamp() + radioScript.getStartDayStamp()), short1, true);
						this.DrawLine("Real EndStamp: ", 0, false, this.colGrey);
						this.DrawLine(timeStampToString(radioBroadCast.getEndStamp() + radioScript.getStartDayStamp()), short1, true);
						this.DrawLine("Script StartStamp: ", 0, false, this.colGrey);
						this.DrawLine(timeStampToString(radioBroadCast.getStartStamp()), short1, true);
						this.DrawLine("Script EndStamp: ", 0, false, this.colGrey);
						this.DrawLine(timeStampToString(radioBroadCast.getEndStamp()), short1, true);
						if (radioBroadCast.getCurrentLine() != null) {
							this.colDyn.r = radioBroadCast.getCurrentLine().getR();
							this.colDyn.g = radioBroadCast.getCurrentLine().getG();
							this.colDyn.b = radioBroadCast.getCurrentLine().getB();
							if (radioBroadCast.getCurrentLine().getText() != null) {
								this.DrawLine("Next line to be aired: ", 0, false, this.colGrey);
								this.DrawLine(radioBroadCast.PeekNextLineText(), short1, true, this.colDyn);
							}
						}
					}
				}
			}
		}
	}

	public static String timeStampToString(int int1) {
		int int2 = int1 / 1440;
		int int3 = int1 / 60 % 24;
		int int4 = int1 % 60;
		return "Day: " + Integer.toString(int2) + ", Hour: " + Integer.toString(int3) + ", Minute: " + Integer.toString(int4);
	}

	private void AddBlancLine() {
		this.drawY += this.drawYLine;
	}

	private void DrawLine(String string, int int1, boolean boolean1, Color color) {
		TextManager.instance.DrawString(UIFont.Medium, (double)(this.drawX + int1), (double)this.drawY, string, (double)color.r, (double)color.g, (double)color.b, (double)color.a);
		if (boolean1) {
			this.drawY += this.drawYLine;
		}
	}

	private void DrawLine(String string, int int1, boolean boolean1) {
		this.DrawLine(string, int1, boolean1, this.colWhite);
	}
}
