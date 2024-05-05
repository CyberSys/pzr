package zombie.inventory.types;

import fmod.fmod.FMODManager;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.characters.SurvivorDesc;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.Translator;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemSoundManager;
import zombie.inventory.ItemType;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.objects.IsoCompost;
import zombie.iso.objects.IsoFireManager;
import zombie.iso.objects.IsoFireplace;
import zombie.iso.objects.IsoWorldInventoryObject;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.ui.ObjectTooltip;
import zombie.util.StringUtils;
import zombie.util.io.BitHeader;
import zombie.util.io.BitHeaderRead;
import zombie.util.io.BitHeaderWrite;


public final class Food extends InventoryItem {
	protected boolean bBadCold = false;
	protected boolean bGoodHot = false;
	private static final float MIN_HEAT = 0.2F;
	private static final float MAX_HEAT = 3.0F;
	protected float Heat = 1.0F;
	protected float endChange = 0.0F;
	protected float hungChange = 0.0F;
	protected String useOnConsume = null;
	protected boolean rotten = false;
	protected boolean bDangerousUncooked = false;
	protected int LastCookMinute = 0;
	public float thirstChange = 0.0F;
	public boolean Poison = false;
	private List ReplaceOnCooked = null;
	private float baseHunger = 0.0F;
	public ArrayList spices = null;
	private boolean isSpice = false;
	private int poisonDetectionLevel = -1;
	private Integer PoisonLevelForRecipe = 0;
	private int UseForPoison = 0;
	private int PoisonPower = 0;
	private String FoodType = null;
	private String CustomEatSound = null;
	private boolean RemoveNegativeEffectOnCooked = false;
	private String Chef = null;
	private String OnCooked = null;
	private String WorldTextureCooked;
	private String WorldTextureRotten;
	private String WorldTextureOverdone;
	private int fluReduction = 0;
	private int ReduceFoodSickness = 0;
	private float painReduction = 0.0F;
	private String HerbalistType;
	private float carbohydrates = 0.0F;
	private float lipids = 0.0F;
	private float proteins = 0.0F;
	private float calories = 0.0F;
	private boolean packaged = false;
	private float freezingTime = 0.0F;
	private boolean frozen = false;
	private boolean canBeFrozen = true;
	protected float LastFrozenUpdate = -1.0F;
	public static final float FreezerAgeMultiplier = 0.02F;
	private String replaceOnRotten = null;
	private boolean forceFoodTypeAsName = false;
	private float rottenTime = 0.0F;
	private float compostTime = 0.0F;
	private String onEat = null;
	private boolean badInMicrowave = false;
	private boolean cookedInMicrowave = false;
	private long m_cookingSound = 0L;
	private int m_cookingParameter = -1;
	private static final int COOKING_STATE_COOKING = 0;
	private static final int COOKING_STATE_BURNING = 1;

	public String getCategory() {
		return this.mainCategory != null ? this.mainCategory : "Food";
	}

	public Food(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
		Texture.WarnFailFindTexture = false;
		this.texturerotten = Texture.trygetTexture(string4 + "Rotten");
		this.textureCooked = Texture.trygetTexture(string4 + "Cooked");
		this.textureBurnt = Texture.trygetTexture(string4 + "Overdone");
		String string5 = "Overdone.png";
		if (this.textureBurnt == null) {
			this.textureBurnt = Texture.trygetTexture(string4 + "Burnt");
			if (this.textureBurnt != null) {
				string5 = "Burnt.png";
			}
		}

		String string6 = "Rotten.png";
		if (this.texturerotten == null) {
			this.texturerotten = Texture.trygetTexture(string4 + "Spoiled");
			if (this.texturerotten != null) {
				string6 = "Spoiled.png";
			}
		}

		Texture.WarnFailFindTexture = true;
		if (this.texturerotten == null) {
			this.texturerotten = this.texture;
		}

		if (this.textureCooked == null) {
			this.textureCooked = this.texture;
		}

		if (this.textureBurnt == null) {
			this.textureBurnt = this.texture;
		}

		this.WorldTextureCooked = this.WorldTexture.replace(".png", "Cooked.png");
		this.WorldTextureOverdone = this.WorldTexture.replace(".png", string5);
		this.WorldTextureRotten = this.WorldTexture.replace(".png", string6);
		this.cat = ItemType.Food;
	}

	public Food(String string, String string2, String string3, Item item) {
		super(string, string2, string3, item);
		String string4 = item.ItemName;
		Texture.WarnFailFindTexture = false;
		this.texture = item.NormalTexture;
		if (item.SpecialTextures.size() == 0) {
			boolean boolean1 = false;
		}

		if (item.SpecialTextures.size() > 0) {
			this.texturerotten = (Texture)item.SpecialTextures.get(0);
		}

		if (item.SpecialTextures.size() > 1) {
			this.textureCooked = (Texture)item.SpecialTextures.get(1);
		}

		if (item.SpecialTextures.size() > 2) {
			this.textureBurnt = (Texture)item.SpecialTextures.get(2);
		}

		Texture.WarnFailFindTexture = true;
		if (this.texturerotten == null) {
			this.texturerotten = this.texture;
		}

		if (this.textureCooked == null) {
			this.textureCooked = this.texture;
		}

		if (this.textureBurnt == null) {
			this.textureBurnt = this.texture;
		}

		if (item.SpecialWorldTextureNames.size() > 0) {
			this.WorldTextureRotten = (String)item.SpecialWorldTextureNames.get(0);
		}

		if (item.SpecialWorldTextureNames.size() > 1) {
			this.WorldTextureCooked = (String)item.SpecialWorldTextureNames.get(1);
		}

		if (item.SpecialWorldTextureNames.size() > 2) {
			this.WorldTextureOverdone = (String)item.SpecialWorldTextureNames.get(2);
		}

		this.cat = ItemType.Food;
	}

	public boolean IsFood() {
		return true;
	}

	public int getSaveType() {
		return Item.Type.Food.ordinal();
	}

	public void update() {
		if (this.hasTag("AlreadyCooked")) {
			this.setCooked(true);
		}

		this.updateTemperature();
		ItemContainer itemContainer = this.getOutermostContainer();
		if (itemContainer != null) {
			int int1;
			float float1;
			if (this.IsCookable && !this.isFrozen()) {
				if (this.Heat > 1.6F) {
					int1 = GameTime.getInstance().getMinutes();
					if (int1 != this.LastCookMinute) {
						this.LastCookMinute = int1;
						float1 = this.Heat / 1.5F;
						if (itemContainer.getTemprature() <= 1.6F) {
							float1 *= 0.05F;
						}

						this.CookingTime += float1;
						if (this.shouldPlayCookingSound()) {
							ItemSoundManager.addItem(this);
						}

						if (this.isTaintedWater() && this.CookingTime > Math.min(this.MinutesToCook, 10.0F)) {
							this.setTaintedWater(false);
						}

						if (!this.isCooked() && !this.Burnt && this.CookingTime > this.MinutesToCook) {
							int int2;
							if (this.getReplaceOnCooked() != null) {
								if (GameClient.bClient) {
									GameClient.instance.sendReplaceOnCooked(this);
									this.container.Remove((InventoryItem)this);
									IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
									return;
								} else {
									for (int2 = 0; int2 < this.getReplaceOnCooked().size(); ++int2) {
										InventoryItem inventoryItem = this.container.AddItem((String)this.getReplaceOnCooked().get(int2));
										if (inventoryItem != null) {
											inventoryItem.copyConditionModData(this);
											if (inventoryItem instanceof Food && ((Food)inventoryItem).isBadInMicrowave() && this.container.isMicrowave()) {
												inventoryItem.setUnhappyChange(5.0F);
												inventoryItem.setBoredomChange(5.0F);
												((Food)inventoryItem).cookedInMicrowave = true;
											}
										}
									}

									this.container.Remove((InventoryItem)this);
									IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
									return;
								}
							}

							this.setCooked(true);
							if (this.getScriptItem().RemoveUnhappinessWhenCooked) {
								this.setUnhappyChange(0.0F);
							}

							if (this.type.equals("RicePot") || this.type.equals("PastaPot") || this.type.equals("RicePan") || this.type.equals("PastaPan") || this.type.equals("WaterPotRice") || this.type.equals("WaterPotPasta") || this.type.equals("WaterSaucepanRice") || this.type.equals("WaterSaucepanPasta") || this.type.equals("RiceBowl") || this.type.equals("PastaBowl")) {
								this.setAge(0.0F);
								this.setOffAge(1);
								this.setOffAgeMax(2);
							}

							if (this.isRemoveNegativeEffectOnCooked()) {
								if (this.thirstChange > 0.0F) {
									this.setThirstChange(0.0F);
								}

								if (this.unhappyChange > 0.0F) {
									this.setUnhappyChange(0.0F);
								}

								if (this.boredomChange > 0.0F) {
									this.setBoredomChange(0.0F);
								}
							}

							if (this.getOnCooked() != null) {
								LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(this.getOnCooked()), this);
							}

							if (this.isBadInMicrowave() && this.container.isMicrowave()) {
								this.setUnhappyChange(5.0F);
								this.setBoredomChange(5.0F);
								this.cookedInMicrowave = true;
							}

							if (this.Chef != null && !this.Chef.isEmpty()) {
								for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
									IsoPlayer player = IsoPlayer.players[int2];
									if (player != null && !player.isDead() && this.Chef.equals(player.getFullName())) {
										player.getXp().AddXP(PerkFactory.Perks.Cooking, 10.0F);
										break;
									}
								}
							}
						}

						if (this.CookingTime > this.MinutesToBurn) {
							this.Burnt = true;
							this.setCooked(false);
						}

						if (IsoWorld.instance.isHydroPowerOn() && this.Burnt && this.CookingTime >= 50.0F && this.CookingTime >= this.MinutesToCook * 2.0F + this.MinutesToBurn / 2.0F && Rand.Next(Rand.AdjustForFramerate(200)) == 0) {
							boolean boolean1 = this.container != null && this.container.getParent() != null && this.container.getParent().getName() != null && this.container.getParent().getName().equals("Campfire");
							if (!boolean1 && this.container != null && this.container.getParent() != null && this.container.getParent() instanceof IsoFireplace) {
								boolean1 = true;
							}

							if (this.container != null && this.container.SourceGrid != null && !boolean1) {
								IsoFireManager.StartFire(this.container.SourceGrid.getCell(), this.container.SourceGrid, true, 500000);
								this.IsCookable = false;
							}
						}
					}
				}
			} else if (this.isTaintedWater() && this.Heat > 1.6F && !this.isFrozen()) {
				int1 = GameTime.getInstance().getMinutes();
				if (int1 != this.LastCookMinute) {
					this.LastCookMinute = int1;
					float1 = 1.0F;
					if (itemContainer.getTemprature() <= 1.6F) {
						float1 = (float)((double)float1 * 0.2);
					}

					this.CookingTime += float1;
					if (this.CookingTime > 10.0F) {
						this.setTaintedWater(false);
					}
				}
			}
		}

		this.updateRotting(itemContainer);
	}

	public void updateSound(BaseSoundEmitter baseSoundEmitter) {
		if (this.shouldPlayCookingSound()) {
			if (baseSoundEmitter.isPlaying(this.m_cookingSound)) {
				this.setCookingParameter(baseSoundEmitter);
				return;
			}

			ItemContainer itemContainer = this.getOutermostContainer();
			IsoGridSquare square = itemContainer.getParent().getSquare();
			baseSoundEmitter.setPos((float)square.getX() + 0.5F, (float)square.getY() + 0.5F, (float)square.getZ());
			this.m_cookingSound = baseSoundEmitter.playSoundImpl(this.getCookingSound(), (IsoObject)null);
			this.setCookingParameter(baseSoundEmitter);
		} else {
			baseSoundEmitter.stopOrTriggerSound(this.m_cookingSound);
			this.m_cookingSound = 0L;
			this.m_cookingParameter = -1;
			ItemSoundManager.removeItem(this);
		}
	}

	private boolean shouldPlayCookingSound() {
		if (GameServer.bServer) {
			return false;
		} else if (StringUtils.isNullOrWhitespace(this.getCookingSound())) {
			return false;
		} else {
			ItemContainer itemContainer = this.getOutermostContainer();
			if (itemContainer != null && itemContainer.getParent() != null && itemContainer.getParent().getObjectIndex() != -1 && !(itemContainer.getTemprature() <= 1.6F)) {
				return this.isCookable() && !this.isFrozen() && this.getHeat() > 1.6F;
			} else {
				return false;
			}
		}
	}

	private void setCookingParameter(BaseSoundEmitter baseSoundEmitter) {
		boolean boolean1 = this.CookingTime > this.MinutesToCook;
		int int1 = boolean1 ? 1 : 0;
		if (int1 != this.m_cookingParameter) {
			this.m_cookingParameter = int1;
			baseSoundEmitter.setParameterValue(this.m_cookingSound, FMODManager.instance.getParameterDescription("CookingState"), (float)this.m_cookingParameter);
		}
	}

	private void updateTemperature() {
		ItemContainer itemContainer = this.getOutermostContainer();
		float float1 = itemContainer == null ? 1.0F : itemContainer.getTemprature();
		if (this.Heat > float1) {
			this.Heat -= 0.001F * GameTime.instance.getMultiplier();
			if (this.Heat < Math.max(0.2F, float1)) {
				this.Heat = Math.max(0.2F, float1);
			}
		}

		if (this.Heat < float1) {
			this.Heat += float1 / 1000.0F * GameTime.instance.getMultiplier();
			if (this.Heat > Math.min(3.0F, float1)) {
				this.Heat = Math.min(3.0F, float1);
			}
		}
	}

	private void updateRotting(ItemContainer itemContainer) {
		if ((double)this.OffAgeMax != 1.0E9) {
			if (!GameClient.bClient || this.isInLocalPlayerInventory()) {
				if (!GameServer.bServer || this.container == null || this.getOutermostContainer() == this.container) {
					if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
						this.updateAge();
						if (this.isRotten()) {
							InventoryItem inventoryItem = InventoryItemFactory.CreateItem(this.getModule() + "." + this.replaceOnRotten, this);
							if (inventoryItem == null) {
								String string = this.replaceOnRotten;
								DebugLog.General.warn("ReplaceOnRotten = " + string + " doesn\'t exist for " + this.getFullType());
								this.destroyThisItem();
								return;
							}

							inventoryItem.setAge(this.getAge());
							IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
							if (worldInventoryObject != null && worldInventoryObject.getSquare() != null) {
								IsoGridSquare square = worldInventoryObject.getSquare();
								if (!GameServer.bServer) {
									worldInventoryObject.item = inventoryItem;
									inventoryItem.setWorldItem(worldInventoryObject);
									worldInventoryObject.updateSprite();
									IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
									LuaEventManager.triggerEvent("OnContainerUpdate");
									return;
								}

								square.AddWorldInventoryItem(inventoryItem, worldInventoryObject.xoff, worldInventoryObject.yoff, worldInventoryObject.zoff, true);
							} else if (this.container != null) {
								this.container.AddItem(inventoryItem);
								if (GameServer.bServer) {
									GameServer.sendAddItemToContainer(this.container, inventoryItem);
								}
							}

							this.destroyThisItem();
							return;
						}
					}

					if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() >= 0) {
						if (itemContainer != null && itemContainer.parent instanceof IsoCompost) {
							return;
						}

						this.updateAge();
						if (this.getAge() > (float)(this.getOffAgeMax() + SandboxOptions.instance.DaysForRottenFoodRemoval.getValue())) {
							this.destroyThisItem();
							return;
						}
					}
				}
			}
		}
	}

	public void updateAge() {
		ItemContainer itemContainer = this.getOutermostContainer();
		this.updateFreezing(itemContainer);
		boolean boolean1 = false;
		if (itemContainer != null && itemContainer.getSourceGrid() != null && itemContainer.getSourceGrid().haveElectricity()) {
			boolean1 = true;
		}

		float float1 = (float)GameTime.getInstance().getWorldAgeHours();
		float float2 = 0.2F;
		if (SandboxOptions.instance.FridgeFactor.getValue() == 1) {
			float2 = 0.4F;
		} else if (SandboxOptions.instance.FridgeFactor.getValue() == 2) {
			float2 = 0.3F;
		} else if (SandboxOptions.instance.FridgeFactor.getValue() == 4) {
			float2 = 0.1F;
		} else if (SandboxOptions.instance.FridgeFactor.getValue() == 5) {
			float2 = 0.03F;
		}

		if (this.LastAged < 0.0F) {
			this.LastAged = float1;
		} else if (this.LastAged > float1) {
			this.LastAged = float1;
		}

		if (float1 > this.LastAged) {
			double double1 = (double)(float1 - this.LastAged);
			if (itemContainer != null && this.Heat != itemContainer.getTemprature()) {
				if (double1 < 0.3333333432674408) {
					if (!IsoWorld.instance.getCell().getProcessItems().contains(this)) {
						this.Heat = GameTime.instance.Lerp(this.Heat, itemContainer.getTemprature(), (float)double1 / 0.33333334F);
						IsoWorld.instance.getCell().addToProcessItems((InventoryItem)this);
					}
				} else {
					this.Heat = itemContainer.getTemprature();
				}
			}

			float float3;
			if (this.isFrozen()) {
				double1 *= 0.019999999552965164;
			} else if (itemContainer != null && (itemContainer.getType().equals("fridge") || itemContainer.getType().equals("freezer"))) {
				if (boolean1) {
					double1 *= (double)float2;
				} else if (SandboxOptions.instance.getElecShutModifier() > -1 && this.LastAged < (float)(SandboxOptions.instance.getElecShutModifier() * 24)) {
					float3 = Math.min((float)(SandboxOptions.instance.getElecShutModifier() * 24), float1);
					double1 = (double)((float3 - this.LastAged) * float2);
					if (float1 > (float)(SandboxOptions.instance.getElecShutModifier() * 24)) {
						double1 += (double)(float1 - (float)(SandboxOptions.instance.getElecShutModifier() * 24));
					}
				}
			}

			float3 = 1.0F;
			if (SandboxOptions.instance.FoodRotSpeed.getValue() == 1) {
				float3 = 1.7F;
			} else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 2) {
				float3 = 1.4F;
			} else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 4) {
				float3 = 0.7F;
			} else if (SandboxOptions.instance.FoodRotSpeed.getValue() == 5) {
				float3 = 0.4F;
			}

			boolean boolean2 = !this.Burnt && this.OffAge < 1000000000 && this.Age < (float)this.OffAge;
			boolean boolean3 = !this.Burnt && this.OffAgeMax < 1000000000 && this.Age >= (float)this.OffAgeMax;
			this.Age = (float)((double)this.Age + double1 * (double)float3 / 24.0);
			this.LastAged = float1;
			boolean boolean4 = !this.Burnt && this.OffAge < 1000000000 && this.Age < (float)this.OffAge;
			boolean boolean5 = !this.Burnt && this.OffAgeMax < 1000000000 && this.Age >= (float)this.OffAgeMax;
			if (!GameServer.bServer && (boolean2 != boolean4 || boolean3 != boolean5)) {
				LuaEventManager.triggerEvent("OnContainerUpdate", this);
			}
		}
	}

	public void setAutoAge() {
		ItemContainer itemContainer = this.getOutermostContainer();
		float float1 = (float)GameTime.getInstance().getWorldAgeHours() / 24.0F;
		float1 += (float)((SandboxOptions.instance.TimeSinceApo.getValue() - 1) * 30);
		float float2 = float1;
		boolean boolean1 = false;
		if (itemContainer != null && itemContainer.getParent() != null && itemContainer.getParent().getSprite() != null) {
			boolean1 = itemContainer.getParent().getSprite().getProperties().Is("IsFridge");
		}

		int int1;
		float float3;
		if (itemContainer != null && (boolean1 || itemContainer.getType().equals("fridge") || itemContainer.getType().equals("freezer"))) {
			int1 = SandboxOptions.instance.ElecShutModifier.getValue();
			if (int1 > -1) {
				float3 = Math.min((float)int1, float1);
				int int2 = SandboxOptions.instance.FridgeFactor.getValue();
				float float4 = 0.2F;
				if (int2 == 1) {
					float4 = 0.4F;
				} else if (int2 == 2) {
					float4 = 0.3F;
				} else if (int2 == 4) {
					float4 = 0.1F;
				} else if (int2 == 5) {
					float4 = 0.03F;
				}

				if (!itemContainer.getType().equals("fridge") && this.canBeFrozen() && !boolean1) {
					float float5 = float3;
					float float6 = 100.0F;
					if (float1 > float3) {
						float float7 = (float1 - float3) * 24.0F;
						float float8 = 1440.0F / GameTime.getInstance().getMinutesPerDay() * 60.0F * 5.0F;
						float float9 = 0.0095999995F;
						float6 -= float9 * float8 * float7;
						if (float6 > 0.0F) {
							float5 = float3 + float7 / 24.0F;
						} else {
							float float10 = 100.0F / (float9 * float8);
							float5 = float3 + float10 / 24.0F;
							float6 = 0.0F;
						}
					}

					float2 = float1 - float5;
					float2 += float5 * 0.02F;
					this.setFreezingTime(float6);
				} else {
					float2 = float1 - float3;
					float2 += float3 * float4;
				}
			}
		}

		int1 = SandboxOptions.instance.FoodRotSpeed.getValue();
		float3 = 1.0F;
		if (int1 == 1) {
			float3 = 1.7F;
		} else if (int1 == 2) {
			float3 = 1.4F;
		} else if (int1 == 4) {
			float3 = 0.7F;
		} else if (int1 == 5) {
			float3 = 0.4F;
		}

		this.Age = float2 * float3;
		this.LastAged = (float)GameTime.getInstance().getWorldAgeHours();
		this.LastFrozenUpdate = this.LastAged;
		if (itemContainer != null) {
			this.setHeat(itemContainer.getTemprature());
		}
	}

	public void updateFreezing(ItemContainer itemContainer) {
		float float1 = (float)GameTime.getInstance().getWorldAgeHours();
		if (this.LastFrozenUpdate < 0.0F) {
			this.LastFrozenUpdate = float1;
		} else if (this.LastFrozenUpdate > float1) {
			this.LastFrozenUpdate = float1;
		}

		if (float1 > this.LastFrozenUpdate) {
			float float2 = float1 - this.LastFrozenUpdate;
			float float3 = 4.0F;
			float float4 = 1.5F;
			if (this.isFreezing()) {
				this.setFreezingTime(this.getFreezingTime() + float2 / float3 * 100.0F);
			}

			if (this.isThawing()) {
				float float5 = float4;
				if (itemContainer != null && "fridge".equals(itemContainer.getType()) && itemContainer.isPowered()) {
					float5 = float4 * 2.0F;
				}

				if (itemContainer != null && itemContainer.getTemprature() > 1.0F) {
					float5 /= 6.0F;
				}

				this.setFreezingTime(this.getFreezingTime() - float2 / float5 * 100.0F);
			}

			this.LastFrozenUpdate = float1;
		}
	}

	public float getActualWeight() {
		float float1;
		float float2;
		if (this.haveExtraItems()) {
			float1 = this.getHungChange();
			float float3 = this.getBaseHunger();
			float2 = float3 == 0.0F ? 0.0F : float1 / float3;
			float float4 = 0.0F;
			if (this.getReplaceOnUse() != null) {
				String string = this.getReplaceOnUseFullType();
				Item item = ScriptManager.instance.getItem(string);
				if (item != null) {
					float4 = item.getActualWeight();
				}
			}

			float float5 = super.getActualWeight() + this.getExtraItemsWeight();
			float float6 = (float5 - float4) * float2 + float4;
			return float6;
		} else {
			if (this.getReplaceOnUse() != null && !this.isCustomWeight()) {
				String string2 = this.getReplaceOnUseFullType();
				Item item2 = ScriptManager.instance.getItem(string2);
				if (item2 != null) {
					float2 = 1.0F;
					if (this.getScriptItem().getHungerChange() < 0.0F) {
						float2 = this.getHungChange() * 100.0F / this.getScriptItem().getHungerChange();
					} else if (this.getScriptItem().getThirstChange() < 0.0F) {
						float2 = this.getThirstChange() * 100.0F / this.getScriptItem().getThirstChange();
					}

					return (this.getScriptItem().getActualWeight() - item2.getActualWeight()) * float2 + item2.getActualWeight();
				}
			} else if (!this.isCustomWeight()) {
				float1 = 1.0F;
				if (this.getScriptItem().getHungerChange() < 0.0F) {
					float1 = this.getHungChange() * 100.0F / this.getScriptItem().getHungerChange();
				} else if (this.getScriptItem().getThirstChange() < 0.0F) {
					float1 = this.getThirstChange() * 100.0F / this.getScriptItem().getThirstChange();
				}

				return this.getScriptItem().getActualWeight() * float1;
			}

			return super.getActualWeight();
		}
	}

	public float getWeight() {
		return this.getReplaceOnUse() != null ? this.getActualWeight() : super.getWeight();
	}

	public boolean CanStack(InventoryItem inventoryItem) {
		return false;
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putFloat(this.Age);
		byteBuffer.putFloat(this.LastAged);
		BitHeaderWrite bitHeaderWrite = BitHeader.allocWrite(BitHeader.HeaderSize.Byte, byteBuffer);
		if (this.calories != 0.0F || this.proteins != 0.0F || this.lipids != 0.0F || this.carbohydrates != 0.0F) {
			bitHeaderWrite.addFlags(1);
			byteBuffer.putFloat(this.calories);
			byteBuffer.putFloat(this.proteins);
			byteBuffer.putFloat(this.lipids);
			byteBuffer.putFloat(this.carbohydrates);
		}

		if (this.hungChange != 0.0F) {
			bitHeaderWrite.addFlags(2);
			byteBuffer.putFloat(this.hungChange);
		}

		if (this.baseHunger != 0.0F) {
			bitHeaderWrite.addFlags(4);
			byteBuffer.putFloat(this.baseHunger);
		}

		if (this.unhappyChange != 0.0F) {
			bitHeaderWrite.addFlags(8);
			byteBuffer.putFloat(this.unhappyChange);
		}

		if (this.boredomChange != 0.0F) {
			bitHeaderWrite.addFlags(16);
			byteBuffer.putFloat(this.boredomChange);
		}

		if (this.thirstChange != 0.0F) {
			bitHeaderWrite.addFlags(32);
			byteBuffer.putFloat(this.thirstChange);
		}

		BitHeaderWrite bitHeaderWrite2 = BitHeader.allocWrite(BitHeader.HeaderSize.Integer, byteBuffer);
		if (this.Heat != 1.0F) {
			bitHeaderWrite2.addFlags(1);
			byteBuffer.putFloat(this.Heat);
		}

		if (this.LastCookMinute != 0) {
			bitHeaderWrite2.addFlags(2);
			byteBuffer.putInt(this.LastCookMinute);
		}

		if (this.CookingTime != 0.0F) {
			bitHeaderWrite2.addFlags(4);
			byteBuffer.putFloat(this.CookingTime);
		}

		if (this.Cooked) {
			bitHeaderWrite2.addFlags(8);
		}

		if (this.Burnt) {
			bitHeaderWrite2.addFlags(16);
		}

		if (this.IsCookable) {
			bitHeaderWrite2.addFlags(32);
		}

		if (this.bDangerousUncooked) {
			bitHeaderWrite2.addFlags(64);
		}

		if (this.poisonDetectionLevel != -1) {
			bitHeaderWrite2.addFlags(128);
			byteBuffer.put((byte)this.poisonDetectionLevel);
		}

		if (this.spices != null) {
			bitHeaderWrite2.addFlags(256);
			byteBuffer.put((byte)this.spices.size());
			Iterator iterator = this.spices.iterator();
			while (iterator.hasNext()) {
				String string = (String)iterator.next();
				GameWindow.WriteString(byteBuffer, string);
			}
		}

		if (this.PoisonPower != 0) {
			bitHeaderWrite2.addFlags(512);
			byteBuffer.put((byte)this.PoisonPower);
		}

		if (this.Chef != null) {
			bitHeaderWrite2.addFlags(1024);
			GameWindow.WriteString(byteBuffer, this.Chef);
		}

		if ((double)this.OffAge != 1.0E9) {
			bitHeaderWrite2.addFlags(2048);
			byteBuffer.putInt(this.OffAge);
		}

		if ((double)this.OffAgeMax != 1.0E9) {
			bitHeaderWrite2.addFlags(4096);
			byteBuffer.putInt(this.OffAgeMax);
		}

		if (this.painReduction != 0.0F) {
			bitHeaderWrite2.addFlags(8192);
			byteBuffer.putFloat(this.painReduction);
		}

		if (this.fluReduction != 0) {
			bitHeaderWrite2.addFlags(16384);
			byteBuffer.putInt(this.fluReduction);
		}

		if (this.ReduceFoodSickness != 0) {
			bitHeaderWrite2.addFlags(32768);
			byteBuffer.putInt(this.ReduceFoodSickness);
		}

		if (this.Poison) {
			bitHeaderWrite2.addFlags(65536);
		}

		if (this.UseForPoison != 0) {
			bitHeaderWrite2.addFlags(131072);
			byteBuffer.putShort((short)this.UseForPoison);
		}

		if (this.freezingTime != 0.0F) {
			bitHeaderWrite2.addFlags(262144);
			byteBuffer.putFloat(this.freezingTime);
		}

		if (this.isFrozen()) {
			bitHeaderWrite2.addFlags(524288);
		}

		if (this.LastFrozenUpdate != 0.0F) {
			bitHeaderWrite2.addFlags(1048576);
			byteBuffer.putFloat(this.LastFrozenUpdate);
		}

		if (this.rottenTime != 0.0F) {
			bitHeaderWrite2.addFlags(2097152);
			byteBuffer.putFloat(this.rottenTime);
		}

		if (this.compostTime != 0.0F) {
			bitHeaderWrite2.addFlags(4194304);
			byteBuffer.putFloat(this.compostTime);
		}

		if (this.cookedInMicrowave) {
			bitHeaderWrite2.addFlags(8388608);
		}

		if (this.fatigueChange != 0.0F) {
			bitHeaderWrite2.addFlags(16777216);
			byteBuffer.putFloat(this.fatigueChange);
		}

		if (this.endChange != 0.0F) {
			bitHeaderWrite2.addFlags(33554432);
			byteBuffer.putFloat(this.endChange);
		}

		if (!bitHeaderWrite2.equals(0)) {
			bitHeaderWrite.addFlags(64);
			bitHeaderWrite2.write();
		} else {
			byteBuffer.position(bitHeaderWrite2.getStartPosition());
		}

		bitHeaderWrite.write();
		bitHeaderWrite.release();
		bitHeaderWrite2.release();
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		this.calories = 0.0F;
		this.proteins = 0.0F;
		this.lipids = 0.0F;
		this.carbohydrates = 0.0F;
		this.hungChange = 0.0F;
		this.baseHunger = 0.0F;
		this.unhappyChange = 0.0F;
		this.boredomChange = 0.0F;
		this.thirstChange = 0.0F;
		this.Heat = 1.0F;
		this.LastCookMinute = 0;
		this.CookingTime = 0.0F;
		this.Cooked = false;
		this.Burnt = false;
		this.IsCookable = false;
		this.bDangerousUncooked = false;
		this.poisonDetectionLevel = -1;
		this.spices = null;
		this.PoisonPower = 0;
		this.Chef = null;
		this.OffAge = 1000000000;
		this.OffAgeMax = 1000000000;
		this.painReduction = 0.0F;
		this.fluReduction = 0;
		this.ReduceFoodSickness = 0;
		this.Poison = false;
		this.UseForPoison = 0;
		this.freezingTime = 0.0F;
		this.frozen = false;
		this.LastFrozenUpdate = 0.0F;
		this.rottenTime = 0.0F;
		this.compostTime = 0.0F;
		this.cookedInMicrowave = false;
		this.fatigueChange = 0.0F;
		this.endChange = 0.0F;
		this.Age = byteBuffer.getFloat();
		this.LastAged = byteBuffer.getFloat();
		BitHeaderRead bitHeaderRead = BitHeader.allocRead(BitHeader.HeaderSize.Byte, byteBuffer);
		if (!bitHeaderRead.equals(0)) {
			if (bitHeaderRead.hasFlags(1)) {
				this.calories = byteBuffer.getFloat();
				this.proteins = byteBuffer.getFloat();
				this.lipids = byteBuffer.getFloat();
				this.carbohydrates = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(2)) {
				this.hungChange = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(4)) {
				this.baseHunger = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(8)) {
				this.unhappyChange = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(16)) {
				this.boredomChange = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(32)) {
				this.thirstChange = byteBuffer.getFloat();
			}

			if (bitHeaderRead.hasFlags(64)) {
				BitHeaderRead bitHeaderRead2 = BitHeader.allocRead(BitHeader.HeaderSize.Integer, byteBuffer);
				if (bitHeaderRead2.hasFlags(1)) {
					this.Heat = byteBuffer.getFloat();
				}

				if (bitHeaderRead2.hasFlags(2)) {
					this.LastCookMinute = byteBuffer.getInt();
				}

				if (bitHeaderRead2.hasFlags(4)) {
					this.CookingTime = byteBuffer.getFloat();
				}

				this.Cooked = bitHeaderRead2.hasFlags(8);
				this.Burnt = bitHeaderRead2.hasFlags(16);
				this.IsCookable = bitHeaderRead2.hasFlags(32);
				this.bDangerousUncooked = bitHeaderRead2.hasFlags(64);
				if (bitHeaderRead2.hasFlags(128)) {
					this.poisonDetectionLevel = byteBuffer.get();
				}

				if (bitHeaderRead2.hasFlags(256)) {
					this.spices = new ArrayList();
					byte byte1 = byteBuffer.get();
					for (int int2 = 0; int2 < byte1; ++int2) {
						String string = GameWindow.ReadString(byteBuffer);
						this.spices.add(string);
					}
				}

				if (bitHeaderRead2.hasFlags(512)) {
					this.PoisonPower = byteBuffer.get();
				}

				if (bitHeaderRead2.hasFlags(1024)) {
					this.Chef = GameWindow.ReadString(byteBuffer);
				}

				if (bitHeaderRead2.hasFlags(2048)) {
					this.OffAge = byteBuffer.getInt();
				}

				if (bitHeaderRead2.hasFlags(4096)) {
					this.OffAgeMax = byteBuffer.getInt();
				}

				if (bitHeaderRead2.hasFlags(8192)) {
					this.painReduction = byteBuffer.getFloat();
				}

				if (bitHeaderRead2.hasFlags(16384)) {
					this.fluReduction = byteBuffer.getInt();
				}

				if (bitHeaderRead2.hasFlags(32768)) {
					this.ReduceFoodSickness = byteBuffer.getInt();
				}

				this.Poison = bitHeaderRead2.hasFlags(65536);
				if (bitHeaderRead2.hasFlags(131072)) {
					this.UseForPoison = byteBuffer.getShort();
				}

				if (bitHeaderRead2.hasFlags(262144)) {
					this.freezingTime = byteBuffer.getFloat();
				}

				this.setFrozen(bitHeaderRead2.hasFlags(524288));
				if (bitHeaderRead2.hasFlags(1048576)) {
					this.LastFrozenUpdate = byteBuffer.getFloat();
				}

				if (bitHeaderRead2.hasFlags(2097152)) {
					this.rottenTime = byteBuffer.getFloat();
				}

				if (bitHeaderRead2.hasFlags(4194304)) {
					this.compostTime = byteBuffer.getFloat();
				}

				this.cookedInMicrowave = bitHeaderRead2.hasFlags(8388608);
				if (bitHeaderRead2.hasFlags(16777216)) {
					this.fatigueChange = byteBuffer.getFloat();
				}

				if (bitHeaderRead2.hasFlags(33554432)) {
					this.endChange = byteBuffer.getFloat();
				}

				bitHeaderRead2.release();
			}
		}

		bitHeaderRead.release();
		if (GameServer.bServer && this.LastAged == -1.0F) {
			this.LastAged = (float)GameTime.getInstance().getWorldAgeHours();
		}
	}

	public boolean finishupdate() {
		if (this.container == null && (this.getWorldItem() == null || this.getWorldItem().getSquare() == null)) {
			return true;
		} else if (this.IsCookable) {
			return false;
		} else if (this.container != null && (this.Heat != this.container.getTemprature() || this.container.isTemperatureChanging())) {
			return false;
		} else if (this.isTaintedWater() && this.container != null && this.container.getTemprature() > 1.0F) {
			return false;
		} else {
			if ((!GameClient.bClient || this.isInLocalPlayerInventory()) && (double)this.OffAgeMax != 1.0E9) {
				if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
					return false;
				}

				if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() != -1) {
					return false;
				}
			}

			return true;
		}
	}

	public boolean shouldUpdateInWorld() {
		if (!GameClient.bClient && (double)this.OffAgeMax != 1.0E9) {
			if (this.replaceOnRotten != null && !this.replaceOnRotten.isEmpty()) {
				return true;
			}

			if (SandboxOptions.instance.DaysForRottenFoodRemoval.getValue() != -1) {
				return true;
			}
		}

		return this.getHeat() != 1.0F;
	}

	public String getName() {
		String string = "";
		if (this.Burnt) {
			string = string + this.BurntString + " ";
		} else if (this.OffAge < 1000000000 && this.Age < (float)this.OffAge) {
			string = string + this.FreshString + " ";
		} else if (this.OffAgeMax < 1000000000 && this.Age >= (float)this.OffAgeMax) {
			string = string + this.OffString + " ";
		}

		if (this.isCooked() && !this.Burnt && !this.hasTag("HideCooked")) {
			string = string + this.CookedString + " ";
		} else if (this.IsCookable && !this.Burnt && !this.hasTag("HideCooked")) {
			string = string + this.UnCookedString + " ";
		}

		if (this.isFrozen()) {
			string = string + this.FrozenString + " ";
		}

		string = string.trim();
		return string.isEmpty() ? this.name : Translator.getText("IGUI_FoodNaming", string, this.name);
	}

	public void DoTooltip(ObjectTooltip objectTooltip, ObjectTooltip.Layout layout) {
		ObjectTooltip.LayoutItem layoutItem;
		int int1;
		if (this.getHungerChange() != 0.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Hunger") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			int1 = (int)(this.getHungerChange() * 100.0F);
			layoutItem.setValueRight(int1, false);
		}

		if (this.getThirstChange() != 0.0F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Thirst") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			int1 = (int)(this.getThirstChange() * 100.0F);
			layoutItem.setValueRight(int1, false);
		}

		if (this.getEnduranceChange() != 0.0F) {
			layoutItem = layout.addItem();
			int1 = (int)(this.getEnduranceChange() * 100.0F);
			layoutItem.setLabel(Translator.getText("Tooltip_food_Endurance") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight(int1, true);
		}

		if (this.getStressChange() != 0.0F) {
			layoutItem = layout.addItem();
			int1 = (int)(this.getStressChange() * 100.0F);
			layoutItem.setLabel(Translator.getText("Tooltip_food_Stress") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight(int1, false);
		}

		if (this.getBoredomChange() != 0.0F) {
			layoutItem = layout.addItem();
			int1 = (int)this.getBoredomChange();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Boredom") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight(int1, false);
		}

		if (this.getUnhappyChange() != 0.0F) {
			layoutItem = layout.addItem();
			int1 = (int)this.getUnhappyChange();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Unhappiness") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRight(int1, false);
		}

		float float1;
		float float2;
		float float3;
		float float4;
		float float5;
		float float6;
		float float7;
		float float8;
		if (this.isIsCookable() && !this.isFrozen() && !this.Burnt && (double)this.getHeat() > 1.6) {
			float8 = this.getCookingTime();
			float1 = this.getMinutesToCook();
			float2 = this.getMinutesToBurn();
			float3 = float8 / float1;
			float4 = 0.0F;
			float5 = 0.6F;
			float6 = 0.0F;
			float7 = 0.7F;
			float float9 = 1.0F;
			float float10 = 1.0F;
			float float11 = 0.8F;
			String string = Translator.getText("IGUI_invpanel_Cooking");
			if (float8 > float1) {
				string = Translator.getText("IGUI_invpanel_Burning");
				float9 = 1.0F;
				float10 = 0.0F;
				float11 = 0.0F;
				float3 = (float8 - float1) / (float2 - float1);
				float4 = 0.6F;
				float5 = 0.0F;
				float6 = 0.0F;
			}

			layoutItem = layout.addItem();
			layoutItem.setLabel(string + ": ", float9, float10, float11, 1.0F);
			layoutItem.setProgress(float3, float4, float5, float6, float7);
		}

		if (this.getFreezingTime() < 100.0F && this.getFreezingTime() > 0.0F) {
			float8 = this.getFreezingTime() / 100.0F;
			float1 = 0.0F;
			float2 = 0.6F;
			float3 = 0.0F;
			float4 = 0.7F;
			float5 = 1.0F;
			float6 = 1.0F;
			float7 = 0.8F;
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("IGUI_invpanel_FreezingTime") + ": ", float5, float6, float7, 1.0F);
			layoutItem.setProgress(float8, float1, float2, float3, float4);
		}

		if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue() || this.isPackaged() || objectTooltip.getCharacter() != null && (objectTooltip.getCharacter().Traits.Nutritionist.isSet() || objectTooltip.getCharacter().Traits.Nutritionist2.isSet())) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Calories") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getCalories());
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Carbs") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getCarbohydrates());
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Prots") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getProteins());
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Fat") + ":", 1.0F, 1.0F, 0.8F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getLipids());
		}

		if (this.isbDangerousUncooked() && !this.isCooked() && !this.isBurnt()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_Dangerous_uncooked"), 1.0F, 0.0F, 0.0F, 1.0F);
			if (this.hasTag("Egg")) {
				layoutItem.setLabel(Translator.getText("Tooltip_food_SlightDanger_uncooked"), 1.0F, 0.0F, 0.0F, 1.0F);
			}
		}

		if (this.getScriptItem().RemoveUnhappinessWhenCooked && !this.isCooked()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_CookToRemoveUnhappiness"), 1.0F, 0.0F, 0.0F, 1.0F);
		}

		if ((this.isGoodHot() || this.isBadCold()) && this.Heat < 1.3F) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_BetterHot"), 1.0F, 0.9F, 0.9F, 1.0F);
		}

		if (this.cookedInMicrowave) {
			layoutItem = layout.addItem();
			layoutItem.setLabel(Translator.getText("Tooltip_food_CookedInMicrowave"), 1.0F, 0.9F, 0.9F, 1.0F);
		}

		if (Core.bDebug && DebugOptions.instance.TooltipInfo.getValue()) {
			layoutItem = layout.addItem();
			layoutItem.setLabel("DBG: BaseHunger", 0.0F, 1.0F, 0.0F, 1.0F);
			layoutItem.setValueRight((int)(this.getBaseHunger() * 100.0F), false);
			layoutItem = layout.addItem();
			layoutItem.setLabel("DBG: Age", 0.0F, 1.0F, 0.0F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getAge() * 24.0F);
			if ((double)this.getOffAgeMax() != 1.0E9) {
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: Age Fresh", 0.0F, 1.0F, 0.0F, 1.0F);
				layoutItem.setValueRightNoPlus((float)this.getOffAge() * 24.0F);
				layoutItem = layout.addItem();
				layoutItem.setLabel("DBG: Age Rotten", 0.0F, 1.0F, 0.0F, 1.0F);
				layoutItem.setValueRightNoPlus(this.getOffAgeMax() * 24);
			}

			layoutItem = layout.addItem();
			layoutItem.setLabel("DBG: Heat", 0.0F, 1.0F, 0.0F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getHeat());
			layoutItem = layout.addItem();
			layoutItem.setLabel("DBG: Freeze Time", 0.0F, 1.0F, 0.0F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getFreezingTime());
			layoutItem = layout.addItem();
			layoutItem.setLabel("DBG: Compost Time", 0.0F, 1.0F, 0.0F, 1.0F);
			layoutItem.setValueRightNoPlus(this.getCompostTime());
		}
	}

	public float getEnduranceChange() {
		if (this.Burnt) {
			return this.endChange / 3.0F;
		} else if (this.Age >= (float)this.OffAge && this.Age < (float)this.OffAgeMax) {
			return this.endChange / 2.0F;
		} else {
			return this.isCooked() ? this.endChange * 2.0F : this.endChange;
		}
	}

	public void setEnduranceChange(float float1) {
		this.endChange = float1;
	}

	public float getUnhappyChange() {
		float float1 = this.unhappyChange;
		if (this.isFrozen() && !"Icecream".equals(this.getType())) {
			float1 += 30.0F;
		}

		if (this.Burnt) {
			float1 += 20.0F;
		}

		if (this.Age >= (float)this.OffAge && this.Age < (float)this.OffAgeMax) {
			float1 += 10.0F;
		}

		if (this.Age >= (float)this.OffAgeMax) {
			float1 += 20.0F;
		}

		if (this.isBadCold() && this.IsCookable && this.isCooked() && this.Heat < 1.3F) {
			float1 += 2.0F;
		}

		if (this.isGoodHot() && this.IsCookable && this.isCooked() && this.Heat > 1.3F) {
			float1 -= 2.0F;
		}

		return float1;
	}

	public float getBoredomChange() {
		float float1 = this.boredomChange;
		if (this.isFrozen() && !"Icecream".equals(this.getType())) {
			float1 += 30.0F;
		}

		if (this.Burnt) {
			float1 += 20.0F;
		}

		if (this.Age >= (float)this.OffAge && this.Age < (float)this.OffAgeMax) {
			float1 += 10.0F;
		}

		if (this.Age >= (float)this.OffAgeMax) {
			float1 += 20.0F;
		}

		return float1;
	}

	public float getHungerChange() {
		float float1 = this.hungChange;
		if (this.Burnt) {
			return float1 / 3.0F;
		} else if (this.Age >= (float)this.OffAge && this.Age < (float)this.OffAgeMax) {
			return float1 / 1.3F;
		} else if (this.Age >= (float)this.OffAgeMax) {
			return float1 / 2.2F;
		} else {
			return this.isCooked() ? float1 * 1.3F : float1;
		}
	}

	public float getStressChange() {
		if (this.Burnt) {
			return this.stressChange / 4.0F;
		} else if (this.Age >= (float)this.OffAge && this.Age < (float)this.OffAgeMax) {
			return this.stressChange / 1.3F;
		} else if (this.Age >= (float)this.OffAgeMax) {
			return this.stressChange / 2.0F;
		} else {
			return this.isCooked() ? this.stressChange * 1.3F : this.stressChange;
		}
	}

	public float getBoredomChangeUnmodified() {
		return this.boredomChange;
	}

	public float getEnduranceChangeUnmodified() {
		return this.endChange;
	}

	public float getStressChangeUnmodified() {
		return this.stressChange;
	}

	public float getThirstChangeUnmodified() {
		return this.thirstChange;
	}

	public float getUnhappyChangeUnmodified() {
		return this.unhappyChange;
	}

	public float getScore(SurvivorDesc survivorDesc) {
		float float1 = 0.0F;
		float1 -= this.getHungerChange() * 100.0F;
		return float1;
	}

	public boolean isBadCold() {
		return this.bBadCold;
	}

	public void setBadCold(boolean boolean1) {
		this.bBadCold = boolean1;
	}

	public boolean isGoodHot() {
		return this.bGoodHot;
	}

	public void setGoodHot(boolean boolean1) {
		this.bGoodHot = boolean1;
	}

	public boolean isCookedInMicrowave() {
		return this.cookedInMicrowave;
	}

	public void setCookedInMicrowave(boolean boolean1) {
		this.cookedInMicrowave = boolean1;
	}

	public float getHeat() {
		return this.Heat;
	}

	public float getInvHeat() {
		return this.Heat > 1.0F ? (this.Heat - 1.0F) / 2.0F : 1.0F - (this.Heat - 0.2F) / 0.8F;
	}

	public void setHeat(float float1) {
		this.Heat = float1;
	}

	public float getEndChange() {
		return this.endChange;
	}

	public void setEndChange(float float1) {
		this.endChange = float1;
	}

	@Deprecated
	public float getBaseHungChange() {
		return this.getHungChange();
	}

	public float getHungChange() {
		return this.hungChange;
	}

	public void setHungChange(float float1) {
		this.hungChange = float1;
	}

	public String getUseOnConsume() {
		return this.useOnConsume;
	}

	public void setUseOnConsume(String string) {
		this.useOnConsume = string;
	}

	public boolean isRotten() {
		return this.Age >= (float)this.OffAgeMax;
	}

	public boolean isFresh() {
		return this.Age < (float)this.OffAge;
	}

	public void setRotten(boolean boolean1) {
		this.rotten = boolean1;
	}

	public boolean isbDangerousUncooked() {
		return this.bDangerousUncooked;
	}

	public void setbDangerousUncooked(boolean boolean1) {
		this.bDangerousUncooked = boolean1;
	}

	public int getLastCookMinute() {
		return this.LastCookMinute;
	}

	public void setLastCookMinute(int int1) {
		this.LastCookMinute = int1;
	}

	public float getThirstChange() {
		float float1 = this.thirstChange;
		if (this.Burnt) {
			return float1 / 5.0F;
		} else {
			return this.isCooked() ? float1 / 2.0F : float1;
		}
	}

	public void setThirstChange(float float1) {
		this.thirstChange = float1;
	}

	public void setReplaceOnCooked(List list) {
		this.ReplaceOnCooked = list;
	}

	public List getReplaceOnCooked() {
		return this.ReplaceOnCooked;
	}

	public float getBaseHunger() {
		return this.baseHunger;
	}

	public void setBaseHunger(float float1) {
		this.baseHunger = float1;
	}

	public boolean isSpice() {
		return this.isSpice;
	}

	public void setSpice(boolean boolean1) {
		this.isSpice = boolean1;
	}

	public boolean isPoison() {
		return this.Poison;
	}

	public int getPoisonDetectionLevel() {
		return this.poisonDetectionLevel;
	}

	public void setPoisonDetectionLevel(int int1) {
		this.poisonDetectionLevel = int1;
	}

	public Integer getPoisonLevelForRecipe() {
		return this.PoisonLevelForRecipe;
	}

	public void setPoisonLevelForRecipe(Integer integer) {
		this.PoisonLevelForRecipe = integer;
	}

	public int getUseForPoison() {
		return this.UseForPoison;
	}

	public void setUseForPoison(int int1) {
		this.UseForPoison = int1;
	}

	public int getPoisonPower() {
		return this.PoisonPower;
	}

	public void setPoisonPower(int int1) {
		this.PoisonPower = int1;
	}

	public String getFoodType() {
		return this.FoodType;
	}

	public void setFoodType(String string) {
		this.FoodType = string;
	}

	public boolean isRemoveNegativeEffectOnCooked() {
		return this.RemoveNegativeEffectOnCooked;
	}

	public void setRemoveNegativeEffectOnCooked(boolean boolean1) {
		this.RemoveNegativeEffectOnCooked = boolean1;
	}

	public String getCookingSound() {
		return this.getScriptItem().getCookingSound();
	}

	public String getCustomEatSound() {
		return this.CustomEatSound;
	}

	public void setCustomEatSound(String string) {
		this.CustomEatSound = string;
	}

	public String getChef() {
		return this.Chef;
	}

	public void setChef(String string) {
		this.Chef = string;
	}

	public String getOnCooked() {
		return this.OnCooked;
	}

	public void setOnCooked(String string) {
		this.OnCooked = string;
	}

	public String getHerbalistType() {
		return this.HerbalistType;
	}

	public void setHerbalistType(String string) {
		this.HerbalistType = string;
	}

	public ArrayList getSpices() {
		return this.spices;
	}

	public void setSpices(ArrayList arrayList) {
		if (arrayList != null && !arrayList.isEmpty()) {
			if (this.spices == null) {
				this.spices = new ArrayList(arrayList);
			} else {
				this.spices.clear();
				this.spices.addAll(arrayList);
			}
		} else {
			if (this.spices != null) {
				this.spices.clear();
			}
		}
	}

	public Texture getTex() {
		if (this.Burnt) {
			return this.textureBurnt;
		} else if (this.Age >= (float)this.OffAgeMax) {
			return this.texturerotten;
		} else {
			return this.isCooked() ? this.textureCooked : super.getTex();
		}
	}

	public String getWorldTexture() {
		if (this.Burnt) {
			return this.WorldTextureOverdone;
		} else if (this.Age >= (float)this.OffAgeMax) {
			return this.WorldTextureRotten;
		} else {
			return this.isCooked() ? this.WorldTextureCooked : this.WorldTexture;
		}
	}

	public int getReduceFoodSickness() {
		return this.ReduceFoodSickness;
	}

	public void setReduceFoodSickness(int int1) {
		this.ReduceFoodSickness = int1;
	}

	public int getFluReduction() {
		return this.fluReduction;
	}

	public void setFluReduction(int int1) {
		this.fluReduction = int1;
	}

	public float getPainReduction() {
		return this.painReduction;
	}

	public void setPainReduction(float float1) {
		this.painReduction = float1;
	}

	public float getCarbohydrates() {
		return this.carbohydrates;
	}

	public void setCarbohydrates(float float1) {
		this.carbohydrates = float1;
	}

	public float getLipids() {
		return this.lipids;
	}

	public void setLipids(float float1) {
		this.lipids = float1;
	}

	public float getProteins() {
		return this.proteins;
	}

	public void setProteins(float float1) {
		this.proteins = float1;
	}

	public float getCalories() {
		return this.calories;
	}

	public void setCalories(float float1) {
		this.calories = float1;
	}

	public boolean isPackaged() {
		return this.packaged;
	}

	public void setPackaged(boolean boolean1) {
		this.packaged = boolean1;
	}

	public float getFreezingTime() {
		return this.freezingTime;
	}

	public void setFreezingTime(float float1) {
		if (float1 >= 100.0F) {
			this.setFrozen(true);
			float1 = 100.0F;
		} else if (float1 <= 0.0F) {
			float1 = 0.0F;
			this.setFrozen(false);
		}

		this.freezingTime = float1;
	}

	public void freeze() {
		this.setFreezingTime(100.0F);
	}

	public boolean isFrozen() {
		return this.frozen;
	}

	public void setFrozen(boolean boolean1) {
		this.frozen = boolean1;
	}

	public boolean canBeFrozen() {
		return this.canBeFrozen;
	}

	public void setCanBeFrozen(boolean boolean1) {
		this.canBeFrozen = boolean1;
	}

	public boolean isFreezing() {
		return this.canBeFrozen() && !(this.getFreezingTime() >= 100.0F) && this.getOutermostContainer() != null && "freezer".equals(this.getOutermostContainer().getType()) ? this.getOutermostContainer().isPowered() : false;
	}

	public boolean isThawing() {
		if (this.canBeFrozen() && !(this.getFreezingTime() <= 0.0F)) {
			if (this.getOutermostContainer() != null && "freezer".equals(this.getOutermostContainer().getType())) {
				return !this.getOutermostContainer().isPowered();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}

	public String getReplaceOnRotten() {
		return this.replaceOnRotten;
	}

	public void setReplaceOnRotten(String string) {
		this.replaceOnRotten = string;
	}

	public void multiplyFoodValues(float float1) {
		this.setBoredomChange(this.getBoredomChangeUnmodified() * float1);
		this.setUnhappyChange(this.getUnhappyChangeUnmodified() * float1);
		this.setHungChange(this.getHungChange() * float1);
		this.setFluReduction((int)((float)this.getFluReduction() * float1));
		this.setThirstChange(this.getThirstChangeUnmodified() * float1);
		this.setPainReduction(this.getPainReduction() * float1);
		this.setReduceFoodSickness((int)((float)this.getReduceFoodSickness() * float1));
		this.setEndChange(this.getEnduranceChangeUnmodified() * float1);
		this.setStressChange(this.getStressChangeUnmodified() * float1);
		this.setFatigueChange(this.getFatigueChange() * float1);
		this.setCalories(this.getCalories() * float1);
		this.setCarbohydrates(this.getCarbohydrates() * float1);
		this.setProteins(this.getProteins() * float1);
		this.setLipids(this.getLipids() * float1);
	}

	public float getRottenTime() {
		return this.rottenTime;
	}

	public void setRottenTime(float float1) {
		this.rottenTime = float1;
	}

	public float getCompostTime() {
		return this.compostTime;
	}

	public void setCompostTime(float float1) {
		this.compostTime = float1;
	}

	public String getOnEat() {
		return this.onEat;
	}

	public void setOnEat(String string) {
		this.onEat = string;
	}

	public boolean isBadInMicrowave() {
		return this.badInMicrowave;
	}

	public void setBadInMicrowave(boolean boolean1) {
		this.badInMicrowave = boolean1;
	}

	private void destroyThisItem() {
		IsoWorldInventoryObject worldInventoryObject = this.getWorldItem();
		if (worldInventoryObject != null && worldInventoryObject.getSquare() != null) {
			if (GameServer.bServer) {
				GameServer.RemoveItemFromMap(worldInventoryObject);
			} else {
				worldInventoryObject.removeFromWorld();
				worldInventoryObject.removeFromSquare();
			}

			this.setWorldItem((IsoWorldInventoryObject)null);
		} else if (this.container != null) {
			IsoObject object = this.container.getParent();
			if (GameServer.bServer) {
				if (!this.isInPlayerInventory()) {
					GameServer.sendRemoveItemFromContainer(this.container, this);
				}

				this.container.Remove((InventoryItem)this);
			} else {
				this.container.Remove((InventoryItem)this);
			}

			IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
			LuaManager.updateOverlaySprite(object);
		}

		if (!GameServer.bServer) {
			LuaEventManager.triggerEvent("OnContainerUpdate");
		}
	}
}
