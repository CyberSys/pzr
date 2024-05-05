package zombie.core.skinnedmodel.advancedanimation;

import java.util.HashMap;
import zombie.util.StringUtils;


public class AnimationVariableHandlePool {
	private static final Object s_threadLock = "AnimationVariableHandlePool.ThreadLock";
	private static HashMap s_handlePool = new HashMap();
	private static int s_globalIndexGenerator = 0;

	public static AnimationVariableHandle getOrCreate(String string) {
		synchronized (s_threadLock) {
			return getOrCreateInternal(string);
		}
	}

	private static AnimationVariableHandle getOrCreateInternal(String string) {
		if (!isVariableNameValid(string)) {
			return null;
		} else {
			AnimationVariableHandle animationVariableHandle = (AnimationVariableHandle)s_handlePool.get(string);
			if (animationVariableHandle != null) {
				return animationVariableHandle;
			} else {
				AnimationVariableHandle animationVariableHandle2 = new AnimationVariableHandle();
				animationVariableHandle2.setVariableName(string);
				animationVariableHandle2.setVariableIndex(generateNewVariableIndex());
				s_handlePool.put(string, animationVariableHandle2);
				return animationVariableHandle2;
			}
		}
	}

	private static boolean isVariableNameValid(String string) {
		return !StringUtils.isNullOrWhitespace(string);
	}

	private static int generateNewVariableIndex() {
		return s_globalIndexGenerator++;
	}
}
