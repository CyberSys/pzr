package zombie.asset;

import jassimp.Jassimp;
import java.util.EnumSet;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;


public final class FileTask_LoadAiScene extends FileTask {
	String m_filename;
	EnumSet m_post_process_step_set;

	public FileTask_LoadAiScene(String string, EnumSet enumSet, IFileTaskCallback iFileTaskCallback, FileSystem fileSystem) {
		super(fileSystem, iFileTaskCallback);
		this.m_filename = string;
		this.m_post_process_step_set = enumSet;
	}

	public String getErrorMessage() {
		return this.m_filename;
	}

	public void done() {
		this.m_filename = null;
		this.m_post_process_step_set = null;
	}

	public Object call() throws Exception {
		return Jassimp.importFile(this.m_filename, this.m_post_process_step_set);
	}
}
