package zombie.inventory.types;

import java.util.List;
import zombie.GameTime;
import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.math.PZMath;
import zombie.interfaces.IUpdater;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.ItemContainer;
import zombie.inventory.ItemUser;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoWorld;
import zombie.iso.objects.RainManager;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;
import zombie.util.StringUtils;


public final class DrainableComboItem extends InventoryItem implements Drainable,IUpdater {
	protected boolean bUseWhileEquiped = true;
	protected boolean bUseWhileUnequiped = false;
	protected int ticksPerEquipUse = 30;
	protected float useDelta = 0.03125F;
	protected float delta = 1.0F;
	protected float ticks = 0.0F;
	protected String ReplaceOnDeplete = null;
	protected String ReplaceOnDepleteFullType = null;
	public List ReplaceOnCooked = null;
	private String OnCooked = null;
	private float rainFactor = 0.0F;
	private boolean canConsolidate = true;
	private float WeightEmpty = 0.0F;
	private static final float MIN_HEAT = 0.2F;
	private static final float MAX_HEAT = 3.0F;
	protected float Heat = 1.0F;
	protected int LastCookMinute = 0;

	public DrainableComboItem(String string, String string2, String string3, String string4) {
		super(string, string2, string3, string4);
	}

	public DrainableComboItem(String string, String string2, String string3, Item item) {
		super(string, string2, string3, item);
	}

	public boolean IsDrainable() {
		return true;
	}

	public int getSaveType() {
		return Item.Type.Drainable.ordinal();
	}

	public boolean CanStack(InventoryItem inventoryItem) {
		return false;
	}

	public float getUsedDelta() {
		return this.delta;
	}

	public int getDrainableUsesInt() {
		return (int)Math.floor(((double)this.getUsedDelta() + 1.0E-4) / (double)this.getUseDelta());
	}

	public float getDrainableUsesFloat() {
		return this.getUsedDelta() / this.getUseDelta();
	}

	public void render() {
	}

	public void renderlast() {
	}

	public void setUsedDelta(float float1) {
		this.delta = PZMath.clamp(float1, 0.0F, 1.0F);
		this.updateWeight();
	}

	public boolean shouldUpdateInWorld() {
		if (!GameServer.bServer && this.Heat != 1.0F) {
			return true;
		} else if (this.canStoreWater() && this.isWaterSource() && this.getUsedDelta() < 1.0F) {
			IsoGridSquare square = this.getWorldItem().getSquare();
			return square != null && square.isOutside();
		} else {
			return false;
		}
	}

	public void update() {
		ItemContainer itemContainer = this.getOutermostContainer();
		float float1;
		int int1;
		if (itemContainer != null) {
			float1 = itemContainer.getTemprature();
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

			float float2;
			if (this.IsCookable) {
				if (this.Heat > 1.6F) {
					int1 = GameTime.getInstance().getMinutes();
					if (int1 != this.LastCookMinute) {
						this.LastCookMinute = int1;
						float2 = this.Heat / 1.5F;
						if (itemContainer.getTemprature() <= 1.6F) {
							float2 *= 0.05F;
						}

						this.CookingTime += float2;
						if (this.isTaintedWater() && this.CookingTime > Math.min(this.MinutesToCook, 10.0F)) {
							this.setTaintedWater(false);
						}

						if (!this.isCooked() && this.CookingTime > this.MinutesToCook) {
							this.setCooked(true);
							if (this.getReplaceOnCooked() != null) {
								for (int int2 = 0; int2 < this.getReplaceOnCooked().size(); ++int2) {
									InventoryItem inventoryItem = this.container.AddItem((String)this.getReplaceOnCooked().get(int2));
									if (inventoryItem != null) {
										if (inventoryItem instanceof DrainableComboItem) {
											((DrainableComboItem)inventoryItem).setUsedDelta(this.getUsedDelta());
										}

										inventoryItem.copyConditionModData(this);
									}
								}

								this.container.Remove((InventoryItem)this);
								IsoWorld.instance.CurrentCell.addToProcessItemsRemove((InventoryItem)this);
								return;
							}

							if (this.getOnCooked() != null) {
								LuaManager.caller.protectedCall(LuaManager.thread, LuaManager.env.rawget(this.getOnCooked()), this);
								return;
							}
						}

						if (this.CookingTime > this.MinutesToBurn) {
							this.Burnt = true;
							this.setCooked(false);
						}
					}
				}
			} else if (this.isTaintedWater() && this.Heat > 1.6F) {
				int1 = GameTime.getInstance().getMinutes();
				if (int1 != this.LastCookMinute) {
					this.LastCookMinute = int1;
					float2 = 1.0F;
					if (itemContainer.getTemprature() <= 1.6F) {
						float2 = (float)((double)float2 * 0.2);
					}

					this.CookingTime += float2;
					if (this.CookingTime > 10.0F) {
						this.setTaintedWater(false);
					}
				}
			}
		}

		if (this.container == null && this.Heat != 1.0F) {
			float1 = 1.0F;
			if (this.Heat > float1) {
				this.Heat -= 0.001F * GameTime.instance.getMultiplier();
				if (this.Heat < float1) {
					this.Heat = float1;
				}
			}

			if (this.Heat < float1) {
				this.Heat += float1 / 1000.0F * GameTime.instance.getMultiplier();
				if (this.Heat > float1) {
					this.Heat = float1;
				}
			}
		}

		if (this.bUseWhileEquiped && this.delta > 0.0F) {
			IsoPlayer player = null;
			if (this.container != null && this.container.parent instanceof IsoPlayer) {
				for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
					if (this.container.parent == IsoPlayer.players[int1]) {
						player = IsoPlayer.players[int1];
					}
				}
			}

			if (player != null && (this.canBeActivated() && this.isActivated() || !this.canBeActivated()) && (player.isHandItem(this) || player.isAttachedItem(this))) {
				this.ticks += GameTime.instance.getMultiplier();
				while (this.ticks >= (float)this.ticksPerEquipUse) {
					this.ticks -= (float)this.ticksPerEquipUse;
					if (this.delta > 0.0F) {
						this.Use();
					}
				}
			}
		}

		if (this.bUseWhileUnequiped && this.delta > 0.0F && (this.canBeActivated() && this.isActivated() || !this.canBeActivated())) {
			this.ticks += GameTime.instance.getMultiplier();
			while (this.ticks >= (float)this.ticksPerEquipUse) {
				this.ticks -= (float)this.ticksPerEquipUse;
				if (this.delta > 0.0F) {
					this.Use();
				}
			}
		}

		if (this.getWorldItem() != null && this.canStoreWater() && this.isWaterSource() && RainManager.isRaining() && this.getRainFactor() > 0.0F) {
			IsoGridSquare square = this.getWorldItem().getSquare();
			if (square != null && square.isOutside()) {
				this.setUsedDelta(this.getUsedDelta() + 0.001F * RainManager.getRainIntensity() * GameTime.instance.getMultiplier() * this.getRainFactor());
				if (this.getUsedDelta() > 1.0F) {
					this.setUsedDelta(1.0F);
				}

				this.setTaintedWater(true);
				this.updateWeight();
			}
		}
	}

	public void Use() {
		if (this.getWorldItem() != null) {
			ItemUser.UseItem(this);
		} else {
			this.delta -= this.useDelta;
			InventoryItem inventoryItem;
			if (this.uses > 1) {
				int int1 = this.uses - 1;
				this.uses = 1;
				inventoryItem = InventoryItemFactory.CreateItem(this.getFullType());
				inventoryItem.setUses(int1);
				this.container.AddItem(inventoryItem);
			}

			if (this.delta <= 1.0E-4F) {
				this.delta = 0.0F;
				if (this.getReplaceOnDeplete() != null) {
					String string = this.getReplaceOnDepleteFullType();
					if (this.container != null) {
						inventoryItem = this.container.AddItem(string);
						if (this.container.parent instanceof IsoGameCharacter) {
							IsoGameCharacter gameCharacter = (IsoGameCharacter)this.container.parent;
							if (gameCharacter.getPrimaryHandItem() == this) {
								gameCharacter.setPrimaryHandItem(inventoryItem);
							}

							if (gameCharacter.getSecondaryHandItem() == this) {
								gameCharacter.setSecondaryHandItem(inventoryItem);
							}
						}

						inventoryItem.setCondition(this.getCondition());
						inventoryItem.setFavorite(this.isFavorite());
						this.container.Remove((InventoryItem)this);
					}
				} else {
					super.Use();
				}
			}

			this.updateWeight();
		}
	}

	public void updateWeight() {
		if (this.getReplaceOnDeplete() != null) {
			if (this.getUsedDelta() >= 1.0F) {
				this.setCustomWeight(true);
				this.setActualWeight(this.getScriptItem().getActualWeight());
				this.setWeight(this.getActualWeight());
				return;
			}

			Item item = ScriptManager.instance.getItem(this.ReplaceOnDepleteFullType);
			if (item != null) {
				this.setCustomWeight(true);
				this.setActualWeight((this.getScriptItem().getActualWeight() - item.getActualWeight()) * this.getUsedDelta() + item.getActualWeight());
				this.setWeight(this.getActualWeight());
			}
		}

		if (this.getWeightEmpty() != 0.0F) {
			this.setCustomWeight(true);
			this.setActualWeight((this.getScriptItem().getActualWeight() - this.WeightEmpty) * this.getUsedDelta() + this.WeightEmpty);
		}
	}

	public float getWeightEmpty() {
		return this.WeightEmpty;
	}

	public void setWeightEmpty(float float1) {
		this.WeightEmpty = float1;
	}

	public boolean isUseWhileEquiped() {
		return this.bUseWhileEquiped;
	}

	public void setUseWhileEquiped(boolean boolean1) {
		this.bUseWhileEquiped = boolean1;
	}

	public boolean isUseWhileUnequiped() {
		return this.bUseWhileUnequiped;
	}

	public void setUseWhileUnequiped(boolean boolean1) {
		this.bUseWhileUnequiped = boolean1;
	}

	public int getTicksPerEquipUse() {
		return this.ticksPerEquipUse;
	}

	public void setTicksPerEquipUse(int int1) {
		this.ticksPerEquipUse = int1;
	}

	public float getUseDelta() {
		return this.useDelta;
	}

	public void setUseDelta(float float1) {
		this.useDelta = float1;
	}

	public float getDelta() {
		return this.delta;
	}

	public void setDelta(float float1) {
		this.delta = float1;
	}

	public float getTicks() {
		return this.ticks;
	}

	public void setTicks(float float1) {
		this.ticks = float1;
	}

	public void setReplaceOnDeplete(String string) {
		this.ReplaceOnDeplete = string;
		this.ReplaceOnDepleteFullType = this.getReplaceOnDepleteFullType();
	}

	public String getReplaceOnDeplete() {
		return this.ReplaceOnDeplete;
	}

	public String getReplaceOnDepleteFullType() {
		return StringUtils.moduleDotType(this.getModule(), this.ReplaceOnDeplete);
	}

	public void setHeat(float float1) {
		this.Heat = PZMath.clamp(float1, 0.0F, 3.0F);
	}

	public float getHeat() {
		return this.Heat;
	}

	public float getInvHeat() {
		return (1.0F - this.Heat) / 3.0F;
	}

	public boolean finishupdate() {
		if (this.canStoreWater() && this.isWaterSource() && this.getWorldItem() != null && this.getWorldItem().getSquare() != null) {
			return this.getUsedDelta() >= 1.0F;
		} else if (this.isTaintedWater()) {
			return false;
		} else {
			if (this.container != null) {
				if (this.Heat != this.container.getTemprature() || this.container.isTemperatureChanging()) {
					return false;
				}

				if (this.container.type.equals("campfire") || this.container.type.equals("barbecue")) {
					return false;
				}
			}

			return true;
		}
	}

	public int getRemainingUses() {
		return Math.round(this.getUsedDelta() / this.getUseDelta());
	}

	public float getRainFactor() {
		return this.rainFactor;
	}

	public void setRainFactor(float float1) {
		this.rainFactor = float1;
	}

	public boolean canConsolidate() {
		return this.canConsolidate;
	}

	public void setCanConsolidate(boolean boolean1) {
		this.canConsolidate = boolean1;
	}

	public List getReplaceOnCooked() {
		return this.ReplaceOnCooked;
	}

	public void setReplaceOnCooked(List list) {
		this.ReplaceOnCooked = list;
	}

	public String getOnCooked() {
		return this.OnCooked;
	}

	public void setOnCooked(String string) {
		this.OnCooked = string;
	}
}
