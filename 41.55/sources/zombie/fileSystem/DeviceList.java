package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;


public final class DeviceList {
	private final IFileDevice[] m_devices = new IFileDevice[8];

	public void add(IFileDevice iFileDevice) {
		for (int int1 = 0; int1 < this.m_devices.length; ++int1) {
			if (this.m_devices[int1] == null) {
				this.m_devices[int1] = iFileDevice;
				break;
			}
		}
	}

	public IFile createFile() {
		IFile iFile = null;
		for (int int1 = 0; int1 < this.m_devices.length && this.m_devices[int1] != null; ++int1) {
			iFile = this.m_devices[int1].createFile(iFile);
		}

		return iFile;
	}

	public InputStream createStream(String string) throws IOException {
		InputStream inputStream = null;
		for (int int1 = 0; int1 < this.m_devices.length && this.m_devices[int1] != null; ++int1) {
			inputStream = this.m_devices[int1].createStream(string, inputStream);
		}

		return inputStream;
	}
}
