package zombie.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;


public class WinReqistry {

	public static String getSteamDirectory() {
		String string = readRegistry("HKEY_CURRENT_USER\\Software\\Valve\\Steam", "SteamPath");
		return string;
	}

	public static final String readRegistry(String string, String string2) {
		try {
			Process process = Runtime.getRuntime().exec("reg query \"" + string + "\" /v " + string2);
			WinReqistry.StreamReader streamReader = new WinReqistry.StreamReader(process.getInputStream());
			streamReader.start();
			process.waitFor();
			streamReader.join();
			String string3 = streamReader.getResult();
			if (string3 != null && !string3.equals("")) {
				string3 = string3.substring(string3.indexOf("REG_SZ") + 7).trim();
				String[] stringArray = string3.split("\t");
				return stringArray[stringArray.length - 1];
			} else {
				return null;
			}
		} catch (Exception exception) {
			return null;
		}
	}

	static class StreamReader extends Thread {
		private InputStream is;
		private StringWriter sw = new StringWriter();

		public StreamReader(InputStream inputStream) {
			this.is = inputStream;
		}

		public void run() {
			while (true) {
				try {
					int int1;
					if ((int1 = this.is.read()) != -1) {
						this.sw.write(int1);
						continue;
					}
				} catch (IOException ioException) {
				}

				return;
			}
		}

		public String getResult() {
			return this.sw.toString();
		}
	}
}
