package zombie;

import java.io.BufferedReader;
import java.io.FileReader;


public class SVNRevision {
	public static int REVISION = -1;

	public static int init() {
		try {
			FileReader fileReader = new FileReader("SVNRevision.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String string = bufferedReader.readLine();
			bufferedReader.close();
			REVISION = Integer.parseInt(string);
		} catch (Exception exception) {
			System.out.println("Failed to read SVNRevision.txt");
		}

		return REVISION;
	}
}
