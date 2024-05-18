package zombie.gameStates;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import zombie.FrameLoader;
import zombie.GameWindow;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.ui.UIManager;


public class LoginState extends GameState {
	public static int HEREBEWHEREYOUCRACKOBVIOUSLY_PLEASESPREADTHEWORD = 0;
	public static boolean LoggedIn = true;
	public static String version = "RC3";

	public void enter() {
		String string = GameWindow.getCacheDir() + File.separator;
		File file = new File(string);
		if (!file.exists()) {
			file.mkdirs();
		}

		string = string + "2133243254543.log";
		FrameLoader.makefile = new File(string);
		if (!FrameLoader.makefile.exists() && !LoggedIn) {
			LuaEventManager.triggerEvent("OnLoginState", this);
		} else {
			LoggedIn = true;
		}
	}

	public boolean Test(String string, String string2) {
		string = string.trim();
		string2 = string2.trim();
		try {
			LoggedIn = validateUser(string.trim(), string2.trim(), (String)null);
		} catch (MalformedURLException malformedURLException) {
			malformedURLException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		if (LoggedIn) {
			File file = new File(GameWindow.getCacheDir() + File.separator + "2133243254543.log");
			try {
				if (file.exists()) {
					file.delete();
					file.createNewFile();
				} else {
					file.createNewFile();
				}
			} catch (Exception exception) {
			}
		}

		return LoggedIn;
	}

	public boolean Test(String string) {
		string = string.trim();
		try {
			LoggedIn = validateUser((String)null, (String)null, string);
		} catch (MalformedURLException malformedURLException) {
			malformedURLException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}

		if (LoggedIn) {
			File file = new File(GameWindow.getCacheDir() + File.separator + "2133243254543.log");
			try {
				if (file.exists()) {
					file.delete();
					file.createNewFile();
				} else {
					file.createNewFile();
				}
			} catch (Exception exception) {
			}
		}

		return LoggedIn;
	}

	public String getCachedUsername() {
		return "";
	}

	public String getCachedPassword() {
		return "";
	}

	private static boolean validateUser(String string, String string2, String string3) throws MalformedURLException, IOException {
		String string4 = null;
		try {
			if (string != null && !string.isEmpty()) {
				string4 = "http://www.projectzomboid.com/scripts/auth.php?username=" + string + "&password=" + string2;
			} else {
				string4 = "http://www.desura.com/external/games/projectzomboid.php?cdkey=" + string3;
			}

			URL url = new URL(string4);
			URLConnection urlConnection = url.openConnection();
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String string5;
			do {
				if ((string5 = bufferedReader.readLine()) == null) {
					return false;
				}
			}	 while (!string5.contains("success"));

			return true;
		} catch (Exception exception) {
			if (string != null && !string.isEmpty()) {
				string4 = "http://www.projectzomboid.com/scripts/auth.php?username=" + string + "&password=" + string2;
			} else {
				string4 = "http://www.desura.com/external/games/projectzomboid.php?cdkey=" + string3;
			}

			URL url2 = new URL(string4);
			URLConnection urlConnection2 = url2.openConnection();
			BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(urlConnection2.getInputStream()));
			String string6;
			do {
				if ((string6 = bufferedReader2.readLine()) == null) {
					return false;
				}
			}	 while (!string6.contains("success"));

			return true;
		}
	}

	public static void DrawTexture(Texture texture, int int1, int int2, int int3, int int4, float float1) {
		SpriteRenderer.instance.render(texture, int1, int2, int3, int4, 1.0F, 1.0F, 1.0F, float1);
	}

	public void render() {
		Core.getInstance().StartFrame();
		Core.getInstance().EndFrame();
		Core.getInstance().StartFrameUI();
		UIManager.render();
		Core.getInstance().EndFrameUI();
	}

	public GameStateMachine.StateAction update() {
		if (LoggedIn && version.equals(GameWindow.version) && HEREBEWHEREYOUCRACKOBVIOUSLY_PLEASESPREADTHEWORD == 0) {
			LuaEventManager.triggerEvent("OnLoginStateSuccess");
			return GameStateMachine.StateAction.Continue;
		} else {
			return GameStateMachine.StateAction.Remain;
		}
	}
}
