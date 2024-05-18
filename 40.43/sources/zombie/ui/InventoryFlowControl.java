package zombie.ui;

import java.util.Stack;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.Rectangle;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.textures.Texture;
import zombie.input.Mouse;
import zombie.inventory.InventoryItem;
import zombie.inventory.ItemContainer;
import zombie.inventory.types.Drainable;
import zombie.inventory.types.Food;
import zombie.inventory.types.HandWeapon;
import zombie.scripting.objects.Item;


public class InventoryFlowControl extends UIElement {
	ObjectTooltip toolTip = new ObjectTooltip();
	boolean clicked = false;
	public ItemContainer Container;
	int hTiles = 0;
	Stack InventoryDrawn = new Stack();
	Stack InventoryItemsDrawn = new Stack();
	int wTiles = 0;
	private boolean mouseOver = false;
	public Texture Inventory_TransferLeft = Texture.getSharedTexture("media/ui/Inventory_TransferLeft.png");
	public Texture Inventory_TransferRight = Texture.getSharedTexture("media/ui/Inventory_TransferRight.png");
	InventoryItem mouseItem = null;
	int timeSinceClick = 0;
	public int startRow = 0;
	Texture bkg = null;
	Texture white = null;
	static Color C = new Color(70, 153, 211);
	static Color H = new Color(222, 70, 70);
	static Color G = new Color(162, 162, 162, 0);
	static Color tempcol = new Color(162, 162, 162, 0);
	static Color colDrain = new Color(64, 150, 32, 255);
	static Color trans = new Color(255, 255, 255, 128);

	public InventoryFlowControl(int int1, int int2, int int3, int int4, ItemContainer itemContainer) {
		this.x = (double)int1;
		this.y = (double)int2;
		this.width = (float)(int3 * 32);
		this.height = (float)(int4 * 32);
		this.Container = itemContainer;
		this.wTiles = int3;
		this.toolTip.alpha = this.toolTip.targetAlpha = 0.5F;
		this.hTiles = int4;
		this.AddChild(this.toolTip);
		this.toolTip.visible = false;
	}

	public Boolean onMouseDown(double double1, double double2) {
		if (this.timeSinceClick < 5) {
			this.DoubleClick((int)double1, (int)double2);
		}

		this.clicked = true;
		this.setCapture(true);
		return Boolean.FALSE;
	}

	public Boolean onMouseMove(double double1, double double2) {
		if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
			this.mouseOver = true;
			this.toolTip.setX((double)Mouse.getXA() - this.getAbsoluteX());
			this.toolTip.setY((double)Mouse.getYA() - this.getAbsoluteY());
			this.toolTip.setX(this.toolTip.getX() + 5.0);
			this.toolTip.setY(this.toolTip.getY() + 5.0);
			InventoryItem inventoryItem = this.getInventoryFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
			this.mouseItem = inventoryItem;
			return Boolean.TRUE;
		} else {
			return Boolean.TRUE;
		}
	}

	public void onMouseMoveOutside(double double1, double double2) {
		this.toolTip.setVisible(false);
		this.mouseOver = false;
	}

	public Boolean onMouseUp(double double1, double double2) {
		if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
			if (UIManager.getDragInventory() != null) {
				if (IsoPlayer.getInstance().getCraftIngredient1() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().setCraftIngredient1((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getCraftIngredient2() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().setCraftIngredient2((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getCraftIngredient3() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().setCraftIngredient3((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getCraftIngredient4() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().setCraftIngredient4((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getClothingItem_Head() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().setClothingItem_Head((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getClothingItem_Torso() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Top, (String)null, (String)null);
					IsoPlayer.getInstance().setClothingItem_Torso((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getClothingItem_Hands() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().setClothingItem_Hands((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getClothingItem_Legs() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Bottoms, (String)null, (String)null);
					IsoPlayer.getInstance().setClothingItem_Legs((InventoryItem)null);
				}

				if (IsoPlayer.getInstance().getClothingItem_Feet() == UIManager.getDragInventory()) {
					IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Shoes, (String)null, (String)null);
					IsoPlayer.getInstance().setClothingItem_Feet((InventoryItem)null);
				}
			}

			if (this.clicked) {
				if (UIManager.getDragInventory() != null) {
					this.mouseItem = UIManager.getDragInventory();
				}

				NewContainerPanel newContainerPanel = UIManager.getOpenContainer();
				if (newContainerPanel != null) {
					Rectangle rectangle;
					InventoryItem inventoryItem;
					if (this.mouseItem != null && this.mouseItem.getContainer() == IsoPlayer.instance.getInventory()) {
						rectangle = this.getInventoryRectFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
						if (rectangle != null && double1 - (double)rectangle.getX() > 24.0) {
							newContainerPanel.Flow.Container.AddItem(this.mouseItem);
							newContainerPanel.Flow.Container.dirty = true;
							IsoPlayer.instance.getInventory().Items.remove(this.mouseItem);
							IsoPlayer.instance.getInventory().dirty = true;
							this.toolTip.setX((double)Mouse.getXA() - this.getAbsoluteX());
							this.toolTip.setY((double)Mouse.getYA() - this.getAbsoluteY());
							this.toolTip.setX(this.toolTip.getX() + 5.0);
							this.toolTip.setY(this.toolTip.getY() + 5.0);
							this.dorender(false);
							inventoryItem = this.getInventoryFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
							this.mouseItem = inventoryItem;
							this.mouseOver = true;
							return Boolean.TRUE;
						}
					}

					if (this.mouseItem != null && this.mouseItem.getContainer() == newContainerPanel.Flow.Container) {
						rectangle = this.getInventoryRectFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
						if (rectangle != null && double1 - (double)rectangle.getX() <= 8.0) {
							IsoPlayer.instance.getInventory().AddItem(this.mouseItem);
							IsoPlayer.instance.getInventory().dirty = true;
							newContainerPanel.Flow.Container.Items.remove(this.mouseItem);
							newContainerPanel.Flow.Container.dirty = true;
							this.toolTip.setX((double)Mouse.getXA() - this.getAbsoluteX());
							this.toolTip.setY((double)Mouse.getYA() - this.getAbsoluteY());
							this.toolTip.setX(this.toolTip.getX() + 5.0);
							this.toolTip.setY(this.toolTip.getY() + 5.0);
							this.dorender(false);
							inventoryItem = this.getInventoryFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
							this.mouseItem = inventoryItem;
							this.mouseOver = true;
							return Boolean.TRUE;
						}
					}
				}

				if (this.clicked) {
					this.timeSinceClick = 0;
					if (UIManager.getDragInventory() == null) {
						UIManager.setDragInventory(this.getInventoryFromRect((int)double1, (int)double2));
					} else if (UIManager.getDragInventory() == IsoPlayer.getInstance().getPrimaryHandItem() && UIManager.getDragInventory().getContainer() == this.Container) {
						IsoPlayer.getInstance().setPrimaryHandItem((InventoryItem)null);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (UIManager.getDragInventory() == IsoPlayer.getInstance().getSecondaryHandItem() && UIManager.getDragInventory().getContainer() == this.Container) {
						IsoPlayer.getInstance().setSecondaryHandItem((InventoryItem)null);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (UIManager.getDragInventory() == IsoPlayer.getInstance().getClothingItem_Head() && UIManager.getDragInventory().getContainer() == this.Container) {
						IsoPlayer.getInstance().setClothingItem_Head((InventoryItem)null);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (UIManager.getDragInventory() == IsoPlayer.getInstance().getClothingItem_Torso() && UIManager.getDragInventory().getContainer() == this.Container) {
						IsoPlayer.getInstance().setClothingItem_Torso((InventoryItem)null);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (UIManager.getDragInventory() == IsoPlayer.getInstance().getClothingItem_Hands() && UIManager.getDragInventory().getContainer() == this.Container) {
						IsoPlayer.getInstance().setClothingItem_Hands((InventoryItem)null);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (UIManager.getDragInventory() == IsoPlayer.getInstance().getClothingItem_Legs() && UIManager.getDragInventory().getContainer() == this.Container) {
						IsoPlayer.getInstance().setClothingItem_Legs((InventoryItem)null);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (UIManager.getDragInventory() == IsoPlayer.getInstance().getClothingItem_Feet() && UIManager.getDragInventory().getContainer() == this.Container) {
						IsoPlayer.getInstance().setClothingItem_Feet((InventoryItem)null);
						UIManager.setDragInventory((InventoryItem)null);
					} else if (UIManager.getDragInventory().getContainer() != this.Container) {
						if (UIManager.getDragInventory().getContainer() != null) {
							if (!PZConsole.instance.isVisible() && Keyboard.isKeyDown(29)) {
							}

							if (UIManager.getDragInventory() != null && UIManager.getDragInventory().getUses() <= 0) {
								UIManager.setDragInventory((InventoryItem)null);
							}

							return Boolean.TRUE;
						}

						if (UIManager.getDragInventory() == NewCraftingPanel.instance.result.item) {
							NewCraftingPanel.instance.PerformMakeItem();
						}

						this.Container.AddItem(UIManager.getDragInventory());
						UIManager.getDragInventory().setContainer(this.Container);
						UIManager.setDragInventory((InventoryItem)null);
					} else {
						UIManager.setDragInventory(this.getInventoryFromRect((int)double1, (int)double2));
					}
				}
			}

			this.clicked = false;
			this.setCapture(false);
			return Boolean.FALSE;
		} else {
			return Boolean.TRUE;
		}
	}

	public void render() {
		this.dorender(true);
	}

	public void dorender(boolean boolean1) {
		this.InventoryDrawn.clear();
		this.InventoryItemsDrawn.clear();
		int int1 = 0;
		int int2 = 0;
		if (boolean1 && this.bkg == null) {
			this.bkg = Texture.getSharedTexture("media/ui/ItemBackground_Grey.png");
		}

		for (int int3 = 0; int3 < this.Container.Items.size(); ++int3) {
			InventoryItem inventoryItem = (InventoryItem)this.Container.Items.get(int3);
			if (inventoryItem == null) {
				boolean boolean2 = false;
			} else if (int2 < this.startRow) {
				++int1;
				if (int1 >= this.wTiles) {
					++int2;
					int1 = 0;
				}
			} else {
				if (int2 - this.startRow >= this.hTiles) {
					break;
				}

				if (boolean1) {
					this.DrawTextureCol(this.bkg, (double)(int1 * 32), (double)((int2 - this.startRow) * 32), Color.white);
					if (inventoryItem instanceof Food) {
						float float1 = ((Food)inventoryItem).getHeat();
						boolean boolean3 = false;
						if (float1 > 1.0F) {
							--float1;
							boolean3 = true;
						}

						if (boolean3) {
							tempcol.setColor(G, H, float1);
							this.DrawTextureScaledCol((Texture)null, (double)(int1 * 32), (double)((int2 - this.startRow) * 32), 32.0, 32.0, tempcol);
						} else {
							tempcol.setColor(C, G, float1);
							this.DrawTextureScaledCol((Texture)null, (double)(int1 * 32), (double)((int2 - this.startRow) * 32), 32.0, 32.0, tempcol);
						}
					}

					try {
						if (UIManager.getDragInventory() == inventoryItem) {
							this.DrawTextureCol(inventoryItem.getTex(), (double)(int1 * 32), (double)((int2 - this.startRow) * 32), trans);
						} else {
							this.DrawTextureCol(inventoryItem.getTex(), (double)(int1 * 32), (double)((int2 - this.startRow) * 32), Color.white);
						}
					} catch (Exception exception) {
					}

					if (inventoryItem instanceof Drainable) {
						int int4 = (int)(24.0F * ((Drainable)inventoryItem).getUsedDelta());
						this.DrawTextureScaledCol((Texture)null, (double)(int1 * 32 + inventoryItem.getTex().getWidth() / 2 - 13), (double)((int2 - this.startRow) * 32 + 32 - 5), 26.0, 4.0, Color.black);
						this.DrawTextureScaledCol((Texture)null, (double)(1 + int1 * 32 + inventoryItem.getTex().getWidth() / 2 - 13), (double)(1 + (int2 - this.startRow) * 32 + 32 - 5), (double)int4, 2.0, colDrain);
					}

					Integer integer;
					if (inventoryItem instanceof HandWeapon) {
						integer = (int)((float)((HandWeapon)inventoryItem).getCondition() / (float)((HandWeapon)inventoryItem).getConditionMax() * 5.0F);
						if (inventoryItem.getCondition() > 0 && integer == 0) {
							integer = 1;
						}

						String string = "media/ui/QualityStar_" + integer + ".png";
						this.DrawTextureScaledCol(Texture.getSharedTexture(string), (double)(1 + int1 * 32), (double)(20 + (int2 - this.startRow) * 32), 11.0, 12.0, Color.white);
					}

					if (inventoryItem.getUses() > 1) {
						this.DrawTextRight("x" + (new Integer(inventoryItem.getUses())).toString(), (double)(int1 * 32 + 32), (double)((int2 - this.startRow) * 32), 1.0, 1.0, 1.0, 1.0);
					}

					if (this.mouseItem == inventoryItem) {
						integer = null;
						Texture texture;
						if (this.Container.parent instanceof IsoPlayer) {
							texture = this.Inventory_TransferRight;
						} else {
							texture = this.Inventory_TransferLeft;
						}

						NewContainerPanel newContainerPanel = UIManager.getOpenContainer();
						if (newContainerPanel != null) {
							if (this.Container.parent instanceof IsoPlayer) {
								this.DrawTextureCol(texture, 20.0, (double)((int2 - this.startRow) * 32), Color.white);
							} else {
								this.DrawTextureCol(texture, (double)(int1 * 32), (double)((int2 - this.startRow) * 32), Color.white);
							}
						}
					}
				}

				this.InventoryDrawn.add(new Rectangle(int1 * 32, (int2 - this.startRow) * 32, 34, 34));
				this.InventoryItemsDrawn.add(inventoryItem);
				++int1;
				if (int1 >= this.wTiles) {
					++int2;
					int1 = 0;
				}
			}
		}

		if (boolean1) {
			while (int2 - this.startRow < this.hTiles) {
				if (this.bkg != null) {
					this.DrawTextureCol(this.bkg, (double)(int1 * 32), (double)((int2 - this.startRow) * 32), Color.white);
				}

				++int1;
				if (int1 >= this.wTiles) {
					++int2;
					int1 = 0;
				}
			}
		}

		if (boolean1 && this.mouseOver) {
			if (this.mouseItem != null) {
				this.toolTip.setVisible(true);
				this.mouseItem.DoTooltip(this.toolTip);
			} else {
				this.toolTip.setVisible(false);
			}
		}
	}

	public void update() {
		super.update();
		++this.timeSinceClick;
		if (this.startRow < 0) {
			this.startRow = 0;
		}

		if (this.Container.Items.size() < this.wTiles * this.hTiles) {
			this.startRow = 0;
		} else if (this.startRow + this.hTiles > this.Container.Items.size()) {
			this.startRow = this.Container.Items.size() - this.hTiles;
		}
	}

	private InventoryItem getInventoryFromRect(int int1, int int2) {
		for (int int3 = 0; int3 < this.InventoryDrawn.size(); ++int3) {
			if (((Rectangle)this.InventoryDrawn.get(int3)).contains(int1, int2)) {
				return (InventoryItem)this.InventoryItemsDrawn.get(int3);
			}
		}

		return null;
	}

	private Rectangle getInventoryRectFromRect(int int1, int int2) {
		for (int int3 = 0; int3 < this.InventoryDrawn.size(); ++int3) {
			if (((Rectangle)this.InventoryDrawn.get(int3)).contains(int1, int2)) {
				return (Rectangle)this.InventoryDrawn.get(int3);
			}
		}

		return null;
	}

	private void DoubleClick(int int1, int int2) {
		InventoryItem inventoryItem = this.getInventoryFromRect(int1, int2);
		if (this.Container != IsoPlayer.getInstance().getInventory()) {
			if (UIManager.getDragInventory() == inventoryItem) {
				this.Container.Items.remove(inventoryItem);
				this.Container.dirty = true;
				IsoPlayer.getInstance().getInventory().AddItem(inventoryItem);
				UIManager.setDragInventory((InventoryItem)null);
			}
		}
	}
}
