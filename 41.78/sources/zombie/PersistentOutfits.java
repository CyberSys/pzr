package zombie;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.ZombiesZoneDefinition;
import zombie.characters.AttachedItems.AttachedWeaponDefinitions;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.logger.ExceptionLogger;
import zombie.core.skinnedmodel.population.Outfit;
import zombie.core.skinnedmodel.population.OutfitManager;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.iso.IsoWorld;
import zombie.iso.SliceY;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.randomizedWorld.randomizedVehicleStory.RandomizedVehicleStoryBase;
import zombie.scripting.objects.Item;
import zombie.util.Type;
import zombie.util.list.PZArrayUtil;


public class PersistentOutfits {
	public static final PersistentOutfits instance = new PersistentOutfits();
	public static final int INVALID_ID = 0;
	public static final int FEMALE_BIT = Integer.MIN_VALUE;
	public static final int NO_HAT_BIT = 32768;
	private static final int FILE_VERSION_1 = 1;
	private static final int FILE_VERSION_LATEST = 1;
	private static final byte[] FILE_MAGIC = new byte[]{80, 83, 84, 90};
	private static final int NUM_SEEDS = 500;
	private final long[] m_seeds = new long[500];
	private final ArrayList m_outfitNames = new ArrayList();
	private final PersistentOutfits.DataList m_all = new PersistentOutfits.DataList();
	private final PersistentOutfits.DataList m_female = new PersistentOutfits.DataList();
	private final PersistentOutfits.DataList m_male = new PersistentOutfits.DataList();
	private final TreeMap m_outfitToData;
	private final TreeMap m_outfitToFemale;
	private final TreeMap m_outfitToMale;
	private static final ItemVisuals tempItemVisuals = new ItemVisuals();

	public PersistentOutfits() {
		this.m_outfitToData = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		this.m_outfitToFemale = new TreeMap(String.CASE_INSENSITIVE_ORDER);
		this.m_outfitToMale = new TreeMap(String.CASE_INSENSITIVE_ORDER);
	}

	public void init() {
		this.m_all.clear();
		this.m_female.clear();
		this.m_male.clear();
		this.m_outfitToData.clear();
		this.m_outfitToFemale.clear();
		this.m_outfitToMale.clear();
		this.m_outfitNames.clear();
		if (!GameClient.bClient) {
			for (int int1 = 0; int1 < 500; ++int1) {
				this.m_seeds[int1] = (long)Rand.Next(Integer.MAX_VALUE);
			}
		}

		this.initOutfitList(OutfitManager.instance.m_FemaleOutfits, true);
		this.initOutfitList(OutfitManager.instance.m_MaleOutfits, false);
		this.registerCustomOutfits();
		if (!GameClient.bClient) {
			this.load();
			this.save();
		}
	}

	private void initOutfitList(ArrayList arrayList, boolean boolean1) {
		ArrayList arrayList2 = new ArrayList(arrayList);
		arrayList2.sort((var0,arrayListx)->{
			return var0.m_Name.compareTo(arrayListx.m_Name);
		});
		Iterator iterator = arrayList2.iterator();
		while (iterator.hasNext()) {
			Outfit outfit = (Outfit)iterator.next();
			this.initOutfit(outfit.m_Name, boolean1, true, PersistentOutfits::ApplyOutfit);
		}
	}

	private void initOutfit(String string, boolean boolean1, boolean boolean2, PersistentOutfits.IOutfitter ioutfitter) {
		TreeMap treeMap = boolean1 ? this.m_outfitToFemale : this.m_outfitToMale;
		PersistentOutfits.Data data = (PersistentOutfits.Data)this.m_outfitToData.get(string);
		if (data == null) {
			data = new PersistentOutfits.Data();
			data.m_index = (short)this.m_all.size();
			data.m_outfitName = string;
			data.m_useSeed = boolean2;
			data.m_outfitter = ioutfitter;
			this.m_outfitNames.add(string);
			this.m_outfitToData.put(string, data);
			this.m_all.add(data);
		}

		PersistentOutfits.DataList dataList = boolean1 ? this.m_female : this.m_male;
		dataList.add(data);
		treeMap.put(string, data);
	}

	private void registerCustomOutfits() {
		ArrayList arrayList = IsoWorld.instance.getRandomizedVehicleStoryList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			RandomizedVehicleStoryBase randomizedVehicleStoryBase = (RandomizedVehicleStoryBase)arrayList.get(int1);
			randomizedVehicleStoryBase.registerCustomOutfits();
		}

		ZombiesZoneDefinition.registerCustomOutfits();
		if (GameServer.bServer || GameClient.bClient) {
			this.registerOutfitter("ReanimatedPlayer", false, SharedDescriptors::ApplyReanimatedPlayerOutfit);
		}
	}

	public ArrayList getOutfitNames() {
		return this.m_outfitNames;
	}

	public int pickRandomFemale() {
		if (this.m_female.isEmpty()) {
			return 0;
		} else {
			String string = ((PersistentOutfits.Data)PZArrayUtil.pickRandom((List)this.m_female)).m_outfitName;
			return this.pickOutfitFemale(string);
		}
	}

	public int pickRandomMale() {
		if (this.m_male.isEmpty()) {
			return 0;
		} else {
			String string = ((PersistentOutfits.Data)PZArrayUtil.pickRandom((List)this.m_male)).m_outfitName;
			return this.pickOutfitMale(string);
		}
	}

	public int pickOutfitFemale(String string) {
		PersistentOutfits.Data data = (PersistentOutfits.Data)this.m_outfitToFemale.get(string);
		if (data == null) {
			return 0;
		} else {
			short short1 = (short)data.m_index;
			short short2 = data.m_useSeed ? (short)Rand.Next(500) : 0;
			return Integer.MIN_VALUE | short1 << 16 | short2 + 1;
		}
	}

	public int pickOutfitMale(String string) {
		PersistentOutfits.Data data = (PersistentOutfits.Data)this.m_outfitToMale.get(string);
		if (data == null) {
			return 0;
		} else {
			short short1 = (short)data.m_index;
			short short2 = data.m_useSeed ? (short)Rand.Next(500) : 0;
			return short1 << 16 | short2 + 1;
		}
	}

	public int pickOutfit(String string, boolean boolean1) {
		return boolean1 ? this.pickOutfitFemale(string) : this.pickOutfitMale(string);
	}

	public int getOutfit(int int1) {
		if (int1 == 0) {
			return 0;
		} else {
			int int2 = int1 & Integer.MIN_VALUE;
			int1 &= Integer.MAX_VALUE;
			int int3 = int1 & '耀';
			int1 &= -32769;
			short short1 = (short)(int1 >> 16);
			short short2 = (short)(int1 & '￿');
			if (short1 >= 0 && short1 < this.m_all.size()) {
				PersistentOutfits.Data data = (PersistentOutfits.Data)this.m_all.get(short1);
				if (data.m_useSeed && (short2 < 1 || short2 > 500)) {
					short2 = (short)(Rand.Next(500) + 1);
				}

				return int2 | int3 | short1 << 16 | short2;
			} else {
				return 0;
			}
		}
	}

	public void save() {
		if (!Core.getInstance().isNoSave()) {
			File file = ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin");
			try {
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				try {
					BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
					try {
						synchronized (SliceY.SliceBufferLock) {
							SliceY.SliceBuffer.clear();
							ByteBuffer byteBuffer = SliceY.SliceBuffer;
							this.save(byteBuffer);
							bufferedOutputStream.write(byteBuffer.array(), 0, byteBuffer.position());
						}
					} catch (Throwable throwable) {
						try {
							bufferedOutputStream.close();
						} catch (Throwable throwable2) {
							throwable.addSuppressed(throwable2);
						}

						throw throwable;
					}

					bufferedOutputStream.close();
				} catch (Throwable throwable3) {
					try {
						fileOutputStream.close();
					} catch (Throwable throwable4) {
						throwable3.addSuppressed(throwable4);
					}

					throw throwable3;
				}

				fileOutputStream.close();
			} catch (Exception exception) {
				ExceptionLogger.logException(exception);
			}
		}
	}

	public void save(ByteBuffer byteBuffer) {
		byteBuffer.put(FILE_MAGIC);
		byteBuffer.putInt(1);
		byteBuffer.putShort((short)500);
		for (int int1 = 0; int1 < 500; ++int1) {
			byteBuffer.putLong(this.m_seeds[int1]);
		}
	}

	public void load() {
		File file = ZomboidFileSystem.instance.getFileInCurrentSave("z_outfits.bin");
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			try {
				BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
				try {
					synchronized (SliceY.SliceBufferLock) {
						SliceY.SliceBuffer.clear();
						ByteBuffer byteBuffer = SliceY.SliceBuffer;
						int int1 = bufferedInputStream.read(byteBuffer.array());
						byteBuffer.limit(int1);
						this.load(byteBuffer);
					}
				} catch (Throwable throwable) {
					try {
						bufferedInputStream.close();
					} catch (Throwable throwable2) {
						throwable.addSuppressed(throwable2);
					}

					throw throwable;
				}

				bufferedInputStream.close();
			} catch (Throwable throwable3) {
				try {
					fileInputStream.close();
				} catch (Throwable throwable4) {
					throwable3.addSuppressed(throwable4);
				}

				throw throwable3;
			}

			fileInputStream.close();
		} catch (FileNotFoundException fileNotFoundException) {
		} catch (Exception exception) {
			ExceptionLogger.logException(exception);
		}
	}

	public void load(ByteBuffer byteBuffer) throws IOException {
		byte[] byteArray = new byte[4];
		byteBuffer.get(byteArray);
		if (!Arrays.equals(byteArray, FILE_MAGIC)) {
			throw new IOException("not magic");
		} else {
			int int1 = byteBuffer.getInt();
			if (int1 >= 1 && int1 <= 1) {
				short short1 = byteBuffer.getShort();
				for (int int2 = 0; int2 < short1; ++int2) {
					if (int2 < 500) {
						this.m_seeds[int2] = byteBuffer.getLong();
					}
				}
			}
		}
	}

	public void registerOutfitter(String string, boolean boolean1, PersistentOutfits.IOutfitter ioutfitter) {
		this.initOutfit(string, true, boolean1, ioutfitter);
		this.initOutfit(string, false, boolean1, ioutfitter);
	}

	private static void ApplyOutfit(int int1, String string, IsoGameCharacter gameCharacter) {
		instance.applyOutfit(int1, string, gameCharacter);
	}

	private void applyOutfit(int int1, String string, IsoGameCharacter gameCharacter) {
		boolean boolean1 = (int1 & Integer.MIN_VALUE) != 0;
		int1 &= Integer.MAX_VALUE;
		short short1 = (short)(int1 >> 16);
		PersistentOutfits.Data data = (PersistentOutfits.Data)this.m_all.get(short1);
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(gameCharacter, IsoZombie.class);
		if (zombie != null) {
			zombie.setFemaleEtc(boolean1);
		}

		gameCharacter.dressInNamedOutfit(data.m_outfitName);
		if (zombie != null && gameCharacter.doDirtBloodEtc) {
			AttachedWeaponDefinitions.instance.addRandomAttachedWeapon(zombie);
			zombie.addRandomBloodDirtHolesEtc();
		}

		this.removeFallenHat(int1, gameCharacter);
	}

	public boolean isHatFallen(IsoGameCharacter gameCharacter) {
		return this.isHatFallen(gameCharacter.getPersistentOutfitID());
	}

	public boolean isHatFallen(int int1) {
		return (int1 & '耀') != 0;
	}

	public void setFallenHat(IsoGameCharacter gameCharacter, boolean boolean1) {
		int int1 = gameCharacter.getPersistentOutfitID();
		if (int1 != 0) {
			if (boolean1) {
				int1 |= 32768;
			} else {
				int1 &= -32769;
			}

			gameCharacter.setPersistentOutfitID(int1, gameCharacter.isPersistentOutfitInit());
		}
	}

	public boolean removeFallenHat(int int1, IsoGameCharacter gameCharacter) {
		if ((int1 & '耀') == 0) {
			return false;
		} else if (gameCharacter.isUsingWornItems()) {
			return false;
		} else {
			boolean boolean1 = false;
			gameCharacter.getItemVisuals(tempItemVisuals);
			for (int int2 = 0; int2 < tempItemVisuals.size(); ++int2) {
				ItemVisual itemVisual = (ItemVisual)tempItemVisuals.get(int2);
				Item item = itemVisual.getScriptItem();
				if (item != null && item.getChanceToFall() > 0) {
					gameCharacter.getItemVisuals().remove(itemVisual);
					boolean1 = true;
				}
			}

			return boolean1;
		}
	}

	public void dressInOutfit(IsoGameCharacter gameCharacter, int int1) {
		int1 = this.getOutfit(int1);
		if (int1 != 0) {
			int int2 = int1 & 2147450879;
			short short1 = (short)(int2 >> 16);
			short short2 = (short)(int2 & '￿');
			PersistentOutfits.Data data = (PersistentOutfits.Data)this.m_all.get(short1);
			if (data.m_useSeed) {
				OutfitRNG.setSeed(this.m_seeds[short2 - 1]);
			}

			data.m_outfitter.accept(int1, data.m_outfitName, gameCharacter);
		}
	}

	private static final class DataList extends ArrayList {
	}

	public interface IOutfitter {

		void accept(int int1, String string, IsoGameCharacter gameCharacter);
	}

	private static final class Data {
		int m_index;
		String m_outfitName;
		boolean m_useSeed = true;
		PersistentOutfits.IOutfitter m_outfitter;
	}
}
