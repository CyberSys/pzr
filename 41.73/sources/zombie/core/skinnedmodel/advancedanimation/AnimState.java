package zombie.core.skinnedmodel.advancedanimation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import zombie.ZomboidFileSystem;
import zombie.asset.AssetPath;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.DebugType;
import zombie.util.StringUtils;


public final class AnimState {
	public String m_Name = "";
	public final List m_Nodes = new ArrayList();
	public int m_DefaultIndex = 0;
	public AnimationSet m_Set = null;
	private static final boolean s_bDebugLog_NodeConditions = false;

	public List getAnimNodes(IAnimationVariableSource iAnimationVariableSource, List list) {
		list.clear();
		if (this.m_Nodes.size() <= 0) {
			return list;
		} else {
			int int1;
			int int2;
			AnimNode animNode;
			if (DebugOptions.instance.Animation.AnimLayer.AllowAnimNodeOverride.getValue() && iAnimationVariableSource.getVariableBoolean("dbgForceAnim") && iAnimationVariableSource.isVariable("dbgForceAnimStateName", this.m_Name)) {
				String string = iAnimationVariableSource.getVariableString("dbgForceAnimNodeName");
				int1 = 0;
				for (int2 = this.m_Nodes.size(); int1 < int2; ++int1) {
					animNode = (AnimNode)this.m_Nodes.get(int1);
					if (StringUtils.equalsIgnoreCase(animNode.m_Name, string)) {
						list.add(animNode);
						break;
					}
				}

				return list;
			} else {
				int int3 = -1;
				int1 = 0;
				for (int2 = this.m_Nodes.size(); int1 < int2; ++int1) {
					animNode = (AnimNode)this.m_Nodes.get(int1);
					if (!animNode.isAbstract() && animNode.m_Conditions.size() >= int3 && animNode.checkConditions(iAnimationVariableSource)) {
						if (int3 < animNode.m_Conditions.size()) {
							list.clear();
							int3 = animNode.m_Conditions.size();
						}

						list.add(animNode);
					}
				}

				if (!list.isEmpty()) {
				}

				return list;
			}
		}
	}

	public static AnimState Parse(String string, String string2) {
		boolean boolean1 = DebugLog.isEnabled(DebugType.Animation);
		AnimState animState = new AnimState();
		animState.m_Name = string;
		if (boolean1) {
			DebugLog.Animation.println("Loading AnimState: " + string);
		}

		String[] stringArray = ZomboidFileSystem.instance.resolveAllFiles(string2, (stringx)->{
    return stringx.getName().endsWith(".xml");
}, true);
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string3 = stringArray2[int2];
			File file = new File(string3);
			String string4 = file.getName().split(".xml")[0].toLowerCase();
			if (boolean1) {
				DebugLog.Animation.println(string + " -> AnimNode: " + string4);
			}

			String string5 = ZomboidFileSystem.instance.resolveFileOrGUID(string3);
			AnimNodeAsset animNodeAsset = (AnimNodeAsset)AnimNodeAssetManager.instance.load(new AssetPath(string5));
			if (animNodeAsset.isReady()) {
				AnimNode animNode = animNodeAsset.m_animNode;
				animNode.m_State = animState;
				animState.m_Nodes.add(animNode);
			}
		}

		return animState;
	}

	public String toString() {
		String string = this.m_Name;
		return "AnimState{" + string + ", NodeCount:" + this.m_Nodes.size() + ", DefaultIndex:" + this.m_DefaultIndex + "}";
	}

	public static String getStateName(AnimState animState) {
		return animState != null ? animState.m_Name : null;
	}

	protected void clear() {
		this.m_Nodes.clear();
		this.m_Set = null;
	}

	private static String lambda$getAnimNodes$0(AnimNode animNode) {
		return String.format("%s: %s", animNode.m_Name, animNode.getConditionsString());
	}
}
