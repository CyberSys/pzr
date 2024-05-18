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
	static Comparator comp = new Comparator(){
    
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
			Texture texture = new Texture(1024, 1024);
			try {
				try {
					TextureID.bUseCompression = false;
					this.bitmap = new TextureFBO(texture, true);
				} finally {
					TextureID.bUseCompression = TextureID.bUseCompressionOption;
				}
			} catch (Exception exception) {
				exception.printStackTrace();
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

	public void DoRender(int int1) {
		if (int1 >= 0 && int1 < this.ModelSlots.size()) {
			if (!((ModelManager.ModelSlot)this.ModelSlots.get(int1)).active) {
				GL11.glPushClientAttrib(-1);
				GL11.glPushAttrib(1048575);
				instance.bitmap.startDrawing(true, false);
				GL11.glClearColor(1.0F, 0.0F, 0.0F, 1.0F);
				GL11.glClear(16384);
				GL11.glClearColor(0.0F, 0.0F, 0.0F, 1.0F);
				instance.bitmap.endDrawing();
				GL11.glPopAttrib();
				GL11.glPopClientAttrib();
			} else if (((ModelManager.ModelSlot)this.ModelSlots.get(int1)).character == null) {
				GL11.glPushClientAttrib(-1);
				GL11.glPushAttrib(1048575);
				GL11.glEnable(2929);
				GL11.glEnable(3042);
				GL11.glBlendFunc(770, 771);
				GL11.glEnable(3008);
				GL11.glAlphaFunc(516, 0.0F);
				GL11.glClear(256);
				ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.ModelSlots.get(int1);
				int int2 = SpriteRenderer.instance.states[2].playerIndex;
				modelSlot.Start();
				modelSlot.Render();
				modelSlot.End();
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
				synchronized (((ModelManager.ModelSlot)this.ModelSlots.get(int1)).model.AnimPlayer) {
					GL11.glPushClientAttrib(-1);
					GL11.glPushAttrib(1048575);
					GL11.glEnable(2929);
					GL11.glEnable(3042);
					GL11.glBlendFunc(770, 771);
					GL11.glEnable(3008);
					GL11.glAlphaFunc(516, 0.0F);
					ModelManager.ModelSlot modelSlot2 = (ModelManager.ModelSlot)this.ModelSlots.get(int1);
					modelSlot2.Start();
					modelSlot2.Render();
					modelSlot2.End();
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

	public void RenderSkyBox(TextureDraw textureDraw, int int1, int int2, int int3, int int4) {
		switch (int3) {
		case 1: 
			GL30.glBindFramebuffer(36160, int4);
			break;
		
		case 2: 
			ARBFramebufferObject.glBindFramebuffer(36160, int4);
			break;
		
		case 3: 
			EXTFramebufferObject.glBindFramebufferEXT(36160, int4);
		
		}
		GL11.glPushClientAttrib(-1);
		GL11.glPushAttrib(1048575);
		GL11.glMatrixMode(5889);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		GL11.glOrtho(0.0, 1.0, 1.0, 0.0, -1.0, 1.0);
		GL11.glViewport(0, 0, 512, 512);
		GL11.glMatrixMode(5888);
		GL11.glPushMatrix();
		GL11.glLoadIdentity();
		ARBShaderObjects.glUseProgramObjectARB(int1);
		if (Shader.ShaderMap.containsKey(int1)) {
			((Shader)Shader.ShaderMap.get(int1)).updateSkyBoxParams(textureDraw);
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
		GL11.glViewport(0, 0, SpriteRenderer.instance.states[2].offscreenWidth[int2], SpriteRenderer.instance.states[2].offscreenHeight[int2]);
		switch (int3) {
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

	public void DoneRendering(int int1) {
		if (int1 >= 0 && int1 < this.ModelSlots.size()) {
			ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.ModelSlots.get(int1);
			assert modelSlot.renderRefCount > 0;
			--modelSlot.renderRefCount;
		}
	}

	public void Reset(IsoGameCharacter gameCharacter) {
		if (gameCharacter.legsSprite != null && gameCharacter.legsSprite.modelSlot != null) {
			RenderThread.borrowContext();
			while (SpriteRenderer.instance.DoingRender) {
				try {
					Thread.sleep(2L);
				} catch (InterruptedException interruptedException) {
					interruptedException.printStackTrace();
				}
			}

			this.DoCharacterModelParts(gameCharacter, gameCharacter.legsSprite.modelSlot);
			gameCharacter.legsSprite.modelSlot.UpdateLights();
			RenderThread.returnContext();
		}
	}

	public void Add(IsoGameCharacter gameCharacter) {
		if (this.bCreated && PerformanceSettings.modelsEnabled && PerformanceSettings.support3D) {
			RenderThread.borrowContext();
			this.Contains.add(gameCharacter);
			ModelManager.ModelSlot modelSlot = this.getSlot(gameCharacter);
			if (!this.ModelSlots.contains(modelSlot)) {
				this.ModelSlots.add(modelSlot);
			}

			if (((IsoDirectionFrame)gameCharacter.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
				String string = null;
				string = ((IsoDirectionFrame)gameCharacter.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
				string = string.substring(0, string.indexOf("_"));
				String string2 = gameCharacter.legsSprite.name;
				assert !(gameCharacter instanceof IsoZombie) || ((IsoZombie)gameCharacter).SpriteName.equals(gameCharacter.legsSprite.name);
				modelSlot.model = this.newInstance((Model)this.ModelMap.get(string.toLowerCase().contains("kate") ? "kate" : "male"), gameCharacter, (AnimationPlayer)null);
				modelSlot.model.LoadTexture(string2 + "_Body");
			}

			this.DoCharacterModelParts(gameCharacter, modelSlot);
			modelSlot.active = true;
			gameCharacter.legsSprite.modelSlot = modelSlot;
			modelSlot.UpdateLights();
			RenderThread.returnContext();
		}
	}

	private void DoCharacterModelParts(IsoGameCharacter gameCharacter, ModelManager.ModelSlot modelSlot) {
		modelSlot.sub.clear();
		String string = null;
		if (((IsoDirectionFrame)gameCharacter.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
			string = ((IsoDirectionFrame)gameCharacter.legsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
			string = string.substring(0, string.indexOf("_"));
			modelSlot.model.LoadTexture(gameCharacter.legsSprite.name + "_Body");
			modelSlot.model.tintR = gameCharacter.legsSprite.getTintMod().r;
			modelSlot.model.tintG = gameCharacter.legsSprite.getTintMod().g;
			modelSlot.model.tintB = gameCharacter.legsSprite.getTintMod().b;
		}

		if (gameCharacter.bottomsSprite != null && !gameCharacter.bottomsSprite.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)gameCharacter.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
			String string2 = ((IsoDirectionFrame)gameCharacter.bottomsSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
			string2 = string2.replace(string + "_", "");
			if (string2.contains("F_")) {
				string2 = string2.substring(0, string2.indexOf("_", 2));
			} else {
				string2 = string2.substring(0, string2.indexOf("_"));
			}

			ModelInstance modelInstance = this.newInstance((Model)this.ModelMap.get(string2.toLowerCase()), gameCharacter, modelSlot.model.AnimPlayer);
			modelInstance.LoadTexture(string2 + "_White");
			modelInstance.tintR = gameCharacter.bottomsSprite.getTintMod().r;
			modelInstance.tintG = gameCharacter.bottomsSprite.getTintMod().g;
			modelInstance.tintB = gameCharacter.bottomsSprite.getTintMod().b;
			modelSlot.sub.add(modelInstance);
		}

		if (gameCharacter.topSprite != null && !gameCharacter.topSprite.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)gameCharacter.topSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
			String string3 = ((IsoDirectionFrame)gameCharacter.topSprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
			string3 = string3.replace(string + "_", "");
			if (string3.contains("F_")) {
				string3 = string3.substring(0, string3.indexOf("_", 2));
			} else {
				string3 = string3.substring(0, string3.indexOf("_"));
			}

			ModelInstance modelInstance2 = this.newInstance((Model)this.ModelMap.get(string3.toLowerCase()), gameCharacter, modelSlot.model.AnimPlayer);
			modelInstance2.LoadTexture(string3 + "_White");
			modelInstance2.tintR = gameCharacter.topSprite.getTintMod().r;
			modelInstance2.tintG = gameCharacter.topSprite.getTintMod().g;
			modelInstance2.tintB = gameCharacter.topSprite.getTintMod().b;
			modelSlot.sub.add(modelInstance2);
		}

		if (gameCharacter.getDescriptor() != null && gameCharacter.getDescriptor().getHair() != null && !gameCharacter.getDescriptor().getHair().isEmpty()) {
			String string4 = gameCharacter.getDescriptor().getHair();
			string4 = string4.replace("_White", "");
			if (string4.equals("F_Hair")) {
				string4 = "F_Hair_kate";
			}

			if (this.ModelMap.get(string4.toLowerCase()) != null) {
				ModelInstance modelInstance3 = this.newInstance((Model)this.ModelMap.get(string4.toLowerCase()), gameCharacter, modelSlot.model.AnimPlayer);
				modelInstance3.LoadTexture(string4.replace("_kate", "") + "_White");
				modelInstance3.tintR = gameCharacter.hairSprite.getTintMod().r;
				modelInstance3.tintG = gameCharacter.hairSprite.getTintMod().g;
				modelInstance3.tintB = gameCharacter.hairSprite.getTintMod().b;
				modelSlot.sub.add(modelInstance3);
			}
		}

		try {
			for (int int1 = 0; int1 < gameCharacter.extraSprites.size(); ++int1) {
				IsoSprite sprite = (IsoSprite)gameCharacter.extraSprites.get(int1);
				if (sprite != null && sprite.CurrentAnim != null && !sprite.CurrentAnim.Frames.isEmpty() && ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N) != null) {
					String string5 = ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get(0)).getTexture(IsoDirections.N).getName();
					string5 = string5.replace(string + "_", "");
					string5 = string5.substring(0, string5.lastIndexOf("_"));
					string5 = string5.substring(0, string5.lastIndexOf("_"));
					string5 = string5.substring(0, string5.lastIndexOf("_"));
					string5 = string5.substring(0, string5.lastIndexOf("_"));
					string5 = string5.replace("_White", "");
					if (string5.equals("F_Hair")) {
						string5 = "F_Hair_kate";
					}

					if (this.ModelMap.containsKey(string5.toLowerCase())) {
						ModelInstance modelInstance4 = this.newInstance((Model)this.ModelMap.get(string5.toLowerCase()), gameCharacter, modelSlot.model.AnimPlayer);
						modelInstance4.LoadTexture(string5.replace("_kate", "") + "_White");
						modelInstance4.tintR = sprite.getTintMod().r;
						modelInstance4.tintG = sprite.getTintMod().g;
						modelInstance4.tintB = sprite.getTintMod().b;
						modelSlot.sub.add(modelInstance4);
					}
				}
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		if (gameCharacter.getPrimaryHandItem() instanceof HandWeapon) {
			HandWeapon handWeapon = (HandWeapon)gameCharacter.getPrimaryHandItem();
			String string6 = handWeapon.getWeaponSprite();
			if (string6 != null && this.ModelMap.containsKey("weapons_" + string6.toLowerCase())) {
				ModelInstance modelInstance5 = this.newInstance((Model)this.ModelMap.get("weapons_" + string6.toLowerCase()), gameCharacter, modelSlot.model.AnimPlayer);
				modelSlot.sub.add(modelInstance5);
			}
		}
	}

	public void update() {
		int int1;
		for (int1 = 0; int1 < this.ToResetNextFrame.size(); ++int1) {
			this.Reset((IsoGameCharacter)this.ToResetNextFrame.get(int1));
		}

		this.ToResetNextFrame.clear();
		if (this.removeModels.Check()) {
			for (int1 = 0; int1 < this.ToRemove.size(); ++int1) {
				if (this.DoRemove((IsoGameCharacter)this.ToRemove.get(int1), false)) {
					this.ToRemove.remove(int1--);
				}
			}

			for (int1 = 0; int1 < this.ToRemoveVehicles.size(); ++int1) {
				ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.ToRemoveVehicles.get(int1);
				modelSlot.active = false;
				for (int int2 = 0; int2 < modelSlot.sub.size(); ++int2) {
					((ModelInstance)modelSlot.sub.get(int2)).object = null;
				}

				modelSlot.model.object = null;
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
		for (int1 = 0; int1 < this.torches.size(); ++int1) {
			IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)this.torches.get(int1);
			IsoLightSource lightSource = this.freeLights.isEmpty() ? new IsoLightSource(0, 0, 0, 1.0F, 1.0F, 1.0F, 1) : (IsoLightSource)this.freeLights.pop();
			lightSource.x = (int)torchInfo.x;
			lightSource.y = (int)torchInfo.y;
			lightSource.z = (int)torchInfo.z;
			lightSource.r = 1.0F;
			lightSource.g = 0.85F;
			lightSource.b = 0.6F;
			lightSource.radius = (int)Math.ceil((double)torchInfo.dist);
			this.torchLights.add(lightSource);
		}
	}

	private ModelManager.ModelSlot getSlot(IsoGameCharacter gameCharacter) {
		for (int int1 = 0; int1 < this.ModelSlots.size(); ++int1) {
			ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.ModelSlots.get(int1);
			if (modelSlot.character == gameCharacter) {
				return modelSlot;
			}
		}

		return new ModelManager.ModelSlot(this.ModelSlots.size(), this.newInstance(gameCharacter.isFemale() ? (Model)this.ModelMap.get("kate") : (Model)this.ModelMap.get("male"), gameCharacter, (AnimationPlayer)null), gameCharacter);
	}

	private boolean DoRemove(IsoGameCharacter gameCharacter, boolean boolean1) {
		if (this.Contains.contains(gameCharacter)) {
			for (int int1 = 0; int1 < this.ModelSlots.size(); ++int1) {
				ModelManager.ModelSlot modelSlot = (ModelManager.ModelSlot)this.ModelSlots.get(int1);
				if (modelSlot.character == gameCharacter && (boolean1 || modelSlot.renderRefCount <= 0)) {
					this.Contains.remove(gameCharacter);
					modelSlot.character = null;
					modelSlot.active = false;
					modelSlot.bRemove = false;
					modelSlot.renderRefCount = 0;
					gameCharacter.legsSprite.modelSlot = null;
					return true;
				}
			}
		}

		return false;
	}

	public void Remove(IsoGameCharacter gameCharacter) {
		if (gameCharacter.hasActiveModel()) {
			if (this.Contains.contains(gameCharacter) && !this.ToRemove.contains(gameCharacter)) {
				gameCharacter.legsSprite.modelSlot.bRemove = true;
				this.ToRemove.add(gameCharacter);
			}
		}
	}

	public void Remove(BaseVehicle baseVehicle) {
		if (baseVehicle.sprite != null && baseVehicle.sprite.modelSlot != null) {
			ModelManager.ModelSlot modelSlot = baseVehicle.sprite.modelSlot;
			if (!this.ToRemoveVehicles.contains(modelSlot)) {
				this.ToRemoveVehicles.add(modelSlot);
			}

			baseVehicle.sprite.modelSlot = null;
		}
	}

	public void ResetNextFrame(IsoGameCharacter gameCharacter) {
		this.ToResetNextFrame.add(gameCharacter);
	}

	public void Reset() {
		RenderThread.borrowContext();
		for (int int1 = 0; int1 < this.ToRemove.size(); ++int1) {
			this.DoRemove((IsoGameCharacter)this.ToRemove.get(int1), true);
		}

		this.ToRemove.clear();
		try {
			if (!this.Contains.isEmpty()) {
				IsoGameCharacter[] gameCharacterArray = (IsoGameCharacter[])this.Contains.toArray(new IsoGameCharacter[this.Contains.size()]);
				IsoGameCharacter[] gameCharacterArray2 = gameCharacterArray;
				int int2 = gameCharacterArray.length;
				for (int int3 = 0; int3 < int2; ++int3) {
					IsoGameCharacter gameCharacter = gameCharacterArray2[int3];
					this.DoRemove(gameCharacter, true);
				}
			}

			this.ModelSlots.clear();
		} catch (Exception exception) {
			exception.printStackTrace();
		}

		RenderThread.returnContext();
		this.lights.clear();
		this.lightsTemp.clear();
		compChar = null;
	}

	public void getClosestThreeLights(IsoMovingObject movingObject, IsoLightSource[] lightSourceArray) {
		compChar = movingObject;
		this.lightsTemp.clear();
		int int1;
		for (int1 = 0; int1 < this.lights.size(); ++int1) {
			IsoLightSource lightSource = (IsoLightSource)this.lights.get(int1);
			if (lightSource.bActive && (lightSource.localToBuilding == null || movingObject.getCurrentBuilding() == lightSource.localToBuilding) && !(IsoUtils.DistanceTo(movingObject.x, movingObject.y, (float)lightSource.x + 0.5F, (float)lightSource.y + 0.5F) >= (float)lightSource.radius) && LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)compChar.x, (int)compChar.y, (int)compChar.z, lightSource.x, lightSource.y, lightSource.z, false) != LosUtil.TestResults.Blocked) {
				this.lightsTemp.add(lightSource);
			}
		}

		if (movingObject instanceof BaseVehicle) {
			for (int1 = 0; int1 < this.torches.size(); ++int1) {
				IsoGameCharacter.TorchInfo torchInfo = (IsoGameCharacter.TorchInfo)this.torches.get(int1);
				if (!(IsoUtils.DistanceTo(movingObject.x, movingObject.y, torchInfo.x, torchInfo.y) >= torchInfo.dist) && LosUtil.lineClear(IsoWorld.instance.CurrentCell, (int)compChar.x, (int)compChar.y, (int)compChar.z, (int)torchInfo.x, (int)torchInfo.y, (int)torchInfo.z, false) != LosUtil.TestResults.Blocked) {
					if (torchInfo.bCone) {
						Vector2 vector2 = this.tempVec2;
						vector2.x = torchInfo.x - movingObject.x;
						vector2.y = torchInfo.y - movingObject.y;
						vector2.normalize();
						Vector2 vector22 = this.tempVec2_2;
						vector22.x = torchInfo.angleX;
						vector22.y = torchInfo.angleY;
						vector22.normalize();
						float float1 = vector2.dot(vector22);
						if (float1 >= -0.92F) {
							continue;
						}
					}

					this.lightsTemp.add(this.torchLights.get(int1));
				}
			}
		}

		Collections.sort(this.lightsTemp, comp);
		lightSourceArray[0] = lightSourceArray[1] = lightSourceArray[2] = null;
		if (this.lightsTemp.size() > 0) {
			lightSourceArray[0] = (IsoLightSource)this.lightsTemp.get(0);
		}

		if (this.lightsTemp.size() > 1) {
			lightSourceArray[1] = (IsoLightSource)this.lightsTemp.get(1);
		}

		if (this.lightsTemp.size() > 2) {
			lightSourceArray[2] = (IsoLightSource)this.lightsTemp.get(2);
		}
	}

	public void Add(BaseVehicle baseVehicle) {
		if (this.bCreated && PerformanceSettings.modelsEnabled && PerformanceSettings.support3D) {
			if (baseVehicle != null && baseVehicle.getScript() != null) {
				VehicleScript vehicleScript = baseVehicle.getScript();
				RenderThread.borrowContext();
				ModelInstance modelInstance = new ModelInstance((Model)this.ModelMap.get(vehicleScript.getModel().file), (IsoGameCharacter)null, (AnimationPlayer)null, true, false);
				baseVehicle.getSkin();
				if (vehicleScript.textureDataSkins != null && baseVehicle.getSkinIndex() >= 0 && baseVehicle.getSkinIndex() < vehicleScript.textureDataSkins.length) {
					modelInstance.tex = vehicleScript.textureDataSkins[baseVehicle.getSkinIndex()];
				}

				modelInstance.textureMask = vehicleScript.textureDataMask;
				modelInstance.textureDamage1Overlay = vehicleScript.textureDataDamage1Overlay;
				modelInstance.textureDamage1Shell = vehicleScript.textureDataDamage1Shell;
				modelInstance.textureDamage2Overlay = vehicleScript.textureDataDamage2Overlay;
				modelInstance.textureDamage2Shell = vehicleScript.textureDataDamage2Shell;
				modelInstance.textureLights = vehicleScript.textureDataLights;
				modelInstance.textureRust = vehicleScript.textureDataRust;
				if (modelInstance.tex != null) {
					modelInstance.tex.bindAlways = true;
				} else {
					DebugLog.log("ERROR: ModelManager.Add() texture \'" + baseVehicle.getSkin() + "\' not found");
				}

				ModelManager.ModelSlot modelSlot = null;
				int int1;
				for (int1 = 0; int1 < this.ModelSlots.size(); ++int1) {
					if (!((ModelManager.ModelSlot)this.ModelSlots.get(int1)).active && ((ModelManager.ModelSlot)this.ModelSlots.get(int1)).character == null) {
						modelSlot = (ModelManager.ModelSlot)this.ModelSlots.get(int1);
						break;
					}
				}

				if (modelSlot == null) {
					modelSlot = new ModelManager.ModelSlot(this.ModelSlots.size(), modelInstance, (IsoGameCharacter)null);
					this.ModelSlots.add(modelSlot);
				}

				modelSlot.model = modelInstance;
				modelInstance.object = baseVehicle;
				modelSlot.sub.clear();
				for (int1 = 0; int1 < baseVehicle.models.size(); ++int1) {
					BaseVehicle.ModelInfo modelInfo = (BaseVehicle.ModelInfo)baseVehicle.models.get(int1);
					modelInstance = new ModelInstance((Model)this.ModelMap.get(modelInfo.scriptModel.file), (IsoGameCharacter)null, (AnimationPlayer)null, false, true);
					modelInstance.object = baseVehicle;
					modelSlot.sub.add(modelInstance);
				}

				modelSlot.active = true;
				baseVehicle.sprite.modelSlot = modelSlot;
				RenderThread.returnContext();
			}
		}
	}

	private Model loadStaticModel(String string, String string2, String string3) {
		String string4 = string3;
		if (string3.equals("vehicle") && !Core.getInstance().getPerfReflectionsOnLoad()) {
			string4 = string3 + "_noreflect";
		}

		try {
			Model model = ModelLoader.instance.Load("media/models/" + string + ".txt", string2, string4, true);
			this.ModelMap.put(string, model);
			model.Name = string;
			return model;
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return null;
		}
	}

	private Model loadModel(String string, String string2) {
		try {
			Model model = ModelLoader.instance.Load("media/models/" + string + ".txt", string2, "basicEffect", false);
			this.ModelMap.put(string.toLowerCase(), model);
			model.Name = string.toLowerCase();
			return model;
		} catch (IOException ioException) {
			ioException.printStackTrace();
			return null;
		}
	}

	public ModelInstance newInstance(Model model, IsoGameCharacter gameCharacter, AnimationPlayer animationPlayer) {
		if (model == null) {
			boolean boolean1 = false;
		}

		ModelInstance modelInstance = new ModelInstance(model, gameCharacter, animationPlayer, false, false);
		modelInstance.UpdateDir();
		if (modelInstance.AnimPlayer != null) {
			modelInstance.AnimPlayer.angle = modelInstance.AnimPlayer.targetAngle;
		}

		AnimationTrack animationTrack = modelInstance.Play(gameCharacter.legsSprite.CurrentAnim.name, gameCharacter.def.Looped, gameCharacter.def.Finished, gameCharacter);
		if (animationTrack != null) {
			animationTrack.syncToFrame(gameCharacter.def, gameCharacter.legsSprite.CurrentAnim);
			modelInstance.AnimPlayer.Update(0.0F, true, (Matrix4f)null);
		}

		if (model.Name.equals("male") || model.Name.equals("kate")) {
			modelInstance.updateLights = true;
		}

		return modelInstance;
	}

	public static class ModelSlot {
		public int ID;
		public ModelInstance model;
		public IsoGameCharacter character;
		public ArrayList sub = new ArrayList();
		public boolean active;
		public boolean bRemove;
		public short renderRefCount;

		public ModelSlot(int int1, ModelInstance modelInstance, IsoGameCharacter gameCharacter) {
			this.ID = int1;
			this.model = modelInstance;
			this.character = gameCharacter;
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
			for (int int1 = 0; int1 < this.sub.size(); ++int1) {
				((ModelInstance)this.sub.get(int1)).Draw();
			}
		}

		public void End() {
			ModelManager.instance.bitmap.endDrawing();
		}

		public void Play(String string, boolean boolean1, boolean boolean2, IsoGameCharacter gameCharacter) {
			this.model.Play(string, boolean1, boolean2, gameCharacter);
			for (int int1 = 0; int1 < this.sub.size(); ++int1) {
				((ModelInstance)this.sub.get(int1)).Play(string, boolean1, boolean2, gameCharacter);
			}
		}

		public void Update() {
			float float1 = this.character.def.AnimFrameIncrease / (float)this.character.legsSprite.CurrentAnim.Frames.size();
			if (this.character.getLastSquare() != this.character.getCurrentSquare()) {
				this.UpdateLights();
			}

			synchronized (this.model.AnimPlayer) {
				this.model.UpdateDir();
				this.model.Update(float1);
				for (int int1 = 0; int1 < this.sub.size(); ++int1) {
					((ModelInstance)this.sub.get(int1)).lights = this.model.lights;
					((ModelInstance)this.sub.get(int1)).AnimPlayer = this.model.AnimPlayer;
				}
			}
		}

		public void UpdateLights() {
			if (this.model != null && this.model.object != null) {
				synchronized (this.model.lights) {
					int int1;
					if (this.character instanceof DeadBodyAtlas.AtlasCharacter) {
						this.model.lights[0] = this.model.lights[1] = this.model.lights[2] = null;
						for (int1 = 0; int1 < this.sub.size(); ++int1) {
							((ModelInstance)this.sub.get(int1)).lights = this.model.lights;
						}
					} else {
						ModelManager.instance.getClosestThreeLights(this.model.object, this.model.lights);
						for (int1 = 0; int1 < this.sub.size(); ++int1) {
							((ModelInstance)this.sub.get(int1)).lights = this.model.lights;
						}
					}
				}
			}
		}

		public void ResetToFrameOne() {
			synchronized (this.model.AnimPlayer) {
				this.model.AnimPlayer.ResetToFrameOne();
				Iterator iterator = this.sub.iterator();
				while (iterator.hasNext()) {
					ModelInstance modelInstance = (ModelInstance)iterator.next();
					modelInstance.AnimPlayer.ResetToFrameOne();
				}
			}
		}

		public void DisableBlendingFrom(String string) {
			AnimationTrack animationTrack = this.model.AnimPlayer.getAnimTrack(string);
			if (animationTrack != null) {
				animationTrack.bFinished = true;
			}
		}
	}

	public static class AnimRequest {
		public float angle;
		public String anim;
		public SurvivorDesc desc;
	}
}
