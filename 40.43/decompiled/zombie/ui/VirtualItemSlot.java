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

   public VirtualItemSlot(String var1, int var2, int var3, String var4, IsoGameCharacter var5) {
      this.chr = var5;
      this.type = var1;
      this.tex = Texture.getSharedTexture(var4);
      this.x = (double)var2;
      this.y = (double)var3;
      this.width = (float)this.tex.getWidth();
      this.height = (float)this.tex.getHeight();
   }

   public Boolean onMouseDown(double var1, double var3) {
      if (this.isVisible() && SpeedControls.instance.getCurrentGameSpeed() != 0) {
         super.onMouseDown(var1, var3);
         if (this.item != null && UIManager.getDragInventory() == null && this.type.equals("CraftingResult")) {
            UIManager.setDragInventory(IsoPlayer.getInstance().getInventory().AddItem(this.item));
            if (UIManager.getDragInventory() instanceof Drainable) {
               ((Drainable)UIManager.getDragInventory()).setUsedDelta(((Drainable)this.item).getUsedDelta());
            }

            NewCraftingPanel.instance.PerformMakeItem();
            return Boolean.TRUE;
         } else {
            if (UIManager.getDragInventory() != null) {
               InventoryItem var5 = UIManager.getDragInventory();
               if (this.type.equals("CraftingResult")) {
                  if (this.item != null && UIManager.getDragInventory().getType().equals(this.item.getType()) && this.item.CanStack(var5)) {
                     String var9 = this.item.getType();
                     if (!var9.contains(".")) {
                        var9 = var5.getModule() + "." + this.item.getType();
                     }

                     InventoryItem var7 = InventoryItemFactory.CreateItem(var9);
                     UIManager.getDragInventory().setUses(UIManager.getDragInventory().getUses() + var7.getUses());
                     NewCraftingPanel.instance.PerformMakeItem();
                  }

                  return Boolean.TRUE;
               }

               if (this.type.equals("CraftingIngredient")) {
                  int var6 = this.ExistsInOtherSlot(var5, this.index);
                  if (var6 != -1) {
                     this.chr.setCraftingByIndex(var6, (InventoryItem)null);
                  }

                  if (var5.getContainer() == this.chr.getInventory()) {
                     this.chr.setCraftingByIndex(this.index, var5);
                  } else {
                     if (var5.getContainer() != null) {
                        var5.getContainer().Items.remove(var5);
                        var5.getContainer().dirty = true;
                     }

                     var5 = this.chr.getInventory().AddItem(var5);
                     var5.setContainer(this.chr.getInventory());
                     this.chr.setCraftingByIndex(this.index, var5);
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

                  if (this.chr.getSecondaryHandItem() == var5) {
                     this.item = this.chr.getPrimaryHandItem();
                     this.chr.setPrimaryHandItem(var5);
                     this.chr.setSecondaryHandItem(this.item);
                  } else if (var5.getContainer() == this.chr.getInventory()) {
                     this.chr.setPrimaryHandItem(var5);
                  } else {
                     if (var5.getContainer() != null) {
                        var5.getContainer().Items.remove(var5);
                        var5.getContainer().dirty = true;
                     }

                     var5.setContainer(this.chr.getInventory());
                     this.chr.getInventory().AddItem(var5);
                     this.chr.setPrimaryHandItem(var5);
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

                  if (this.chr.getPrimaryHandItem() == var5) {
                     this.item = this.chr.getSecondaryHandItem();
                     this.chr.setSecondaryHandItem(var5);
                     this.chr.setPrimaryHandItem(this.item);
                  } else if (var5.getContainer() == this.chr.getInventory()) {
                     this.chr.setSecondaryHandItem(var5);
                  } else {
                     if (var5.getContainer() != null) {
                        var5.getContainer().Items.remove(var5);
                        var5.getContainer().dirty = true;
                     }

                     var5.setContainer(this.chr.getInventory());
                     this.chr.getInventory().AddItem(var5);
                     this.chr.setSecondaryHandItem(var5);
                  }

                  this.item = this.chr.getSecondaryHandItem();
                  UIManager.setDragInventory((InventoryItem)null);
               } else if (!this.type.equals("Head_Clothing")) {
                  if (this.type.equals("Torso_Clothing")) {
                     if (var5 instanceof Clothing && ((Clothing)var5).getBodyLocation() == Item.ClothingBodyLocation.Top) {
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

                        if (var5.getContainer() == this.chr.getInventory()) {
                           this.chr.setClothingItem_Torso(var5);
                           if (this.chr.getPrimaryHandItem() == var5) {
                              this.chr.setPrimaryHandItem((InventoryItem)null);
                           }

                           if (this.chr.getSecondaryHandItem() == var5) {
                              this.chr.setSecondaryHandItem((InventoryItem)null);
                           }

                           if (this.chr.getClothingItem_Head() == var5) {
                              this.chr.setClothingItem_Head((InventoryItem)null);
                           }

                           if (this.chr.getClothingItem_Hands() == var5) {
                              this.chr.setClothingItem_Hands((InventoryItem)null);
                           }

                           if (this.chr.getClothingItem_Legs() == var5) {
                              this.chr.setClothingItem_Legs((InventoryItem)null);
                           }

                           if (this.chr.getClothingItem_Feet() == var5) {
                              this.chr.setClothingItem_Feet((InventoryItem)null);
                           }

                           if (Sidebar.instance.MainHand.item == var5) {
                              Sidebar.instance.MainHand.item = null;
                           }

                           if (Sidebar.instance.SecondHand.item == var5) {
                              Sidebar.instance.SecondHand.item = null;
                           }

                           if (ClothingPanel.instance.HeadItem.item == var5) {
                              ClothingPanel.instance.HeadItem.item = null;
                           }

                           if (ClothingPanel.instance.HandsItem.item == var5) {
                              ClothingPanel.instance.HandsItem.item = null;
                           }

                           if (ClothingPanel.instance.LegsItem.item == var5) {
                              ClothingPanel.instance.LegsItem.item = null;
                           }

                           if (ClothingPanel.instance.FeetItem.item == var5) {
                              ClothingPanel.instance.FeetItem.item = null;
                           }
                        } else {
                           if (var5.getContainer() != null) {
                              var5.getContainer().Items.remove(var5);
                              var5.getContainer().dirty = true;
                           }

                           var5.setContainer(this.chr.getInventory());
                           this.chr.getInventory().AddItem(var5);
                           this.chr.setClothingItem_Torso(var5);
                        }

                        this.item = this.chr.getClothingItem_Torso();
                        IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Top, ((Clothing)this.item).getSpriteName(), ((Clothing)this.item).getPalette());
                        UIManager.setDragInventory((InventoryItem)null);
                     }
                  } else if (!this.type.equals("Hands_Clothing")) {
                     if (this.type.equals("Legs_Clothing")) {
                        if (var5 instanceof Clothing && ((Clothing)var5).getBodyLocation() == Item.ClothingBodyLocation.Bottoms) {
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

                           if (var5.getContainer() == this.chr.getInventory()) {
                              this.chr.setClothingItem_Legs(var5);
                              if (this.chr.getPrimaryHandItem() == var5) {
                                 this.chr.setPrimaryHandItem((InventoryItem)null);
                              }

                              if (this.chr.getSecondaryHandItem() == var5) {
                                 this.chr.setSecondaryHandItem((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Head() == var5) {
                                 this.chr.setClothingItem_Head((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Torso() == var5) {
                                 this.chr.setClothingItem_Torso((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Hands() == var5) {
                                 this.chr.setClothingItem_Hands((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Feet() == var5) {
                                 this.chr.setClothingItem_Feet((InventoryItem)null);
                              }

                              if (Sidebar.instance.MainHand.item == var5) {
                                 Sidebar.instance.MainHand.item = null;
                              }

                              if (Sidebar.instance.SecondHand.item == var5) {
                                 Sidebar.instance.SecondHand.item = null;
                              }

                              if (ClothingPanel.instance.HeadItem.item == var5) {
                                 ClothingPanel.instance.HeadItem.item = null;
                              }

                              if (ClothingPanel.instance.TorsoItem.item == var5) {
                                 ClothingPanel.instance.TorsoItem.item = null;
                              }

                              if (ClothingPanel.instance.HandsItem.item == var5) {
                                 ClothingPanel.instance.HandsItem.item = null;
                              }

                              if (ClothingPanel.instance.FeetItem.item == var5) {
                                 ClothingPanel.instance.FeetItem.item = null;
                              }
                           } else {
                              if (var5.getContainer() != null) {
                                 var5.getContainer().Items.remove(var5);
                                 var5.getContainer().dirty = true;
                              }

                              var5.setContainer(this.chr.getInventory());
                              this.chr.getInventory().AddItem(var5);
                              this.chr.setClothingItem_Legs(var5);
                           }

                           this.item = this.chr.getClothingItem_Legs();
                           IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Bottoms, ((Clothing)this.item).getSpriteName(), ((Clothing)this.item).getPalette());
                           UIManager.setDragInventory((InventoryItem)null);
                        }
                     } else if (this.type.equals("Feet_Clothing")) {
                        if (var5 instanceof Clothing && ((Clothing)var5).getBodyLocation() == Item.ClothingBodyLocation.Shoes) {
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

                           if (var5.getContainer() == this.chr.getInventory()) {
                              this.chr.setClothingItem_Feet(var5);
                              if (this.chr.getPrimaryHandItem() == var5) {
                                 this.chr.setPrimaryHandItem((InventoryItem)null);
                              }

                              if (this.chr.getSecondaryHandItem() == var5) {
                                 this.chr.setSecondaryHandItem((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Head() == var5) {
                                 this.chr.setClothingItem_Head((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Torso() == var5) {
                                 this.chr.setClothingItem_Torso((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Hands() == var5) {
                                 this.chr.setClothingItem_Hands((InventoryItem)null);
                              }

                              if (this.chr.getClothingItem_Legs() == var5) {
                                 this.chr.setClothingItem_Legs((InventoryItem)null);
                              }

                              if (Sidebar.instance.MainHand.item == var5) {
                                 Sidebar.instance.MainHand.item = null;
                              }

                              if (Sidebar.instance.SecondHand.item == var5) {
                                 Sidebar.instance.SecondHand.item = null;
                              }

                              if (ClothingPanel.instance.HeadItem.item == var5) {
                                 ClothingPanel.instance.HeadItem.item = null;
                              }

                              if (ClothingPanel.instance.TorsoItem.item == var5) {
                                 ClothingPanel.instance.TorsoItem.item = null;
                              }

                              if (ClothingPanel.instance.HandsItem.item == var5) {
                                 ClothingPanel.instance.HandsItem.item = null;
                              }

                              if (ClothingPanel.instance.LegsItem.item == var5) {
                                 ClothingPanel.instance.LegsItem.item = null;
                              }
                           } else {
                              if (var5.getContainer() != null) {
                                 var5.getContainer().Items.remove(var5);
                                 var5.getContainer().dirty = true;
                              }

                              var5.setContainer(this.chr.getInventory());
                              this.chr.getInventory().AddItem(var5);
                              this.chr.setClothingItem_Feet(var5);
                           }

                           this.item = this.chr.getClothingItem_Feet();
                           IsoPlayer.getInstance().SetClothing(Item.ClothingBodyLocation.Shoes, ((Clothing)this.item).getSpriteName(), ((Clothing)this.item).getPalette());
                           UIManager.setDragInventory((InventoryItem)null);
                        }
                     } else if (UIManager.getDragInventory() != null) {
                        this.item = UIManager.getDragInventory();
                        if (this.getTable() != null && this.getTable().rawget("onPlaceItem") != null) {
                           Object[] var8 = LuaManager.caller.pcall(LuaManager.thread, this.getTable().rawget("onPlaceItem"), this.table, this.item);
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
         this.DrawTexture(this.tex, 0.0D, 0.0D, (double)this.alpha);
         if (this.item != null) {
            if (this.item instanceof Food) {
               float var1 = ((Food)this.item).getHeat();
               boolean var2 = false;
               if (var1 > 1.0F) {
                  --var1;
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
               int var6 = (int)(24.0F * ((Drainable)this.item).getUsedDelta());
               this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)(this.tex.getWidth() / 2 - 13), (double)(this.tex.getHeight() - 13), 26.0D, 4.0D, new Color(0, 0, 0, 255));
               this.DrawTextureScaledCol(Texture.getSharedTexture("media/white.png"), (double)(1 + this.tex.getWidth() / 2 - 13), (double)(1 + this.tex.getHeight() - 13), (double)var6, 2.0D, new Color(64, 150, 32, 255));
            }

            if (this.item instanceof HandWeapon) {
               Integer var7 = (int)((float)((HandWeapon)this.item).getCondition() / (float)((HandWeapon)this.item).getConditionMax() * 5.0F);
               if (this.item.getCondition() > 0 && var7 == 0) {
                  var7 = 1;
               }

               String var8 = "media/ui/QualityStar_" + var7 + ".png";
               this.DrawTextureScaledCol(Texture.getSharedTexture(var8), (double)(1 + (this.tex.getWidth() / 2 - 16)), (double)(20 + (this.tex.getHeight() / 2 - 16)), 11.0D, 12.0D, new Color(255, 255, 255, 255));
            }

            if (this.item.getUses() > 1) {
               this.DrawTextRight("x" + (new Integer(this.item.getUses())).toString(), (double)(this.tex.getWidth() / 2 - 16 + 32), (double)(this.tex.getHeight() / 2 - 16), 1.0D, 1.0D, 1.0D, 1.0D);
            }
         }
      }

      super.render();
   }

   private int ExistsInOtherSlot(InventoryItem var1, int var2) {
      for(int var3 = 0; var3 < 4; ++var3) {
         if (var3 != var2) {
            InventoryItem var4 = null;
            if (var3 == 0) {
               var4 = this.chr.getCraftIngredient1();
            }

            if (var3 == 1) {
               var4 = this.chr.getCraftIngredient2();
            }

            if (var3 == 2) {
               var4 = this.chr.getCraftIngredient3();
            }

            if (var3 == 3) {
               var4 = this.chr.getCraftIngredient4();
            }

            if (var4 == var1) {
               return var3;
            }
         }
      }

      return -1;
   }
}
