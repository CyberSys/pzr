package zombie.core.skinnedmodel.model;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import zombie.ZomboidFileSystem;
import zombie.core.skinnedmodel.model.jassimp.ProcessedAiScene;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.fileSystem.FileSystem;
import zombie.fileSystem.FileTask;
import zombie.fileSystem.IFileTaskCallback;


public abstract class FileTask_AbstractLoadModel extends FileTask {
	protected String m_fileName;
	private final String m_mediaFilePath;
	private final String m_mediaFileXPath;

	protected FileTask_AbstractLoadModel(FileSystem fileSystem, IFileTaskCallback iFileTaskCallback, String string, String string2) {
		super(fileSystem, iFileTaskCallback);
		this.m_mediaFilePath = string;
		this.m_mediaFileXPath = string2;
	}

	public Object call() throws Exception {
		this.checkSlowLoad();
		ModelFileExtensionType modelFileExtensionType = this.checkExtensionType();
		switch (modelFileExtensionType) {
		case X: 
			return this.loadX();
		
		case Fbx: 
			return this.loadFBX();
		
		case Txt: 
			return this.loadTxt();
		
		case None: 
		
		default: 
			return null;
		
		}
	}

	private void checkSlowLoad() {
		if (DebugOptions.instance.AssetSlowLoad.getValue()) {
			try {
				Thread.sleep(500L);
			} catch (InterruptedException interruptedException) {
			}
		}
	}

	private ModelFileExtensionType checkExtensionType() {
		String string = this.getRawFileName();
		String string2 = string.toLowerCase(Locale.ENGLISH);
		if (string2.endsWith(".txt")) {
			return ModelFileExtensionType.Txt;
		} else {
			boolean boolean1 = string.startsWith("x:");
			if (boolean1) {
				DebugLog.Animation.warn("Note: The \'x:\' prefix is not required. name=\"" + string + "\"");
				string2 = string.substring(2);
			}

			if (string.contains("media/") || string.contains(".")) {
				this.m_fileName = string;
				this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
				if ((new File(this.m_fileName)).exists()) {
					if (this.m_fileName.endsWith(".fbx")) {
						return ModelFileExtensionType.Fbx;
					}

					if (this.m_fileName.endsWith(".x")) {
						return ModelFileExtensionType.X;
					}

					return ModelFileExtensionType.X;
				}
			}

			this.m_fileName = this.m_mediaFileXPath + "/" + string2 + ".fbx";
			this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
			if ((new File(this.m_fileName)).exists()) {
				return ModelFileExtensionType.Fbx;
			} else {
				this.m_fileName = this.m_mediaFileXPath + "/" + string2 + ".x";
				this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
				if ((new File(this.m_fileName)).exists()) {
					return ModelFileExtensionType.X;
				} else if (boolean1) {
					return ModelFileExtensionType.None;
				} else {
					if (!string2.endsWith(".x")) {
						this.m_fileName = this.m_mediaFilePath + "/" + string2 + ".txt";
						if (string.contains("media/")) {
							this.m_fileName = string;
						}

						this.m_fileName = ZomboidFileSystem.instance.getString(this.m_fileName);
						if ((new File(this.m_fileName)).exists()) {
							return ModelFileExtensionType.Txt;
						}
					}

					return ModelFileExtensionType.None;
				}
			}
		}
	}

	public abstract String getRawFileName();

	public abstract ProcessedAiScene loadX() throws IOException;

	public abstract ProcessedAiScene loadFBX() throws IOException;

	public abstract ModelTxt loadTxt() throws IOException;
}
