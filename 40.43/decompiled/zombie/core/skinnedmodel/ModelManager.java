package zombie.core.skinnedmodel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Stack;
import org.joml.Matrix4f;
import org.lwjgl.opengl.ARBFramebufferObject;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoZombie;
import zombie.characters.SurvivorDesc;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.SpriteRenderer;
import zombie.core.opengl.RenderThread;
import zombie.core.opengl.Shader;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.animation.AnimationTrack;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.skinnedmodel.model.ModelInstance;
import zombie.core.skinnedmodel.model.ModelLoader;
import zombie.core.textures.Texture;
import zombie.core.textures.TextureDraw;
import zombie.core.textures.TextureFBO;
import zombie.core.textures.TextureID;
import zombie.core.utils.OnceEvery;
import zombie.debug.DebugLog;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoDirections;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LightingJNI;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.SkyBox;
import zombie.scripting.objects.VehicleScript;
import zombie.vehicles.BaseVehicle;

public class ModelManager {
   public static ModelManager instance = new ModelManager();
   public HashMap ModelMap = new HashMap();
   public ArrayList ModelSlots = new ArrayList();
   public TextureFBO bitmap;
   public boolean bCreated = false;
   public boolean bDebugEnableModels = true;
   public HashSet Contains = new HashSet();
   public boolean returnContext = true;
   OnceEvery removeModels = new OnceEvery(2.0F);
   private ArrayList torches = new ArrayList();
   private Stack freeLights = new Stack();
   private ArrayList torchLights = new ArrayList();
   public ArrayList ToRemove = new ArrayList();
   public ArrayList ToRemoveVehicles = new ArrayList();
   ArrayList ToResetNextFrame = new ArrayList();
   static Comparator comp = new Comparator() {
      public int compare(IsoLightSource var1, IsoLightSource var2) {
         float var3 = ModelManager.compChar.DistTo(var1.x, var1.y);
         float var4 = ModelManager.compChar.DistTo(var2.x, var2.y);
         if (var3 > var4) {
            return 1;
         } else {
            return var3 < var4 ? -1 : 0;
         }
      }
   };
   static IsoMovingObject compChar = null;
   public Stack lights = new Stack();
   private Stack lightsTemp = new Stack();
   private Vector2 tempVec2 = new Vector2();
   private Vector2 tempVec2_2 = new Vector2();

   public void create() {
      if (!this.bCreated && PerformanceSettings.modelsEnabled) {
         Texture var1 = new Texture(1024, 1024);

         try {
            try {
               TextureID.bUseCompression = false;
               this.bitmap = new TextureFBO(var1, true);
            } finally {
               TextureID.bUseCompression = TextureID.bUseCompressionOption;
            }
         } catch (Exception var6) {
            var6.printStackTrace();
            PerformanceSettings.modelsEnabled = false;
            System.out.println("Models not compatible with gfx card at this time. Reverting to 2D sprites.");
            return;
         }

         DebugLog.log("Loading 3D models");
         this.loadStaticModel("weapons_baseballbatspiked", "Objects_BaseballBatSpiked", "basicEffect");
         this.loadStaticModel("weapons_baseballbat", "Objects_BaseballBat", "basicEffect");
         this.loadStaticModel("weapons_fireaxe", "Objects_Fireaxe", "basicEffect");
         this.loadStaticModel("weapons_fryingpan", "Objects_FryingPan", "basicEffect");
         this.loadStaticModel("weapons_golfclub", "Objects_GolfClub", "basicEffect");
         this.loadStaticModel("weapons_hammer", "Objects_Hammer", "basicEffect");
         this.loadStaticModel("weapons_handgun", "Objects_Handgun", "basicEffect");
         this.loadStaticModel("weapons_knife", "Objects_Knife", "basicEffect");
         this.loadStaticModel("weapons_poolcue", "Objects_PoolCue", "basicEffect");
         this.loadStaticModel("weapons_rollingpin", "Objects_RollingPin", "basicEffect");
         this.loadStaticModel("weapons_shotgun", "Objects_Shotgun", "basicEffect");
         this.loadStaticModel("weapons_shotgunsawn", "Objects_ShotgunSawn", "basicEffect");
         this.loadStaticModel("weapons_shovel", "Objects_Shovel", "basicEffect");
         this.loadStaticModel("weapons_sledgehammer", "Objects_Sledgehammer", "basicEffect");
         this.loadStaticModel("weapons_woodplank", "Objects_WoodPlank", "basicEffect");
         this.loadModel("male", (String)null);
         this.loadModel("kate", (String)null);
         this.loadModel("Shirt", (String)null);
         this.loadModel("Trousers", (String)null);
         this.loadModel("vest", (String)null);
         this.loadModel("f_vest", (String)null);
         this.loadModel("f_trousers", (String)null);
         this.loadModel("f_skirt", (String)null);
         this.loadModel("f_blouse", (String)null);
         this.loadModel("beard_only", (String)null);
         this.loadModel("beard_moustache", (String)null);
         this.loadModel("beard_goatee", (String)null);
         this.loadModel("beard_full", (String)null);
         this.loadModel("beard_chops", (String)null);
         this.loadModel("f_hair_overeye", (String)null);
         this.loadModel("f_hair_long2", (String)null);
         this.loadModel("f_hair_long", (String)null);
         this.loadModel("f_hair_kate", (String)null);
         this.loadModel("f_hair_bob", (String)null);
         this.loadModel("f_hair_long2", (String)null);
         this.loadModel("f_hair_long2", (String)null);
         this.loadModel("hair_baldspot", (String)null);
         this.loadModel("hair_messy", (String)null);
         this.loadModel("hair_picard", (String)null);
         this.loadModel("hair_recede", (String)null);
         this.loadModel("hair_short", (String)null);
         this.loadStaticModel("Vehicles_PickUpVanLights", "Vehicle_PickUpTruck_Police", "vehicle");
         this.loadStaticModel("Vehicles_PickUpVan", "Vehicle_PickUpTruck_Black", "vehicle");
         this.loadStaticModel("Vehicles_PickUpTruckLights", "Vehicle_PickUpTruck_Police", "vehicle");
         this.loadStaticModel("Vehicles_PickUpTruck", "Vehicle_PickUpTruck_Black", "vehicle");
         this.loadStaticModel("Vehicles_CarNormal", "Vehicle_CarNormal_Black", "vehicle");
         this.loadStaticModel("Vehicles_CarStationWagon", "Vehicle_CarStationWagon_Black", "vehicle");
         this.loadStaticModel("Vehicles_CarLights", "Vehicle_CarLights_Police", "vehicle");
         this.loadStaticModel("Vehicles_Van", "Vehicle_Van_Black", "vehicle");
         this.loadStaticModel("Vehicles_VanSeats", "Vehicle_VanSeats_Black", "vehicle");
         this.loadStaticModel("Vehicles_Ambulance", "Vehicle_Van_Ambulance", "vehicle");
         this.loadStaticModel("Vehicles_VanRadio", "Vehicle_Van_Radio", "vehicle");
         this.loadStaticModel("Vehicle_StepVan", "Vehicle_StepVan", "vehicle");
         this.loadStaticModel("Vehicles_SportsCar", "Vehicle_SportsCar_Black", "vehicle");
         this.loadStaticModel("Vehicles_SmallCar", "Vehicle_SmallCar_Black", "vehicle");
         this.loadStaticModel("Vehicles_CarTaxi", "Vehicle_Taxi_Yellow", "vehicle");
         this.loadStaticModel("Vehicles_ModernCar", "Vehicle_CarModern_Black", "vehicle");
         this.loadStaticModel("Vehicles_ModernCar02", "Vehicle_CarModern2_Black", "vehicle");
         this.loadStaticModel("Vehicles_SUV", "Vehicle_SUV_Black", "vehicle");
         this.loadStaticModel("Vehicles_SmallCar02", "Vehicle_SmallCar02_Black", "vehicle");
         this.loadStaticModel("Vehicles_OffRoad", "Vehicles_OffRoad", "vehicle");
         this.loadStaticModel("Vehicles_LuxuryCar", "Vehicles_LuxuryCar", "vehicle");
         this.loadStaticModel("Vehicles_PickupBurnt", "Vehicles_PickUpBurnt", "vehicle");
         this.loadStaticModel("Vehicles_ModernCarBurnt", "Vehicles_ModernCar_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_ModernCar02_Burnt", "Vehicles_ModernCar02_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_SUV_Burnt", "Vehicles_SUV_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_CarNormal_Burnt", "Vehicles_NormalCar_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_NormalCar_BurntPolice", "Vehicles_NormalCar_BurntPolice", "vehicle");
         this.loadStaticModel("Vehicles_Ambulance_Burnt", "Vehicles_Ambulance_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_VanRadio_Burnt", "Vehicles_VanRadio_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_VanSeats_Burnt", "Vehicles_VanSeats_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_Van_Burnt", "Vehicles_Van_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_SmallCar_Burnt", "Vehicles_SmallCar_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_SmallCar02Burnt", "Vehicles_SmallCar02_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_SportsCar_Burnt", "Vehicles_SportsCar_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_OffRoadBurnt", "Vehicles_OffRoad_Burnt", "vehicle");
         this.loadStaticModel("Vehicles_PickUpVanLightsBurnt", "Vehicles_PickUpVanLightsBurnt", "vehicle");
         this.loadStaticModel("Vehicles_PickUpVanBurnt", "Vehicles_PickUpVanBurnt", "vehicle");
         this.loadStaticModel("Vehicles_LuxuryCarBurnt", "Vehicles_LuxuryCarBurnt", "vehicle");
         this.loadStaticModel("Vehicles_Wheel", "Vehicles/vehicle_wheel", "vehiclewheel");
         this.loadStaticModel("Vehicles_Wheel02", "Vehicles/vehicle_wheel02", "vehiclewheel");
         this.loadStaticModel("Vehicles_Wheel03", "Vehicles/vehicle_wheel03", "vehiclewheel");
         this.loadStaticModel("Vehicles_Wheel04", "Vehicles/vehicle_wheel04", "vehiclewheel");
         this.bCreated = true;
      }
   }

   public void DoRender(int var1) {
      if (var1 >= 0 && var1 < this.ModelSlots.size()) {
         if (!((ModelManager.ModelSlot)this.ModelSlots.get(var1)).active) {
            GL11.glPushClientAttrib(-1);
            GL11.glPushAttrib(1048575);
            instance.bitmap.startDrawing(true, false);
            GL11.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);
            GL11.glClear(16384);
            GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
            instance.bitmap.endDrawing();
            GL11.glPopAttrib();
            GL11.glPopClientAttrib();
         } else if (((ModelManager.ModelSlot)this.ModelSlots.get(var1)).character == null) {
            GL11.glPushClientAttrib(-1);
            GL11.glPushAttrib(1048575);
            GL11.glEnable(2929);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3008);
            GL11.glAlphaFunc(516, 0.0F);
            GL11.glClear(256);
            ModelManager.ModelSlot var2 = (ModelManager.ModelSlot)this.ModelSlots.get(var1);
            int var6 = SpriteRenderer.instance.states[2].playerIndex;
            var2.Start();
            var2.Render();
            var2.End();
            GL11.glPopAttrib();
            GL11.glPopClientAttrib();
            GL11.glEnable(3553);
            SpriteRenderer.ringBuffer.restoreVBOs = true;
            GL11.glEnable(2929);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3008);
            GL11.glAlphaFunc(516, 0.0F);
         } else {
            synchronized(((ModelManager.ModelSlot)this.ModelSlots.get(var1)).model.AnimPlayer) {
               GL11.glPushClientAttrib(-1);
               GL11.glPushAttrib(1048575);
               GL11.glEnable(2929);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               GL11.glEnable(3008);
               GL11.glAlphaFunc(516, 0.0F);
               ModelManager.ModelSlot var3 = (ModelManager.ModelSlot)this.ModelSlots.get(var1);
               var3.Start();
               var3.Render();
               var3.End();
               GL11.glPopAttrib();
               GL11.glPopClientAttrib();
               GL11.glEnable(3553);
               SpriteRenderer.ringBuffer.restoreVBOs = true;
               GL11.glEnable(2929);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               GL11.glEnable(3008);
               GL11.glAlphaFunc(516, 0.0F);
            }
         }
      }
   }

   public void RenderSkyBox(TextureDraw var1, int var2, int var3, int var4, int var5) {
      switch(var4) {
      case 1:
         GL30.glBindFramebuffer(36160, var5);
         break;
      case 2:
         ARBFramebufferObject.glBindFramebuffer(36160, var5);
         break;
      case 3:
         EXTFramebufferObject.glBindFramebufferEXT(36160, var5);
      }

      GL11.glPushClientAttrib(-1);
      GL11.glPushAttrib(1048575);
      GL11.glMatrixMode(5889);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      GL11.glOrtho(0.0D, 1.0D, 1.0D, 0.0D, -1.0D, 1.0D);
      GL11.glViewport(0, 0, 512, 512);
      GL11.glMatrixMode(5888);
      GL11.glPushMatrix();
      GL11.glLoadIdentity();
      ARBShaderObjects.glUseProgramObjectARB(var2);
      if (Shader.ShaderMap.containsKey(var2)) {
         ((Shader)Shader.ShaderMap.get(var2)).updateSkyBoxParams(var1);
      }

      GL11.glColor4f(0.13F, 0.96F, 0.13F, 1.0F);
      GL11.glBegin(7);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 1.0F);
      GL11.glVertex2f(0.0F, 1.0F);
      GL11.glTexCoord2f(1.0F, 1.0F);
      GL11.glVertex2f(1.0F, 1.0F);
      GL11.glTexCoord2f(1.0F, 0.0F);
      GL11.glVertex2f(1.0F, 0.0F);
      GL11.glTexCoord2f(0.0F, 0.0F);
      GL11.glEnd();
      GL11.glFlush();
      ARBShaderObjects.glUseProgramObjectARB(0);
      GL11.glMatrixMode(5888);
      GL11.glPopMatrix();
      GL11.glMatrixMode(5889);
      GL11.glPopMatrix();
      GL11.glPopAttrib();
      GL11.glPopClientAttrib();
      GL11.glViewport(0, 0, SpriteRenderer.instance.states[2].offscreenWidth[var3], SpriteRenderer.instance.states[2].offscreenHeight[var3]);
      switch(var4) {
      case 1:
         GL30.glBindFramebuffer(36160, 0);
         break;
      case 2:
         ARBFramebufferObject.glBindFramebuffer(36160, 0);
         break;
      case 3:
         EXTFramebufferObject.glBindFramebufferEXT(36160, 0);
      }

      SkyBox.getInstance().swapTextureFBO();
   }

   public void DoneRendering(int var1) {
      if (var1 >= 0 && var1 < this.ModelSlots.size()) {
         ModelManager.ModelSlot var2 = (ModelManager.ModelSlot)this.ModelSlots.get(var1);

         assert var2.renderRefCount > 0;

         --var2.renderRefCount;
      }

   }

   public void Reset(IsoGameCharacter var1) {
      if (var1.legsSprite != null && var1.legsSprite.modelSlot != null) {
         RenderThread.borrowContext();

         while(SpriteRenderer.instance.DoingRender) {
            try {
               Thread.sleep(2L);
            } catch (InterruptedException var3) {
               var3.printStackTrace();
            }
         }

         this.DoCharacterModelParts(var1, var1.legsSprite.modelSlot);
         var1.legsSprite.modelSlot.UpdateLights();
         RenderThread.returnContext();
      }
   }

   public void Add(IsoGameCharacter var1) {
      if (this.bCreated && PerformanceSettings.modelsEnabled && PerformanceSettings.support3D) {
         RenderThread.borrowContext();
         this.Contains.add(var1);
         ModelManager.ModelSlot var2 = this.getSlot(var1);
         if (!this.ModelSlots.contains(var2)) {
            this.ModelSlots.add(var2);
         }

         if (((IsoDirectionFrame)var1.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
            String var3 = null;
            var3 = ((IsoDirectionFrame)var1.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
            var3 = var3.substring(0, var3.indexOf("_"));
            String var4 = var1.legsSprite.name;

            assert !(var1 instanceof IsoZombie) || ((IsoZombie)var1).SpriteName.equals(var1.legsSprite.name);

            var2.model = this.newInstance((Model)this.ModelMap.get(var3.toLowerCase().contains("kate") ? "kate" : "male"), var1, (AnimationPlayer)null);
            var2.model.LoadTexture(var4 + "_Body");
         }

         this.DoCharacterModelParts(var1, var2);
         var2.active = true;
         var1.legsSprite.modelSlot = var2;
         var2.UpdateLights();
         RenderThread.returnContext();
      }
   }

   private void DoCharacterModelParts(IsoGameCharacter var1, ModelManager.ModelSlot var2) {
      var2.sub.clear();
      String var3 = null;
      if (((IsoDirectionFrame)var1.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
         var3 = ((IsoDirectionFrame)var1.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
         var3 = var3.substring(0, var3.indexOf("_"));
         var2.model.LoadTexture(var1.legsSprite.name + "_Body");
         var2.model.tintR = var1.legsSprite.getTintMod().r;
         var2.model.tintG = var1.legsSprite.getTintMod().g;
         var2.model.tintB = var1.legsSprite.getTintMod().b;
      }

      if (var1.bottomsSprite != null && !var1.bottomsSprite.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)var1.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
         String var4 = ((IsoDirectionFrame)var1.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
         var4 = var4.replace(var3 + "_", "");
         if (var4.contains("F_")) {
            var4 = var4.substring(0, var4.indexOf("_", 2));
         } else {
            var4 = var4.substring(0, var4.indexOf("_"));
         }

         ModelInstance var5 = this.newInstance((Model)this.ModelMap.get(var4.toLowerCase()), var1, var2.model.AnimPlayer);
         var5.LoadTexture(var4 + "_White");
         var5.tintR = var1.bottomsSprite.getTintMod().r;
         var5.tintG = var1.bottomsSprite.getTintMod().g;
         var5.tintB = var1.bottomsSprite.getTintMod().b;
         var2.sub.add(var5);
      }

      if (var1.topSprite != null && !var1.topSprite.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)var1.topSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
         String var12 = ((IsoDirectionFrame)var1.topSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
         var12 = var12.replace(var3 + "_", "");
         if (var12.contains("F_")) {
            var12 = var12.substring(0, var12.indexOf("_", 2));
         } else {
            var12 = var12.substring(0, var12.indexOf("_"));
         }

         ModelInstance var6 = this.newInstance((Model)this.ModelMap.get(var12.toLowerCase()), var1, var2.model.AnimPlayer);
         var6.LoadTexture(var12 + "_White");
         var6.tintR = var1.topSprite.getTintMod().r;
         var6.tintG = var1.topSprite.getTintMod().g;
         var6.tintB = var1.topSprite.getTintMod().b;
         var2.sub.add(var6);
      }

      if (var1.getDescriptor() != null && var1.getDescriptor().getHair() != null && !var1.getDescriptor().getHair().isEmpty()) {
         String var13 = var1.getDescriptor().getHair();
         var13 = var13.replace("_White", "");
         if (var13.equals("F_Hair")) {
            var13 = "F_Hair_kate";
         }

         if (this.ModelMap.get(var13.toLowerCase()) != null) {
            ModelInstance var7 = this.newInstance((Model)this.ModelMap.get(var13.toLowerCase()), var1, var2.model.AnimPlayer);
            var7.LoadTexture(var13.replace("_kate", "") + "_White");
            var7.tintR = var1.hairSprite.getTintMod().r;
            var7.tintG = var1.hairSprite.getTintMod().g;
            var7.tintB = var1.hairSprite.getTintMod().b;
            var2.sub.add(var7);
         }
      }

      try {
         for(int var14 = 0; var14 < var1.extraSprites.size(); ++var14) {
            IsoSprite var8 = (IsoSprite)var1.extraSprites.get(var14);
            if (var8 != null && var8.CurrentAnim != null && !var8.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)var8.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
               String var9 = ((IsoDirectionFrame)var8.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
               var9 = var9.replace(var3 + "_", "");
               var9 = var9.substring(0, var9.lastIndexOf("_"));
               var9 = var9.substring(0, var9.lastIndexOf("_"));
               var9 = var9.substring(0, var9.lastIndexOf("_"));
               var9 = var9.substring(0, var9.lastIndexOf("_"));
               var9 = var9.replace("_White", "");
               if (var9.equals("F_Hair")) {
                  var9 = "F_Hair_kate";
               }

               if (this.ModelMap.containsKey(var9.toLowerCase())) {
                  ModelInstance var10 = this.newInstance((Model)this.ModelMap.get(var9.toLowerCase()), var1, var2.model.AnimPlayer);
                  var10.LoadTexture(var9.replace("_kate", "") + "_White");
                  var10.tintR = var8.getTintMod().r;
                  var10.tintG = var8.getTintMod().g;
                  var10.tintB = var8.getTintMod().b;
                  var2.sub.add(var10);
               }
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

      if (var1.getPrimaryHandItem() instanceof HandWeapon) {
         HandWeapon var15 = (HandWeapon)var1.getPrimaryHandItem();
         String var16 = var15.getWeaponSprite();
         if (var16 != null && this.ModelMap.containsKey("weapons_" + var16.toLowerCase())) {
            ModelInstance var17 = this.newInstance((Model)this.ModelMap.get("weapons_" + var16.toLowerCase()), var1, var2.model.AnimPlayer);
            var2.sub.add(var17);
         }
      }

   }

   public void update() {
      int var1;
      for(var1 = 0; var1 < this.ToResetNextFrame.size(); ++var1) {
         this.Reset((IsoGameCharacter)this.ToResetNextFrame.get(var1));
      }

      this.ToResetNextFrame.clear();
      if (this.removeModels.Check()) {
         for(var1 = 0; var1 < this.ToRemove.size(); ++var1) {
            if (this.DoRemove((IsoGameCharacter)this.ToRemove.get(var1), false)) {
               this.ToRemove.remove(var1--);
            }
         }

         for(var1 = 0; var1 < this.ToRemoveVehicles.size(); ++var1) {
            ModelManager.ModelSlot var2 = (ModelManager.ModelSlot)this.ToRemoveVehicles.get(var1);
            var2.active = false;

            for(int var3 = 0; var3 < var2.sub.size(); ++var3) {
               ((ModelInstance)var2.sub.get(var3)).object = null;
            }

            var2.model.object = null;
         }

         this.ToRemoveVehicles.clear();
      }

      this.lights.clear();
      if (IsoWorld.instance != null && IsoWorld.instance.CurrentCell != null) {
         this.lights.addAll(IsoWorld.instance.CurrentCell.getLamppostPositions());
      }

      this.freeLights.addAll(this.torchLights);
      this.torchLights.clear();
      this.torches.clear();
      LightingJNI.getTorches(this.torches);

      for(var1 = 0; var1 < this.torches.size(); ++var1) {
         IsoGameCharacter.TorchInfo var4 = (IsoGameCharacter.TorchInfo)this.torches.get(var1);
         IsoLightSource var5 = this.freeLights.isEmpty() ? new IsoLightSource(0, 0, 0, 1.0F, 1.0F, 1.0F, 1) : (IsoLightSource)this.freeLights.pop();
         var5.x = (int)var4.x;
         var5.y = (int)var4.y;
         var5.z = (int)var4.z;
         var5.r = 1.0F;
         var5.g = 0.85F;
         var5.b = 0.6F;
         var5.radius = (int)Math.ceil((double)var4.dist);
         this.torchLights.add(var5);
      }

   }

   private ModelManager.ModelSlot getSlot(IsoGameCharacter var1) {
      for(int var2 = 0; var2 < this.ModelSlots.size(); ++var2) {
         ModelManager.ModelSlot var3 = (ModelManager.ModelSlot)this.ModelSlots.get(var2);
         if (var3.character == var1) {
            return var3;
         }
      }

      return new ModelManager.ModelSlot(this.ModelSlots.size(), this.newInstance(var1.isFemale() ? (Model)this.ModelMap.get("kate") : (Model)this.ModelMap.get("male"), var1, (AnimationPlayer)null), var1);
   }

   private boolean DoRemove(IsoGameCharacter var1, boolean var2) {
      if (this.Contains.contains(var1)) {
         for(int var3 = 0; var3 < this.ModelSlots.size(); ++var3) {
            ModelManager.ModelSlot var4 = (ModelManager.ModelSlot)this.ModelSlots.get(var3);
            if (var4.character == var1 && (var2 || var4.renderRefCount <= 0)) {
               this.Contains.remove(var1);
               var4.character = null;
               var4.active = false;
               var4.bRemove = false;
               var4.renderRefCount = 0;
               var1.legsSprite.modelSlot = null;
               return true;
            }
         }
      }

      return false;
   }

   public void Remove(IsoGameCharacter var1) {
      if (var1.hasActiveModel()) {
         if (this.Contains.contains(var1) && !this.ToRemove.contains(var1)) {
            var1.legsSprite.modelSlot.bRemove = true;
            this.ToRemove.add(var1);
         }

      }
   }

   public void Remove(BaseVehicle var1) {
      if (var1.sprite != null && var1.sprite.modelSlot != null) {
         ModelManager.ModelSlot var2 = var1.sprite.modelSlot;
         if (!this.ToRemoveVehicles.contains(var2)) {
            this.ToRemoveVehicles.add(var2);
         }

         var1.sprite.modelSlot = null;
      }

   }

   public void ResetNextFrame(IsoGameCharacter var1) {
      this.ToResetNextFrame.add(var1);
   }

   public void Reset() {
      RenderThread.borrowContext();

      for(int var1 = 0; var1 < this.ToRemove.size(); ++var1) {
         this.DoRemove((IsoGameCharacter)this.ToRemove.get(var1), true);
      }

      this.ToRemove.clear();

      try {
         if (!this.Contains.isEmpty()) {
            IsoGameCharacter[] var7 = (IsoGameCharacter[])this.Contains.toArray(new IsoGameCharacter[this.Contains.size()]);
            IsoGameCharacter[] var2 = var7;
            int var3 = var7.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               IsoGameCharacter var5 = var2[var4];
               this.DoRemove(var5, true);
            }
         }

         this.ModelSlots.clear();
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      RenderThread.returnContext();
      this.lights.clear();
      this.lightsTemp.clear();
      compChar = null;
   }

   public void getClosestThreeLights(IsoMovingObject var1, IsoLightSource[] var2) {
      compChar = var1;
      this.lightsTemp.clear();

      int var3;
      for(var3 = 0; var3 < this.lights.size(); ++var3) {
         IsoLightSource var4 = (IsoLightSource)this.lights.get(var3);
         if (var4.bActive && (var4.localToBuilding == null || var1.getCurrentBuilding() == var4.localToBuilding) && !(IsoUtils.DistanceTo(var1.x, var1.y, (float)var4.x + 0.5F, (float)var4.y + 0.5F) >= (float)var4.radius) && LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)compChar.x, (int)compChar.y, (int)compChar.z, var4.x, var4.y, var4.z, false) != LosUtil.TestResults.Blocked) {
            this.lightsTemp.add(var4);
         }
      }

      if (var1 instanceof BaseVehicle) {
         for(var3 = 0; var3 < this.torches.size(); ++var3) {
            IsoGameCharacter.TorchInfo var8 = (IsoGameCharacter.TorchInfo)this.torches.get(var3);
            if (!(IsoUtils.DistanceTo(var1.x, var1.y, var8.x, var8.y) >= var8.dist) && LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)compChar.x, (int)compChar.y, (int)compChar.z, (int)var8.x, (int)var8.y, (int)var8.z, false) != LosUtil.TestResults.Blocked) {
               if (var8.bCone) {
                  Vector2 var5 = this.tempVec2;
                  var5.x = var8.x - var1.x;
                  var5.y = var8.y - var1.y;
                  var5.normalize();
                  Vector2 var6 = this.tempVec2_2;
                  var6.x = var8.angleX;
                  var6.y = var8.angleY;
                  var6.normalize();
                  float var7 = var5.dot(var6);
                  if (var7 >= -0.92F) {
                     continue;
                  }
               }

               this.lightsTemp.add(this.torchLights.get(var3));
            }
         }
      }

      Collections.sort(this.lightsTemp, comp);
      var2[0] = var2[1] = var2[2] = null;
      if (this.lightsTemp.size() > 0) {
         var2[0] = (IsoLightSource)this.lightsTemp.get(0);
      }

      if (this.lightsTemp.size() > 1) {
         var2[1] = (IsoLightSource)this.lightsTemp.get(1);
      }

      if (this.lightsTemp.size() > 2) {
         var2[2] = (IsoLightSource)this.lightsTemp.get(2);
      }

   }

   public void Add(BaseVehicle var1) {
      if (this.bCreated && PerformanceSettings.modelsEnabled && PerformanceSettings.support3D) {
         if (var1 != null && var1.getScript() != null) {
            VehicleScript var2 = var1.getScript();
            RenderThread.borrowContext();
            ModelInstance var3 = new ModelInstance((Model)this.ModelMap.get(var2.getModel().file), (IsoGameCharacter)null, (AnimationPlayer)null, true, false);
            var1.getSkin();
            if (var2.textureDataSkins != null && var1.getSkinIndex() >= 0 && var1.getSkinIndex() < var2.textureDataSkins.length) {
               var3.tex = var2.textureDataSkins[var1.getSkinIndex()];
            }

            var3.textureMask = var2.textureDataMask;
            var3.textureDamage1Overlay = var2.textureDataDamage1Overlay;
            var3.textureDamage1Shell = var2.textureDataDamage1Shell;
            var3.textureDamage2Overlay = var2.textureDataDamage2Overlay;
            var3.textureDamage2Shell = var2.textureDataDamage2Shell;
            var3.textureLights = var2.textureDataLights;
            var3.textureRust = var2.textureDataRust;
            if (var3.tex != null) {
               var3.tex.bindAlways = true;
            } else {
               DebugLog.log("ERROR: ModelManager.Add() texture '" + var1.getSkin() + "' not found");
            }

            ModelManager.ModelSlot var4 = null;

            int var5;
            for(var5 = 0; var5 < this.ModelSlots.size(); ++var5) {
               if (!((ModelManager.ModelSlot)this.ModelSlots.get(var5)).active && ((ModelManager.ModelSlot)this.ModelSlots.get(var5)).character == null) {
                  var4 = (ModelManager.ModelSlot)this.ModelSlots.get(var5);
                  break;
               }
            }

            if (var4 == null) {
               var4 = new ModelManager.ModelSlot(this.ModelSlots.size(), var3, (IsoGameCharacter)null);
               this.ModelSlots.add(var4);
            }

            var4.model = var3;
            var3.object = var1;
            var4.sub.clear();

            for(var5 = 0; var5 < var1.models.size(); ++var5) {
               BaseVehicle.ModelInfo var6 = (BaseVehicle.ModelInfo)var1.models.get(var5);
               var3 = new ModelInstance((Model)this.ModelMap.get(var6.scriptModel.file), (IsoGameCharacter)null, (AnimationPlayer)null, false, true);
               var3.object = var1;
               var4.sub.add(var3);
            }

            var4.active = true;
            var1.sprite.modelSlot = var4;
            RenderThread.returnContext();
         }
      }
   }

   private Model loadStaticModel(String var1, String var2, String var3) {
      String var4 = var3;
      if (var3.equals("vehicle") && !Core.getInstance().getPerfReflectionsOnLoad()) {
         var4 = var3 + "_noreflect";
      }

      try {
         Model var5 = ModelLoader.instance.Load("media/models/" + var1 + ".txt", var2, var4, true);
         this.ModelMap.put(var1, var5);
         var5.Name = var1;
         return var5;
      } catch (IOException var6) {
         var6.printStackTrace();
         return null;
      }
   }

   private Model loadModel(String var1, String var2) {
      try {
         Model var3 = ModelLoader.instance.Load("media/models/" + var1 + ".txt", var2, "basicEffect", false);
         this.ModelMap.put(var1.toLowerCase(), var3);
         var3.Name = var1.toLowerCase();
         return var3;
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public ModelInstance newInstance(Model var1, IsoGameCharacter var2, AnimationPlayer var3) {
      if (var1 == null) {
         boolean var4 = false;
      }

      ModelInstance var6 = new ModelInstance(var1, var2, var3, false, false);
      var6.UpdateDir();
      if (var6.AnimPlayer != null) {
         var6.AnimPlayer.angle = var6.AnimPlayer.targetAngle;
      }

      AnimationTrack var5 = var6.Play(var2.legsSprite.CurrentAnim.name, var2.def.Looped, var2.def.Finished, var2);
      if (var5 != null) {
         var5.syncToFrame(var2.def, var2.legsSprite.CurrentAnim);
         var6.AnimPlayer.Update(0.0F, true, (Matrix4f)null);
      }

      if (var1.Name.equals("male") || var1.Name.equals("kate")) {
         var6.updateLights = true;
      }

      return var6;
   }

   public static class ModelSlot {
      public int ID;
      public ModelInstance model;
      public IsoGameCharacter character;
      public ArrayList sub = new ArrayList();
      public boolean active;
      public boolean bRemove;
      public short renderRefCount;

      public ModelSlot(int var1, ModelInstance var2, IsoGameCharacter var3) {
         this.ID = var1;
         this.model = var2;
         this.character = var3;
      }

      public void Start() {
         GL11.glEnable(2929);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         GL11.glEnable(3008);
         GL11.glAlphaFunc(516, 0.0F);
         GL11.glDisable(3089);
         ModelManager.instance.bitmap.startDrawing(true, true);
         ModelCamera.instance.w = ModelManager.instance.bitmap.getTexture().getWidth();
         ModelCamera.instance.h = ModelManager.instance.bitmap.getTexture().getHeight();
      }

      public void Render() {
         this.model.Draw();

         for(int var1 = 0; var1 < this.sub.size(); ++var1) {
            ((ModelInstance)this.sub.get(var1)).Draw();
         }

      }

      public void End() {
         ModelManager.instance.bitmap.endDrawing();
      }

      public void Play(String var1, boolean var2, boolean var3, IsoGameCharacter var4) {
         this.model.Play(var1, var2, var3, var4);

         for(int var5 = 0; var5 < this.sub.size(); ++var5) {
            ((ModelInstance)this.sub.get(var5)).Play(var1, var2, var3, var4);
         }

      }

      public void Update() {
         float var1 = this.character.def.AnimFrameIncrease / (float)this.character.legsSprite.CurrentAnim.Frames.size();
         if (this.character.getLastSquare() != this.character.getCurrentSquare()) {
            this.UpdateLights();
         }

         synchronized(this.model.AnimPlayer) {
            this.model.UpdateDir();
            this.model.Update(var1);

            for(int var3 = 0; var3 < this.sub.size(); ++var3) {
               ((ModelInstance)this.sub.get(var3)).lights = this.model.lights;
               ((ModelInstance)this.sub.get(var3)).AnimPlayer = this.model.AnimPlayer;
            }

         }
      }

      public void UpdateLights() {
         if (this.model != null && this.model.object != null) {
            synchronized(this.model.lights) {
               int var2;
               if (this.character instanceof DeadBodyAtlas.AtlasCharacter) {
                  this.model.lights[0] = this.model.lights[1] = this.model.lights[2] = null;

                  for(var2 = 0; var2 < this.sub.size(); ++var2) {
                     ((ModelInstance)this.sub.get(var2)).lights = this.model.lights;
                  }

               } else {
                  ModelManager.instance.getClosestThreeLights(this.model.object, this.model.lights);

                  for(var2 = 0; var2 < this.sub.size(); ++var2) {
                     ((ModelInstance)this.sub.get(var2)).lights = this.model.lights;
                  }

               }
            }
         }
      }

      public void ResetToFrameOne() {
         synchronized(this.model.AnimPlayer) {
            this.model.AnimPlayer.ResetToFrameOne();
            Iterator var2 = this.sub.iterator();

            while(var2.hasNext()) {
               ModelInstance var3 = (ModelInstance)var2.next();
               var3.AnimPlayer.ResetToFrameOne();
            }

         }
      }

      public void DisableBlendingFrom(String var1) {
         AnimationTrack var2 = this.model.AnimPlayer.getAnimTrack(var1);
         if (var2 != null) {
            var2.bFinished = true;
         }

      }
   }

   public static class AnimRequest {
      public float angle;
      public String anim;
      public SurvivorDesc desc;
   }
}
