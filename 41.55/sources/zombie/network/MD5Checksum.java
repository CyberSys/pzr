package zombie.network;

import java.io.File;
import java.io.FileInputStream;
import java.util.zip.CRC32;


public class MD5Checksum {

	public static long createChecksum(String string) throws Exception {
		File file = new File(string);
		if (!file.exists()) {
			return 0L;
		} else {
			FileInputStream fileInputStream = new FileInputStream(string);
			CRC32 cRC32 = new CRC32();
			byte[] byteArray = new byte[1024];
			int int1;
			while ((int1 = fileInputStream.read(byteArray)) != -1) {
				cRC32.update(byteArray, 0, int1);
			}

			long long1 = cRC32.getValue();
			fileInputStream.close();
			return long1;
		}
	}

	public static void main(String[] stringArray) {
	}
}
