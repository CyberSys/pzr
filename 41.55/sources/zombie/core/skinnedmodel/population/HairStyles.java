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
public class HairStyles {
	@XmlElement(name = "male")
	public final ArrayList m_MaleStyles = new ArrayList();
	@XmlElement(name = "female")
	public final ArrayList m_FemaleStyles = new ArrayList();
	@XmlTransient
	public static HairStyles instance;

	public static void init() {
		String string = ZomboidFileSystem.instance.base.getAbsolutePath();
		instance = Parse(string + File.separator + ZomboidFileSystem.processFilePath("media/hairStyles/hairStyles.xml", File.separatorChar));
		if (instance != null) {
			Iterator iterator = ZomboidFileSystem.instance.getModIDs().iterator();
			while (true) {
				String string2;
				HairStyles hairStyles;
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
					hairStyles = Parse(string3 + File.separator + ZomboidFileSystem.processFilePath("media/hairStyles/hairStyles.xml", File.separatorChar));
				}		 while (hairStyles == null);

				Iterator iterator2 = hairStyles.m_FemaleStyles.iterator();
				HairStyle hairStyle;
				HairStyle hairStyle2;
				int int1;
				while (iterator2.hasNext()) {
					hairStyle = (HairStyle)iterator2.next();
					hairStyle2 = instance.FindFemaleStyle(hairStyle.name);
					if (hairStyle2 == null) {
						instance.m_FemaleStyles.add(hairStyle);
					} else {
						if (DebugLog.isEnabled(DebugType.Clothing)) {
							DebugLog.Clothing.println("mod \"%s\" overrides hair \"%s\"", string2, hairStyle.name);
						}

						int1 = instance.m_FemaleStyles.indexOf(hairStyle2);
						instance.m_FemaleStyles.set(int1, hairStyle);
					}
				}

				iterator2 = hairStyles.m_MaleStyles.iterator();
				while (iterator2.hasNext()) {
					hairStyle = (HairStyle)iterator2.next();
					hairStyle2 = instance.FindMaleStyle(hairStyle.name);
					if (hairStyle2 == null) {
						instance.m_MaleStyles.add(hairStyle);
					} else {
						if (DebugLog.isEnabled(DebugType.Clothing)) {
							DebugLog.Clothing.println("mod \"%s\" overrides hair \"%s\"", string2, hairStyle.name);
						}

						int1 = instance.m_MaleStyles.indexOf(hairStyle2);
						instance.m_MaleStyles.set(int1, hairStyle);
					}
				}
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			instance.m_FemaleStyles.clear();
			instance.m_MaleStyles.clear();
			instance = null;
		}
	}

	public static HairStyles Parse(String string) {
		try {
			return parse(string);
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (IOException | JAXBException error) {
			ExceptionLogger.logException(error);
		}

		return null;
	}

	public static HairStyles parse(String string) throws JAXBException, IOException {
		FileInputStream fileInputStream = new FileInputStream(string);
		HairStyles hairStyles;
		try {
			JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{HairStyles.class});
			Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
			hairStyles = (HairStyles)unmarshaller.unmarshal(fileInputStream);
		} catch (Throwable throwable) {
			try {
				fileInputStream.close();
			} catch (Throwable throwable2) {
				throwable.addSuppressed(throwable2);
			}

			throw throwable;
		}

		fileInputStream.close();
		return hairStyles;
	}

	public HairStyle FindMaleStyle(String string) {
		return this.FindStyle(this.m_MaleStyles, string);
	}

	public HairStyle FindFemaleStyle(String string) {
		return this.FindStyle(this.m_FemaleStyles, string);
	}

	private HairStyle FindStyle(ArrayList arrayList, String string) {
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			HairStyle hairStyle = (HairStyle)arrayList.get(int1);
			if (hairStyle.name.equalsIgnoreCase(string)) {
				return hairStyle;
			}

			if ("".equals(string) && hairStyle.name.equalsIgnoreCase("bald")) {
				return hairStyle;
			}
		}

		return null;
	}

	public String getRandomMaleStyle(String string) {
		return HairOutfitDefinitions.instance.getRandomHaircut(string, this.m_MaleStyles);
	}

	public String getRandomFemaleStyle(String string) {
		return HairOutfitDefinitions.instance.getRandomHaircut(string, this.m_FemaleStyles);
	}

	public HairStyle getAlternateForHat(HairStyle hairStyle, String string) {
		if (!"nohair".equalsIgnoreCase(string) && !"nohairnobeard".equalsIgnoreCase(string)) {
			if (this.m_FemaleStyles.contains(hairStyle)) {
				return this.FindFemaleStyle(hairStyle.getAlternate(string));
			} else {
				return this.m_MaleStyles.contains(hairStyle) ? this.FindMaleStyle(hairStyle.getAlternate(string)) : hairStyle;
			}
		} else {
			return null;
		}
	}

	public ArrayList getAllMaleStyles() {
		return this.m_MaleStyles;
	}

	public ArrayList getAllFemaleStyles() {
		return this.m_FemaleStyles;
	}
}
