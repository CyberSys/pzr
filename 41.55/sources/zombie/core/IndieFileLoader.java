package zombie.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import zombie.ZomboidFileSystem;


public class IndieFileLoader {

	public static InputStreamReader getStreamReader(String string) throws FileNotFoundException {
		return getStreamReader(string, false);
	}

	public static InputStreamReader getStreamReader(String string, boolean boolean1) throws FileNotFoundException {
		InputStreamReader inputStreamReader = null;
		Object object = null;
		if (object != null && !boolean1) {
			inputStreamReader = new InputStreamReader((InputStream)object);
		} else {
			try {
				FileInputStream fileInputStream = new FileInputStream(ZomboidFileSystem.instance.getString(string));
				inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
			} catch (Exception exception) {
				String string2 = Core.getMyDocumentFolder();
				FileInputStream fileInputStream2 = new FileInputStream(string2 + File.separator + "mods" + File.separator + string);
				inputStreamReader = new InputStreamReader(fileInputStream2);
			}
		}

		return inputStreamReader;
	}
}
