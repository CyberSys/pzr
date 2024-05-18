package zombie.characters;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Stack;
import zombie.GameTime;
import zombie.IndieGL;
import zombie.LOSThread;
import zombie.WorldSoundManager;
import zombie.Lua.LuaEventManager;
import zombie.ai.states.DieState;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.ReanimateState;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.SwipeStatePlayer;
import zombie.behaviors.BehaviorHub;
import zombie.behaviors.survivor.orders.GuardOrder;
import zombie.behaviors.survivor.orders.LootBuilding;
import zombie.behaviors.survivor.orders.Order;
import zombie.behaviors.survivor.orders.Needs.DrinkWater;
import zombie.behaviors.survivor.orders.Needs.Heal;
import zombie.characters.Moodles.MoodleType;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.utils.OnceEvery;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoPushableObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.Vector2;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.scripting.ScriptManager;
import zombie.ui.ObjectTooltip;
import zombie.ui.TutorialManager;
import zombie.ui.UIManager;


public class IsoSurvivor extends IsoLivingCharacter {
	public boolean NoGoreDeath = false;
	public BehaviorHub behaviours = new BehaviorHub();
	public boolean Draggable = false;
	public IsoGameCharacter following = null;
	boolean Dragging;
	int repathDelay = 0;
	public int nightsSurvived = 0;
	public int ping = 0;
	public IsoPushableObject collidePushable;
	private boolean tryToTeamUp = true;
	public boolean bLastSpottedPlayer = false;
	public boolean bSpottedPlayer = false;
	public boolean bWillJoinPlayer = false;
	public boolean HasBeenDragged = false;
	public IsoGameCharacter[] ClosestTwoSurvivors = new IsoGameCharacter[2];
	int NeightbourUpdate = 20;
	int NeightbourUpdateMax = 20;
	public Vector2 lmove = new Vector2(0.0F, 0.0F);
	public ArrayList LastLocalNeutralList = new ArrayList();
	OnceEvery LOSUpdate = new OnceEvery(0.4F, true);
	public int dangerTile = 0;
	public int lastDangerTile = 0;
	IsoGameCharacter aimAt = null;
	Stack availableTemp = new Stack();
	public static int SatisfiedByFoodLevel = 100;
	public static int SatisfiedByWeaponLevel = 80;

	public static byte[] createChecksum(String string) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(string);
		byte[] byteArray = new byte[1024];
		MessageDigest messageDigest = MessageDigest.getInstance("MD5");
		int int1;
		do {
			int1 = fileInputStream.read(byteArray);
			if (int1 > 0) {
				messageDigest.update(byteArray, 0, int1);
			}
		} while (int1 != -1);

		fileInputStream.close();
		return messageDigest.digest();
	}

	public static String getMD5Checksum(String string) throws Exception {
		byte[] byteArray = createChecksum(string);
		String string2 = "";
		for (int int1 = 0; int1 < byteArray.length; ++int1) {
			string2 = string2 + Integer.toString((byteArray[int1] & 255) + 256, 16).substring(1);
		}

		return string2;
	}

	public static boolean DoChecksumCheck(String string, String string2) {
		String string3 = "";
		try {
			string3 = getMD5Checksum(string);
			if (!string3.equals(string2)) {
				return false;
			}
		} catch (Exception exception) {
			string3 = "";
			try {
				string3 = getMD5Checksum("D:/Dropbox/Zomboid/zombie/build/classes/" + string);
			} catch (Exception exception2) {
				return false;
			}
		}

		return string3.equals(string2);
	}

	public void Despawn() {
		if (this.descriptor != null) {
			this.descriptor.Instance = null;
			if (this.descriptor.Group != null && this.descriptor.Group.Leader == this.descriptor) {
				this.descriptor.Group.Despawn();
			}
		}
	}

	public static boolean DoChecksumCheck() {
		if (!DoChecksumCheck("zombie/GameWindow.class", "c4a62b8857f0fb6b9c103ff6ef127a9b")) {
			return false;
		} else if (!DoChecksumCheck("zombie/GameWindow$1.class", "5d93dc446b2dc49092fe4ecb5edf5f17")) {
			return false;
		} else if (!DoChecksumCheck("zombie/GameWindow$2.class", "a3e3d2c8cf6f0efaa1bf7f6ceb572073")) {
			return false;
		} else if (!DoChecksumCheck("zombie/gameStates/MainScreenState.class", "206848ba7cb764293dd2c19780263854")) {
			return false;
		} else if (!DoChecksumCheck("zombie/FrameLoader$1.class", "0ebfcc9557cc28d53aa982a71616bf5b")) {
			return false;
		} else {
			return DoChecksumCheck("zombie/FrameLoader.class", "d5b1f7b2886a499d848c204f6a815776");
		}
	}

	public String getObjectName() {
		return "Survivor";
	}

	public IsoSurvivor(IsoCell cell) {
		super(cell, 0.0F, 0.0F, 0.0F);
		this.OutlineOnMouseover = true;
		this.getCell().getSurvivorList().add(this);
		LuaEventManager.triggerEvent("OnCreateSurvivor", this);
	}

	public IsoSurvivor(IsoCell cell, int int1, int int2, int int3) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.getCell().getSurvivorList().add(this);
		this.OutlineOnMouseover = true;
		this.descriptor = new SurvivorDesc();
		this.PathSpeed = 0.05F;
		this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
		this.sprite.LoadFramesPcx("Wife", "death", 1);
		this.sprite.LoadFramesPcx("Wife", "dragged", 1);
		this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
		this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
		this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
		this.name = "Kate";
		this.solid = false;
		this.IgnoreStaggerBack = true;
		this.SpeakColour = new Color(204, 100, 100);
		this.dir = IsoDirections.S;
		this.OutlineOnMouseover = true;
		this.finder.maxSearchDistance = 120;
		this.CreateBehaviors();
		LuaEventManager.triggerEvent("OnCreateSurvivor", this);
		LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
	}

	public IsoSurvivor(SurvivorDesc survivorDesc, IsoCell cell, int int1, int int2, int int3) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.bFemale = survivorDesc.isFemale();
		this.descriptor = survivorDesc;
		survivorDesc.setInstance(this);
		this.OutlineOnMouseover = true;
		this.PathSpeed = 0.05F;
		String string = "Zombie_palette";
		string = string + "01";
		this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
		this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
		this.finder.maxSearchDistance = 120;
		this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
		this.Personality = SurvivorPersonality.CreatePersonality(SurvivorPersonality.Personality.GunNut);
		this.CreateBehaviors();
		this.Dressup(survivorDesc);
		LuaEventManager.triggerEventGarbage("OnCreateSurvivor", this);
		LuaEventManager.triggerEventGarbage("OnCreateLivingCharacter", this, this.descriptor);
	}

	public void reloadSpritePart() {
		this.sprite.AnimMap.clear();
		this.sprite.AnimStack.clear();
		this.sprite.CurrentAnim = null;
		this.extraSprites = new ArrayList();
		if (this.isFemale()) {
			if (this.descriptor.top != null) {
				this.descriptor.top = this.descriptor.top.replace("Shirt", "Blouse");
			}

			if (this.descriptor.toppal != null) {
				this.descriptor.toppal = this.descriptor.toppal.replace("Shirt", "Blouse");
			}
		} else {
			if (this.descriptor.top != null) {
				this.descriptor.top = this.descriptor.top.replace("Blouse", "Shirt");
			}

			if (this.descriptor.toppal != null) {
				this.descriptor.toppal = this.descriptor.toppal.replace("Blouse", "Shirt");
			}

			if (this.descriptor.bottoms != null) {
				this.descriptor.bottoms = this.descriptor.bottoms.replace("Skirt", "Trousers");
			}

			if (this.descriptor.bottomspal != null) {
				this.descriptor.bottomspal = this.descriptor.bottomspal.replace("Skirt", "Trousers");
			}
		}

		InventoryItem inventoryItem = this.ClothingItem_Torso;
		if (inventoryItem != null) {
			this.getInventory().Remove(inventoryItem);
			this.ClothingItem_Torso = null;
			this.topSprite = null;
		}

		if (this.descriptor.toppal != null && !this.descriptor.toppal.isEmpty()) {
			this.ClothingItem_Torso = this.getInventory().AddItem((InventoryItem)Clothing.CreateFromSprite(this.descriptor.toppal.replace("_White", "")));
			if (inventoryItem != null) {
				this.ClothingItem_Torso.col.set(this.descriptor.topColor != null ? this.descriptor.topColor : new Color(1, 1, 1));
			}
		}

		InventoryItem inventoryItem2 = this.ClothingItem_Legs;
		if (inventoryItem2 != null) {
			this.getInventory().Remove(inventoryItem2);
			this.ClothingItem_Legs = null;
			this.bottomsSprite = null;
		}

		if (this.descriptor.bottomspal != null && !this.descriptor.bottomspal.isEmpty()) {
			this.ClothingItem_Legs = this.getInventory().AddItem((InventoryItem)Clothing.CreateFromSprite(this.descriptor.bottomspal.replace("_White", "")));
			if (inventoryItem2 != null) {
				this.ClothingItem_Legs.col.set(this.descriptor.trouserColor != null ? this.descriptor.trouserColor : new Color(1, 1, 1));
			}
		}

		InventoryItem inventoryItem3 = this.ClothingItem_Feet;
		if (this.descriptor.shoes != null && !this.descriptor.shoes.isEmpty()) {
			if (inventoryItem3 == null) {
				this.ClothingItem_Feet = this.getInventory().AddItem((InventoryItem)Clothing.CreateFromSprite("Shoes"));
				this.ClothingItem_Feet.col = new Color(64, 64, 64);
			}
		} else if (inventoryItem3 != null) {
			this.getInventory().Remove(inventoryItem3);
			this.ClothingItem_Feet = null;
		}

		this.InitSpriteParts(this.descriptor, this.descriptor.legs, this.descriptor.torso, this.descriptor.head, this.descriptor.top, this.descriptor.bottoms, this.descriptor.shoes, this.descriptor.skinpal, this.descriptor.toppal, this.descriptor.bottomspal, this.descriptor.shoespal, this.descriptor.hair, this.descriptor.extra);
	}

	public IsoSurvivor(SurvivorDesc survivorDesc, IsoCell cell, int int1, int int2, int int3, boolean boolean1) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.bFemale = survivorDesc.isFemale();
		this.descriptor = survivorDesc;
		if (boolean1) {
			survivorDesc.setInstance(this);
		}

		this.OutlineOnMouseover = true;
		this.PathSpeed = 0.05F;
		String string = "Zombie_palette";
		string = string + "01";
		this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
		this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
		this.finder.maxSearchDistance = 120;
		this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
		this.Personality = SurvivorPersonality.CreatePersonality(SurvivorPersonality.Personality.GunNut);
		this.CreateBehaviors();
		this.Dressup(survivorDesc);
		LuaEventManager.triggerEvent("OnCreateSurvivor", this);
	}

	public IsoSurvivor(SurvivorPersonality.Personality personality, SurvivorDesc survivorDesc, IsoCell cell, int int1, int int2, int int3) {
		super(cell, (float)int1, (float)int2, (float)int3);
		this.bFemale = survivorDesc.isFemale();
		this.getCell().getSurvivorList().add(this);
		if (personality == SurvivorPersonality.Personality.Kate) {
			this.OutlineOnMouseover = true;
			this.sprite.LoadFramesPcx("Wife", "death", 1);
			this.sprite.LoadFramesPcx("Wife", "dragged", 1);
			this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
			this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
			this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
			this.solid = false;
			this.IgnoreStaggerBack = true;
			this.SpeakColour = new Color(204, 100, 100);
			this.dir = IsoDirections.S;
			this.descriptor = survivorDesc;
			survivorDesc.setInstance(this);
			this.PathSpeed = 0.05F;
			this.finder.maxSearchDistance = 120;
			this.CreateBehaviors();
			this.bOnBed = true;
			this.offsetY += 25.0F;
			this.offsetX -= 10.0F;
			this.ApplyInBedOffset(true);
			this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
			this.inflictWound(IsoGameCharacter.BodyLocation.Leg, 1.0F, false, 1.0F);
			this.OutlineOnMouseover = true;
		} else {
			this.OutlineOnMouseover = true;
			this.descriptor = survivorDesc;
			survivorDesc.setInstance(this);
			this.PathSpeed = 0.06F;
			String string = "Zombie_palette";
			string = string + "01";
			this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
			this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
			this.finder.maxSearchDistance = 120;
			this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
			this.Personality = SurvivorPersonality.CreatePersonality(personality);
			this.CreateBehaviors();
			this.Dressup(survivorDesc);
			LuaEventManager.triggerEvent("OnCreateSurvivor", this);
			LuaEventManager.triggerEvent("OnCreateLivingCharacter", this, this.descriptor);
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		super.load(byteBuffer, int1);
		SurvivorDesc survivorDesc = this.descriptor;
		this.Personality = SurvivorPersonality.CreatePersonality(SurvivorPersonality.Personality.GunNut);
		if (this.Personality.type == SurvivorPersonality.Personality.Kate) {
			this.OutlineOnMouseover = true;
			this.sprite.LoadFramesPcx("Wife", "death", 1);
			this.sprite.LoadFramesPcx("Wife", "dragged", 1);
			this.sprite.LoadFramesPcx("Wife", "asleep_normal", 1);
			this.sprite.LoadFramesPcx("Wife", "asleep_bandaged", 1);
			this.sprite.LoadFramesPcx("Wife", "asleep_bleeding", 1);
			this.setSolid(false);
			this.IgnoreStaggerBack = true;
			this.SpeakColour = new Color(204, 100, 100);
			this.dir = IsoDirections.S;
			this.descriptor = survivorDesc;
			survivorDesc.setInstance(this);
			this.PathSpeed = 0.05F;
			this.finder.maxSearchDistance = 120;
			this.CreateBehaviors();
			this.bOnBed = true;
			this.offsetY += 5.0F;
			this.offsetX -= 21.0F;
			this.ApplyInBedOffset(true);
			this.NeightbourUpdate = Rand.Next(this.NeightbourUpdateMax);
		} else {
			this.bFemale = survivorDesc.isFemale();
			this.InitSpriteParts(survivorDesc, survivorDesc.legs, survivorDesc.torso, survivorDesc.head, survivorDesc.top, survivorDesc.bottoms, survivorDesc.shoes, survivorDesc.skinpal, survivorDesc.toppal, survivorDesc.bottomspal, survivorDesc.shoespal, survivorDesc.hair, survivorDesc.extra);
			this.CreateBehaviors();
			this.SpeakColour = new Color(Rand.Next(200) + 55, Rand.Next(200) + 55, Rand.Next(200) + 55, 255);
			this.finder.maxSearchDistance = 120;
			this.PathSpeed = 0.06F;
		}
	}

	public void DoTooltip(ObjectTooltip objectTooltip) {
		boolean boolean1 = true;
		String string = "";
		string = this.descriptor.forename + " " + this.descriptor.surname;
		int int1 = 5;
		objectTooltip.DrawText(string, 5.0, (double)int1, 1.0, 1.0, 0.800000011920929, 1.0);
		for (int int2 = 0; int2 < this.wounds.size(); ++int2) {
			IsoGameCharacter.Wound wound = (IsoGameCharacter.Wound)this.wounds.get(int2);
			int1 += 25;
			objectTooltip.DrawText("Broken Leg", 5.0, (double)int1, 0.5, 0.5, 0.0, 1.0);
			int1 += 14;
			if (wound.tourniquet) {
				objectTooltip.DrawText("  Stemmed", 5.0, (double)int1, 0.0, 1.0, 0.0, 1.0);
				int1 += 14;
			}

			if (wound.bandaged) {
				objectTooltip.DrawText("  Bandaged", 5.0, (double)int1, 0.0, 1.0, 0.0, 1.0);
				int1 += 14;
			}

			if (wound.bleeding > 0.5F) {
				objectTooltip.DrawText("  Bleeding Badly", 5.0, (double)int1, 1.0, 0.0, 0.0, 1.0);
			} else if (wound.bleeding > 0.0F) {
				objectTooltip.DrawText("  Bleeding", 5.0, (double)int1, 0.5, 0.5, 0.0, 1.0);
			}
		}

		objectTooltip.setHeight((double)(int1 + 32));
	}

	public boolean HasTooltip() {
		return true;
	}

	public void spotted(IsoMovingObject movingObject) {
		if (movingObject == IsoPlayer.instance) {
			ScriptManager.instance.Trigger("OnSpotPlayer", this.getScriptName());
			this.LastKnownLocation.put("Player", new IsoGameCharacter.Location((int)movingObject.getX(), (int)movingObject.getY(), (int)movingObject.getZ()));
			this.bSpottedPlayer = true;
			if (this.getZ() == IsoPlayer.instance.getZ() && IsoUtils.DistanceManhatten(this.getX(), this.getY(), IsoPlayer.instance.getX(), IsoPlayer.instance.getY()) < 8.0F && this.getCurrentSquare().getRoom() == IsoPlayer.instance.getCurrentSquare().getRoom()) {
				this.Meet(IsoPlayer.instance);
			}
		}

		for (int int1 = 0; int1 < this.EnemyList.size(); ++int1) {
			if (((IsoGameCharacter)this.EnemyList.get(int1)).descriptor.InGroupWith(this)) {
				this.EnemyList.remove(int1);
				--int1;
			}
		}
	}

	public boolean onMouseLeftClick(int int1, int int2) {
		if (IsoPlayer.instance != null && IsoPlayer.instance.isAiming) {
			return false;
		} else {
			if (IsoCamera.CamCharacter != IsoPlayer.instance && Core.bDebug) {
				IsoCamera.CamCharacter = this;
			}

			if (this != TutorialManager.instance.wife && UIManager.getDragInventory() == null) {
			}

			if (IsoPlayer.instance.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && IsoPlayer.instance.DistTo(this) < 4.0F && UIManager.getDragInventory() != null) {
				UIManager.getDragInventory().Use(this);
			}

			if (this.Draggable && this == TutorialManager.instance.wife && IsoUtils.DistanceTo(IsoPlayer.instance.getX(), IsoPlayer.instance.getY(), this.getX(), this.getY()) < 2.0F) {
				this.Draggable = true;
				this.Dragging = !this.Dragging;
				if (this.Dragging) {
					IsoPlayer.instance.DragCharacter = this;
					this.sprite.PlayAnim("dragged");
					this.ApplyInBedOffset(false);
				} else {
					IsoPlayer.instance.DragCharacter = null;
				}
			}

			if (IsoPlayer.instance.Health <= 0.0F || IsoPlayer.instance.BodyDamage.getHealth() <= 0.0F) {
				this.Dragging = false;
			}

			return true;
		}
	}

	public boolean AttemptAttack() {
		return this.DoAttack(1.0F);
	}

	public boolean DoAttack(float float1) {
		if (this.stateMachine.getCurrent() == SwipeStatePlayer.instance()) {
			return false;
		} else {
			if (float1 > 90.0F) {
				float1 = 90.0F;
			}

			float1 /= 25.0F;
			this.useChargeDelta = float1;
			if (this.useChargeDelta < 0.1F) {
				this.useChargeDelta = 1.0F;
			}

			if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() < 0.0F)) {
				if (this.leftHandItem != null && this.AttackDelay <= 0.0F && (!this.sprite.CurrentAnim.name.contains("Attack") || this.def.Frame >= (float)(this.sprite.CurrentAnim.Frames.size() - 1)) || this.def.Frame == 0.0F) {
					InventoryItem inventoryItem = this.leftHandItem;
					if (inventoryItem instanceof HandWeapon) {
						this.useHandWeapon = (HandWeapon)inventoryItem;
						if (this.useHandWeapon.isCantAttackWithLowestEndurance() && this.stats.enduranceRecharging) {
							return false;
						}

						if (UIManager.getPicked() != null) {
							this.attackTargetSquare = UIManager.getPicked().square;
							if (UIManager.getPicked().tile instanceof IsoMovingObject) {
								this.attackTargetSquare = ((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare();
							}
						}

						if (this.useHandWeapon.getAmmoType() != null && !this.inventory.contains(this.useHandWeapon.getAmmoType())) {
							return false;
						}

						if (this.useHandWeapon.getOtherHandRequire() == null || this.rightHandItem != null && this.rightHandItem.getType().equals(this.useHandWeapon.getOtherHandRequire())) {
							float float2 = this.useHandWeapon.getSwingTime();
							if (this.useHandWeapon.isUseEndurance() && this.stats.enduranceRecharging) {
								float2 *= 1.3F;
							}

							if (float2 < this.useHandWeapon.getMinimumSwingTime()) {
								float2 = this.useHandWeapon.getMinimumSwingTime();
							}

							float2 *= this.useHandWeapon.getSpeedMod(this);
							float2 *= 1.0F / GameTime.instance.getMultiplier();
							this.AttackDelayMax = this.AttackDelay = (float)((int)(float2 * 60.0F));
							this.AttackDelayUse = (float)((int)(this.AttackDelayMax * this.useHandWeapon.getDoSwingBeforeImpact()));
							this.AttackDelayUse = this.AttackDelayMax - this.AttackDelayUse - 2.0F;
							this.AttackWasSuperAttack = this.superAttack;
							if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
								this.stateMachine.changeState(SwipeStatePlayer.instance());
							}

							if (this.useHandWeapon.getAmmoType() != null) {
								this.inventory.RemoveOneOf(this.useHandWeapon.getAmmoType());
							}

							if (this.useHandWeapon.isUseSelf() && this.leftHandItem != null) {
								this.leftHandItem.Use();
							}

							if (this.useHandWeapon.isOtherHandUse() && this.rightHandItem != null) {
								this.rightHandItem.Use();
							}

							return true;
						}

						return false;
					}
				}

				return false;
			} else {
				return false;
			}
		}
	}

	public void update() {
		this.bCollidedWithPushable = false;
		if (this.getCurrentSquare() == null) {
			this.ensureOnTile();
		}

		LuaEventManager.triggerEvent("OnNPCSurvivorUpdate", this);
		this.lastDangerTile = this.dangerTile;
		this.dangerTile = IsoWorld.instance.CurrentCell.getDangerScore((int)this.getX(), (int)this.getY());
		if (this.LOSUpdate.Check()) {
			LOSThread.instance.AddJob(this);
		}

		if (this.getLastSquare() != this.getCurrentSquare() && this.getCurrentSquare() != null && this.getLastSquare() != null) {
			IsoObject object = this.getCurrentSquare().getDoorFrameTo(this.getLastSquare());
			if (object != null && object instanceof IsoDoor) {
				if (((IsoDoor)object).open && this.RemoteID == -1) {
					IsoGridSquare square = object.square;
					if (((IsoDoor)object).north) {
						square = IsoWorld.instance.CurrentCell.getGridSquare(square.getX(), square.getY() - 1, square.getZ());
					} else {
						square = IsoWorld.instance.CurrentCell.getGridSquare(square.getX() - 1, square.getY(), square.getZ());
					}

					if (object.square.getRoom() == null || square.getRoom() == null) {
						((IsoDoor)object).ToggleDoor(this);
					}
				}
			} else if (object != null && object instanceof IsoThumpable && ((IsoThumpable)object).open && this.RemoteID == -1) {
				((IsoThumpable)object).ToggleDoor(this);
			}
		}

		boolean boolean1;
		if (IsoCamera.CamCharacter == this) {
			boolean1 = false;
		}

		if (this.descriptor.Instance == null) {
			this.descriptor.Instance = this;
		}

		this.leftHandItem = this.inventory.getBestWeapon(this.descriptor);
		if (!(this.Health <= 0.0F) && !(this.BodyDamage.getHealth() <= 0.0F)) {
			if ((this.Moodles.getMoodleLevel(MoodleType.Hungry) > 1 || this.BodyDamage.getHealth() < 100.0F && this.Moodles.getMoodleLevel(MoodleType.FoodEaten) < 2) && !this.HasPersonalNeed("Heal")) {
				this.PersonalNeeds.add(new Heal(this));
			}

			if (this.Moodles.getMoodleLevel(MoodleType.Thirst) > 0 && !this.HasPersonalNeed("DrinkWater")) {
				this.PersonalNeeds.add(new DrinkWater(this));
			}

			boolean1 = true;
			this.stats.fatigue = 0.0F;
			--this.NeightbourUpdate;
			boolean boolean2 = false;
			Stats stats = this.stats;
			stats.stress += 1.0E-6F * (float)this.LocalRelevantEnemyList.size();
			if (this.NeightbourUpdate <= 0) {
				if (IsoPlayer.DemoMode) {
					WorldSoundManager.instance.addSound(this, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 90, 90);
				}

				this.LastLocalNeutralList.clear();
				this.LastLocalNeutralList.addAll(this.LocalNeutralList);
				this.LocalNeutralList.clear();
				this.NeightbourUpdate = this.NeightbourUpdateMax;
				this.VeryCloseEnemyList.clear();
				this.ClosestTwoSurvivors[0] = null;
				this.ClosestTwoSurvivors[1] = null;
				this.LastLocalEnemies = this.LocalEnemyList.size();
				this.LocalEnemyList.clear();
				this.LocalRelevantEnemyList.clear();
				this.dangerLevels = 0.0F;
				synchronized (this.LocalList) {
					for (int int1 = 0; int1 < this.LocalList.size(); ++int1) {
						IsoMovingObject movingObject = (IsoMovingObject)this.LocalList.get(int1);
						if (movingObject != this && movingObject instanceof IsoGameCharacter && (!(movingObject instanceof IsoZombie) || !((IsoZombie)movingObject).Ghost) && (!(movingObject instanceof IsoGameCharacter) || ((IsoGameCharacter)movingObject).VisibleToNPCs) && movingObject.getCurrentSquare() != null) {
							int int2 = (int)(this.getX() - movingObject.getX());
							int int3 = (int)(this.getY() - movingObject.getY());
							int int4 = Math.abs(int2);
							int int5 = Math.abs(int3);
							if (int4 < 1) {
								int4 = 1;
							}

							if (int5 < 1) {
								int5 = 1;
							}

							movingObject.ensureOnTile();
							if (movingObject instanceof IsoZombie && movingObject.getCurrentSquare() != null && movingObject.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && movingObject.getZ() == this.getZ()) {
								float float1 = 5.0F / (float)int4;
								float float2 = 5.0F / (float)int5;
								this.dangerLevels += float1 + float2;
							}

							if (int4 < 8 && int5 < 8 && movingObject.getCurrentSquare() != null && movingObject.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom()) {
								if (movingObject instanceof IsoSurvivor && !this.LastLocalNeutralList.contains(movingObject)) {
									this.Meet((IsoSurvivor)movingObject);
								}

								if (movingObject instanceof IsoSurvivor || movingObject instanceof IsoPlayer) {
									if (this.ClosestTwoSurvivors[0] == null) {
										this.ClosestTwoSurvivors[0] = (IsoGameCharacter)movingObject;
									} else if (this.ClosestTwoSurvivors[1] == null) {
										this.ClosestTwoSurvivors[1] = (IsoGameCharacter)movingObject;
									}
								}

								if (int4 < 3 && int5 < 3 && this.getZ() == movingObject.getZ() && movingObject.getCurrentSquare() != null && movingObject.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && movingObject instanceof IsoZombie) {
									this.VeryCloseEnemyList.add(movingObject);
								}
							}

							if (movingObject.getCurrentSquare() != null && movingObject.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom() && movingObject.getCurrentSquare() != null && this.getCurrentSquare() != null && !(movingObject instanceof IsoZombie) && this.EnemyList.contains((IsoGameCharacter)movingObject)) {
							}

							if (this instanceof IsoGameCharacter) {
								if (!(movingObject instanceof IsoZombie) && !this.EnemyList.contains((IsoGameCharacter)movingObject)) {
									if (this.descriptor.Group == ((IsoGameCharacter)movingObject).descriptor.Group) {
										this.LocalGroupList.add((IsoGameCharacter)movingObject);
									}

									this.LocalNeutralList.add((IsoGameCharacter)movingObject);
								} else if (!(movingObject instanceof IsoZombie) || !((IsoZombie)movingObject).Ghost) {
									this.LocalRelevantEnemyList.add((IsoGameCharacter)movingObject);
									this.LocalEnemyList.add((IsoGameCharacter)movingObject);
									movingObject.spotted(this, false);
								}
							}
						}
					}
				}

				if (this.LastLocalEnemies < this.LocalEnemyList.size()) {
				}
			}

			if (!this.getAllowBehaviours()) {
				this.setNx(this.getScriptnx());
				this.setNy(this.getScriptny());
			}

			if (this.getTimeSinceZombieAttack() == 1) {
				this.masterBehaviorList.reset();
			}

			super.update();
			if (this.stateMachine.getCurrent() != StaggerBackState.instance() && this.stateMachine.getCurrent() != StaggerBackDieState.instance() && this.stateMachine.getCurrent() != FakeDeadZombieState.instance() && this.stateMachine.getCurrent() != ReanimateState.instance()) {
				if (this.behaviours != null) {
					this.behaviours.SetTriggerValue("Hunger", this.stats.hunger);
					this.behaviours.SetTriggerValue("IdleBoredom", this.stats.idleboredom);
				}

				Vector2 vector2 = new Vector2(this.getNx() - this.getLx(), this.getNy() - this.getLy());
				if ((this.Health <= 0.0F || this.BodyDamage.getHealth() < 0.0F) && this == TutorialManager.instance.wife && !this.NoGoreDeath) {
					this.PlayAnim("death");
				}

				vector2.x *= this.getGlobalMovementMod();
				vector2.y *= this.getGlobalMovementMod();
				if (this.Dragging) {
					this.HasBeenDragged = true;
					if (IsoPlayer.instance.dir == IsoDirections.N || IsoPlayer.instance.dir == IsoDirections.S || IsoPlayer.instance.dir == IsoDirections.E || IsoPlayer.instance.dir == IsoDirections.W) {
						this.dir = IsoPlayer.instance.dir;
					}
				}

				if (this.HasBeenDragged) {
					this.sprite.PlayAnim("dragged");
				}

				if (this != TutorialManager.instance.wife) {
					if (this.RemoteID == -1 && !this.isDead()) {
						if (vector2.getLength() > 0.0F && this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
							if (this.stateMachine.getCurrent() != SwipeStatePlayer.instance()) {
								this.def.setFrameSpeedPerFrame(0.3F);
							}

							if (vector2.getLength() > 0.07F && this.lmove.getLength() > 0.07F) {
								this.def.Looped = true;
								this.PlayAnimNoReset("Run");
								this.def.Finished = false;
							} else {
								this.def.Looped = true;
								this.PlayAnimNoReset("Walk");
								this.def.Finished = false;
							}
						} else if (this.lmove.getLength() == 0.0F) {
							InventoryItem inventoryItem;
							if (this.legsSprite != null && !this.legsSprite.CurrentAnim.name.contains("Attack_")) {
								this.def.setFrameSpeedPerFrame(0.1F);
								if (this.aimAt == null) {
									this.PlayAnim("Idle");
								} else {
									inventoryItem = this.leftHandItem;
									if (inventoryItem instanceof HandWeapon && inventoryItem.getSwingAnim() != null) {
										this.useHandWeapon = (HandWeapon)inventoryItem;
										this.PlayAnimFrame("Attack_" + inventoryItem.getSwingAnim(), 0);
									} else {
										this.def.setFrameSpeedPerFrame(0.1F);
										this.PlayAnim("Idle");
									}
								}
							} else if (this.aimAt != null) {
								inventoryItem = this.leftHandItem;
								if (inventoryItem instanceof HandWeapon && inventoryItem.getSwingAnim() != null) {
									this.useHandWeapon = (HandWeapon)inventoryItem;
									this.PlayAnimFrame("Attack_" + inventoryItem.getSwingAnim(), 0);
								} else {
									this.def.setFrameSpeedPerFrame(0.1F);
									this.PlayAnim("Idle");
								}

								Vector2 vector22 = new Vector2(this.getX(), this.getY());
								Vector2 vector23 = new Vector2(this.aimAt.getX(), this.aimAt.getY());
								vector23.x -= vector22.x;
								vector23.y -= vector22.y;
								vector23.normalize();
								this.DirectionFromVector(vector23);
								this.angle.x = vector23.x;
								this.angle.y = vector23.y;
								if (this.aimAt.Health <= 0.0F || this.aimAt.BodyDamage.getHealth() <= 0.0F) {
									this.aimAt = null;
								}
							}
						}
					}

					this.seperate();
					this.lmove.x = vector2.x;
					this.lmove.y = vector2.y;
					--this.repathDelay;
				}
			}
		} else {
			this.stateMachine.changeState(DieState.instance());
			this.stateMachine.Lock = true;
			super.update();
		}
	}

	public void SetAllFrames(short short1) {
		this.def.Frame = (float)short1;
	}

	public void renderlast() {
		super.renderlast();
		if (IsoCamera.CamCharacter == this) {
			IndieGL.End();
			byte byte1 = 50;
			int int1 = byte1 + 20;
			this.masterProper.renderDebug(int1);
		}
	}

	private void CreateBehaviors() {
		if (this.Personality != null) {
			this.Personality.CreateBehaviours(this);
		}
	}

	public void OnDeath() {
		if (this == TutorialManager.instance.wife && !this.NoGoreDeath) {
			this.PlayAnimUnlooped("death");
		}
	}

	public void Aim(IsoGameCharacter gameCharacter) {
		this.aimAt = gameCharacter;
	}

	private void Meet(IsoGameCharacter gameCharacter) {
		if (this.tryToTeamUp) {
			if (this.RemoteID == -1) {
				if (gameCharacter.getCurrentSquare().getRoom() == this.getCurrentSquare().getRoom()) {
					if (gameCharacter.getAllowBehaviours()) {
						if (this.getAllowBehaviours()) {
							this.descriptor.meet(gameCharacter.descriptor);
							if (!this.MeetList.contains(gameCharacter.descriptor.ID)) {
								if (!gameCharacter.getActiveInInstances().isEmpty() || !this.getActiveInInstances().isEmpty()) {
									return;
								}

								if (gameCharacter.Speaking || this.Speaking) {
									return;
								}

								this.MeetList.add(gameCharacter.descriptor.ID);
								gameCharacter.MeetList.add(this.descriptor.ID);
								if (gameCharacter.descriptor.Group != this.descriptor.Group && !(gameCharacter instanceof IsoPlayer)) {
									this.MeetFirstTime(gameCharacter);
								}

								LuaEventManager.triggerEvent("OnCharacterMeet", this, gameCharacter, 0);
							} else {
								if (!gameCharacter.getActiveInInstances().isEmpty() || !this.getActiveInInstances().isEmpty()) {
									return;
								}

								if (gameCharacter.Speaking || this.Speaking) {
									return;
								}

								LuaEventManager.triggerEvent("OnCharacterMeet", this, gameCharacter, (Integer)this.descriptor.MetCount.get(gameCharacter.descriptor.ID) - 1);
							}
						}
					}
				}
			}
		}
	}

	private void MeetAgain(IsoGameCharacter gameCharacter) {
		Integer integer;
		if (gameCharacter.BodyDamage.getNumPartsBitten() <= 0 && gameCharacter.BodyDamage.getNumPartsScratched() <= 0) {
			integer = null;
			String string = "Base.MeetAgain";
			Object object = null;
			Object object2 = null;
			if (gameCharacter instanceof IsoSurvivor) {
				if (Rand.Next(2) == 0) {
					object = this;
					object2 = gameCharacter;
				} else {
					object = gameCharacter;
					object2 = this;
				}
			} else {
				object = this;
				object2 = gameCharacter;
			}

			if (IsoPlayer.instance != null && IsoPlayer.instance.GhostMode) {
			}

			Integer integer2 = ScriptManager.instance.getFlagIntValue(string + "Count");
			ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer2) + 1), "Met", (IsoGameCharacter)object2, "Other", (IsoGameCharacter)object);
		} else if (this.BodyDamage.getNumPartsBitten() <= 0 && this.BodyDamage.getNumPartsScratched() <= 0) {
			integer = ScriptManager.instance.getFlagIntValue("Base.YouBeenBitCount");
			if (IsoPlayer.instance != null && IsoPlayer.instance.GhostMode) {
			}

			ScriptManager.instance.PlayInstanceScript((String)null, "Base.YouBeenBit" + (Rand.Next(integer) + 1), "Bitten", gameCharacter, "Other", this);
		}
	}

	public void FollowMe(IsoGameCharacter gameCharacter) {
		String string = null;
		string = "Base.FollowMe";
		ScriptManager.instance.PlayInstanceScript((String)null, string, "Follower", this, "Leader", gameCharacter);
	}

	public void StayHere(IsoGameCharacter gameCharacter) {
		String string = null;
		string = "Base.StayHere";
		ScriptManager.instance.PlayInstanceScript((String)null, string, "Follower", this, "Leader", gameCharacter);
	}

	public void Guard(IsoPlayer player) {
		player.GuardModeUI = 1;
		player.GuardChosen = this;
		String string = null;
		string = "Base.GuardA";
		ScriptManager.instance.PlayInstanceScript((String)null, string, "Follower", this, "Leader", player);
	}

	public void DoGuard(IsoPlayer player) {
		String string = null;
		string = "Base.GuardB";
		ScriptManager.instance.PlayInstanceScript((String)null, string, "Follower", this, "Leader", player);
		this.Orders.push(new GuardOrder(this, player.GuardStand, player.GuardFace));
	}

	public void MeetFirstTime(IsoGameCharacter gameCharacter, boolean boolean1, boolean boolean2) {
		if (this.tryToTeamUp) {
			if (boolean2 && (gameCharacter.BodyDamage.getNumPartsBitten() > 0 || gameCharacter.BodyDamage.getNumPartsScratched() > 0)) {
				if (this.BodyDamage.getNumPartsBitten() <= 0 && this.BodyDamage.getNumPartsScratched() <= 0) {
					Integer integer = ScriptManager.instance.getFlagIntValue("Base.YouBeenBitCount");
					ScriptManager.instance.PlayInstanceScript((String)null, "Base.YouBeenBit" + (Rand.Next(integer) + 1), "Bitten", gameCharacter, "Other", this);
				}
			} else {
				String string = null;
				string = "Base.FirstMeet";
				Object object = null;
				Object object2 = null;
				if (gameCharacter instanceof IsoSurvivor) {
					if (Rand.Next(2) == 0) {
						object = this;
						object2 = gameCharacter;
					} else {
						object = gameCharacter;
						object2 = this;
					}
				} else if (boolean1) {
					object2 = this;
					object = gameCharacter;
				} else {
					object = this;
					object2 = gameCharacter;
				}

				Integer integer2 = ScriptManager.instance.getFlagIntValue(string + "Count");
				ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer2) + 1), "Met", (IsoGameCharacter)object2, "Other", (IsoGameCharacter)object);
			}
		}
	}

	public void MeetFirstTime(IsoGameCharacter gameCharacter) {
		this.MeetFirstTime(gameCharacter, false, true);
	}

	public void Killed(IsoGameCharacter gameCharacter) {
		if (!this.Speaking) {
			if (this.getActiveInInstances().isEmpty()) {
				if (Rand.Next(30) == 0) {
					IsoGameCharacter gameCharacter2 = this.ClosestTwoSurvivors[0];
					IsoGameCharacter gameCharacter3 = this.ClosestTwoSurvivors[1];
					Integer integer = 3;
					if (gameCharacter3 == null || !gameCharacter3.getActiveInInstances().isEmpty() || gameCharacter3.DistTo(this) > 8.0F) {
						gameCharacter3 = null;
						integer = integer - 1;
					}

					if (gameCharacter2 != null && (!gameCharacter2.getActiveInInstances().isEmpty() || gameCharacter2.DistTo(this) > 8.0F)) {
						gameCharacter2 = gameCharacter3;
						gameCharacter3 = null;
						integer = integer - 1;
					} else if (gameCharacter2 == null) {
						integer = integer - 1;
					}

					String string = "Base.Killed";
					integer = Rand.Next(integer) + 1;
					if (gameCharacter instanceof IsoZombie) {
						string = string + "Zombie_";
					} else {
						string = string + "Survivor_";
					}

					string = string + integer + "Man";
					Integer integer2 = ScriptManager.instance.getFlagIntValue(string + "Count");
					if (integer == 3) {
						ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer2) + 1), "B", gameCharacter3, "A", gameCharacter2, "Killer", this);
					}

					if (integer == 2) {
						ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer2) + 1), "A", gameCharacter2, "Killer", this);
					}

					if (integer == 1) {
						ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer2) + 1), (String)"Killer", (IsoGameCharacter)this);
					}
				}
			}
		}
	}

	public void ChewedByZombies() {
		IsoGameCharacter gameCharacter = this.ClosestTwoSurvivors[0];
		IsoGameCharacter gameCharacter2 = this.ClosestTwoSurvivors[1];
		Integer integer = 3;
		if (gameCharacter2 == null || !gameCharacter2.getActiveInInstances().isEmpty()) {
			gameCharacter2 = null;
			integer = integer - 1;
		}

		if (gameCharacter == null || gameCharacter.getActiveInInstances().isEmpty() && !(gameCharacter.DistTo(this) > 8.0F)) {
			if (gameCharacter == null) {
				integer = integer - 1;
			}
		} else {
			gameCharacter = gameCharacter2;
			gameCharacter2 = null;
			integer = integer - 1;
		}

		if (integer == 3) {
			integer = 2;
		}

		integer = Rand.Next(integer) + 1;
		String string = "Base.ChewedByZombies";
		string = string + "_" + integer + "Man";
		Integer integer2 = ScriptManager.instance.getFlagIntValue(string + "Count");
		if (integer == 2) {
			ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer2) + 1), "A", gameCharacter, "Chewed", this);
		}

		if (integer == 1) {
			ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer2) + 1), (String)"Chewed", (IsoGameCharacter)this);
		}
	}

	private void DoRandomTalk() {
	}

	public void GivenItemBy(IsoGameCharacter gameCharacter, String string, boolean boolean1) {
		if (!this.Speaking) {
			if (this.getActiveInInstances().isEmpty()) {
				String string2 = null;
				string2 = "Base.GivenItem";
				if (boolean1) {
					string2 = string2 + "Needed";
				} else {
					string2 = string2 + "Unneeded";
				}

				Object object = null;
				Object object2 = null;
				Integer integer = ScriptManager.instance.getFlagIntValue(string2 + "Count");
				ScriptManager.instance.PlayInstanceScript((String)null, string2 + (Rand.Next(integer) + 1), "Giver", gameCharacter, "Taker", this);
			}
		}
	}

	public void PatchedUpBy(IsoGameCharacter gameCharacter) {
		if (!this.Speaking) {
			if (this.getActiveInInstances().isEmpty()) {
				String string = null;
				string = "Base.PatchedUp";
				Object object = null;
				Object object2 = null;
				Integer integer = ScriptManager.instance.getFlagIntValue(string + "Count");
				ScriptManager.instance.PlayInstanceScript((String)null, string + (Rand.Next(integer) + 1), "Medic", gameCharacter, "Hurt", this);
			}
		}
	}

	public Stack getAvailableMembers() {
		this.availableTemp.clear();
		for (int int1 = 0; int1 < this.descriptor.Group.Members.size(); ++int1) {
			boolean boolean1 = false;
			SurvivorDesc survivorDesc = (SurvivorDesc)this.descriptor.Group.Members.get(int1);
			if (survivorDesc != this.descriptor) {
				if (survivorDesc.Instance.getCurrentSquare().getRoom() != null && this.getCurrentSquare().getRoom() != null && survivorDesc.Instance.getCurrentSquare().getRoom().building == this.getCurrentSquare().getRoom().building) {
					boolean1 = true;
				}

				if (survivorDesc.Instance.DistTo(this) < 10.0F) {
					boolean1 = true;
				}

				if (boolean1) {
					this.availableTemp.add(survivorDesc.Instance);
				}
			}
		}

		return this.availableTemp;
	}

	private boolean HasPersonalNeed(String string) {
		for (int int1 = 0; int1 < this.PersonalNeeds.size(); ++int1) {
			if (((Order)this.PersonalNeeds.get(int1)).type.equals(string)) {
				return true;
			}
		}

		return false;
	}

	public boolean SatisfiedWithInventory(LootBuilding.LootStyle lootStyle, IsoSurvivor.SatisfiedBy satisfiedBy) {
		float float1 = 0.0F;
		switch (satisfiedBy) {
		case Food: 
			float1 = this.inventory.getTotalFoodScore(this.descriptor);
			if (float1 > (float)SatisfiedByFoodLevel) {
				return true;
			}

		
		case Weapons: 
			float1 = this.inventory.getTotalWeaponScore(this.descriptor);
			if (float1 > (float)SatisfiedByWeaponLevel) {
				return true;
			}

		
		default: 
			return false;
		
		}
	}

	public boolean getTryToTeamUp() {
		return this.tryToTeamUp;
	}

	public void setTryToTeamUp(boolean boolean1) {
		this.tryToTeamUp = boolean1;
	}

	public void reloadSpriteColors() {
		InventoryItem inventoryItem = this.ClothingItem_Torso;
		if (inventoryItem != null && this.topSprite != null && this.descriptor.topColor != null) {
			this.topSprite.TintMod.r = this.descriptor.topColor.r;
			this.topSprite.TintMod.g = this.descriptor.topColor.g;
			this.topSprite.TintMod.b = this.descriptor.topColor.b;
			this.topSprite.TintMod.desaturate(0.5F);
		}

		InventoryItem inventoryItem2 = this.ClothingItem_Legs;
		if (inventoryItem2 != null && this.bottomsSprite != null && this.descriptor.trouserColor != null) {
			this.bottomsSprite.TintMod.r = this.descriptor.trouserColor.r;
			this.bottomsSprite.TintMod.g = this.descriptor.trouserColor.g;
			this.bottomsSprite.TintMod.b = this.descriptor.trouserColor.b;
			this.bottomsSprite.TintMod.desaturate(0.5F);
		}
	}
	public static enum SatisfiedBy {

		Food,
		Weapons,
		Water;
	}
}
