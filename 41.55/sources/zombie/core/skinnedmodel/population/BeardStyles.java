package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.ZomboidFileSystem;
import zombie.characters.HairOutfitDefinitions;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;


@XmlRootElement
public class BeardStyles {
	@XmlElement(name = "style")
	public final ArrayList m_Styles = new ArrayList();
	@XmlTransient
	public static BeardStyles instance;

	public static void init() {
		String string = ZomboidFileSystem.instance.base.getAbsolutePath();
		instance = Parse(string + File.separator + ZomboidFileSystem.processFilePath("media/hairStyles/beardStyles.xml", File.separatorChar));
		if (instance != null) {
			instance.m_Styles.add(0, new BeardStyle());
			Iterator iterator = ZomboidFileSystem.instance.getModIDs().iterator();
			while (true) {
				String string2;
				BeardStyles beardStyles;
				do {
					ChooseGameInfo.Mod mod;
					do {
						if (!iterator.hasNext()) {
							return;
						}

						string2 = (String)iterator.next();
						mod = ChooseGameInfo.getAvailableModDetails(string2);
					}			 while (mod == null);

					String string3 = ZomboidFileSystem.instance.getModDir(string2);
					beardStyles = Parse(string3 + File.separator + ZomboidFileSystem.processFilePath("media/hairStyles/beardStyles.xml", File.separatorChar));
				}		 while (beardStyles == null);

				Iterator iterator2 = beardStyles.m_Styles.iterator();
				while (iterator2.hasNext()) {
					BeardStyle beardStyle = (BeardStyle)iterator2.next();
					BeardStyle beardStyle2 = instance.FindStyle(beardStyle.name);
					if (beardStyle2 == null) {
						instance.m_Styles.add(beardStyle);
					} else {
						if (DebugLog.isEnabled(DebugType.Clothing)) {
							DebugLog.Clothing.println("mod \"%s\" overrides beard \"%s\"", string2, beardStyle.name);
						}

						int int1 = instance.m_Styles.indexOf(beardStyle2);
						instance.m_Styles.set(int1, beardStyle);
					}
				}
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			instance.m_Styles.clear();
			instance = null;
		}
	}

	public static BeardStyles Parse(String string) {
		try {
			return parse(string);
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (IOException | JAXBException error) {
			ExceptionLogger.logException(error);
		}

		return null;
	}

	public static BeardStyles parse(String string) throws JAXBException, IOException {
		FileInputStream fileInputStream = new FileInputStream(string);
		BeardStyles beardStyles;
		try {
			JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{BeardStyles.class});
			Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
			beardStyles = (BeardStyles)unmarshaller.unmarshal(fileInputStream);
		} catch (Throwable throwable) {
			try {
				fileInputStream.close();
			} catch (Throwable throwable2) {
				throwable.addSuppressed(throwable2);
			}

			throw throwable;
		}

		fileInputStream.close();
		return beardStyles;
	}

	public BeardStyle FindStyle(String string) {
		for (int int1 = 0; int1 < this.m_Styles.size(); ++int1) {
			BeardStyle beardStyle = (BeardStyle)this.m_Styles.get(int1);
			if (beardStyle.name.equalsIgnoreCase(string)) {
				return beardStyle;
			}
		}

		return null;
	}

	public String getRandomStyle(String string) {
		return HairOutfitDefinitions.instance.getRandomBeard(string, this.m_Styles);
	}

	public BeardStyles getInstance() {
		return instance;
	}

	public ArrayList getAllStyles() {
		return this.m_Styles;
	}
}
