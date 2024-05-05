package zombie.core;

import java.io.File;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.gameStates.ChooseGameInfo;
import zombie.util.Lambda;
import zombie.util.list.PZArrayUtil;


public final class Languages {
	public static final Languages instance = new Languages();
	private final ArrayList m_languages = new ArrayList();
	private Language m_defaultLanguage = new Language(0, "EN", "English", "UTF-8", (String)null, false);

	public Languages() {
		this.m_languages.add(this.m_defaultLanguage);
	}

	public void init() {
		this.m_languages.clear();
		this.m_defaultLanguage = new Language(0, "EN", "English", "UTF-8", (String)null, false);
		this.m_languages.add(this.m_defaultLanguage);
		this.loadTranslateDirectory(ZomboidFileSystem.instance.getMediaPath("lua/shared/Translate"));
		Iterator iterator = ZomboidFileSystem.instance.getModIDs().iterator();
		while (iterator.hasNext()) {
			String string = (String)iterator.next();
			ChooseGameInfo.Mod mod = ChooseGameInfo.getAvailableModDetails(string);
			if (mod != null) {
				File file = new File(mod.getDir(), "media/lua/shared/Translate");
				if (file.isDirectory()) {
					this.loadTranslateDirectory(file.getAbsolutePath());
				}
			}
		}
	}

	public Language getDefaultLanguage() {
		return this.m_defaultLanguage;
	}

	public int getNumLanguages() {
		return this.m_languages.size();
	}

	public Language getByIndex(int int1) {
		return int1 >= 0 && int1 < this.m_languages.size() ? (Language)this.m_languages.get(int1) : null;
	}

	public Language getByName(String string) {
		return (Language)PZArrayUtil.find((List)this.m_languages, Lambda.predicate(string, (var0,stringx)->{
			return var0.name().equalsIgnoreCase(stringx);
		}));
	}

	public int getIndexByName(String string) {
		return PZArrayUtil.indexOf((List)this.m_languages, Lambda.predicate(string, (var0,stringx)->{
			return var0.name().equalsIgnoreCase(stringx);
		}));
	}

	private void loadTranslateDirectory(String string) {
		Filter filter = (var0)->{
    return Files.isDirectory(var0, new LinkOption[0]) && Files.exists(var0.resolve("language.txt"), new LinkOption[0]);
};
		Path path = FileSystems.getDefault().getPath(string);
		if (Files.exists(path, new LinkOption[0])) {
			try {
				DirectoryStream directoryStream = Files.newDirectoryStream(path, filter);
				try {
					Iterator iterator = directoryStream.iterator();
					while (iterator.hasNext()) {
						Path path2 = (Path)iterator.next();
						LanguageFileData languageFileData = this.loadLanguageDirectory(path2.toAbsolutePath());
						if (languageFileData != null) {
							int int1 = this.getIndexByName(languageFileData.name);
							Language language;
							if (int1 == -1) {
								language = new Language(this.m_languages.size(), languageFileData.name, languageFileData.text, languageFileData.charset, languageFileData.base, languageFileData.azerty);
								this.m_languages.add(language);
							} else {
								language = new Language(int1, languageFileData.name, languageFileData.text, languageFileData.charset, languageFileData.base, languageFileData.azerty);
								this.m_languages.set(int1, language);
								if (languageFileData.name.equals(this.m_defaultLanguage.name())) {
									this.m_defaultLanguage = language;
								}
							}
						}
					}
				} catch (Throwable throwable) {
					if (directoryStream != null) {
						try {
							directoryStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}
					}

					throw throwable;
				}

				if (directoryStream != null) {
					directoryStream.close();
				}
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}
	}

	private LanguageFileData loadLanguageDirectory(Path path) {
		String string = path.getFileName().toString();
		LanguageFileData languageFileData = new LanguageFileData();
		languageFileData.name = string;
		LanguageFile languageFile = new LanguageFile();
		String string2 = path.resolve("language.txt").toString();
		return !languageFile.read(string2, languageFileData) ? null : languageFileData;
	}
}
