package zombie.ai.states;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjglx.input.Keyboard;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaHookManager;
import zombie.ai.State;
import zombie.audio.parameters.ParameterMeleeHitSurface;
import zombie.audio.parameters.ParameterZombieState;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.Faction;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoLivingCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.Stats;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.Moodles.MoodleType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.network.ByteBufferWriter;
import zombie.core.skinnedmodel.advancedanimation.AnimEvent;
import zombie.core.skinnedmodel.animation.AnimationPlayer;
import zombie.core.skinnedmodel.model.Model;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.Clothing;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponType;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.Vector3;
import zombie.iso.areas.NonPvpZone;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.network.packets.HitPacket;
import zombie.popman.ObjectPool;
import zombie.scripting.objects.VehicleScript;
import zombie.ui.MoodlesUI;
import zombie.ui.UIManager;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;


public final class SwipeStatePlayer extends State {
	private static final SwipeStatePlayer _instance = new SwipeStatePlayer();
	static final Integer PARAM_LOWER_CONDITION = 0;
	static final Integer PARAM_ATTACKED = 1;
	public static final ArrayList HitList = new ArrayList();
	private static final ArrayList HitList2 = new ArrayList();
	private static final Vector2 tempVector2_1 = new Vector2();
	private static final Vector2 tempVector2_2 = new Vector2();
	private final ArrayList dotList = new ArrayList();
	private boolean bHitOnlyTree;
	public final ObjectPool hitInfoPool = new ObjectPool(SwipeStatePlayer.HitInfo::new);
	private static final SwipeStatePlayer.CustomComparator Comparator = new SwipeStatePlayer.CustomComparator();
	static final Vector3 tempVector3_1 = new Vector3();
	static final Vector3 tempVector3_2 = new Vector3();
	static final Vector3 tempVectorBonePos = new Vector3();
	static final ArrayList movingStatic = new ArrayList();
	public final SwipeStatePlayer.AttackVars attackVars = new SwipeStatePlayer.AttackVars();
	private final Vector4f tempVector4f = new Vector4f();
	private final SwipeStatePlayer.WindowVisitor windowVisitor = new SwipeStatePlayer.WindowVisitor();

	public static SwipeStatePlayer instance() {
		return _instance;
	}

	public static void WeaponLowerCondition(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (handWeapon.getUses() > 1) {
			handWeapon.Use();
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem(handWeapon.getFullType());
			inventoryItem.setCondition(handWeapon.getCondition() - 1);
			handWeapon.getContainer().AddItem(inventoryItem);
			gameCharacter.setPrimaryHandItem(inventoryItem);
		} else {
			handWeapon.setCondition(handWeapon.getCondition() - 1);
		}
	}

	private static HandWeapon GetWeapon(IsoGameCharacter gameCharacter) {
		HandWeapon handWeapon = gameCharacter.getUseHandWeapon();
		if (((IsoLivingCharacter)gameCharacter).bDoShove || gameCharacter.isForceShove()) {
			handWeapon = ((IsoLivingCharacter)gameCharacter).bareHands;
		}

		return handWeapon;
	}

	private void doAttack(IsoPlayer player, float float1, boolean boolean1, String string, SwipeStatePlayer.AttackVars attackVars) {
		player.setForceShove(boolean1);
		player.setClickSound(string);
		if (boolean1) {
			float1 *= 2.0F;
		}

		if (float1 > 90.0F) {
			float1 = 90.0F;
		}

		float1 /= 25.0F;
		player.useChargeDelta = float1;
		Object object = player.getPrimaryHandItem();
		if (object == null || !(object instanceof HandWeapon) || boolean1 || attackVars.bDoShove) {
			object = player.bareHands;
		}

		if (object instanceof HandWeapon) {
			player.setUseHandWeapon((HandWeapon)object);
			if (player.PlayerIndex == 0 && player.JoypadBind == -1 && UIManager.getPicked() != null && (!GameClient.bClient || player.isLocalPlayer())) {
				if (UIManager.getPicked().tile instanceof IsoMovingObject) {
					player.setAttackTargetSquare(((IsoMovingObject)UIManager.getPicked().tile).getCurrentSquare());
				} else {
					player.setAttackTargetSquare(UIManager.getPicked().square);
				}
			}

			player.setRecoilDelay((float)attackVars.recoilDelay);
			if (boolean1) {
				player.setRecoilDelay(10.0F);
			}
		}
	}

	public void enter(IsoGameCharacter gameCharacter) {
		if ("HitReaction".equals(gameCharacter.getHitReaction())) {
			gameCharacter.clearVariable("HitReaction");
		}

		UIManager.speedControls.SetCurrentGameSpeed(1);
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		hashMap.put(PARAM_LOWER_CONDITION, Boolean.FALSE);
		hashMap.put(PARAM_ATTACKED, Boolean.FALSE);
		if (!(gameCharacter instanceof IsoPlayer) || !((IsoPlayer)gameCharacter).bRemote) {
			gameCharacter.updateRecoilVar();
		}

		if ("Auto".equals(gameCharacter.getVariableString("FireMode"))) {
			gameCharacter.setVariable("autoShootSpeed", 4.0F * GameTime.getAnimSpeedFix());
			gameCharacter.setVariable("autoShootVarY", 0.0F);
			if (System.currentTimeMillis() - gameCharacter.lastAutomaticShoot < 600L) {
				++gameCharacter.shootInARow;
				float float1 = Math.max(0.0F, 1.0F - (float)gameCharacter.shootInARow / 20.0F);
				gameCharacter.setVariable("autoShootVarX", float1);
				gameCharacter.setVariable("autoShootSpeed", (4.0F - (float)gameCharacter.shootInARow / 10.0F) * GameTime.getAnimSpeedFix());
			} else {
				gameCharacter.setVariable("autoShootVarX", 1.0F);
				gameCharacter.shootInARow = 0;
			}

			gameCharacter.lastAutomaticShoot = System.currentTimeMillis();
		}

		IsoPlayer player = (IsoPlayer)gameCharacter;
		gameCharacter.setVariable("ShotDone", false);
		gameCharacter.setVariable("ShoveAnim", false);
		this.CalcAttackVars((IsoLivingCharacter)gameCharacter, this.attackVars);
		this.doAttack(player, 2.0F, gameCharacter.isForceShove(), gameCharacter.getClickSound(), this.attackVars);
		HandWeapon handWeapon = gameCharacter.getUseHandWeapon();
		gameCharacter.setVariable("AimFloorAnim", this.attackVars.bAimAtFloor);
		LuaEventManager.triggerEvent("OnWeaponSwing", gameCharacter, handWeapon);
		if (LuaHookManager.TriggerHook("WeaponSwing", gameCharacter, handWeapon)) {
			gameCharacter.getStateMachine().revertToPreviousState(this);
		}

		gameCharacter.StopAllActionQueue();
		if (((IsoPlayer)gameCharacter).isLocalPlayer()) {
			IsoWorld.instance.CurrentCell.setDrag((KahluaTable)null, ((IsoPlayer)gameCharacter).PlayerIndex);
		}

		handWeapon = this.attackVars.weapon;
		player.setAimAtFloor(this.attackVars.bAimAtFloor);
		boolean boolean1 = player.bDoShove;
		player.bDoShove = this.attackVars.bDoShove;
		player.useChargeDelta = this.attackVars.useChargeDelta;
		player.targetOnGround = this.attackVars.targetOnGround;
		if (!player.bDoShove && !boolean1 && player.getClickSound() == null && handWeapon.getPhysicsObject() == null && !handWeapon.isRanged()) {
		}

		if (GameClient.bClient && gameCharacter == IsoPlayer.getInstance()) {
			GameClient.instance.sendPlayer((IsoPlayer)gameCharacter);
		}

		if (!player.bDoShove && !boolean1 && !handWeapon.isRanged() && player.isLocalPlayer()) {
			gameCharacter.playSound(handWeapon.getSwingSound());
		} else if ((player.bDoShove || boolean1) && player.isLocalPlayer()) {
			if (player.targetOnGround != null) {
				gameCharacter.playSound("AttackStomp");
			} else {
				gameCharacter.playSound("AttackShove");
			}
		}
	}

	public void execute(IsoGameCharacter gameCharacter) {
		gameCharacter.StopAllActionQueue();
	}

	private int DoSwingCollisionBoneCheck(IsoGameCharacter gameCharacter, HandWeapon handWeapon, IsoGameCharacter gameCharacter2, int int1, float float1) {
		movingStatic.clear();
		float float2 = handWeapon.WeaponLength;
		float2 += 0.5F;
		if (gameCharacter.isAimAtFloor() && ((IsoLivingCharacter)gameCharacter).bDoShove) {
			float2 = 0.3F;
		}

		Model.BoneToWorldCoords(gameCharacter2, int1, tempVectorBonePos);
		for (int int2 = 1; int2 <= 10; ++int2) {
			float float3 = (float)int2 / 10.0F;
			tempVector3_1.x = gameCharacter.x;
			tempVector3_1.y = gameCharacter.y;
			tempVector3_1.z = gameCharacter.z;
			Vector3 vector3 = tempVector3_1;
			vector3.x += gameCharacter.getForwardDirection().x * float2 * float3;
			vector3 = tempVector3_1;
			vector3.y += gameCharacter.getForwardDirection().y * float2 * float3;
			tempVector3_1.x = tempVectorBonePos.x - tempVector3_1.x;
			tempVector3_1.y = tempVectorBonePos.y - tempVector3_1.y;
			tempVector3_1.z = 0.0F;
			boolean boolean1 = tempVector3_1.getLength() < float1;
			if (boolean1) {
				return int1;
			}
		}

		return -1;
	}

	public void animEvent(IsoGameCharacter gameCharacter, AnimEvent animEvent) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		if (animEvent.m_EventName.equalsIgnoreCase("ActiveAnimFinishing") || animEvent.m_EventName.equalsIgnoreCase("NonLoopedAnimFadeOut")) {
			boolean boolean1 = hashMap.get(PARAM_LOWER_CONDITION) == Boolean.TRUE;
			if (boolean1 && !gameCharacter.isRangedWeaponEmpty()) {
				hashMap.put(PARAM_LOWER_CONDITION, Boolean.FALSE);
				HandWeapon handWeapon = GetWeapon(gameCharacter);
				int int1 = handWeapon.getConditionLowerChance();
				if (gameCharacter instanceof IsoPlayer && "charge".equals(((IsoPlayer)gameCharacter).getAttackType())) {
					int1 = (int)((double)int1 / 1.5);
				}

				if (Rand.Next(int1 + gameCharacter.getMaintenanceMod() * 2) == 0) {
					WeaponLowerCondition(handWeapon, gameCharacter);
				} else if (Rand.NextBool(2) && !handWeapon.isRanged() && !handWeapon.getName().contains("Bare Hands")) {
					if (handWeapon.isTwoHandWeapon() && (gameCharacter.getPrimaryHandItem() != handWeapon || gameCharacter.getSecondaryHandItem() != handWeapon) && Rand.NextBool(3)) {
						return;
					}

					gameCharacter.getXp().AddXP(PerkFactory.Perks.Maintenance, 1.0F);
				}
			}
		}

		if (animEvent.m_EventName.equalsIgnoreCase("AttackAnim")) {
			gameCharacter.setVariable("AttackAnim", Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("BlockTurn")) {
			gameCharacter.setIgnoreMovement(Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("ShoveAnim")) {
			gameCharacter.setVariable("ShoveAnim", Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("StompAnim")) {
			gameCharacter.setVariable("StompAnim", Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		HandWeapon handWeapon2 = GetWeapon(gameCharacter);
		if (animEvent.m_EventName.equalsIgnoreCase("AttackCollisionCheck") && hashMap.get(PARAM_ATTACKED) == Boolean.FALSE) {
			this.ConnectSwing(gameCharacter, handWeapon2);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("BlockMovement") && SandboxOptions.instance.AttackBlockMovements.getValue()) {
			gameCharacter.setVariable("SlowingMovement", Boolean.parseBoolean(animEvent.m_ParameterValue));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("WeaponEmptyCheck") && gameCharacter.getClickSound() != null) {
			if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
				return;
			}

			gameCharacter.playSound(gameCharacter.getClickSound());
			gameCharacter.setRecoilDelay(10.0F);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("ShotDone") && handWeapon2 != null && handWeapon2.isRackAfterShoot()) {
			gameCharacter.setVariable("ShotDone", true);
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SetVariable") && animEvent.m_ParameterValue.startsWith("ShotDone=")) {
			gameCharacter.setVariable("ShotDone", gameCharacter.getVariableBoolean("ShotDone") && handWeapon2 != null && handWeapon2.isRackAfterShoot());
		}

		if (animEvent.m_EventName.equalsIgnoreCase("playRackSound")) {
			if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
				return;
			}

			gameCharacter.playSound(handWeapon2.getRackSound());
		}

		if (animEvent.m_EventName.equalsIgnoreCase("playClickSound")) {
			if (gameCharacter instanceof IsoPlayer && !((IsoPlayer)gameCharacter).isLocalPlayer()) {
				return;
			}

			gameCharacter.playSound(handWeapon2.getClickSound());
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SetMeleeDelay")) {
			gameCharacter.setMeleeDelay(PZMath.tryParseFloat(animEvent.m_ParameterValue, 0.0F));
		}

		if (animEvent.m_EventName.equalsIgnoreCase("SitGroundStarted")) {
			gameCharacter.setVariable("SitGroundAnim", "Idle");
		}
	}

	public void exit(IsoGameCharacter gameCharacter) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		gameCharacter.setSprinting(false);
		((IsoPlayer)gameCharacter).setForceSprint(false);
		gameCharacter.setIgnoreMovement(false);
		gameCharacter.setVariable("ShoveAnim", false);
		gameCharacter.setVariable("StompAnim", false);
		gameCharacter.setVariable("AttackAnim", false);
		gameCharacter.setVariable("AimFloorAnim", false);
		((IsoPlayer)gameCharacter).setBlockMovement(false);
		if (gameCharacter.isAimAtFloor() && ((IsoLivingCharacter)gameCharacter).bDoShove) {
			Clothing clothing = (Clothing)gameCharacter.getWornItem("Shoes");
			byte byte1 = 10;
			int int1;
			if (clothing == null) {
				int1 = 3;
			} else {
				int1 = byte1 + clothing.getConditionLowerChance() / 2;
				if (Rand.Next(clothing.getConditionLowerChance()) == 0) {
					clothing.setCondition(clothing.getCondition() - 1);
				}
			}

			if (Rand.Next(int1) == 0) {
				if (clothing == null) {
					gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).AddDamage((float)Rand.Next(5, 10));
					gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).setAdditionalPain(gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).getAdditionalPain() + (float)Rand.Next(5, 10));
				} else {
					gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).AddDamage((float)Rand.Next(1, 5));
					gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).setAdditionalPain(gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Foot_R).getAdditionalPain() + (float)Rand.Next(1, 5));
				}
			}
		}

		HandWeapon handWeapon = GetWeapon(gameCharacter);
		gameCharacter.clearVariable("ZombieHitReaction");
		((IsoPlayer)gameCharacter).attackStarted = false;
		((IsoPlayer)gameCharacter).setAttackType((String)null);
		((IsoLivingCharacter)gameCharacter).bDoShove = false;
		gameCharacter.clearVariable("RackWeapon");
		gameCharacter.clearVariable("bShoveAiming");
		boolean boolean1 = hashMap.get(PARAM_ATTACKED) == Boolean.TRUE;
		if (handWeapon != null && (handWeapon.getCondition() <= 0 || boolean1 && handWeapon.isUseSelf())) {
			gameCharacter.removeFromHands(handWeapon);
			gameCharacter.getInventory().setDrawDirty(true);
		}

		((IsoPlayer)gameCharacter).NetRemoteState = 0;
		if (gameCharacter.isRangedWeaponEmpty()) {
			gameCharacter.setRecoilDelay(10.0F);
		}

		gameCharacter.setRangedWeaponEmpty(false);
		gameCharacter.setForceShove(false);
		gameCharacter.setClickSound((String)null);
		if (boolean1) {
			LuaEventManager.triggerEvent("OnPlayerAttackFinished", gameCharacter, handWeapon);
		}
	}

	public void CalcAttackVars(IsoLivingCharacter livingCharacter, SwipeStatePlayer.AttackVars attackVars) {
		HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(livingCharacter.getPrimaryHandItem(), HandWeapon.class);
		if (handWeapon != null && handWeapon.getOtherHandRequire() != null) {
			InventoryItem inventoryItem = livingCharacter.getSecondaryHandItem();
			if (inventoryItem == null || !inventoryItem.getType().equals(handWeapon.getOtherHandRequire())) {
				handWeapon = null;
			}
		}

		boolean boolean1 = livingCharacter.getVariableBoolean("AttackAnim") || livingCharacter.getVariableBoolean("ShoveAnim") || livingCharacter.getVariableBoolean("StompAnim");
		attackVars.weapon = handWeapon == null ? livingCharacter.bareHands : handWeapon;
		attackVars.targetOnGround = null;
		attackVars.bAimAtFloor = false;
		attackVars.bCloseKill = false;
		attackVars.bDoShove = livingCharacter.bDoShove;
		if (!boolean1) {
			livingCharacter.setVariable("ShoveAimX", 0.5F);
			livingCharacter.setVariable("ShoveAimY", 1.0F);
			if (attackVars.bDoShove && livingCharacter.getVariableBoolean("isMoving")) {
				livingCharacter.setVariable("ShoveAim", true);
			} else {
				livingCharacter.setVariable("ShoveAim", false);
			}
		}

		attackVars.useChargeDelta = livingCharacter.useChargeDelta;
		attackVars.recoilDelay = 0;
		boolean boolean2 = false;
		if (attackVars.weapon == livingCharacter.bareHands || attackVars.bDoShove || livingCharacter.isForceShove()) {
			attackVars.bDoShove = true;
			attackVars.bAimAtFloor = false;
			attackVars.weapon = livingCharacter.bareHands;
		}

		this.calcValidTargets(livingCharacter, attackVars.weapon, true, attackVars.targetsProne, attackVars.targetsStanding);
		SwipeStatePlayer.HitInfo hitInfo = attackVars.targetsStanding.isEmpty() ? null : (SwipeStatePlayer.HitInfo)attackVars.targetsStanding.get(0);
		SwipeStatePlayer.HitInfo hitInfo2 = attackVars.targetsProne.isEmpty() ? null : (SwipeStatePlayer.HitInfo)attackVars.targetsProne.get(0);
		if (this.isProneTargetBetter(livingCharacter, hitInfo, hitInfo2)) {
			hitInfo = null;
		}

		if (!boolean1) {
			livingCharacter.setAimAtFloor(false);
		}

		float float1 = Float.MAX_VALUE;
		if (hitInfo != null) {
			if (!boolean1) {
				livingCharacter.setAimAtFloor(false);
			}

			attackVars.bAimAtFloor = false;
			attackVars.targetOnGround = null;
			float1 = hitInfo.distSq;
		} else if (hitInfo2 != null && (Core.OptionAutoProneAtk || livingCharacter.bDoShove)) {
			if (!boolean1) {
				livingCharacter.setAimAtFloor(true);
			}

			attackVars.bAimAtFloor = true;
			attackVars.targetOnGround = (IsoGameCharacter)hitInfo2.object;
		}

		if (!(float1 >= attackVars.weapon.getMinRange() * attackVars.weapon.getMinRange()) && (hitInfo == null || !this.isWindowBetween(livingCharacter, hitInfo.object))) {
			if (livingCharacter.getStats().NumChasingZombies <= 1 && WeaponType.getWeaponType((IsoGameCharacter)livingCharacter) == WeaponType.knife) {
				attackVars.bCloseKill = true;
				return;
			}

			attackVars.bDoShove = true;
			IsoPlayer player = (IsoPlayer)Type.tryCastTo(livingCharacter, IsoPlayer.class);
			if (player != null && !player.isAuthorizeShoveStomp()) {
				attackVars.bDoShove = false;
			}

			attackVars.bAimAtFloor = false;
			if (livingCharacter.bareHands.getSwingAnim() != null) {
				attackVars.useChargeDelta = 3.0F;
			}
		}

		int int1 = Core.getInstance().getKey("ManualFloorAtk");
		int int2 = Core.getInstance().getKey("Sprint");
		boolean boolean3 = livingCharacter.getVariableBoolean("StartedAttackWhileSprinting");
		if (Keyboard.isKeyDown(int1) && (int1 != int2 || !boolean3)) {
			attackVars.bAimAtFloor = true;
			attackVars.bDoShove = false;
			livingCharacter.bDoShove = false;
		}

		if (attackVars.weapon.isRanged()) {
			int int3 = attackVars.weapon.getRecoilDelay();
			Float Float1 = (float)int3 * (1.0F - (float)livingCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 30.0F);
			attackVars.recoilDelay = Float1.intValue();
			Float1 = 1.0F;
			livingCharacter.setVariable("singleShootSpeed", (0.8F + (float)livingCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 10.0F) * GameTime.getAnimSpeedFix());
		}
	}

	public void calcValidTargets(IsoLivingCharacter livingCharacter, HandWeapon handWeapon, boolean boolean1, ArrayList arrayList, ArrayList arrayList2) {
		this.hitInfoPool.release((List)arrayList);
		this.hitInfoPool.release((List)arrayList2);
		arrayList.clear();
		arrayList2.clear();
		float float1 = Core.getInstance().getIgnoreProneZombieRange();
		float float2 = handWeapon.getMaxRange() * handWeapon.getRangeMod(livingCharacter);
		float float3 = Math.max(float1, float2 + (boolean1 ? 1.0F : 0.0F));
		ArrayList arrayList3 = IsoWorld.instance.CurrentCell.getObjectList();
		for (int int1 = 0; int1 < arrayList3.size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)arrayList3.get(int1);
			SwipeStatePlayer.HitInfo hitInfo = this.calcValidTarget(livingCharacter, handWeapon, movingObject, float3);
			if (hitInfo != null) {
				if (isStanding(movingObject)) {
					arrayList2.add(hitInfo);
				} else {
					arrayList.add(hitInfo);
				}
			}
		}

		if (!arrayList.isEmpty() && this.shouldIgnoreProneZombies(livingCharacter, arrayList2, float1)) {
			this.hitInfoPool.release((List)arrayList);
			arrayList.clear();
		}

		float float4 = handWeapon.getMinAngle();
		float float5 = handWeapon.getMaxAngle();
		if (handWeapon.isRanged()) {
			float4 -= handWeapon.getAimingPerkMinAngleModifier() * ((float)livingCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0F);
		}

		this.removeUnhittableTargets(livingCharacter, handWeapon, float4, float5, boolean1, arrayList2);
		float4 = handWeapon.getMinAngle();
		float4 = (float)((double)float4 / 1.5);
		this.removeUnhittableTargets(livingCharacter, handWeapon, float4, float5, boolean1, arrayList);
		arrayList2.sort(Comparator);
		arrayList.sort(Comparator);
	}

	private boolean shouldIgnoreProneZombies(IsoGameCharacter gameCharacter, ArrayList arrayList, float float1) {
		if (float1 <= 0.0F) {
			return false;
		} else {
			boolean boolean1 = gameCharacter.isInvisible() || gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isGhostMode();
			for (int int1 = 0; int1 < arrayList.size(); ++int1) {
				SwipeStatePlayer.HitInfo hitInfo = (SwipeStatePlayer.HitInfo)arrayList.get(int1);
				IsoZombie zombie = (IsoZombie)Type.tryCastTo(hitInfo.object, IsoZombie.class);
				if ((zombie == null || zombie.target != null || boolean1) && !(hitInfo.distSq > float1 * float1)) {
					boolean boolean2 = PolygonalMap2.instance.lineClearCollide(gameCharacter.x, gameCharacter.y, hitInfo.object.x, hitInfo.object.y, (int)gameCharacter.z, gameCharacter, false, true);
					if (!boolean2) {
						return true;
					}
				}
			}

			return false;
		}
	}

	private boolean isUnhittableTarget(IsoGameCharacter gameCharacter, HandWeapon handWeapon, float float1, float float2, SwipeStatePlayer.HitInfo hitInfo, boolean boolean1) {
		if (!(hitInfo.dot < float1) && !(hitInfo.dot > float2)) {
			Vector3 vector3 = tempVectorBonePos.set(hitInfo.x, hitInfo.y, hitInfo.z);
			return !gameCharacter.IsAttackRange(handWeapon, hitInfo.object, vector3, boolean1);
		} else {
			return true;
		}
	}

	private void removeUnhittableTargets(IsoGameCharacter gameCharacter, HandWeapon handWeapon, float float1, float float2, boolean boolean1, ArrayList arrayList) {
		for (int int1 = arrayList.size() - 1; int1 >= 0; --int1) {
			SwipeStatePlayer.HitInfo hitInfo = (SwipeStatePlayer.HitInfo)arrayList.get(int1);
			if (this.isUnhittableTarget(gameCharacter, handWeapon, float1, float2, hitInfo, boolean1)) {
				this.hitInfoPool.release((Object)hitInfo);
				arrayList.remove(int1);
			}
		}
	}

	private boolean getNearestTargetPosAndDot(IsoGameCharacter gameCharacter, HandWeapon handWeapon, IsoMovingObject movingObject, boolean boolean1, Vector4f vector4f) {
		this.getNearestTargetPosAndDot(gameCharacter, movingObject, vector4f);
		float float1 = vector4f.w;
		float float2 = handWeapon.getMinAngle();
		float float3 = handWeapon.getMaxAngle();
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
		if (gameCharacter2 != null) {
			if (isStanding(movingObject)) {
				if (handWeapon.isRanged()) {
					float2 -= handWeapon.getAimingPerkMinAngleModifier() * ((float)gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) / 2.0F);
				}
			} else {
				float2 /= 1.5F;
			}
		}

		if (!(float1 < float2) && !(float1 > float3)) {
			Vector3 vector3 = tempVectorBonePos.set(vector4f.x, vector4f.y, vector4f.z);
			return gameCharacter.IsAttackRange(handWeapon, movingObject, vector3, boolean1);
		} else {
			return false;
		}
	}

	private void getNearestTargetPosAndDot(IsoGameCharacter gameCharacter, Vector3 vector3, Vector2 vector2, Vector4f vector4f) {
		float float1 = gameCharacter.getDotWithForwardDirection(vector3);
		float1 = PZMath.clamp(float1, -1.0F, 1.0F);
		vector4f.w = Math.max(float1, vector4f.w);
		float float2 = IsoUtils.DistanceToSquared(gameCharacter.x, gameCharacter.y, (float)((int)gameCharacter.z * 3), vector3.x, vector3.y, (float)((int)Math.max(vector3.z, 0.0F) * 3));
		if (float2 < vector2.x) {
			vector2.x = float2;
			vector4f.set(vector3.x, vector3.y, vector3.z, vector4f.w);
		}
	}

	private void getNearestTargetPosAndDot(IsoGameCharacter gameCharacter, IsoMovingObject movingObject, String string, Vector2 vector2, Vector4f vector4f) {
		Vector3 vector3 = getBoneWorldPos(movingObject, string, tempVectorBonePos);
		this.getNearestTargetPosAndDot(gameCharacter, vector3, vector2, vector4f);
	}

	private void getNearestTargetPosAndDot(IsoGameCharacter gameCharacter, IsoMovingObject movingObject, Vector4f vector4f) {
		Vector2 vector2 = tempVector2_1.set(Float.MAX_VALUE, Float.NaN);
		vector4f.w = Float.NEGATIVE_INFINITY;
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
		if (gameCharacter2 == null) {
			this.getNearestTargetPosAndDot(gameCharacter, movingObject, (String)null, vector2, vector4f);
		} else {
			getBoneWorldPos(movingObject, "Bip01_Head", tempVector3_1);
			getBoneWorldPos(movingObject, "Bip01_HeadNub", tempVector3_2);
			tempVector3_1.addToThis(tempVector3_2);
			tempVector3_1.div(2.0F);
			Vector3 vector3 = tempVector3_1;
			if (isStanding(movingObject)) {
				this.getNearestTargetPosAndDot(gameCharacter, vector3, vector2, vector4f);
				this.getNearestTargetPosAndDot(gameCharacter, movingObject, "Bip01_Pelvis", vector2, vector4f);
				Vector3 vector32 = tempVectorBonePos.set(movingObject.getX(), movingObject.getY(), movingObject.getZ());
				this.getNearestTargetPosAndDot(gameCharacter, vector32, vector2, vector4f);
			} else {
				this.getNearestTargetPosAndDot(gameCharacter, vector3, vector2, vector4f);
				this.getNearestTargetPosAndDot(gameCharacter, movingObject, "Bip01_Pelvis", vector2, vector4f);
				this.getNearestTargetPosAndDot(gameCharacter, movingObject, "Bip01_DressFrontNub", vector2, vector4f);
			}
		}
	}

	private SwipeStatePlayer.HitInfo calcValidTarget(IsoLivingCharacter livingCharacter, HandWeapon handWeapon, IsoMovingObject movingObject, float float1) {
		if (movingObject == livingCharacter) {
			return null;
		} else {
			IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
			if (gameCharacter == null) {
				return null;
			} else if (gameCharacter.isGodMod()) {
				return null;
			} else if (!this.checkPVP(livingCharacter, movingObject)) {
				return null;
			} else {
				float float2 = Math.abs(gameCharacter.getZ() - livingCharacter.getZ());
				if (!handWeapon.isRanged() && float2 >= 0.5F) {
					return null;
				} else if (float2 > 3.3F) {
					return null;
				} else if (!gameCharacter.isShootable()) {
					return null;
				} else if (gameCharacter.isCurrentState(FakeDeadZombieState.instance())) {
					return null;
				} else if (gameCharacter.isDead()) {
					return null;
				} else if (gameCharacter.getHitReaction() != null && gameCharacter.getHitReaction().contains("Death")) {
					return null;
				} else {
					Vector4f vector4f = this.tempVector4f;
					this.getNearestTargetPosAndDot(livingCharacter, gameCharacter, vector4f);
					float float3 = vector4f.w;
					float float4 = IsoUtils.DistanceToSquared(livingCharacter.x, livingCharacter.y, (float)((int)livingCharacter.z * 3), vector4f.x, vector4f.y, (float)((int)vector4f.z * 3));
					if (float3 < 0.0F) {
						return null;
					} else if (float4 > float1 * float1) {
						return null;
					} else {
						LosUtil.TestResults testResults = LosUtil.lineClear(livingCharacter.getCell(), (int)livingCharacter.getX(), (int)livingCharacter.getY(), (int)livingCharacter.getZ(), (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), false);
						return testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor ? ((SwipeStatePlayer.HitInfo)this.hitInfoPool.alloc()).init(gameCharacter, float3, float4, vector4f.x, vector4f.y, vector4f.z) : null;
					}
				}
			}
		}
	}

	public static boolean isProne(IsoMovingObject movingObject) {
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
		if (zombie == null) {
			return movingObject.isOnFloor();
		} else if (zombie.isOnFloor()) {
			return true;
		} else if (zombie.isCurrentState(ZombieEatBodyState.instance())) {
			return true;
		} else if (zombie.isDead()) {
			return true;
		} else if (zombie.isSitAgainstWall()) {
			return true;
		} else {
			return zombie.isCrawling();
		}
	}

	public static boolean isStanding(IsoMovingObject movingObject) {
		return !isProne(movingObject);
	}

	public boolean isProneTargetBetter(IsoGameCharacter gameCharacter, SwipeStatePlayer.HitInfo hitInfo, SwipeStatePlayer.HitInfo hitInfo2) {
		if (hitInfo != null && hitInfo.object != null) {
			if (hitInfo2 != null && hitInfo2.object != null) {
				if (hitInfo.distSq <= hitInfo2.distSq) {
					return false;
				} else {
					boolean boolean1 = PolygonalMap2.instance.lineClearCollide(gameCharacter.x, gameCharacter.y, hitInfo.object.x, hitInfo.object.y, (int)gameCharacter.z, (IsoMovingObject)null, false, true);
					if (!boolean1) {
						return false;
					} else {
						boolean boolean2 = PolygonalMap2.instance.lineClearCollide(gameCharacter.x, gameCharacter.y, hitInfo2.object.x, hitInfo2.object.y, (int)gameCharacter.z, (IsoMovingObject)null, false, true);
						return !boolean2;
					}
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean checkPVP(IsoGameCharacter gameCharacter, IsoMovingObject movingObject) {
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		IsoPlayer player2 = (IsoPlayer)Type.tryCastTo(movingObject, IsoPlayer.class);
		if (GameClient.bClient && player2 != null) {
			if (player2.isGodMod() || !ServerOptions.instance.PVP.getValue() || ServerOptions.instance.SafetySystem.getValue() && gameCharacter.isSafety() && ((IsoGameCharacter)movingObject).isSafety()) {
				return false;
			}

			if (NonPvpZone.getNonPvpZone((int)movingObject.getX(), (int)movingObject.getY()) != null) {
				return false;
			}

			if (player != null && NonPvpZone.getNonPvpZone((int)gameCharacter.getX(), (int)gameCharacter.getY()) != null) {
				return false;
			}

			if (player != null && !player.factionPvp && !player2.factionPvp) {
				Faction faction = Faction.getPlayerFaction(player);
				Faction faction2 = Faction.getPlayerFaction(player2);
				if (faction2 != null && faction == faction2) {
					return false;
				}
			}
		}

		if (!GameClient.bClient && player2 != null && !IsoPlayer.getCoopPVP()) {
			return false;
		} else {
			return true;
		}
	}

	private void CalcHitListShove(IsoGameCharacter gameCharacter, boolean boolean1, SwipeStatePlayer.AttackVars attackVars) {
		HandWeapon handWeapon = attackVars.weapon;
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getObjectList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int1);
			if (movingObject != gameCharacter && !(movingObject instanceof BaseVehicle)) {
				IsoGameCharacter gameCharacter2 = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
				if (gameCharacter2 != null && !gameCharacter2.isGodMod() && !gameCharacter2.isDead()) {
					IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
					if ((zombie == null || !zombie.isCurrentState(FakeDeadZombieState.instance())) && this.checkPVP(gameCharacter, movingObject)) {
						boolean boolean2 = movingObject == attackVars.targetOnGround || movingObject.isShootable() && isStanding(movingObject) && !attackVars.bAimAtFloor || movingObject.isShootable() && isProne(movingObject) && attackVars.bAimAtFloor;
						if (boolean2) {
							Vector4f vector4f = this.tempVector4f;
							if (this.getNearestTargetPosAndDot(gameCharacter, handWeapon, movingObject, boolean1, vector4f)) {
								float float1 = vector4f.w;
								float float2 = IsoUtils.DistanceToSquared(gameCharacter.x, gameCharacter.y, (float)((int)gameCharacter.z * 3), vector4f.x, vector4f.y, (float)((int)vector4f.z * 3));
								LosUtil.TestResults testResults = LosUtil.lineClear(gameCharacter.getCell(), (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), (int)movingObject.getX(), (int)movingObject.getY(), (int)movingObject.getZ(), false);
								if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor && (movingObject.getCurrentSquare() == null || gameCharacter.getCurrentSquare() == null || movingObject.getCurrentSquare() == gameCharacter.getCurrentSquare() || !movingObject.getCurrentSquare().isWindowBlockedTo(gameCharacter.getCurrentSquare())) && movingObject.getSquare().getTransparentWallTo(gameCharacter.getSquare()) == null) {
									SwipeStatePlayer.HitInfo hitInfo = ((SwipeStatePlayer.HitInfo)this.hitInfoPool.alloc()).init(movingObject, float1, float2, vector4f.x, vector4f.y, vector4f.z);
									if (attackVars.targetOnGround == movingObject) {
										HitList.clear();
										HitList.add(hitInfo);
										break;
									}

									HitList.add(hitInfo);
								}
							}
						}
					}
				}
			}
		}
	}

	private void CalcHitListWeapon(IsoGameCharacter gameCharacter, boolean boolean1, SwipeStatePlayer.AttackVars attackVars) {
		HandWeapon handWeapon = attackVars.weapon;
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getObjectList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int1);
			if (movingObject != gameCharacter) {
				IsoGameCharacter gameCharacter2 = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
				if ((gameCharacter2 == null || !gameCharacter2.isGodMod()) && (gameCharacter2 == null || !gameCharacter2.isDead())) {
					IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
					if ((zombie == null || !zombie.isCurrentState(FakeDeadZombieState.instance())) && this.checkPVP(gameCharacter, movingObject)) {
						boolean boolean2 = movingObject == attackVars.targetOnGround || movingObject.isShootable() && isStanding(movingObject) && !attackVars.bAimAtFloor || movingObject.isShootable() && isProne(movingObject) && attackVars.bAimAtFloor;
						if (boolean2) {
							Vector4f vector4f = this.tempVector4f;
							float float1;
							if (movingObject instanceof BaseVehicle) {
								VehiclePart vehiclePart = ((BaseVehicle)movingObject).getNearestBodyworkPart(gameCharacter);
								if (vehiclePart == null) {
									continue;
								}

								float1 = gameCharacter.getDotWithForwardDirection(movingObject.x, movingObject.y);
								if (float1 < 0.8F) {
									continue;
								}

								vector4f.set(movingObject.x, movingObject.y, movingObject.z, float1);
							} else if (gameCharacter2 == null || !this.getNearestTargetPosAndDot(gameCharacter, handWeapon, movingObject, boolean1, vector4f)) {
								continue;
							}

							LosUtil.TestResults testResults = LosUtil.lineClear(gameCharacter.getCell(), (int)gameCharacter.getX(), (int)gameCharacter.getY(), (int)gameCharacter.getZ(), (int)movingObject.getX(), (int)movingObject.getY(), (int)movingObject.getZ(), false);
							if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor) {
								float1 = vector4f.w;
								float float2 = IsoUtils.DistanceToSquared(gameCharacter.x, gameCharacter.y, (float)((int)gameCharacter.z * 3), vector4f.x, vector4f.y, (float)((int)vector4f.z * 3));
								if (movingObject.getSquare().getTransparentWallTo(gameCharacter.getSquare()) != null && gameCharacter instanceof IsoPlayer) {
									if (WeaponType.getWeaponType(gameCharacter) == WeaponType.spear) {
										((IsoPlayer)gameCharacter).setAttackType("spearStab");
									} else if (WeaponType.getWeaponType(gameCharacter) != WeaponType.knife) {
										continue;
									}
								}

								IsoWindow window = this.getWindowBetween(gameCharacter, movingObject);
								if (window == null || !window.isBarricaded()) {
									SwipeStatePlayer.HitInfo hitInfo = ((SwipeStatePlayer.HitInfo)this.hitInfoPool.alloc()).init(movingObject, float1, float2, vector4f.x, vector4f.y, vector4f.z);
									hitInfo.window = window;
									HitList.add(hitInfo);
								}
							}
						}
					}
				}
			}
		}

		if (HitList.isEmpty()) {
			this.CalcHitListWindow(gameCharacter, handWeapon);
		}
	}

	private void CalcHitListWindow(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		Vector2 vector2 = gameCharacter.getLookVector(tempVector2_1);
		vector2.setLength(handWeapon.getMaxRange() * handWeapon.getRangeMod(gameCharacter));
		SwipeStatePlayer.HitInfo hitInfo = null;
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getWindowList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoWindow window = (IsoWindow)arrayList.get(int1);
			if ((int)window.getZ() == (int)gameCharacter.z && this.windowVisitor.isHittable(window)) {
				float float1 = window.getX();
				float float2 = window.getY();
				float float3 = float1 + (window.getNorth() ? 1.0F : 0.0F);
				float float4 = float2 + (window.getNorth() ? 0.0F : 1.0F);
				if (Line2D.linesIntersect((double)gameCharacter.x, (double)gameCharacter.y, (double)(gameCharacter.x + vector2.x), (double)(gameCharacter.y + vector2.y), (double)float1, (double)float2, (double)float3, (double)float4)) {
					IsoGridSquare square = window.getAddSheetSquare(gameCharacter);
					if (square != null && !LosUtil.lineClearCollide((int)gameCharacter.x, (int)gameCharacter.y, (int)gameCharacter.z, square.x, square.y, square.z, false)) {
						float float5 = IsoUtils.DistanceToSquared(gameCharacter.x, gameCharacter.y, float1 + (float3 - float1) / 2.0F, float2 + (float4 - float2) / 2.0F);
						if (hitInfo == null || !(hitInfo.distSq < float5)) {
							float float6 = 1.0F;
							if (hitInfo == null) {
								hitInfo = (SwipeStatePlayer.HitInfo)this.hitInfoPool.alloc();
							}

							hitInfo.init(window, float6, float5);
						}
					}
				}
			}
		}

		if (hitInfo != null) {
			HitList.add(hitInfo);
		}
	}

	public void CalcHitList(IsoGameCharacter gameCharacter, boolean boolean1, SwipeStatePlayer.AttackVars attackVars) {
		this.hitInfoPool.release((List)HitList);
		HitList.clear();
		HandWeapon handWeapon = attackVars.weapon;
		int int1 = handWeapon.getMaxHitCount();
		if (attackVars.bDoShove) {
			int1 = WeaponType.getWeaponType(gameCharacter) != WeaponType.barehand ? 3 : 1;
		}

		if (!handWeapon.isRanged() && !SandboxOptions.instance.MultiHitZombies.getValue()) {
			int1 = 1;
		}

		if (handWeapon == ((IsoPlayer)gameCharacter).bareHands && !(gameCharacter.getPrimaryHandItem() instanceof HandWeapon)) {
			int1 = 1;
		}

		if (handWeapon == ((IsoPlayer)gameCharacter).bareHands && attackVars.targetOnGround != null) {
			int1 = 1;
		}

		if (0 < int1) {
			if (attackVars.bDoShove) {
				this.CalcHitListShove(gameCharacter, boolean1, attackVars);
			} else {
				this.CalcHitListWeapon(gameCharacter, boolean1, attackVars);
			}

			if (HitList.size() == 1 && ((SwipeStatePlayer.HitInfo)HitList.get(0)).object == null) {
				return;
			}

			this.filterTargetsByZ(gameCharacter);
			Collections.sort(HitList, Comparator);
			if (handWeapon.isPiercingBullets()) {
				HitList2.clear();
				double double1 = 0.0;
				for (int int2 = 0; int2 < HitList.size(); ++int2) {
					SwipeStatePlayer.HitInfo hitInfo = (SwipeStatePlayer.HitInfo)HitList.get(int2);
					IsoMovingObject movingObject = hitInfo.object;
					if (movingObject != null) {
						double double2 = (double)(gameCharacter.getX() - movingObject.getX());
						double double3 = (double)(-(gameCharacter.getY() - movingObject.getY()));
						double double4 = Math.atan2(double3, double2);
						if (double4 < 0.0) {
							double4 = Math.abs(double4);
						} else {
							double4 = 6.283185307179586 - double4;
						}

						if (int2 == 0) {
							double1 = Math.toDegrees(double4);
							HitList2.add(hitInfo);
						} else {
							double double5 = Math.toDegrees(double4);
							if (Math.abs(double1 - double5) < 1.0) {
								HitList2.add(hitInfo);
								break;
							}
						}
					}
				}

				HitList.removeAll(HitList2);
				this.hitInfoPool.release((List)HitList);
				HitList.clear();
				HitList.addAll(HitList2);
			} else {
				while (HitList.size() > int1) {
					this.hitInfoPool.release((Object)((SwipeStatePlayer.HitInfo)HitList.remove(HitList.size() - 1)));
				}
			}
		}

		for (int int3 = 0; int3 < HitList.size(); ++int3) {
			SwipeStatePlayer.HitInfo hitInfo2 = (SwipeStatePlayer.HitInfo)HitList.get(int3);
			hitInfo2.chance = this.CalcHitChance(gameCharacter, handWeapon, hitInfo2);
		}
	}

	private void filterTargetsByZ(IsoGameCharacter gameCharacter) {
		float float1 = Float.MAX_VALUE;
		SwipeStatePlayer.HitInfo hitInfo = null;
		int int1;
		SwipeStatePlayer.HitInfo hitInfo2;
		float float2;
		for (int1 = 0; int1 < HitList.size(); ++int1) {
			hitInfo2 = (SwipeStatePlayer.HitInfo)HitList.get(int1);
			float2 = Math.abs(hitInfo2.z - gameCharacter.getZ());
			if (float2 < float1) {
				float1 = float2;
				hitInfo = hitInfo2;
			}
		}

		if (hitInfo != null) {
			for (int1 = HitList.size() - 1; int1 >= 0; --int1) {
				hitInfo2 = (SwipeStatePlayer.HitInfo)HitList.get(int1);
				if (hitInfo2 != hitInfo) {
					float2 = Math.abs(hitInfo2.z - hitInfo.z);
					if (float2 > 0.5F) {
						this.hitInfoPool.release((Object)hitInfo2);
						HitList.remove(int1);
					}
				}
			}
		}
	}

	public int CalcHitChance(IsoGameCharacter gameCharacter, HandWeapon handWeapon, SwipeStatePlayer.HitInfo hitInfo) {
		IsoMovingObject movingObject = hitInfo.object;
		if (movingObject == null) {
			return 0;
		} else {
			if (gameCharacter.getVehicle() != null) {
				BaseVehicle baseVehicle = gameCharacter.getVehicle();
				Vector3f vector3f = baseVehicle.getForwardVector((Vector3f)((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).alloc());
				Vector2 vector2 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
				vector2.x = vector3f.x;
				vector2.y = vector3f.z;
				vector2.normalize();
				int int1 = baseVehicle.getSeat(gameCharacter);
				VehicleScript.Area area = baseVehicle.getScript().getAreaById(baseVehicle.getPassengerArea(int1));
				byte byte1 = -90;
				if (area.x > 0.0F) {
					byte1 = 90;
				}

				vector2.rotate((float)Math.toRadians((double)byte1));
				vector2.normalize();
				Vector2 vector22 = (Vector2)((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).alloc();
				vector22.x = movingObject.x;
				vector22.y = movingObject.y;
				vector22.x -= gameCharacter.x;
				vector22.y -= gameCharacter.y;
				vector22.normalize();
				float float1 = vector22.dot(vector2);
				if ((double)float1 > -0.6) {
					return 0;
				}

				((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector2);
				((BaseVehicle.Vector2ObjectPool)BaseVehicle.TL_vector2_pool.get()).release(vector22);
				((BaseVehicle.Vector3fObjectPool)BaseVehicle.TL_vector3f_pool.get()).release(vector3f);
			}

			if (System.currentTimeMillis() - gameCharacter.lastAutomaticShoot > 600L) {
				gameCharacter.shootInARow = 0;
			}

			int int2 = handWeapon.getHitChance();
			int2 = (int)((float)int2 + handWeapon.getAimingPerkHitChanceModifier() * (float)gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming));
			if (int2 > 95) {
				int2 = 95;
			}

			int2 -= gameCharacter.shootInARow * 2;
			float float2 = PZMath.sqrt(hitInfo.distSq);
			float float3 = 1.3F;
			if (movingObject instanceof IsoPlayer) {
				float2 = (float)((double)float2 * 1.5);
				float3 = 1.0F;
			}

			int2 = (int)((float)int2 + (handWeapon.getMaxRange() * handWeapon.getRangeMod(gameCharacter) - float2) * float3);
			if (handWeapon.getMinRangeRanged() > 0.0F) {
				if (float2 < handWeapon.getMinRangeRanged()) {
					int2 -= 50;
				}
			} else if ((double)float2 < 1.7 && handWeapon.isRanged() && !(movingObject instanceof IsoPlayer)) {
				int2 += 35;
			}

			if (handWeapon.isRanged() && gameCharacter.getBeenMovingFor() > (float)(handWeapon.getAimingTime() + gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming))) {
				int2 = (int)((float)int2 - (gameCharacter.getBeenMovingFor() - (float)(handWeapon.getAimingTime() + gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming))));
			}

			if (hitInfo.object instanceof IsoPlayer) {
				IsoPlayer player = (IsoPlayer)hitInfo.object;
				if (player.isPlayerMoving()) {
					int2 -= 5;
				}

				if (player.isRunning()) {
					int2 -= 10;
				}

				if (player.isSprinting()) {
					int2 -= 15;
				}
			}

			if (handWeapon.isRanged() && gameCharacter.getVehicle() != null) {
				int2 = (int)((float)int2 - Math.abs(gameCharacter.getVehicle().getCurrentSpeedKmHour()) * 2.0F);
			}

			if (gameCharacter.Traits.Marksman.isSet()) {
				int2 += 20;
			}

			float float4 = 0.0F;
			for (int int3 = BodyPartType.ToIndex(BodyPartType.Hand_L); int3 <= BodyPartType.ToIndex(BodyPartType.UpperArm_R); ++int3) {
				float4 += ((BodyPart)gameCharacter.getBodyDamage().getBodyParts().get(int3)).getPain();
			}

			if (float4 > 0.0F) {
				int2 = (int)((float)int2 - float4 / 10.0F);
			}

			int2 -= gameCharacter.getMoodles().getMoodleLevel(MoodleType.Tired) * 5;
			if (int2 <= 10) {
				int2 = 10;
			}

			if (int2 > 100 || !handWeapon.isRanged()) {
				int2 = 100;
			}

			return int2;
		}
	}

	@Deprecated
	private void DoHitSound(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		this.attackVars.weapon = handWeapon;
		this.attackVars.targetOnGround = ((IsoLivingCharacter)gameCharacter).targetOnGround;
		this.attackVars.bAimAtFloor = gameCharacter.isAimAtFloor();
		this.attackVars.bDoShove = ((IsoLivingCharacter)gameCharacter).bDoShove;
		this.CalcHitList(gameCharacter, false, this.attackVars);
		if (!HitList.isEmpty()) {
			for (int int1 = 0; int1 < HitList.size(); ++int1) {
				SwipeStatePlayer.HitInfo hitInfo = (SwipeStatePlayer.HitInfo)HitList.get(int1);
				IsoMovingObject movingObject = hitInfo.object;
				if (movingObject != null) {
					Vector2 vector2 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
					Vector2 vector22 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
					vector22.x -= vector2.x;
					vector22.y -= vector2.y;
					Vector2 vector23 = tempVector2_1.set(gameCharacter.getForwardDirection().getX(), gameCharacter.getForwardDirection().getY());
					vector23.tangent();
					vector22.normalize();
					boolean boolean1 = true;
					float float1 = vector23.dot(vector22);
					for (int int2 = 0; int2 < this.dotList.size(); ++int2) {
						float float2 = (Float)this.dotList.get(int2);
						if ((double)Math.abs(float1 - float2) < 1.0E-4) {
							boolean1 = false;
						}
					}

					if (boolean1 && movingObject instanceof IsoZombie) {
						IsoZombie zombie = (IsoZombie)movingObject;
						if (zombie.vocalEvent != 0L) {
							if (!zombie.isOnFloor() && ((IsoLivingCharacter)gameCharacter).bDoShove) {
								zombie.parameterZombieState.setState(ParameterZombieState.State.Pushed);
							} else {
								zombie.parameterZombieState.setState(ParameterZombieState.State.Hit);
							}
						}
					}
				}
			}
		}
	}

	public static Vector3 getBoneWorldPos(IsoMovingObject movingObject, String string, Vector3 vector3) {
		IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
		if (gameCharacter != null && string != null) {
			AnimationPlayer animationPlayer = gameCharacter.getAnimationPlayer();
			if (animationPlayer != null && animationPlayer.isReady()) {
				int int1 = animationPlayer.getSkinningBoneIndex(string, -1);
				if (int1 == -1) {
					return vector3.set(movingObject.x, movingObject.y, movingObject.z);
				} else {
					Model.BoneToWorldCoords(gameCharacter, int1, vector3);
					return vector3;
				}
			} else {
				return vector3.set(movingObject.x, movingObject.y, movingObject.z);
			}
		} else {
			return vector3.set(movingObject.x, movingObject.y, movingObject.z);
		}
	}

	public void ConnectSwing(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		HashMap hashMap = gameCharacter.getStateMachineParams(this);
		IsoLivingCharacter livingCharacter = (IsoLivingCharacter)gameCharacter;
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(gameCharacter, IsoPlayer.class);
		if (gameCharacter.getVariableBoolean("ShoveAnim")) {
			livingCharacter.bDoShove = true;
		}

		if (GameServer.bServer) {
			DebugLog.log(DebugType.Network, "Player swing connects.");
		}

		LuaEventManager.triggerEvent("OnWeaponSwingHitPoint", gameCharacter, handWeapon);
		if (handWeapon.getPhysicsObject() != null) {
			gameCharacter.Throw(handWeapon);
		}

		if (handWeapon.isUseSelf()) {
			handWeapon.Use();
		}

		if (handWeapon.isOtherHandUse() && gameCharacter.getSecondaryHandItem() != null) {
			gameCharacter.getSecondaryHandItem().Use();
		}

		boolean boolean1 = false;
		if (livingCharacter.bDoShove && !gameCharacter.isAimAtFloor()) {
			boolean1 = true;
		}

		boolean boolean2 = false;
		boolean boolean3 = false;
		this.attackVars.weapon = handWeapon;
		this.attackVars.targetOnGround = livingCharacter.targetOnGround;
		this.attackVars.bAimAtFloor = gameCharacter.isAimAtFloor();
		this.attackVars.bDoShove = livingCharacter.bDoShove;
		if (gameCharacter.getVariableBoolean("ShoveAnim")) {
			this.attackVars.bDoShove = true;
		}

		this.CalcHitList(gameCharacter, false, this.attackVars);
		int int1 = HitList.size();
		boolean boolean4 = false;
		if (int1 == 0) {
			boolean4 = this.CheckObjectHit(gameCharacter, handWeapon);
		}

		Stats stats;
		if (handWeapon.isUseEndurance()) {
			float float1 = 0.0F;
			if (handWeapon.isTwoHandWeapon() && (gameCharacter.getPrimaryHandItem() != handWeapon || gameCharacter.getSecondaryHandItem() != handWeapon)) {
				float1 = handWeapon.getWeight() / 1.5F / 10.0F;
			}

			if (int1 <= 0 && !gameCharacter.isForceShove()) {
				float float2 = (handWeapon.getWeight() * 0.28F * handWeapon.getFatigueMod(gameCharacter) * gameCharacter.getFatigueMod() * handWeapon.getEnduranceMod() * 0.3F + float1) * 0.04F;
				float float3 = 1.0F;
				if (gameCharacter.Traits.Asthmatic.isSet()) {
					float3 = 1.3F;
				}

				stats = gameCharacter.getStats();
				stats.endurance -= float2 * float3;
			}
		}

		gameCharacter.setLastHitCount(HitList.size());
		if (!handWeapon.isMultipleHitConditionAffected()) {
			boolean2 = true;
		}

		int int2 = 1;
		this.dotList.clear();
		if (HitList.isEmpty() && gameCharacter.getClickSound() != null && !livingCharacter.bDoShove) {
			if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer() || !(gameCharacter instanceof IsoPlayer)) {
				gameCharacter.getEmitter().playSound(gameCharacter.getClickSound());
			}

			gameCharacter.setRecoilDelay(10.0F);
		}

		boolean boolean5 = false;
		int int3;
		for (int int4 = 0; int4 < HitList.size(); ++int4) {
			int3 = 0;
			boolean boolean6 = false;
			SwipeStatePlayer.HitInfo hitInfo = (SwipeStatePlayer.HitInfo)HitList.get(int4);
			IsoMovingObject movingObject = hitInfo.object;
			BaseVehicle baseVehicle = (BaseVehicle)Type.tryCastTo(movingObject, BaseVehicle.class);
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(movingObject, IsoZombie.class);
			if (hitInfo.object == null && hitInfo.window != null) {
				hitInfo.window.WeaponHit(gameCharacter, handWeapon);
			} else {
				this.smashWindowBetween(gameCharacter, movingObject, handWeapon);
				if (!this.isWindowBetween(gameCharacter, movingObject)) {
					int int5 = hitInfo.chance;
					boolean boolean7 = Rand.Next(100) <= int5;
					if (boolean7) {
						Vector2 vector2 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
						Vector2 vector22 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
						vector22.x -= vector2.x;
						vector22.y -= vector2.y;
						Vector2 vector23 = gameCharacter.getLookVector(tempVector2_1);
						vector23.tangent();
						vector22.normalize();
						boolean boolean8 = true;
						float float4 = vector23.dot(vector22);
						float float5;
						for (int int6 = 0; int6 < this.dotList.size(); ++int6) {
							float5 = (Float)this.dotList.get(int6);
							if ((double)Math.abs(float4 - float5) < 1.0E-4) {
								boolean8 = false;
							}
						}

						float float6 = handWeapon.getMinDamage();
						float5 = handWeapon.getMaxDamage();
						if (!boolean8) {
							float6 /= 5.0F;
							float5 /= 5.0F;
						}

						if (gameCharacter.isAimAtFloor() && !handWeapon.isRanged() && gameCharacter.isNPC()) {
							splash(movingObject, handWeapon, gameCharacter);
							int3 = Rand.Next(2);
						} else if (gameCharacter.isAimAtFloor() && !handWeapon.isRanged()) {
							if (player == null || player.isLocalPlayer()) {
								if (!StringUtils.isNullOrEmpty(handWeapon.getHitFloorSound())) {
									player.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Body);
									gameCharacter.playSound(handWeapon.getHitFloorSound());
								} else {
									if (player != null) {
										player.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Body);
									}

									gameCharacter.playSound(handWeapon.getZombieHitSound());
								}
							}

							int int7 = this.DoSwingCollisionBoneCheck(gameCharacter, GetWeapon(gameCharacter), (IsoGameCharacter)movingObject, ((IsoGameCharacter)movingObject).getAnimationPlayer().getSkinningBoneIndex("Bip01_Head", -1), 0.28F);
							if (int7 == -1) {
								int7 = this.DoSwingCollisionBoneCheck(gameCharacter, GetWeapon(gameCharacter), (IsoGameCharacter)movingObject, ((IsoGameCharacter)movingObject).getAnimationPlayer().getSkinningBoneIndex("Bip01_Spine", -1), 0.28F);
								if (int7 == -1) {
									int7 = this.DoSwingCollisionBoneCheck(gameCharacter, GetWeapon(gameCharacter), (IsoGameCharacter)movingObject, ((IsoGameCharacter)movingObject).getAnimationPlayer().getSkinningBoneIndex("Bip01_L_Calf", -1), 0.13F);
									if (int7 == -1) {
										int7 = this.DoSwingCollisionBoneCheck(gameCharacter, GetWeapon(gameCharacter), (IsoGameCharacter)movingObject, ((IsoGameCharacter)movingObject).getAnimationPlayer().getSkinningBoneIndex("Bip01_R_Calf", -1), 0.13F);
									}

									if (int7 == -1) {
										int7 = this.DoSwingCollisionBoneCheck(gameCharacter, GetWeapon(gameCharacter), (IsoGameCharacter)movingObject, ((IsoGameCharacter)movingObject).getAnimationPlayer().getSkinningBoneIndex("Bip01_L_Foot", -1), 0.23F);
									}

									if (int7 == -1) {
										int7 = this.DoSwingCollisionBoneCheck(gameCharacter, GetWeapon(gameCharacter), (IsoGameCharacter)movingObject, ((IsoGameCharacter)movingObject).getAnimationPlayer().getSkinningBoneIndex("Bip01_R_Foot", -1), 0.23F);
									}

									if (int7 == -1) {
										continue;
									}

									boolean6 = true;
								}
							} else {
								splash(movingObject, handWeapon, gameCharacter);
								splash(movingObject, handWeapon, gameCharacter);
								int3 = Rand.Next(0, 3) + 1;
							}
						}

						if (!this.attackVars.bAimAtFloor && (!this.attackVars.bCloseKill || !gameCharacter.isCriticalHit()) && !livingCharacter.bDoShove && movingObject instanceof IsoGameCharacter && (player == null || player.isLocalPlayer())) {
							if (player != null) {
								player.setMeleeHitSurface(ParameterMeleeHitSurface.Material.Body);
							}

							if (handWeapon.isRanged()) {
								((IsoGameCharacter)movingObject).playSound(handWeapon.getZombieHitSound());
							} else {
								gameCharacter.playSound(handWeapon.getZombieHitSound());
							}
						}

						float float7;
						if (handWeapon.isRanged() && zombie != null) {
							Vector2 vector24 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
							Vector2 vector25 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
							vector25.x -= vector24.x;
							vector25.y -= vector24.y;
							Vector2 vector26 = zombie.getForwardDirection();
							vector25.normalize();
							vector26.normalize();
							float7 = vector25.dot(vector26);
							zombie.setHitFromBehind((double)float7 > 0.5);
						}

						if (this.dotList.isEmpty()) {
							this.dotList.add(float4);
						}

						if (zombie != null && zombie.isCurrentState(ZombieOnGroundState.instance())) {
							zombie.setReanimateTimer(zombie.getReanimateTimer() + (float)Rand.Next(10));
						}

						if (zombie != null && zombie.isCurrentState(ZombieGetUpState.instance())) {
							zombie.setReanimateTimer((float)(Rand.Next(60) + 30));
						}

						boolean boolean9 = false;
						if (!handWeapon.isTwoHandWeapon() || gameCharacter.isItemInBothHands(handWeapon)) {
							boolean9 = true;
						}

						float float8 = float5 - float6;
						float float9;
						if (float8 == 0.0F) {
							float9 = float6 + 0.0F;
						} else {
							float9 = float6 + (float)Rand.Next((int)(float8 * 1000.0F)) / 1000.0F;
						}

						if (!handWeapon.isRanged()) {
							float9 *= handWeapon.getDamageMod(gameCharacter) * gameCharacter.getHittingMod();
						}

						if (!boolean9 && !handWeapon.isRanged() && float5 > float6) {
							float9 -= float6;
						}

						int int8;
						if (gameCharacter.isAimAtFloor() && livingCharacter.bDoShove) {
							float7 = 0.0F;
							for (int8 = BodyPartType.ToIndex(BodyPartType.UpperLeg_L); int8 <= BodyPartType.ToIndex(BodyPartType.Foot_R); ++int8) {
								float7 += ((BodyPart)gameCharacter.getBodyDamage().getBodyParts().get(int8)).getPain();
							}

							if (float7 > 10.0F) {
								float9 /= PZMath.clamp(float7 / 10.0F, 1.0F, 30.0F);
								MoodlesUI.getInstance().wiggle(MoodleType.Pain);
								MoodlesUI.getInstance().wiggle(MoodleType.Injured);
							}
						} else {
							float7 = 0.0F;
							for (int8 = BodyPartType.ToIndex(BodyPartType.Hand_L); int8 <= BodyPartType.ToIndex(BodyPartType.UpperArm_R); ++int8) {
								float7 += ((BodyPart)gameCharacter.getBodyDamage().getBodyParts().get(int8)).getPain();
							}

							if (float7 > 10.0F) {
								float9 /= PZMath.clamp(float7 / 10.0F, 1.0F, 30.0F);
								MoodlesUI.getInstance().wiggle(MoodleType.Pain);
								MoodlesUI.getInstance().wiggle(MoodleType.Injured);
							}
						}

						if (gameCharacter.Traits.Underweight.isSet()) {
							float9 *= 0.8F;
						}

						if (gameCharacter.Traits.VeryUnderweight.isSet()) {
							float9 *= 0.6F;
						}

						if (gameCharacter.Traits.Emaciated.isSet()) {
							float9 *= 0.4F;
						}

						float7 = float9 / ((float)int2 / 2.0F);
						if (gameCharacter.isAttackWasSuperAttack()) {
							float7 *= 5.0F;
						}

						++int2;
						if (handWeapon.isMultipleHitConditionAffected()) {
							boolean2 = true;
						}

						Vector2 vector27 = tempVector2_1.set(gameCharacter.getX(), gameCharacter.getY());
						Vector2 vector28 = tempVector2_2.set(movingObject.getX(), movingObject.getY());
						vector28.x -= vector27.x;
						vector28.y -= vector27.y;
						float float10 = vector28.getLength();
						float float11 = 1.0F;
						if (!handWeapon.isRangeFalloff()) {
							float11 = float10 / handWeapon.getMaxRange(gameCharacter);
						}

						float11 *= 2.0F;
						if (float11 < 0.3F) {
							float11 = 1.0F;
						}

						if (handWeapon.isRanged() && gameCharacter.getPerkLevel(PerkFactory.Perks.Aiming) < 6 && gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 2) {
							float7 -= (float)gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.2F;
							MoodlesUI.getInstance().wiggle(MoodleType.Panic);
						}

						if (!handWeapon.isRanged() && gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) > 1) {
							float7 -= (float)gameCharacter.getMoodles().getMoodleLevel(MoodleType.Panic) * 0.1F;
							MoodlesUI.getInstance().wiggle(MoodleType.Panic);
						}

						if (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Stress) > 1) {
							float7 -= (float)gameCharacter.getMoodles().getMoodleLevel(MoodleType.Stress) * 0.1F;
							MoodlesUI.getInstance().wiggle(MoodleType.Stress);
						}

						if (float7 < 0.0F) {
							float7 = 0.1F;
						}

						if (gameCharacter.isAimAtFloor() && livingCharacter.bDoShove) {
							float7 = Rand.Next(0.7F, 1.0F) + (float)gameCharacter.getPerkLevel(PerkFactory.Perks.Strength) * 0.2F;
							Clothing clothing = (Clothing)gameCharacter.getWornItem("Shoes");
							if (clothing == null) {
								float7 *= 0.5F;
							} else {
								float7 *= clothing.getStompPower();
							}
						}

						if (!handWeapon.isRanged()) {
							switch (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Endurance)) {
							case 0: 
							
							default: 
								break;
							
							case 1: 
								float7 *= 0.5F;
								MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
								break;
							
							case 2: 
								float7 *= 0.2F;
								MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
								break;
							
							case 3: 
								float7 *= 0.1F;
								MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
								break;
							
							case 4: 
								float7 *= 0.05F;
								MoodlesUI.getInstance().wiggle(MoodleType.Endurance);
							
							}

							switch (gameCharacter.getMoodles().getMoodleLevel(MoodleType.Tired)) {
							case 0: 
							
							default: 
								break;
							
							case 1: 
								float7 *= 0.5F;
								MoodlesUI.getInstance().wiggle(MoodleType.Tired);
								break;
							
							case 2: 
								float7 *= 0.2F;
								MoodlesUI.getInstance().wiggle(MoodleType.Tired);
								break;
							
							case 3: 
								float7 *= 0.1F;
								MoodlesUI.getInstance().wiggle(MoodleType.Tired);
								break;
							
							case 4: 
								float7 *= 0.05F;
								MoodlesUI.getInstance().wiggle(MoodleType.Tired);
							
							}
						}

						gameCharacter.knockbackAttackMod = 1.0F;
						if ("KnifeDeath".equals(gameCharacter.getVariableString("ZombieHitReaction"))) {
							float11 *= 1000.0F;
							gameCharacter.knockbackAttackMod = 0.0F;
							gameCharacter.addWorldSoundUnlessInvisible(4, 4, false);
							this.attackVars.bCloseKill = true;
							movingObject.setCloseKilled(true);
						} else {
							this.attackVars.bCloseKill = false;
							movingObject.setCloseKilled(false);
							gameCharacter.addWorldSoundUnlessInvisible(8, 8, false);
							if (Rand.Next(3) == 0 || gameCharacter.isAimAtFloor() && livingCharacter.bDoShove) {
								gameCharacter.addWorldSoundUnlessInvisible(10, 10, false);
							} else if (Rand.Next(7) == 0) {
								gameCharacter.addWorldSoundUnlessInvisible(16, 16, false);
							}
						}

						movingObject.setHitFromAngle(hitInfo.dot);
						boolean boolean10 = false;
						int int9;
						if (zombie != null) {
							zombie.setHitFromBehind(gameCharacter.isBehind(zombie));
							zombie.setHitAngle(zombie.getForwardDirection());
							zombie.setPlayerAttackPosition(zombie.testDotSide(gameCharacter));
							zombie.setHitHeadWhileOnFloor(int3);
							zombie.setHitLegsWhileOnFloor(boolean6);
							if (int3 > 0) {
								zombie.addBlood(BloodBodyPartType.Head, true, true, true);
								zombie.addBlood(BloodBodyPartType.Torso_Upper, true, false, false);
								zombie.addBlood(BloodBodyPartType.UpperArm_L, true, false, false);
								zombie.addBlood(BloodBodyPartType.UpperArm_R, true, false, false);
								float7 *= 3.0F;
							}

							if (boolean6) {
								float7 = 0.0F;
							}

							boolean boolean11 = false;
							int int10;
							if (int3 > 0) {
								int10 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Head), BodyPartType.ToIndex(BodyPartType.Neck) + 1);
							} else if (boolean6) {
								int10 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Groin), BodyPartType.ToIndex(BodyPartType.Foot_R) + 1);
							} else {
								int10 = Rand.Next(BodyPartType.ToIndex(BodyPartType.Hand_L), BodyPartType.ToIndex(BodyPartType.Neck) + 1);
							}

							float float12 = zombie.getBodyPartClothingDefense(int10, false, handWeapon.isRanged()) / 2.0F;
							float12 += zombie.getBodyPartClothingDefense(int10, true, handWeapon.isRanged());
							if (float12 > 70.0F) {
								float12 = 70.0F;
							}

							float float13 = float7 * Math.abs(1.0F - float12 / 100.0F);
							float7 = float13;
							if (!GameClient.bClient && !GameServer.bServer || GameClient.bClient && gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
								boolean3 = zombie.helmetFall(int3 > 0);
							}

							if ("KnifeDeath".equals(gameCharacter.getVariableString("ZombieHitReaction")) && !"Tutorial".equals(Core.GameMode)) {
								byte byte1 = 8;
								if (zombie.isCurrentState(AttackState.instance())) {
									byte1 = 3;
								}

								int9 = gameCharacter.getPerkLevel(PerkFactory.Perks.SmallBlade) + 1;
								if (Rand.NextBool(byte1 + int9 * 2)) {
									InventoryItem inventoryItem = gameCharacter.getPrimaryHandItem();
									gameCharacter.getInventory().Remove(inventoryItem);
									gameCharacter.removeFromHands(inventoryItem);
									zombie.setAttachedItem("JawStab", inventoryItem);
									boolean10 = true;
								}

								zombie.setVariable("bKnifeDeath", true);
							}
						}

						float float14 = 0.0F;
						Boolean Boolean1 = null;
						if (!GameClient.bClient || movingObject instanceof IsoZombie || movingObject instanceof IsoPlayer) {
							if (baseVehicle == null && movingObject.getSquare() != null && gameCharacter.getSquare() != null) {
								movingObject.setCloseKilled(this.attackVars.bCloseKill);
								if (((IsoPlayer)gameCharacter).isLocalPlayer() || gameCharacter.isNPC()) {
									Boolean1 = gameCharacter.isCrit;
									float14 = movingObject.Hit(handWeapon, gameCharacter, float7, boolean1, float11);
								}

								LuaEventManager.triggerEvent("OnWeaponHitXp", gameCharacter, handWeapon, movingObject, float7);
								if ((!livingCharacter.bDoShove || gameCharacter.isAimAtFloor()) && gameCharacter.DistToSquared(movingObject) < 2.0F && Math.abs(gameCharacter.z - movingObject.z) < 0.5F) {
									gameCharacter.addBlood((BloodBodyPartType)null, false, false, false);
								}

								if (movingObject instanceof IsoGameCharacter) {
									if (((IsoGameCharacter)movingObject).isDead()) {
										stats = gameCharacter.getStats();
										stats.stress -= 0.02F;
									} else if (!(movingObject instanceof IsoPlayer) && (!livingCharacter.bDoShove || gameCharacter.isAimAtFloor())) {
										splash(movingObject, handWeapon, gameCharacter);
									}
								}
							} else if (baseVehicle != null) {
								VehiclePart vehiclePart = baseVehicle.getNearestBodyworkPart(gameCharacter);
								if (vehiclePart != null) {
									VehicleWindow vehicleWindow = vehiclePart.getWindow();
									for (int9 = 0; int9 < vehiclePart.getChildCount(); ++int9) {
										VehiclePart vehiclePart2 = vehiclePart.getChild(int9);
										if (vehiclePart2.getWindow() != null) {
											vehicleWindow = vehiclePart2.getWindow();
											break;
										}
									}

									if (vehicleWindow != null && vehicleWindow.isHittable()) {
										int9 = this.calcDamageToVehicle((int)float7 * 10, handWeapon.getDoorDamage(), true);
										vehicleWindow.damage(int9);
										gameCharacter.playSound("HitVehicleWindowWithWeapon");
									} else {
										int9 = this.calcDamageToVehicle((int)float7 * 10, handWeapon.getDoorDamage(), false);
										vehiclePart.setCondition(vehiclePart.getCondition() - int9);
										player.setVehicleHitLocation(baseVehicle);
										gameCharacter.playSound("HitVehiclePartWithWeapon");
									}
								}
							}
						}

						if (GameClient.bClient && ((IsoPlayer)gameCharacter).isLocalPlayer()) {
							if (movingObject instanceof IsoPlayer) {
								gameCharacter.setSafetyCooldown(gameCharacter.getSafetyCooldown() + (float)ServerOptions.instance.SafetyCooldownTimer.getValue());
							}

							ByteBufferWriter byteBufferWriter = GameClient.connection.startPacket();
							PacketTypes.doPacket((short)26, byteBufferWriter);
							HitPacket hitPacket = new HitPacket();
							hitPacket.set((IsoPlayer)gameCharacter, movingObject, handWeapon, float14, boolean1, float11, boolean3, boolean10, Boolean1);
							hitPacket.write(byteBufferWriter);
							GameClient.connection.endPacket();
							boolean5 = true;
						}
					}
				}
			}
		}

		if (GameClient.bClient && ((IsoPlayer)gameCharacter).isLocalPlayer() && !boolean5) {
			ByteBufferWriter byteBufferWriter2 = GameClient.connection.startPacket();
			PacketTypes.doPacket((short)26, byteBufferWriter2);
			HitPacket hitPacket2 = new HitPacket();
			hitPacket2.set((IsoPlayer)gameCharacter, (IsoMovingObject)null, handWeapon, 0.0F, boolean1, 1.0F, boolean3, false, (Boolean)null);
			hitPacket2.write(byteBufferWriter2);
			GameClient.connection.endPacket();
		}

		if (!boolean2 && boolean4) {
			boolean boolean12 = this.bHitOnlyTree && handWeapon.getScriptItem().Categories.contains("Axe");
			int3 = boolean12 ? 2 : 1;
			if (Rand.Next(handWeapon.getConditionLowerChance() * int3 + gameCharacter.getMaintenanceMod() * 2) == 0) {
				boolean2 = true;
			} else if (Rand.NextBool(2) && !handWeapon.getName().contains("Bare Hands") && (!handWeapon.isTwoHandWeapon() || gameCharacter.getPrimaryHandItem() == handWeapon || gameCharacter.getSecondaryHandItem() == handWeapon || !Rand.NextBool(3))) {
				gameCharacter.getXp().AddXP(PerkFactory.Perks.Maintenance, 1.0F);
			}
		}

		hashMap.put(PARAM_LOWER_CONDITION, boolean2 ? Boolean.TRUE : Boolean.FALSE);
		hashMap.put(PARAM_ATTACKED, Boolean.TRUE);
	}

	private int calcDamageToVehicle(int int1, int int2, boolean boolean1) {
		if (int1 <= 0) {
			return 0;
		} else {
			float float1 = (float)int1;
			float float2 = PZMath.clamp(float1 / (boolean1 ? 10.0F : 40.0F), 0.0F, 1.0F);
			int int3 = (int)((float)int2 * float2);
			return PZMath.clamp(int3, 1, int2);
		}
	}

	private static void splash(IsoMovingObject movingObject, HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		IsoGameCharacter gameCharacter2 = (IsoGameCharacter)movingObject;
		if (handWeapon != null && SandboxOptions.instance.BloodLevel.getValue() > 1) {
			int int1 = handWeapon.getSplatNumber();
			if (int1 < 1) {
				int1 = 1;
			}

			if (Core.bLastStand) {
				int1 *= 3;
			}

			switch (SandboxOptions.instance.BloodLevel.getValue()) {
			case 2: 
				int1 /= 2;
			
			case 3: 
			
			default: 
				break;
			
			case 4: 
				int1 *= 2;
				break;
			
			case 5: 
				int1 *= 5;
			
			}

			for (int int2 = 0; int2 < int1; ++int2) {
				gameCharacter2.splatBlood(3, 0.3F);
			}
		}

		byte byte1 = 3;
		byte byte2 = 7;
		switch (SandboxOptions.instance.BloodLevel.getValue()) {
		case 1: 
			byte2 = 0;
			break;
		
		case 2: 
			byte2 = 4;
			byte1 = 5;
		
		case 3: 
		
		default: 
			break;
		
		case 4: 
			byte2 = 10;
			byte1 = 2;
			break;
		
		case 5: 
			byte2 = 15;
			byte1 = 0;
		
		}
		if (SandboxOptions.instance.BloodLevel.getValue() > 1) {
			gameCharacter2.splatBloodFloorBig(0.3F);
		}

		float float1 = 0.5F;
		if (gameCharacter2 instanceof IsoZombie && (((IsoZombie)gameCharacter2).bCrawling || gameCharacter2.getCurrentState() == ZombieOnGroundState.instance())) {
			float1 = 0.2F;
		}

		float float2 = Rand.Next(1.5F, 5.0F);
		float float3 = Rand.Next(1.5F, 5.0F);
		if (gameCharacter instanceof IsoPlayer && ((IsoPlayer)gameCharacter).bDoShove) {
			float2 = Rand.Next(0.0F, 0.5F);
			float3 = Rand.Next(0.0F, 0.5F);
		}

		if (byte2 > 0) {
			gameCharacter2.playBloodSplatterSound();
		}

		for (int int3 = 0; int3 < byte2; ++int3) {
			if (Rand.Next(byte1) == 0) {
				new IsoZombieGiblets(IsoZombieGiblets.GibletType.A, gameCharacter2.getCell(), gameCharacter2.getX(), gameCharacter2.getY(), gameCharacter2.getZ() + float1, gameCharacter2.getHitDir().x * float2, gameCharacter2.getHitDir().y * float3);
			}
		}
	}

	private boolean checkObjectHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon, IsoGridSquare square, boolean boolean1, boolean boolean2) {
		if (square == null) {
			return false;
		} else {
			for (int int1 = square.getSpecialObjects().size() - 1; int1 >= 0; --int1) {
				IsoObject object = (IsoObject)square.getSpecialObjects().get(int1);
				IsoDoor door = (IsoDoor)Type.tryCastTo(object, IsoDoor.class);
				IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(object, IsoThumpable.class);
				IsoWindow window = (IsoWindow)Type.tryCastTo(object, IsoWindow.class);
				Thumpable thumpable2;
				if (door != null && (boolean1 && door.north || boolean2 && !door.north)) {
					thumpable2 = door.getThumpableFor(gameCharacter);
					if (thumpable2 != null) {
						thumpable2.WeaponHit(gameCharacter, handWeapon);
						return true;
					}
				}

				if (thumpable != null) {
					if (!thumpable.isDoor() && !thumpable.isWindow() && thumpable.isBlockAllTheSquare()) {
						thumpable2 = thumpable.getThumpableFor(gameCharacter);
						if (thumpable2 != null) {
							thumpable2.WeaponHit(gameCharacter, handWeapon);
							return true;
						}
					} else if (boolean1 && thumpable.north || boolean2 && !thumpable.north) {
						thumpable2 = thumpable.getThumpableFor(gameCharacter);
						if (thumpable2 != null) {
							thumpable2.WeaponHit(gameCharacter, handWeapon);
							return true;
						}
					}
				}

				if (window != null && (boolean1 && window.north || boolean2 && !window.north)) {
					thumpable2 = window.getThumpableFor(gameCharacter);
					if (thumpable2 != null) {
						thumpable2.WeaponHit(gameCharacter, handWeapon);
						return true;
					}
				}
			}

			return false;
		}
	}

	private boolean CheckObjectHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (gameCharacter.isAimAtFloor()) {
			this.bHitOnlyTree = false;
			return false;
		} else {
			boolean boolean1 = false;
			int int1 = 0;
			int int2 = 0;
			IsoDirections directions = IsoDirections.fromAngle(gameCharacter.getForwardDirection());
			int int3 = 0;
			int int4 = 0;
			if (directions == IsoDirections.NE || directions == IsoDirections.N || directions == IsoDirections.NW) {
				--int4;
			}

			if (directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW) {
				++int4;
			}

			if (directions == IsoDirections.NW || directions == IsoDirections.W || directions == IsoDirections.SW) {
				--int3;
			}

			if (directions == IsoDirections.NE || directions == IsoDirections.E || directions == IsoDirections.SE) {
				++int3;
			}

			IsoCell cell = IsoWorld.instance.CurrentCell;
			IsoGridSquare square = gameCharacter.getCurrentSquare();
			IsoGridSquare square2 = cell.getGridSquare(square.getX() + int3, square.getY() + int4, square.getZ());
			if (square2 != null) {
				if (this.checkObjectHit(gameCharacter, handWeapon, square2, false, false)) {
					boolean1 = true;
					++int1;
				}

				if (!square2.isBlockedTo(square)) {
					for (int int5 = 0; int5 < square2.getObjects().size(); ++int5) {
						IsoObject object = (IsoObject)square2.getObjects().get(int5);
						if (object instanceof IsoTree) {
							((IsoTree)object).WeaponHit(gameCharacter, handWeapon);
							boolean1 = true;
							++int1;
							++int2;
							if (object.getObjectIndex() == -1) {
								--int5;
							}
						}
					}
				}
			}

			if ((directions == IsoDirections.NE || directions == IsoDirections.N || directions == IsoDirections.NW) && this.checkObjectHit(gameCharacter, handWeapon, square, true, false)) {
				boolean1 = true;
				++int1;
			}

			IsoGridSquare square3;
			if (directions == IsoDirections.SE || directions == IsoDirections.S || directions == IsoDirections.SW) {
				square3 = cell.getGridSquare(square.getX(), square.getY() + 1, square.getZ());
				if (this.checkObjectHit(gameCharacter, handWeapon, square3, true, false)) {
					boolean1 = true;
					++int1;
				}
			}

			if (directions == IsoDirections.SE || directions == IsoDirections.E || directions == IsoDirections.NE) {
				square3 = cell.getGridSquare(square.getX() + 1, square.getY(), square.getZ());
				if (this.checkObjectHit(gameCharacter, handWeapon, square3, false, true)) {
					boolean1 = true;
					++int1;
				}
			}

			if ((directions == IsoDirections.NW || directions == IsoDirections.W || directions == IsoDirections.SW) && this.checkObjectHit(gameCharacter, handWeapon, square, false, true)) {
				boolean1 = true;
				++int1;
			}

			this.bHitOnlyTree = boolean1 && int1 == int2;
			return boolean1;
		}
	}

	private LosUtil.TestResults los(int int1, int int2, int int3, int int4, int int5, SwipeStatePlayer.LOSVisitor lOSVisitor) {
		IsoCell cell = IsoWorld.instance.CurrentCell;
		int int6 = int4 - int2;
		int int7 = int3 - int1;
		int int8 = int5 - int5;
		float float1 = 0.5F;
		float float2 = 0.5F;
		IsoGridSquare square = cell.getGridSquare(int1, int2, int5);
		float float3;
		float float4;
		IsoGridSquare square2;
		if (Math.abs(int7) > Math.abs(int6)) {
			float3 = (float)int6 / (float)int7;
			float4 = (float)int8 / (float)int7;
			float1 += (float)int2;
			float2 += (float)int5;
			int7 = int7 < 0 ? -1 : 1;
			float3 *= (float)int7;
			for (float4 *= (float)int7; int1 != int3; square = square2) {
				int1 += int7;
				float1 += float3;
				float2 += float4;
				square2 = cell.getGridSquare(int1, (int)float1, (int)float2);
				if (lOSVisitor.visit(square2, square)) {
					return lOSVisitor.getResult();
				}
			}
		} else {
			float3 = (float)int7 / (float)int6;
			float4 = (float)int8 / (float)int6;
			float1 += (float)int1;
			float2 += (float)int5;
			int6 = int6 < 0 ? -1 : 1;
			float3 *= (float)int6;
			for (float4 *= (float)int6; int2 != int4; square = square2) {
				int2 += int6;
				float1 += float3;
				float2 += float4;
				square2 = cell.getGridSquare((int)float1, int2, (int)float2);
				if (lOSVisitor.visit(square2, square)) {
					return lOSVisitor.getResult();
				}
			}
		}

		return LosUtil.TestResults.Clear;
	}

	private IsoWindow getWindowBetween(int int1, int int2, int int3, int int4, int int5) {
		this.windowVisitor.init();
		this.los(int1, int2, int3, int4, int5, this.windowVisitor);
		return this.windowVisitor.window;
	}

	private IsoWindow getWindowBetween(IsoMovingObject movingObject, IsoMovingObject movingObject2) {
		return this.getWindowBetween((int)movingObject.x, (int)movingObject.y, (int)movingObject2.x, (int)movingObject2.y, (int)movingObject.z);
	}

	private boolean isWindowBetween(IsoMovingObject movingObject, IsoMovingObject movingObject2) {
		return this.getWindowBetween(movingObject, movingObject2) != null;
	}

	private void smashWindowBetween(IsoGameCharacter gameCharacter, IsoMovingObject movingObject, HandWeapon handWeapon) {
		IsoWindow window = this.getWindowBetween(gameCharacter, movingObject);
		if (window != null) {
			window.WeaponHit(gameCharacter, handWeapon);
		}
	}

	public void changeWeapon(HandWeapon handWeapon, IsoGameCharacter gameCharacter) {
		if (handWeapon != null && handWeapon.isUseSelf()) {
			gameCharacter.getInventory().setDrawDirty(true);
			Iterator iterator = gameCharacter.getInventory().getItems().iterator();
			while (iterator.hasNext()) {
				InventoryItem inventoryItem = (InventoryItem)iterator.next();
				if (inventoryItem != handWeapon && inventoryItem instanceof HandWeapon && inventoryItem.getType() == handWeapon.getType() && inventoryItem.getCondition() > 0) {
					if (gameCharacter.getPrimaryHandItem() == handWeapon && gameCharacter.getSecondaryHandItem() == handWeapon) {
						gameCharacter.setPrimaryHandItem(inventoryItem);
						gameCharacter.setSecondaryHandItem(inventoryItem);
					} else if (gameCharacter.getPrimaryHandItem() == handWeapon) {
						gameCharacter.setPrimaryHandItem(inventoryItem);
					} else if (gameCharacter.getSecondaryHandItem() == handWeapon) {
						gameCharacter.setSecondaryHandItem(inventoryItem);
					}

					return;
				}
			}
		}

		if (handWeapon == null || handWeapon.getCondition() <= 0 || handWeapon.isUseSelf()) {
			HandWeapon handWeapon2 = (HandWeapon)gameCharacter.getInventory().getBestWeapon(gameCharacter.getDescriptor());
			gameCharacter.setPrimaryHandItem((InventoryItem)null);
			if (gameCharacter.getSecondaryHandItem() == handWeapon) {
				gameCharacter.setSecondaryHandItem((InventoryItem)null);
			}

			if (handWeapon2 != null && handWeapon2 != gameCharacter.getPrimaryHandItem() && handWeapon2.getCondition() > 0) {
				gameCharacter.setPrimaryHandItem(handWeapon2);
				if (handWeapon2.isTwoHandWeapon() && gameCharacter.getSecondaryHandItem() == null) {
					gameCharacter.setSecondaryHandItem(handWeapon2);
				}
			}
		}
	}

	public static final class AttackVars {
		public HandWeapon weapon;
		public IsoGameCharacter targetOnGround;
		public boolean bAimAtFloor;
		public boolean bCloseKill;
		public boolean bDoShove;
		public float useChargeDelta;
		public int recoilDelay;
		public final ArrayList targetsStanding = new ArrayList();
		public final ArrayList targetsProne = new ArrayList();
	}

	private static final class WindowVisitor implements SwipeStatePlayer.LOSVisitor {
		LosUtil.TestResults test;
		IsoWindow window;

		void init() {
			this.test = LosUtil.TestResults.Clear;
			this.window = null;
		}

		public boolean visit(IsoGridSquare square, IsoGridSquare square2) {
			if (square != null && square2 != null) {
				boolean boolean1 = true;
				boolean boolean2 = false;
				LosUtil.TestResults testResults = square.testVisionAdjacent(square2.getX() - square.getX(), square2.getY() - square.getY(), square2.getZ() - square.getZ(), boolean1, boolean2);
				if (testResults == LosUtil.TestResults.ClearThroughWindow) {
					IsoWindow window = square.getWindowTo(square2);
					if (this.isHittable(window) && window.TestVision(square, square2) == IsoObject.VisionResult.Unblocked) {
						this.window = window;
						return true;
					}
				}

				if (testResults == LosUtil.TestResults.Blocked || this.test == LosUtil.TestResults.Clear || testResults == LosUtil.TestResults.ClearThroughWindow && this.test == LosUtil.TestResults.ClearThroughOpenDoor) {
					this.test = testResults;
				} else if (testResults == LosUtil.TestResults.ClearThroughClosedDoor && this.test == LosUtil.TestResults.ClearThroughOpenDoor) {
					this.test = testResults;
				}

				return this.test == LosUtil.TestResults.Blocked;
			} else {
				return false;
			}
		}

		public LosUtil.TestResults getResult() {
			return this.test;
		}

		boolean isHittable(IsoWindow window) {
			if (window == null) {
				return false;
			} else if (window.isBarricaded()) {
				return true;
			} else {
				return !window.isDestroyed() && !window.IsOpen();
			}
		}
	}

	public static final class HitInfo {
		public IsoMovingObject object;
		public IsoWindow window;
		public float x;
		public float y;
		public float z;
		public float dot;
		public float distSq;
		public int chance = 0;

		public SwipeStatePlayer.HitInfo init(IsoMovingObject movingObject, float float1, float float2, float float3, float float4, float float5) {
			this.window = null;
			this.object = movingObject;
			this.x = float3;
			this.y = float4;
			this.z = float5;
			this.dot = float1;
			this.distSq = float2;
			return this;
		}

		public SwipeStatePlayer.HitInfo init(IsoWindow window, float float1, float float2) {
			this.object = null;
			this.window = window;
			this.z = window.getZ();
			this.dot = float1;
			this.distSq = float2;
			return this;
		}
	}

	public static class CustomComparator implements Comparator {

		public int compare(SwipeStatePlayer.HitInfo hitInfo, SwipeStatePlayer.HitInfo hitInfo2) {
			float float1 = hitInfo.distSq;
			float float2 = hitInfo2.distSq;
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(hitInfo.object, IsoZombie.class);
			IsoZombie zombie2 = (IsoZombie)Type.tryCastTo(hitInfo2.object, IsoZombie.class);
			if (zombie != null && zombie2 != null) {
				boolean boolean1 = SwipeStatePlayer.isProne(zombie);
				boolean boolean2 = SwipeStatePlayer.isProne(zombie2);
				boolean boolean3 = zombie.isCurrentState(ZombieGetUpState.instance());
				boolean boolean4 = zombie2.isCurrentState(ZombieGetUpState.instance());
				if (boolean3 && !boolean4 && boolean2) {
					return -1;
				}

				if (!boolean3 && boolean1 && boolean4) {
					return 1;
				}

				if (boolean1 && boolean2) {
					if (zombie.isCrawling() && !zombie2.isCrawling()) {
						return -1;
					}

					if (!zombie.isCrawling() && zombie2.isCrawling()) {
						return 1;
					}
				}
			}

			if (float1 > float2) {
				return 1;
			} else {
				return float2 > float1 ? -1 : 0;
			}
		}
	}

	private interface LOSVisitor {

		boolean visit(IsoGridSquare square, IsoGridSquare square2);

		LosUtil.TestResults getResult();
	}
}
