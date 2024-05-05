package zombie.fileSystem;

import java.io.InputStream;


public interface IFile {

	boolean open(String string, int int1);

	void close();

	boolean read(byte[] byteArray, long long1);

	boolean write(byte[] byteArray, long long1);

	byte[] getBuffer();

	long size();

	boolean seek(FileSeekMode fileSeekMode, long long1);

	long pos();

	InputStream getInputStream();

	IFileDevice getDevice();

	void release();
}
