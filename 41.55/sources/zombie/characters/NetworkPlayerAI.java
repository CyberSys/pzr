package zombie.characters;

import java.util.LinkedList;
import zombie.GameTime;
import zombie.ai.states.FishingState;
import zombie.core.math.PZMath;
import zombie.core.utils.UpdateLimit;
import zombie.core.utils.UpdateTimer;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoUtils;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.network.GameServer;
import zombie.network.packets.EventUpdatePacket;
import zombie.network.packets.PlayerPacket;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;


public class NetworkPlayerAI {
	public final LinkedList events = new LinkedList();
	public NetworkPlayerAI.Event lastEvent = null;
	public boolean pressedMovement = false;
	public float climbSpeed = 0.16F;
	public float climbDownSpeed = 0.16F;
	IsoPlayer player;
	private PathFindBehavior2 pfb2 = null;
	private final UpdateTimer timer = new UpdateTimer();
	private byte lastDirection = 0;
	private boolean needUpdate = false;
	public boolean usePathFind = false;
	public float targetX = 0.0F;
	public float targetY = 0.0F;
	public int targetZ = 0;
	public int targetT = 0;
	public boolean needToMovingUsingPathFinder = false;
	public boolean forcePathFinder = false;
	public Vector2 direction = new Vector2();
	public Vector2 distance = new Vector2();
	public boolean moving = false;
	public static float controlQuality = 0.0F;
	public byte footstepSoundRadius = 0;
	public int lastBooleanVariables = 0;
	public float lastForwardDirection = 0.0F;
	public float lastPlayerMoveDirLen = 0.0F;
	private static final int predictInterval = 1000;

	public NetworkPlayerAI(IsoPlayer player) {
		this.player = player;
		this.pfb2 = this.player.getPathFindBehavior2();
	}

	public void needToUpdate() {
		this.needUpdate = true;
	}

	public boolean isNeedToUpdate() {
		int int1 = NetworkPlayerVariables.getBooleanVariables(this.player);
		byte byte1 = (byte)((int)(this.player.playerMoveDir.getDirection() * 10.0F));
		if (!this.timer.check() && int1 == this.lastBooleanVariables && this.lastDirection == byte1 && !this.needUpdate) {
			return false;
		} else {
			this.lastDirection = byte1;
			this.needUpdate = false;
			return true;
		}
	}

	public void setUpdateTimer(float float1) {
		this.timer.reset((long)PZMath.clamp((int)float1, 200, 3800));
	}

	private void setUsingExtrapolation(PlayerPacket playerPacket, int int1, int int2) {
		Vector2 vector2 = this.player.dir.ToVector();
		this.player.networkCharacter.checkResetPlayer(int1);
		NetworkCharacter.Transform transform = this.player.networkCharacter.predict(int2, int1, this.player.x, this.player.y, vector2.x, vector2.y);
		if (this.player.z == this.pfb2.getTargetZ() && !PolygonalMap2.instance.lineClearCollide(this.player.x, this.player.y, transform.position.x, transform.position.y, (int)this.player.z, (IsoMovingObject)null)) {
			playerPacket.x = transform.position.x;
			playerPacket.y = transform.position.y;
			playerPacket.z = (byte)((int)this.pfb2.getTargetZ());
			playerPacket.t = transform.time;
		} else {
			Vector2 vector22 = PolygonalMap2.instance.getCollidepoint(this.player.x, this.player.y, transform.position.x, transform.position.y, (int)this.player.z, (IsoMovingObject)null, 2);
			playerPacket.x = vector22.x;
			playerPacket.y = vector22.y;
			playerPacket.z = (byte)((int)this.player.z);
			playerPacket.t = (int)((float)int1 + (float)int2 * IsoUtils.DistanceTo(this.player.x, this.player.y, vector22.x, vector22.y) / IsoUtils.DistanceTo(this.player.x, this.player.y, transform.position.x, transform.position.y));
		}

		playerPacket.usePathFinder = false;
	}

	private void setUsingPathFindState(PlayerPacket playerPacket, int int1) {
		this.player.networkCharacter.checkResetPlayer(int1);
		playerPacket.x = this.pfb2.getTargetX();
		playerPacket.y = this.pfb2.getTargetY();
		playerPacket.z = (byte)((int)this.pfb2.getTargetZ());
		playerPacket.t = -1;
		playerPacket.usePathFinder = true;
	}

	public boolean set(PlayerPacket playerPacket) {
		int int1 = (int)(GameTime.getServerTime() / 1000000L);
		playerPacket.realx = this.player.x;
		playerPacket.realy = this.player.y;
		playerPacket.realz = (byte)((int)this.player.z);
		playerPacket.realdir = (byte)this.player.dir.index();
		playerPacket.realt = int1;
		if (this.player.vehicle == null) {
			playerPacket.VehicleID = -1;
			playerPacket.VehicleSeat = -1;
		} else {
			playerPacket.VehicleID = this.player.vehicle.VehicleID;
			playerPacket.VehicleSeat = (short)this.player.vehicle.getSeat(this.player);
		}

		boolean boolean1 = this.timer.check();
		if (boolean1) {
			this.setUpdateTimer(600.0F);
		}

		if (this.pfb2.isMovingUsingPathFind()) {
			this.setUsingPathFindState(playerPacket, int1);
		} else {
			this.setUsingExtrapolation(playerPacket, int1, 1000);
		}

		boolean boolean2 = (double)this.player.playerMoveDir.getLength() < 0.01 && this.lastPlayerMoveDirLen > 0.01F;
		this.lastPlayerMoveDirLen = this.player.playerMoveDir.getLength();
		playerPacket.booleanVariables = NetworkPlayerVariables.getBooleanVariables(this.player);
		this.pressedMovement = false;
		boolean boolean3 = this.lastBooleanVariables != playerPacket.booleanVariables;
		this.lastBooleanVariables = playerPacket.booleanVariables;
		playerPacket.direction = this.player.getForwardDirection().getDirection();
		boolean boolean4 = Math.abs(this.lastForwardDirection - playerPacket.direction) > 0.2F;
		this.lastForwardDirection = playerPacket.direction;
		playerPacket.footstepSoundRadius = this.footstepSoundRadius;
		return boolean1 || boolean3 || boolean4 || this.player.JustMoved || boolean2;
	}

	public void parse(PlayerPacket playerPacket) {
		if (!this.player.isTeleporting()) {
			this.targetX = playerPacket.x;
			this.targetY = playerPacket.y;
			this.targetZ = playerPacket.z;
			this.targetT = playerPacket.t;
			if (this.targetX == this.player.x && this.targetY == this.player.y) {
				this.player.JustMoved = false;
			} else {
				this.player.JustMoved = true;
			}

			this.needToMovingUsingPathFinder = playerPacket.usePathFinder;
			this.direction.set((float)Math.cos((double)playerPacket.direction), (float)Math.sin((double)playerPacket.direction));
			this.distance.set(playerPacket.x - this.player.x, playerPacket.y - this.player.y);
			if (this.usePathFind) {
				this.pfb2.pathToLocationF(playerPacket.x, playerPacket.y, (float)playerPacket.z);
				this.pfb2.walkingOnTheSpot.reset(this.player.x, this.player.y);
				this.pfb2.setTargetT(this.targetT);
			}

			BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(playerPacket.VehicleID);
			NetworkPlayerVariables.setBooleanVariables(this.player, playerPacket.booleanVariables);
			this.player.setbSeenThisFrame(false);
			this.player.setbCouldBeSeenThisFrame(false);
			this.player.TimeSinceLastNetData = 0;
			this.player.ensureOnTile();
			this.player.realx = playerPacket.realx;
			this.player.realy = playerPacket.realy;
			this.player.realz = playerPacket.realz;
			this.player.realdir = IsoDirections.fromIndex(playerPacket.realdir);
			this.player.lastUpdateX = this.player.x;
			this.player.lastUpdateY = this.player.y;
			this.player.lastUpdateT = (float)(GameTime.getServerTime() / 1000000L);
			int int1 = (int)(GameTime.getServerTime() / 1000000L);
			if (playerPacket.t == playerPacket.realt) {
				controlQuality = 1.0F;
			} else {
				controlQuality = 1.0F - Math.min(1.0F, Math.max(0.0F, (float)((int1 - playerPacket.realt) / (playerPacket.t - playerPacket.realt))));
			}

			this.footstepSoundRadius = playerPacket.footstepSoundRadius;
			String string;
			IsoGameCharacter gameCharacter;
			if (this.player.getVehicle() == null) {
				if (baseVehicle != null) {
					if (playerPacket.VehicleSeat >= 0 && playerPacket.VehicleSeat < baseVehicle.getMaxPassengers()) {
						gameCharacter = baseVehicle.getCharacter(playerPacket.VehicleSeat);
						if (gameCharacter == null) {
							if (GameServer.bDebug) {
								DebugLog.log(this.player.getUsername() + " got in vehicle " + baseVehicle.VehicleID + " seat " + playerPacket.VehicleSeat);
							}

							baseVehicle.enterRSync(playerPacket.VehicleSeat, this.player, baseVehicle);
						} else if (gameCharacter != this.player) {
							string = this.player.getUsername();
							DebugLog.log(string + " got in same seat as " + ((IsoPlayer)gameCharacter).getUsername());
							this.player.sendObjectChange("exitVehicle");
						}
					} else {
						DebugLog.log(this.player.getUsername() + " invalid seat vehicle " + baseVehicle.VehicleID + " seat " + playerPacket.VehicleSeat);
					}
				}
			} else if (baseVehicle != null) {
				if (baseVehicle == this.player.getVehicle() && this.player.getVehicle().getSeat(this.player) != -1) {
					gameCharacter = baseVehicle.getCharacter(playerPacket.VehicleSeat);
					if (gameCharacter == null) {
						if (baseVehicle.getSeat(this.player) != playerPacket.VehicleSeat) {
							baseVehicle.switchSeatRSync(this.player, playerPacket.VehicleSeat);
						}
					} else if (gameCharacter != this.player) {
						string = this.player.getUsername();
						DebugLog.log(string + " switched to same seat as " + ((IsoPlayer)gameCharacter).getUsername());
						this.player.sendObjectChange("exitVehicle");
					}
				} else {
					string = this.player.getUsername();
					DebugLog.log(string + " vehicle/seat remote " + baseVehicle.VehicleID + "/" + playerPacket.VehicleSeat + " local " + this.player.getVehicle().VehicleID + "/" + this.player.getVehicle().getSeat(this.player));
					this.player.sendObjectChange("exitVehicle");
				}
			} else {
				this.player.getVehicle().exitRSync(this.player);
				this.player.setVehicle((BaseVehicle)null);
			}
		}
	}

	public static class Event {
		public static final UpdateLimit eventTimer = new UpdateLimit(7000L);
		public short id;
		public float x;
		public float y;
		public float z;
		public byte dir;
		public byte name;
		public String type1;
		public String type2;
		public String type3;
		public String type4;
		public float param1;
		public float param2;
		public float walkInjury;
		public float walkSpeed;
		public int booleanVariables;

		public Event(NetworkPlayerAI.Event event) {
			this.set(event);
			eventTimer.Reset(5000L);
		}

		public Event() {
		}

		public void set(NetworkPlayerAI.Event event) {
			this.id = event.id;
			this.x = event.x;
			this.y = event.y;
			this.z = event.z;
			this.dir = event.dir;
			this.name = event.name;
			this.type1 = event.type1;
			this.type2 = event.type2;
			this.type3 = event.type3;
			this.type4 = event.type4;
			this.param1 = event.param1;
			this.param2 = event.param2;
			this.walkInjury = event.walkInjury;
			this.walkSpeed = event.walkSpeed;
			this.booleanVariables = event.booleanVariables;
		}

		public boolean isMovableEvent() {
			if (this.name != -1 && this.name != -2) {
				if (this.name >= 0 && this.name < EventUpdatePacket.EventUpdate.values().length) {
					EventUpdatePacket.EventUpdate eventUpdate = EventUpdatePacket.EventUpdate.values()[this.name];
					return EventUpdatePacket.EventUpdate.EventBandage.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventWearClothing.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventEating.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventDrinking.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventAttachItem.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventTakeWater.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventClimbFence.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventFallClimb.equals(eventUpdate) || EventUpdatePacket.EventUpdate.EventReloading.equals(eventUpdate);
				} else {
					return false;
				}
			} else {
				return "Drink".equals(this.type1) || "Eat".equals(this.type1);
			}
		}

		public String getDescription() {
			String string;
			if (this.name == -1) {
				string = "start action";
			} else if (this.name == -2) {
				string = "end action";
			} else if (this.name >= 0 && this.name < EventUpdatePacket.EventUpdate.values().length) {
				string = EventUpdatePacket.EventUpdate.values()[this.name].name();
			} else {
				string = "unknown";
			}

			return String.format("%s(%d), %s %s  %s %s %f %f", string, this.name, this.type1, this.type2, this.type3, this.type4, this.param1, this.param2);
		}

		public IsoWindow getWindow(IsoPlayer player) {
			IsoDirections[] directionsArray = IsoDirections.values();
			int int1 = directionsArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoDirections directions = directionsArray[int2];
				IsoObject object = player.getContextDoorOrWindowOrWindowFrame(directions);
				if (object instanceof IsoWindow) {
					return (IsoWindow)object;
				}
			}

			return null;
		}

		public IsoObject getObject(IsoPlayer player) {
			IsoDirections[] directionsArray = IsoDirections.values();
			int int1 = directionsArray.length;
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoDirections directions = directionsArray[int2];
				IsoObject object = player.getContextDoorOrWindowOrWindowFrame(directions);
				if (object instanceof IsoWindow || object instanceof IsoThumpable || IsoWindowFrame.isWindowFrame(object)) {
					return object;
				}
			}

			return null;
		}

		private IsoDirections checkCurrentIsEventGridSquareFence(IsoPlayer player) {
			IsoGridSquare square = player.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z);
			IsoGridSquare square2 = player.getCell().getGridSquare((double)this.x, (double)(this.y + 1.0F), (double)this.z);
			IsoGridSquare square3 = player.getCell().getGridSquare((double)(this.x + 1.0F), (double)this.y, (double)this.z);
			IsoDirections directions;
			if (square.Is(IsoFlagType.HoppableN)) {
				directions = IsoDirections.N;
			} else if (square.Is(IsoFlagType.HoppableW)) {
				directions = IsoDirections.W;
			} else if (square2.Is(IsoFlagType.HoppableN)) {
				directions = IsoDirections.S;
			} else if (square3.Is(IsoFlagType.HoppableW)) {
				directions = IsoDirections.E;
			} else {
				directions = IsoDirections.Max;
			}

			return directions;
		}

		public boolean isTimeout() {
			return eventTimer.Check();
		}

		public boolean process(IsoPlayer player) {
			boolean boolean1 = false;
			player.overridePrimaryHandModel = null;
			player.overrideSecondaryHandModel = null;
			if (!this.requireNonMoving(this.name) || player.getCurrentSquare() == player.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z) && !player.isPlayerMoving()) {
				NetworkPlayerVariables.setBooleanVariables(player, this.booleanVariables);
				player.setVariable("WalkInjury", this.walkInjury);
				player.setVariable("WalkSpeed", this.walkSpeed);
				if (this.name == -1) {
					player.setVariable("PerformingAction", this.type1);
					player.setVariable("IsPerformingAnAction", true);
					player.overridePrimaryHandModel = this.type2;
					player.overrideSecondaryHandModel = this.type3;
					if (!StringUtils.isNullOrEmpty(this.type2) || !StringUtils.isNullOrEmpty(this.type3)) {
						player.forceNullOverride = true;
					}

					player.resetModelNextFrame();
					boolean1 = true;
				} else if (this.name == -2) {
					NetworkPlayerAI.Event event = player.networkAI.lastEvent;
					if (event != null && CharacterActionAnims.Reload.name().equals(event.type1)) {
					}

					player.clearNetworkEvents();
					boolean1 = true;
				} else if (this.name >= 0 && this.name < EventUpdatePacket.EventUpdate.values().length) {
					EventUpdatePacket.EventUpdate eventUpdate = EventUpdatePacket.EventUpdate.values()[this.name];
					IsoWindow window;
					switch (eventUpdate) {
					case EventCleanBlood: 
						player.setVariable("PerformingAction", this.type1);
						player.setVariable("LootPosition", this.type2);
						player.forceNullOverride = true;
						player.overridePrimaryHandModel = this.type3;
						player.overrideSecondaryHandModel = this.type4;
						player.resetModelNextFrame();
						player.setVariable("IsPerformingAnAction", true);
						return true;
					
					case EventToggleTorch: 
						if (player.getPrimaryHandItem() != null && player.getPrimaryHandItem().canEmitLight()) {
							player.getPrimaryHandItem().setActivatedRemote(this.param1 == 1.0F);
						} else if (player.getSecondaryHandItem() != null && player.getSecondaryHandItem().canEmitLight()) {
							player.getSecondaryHandItem().setActivatedRemote(this.param1 == 1.0F);
						}

					
					case EventFallClimb: 
						if (this.param1 == 1.0F) {
							player.setVariable("ClimbFenceOutcome", this.type1);
							player.setVariable("BumpDone", true);
							player.setFallOnFront(true);
						}

						return true;
					
					case EventWashClothing: 
						player.setVariable("PerformingAction", this.type1);
						player.setVariable("LootPosition", "");
						player.overridePrimaryHandModel = null;
						player.overrideSecondaryHandModel = null;
						player.resetModelNextFrame();
						player.setVariable("IsPerformingAnAction", true);
						return true;
					
					case collideWithWall: 
						player.setCollideType(this.type1);
						player.actionContext.reportEvent("collideWithWall");
						return true;
					
					case EventTakeWater: 
						player.setVariable("PerformingAction", this.type1);
						player.setVariable("FoodType", this.type2);
						player.forceNullOverride = true;
						player.overridePrimaryHandModel = this.type3;
						player.overrideSecondaryHandModel = this.type4;
						player.resetModelNextFrame();
						player.setVariable("IsPerformingAnAction", true);
						return true;
					
					case EventLootItem: 
						player.setVariable("PerformingAction", "Loot");
						player.setVariable("LootPosition", this.type1);
						player.setVariable("IsPerformingAnAction", true);
						player.forceNullOverride = true;
						return true;
					
					case EventAttachItem: 
						player.setVariable("PerformingAction", this.type1);
						player.setVariable("AttachAnim", this.type2);
						player.setVariable("IsPerformingAnAction", true);
						return true;
					
					case EventReloading: 
						player.setVariable("PerformingAction", "Reload");
						player.setVariable("WeaponReloadType", this.type1);
						player.setVariable("isLoading", Boolean.parseBoolean(this.type2));
						player.setVariable("isRacking", Boolean.parseBoolean(this.type3));
						player.setVariable("isUnloading", Boolean.parseBoolean(this.type4));
						player.setVariable("IsPerformingAnAction", true);
						return true;
					
					case EventBandage: 
						player.setVariable("PerformingAction", "Bandage");
						player.setVariable("IsPerformingAnAction", true);
						player.setVariable("BandageType", this.type1);
						break;
					
					case EventRead: 
						player.setVariable("PerformingAction", "read");
						player.setVariable("IsPerformingAnAction", true);
						player.setVariable("ReadType", this.type1);
						player.forceNullOverride = true;
						player.overrideSecondaryHandModel = this.type2;
						player.resetModelNextFrame();
						return true;
					
					case EventWearClothing: 
						player.setVariable("PerformingAction", "WearClothing");
						player.setVariable("WearClothingLocation", this.type1);
						player.setVariable("IsPerformingAnAction", true);
						return true;
					
					case EventEating: 
						player.setVariable("PerformingAction", this.type1);
						player.setVariable("FoodType", this.type2);
						player.forceNullOverride = true;
						player.overridePrimaryHandModel = this.type3;
						player.overrideSecondaryHandModel = this.type4;
						player.resetModelNextFrame();
						player.setVariable("IsPerformingAnAction", true);
						return true;
					
					case EventFishing: 
						player.setVariable("FishingStage", this.type1);
						if (!FishingState.instance().equals(player.getCurrentState())) {
							player.setVariable("forceGetUp", true);
							player.actionContext.reportEvent("EventFishing");
						}

						return true;
					
					case EventFitness: 
						player.setVariable("ExerciseType", this.type1);
						player.actionContext.reportEvent("EventFitness");
						boolean1 = true;
						break;
					
					case EventUpdateFitness: 
						player.clearVariable("ExerciseHand");
						player.setVariable("ExerciseType", this.type2);
						if (!StringUtils.isNullOrEmpty(this.type1)) {
							player.setVariable("ExerciseHand", this.type1);
						}

						player.setVariable("FitnessStruggle", this.param1 == 1.0F);
						player.setPrimaryHandItem((InventoryItem)null);
						player.setSecondaryHandItem((InventoryItem)null);
						player.overridePrimaryHandModel = null;
						player.overrideSecondaryHandModel = null;
						player.overridePrimaryHandModel = this.type3;
						player.overrideSecondaryHandModel = this.type4;
						player.resetModelNextFrame();
						return true;
					
					case EventEmote: 
						player.setVariable("emote", this.type1);
						player.actionContext.reportEvent("EventEmote");
						return true;
					
					case EventSitOnGround: 
						player.actionContext.reportEvent("EventSitOnGround");
						boolean1 = true;
						break;
					
					case EventClimbRope: 
						player.networkAI.climbSpeed = this.param1;
						player.networkAI.climbDownSpeed = this.param2;
						player.climbSheetRope();
						return true;
					
					case EventClimbDownRope: 
						player.networkAI.climbSpeed = this.param1;
						player.networkAI.climbDownSpeed = this.param2;
						player.climbDownSheetRope();
						return true;
					
					case EventClimbFence: 
						IsoDirections directions = this.checkCurrentIsEventGridSquareFence(player);
						if (directions != IsoDirections.Max) {
							player.climbOverFence(directions);
							boolean1 = true;
							if (player.isSprinting()) {
								player.setVariable("VaultOverSprint", true);
							}

							if (player.isRunning()) {
								player.setVariable("VaultOverRun", true);
							}

							return true;
						}

						break;
					
					case EventClimbWall: 
						IsoDirections[] directionsArray = IsoDirections.values();
						int int1 = directionsArray.length;
						for (int int2 = 0; int2 < int1; ++int2) {
							IsoDirections directions2 = directionsArray[int2];
							if (player.climbOverWall(directions2)) {
								return true;
							}
						}

						return boolean1;
					
					case EventClimbWindow: 
						IsoObject object = this.getObject(player);
						if (object instanceof IsoWindow) {
							player.climbThroughWindow((IsoWindow)object);
							return true;
						}

						if (object instanceof IsoThumpable) {
							player.climbThroughWindow((IsoThumpable)object);
							return true;
						}

						if (IsoWindowFrame.isWindowFrame(object)) {
							player.climbThroughWindowFrame(object);
							return true;
						}

						break;
					
					case EventOpenWindow: 
						window = this.getWindow(player);
						if (window != null) {
							player.openWindow(window);
							boolean1 = true;
						}

						break;
					
					case EventCloseWindow: 
						window = this.getWindow(player);
						if (window != null) {
							player.closeWindow(window);
							boolean1 = true;
						}

						break;
					
					case EventSmashWindow: 
						window = this.getWindow(player);
						if (window != null) {
							player.smashWindow(window);
							boolean1 = true;
						}

						break;
					
					case wasBumped: 
						player.setBumpDone(false);
						player.setVariable("BumpFallAnimFinished", false);
						player.setBumpType(this.type1);
						player.setBumpFallType(this.type2);
						player.setBumpFall(this.param1 > 0.0F);
						player.setBumpStaggered(this.param2 > 0.0F);
						player.reportEvent("wasBumped");
						return true;
					
					default: 
						DebugLog.log(DebugType.Multiplayer, "Remote player: unprocessed event type");
					
					}
				}
			}

			return boolean1;
		}

		private boolean requireNonMoving(byte byte1) {
			if (byte1 != -1 && byte1 != -2) {
				EventUpdatePacket.EventUpdate eventUpdate = EventUpdatePacket.EventUpdate.values()[byte1];
				return eventUpdate == EventUpdatePacket.EventUpdate.EventClimbWindow || eventUpdate == EventUpdatePacket.EventUpdate.EventClimbFence || eventUpdate == EventUpdatePacket.EventUpdate.EventClimbDownRope || eventUpdate == EventUpdatePacket.EventUpdate.EventClimbRope || eventUpdate == EventUpdatePacket.EventUpdate.EventClimbWall;
			} else {
				return false;
			}
		}
	}
}
