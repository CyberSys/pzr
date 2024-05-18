package se.krka.kahlua.stdlib;

import se.krka.kahlua.vm.JavaFunction;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaThread;
import se.krka.kahlua.vm.KahluaUtil;
import se.krka.kahlua.vm.LuaCallFrame;
import se.krka.kahlua.vm.LuaClosure;
import se.krka.kahlua.vm.Platform;
import zombie.Lua.LuaManager;


public final class StringLib implements JavaFunction {
	private static final int SUB = 0;
	private static final int CHAR = 1;
	private static final int BYTE = 2;
	private static final int LOWER = 3;
	private static final int UPPER = 4;
	private static final int REVERSE = 5;
	private static final int FORMAT = 6;
	private static final int FIND = 7;
	private static final int MATCH = 8;
	private static final int GSUB = 9;
	private static final int TRIM = 10;
	private static final int SPLIT = 11;
	private static final int SORT = 12;
	private static final int CONTAINS = 13;
	private static final int NUM_FUNCTIONS = 14;
	private static final boolean[] SPECIALS = new boolean[256];
	private static final int LUA_MAXCAPTURES = 32;
	private static final char L_ESC = '%';
	private static final int CAP_UNFINISHED = -1;
	private static final int CAP_POSITION = -2;
	private static final String[] names;
	private static final StringLib[] functions;
	private static final Class STRING_CLASS;
	private final int methodId;
	private static final char[] digits;

	public StringLib(int int1) {
		this.methodId = int1;
	}

	public static void register(Platform platform, KahluaTable kahluaTable) {
		KahluaTable kahluaTable2 = platform.newTable();
		for (int int1 = 0; int1 < 14; ++int1) {
			kahluaTable2.rawset(names[int1], functions[int1]);
		}

		kahluaTable2.rawset("__index", kahluaTable2);
		KahluaTable kahluaTable3 = KahluaUtil.getClassMetatables(platform, kahluaTable);
		kahluaTable3.rawset(STRING_CLASS, kahluaTable2);
		kahluaTable.rawset("string", kahluaTable2);
	}

	public String toString() {
		return names[this.methodId];
	}

	public int call(LuaCallFrame luaCallFrame, int int1) {
		switch (this.methodId) {
		case 0: 
			return this.sub(luaCallFrame, int1);
		
		case 1: 
			return this.stringChar(luaCallFrame, int1);
		
		case 2: 
			return this.stringByte(luaCallFrame, int1);
		
		case 3: 
			return this.lower(luaCallFrame, int1);
		
		case 4: 
			return this.upper(luaCallFrame, int1);
		
		case 5: 
			return this.reverse(luaCallFrame, int1);
		
		case 6: 
			return this.format(luaCallFrame, int1);
		
		case 7: 
			return findAux(luaCallFrame, true);
		
		case 8: 
			return findAux(luaCallFrame, false);
		
		case 9: 
			return gsub(luaCallFrame, int1);
		
		case 10: 
			return trim(luaCallFrame, int1);
		
		case 11: 
			return split(luaCallFrame, int1);
		
		case 12: 
			return sort(luaCallFrame, int1);
		
		case 13: 
			return this.contains(luaCallFrame, int1);
		
		default: 
			return 0;
		
		}
	}

	private long unsigned(long long1) {
		if (long1 < 0L) {
			long1 += 4294967296L;
		}

		return long1;
	}

	private int format(LuaCallFrame luaCallFrame, int int1) {
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[6]);
		int int2 = string.length();
		int int3 = 2;
		StringBuffer stringBuffer = new StringBuffer();
		label327: for (int int4 = 0; int4 < int2; ++int4) {
			char char1 = string.charAt(int4);
			if (char1 == '%') {
				++int4;
				KahluaUtil.luaAssert(int4 < int2, "incomplete option to \'format\'");
				char1 = string.charAt(int4);
				if (char1 == '%') {
					stringBuffer.append('%');
				} else {
					boolean boolean1 = false;
					boolean boolean2 = false;
					boolean boolean3 = false;
					boolean boolean4 = false;
					boolean boolean5 = false;
					while (true) {
						switch (char1) {
						case ' ': 
							boolean5 = true;
							break;
						
						case '#': 
							boolean1 = true;
							break;
						
						case '+': 
							boolean4 = true;
							break;
						
						case '-': 
							boolean3 = true;
							break;
						
						case '0': 
							boolean2 = true;
							break;
						
						default: 
							int int5;
							for (int5 = 0; char1 >= '0' && char1 <= '9'; char1 = string.charAt(int4)) {
								int5 = 10 * int5 + char1 - 48;
								++int4;
								KahluaUtil.luaAssert(int4 < int2, "incomplete option to \'format\'");
							}

							int int6 = 0;
							boolean boolean6 = false;
							if (char1 == '.') {
								boolean6 = true;
								++int4;
								KahluaUtil.luaAssert(int4 < int2, "incomplete option to \'format\'");
								for (char1 = string.charAt(int4); char1 >= '0' && char1 <= '9'; char1 = string.charAt(int4)) {
									int6 = 10 * int6 + char1 - 48;
									++int4;
									KahluaUtil.luaAssert(int4 < int2, "incomplete option to \'format\'");
								}
							}

							if (boolean3) {
								boolean2 = false;
							}

							byte byte1 = 10;
							boolean boolean7 = false;
							byte byte2 = 6;
							String string2 = "";
							switch (char1) {
							case 'E': 
								boolean7 = true;
								break;
							
							case 'F': 
							
							case 'H': 
							
							case 'I': 
							
							case 'J': 
							
							case 'K': 
							
							case 'L': 
							
							case 'M': 
							
							case 'N': 
							
							case 'O': 
							
							case 'P': 
							
							case 'Q': 
							
							case 'R': 
							
							case 'S': 
							
							case 'T': 
							
							case 'U': 
							
							case 'V': 
							
							case 'W': 
							
							case 'Y': 
							
							case 'Z': 
							
							case '[': 
							
							case '\\': 
							
							case ']': 
							
							case '^': 
							
							case '_': 
							
							case '`': 
							
							case 'a': 
							
							case 'b': 
							
							case 'h': 
							
							case 'j': 
							
							case 'k': 
							
							case 'l': 
							
							case 'm': 
							
							case 'n': 
							
							case 'p': 
							
							case 'r': 
							
							case 't': 
							
							case 'v': 
							
							case 'w': 
							
							default: 
								throw new RuntimeException("invalid option \'%" + char1 + "\' to \'format\'");
							
							case 'G': 
								boolean7 = true;
								break;
							
							case 'X': 
								byte1 = 16;
								byte2 = 1;
								boolean7 = true;
								string2 = "0X";
								break;
							
							case 'c': 
								boolean2 = false;
								break;
							
							case 'd': 
							
							case 'i': 
								byte2 = 1;
							
							case 'e': 
							
							case 'f': 
							
							case 'g': 
								break;
							
							case 'o': 
								byte1 = 8;
								byte2 = 1;
								string2 = "0";
								break;
							
							case 'q': 
								int5 = 0;
								break;
							
							case 's': 
								boolean2 = false;
								break;
							
							case 'u': 
								byte2 = 1;
								break;
							
							case 'x': 
								byte1 = 16;
								byte2 = 1;
								string2 = "0x";
							
							}

							if (!boolean6) {
								int6 = byte2;
							}

							if (boolean6 && byte1 != 10) {
								boolean2 = false;
							}

							int int7 = boolean2 ? 48 : 32;
							int int8 = stringBuffer.length();
							if (!boolean3) {
								this.extend(stringBuffer, int5, (char)int7);
							}

							String string3;
							int int9;
							Double Double1;
							int int10;
							double double1;
							boolean boolean8;
							switch (char1) {
							case 'E': 
							
							case 'e': 
							
							case 'f': 
								Double1 = this.getDoubleArg(luaCallFrame, int3);
								boolean8 = Double1.isInfinite() || Double1.isNaN();
								double1 = Double1;
								if (KahluaUtil.isNegative(double1)) {
									if (!boolean8) {
										stringBuffer.append('-');
									}

									double1 = -double1;
								} else if (boolean4) {
									stringBuffer.append('+');
								} else if (boolean5) {
									stringBuffer.append(' ');
								}

								if (boolean8) {
									stringBuffer.append(KahluaUtil.numberToString(Double1));
								} else if (char1 == 'f') {
									this.appendPrecisionNumber(stringBuffer, double1, int6, boolean1);
								} else {
									this.appendScientificNumber(stringBuffer, double1, int6, boolean1, false);
								}

								break;
							
							case 'F': 
							
							case 'H': 
							
							case 'I': 
							
							case 'J': 
							
							case 'K': 
							
							case 'L': 
							
							case 'M': 
							
							case 'N': 
							
							case 'O': 
							
							case 'P': 
							
							case 'Q': 
							
							case 'R': 
							
							case 'S': 
							
							case 'T': 
							
							case 'U': 
							
							case 'V': 
							
							case 'W': 
							
							case 'Y': 
							
							case 'Z': 
							
							case '[': 
							
							case '\\': 
							
							case ']': 
							
							case '^': 
							
							case '_': 
							
							case '`': 
							
							case 'a': 
							
							case 'b': 
							
							case 'h': 
							
							case 'j': 
							
							case 'k': 
							
							case 'l': 
							
							case 'm': 
							
							case 'n': 
							
							case 'p': 
							
							case 'r': 
							
							case 't': 
							
							case 'v': 
							
							case 'w': 
							
							default: 
								throw new RuntimeException("Internal error");
							
							case 'G': 
							
							case 'g': 
								if (int6 <= 0) {
									int6 = 1;
								}

								Double1 = this.getDoubleArg(luaCallFrame, int3);
								boolean8 = Double1.isInfinite() || Double1.isNaN();
								double1 = Double1;
								if (KahluaUtil.isNegative(double1)) {
									if (!boolean8) {
										stringBuffer.append('-');
									}

									double1 = -double1;
								} else if (boolean4) {
									stringBuffer.append('+');
								} else if (boolean5) {
									stringBuffer.append(' ');
								}

								if (boolean8) {
									stringBuffer.append(KahluaUtil.numberToString(Double1));
								} else {
									double double2 = roundToSignificantNumbers(double1, int6);
									if (double2 != 0.0 && (!(double2 >= 1.0E-4) || !(double2 < (double)KahluaUtil.ipow(10L, int6)))) {
										this.appendScientificNumber(stringBuffer, double2, int6 - 1, boolean1, true);
										break;
									}

									int int11;
									if (double2 == 0.0) {
										int11 = 1;
									} else if (Math.floor(double2) == 0.0) {
										int11 = 0;
									} else {
										double double3 = double2;
										for (int11 = 1; double3 >= 10.0; ++int11) {
											double3 /= 10.0;
										}
									}

									this.appendSignificantNumber(stringBuffer, double2, int6 - int11, boolean1);
								}

								break;
							
							case 'X': 
							
							case 'o': 
							
							case 'u': 
							
							case 'x': 
								long long1 = this.getDoubleArg(luaCallFrame, int3).longValue();
								long1 = this.unsigned(long1);
								if (boolean1) {
									if (byte1 == 8) {
										int10 = 0;
										for (long long2 = long1; long2 > 0L; ++int10) {
											long2 /= 8L;
										}

										if (int6 <= int10) {
											stringBuffer.append(string2);
										}
									} else if (byte1 == 16 && long1 != 0L) {
										stringBuffer.append(string2);
									}
								}

								if (long1 != 0L || int6 > 0) {
									stringBufferAppend(stringBuffer, (double)long1, byte1, false, int6);
								}

								break;
							
							case 'c': 
								stringBuffer.append((char)this.getDoubleArg(luaCallFrame, int3).shortValue());
								break;
							
							case 'd': 
							
							case 'i': 
								Double1 = this.getDoubleArg(luaCallFrame, int3);
								long long3 = Double1.longValue();
								if (long3 < 0L) {
									stringBuffer.append('-');
									long3 = -long3;
								} else if (boolean4) {
									stringBuffer.append('+');
								} else if (boolean5) {
									stringBuffer.append(' ');
								}

								if (long3 != 0L || int6 > 0) {
									stringBufferAppend(stringBuffer, (double)long3, byte1, false, int6);
								}

								break;
							
							case 'q': 
								string3 = this.getStringArg(luaCallFrame, int3);
								stringBuffer.append('\"');
								for (int9 = 0; int9 < string3.length(); ++int9) {
									char char2 = string3.charAt(int9);
									switch (char2) {
									case '\n': 
										stringBuffer.append("\\\n");
										break;
									
									case '\r': 
										stringBuffer.append("\\r");
										break;
									
									case '\"': 
										stringBuffer.append("\\\"");
										break;
									
									case '\\': 
										stringBuffer.append("\\");
										break;
									
									default: 
										stringBuffer.append(char2);
									
									}
								}

								stringBuffer.append('\"');
								break;
							
							case 's': 
								string3 = this.getStringArg(luaCallFrame, int3);
								int9 = string3.length();
								if (boolean6) {
									int9 = Math.min(int6, string3.length());
								}

								this.append(stringBuffer, string3, 0, int9);
							
							}

							int int12;
							if (boolean3) {
								int12 = stringBuffer.length();
								int9 = int5 - (int12 - int8);
								if (int9 > 0) {
									this.extend(stringBuffer, int9, ' ');
								}
							} else {
								int12 = stringBuffer.length();
								int9 = int12 - int8 - int5;
								int9 = Math.min(int9, int5);
								if (int9 > 0) {
									stringBuffer.delete(int8, int8 + int9);
								}

								if (boolean2) {
									int10 = int8 + (int5 - int9);
									char char3 = stringBuffer.charAt(int10);
									if (char3 == '+' || char3 == '-' || char3 == ' ') {
										stringBuffer.setCharAt(int10, '0');
										stringBuffer.setCharAt(int8, char3);
									}
								}
							}

							if (boolean7) {
								this.stringBufferUpperCase(stringBuffer, int8);
							}

							++int3;
							continue label327;
						
						}

						++int4;
						KahluaUtil.luaAssert(int4 < int2, "incomplete option to \'format\'");
						char1 = string.charAt(int4);
					}
				}
			} else {
				stringBuffer.append(char1);
			}
		}
		luaCallFrame.push(stringBuffer.toString());
		return 1;
	}

	private void append(StringBuffer stringBuffer, String string, int int1, int int2) {
		for (int int3 = int1; int3 < int2; ++int3) {
			stringBuffer.append(string.charAt(int3));
		}
	}

	private void extend(StringBuffer stringBuffer, int int1, char char1) {
		int int2 = stringBuffer.length();
		stringBuffer.setLength(int2 + int1);
		for (int int3 = int1 - 1; int3 >= 0; --int3) {
			stringBuffer.setCharAt(int2 + int3, char1);
		}
	}

	private void stringBufferUpperCase(StringBuffer stringBuffer, int int1) {
		int int2 = stringBuffer.length();
		for (int int3 = int1; int3 < int2; ++int3) {
			char char1 = stringBuffer.charAt(int3);
			if (char1 >= 'a' && char1 <= 'z') {
				stringBuffer.setCharAt(int3, (char)(char1 - 32));
			}
		}
	}

	private static void stringBufferAppend(StringBuffer stringBuffer, double double1, int int1, boolean boolean1, int int2) {
		int int3;
		for (int3 = stringBuffer.length(); double1 > 0.0 || int2 > 0; --int2) {
			double double2 = Math.floor(double1 / (double)int1);
			stringBuffer.append(digits[(int)(double1 - double2 * (double)int1)]);
			double1 = double2;
		}

		int int4 = stringBuffer.length() - 1;
		if (int3 > int4 && boolean1) {
			stringBuffer.append('0');
		} else {
			int int5 = (1 + int4 - int3) / 2;
			for (int int6 = int5 - 1; int6 >= 0; --int6) {
				int int7 = int3 + int6;
				int int8 = int4 - int6;
				char char1 = stringBuffer.charAt(int7);
				char char2 = stringBuffer.charAt(int8);
				stringBuffer.setCharAt(int7, char2);
				stringBuffer.setCharAt(int8, char1);
			}
		}
	}

	private void appendPrecisionNumber(StringBuffer stringBuffer, double double1, int int1, boolean boolean1) {
		double1 = roundToPrecision(double1, int1);
		double double2 = Math.floor(double1);
		double double3 = double1 - double2;
		for (int int2 = 0; int2 < int1; ++int2) {
			double3 *= 10.0;
		}

		double3 = KahluaUtil.round(double2 + double3) - double2;
		stringBufferAppend(stringBuffer, double2, 10, true, 0);
		if (boolean1 || int1 > 0) {
			stringBuffer.append('.');
		}

		stringBufferAppend(stringBuffer, double3, 10, false, int1);
	}

	private void appendSignificantNumber(StringBuffer stringBuffer, double double1, int int1, boolean boolean1) {
		double double2 = Math.floor(double1);
		stringBufferAppend(stringBuffer, double2, 10, true, 0);
		double double3 = roundToSignificantNumbers(double1 - double2, int1);
		boolean boolean2 = double2 == 0.0 && double3 != 0.0;
		int int2 = 0;
		int int3 = int1;
		int int4;
		for (int4 = 0; int4 < int3; ++int4) {
			double3 *= 10.0;
			if (Math.floor(double3) == 0.0 && double3 != 0.0) {
				++int2;
				if (boolean2) {
					++int3;
				}
			}
		}

		double3 = KahluaUtil.round(double3);
		if (!boolean1) {
			while (double3 > 0.0 && double3 % 10.0 == 0.0) {
				double3 /= 10.0;
				--int1;
			}
		}

		stringBuffer.append('.');
		int4 = stringBuffer.length();
		this.extend(stringBuffer, int2, '0');
		int int5 = stringBuffer.length();
		stringBufferAppend(stringBuffer, double3, 10, false, 0);
		int int6 = stringBuffer.length();
		int int7 = int6 - int5;
		if (boolean1 && int7 < int1) {
			int int8 = int1 - int7 - int2;
			this.extend(stringBuffer, int8, '0');
		}

		if (!boolean1 && int4 == stringBuffer.length()) {
			stringBuffer.delete(int4 - 1, stringBuffer.length());
		}
	}

	private void appendScientificNumber(StringBuffer stringBuffer, double double1, int int1, boolean boolean1, boolean boolean2) {
		int int2 = 0;
		int int3;
		for (int3 = 0; int3 < 2; ++int3) {
			if (double1 >= 1.0) {
				while (double1 >= 10.0) {
					double1 /= 10.0;
					++int2;
				}
			} else {
				while (double1 > 0.0 && double1 < 1.0) {
					double1 *= 10.0;
					--int2;
				}
			}

			double1 = roundToPrecision(double1, int1);
		}

		int3 = Math.abs(int2);
		char char1;
		if (int2 >= 0) {
			char1 = '+';
		} else {
			char1 = '-';
		}

		if (boolean2) {
			this.appendSignificantNumber(stringBuffer, double1, int1, boolean1);
		} else {
			this.appendPrecisionNumber(stringBuffer, double1, int1, boolean1);
		}

		stringBuffer.append('e');
		stringBuffer.append(char1);
		stringBufferAppend(stringBuffer, (double)int3, 10, true, 2);
	}

	private String getStringArg(LuaCallFrame luaCallFrame, int int1) {
		return this.getStringArg(luaCallFrame, int1, names[6]);
	}

	private String getStringArg(LuaCallFrame luaCallFrame, int int1, String string) {
		return KahluaUtil.getStringArg(luaCallFrame, int1, string);
	}

	private Double getDoubleArg(LuaCallFrame luaCallFrame, int int1) {
		return this.getDoubleArg(luaCallFrame, int1, names[6]);
	}

	private Double getDoubleArg(LuaCallFrame luaCallFrame, int int1, String string) {
		return KahluaUtil.getNumberArg(luaCallFrame, int1, string);
	}

	private int lower(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "not enough arguments");
		String string = this.getStringArg(luaCallFrame, 1, names[3]);
		luaCallFrame.push(string.toLowerCase());
		return 1;
	}

	private int upper(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "not enough arguments");
		String string = this.getStringArg(luaCallFrame, 1, names[4]);
		luaCallFrame.push(string.toUpperCase());
		return 1;
	}

	private int contains(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 2, "not enough arguments");
		String string = this.getStringArg(luaCallFrame, 1, names[13]);
		String string2 = this.getStringArg(luaCallFrame, 2, names[13]);
		luaCallFrame.push(string.contains(string2));
		return 1;
	}

	private int reverse(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "not enough arguments");
		String string = this.getStringArg(luaCallFrame, 1, names[5]);
		string = (new StringBuffer(string)).reverse().toString();
		luaCallFrame.push(string);
		return 1;
	}

	private int stringByte(LuaCallFrame luaCallFrame, int int1) {
		KahluaUtil.luaAssert(int1 >= 1, "not enough arguments");
		String string = this.getStringArg(luaCallFrame, 1, names[2]);
		int int2 = this.nullDefault(1, KahluaUtil.getOptionalNumberArg(luaCallFrame, 2));
		int int3 = this.nullDefault(int2, KahluaUtil.getOptionalNumberArg(luaCallFrame, 3));
		int int4 = string.length();
		if (int2 < 0) {
			int2 += int4 + 1;
		}

		if (int2 <= 0) {
			int2 = 1;
		}

		if (int3 < 0) {
			int3 += int4 + 1;
		} else if (int3 > int4) {
			int3 = int4;
		}

		int int5 = 1 + int3 - int2;
		if (int5 <= 0) {
			return 0;
		} else {
			luaCallFrame.setTop(int5);
			int int6 = int2 - 1;
			for (int int7 = 0; int7 < int5; ++int7) {
				char char1 = string.charAt(int6 + int7);
				luaCallFrame.set(int7, KahluaUtil.toDouble((long)char1));
			}

			return int5;
		}
	}

	private int nullDefault(int int1, Double Double1) {
		return Double1 == null ? int1 : Double1.intValue();
	}

	private int stringChar(LuaCallFrame luaCallFrame, int int1) {
		StringBuffer stringBuffer = new StringBuffer();
		for (int int2 = 0; int2 < int1; ++int2) {
			int int3 = this.getDoubleArg(luaCallFrame, int2 + 1, names[1]).intValue();
			stringBuffer.append((char)int3);
		}

		return luaCallFrame.push(stringBuffer.toString());
	}

	private int sub(LuaCallFrame luaCallFrame, int int1) {
		String string = this.getStringArg(luaCallFrame, 1, names[0]);
		double double1 = this.getDoubleArg(luaCallFrame, 2, names[0]);
		double double2 = -1.0;
		if (int1 >= 3) {
			double2 = this.getDoubleArg(luaCallFrame, 3, names[0]);
		}

		int int2 = (int)double1;
		int int3 = (int)double2;
		int int4 = string.length();
		if (int2 < 0) {
			int2 = Math.max(int4 + int2 + 1, 1);
		} else if (int2 == 0) {
			int2 = 1;
		}

		if (int3 < 0) {
			int3 = Math.max(0, int3 + int4 + 1);
		} else if (int3 > int4) {
			int3 = int4;
		}

		if (int2 > int3) {
			return luaCallFrame.push("");
		} else {
			String string2 = string.substring(int2 - 1, int3);
			return luaCallFrame.push(string2);
		}
	}

	public static double roundToPrecision(double double1, int int1) {
		double double2 = (double)KahluaUtil.ipow(10L, int1);
		return KahluaUtil.round(double1 * double2) / double2;
	}

	public static double roundToSignificantNumbers(double double1, int int1) {
		if (double1 == 0.0) {
			return 0.0;
		} else if (double1 < 0.0) {
			return -roundToSignificantNumbers(-double1, int1);
		} else {
			double double2 = (double)KahluaUtil.ipow(10L, int1 - 1);
			double double3 = double2 * 10.0;
			double double4;
			for (double4 = 1.0; double4 * double1 < double2; double4 *= 10.0) {
			}

			while (double4 * double1 >= double3) {
				double4 /= 10.0;
			}

			return KahluaUtil.round(double1 * double4) / double4;
		}
	}

	private static Object push_onecapture(StringLib.MatchState matchState, int int1, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		if (int1 >= matchState.level) {
			if (int1 == 0) {
				String string = stringPointer.string.substring(stringPointer.index, stringPointer2.index);
				matchState.callFrame.push(string);
				return string;
			} else {
				throw new RuntimeException("invalid capture index");
			}
		} else {
			int int2 = matchState.capture[int1].len;
			if (int2 == -1) {
				throw new RuntimeException("unfinished capture");
			} else if (int2 == -2) {
				Double Double1 = new Double((double)(matchState.src_init.length() - matchState.capture[int1].init.length() + 1));
				matchState.callFrame.push(Double1);
				return Double1;
			} else {
				int int3 = matchState.capture[int1].init.index;
				String string2 = matchState.capture[int1].init.string.substring(int3, int3 + int2);
				matchState.callFrame.push(string2);
				return string2;
			}
		}
	}

	private static int push_captures(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		int int1 = matchState.level == 0 && stringPointer != null ? 1 : matchState.level;
		KahluaUtil.luaAssert(int1 <= 32, "too many captures");
		for (int int2 = 0; int2 < int1; ++int2) {
			push_onecapture(matchState, int2, stringPointer, stringPointer2);
		}

		return int1;
	}

	private static boolean noSpecialChars(String string) {
		for (int int1 = 0; int1 < string.length(); ++int1) {
			char char1 = string.charAt(int1);
			if (char1 < 256 && SPECIALS[char1]) {
				return false;
			}
		}

		return true;
	}

	private static int findAux(LuaCallFrame luaCallFrame, boolean boolean1) {
		String string = boolean1 ? names[7] : names[8];
		String string2 = KahluaUtil.getStringArg(luaCallFrame, 1, string);
		String string3 = KahluaUtil.getStringArg(luaCallFrame, 2, string);
		Double Double1 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 3);
		boolean boolean2 = KahluaUtil.boolEval(KahluaUtil.getOptionalArg(luaCallFrame, 4));
		int int1 = Double1 == null ? 0 : Double1.intValue() - 1;
		if (int1 < 0) {
			int1 += string2.length();
			if (int1 < 0) {
				int1 = 0;
			}
		} else if (int1 > string2.length()) {
			int1 = string2.length();
		}

		if (boolean1 && (boolean2 || noSpecialChars(string3))) {
			int int2 = string2.indexOf(string3, int1);
			if (int2 > -1) {
				return luaCallFrame.push(KahluaUtil.toDouble((long)(int2 + 1)), KahluaUtil.toDouble((long)(int2 + string3.length())));
			}
		} else {
			StringLib.StringPointer stringPointer = new StringLib.StringPointer(string2);
			StringLib.StringPointer stringPointer2 = new StringLib.StringPointer(string3);
			boolean boolean3 = false;
			if (stringPointer2.getChar() == '^') {
				boolean3 = true;
				stringPointer2.postIncrString(1);
			}

			StringLib.StringPointer stringPointer3 = stringPointer.getClone();
			stringPointer3.postIncrString(int1);
			StringLib.MatchState matchState = new StringLib.MatchState(luaCallFrame, stringPointer.getClone(), stringPointer.getStringLength());
			do {
				matchState.level = 0;
				StringLib.StringPointer stringPointer4;
				if ((stringPointer4 = match(matchState, stringPointer3, stringPointer2)) != null) {
					if (boolean1) {
						return luaCallFrame.push(new Double((double)(stringPointer.length() - stringPointer3.length() + 1)), new Double((double)(stringPointer.length() - stringPointer4.length()))) + push_captures(matchState, (StringLib.StringPointer)null, (StringLib.StringPointer)null);
					}

					return push_captures(matchState, stringPointer3, stringPointer4);
				}
			}	 while (stringPointer3.postIncrStringI(1) < matchState.endIndex && !boolean3);
		}

		return luaCallFrame.pushNil();
	}

	private static StringLib.StringPointer startCapture(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2, int int1) {
		int int2 = matchState.level;
		KahluaUtil.luaAssert(int2 < 32, "too many captures");
		matchState.capture[int2].init = stringPointer.getClone();
		matchState.capture[int2].init.setIndex(stringPointer.getIndex());
		matchState.capture[int2].len = int1;
		matchState.level = int2 + 1;
		StringLib.StringPointer stringPointer3;
		if ((stringPointer3 = match(matchState, stringPointer, stringPointer2)) == null) {
			--matchState.level;
		}

		return stringPointer3;
	}

	private static int captureToClose(StringLib.MatchState matchState) {
		int int1 = matchState.level;
		--int1;
		while (int1 >= 0) {
			if (matchState.capture[int1].len == -1) {
				return int1;
			}

			--int1;
		}

		throw new RuntimeException("invalid pattern capture");
	}

	private static StringLib.StringPointer endCapture(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		int int1 = captureToClose(matchState);
		matchState.capture[int1].len = matchState.capture[int1].init.length() - stringPointer.length();
		StringLib.StringPointer stringPointer3;
		if ((stringPointer3 = match(matchState, stringPointer, stringPointer2)) == null) {
			matchState.capture[int1].len = -1;
		}

		return stringPointer3;
	}

	private static int checkCapture(StringLib.MatchState matchState, int int1) {
		int1 -= 49;
		KahluaUtil.luaAssert(int1 < 0 || int1 >= matchState.level || matchState.capture[int1].len == -1, "invalid capture index");
		return int1;
	}

	private static StringLib.StringPointer matchCapture(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, int int1) {
		int1 = checkCapture(matchState, int1);
		int int2 = matchState.capture[int1].len;
		if (matchState.endIndex - stringPointer.length() >= int2 && matchState.capture[int1].init.compareTo(stringPointer, int2) == 0) {
			StringLib.StringPointer stringPointer2 = stringPointer.getClone();
			stringPointer2.postIncrString(int2);
			return stringPointer2;
		} else {
			return null;
		}
	}

	private static StringLib.StringPointer matchBalance(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		KahluaUtil.luaAssert(stringPointer2.getChar() != 0 && stringPointer2.getChar(1) != 0, "unbalanced pattern");
		StringLib.StringPointer stringPointer3 = stringPointer.getClone();
		if (stringPointer3.getChar() != stringPointer2.getChar()) {
			return null;
		} else {
			char char1 = stringPointer2.getChar();
			char char2 = stringPointer2.getChar(1);
			int int1 = 1;
			while (stringPointer3.preIncrStringI(1) < matchState.endIndex) {
				if (stringPointer3.getChar() == char2) {
					--int1;
					if (int1 == 0) {
						StringLib.StringPointer stringPointer4 = stringPointer3.getClone();
						stringPointer4.postIncrString(1);
						return stringPointer4;
					}
				} else if (stringPointer3.getChar() == char1) {
					++int1;
				}
			}

			return null;
		}
	}

	private static StringLib.StringPointer classEnd(StringLib.StringPointer stringPointer) {
		StringLib.StringPointer stringPointer2 = stringPointer.getClone();
		switch (stringPointer2.postIncrString(1)) {
		case '%': 
			KahluaUtil.luaAssert(stringPointer2.getChar() != 0, "malformed pattern (ends with \'%\')");
			stringPointer2.postIncrString(1);
			return stringPointer2;
		
		case '[': 
			if (stringPointer2.getChar() == '^') {
				stringPointer2.postIncrString(1);
			}

			do {
				KahluaUtil.luaAssert(stringPointer2.getChar() != 0, "malformed pattern (missing \']\')");
				if (stringPointer2.postIncrString(1) == '%' && stringPointer2.getChar() != 0) {
					stringPointer2.postIncrString(1);
				}
			}	 while (stringPointer2.getChar() != ']');

			stringPointer2.postIncrString(1);
			return stringPointer2;
		
		default: 
			return stringPointer2;
		
		}
	}

	private static boolean singleMatch(char char1, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		switch (stringPointer.getChar()) {
		case '%': 
			return matchClass(stringPointer.getChar(1), char1);
		
		case '.': 
			return true;
		
		case '[': 
			StringLib.StringPointer stringPointer3 = stringPointer2.getClone();
			stringPointer3.postIncrString(-1);
			return matchBracketClass(char1, stringPointer, stringPointer3);
		
		default: 
			return stringPointer.getChar() == char1;
		
		}
	}

	private static StringLib.StringPointer minExpand(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2, StringLib.StringPointer stringPointer3) {
		StringLib.StringPointer stringPointer4 = stringPointer3.getClone();
		StringLib.StringPointer stringPointer5 = stringPointer.getClone();
		stringPointer4.postIncrString(1);
		while (true) {
			StringLib.StringPointer stringPointer6 = match(matchState, stringPointer5, stringPointer4);
			if (stringPointer6 != null) {
				return stringPointer6;
			}

			if (stringPointer5.getIndex() >= matchState.endIndex || !singleMatch(stringPointer5.getChar(), stringPointer2, stringPointer3)) {
				return null;
			}

			stringPointer5.postIncrString(1);
		}
	}

	private static StringLib.StringPointer maxExpand(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2, StringLib.StringPointer stringPointer3) {
		int int1;
		for (int1 = 0; stringPointer.getIndex() + int1 < matchState.endIndex && singleMatch(stringPointer.getChar(int1), stringPointer2, stringPointer3); ++int1) {
		}

		while (int1 >= 0) {
			StringLib.StringPointer stringPointer4 = stringPointer.getClone();
			stringPointer4.postIncrString(int1);
			StringLib.StringPointer stringPointer5 = stringPointer3.getClone();
			stringPointer5.postIncrString(1);
			StringLib.StringPointer stringPointer6 = match(matchState, stringPointer4, stringPointer5);
			if (stringPointer6 != null) {
				return stringPointer6;
			}

			--int1;
		}

		return null;
	}

	private static boolean matchBracketClass(char char1, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		StringLib.StringPointer stringPointer3 = stringPointer.getClone();
		StringLib.StringPointer stringPointer4 = stringPointer2.getClone();
		boolean boolean1 = true;
		if (stringPointer3.getChar(1) == '^') {
			boolean1 = false;
			stringPointer3.postIncrString(1);
		}

		label38: do {
			while (stringPointer3.preIncrStringI(1) < stringPointer4.getIndex()) {
				if (stringPointer3.getChar() == '%') {
					stringPointer3.postIncrString(1);
					continue label38;
				}

				if (stringPointer3.getChar(1) == '-' && stringPointer3.getIndex() + 2 < stringPointer4.getIndex()) {
					stringPointer3.postIncrString(2);
					if (stringPointer3.getChar(-2) <= char1 && char1 <= stringPointer3.getChar()) {
						return boolean1;
					}
				} else if (stringPointer3.getChar() == char1) {
					return boolean1;
				}
			}

			return !boolean1;
		} while (!matchClass(stringPointer3.getChar(), char1));
		return boolean1;
	}

	private static StringLib.StringPointer match(StringLib.MatchState matchState, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		StringLib.StringPointer stringPointer3 = stringPointer.getClone();
		StringLib.StringPointer stringPointer4 = stringPointer2.getClone();
		boolean boolean1 = true;
		boolean boolean2 = false;
		while (boolean1) {
			StringLib.StringPointer stringPointer5;
			StringLib.StringPointer stringPointer6;
			boolean1 = false;
			boolean2 = false;
			label87: switch (stringPointer4.getChar()) {
			case ' ': 
				return stringPointer3;
			
			case '$': 
				if (stringPointer4.getChar(1) == 0) {
					return stringPointer3.getIndex() == matchState.endIndex ? stringPointer3 : null;
				}

			
			default: 
				boolean2 = true;
				break;
			
			case '%': 
				switch (stringPointer4.getChar(1)) {
				case 'b': 
					stringPointer5 = stringPointer4.getClone();
					stringPointer5.postIncrString(2);
					stringPointer3 = matchBalance(matchState, stringPointer3, stringPointer5);
					if (stringPointer3 == null) {
						return null;
					}

					stringPointer4.postIncrString(4);
					boolean1 = true;
					continue;
				
				case 'f': 
					stringPointer4.postIncrString(2);
					KahluaUtil.luaAssert(stringPointer4.getChar() == '[', "missing \'[\' after \'%%f\' in pattern");
					stringPointer5 = classEnd(stringPointer4);
					char char1 = stringPointer3.getIndex() == matchState.src_init.getIndex() ? 0 : stringPointer3.getChar(-1);
					stringPointer6 = stringPointer5.getClone();
					stringPointer6.postIncrString(-1);
					if (!matchBracketClass(char1, stringPointer4, stringPointer6) && matchBracketClass(stringPointer3.getChar(), stringPointer4, stringPointer6)) {
						stringPointer4 = stringPointer5;
						boolean1 = true;
						continue;
					}

					return null;
				
				default: 
					if (Character.isDigit(stringPointer4.getChar(1))) {
						stringPointer3 = matchCapture(matchState, stringPointer3, stringPointer4.getChar(1));
						if (stringPointer3 == null) {
							return null;
						}

						stringPointer4.postIncrString(2);
						boolean1 = true;
						continue;
					}

					boolean2 = true;
					break label87;
				
				}

			
			case '(': 
				stringPointer5 = stringPointer4.getClone();
				if (stringPointer4.getChar(1) == ')') {
					stringPointer5.postIncrString(2);
					return startCapture(matchState, stringPointer3, stringPointer5, -2);
				}

				stringPointer5.postIncrString(1);
				return startCapture(matchState, stringPointer3, stringPointer5, -1);
			
			case ')': 
				stringPointer5 = stringPointer4.getClone();
				stringPointer5.postIncrString(1);
				return endCapture(matchState, stringPointer3, stringPointer5);
			
			}

			if (boolean2) {
				stringPointer5 = classEnd(stringPointer4);
				boolean boolean3 = stringPointer3.getIndex() < matchState.endIndex && singleMatch(stringPointer3.getChar(), stringPointer4, stringPointer5);
				switch (stringPointer5.getChar()) {
				case '*': 
					return maxExpand(matchState, stringPointer3, stringPointer4, stringPointer5);
				
				case '+': 
					stringPointer6 = stringPointer3.getClone();
					stringPointer6.postIncrString(1);
					return boolean3 ? maxExpand(matchState, stringPointer6, stringPointer4, stringPointer5) : null;
				
				case '-': 
					return minExpand(matchState, stringPointer3, stringPointer4, stringPointer5);
				
				case '?': 
					StringLib.StringPointer stringPointer7 = stringPointer3.getClone();
					stringPointer7.postIncrString(1);
					StringLib.StringPointer stringPointer8 = stringPointer5.getClone();
					stringPointer8.postIncrString(1);
					if (boolean3 && (stringPointer6 = match(matchState, stringPointer7, stringPointer8)) != null) {
						return stringPointer6;
					}

					stringPointer4 = stringPointer5;
					stringPointer5.postIncrString(1);
					boolean1 = true;
					break;
				
				default: 
					if (!boolean3) {
						return null;
					}

					stringPointer3.postIncrString(1);
					stringPointer4 = stringPointer5;
					boolean1 = true;
				
				}
			}
		}

		return null;
	}

	private static boolean matchClass(char char1, char char2) {
		char char3 = Character.toLowerCase(char1);
		boolean boolean1;
		switch (char3) {
		case 'a': 
			boolean1 = Character.isLowerCase(char2) || Character.isUpperCase(char2);
			break;
		
		case 'b': 
		
		case 'e': 
		
		case 'f': 
		
		case 'g': 
		
		case 'h': 
		
		case 'i': 
		
		case 'j': 
		
		case 'k': 
		
		case 'm': 
		
		case 'n': 
		
		case 'o': 
		
		case 'q': 
		
		case 'r': 
		
		case 't': 
		
		case 'v': 
		
		case 'y': 
		
		default: 
			return char1 == char2;
		
		case 'c': 
			boolean1 = isControl(char2);
			break;
		
		case 'd': 
			boolean1 = Character.isDigit(char2);
			break;
		
		case 'l': 
			boolean1 = Character.isLowerCase(char2);
			break;
		
		case 'p': 
			boolean1 = isPunct(char2);
			break;
		
		case 's': 
			boolean1 = isSpace(char2);
			break;
		
		case 'u': 
			boolean1 = Character.isUpperCase(char2);
			break;
		
		case 'w': 
			boolean1 = Character.isLowerCase(char2) || Character.isUpperCase(char2) || Character.isDigit(char2);
			break;
		
		case 'x': 
			boolean1 = isHex(char2);
			break;
		
		case 'z': 
			boolean1 = char2 == 0;
		
		}
		return char3 == char1 == boolean1;
	}

	private static boolean isPunct(char char1) {
		return char1 >= '!' && char1 <= '/' || char1 >= ':' && char1 <= '@' || char1 >= '[' && char1 <= '`' || char1 >= '{' && char1 <= '~';
	}

	private static boolean isSpace(char char1) {
		return char1 >= '\t' && char1 <= '\r' || char1 == ' ';
	}

	private static boolean isControl(char char1) {
		return char1 >= 0 && char1 <= 31 || char1 == 127;
	}

	private static boolean isHex(char char1) {
		return char1 >= '0' && char1 <= '9' || char1 >= 'a' && char1 <= 'f' || char1 >= 'A' && char1 <= 'F';
	}

	private static int gsub(LuaCallFrame luaCallFrame, int int1) {
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[9]);
		String string2 = KahluaUtil.getStringArg(luaCallFrame, 2, names[9]);
		Object object = KahluaUtil.getArg(luaCallFrame, 3, names[9]);
		String string3 = KahluaUtil.rawTostring(object);
		if (string3 != null) {
			object = string3;
		}

		Double Double1 = KahluaUtil.getOptionalNumberArg(luaCallFrame, 4);
		int int2 = Double1 == null ? Integer.MAX_VALUE : Double1.intValue();
		StringLib.StringPointer stringPointer = new StringLib.StringPointer(string2);
		StringLib.StringPointer stringPointer2 = new StringLib.StringPointer(string);
		boolean boolean1 = false;
		if (stringPointer.getChar() == '^') {
			boolean1 = true;
			stringPointer.postIncrString(1);
		}

		if (!(object instanceof Double) && !(object instanceof String) && !(object instanceof LuaClosure) && !(object instanceof JavaFunction) && !(object instanceof KahluaTable)) {
			KahluaUtil.fail("string/function/table expected, got " + object);
		}

		StringLib.MatchState matchState = new StringLib.MatchState(luaCallFrame, stringPointer2.getClone(), stringPointer2.length());
		int int3 = 0;
		StringBuffer stringBuffer = new StringBuffer();
		while (int3 < int2) {
			matchState.level = 0;
			StringLib.StringPointer stringPointer3 = match(matchState, stringPointer2, stringPointer);
			if (stringPointer3 != null) {
				++int3;
				addValue(matchState, object, stringBuffer, stringPointer2, stringPointer3);
			}

			if (stringPointer3 != null && stringPointer3.getIndex() > stringPointer2.getIndex()) {
				stringPointer2.setIndex(stringPointer3.getIndex());
			} else {
				if (stringPointer2.getIndex() >= matchState.endIndex) {
					break;
				}

				stringBuffer.append(stringPointer2.postIncrString(1));
			}

			if (boolean1) {
				break;
			}
		}

		return luaCallFrame.push(stringBuffer.append(stringPointer2.getString()).toString(), new Double((double)int3));
	}

	private static int trim(LuaCallFrame luaCallFrame, int int1) {
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[10]);
		return luaCallFrame.push(string.trim());
	}

	private static int split(LuaCallFrame luaCallFrame, int int1) {
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[11]);
		String string2 = KahluaUtil.getStringArg(luaCallFrame, 2, names[11]);
		String[] stringArray = string.split(string2);
		KahluaTable kahluaTable = LuaManager.platform.newTable();
		for (int int2 = 0; int2 < stringArray.length; ++int2) {
			kahluaTable.rawset(int2 + 1, stringArray[int2]);
		}

		return luaCallFrame.push(kahluaTable);
	}

	private static int sort(LuaCallFrame luaCallFrame, int int1) {
		String string = KahluaUtil.getStringArg(luaCallFrame, 1, names[12]);
		String string2 = KahluaUtil.getStringArg(luaCallFrame, 2, names[12]);
		return luaCallFrame.push(string.compareTo(string2) > 0);
	}

	private static void addValue(StringLib.MatchState matchState, Object object, StringBuffer stringBuffer, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		String string = KahluaUtil.rawTostring(object);
		if (string != null) {
			stringBuffer.append(addString(matchState, string, stringPointer, stringPointer2));
		} else {
			Object object2 = matchState.getCapture(0);
			String string2;
			if (object2 != null) {
				string2 = KahluaUtil.rawTostring(object2);
			} else {
				string2 = stringPointer.getStringSubString(stringPointer2.getIndex() - stringPointer.getIndex());
			}

			Object object3 = null;
			if (object instanceof KahluaTable) {
				object3 = ((KahluaTable)object).rawget(string2);
			} else {
				object3 = matchState.callFrame.getThread().call(object, string2, (Object)null, (Object)null);
			}

			if (object3 == null) {
				object3 = string2;
			}

			stringBuffer.append(KahluaUtil.rawTostring(object3));
		}
	}

	private static String addString(StringLib.MatchState matchState, String string, StringLib.StringPointer stringPointer, StringLib.StringPointer stringPointer2) {
		StringLib.StringPointer stringPointer3 = new StringLib.StringPointer(string);
		StringBuffer stringBuffer = new StringBuffer();
		for (int int1 = 0; int1 < string.length(); ++int1) {
			char char1 = stringPointer3.getChar(int1);
			if (char1 != '%') {
				stringBuffer.append(char1);
			} else {
				++int1;
				char1 = stringPointer3.getChar(int1);
				if (!Character.isDigit(char1)) {
					stringBuffer.append(char1);
				} else if (char1 == '0') {
					int int2 = stringPointer.getStringLength() - stringPointer2.length();
					stringBuffer.append(stringPointer.getStringSubString(int2));
				} else {
					Object object = matchState.getCapture(char1 - 49);
					if (object == null) {
						throw new KahluaException("invalid capture index");
					}

					stringBuffer.append(KahluaUtil.tostring(object, (KahluaThread)null));
				}
			}
		}

		return stringBuffer.toString();
	}

	static  {
	String var0 = "^$*+?.([%-";
	for (int var1 = 0; var1 < var0.length(); ++var1) {
		SPECIALS[var0.charAt(var1)] = true;
	}

		STRING_CLASS = "".getClass();
		names = new String[14];
		names[0] = "sub";
		names[1] = "char";
		names[2] = "byte";
		names[3] = "lower";
		names[4] = "upper";
		names[5] = "reverse";
		names[6] = "format";
		names[7] = "find";
		names[8] = "match";
		names[9] = "gsub";
		names[10] = "trim";
		names[11] = "split";
		names[12] = "sort";
		names[13] = "contains";
		functions = new StringLib[14];
	for (int var2 = 0; var2 < 14; ++var2) {
		functions[var2] = new StringLib(var2);
	}

		digits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	}

	public static class StringPointer {
		private final String string;
		private int index = 0;

		public StringPointer(String string) {
			this.string = string;
		}

		public StringPointer(String string, int int1) {
			this.string = string;
			this.index = int1;
		}

		public StringLib.StringPointer getClone() {
			return new StringLib.StringPointer(this.string, this.index);
		}

		public int getIndex() {
			return this.index;
		}

		public void setIndex(int int1) {
			this.index = int1;
		}

		public String getString() {
			return this.index == 0 ? this.string : this.string.substring(this.index);
		}

		public int getStringLength() {
			return this.getStringLength(0);
		}

		public int getStringLength(int int1) {
			return this.string.length() - (this.index + int1);
		}

		public String getStringSubString(int int1) {
			return this.string.substring(this.index, this.index + int1);
		}

		public char getChar() {
			return this.getChar(0);
		}

		public char getChar(int int1) {
			int int2 = this.index + int1;
			return int2 >= this.string.length() ? ' ' : this.string.charAt(int2);
		}

		public int length() {
			return this.string.length() - this.index;
		}

		public int postIncrStringI(int int1) {
			int int2 = this.index;
			this.index += int1;
			return int2;
		}

		public int preIncrStringI(int int1) {
			this.index += int1;
			return this.index;
		}

		public char postIncrString(int int1) {
			char char1 = this.getChar();
			this.index += int1;
			return char1;
		}

		public int compareTo(StringLib.StringPointer stringPointer, int int1) {
			for (int int2 = 0; int2 < int1; ++int2) {
				int int3 = this.getChar(int2) - stringPointer.getChar(int2);
				if (int3 != 0) {
					return int3;
				}
			}

			return 0;
		}
	}

	public static class MatchState {
		public final LuaCallFrame callFrame;
		public final StringLib.StringPointer src_init;
		public final int endIndex;
		public final StringLib.MatchState.Capture[] capture;
		public int level;

		public MatchState(LuaCallFrame luaCallFrame, StringLib.StringPointer stringPointer, int int1) {
			this.callFrame = luaCallFrame;
			this.src_init = stringPointer;
			this.endIndex = int1;
			this.capture = new StringLib.MatchState.Capture[32];
			for (int int2 = 0; int2 < 32; ++int2) {
				this.capture[int2] = new StringLib.MatchState.Capture();
			}
		}

		public Object getCapture(int int1) {
			if (int1 >= this.level) {
				return null;
			} else {
				return this.capture[int1].len == -2 ? new Double((double)(this.src_init.length() - this.capture[int1].init.length() + 1)) : this.capture[int1].init.getStringSubString(this.capture[int1].len);
			}
		}

		public static class Capture {
			public StringLib.StringPointer init;
			public int len;
		}
	}
}
