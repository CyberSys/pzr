package zombie.characters;

import java.util.ArrayList;
import zombie.GameTime;
import zombie.ZomboidGlobals;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characterTextures.BloodClothingType;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.BodyDamage.Thermoregulator;
import zombie.core.math.PZMath;
import zombie.core.skinnedmodel.visual.ItemVisual;
import zombie.core.skinnedmodel.visual.ItemVisuals;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;


public final class ClothingWetness {
	private static final ItemVisuals itemVisuals = new ItemVisuals();
	private static final ArrayList coveredParts = new ArrayList();
	public final IsoGameCharacter character;
	public final ClothingWetness.ItemList[] clothing;
	public final int[] perspiringParts;
	public boolean changed;

	public ClothingWetness(IsoGameCharacter gameCharacter) {
		this.clothing = new ClothingWetness.ItemList[BloodBodyPartType.MAX.index()];
		this.perspiringParts = new int[BloodBodyPartType.MAX.index()];
		this.changed = true;
		this.character = gameCharacter;
		for (int int1 = 0; int1 < this.clothing.length; ++int1) {
			this.clothing[int1] = new ClothingWetness.ItemList();
		}
	}

	public void calculateExposedItems() {
		int int1;
		for (int1 = 0; int1 < this.clothing.length; ++int1) {
			this.clothing[int1].clear();
		}

		this.character.getItemVisuals(itemVisuals);
		for (int1 = itemVisuals.size() - 1; int1 >= 0; --int1) {
			ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int1);
			InventoryItem inventoryItem = itemVisual.getInventoryItem();
			ArrayList arrayList = inventoryItem.getBloodClothingType();
			if (arrayList != null) {
				coveredParts.clear();
				BloodClothingType.getCoveredParts(arrayList, coveredParts);
				for (int int2 = 0; int2 < coveredParts.size(); ++int2) {
					BloodBodyPartType bloodBodyPartType = (BloodBodyPartType)coveredParts.get(int2);
					this.clothing[bloodBodyPartType.index()].add(inventoryItem);
				}
			}
		}
	}

	public void updateWetness(float float1, float float2) {
		boolean boolean1 = false;
		InventoryItem inventoryItem = this.character.getPrimaryHandItem();
		if (inventoryItem != null && inventoryItem.isProtectFromRainWhileEquipped()) {
			boolean1 = true;
		}

		inventoryItem = this.character.getSecondaryHandItem();
		if (inventoryItem != null && inventoryItem.isProtectFromRainWhileEquipped()) {
			boolean1 = true;
		}

		if (this.changed) {
			this.changed = false;
			this.calculateExposedItems();
		}

		this.character.getItemVisuals(itemVisuals);
		for (int int1 = 0; int1 < itemVisuals.size(); ++int1) {
			InventoryItem inventoryItem2 = ((ItemVisual)itemVisuals.get(int1)).getInventoryItem();
			if (inventoryItem2 instanceof Clothing) {
				if (inventoryItem2.getBloodClothingType() == null) {
					((Clothing)inventoryItem2).updateWetness(true);
				} else {
					((Clothing)inventoryItem2).flushWetness();
				}
			}
		}

		float float3 = (float)ZomboidGlobals.WetnessIncrease * GameTime.instance.getMultiplier();
		float float4 = (float)ZomboidGlobals.WetnessDecrease * GameTime.instance.getMultiplier();
		int int2;
		BloodBodyPartType bloodBodyPartType;
		BodyPartType bodyPartType;
		BodyPart bodyPart;
		Thermoregulator.ThermalNode thermalNode;
		float float5;
		float float6;
		boolean boolean2;
		boolean boolean3;
		float float7;
		boolean boolean4;
		label282: for (int2 = 0; int2 < this.clothing.length; ++int2) {
			bloodBodyPartType = BloodBodyPartType.FromIndex(int2);
			bodyPartType = BodyPartType.FromIndex(int2);
			if (bodyPartType != BodyPartType.MAX) {
				bodyPart = this.character.getBodyDamage().getBodyPart(bodyPartType);
				thermalNode = this.character.getBodyDamage().getThermoregulator().getNodeForBloodType(bloodBodyPartType);
				if (bodyPart != null && thermalNode != null) {
					float5 = 0.0F;
					float6 = PZMath.clamp(thermalNode.getSecondaryDelta(), 0.0F, 1.0F);
					float6 *= float6;
					float6 *= 0.2F + 0.8F * (1.0F - thermalNode.getDistToCore());
					float float8;
					if (float6 > 0.1F) {
						float5 += float6;
					} else {
						float8 = (thermalNode.getSkinCelcius() - 20.0F) / 22.0F;
						float8 *= float8;
						float8 -= float1;
						float8 = Math.max(0.0F, float8);
						float5 -= float8;
						if (float1 > 0.0F) {
							float5 = 0.0F;
						}
					}

					this.perspiringParts[int2] = float5 > 0.0F ? 1 : 0;
					if (float5 != 0.0F) {
						if (float5 > 0.0F) {
							float5 *= float3;
						} else {
							float5 *= float4;
						}

						bodyPart.setWetness(bodyPart.getWetness() + float5);
						if ((!(float5 > 0.0F) || !(bodyPart.getWetness() < 25.0F)) && (!(float5 < 0.0F) || !(bodyPart.getWetness() > 50.0F))) {
							if (float5 > 0.0F) {
								float8 = this.character.getBodyDamage().getThermoregulator().getExternalAirTemperature();
								float8 += 10.0F;
								float8 = PZMath.clamp(float8, 0.0F, 20.0F) / 20.0F;
								float5 *= 0.4F + 0.6F * float8;
							}

							boolean4 = false;
							boolean2 = false;
							boolean3 = false;
							float7 = 1.0F;
							int int3 = this.clothing[int2].size() - 1;
							InventoryItem inventoryItem3;
							Clothing clothing;
							while (true) {
								if (int3 < 0) {
									continue label282;
								}

								int int4;
								if (float5 > 0.0F) {
									int4 = this.perspiringParts[int2]++;
								}

								inventoryItem3 = (InventoryItem)this.clothing[int2].get(int3);
								if (inventoryItem3 instanceof Clothing) {
									float7 = 1.0F;
									clothing = (Clothing)inventoryItem3;
									ItemVisual itemVisual = clothing.getVisual();
									if (itemVisual == null) {
										break;
									}

									if (itemVisual.getHole(bloodBodyPartType) > 0.0F) {
										boolean4 = true;
									} else if (float5 > 0.0F && clothing.getWetness() >= 100.0F) {
										boolean2 = true;
									} else {
										if (!(float5 < 0.0F) || !(clothing.getWetness() <= 0.0F)) {
											if (float5 > 0.0F && clothing.getWaterResistance() > 0.0F) {
												float7 = PZMath.max(0.0F, 1.0F - clothing.getWaterResistance());
												if (float7 <= 0.0F) {
													int4 = this.perspiringParts[int2]--;
													continue label282;
												}
											}

											break;
										}

										boolean3 = true;
									}
								}

								--int3;
							}

							coveredParts.clear();
							BloodClothingType.getCoveredParts(inventoryItem3.getBloodClothingType(), coveredParts);
							int int5 = coveredParts.size();
							float float9 = float5;
							if (float5 > 0.0F) {
								float9 = float5 * float7;
							}

							if (boolean4 || boolean2 || boolean3) {
								float9 /= 2.0F;
							}

							clothing.setWetness(clothing.getWetness() + float9);
						}
					}
				}
			}
		}
		for (int2 = 0; int2 < this.clothing.length; ++int2) {
			bloodBodyPartType = BloodBodyPartType.FromIndex(int2);
			bodyPartType = BodyPartType.FromIndex(int2);
			if (bodyPartType != BodyPartType.MAX) {
				bodyPart = this.character.getBodyDamage().getBodyPart(bodyPartType);
				thermalNode = this.character.getBodyDamage().getThermoregulator().getNodeForBloodType(bloodBodyPartType);
				if (bodyPart != null && thermalNode != null) {
					float5 = 100.0F;
					if (boolean1) {
						float5 = 100.0F * BodyPartType.GetUmbrellaMod(bodyPartType);
					}

					float6 = 0.0F;
					if (float1 > 0.0F) {
						float6 = float1 * float3;
					} else {
						float6 -= float2 * float4;
					}

					boolean4 = false;
					boolean2 = false;
					boolean3 = false;
					float7 = 1.0F;
					float float10 = 2.0F;
					for (int int6 = 0; int6 < this.clothing[int2].size(); ++int6) {
						int int7 = 1 + (this.clothing[int2].size() - int6);
						float7 = 1.0F;
						InventoryItem inventoryItem4 = (InventoryItem)this.clothing[int2].get(int6);
						if (inventoryItem4 instanceof Clothing) {
							Clothing clothing2 = (Clothing)inventoryItem4;
							ItemVisual itemVisual2 = clothing2.getVisual();
							if (itemVisual2 != null) {
								if (itemVisual2.getHole(bloodBodyPartType) > 0.0F) {
									boolean4 = true;
									continue;
								}

								if (float6 > 0.0F && clothing2.getWetness() >= 100.0F) {
									boolean2 = true;
									continue;
								}

								if (float6 < 0.0F && clothing2.getWetness() <= 0.0F) {
									boolean3 = true;
									continue;
								}

								if (float6 > 0.0F && clothing2.getWaterResistance() > 0.0F) {
									float7 = PZMath.max(0.0F, 1.0F - clothing2.getWaterResistance());
									if (float7 <= 0.0F) {
										break;
									}
								}
							}

							coveredParts.clear();
							BloodClothingType.getCoveredParts(inventoryItem4.getBloodClothingType(), coveredParts);
							int int8 = coveredParts.size();
							float float11 = float6;
							if (float6 > 0.0F) {
								float11 = float6 * float7;
							}

							float11 /= (float)int8;
							if (boolean4 || boolean2 || boolean3) {
								float11 /= float10;
							}

							if (float6 < 0.0F && int7 > this.perspiringParts[int2] || float6 > 0.0F && clothing2.getWetness() <= float5) {
								clothing2.setWetness(clothing2.getWetness() + float11);
							}

							if (float6 > 0.0F) {
								break;
							}

							if (boolean3) {
								float10 *= 2.0F;
							}
						}
					}

					if (!this.clothing[int2].isEmpty()) {
						InventoryItem inventoryItem5 = (InventoryItem)this.clothing[int2].get(this.clothing[int2].size() - 1);
						if (inventoryItem5 instanceof Clothing) {
							Clothing clothing3 = (Clothing)inventoryItem5;
							if (float6 > 0.0F && this.perspiringParts[int2] == 0 && clothing3.getWetness() >= 50.0F && bodyPart.getWetness() <= float5) {
								bodyPart.setWetness(bodyPart.getWetness() + float6 / 2.0F);
							}

							if (float6 < 0.0F && this.perspiringParts[int2] == 0 && clothing3.getWetness() <= 50.0F) {
								bodyPart.setWetness(bodyPart.getWetness() + float6 / 2.0F);
							}
						}
					} else if (float6 < 0.0F && this.perspiringParts[int2] == 0 || bodyPart.getWetness() <= float5) {
						bodyPart.setWetness(bodyPart.getWetness() + float6);
					}
				}
			}
		}
	}

	@Deprecated
	public void increaseWetness(float float1) {
		if (!(float1 <= 0.0F)) {
			if (this.changed) {
				this.changed = false;
				this.calculateExposedItems();
			}

			this.character.getItemVisuals(itemVisuals);
			int int1;
			for (int1 = 0; int1 < itemVisuals.size(); ++int1) {
				InventoryItem inventoryItem = ((ItemVisual)itemVisuals.get(int1)).getInventoryItem();
				if (inventoryItem instanceof Clothing) {
					((Clothing)inventoryItem).flushWetness();
				}
			}

			int1 = 0;
			for (int int2 = 0; int2 < this.clothing.length; ++int2) {
				BloodBodyPartType bloodBodyPartType = BloodBodyPartType.FromIndex(int2);
				boolean boolean1 = false;
				boolean boolean2 = false;
				int int3 = 0;
				label85: {
					InventoryItem inventoryItem2;
					Clothing clothing;
					while (true) {
						if (int3 >= this.clothing[int2].size()) {
							break label85;
						}

						inventoryItem2 = (InventoryItem)this.clothing[int2].get(int3);
						if (inventoryItem2 instanceof Clothing) {
							clothing = (Clothing)inventoryItem2;
							ItemVisual itemVisual = clothing.getVisual();
							if (itemVisual == null) {
								break;
							}

							if (itemVisual.getHole(bloodBodyPartType) > 0.0F) {
								boolean1 = true;
							} else {
								if (!(clothing.getWetness() >= 100.0F)) {
									break;
								}

								boolean2 = true;
							}
						}

						++int3;
					}

					coveredParts.clear();
					BloodClothingType.getCoveredParts(inventoryItem2.getBloodClothingType(), coveredParts);
					int int4 = coveredParts.size();
					float float2 = float1 / (float)int4;
					if (boolean1 || boolean2) {
						float2 /= 2.0F;
					}

					clothing.setWetness(clothing.getWetness() + float2);
				}

				if (this.clothing[int2].isEmpty()) {
					++int1;
				} else {
					InventoryItem inventoryItem3 = (InventoryItem)this.clothing[int2].get(this.clothing[int2].size() - 1);
					if (inventoryItem3 instanceof Clothing) {
						Clothing clothing2 = (Clothing)inventoryItem3;
						if (clothing2.getWetness() >= 100.0F) {
							++int1;
						}
					}
				}
			}

			if (int1 > 0) {
				float float3 = this.character.getBodyDamage().getWetness();
				float float4 = float1 * ((float)int1 / (float)this.clothing.length);
				this.character.getBodyDamage().setWetness(float3 + float4);
			}
		}
	}

	@Deprecated
	public void decreaseWetness(float float1) {
		if (!(float1 <= 0.0F)) {
			if (this.changed) {
				this.changed = false;
				this.calculateExposedItems();
			}

			this.character.getItemVisuals(itemVisuals);
			for (int int1 = itemVisuals.size() - 1; int1 >= 0; --int1) {
				ItemVisual itemVisual = (ItemVisual)itemVisuals.get(int1);
				InventoryItem inventoryItem = itemVisual.getInventoryItem();
				if (inventoryItem instanceof Clothing) {
					Clothing clothing = (Clothing)inventoryItem;
					if (clothing.getWetness() > 0.0F) {
						clothing.setWetness(clothing.getWetness() - float1);
					}
				}
			}
		}
	}

	private static final class ItemList extends ArrayList {
	}
}
