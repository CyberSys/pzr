package zombie.core.physics;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.lwjgl.opengl.GL11;
import zombie.GameTime;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.TextureDraw;
import zombie.iso.IsoCamera;
import zombie.iso.IsoChunkMap;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;

public class WorldSimulation {
   public static WorldSimulation instance = new WorldSimulation();
   public static final boolean LEVEL_ZERO_ONLY = true;
   public float offsetX = 0.0F;
   public float offsetY = 0.0F;
   public int maxSubSteps;
   public boolean created = false;
   public long time;
   public HashMap physicsObjectMap = new HashMap();
   static final boolean DEBUG = false;
   BufferedWriter DebugInDataWriter;
   BufferedWriter DebugOutDataWriter;
   private float[] ff = new float[8192];
   private float[] wheelSteer = new float[4];
   private float[] wheelRotation = new float[4];
   private float[] wheelSkidInfo = new float[4];
   private float[] wheelSuspensionLength = new float[4];
   private final float[] tempFloats = new float[23];
   ArrayList collideVehicles = new ArrayList(4);
   protected Transform tempTransform = new Transform();
   protected Quaternionf javaxQuat4f = new Quaternionf();
   private Vector3f tempVector3f = new Vector3f();
   private Vector3f tempVector3f_2 = new Vector3f();

   public void create() {
      if (!this.created) {
         this.offsetX = (float)(IsoWorld.instance.MetaGrid.getMinX() * 300);
         this.offsetY = (float)(IsoWorld.instance.MetaGrid.getMinY() * 300);
         this.time = GameTime.getServerTime();
         IsoChunkMap var1 = IsoWorld.instance.CurrentCell.ChunkMap[0];
         Bullet.initWorld((int)this.offsetX, (int)this.offsetY, var1.getWorldXMin(), var1.getWorldYMin(), IsoChunkMap.ChunkGridWidth);

         for(int var2 = 0; var2 < 4; ++var2) {
            this.wheelSteer[var2] = 0.0F;
            this.wheelRotation[var2] = 0.0F;
            this.wheelSkidInfo[var2] = 0.0F;
            this.wheelSuspensionLength[var2] = 0.0F;
         }

         this.created = true;
      }
   }

   public void destroy() {
      Bullet.destroyWorld();
   }

   private void updatePhysic() {
      Bullet.stepSimulation(GameTime.instance.getRealworldSecondsSinceLastUpdate(), GameServer.bServer ? 5 : 2, 0.016666668F);
      if (GameTime.instance.getRealworldSecondsSinceLastUpdate() < 0.01F) {
         this.time = GameTime.getServerTime();
      } else {
         this.time += (long)(1.0E9F * GameTime.instance.getRealworldSecondsSinceLastUpdate());
      }

   }

   public void update() {
      if (this.created) {
         if (GameServer.bServer) {
            if (GameTime.instance.getRealworldSecondsSinceLastUpdate() > 0.01F) {
               if ((float)(GameTime.getServerTime() - this.time) > 5.0E9F * GameTime.instance.getRealworldSecondsSinceLastUpdate()) {
                  this.time = GameTime.getServerTime();
               }

               while((float)(GameTime.getServerTime() - this.time) > 1.0E9F * GameTime.instance.getRealworldSecondsSinceLastUpdate()) {
                  this.updatePhysic();
               }
            }
         } else {
            this.updatePhysic();
         }

         this.collideVehicles.clear();
         BaseVehicle var1 = null;
         IsoPlayer var2 = IsoPlayer.players[IsoPlayer.getPlayerIndex()];
         if (var2 != null) {
            var1 = var2.getVehicle();
         }

         int var3 = Bullet.getVehicleCount();
         int var4 = 0;

         int var5;
         int var6;
         int var7;
         float var10;
         float var11;
         int var25;
         while(var4 < var3) {
            var5 = Bullet.getVehiclePhysics(var4, this.ff);
            if (var5 <= 0) {
               break;
            }

            var4 += var5;
            var6 = 0;

            for(var7 = 0; var7 < var5; ++var7) {
               boolean var8 = false;
               int var9 = (int)this.ff[var6++];
               var10 = this.ff[var6++];
               var11 = this.ff[var6++];
               float var12 = this.ff[var6++];
               this.tempTransform.origin.set(var10, var11, var12);
               float var13 = this.ff[var6++];
               float var14 = this.ff[var6++];
               float var15 = this.ff[var6++];
               float var16 = this.ff[var6++];
               this.javaxQuat4f.set(var13, var14, var15, var16);
               this.tempTransform.setRotation(this.javaxQuat4f);
               float var17 = this.ff[var6++];
               float var18 = this.ff[var6++];
               float var19 = this.ff[var6++];
               this.tempVector3f.set(var17, var18, var19);
               float var20 = this.ff[var6++];
               float var21 = this.ff[var6++];
               int var22 = (int)this.ff[var6++];

               for(int var23 = 0; var23 < var22; ++var23) {
                  this.wheelSteer[var23] = this.ff[var6++];
                  this.wheelRotation[var23] = this.ff[var6++];
                  this.wheelSkidInfo[var23] = this.ff[var6++];
                  this.wheelSuspensionLength[var23] = this.ff[var6++];
               }

               var25 = (int)(var10 * 100.0F + var11 * 100.0F + var12 * 100.0F + var13 * 100.0F + var14 * 100.0F + var15 * 100.0F + var16 * 100.0F);
               BaseVehicle var29 = this.getVehicleById((short)var9);
               if (var29 != null) {
                  if (var29.VehicleID == var9) {
                     if (var21 > 0.5F) {
                        this.collideVehicles.add(var29);
                        var29.authSimulationHash = var25;
                     }

                     if (GameServer.bServer) {
                        var29.authorizationServerUpdate();
                     }
                  }

                  if (var29 != null) {
                     if (GameClient.bClient && var29.netPlayerAuthorization == 1) {
                        if (var29.authSimulationHash != var25) {
                           var29.authSimulationTime = System.currentTimeMillis();
                           var29.authSimulationHash = var25;
                        }

                        if (System.currentTimeMillis() - var29.authSimulationTime > 1000L) {
                           VehicleManager.instance.sendCollide(var29, var2, false);
                           var29.authSimulationTime = 0L;
                        }
                     }

                     int var24;
                     if (!GameClient.bClient || var29.netPlayerAuthorization != 0 && var29.netPlayerAuthorization != 4) {
                        if (this.compareTransform(this.tempTransform, var29.getPoly().t)) {
                           var29.polyDirty = true;
                        }

                        var29.jniTransform.set(this.tempTransform);
                        var29.jniLinearVelocity.set((Vector3fc)this.tempVector3f);
                        var29.jniSpeed = var20;
                        var29.jniIsCollide = var21 > 0.5F;

                        for(var24 = 0; var24 < 4; ++var24) {
                           var29.wheelInfo[var24].steering = this.wheelSteer[var24];
                           var29.wheelInfo[var24].rotation = this.wheelRotation[var24];
                           var29.wheelInfo[var24].skidInfo = this.wheelSkidInfo[var24];
                           var29.wheelInfo[var24].suspensionLength = this.wheelSuspensionLength[var24];
                        }
                     } else {
                        for(var24 = 0; var24 < 4; ++var24) {
                           var29.wheelInfo[var24].suspensionLength = this.wheelSuspensionLength[var24];
                        }
                     }
                  }
               }
            }
         }

         if (GameClient.bClient && var1 != null) {
            for(var5 = 0; var5 < this.collideVehicles.size(); ++var5) {
               BaseVehicle var27 = (BaseVehicle)this.collideVehicles.get(var5);
               if (var27.DistTo(var1) < 8.0F && var27.netPlayerAuthorization == 0) {
                  VehicleManager.instance.sendCollide(var27, var2, true);
                  var27.authorizationClientForecast(true);
                  var27.authSimulationTime = System.currentTimeMillis();
               }
            }
         }

         var5 = Bullet.getObjectPhysics(this.ff);
         var6 = 0;

         for(var7 = 0; var7 < var5; ++var7) {
            var25 = (int)this.ff[var6++];
            float var26 = this.ff[var6++];
            var10 = this.ff[var6++];
            var11 = this.ff[var6++];
            var26 += this.offsetX;
            var11 += this.offsetY;
            IsoMovingObject var28 = (IsoMovingObject)this.physicsObjectMap.get(var25);
            if (var28 != null) {
               var28.removeFromSquare();
               var28.setX(var26 + 0.18F);
               var28.setY(var11);
               var28.setZ(Math.max(0.0F, var10 / 3.0F / 0.82F));
               var28.setCurrent(IsoWorld.instance.getCell().getGridSquare((double)var28.getX(), (double)var28.getY(), (double)var28.getZ()));
            }
         }

      }
   }

   private BaseVehicle getVehicleById(short var1) {
      if (!GameServer.bServer && !GameClient.bClient) {
         for(int var2 = 0; var2 < IsoWorld.instance.CurrentCell.getVehicles().size(); ++var2) {
            BaseVehicle var3 = (BaseVehicle)IsoWorld.instance.CurrentCell.getVehicles().get(var2);
            if (var3.VehicleID == var1) {
               return var3;
            }
         }

         return null;
      } else {
         return VehicleManager.instance.getVehicleByID(var1);
      }
   }

   private boolean compareTransform(Transform var1, Transform var2) {
      if (!(Math.abs(var1.origin.x - var2.origin.x) > 0.01F) && !(Math.abs(var1.origin.z - var2.origin.z) > 0.01F) && (int)var1.origin.y == (int)var2.origin.y) {
         byte var3 = 2;
         var1.basis.getColumn(var3, this.tempVector3f_2);
         float var4 = this.tempVector3f_2.x;
         float var5 = this.tempVector3f_2.z;
         var2.basis.getColumn(var3, this.tempVector3f_2);
         float var6 = this.tempVector3f_2.x;
         float var7 = this.tempVector3f_2.z;
         return Math.abs(var4 - var6) > 0.001F || Math.abs(var5 - var7) > 0.001F;
      } else {
         return true;
      }
   }

   public void createServerCell(ServerMap.ServerCell var1) {
      this.create();
      Bullet.createServerCell(var1.WX, var1.WY);
   }

   public void removeServerCell(ServerMap.ServerCell var1) {
      Bullet.removeServerCell(var1.WX, var1.WY);
   }

   public int getOwnVehiclePhysics(int var1, ByteBufferWriter var2) {
      if (Bullet.getOwnVehiclePhysics(var1, this.ff) != 0) {
         return -1;
      } else {
         for(int var3 = 0; var3 < 23; ++var3) {
            var2.bb.putFloat(this.ff[var3]);
         }

         return 1;
      }
   }

   public int setOwnVehiclePhysics(int var1, float[] var2) {
      return Bullet.setOwnVehiclePhysics(var1, var2);
   }

   public void activateChunkMap(int var1) {
      this.create();
      IsoChunkMap var2 = IsoWorld.instance.CurrentCell.ChunkMap[var1];
      if (!GameServer.bServer) {
         Bullet.activateChunkMap(var1, var2.getWorldXMin(), var2.getWorldYMin(), IsoChunkMap.ChunkGridWidth);
      }
   }

   public void deactivateChunkMap(int var1) {
      if (this.created) {
         Bullet.deactivateChunkMap(var1);
      }
   }

   public void scrollGroundLeft(int var1) {
      if (this.created) {
         Bullet.scrollChunkMapLeft(var1);
      }
   }

   public void scrollGroundRight(int var1) {
      if (this.created) {
         Bullet.scrollChunkMapRight(var1);
      }
   }

   public void scrollGroundUp(int var1) {
      if (this.created) {
         Bullet.scrollChunkMapUp(var1);
      }
   }

   public void scrollGroundDown(int var1) {
      if (this.created) {
         Bullet.scrollChunkMapDown(var1);
      }
   }

   public static TextureDraw.GenericDrawer getDrawer(int var0) {
      if (WorldSimulation.Drawer3.instance[var0] == null) {
         WorldSimulation.Drawer3.instance[var0] = new WorldSimulation.Drawer3(var0);
      }

      WorldSimulation.Drawer3.instance[var0].init();
      return WorldSimulation.Drawer3.instance[var0];
   }

   public static class Drawer3 extends TextureDraw.GenericDrawer {
      public static WorldSimulation.Drawer3[] instance = new WorldSimulation.Drawer3[4];
      private float camOffX;
      private float camOffY;
      private int drawOffsetX;
      private int drawOffsetY;
      private int playerIndex;
      private float playerX;
      private float playerY;
      private float playerZ;

      public Drawer3(int var1) {
         this.playerIndex = var1;
      }

      public void init() {
         this.camOffX = IsoCamera.RightClickX[IsoPlayer.getPlayerIndex()] + (float)IsoCamera.PLAYER_OFFSET_X;
         this.camOffY = IsoCamera.RightClickY[IsoPlayer.getPlayerIndex()] + (float)IsoCamera.PLAYER_OFFSET_Y;
         this.camOffX += this.XToScreenExact(IsoPlayer.instance.x - (float)((int)IsoPlayer.instance.x), IsoPlayer.instance.y - (float)((int)IsoPlayer.instance.y), 0.0F, 0);
         this.camOffY += this.YToScreenExact(IsoPlayer.instance.x - (float)((int)IsoPlayer.instance.x), IsoPlayer.instance.y - (float)((int)IsoPlayer.instance.y), 0.0F, 0);
         this.drawOffsetX = (int)IsoPlayer.instance.x;
         this.drawOffsetY = (int)IsoPlayer.instance.y;
         this.playerX = IsoPlayer.instance.x;
         this.playerY = IsoPlayer.instance.y;
         this.playerZ = IsoPlayer.instance.z;
      }

      public void render() {
         GL11.glPushAttrib(1048575);
         GL11.glDisable(3553);
         GL11.glDisable(3042);
         GL11.glMatrixMode(5889);
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         GL11.glOrtho(0.0D, (double)Core.getInstance().getOffscreenWidth(this.playerIndex), (double)Core.getInstance().getOffscreenHeight(this.playerIndex), 0.0D, 10000.0D, -10000.0D);
         GL11.glMatrixMode(5888);
         GL11.glPushMatrix();
         GL11.glLoadIdentity();
         int var1 = -this.drawOffsetX;
         int var2 = -this.drawOffsetY;
         byte var3 = 0;
         byte var4 = 0;
         float var5 = 0.0F;
         float var6 = 0.0F;
         GL11.glTranslatef((float)(Core.getInstance().getOffscreenWidth(this.playerIndex) / 2), (float)(Core.getInstance().getOffscreenHeight(this.playerIndex) / 2), 0.0F);
         var5 = this.XToScreenExact((float)var3, (float)var4, this.playerZ, 0);
         var6 = this.YToScreenExact((float)var3, (float)var4, this.playerZ, 0);
         var5 += this.camOffX;
         var6 += this.camOffY;
         GL11.glTranslatef(-var5, -var6, 0.0F);
         var1 = (int)((float)var1 + WorldSimulation.instance.offsetX);
         var2 = (int)((float)var2 + WorldSimulation.instance.offsetY);
         int var7 = 32 * Core.TileScale;
         float var8 = (float)Math.sqrt((double)(var7 * var7 + var7 * var7));
         GL11.glScalef(var8, var8, var8);
         GL11.glRotatef(210.0F, 1.0F, 0.0F, 0.0F);
         GL11.glRotatef(-45.0F, 0.0F, 1.0F, 0.0F);
         Bullet.debugDrawWorld(var1, var2);
         GL11.glBegin(1);
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         GL11.glVertex3d(0.0D, 0.0D, 0.0D);
         GL11.glVertex3d(1.0D, 0.0D, 0.0D);
         GL11.glVertex3d(0.0D, 0.0D, 0.0D);
         GL11.glVertex3d(0.0D, 1.0D, 0.0D);
         GL11.glVertex3d(0.0D, 0.0D, 0.0D);
         GL11.glVertex3d(0.0D, 0.0D, 1.0D);
         GL11.glEnd();
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         GL11.glMatrixMode(5889);
         GL11.glPopMatrix();
         GL11.glMatrixMode(5888);
         GL11.glPopMatrix();
         GL11.glEnable(3042);
         GL11.glEnable(3553);
         GL11.glPopAttrib();
      }

      public float YToScreenExact(float var1, float var2, float var3, int var4) {
         float var5 = IsoUtils.YToScreen(var1, var2, var3, var4);
         return var5;
      }

      public float XToScreenExact(float var1, float var2, float var3, int var4) {
         float var5 = IsoUtils.XToScreen(var1, var2, var3, var4);
         return var5;
      }
   }
}
