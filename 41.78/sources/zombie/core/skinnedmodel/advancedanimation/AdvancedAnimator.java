package zombie.core.skinnedmodel.advancedanimation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import org.w3c.dom.Element;
import zombie.DebugFileWatcher;
import zombie.GameProfiler;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaManager;
import zombie.characters.CharacterActionAnims;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.animation.debug.AnimationPlayerRecorder;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.util.Lambda;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.list.PZArrayList;
import zombie.util.list.PZArrayUtil;


public final class AdvancedAnimator implements IAnimEventCallback {
	private IAnimatable character;
	public AnimationSet animSet;
	public final ArrayList animCallbackHandlers = new ArrayList();
	private AnimLayer m_rootLayer = null;
	private final List m_subLayers = new ArrayList();
	public static float s_MotionScale = 0.76F;
	public static float s_RotationScale = 0.76F;
	private AnimatorDebugMonitor debugMonitor;
	private static long animSetModificationTime = -1L;
	private static long actionGroupModificationTime = -1L;
	private AnimationPlayerRecorder m_recorder = null;

	public static void systemInit() {
		DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/AnimSets", AdvancedAnimator::isAnimSetFilePath, AdvancedAnimator::onAnimSetsRefreshTriggered));
		DebugFileWatcher.instance.add(new PredicatedFileWatcher("media/actiongroups", AdvancedAnimator::isActionGroupFilePath, AdvancedAnimator::onActionGroupsRefreshTriggered));
		LoadDefaults();
	}

	private static boolean isAnimSetFilePath(String string) {
		if (string == null) {
			return false;
		} else if (!string.endsWith(".xml")) {
			return false;
		} else {
			ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				String string2 = (String)arrayList.get(int1);
				ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string2);
				if (mod != null && mod.animSetsFile != null && string.startsWith(mod.animSetsFile.getPath())) {
					return true;
				}
			}

			String string3 = ZomboidFileSystem.instance.getAnimSetsPath();
			if (!string.startsWith(string3)) {
				return false;
			} else {
				return true;
			}
		}
	}

	private static boolean isActionGroupFilePath(String string) {
		if (string == null) {
			return false;
		} else if (!string.endsWith(".xml")) {
			return false;
		} else {
			ArrayList arrayList = ZomboidFileSystem.instance.getModIDs();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				String string2 = (String)arrayList.get(int1);
				ChooseGameInfo.Mod mod = ChooseGameInfo.getModDetails(string2);
				if (mod != null && mod.actionGroupsFile != null && string.startsWith(mod.actionGroupsFile.getPath())) {
					return true;
				}
			}

			String string3 = ZomboidFileSystem.instance.getActionGroupsPath();
			if (!string.startsWith(string3)) {
				return false;
			} else {
				return true;
			}
		}
	}

	private static void onActionGroupsRefreshTriggered(String string) {
		DebugLog.General.println("DebugFileWatcher Hit. ActionGroups: " + string);
		actionGroupModificationTime = System.currentTimeMillis() + 1000L;
	}

	private static void onAnimSetsRefreshTriggered(String string) {
		DebugLog.General.println("DebugFileWatcher Hit. AnimSets: " + string);
		animSetModificationTime = System.currentTimeMillis() + 1000L;
	}

	public static void checkModifiedFiles() {
		if (animSetModificationTime != -1L && animSetModificationTime < System.currentTimeMillis()) {
			DebugLog.General.println("Refreshing AnimSets.");
			animSetModificationTime = -1L;
			LoadDefaults();
			LuaManager.GlobalObject.refreshAnimSets(true);
		}

		if (actionGroupModificationTime != -1L && actionGroupModificationTime < System.currentTimeMillis()) {
			DebugLog.General.println("Refreshing action groups.");
			actionGroupModificationTime = -1L;
			LuaManager.GlobalObject.reloadActionGroups();
		}
	}

	private static void LoadDefaults() {
		try {
			Element element = PZXmlUtil.parseXml("media/AnimSets/Defaults.xml");
			String string = element.getElementsByTagName("MotionScale").item(0).getTextContent();
			s_MotionScale = Float.parseFloat(string);
			String string2 = element.getElementsByTagName("RotationScale").item(0).getTextContent();
			s_RotationScale = Float.parseFloat(string2);
		} catch (PZXmlParserException pZXmlParserException) {
			DebugLog.General.error("Exception thrown: " + pZXmlParserException);
			pZXmlParserException.printStackTrace();
		}
	}

	public String GetDebug() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("GameState: ");
		if (this.character instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)this.character;
			stringBuilder.append(gameCharacter.getCurrentState() == null ? "null" : gameCharacter.getCurrentState().getClass().getSimpleName()).append("\n");
		}

		if (this.m_rootLayer != null) {
			stringBuilder.append("Layer: ").append(0).append("\n");
			stringBuilder.append(this.m_rootLayer.GetDebugString()).append("\n");
		}

		stringBuilder.append("Variables:\n");
		stringBuilder.append("Weapon: ").append(this.character.getVariableString("weapon")).append("\n");
		stringBuilder.append("Aim: ").append(this.character.getVariableString("aim")).append("\n");
		Iterator iterator = this.character.getGameVariables().iterator();
		while (iterator.hasNext()) {
			IAnimationVariableSlot iAnimationVariableSlot = (IAnimationVariableSlot)iterator.next();
			stringBuilder.append("  ").append(iAnimationVariableSlot.getKey()).append(" : ").append(iAnimationVariableSlot.getValueString()).append("\n");
		}

		return stringBuilder.toString();
	}

	public void OnAnimDataChanged(boolean boolean1) {
		if (boolean1 && this.character instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)this.character;
			++gameCharacter.getStateMachine().activeStateChanged;
			gameCharacter.setDefaultState();
			if (gameCharacter instanceof IsoZombie) {
				gameCharacter.setOnFloor(false);
			}

			--gameCharacter.getStateMachine().activeStateChanged;
		}

		this.SetAnimSet(AnimationSet.GetAnimationSet(this.character.GetAnimSetName(), false));
		if (this.character.getAnimationPlayer() != null) {
			this.character.getAnimationPlayer().reset();
		}

		if (this.m_rootLayer != null) {
			this.m_rootLayer.Reset();
		}

		for (int int1 = 0; int1 < this.m_subLayers.size(); ++int1) {
			AdvancedAnimator.SubLayerSlot subLayerSlot = (AdvancedAnimator.SubLayerSlot)this.m_subLayers.get(int1);
			subLayerSlot.animLayer.Reset();
		}
	}

	public void Reload() {
	}

	public void init(IAnimatable iAnimatable) {
		this.character = iAnimatable;
		this.m_rootLayer = new AnimLayer(iAnimatable, this);
	}

	public void SetAnimSet(AnimationSet animationSet) {
		this.animSet = animationSet;
	}

	public void OnAnimEvent(AnimLayer animLayer, AnimEvent animEvent) {
		for (int int1 = 0; int1 < this.animCallbackHandlers.size(); ++int1) {
			IAnimEventCallback iAnimEventCallback = (IAnimEventCallback)this.animCallbackHandlers.get(int1);
			iAnimEventCallback.OnAnimEvent(animLayer, animEvent);
		}
	}

	public String getCurrentStateName() {
		return this.m_rootLayer == null ? null : this.m_rootLayer.getCurrentStateName();
	}

	public boolean containsState(String string) {
		return this.animSet != null && this.animSet.containsState(string);
	}

	public void SetState(String string) {
		this.SetState(string, PZArrayList.emptyList());
	}

	public void SetState(String string, List list) {
		if (this.animSet == null) {
			DebugLog.Animation.error("(" + string + ") Cannot set state. AnimSet is null.");
		} else {
			if (!this.animSet.containsState(string)) {
				DebugLog.Animation.error("State not found: " + string);
			}

			this.m_rootLayer.TransitionTo(this.animSet.GetState(string), false);
			PZArrayUtil.forEach(this.m_subLayers, (var0)->{
				var0.shouldBeActive = false;
			});

			Lambda.forEachFrom(PZArrayUtil::forEach, (List)list, this, (var0,stringx)->{
				AdvancedAnimator.SubLayerSlot list = stringx.getOrCreateSlot(var0);
				list.transitionTo(stringx.animSet.GetState(var0), false);
			});

			PZArrayUtil.forEach(this.m_subLayers, AdvancedAnimator.SubLayerSlot::applyTransition);
		}
	}

	protected AdvancedAnimator.SubLayerSlot getOrCreateSlot(String string) {
		AdvancedAnimator.SubLayerSlot subLayerSlot = null;
		int int1 = 0;
		int int2;
		AdvancedAnimator.SubLayerSlot subLayerSlot2;
		for (int2 = this.m_subLayers.size(); int1 < int2; ++int1) {
			subLayerSlot2 = (AdvancedAnimator.SubLayerSlot)this.m_subLayers.get(int1);
			if (subLayerSlot2.animLayer.isCurrentState(string)) {
				subLayerSlot = subLayerSlot2;
				break;
			}
		}

		if (subLayerSlot != null) {
			return subLayerSlot;
		} else {
			int1 = 0;
			for (int2 = this.m_subLayers.size(); int1 < int2; ++int1) {
				subLayerSlot2 = (AdvancedAnimator.SubLayerSlot)this.m_subLayers.get(int1);
				if (subLayerSlot2.animLayer.isStateless()) {
					subLayerSlot = subLayerSlot2;
					break;
				}
			}

			if (subLayerSlot != null) {
				return subLayerSlot;
			} else {
				AdvancedAnimator.SubLayerSlot subLayerSlot3 = new AdvancedAnimator.SubLayerSlot(this.m_rootLayer, this.character, this);
				this.m_subLayers.add(subLayerSlot3);
				return subLayerSlot3;
			}
		}
	}

	public void update() {
		GameProfiler.getInstance().invokeAndMeasure("AdvancedAnimator.Update", this, AdvancedAnimator::updateInternal);
	}

	private void updateInternal() {
		if (this.character.getAnimationPlayer() != null) {
			if (this.character.getAnimationPlayer().isReady()) {
				if (this.animSet != null) {
					if (!this.m_rootLayer.hasState()) {
						this.m_rootLayer.TransitionTo(this.animSet.GetState("Idle"), true);
					}

					this.m_rootLayer.Update();
					int int1;
					for (int1 = 0; int1 < this.m_subLayers.size(); ++int1) {
						AdvancedAnimator.SubLayerSlot subLayerSlot = (AdvancedAnimator.SubLayerSlot)this.m_subLayers.get(int1);
						subLayerSlot.update();
					}

					if (this.debugMonitor != null && this.character instanceof IsoGameCharacter) {
						int1 = 1 + this.getActiveSubLayerCount();
						AnimLayer[] animLayerArray = new AnimLayer[int1];
						animLayerArray[0] = this.m_rootLayer;
						int1 = 0;
						for (int int2 = 0; int2 < this.m_subLayers.size(); ++int2) {
							AdvancedAnimator.SubLayerSlot subLayerSlot2 = (AdvancedAnimator.SubLayerSlot)this.m_subLayers.get(int2);
							if (subLayerSlot2.shouldBeActive) {
								animLayerArray[1 + int1] = subLayerSlot2.animLayer;
								++int1;
							}
						}

						this.debugMonitor.update((IsoGameCharacter)this.character, animLayerArray);
					}
				}
			}
		}
	}

	public void render() {
		if (this.character.getAnimationPlayer() != null) {
			if (this.character.getAnimationPlayer().isReady()) {
				if (this.animSet != null) {
					if (this.m_rootLayer.hasState()) {
						this.m_rootLayer.render();
					}
				}
			}
		}
	}

	public void printDebugCharacterActions(String string) {
		if (this.animSet != null) {
			AnimState animState = this.animSet.GetState("actions");
			if (animState != null) {
				boolean boolean1 = false;
				boolean boolean2 = false;
				CharacterActionAnims[] characterActionAnimsArray = CharacterActionAnims.values();
				int int1 = characterActionAnimsArray.length;
				for (int int2 = 0; int2 < int1; ++int2) {
					CharacterActionAnims characterActionAnims = characterActionAnimsArray[int2];
					boolean1 = false;
					String string2;
					if (characterActionAnims == CharacterActionAnims.None) {
						string2 = string;
						boolean1 = true;
					} else {
						string2 = characterActionAnims.toString();
					}

					boolean boolean3 = false;
					Iterator iterator = animState.m_Nodes.iterator();
					while (iterator.hasNext()) {
						AnimNode animNode = (AnimNode)iterator.next();
						Iterator iterator2 = animNode.m_Conditions.iterator();
						while (iterator2.hasNext()) {
							AnimCondition animCondition = (AnimCondition)iterator2.next();
							if (animCondition.m_Type == AnimCondition.Type.STRING && animCondition.m_Name.toLowerCase().equals("performingaction") && animCondition.m_StringValue.equalsIgnoreCase(string2)) {
								boolean3 = true;
								break;
							}
						}

						if (boolean3) {
							break;
						}
					}

					if (boolean3) {
						if (boolean1) {
							boolean2 = true;
						}
					} else {
						DebugLog.log("WARNING: did not find node with condition \'PerformingAction = " + string2 + "\' in player/actions/");
					}
				}

				if (boolean2) {
					if (DebugLog.isEnabled(DebugType.Animation)) {
						DebugLog.Animation.debugln("SUCCESS - Current \'actions\' TargetNode: \'" + string + "\' was found.");
					}
				} else if (DebugLog.isEnabled(DebugType.Animation)) {
					DebugLog.Animation.debugln("FAIL - Current \'actions\' TargetNode: \'" + string + "\' not found.");
				}
			}
		}
	}

	public ArrayList debugGetVariables() {
		ArrayList arrayList = new ArrayList();
		if (this.animSet != null) {
			Iterator iterator = this.animSet.states.entrySet().iterator();
			while (iterator.hasNext()) {
				Entry entry = (Entry)iterator.next();
				AnimState animState = (AnimState)entry.getValue();
				Iterator iterator2 = animState.m_Nodes.iterator();
				while (iterator2.hasNext()) {
					AnimNode animNode = (AnimNode)iterator2.next();
					Iterator iterator3 = animNode.m_Conditions.iterator();
					while (iterator3.hasNext()) {
						AnimCondition animCondition = (AnimCondition)iterator3.next();
						if (animCondition.m_Name != null && !arrayList.contains(animCondition.m_Name.toLowerCase())) {
							arrayList.add(animCondition.m_Name.toLowerCase());
						}
					}
				}
			}
		}

		return arrayList;
	}

	public AnimatorDebugMonitor getDebugMonitor() {
		return this.debugMonitor;
	}

	public void setDebugMonitor(AnimatorDebugMonitor animatorDebugMonitor) {
		this.debugMonitor = animatorDebugMonitor;
	}

	public IAnimatable getCharacter() {
		return this.character;
	}

	public void updateSpeedScale(String string, float float1) {
		if (this.m_rootLayer != null) {
			List list = this.m_rootLayer.getLiveAnimNodes();
			for (int int1 = 0; int1 < list.size(); ++int1) {
				LiveAnimNode liveAnimNode = (LiveAnimNode)list.get(int1);
				if (liveAnimNode.isActive() && liveAnimNode.getSourceNode() != null && string.equals(liveAnimNode.getSourceNode().m_SpeedScaleVariable)) {
					liveAnimNode.getSourceNode().m_SpeedScale = float1.makeConcatWithConstants < invokedynamic > (float1);
					for (int int2 = 0; int2 < liveAnimNode.m_AnimationTracks.size(); ++int2) {
						((AnimationTrack)liveAnimNode.m_AnimationTracks.get(int2)).SpeedDelta = float1;
					}
				}
			}
		}
	}

	public boolean containsAnyIdleNodes() {
		if (this.m_rootLayer == null) {
			return false;
		} else {
			boolean boolean1 = false;
			List list = this.m_rootLayer.getLiveAnimNodes();
			int int1;
			for (int1 = 0; int1 < list.size() && !boolean1; ++int1) {
				boolean1 = ((LiveAnimNode)list.get(int1)).isIdleAnimActive();
			}

			for (int1 = 0; int1 < this.getSubLayerCount(); ++int1) {
				AnimLayer animLayer = this.getSubLayerAt(int1);
				list = animLayer.getLiveAnimNodes();
				for (int int2 = 0; int2 < list.size(); ++int2) {
					boolean1 = ((LiveAnimNode)list.get(int2)).isIdleAnimActive();
					if (!boolean1) {
						break;
					}
				}
			}

			return boolean1;
		}
	}

	public AnimLayer getRootLayer() {
		return this.m_rootLayer;
	}

	public int getSubLayerCount() {
		return this.m_subLayers.size();
	}

	public AnimLayer getSubLayerAt(int int1) {
		return ((AdvancedAnimator.SubLayerSlot)this.m_subLayers.get(int1)).animLayer;
	}

	public int getActiveSubLayerCount() {
		int int1 = 0;
		for (int int2 = 0; int2 < this.m_subLayers.size(); ++int2) {
			AdvancedAnimator.SubLayerSlot subLayerSlot = (AdvancedAnimator.SubLayerSlot)this.m_subLayers.get(int2);
			if (subLayerSlot.shouldBeActive) {
				++int1;
			}
		}

		return int1;
	}

	public void setRecorder(AnimationPlayerRecorder animationPlayerRecorder) {
		this.m_recorder = animationPlayerRecorder;
	}

	public boolean isRecording() {
		return this.m_recorder != null && this.m_recorder.isRecording();
	}

	public static class SubLayerSlot {
		public boolean shouldBeActive = false;
		public final AnimLayer animLayer;

		public SubLayerSlot(AnimLayer animLayer, IAnimatable iAnimatable, IAnimEventCallback iAnimEventCallback) {
			this.animLayer = new AnimLayer(animLayer, iAnimatable, iAnimEventCallback);
		}

		public void update() {
			this.animLayer.Update();
		}

		public void transitionTo(AnimState animState, boolean boolean1) {
			this.animLayer.TransitionTo(animState, boolean1);
			this.shouldBeActive = animState != null;
		}

		public void applyTransition() {
			if (!this.shouldBeActive) {
				this.transitionTo((AnimState)null, false);
			}
		}
	}
}
