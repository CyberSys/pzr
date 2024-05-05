package zombie.iso.sprite.shapers;

import java.io.File;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.TransformerException;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.core.Core;
import zombie.debug.DebugLog;
import zombie.debug.LogSeverity;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;


public class SpritePaddingSettings {
	private static SpritePaddingSettings.Settings m_settings = null;
	private static String m_settingsFilePath = null;
	private static PredicatedFileWatcher m_fileWatcher = null;

	public static void settingsFileChanged(SpritePaddingSettings.Settings settings) {
		DebugLog.General.println("Settings file changed.");
		m_settings = settings;
	}

	private static void loadSettings() {
		String string = getSettingsFilePath();
		File file = (new File(string)).getAbsoluteFile();
		if (file.isFile()) {
			try {
				m_settings = (SpritePaddingSettings.Settings)PZXmlUtil.parse(SpritePaddingSettings.Settings.class, file.getPath());
			} catch (PZXmlParserException pZXmlParserException) {
				DebugLog.General.printException(pZXmlParserException, "Error parsing file: " + string, LogSeverity.Warning);
				m_settings = new SpritePaddingSettings.Settings();
			}
		} else {
			m_settings = new SpritePaddingSettings.Settings();
			saveSettings();
		}

		if (m_fileWatcher == null) {
			m_fileWatcher = new PredicatedFileWatcher(string, SpritePaddingSettings.Settings.class, SpritePaddingSettings::settingsFileChanged);
			DebugFileWatcher.instance.add(m_fileWatcher);
		}
	}

	private static String getSettingsFilePath() {
		if (m_settingsFilePath == null) {
			m_settingsFilePath = ZomboidFileSystem.instance.getLocalWorkDirSub("SpritePaddingSettings.xml");
		}

		return m_settingsFilePath;
	}

	private static void saveSettings() {
		try {
			PZXmlUtil.write((Object)m_settings, (new File(getSettingsFilePath())).getAbsoluteFile());
		} catch (IOException | JAXBException | TransformerException error) {
			error.printStackTrace();
		}
	}

	public static SpritePaddingSettings.Settings getSettings() {
		if (m_settings == null) {
			loadSettings();
		}

		return m_settings;
	}

	@XmlRootElement(name = "FloorShaperDeDiamondSettings")
	public static class Settings {
		public SpritePadding.IsoPaddingSettings IsoPadding = new SpritePadding.IsoPaddingSettings();
		public FloorShaperDeDiamond.Settings FloorDeDiamond = new FloorShaperDeDiamond.Settings();
		public FloorShaperAttachedSprites.Settings AttachedSprites = new FloorShaperAttachedSprites.Settings();
	}

	public abstract static class GenericZoomBasedSettingGroup {

		public abstract Object getCurrentZoomSetting();

		public static Object getCurrentZoomSetting(Object object, Object object2, Object object3) {
			float float1 = Core.getInstance().getCurrentPlayerZoom();
			if (float1 < 1.0F) {
				return object;
			} else {
				return float1 == 1.0F ? object2 : object3;
			}
		}
	}
}
