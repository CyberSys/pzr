package zombie.input;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import org.lwjglx.input.Controller;
import zombie.GameWindow;
import zombie.ZomboidFileSystem;
import zombie.Lua.LuaEventManager;
import zombie.characters.IsoPlayer;
import zombie.core.BoxedStaticValues;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.iso.Vector2;


public final class JoypadManager {
	public static final JoypadManager instance = new JoypadManager();
	public final JoypadManager.Joypad[] Joypads = new JoypadManager.Joypad[4];
	public final JoypadManager.Joypad[] JoypadsController = new JoypadManager.Joypad[16];
	public final ArrayList JoypadList = new ArrayList();
	public final HashSet ActiveControllerGUIDs = new HashSet();
	private static final int VERSION_1 = 1;
	private static final int VERSION_2 = 2;
	private static final int VERSION_LATEST = 2;

	public JoypadManager.Joypad addJoypad(int int1, String string, String string2) {
		JoypadManager.Joypad joypad = new JoypadManager.Joypad();
		joypad.ID = int1;
		joypad.guid = string;
		joypad.name = string2;
		this.JoypadsController[int1] = joypad;
		this.doControllerFile(joypad);
		if (!joypad.isDisabled() && this.ActiveControllerGUIDs.contains(string)) {
			this.JoypadList.add(joypad);
		}

		return joypad;
	}

	private JoypadManager.Joypad checkJoypad(int int1) {
		if (this.JoypadsController[int1] == null) {
			Controller controller = GameWindow.GameInput.getController(int1);
			this.addJoypad(int1, controller.getGUID(), controller.getGamepadName());
		}

		return this.JoypadsController[int1];
	}

	private void doControllerFile(JoypadManager.Joypad joypad) {
		File file = new File(ZomboidFileSystem.instance.getCacheDirSub("joypads"));
		if (!file.exists()) {
			file.mkdir();
		}

		file = new File(ZomboidFileSystem.instance.getCacheDirSub("joypads" + File.separator + joypad.guid + ".config"));
		try {
			FileReader fileReader = new FileReader(file.getAbsolutePath());
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try {
					System.out.println("reloading " + file.getAbsolutePath());
					int int1 = -1;
					try {
						String string = "";
						label186: while (true) {
							String[] stringArray;
							do {
								do {
									do {
										do {
											if (string == null) {
												break label186;
											}

											string = bufferedReader.readLine();
										}								 while (string == null);
									}							 while (string.trim().length() == 0);
								}						 while (string.trim().startsWith("//"));

								stringArray = string.split("=");
							}					 while (stringArray.length != 2);

							stringArray[0] = stringArray[0].trim();
							stringArray[1] = stringArray[1].trim();
							if (stringArray[0].equals("Version")) {
								int1 = Integer.parseInt(stringArray[1]);
								if (int1 < 1 || int1 > 2) {
									DebugLog.General.warn("Unknown version %d in %s", int1, file.getAbsolutePath());
									break;
								}

								if (int1 == 1) {
									DebugLog.General.warn("Obsolete version %d in %s.  Using default values.", int1, file.getAbsolutePath());
									break;
								}
							}

							if (int1 == -1) {
								DebugLog.General.warn("Ignoring %s=%s because Version is missing", stringArray[0], stringArray[1]);
							} else if (stringArray[0].equals("MovementAxisX")) {
								joypad.MovementAxisX = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("MovementAxisXFlipped")) {
								joypad.MovementAxisXFlipped = stringArray[1].equals("true");
							} else if (stringArray[0].equals("MovementAxisY")) {
								joypad.MovementAxisY = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("MovementAxisYFlipped")) {
								joypad.MovementAxisYFlipped = stringArray[1].equals("true");
							} else if (stringArray[0].equals("MovementAxisDeadZone")) {
								joypad.MovementAxisDeadZone = Float.parseFloat(stringArray[1]);
							} else if (stringArray[0].equals("AimingAxisX")) {
								joypad.AimingAxisX = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("AimingAxisXFlipped")) {
								joypad.AimingAxisXFlipped = stringArray[1].equals("true");
							} else if (stringArray[0].equals("AimingAxisY")) {
								joypad.AimingAxisY = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("AimingAxisYFlipped")) {
								joypad.AimingAxisYFlipped = stringArray[1].equals("true");
							} else if (stringArray[0].equals("AimingAxisDeadZone")) {
								joypad.AimingAxisDeadZone = Float.parseFloat(stringArray[1]);
							} else if (stringArray[0].equals("AButton")) {
								joypad.AButton = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("BButton")) {
								joypad.BButton = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("XButton")) {
								joypad.XButton = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("YButton")) {
								joypad.YButton = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("LBumper")) {
								joypad.BumperLeft = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("RBumper")) {
								joypad.BumperRight = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("L3")) {
								joypad.LeftStickButton = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("R3")) {
								joypad.RightStickButton = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("Back")) {
								joypad.Back = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("Start")) {
								joypad.Start = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("DPadUp")) {
								joypad.DPadUp = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("DPadDown")) {
								joypad.DPadDown = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("DPadLeft")) {
								joypad.DPadLeft = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("DPadRight")) {
								joypad.DPadRight = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("TriggersFlipped")) {
								joypad.TriggersFlipped = stringArray[1].equals("true");
							} else if (stringArray[0].equals("TriggerLeft")) {
								joypad.TriggerLeft = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("TriggerRight")) {
								joypad.TriggerRight = Integer.parseInt(stringArray[1]);
							} else if (stringArray[0].equals("Disabled")) {
								joypad.Disabled = stringArray[1].equals("true");
							} else if (stringArray[0].equals("Sensitivity")) {
								joypad.setDeadZone(Float.parseFloat(stringArray[1]));
							}
						}
					} catch (Exception exception) {
						ExceptionLogger.logException(exception);
					}
				} catch (Throwable throwable) {
					try {
						bufferedReader.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedReader.close();
			} catch (Throwable throwable3) {
				try {
					fileReader.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileReader.close();
		} catch (FileNotFoundException fileNotFoundException) {
			if (!this.ActiveControllerGUIDs.contains(joypad.guid)) {
				this.ActiveControllerGUIDs.add(joypad.guid);
				try {
					Core.getInstance().saveOptions();
				} catch (Exception exception2) {
					ExceptionLogger.logException(exception2);
				}
			}
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
		}

		this.saveFile(joypad);
	}

	private void saveFile(JoypadManager.Joypad joypad) {
		String string = ZomboidFileSystem.instance.getCacheDir();
		File file = new File(string + File.separator + "joypads");
		if (!file.exists()) {
			file.mkdir();
		}

		file = new File(ZomboidFileSystem.instance.getCacheDirSub("joypads" + File.separator + joypad.guid + ".config"));
		try {
			FileWriter fileWriter = new FileWriter(file.getAbsolutePath());
			try {
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				try {
					String string2 = System.getProperty("line.separator");
					bufferedWriter.write("Version=2" + string2);
					bufferedWriter.write("Name=" + joypad.name + string2);
					bufferedWriter.write("MovementAxisX=" + joypad.MovementAxisX + string2);
					bufferedWriter.write("MovementAxisXFlipped=" + joypad.MovementAxisXFlipped + string2);
					bufferedWriter.write("MovementAxisY=" + joypad.MovementAxisY + string2);
					bufferedWriter.write("MovementAxisYFlipped=" + joypad.MovementAxisYFlipped + string2);
					bufferedWriter.write("// Set the dead zone to the smallest number between 0.0 and 1.0." + string2);
					bufferedWriter.write("// This is to fix \"loose sticks\"." + string2);
					bufferedWriter.write("MovementAxisDeadZone=" + joypad.MovementAxisDeadZone + string2);
					bufferedWriter.write("AimingAxisX=" + joypad.AimingAxisX + string2);
					bufferedWriter.write("AimingAxisXFlipped=" + joypad.AimingAxisXFlipped + string2);
					bufferedWriter.write("AimingAxisY=" + joypad.AimingAxisY + string2);
					bufferedWriter.write("AimingAxisYFlipped=" + joypad.AimingAxisYFlipped + string2);
					bufferedWriter.write("AimingAxisDeadZone=" + joypad.AimingAxisDeadZone + string2);
					bufferedWriter.write("AButton=" + joypad.AButton + string2);
					bufferedWriter.write("BButton=" + joypad.BButton + string2);
					bufferedWriter.write("XButton=" + joypad.XButton + string2);
					bufferedWriter.write("YButton=" + joypad.YButton + string2);
					bufferedWriter.write("LBumper=" + joypad.BumperLeft + string2);
					bufferedWriter.write("RBumper=" + joypad.BumperRight + string2);
					bufferedWriter.write("L3=" + joypad.LeftStickButton + string2);
					bufferedWriter.write("R3=" + joypad.RightStickButton + string2);
					bufferedWriter.write("Back=" + joypad.Back + string2);
					bufferedWriter.write("Start=" + joypad.Start + string2);
					bufferedWriter.write("// Normally the D-pad is treated as a single axis (the POV Hat), and these should be -1." + string2);
					bufferedWriter.write("// If your D-pad is actually 4 separate buttons, set the button numbers here." + string2);
					bufferedWriter.write("DPadUp=" + joypad.DPadUp + string2);
					bufferedWriter.write("DPadDown=" + joypad.DPadDown + string2);
					bufferedWriter.write("DPadLeft=" + joypad.DPadLeft + string2);
					bufferedWriter.write("DPadRight=" + joypad.DPadRight + string2);
					bufferedWriter.write("TriggersFlipped=" + joypad.TriggersFlipped + string2);
					bufferedWriter.write("// If your triggers are buttons, set the button numbers here." + string2);
					bufferedWriter.write("// If these are set to something other than -1, then Triggers= is ignored." + string2);
					bufferedWriter.write("TriggerLeft=" + joypad.TriggerLeft + string2);
					bufferedWriter.write("TriggerRight=" + joypad.TriggerRight + string2);
					bufferedWriter.write("Disabled=" + joypad.Disabled + string2);
					float float1 = joypad.getDeadZone(0);
					bufferedWriter.write("Sensitivity=" + float1 + string2);
				} catch (Throwable throwable) {
					try {
						bufferedWriter.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedWriter.close();
			} catch (Throwable throwable3) {
				try {
					fileWriter.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileWriter.close();
		} catch (IOException ioException) {
			ExceptionLogger.logException(ioException);
		}
	}

	public void reloadControllerFiles() {
		for (int int1 = 0; int1 < GameWindow.GameInput.getControllerCount(); ++int1) {
			Controller controller = GameWindow.GameInput.getController(int1);
			if (controller != null) {
				if (this.JoypadsController[int1] == null) {
					this.addJoypad(int1, controller.getGUID(), controller.getGamepadName());
				} else {
					this.doControllerFile(this.JoypadsController[int1]);
				}
			}
		}
	}

	public void assignJoypad(int int1, int int2) {
		this.checkJoypad(int1);
		this.Joypads[int2] = this.JoypadsController[int1];
		this.Joypads[int2].player = int2;
	}

	public JoypadManager.Joypad getFromPlayer(int int1) {
		return this.Joypads[int1];
	}

	public JoypadManager.Joypad getFromControllerID(int int1) {
		return this.JoypadsController[int1];
	}

	public void onPressed(int int1, int int2) {
		this.checkJoypad(int1);
		this.JoypadsController[int1].onPressed(int2);
	}

	public boolean isDownPressed(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].isDownPressed();
	}

	public boolean isUpPressed(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].isUpPressed();
	}

	public boolean isRightPressed(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].isRightPressed();
	}

	public boolean isLeftPressed(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].isLeftPressed();
	}

	public boolean isLBPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isLBPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isLBPressed();
		}
	}

	public boolean isRBPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isRBPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isRBPressed();
		}
	}

	public boolean isL3Pressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isL3Pressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isL3Pressed();
		}
	}

	public boolean isR3Pressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isR3Pressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isR3Pressed();
		}
	}

	public boolean isRTPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isRTPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isRTPressed();
		}
	}

	public boolean isLTPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isLTPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isLTPressed();
		}
	}

	public boolean isAPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isAPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isAPressed();
		}
	}

	public boolean isBPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isBPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isBPressed();
		}
	}

	public boolean isXPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isXPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isXPressed();
		}
	}

	public boolean isYPressed(int int1) {
		if (int1 < 0) {
			for (int int2 = 0; int2 < this.JoypadList.size(); ++int2) {
				if (((JoypadManager.Joypad)this.JoypadList.get(int2)).isYPressed()) {
					return true;
				}
			}

			return false;
		} else {
			this.checkJoypad(int1);
			return this.JoypadsController[int1].isYPressed();
		}
	}

	public boolean isButtonStartPress(int int1, int int2) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonStartPress(int2);
	}

	public boolean isButtonReleasePress(int int1, int int2) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonReleasePress(int2);
	}

	public boolean isAButtonStartPress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return this.isButtonStartPress(int1, joypad.getAButton());
	}

	public boolean isBButtonStartPress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonStartPress(joypad.getBButton());
	}

	public boolean isXButtonStartPress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonStartPress(joypad.getXButton());
	}

	public boolean isYButtonStartPress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonStartPress(joypad.getYButton());
	}

	public boolean isAButtonReleasePress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonReleasePress(joypad.getAButton());
	}

	public boolean isBButtonReleasePress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonReleasePress(joypad.getBButton());
	}

	public boolean isXButtonReleasePress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonReleasePress(joypad.getXButton());
	}

	public boolean isYButtonReleasePress(int int1) {
		JoypadManager.Joypad joypad = this.checkJoypad(int1);
		return joypad.isButtonReleasePress(joypad.getYButton());
	}

	public float getMovementAxisX(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].getMovementAxisX();
	}

	public float getMovementAxisY(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].getMovementAxisY();
	}

	public float getAimingAxisX(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].getAimingAxisX();
	}

	public float getAimingAxisY(int int1) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].getAimingAxisY();
	}

	public void onPressedAxis(int int1, int int2) {
		this.checkJoypad(int1);
		this.JoypadsController[int1].onPressedAxis(int2);
	}

	public void onPressedAxisNeg(int int1, int int2) {
		this.checkJoypad(int1);
		this.JoypadsController[int1].onPressedAxisNeg(int2);
	}

	public void onPressedTrigger(int int1, int int2) {
		this.checkJoypad(int1);
		this.JoypadsController[int1].onPressedTrigger(int2);
	}

	public void onPressedPov(int int1) {
		this.checkJoypad(int1);
		this.JoypadsController[int1].onPressedPov();
	}

	public float getDeadZone(int int1, int int2) {
		this.checkJoypad(int1);
		return this.JoypadsController[int1].getDeadZone(int2);
	}

	public void setDeadZone(int int1, int int2, float float1) {
		this.checkJoypad(int1);
		this.JoypadsController[int1].setDeadZone(int2, float1);
	}

	public void saveControllerSettings(int int1) {
		this.checkJoypad(int1);
		this.saveFile(this.JoypadsController[int1]);
	}

	public long getLastActivity(int int1) {
		return this.JoypadsController[int1] == null ? 0L : this.JoypadsController[int1].lastActivity;
	}

	public void setControllerActive(String string, boolean boolean1) {
		if (boolean1) {
			this.ActiveControllerGUIDs.add(string);
		} else {
			this.ActiveControllerGUIDs.remove(string);
		}

		this.syncActiveControllers();
	}

	public void syncActiveControllers() {
		this.JoypadList.clear();
		for (int int1 = 0; int1 < this.JoypadsController.length; ++int1) {
			JoypadManager.Joypad joypad = this.JoypadsController[int1];
			if (joypad != null && !joypad.isDisabled() && this.ActiveControllerGUIDs.contains(joypad.guid)) {
				this.JoypadList.add(joypad);
			}
		}
	}

	public boolean isJoypadConnected(int int1) {
		if (int1 >= 0 && int1 < 16) {
			assert Thread.currentThread() == GameWindow.GameThread;
			return GameWindow.GameInput.getController(int1) != null;
		} else {
			return false;
		}
	}

	public void onControllerConnected(Controller controller) {
		JoypadManager.Joypad joypad = this.JoypadsController[controller.getID()];
		if (joypad != null) {
			LuaEventManager.triggerEvent("OnJoypadBeforeReactivate", BoxedStaticValues.toDouble((double)joypad.getID()));
			joypad.bConnected = true;
			LuaEventManager.triggerEvent("OnJoypadReactivate", BoxedStaticValues.toDouble((double)joypad.getID()));
		}
	}

	public void onControllerDisconnected(Controller controller) {
		JoypadManager.Joypad joypad = this.JoypadsController[controller.getID()];
		if (joypad != null) {
			LuaEventManager.triggerEvent("OnJoypadBeforeDeactivate", BoxedStaticValues.toDouble((double)joypad.getID()));
			joypad.bConnected = false;
			LuaEventManager.triggerEvent("OnJoypadDeactivate", BoxedStaticValues.toDouble((double)joypad.getID()));
		}
	}

	public void revertToKeyboardAndMouse() {
		for (int int1 = 0; int1 < this.JoypadList.size(); ++int1) {
			JoypadManager.Joypad joypad = (JoypadManager.Joypad)this.JoypadList.get(int1);
			if (joypad.player == 0) {
				if (GameWindow.ActivatedJoyPad == joypad) {
					GameWindow.ActivatedJoyPad = null;
				}

				IsoPlayer player = IsoPlayer.players[0];
				if (player != null) {
					player.JoypadBind = -1;
				}

				this.JoypadsController[joypad.getID()] = null;
				this.Joypads[0] = null;
				this.JoypadList.remove(int1);
				break;
			}
		}
	}

	public void renderUI() {
		assert Thread.currentThread() == GameWindow.GameThread;
		if (DebugOptions.instance.JoypadRenderUI.getValue()) {
			if (!GameWindow.DrawReloadingLua) {
				LuaEventManager.triggerEvent("OnJoypadRenderUI");
			}
		}
	}

	public void Reset() {
		for (int int1 = 0; int1 < this.Joypads.length; ++int1) {
			this.Joypads[int1] = null;
		}
	}

	public static final class Joypad {
		String guid;
		String name;
		int ID;
		int player = -1;
		int MovementAxisX = 0;
		boolean MovementAxisXFlipped = false;
		int MovementAxisY = 1;
		boolean MovementAxisYFlipped = false;
		float MovementAxisDeadZone = 0.0F;
		int AimingAxisX = 2;
		boolean AimingAxisXFlipped = false;
		int AimingAxisY = 3;
		boolean AimingAxisYFlipped = false;
		float AimingAxisDeadZone = 0.0F;
		int AButton = 0;
		int BButton = 1;
		int XButton = 2;
		int YButton = 3;
		int DPadUp = -1;
		int DPadDown = -1;
		int DPadLeft = -1;
		int DPadRight = -1;
		int BumperLeft = 4;
		int BumperRight = 5;
		int Back = 6;
		int Start = 7;
		int LeftStickButton = 9;
		int RightStickButton = 10;
		boolean TriggersFlipped = false;
		int TriggerLeft = 4;
		int TriggerRight = 5;
		boolean Disabled = false;
		boolean bConnected = true;
		long lastActivity;
		private static final Vector2 tempVec2 = new Vector2();

		public boolean isDownPressed() {
			return this.DPadDown != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadDown, this.ID) : GameWindow.GameInput.isControllerDownD(this.ID);
		}

		public boolean isUpPressed() {
			return this.DPadUp != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadUp, this.ID) : GameWindow.GameInput.isControllerUpD(this.ID);
		}

		public boolean isRightPressed() {
			return this.DPadRight != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadRight, this.ID) : GameWindow.GameInput.isControllerRightD(this.ID);
		}

		public boolean isLeftPressed() {
			return this.DPadLeft != -1 ? GameWindow.GameInput.isButtonPressedD(this.DPadLeft, this.ID) : GameWindow.GameInput.isControllerLeftD(this.ID);
		}

		public boolean isLBPressed() {
			return GameWindow.GameInput.isButtonPressedD(this.BumperLeft, this.ID);
		}

		public boolean isRBPressed() {
			return GameWindow.GameInput.isButtonPressedD(this.BumperRight, this.ID);
		}

		public boolean isL3Pressed() {
			return GameWindow.GameInput.isButtonPressedD(this.LeftStickButton, this.ID);
		}

		public boolean isR3Pressed() {
			return GameWindow.GameInput.isButtonPressedD(this.RightStickButton, this.ID);
		}

		public boolean isRTPressed() {
			int int1 = this.TriggerRight;
			if (GameWindow.GameInput.getAxisCount(this.ID) <= int1) {
				return this.isRBPressed();
			} else if (this.TriggersFlipped) {
				return GameWindow.GameInput.getAxisValue(this.ID, int1) < -0.7F;
			} else {
				return GameWindow.GameInput.getAxisValue(this.ID, int1) > 0.7F;
			}
		}

		public boolean isLTPressed() {
			int int1 = this.TriggerLeft;
			if (GameWindow.GameInput.getAxisCount(this.ID) <= int1) {
				return this.isLBPressed();
			} else if (this.TriggersFlipped) {
				return GameWindow.GameInput.getAxisValue(this.ID, int1) < -0.7F;
			} else {
				return GameWindow.GameInput.getAxisValue(this.ID, int1) > 0.7F;
			}
		}

		public boolean isAPressed() {
			return GameWindow.GameInput.isButtonPressedD(this.AButton, this.ID);
		}

		public boolean isBPressed() {
			return GameWindow.GameInput.isButtonPressedD(this.BButton, this.ID);
		}

		public boolean isXPressed() {
			return GameWindow.GameInput.isButtonPressedD(this.XButton, this.ID);
		}

		public boolean isYPressed() {
			return GameWindow.GameInput.isButtonPressedD(this.YButton, this.ID);
		}

		public boolean isButtonPressed(int int1) {
			return GameWindow.GameInput.isButtonPressedD(int1, this.ID);
		}

		public boolean wasButtonPressed(int int1) {
			return GameWindow.GameInput.wasButtonPressed(this.ID, int1);
		}

		public boolean isButtonStartPress(int int1) {
			return GameWindow.GameInput.isButtonStartPress(this.ID, int1);
		}

		public boolean isButtonReleasePress(int int1) {
			return GameWindow.GameInput.isButtonReleasePress(this.ID, int1);
		}

		public float getMovementAxisX() {
			if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisX) {
				return 0.0F;
			} else {
				this.MovementAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.MovementAxisX);
				float float1 = this.MovementAxisDeadZone;
				if (float1 > 0.0F && float1 < 1.0F) {
					float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
					float float3 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
					Vector2 vector2 = tempVec2.set(float2, float3);
					if (vector2.getLength() < float1) {
						vector2.set(0.0F, 0.0F);
					} else {
						vector2.setLength((vector2.getLength() - float1) / (1.0F - float1));
					}

					return this.MovementAxisXFlipped ? -vector2.getX() : vector2.getX();
				} else {
					return this.MovementAxisXFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX) : GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
				}
			}
		}

		public float getMovementAxisY() {
			if (GameWindow.GameInput.getAxisCount(this.ID) <= this.MovementAxisY) {
				return 0.0F;
			} else {
				this.MovementAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.MovementAxisY);
				float float1 = this.MovementAxisDeadZone;
				if (float1 > 0.0F && float1 < 1.0F) {
					float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisX);
					float float3 = GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
					Vector2 vector2 = tempVec2.set(float2, float3);
					if (vector2.getLength() < float1) {
						vector2.set(0.0F, 0.0F);
					} else {
						vector2.setLength((vector2.getLength() - float1) / (1.0F - float1));
					}

					return this.MovementAxisYFlipped ? -vector2.getY() : vector2.getY();
				} else {
					return this.MovementAxisYFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY) : GameWindow.GameInput.getAxisValue(this.ID, this.MovementAxisY);
				}
			}
		}

		public float getAimingAxisX() {
			if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisX) {
				return 0.0F;
			} else {
				this.AimingAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.AimingAxisX);
				float float1 = this.AimingAxisDeadZone;
				if (float1 > 0.0F && float1 < 1.0F) {
					float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
					float float3 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
					Vector2 vector2 = tempVec2.set(float2, float3);
					if (vector2.getLength() < float1) {
						vector2.set(0.0F, 0.0F);
					} else {
						vector2.setLength((vector2.getLength() - float1) / (1.0F - float1));
					}

					return this.AimingAxisXFlipped ? -vector2.getX() : vector2.getX();
				} else {
					return this.AimingAxisXFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX) : GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
				}
			}
		}

		public float getAimingAxisY() {
			if (GameWindow.GameInput.getAxisCount(this.ID) <= this.AimingAxisY) {
				return 0.0F;
			} else {
				this.AimingAxisDeadZone = GameWindow.GameInput.getController(this.ID).getDeadZone(this.AimingAxisY);
				float float1 = this.AimingAxisDeadZone;
				if (float1 > 0.0F && float1 < 1.0F) {
					float float2 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisX);
					float float3 = GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
					Vector2 vector2 = tempVec2.set(float2, float3);
					if (vector2.getLength() < float1) {
						vector2.set(0.0F, 0.0F);
					} else {
						vector2.setLength((vector2.getLength() - float1) / (1.0F - float1));
					}

					return this.AimingAxisYFlipped ? -vector2.getY() : vector2.getY();
				} else {
					return this.AimingAxisYFlipped ? -GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY) : GameWindow.GameInput.getAxisValue(this.ID, this.AimingAxisY);
				}
			}
		}

		public void onPressed(int int1) {
			this.lastActivity = System.currentTimeMillis();
		}

		public void onPressedAxis(int int1) {
			this.lastActivity = System.currentTimeMillis();
		}

		public void onPressedAxisNeg(int int1) {
			this.lastActivity = System.currentTimeMillis();
		}

		public void onPressedTrigger(int int1) {
			this.lastActivity = System.currentTimeMillis();
		}

		public void onPressedPov() {
			this.lastActivity = System.currentTimeMillis();
		}

		public float getDeadZone(int int1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getAxisCount(this.ID)) {
				float float1 = GameWindow.GameInput.getController(this.ID).getDeadZone(int1);
				float float2 = 0.0F;
				if ((int1 == this.MovementAxisX || int1 == this.MovementAxisY) && this.MovementAxisDeadZone > 0.0F && this.MovementAxisDeadZone < 1.0F) {
					float2 = this.MovementAxisDeadZone;
				}

				if ((int1 == this.AimingAxisX || int1 == this.AimingAxisY) && this.AimingAxisDeadZone > 0.0F && this.AimingAxisDeadZone < 1.0F) {
					float2 = this.AimingAxisDeadZone;
				}

				return Math.max(float1, float2);
			} else {
				return 0.0F;
			}
		}

		public void setDeadZone(int int1, float float1) {
			if (int1 >= 0 && int1 < GameWindow.GameInput.getAxisCount(this.ID)) {
				GameWindow.GameInput.getController(this.ID).setDeadZone(int1, float1);
			}
		}

		public void setDeadZone(float float1) {
			for (int int1 = 0; int1 < GameWindow.GameInput.getAxisCount(this.ID); ++int1) {
				GameWindow.GameInput.getController(this.ID).setDeadZone(int1, float1);
			}
		}

		public int getID() {
			return this.ID;
		}

		public boolean isDisabled() {
			return this.Disabled;
		}

		public int getAButton() {
			return this.AButton;
		}

		public int getBButton() {
			return this.BButton;
		}

		public int getXButton() {
			return this.XButton;
		}

		public int getYButton() {
			return this.YButton;
		}

		public int getLBumper() {
			return this.BumperLeft;
		}

		public int getRBumper() {
			return this.BumperRight;
		}

		public int getL3() {
			return this.LeftStickButton;
		}

		public int getR3() {
			return this.RightStickButton;
		}

		public int getBackButton() {
			return this.Back;
		}

		public int getStartButton() {
			return this.Start;
		}
	}
}
