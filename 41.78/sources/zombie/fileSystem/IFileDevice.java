package zombie.fileSystem;

import java.io.IOException;
import java.io.InputStream;


public interface IFileDevice {

	IFile createFile(IFile iFile);

	void destroyFile(IFile iFile);

	InputStream createStream(String string, InputStream inputStream) throws IOException;

	void destroyStream(InputStream inputStream);

	String name();
}
