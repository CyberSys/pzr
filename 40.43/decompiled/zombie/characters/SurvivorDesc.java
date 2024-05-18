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

   public void setProfessionSkills(ProfessionFactory.Profession var1) {
      this.xpBoostMap = var1.XPBoostMap;
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
      KahluaTable var1 = (KahluaTable)LuaManager.env.rawget("MetaSurvivor");
      Double var2 = (Double)LuaManager.caller.pcall(LuaManager.thread, var1.rawget("getCalculatedToughness"), (Object)this.metaTable)[1];
      return var2.intValue();
   }

   public static void setIDCount(int var0) {
      IDCount = var0;
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

   public SurvivorDesc(boolean var1) {
      this.type = SurvivorFactory.SurvivorType.Neutral;
      this.beardNumber = 0;
      this.ID = IDCount++;
      this.doStats();
   }

   public SurvivorDesc(SurvivorDesc var1) {
      this.type = SurvivorFactory.SurvivorType.Neutral;
      this.beardNumber = 0;
      this.aggressiveness = var1.aggressiveness;
      this.bDead = var1.bDead;
      this.beard = var1.beard;
      this.beardNoColor = var1.beardNoColor;
      this.beardNumber = var1.beardNumber;
      this.bFemale = var1.bFemale;
      this.bottoms = var1.bottoms;
      this.bottomspal = var1.bottomspal;
      this.bravery = var1.bravery;
      this.compassion = var1.compassion;
      this.extra.addAll(var1.extra);
      this.favourindoors = var1.favourindoors;
      this.forename = var1.forename;
      this.friendliness = var1.friendliness;
      this.hair = var1.hair;
      this.hairColor = new Color(var1.hairColor);
      this.hairNoColor = var1.hairNoColor;
      this.hairNumber = var1.hairNumber;
      this.head = var1.head;
      this.InventoryScript = var1.InventoryScript;
      this.legs = var1.legs;
      this.loner = var1.loner;
      this.loyalty = var1.loyalty;
      this.Profession = var1.Profession;
      this.shoes = var1.shoes;
      this.shoespal = var1.shoespal;
      this.skinColor = new Color(var1.skinColor);
      this.skinpal = var1.skinpal;
      this.surname = var1.surname;
      this.temper = var1.temper;
      this.top = var1.top;
      this.topColor = new Color(var1.topColor);
      this.toppal = var1.toppal;
      this.torso = var1.torso;
      this.torsoNumber = var1.torsoNumber;
      this.trouserColor = new Color(var1.trouserColor);
      this.type = var1.type;
   }

   public SurvivorGroup CreateGroup() {
      this.Group = new SurvivorGroup(this);
      return this.Group;
   }

   public void meet(SurvivorDesc var1) {
      if (this.MetCount.containsKey(var1.ID)) {
         this.MetCount.put(var1.ID, (Integer)this.MetCount.get(var1.ID) + 1);
      } else {
         this.MetCount.put(var1.ID, 1);
      }

      if (var1.MetCount.containsKey(this.ID)) {
         var1.MetCount.put(this.ID, (Integer)var1.MetCount.get(this.ID) + 1);
      } else {
         var1.MetCount.put(this.ID, 1);
      }

   }

   public boolean hasObservation(String var1) {
      for(int var2 = 0; var2 < this.Observations.size(); ++var2) {
         if (var1.equals(((ObservationFactory.Observation)this.Observations.get(var2)).getTraitID())) {
            return true;
         }
      }

      return false;
   }

   public void load(ByteBuffer var1, int var2, IsoGameCharacter var3) throws IOException {
      this.ID = var1.getInt();
      IsoWorld.instance.SurvivorDescriptors.put(this.ID, this);
      this.forename = GameWindow.ReadString(var1);
      this.surname = GameWindow.ReadString(var1);
      this.legs = GameWindow.ReadString(var1);
      this.torso = GameWindow.ReadString(var1);
      this.head = GameWindow.ReadString(var1);
      this.top = GameWindow.ReadString(var1);
      this.bottoms = GameWindow.ReadString(var1);
      this.shoes = GameWindow.ReadString(var1);
      this.shoespal = GameWindow.ReadString(var1);
      this.bottomspal = GameWindow.ReadString(var1);
      this.toppal = GameWindow.ReadString(var1);
      this.skinpal = GameWindow.ReadString(var1);
      this.hair = GameWindow.ReadString(var1);
      this.bFemale = var1.getInt() == 1;
      this.Profession = GameWindow.ReadString(var1);
      this.hairColor = new Color(var1.getFloat(), var1.getFloat(), var1.getFloat());
      this.topColor = new Color(var1.getFloat(), var1.getFloat(), var1.getFloat());
      this.trouserColor = new Color(var1.getFloat(), var1.getFloat(), var1.getFloat());
      this.skinColor = new Color(var1.getFloat(), var1.getFloat(), var1.getFloat());
      if (this.shoespal.length() == 0) {
         this.shoespal = null;
      }

      this.doStats();
      if (IDCount < this.ID) {
         IDCount = this.ID;
      }

      this.extra.clear();
      int var4;
      int var5;
      if (var1.getInt() == 1) {
         var4 = var1.getInt();

         for(var5 = 0; var5 < var4; ++var5) {
            String var6 = GameWindow.ReadString(var1);
            this.extra.add(var6);
         }
      }

      if (var2 >= 58) {
         var4 = var1.getInt();

         for(var5 = 0; var5 < var4; ++var5) {
            this.getXPBoostMap().put(PerkFactory.Perks.fromIndex(var1.getInt()), var1.getInt());
         }
      }

      this.Instance = var3;
   }

   public void save(ByteBuffer var1) throws IOException {
      var1.putInt(this.ID);
      GameWindow.WriteString(var1, this.forename);
      GameWindow.WriteString(var1, this.surname);
      GameWindow.WriteString(var1, this.legs);
      GameWindow.WriteString(var1, this.torso);
      GameWindow.WriteString(var1, this.head);
      GameWindow.WriteString(var1, this.top);
      GameWindow.WriteString(var1, this.bottoms);
      GameWindow.WriteString(var1, this.shoes);
      if (this.shoespal == null) {
         GameWindow.WriteString(var1, "");
      } else {
         GameWindow.WriteString(var1, this.shoespal);
      }

      GameWindow.WriteString(var1, this.bottomspal);
      GameWindow.WriteString(var1, this.toppal);
      GameWindow.WriteString(var1, this.skinpal);
      GameWindow.WriteString(var1, this.hair);
      var1.putInt(this.bFemale ? 1 : 0);
      GameWindow.WriteString(var1, this.Profession);
      var1.putFloat(this.hairColor.r);
      var1.putFloat(this.hairColor.g);
      var1.putFloat(this.hairColor.b);
      var1.putFloat(this.topColor.r);
      var1.putFloat(this.topColor.g);
      var1.putFloat(this.topColor.b);
      var1.putFloat(this.trouserColor.r);
      var1.putFloat(this.trouserColor.g);
      var1.putFloat(this.trouserColor.b);
      var1.putFloat(this.skinColor.r);
      var1.putFloat(this.skinColor.g);
      var1.putFloat(this.skinColor.b);
      Iterator var2;
      if (this.extra != null && !this.extra.isEmpty()) {
         var1.putInt(1);
         var1.putInt(this.extra.size());
         var2 = this.extra.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            GameWindow.WriteString(var1, var3);
         }
      } else {
         var1.putInt(0);
      }

      var1.putInt(this.getXPBoostMap().size());
      var2 = this.getXPBoostMap().entrySet().iterator();

      while(var2.hasNext()) {
         Entry var4 = (Entry)var2.next();
         var1.putInt(((PerkFactory.Perks)var4.getKey()).index());
         var1.putInt((Integer)var4.getValue());
      }

   }

   private static int getUnsignedByte(ByteBuffer var0) {
      return var0.get() & 255;
   }

   public void loadCompact(ByteBuffer var1) throws IOException {
      this.ID = -1;
      this.legs = GameWindow.ReadString(var1);
      this.torso = GameWindow.ReadString(var1);
      this.head = GameWindow.ReadString(var1);
      this.top = GameWindow.ReadString(var1);
      this.bottoms = GameWindow.ReadString(var1);
      this.shoes = GameWindow.ReadString(var1);
      this.shoespal = GameWindow.ReadString(var1);
      this.bottomspal = GameWindow.ReadString(var1);
      this.toppal = GameWindow.ReadString(var1);
      this.skinpal = GameWindow.ReadString(var1);
      this.hair = GameWindow.ReadString(var1);
      this.bFemale = var1.get() == 1;
      this.hairColor = new Color(getUnsignedByte(var1), getUnsignedByte(var1), getUnsignedByte(var1));
      this.topColor = new Color(getUnsignedByte(var1), getUnsignedByte(var1), getUnsignedByte(var1));
      this.trouserColor = new Color(getUnsignedByte(var1), getUnsignedByte(var1), getUnsignedByte(var1));
      this.skinColor = new Color(getUnsignedByte(var1), getUnsignedByte(var1), getUnsignedByte(var1));
      if (this.shoespal.length() == 0) {
         this.shoespal = null;
      }

      this.extra.clear();
      if (var1.get() == 1) {
         byte var2 = var1.get();

         for(int var3 = 0; var3 < var2; ++var3) {
            String var4 = GameWindow.ReadString(var1);
            this.extra.add(var4);
         }
      }

   }

   public void saveCompact(ByteBuffer var1) throws IOException {
      GameWindow.WriteString(var1, this.legs);
      GameWindow.WriteString(var1, this.torso);
      GameWindow.WriteString(var1, this.head);
      GameWindow.WriteString(var1, this.top);
      GameWindow.WriteString(var1, this.bottoms);
      GameWindow.WriteString(var1, this.shoes);
      if (this.shoespal == null) {
         GameWindow.WriteString(var1, "");
      } else {
         GameWindow.WriteString(var1, this.shoespal);
      }

      GameWindow.WriteString(var1, this.bottomspal);
      GameWindow.WriteString(var1, this.toppal);
      GameWindow.WriteString(var1, this.skinpal);
      GameWindow.WriteString(var1, this.hair);
      var1.put((byte)(this.bFemale ? 1 : 0));
      var1.put((byte)((int)(this.hairColor.r * 255.0F)));
      var1.put((byte)((int)(this.hairColor.g * 255.0F)));
      var1.put((byte)((int)(this.hairColor.b * 255.0F)));
      var1.put((byte)((int)(this.topColor.r * 255.0F)));
      var1.put((byte)((int)(this.topColor.g * 255.0F)));
      var1.put((byte)((int)(this.topColor.b * 255.0F)));
      var1.put((byte)((int)(this.trouserColor.r * 255.0F)));
      var1.put((byte)((int)(this.trouserColor.g * 255.0F)));
      var1.put((byte)((int)(this.trouserColor.b * 255.0F)));
      var1.put((byte)((int)(this.skinColor.r * 255.0F)));
      var1.put((byte)((int)(this.skinColor.g * 255.0F)));
      var1.put((byte)((int)(this.skinColor.b * 255.0F)));
      if (this.extra != null && !this.extra.isEmpty()) {
         var1.put((byte)1);
         var1.put((byte)this.extra.size());
         Iterator var2 = this.extra.iterator();

         while(var2.hasNext()) {
            String var3 = (String)var2.next();
            GameWindow.WriteString(var1, var3);
         }
      } else {
         var1.put((byte)0);
      }

   }

   public void AddToGroup(SurvivorGroup var1) {
      if (this.Group != var1) {
         var1.addMember(this);
         this.Group = var1;
      }
   }

   public boolean InGroupWith(IsoMovingObject var1) {
      if (var1 instanceof IsoZombie) {
         return false;
      } else if (!(var1 instanceof IsoSurvivor) && !(var1 instanceof IsoPlayer)) {
         return false;
      } else {
         return ((IsoGameCharacter)var1).descriptor.Group == this.Group && this.Group != null;
      }
   }

   public void addObservation(String var1) {
      ObservationFactory.Observation var2 = ObservationFactory.getObservation(var1);
      if (var2 != null) {
         this.Observations.add(var2);
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

   public boolean Test(float var1) {
      var1 *= 10.0F;
      return (float)Rand.Next(100) < var1;
   }

   void AddToOthersGroup(SurvivorDesc var1) {
      if (var1.Group != this.Group || this.Group == null) {
         this.Group = var1.Group;
         this.Group.addMember(this);
      }
   }

   public int getMetCount(SurvivorDesc var1) {
      return this.MetCount.containsKey(var1.ID) ? (Integer)this.MetCount.get(var1.ID) : 0;
   }

   public boolean IsLeader() {
      return this.Group != null && this.Group.Leader == this;
   }

   public SurvivorGroup getGroup() {
      return this.Group;
   }

   public void setGroup(SurvivorGroup var1) {
      this.Group = var1;
   }

   public Stack getChildrenList() {
      return this.ChildrenList;
   }

   public void setChildrenList(Stack var1) {
      this.ChildrenList = var1;
   }

   public String getForename() {
      return this.forename;
   }

   public void setForename(String var1) {
      this.forename = var1;
   }

   public int getID() {
      return this.ID;
   }

   public void setID(int var1) {
      this.ID = var1;
   }

   public IsoGameCharacter getInstance() {
      return this.Instance;
   }

   public void setInstance(IsoGameCharacter var1) {
      this.Instance = var1;
   }

   public Stack getParentList() {
      return this.ParentList;
   }

   public void setParentList(Stack var1) {
      this.ParentList = var1;
   }

   public Stack getSiblingList() {
      return this.SiblingList;
   }

   public void setSiblingList(Stack var1) {
      this.SiblingList = var1;
   }

   public String getSurname() {
      return this.surname;
   }

   public void setSurname(String var1) {
      this.surname = var1;
   }

   public String getInventoryScript() {
      return this.InventoryScript;
   }

   public void setInventoryScript(String var1) {
      this.InventoryScript = var1;
   }

   public String getLegs() {
      return this.legs;
   }

   public void setLegs(String var1) {
      this.legs = var1;
   }

   public String getTorso() {
      return this.torso;
   }

   public void setTorso(String var1) {
      this.torso = var1;
   }

   public String getHead() {
      return this.head;
   }

   public void setHead(String var1) {
      this.head = var1;
   }

   public String getTop() {
      return this.top;
   }

   public void setTop(String var1) {
      this.top = var1;
   }

   public String getBottoms() {
      return this.bottoms;
   }

   public void setBottoms(String var1) {
      this.bottoms = var1;
   }

   public String getShoes() {
      return this.shoes;
   }

   public void setShoes(String var1) {
      this.shoes = var1;
   }

   public String getShoespal() {
      return this.shoespal;
   }

   public void setShoespal(String var1) {
      this.shoespal = var1;
   }

   public String getBottomspal() {
      return this.bottomspal;
   }

   public void setBottomspal(String var1) {
      this.bottomspal = var1;
   }

   public String getToppal() {
      return this.toppal;
   }

   public void setToppal(String var1) {
      this.toppal = var1;
   }

   public String getSkinpal() {
      return this.skinpal;
   }

   public void setSkinpal(String var1) {
      this.skinpal = var1;
   }

   public HashMap getMetCount() {
      return this.MetCount;
   }

   public void setMetCount(HashMap var1) {
      this.MetCount = var1;
   }

   public float getBravery() {
      return this.bravery;
   }

   public void setBravery(float var1) {
      this.bravery = var1;
   }

   public float getLoner() {
      return this.loner;
   }

   public void setLoner(float var1) {
      this.loner = var1;
   }

   public float getAggressiveness() {
      return this.aggressiveness;
   }

   public void setAggressiveness(float var1) {
      this.aggressiveness = var1;
   }

   public float getCompassion() {
      return this.compassion;
   }

   public void setCompassion(float var1) {
      this.compassion = var1;
   }

   public float getTemper() {
      return this.temper;
   }

   public void setTemper(float var1) {
      this.temper = var1;
   }

   public float getFriendliness() {
      return this.friendliness;
   }

   public void setFriendliness(float var1) {
      this.friendliness = var1;
   }

   public float getFavourindoors() {
      return this.favourindoors;
   }

   public void setFavourindoors(float var1) {
      this.favourindoors = var1;
   }

   public float getLoyalty() {
      return this.loyalty;
   }

   public void setLoyalty(float var1) {
      this.loyalty = var1;
   }

   public String getProfession() {
      return this.Profession;
   }

   public void setProfession(String var1) {
      this.Profession = var1;
   }

   public boolean isAggressive() {
      for(int var1 = 0; var1 < this.Observations.size(); ++var1) {
         if ("Aggressive".equals(((ObservationFactory.Observation)this.Observations.get(var1)).getTraitID())) {
            return true;
         }
      }

      return false;
   }

   public ArrayList getObservations() {
      return this.Observations;
   }

   public boolean isFriendly() {
      for(int var1 = 0; var1 < this.Observations.size(); ++var1) {
         if ("Friendly".equals(((ObservationFactory.Observation)this.Observations.get(var1)).getTraitID())) {
            return true;
         }
      }

      return false;
   }

   public SurvivorFactory.SurvivorType getType() {
      return this.type;
   }

   public void setType(SurvivorFactory.SurvivorType var1) {
      this.type = var1;
   }

   public boolean TestHit(IsoMovingObject var1) {
      return Rand.Next(4) == 0;
   }

   public void setFemale(boolean var1) {
      this.bFemale = var1;
   }

   public int getHairNumber() {
      return this.hairNumber;
   }

   public void setHairNumber(int var1) {
      this.hairNumber = var1;
   }

   public int getTorsoNumber() {
      return this.torsoNumber;
   }

   public void setTorsoNumber(int var1) {
      this.torsoNumber = var1;
   }

   public String getHair() {
      return this.hair;
   }

   public void setHair(String var1) {
      this.hair = var1;
   }

   public String getHairNoColor() {
      return this.hairNoColor;
   }

   public void setHairNoColor(String var1) {
      this.hairNoColor = var1;
   }

   public ColorInfo getHairColor() {
      return new ColorInfo(this.hairColor.r, this.hairColor.g, this.hairColor.b, this.hairColor.a);
   }

   public void setHairColor(Color var1) {
      this.hairColor = var1;
   }

   public ArrayList getExtras() {
      return this.extra;
   }

   public void setBeardNumber(int var1) {
      this.beardNumber = var1;
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

   public static void addTrouserColor(ColorInfo var0) {
      TrouserCommonColors.add(var0.toColor());
   }

   public static void addHairColor(ColorInfo var0) {
      HairCommonColors.add(var0.toColor());
   }

   public void setTrouserColor(Color var1) {
      this.trouserColor = var1;
   }

   public void setTopColor(Color var1) {
      this.topColor = var1;
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
