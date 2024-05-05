package zombie.characters.action.conditions;

import org.w3c.dom.Element;
import zombie.characters.action.ActionContext;
import zombie.characters.action.IActionCondition;
import zombie.core.Core;
import zombie.core.skinnedmodel.advancedanimation.IAnimatable;
import zombie.core.skinnedmodel.advancedanimation.IAnimationVariableSource;
import zombie.core.skinnedmodel.advancedanimation.debug.AnimatorDebugMonitor;
import zombie.util.StringUtils;


public final class CharacterVariableCondition implements IActionCondition {
	private CharacterVariableCondition.Operator op;
	private Object lhsValue;
	private Object rhsValue;

	private static Object parseValue(String string, boolean boolean1) {
		if (string.length() <= 0) {
			return string;
		} else {
			char char1 = string.charAt(0);
			int int1;
			char char2;
			if (char1 == '-' || char1 == '+' || char1 >= '0' && char1 <= '9') {
				int int2 = 0;
				if (char1 >= '0' && char1 <= '9') {
					int2 = char1 - 48;
				}

				for (int1 = 1; int1 < string.length(); ++int1) {
					char2 = string.charAt(int1);
					if (char2 >= '0' && char2 <= '9') {
						int2 = int2 * 10 + (char2 - 48);
					} else if (char2 != ',') {
						if (char2 != '.') {
							return string;
						}

						++int1;
						break;
					}
				}

				if (int1 == string.length()) {
					return int2;
				} else {
					float float1 = (float)int2;
					for (float float2 = 10.0F; int1 < string.length(); ++int1) {
						char char3 = string.charAt(int1);
						if (char3 >= '0' && char3 <= '9') {
							float1 += (float)(char3 - 48) / float2;
							float2 *= 10.0F;
						} else if (char3 != ',') {
							return string;
						}
					}

					if (char1 == '-') {
						float1 *= -1.0F;
					}

					return float1;
				}
			} else if (!string.equalsIgnoreCase("true") && !string.equalsIgnoreCase("yes")) {
				if (!string.equalsIgnoreCase("false") && !string.equalsIgnoreCase("no")) {
					if (boolean1) {
						if (char1 != '\'' && char1 != '\"') {
							return new CharacterVariableCondition.CharacterVariableLookup(string);
						} else {
							StringBuilder stringBuilder = new StringBuilder(string.length() - 2);
							for (int1 = 1; int1 < string.length(); ++int1) {
								char2 = string.charAt(int1);
								switch (char2) {
								case '\"': 
								
								case '\'': 
									if (char2 == char1) {
										return stringBuilder.toString();
									}

								
								default: 
									stringBuilder.append(char2);
									break;
								
								case '\\': 
									stringBuilder.append(string.charAt(int1));
								
								}
							}

							return stringBuilder.toString();
						}
					} else {
						return string;
					}
				} else {
					return false;
				}
			} else {
				return true;
			}
		}
	}

	private boolean load(Element element) {
		String string = element.getNodeName();
		byte byte1 = -1;
		switch (string.hashCode()) {
		case -1971989233: 
			if (string.equals("gtrEqual")) {
				byte1 = 8;
			}

			break;
		
		case -1295482945: 
			if (string.equals("equals")) {
				byte1 = 5;
			}

			break;
		
		case -1180085800: 
			if (string.equals("isTrue")) {
				byte1 = 0;
			}

			break;
		
		case 102693: 
			if (string.equals("gtr")) {
				byte1 = 3;
			}

			break;
		
		case 3318169: 
			if (string.equals("less")) {
				byte1 = 4;
			}

			break;
		
		case 341896475: 
			if (string.equals("lessEqual")) {
				byte1 = 7;
			}

			break;
		
		case 881486962: 
			if (string.equals("notEquals")) {
				byte1 = 6;
			}

			break;
		
		case 950484197: 
			if (string.equals("compare")) {
				byte1 = 2;
			}

			break;
		
		case 2058602009: 
			if (string.equals("isFalse")) {
				byte1 = 1;
			}

		
		}
		switch (byte1) {
		case 0: 
			this.op = CharacterVariableCondition.Operator.Equal;
			this.lhsValue = new CharacterVariableCondition.CharacterVariableLookup(element.getTextContent().trim());
			this.rhsValue = true;
			return true;
		
		case 1: 
			this.op = CharacterVariableCondition.Operator.Equal;
			this.lhsValue = new CharacterVariableCondition.CharacterVariableLookup(element.getTextContent().trim());
			this.rhsValue = false;
			return true;
		
		case 2: 
			String string2 = element.getAttribute("op").trim();
			byte byte2 = -1;
			switch (string2.hashCode()) {
			case 60: 
				if (string2.equals("<")) {
					byte2 = 5;
				}

				break;
			
			case 61: 
				if (string2.equals("=")) {
					byte2 = 1;
				}

				break;
			
			case 62: 
				if (string2.equals(">")) {
					byte2 = 6;
				}

				break;
			
			case 1084: 
				if (string2.equals("!=")) {
					byte2 = 3;
				}

				break;
			
			case 1921: 
				if (string2.equals("<=")) {
					byte2 = 7;
				}

				break;
			
			case 1922: 
				if (string2.equals("<>")) {
					byte2 = 4;
				}

				break;
			
			case 1952: 
				if (string2.equals("==")) {
					byte2 = 2;
				}

				break;
			
			case 1983: 
				if (string2.equals(">=")) {
					byte2 = 8;
				}

			
			}

			switch (byte2) {
			case 1: 
			
			case 2: 
				this.op = CharacterVariableCondition.Operator.Equal;
				break;
			
			case 3: 
			
			case 4: 
				this.op = CharacterVariableCondition.Operator.NotEqual;
				break;
			
			case 5: 
				this.op = CharacterVariableCondition.Operator.Less;
				break;
			
			case 6: 
				this.op = CharacterVariableCondition.Operator.Greater;
				break;
			
			case 7: 
				this.op = CharacterVariableCondition.Operator.LessEqual;
				break;
			
			case 8: 
				this.op = CharacterVariableCondition.Operator.GreaterEqual;
				break;
			
			default: 
				return false;
			
			}

			this.loadCompareValues(element);
			return true;
		
		case 3: 
			this.op = CharacterVariableCondition.Operator.Greater;
			this.loadCompareValues(element);
			return true;
		
		case 4: 
			this.op = CharacterVariableCondition.Operator.Less;
			this.loadCompareValues(element);
			return true;
		
		case 5: 
			this.op = CharacterVariableCondition.Operator.Equal;
			this.loadCompareValues(element);
			return true;
		
		case 6: 
			this.op = CharacterVariableCondition.Operator.NotEqual;
			this.loadCompareValues(element);
			return true;
		
		case 7: 
			this.op = CharacterVariableCondition.Operator.LessEqual;
			this.loadCompareValues(element);
			return true;
		
		case 8: 
			this.op = CharacterVariableCondition.Operator.GreaterEqual;
			this.loadCompareValues(element);
			return true;
		
		default: 
			return false;
		
		}
	}

	private void loadCompareValues(Element element) {
		String string = element.getAttribute("a").trim();
		String string2 = element.getAttribute("b").trim();
		this.lhsValue = parseValue(string, true);
		this.rhsValue = parseValue(string2, false);
	}

	private static Object resolveValue(Object object, IAnimationVariableSource iAnimationVariableSource) {
		if (object instanceof CharacterVariableCondition.CharacterVariableLookup) {
			String string = iAnimationVariableSource.getVariableString(((CharacterVariableCondition.CharacterVariableLookup)object).variableName);
			return string != null ? parseValue(string, false) : null;
		} else {
			return object;
		}
	}

	private boolean resolveCompareTo(int int1) {
		switch (this.op) {
		case Equal: 
			return int1 == 0;
		
		case NotEqual: 
			return int1 != 0;
		
		case Less: 
			return int1 < 0;
		
		case LessEqual: 
			return int1 <= 0;
		
		case Greater: 
			return int1 > 0;
		
		case GreaterEqual: 
			return int1 >= 0;
		
		default: 
			return false;
		
		}
	}

	public boolean passes(ActionContext actionContext, int int1) {
		IAnimatable iAnimatable = actionContext.getOwner();
		Object object = resolveValue(this.lhsValue, iAnimatable);
		Object object2 = resolveValue(this.rhsValue, iAnimatable);
		boolean boolean1;
		if (object == null && object2 instanceof String && StringUtils.isNullOrEmpty((String)object2)) {
			if (this.op == CharacterVariableCondition.Operator.Equal) {
				return true;
			}

			if (this.op == CharacterVariableCondition.Operator.NotEqual) {
				return false;
			}

			boolean1 = true;
		}

		if (object != null && object2 != null) {
			if (object.getClass().equals(object2.getClass())) {
				if (object instanceof String) {
					return this.resolveCompareTo(((String)object).compareTo((String)object2));
				}

				if (object instanceof Integer) {
					return this.resolveCompareTo(((Integer)object).compareTo((Integer)object2));
				}

				if (object instanceof Float) {
					return this.resolveCompareTo(((Float)object).compareTo((Float)object2));
				}

				if (object instanceof Boolean) {
					return this.resolveCompareTo(((Boolean)object).compareTo((Boolean)object2));
				}
			}

			boolean1 = object instanceof Integer;
			boolean boolean2 = object instanceof Float;
			boolean boolean3 = object2 instanceof Integer;
			boolean boolean4 = object2 instanceof Float;
			if ((boolean1 || boolean2) && (boolean3 || boolean4)) {
				boolean boolean5 = this.lhsValue instanceof CharacterVariableCondition.CharacterVariableLookup;
				boolean boolean6 = this.rhsValue instanceof CharacterVariableCondition.CharacterVariableLookup;
				float float1;
				float float2;
				if (boolean5 == boolean6) {
					float1 = boolean2 ? (Float)object : (float)(Integer)object;
					float2 = boolean4 ? (Float)object2 : (float)(Integer)object2;
					return this.resolveCompareTo(Float.compare(float1, float2));
				} else {
					int int2;
					int int3;
					if (boolean5) {
						if (boolean4) {
							float1 = boolean2 ? (Float)object : (float)(Integer)object;
							float2 = (Float)object2;
							return this.resolveCompareTo(Float.compare(float1, float2));
						} else {
							int2 = boolean2 ? (int)(Float)object : (Integer)object;
							int3 = (Integer)object2;
							return this.resolveCompareTo(Integer.compare(int2, int3));
						}
					} else if (boolean2) {
						float1 = (Float)object;
						float2 = boolean4 ? (Float)object2 : (float)(Integer)object2;
						return this.resolveCompareTo(Float.compare(float1, float2));
					} else {
						int2 = (Integer)object;
						int3 = boolean4 ? (int)(Float)object2 : (Integer)object2;
						return this.resolveCompareTo(Integer.compare(int2, int3));
					}
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public IActionCondition clone() {
		return this;
	}

	private static String getOpString(CharacterVariableCondition.Operator operator) {
		switch (operator) {
		case Equal: 
			return " == ";
		
		case NotEqual: 
			return " != ";
		
		case Less: 
			return " < ";
		
		case LessEqual: 
			return " <= ";
		
		case Greater: 
			return " > ";
		
		case GreaterEqual: 
			return " >=";
		
		default: 
			return " ?? ";
		
		}
	}

	private static String valueToString(Object object) {
		return object instanceof String ? "\"" + (String)object + "\"" : object.toString();
	}

	public String getDescription() {
		String string = valueToString(this.lhsValue);
		return string + getOpString(this.op) + valueToString(this.rhsValue);
	}

	private static class CharacterVariableLookup {
		public String variableName;

		public CharacterVariableLookup(String string) {
			this.variableName = string;
			if (Core.bDebug) {
				AnimatorDebugMonitor.registerVariable(string);
			}
		}

		public String toString() {
			return this.variableName;
		}
	}

	static enum Operator {

		Equal,
		NotEqual,
		Less,
		Greater,
		LessEqual,
		GreaterEqual;

		private static CharacterVariableCondition.Operator[] $values() {
			return new CharacterVariableCondition.Operator[]{Equal, NotEqual, Less, Greater, LessEqual, GreaterEqual};
		}
	}

	public static class Factory implements IActionCondition.IFactory {
		public IActionCondition create(Element element) {
			CharacterVariableCondition characterVariableCondition = new CharacterVariableCondition();
			return characterVariableCondition.load(element) ? characterVariableCondition : null;
		}
	}
}
