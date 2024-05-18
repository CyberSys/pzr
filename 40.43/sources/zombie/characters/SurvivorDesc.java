package zombie.characters;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Map.Entry;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameWindow;
import zombie.Lua.LuaManager;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.skills.PerkFactory;
import zombie.characters.traits.ObservationFactory;
import zombie.core.Color;
import zombie.core.Rand;
import zombie.core.textures.ColorInfo;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoWorld;


public class SurvivorDesc {
	protected static int IDCount = 0;
	public Color trouserColor;
	public Color topColor;
	public Color hairColor;
	public Color skinColor;
	public static ArrayList TrouserCommonColors = new ArrayList();
	public static ArrayList HairCommonColors = new ArrayList();
	public HashMap xpBoostMap;
	KahluaTable metaTable;
	public String Profession = "";
	protected SurvivorGroup Group;
	protected Stack ChildrenList = new Stack();
	protected String forename = "None";
	protected int ID = 0;
	protected IsoGameCharacter Instance = null;
	protected Stack ParentList = new Stack();
	protected Stack SiblingList = new Stack();
	private boolean bFemale = true;
	protected String surname = "None";
	protected String InventoryScript = null;
	protected String legs = "Base_Legs";
	protected String torso = "Base_Torso";
	protected String head = "Base_Head1";
	public String top = "Shirt";
	public String bottoms = "Trousers";
	public String shoes = "Shoes1";
	public String shoespal = null;
	public String bottomspal = "Trousers_Grey";
	public String toppal = "Shirt_Blue";
	public String skinpal = "Skin_01";
	protected HashMap MetCount = new HashMap();
	protected float bravery = 1.0F;
	protected float loner = 0.0F;
	protected float aggressiveness = 1.0F;
	protected float compassion = 1.0F;
	protected float temper = 0.0F;
	protected float friendliness = 0.0F;
	protected float favourindoors = 0.0F;
	protected float loyalty = 0.0F;
	public String hair = "none";
	public String hairNoColor = "none";
	private int hairNumber = 0;
	public ArrayList extra = new ArrayList();
	public String beard = "none";
	public ArrayList Observations = new ArrayList(0);
	private SurvivorFactory.SurvivorType type;
	public boolean bDead;
	public int torsoNumber;
	private int beardNumber;
	public String beardNoColor;

	public static int getIDCount() {
		return IDCount;
	}

	public void setProfessionSkills(ProfessionFactory.Profession profession) {
		this.xpBoostMap = profession.XPBoostMap;
	}

	public HashMap getXPBoostMap() {
		if (this.xpBoostMap == null) {
			this.xpBoostMap = new HashMap();
		}

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

	public boolean isFemale() {
		return this.bFemale;
	}

	public boolean isDead() {
		return this.bDead;
	}

	public SurvivorDesc() {
		this.type = SurvivorFactory.SurvivorType.Neutral;
		this.beardNumber = 0;
		this.ID = IDCount++;
		IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
		this.doStats();
	}

	public SurvivorDesc(boolean boolean1) {
		this.type = SurvivorFactory.SurvivorType.Neutral;
		this.beardNumber = 0;
		this.ID = IDCount++;
		this.doStats();
	}

	public SurvivorDesc(SurvivorDesc survivorDesc) {
		this.type = SurvivorFactory.SurvivorType.Neutral;
		this.beardNumber = 0;
		this.aggressiveness = survivorDesc.aggressiveness;
		this.bDead = survivorDesc.bDead;
		this.beard = survivorDesc.beard;
		this.beardNoColor = survivorDesc.beardNoColor;
		this.beardNumber = survivorDesc.beardNumber;
		this.bFemale = survivorDesc.bFemale;
		this.bottoms = survivorDesc.bottoms;
		this.bottomspal = survivorDesc.bottomspal;
		this.bravery = survivorDesc.bravery;
		this.compassion = survivorDesc.compassion;
		this.extra.addAll(survivorDesc.extra);
		this.favourindoors = survivorDesc.favourindoors;
		this.forename = survivorDesc.forename;
		this.friendliness = survivorDesc.friendliness;
		this.hair = survivorDesc.hair;
		this.hairColor = new Color(survivorDesc.hairColor);
		this.hairNoColor = survivorDesc.hairNoColor;
		this.hairNumber = survivorDesc.hairNumber;
		this.head = survivorDesc.head;
		this.InventoryScript = survivorDesc.InventoryScript;
		this.legs = survivorDesc.legs;
		this.loner = survivorDesc.loner;
		this.loyalty = survivorDesc.loyalty;
		this.Profession = survivorDesc.Profession;
		this.shoes = survivorDesc.shoes;
		this.shoespal = survivorDesc.shoespal;
		this.skinColor = new Color(survivorDesc.skinColor);
		this.skinpal = survivorDesc.skinpal;
		this.surname = survivorDesc.surname;
		this.temper = survivorDesc.temper;
		this.top = survivorDesc.top;
		this.topColor = new Color(survivorDesc.topColor);
		this.toppal = survivorDesc.toppal;
		this.torso = survivorDesc.torso;
		this.torsoNumber = survivorDesc.torsoNumber;
		this.trouserColor = new Color(survivorDesc.trouserColor);
		this.type = survivorDesc.type;
	}

	public SurvivorGroup CreateGroup() {
		this.Group = new SurvivorGroup(this);
		return this.Group;
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

	public void load(ByteBuffer byteBuffer, int int1, IsoGameCharacter gameCharacter) throws IOException {
		this.ID = byteBuffer.getInt();
		IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
		this.forename = GameWindow.ReadString(byteBuffer);
		this.surname = GameWindow.ReadString(byteBuffer);
		this.legs = GameWindow.ReadString(byteBuffer);
		this.torso = GameWindow.ReadString(byteBuffer);
		this.head = GameWindow.ReadString(byteBuffer);
		this.top = GameWindow.ReadString(byteBuffer);
		this.bottoms = GameWindow.ReadString(byteBuffer);
		this.shoes = GameWindow.ReadString(byteBuffer);
		this.shoespal = GameWindow.ReadString(byteBuffer);
		this.bottomspal = GameWindow.ReadString(byteBuffer);
		this.toppal = GameWindow.ReadString(byteBuffer);
		this.skinpal = GameWindow.ReadString(byteBuffer);
		this.hair = GameWindow.ReadString(byteBuffer);
		this.bFemale = byteBuffer.getInt() == 1;
		this.Profession = GameWindow.ReadString(byteBuffer);
		this.hairColor = new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
		this.topColor = new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
		this.trouserColor = new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
		this.skinColor = new Color(byteBuffer.getFloat(), byteBuffer.getFloat(), byteBuffer.getFloat());
		if (this.shoespal.length() == 0) {
			this.shoespal = null;
		}

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

		if (int1 >= 58) {
			int2 = byteBuffer.getInt();
			for (int3 = 0; int3 < int2; ++int3) {
				this.getXPBoostMap().put(PerkFactory.Perks.fromIndex(byteBuffer.getInt()), byteBuffer.getInt());
			}
		}

		this.Instance = gameCharacter;
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.putInt(this.ID);
		GameWindow.WriteString(byteBuffer, this.forename);
		GameWindow.WriteString(byteBuffer, this.surname);
		GameWindow.WriteString(byteBuffer, this.legs);
		GameWindow.WriteString(byteBuffer, this.torso);
		GameWindow.WriteString(byteBuffer, this.head);
		GameWindow.WriteString(byteBuffer, this.top);
		GameWindow.WriteString(byteBuffer, this.bottoms);
		GameWindow.WriteString(byteBuffer, this.shoes);
		if (this.shoespal == null) {
			GameWindow.WriteString(byteBuffer, "");
		} else {
			GameWindow.WriteString(byteBuffer, this.shoespal);
		}

		GameWindow.WriteString(byteBuffer, this.bottomspal);
		GameWindow.WriteString(byteBuffer, this.toppal);
		GameWindow.WriteString(byteBuffer, this.skinpal);
		GameWindow.WriteString(byteBuffer, this.hair);
		byteBuffer.putInt(this.bFemale ? 1 : 0);
		GameWindow.WriteString(byteBuffer, this.Profession);
		byteBuffer.putFloat(this.hairColor.r);
		byteBuffer.putFloat(this.hairColor.g);
		byteBuffer.putFloat(this.hairColor.b);
		byteBuffer.putFloat(this.topColor.r);
		byteBuffer.putFloat(this.topColor.g);
		byteBuffer.putFloat(this.topColor.b);
		byteBuffer.putFloat(this.trouserColor.r);
		byteBuffer.putFloat(this.trouserColor.g);
		byteBuffer.putFloat(this.trouserColor.b);
		byteBuffer.putFloat(this.skinColor.r);
		byteBuffer.putFloat(this.skinColor.g);
		byteBuffer.putFloat(this.skinColor.b);
		Iterator iterator;
		if (this.extra != null && !this.extra.isEmpty()) {
			byteBuffer.putInt(1);
			byteBuffer.putInt(this.extra.size());
			iterator = this.extra.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				GameWindow.WriteString(byteBuffer, string);
			}
		} else {
			byteBuffer.putInt(0);
		}

		byteBuffer.putInt(this.getXPBoostMap().size());
		iterator = this.getXPBoostMap().entrySet().iterator();
		while (iterator.hasNext()) {
			Entry entry = (Entry)iterator.next();
			byteBuffer.putInt(((PerkFactory.Perks)entry.getKey()).index());
			byteBuffer.putInt((Integer)entry.getValue());
		}
	}

	private static int getUnsignedByte(ByteBuffer byteBuffer) {
		return byteBuffer.get() & 255;
	}

	public void loadCompact(ByteBuffer byteBuffer) throws IOException {
		this.ID = -1;
		this.legs = GameWindow.ReadString(byteBuffer);
		this.torso = GameWindow.ReadString(byteBuffer);
		this.head = GameWindow.ReadString(byteBuffer);
		this.top = GameWindow.ReadString(byteBuffer);
		this.bottoms = GameWindow.ReadString(byteBuffer);
		this.shoes = GameWindow.ReadString(byteBuffer);
		this.shoespal = GameWindow.ReadString(byteBuffer);
		this.bottomspal = GameWindow.ReadString(byteBuffer);
		this.toppal = GameWindow.ReadString(byteBuffer);
		this.skinpal = GameWindow.ReadString(byteBuffer);
		this.hair = GameWindow.ReadString(byteBuffer);
		this.bFemale = byteBuffer.get() == 1;
		this.hairColor = new Color(getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer));
		this.topColor = new Color(getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer));
		this.trouserColor = new Color(getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer));
		this.skinColor = new Color(getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer), getUnsignedByte(byteBuffer));
		if (this.shoespal.length() == 0) {
			this.shoespal = null;
		}

		this.extra.clear();
		if (byteBuffer.get() == 1) {
			byte byte1 = byteBuffer.get();
			for (int int1 = 0; int1 < byte1; ++int1) {
				String string = GameWindow.ReadString(byteBuffer);
				this.extra.add(string);
			}
		}
	}

	public void saveCompact(ByteBuffer byteBuffer) throws IOException {
		GameWindow.WriteString(byteBuffer, this.legs);
		GameWindow.WriteString(byteBuffer, this.torso);
		GameWindow.WriteString(byteBuffer, this.head);
		GameWindow.WriteString(byteBuffer, this.top);
		GameWindow.WriteString(byteBuffer, this.bottoms);
		GameWindow.WriteString(byteBuffer, this.shoes);
		if (this.shoespal == null) {
			GameWindow.WriteString(byteBuffer, "");
		} else {
			GameWindow.WriteString(byteBuffer, this.shoespal);
		}

		GameWindow.WriteString(byteBuffer, this.bottomspal);
		GameWindow.WriteString(byteBuffer, this.toppal);
		GameWindow.WriteString(byteBuffer, this.skinpal);
		GameWindow.WriteString(byteBuffer, this.hair);
		byteBuffer.put((byte)(this.bFemale ? 1 : 0));
		byteBuffer.put((byte)((int)(this.hairColor.r * 255.0F)));
		byteBuffer.put((byte)((int)(this.hairColor.g * 255.0F)));
		byteBuffer.put((byte)((int)(this.hairColor.b * 255.0F)));
		byteBuffer.put((byte)((int)(this.topColor.r * 255.0F)));
		byteBuffer.put((byte)((int)(this.topColor.g * 255.0F)));
		byteBuffer.put((byte)((int)(this.topColor.b * 255.0F)));
		byteBuffer.put((byte)((int)(this.trouserColor.r * 255.0F)));
		byteBuffer.put((byte)((int)(this.trouserColor.g * 255.0F)));
		byteBuffer.put((byte)((int)(this.trouserColor.b * 255.0F)));
		byteBuffer.put((byte)((int)(this.skinColor.r * 255.0F)));
		byteBuffer.put((byte)((int)(this.skinColor.g * 255.0F)));
		byteBuffer.put((byte)((int)(this.skinColor.b * 255.0F)));
		if (this.extra != null && !this.extra.isEmpty()) {
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

	public void AddToGroup(SurvivorGroup survivorGroup) {
		if (this.Group != survivorGroup) {
			survivorGroup.addMember(this);
			this.Group = survivorGroup;
		}
	}

	public boolean InGroupWith(IsoMovingObject movingObject) {
		if (movingObject instanceof IsoZombie) {
			return false;
		} else if (!(movingObject instanceof IsoSurvivor) && !(movingObject instanceof IsoPlayer)) {
			return false;
		} else {
			return ((IsoGameCharacter)movingObject).descriptor.Group == this.Group && this.Group != null;
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

	public boolean Test(float float1) {
		float1 *= 10.0F;
		return (float)Rand.Next(100) < float1;
	}

	void AddToOthersGroup(SurvivorDesc survivorDesc) {
		if (survivorDesc.Group != this.Group || this.Group == null) {
			this.Group = survivorDesc.Group;
			this.Group.addMember(this);
		}
	}

	public int getMetCount(SurvivorDesc survivorDesc) {
		return this.MetCount.containsKey(survivorDesc.ID) ? (Integer)this.MetCount.get(survivorDesc.ID) : 0;
	}

	public boolean IsLeader() {
		return this.Group != null && this.Group.Leader == this;
	}

	public SurvivorGroup getGroup() {
		return this.Group;
	}

	public void setGroup(SurvivorGroup survivorGroup) {
		this.Group = survivorGroup;
	}

	public Stack getChildrenList() {
		return this.ChildrenList;
	}

	public void setChildrenList(Stack stack) {
		this.ChildrenList = stack;
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

	public Stack getParentList() {
		return this.ParentList;
	}

	public void setParentList(Stack stack) {
		this.ParentList = stack;
	}

	public Stack getSiblingList() {
		return this.SiblingList;
	}

	public void setSiblingList(Stack stack) {
		this.SiblingList = stack;
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

	public String getLegs() {
		return this.legs;
	}

	public void setLegs(String string) {
		this.legs = string;
	}

	public String getTorso() {
		return this.torso;
	}

	public void setTorso(String string) {
		this.torso = string;
	}

	public String getHead() {
		return this.head;
	}

	public void setHead(String string) {
		this.head = string;
	}

	public String getTop() {
		return this.top;
	}

	public void setTop(String string) {
		this.top = string;
	}

	public String getBottoms() {
		return this.bottoms;
	}

	public void setBottoms(String string) {
		this.bottoms = string;
	}

	public String getShoes() {
		return this.shoes;
	}

	public void setShoes(String string) {
		this.shoes = string;
	}

	public String getShoespal() {
		return this.shoespal;
	}

	public void setShoespal(String string) {
		this.shoespal = string;
	}

	public String getBottomspal() {
		return this.bottomspal;
	}

	public void setBottomspal(String string) {
		this.bottomspal = string;
	}

	public String getToppal() {
		return this.toppal;
	}

	public void setToppal(String string) {
		this.toppal = string;
	}

	public String getSkinpal() {
		return this.skinpal;
	}

	public void setSkinpal(String string) {
		this.skinpal = string;
	}

	public HashMap getMetCount() {
		return this.MetCount;
	}

	public void setMetCount(HashMap hashMap) {
		this.MetCount = hashMap;
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
		for (int int1 = 0; int1 < this.Observations.size(); ++int1) {
			if ("Aggressive".equals(((ObservationFactory.Observation)this.Observations.get(int1)).getTraitID())) {
				return true;
			}
		}

		return false;
	}

	public ArrayList getObservations() {
		return this.Observations;
	}

	public boolean isFriendly() {
		for (int int1 = 0; int1 < this.Observations.size(); ++int1) {
			if ("Friendly".equals(((ObservationFactory.Observation)this.Observations.get(int1)).getTraitID())) {
				return true;
			}
		}

		return false;
	}

	public SurvivorFactory.SurvivorType getType() {
		return this.type;
	}

	public void setType(SurvivorFactory.SurvivorType survivorType) {
		this.type = survivorType;
	}

	public boolean TestHit(IsoMovingObject movingObject) {
		return Rand.Next(4) == 0;
	}

	public void setFemale(boolean boolean1) {
		this.bFemale = boolean1;
	}

	public int getHairNumber() {
		return this.hairNumber;
	}

	public void setHairNumber(int int1) {
		this.hairNumber = int1;
	}

	public int getTorsoNumber() {
		return this.torsoNumber;
	}

	public void setTorsoNumber(int int1) {
		this.torsoNumber = int1;
	}

	public String getHair() {
		return this.hair;
	}

	public void setHair(String string) {
		this.hair = string;
	}

	public String getHairNoColor() {
		return this.hairNoColor;
	}

	public void setHairNoColor(String string) {
		this.hairNoColor = string;
	}

	public ColorInfo getHairColor() {
		return new ColorInfo(this.hairColor.r, this.hairColor.g, this.hairColor.b, this.hairColor.a);
	}

	public void setHairColor(Color color) {
		this.hairColor = color;
	}

	public ArrayList getExtras() {
		return this.extra;
	}

	public void setBeardNumber(int int1) {
		this.beardNumber = int1;
	}

	public int getBeardNumber() {
		return this.beardNumber;
	}

	public String getBeardNoColor() {
		return this.beardNoColor;
	}

	public ArrayList getCommonHairColor() {
		return HairCommonColors;
	}

	public ArrayList getCommonTrouserColor() {
		return TrouserCommonColors;
	}

	public static void addTrouserColor(ColorInfo colorInfo) {
		TrouserCommonColors.add(colorInfo.toColor());
	}

	public static void addHairColor(ColorInfo colorInfo) {
		HairCommonColors.add(colorInfo.toColor());
	}

	public void setTrouserColor(Color color) {
		this.trouserColor = color;
	}

	public void setTopColor(Color color) {
		this.topColor = color;
	}

	public ColorInfo getTrouserColor() {
		return new ColorInfo(this.trouserColor.r, this.trouserColor.g, this.trouserColor.b, this.trouserColor.a);
	}

	public ColorInfo getTopColor() {
		return new ColorInfo(this.topColor.r, this.topColor.g, this.topColor.b, this.topColor.a);
	}

	public static Color getRandomSkinColor() {
		return Rand.Next(3) == 0 ? new Color(Rand.Next(0.5F, 0.6F), Rand.Next(0.3F, 0.4F), Rand.Next(0.15F, 0.23F)) : new Color(Rand.Next(0.9F, 1.0F), Rand.Next(0.75F, 0.88F), Rand.Next(0.45F, 0.58F));
	}
}
