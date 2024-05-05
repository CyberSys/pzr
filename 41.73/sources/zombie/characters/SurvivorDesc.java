package zombie.characters;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.WornItems.BodyLocations;
import zombie.characters.WornItems.WornItems;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.ObservationFactory;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.skinnedmodel.population.OutfitRNG;
import zombie.core.skinnedmodel.visual.HumanVisual;
import zombie.core.skinnedmodel.visual.IHumanVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoWorld;


public final class SurvivorDesc implements IHumanVisual {
	public final HumanVisual humanVisual = new HumanVisual(this);
	public final WornItems wornItems = new WornItems(BodyLocations.getGroup("Human"));
	SurvivorGroup group = new SurvivorGroup();
	private static int IDCount = 0;
	public static final ArrayList TrouserCommonColors = new ArrayList();
	public static final ArrayList HairCommonColors = new ArrayList();
	private final HashMap xpBoostMap = new HashMap();
	private KahluaTable metaTable;
	public String Profession = "";
	protected String forename = "None";
	protected int ID = 0;
	protected IsoGameCharacter Instance = null;
	private boolean bFemale = true;
	protected String surname = "None";
	private String InventoryScript = null;
	protected String torso = "Base_Torso";
	protected final HashMap MetCount = new HashMap();
	protected float bravery = 1.0F;
	protected float loner = 0.0F;
	protected float aggressiveness = 1.0F;
	protected float compassion = 1.0F;
	protected float temper = 0.0F;
	protected float friendliness = 0.0F;
	private float favourindoors = 0.0F;
	protected float loyalty = 0.0F;
	public final ArrayList extra = new ArrayList();
	private final ArrayList Observations = new ArrayList(0);
	private SurvivorFactory.SurvivorType type;
	public boolean bDead;

	public HumanVisual getHumanVisual() {
		return this.humanVisual;
	}

	public void getItemVisuals(ItemVisuals itemVisuals) {
		this.wornItems.getItemVisuals(itemVisuals);
	}

	public boolean isFemale() {
		return this.bFemale;
	}

	public boolean isZombie() {
		return false;
	}

	public boolean isSkeleton() {
		return false;
	}

	public WornItems getWornItems() {
		return this.wornItems;
	}

	public void setWornItem(String string, InventoryItem inventoryItem) {
		this.wornItems.setItem(string, inventoryItem);
	}

	public InventoryItem getWornItem(String string) {
		return this.wornItems.getItem(string);
	}

	public void dressInNamedOutfit(String string) {
		ItemVisuals itemVisuals = new ItemVisuals();
		this.getHumanVisual().dressInNamedOutfit(string, itemVisuals);
		this.getWornItems().setFromItemVisuals(itemVisuals);
	}

	public SurvivorGroup getGroup() {
		return this.group;
	}

	public boolean isLeader() {
		return this.group.getLeader() == this;
	}

	public static int getIDCount() {
		return IDCount;
	}

	public void setProfessionSkills(ProfessionFactory.Profession profession) {
		this.getXPBoostMap().clear();
		this.getXPBoostMap().putAll(profession.XPBoostMap);
	}

	public HashMap getXPBoostMap() {
		return this.xpBoostMap;
	}

	public KahluaTable getMeta() {
		if (this.metaTable == null) {
			this.metaTable = (KahluaTable)LuaManager.caller.pcall(LuaManager.thread, LuaManager.env.rawget("createMetaSurvivor"), (Object)this)[1];
		}

		return this.metaTable;
	}

	public int getCalculatedToughness() {
		this.metaTable = this.getMeta();
		KahluaTable kahluaTable = (KahluaTable)LuaManager.env.rawget("MetaSurvivor");
		Double Double1 = (Double)LuaManager.caller.pcall(LuaManager.thread, kahluaTable.rawget("getCalculatedToughness"), (Object)this.metaTable)[1];
		return Double1.intValue();
	}

	public static void setIDCount(int int1) {
		IDCount = int1;
	}

	public boolean isDead() {
		return this.bDead;
	}

	public SurvivorDesc() {
		this.type = SurvivorFactory.SurvivorType.Neutral;
		this.ID = IDCount++;
		IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
		this.doStats();
	}

	public SurvivorDesc(boolean boolean1) {
		this.type = SurvivorFactory.SurvivorType.Neutral;
		this.ID = IDCount++;
		this.doStats();
	}

	public SurvivorDesc(SurvivorDesc survivorDesc) {
		this.type = SurvivorFactory.SurvivorType.Neutral;
		this.aggressiveness = survivorDesc.aggressiveness;
		this.bDead = survivorDesc.bDead;
		this.bFemale = survivorDesc.bFemale;
		this.bravery = survivorDesc.bravery;
		this.compassion = survivorDesc.compassion;
		this.extra.addAll(survivorDesc.extra);
		this.favourindoors = survivorDesc.favourindoors;
		this.forename = survivorDesc.forename;
		this.friendliness = survivorDesc.friendliness;
		this.InventoryScript = survivorDesc.InventoryScript;
		this.loner = survivorDesc.loner;
		this.loyalty = survivorDesc.loyalty;
		this.Profession = survivorDesc.Profession;
		this.surname = survivorDesc.surname;
		this.temper = survivorDesc.temper;
		this.torso = survivorDesc.torso;
		this.type = survivorDesc.type;
	}

	public void meet(SurvivorDesc survivorDesc) {
		if (this.MetCount.containsKey(survivorDesc.ID)) {
			this.MetCount.put(survivorDesc.ID, (Integer)this.MetCount.get(survivorDesc.ID) + 1);
		} else {
			this.MetCount.put(survivorDesc.ID, 1);
		}

		if (survivorDesc.MetCount.containsKey(this.ID)) {
			survivorDesc.MetCount.put(this.ID, (Integer)survivorDesc.MetCount.get(this.ID) + 1);
		} else {
			survivorDesc.MetCount.put(this.ID, 1);
		}
	}

	public boolean hasObservation(String string) {
		for (int int1 = 0; int1 < this.Observations.size(); ++int1) {
			if (string.equals(((ObservationFactory.Observation)this.Observations.get(int1)).getTraitID())) {
				return true;
			}
		}

		return false;
	}

	private void savePerk(ByteBuffer byteBuffer, PerkFactory.Perk perk) throws IOException {
		GameWindow.WriteStringUTF(byteBuffer, perk == null ? "" : perk.getId());
	}

	private PerkFactory.Perk loadPerk(ByteBuffer byteBuffer, int int1) throws IOException {
		PerkFactory.Perk perk;
		if (int1 >= 152) {
			String string = GameWindow.ReadStringUTF(byteBuffer);
			perk = PerkFactory.Perks.FromString(string);
			return perk == PerkFactory.Perks.MAX ? null : perk;
		} else {
			int int2 = byteBuffer.getInt();
			if (int2 >= 0 && int2 < PerkFactory.Perks.MAX.index()) {
				perk = PerkFactory.Perks.fromIndex(int2);
				return perk == PerkFactory.Perks.MAX ? null : perk;
			} else {
				return null;
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, IsoGameCharacter gameCharacter) throws IOException {
		this.ID = byteBuffer.getInt();
		IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
		this.forename = GameWindow.ReadString(byteBuffer);
		this.surname = GameWindow.ReadString(byteBuffer);
		this.torso = GameWindow.ReadString(byteBuffer);
		this.bFemale = byteBuffer.getInt() == 1;
		this.Profession = GameWindow.ReadString(byteBuffer);
		this.doStats();
		if (IDCount < this.ID) {
			IDCount = this.ID;
		}

		this.extra.clear();
		int int2;
		int int3;
		if (byteBuffer.getInt() == 1) {
			int2 = byteBuffer.getInt();
			for (int3 = 0; int3 < int2; ++int3) {
				String string = GameWindow.ReadString(byteBuffer);
				this.extra.add(string);
			}
		}

		int2 = byteBuffer.getInt();
		for (int3 = 0; int3 < int2; ++int3) {
			PerkFactory.Perk perk = this.loadPerk(byteBuffer, int1);
			int int4 = byteBuffer.getInt();
			if (perk != null) {
				this.getXPBoostMap().put(perk, int4);
			}
		}

		this.Instance = gameCharacter;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putInt(this.ID);
		GameWindow.WriteString(byteBuffer, this.forename);
		GameWindow.WriteString(byteBuffer, this.surname);
		GameWindow.WriteString(byteBuffer, this.torso);
		byteBuffer.putInt(this.bFemale ? 1 : 0);
		GameWindow.WriteString(byteBuffer, this.Profession);
		if (!this.extra.isEmpty()) {
			byteBuffer.putInt(1);
			byteBuffer.putInt(this.extra.size());
			for (int int1 = 0; int1 < this.extra.size(); ++int1) {
				String string = (String)this.extra.get(int1);
				GameWindow.WriteString(byteBuffer, string);
			}
		} else {
			byteBuffer.putInt(0);
		}

		byteBuffer.putInt(this.getXPBoostMap().size());
		Iterator iterator = this.getXPBoostMap().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			this.savePerk(byteBuffer, (PerkFactory.Perk)entry.getKey());
			byteBuffer.putInt((Integer)entry.getValue());
		}
	}

	public void loadCompact(ByteBuffer byteBuffer) {
		this.ID = -1;
		this.torso = GameWindow.ReadString(byteBuffer);
		this.bFemale = byteBuffer.get() == 1;
		this.extra.clear();
		if (byteBuffer.get() == 1) {
			byte byte1 = byteBuffer.get();
			for (int int1 = 0; int1 < byte1; ++int1) {
				String string = GameWindow.ReadString(byteBuffer);
				this.extra.add(string);
			}
		}
	}

	public void saveCompact(ByteBuffer byteBuffer) throws UnsupportedEncodingException {
		GameWindow.WriteString(byteBuffer, this.torso);
		byteBuffer.put((byte)(this.bFemale ? 1 : 0));
		if (!this.extra.isEmpty()) {
			byteBuffer.put((byte)1);
			byteBuffer.put((byte)this.extra.size());
			Iterator iterator = this.extra.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				GameWindow.WriteString(byteBuffer, string);
			}
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void addObservation(String string) {
		ObservationFactory.Observation observation = ObservationFactory.getObservation(string);
		if (observation != null) {
			this.Observations.add(observation);
		}
	}

	private void doStats() {
		this.bravery = Rand.Next(2) == 0 ? 10.0F : 0.0F;
		this.aggressiveness = Rand.Next(2) == 0 ? 10.0F : 0.0F;
		this.compassion = 10.0F - this.aggressiveness;
		this.loner = Rand.Next(2) == 0 ? 10.0F : 0.0F;
		this.temper = Rand.Next(2) == 0 ? 10.0F : 0.0F;
		this.friendliness = 10.0F - this.loner;
		this.favourindoors = Rand.Next(2) == 0 ? 10.0F : 0.0F;
		this.loyalty = Rand.Next(2) == 0 ? 10.0F : 0.0F;
	}

	public int getMetCount(SurvivorDesc survivorDesc) {
		return this.MetCount.containsKey(survivorDesc.ID) ? (Integer)this.MetCount.get(survivorDesc.ID) : 0;
	}

	public String getForename() {
		return this.forename;
	}

	public void setForename(String string) {
		this.forename = string;
	}

	public int getID() {
		return this.ID;
	}

	public void setID(int int1) {
		this.ID = int1;
	}

	public IsoGameCharacter getInstance() {
		return this.Instance;
	}

	public void setInstance(IsoGameCharacter gameCharacter) {
		this.Instance = gameCharacter;
	}

	public String getSurname() {
		return this.surname;
	}

	public void setSurname(String string) {
		this.surname = string;
	}

	public String getInventoryScript() {
		return this.InventoryScript;
	}

	public void setInventoryScript(String string) {
		this.InventoryScript = string;
	}

	public String getTorso() {
		return this.torso;
	}

	public void setTorso(String string) {
		this.torso = string;
	}

	public HashMap getMetCount() {
		return this.MetCount;
	}

	public float getBravery() {
		return this.bravery;
	}

	public void setBravery(float float1) {
		this.bravery = float1;
	}

	public float getLoner() {
		return this.loner;
	}

	public void setLoner(float float1) {
		this.loner = float1;
	}

	public float getAggressiveness() {
		return this.aggressiveness;
	}

	public void setAggressiveness(float float1) {
		this.aggressiveness = float1;
	}

	public float getCompassion() {
		return this.compassion;
	}

	public void setCompassion(float float1) {
		this.compassion = float1;
	}

	public float getTemper() {
		return this.temper;
	}

	public void setTemper(float float1) {
		this.temper = float1;
	}

	public float getFriendliness() {
		return this.friendliness;
	}

	public void setFriendliness(float float1) {
		this.friendliness = float1;
	}

	public float getFavourindoors() {
		return this.favourindoors;
	}

	public void setFavourindoors(float float1) {
		this.favourindoors = float1;
	}

	public float getLoyalty() {
		return this.loyalty;
	}

	public void setLoyalty(float float1) {
		this.loyalty = float1;
	}

	public String getProfession() {
		return this.Profession;
	}

	public void setProfession(String string) {
		this.Profession = string;
	}

	public boolean isAggressive() {
		Iterator iterator = this.Observations.iterator();
		ObservationFactory.Observation observation;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			observation = (ObservationFactory.Observation)iterator.next();
		} while (!"Aggressive".equals(observation.getTraitID()));

		return true;
	}

	public ArrayList getObservations() {
		return this.Observations;
	}

	public boolean isFriendly() {
		Iterator iterator = this.Observations.iterator();
		ObservationFactory.Observation observation;
		do {
			if (!iterator.hasNext()) {
				return false;
			}

			observation = (ObservationFactory.Observation)iterator.next();
		} while (!"Friendly".equals(observation.getTraitID()));

		return true;
	}

	public SurvivorFactory.SurvivorType getType() {
		return this.type;
	}

	public void setType(SurvivorFactory.SurvivorType survivorType) {
		this.type = survivorType;
	}

	public void setFemale(boolean boolean1) {
		this.bFemale = boolean1;
	}

	public ArrayList getExtras() {
		return this.extra;
	}

	public ArrayList getCommonHairColor() {
		return HairCommonColors;
	}

	public static void addTrouserColor(ColorInfo colorInfo) {
		TrouserCommonColors.add(colorInfo.toColor());
	}

	public static void addHairColor(ColorInfo colorInfo) {
		HairCommonColors.add(colorInfo.toImmutableColor());
	}

	public static Color getRandomSkinColor() {
		return OutfitRNG.Next(3) == 0 ? new Color(OutfitRNG.Next(0.5F, 0.6F), OutfitRNG.Next(0.3F, 0.4F), OutfitRNG.Next(0.15F, 0.23F)) : new Color(OutfitRNG.Next(0.9F, 1.0F), OutfitRNG.Next(0.75F, 0.88F), OutfitRNG.Next(0.45F, 0.58F));
	}
}
