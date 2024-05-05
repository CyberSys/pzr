package zombie.fileSystem;

import java.util.concurrent.Callable;


public abstract class FileTask implements Callable {
	protected final FileSystem m_file_system;
	protected final IFileTaskCallback m_cb;
	protected int m_priority = 5;

	public FileTask(FileSystem fileSystem) {
		this.m_file_system = fileSystem;
		this.m_cb = null;
	}

	public FileTask(FileSystem fileSystem, IFileTaskCallback iFileTaskCallback) {
		this.m_file_system = fileSystem;
		this.m_cb = iFileTaskCallback;
	}

	public void handleResult(Object object) {
		if (this.m_cb != null) {
			this.m_cb.onFileTaskFinished(object);
		}
	}

	public void setPriority(int int1) {
		this.m_priority = int1;
	}

	public abstract void done();

	public String getErrorMessage() {
		return null;
	}
}
