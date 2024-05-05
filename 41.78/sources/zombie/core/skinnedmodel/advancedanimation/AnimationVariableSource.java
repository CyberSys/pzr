package zombie.core.skinnedmodel.advancedanimation;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


public class AnimationVariableSource implements IAnimationVariableMap {
	private final Map m_GameVariables;
	private IAnimationVariableSlot[] m_cachedGameVariableSlots;

	public AnimationVariableSource() {
		this.m_GameVariables = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		this.m_cachedGameVariableSlots = new IAnimationVariableSlot[0];
	}

	public IAnimationVariableSlot getVariable(AnimationVariableHandle animationVariableHandle) {
		if (animationVariableHandle == null) {
			return null;
		} else {
			int int1 = animationVariableHandle.getVariableIndex();
			if (int1 < 0) {
				return null;
			} else {
				IAnimationVariableSlot iAnimationVariableSlot;
				if (this.m_cachedGameVariableSlots != null && int1 < this.m_cachedGameVariableSlots.length) {
					iAnimationVariableSlot = this.m_cachedGameVariableSlots[int1];
					if (iAnimationVariableSlot == null) {
						this.m_cachedGameVariableSlots[int1] = (IAnimationVariableSlot)this.m_GameVariables.get(animationVariableHandle.getVariableName());
						iAnimationVariableSlot = this.m_cachedGameVariableSlots[int1];
					}

					return iAnimationVariableSlot;
				} else {
					iAnimationVariableSlot = (IAnimationVariableSlot)this.m_GameVariables.get(animationVariableHandle.getVariableName());
					if (iAnimationVariableSlot == null) {
						return null;
					} else {
						IAnimationVariableSlot[] iAnimationVariableSlotArray = new IAnimationVariableSlot[int1 + 1];
						IAnimationVariableSlot[] iAnimationVariableSlotArray2 = this.m_cachedGameVariableSlots;
						if (iAnimationVariableSlotArray2 != null) {
							this.m_cachedGameVariableSlots = (IAnimationVariableSlot[])PZArrayUtil.arrayCopy((Object[])iAnimationVariableSlotArray2, (Object[])iAnimationVariableSlotArray, 0, iAnimationVariableSlotArray2.length);
						}

						iAnimationVariableSlotArray[int1] = iAnimationVariableSlot;
						this.m_cachedGameVariableSlots = iAnimationVariableSlotArray;
						return iAnimationVariableSlot;
					}
				}
			}
		}
	}

	public IAnimationVariableSlot getVariable(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			String string2 = string.trim();
			return (IAnimationVariableSlot)this.m_GameVariables.get(string2);
		}
	}

	public IAnimationVariableSlot getOrCreateVariable(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			String string2 = string.trim();
			Object object = (IAnimationVariableSlot)this.m_GameVariables.get(string2);
			if (object == null) {
				object = new AnimationVariableGenericSlot(string2.toLowerCase());
				this.setVariable((IAnimationVariableSlot)object);
			}

			return (IAnimationVariableSlot)object;
		}
	}

	public void setVariable(IAnimationVariableSlot iAnimationVariableSlot) {
		this.m_GameVariables.put(iAnimationVariableSlot.getKey(), iAnimationVariableSlot);
	}

	public void setVariable(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackBool(string, callbackGetStrongTyped));
	}

	public void setVariable(String string, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackBool(string, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackString(string, callbackGetStrongTyped));
	}

	public void setVariable(String string, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackString(string, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackFloat(string, callbackGetStrongTyped));
	}

	public void setVariable(String string, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackFloat(string, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackInt(string, callbackGetStrongTyped));
	}

	public void setVariable(String string, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackInt(string, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, boolean boolean1, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackBool(string, boolean1, callbackGetStrongTyped));
	}

	public void setVariable(String string, boolean boolean1, AnimationVariableSlotCallbackBool.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackBool.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackBool(string, boolean1, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, String string2, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackString(string, string2, callbackGetStrongTyped));
	}

	public void setVariable(String string, String string2, AnimationVariableSlotCallbackString.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackString.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackString(string, string2, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, float float1, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackFloat(string, float1, callbackGetStrongTyped));
	}

	public void setVariable(String string, float float1, AnimationVariableSlotCallbackFloat.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackFloat.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackFloat(string, float1, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, int int1, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackInt(string, int1, callbackGetStrongTyped));
	}

	public void setVariable(String string, int int1, AnimationVariableSlotCallbackInt.CallbackGetStrongTyped callbackGetStrongTyped, AnimationVariableSlotCallbackInt.CallbackSetStrongTyped callbackSetStrongTyped) {
		this.setVariable(new AnimationVariableSlotCallbackInt(string, int1, callbackGetStrongTyped, callbackSetStrongTyped));
	}

	public void setVariable(String string, String string2) {
		this.getOrCreateVariable(string).setValue(string2);
	}

	public void setVariable(String string, boolean boolean1) {
		this.getOrCreateVariable(string).setValue(boolean1);
	}

	public void setVariable(String string, float float1) {
		this.getOrCreateVariable(string).setValue(float1);
	}

	public void clearVariable(String string) {
		IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(string);
		if (iAnimationVariableSlot != null) {
			iAnimationVariableSlot.clear();
		}
	}

	public void clearVariables() {
		Iterator iterator = this.getGameVariables().iterator();
		while (iterator.hasNext()) {
			IAnimationVariableSlot iAnimationVariableSlot = (IAnimationVariableSlot)iterator.next();
			iAnimationVariableSlot.clear();
		}
	}

	public String getVariableString(String string) {
		IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(string);
		return iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueString() : "";
	}

	public float getVariableFloat(String string, float float1) {
		IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(string);
		return iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueFloat() : float1;
	}

	public boolean getVariableBoolean(String string) {
		IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(string);
		return iAnimationVariableSlot != null && iAnimationVariableSlot.getValueBool();
	}

	public boolean getVariableBoolean(String string, boolean boolean1) {
		IAnimationVariableSlot iAnimationVariableSlot = this.getVariable(string);
		return iAnimationVariableSlot != null ? iAnimationVariableSlot.getValueBool() : boolean1;
	}

	public Iterable getGameVariables() {
		return this.m_GameVariables.values();
	}

	public boolean isVariable(String string, String string2) {
		return StringUtils.equalsIgnoreCase(this.getVariableString(string), string2);
	}

	public boolean containsVariable(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return false;
		} else {
			String string2 = string.trim();
			return this.m_GameVariables.containsKey(string2);
		}
	}
}
