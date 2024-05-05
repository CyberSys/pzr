package zombie.core.skinnedmodel.advancedanimation;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import zombie.ZomboidFileSystem;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;


public final class AnimationSet {
	protected static final HashMap setMap = new HashMap();
	public final HashMap states = new HashMap();
	public String m_Name = "";

	public static AnimationSet GetAnimationSet(String string, boolean boolean1) {
		AnimationSet animationSet = (AnimationSet)setMap.get(string);
		if (animationSet != null && !boolean1) {
			return animationSet;
		} else {
			animationSet = new AnimationSet();
			animationSet.Load(string);
			setMap.put(string, animationSet);
			return animationSet;
		}
	}

	public static void Reset() {
		Iterator iterator = setMap.values().iterator();
		while (iterator.hasNext()) {
			AnimationSet animationSet = (AnimationSet)iterator.next();
			animationSet.clear();
		}

		setMap.clear();
	}

	public AnimState GetState(String string) {
		AnimState animState = (AnimState)this.states.get(string.toLowerCase(Locale.ENGLISH));
		if (animState != null) {
			return animState;
		} else {
			DebugLog.Animation.warn("AnimState not found: " + string);
			animState = new AnimState();
			return animState;
		}
	}

	public boolean containsState(String string) {
		return this.states.containsKey(string.toLowerCase(Locale.ENGLISH));
	}

	public boolean Load(String string) {
		if (DebugLog.isEnabled(DebugType.Animation)) {
			DebugLog.Animation.println("Loading AnimSet: " + string);
		}

		this.m_Name = string;
		String[] stringArray = ZomboidFileSystem.instance.resolveAllDirectories("media/AnimSets/" + string, (var0)->{
    return true;
}, false);
		String[] stringArray2 = stringArray;
		int int1 = stringArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			String string2 = stringArray2[int2];
			String string3 = (new File(string2)).getName();
			AnimState animState = AnimState.Parse(string3, string2);
			animState.m_Set = this;
			this.states.put(string3, animState);
		}

		return true;
	}

	private void clear() {
		Iterator iterator = this.states.values().iterator();
		while (iterator.hasNext()) {
			AnimState animState = (AnimState)iterator.next();
			animState.clear();
		}

		this.states.clear();
	}
}
