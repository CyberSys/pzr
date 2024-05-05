package zombie.asset;

import java.io.File;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;


public final class FileTask_Exists extends FileTask {
	String fileName;

	public FileTask_Exists(String string, IFileTaskCallback iFileTaskCallback, FileSystem fileSystem) {
		super(fileSystem, iFileTaskCallback);
		this.fileName = string;
	}

	public void done() {
	}

	public Object call() throws Exception {
		return (new File(this.fileName)).exists();
	}
}
