package zombie;

import java.io.File;


public class SaveManager {
	public static SaveManager instance = new SaveManager();

	private static boolean deleteDirectory(File file) {
		if (file.exists()) {
			File[] fileArray = file.listFiles();
			for (int int1 = 0; int1 < fileArray.length; ++int1) {
				if (fileArray[int1].isDirectory()) {
					deleteDirectory(fileArray[int1]);
				} else {
					fileArray[int1].delete();
				}
			}
		}

		return file.delete();
	}

	public void init(String string) {
	}

	public void StartMassInsertion() {
	}

	public void EndMassInsertion() {
	}
}
