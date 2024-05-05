package zombie.inventory.types;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.WornItems.WornItem;
import zombie.characters.WornItems.WornItems;
import zombie.characters.skills.PerkFactory;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.math.PZMath;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoClothingDryer;
import zombie.iso.objects.IsoClothingWasher;
import zombie.iso.objects.IsoDeadBody;
import zombie.iso.weather.ClimateManager;
import zombie.network.GameClient;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.StringUtils;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;


public class Clothing extends InventoryItem {
	private float temperature;
	private float insulation = 0.0F;
	private float windresistance = 0.0F;
	private float waterResistance = 0.0F;
	HashMap patches;
	protected String SpriteName = null;
	protected String palette;
	public float bloodLevel = 0.0F;
	private float dirtyness = 0.0F;
	private float wetness = 0.0F;
	private float WeightWet = 0.0F;
	private float lastWetnessUpdate = -1.0F;
	private final String dirtyString = Translator.getText("IGUI_ClothingName_Dirty");
	private final String bloodyString = Translator.getText("IGUI_ClothingName_Bloody");
	private final String wetString = Translator.getText("IGUI_ClothingName_Wet");
	private final String soakedString = Translator.getText("IGUI_ClothingName_Soaked");
	private final String wornString = Translator.getText("IGUI_ClothingName_Worn");
	private int ConditionLowerChance = 10000;
	private float stompPower = 1.0F;
	private float runSpeedModifier = 1.0F;
	private float combatSpeedModifier = 1.0F;
	private Boolean removeOnBroken = false;
	private Boolean canHaveHoles = true;
	private float biteDefense = 0.0F;
	private float scratchDefense = 0.0F;
	private float bulletDefense = 0.0F;
	public static final int CONDITION_PER_HOLES = 3;
	private float neckProtectionModifier = 1.0F;
	private int chanceToFall = 0;

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Clothing";
	}

	public Clothing(String string, String string2, String string3, String string4, String string5, String string6) {
		super(string, string2, string3, string4);
		this.SpriteName = string6;
		this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
		this.palette = string5;
	}

	public Clothing(String string, String string2, String string3, Item item, String string4, String string5) {
		super(string, string2, string3, item);
		this.SpriteName = string5;
		this.col = new Color(Rand.Next(255), Rand.Next(255), Rand.Next(255));
		this.palette = string4;
	}

	public boolean IsClothing() {
		return true;
	}

	public int getSaveType() {
		return Item.Type.Clothing.ordinal();
	}

	public void Unwear() {
		if (this.container != null && this.container.parent instanceof IsoGameCharacter) {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)this.container.parent;
			gameCharacter.removeWornItem(this);
			if (gameCharacter instanceof IsoPlayer) {
				LuaEventManager.triggerEvent("OnClothingUpdated", gameCharacter);
			}

			IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
		}
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		float float1 = 1.0F;
		float float2 = 1.0F;
		float float3 = 0.8F;
		float float4 = 1.0F;
		float float5 = 0.0F;
		float float6 = 0.6F;
		float float7 = 0.0F;
		float float8 = 0.7F;
		ObjectTooltip.LayoutItem layoutItem;
		float float9;
		if (!this.isCosmetic()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_weapon_Condition") + ":", float1, float2, float3, float4);
			float9 = (float)this.Condition / (float)this.ConditionMax;
			layoutItem.setProgress(float9, float5, float6, float7, float8);
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_item_Insulation") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
			float9 = this.getInsulation();
			if (float9 > 0.8F) {
				layoutItem.setProgress(float9, 0.0F, 0.6F, 0.0F, 0.7F);
			} else if (float9 > 0.6F) {
				layoutItem.setProgress(float9, 0.3F, 0.6F, 0.0F, 0.7F);
			} else if (float9 > 0.4F) {
				layoutItem.setProgress(float9, 0.6F, 0.6F, 0.0F, 0.7F);
			} else if (float9 > 0.2F) {
				layoutItem.setProgress(float9, 0.6F, 0.3F, 0.0F, 0.7F);
			} else {
				layoutItem.setProgress(float9, 0.6F, 0.0F, 0.0F, 0.7F);
			}

			float9 = this.getWindresistance();
			if (float9 > 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_item_Windresist") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
				if (float9 > 0.8F) {
					layoutItem.setProgress(float9, 0.0F, 0.6F, 0.0F, 0.7F);
				} else if (float9 > 0.6F) {
					layoutItem.setProgress(float9, 0.3F, 0.6F, 0.0F, 0.7F);
				} else if (float9 > 0.4F) {
					layoutItem.setProgress(float9, 0.6F, 0.6F, 0.0F, 0.7F);
				} else if (float9 > 0.2F) {
					layoutItem.setProgress(float9, 0.6F, 0.3F, 0.0F, 0.7F);
				} else {
					layoutItem.setProgress(float9, 0.6F, 0.0F, 0.0F, 0.7F);
				}
			}

			float9 = this.getWaterResistance();
			if (float9 > 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_item_Waterresist") + ": ", 1.0F, 1.0F, 0.8F, 1.0F);
				if (float9 > 0.8F) {
					layoutItem.setProgress(float9, 0.0F, 0.6F, 0.0F, 0.7F);
				} else if (float9 > 0.6F) {
					layoutItem.setProgress(float9, 0.3F, 0.6F, 0.0F, 0.7F);
				} else if (float9 > 0.4F) {
					layoutItem.setProgress(float9, 0.6F, 0.6F, 0.0F, 0.7F);
				} else if (float9 > 0.2F) {
					layoutItem.setProgress(float9, 0.6F, 0.3F, 0.0F, 0.7F);
				} else {
					layoutItem.setProgress(float9, 0.6F, 0.0F, 0.0F, 0.7F);
				}
			}
		}

		if (this.bloodLevel != 0.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_clothing_bloody") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			float9 = this.bloodLevel / 100.0F;
			layoutItem.setProgress(float9, float5, float6, float7, float8);
		}

		if (this.dirtyness >= 1.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_clothing_dirty") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			float9 = this.dirtyness / 100.0F;
			layoutItem.setProgress(float9, float5, float6, float7, float8);
		}

		if (this.wetness != 0.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_clothing_wet") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			float9 = this.wetness / 100.0F;
			layoutItem.setProgress(float9, float5, float6, float7, float8);
		}

		if (!this.isEquipped() && objectTooltip.getCharacter() != null) {
			float float10 = 0.0F;
			float float11 = 0.0F;
			float float12 = 0.0F;
			WornItems wornItems = objectTooltip.getCharacter().getWornItems();
			for (int int1 = 0; int1 < wornItems.size(); ++int1) {
				WornItem wornItem = wornItems.get(int1);
				if (this.getBodyLocation().equals(wornItem.getLocation()) || wornItems.getBodyLocationGroup().isExclusive(this.getBodyLocation(), wornItem.getLocation())) {
					float10 += ((Clothing)wornItem.getItem()).getBiteDefense();
					float11 += ((Clothing)wornItem.getItem()).getScratchDefense();
					float12 += ((Clothing)wornItem.getItem()).getBulletDefense();
				}
			}

			float float13 = this.getBiteDefense();
			if (float13 != float10) {
				layoutItem = layout.addItem();
				if (float13 > 0.0F || float10 > 0.0F) {
					layoutItem.setLabel(Translator.getText("Tooltip_BiteDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
					if (float13 > float10) {
						layoutItem.setValue((int)float13 + " (+" + (int)(float13 - float10) + ")", 0.0F, 1.0F, 0.0F, 1.0F);
					} else {
						layoutItem.setValue((int)float13 + " (-" + (int)(float10 - float13) + ")", 1.0F, 0.0F, 0.0F, 1.0F);
					}
				}
			} else if (this.getBiteDefense() != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_BiteDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValueRightNoPlus((int)this.getBiteDefense());
			}

			float float14 = this.getScratchDefense();
			if (float14 != float11) {
				layoutItem = layout.addItem();
				if (float14 > 0.0F || float11 > 0.0F) {
					layoutItem.setLabel(Translator.getText("Tooltip_ScratchDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
					if (float14 > float11) {
						layoutItem.setValue((int)float14 + " (+" + (int)(float14 - float11) + ")", 0.0F, 1.0F, 0.0F, 1.0F);
					} else {
						layoutItem.setValue((int)float14 + " (-" + (int)(float11 - float14) + ")", 1.0F, 0.0F, 0.0F, 1.0F);
					}
				}
			} else if (this.getScratchDefense() != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_ScratchDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValueRightNoPlus((int)this.getScratchDefense());
			}

			float float15 = this.getBulletDefense();
			if (float15 != float12) {
				layoutItem = layout.addItem();
				if (float15 > 0.0F || float12 > 0.0F) {
					layoutItem.setLabel(Translator.getText("Tooltip_BulletDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
					if (float15 > float12) {
						layoutItem.setValue((int)float15 + " (+" + (int)(float15 - float12) + ")", 0.0F, 1.0F, 0.0F, 1.0F);
					} else {
						layoutItem.setValue((int)float15 + " (-" + (int)(float12 - float15) + ")", 1.0F, 0.0F, 0.0F, 1.0F);
					}
				}
			} else if (this.getBulletDefense() != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_BulletDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValueRightNoPlus((int)this.getBulletDefense());
			}
		} else {
			if (this.getBiteDefense() != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_BiteDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValueRightNoPlus((int)this.getBiteDefense());
			}

			if (this.getScratchDefense() != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_ScratchDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValueRightNoPlus((int)this.getScratchDefense());
			}

			if (this.getBulletDefense() != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel(Translator.getText("Tooltip_BulletDefense") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
				layoutItem.setValueRightNoPlus((int)this.getBulletDefense());
			}
		}

		if (this.getRunSpeedModifier() != 1.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_RunSpeedModifier") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getRunSpeedModifier());
		}

		if (this.getCombatSpeedModifier() != 1.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_CombatSpeedModifier") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getCombatSpeedModifier());
		}

		if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
			int int2;
			if (this.bloodLevel != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: bloodLevel:", 1.0F, 1.0F, 0.8F, 1.0F);
				int2 = (int)Math.ceil((double)this.bloodLevel);
				layoutItem.setValueRight(int2, false);
			}

			if (this.dirtyness != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: dirtyness:", 1.0F, 1.0F, 0.8F, 1.0F);
				int2 = (int)Math.ceil((double)this.dirtyness);
				layoutItem.setValueRight(int2, false);
			}

			if (this.wetness != 0.0F) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: wetness:", 1.0F, 1.0F, 0.8F, 1.0F);
				int2 = (int)Math.ceil((double)this.wetness);
				layoutItem.setValueRight(int2, false);
			}
		}
	}

	public boolean isDirty() {
		return this.dirtyness > 15.0F;
	}

	public boolean isBloody() {
		return this.bloodLevel > 25.0F;
	}

	public String getName() {
		String string = "";
		if (this.isDirty()) {
			string = string + this.dirtyString + ", ";
		}

		if (this.isBloody()) {
			string = string + this.bloodyString + ", ";
		}

		if (this.getWetness() >= 100.0F) {
			string = string + this.soakedString + ", ";
		} else if (this.getWetness() > 25.0F) {
			string = string + this.wetString + ", ";
		}

		if (this.getCondition() < this.getConditionMax() / 3) {
			string = string + this.wornString + ", ";
		}

		if (string.length() > 2) {
			string = string.substring(0, string.length() - 2);
		}

		string = string.trim();
		return string.isEmpty() ? this.name : Translator.getText("IGUI_ClothingNaming", string, this.name);
	}

	public void update() {
		if (this.container == null || SandboxOptions.instance.ClothingDegradation.getValue() == 1) {
			;
		}
	}

	public void updateWetness() {
		this.updateWetness(false);
	}

	public void updateWetness(boolean boolean1) {
		if (boolean1 || !this.isEquipped()) {
			if (this.getBloodClothingType() == null) {
				this.setWetness(0.0F);
			} else {
				float float1 = (float)GameTime.getInstance().getWorldAgeHours();
				if (this.lastWetnessUpdate < 0.0F) {
					this.lastWetnessUpdate = float1;
				} else if (this.lastWetnessUpdate > float1) {
					this.lastWetnessUpdate = float1;
				}

				float float2 = float1 - this.lastWetnessUpdate;
				if (!(float2 < 0.016666668F)) {
					this.lastWetnessUpdate = float1;
					float float3;
					switch (this.getWetDryState()) {
					case Invalid: 
					
					default: 
						break;
					
					case Dryer: 
						if (this.getWetness() > 0.0F) {
							float3 = float2 * 20.0F;
							if (this.isEquipped()) {
								float3 *= 2.0F;
							}

							this.setWetness(this.getWetness() - float3);
						}

						break;
					
					case Wetter: 
						if (this.getWetness() < 100.0F) {
							float3 = ClimateManager.getInstance().getRainIntensity();
							if (float3 < 0.1F) {
								float3 = 0.0F;
							}

							float float4 = float3 * float2 * 100.0F;
							this.setWetness(this.getWetness() + float4);
						}

					
					}
				}
			}
		}
	}

	public float getBulletDefense() {
		return this.bulletDefense;
	}

	public void setBulletDefense(float float1) {
		this.bulletDefense = float1;
	}

	private Clothing.WetDryState getWetDryState() {
		if (this.getWorldItem() == null) {
			if (this.container == null) {
				return Clothing.WetDryState.Invalid;
			} else if (this.container.parent instanceof IsoDeadBody) {
				IsoDeadBody deadBody = (IsoDeadBody)this.container.parent;
				if (deadBody.getSquare() == null) {
					return Clothing.WetDryState.Invalid;
				} else if (deadBody.getSquare().isInARoom()) {
					return Clothing.WetDryState.Dryer;
				} else {
					return ClimateManager.getInstance().isRaining() ? Clothing.WetDryState.Wetter : Clothing.WetDryState.Dryer;
				}
			} else if (this.container.parent instanceof IsoGameCharacter) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)this.container.parent;
				if (gameCharacter.getCurrentSquare() == null) {
					return Clothing.WetDryState.Invalid;
				} else if (!gameCharacter.getCurrentSquare().isInARoom() && !gameCharacter.getCurrentSquare().haveRoof) {
					if (ClimateManager.getInstance().isRaining()) {
						if (!this.isEquipped()) {
							return Clothing.WetDryState.Dryer;
						} else if (gameCharacter.isAsleep() && gameCharacter.getBed() != null && "Tent".equals(gameCharacter.getBed().getName())) {
							return Clothing.WetDryState.Dryer;
						} else {
							BaseVehicle baseVehicle = gameCharacter.getVehicle();
							if (baseVehicle != null && baseVehicle.hasRoof(baseVehicle.getSeat(gameCharacter))) {
								VehiclePart vehiclePart = baseVehicle.getPartById("Windshield");
								if (vehiclePart != null) {
									VehicleWindow vehicleWindow = vehiclePart.getWindow();
									if (vehicleWindow != null && vehicleWindow.isHittable()) {
										return Clothing.WetDryState.Dryer;
									}
								}
							}

							return Clothing.WetDryState.Wetter;
						}
					} else {
						return Clothing.WetDryState.Dryer;
					}
				} else {
					return Clothing.WetDryState.Dryer;
				}
			} else if (this.container.parent == null) {
				return Clothing.WetDryState.Dryer;
			} else if (this.container.parent instanceof IsoClothingDryer && ((IsoClothingDryer)this.container.parent).isActivated()) {
				return Clothing.WetDryState.Invalid;
			} else {
				return this.container.parent instanceof IsoClothingWasher && ((IsoClothingWasher)this.container.parent).isActivated() ? Clothing.WetDryState.Invalid : Clothing.WetDryState.Dryer;
			}
		} else if (this.getWorldItem().getSquare() == null) {
			return Clothing.WetDryState.Invalid;
		} else if (this.getWorldItem().getSquare().isInARoom()) {
			return Clothing.WetDryState.Dryer;
		} else {
			return ClimateManager.getInstance().isRaining() ? Clothing.WetDryState.Wetter : Clothing.WetDryState.Dryer;
		}
	}

	public void flushWetness() {
		if (!(this.lastWetnessUpdate < 0.0F)) {
			this.updateWetness(true);
			this.lastWetnessUpdate = -1.0F;
		}
	}

	public boolean finishupdate() {
		if (this.container != null && this.container.parent instanceof IsoGameCharacter) {
			return !this.isEquipped();
		} else {
			return true;
		}
	}

	public void Use(boolean boolean1, boolean boolean2) {
		if (this.uses <= 1) {
			this.Unwear();
		}

		super.Use(boolean1, boolean2);
	}

	public boolean CanStack(InventoryItem inventoryItem) {
		return this.ModDataMatches(inventoryItem) && this.palette == null && ((Clothing)inventoryItem).palette == null || this.palette.equals(((Clothing)inventoryItem).palette);
	}

	public static Clothing CreateFromSprite(String string) {
		try {
			Clothing clothing = null;
			clothing = (Clothing)InventoryItemFactory.CreateItem(string, 1.0F);
			return clothing;
		} catch (Exception exception) {
			return null;
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
		if (this.getSpriteName() != null) {
			bitHeaderWrite.addFlags(1);
			GameWindow.WriteString(byteBuffer, this.getSpriteName());
		}

		if (this.dirtyness != 0.0F) {
			bitHeaderWrite.addFlags(2);
			byteBuffer.putFloat(this.dirtyness);
		}

		if (this.bloodLevel != 0.0F) {
			bitHeaderWrite.addFlags(4);
			byteBuffer.putFloat(this.bloodLevel);
		}

		if (this.wetness != 0.0F) {
			bitHeaderWrite.addFlags(8);
			byteBuffer.putFloat(this.wetness);
		}

		if (this.lastWetnessUpdate != 0.0F) {
			bitHeaderWrite.addFlags(16);
			byteBuffer.putFloat(this.lastWetnessUpdate);
		}

		if (this.patches != null) {
			bitHeaderWrite.addFlags(32);
			byteBuffer.put((byte)this.patches.size());
			Iterator iterator = this.patches.keySet().iterator();
			while (iterator.hasNext()) {
				int int1 = (Integer)iterator.next();
				byteBuffer.put((byte)int1);
				((Clothing.ClothingPatch)this.patches.get(int1)).save(byteBuffer, false);
			}
		}

		bitHeaderWrite.write();
		bitHeaderWrite.release();
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
		if (!bitHeaderRead.equals(0)) {
			if (bitHeaderRead.hasFlags(1)) {
				this.setSpriteName(GameWindow.ReadString(byteBuffer));
			}

			if (bitHeaderRead.hasFlags(2)) {
				this.dirtyness = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(4)) {
				this.bloodLevel = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(8)) {
				this.wetness = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(16)) {
				this.lastWetnessUpdate = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(32)) {
				byte byte1 = byteBuffer.get();
				for (int int2 = 0; int2 < byte1; ++int2) {
					byte byte2 = byteBuffer.get();
					Clothing.ClothingPatch clothingPatch = new Clothing.ClothingPatch();
					clothingPatch.load(byteBuffer, int1);
					if (this.patches == null) {
						this.patches = new HashMap();
					}

					this.patches.put(Integer.valueOf(byte2), clothingPatch);
				}
			}
		}

		bitHeaderRead.release();
		this.synchWithVisual();
	}

	public String getSpriteName() {
		return this.SpriteName;
	}

	public void setSpriteName(String string) {
		this.SpriteName = string;
	}

	public String getPalette() {
		return this.palette == null ? "Trousers_White" : this.palette;
	}

	public void setPalette(String string) {
		this.palette = string;
	}

	public float getTemperature() {
		return this.temperature;
	}

	public void setTemperature(float float1) {
		this.temperature = float1;
	}

	public void setDirtyness(float float1) {
		this.dirtyness = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public void setBloodLevel(float float1) {
		this.bloodLevel = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public float getDirtyness() {
		return this.dirtyness;
	}

	public float getBloodlevel() {
		return this.bloodLevel;
	}

	public float getBloodlevelForPart(BloodBodyPartType bloodBodyPartType) {
		return this.getVisual().getBlood(bloodBodyPartType);
	}

	public float getBloodLevel() {
		return this.bloodLevel;
	}

	public float getBloodLevelForPart(BloodBodyPartType bloodBodyPartType) {
		return this.getVisual().getBlood(bloodBodyPartType);
	}

	public float getWeight() {
		float float1 = this.getActualWeight();
		float float2 = this.getWeightWet();
		if (float2 <= 0.0F) {
			float2 = float1 * 1.25F;
		}

		return PZMath.lerp(float1, float2, this.getWetness() / 100.0F);
	}

	public void setWetness(float float1) {
		this.wetness = PZMath.clamp(float1, 0.0F, 100.0F);
	}

	public float getWetness() {
		return this.wetness;
	}

	public float getWeightWet() {
		return this.WeightWet;
	}

	public void setWeightWet(float float1) {
		this.WeightWet = float1;
	}

	public int getConditionLowerChance() {
		return this.ConditionLowerChance;
	}

	public void setConditionLowerChance(int int1) {
		this.ConditionLowerChance = int1;
	}

	public void setCondition(int int1) {
		this.setCondition(int1, true);
		if (int1 <= 0) {
			this.Unwear();
			if (this.getContainer() != null) {
				this.getContainer().setDrawDirty(true);
			}

			if (this.isRemoveOnBroken() && this.getContainer() != null) {
				this.container.Remove((InventoryItem)this);
			}
		}
	}

	public float getClothingDirtynessIncreaseLevel() {
		if (SandboxOptions.instance.ClothingDegradation.getValue() == 2) {
			return 2.5E-4F;
		} else {
			return SandboxOptions.instance.ClothingDegradation.getValue() == 4 ? 0.025F : 0.0025F;
		}
	}

	public float getInsulation() {
		return this.insulation;
	}

	public void setInsulation(float float1) {
		this.insulation = float1;
	}

	public float getStompPower() {
		return this.stompPower;
	}

	public void setStompPower(float float1) {
		this.stompPower = float1;
	}

	public float getRunSpeedModifier() {
		return this.runSpeedModifier;
	}

	public void setRunSpeedModifier(float float1) {
		this.runSpeedModifier = float1;
	}

	public float getCombatSpeedModifier() {
		return this.combatSpeedModifier;
	}

	public void setCombatSpeedModifier(float float1) {
		this.combatSpeedModifier = float1;
	}

	public Boolean isRemoveOnBroken() {
		return this.removeOnBroken;
	}

	public void setRemoveOnBroken(Boolean Boolean1) {
		this.removeOnBroken = Boolean1;
	}

	public Boolean getCanHaveHoles() {
		return this.canHaveHoles;
	}

	public void setCanHaveHoles(Boolean Boolean1) {
		this.canHaveHoles = Boolean1;
	}

	public boolean isCosmetic() {
		return this.getScriptItem().isCosmetic();
	}

	public String toString() {
		String string = this.getClass().getSimpleName();
		return string + "{ clothingItemName=\"" + this.getClothingItemName() + "\" }";
	}

	public float getBiteDefense() {
		return this.getCondition() <= 0 ? 0.0F : this.biteDefense;
	}

	public void setBiteDefense(float float1) {
		this.biteDefense = float1;
	}

	public float getScratchDefense() {
		return this.getCondition() <= 0 ? 0.0F : this.scratchDefense;
	}

	public void setScratchDefense(float float1) {
		this.scratchDefense = float1;
	}

	public float getNeckProtectionModifier() {
		return this.neckProtectionModifier;
	}

	public void setNeckProtectionModifier(float float1) {
		this.neckProtectionModifier = float1;
	}

	public int getChanceToFall() {
		return this.chanceToFall;
	}

	public void setChanceToFall(int int1) {
		this.chanceToFall = int1;
	}

	public float getWindresistance() {
		return this.windresistance;
	}

	public void setWindresistance(float float1) {
		this.windresistance = float1;
	}

	public float getWaterResistance() {
		return this.waterResistance;
	}

	public void setWaterResistance(float float1) {
		this.waterResistance = float1;
	}

	public int getHolesNumber() {
		return this.getVisual() != null ? this.getVisual().getHolesNumber() : 0;
	}

	public int getPatchesNumber() {
		return this.patches.size();
	}

	public float getDefForPart(BloodBodyPartType bloodBodyPartType, boolean boolean1, boolean boolean2) {
		if (this.getVisual().getHole(bloodBodyPartType) > 0.0F) {
			return 0.0F;
		} else {
			Clothing.ClothingPatch clothingPatch = this.getPatchType(bloodBodyPartType);
			float float1 = this.getScratchDefense();
			if (boolean1) {
				float1 = this.getBiteDefense();
			}

			if (boolean2) {
				float1 = this.getBulletDefense();
			}

			if (bloodBodyPartType == BloodBodyPartType.Neck && this.getScriptItem().neckProtectionModifier < 1.0F) {
				float1 *= this.getScriptItem().neckProtectionModifier;
			}

			if (clothingPatch != null) {
				int int1 = clothingPatch.scratchDefense;
				if (boolean1) {
					int1 = clothingPatch.biteDefense;
				}

				if (boolean2) {
					int1 = clothingPatch.biteDefense;
				}

				if (!clothingPatch.hasHole) {
					float1 += (float)int1;
				} else {
					float1 = (float)int1;
				}
			}

			return float1;
		}
	}

	public static int getBiteDefenseFromItem(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		int int1 = Math.max(1, gameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring));
		Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromType(inventoryItem.getFabricType());
		return clothingPatchFabricType.maxBiteDef > 0 ? (int)Math.max(1.0F, (float)clothingPatchFabricType.maxBiteDef * ((float)int1 / 10.0F)) : 0;
	}

	public static int getScratchDefenseFromItem(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		int int1 = Math.max(1, gameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring));
		Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromType(inventoryItem.getFabricType());
		return (int)Math.max(1.0F, (float)clothingPatchFabricType.maxScratchDef * ((float)int1 / 10.0F));
	}

	public Clothing.ClothingPatch getPatchType(BloodBodyPartType bloodBodyPartType) {
		return this.patches != null ? (Clothing.ClothingPatch)this.patches.get(bloodBodyPartType.index()) : null;
	}

	public void removePatch(BloodBodyPartType bloodBodyPartType) {
		if (this.patches != null) {
			this.getVisual().removePatch(bloodBodyPartType.index());
			Clothing.ClothingPatch clothingPatch = (Clothing.ClothingPatch)this.patches.get(bloodBodyPartType.index());
			if (clothingPatch != null && clothingPatch.hasHole) {
				this.getVisual().setHole(bloodBodyPartType);
				this.setCondition(this.getCondition() - clothingPatch.conditionGain);
			}

			this.patches.remove(bloodBodyPartType.index());
			if (GameClient.bClient && this.getContainer() != null && this.getContainer().getParent() instanceof IsoPlayer) {
				GameClient.instance.sendClothing((IsoPlayer)this.getContainer().getParent(), "", (InventoryItem)null);
			}
		}
	}

	public boolean canFullyRestore(IsoGameCharacter gameCharacter, BloodBodyPartType bloodBodyPartType, InventoryItem inventoryItem) {
		return gameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring) > 7 && inventoryItem.getFabricType().equals(this.getFabricType()) && this.getVisual().getHole(bloodBodyPartType) > 0.0F;
	}

	public void addPatch(IsoGameCharacter gameCharacter, BloodBodyPartType bloodBodyPartType, InventoryItem inventoryItem) {
		Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromType(inventoryItem.getFabricType());
		if (this.canFullyRestore(gameCharacter, bloodBodyPartType, inventoryItem)) {
			this.getVisual().removeHole(bloodBodyPartType.index());
			this.setCondition(this.getCondition() + this.getCondLossPerHole());
		} else {
			if (clothingPatchFabricType == Clothing.ClothingPatchFabricType.Cotton) {
				this.getVisual().setBasicPatch(bloodBodyPartType);
			} else if (clothingPatchFabricType == Clothing.ClothingPatchFabricType.Denim) {
				this.getVisual().setDenimPatch(bloodBodyPartType);
			} else {
				this.getVisual().setLeatherPatch(bloodBodyPartType);
			}

			if (this.patches == null) {
				this.patches = new HashMap();
			}

			int int1 = Math.max(1, gameCharacter.getPerkLevel(PerkFactory.Perks.Tailoring));
			float float1 = this.getVisual().getHole(bloodBodyPartType);
			int int2 = this.getCondLossPerHole();
			if (int1 < 3) {
				int2 -= 2;
			} else if (int1 < 6) {
				--int2;
			}

			Clothing.ClothingPatch clothingPatch = new Clothing.ClothingPatch(int1, clothingPatchFabricType.index, float1 > 0.0F);
			if (float1 > 0.0F) {
				int2 = Math.max(1, int2);
				this.setCondition(this.getCondition() + int2);
				clothingPatch.conditionGain = int2;
			}

			this.patches.put(bloodBodyPartType.index(), clothingPatch);
			this.getVisual().removeHole(bloodBodyPartType.index());
			if (GameClient.bClient && gameCharacter instanceof IsoPlayer) {
				GameClient.instance.sendClothing((IsoPlayer)gameCharacter, "", (InventoryItem)null);
			}
		}
	}

	public ArrayList getCoveredParts() {
		ArrayList arrayList = this.getScriptItem().getBloodClothingType();
		return BloodClothingType.getCoveredParts(arrayList);
	}

	public int getNbrOfCoveredParts() {
		ArrayList arrayList = this.getScriptItem().getBloodClothingType();
		return BloodClothingType.getCoveredPartCount(arrayList);
	}

	public int getCondLossPerHole() {
		int int1 = this.getNbrOfCoveredParts();
		byte byte1;
		if (int1 <= 2) {
			byte1 = 10;
		} else if (int1 <= 5) {
			byte1 = 3;
		} else {
			byte1 = 2;
		}

		return byte1;
	}

	public void copyPatchesTo(Clothing clothing) {
		clothing.patches = this.patches;
	}

	public String getClothingExtraSubmenu() {
		return this.ScriptItem.clothingExtraSubmenu;
	}

	public boolean canBe3DRender() {
		if (!StringUtils.isNullOrEmpty(this.getWorldStaticItem())) {
			return true;
		} else {
			return "Bip01_Head".equalsIgnoreCase(this.getClothingItem().m_AttachBone) && (!this.isCosmetic() || "Eyes".equals(this.getBodyLocation()));
		}
	}

	private static enum WetDryState {

		Invalid,
		Dryer,
		Wetter;

		private static Clothing.WetDryState[] $values() {
			return new Clothing.WetDryState[]{Invalid, Dryer, Wetter};
		}
	}

	public class ClothingPatch {
		public int tailorLvl = 0;
		public int fabricType = 0;
		public int scratchDefense = 0;
		public int biteDefense = 0;
		public boolean hasHole;
		public int conditionGain = 0;

		public String getFabricTypeName() {
			return Translator.getText("IGUI_FabricType_" + this.fabricType);
		}

		public int getScratchDefense() {
			return this.scratchDefense;
		}

		public int getBiteDefense() {
			return this.biteDefense;
		}

		public int getFabricType() {
			return this.fabricType;
		}

		public ClothingPatch() {
		}

		public ClothingPatch(int int1, int int2, boolean boolean1) {
			this.tailorLvl = int1;
			this.fabricType = int2;
			this.hasHole = boolean1;
			Clothing.ClothingPatchFabricType clothingPatchFabricType = Clothing.ClothingPatchFabricType.fromIndex(int2);
			this.scratchDefense = (int)Math.max(1.0F, (float)clothingPatchFabricType.maxScratchDef * ((float)int1 / 10.0F));
			if (clothingPatchFabricType.maxBiteDef > 0) {
				this.biteDefense = (int)Math.max(1.0F, (float)clothingPatchFabricType.maxBiteDef * ((float)int1 / 10.0F));
			}
		}

		public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
			byteBuffer.put((byte)this.tailorLvl);
			byteBuffer.put((byte)this.fabricType);
			byteBuffer.put((byte)this.scratchDefense);
			byteBuffer.put((byte)this.biteDefense);
			byteBuffer.put((byte)(this.hasHole ? 1 : 0));
			byteBuffer.putShort((short)this.conditionGain);
		}

		public void load(ByteBuffer byteBuffer, int int1) throws IOException {
			this.tailorLvl = byteBuffer.get();
			if (int1 < 178) {
				this.fabricType = byteBuffer.getShort();
			} else {
				this.fabricType = byteBuffer.get();
			}

			this.scratchDefense = byteBuffer.get();
			this.biteDefense = byteBuffer.get();
			this.hasHole = byteBuffer.get() == 1;
			this.conditionGain = byteBuffer.getShort();
		}

		@Deprecated
		public void save_old(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
			byteBuffer.putInt(this.tailorLvl);
			byteBuffer.putInt(this.fabricType);
			byteBuffer.putInt(this.scratchDefense);
			byteBuffer.putInt(this.biteDefense);
			byteBuffer.put((byte)(this.hasHole ? 1 : 0));
			byteBuffer.putInt(this.conditionGain);
		}

		@Deprecated
		public void load_old(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
			this.tailorLvl = byteBuffer.getInt();
			this.fabricType = byteBuffer.getInt();
			this.scratchDefense = byteBuffer.getInt();
			this.biteDefense = byteBuffer.getInt();
			this.hasHole = byteBuffer.get() == 1;
			this.conditionGain = byteBuffer.getInt();
		}
	}

	public static enum ClothingPatchFabricType {

		Cotton,
		Denim,
		Leather,
		index,
		type,
		maxScratchDef,
		maxBiteDef;

		private ClothingPatchFabricType(int int1, String string, int int2, int int3) {
			this.index = int1;
			this.type = string;
			this.maxScratchDef = int2;
			this.maxBiteDef = int3;
		}
		public String getType() {
			return this.type;
		}
		public static Clothing.ClothingPatchFabricType fromType(String string) {
			if (StringUtils.isNullOrEmpty(string)) {
				return null;
			} else if (Cotton.type.equals(string)) {
				return Cotton;
			} else if (Denim.type.equals(string)) {
				return Denim;
			} else {
				return Leather.type.equals(string) ? Leather : null;
			}
		}
		public static Clothing.ClothingPatchFabricType fromIndex(int int1) {
			if (int1 == 1) {
				return Cotton;
			} else if (int1 == 2) {
				return Denim;
			} else {
				return int1 == 3 ? Leather : null;
			}
		}
		private static Clothing.ClothingPatchFabricType[] $values() {
			return new Clothing.ClothingPatchFabricType[]{Cotton, Denim, Leather};
		}
	}
}
