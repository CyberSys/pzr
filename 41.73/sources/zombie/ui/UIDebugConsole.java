package zombie.ui;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import se.krka.kahlua.integration.LuaReturn;
import se.krka.kahlua.luaj.compiler.LuaCompiler;
import se.krka.kahlua.stdlib.BaseLib;
import se.krka.kahlua.vm.KahluaException;
import se.krka.kahlua.vm.LuaClosure;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.core.Core;
import zombie.debug.DebugOptions;


public final class UIDebugConsole extends NewWindow {
	public static UIDebugConsole instance;
	IsoGameCharacter ParentChar;
	ScrollBar ScrollBarV;
	UITextBox2 OutputLog;
	public UITextBox2 CommandLine;
	UITextBox2 autosuggest;
	String ConsoleVersion = "v1.1.0";
	int inputlength = 0;
	private final ArrayList Previous = new ArrayList();
	private final ArrayList globalLuaMethods = new ArrayList();
	public int PreviousIndex = 0;
	Method prevSuggestion = null;
	String[] AvailableCommands = new String[]{"?", "help", "commands", "clr", "AddInvItem", "SpawnZombie"};
	String[] AvailableCommandsHelp = new String[]{"\'?\' - Shows available commands", "\'help\' - Shows available commands", "\'commands\' - Shows available commands", "\'clr\' - Clears the command log", "\'AddInvItem\' - Adds an item to player inventory. USAGE - AddInvItem \'ItemName\' [ammount]", "\'SpawnZombie\' - Spawn a zombie at a map location. USAGE - SpawnZombie X,Y,Z (integers)"};
	public boolean bDebounceUp = false;
	public boolean bDebounceDown = false;
	private static final Object outputLock = "DebugConsole Output Lock";
	private static final ByteBuffer outputBB = ByteBuffer.allocate(8192);
	private static boolean outputChanged = false;
	private static CharsetDecoder outputDecoder;
	private static char[] outputChars;
	private static CharBuffer outputCharBuf = null;

	public UIDebugConsole(int int1, int int2) {
		super(int1, int2, 10, 10, true);
		this.ResizeToFitY = false;
		this.visible = true;
		instance = this;
		this.width = 640.0F;
		int int3 = TextManager.instance.getFontHeight(UIFont.DebugConsole);
		byte byte1 = 11;
		byte byte2 = 5;
		this.OutputLog = new UITextBox2(UIFont.DebugConsole, 5, 33, 630, int3 * byte1 + byte2 * 2, "Project Zomboid - " + Core.getInstance().getVersionNumber() + "\nDebug Console - " + this.ConsoleVersion + "\n(C) Indie Stone Studios 2021\n---------------------------------------------------------------------------------------------------------------------------\n\n", true);
		this.OutputLog.multipleLine = true;
		this.OutputLog.bAlwaysPaginate = false;
		this.CommandLine = new UIDebugConsole.CommandEntry(UIFont.DebugConsole, 5, (int)(this.OutputLog.getY() + this.OutputLog.getHeight()) + 15, 630, 24, "", true);
		this.CommandLine.IsEditable = true;
		this.CommandLine.TextEntryMaxLength = 256;
		this.autosuggest = new UITextBox2(UIFont.DebugConsole, 5, 180, 15, 25, "", true);
		this.height = (float)((int)(this.CommandLine.getY() + this.CommandLine.getHeight()) + 6);
		this.ScrollBarV = new ScrollBar("UIDebugConsoleScrollbar", (UIEventHandler)null, (int)(this.OutputLog.getX() + this.OutputLog.getWidth()) - 14, this.OutputLog.getY().intValue() + 4, this.OutputLog.getHeight().intValue() - 8, true);
		this.ScrollBarV.SetParentTextBox(this.OutputLog);
		this.AddChild(this.OutputLog);
		this.AddChild(this.ScrollBarV);
		this.AddChild(this.CommandLine);
		this.AddChild(this.autosuggest);
		this.InitSuggestionEngine();
		if (Core.bDebug) {
			BaseLib.setPrintCallback(this::SpoolText);
		}
	}

	public void render() {
		if (this.isVisible()) {
			super.render();
			this.DrawTextCentre(UIFont.DebugConsole, "Command Console", this.getWidth() / 2.0, 2.0, 1.0, 1.0, 1.0, 1.0);
			this.DrawText(UIFont.DebugConsole, "Output Log", 7.0, 19.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
			this.DrawText(UIFont.DebugConsole, "Lua Command Line", 7.0, this.OutputLog.getY() + this.OutputLog.getHeight() + 1.0, 0.699999988079071, 0.699999988079071, 1.0, 1.0);
		}
	}

	public void update() {
		if (this.isVisible()) {
			this.handleOutput();
			super.update();
			if (this.CommandLine.getText().length() != this.inputlength && this.CommandLine.getText().length() != 0) {
				this.inputlength = this.CommandLine.getText().length();
				String[] stringArray = this.CommandLine.getText().split(":");
				String string = "";
				if (stringArray.length > 0) {
					string = stringArray[stringArray.length - 1];
					if (stringArray[stringArray.length - 1].isEmpty() && this.autosuggest.isVisible()) {
						this.autosuggest.setVisible(false);
						return;
					}
				}

				Method method = null;
				if (stringArray.length > 1 && stringArray[0].indexOf(")") > 0 && !stringArray[stringArray.length - 1].contains("(")) {
					ArrayList arrayList = new ArrayList(this.globalLuaMethods);
					int int1 = 0;
					while (true) {
						if (int1 >= stringArray.length) {
							method = this.SuggestionEngine(string, arrayList);
							break;
						}

						String string2 = stringArray[int1];
						if (string2.indexOf(")") > 0) {
							string2 = string2.split("\\(", 0)[0];
							Iterator iterator = arrayList.iterator();
							label78: while (iterator.hasNext()) {
								Method method2 = (Method)iterator.next();
								if (method2.getName().equals(string2)) {
									arrayList.clear();
									Class javaClass = method2.getReturnType();
									while (true) {
										if (javaClass == null) {
											break label78;
										}

										Method[] methodArray = javaClass.getDeclaredMethods();
										int int2 = methodArray.length;
										for (int int3 = 0; int3 < int2; ++int3) {
											Method method3 = methodArray[int3];
											if (Modifier.isPublic(method3.getModifiers())) {
												arrayList.add(method3);
											}
										}

										javaClass = javaClass.getSuperclass();
									}
								}
							}
						}

						++int1;
					}
				} else if (stringArray.length == 1) {
					method = this.SuggestionEngine(string);
				}

				String string3 = "void";
				if (method != null) {
					if (!method.getReturnType().toString().equals("void")) {
						String[] stringArray2 = method.getReturnType().toString().split("\\.");
						string3 = stringArray2[stringArray2.length - 1];
					}

					if (!this.autosuggest.isVisible()) {
						this.autosuggest.setVisible(true);
					}

					this.autosuggest.SetText("<" + string3 + "> " + method.getName());
					this.autosuggest.setX((double)(5 * this.CommandLine.getText().length()));
					this.autosuggest.setWidth((double)(15 * (string3.length() + method.getName().length())));
					this.autosuggest.Frame.width = (float)(10 * (string3.length() + method.getName().length()));
				}
			} else if (this.CommandLine.getText().length() == 0 && this.autosuggest.isVisible()) {
				this.autosuggest.setVisible(false);
			}
		}
	}

	public void ProcessCommand() {
		if (this.CommandLine.internalText != null) {
			String string = this.CommandLine.internalText;
			this.CommandLine.internalText = "";
			string = string.trim();
			String[] stringArray = string.split(" ");
			stringArray[0] = stringArray[0].trim();
			if (this.Previous.isEmpty() || !string.equals(this.Previous.get(this.Previous.size() - 1))) {
				this.Previous.add(string);
			}

			this.PreviousIndex = this.Previous.size();
			this.CommandLine.DoingTextEntry = true;
			Core.CurrentTextEntryBox = this.CommandLine;
			if ("clear".equals(string)) {
				this.OutputLog.bTextChanged = true;
				this.OutputLog.clearInput();
			} else {
				if (DebugOptions.instance.UIDebugConsoleEchoCommand.getValue()) {
					this.SpoolText("[USER] - \"" + string + "\".");
				}

				try {
					LuaClosure luaClosure = LuaCompiler.loadstring(string, "console", LuaManager.env);
					LuaReturn luaReturn = LuaManager.caller.protectedCall(LuaManager.thread, luaClosure);
				} catch (KahluaException kahluaException) {
					this.SpoolText(kahluaException.getMessage());
				} catch (Exception exception) {
					Logger.getLogger(UIDebugConsole.class.getName()).log(Level.SEVERE, (String)null, exception);
				}
			}
		}
	}

	void historyPrev() {
		--this.PreviousIndex;
		if (this.PreviousIndex < 0) {
			this.PreviousIndex = 0;
		}

		if (this.PreviousIndex >= 0 && this.PreviousIndex < this.Previous.size()) {
			this.CommandLine.SetText((String)this.Previous.get(this.PreviousIndex));
		}
	}

	void historyNext() {
		++this.PreviousIndex;
		if (this.PreviousIndex >= this.Previous.size()) {
			this.PreviousIndex = this.Previous.size() - 1;
		}

		if (this.PreviousIndex >= 0 && this.PreviousIndex < this.Previous.size()) {
			this.CommandLine.SetText((String)this.Previous.get(this.PreviousIndex));
		}
	}

	public void onOtherKey(int int1) {
		switch (int1) {
		case 15: 
			if (this.prevSuggestion != null) {
				String[] stringArray = this.CommandLine.getText().split(":");
				StringBuilder stringBuilder = new StringBuilder();
				if (stringArray.length > 0) {
					stringArray[stringArray.length - 1] = this.prevSuggestion.getName();
					for (int int2 = 0; int2 < stringArray.length; ++int2) {
						stringBuilder.append(stringArray[int2]);
						if (int2 != stringArray.length - 1) {
							stringBuilder.append(":");
						}
					}
				}

				if (this.prevSuggestion.getParameterTypes().length == 0) {
					this.CommandLine.SetText(stringBuilder + "()");
				} else {
					this.CommandLine.SetText(stringBuilder + "(");
				}
			}

		
		default: 
		
		}
	}

	void ClearConsole() {
		this.OutputLog.bTextChanged = true;
		this.OutputLog.SetText("");
		this.UpdateViewPos();
	}

	void UpdateViewPos() {
		this.OutputLog.TopLineIndex = this.OutputLog.Lines.size() - this.OutputLog.NumVisibleLines;
		if (this.OutputLog.TopLineIndex < 0) {
			this.OutputLog.TopLineIndex = 0;
		}

		this.ScrollBarV.scrollToBottom();
	}

	void SpoolText(String string) {
		this.OutputLog.bTextChanged = true;
		this.OutputLog.SetText(this.OutputLog.Text + string + "\n");
		this.UpdateViewPos();
	}

	Method SuggestionEngine(String string) {
		return this.SuggestionEngine(string, this.globalLuaMethods);
	}

	Method SuggestionEngine(String string, ArrayList arrayList) {
		int int1 = 0;
		boolean boolean1 = false;
		Method method = null;
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Method method2 = (Method)iterator.next();
			if (method == null) {
				method = method2;
				int1 = this.levenshteinDistance(string, method2.getName());
			} else {
				int int2 = this.levenshteinDistance(string, method2.getName());
				if (int2 < int1) {
					int1 = int2;
					method = method2;
				}
			}
		}

		this.prevSuggestion = method;
		return method;
	}

	void InitSuggestionEngine() {
		Class javaClass = LuaManager.GlobalObject.class;
		this.globalLuaMethods.addAll(Arrays.asList(javaClass.getDeclaredMethods()));
	}

	public int levenshteinDistance(CharSequence charSequence, CharSequence charSequence2) {
		int int1 = charSequence.length() + 1;
		int int2 = charSequence2.length() + 1;
		int[] intArray = new int[int1];
		int[] intArray2 = new int[int1];
		int int3;
		for (int3 = 0; int3 < int1; intArray[int3] = int3++) {
		}

		for (int3 = 1; int3 < int2; ++int3) {
			intArray2[0] = int3;
			for (int int4 = 1; int4 < int1; ++int4) {
				int int5 = charSequence.charAt(int4 - 1) == charSequence2.charAt(int3 - 1) ? 0 : 1;
				int int6 = intArray[int4 - 1] + int5;
				int int7 = intArray[int4] + 1;
				int int8 = intArray2[int4 - 1] + 1;
				intArray2[int4] = Math.min(Math.min(int7, int8), int6);
			}

			int[] intArray3 = intArray;
			intArray = intArray2;
			intArray2 = intArray3;
		}

		return intArray[int1 - 1];
	}

	void setSuggestWidth(int int1) {
		this.autosuggest.setWidth((double)int1);
		this.autosuggest.Frame.width = (float)int1;
	}

	public void addOutput(byte[] byteArray, int int1, int int2) {
		if (int2 >= 1) {
			synchronized (outputLock) {
				int int3 = int2 - outputBB.capacity();
				if (int3 > 0) {
					int1 += int3;
					int2 -= int3;
				}

				if (outputBB.position() + int2 > outputBB.capacity()) {
					outputBB.clear();
				}

				outputBB.put(byteArray, int1, int2);
				if (byteArray[int1 + int2 - 1] == 10) {
					outputChanged = true;
				}
			}
		}
	}

	private void handleOutput() {
		synchronized (outputLock) {
			if (outputChanged) {
				outputChanged = false;
				try {
					if (outputDecoder == null) {
						outputDecoder = Charset.forName("UTF-8").newDecoder().onMalformedInput(CodingErrorAction.REPLACE).onUnmappableCharacter(CodingErrorAction.REPLACE);
					}

					outputDecoder.reset();
					int int1 = outputBB.position();
					outputBB.flip();
					int int2 = (int)((double)int1 * (double)outputDecoder.maxCharsPerByte());
					if (outputChars == null || outputChars.length < int2) {
						int int3 = (int2 + 128 - 1) / 128 * 128;
						outputChars = new char[int3];
						outputCharBuf = CharBuffer.wrap(outputChars);
					}

					outputCharBuf.clear();
					CoderResult coderResult = outputDecoder.decode(outputBB, outputCharBuf, true);
					outputBB.clear();
					String string = new String(outputChars, 0, outputCharBuf.position());
					this.OutputLog.bTextChanged = true;
					this.OutputLog.SetText(this.OutputLog.Text + string);
					short short1 = 8192;
					if (this.OutputLog.Text.length() > short1) {
						int int4;
						for (int4 = this.OutputLog.Text.length() - short1; int4 < this.OutputLog.Text.length() && this.OutputLog.Text.charAt(int4) != '\n'; ++int4) {
						}

						this.OutputLog.bTextChanged = true;
						this.OutputLog.SetText(this.OutputLog.Text.substring(int4 + 1));
					}
				} catch (Exception exception) {
				}

				this.UpdateViewPos();
			}
		}
	}

	private class CommandEntry extends UITextBox2 {

		public CommandEntry(UIFont uIFont, int int1, int int2, int int3, int int4, String string, boolean boolean1) {
			super(uIFont, int1, int2, int3, int4, string, boolean1);
		}

		public void onPressUp() {
			UIDebugConsole.this.historyPrev();
		}

		public void onPressDown() {
			UIDebugConsole.this.historyNext();
		}

		public void onOtherKey(int int1) {
			UIDebugConsole.this.onOtherKey(int1);
		}
	}
}
