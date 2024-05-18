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

   public InventoryFlowControl(int var1, int var2, int var3, int var4, ItemContainer var5) {
      this.x = (double)var1;
      this.y = (double)var2;
      this.width = (float)(var3 * 32);
      this.height = (float)(var4 * 32);
      this.Container = var5;
      this.wTiles = var3;
      this.toolTip.alpha = this.toolTip.targetAlpha = 0.5F;
      this.hTiles = var4;
      this.AddChild(this.toolTip);
      this.toolTip.visible = false;
   }

   public Boolean onMouseDown(double var1, double var3) {
      if (this.timeSinceClick < 5) {
         this.DoubleClick((int)var1, (int)var3);
      }

      this.clicked = true;
      this.setCapture(true);
      return Boolean.FALSE;
   }

   public Boolean onMouseMove(double var1, double var3) {
      if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
         this.mouseOver = true;
         this.toolTip.setX((double)Mouse.getXA() - this.getAbsoluteX());
         this.toolTip.setY((double)Mouse.getYA() - this.getAbsoluteY());
         this.toolTip.setX(this.toolTip.getX() + 5.0D);
         this.toolTip.setY(this.toolTip.getY() + 5.0D);
         InventoryItem var5 = this.getInventoryFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
         this.mouseItem = var5;
         return Boolean.TRUE;
      } else {
         return Boolean.TRUE;
      }
   }

   public void onMouseMoveOutside(double var1, double var3) {
      this.toolTip.setVisible(false);
      this.mouseOver = false;
   }

   public Boolean onMouseUp(double var1, double var3) {
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

            NewContainerPanel var5 = UIManager.getOpenContainer();
            if (var5 != null) {
               Rectangle var6;
               InventoryItem var7;
               if (this.mouseItem != null && this.mouseItem.getContainer() == IsoPlayer.instance.getInventory()) {
                  var6 = this.getInventoryRectFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
                  if (var6 != null && var1 - (double)var6.getX() > 24.0D) {
                     var5.Flow.Container.AddItem(this.mouseItem);
                     var5.Flow.Container.dirty = true;
                     IsoPlayer.instance.getInventory().Items.remove(this.mouseItem);
                     IsoPlayer.instance.getInventory().dirty = true;
                     this.toolTip.setX((double)Mouse.getXA() - this.getAbsoluteX());
                     this.toolTip.setY((double)Mouse.getYA() - this.getAbsoluteY());
                     this.toolTip.setX(this.toolTip.getX() + 5.0D);
                     this.toolTip.setY(this.toolTip.getY() + 5.0D);
                     this.dorender(false);
                     var7 = this.getInventoryFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
                     this.mouseItem = var7;
                     this.mouseOver = true;
                     return Boolean.TRUE;
                  }
               }

               if (this.mouseItem != null && this.mouseItem.getContainer() == var5.Flow.Container) {
                  var6 = this.getInventoryRectFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
                  if (var6 != null && var1 - (double)var6.getX() <= 8.0D) {
                     IsoPlayer.instance.getInventory().AddItem(this.mouseItem);
                     IsoPlayer.instance.getInventory().dirty = true;
                     var5.Flow.Container.Items.remove(this.mouseItem);
                     var5.Flow.Container.dirty = true;
                     this.toolTip.setX((double)Mouse.getXA() - this.getAbsoluteX());
                     this.toolTip.setY((double)Mouse.getYA() - this.getAbsoluteY());
                     this.toolTip.setX(this.toolTip.getX() + 5.0D);
                     this.toolTip.setY(this.toolTip.getY() + 5.0D);
                     this.dorender(false);
                     var7 = this.getInventoryFromRect(this.toolTip.getX().intValue(), this.toolTip.getY().intValue());
                     this.mouseItem = var7;
                     this.mouseOver = true;
                     return Boolean.TRUE;
                  }
               }
            }

            if (this.clicked) {
               this.timeSinceClick = 0;
               if (UIManager.getDragInventory() == null) {
                  UIManager.setDragInventory(this.getInventoryFromRect((int)var1, (int)var3));
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
                  UIManager.setDragInventory(this.getInventoryFromRect((int)var1, (int)var3));
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

   public void dorender(boolean var1) {
      this.InventoryDrawn.clear();
      this.InventoryItemsDrawn.clear();
      int var2 = 0;
      int var3 = 0;
      if (var1 && this.bkg == null) {
         this.bkg = Texture.getSharedTexture("media/ui/ItemBackground_Grey.png");
      }

      for(int var4 = 0; var4 < this.Container.Items.size(); ++var4) {
         InventoryItem var5 = (InventoryItem)this.Container.Items.get(var4);
         if (var5 == null) {
            boolean var6 = false;
         } else if (var3 < this.startRow) {
            ++var2;
            if (var2 >= this.wTiles) {
               ++var3;
               var2 = 0;
            }
         } else {
            if (var3 - this.startRow >= this.hTiles) {
               break;
            }

            if (var1) {
               this.DrawTextureCol(this.bkg, (double)(var2 * 32), (double)((var3 - this.startRow) * 32), Color.white);
               if (var5 instanceof Food) {
                  float var9 = ((Food)var5).getHeat();
                  boolean var7 = false;
                  if (var9 > 1.0F) {
                     --var9;
                     var7 = true;
                  }

                  if (var7) {
                     tempcol.setColor(G, H, var9);
                     this.DrawTextureScaledCol((Texture)null, (double)(var2 * 32), (double)((var3 - this.startRow) * 32), 32.0D, 32.0D, tempcol);
                  } else {
                     tempcol.setColor(C, G, var9);
                     this.DrawTextureScaledCol((Texture)null, (double)(var2 * 32), (double)((var3 - this.startRow) * 32), 32.0D, 32.0D, tempcol);
                  }
               }

               try {
                  if (UIManager.getDragInventory() == var5) {
                     this.DrawTextureCol(var5.getTex(), (double)(var2 * 32), (double)((var3 - this.startRow) * 32), trans);
                  } else {
                     this.DrawTextureCol(var5.getTex(), (double)(var2 * 32), (double)((var3 - this.startRow) * 32), Color.white);
                  }
               } catch (Exception var8) {
               }

               if (var5 instanceof Drainable) {
                  int var10 = (int)(24.0F * ((Drainable)var5).getUsedDelta());
                  this.DrawTextureScaledCol((Texture)null, (double)(var2 * 32 + var5.getTex().getWidth() / 2 - 13), (double)((var3 - this.startRow) * 32 + 32 - 5), 26.0D, 4.0D, Color.black);
                  this.DrawTextureScaledCol((Texture)null, (double)(1 + var2 * 32 + var5.getTex().getWidth() / 2 - 13), (double)(1 + (var3 - this.startRow) * 32 + 32 - 5), (double)var10, 2.0D, colDrain);
               }

               Integer var11;
               if (var5 instanceof HandWeapon) {
                  var11 = (int)((float)((HandWeapon)var5).getCondition() / (float)((HandWeapon)var5).getConditionMax() * 5.0F);
                  if (var5.getCondition() > 0 && var11 == 0) {
                     var11 = 1;
                  }

                  String var12 = "media/ui/QualityStar_" + var11 + ".png";
                  this.DrawTextureScaledCol(Texture.getSharedTexture(var12), (double)(1 + var2 * 32), (double)(20 + (var3 - this.startRow) * 32), 11.0D, 12.0D, Color.white);
               }

               if (var5.getUses() > 1) {
                  this.DrawTextRight("x" + (new Integer(var5.getUses())).toString(), (double)(var2 * 32 + 32), (double)((var3 - this.startRow) * 32), 1.0D, 1.0D, 1.0D, 1.0D);
               }

               if (this.mouseItem == var5) {
                  var11 = null;
                  Texture var14;
                  if (this.Container.parent instanceof IsoPlayer) {
                     var14 = this.Inventory_TransferRight;
                  } else {
                     var14 = this.Inventory_TransferLeft;
                  }

                  NewContainerPanel var13 = UIManager.getOpenContainer();
                  if (var13 != null) {
                     if (this.Container.parent instanceof IsoPlayer) {
                        this.DrawTextureCol(var14, 20.0D, (double)((var3 - this.startRow) * 32), Color.white);
                     } else {
                        this.DrawTextureCol(var14, (double)(var2 * 32), (double)((var3 - this.startRow) * 32), Color.white);
                     }
                  }
               }
            }

            this.InventoryDrawn.add(new Rectangle(var2 * 32, (var3 - this.startRow) * 32, 34, 34));
            this.InventoryItemsDrawn.add(var5);
            ++var2;
            if (var2 >= this.wTiles) {
               ++var3;
               var2 = 0;
            }
         }
      }

      if (var1) {
         while(var3 - this.startRow < this.hTiles) {
            if (this.bkg != null) {
               this.DrawTextureCol(this.bkg, (double)(var2 * 32), (double)((var3 - this.startRow) * 32), Color.white);
            }

            ++var2;
            if (var2 >= this.wTiles) {
               ++var3;
               var2 = 0;
            }
         }
      }

      if (var1 && this.mouseOver) {
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

   private InventoryItem getInventoryFromRect(int var1, int var2) {
      for(int var3 = 0; var3 < this.InventoryDrawn.size(); ++var3) {
         if (((Rectangle)this.InventoryDrawn.get(var3)).contains(var1, var2)) {
            return (InventoryItem)this.InventoryItemsDrawn.get(var3);
         }
      }

      return null;
   }

   private Rectangle getInventoryRectFromRect(int var1, int var2) {
      for(int var3 = 0; var3 < this.InventoryDrawn.size(); ++var3) {
         if (((Rectangle)this.InventoryDrawn.get(var3)).contains(var1, var2)) {
            return (Rectangle)this.InventoryDrawn.get(var3);
         }
      }

      return null;
   }

   private void DoubleClick(int var1, int var2) {
      InventoryItem var3 = this.getInventoryFromRect(var1, var2);
      if (this.Container != IsoPlayer.getInstance().getInventory()) {
         if (UIManager.getDragInventory() == var3) {
            this.Container.Items.remove(var3);
            this.Container.dirty = true;
            IsoPlayer.getInstance().getInventory().AddItem(var3);
            UIManager.setDragInventory((InventoryItem)null);
         }

      }
   }
}
