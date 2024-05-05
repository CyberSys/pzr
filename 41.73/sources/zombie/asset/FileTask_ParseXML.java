package zombie.asset;

import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;
import zombie.util.PZXmlUtil;


public final class FileTask_ParseXML extends FileTask {
	Class m_class;
	String m_filename;

	public FileTask_ParseXML(Class javaClass, String string, IFileTaskCallback iFileTaskCallback, FileSystem fileSystem) {
		super(fileSystem, iFileTaskCallback);
		this.m_class = javaClass;
		this.m_filename = string;
	}

	public String getErrorMessage() {
		return this.m_filename;
	}

	public void done() {
		this.m_class = null;
		this.m_filename = null;
	}

	public Object call() throws Exception {
		return PZXmlUtil.parse(this.m_class, this.m_filename);
	}
}
