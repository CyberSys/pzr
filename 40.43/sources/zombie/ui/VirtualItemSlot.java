package zombie.ui;

import zombie.Lua.LuaManager;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.scripting.objects.Item;


public class VirtualItemSlot extends UIElement {
	public float alpha = 1.0F;
	public int index = 0;
	public IsoGameCharacter chr;
	InventoryItem item;
	Texture tex;
	String type;

	public VirtualItemSlot(String string, int int1, int int2, String string2, IsoGameCharacter gameCharacter) {
		this.chr = gameCharacter;
		this.type = string;
		this.tex = Texture.getSharedTexture(string2);
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = (float)this.tex.getWidth();
		this.height = (float)this.tex.getHeight();
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
			super.onMouseDown(double1, double2);
			if (this.item != null && UIManager.getDragInventory() == null && this.type.equals("CraftingResult")) {
				UIManager.setDragInventory(IsoPlayer.getInstance().getInventory().AddItem(this.item));
				if (UIManager.getDragInventory() instanceof Drainable) {
					((Drainable)UIManager.getDragInventory()).setUsedDelta(((Drainable)this.item).getUsedDelta());
				}

				NewCraftingPanel.instance.PerformMakeItem();
				return Boolean.TRUE;
			} else {
				if (UIManager.getDragInventory() != null) {
					InventoryItem inventoryItem = UIManager.getDragInventory();
					if (this.type.equals("CraftingResult")) {
						if (this.item != null && UIManager.getDragInventory().getType().equals(this.item.getType()) && this.item.CanStack(inventoryItem)) {
							String string = this.item.getType();
							if (!string.contains(".")) {
								string = inventoryItem.getModule() + "." + this.item.getType();
							}

							InventoryItem inventoryItem2 = InventoryItemFactory.CreateItem(string);
							UIManager.getDragInventory().setUses(UIManager.getDragInventory().getUses() + inventoryItem2.getUses());
							NewCraftingPanel.instance.PerformMakeItem();
						}

						return Boolean.TRUE;
					}

					if (this.type.equals("CraftingIngredient")) {
						int int1 = this.ExistsInOtherSlot(inventoryItem, this.index);
						if (int1 != -1) {
							this.chr.setCraftingByIndex(int1, (InventoryItem)null);
						}

						if (inventoryItem.getContainer() == this.chr.getInventory()) {
							this.chr.setCraftingByIndex(this.index, inventoryItem);
						} else {
							if (inventoryItem.getContainer() != null) {
								inventoryItem.getContainer().Items.remove(inventoryItem);
								inventoryItem.getContainer().dirty = true;
							}

							inventoryItem = this.chr.getInventory().AddItem(inventoryItem);
							inventoryItem.setContainer(this.chr.getInventory());
							this.chr.setCraftingByIndex(this.index, inventoryItem);
						}

						this.item = this.chr.getCraftingByIndex(this.index);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (this.type.equals("Main")) {
						if (this.chr.getCraftIngredient1() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient1((InventoryItem)null);
						}

						if (this.chr.getCraftIngredient2() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient2((InventoryItem)null);
						}

						if (this.chr.getCraftIngredient3() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient3((InventoryItem)null);
						}

						if (this.chr.getCraftIngredient4() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient4((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Head() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Head((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Torso() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Torso((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Hands() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Hands((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Legs() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Legs((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Feet() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Feet((InventoryItem)null);
						}

						if (this.chr.getSecondaryHandItem() == inventoryItem) {
							this.item = this.chr.getPrimaryHandItem();
							this.chr.setPrimaryHandItem(inventoryItem);
							this.chr.setSecondaryHandItem(this.item);
						} else if (inventoryItem.getContainer() == this.chr.getInventory()) {
							this.chr.setPrimaryHandItem(inventoryItem);
						} else {
							if (inventoryItem.getContainer() != null) {
								inventoryItem.getContainer().Items.remove(inventoryItem);
								inventoryItem.getContainer().dirty = true;
							}

							inventoryItem.setContainer(this.chr.getInventory());
							this.chr.getInventory().AddItem(inventoryItem);
							this.chr.setPrimaryHandItem(inventoryItem);
						}

						this.item = this.chr.getPrimaryHandItem();
						UIManager.setDragInventory((InventoryItem)null);
					} else if (this.type.equals("Secondary")) {
						if (this.chr.getCraftIngredient1() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient1((InventoryItem)null);
						}

						if (this.chr.getCraftIngredient2() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient2((InventoryItem)null);
						}

						if (this.chr.getCraftIngredient3() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient3((InventoryItem)null);
						}

						if (this.chr.getCraftIngredient4() == UIManager.getDragInventory()) {
							this.chr.setCraftIngredient4((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Head() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Head((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Torso() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Torso((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Hands() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Hands((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Legs() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Legs((InventoryItem)null);
						}

						if (this.chr.getClothingItem_Feet() == UIManager.getDragInventory()) {
							this.chr.setClothingItem_Feet((InventoryItem)null);
						}

						if (this.chr.getPrimaryHandItem() == inventoryItem) {
							this.item = this.chr.getSecondaryHandItem();
							this.chr.setSecondaryHandItem(inventoryItem);
							this.chr.setPrimaryHandItem(this.item);
						} else if (inventoryItem.getContainer() == this.chr.getInventory()) {
							this.chr.setSecondaryHandItem(inventoryItem);
						} else {
							if (inventoryItem.getContainer() != null) {
								inventoryItem.getContainer().Items.remove(inventoryItem);
								inventoryItem.getContainer().dirty = true;
							}

							inventoryItem.setContainer(this.chr.getInventory());
							this.chr.getInventory().AddItem(inventoryItem);
							this.chr.setSecondaryHandItem(inventoryItem);
						}

						this.item = this.chr.getSecondaryHandItem();
						UIManager.setDragInventory((InventoryItem)null);
					} else if (!this.type.equals("Head_Clothing")) {
						if (this.type.equals("Torso_Clothing")) {
							if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Top) {
								if (this.chr.getCraftIngredient1() == UIManager.getDragInventory()) {
									this.chr.setCraftIngredient1((InventoryItem)null);
								}

								if (this.chr.getCraftIngredient2() == UIManager.getDragInventory()) {
									this.chr.setCraftIngredient2((InventoryItem)null);
								}

								if (this.chr.getCraftIngredient3() == UIManager.getDragInventory()) {
									this.chr.setCraftIngredient3((InventoryItem)null);
								}

								if (this.chr.getCraftIngredient4() == UIManager.getDragInventory()) {
									this.chr.setCraftIngredient4((InventoryItem)null);
								}

								if (this.chr.getClothingItem_Head() == UIManager.getDragInventory()) {
									this.chr.setClothingItem_Head((InventoryItem)null);
								}

								if (this.chr.getClothingItem_Hands() == UIManager.getDragInventory()) {
									this.chr.setClothingItem_Hands((InventoryItem)null);
								}

								if (this.chr.getClothingItem_Legs() == UIManager.getDragInventory()) {
									this.chr.setClothingItem_Legs((InventoryItem)null);
								}

								if (this.chr.getClothingItem_Feet() == UIManager.getDragInventory()) {
									this.chr.setClothingItem_Feet((InventoryItem)null);
								}

								if (inventoryItem.getContainer() == this.chr.getInventory()) {
									this.chr.setClothingItem_Torso(inventoryItem);
									if (this.chr.getPrimaryHandItem() == inventoryItem) {
										this.chr.setPrimaryHandItem((InventoryItem)null);
									}

									if (this.chr.getSecondaryHandItem() == inventoryItem) {
										this.chr.setSecondaryHandItem((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Head() == inventoryItem) {
										this.chr.setClothingItem_Head((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Hands() == inventoryItem) {
										this.chr.setClothingItem_Hands((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Legs() == inventoryItem) {
										this.chr.setClothingItem_Legs((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Feet() == inventoryItem) {
										this.chr.setClothingItem_Feet((InventoryItem)null);
									}

									if (Sidebar.instance.MainHand.item == inventoryItem) {
										Sidebar.instance.MainHand.item = null;
									}

									if (Sidebar.instance.SecondHand.item == inventoryItem) {
										Sidebar.instance.SecondHand.item = null;
									}

									if (ClothingPanel.instance.HeadItem.item == inventoryItem) {
										ClothingPanel.instance.HeadItem.item = null;
									}

									if (ClothingPanel.instance.HandsItem.item == inventoryItem) {
										ClothingPanel.instance.HandsItem.item = null;
									}

									if (ClothingPanel.instance.LegsItem.item == inventoryItem) {
										ClothingPanel.instance.LegsItem.item = null;
									}

									if (ClothingPanel.instance.FeetItem.item == inventoryItem) {
										ClothingPanel.instance.FeetItem.item = null;
									}
								} else {
									if (inventoryItem.getContainer() != null) {
										inventoryItem.getContainer().Items.remove(inventoryItem);
										inventoryItem.getContainer().dirty = true;
									}

									inventoryItem.setContainer(this.chr.getInventory());
									this.chr.getInventory().AddItem(inventoryItem);
									this.chr.setClothingItem_Torso(inventoryItem);
								}

								this.item = this.chr.getClothingItem_Torso();
								IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Top, ((Clothing)this.item).getSpriteName(), ((Clothing)this.item).getPalette());
								UIManager.setDragInventory((InventoryItem)null);
							}
						} else if (!this.type.equals("Hands_Clothing")) {
							if (this.type.equals("Legs_Clothing")) {
								if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Bottoms) {
									if (this.chr.getCraftIngredient1() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient1((InventoryItem)null);
									}

									if (this.chr.getCraftIngredient2() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient2((InventoryItem)null);
									}

									if (this.chr.getCraftIngredient3() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient3((InventoryItem)null);
									}

									if (this.chr.getCraftIngredient4() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient4((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Head() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Head((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Torso() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Torso((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Hands() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Hands((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Feet() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Feet((InventoryItem)null);
									}

									if (inventoryItem.getContainer() == this.chr.getInventory()) {
										this.chr.setClothingItem_Legs(inventoryItem);
										if (this.chr.getPrimaryHandItem() == inventoryItem) {
											this.chr.setPrimaryHandItem((InventoryItem)null);
										}

										if (this.chr.getSecondaryHandItem() == inventoryItem) {
											this.chr.setSecondaryHandItem((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Head() == inventoryItem) {
											this.chr.setClothingItem_Head((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Torso() == inventoryItem) {
											this.chr.setClothingItem_Torso((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Hands() == inventoryItem) {
											this.chr.setClothingItem_Hands((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Feet() == inventoryItem) {
											this.chr.setClothingItem_Feet((InventoryItem)null);
										}

										if (Sidebar.instance.MainHand.item == inventoryItem) {
											Sidebar.instance.MainHand.item = null;
										}

										if (Sidebar.instance.SecondHand.item == inventoryItem) {
											Sidebar.instance.SecondHand.item = null;
										}

										if (ClothingPanel.instance.HeadItem.item == inventoryItem) {
											ClothingPanel.instance.HeadItem.item = null;
										}

										if (ClothingPanel.instance.TorsoItem.item == inventoryItem) {
											ClothingPanel.instance.TorsoItem.item = null;
										}

										if (ClothingPanel.instance.HandsItem.item == inventoryItem) {
											ClothingPanel.instance.HandsItem.item = null;
										}

										if (ClothingPanel.instance.FeetItem.item == inventoryItem) {
											ClothingPanel.instance.FeetItem.item = null;
										}
									} else {
										if (inventoryItem.getContainer() != null) {
											inventoryItem.getContainer().Items.remove(inventoryItem);
											inventoryItem.getContainer().dirty = true;
										}

										inventoryItem.setContainer(this.chr.getInventory());
										this.chr.getInventory().AddItem(inventoryItem);
										this.chr.setClothingItem_Legs(inventoryItem);
									}

									this.item = this.chr.getClothingItem_Legs();
									IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Bottoms, ((Clothing)this.item).getSpriteName(), ((Clothing)this.item).getPalette());
									UIManager.setDragInventory((InventoryItem)null);
								}
							} else if (this.type.equals("Feet_Clothing")) {
								if (inventoryItem instanceof Clothing && ((Clothing)inventoryItem).getBodyLocation() == Item.ClothingBodyLocation.Shoes) {
									if (this.chr.getCraftIngredient1() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient1((InventoryItem)null);
									}

									if (this.chr.getCraftIngredient2() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient2((InventoryItem)null);
									}

									if (this.chr.getCraftIngredient3() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient3((InventoryItem)null);
									}

									if (this.chr.getCraftIngredient4() == UIManager.getDragInventory()) {
										this.chr.setCraftIngredient4((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Head() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Head((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Torso() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Torso((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Hands() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Hands((InventoryItem)null);
									}

									if (this.chr.getClothingItem_Legs() == UIManager.getDragInventory()) {
										this.chr.setClothingItem_Legs((InventoryItem)null);
									}

									if (inventoryItem.getContainer() == this.chr.getInventory()) {
										this.chr.setClothingItem_Feet(inventoryItem);
										if (this.chr.getPrimaryHandItem() == inventoryItem) {
											this.chr.setPrimaryHandItem((InventoryItem)null);
										}

										if (this.chr.getSecondaryHandItem() == inventoryItem) {
											this.chr.setSecondaryHandItem((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Head() == inventoryItem) {
											this.chr.setClothingItem_Head((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Torso() == inventoryItem) {
											this.chr.setClothingItem_Torso((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Hands() == inventoryItem) {
											this.chr.setClothingItem_Hands((InventoryItem)null);
										}

										if (this.chr.getClothingItem_Legs() == inventoryItem) {
											this.chr.setClothingItem_Legs((InventoryItem)null);
										}

										if (Sidebar.instance.MainHand.item == inventoryItem) {
											Sidebar.instance.MainHand.item = null;
										}

										if (Sidebar.instance.SecondHand.item == inventoryItem) {
											Sidebar.instance.SecondHand.item = null;
										}

										if (ClothingPanel.instance.HeadItem.item == inventoryItem) {
											ClothingPanel.instance.HeadItem.item = null;
										}

										if (ClothingPanel.instance.TorsoItem.item == inventoryItem) {
											ClothingPanel.instance.TorsoItem.item = null;
										}

										if (ClothingPanel.instance.HandsItem.item == inventoryItem) {
											ClothingPanel.instance.HandsItem.item = null;
										}

										if (ClothingPanel.instance.LegsItem.item == inventoryItem) {
											ClothingPanel.instance.LegsItem.item = null;
										}
									} else {
										if (inventoryItem.getContainer() != null) {
											inventoryItem.getContainer().Items.remove(inventoryItem);
											inventoryItem.getContainer().dirty = true;
										}

										inventoryItem.setContainer(this.chr.getInventory());
										this.chr.getInventory().AddItem(inventoryItem);
										this.chr.setClothingItem_Feet(inventoryItem);
									}

									this.item = this.chr.getClothingItem_Feet();
									IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Shoes, ((Clothing)this.item).getSpriteName(), ((Clothing)this.item).getPalette());
									UIManager.setDragInventory((InventoryItem)null);
								}
							} else if (UIManager.getDragInventory() != null) {
								this.item = UIManager.getDragInventory();
								if (this.getTable() != null && this.getTable().rawget("onPlaceItem") != null) {
									Object[] objectArray = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onPlaceItem"), this.table, this.item);
								}

								UIManager.setDragInventory((InventoryItem)null);
							}
						}
					}
				} else {
					if (this.type.equals("Main")) {
						this.item = this.chr.getPrimaryHandItem();
					} else if (this.type.equals("Secondary")) {
						this.item = this.chr.getSecondaryHandItem();
					} else if (this.type.equals("Head_Clothing")) {
						this.item = this.chr.getClothingItem_Head();
					} else if (this.type.equals("Torso_Clothing")) {
						this.item = this.chr.getClothingItem_Torso();
					} else if (this.type.equals("Hands_Clothing")) {
						this.item = this.chr.getClothingItem_Hands();
					} else if (this.type.equals("Legs_Clothing")) {
						this.item = this.chr.getClothingItem_Legs();
					} else if (this.type.equals("Feet_Clothing")) {
						this.item = this.chr.getClothingItem_Feet();
					}

					UIManager.setDragInventory(this.item);
				}

				return Boolean.TRUE;
			}
		} else {
			return Boolean.TRUE;
		}
	}

	public void render() {
		if (this.type.equals("CraftingIngredient")) {
			this.item = this.chr.getCraftingByIndex(this.index);
		} else if (this.type.equals("Main")) {
			if (this.chr.getPrimaryHandItem() != null && this.chr.getInventory() != this.chr.getPrimaryHandItem().getContainer()) {
				this.chr.setPrimaryHandItem((InventoryItem)null);
			}

			this.item = this.chr.getPrimaryHandItem();
		} else if (this.type.equals("Secondary")) {
			if (this.chr.getSecondaryHandItem() != null && this.chr.getInventory() != this.chr.getSecondaryHandItem().getContainer()) {
				this.chr.setSecondaryHandItem((InventoryItem)null);
			}

			this.item = this.chr.getSecondaryHandItem();
		} else if (this.type.equals("Head_Clothing")) {
			if (this.chr.getClothingItem_Head() != null && this.chr.getInventory() != this.chr.getClothingItem_Head().getContainer()) {
				this.chr.setClothingItem_Head((InventoryItem)null);
			}

			this.item = this.chr.getClothingItem_Head();
		} else if (this.type.equals("Torso_Clothing")) {
			if (this.chr.getClothingItem_Torso() != null && this.chr.getInventory() != this.chr.getClothingItem_Torso().getContainer()) {
				this.chr.setClothingItem_Torso((InventoryItem)null);
			}

			this.item = this.chr.getClothingItem_Torso();
		} else if (this.type.equals("Hands_Clothing")) {
			if (this.chr.getClothingItem_Hands() != null && this.chr.getInventory() != this.chr.getClothingItem_Hands().getContainer()) {
				this.chr.setClothingItem_Hands((InventoryItem)null);
			}

			this.item = this.chr.getClothingItem_Hands();
		} else if (this.type.equals("Legs_Clothing")) {
			if (this.chr.getClothingItem_Legs() != null && this.chr.getInventory() != this.chr.getClothingItem_Legs().getContainer()) {
				this.chr.setClothingItem_Legs((InventoryItem)null);
			}

			this.item = this.chr.getClothingItem_Legs();
		} else if (this.type.equals("Feet_Clothing")) {
			if (this.chr.getClothingItem_Feet() != null && this.chr.getInventory() != this.chr.getClothingItem_Feet().getContainer()) {
				this.chr.setClothingItem_Feet((InventoryItem)null);
			}

			this.item = this.chr.getClothingItem_Feet();
		}

		if (this.tex != null) {
			this.DrawTexture(this.tex, 0.0, 0.0, (double)this.alpha);
			if (this.item != null) {
				if (this.item instanceof Food) {
					float float1 = ((Food)this.item).getHeat();
					boolean boolean1 = false;
					if (float1 > 1.0F) {
						--float1;
					}

					new Color(70, 153, 211);
					new Color(222, 70, 70);
					new Color(162, 162, 162);
				}

				if (UIManager.getDragInventory() == this.item) {
					this.DrawTextureCol(this.item.getTex(), (double)(this.tex.getWidth() / 2 - 16), (double)(this.tex.getHeight() / 2 - 16), new Color(255, 255, 255, (byte)((int)(128.0F * this.alpha))));
				} else {
					this.DrawTexture(this.item.getTex(), (double)(this.tex.getWidth() / 2 - 16), (double)(this.tex.getHeight() / 2 - 16), (double)this.alpha);
				}

				if (this.item instanceof Drainable) {
					int int1 = (int)(24.0F * ((Drainable)this.item).getUsedDelta());
					this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)(this.tex.getWidth() / 2 - 13), (double)(this.tex.getHeight() - 13), 26.0, 4.0, new Color(0, 0, 0, 255));
					this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)(1 + this.tex.getWidth() / 2 - 13), (double)(1 + this.tex.getHeight() - 13), (double)int1, 2.0, new Color(64, 150, 32, 255));
				}

				if (this.item instanceof HandWeapon) {
					Integer integer = (int)((float)((HandWeapon)this.item).getCondition() / (float)((HandWeapon)this.item).getConditionMax() * 5.0F);
					if (this.item.getCondition() > 0 && integer == 0) {
						integer = 1;
					}

					String string = "media/ui/QualityStar_" + integer + ".png";
					this.DrawTextureScaledCol(Texture.getSharedTexture(string), (double)(1 + (this.tex.getWidth() / 2 - 16)), (double)(20 + (this.tex.getHeight() / 2 - 16)), 11.0, 12.0, new Color(255, 255, 255, 255));
				}

				if (this.item.getUses() > 1) {
					this.DrawTextRight("x" + (new Integer(this.item.getUses())).toString(), (double)(this.tex.getWidth() / 2 - 16 + 32), (double)(this.tex.getHeight() / 2 - 16), 1.0, 1.0, 1.0, 1.0);
				}
			}
		}

		super.render();
	}

	private int ExistsInOtherSlot(InventoryItem inventoryItem, int int1) {
		for (int int2 = 0; int2 < 4; ++int2) {
			if (int2 != int1) {
				InventoryItem inventoryItem2 = null;
				if (int2 == 0) {
					inventoryItem2 = this.chr.getCraftIngredient1();
				}

				if (int2 == 1) {
					inventoryItem2 = this.chr.getCraftIngredient2();
				}

				if (int2 == 2) {
					inventoryItem2 = this.chr.getCraftIngredient3();
				}

				if (int2 == 3) {
					inventoryItem2 = this.chr.getCraftIngredient4();
				}

				if (inventoryItem2 == inventoryItem) {
					return int2;
				}
			}
		}

		return -1;
	}
}
