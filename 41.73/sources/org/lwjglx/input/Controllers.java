package org.lwjglx.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.ByteBuffer;
import java.util.function.Consumer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.core.logger.ExceptionLogger;


public class Controllers {
	public static final int MAX_AXES = 6;
	public static final int MAX_BUTTONS = 15;
	public static final int MAX_CONTROLLERS = 16;
	private static final Controller[] controllers = new Controller[16];
	private static boolean isCreated = false;
	private static Consumer controllerConnectedCallback = null;
	private static Consumer controllerDisconnectedCallback = null;
	private static int debugToggleControllerPluggedIn = -1;

	public static void create() {
		readGameControllerDB();
		GLFW.glfwSetJoystickCallback(Controllers::updateControllersCount);
		for (int int1 = 0; int1 < 16; ++int1) {
			if (GLFW.glfwJoystickPresent(int1)) {
				controllers[int1] = new Controller(int1);
			}
		}

		isCreated = true;
	}

	private static void readGameControllerDB() {
		File file = (new File("./media/gamecontrollerdb.txt")).getAbsoluteFile();
		if (file.exists()) {
			readGameControllerDB(file);
		}

		String string = ZomboidFileSystem.instance.getCacheDirSub("joypads" + File.separator + "gamecontrollerdb.txt");
		file = new File(string);
		if (file.exists()) {
			readGameControllerDB(file);
		}
	}

	private static void readGameControllerDB(File file) {
		try {
			FileReader fileReader = new FileReader(file);
			try {
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				try {
					StringBuilder stringBuilder = new StringBuilder();
					String string;
					while ((string = bufferedReader.readLine()) != null) {
						if (!string.startsWith("#")) {
							stringBuilder.append(string);
							stringBuilder.append(System.lineSeparator());
						}
					}

					ByteBuffer byteBuffer = MemoryUtil.memUTF8(stringBuilder.toString());
					if (GLFW.glfwUpdateGamepadMappings(byteBuffer)) {
					}

					MemoryUtil.memFree(byteBuffer);
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
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public static void setControllerConnectedCallback(Consumer consumer) {
		controllerConnectedCallback = consumer;
	}

	public static void setControllerDisconnectedCallback(Consumer consumer) {
		controllerDisconnectedCallback = consumer;
	}

	public static int getControllerCount() {
		if (!isCreated()) {
			throw new RuntimeException("Before calling \'getJoypadCount()\' you should call \'create()\' method");
		} else {
			return controllers.length;
		}
	}

	public static Controller getController(int int1) {
		if (!isCreated()) {
			throw new RuntimeException("Before calling \'getJoypad(int)\' you should call \'create()\' method");
		} else {
			return controllers[int1];
		}
	}

	public static boolean isCreated() {
		return isCreated;
	}

	public static void poll(GamepadState[] gamepadStateArray) {
		if (!isCreated()) {
			throw new RuntimeException("Before calling \'poll()\' you should call \'create()\' method");
		} else {
			int int1;
			if (Core.bDebug && debugToggleControllerPluggedIn >= 0 && debugToggleControllerPluggedIn < 16) {
				int1 = debugToggleControllerPluggedIn;
				debugToggleControllerPluggedIn = -1;
				if (controllers[int1] != null) {
					updateControllersCount(int1, 262146);
				} else if (GLFW.glfwJoystickIsGamepad(int1)) {
					updateControllersCount(int1, 262145);
				}
			}

			for (int1 = 0; int1 < controllers.length; ++int1) {
				Controller controller = controllers[int1];
				if (controller != null) {
					controller.poll(gamepadStateArray[int1]);
				}
			}
		}
	}

	private static void updateControllersCount(int int1, int int2) {
		if (int2 == 262145) {
			Controller controller = new Controller(int1);
			controllers[int1] = controller;
			if (controllerConnectedCallback != null) {
				controllerConnectedCallback.accept(int1);
			}
		} else if (int2 == 262146) {
			controllers[int1] = null;
			if (controllerDisconnectedCallback != null) {
				controllerDisconnectedCallback.accept(int1);
			}
		}
	}

	public static void setDebugToggleControllerPluggedIn(int int1) {
		debugToggleControllerPluggedIn = int1;
	}
}
