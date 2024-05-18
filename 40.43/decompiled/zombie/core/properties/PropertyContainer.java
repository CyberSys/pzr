package zombie.core.properties;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import zombie.core.TilePropertyAliasMap;
import zombie.core.Collections.NonBlockingHashMap;
import zombie.iso.SpriteDetails.IsoFlagType;

public class PropertyContainer {
   private EnumSet SpriteFlags;
   private TIntIntHashMap Properties = new TIntIntHashMap();
   private int[] keyArray;
   public static NonBlockingHashMap test = new NonBlockingHashMap();
   public static List sorted = Collections.synchronizedList(new ArrayList());
   public boolean solid = false;
   public boolean trans = false;
   public boolean solidtrans = false;
   public boolean collideN = false;
   public boolean collideW = false;
   public boolean solidfloor = false;
   public boolean water = false;
   public boolean isBush = false;
   private byte Surface;
   private byte SurfaceFlags;
   private short StackReplaceTileOffset;
   private static final byte SURFACE_VALID = 1;
   private static final byte SURFACE_ISOFFSET = 2;
   private static final byte SURFACE_ISTABLE = 4;
   private static final byte SURFACE_ISTABLETOP = 8;

   public PropertyContainer() {
      this.SpriteFlags = EnumSet.noneOf(IsoFlagType.class);
   }

   public void CreateKeySet() {
      TIntSet var1 = this.Properties.keySet();
      this.keyArray = var1.toArray();
   }

   public PropertyContainer(PropertyContainer var1) {
      this.AddProperties(var1);
   }

   public void AddProperties(PropertyContainer var1) {
      if (var1.keyArray != null) {
         for(int var2 = 0; var2 < var1.keyArray.length; ++var2) {
            int var3 = var1.keyArray[var2];
            this.Properties.put(var3, var1.Properties.get(var3));
         }

         this.solid |= var1.solid;
         this.trans |= var1.trans;
         this.solidtrans |= var1.solidtrans;
         this.collideN |= var1.collideN;
         this.collideW |= var1.collideW;
         this.solidfloor |= var1.solidfloor;
         this.water |= var1.water;
         this.isBush |= var1.isBush;
         this.SpriteFlags.addAll(var1.SpriteFlags);
      }
   }

   public void Clear() {
      this.solid = false;
      this.trans = false;
      this.solidtrans = false;
      this.collideN = false;
      this.collideW = false;
      this.solidfloor = false;
      this.water = false;
      this.isBush = false;
      this.SpriteFlags.clear();
      this.Properties.clear();
      this.SurfaceFlags &= -2;
   }

   public boolean Is(IsoFlagType var1) {
      if (var1 == IsoFlagType.solid) {
         return this.solid;
      } else if (var1 == IsoFlagType.trans) {
         return this.trans;
      } else if (var1 == IsoFlagType.solidtrans) {
         return this.solidtrans;
      } else if (var1 == IsoFlagType.collideN) {
         return this.collideN;
      } else if (var1 == IsoFlagType.collideW) {
         return this.collideW;
      } else if (var1 == IsoFlagType.solidfloor) {
         return this.solidfloor;
      } else {
         return var1 == IsoFlagType.water ? this.water : this.SpriteFlags.contains(var1);
      }
   }

   public boolean Is(Double var1) {
      return this.SpriteFlags.contains(var1.intValue());
   }

   public void Set(String var1, String var2) {
      this.Set(var1, var2, true);
   }

   public void Set(String var1, String var2, boolean var3) {
      if (var1 != null) {
         if (var3) {
            IsoFlagType var4 = IsoFlagType.FromString(var1);
            if (var4 != IsoFlagType.MAX) {
               if (var4 == IsoFlagType.solid) {
                  this.solid = true;
               } else if (var4 == IsoFlagType.trans) {
                  this.trans = true;
               } else if (var4 == IsoFlagType.solidtrans) {
                  this.solidtrans = true;
               } else if (var4 == IsoFlagType.collideN) {
                  this.collideN = true;
               } else if (var4 == IsoFlagType.collideW) {
                  this.collideW = true;
               } else if (var4 == IsoFlagType.solidfloor) {
                  this.solidfloor = true;
               } else if (var4 == IsoFlagType.water) {
                  this.water = true;
               }

               this.Set(var4, var2);
               return;
            }
         }

         int var6 = TilePropertyAliasMap.instance.getIDFromPropertyName(var1);
         if (var6 != -1) {
            int var5 = TilePropertyAliasMap.instance.getIDFromPropertyValue(var6, var2);
            this.SurfaceFlags &= -2;
            this.Properties.put(var6, var5);
         }
      }
   }

   public void Set(IsoFlagType var1) {
      if (var1 == IsoFlagType.solid) {
         this.solid = true;
      } else if (var1 == IsoFlagType.trans) {
         this.trans = true;
      } else if (var1 == IsoFlagType.solidtrans) {
         this.solidtrans = true;
      } else if (var1 == IsoFlagType.collideN) {
         this.collideN = true;
      } else if (var1 == IsoFlagType.collideW) {
         this.collideW = true;
      } else if (var1 == IsoFlagType.solidfloor) {
         this.solidfloor = true;
      } else if (var1 == IsoFlagType.water) {
         this.water = true;
      }

      this.SpriteFlags.add(var1);
   }

   public void Set(IsoFlagType var1, String var2) {
      if (var1 == IsoFlagType.solid) {
         this.solid = true;
      } else if (var1 == IsoFlagType.trans) {
         this.trans = true;
      } else if (var1 == IsoFlagType.solidtrans) {
         this.solidtrans = true;
      } else if (var1 == IsoFlagType.collideN) {
         this.collideN = true;
      } else if (var1 == IsoFlagType.collideW) {
         this.collideW = true;
      } else if (var1 == IsoFlagType.solidfloor) {
         this.solidfloor = true;
      } else if (var1 == IsoFlagType.water) {
         this.water = true;
      }

      this.SpriteFlags.add(var1);
   }

   public void UnSet(String var1) {
      int var2 = TilePropertyAliasMap.instance.getIDFromPropertyName(var1);
      this.Properties.remove(var2);
   }

   public void UnSet(IsoFlagType var1) {
      if (var1 == IsoFlagType.solid) {
         this.solid = false;
      } else if (var1 == IsoFlagType.trans) {
         this.trans = false;
      } else if (var1 == IsoFlagType.solidtrans) {
         this.solidtrans = false;
      } else if (var1 == IsoFlagType.collideN) {
         this.collideN = false;
      } else if (var1 == IsoFlagType.collideW) {
         this.collideW = false;
      } else if (var1 == IsoFlagType.solidfloor) {
         this.solidfloor = false;
      } else if (var1 == IsoFlagType.water) {
         this.water = false;
      }

      this.SpriteFlags.remove(var1);
   }

   public String Val(String var1) {
      int var2 = TilePropertyAliasMap.instance.getIDFromPropertyName(var1);
      return !this.Properties.containsKey(var2) ? null : TilePropertyAliasMap.instance.getPropertyValueString(var2, this.Properties.get(var2));
   }

   public boolean Is(String var1) {
      int var2 = TilePropertyAliasMap.instance.getIDFromPropertyName(var1);
      return this.Properties.containsKey(var2);
   }

   public EnumSet getFlags() {
      return this.SpriteFlags;
   }

   public ArrayList getPropertyNames() {
      final ArrayList var1 = new ArrayList();
      TIntSet var2 = this.Properties.keySet();
      var2.forEach(new TIntProcedure() {
         public boolean execute(int var1x) {
            var1.add(((TilePropertyAliasMap.TileProperty)TilePropertyAliasMap.instance.Properties.get(var1x)).propertyName);
            return true;
         }
      });
      return var1;
   }

   private void initSurface() {
      if ((this.SurfaceFlags & 1) == 0) {
         this.Surface = 0;
         this.StackReplaceTileOffset = 0;
         this.SurfaceFlags = 1;
         if (this.Properties != null) {
            this.Properties.forEachEntry(new TIntIntProcedure() {
               public boolean execute(int var1, int var2) {
                  TilePropertyAliasMap.TileProperty var3 = (TilePropertyAliasMap.TileProperty)TilePropertyAliasMap.instance.Properties.get(var1);
                  String var4 = var3.propertyName;
                  String var5 = (String)var3.possibleValues.get(var2);
                  if ("Surface".equals(var4) && var5 != null) {
                     try {
                        int var6 = Integer.parseInt(var5);
                        if (var6 >= 0 && var6 <= 128) {
                           PropertyContainer.this.Surface = (byte)var6;
                        }
                     } catch (NumberFormatException var8) {
                     }
                  } else if ("IsSurfaceOffset".equals(var4)) {
                     PropertyContainer.this.SurfaceFlags = (byte)(PropertyContainer.this.SurfaceFlags | 2);
                  } else if ("IsTable".equals(var4)) {
                     PropertyContainer.this.SurfaceFlags = (byte)(PropertyContainer.this.SurfaceFlags | 4);
                  } else if ("IsTableTop".equals(var4)) {
                     PropertyContainer.this.SurfaceFlags = (byte)(PropertyContainer.this.SurfaceFlags | 8);
                  } else if ("StackReplaceTileOffset".equals(var4)) {
                     try {
                        PropertyContainer.this.StackReplaceTileOffset = (short)Integer.parseInt(var5);
                     } catch (NumberFormatException var7) {
                     }
                  }

                  return true;
               }
            });
         }
      }
   }

   public int getSurface() {
      this.initSurface();
      return this.Surface;
   }

   public boolean isSurfaceOffset() {
      this.initSurface();
      return (this.SurfaceFlags & 2) != 0;
   }

   public boolean isTable() {
      this.initSurface();
      return (this.SurfaceFlags & 4) != 0;
   }

   public boolean isTableTop() {
      this.initSurface();
      return (this.SurfaceFlags & 8) != 0;
   }

   public int getStackReplaceTileOffset() {
      this.initSurface();
      return this.StackReplaceTileOffset;
   }

   public static class MostTested {
      public IsoFlagType flag;
      public int count;
   }

   private static class ProfileEntryComparitor implements Comparator {
      public ProfileEntryComparitor() {
      }

      public int compare(Object var1, Object var2) {
         double var3 = (double)((PropertyContainer.MostTested)var1).count;
         double var5 = (double)((PropertyContainer.MostTested)var2).count;
         if (var3 > var5) {
            return -1;
         } else {
            return var5 > var3 ? 1 : 0;
         }
      }
   }
}
