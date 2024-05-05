package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.ZomboidFileSystem;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;


@XmlRootElement
public class ClothingDecals {
	@XmlElement(name = "group")
	public final ArrayList m_Groups = new ArrayList();
	@XmlTransient
	public static ClothingDecals instance;
	private final HashMap m_cachedDecals = new HashMap();

	public static void init() {
		if (instance != null) {
			throw new IllegalStateException("ClothingDecals Already Initialized.");
		} else {
			String string = ZomboidFileSystem.instance.base.getAbsolutePath();
			instance = Parse(string + File.separator + ZomboidFileSystem.processFilePath("media/clothing/clothingDecals.xml", File.separatorChar));
			if (instance != null) {
				Iterator iterator = ZomboidFileSystem.instance.getModIDs().iterator();
				while (true) {
					String string2;
					ClothingDecals clothingDecals;
					do {
						ChooseGameInfo.Mod mod;
						do {
							if (!iterator.hasNext()) {
								return;
							}

							string2 = (String)iterator.next();
							mod = ChooseGameInfo.getAvailableModDetails(string2);
						}				 while (mod == null);

						String string3 = ZomboidFileSystem.instance.getModDir(string2);
						clothingDecals = Parse(string3 + File.separator + ZomboidFileSystem.processFilePath("media/clothing/clothingDecals.xml", File.separatorChar));
					}			 while (clothingDecals == null);

					Iterator iterator2 = clothingDecals.m_Groups.iterator();
					while (iterator2.hasNext()) {
						ClothingDecalGroup clothingDecalGroup = (ClothingDecalGroup)iterator2.next();
						ClothingDecalGroup clothingDecalGroup2 = instance.FindGroup(clothingDecalGroup.m_Name);
						if (clothingDecalGroup2 == null) {
							instance.m_Groups.add(clothingDecalGroup);
						} else {
							if (DebugLog.isEnabled(DebugType.Clothing)) {
								DebugLog.Clothing.println("mod \"%s\" overrides decal group \"%s\"", string2, clothingDecalGroup.m_Name);
							}

							int int1 = instance.m_Groups.indexOf(clothingDecalGroup2);
							instance.m_Groups.set(int1, clothingDecalGroup);
						}
					}
				}
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			instance.m_cachedDecals.clear();
			instance.m_Groups.clear();
			instance = null;
		}
	}

	public static ClothingDecals Parse(String string) {
		try {
			return parse(string);
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (JAXBException | IOException error) {
			ExceptionLogger.logException(error);
		}

		return null;
	}

	public static ClothingDecals parse(String string) throws JAXBException, IOException {
		FileInputStream fileInputStream = new FileInputStream(string);
		ClothingDecals clothingDecals;
		try {
			JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{ClothingDecals.class});
			Unmarshaller unmarshaller = jAXBContext.createUnmarshaller();
			clothingDecals = (ClothingDecals)unmarshaller.unmarshal(fileInputStream);
		} catch (Throwable throwable) {
			try {
				fileInputStream.close();
			} catch (Throwable throwable2) {
				throwable.addSuppressed(throwable2);
			}

			throw throwable;
		}

		fileInputStream.close();
		return clothingDecals;
	}

	public ClothingDecal getDecal(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			ClothingDecals.CachedDecal cachedDecal = (ClothingDecals.CachedDecal)this.m_cachedDecals.get(string);
			if (cachedDecal == null) {
				cachedDecal = new ClothingDecals.CachedDecal();
				this.m_cachedDecals.put(string, cachedDecal);
			}

			if (cachedDecal.m_decal != null) {
				return cachedDecal.m_decal;
			} else {
				String string2 = ZomboidFileSystem.instance.getString("media/clothing/clothingDecals/" + string + ".xml");
				try {
					cachedDecal.m_decal = (ClothingDecal)PZXmlUtil.parse(ClothingDecal.class, string2);
					cachedDecal.m_decal.name = string;
				} catch (PZXmlParserException pZXmlParserException) {
					System.err.println("Failed to load ClothingDecal: " + string2);
					ExceptionLogger.logException(pZXmlParserException);
					return null;
				}

				return cachedDecal.m_decal;
			}
		}
	}

	public ClothingDecalGroup FindGroup(String string) {
		if (StringUtils.isNullOrWhitespace(string)) {
			return null;
		} else {
			for (int int1 = 0; int1 < this.m_Groups.size(); ++int1) {
				ClothingDecalGroup clothingDecalGroup = (ClothingDecalGroup)this.m_Groups.get(int1);
				if (clothingDecalGroup.m_Name.equalsIgnoreCase(string)) {
					return clothingDecalGroup;
				}
			}

			return null;
		}
	}

	public String getRandomDecal(String string) {
		ClothingDecalGroup clothingDecalGroup = this.FindGroup(string);
		return clothingDecalGroup == null ? null : clothingDecalGroup.getRandomDecal();
	}

	private static final class CachedDecal {
		ClothingDecal m_decal;
	}
}
