package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameWindow;
import zombie.SoundManager;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.debug.DebugLog;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Item;

public class IsoTrap extends IsoObject {
   private int timerBeforeExplosion = 0;
   private int FPS;
   private int sensorRange = 0;
   private int firePower = 0;
   private int fireRange = 0;
   private int explosionPower = 0;
   private int explosionRange = 0;
   private int smokeRange = 0;
   private int noiseRange = 0;
   private float extraDamage = 0.0F;
   private int remoteControlID = -1;
   private String countDownSound = null;
   private String explosionSound = null;
   private int lastBeep = 0;
   private HandWeapon weapon;

   public IsoTrap(IsoCell var1) {
      super(var1);
      this.FPS = GameServer.bServer ? 10 : PerformanceSettings.LockFPS;
   }

   public IsoTrap(HandWeapon var1, IsoCell var2, IsoGridSquare var3) {
      this.square = var3;
      this.initSprite(var1);
      this.setSensorRange(var1.getSensorRange());
      this.setFireRange(var1.getFireRange());
      this.setFirePower(var1.getFirePower());
      this.setExplosionPower(var1.getExplosionPower());
      this.setExplosionRange(var1.getExplosionRange());
      this.setSmokeRange(var1.getSmokeRange());
      this.setNoiseRange(var1.getNoiseRange());
      this.setExtraDamage(var1.getExtraDamage());
      this.setRemoteControlID(var1.getRemoteControlID());
      this.setCountDownSound(var1.getCountDownSound());
      this.setExplosionSound(var1.getExplosionSound());
      this.FPS = GameServer.bServer ? 10 : PerformanceSettings.LockFPS;
      if (var1.getExplosionTimer() > 0) {
         this.timerBeforeExplosion = var1.getExplosionTimer() * this.FPS - 1;
      } else if (!var1.canBeRemote()) {
         this.timerBeforeExplosion = 1;
      }

      if (var1.canBePlaced()) {
         this.weapon = var1;
      }

   }

   private void initSprite(HandWeapon var1) {
      if (var1 != null) {
         String var2;
         if (var1.getPlacedSprite() != null && !var1.getPlacedSprite().isEmpty()) {
            var2 = var1.getPlacedSprite();
         } else if (var1.getTex() != null && var1.getTex().getName() != null) {
            var2 = var1.getTex().getName();
         } else {
            var2 = "media/inventory/world/WItem_Sack.png";
         }

         this.sprite = IsoSprite.CreateSprite(IsoWorld.instance.CurrentCell.SpriteManager);
         Texture var3 = this.sprite.LoadFrameExplicit(var2);
         if (var2.startsWith("Item_") && var3 != null) {
            if (var1.getScriptItem() != null && !var1.getScriptItem().ResizeWorldIcon) {
               if (var1.getScriptItem() != null) {
                  this.sprite.def.setScale(var1.getScriptItem().ScaleWorldIcon, var1.getScriptItem().ScaleWorldIcon);
               }
            } else {
               this.sprite.def.scaleAspect((float)var3.getWidthOrig(), (float)var3.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
            }
         }

      }
   }

   public void update() {
      if (this.timerBeforeExplosion > 0) {
         if (this.timerBeforeExplosion / this.FPS + 1 != this.lastBeep) {
            this.lastBeep = this.timerBeforeExplosion / this.FPS + 1;
            if (this.getCountDownSound() != null && !this.getCountDownSound().isEmpty()) {
               SoundManager.instance.PlayWorldSound(this.getCountDownSound(), this.square, 0.0F, 20.0F, 1.0F, false);
            } else if (this.lastBeep == 1) {
               SoundManager.instance.PlayWorldSound("TrapTimerExpired", this.square, 0.0F, 20.0F, 1.0F, false);
            } else {
               SoundManager.instance.PlayWorldSound("TrapTimerLoop", this.square, 0.0F, 20.0F, 1.0F, false);
            }
         }

         --this.timerBeforeExplosion;
         if (this.timerBeforeExplosion == 0) {
            this.triggerExplosion(this.getSensorRange() > 0);
         }
      }

   }

   public void render(float var1, float var2, float var3, ColorInfo var4, boolean var5) {
      if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
         Texture var6 = ((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get(0)).getTexture(this.dir);
         if (var6 != null) {
            if (var6.getName().startsWith("Item_")) {
               float var7 = (float)var6.getWidthOrig() * this.sprite.def.getScaleX() / 2.0F;
               float var8 = (float)var6.getHeightOrig() * this.sprite.def.getScaleY() * 3.0F / 4.0F;
               this.alpha[IsoPlayer.getPlayerIndex()] = 1.0F;
               this.offsetX = 0.0F;
               this.offsetY = 0.0F;
               this.sx = 0;
               this.sprite.render(this, var1 + 0.5F, var2 + 0.5F, var3, this.dir, this.offsetX + var7, this.offsetY + var8, var4);
            } else {
               this.offsetX = (float)(32 * Core.TileScale);
               this.offsetY = (float)(96 * Core.TileScale);
               this.sx = 0;
               super.render(var1, var2, var3, var4, var5);
            }

         }
      }
   }

   public void triggerExplosion(boolean var1) {
      if (!GameClient.bClient) {
         if (this.getExplosionRange() > 0 && !var1) {
            this.square.drawCircleExplosion(this.getExplosionRange(), this, var1);
         }

         if (this.getFireRange() > 0 && !var1) {
            this.square.drawCircleExplosion(this.getFireRange(), this, var1);
         }

         if (this.getSmokeRange() > 0 && !var1) {
            this.square.drawCircleExplosion(this.getSmokeRange(), this, var1);
         }

         if (this.getSensorRange() > 0) {
            this.square.setTrapPositionX(this.square.getX());
            this.square.setTrapPositionY(this.square.getY());
            this.square.setTrapPositionZ(this.square.getZ());
            this.square.drawCircleExplosion(this.getSensorRange(), this, var1);
         }

         if (this.getNoiseRange() > 0 && !var1) {
            this.square.drawCircleExplosion(0, (IsoTrap)this, var1);
         }

         if (!var1 && (this.weapon == null || !this.weapon.canBeReused())) {
            if (GameServer.bServer) {
               GameServer.RemoveItemFromMap(this);
            } else {
               this.removeFromWorld();
               this.removeFromSquare();
            }
         }

      }
   }

   public void load(ByteBuffer var1, int var2) throws IOException {
      super.load(var1, var2);
      this.sensorRange = var1.getInt();
      this.firePower = var1.getInt();
      this.fireRange = var1.getInt();
      this.explosionPower = var1.getInt();
      this.explosionRange = var1.getInt();
      this.smokeRange = var1.getInt();
      this.noiseRange = var1.getInt();
      this.extraDamage = var1.getFloat();
      this.remoteControlID = var1.getInt();
      if (var2 >= 78) {
         this.timerBeforeExplosion = var1.getInt() * this.FPS;
         this.countDownSound = GameWindow.ReadStringUTF(var1);
         this.explosionSound = GameWindow.ReadStringUTF(var1);
         if ("bigExplosion".equals(this.explosionSound)) {
            this.explosionSound = "BigExplosion";
         }

         if ("smallExplosion".equals(this.explosionSound)) {
            this.explosionSound = "SmallExplosion";
         }

         if ("feedback".equals(this.explosionSound)) {
            this.explosionSound = "NoiseTrapExplosion";
         }
      }

      if (var2 >= 82) {
         boolean var3 = var1.get() == 1;
         if (var3) {
            InventoryItem var4 = this.loadItem(var1, var2);
            if (var4 instanceof HandWeapon) {
               this.weapon = (HandWeapon)var4;
               this.initSprite(this.weapon);
            }
         }
      }

   }

   private InventoryItem loadItem(ByteBuffer var1, int var2) throws IOException {
      int var3 = var1.getInt();
      if (var3 <= 0) {
         throw new IOException("invalid item data length " + var3);
      } else {
         int var4 = var1.position();
         String var5 = GameWindow.ReadString(var1);
         byte var6 = var1.get();
         if (var6 < 0) {
            throw new IOException("invalid item save-type " + var6);
         } else {
            if (var5.contains("..")) {
               var5 = var5.replace("..", ".");
            }

            InventoryItem var7 = InventoryItemFactory.CreateItem(var5);
            if (var7 == null) {
               if (var5.length() > 40) {
                  var5 = "<unknown>";
               }

               DebugLog.log("Cannot load \"" + var5 + "\" item. Make sure all mods used in save are installed.");

               while(var1.position() < var4 + var3) {
                  var1.get();
               }

               return null;
            } else if (var7.getSaveType() == var6) {
               var7.load(var1, var2, false);
               if (var1.position() != var4 + var3) {
                  throw new IOException("item load() read more data than save() wrote (" + var5 + ")");
               } else {
                  Item var8 = ScriptManager.instance.FindItem(var5);
                  return var8 != null && var8.getObsolete() ? null : var7;
               }
            } else {
               DebugLog.log("ignoring \"" + var5 + "\" because type changed from " + var6 + " to " + var7.getSaveType());

               while(var1.position() < var4 + var3) {
                  var1.get();
               }

               return null;
            }
         }
      }
   }

   public void save(ByteBuffer var1) throws IOException {
      super.save(var1);
      var1.putInt(this.sensorRange);
      var1.putInt(this.firePower);
      var1.putInt(this.fireRange);
      var1.putInt(this.explosionPower);
      var1.putInt(this.explosionRange);
      var1.putInt(this.smokeRange);
      var1.putInt(this.noiseRange);
      var1.putFloat(this.extraDamage);
      var1.putInt(this.remoteControlID);
      var1.putInt(this.timerBeforeExplosion > 1 ? Math.max(this.timerBeforeExplosion / this.FPS, 1) : 0);
      GameWindow.WriteStringUTF(var1, this.countDownSound);
      GameWindow.WriteStringUTF(var1, this.explosionSound);
      if (this.weapon != null) {
         var1.put((byte)1);
         this.weapon.saveWithSize(var1, false);
      } else {
         var1.put((byte)0);
      }

   }

   public void addToWorld() {
      if (!IsoWorld.instance.getCell().getProcessIsoObjects().contains(this)) {
         IsoWorld.instance.getCell().getProcessIsoObjects().add(this);
      }

   }

   public int getTimerBeforeExplosion() {
      return this.timerBeforeExplosion;
   }

   public void setTimerBeforeExplosion(int var1) {
      this.timerBeforeExplosion = var1;
   }

   public int getSensorRange() {
      return this.sensorRange;
   }

   public void setSensorRange(int var1) {
      this.sensorRange = var1;
   }

   public int getFireRange() {
      return this.fireRange;
   }

   public void setFireRange(int var1) {
      this.fireRange = var1;
   }

   public int getFirePower() {
      return this.firePower;
   }

   public void setFirePower(int var1) {
      this.firePower = var1;
   }

   public int getExplosionPower() {
      return this.explosionPower;
   }

   public void setExplosionPower(int var1) {
      this.explosionPower = var1;
   }

   public int getNoiseRange() {
      return this.noiseRange;
   }

   public void setNoiseRange(int var1) {
      this.noiseRange = var1;
   }

   public int getExplosionRange() {
      return this.explosionRange;
   }

   public void setExplosionRange(int var1) {
      this.explosionRange = var1;
   }

   public int getSmokeRange() {
      return this.smokeRange;
   }

   public void setSmokeRange(int var1) {
      this.smokeRange = var1;
   }

   public float getExtraDamage() {
      return this.extraDamage;
   }

   public void setExtraDamage(float var1) {
      this.extraDamage = var1;
   }

   public String getObjectName() {
      return "IsoTrap";
   }

   public int getRemoteControlID() {
      return this.remoteControlID;
   }

   public void setRemoteControlID(int var1) {
      this.remoteControlID = var1;
   }

   public String getCountDownSound() {
      return this.countDownSound;
   }

   public void setCountDownSound(String var1) {
      this.countDownSound = var1;
   }

   public String getExplosionSound() {
      return this.explosionSound;
   }

   public void setExplosionSound(String var1) {
      this.explosionSound = var1;
   }

   public InventoryItem getItem() {
      return this.weapon;
   }

   public static void triggerRemote(IsoPlayer var0, int var1, int var2) {
      int var3 = (int)var0.getX();
      int var4 = (int)var0.getY();
      int var5 = (int)var0.getZ();
      int var6 = Math.max(var5 - var2 / 2, 0);
      int var7 = Math.min(var5 + var2 / 2, 8);
      IsoCell var8 = IsoWorld.instance.CurrentCell;

      for(int var9 = var6; var9 < var7; ++var9) {
         for(int var10 = var4 - var2; var10 < var4 + var2; ++var10) {
            for(int var11 = var3 - var2; var11 < var3 + var2; ++var11) {
               IsoGridSquare var12 = var8.getGridSquare(var11, var10, var9);
               if (var12 != null) {
                  for(int var13 = var12.getObjects().size() - 1; var13 >= 0; --var13) {
                     IsoObject var14 = (IsoObject)var12.getObjects().get(var13);
                     if (var14 instanceof IsoTrap && ((IsoTrap)var14).getRemoteControlID() == var1) {
                        ((IsoTrap)var14).triggerExplosion(false);
                     }
                  }
               }
            }
         }
      }

   }
}
