package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import zombie.GameWindow;
import zombie.characters.professions.ProfessionFactory;
import zombie.characters.traits.TraitFactory;
import zombie.core.Translator;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemType;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;


public class Literature extends InventoryItem {
	public boolean bAlreadyRead = false;
	public String requireInHandOrInventory = null;
	public String useOnConsume = null;
	private int numberOfPages = -1;
	private String bookName = "";
	private int LvlSkillTrained = -1;
	private int NumLevelsTrained;
	private String SkillTrained = "None";
	private int alreadyReadPages = 0;
	private boolean canBeWrite = false;
	private HashMap customPages = null;
	private String lockedBy = null;
	private int pageToWrite;
	private List teachedRecipes = null;

	public Literature(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		this.setBookName(string2);
		this.cat = ItemType.Literature;
	}

	public Literature(String string, String string2, String string3, Item item) {
		super(string, string2, string3, item);
		this.setBookName(string2);
		this.cat = ItemType.Literature;
	}

	public int getSaveType() {
		return Item.Type.Literature.ordinal();
	}

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Literature";
	}

	public void update() {
		if (this.container != null) {
		}
	}

	public boolean finishupdate() {
		return true;
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		ObjectTooltip.LayoutItem layoutItem;
		int int1;
		if (this.getBoredomChange() != 0.0F) {
			layoutItem = layout.addItem();
			int1 = (int)this.getBoredomChange();
			layoutItem.setLabel(Translator.getText("Tooltip_literature_Boredom_Reduction") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight(int1, false);
		}

		if (this.getStressChange() != 0.0F) {
			layoutItem = layout.addItem();
			int1 = (int)(this.getStressChange() * 100.0F);
			layoutItem.setLabel(Translator.getText("Tooltip_literature_Stress_Reduction") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight(int1, false);
		}

		if (this.getUnhappyChange() != 0.0F) {
			layoutItem = layout.addItem();
			int1 = (int)this.getUnhappyChange();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Unhappiness") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight(int1, false);
		}

		if (this.getNumberOfPages() != -1) {
			layoutItem = layout.addItem();
			int1 = this.getAlreadyReadPages();
			if (objectTooltip.getCharacter() != null) {
				int1 = objectTooltip.getCharacter().getAlreadyReadPages(this.getFullType());
			}

			layoutItem.setLabel(Translator.getText("Tooltip_literature_Number_of_Pages") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValue(int1 + " / " + this.getNumberOfPages(), 1.0F, 1.0F, 1.0F, 1.0F);
		}

		String string;
		if (this.getLvlSkillTrained() != -1) {
			layoutItem = layout.addItem();
			string = this.getLvlSkillTrained() + "";
			if (this.getLvlSkillTrained() != this.getMaxLevelTrained()) {
				string = string + "-" + this.getMaxLevelTrained();
			}

			layoutItem.setLabel(Translator.getText("Tooltip_Literature_XpMultiplier", string), 1.0F, 1.0F, 0.8F, 1.0F);
		}

		if (this.getTeachedRecipes() != null) {
			Iterator iterator = this.getTeachedRecipes().iterator();
			while (iterator.hasNext()) {
				String string2 = (String)iterator.next();
				layoutItem = layout.addItem();
				String string3 = Translator.getRecipeName(string2);
				layoutItem.setLabel(Translator.getText("Tooltip_Literature_TeachedRecipes", string3), 1.0F, 1.0F, 0.8F, 1.0F);
			}

			if (objectTooltip.getCharacter() != null) {
				layoutItem = layout.addItem();
				string = Translator.getText("Tooltip_literature_NotBeenRead");
				if (objectTooltip.getCharacter().getKnownRecipes().containsAll(this.getTeachedRecipes())) {
					string = Translator.getText("Tooltip_literature_HasBeenRead");
				}

				layoutItem.setLabel(string, 1.0F, 1.0F, 0.8F, 1.0F);
				if (objectTooltip.getCharacter().getKnownRecipes().containsAll(this.getTeachedRecipes())) {
					ProfessionFactory.Profession profession = ProfessionFactory.getProfession(objectTooltip.getCharacter().getDescriptor().getProfession());
					ArrayList arrayList = objectTooltip.getCharacter().getTraits();
					int int2 = 0;
					int int3 = 0;
					for (int int4 = 0; int4 < this.getTeachedRecipes().size(); ++int4) {
						String string4 = (String)this.getTeachedRecipes().get(int4);
						if (profession != null && profession.getFreeRecipes().contains(string4)) {
							++int2;
						}

						for (int int5 = 0; int5 < arrayList.size(); ++int5) {
							TraitFactory.Trait trait = TraitFactory.getTrait((String)arrayList.get(int5));
							if (trait != null && trait.getFreeRecipes().contains(string4)) {
								++int3;
							}
						}
					}

					if (int2 > 0 || int3 > 0) {
						layoutItem = layout.addItem();
						layoutItem.setLabel(Translator.getText("Tooltip_literature_AlreadyKnown"), 0.0F, 1.0F, 0.8F, 1.0F);
					}
				}
			}
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putInt(this.getNumberOfPages());
		byteBuffer.putInt(this.getAlreadyReadPages());
		byteBuffer.put((byte)(this.canBeWrite() ? 1 : 0));
		if (this.customPages != null) {
			byteBuffer.putInt(this.customPages.size());
			Iterator iterator = this.customPages.values().iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				GameWindow.WriteString(byteBuffer, string);
			}
		} else {
			byteBuffer.putInt(0);
		}

		if (this.getLockedBy() != null) {
			byteBuffer.put((byte)1);
			GameWindow.WriteString(byteBuffer, this.getLockedBy());
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		if (int1 >= 37 || !this.type.equals("Notebook")) {
			this.setNumberOfPages(byteBuffer.getInt());
			if (int1 < 60) {
				GameWindow.ReadString(byteBuffer);
				byteBuffer.getInt();
			}

			this.setAlreadyReadPages(byteBuffer.getInt());
			if (int1 >= 37) {
				this.setCanBeWrite(byteBuffer.get() == 1);
				int int2 = byteBuffer.getInt();
				if (int2 > 0) {
					this.customPages = new HashMap();
					for (int int3 = 0; int3 < int2; ++int3) {
						this.customPages.put(int3 + 1, GameWindow.ReadString(byteBuffer));
					}
				}

				if (byteBuffer.get() == 1) {
					this.setLockedBy(GameWindow.ReadString(byteBuffer));
				}
			}
		}
	}

	public float getBoredomChange() {
		return !this.bAlreadyRead ? this.boredomChange : 0.0F;
	}

	public float getUnhappyChange() {
		return !this.bAlreadyRead ? this.unhappyChange : 0.0F;
	}

	public float getStressChange() {
		return !this.bAlreadyRead ? this.stressChange : 0.0F;
	}

	public int getNumberOfPages() {
		return this.numberOfPages;
	}

	public void setNumberOfPages(int int1) {
		this.numberOfPages = int1;
	}

	public String getBookName() {
		return this.bookName;
	}

	public void setBookName(String string) {
		this.bookName = string;
	}

	public int getLvlSkillTrained() {
		return this.LvlSkillTrained;
	}

	public void setLvlSkillTrained(int int1) {
		this.LvlSkillTrained = int1;
	}

	public int getNumLevelsTrained() {
		return this.NumLevelsTrained;
	}

	public void setNumLevelsTrained(int int1) {
		this.NumLevelsTrained = int1;
	}

	public int getMaxLevelTrained() {
		return this.getLvlSkillTrained() + this.getNumLevelsTrained() - 1;
	}

	public String getSkillTrained() {
		return this.SkillTrained;
	}

	public void setSkillTrained(String string) {
		this.SkillTrained = string;
	}

	public int getAlreadyReadPages() {
		return this.alreadyReadPages;
	}

	public void setAlreadyReadPages(int int1) {
		this.alreadyReadPages = int1;
	}

	public boolean canBeWrite() {
		return this.canBeWrite;
	}

	public void setCanBeWrite(boolean boolean1) {
		this.canBeWrite = boolean1;
	}

	public HashMap getCustomPages() {
		if (this.customPages == null) {
			this.customPages = new HashMap();
			this.customPages.put(1, "");
		}

		return this.customPages;
	}

	public void setCustomPages(HashMap hashMap) {
		this.customPages = hashMap;
	}

	public void addPage(Integer integer, String string) {
		if (this.customPages == null) {
			this.customPages = new HashMap();
		}

		this.customPages.put(integer, string);
	}

	public String seePage(Integer integer) {
		if (this.customPages == null) {
			this.customPages = new HashMap();
			this.customPages.put(1, "");
		}

		return (String)this.customPages.get(integer);
	}

	public String getLockedBy() {
		return this.lockedBy;
	}

	public void setLockedBy(String string) {
		this.lockedBy = string;
	}

	public int getPageToWrite() {
		return this.pageToWrite;
	}

	public void setPageToWrite(int int1) {
		this.pageToWrite = int1;
	}

	public List getTeachedRecipes() {
		return this.teachedRecipes;
	}

	public void setTeachedRecipes(List list) {
		this.teachedRecipes = list;
	}
}
