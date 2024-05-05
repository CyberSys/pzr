package zombie.core.skinnedmodel.advancedanimation.debug;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Map.Entry;
import zombie.characters.IsoGameCharacter;
import zombie.core.Color;
import zombie.core.Colors;
import zombie.core.skinnedmodel.advancedanimation.AnimLayer;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSlot;
import zombie.core.skinnedmodel.advancedanimation.LiveAnimNode;
import zombie.core.skinnedmodel.animation.AnimationTrack;


public final class AnimatorDebugMonitor {
	private static final ArrayList knownVariables = new ArrayList();
	private static boolean knownVarsDirty = false;
	private String currentState = "null";
	private AnimatorDebugMonitor.MonitoredLayer[] monitoredLayers;
	private final HashMap monitoredVariables = new HashMap();
	private final ArrayList customVariables = new ArrayList();
	private final LinkedList logLines = new LinkedList();
	private final Queue logLineQueue = new LinkedList();
	private boolean floatsListDirty = false;
	private boolean hasFilterChanges = false;
	private boolean hasLogUpdates = false;
	private String logString = "";
	private static final int maxLogSize = 1028;
	private static final int maxOutputLines = 128;
	private static final int maxFloatCache = 1024;
	private final ArrayList floatsOut = new ArrayList();
	private AnimatorDebugMonitor.MonitoredVar selectedVariable;
	private int tickCount = 0;
	private boolean doTickStamps = false;
	private static final int tickStampLength = 10;
	private static final Color col_curstate;
	private static final Color col_layer_nodename;
	private static final Color col_layer_activated;
	private static final Color col_layer_deactivated;
	private static final Color col_track_activated;
	private static final Color col_track_deactivated;
	private static final Color col_node_activated;
	private static final Color col_node_deactivated;
	private static final Color col_var_activated;
	private static final Color col_var_changed;
	private static final Color col_var_deactivated;
	private static final String TAG_VAR = "[variable]";
	private static final String TAG_LAYER = "[layer]";
	private static final String TAG_NODE = "[active_nodes]";
	private static final String TAG_TRACK = "[anim_tracks]";
	private boolean[] logFlags;

	public AnimatorDebugMonitor(IsoGameCharacter gameCharacter) {
		this.logFlags = new boolean[AnimatorDebugMonitor.LogType.MAX.value()];
		this.logFlags[AnimatorDebugMonitor.LogType.DEFAULT.value()] = true;
		int int1;
		for (int1 = 0; int1 < this.logFlags.length; ++int1) {
			this.logFlags[int1] = true;
		}

		for (int1 = 0; int1 < 1024; ++int1) {
			this.floatsOut.add(0.0F);
		}

		this.initCustomVars();
		if (gameCharacter != null && gameCharacter.advancedAnimator != null) {
			ArrayList arrayList = gameCharacter.advancedAnimator.debugGetVariables();
			Iterator iterator = arrayList.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				registerVariable(string);
			}
		}
	}

	private void initCustomVars() {
		this.addCustomVariable("aim");
		this.addCustomVariable("bdead");
		this.addCustomVariable("bfalling");
		this.addCustomVariable("baimatfloor");
		this.addCustomVariable("battackfrombehind");
		this.addCustomVariable("attacktype");
		this.addCustomVariable("bundervehicle");
		this.addCustomVariable("reanimatetimer");
		this.addCustomVariable("isattacking");
		this.addCustomVariable("canclimbdownrope");
		this.addCustomVariable("frombehind");
		this.addCustomVariable("fallonfront");
		this.addCustomVariable("hashitreaction");
		this.addCustomVariable("hitreaction");
		this.addCustomVariable("collided");
		this.addCustomVariable("collidetype");
		this.addCustomVariable("intrees");
	}

	public void addCustomVariable(String string) {
		String string2 = string.toLowerCase();
		if (!this.customVariables.contains(string2)) {
			this.customVariables.add(string2);
		}

		registerVariable(string);
	}

	public void removeCustomVariable(String string) {
		String string2 = string.toLowerCase();
		this.customVariables.remove(string2);
	}

	public void setFilter(int int1, boolean boolean1) {
		if (int1 >= 0 && int1 < AnimatorDebugMonitor.LogType.MAX.value()) {
			this.logFlags[int1] = boolean1;
			this.hasFilterChanges = true;
		}
	}

	public boolean getFilter(int int1) {
		return int1 >= 0 && int1 < AnimatorDebugMonitor.LogType.MAX.value() ? this.logFlags[int1] : false;
	}

	public boolean isDoTickStamps() {
		return this.doTickStamps;
	}

	public void setDoTickStamps(boolean boolean1) {
		if (this.doTickStamps != boolean1) {
			this.doTickStamps = boolean1;
			this.hasFilterChanges = true;
		}
	}

	private void queueLogLine(String string) {
		this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, (Color)null, true);
	}

	private void queueLogLine(String string, Color color) {
		this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, color, true);
	}

	private void queueLogLine(AnimatorDebugMonitor.LogType logType, String string, Color color) {
		this.addLogLine(logType, string, color, true);
	}

	private void addLogLine(String string) {
		this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, (Color)null, false);
	}

	private void addLogLine(String string, Color color) {
		this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, color, false);
	}

	private void addLogLine(String string, Color color, boolean boolean1) {
		this.addLogLine(AnimatorDebugMonitor.LogType.DEFAULT, string, color, boolean1);
	}

	private void addLogLine(AnimatorDebugMonitor.LogType logType, String string, Color color) {
		this.addLogLine(logType, string, color, false);
	}

	private void addLogLine(AnimatorDebugMonitor.LogType logType, String string, Color color, boolean boolean1) {
		AnimatorDebugMonitor.MonitorLogLine monitorLogLine = new AnimatorDebugMonitor.MonitorLogLine();
		monitorLogLine.line = string;
		monitorLogLine.color = color;
		monitorLogLine.type = logType;
		monitorLogLine.tick = this.tickCount;
		if (boolean1) {
			this.logLineQueue.add(monitorLogLine);
		} else {
			this.log(monitorLogLine);
		}
	}

	private void log(AnimatorDebugMonitor.MonitorLogLine monitorLogLine) {
		this.logLines.addFirst(monitorLogLine);
		if (this.logLines.size() > 1028) {
			this.logLines.removeLast();
		}

		this.hasLogUpdates = true;
	}

	private void processQueue() {
		while (this.logLineQueue.size() > 0) {
			AnimatorDebugMonitor.MonitorLogLine monitorLogLine = (AnimatorDebugMonitor.MonitorLogLine)this.logLineQueue.poll();
			this.log(monitorLogLine);
		}
	}

	private void preUpdate() {
		Entry entry;
		for (Iterator iterator = this.monitoredVariables.entrySet().iterator(); iterator.hasNext(); ((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).updated = false) {
			entry = (Entry)iterator.next();
		}

		for (int int1 = 0; int1 < this.monitoredLayers.length; ++int1) {
			AnimatorDebugMonitor.MonitoredLayer monitoredLayer = this.monitoredLayers[int1];
			monitoredLayer.updated = false;
			Iterator iterator2;
			Entry entry2;
			for (iterator2 = monitoredLayer.activeNodes.entrySet().iterator(); iterator2.hasNext(); ((AnimatorDebugMonitor.MonitoredNode)entry2.getValue()).updated = false) {
				entry2 = (Entry)iterator2.next();
			}

			for (iterator2 = monitoredLayer.animTracks.entrySet().iterator(); iterator2.hasNext(); ((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).updated = false) {
				entry2 = (Entry)iterator2.next();
			}
		}
	}

	private void postUpdate() {
		Iterator iterator = this.monitoredVariables.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).active && !((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).updated) {
				this.addLogLine(AnimatorDebugMonitor.LogType.VAR, "[variable] : removed -> \'" + (String)entry.getKey() + "\', last value: \'" + ((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).value + "\'.", col_var_deactivated);
				((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).active = false;
			}
		}

		for (int int1 = 0; int1 < this.monitoredLayers.length; ++int1) {
			AnimatorDebugMonitor.MonitoredLayer monitoredLayer = this.monitoredLayers[int1];
			Iterator iterator2 = monitoredLayer.activeNodes.entrySet().iterator();
			Entry entry2;
			while (iterator2.hasNext()) {
				entry2 = (Entry)iterator2.next();
				if (((AnimatorDebugMonitor.MonitoredNode)entry2.getValue()).active && !((AnimatorDebugMonitor.MonitoredNode)entry2.getValue()).updated) {
					this.addLogLine(AnimatorDebugMonitor.LogType.NODE, "[layer][" + monitoredLayer.index + "] [active_nodes] : deactivated -> \'" + ((AnimatorDebugMonitor.MonitoredNode)entry2.getValue()).name + "\'.", col_node_deactivated);
					((AnimatorDebugMonitor.MonitoredNode)entry2.getValue()).active = false;
				}
			}

			iterator2 = monitoredLayer.animTracks.entrySet().iterator();
			while (iterator2.hasNext()) {
				entry2 = (Entry)iterator2.next();
				if (((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).active && !((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).updated) {
					this.addLogLine(AnimatorDebugMonitor.LogType.TRACK, "[layer][" + monitoredLayer.index + "] [anim_tracks] : deactivated -> \'" + ((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).name + "\'.", col_track_deactivated);
					((AnimatorDebugMonitor.MonitoredTrack)entry2.getValue()).active = false;
				}
			}

			if (monitoredLayer.active && !monitoredLayer.updated) {
				this.addLogLine(AnimatorDebugMonitor.LogType.LAYER, "[layer][" + int1 + "] : deactivated (last animstate: \'" + monitoredLayer.nodeName + "\').", col_layer_deactivated);
				monitoredLayer.active = false;
			}
		}
	}

	public void update(IsoGameCharacter gameCharacter, AnimLayer[] animLayerArray) {
		if (gameCharacter != null) {
			this.ensureLayers(animLayerArray);
			this.preUpdate();
			Iterator iterator = gameCharacter.getGameVariables().iterator();
			while (iterator.hasNext()) {
				IAnimationVariableSlot iAnimationVariableSlot = (IAnimationVariableSlot)iterator.next();
				this.updateVariable(iAnimationVariableSlot.getKey(), iAnimationVariableSlot.getValueString());
			}

			Iterator iterator2 = this.customVariables.iterator();
			while (iterator2.hasNext()) {
				String string = (String)iterator2.next();
				String string2 = gameCharacter.getVariableString(string);
				if (string2 != null) {
					this.updateVariable(string, string2);
				}
			}

			this.updateCurrentState(gameCharacter.getCurrentState() == null ? "null" : gameCharacter.getCurrentState().getClass().getSimpleName());
			for (int int1 = 0; int1 < animLayerArray.length; ++int1) {
				if (animLayerArray[int1] != null) {
					this.updateLayer(int1, animLayerArray[int1]);
				}
			}

			this.postUpdate();
			this.processQueue();
			++this.tickCount;
		}
	}

	private void updateCurrentState(String string) {
		if (!this.currentState.equals(string)) {
			this.queueLogLine("Character.currentState changed from \'" + this.currentState + "\' to: \'" + string + "\'.", col_curstate);
			this.currentState = string;
		}
	}

	private void updateLayer(int int1, AnimLayer animLayer) {
		AnimatorDebugMonitor.MonitoredLayer monitoredLayer = this.monitoredLayers[int1];
		String string = animLayer.getDebugNodeName();
		if (!monitoredLayer.active) {
			monitoredLayer.active = true;
			this.queueLogLine(AnimatorDebugMonitor.LogType.LAYER, "[layer][" + int1 + "] activated -> animstate: \'" + string + "\'.", col_layer_activated);
		}

		if (!monitoredLayer.nodeName.equals(string)) {
			this.queueLogLine(AnimatorDebugMonitor.LogType.LAYER, "[layer][" + int1 + "] changed -> animstate from \'" + monitoredLayer.nodeName + "\' to: \'" + string + "\'.", col_layer_nodename);
			monitoredLayer.nodeName = string;
		}

		Iterator iterator = animLayer.getLiveAnimNodes().iterator();
		while (iterator.hasNext()) {
			LiveAnimNode liveAnimNode = (LiveAnimNode)iterator.next();
			this.updateActiveNode(monitoredLayer, liveAnimNode.getSourceNode().m_Name);
		}

		if (animLayer.getAnimationTrack() != null) {
			iterator = animLayer.getAnimationTrack().getTracks().iterator();
			while (iterator.hasNext()) {
				AnimationTrack animationTrack = (AnimationTrack)iterator.next();
				if (animationTrack.getLayerIdx() == int1) {
					this.updateAnimTrack(monitoredLayer, animationTrack.name, animationTrack.BlendDelta);
				}
			}
		}

		monitoredLayer.updated = true;
	}

	private void updateActiveNode(AnimatorDebugMonitor.MonitoredLayer monitoredLayer, String string) {
		AnimatorDebugMonitor.MonitoredNode monitoredNode = (AnimatorDebugMonitor.MonitoredNode)monitoredLayer.activeNodes.get(string);
		if (monitoredNode == null) {
			monitoredNode = new AnimatorDebugMonitor.MonitoredNode();
			monitoredNode.name = string;
			monitoredLayer.activeNodes.put(string, monitoredNode);
		}

		if (!monitoredNode.active) {
			monitoredNode.active = true;
			this.queueLogLine(AnimatorDebugMonitor.LogType.NODE, "[layer][" + monitoredLayer.index + "] [active_nodes] : activated -> \'" + string + "\'.", col_node_activated);
		}

		monitoredNode.updated = true;
	}

	private void updateAnimTrack(AnimatorDebugMonitor.MonitoredLayer monitoredLayer, String string, float float1) {
		AnimatorDebugMonitor.MonitoredTrack monitoredTrack = (AnimatorDebugMonitor.MonitoredTrack)monitoredLayer.animTracks.get(string);
		if (monitoredTrack == null) {
			monitoredTrack = new AnimatorDebugMonitor.MonitoredTrack();
			monitoredTrack.name = string;
			monitoredTrack.blendDelta = float1;
			monitoredLayer.animTracks.put(string, monitoredTrack);
		}

		if (!monitoredTrack.active) {
			monitoredTrack.active = true;
			this.queueLogLine(AnimatorDebugMonitor.LogType.TRACK, "[layer][" + monitoredLayer.index + "] [anim_tracks] : activated -> \'" + string + "\'.", col_track_activated);
		}

		if (monitoredTrack.blendDelta != float1) {
			monitoredTrack.blendDelta = float1;
		}

		monitoredTrack.updated = true;
	}

	private void updateVariable(String string, String string2) {
		AnimatorDebugMonitor.MonitoredVar monitoredVar = (AnimatorDebugMonitor.MonitoredVar)this.monitoredVariables.get(string);
		boolean boolean1 = false;
		if (monitoredVar == null) {
			monitoredVar = new AnimatorDebugMonitor.MonitoredVar();
			this.monitoredVariables.put(string, monitoredVar);
			boolean1 = true;
		}

		if (!monitoredVar.active) {
			monitoredVar.active = true;
			monitoredVar.key = string;
			monitoredVar.value = string2;
			this.queueLogLine(AnimatorDebugMonitor.LogType.VAR, "[variable] : added -> \'" + string + "\', value: \'" + string2 + "\'.", col_var_activated);
			if (boolean1) {
				registerVariable(string);
			}
		} else if (string2 == null) {
			if (monitoredVar.isFloat) {
				monitoredVar.isFloat = false;
				this.floatsListDirty = true;
			}

			monitoredVar.value = null;
		} else if (monitoredVar.value == null || !monitoredVar.value.equals(string2)) {
			try {
				float float1 = Float.parseFloat(string2);
				monitoredVar.logFloat(float1);
				if (!monitoredVar.isFloat) {
					monitoredVar.isFloat = true;
					this.floatsListDirty = true;
				}
			} catch (NumberFormatException numberFormatException) {
				if (monitoredVar.isFloat) {
					monitoredVar.isFloat = false;
					this.floatsListDirty = true;
				}
			}

			if (!monitoredVar.isFloat) {
				this.queueLogLine(AnimatorDebugMonitor.LogType.VAR, "[variable] : updated -> \'" + string + "\' changed from \'" + monitoredVar.value + "\' to: \'" + string2 + "\'.", col_var_changed);
			}

			monitoredVar.value = string2;
		}

		monitoredVar.updated = true;
	}

	private void buildLogString() {
		ListIterator listIterator = this.logLines.listIterator(0);
		int int1 = 0;
		int int2 = 0;
		while (listIterator.hasNext()) {
			AnimatorDebugMonitor.MonitorLogLine monitorLogLine = (AnimatorDebugMonitor.MonitorLogLine)listIterator.next();
			++int2;
			if (this.logFlags[monitorLogLine.type.value()]) {
				++int1;
				if (int1 >= 128) {
					break;
				}
			}
		}

		if (int2 == 0) {
			this.logString = "";
		} else {
			listIterator = this.logLines.listIterator(int2);
			StringBuilder stringBuilder = new StringBuilder();
			while (listIterator.hasPrevious()) {
				AnimatorDebugMonitor.MonitorLogLine monitorLogLine2 = (AnimatorDebugMonitor.MonitorLogLine)listIterator.previous();
				if (this.logFlags[monitorLogLine2.type.value()]) {
					stringBuilder.append(" <TEXT> ");
					if (this.doTickStamps) {
						stringBuilder.append("[");
						stringBuilder.append(String.format("%010d", monitorLogLine2.tick));
						stringBuilder.append("]");
					}

					if (monitorLogLine2.color != null) {
						stringBuilder.append(" <RGB:");
						stringBuilder.append(monitorLogLine2.color.r);
						stringBuilder.append(",");
						stringBuilder.append(monitorLogLine2.color.g);
						stringBuilder.append(",");
						stringBuilder.append(monitorLogLine2.color.b);
						stringBuilder.append("> ");
					}

					stringBuilder.append(monitorLogLine2.line);
					stringBuilder.append(" <LINE> ");
				}
			}

			this.logString = stringBuilder.toString();
			this.hasLogUpdates = false;
			this.hasFilterChanges = false;
		}
	}

	public boolean IsDirty() {
		return this.hasLogUpdates || this.hasFilterChanges;
	}

	public String getLogString() {
		if (this.hasLogUpdates || this.hasFilterChanges) {
			this.buildLogString();
		}

		return this.logString;
	}

	public boolean IsDirtyFloatList() {
		return this.floatsListDirty;
	}

	public ArrayList getFloatNames() {
		this.floatsListDirty = false;
		ArrayList arrayList = new ArrayList();
		Iterator iterator = this.monitoredVariables.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			if (((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).isFloat) {
				arrayList.add(((AnimatorDebugMonitor.MonitoredVar)entry.getValue()).key);
			}
		}

		Collections.sort(arrayList);
		return arrayList;
	}

	public static boolean isKnownVarsDirty() {
		return knownVarsDirty;
	}

	public static List getKnownVariables() {
		knownVarsDirty = false;
		Collections.sort(knownVariables);
		return knownVariables;
	}

	public void setSelectedVariable(String string) {
		if (string == null) {
			this.selectedVariable = null;
		} else {
			this.selectedVariable = (AnimatorDebugMonitor.MonitoredVar)this.monitoredVariables.get(string);
		}
	}

	public String getSelectedVariable() {
		return this.selectedVariable != null ? this.selectedVariable.key : null;
	}

	public float getSelectedVariableFloat() {
		return this.selectedVariable != null ? this.selectedVariable.valFloat : 0.0F;
	}

	public String getSelectedVarMinFloat() {
		return this.selectedVariable != null && this.selectedVariable.isFloat && this.selectedVariable.f_min != -1.0F ? this.selectedVariable.f_min.makeConcatWithConstants < invokedynamic > (this.selectedVariable.f_min) : "-1.0";
	}

	public String getSelectedVarMaxFloat() {
		return this.selectedVariable != null && this.selectedVariable.isFloat && this.selectedVariable.f_max != -1.0F ? this.selectedVariable.f_max.makeConcatWithConstants < invokedynamic > (this.selectedVariable.f_max) : "1.0";
	}

	public ArrayList getSelectedVarFloatList() {
		if (this.selectedVariable != null && this.selectedVariable.isFloat) {
			AnimatorDebugMonitor.MonitoredVar monitoredVar = this.selectedVariable;
			int int1 = monitoredVar.f_index - 1;
			if (int1 < 0) {
				int1 = 0;
			}

			float float1 = monitoredVar.f_max - monitoredVar.f_min;
			for (int int2 = 0; int2 < 1024; ++int2) {
				float float2 = (monitoredVar.f_floats[int1--] - monitoredVar.f_min) / float1;
				this.floatsOut.set(int2, float2);
				if (int1 < 0) {
					int1 = monitoredVar.f_floats.length - 1;
				}
			}

			return this.floatsOut;
		} else {
			return null;
		}
	}

	public static void registerVariable(String string) {
		if (string != null) {
			string = string.toLowerCase();
			if (!knownVariables.contains(string)) {
				knownVariables.add(string);
				knownVarsDirty = true;
			}
		}
	}

	private void ensureLayers(AnimLayer[] animLayerArray) {
		int int1 = animLayerArray.length;
		if (this.monitoredLayers == null || this.monitoredLayers.length != int1) {
			this.monitoredLayers = new AnimatorDebugMonitor.MonitoredLayer[int1];
			for (int int2 = 0; int2 < int1; ++int2) {
				this.monitoredLayers[int2] = new AnimatorDebugMonitor.MonitoredLayer(int2);
			}
		}
	}

	static  {
		col_curstate = Colors.Cyan;
		col_layer_nodename = Colors.CornFlowerBlue;
		col_layer_activated = Colors.DarkTurquoise;
		col_layer_deactivated = Colors.Orange;
		col_track_activated = Colors.SandyBrown;
		col_track_deactivated = Colors.Salmon;
		col_node_activated = Colors.Pink;
		col_node_deactivated = Colors.Plum;
		col_var_activated = Colors.Chartreuse;
		col_var_changed = Colors.LimeGreen;
		col_var_deactivated = Colors.Gold;
	}

	private static enum LogType {

		DEFAULT,
		LAYER,
		NODE,
		TRACK,
		VAR,
		MAX,
		val;

		private LogType(int int1) {
			this.val = int1;
		}
		public int value() {
			return this.val;
		}
		private static AnimatorDebugMonitor.LogType[] $values() {
			return new AnimatorDebugMonitor.LogType[]{DEFAULT, LAYER, NODE, TRACK, VAR, MAX};
		}
	}

	private class MonitorLogLine {
		String line;
		Color color = null;
		AnimatorDebugMonitor.LogType type;
		int tick;

		private MonitorLogLine() {
			this.type = AnimatorDebugMonitor.LogType.DEFAULT;
		}
	}

	private class MonitoredVar {
		String key = "";
		String value = "";
		boolean isFloat = false;
		float valFloat;
		boolean active = false;
		boolean updated = false;
		float[] f_floats;
		int f_index = 0;
		float f_min = -1.0F;
		float f_max = 1.0F;

		public void logFloat(float float1) {
			if (this.f_floats == null) {
				this.f_floats = new float[1024];
			}

			if (float1 != this.valFloat) {
				this.valFloat = float1;
				this.f_floats[this.f_index++] = float1;
				if (float1 < this.f_min) {
					this.f_min = float1;
				}

				if (float1 > this.f_max) {
					this.f_max = float1;
				}

				if (this.f_index >= 1024) {
					this.f_index = 0;
				}
			}
		}
	}

	private class MonitoredLayer {
		int index;
		String nodeName = "";
		HashMap activeNodes = new HashMap();
		HashMap animTracks = new HashMap();
		boolean active = false;
		boolean updated = false;

		public MonitoredLayer(int int1) {
			this.index = int1;
		}
	}

	private class MonitoredNode {
		String name = "";
		boolean active = false;
		boolean updated = false;
	}

	private class MonitoredTrack {
		String name = "";
		float blendDelta;
		boolean active = false;
		boolean updated = false;
	}
}
