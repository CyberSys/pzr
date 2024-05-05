package zombie.core.skinnedmodel.population;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import zombie.DebugFileWatcher;
import zombie.PredicatedFileWatcher;
import zombie.ZomboidFileSystem;
import zombie.asset.AssetPath;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.gameStates.ChooseGameInfo;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.PZXmlParserException;
import zombie.util.PZXmlUtil;
import zombie.util.StringUtils;
import zombie.util.list.PZArrayUtil;


@XmlRootElement
public class OutfitManager {
	public ArrayList m_MaleOutfits = new ArrayList();
	public ArrayList m_FemaleOutfits = new ArrayList();
	@XmlTransient
	public static OutfitManager instance;
	@XmlTransient
	private final Hashtable m_cachedClothingItems = new Hashtable();
	@XmlTransient
	private final ArrayList m_clothingItemListeners = new ArrayList();
	@XmlTransient
	private final TreeMap m_femaleOutfitMap;
	@XmlTransient
	private final TreeMap m_maleOutfitMap;

	public OutfitManager() {
		this.m_femaleOutfitMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		this.m_maleOutfitMap = new TreeMap(String.CASE_INSENSITIVE_ORDER);
	}

	public static void init() {
		if (instance != null) {
			throw new IllegalStateException("OutfitManager Already Initialized.");
		} else {
			instance = tryParse("game", "media/clothing/clothing.xml");
			if (instance != null) {
				instance.loaded();
			}
		}
	}

	public static void Reset() {
		if (instance != null) {
			instance.unload();
			instance = null;
		}
	}

	private void loaded() {
		Iterator iterator = ZomboidFileSystem.instance.getModIDs().iterator();
		while (true) {
			String string;
			OutfitManager outfitManager;
			do {
				ChooseGameInfo.Mod mod;
				do {
					if (!iterator.hasNext()) {
						DebugFileWatcher.instance.add(new PredicatedFileWatcher(ZomboidFileSystem.instance.getString("media/clothing/clothing.xml"), (var0)->{
							onClothingXmlFileChanged();
						}));

						this.loadAllClothingItems();
						iterator = this.m_MaleOutfits.iterator();
						Outfit outfit;
						Iterator iterator2;
						ClothingItemReference clothingItemReference;
						while (iterator.hasNext()) {
							outfit = (Outfit)iterator.next();
							outfit.m_Immutable = true;
							for (iterator2 = outfit.m_items.iterator(); iterator2.hasNext(); clothingItemReference.m_Immutable = true) {
								clothingItemReference = (ClothingItemReference)iterator2.next();
							}
						}

						iterator = this.m_FemaleOutfits.iterator();
						while (iterator.hasNext()) {
							outfit = (Outfit)iterator.next();
							outfit.m_Immutable = true;
							for (iterator2 = outfit.m_items.iterator(); iterator2.hasNext(); clothingItemReference.m_Immutable = true) {
								clothingItemReference = (ClothingItemReference)iterator2.next();
							}
						}

						Collections.shuffle(this.m_MaleOutfits);
						Collections.shuffle(this.m_FemaleOutfits);
						return;
					}

					string = (String)iterator.next();
					mod = ChooseGameInfo.getAvailableModDetails(string);
				}		 while (mod == null);

				outfitManager = tryParse(string, "media/clothing/clothing.xml");
			}	 while (outfitManager == null);

			Iterator iterator3;
			Outfit outfit2;
			Outfit outfit3;
			for (iterator3 = outfitManager.m_MaleOutfits.iterator(); iterator3.hasNext(); this.m_maleOutfitMap.put(outfit2.m_Name, outfit2)) {
				outfit2 = (Outfit)iterator3.next();
				outfit3 = this.FindMaleOutfit(outfit2.m_Name);
				if (outfit3 == null) {
					this.m_MaleOutfits.add(outfit2);
				} else {
					if (DebugLog.isEnabled(DebugType.Clothing)) {
						DebugLog.Clothing.println("mod \"%s\" overrides male outfit \"%s\"", string, outfit2.m_Name);
					}

					this.m_MaleOutfits.set(this.m_MaleOutfits.indexOf(outfit3), outfit2);
				}
			}

			for (iterator3 = outfitManager.m_FemaleOutfits.iterator(); iterator3.hasNext(); this.m_femaleOutfitMap.put(outfit2.m_Name, outfit2)) {
				outfit2 = (Outfit)iterator3.next();
				outfit3 = this.FindFemaleOutfit(outfit2.m_Name);
				if (outfit3 == null) {
					this.m_FemaleOutfits.add(outfit2);
				} else {
					if (DebugLog.isEnabled(DebugType.Clothing)) {
						DebugLog.Clothing.println("mod \"%s\" overrides female outfit \"%s\"", string, outfit2.m_Name);
					}

					this.m_FemaleOutfits.set(this.m_FemaleOutfits.indexOf(outfit3), outfit2);
				}
			}
		}
	}

	private static void onClothingXmlFileChanged() {
		DebugLog.Clothing.println("OutfitManager.onClothingXmlFileChanged> Detected change in media/clothing/clothing.xml");
		Reload();
	}

	public static void Reload() {
		DebugLog.Clothing.println("Reloading OutfitManager");
		OutfitManager outfitManager = instance;
		instance = tryParse("game", "media/clothing/clothing.xml");
		if (instance != null) {
			instance.loaded();
		}

		if (outfitManager != null && instance != null) {
			instance.onReloaded(outfitManager);
		}
	}

	private void onReloaded(OutfitManager outfitManager) {
		PZArrayUtil.copy(this.m_clothingItemListeners, outfitManager.m_clothingItemListeners);
		outfitManager.unload();
		this.loadAllClothingItems();
	}

	private void unload() {
		Iterator iterator = this.m_cachedClothingItems.values().iterator();
		while (iterator.hasNext()) {
			OutfitManager.ClothingItemEntry clothingItemEntry = (OutfitManager.ClothingItemEntry)iterator.next();
			DebugFileWatcher.instance.remove(clothingItemEntry.m_fileWatcher);
		}

		this.m_cachedClothingItems.clear();
		this.m_clothingItemListeners.clear();
	}

	public void addClothingItemListener(IClothingItemListener iClothingItemListener) {
		if (iClothingItemListener != null) {
			if (!this.m_clothingItemListeners.contains(iClothingItemListener)) {
				this.m_clothingItemListeners.add(iClothingItemListener);
			}
		}
	}

	public void removeClothingItemListener(IClothingItemListener iClothingItemListener) {
		this.m_clothingItemListeners.remove(iClothingItemListener);
	}

	private void invokeClothingItemChangedEvent(String string) {
		Iterator iterator = this.m_clothingItemListeners.iterator();
		while (iterator.hasNext()) {
			IClothingItemListener iClothingItemListener = (IClothingItemListener)iterator.next();
			iClothingItemListener.clothingItemChanged(string);
		}
	}

	public Outfit GetRandomOutfit(boolean boolean1) {
		Outfit outfit;
		if (boolean1) {
			outfit = (Outfit)PZArrayUtil.pickRandom((List)this.m_FemaleOutfits);
		} else {
			outfit = (Outfit)PZArrayUtil.pickRandom((List)this.m_MaleOutfits);
		}

		return outfit;
	}

	public Outfit GetRandomNonProfessionalOutfit(boolean boolean1) {
		int int1 = Rand.Next(5);
		String string = "Generic0" + (int1 + 1);
		if (Rand.NextBool(4)) {
			int int2;
			if (boolean1) {
				int2 = Rand.Next(3);
				switch (int2) {
				case 0: 
					string = "Mannequin1";
					break;
				
				case 1: 
					string = "Mannequin2";
					break;
				
				case 2: 
					string = "Classy";
				
				}
			} else {
				int2 = Rand.Next(3);
				switch (int2) {
				case 0: 
					string = "Classy";
					break;
				
				case 1: 
					string = "Tourist";
					break;
				
				case 2: 
					string = "MallSecurity";
				
				}
			}
		}

		return this.GetSpecificOutfit(boolean1, string);
	}

	public Outfit GetSpecificOutfit(boolean boolean1, String string) {
		Outfit outfit;
		if (boolean1) {
			outfit = this.FindFemaleOutfit(string);
		} else {
			outfit = this.FindMaleOutfit(string);
		}

		return outfit;
	}

	private static OutfitManager tryParse(String string, String string2) {
		try {
			return parse(string, string2);
		} catch (PZXmlParserException pZXmlParserException) {
			pZXmlParserException.printStackTrace();
			return null;
		}
	}

	private static OutfitManager parse(String string, String string2) throws PZXmlParserException {
		if ("game".equals(string)) {
			String string3 = ZomboidFileSystem.instance.base.getAbsolutePath();
			string2 = string3 + File.separator + ZomboidFileSystem.processFilePath(string2, File.separatorChar);
		} else {
			String string4 = ZomboidFileSystem.instance.getModDir(string);
			string2 = string4 + File.separator + ZomboidFileSystem.processFilePath(string2, File.separatorChar);
		}

		if (!(new File(string2)).exists()) {
			return null;
		} else {
			OutfitManager outfitManager = (OutfitManager)PZXmlUtil.parse(OutfitManager.class, string2);
			if (outfitManager != null) {
				PZArrayUtil.forEach((List)outfitManager.m_MaleOutfits, (string2x)->{
					string2x.setModID(string);
				});

				PZArrayUtil.forEach((List)outfitManager.m_FemaleOutfits, (string2x)->{
					string2x.setModID(string);
				});

				PZArrayUtil.forEach((List)outfitManager.m_MaleOutfits, (string2x)->{
					outfitManager.m_maleOutfitMap.put(string2x.m_Name, string2x);
				});

				PZArrayUtil.forEach((List)outfitManager.m_FemaleOutfits, (string2x)->{
					outfitManager.m_femaleOutfitMap.put(string2x.m_Name, string2x);
				});
			}

			return outfitManager;
		}
	}

	private static void tryWrite(OutfitManager outfitManager, String string) {
		try {
			write(outfitManager, string);
		} catch (IOException | JAXBException error) {
			error.printStackTrace();
		}
	}

	private static void write(OutfitManager outfitManager, String string) throws IOException, JAXBException {
		FileOutputStream fileOutputStream = new FileOutputStream(string);
		try {
			JAXBContext jAXBContext = JAXBContext.newInstance(new Class[]{OutfitManager.class});
			Marshaller marshaller = jAXBContext.createMarshaller();
			marshaller.setProperty("jaxb.formatted.output", Boolean.TRUE);
			marshaller.marshal(outfitManager, fileOutputStream);
		} catch (Throwable throwable) {
			try {
				fileOutputStream.close();
			} catch (Throwable throwable2) {
				throwable.addSuppressed(throwable2);
			}

			throw throwable;
		}

		fileOutputStream.close();
	}

	public Outfit FindMaleOutfit(String string) {
		return (Outfit)this.m_maleOutfitMap.get(string);
	}

	public Outfit FindFemaleOutfit(String string) {
		return (Outfit)this.m_femaleOutfitMap.get(string);
	}

	private Outfit FindOutfit(ArrayList arrayList, String string) {
		Outfit outfit = null;
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			Outfit outfit2 = (Outfit)arrayList.get(int1);
			if (outfit2.m_Name.equalsIgnoreCase(string)) {
				outfit = outfit2;
				break;
			}
		}

		return outfit;
	}

	public ClothingItem getClothingItem(String string) {
		String string2 = ZomboidFileSystem.instance.getFilePathFromGuid(string);
		if (string2 == null) {
			return null;
		} else {
			OutfitManager.ClothingItemEntry clothingItemEntry = (OutfitManager.ClothingItemEntry)this.m_cachedClothingItems.get(string);
			if (clothingItemEntry == null) {
				clothingItemEntry = new OutfitManager.ClothingItemEntry();
				clothingItemEntry.m_filePath = string2;
				clothingItemEntry.m_guid = string;
				clothingItemEntry.m_item = null;
				this.m_cachedClothingItems.put(string, clothingItemEntry);
			}

			if (clothingItemEntry.m_item != null) {
				clothingItemEntry.m_item.m_GUID = string;
				return clothingItemEntry.m_item;
			} else {
				try {
					String string3 = ZomboidFileSystem.instance.resolveFileOrGUID(string2);
					clothingItemEntry.m_item = (ClothingItem)ClothingItemAssetManager.instance.load(new AssetPath(string3));
					clothingItemEntry.m_item.m_Name = this.extractClothingItemName(string2);
					clothingItemEntry.m_item.m_GUID = string;
				} catch (Exception exception) {
					System.err.println("Failed to load ClothingItem: " + string2);
					ExceptionLogger.logException(exception);
					return null;
				}

				if (clothingItemEntry.m_fileWatcher == null) {
					String string4 = clothingItemEntry.m_filePath;
					string4 = ZomboidFileSystem.instance.getString(string4);
					clothingItemEntry.m_fileWatcher = new PredicatedFileWatcher(string4, (string2x)->{
						this.onClothingItemFileChanged(clothingItemEntry);
					});

					DebugFileWatcher.instance.add(clothingItemEntry.m_fileWatcher);
				}

				return clothingItemEntry.m_item;
			}
		}
	}

	private String extractClothingItemName(String string) {
		String string2 = StringUtils.trimPrefix(string, "media/clothing/clothingItems/");
		string2 = StringUtils.trimSuffix(string2, ".xml");
		return string2;
	}

	private void onClothingItemFileChanged(OutfitManager.ClothingItemEntry clothingItemEntry) {
		ClothingItemAssetManager.instance.reload(clothingItemEntry.m_item);
	}

	public void onClothingItemStateChanged(ClothingItem clothingItem) {
		if (clothingItem.isReady()) {
			this.invokeClothingItemChangedEvent(clothingItem.m_GUID);
		}
	}

	public void loadAllClothingItems() {
		ArrayList arrayList = ScriptManager.instance.getAllItems();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			Item item = (Item)arrayList.get(int1);
			String string;
			if (item.replacePrimaryHand != null) {
				string = ZomboidFileSystem.instance.getGuidFromFilePath("media/clothing/clothingItems/" + item.replacePrimaryHand.clothingItemName + ".xml");
				if (string != null) {
					item.replacePrimaryHand.clothingItem = this.getClothingItem(string);
				}
			}

			if (item.replaceSecondHand != null) {
				string = ZomboidFileSystem.instance.getGuidFromFilePath("media/clothing/clothingItems/" + item.replaceSecondHand.clothingItemName + ".xml");
				if (string != null) {
					item.replaceSecondHand.clothingItem = this.getClothingItem(string);
				}
			}

			if (!StringUtils.isNullOrWhitespace(item.getClothingItem())) {
				string = ZomboidFileSystem.instance.getGuidFromFilePath("media/clothing/clothingItems/" + item.getClothingItem() + ".xml");
				if (string != null) {
					ClothingItem clothingItem = this.getClothingItem(string);
					item.setClothingItemAsset(clothingItem);
				}
			}
		}
	}

	public boolean isLoadingClothingItems() {
		Iterator iterator = this.m_cachedClothingItems.values().iterator();
		OutfitManager.ClothingItemEntry clothingItemEntry;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			clothingItemEntry = (OutfitManager.ClothingItemEntry)iterator.next();
		} while (!clothingItemEntry.m_item.isEmpty());

		return true;
	}

	public void debugOutfits() {
		this.debugOutfits(this.m_FemaleOutfits);
		this.debugOutfits(this.m_MaleOutfits);
	}

	private void debugOutfits(ArrayList arrayList) {
		Iterator iterator = arrayList.iterator();
		while (iterator.hasNext()) {
			Outfit outfit = (Outfit)iterator.next();
			this.debugOutfit(outfit);
		}
	}

	private void debugOutfit(Outfit outfit) {
		String string = null;
		Iterator iterator = outfit.m_items.iterator();
		while (iterator.hasNext()) {
			ClothingItemReference clothingItemReference = (ClothingItemReference)iterator.next();
			ClothingItem clothingItem = this.getClothingItem(clothingItemReference.itemGUID);
			if (clothingItem != null && !clothingItem.isEmpty()) {
				String string2 = ScriptManager.instance.getItemTypeForClothingItem(clothingItem.m_Name);
				if (string2 != null) {
					Item item = ScriptManager.instance.getItem(string2);
					if (item != null && item.getType() == Item.Type.Container) {
						String string3 = StringUtils.isNullOrWhitespace(item.getBodyLocation()) ? item.CanBeEquipped : item.getBodyLocation();
						if (string != null && string.equals(string3)) {
							DebugLog.Clothing.warn("outfit \"%s\" has multiple bags", outfit.m_Name);
						}

						string = string3;
					}
				}
			}
		}
	}

	private static final class ClothingItemEntry {
		public ClothingItem m_item;
		public String m_guid;
		public String m_filePath;
		public PredicatedFileWatcher m_fileWatcher;
	}
}
