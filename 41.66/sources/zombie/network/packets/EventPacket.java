package zombie.network.packets;

import java.nio.ByteBuffer;
import java.util.HashMap;
import zombie.GameWindow;
import zombie.ai.states.ClimbDownSheetRopeState;
import zombie.ai.states.ClimbSheetRopeState;
import zombie.ai.states.FishingState;
import zombie.ai.states.SmashWindowState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.characters.NetworkPlayerVariables;
import zombie.characters.CharacterTimedActions.BaseAction;
import zombie.core.Core;
import zombie.core.network.ByteBufferWriter;
import zombie.core.raknet.UdpConnection;
import zombie.debug.DebugLog;
import zombie.debug.DebugType;
import zombie.inventory.InventoryItem;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoWindow;
import zombie.iso.objects.IsoWindowFrame;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.util.StringUtils;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.VehicleManager;
import zombie.vehicles.VehiclePart;
import zombie.vehicles.VehicleWindow;


public class EventPacket implements INetworkPacket {
	public static final int MAX_PLAYER_EVENTS = 10;
	private static final long EVENT_TIMEOUT = 5000L;
	private static final short EVENT_FLAGS_VAULT_OVER_SPRINT = 1;
	private static final short EVENT_FLAGS_VAULT_OVER_RUN = 2;
	private static final short EVENT_FLAGS_BUMP_FALL = 4;
	private static final short EVENT_FLAGS_BUMP_STAGGERED = 8;
	private static final short EVENT_FLAGS_ACTIVATE_ITEM = 16;
	private static final short EVENT_FLAGS_CLIMB_SUCCESS = 32;
	private static final short EVENT_FLAGS_CLIMB_STRUGGLE = 64;
	private static final short EVENT_FLAGS_BUMP_FROM_BEHIND = 128;
	private static final short EVENT_FLAGS_BUMP_TARGET_TYPE = 256;
	private static final short EVENT_FLAGS_PRESSED_MOVEMENT = 512;
	private static final short EVENT_FLAGS_PRESSED_CANCEL_ACTION = 1024;
	private static final short EVENT_FLAGS_SMASH_CAR_WINDOW = 2048;
	private static final short EVENT_FLAGS_FITNESS_FINISHED = 4096;
	private short id;
	public float x;
	public float y;
	public float z;
	private byte eventID;
	private String type1;
	private String type2;
	private String type3;
	private String type4;
	private float strafeSpeed;
	private float walkSpeed;
	private float walkInjury;
	private int booleanVariables;
	private short flags;
	private IsoPlayer player;
	private EventPacket.EventType event;
	private long timestamp;

	public String getDescription() {
		short short1 = this.id;
		return "[ player=" + short1 + " \"" + (this.player == null ? "?" : this.player.getUsername()) + "\" | name=\"" + (this.event == null ? "?" : this.event.name()) + "\" | pos=( " + this.x + " ; " + this.y + " ; " + this.z + " ) | type1=\"" + this.type1 + "\" | type2=\"" + this.type2 + "\" | type3=\"" + this.type3 + "\" | type4=\"" + this.type4 + "\" | flags=" + this.flags + "\" | variables=" + this.booleanVariables + " ]";
	}

	public boolean isConsistent() {
		boolean boolean1 = this.player != null && this.event != null;
		if (!boolean1 && Core.bDebug) {
			DebugLog.log(DebugType.Multiplayer, "[Event] is not consistent " + this.getDescription());
		}

		return boolean1;
	}

	public void parse(ByteBuffer byteBuffer, UdpConnection udpConnection) {
		this.id = byteBuffer.getShort();
		this.x = byteBuffer.getFloat();
		this.y = byteBuffer.getFloat();
		this.z = byteBuffer.getFloat();
		this.eventID = byteBuffer.get();
		this.type1 = GameWindow.ReadString(byteBuffer);
		this.type2 = GameWindow.ReadString(byteBuffer);
		this.type3 = GameWindow.ReadString(byteBuffer);
		this.type4 = GameWindow.ReadString(byteBuffer);
		this.strafeSpeed = byteBuffer.getFloat();
		this.walkSpeed = byteBuffer.getFloat();
		this.walkInjury = byteBuffer.getFloat();
		this.booleanVariables = byteBuffer.getInt();
		this.flags = byteBuffer.getShort();
		if (this.eventID >= 0 && this.eventID < EventPacket.EventType.values().length) {
			this.event = EventPacket.EventType.values()[this.eventID];
		} else {
			DebugLog.Multiplayer.warn("Unknown event=" + this.eventID);
			this.event = null;
		}

		if (GameServer.bServer) {
			this.player = (IsoPlayer)GameServer.IDToPlayerMap.get(this.id);
		} else if (GameClient.bClient) {
			this.player = (IsoPlayer)GameClient.IDToPlayerMap.get(this.id);
		} else {
			this.player = null;
		}

		this.timestamp = System.currentTimeMillis() + 5000L;
	}

	public void write(ByteBufferWriter byteBufferWriter) {
		byteBufferWriter.putShort(this.id);
		byteBufferWriter.putFloat(this.x);
		byteBufferWriter.putFloat(this.y);
		byteBufferWriter.putFloat(this.z);
		byteBufferWriter.putByte(this.eventID);
		byteBufferWriter.putUTF(this.type1);
		byteBufferWriter.putUTF(this.type2);
		byteBufferWriter.putUTF(this.type3);
		byteBufferWriter.putUTF(this.type4);
		byteBufferWriter.putFloat(this.strafeSpeed);
		byteBufferWriter.putFloat(this.walkSpeed);
		byteBufferWriter.putFloat(this.walkInjury);
		byteBufferWriter.putInt(this.booleanVariables);
		byteBufferWriter.putShort(this.flags);
	}

	public boolean isRelevant(UdpConnection udpConnection) {
		return udpConnection.RelevantTo(this.x, this.y);
	}

	public boolean isMovableEvent() {
		if (!this.isConsistent()) {
			return false;
		} else {
			return EventPacket.EventType.EventClimbFence.equals(this.event) || EventPacket.EventType.EventFallClimb.equals(this.event);
		}
	}

	private boolean requireNonMoving() {
		return this.isConsistent() && (EventPacket.EventType.EventClimbWindow.equals(this.event) || EventPacket.EventType.EventClimbFence.equals(this.event) || EventPacket.EventType.EventClimbDownRope.equals(this.event) || EventPacket.EventType.EventClimbRope.equals(this.event) || EventPacket.EventType.EventClimbWall.equals(this.event));
	}

	private IsoWindow getWindow(IsoPlayer player) {
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

	private IsoObject getObject(IsoPlayer player) {
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
		return System.currentTimeMillis() > this.timestamp;
	}

	public void tryProcess() {
		if (this.isConsistent()) {
			if (this.player.networkAI.events.size() < 10) {
				this.player.networkAI.events.add(this);
			} else {
				DebugLog.Multiplayer.warn("Event skipped: " + this.getDescription());
			}
		}
	}

	public boolean process(IsoPlayer player) {
		boolean boolean1 = false;
		if (this.isConsistent()) {
			player.overridePrimaryHandModel = null;
			player.overrideSecondaryHandModel = null;
			if (player.getCurrentSquare() == player.getCell().getGridSquare((double)this.x, (double)this.y, (double)this.z) && !player.isPlayerMoving() || !this.requireNonMoving()) {
				IsoWindow window;
				switch (this.event) {
				case EventSetActivatedPrimary: 
					if (player.getPrimaryHandItem() != null && player.getPrimaryHandItem().canEmitLight()) {
						player.getPrimaryHandItem().setActivatedRemote((this.flags & 16) != 0);
						boolean1 = true;
					}

					break;
				
				case EventSetActivatedSecondary: 
					if (player.getSecondaryHandItem() != null && player.getSecondaryHandItem().canEmitLight()) {
						player.getSecondaryHandItem().setActivatedRemote((this.flags & 16) != 0);
						boolean1 = true;
					}

					break;
				
				case EventFallClimb: 
					player.setVariable("ClimbFenceOutcome", "fall");
					player.setVariable("BumpDone", true);
					player.setFallOnFront(true);
					boolean1 = true;
					break;
				
				case collideWithWall: 
					player.setCollideType(this.type1);
					player.actionContext.reportEvent("collideWithWall");
					boolean1 = true;
					break;
				
				case EventFishing: 
					player.setVariable("FishingStage", this.type1);
					if (!FishingState.instance().equals(player.getCurrentState())) {
						player.setVariable("forceGetUp", true);
						player.actionContext.reportEvent("EventFishing");
					}

					boolean1 = true;
					break;
				
				case EventFitness: 
					player.setVariable("ExerciseType", this.type1);
					player.setVariable("FitnessFinished", false);
					player.actionContext.reportEvent("EventFitness");
					boolean1 = true;
					break;
				
				case EventUpdateFitness: 
					player.clearVariable("ExerciseHand");
					player.setVariable("ExerciseType", this.type2);
					if (!StringUtils.isNullOrEmpty(this.type1)) {
						player.setVariable("ExerciseHand", this.type1);
					}

					player.setFitnessSpeed();
					if ((this.flags & 4096) != 0) {
						player.setVariable("ExerciseStarted", false);
						player.setVariable("ExerciseEnded", true);
					}

					player.setPrimaryHandItem((InventoryItem)null);
					player.setSecondaryHandItem((InventoryItem)null);
					player.overridePrimaryHandModel = null;
					player.overrideSecondaryHandModel = null;
					player.overridePrimaryHandModel = this.type3;
					player.overrideSecondaryHandModel = this.type4;
					player.resetModelNextFrame();
					boolean1 = true;
					break;
				
				case EventEmote: 
					player.setVariable("emote", this.type1);
					player.actionContext.reportEvent("EventEmote");
					boolean1 = true;
					break;
				
				case EventSitOnGround: 
					player.actionContext.reportEvent("EventSitOnGround");
					boolean1 = true;
					break;
				
				case EventClimbRope: 
					player.climbSheetRope();
					boolean1 = true;
					break;
				
				case EventClimbDownRope: 
					player.climbDownSheetRope();
					boolean1 = true;
					break;
				
				case EventClimbFence: 
					IsoDirections directions = this.checkCurrentIsEventGridSquareFence(player);
					if (directions != IsoDirections.Max) {
						player.climbOverFence(directions);
						if (player.isSprinting()) {
							player.setVariable("VaultOverSprint", true);
						}

						if (player.isRunning()) {
							player.setVariable("VaultOverRun", true);
						}

						boolean1 = true;
					}

					break;
				
				case EventClimbWall: 
					player.setClimbOverWallStruggle((this.flags & 64) != 0);
					player.setClimbOverWallSuccess((this.flags & 32) != 0);
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
						boolean1 = true;
					} else if (object instanceof IsoThumpable) {
						player.climbThroughWindow((IsoThumpable)object);
						boolean1 = true;
					}

					if (IsoWindowFrame.isWindowFrame(object)) {
						player.climbThroughWindowFrame(object);
						boolean1 = true;
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
					if ((this.flags & 2048) != 0) {
						BaseVehicle baseVehicle = VehicleManager.instance.getVehicleByID(Short.parseShort(this.type1));
						if (baseVehicle != null) {
							VehiclePart vehiclePart = baseVehicle.getPartById(this.type2);
							if (vehiclePart != null) {
								VehicleWindow vehicleWindow = vehiclePart.getWindow();
								if (vehicleWindow != null) {
									player.smashCarWindow(vehiclePart);
									boolean1 = true;
								}
							}
						}
					} else {
						window = this.getWindow(player);
						if (window != null) {
							player.smashWindow(window);
							boolean1 = true;
						}
					}

					break;
				
				case wasBumped: 
					player.setBumpDone(false);
					player.setVariable("BumpFallAnimFinished", false);
					player.setBumpType(this.type1);
					player.setBumpFallType(this.type2);
					player.setBumpFall((this.flags & 4) != 0);
					player.setBumpStaggered((this.flags & 8) != 0);
					player.reportEvent("wasBumped");
					if (!StringUtils.isNullOrEmpty(this.type3) && !StringUtils.isNullOrEmpty(this.type4)) {
						IsoGameCharacter gameCharacter = null;
						if ((this.flags & 256) != 0) {
							gameCharacter = (IsoGameCharacter)GameClient.IDToZombieMap.get(Short.parseShort(this.type3));
						} else {
							gameCharacter = (IsoGameCharacter)GameClient.IDToPlayerMap.get(Short.parseShort(this.type3));
						}

						if (gameCharacter != null) {
							gameCharacter.setBumpType(this.type4);
							gameCharacter.setHitFromBehind((this.flags & 128) != 0);
						}
					}

					boolean1 = true;
					break;
				
				case EventOverrideItem: 
					if (player.getNetworkCharacterAI().getAction() != null) {
						player.getNetworkCharacterAI().setOverride(true, this.type1, this.type2);
					}

					boolean1 = true;
					break;
				
				case ChargeSpearConnect: 
					boolean1 = true;
					break;
				
				case Update: 
					player.networkAI.setPressedMovement((this.flags & 512) != 0);
					player.networkAI.setPressedCancelAction((this.flags & 1024) != 0);
					boolean1 = true;
					break;
				
				case Unknown: 
				
				default: 
					DebugLog.Multiplayer.warn("[Event] unknown: " + this.getDescription());
					boolean1 = true;
				
				}
			}
		}

		return boolean1;
	}

	public boolean set(IsoPlayer player, String string) {
		boolean boolean1 = false;
		this.player = player;
		this.id = player.getOnlineID();
		this.x = player.getX();
		this.y = player.getY();
		this.z = player.getZ();
		this.type1 = null;
		this.type2 = null;
		this.type3 = null;
		this.type4 = null;
		this.booleanVariables = NetworkPlayerVariables.getBooleanVariables(player);
		this.strafeSpeed = player.getVariableFloat("StrafeSpeed", 1.0F);
		this.walkSpeed = player.getVariableFloat("WalkSpeed", 1.0F);
		this.walkInjury = player.getVariableFloat("WalkInjury", 0.0F);
		this.flags = 0;
		EventPacket.EventType[] eventTypeArray = EventPacket.EventType.values();
		int int1 = eventTypeArray.length;
		for (int int2 = 0; int2 < int1; ++int2) {
			EventPacket.EventType eventType = eventTypeArray[int2];
			if (eventType.name().equals(string)) {
				this.event = eventType;
				this.eventID = (byte)eventType.ordinal();
				switch (eventType) {
				case EventSetActivatedPrimary: 
					this.flags = (short)(this.flags | (player.getPrimaryHandItem().isActivated() ? 16 : 0));
					break;
				
				case EventSetActivatedSecondary: 
					this.flags = (short)(this.flags | (player.getSecondaryHandItem().isActivated() ? 16 : 0));
				
				case EventFallClimb: 
				
				case EventSitOnGround: 
				
				case EventClimbRope: 
				
				case EventClimbDownRope: 
				
				case EventClimbWindow: 
				
				case EventOpenWindow: 
				
				case EventCloseWindow: 
				
				case ChargeSpearConnect: 
					break;
				
				case collideWithWall: 
					this.type1 = player.getCollideType();
					break;
				
				case EventFishing: 
					this.type1 = player.getVariableString("FishingStage");
					break;
				
				case EventFitness: 
					this.type1 = player.getVariableString("ExerciseType");
					break;
				
				case EventUpdateFitness: 
					this.type1 = player.getVariableString("ExerciseHand");
					this.type2 = player.getVariableString("ExerciseType");
					if (player.getPrimaryHandItem() != null) {
						this.type3 = player.getPrimaryHandItem().getStaticModel();
					}

					if (player.getSecondaryHandItem() != null && player.getSecondaryHandItem() != player.getPrimaryHandItem()) {
						this.type4 = player.getSecondaryHandItem().getStaticModel();
					}

					this.flags = (short)(this.flags | (player.getVariableBoolean("FitnessFinished") ? 4096 : 0));
					break;
				
				case EventEmote: 
					this.type1 = player.getVariableString("emote");
					break;
				
				case EventClimbFence: 
					if (player.getVariableBoolean("VaultOverRun")) {
						this.flags = (short)(this.flags | 2);
					}

					if (player.getVariableBoolean("VaultOverSprint")) {
						this.flags = (short)(this.flags | 1);
					}

					break;
				
				case EventClimbWall: 
					this.flags = (short)(this.flags | (player.isClimbOverWallSuccess() ? 32 : 0));
					this.flags = (short)(this.flags | (player.isClimbOverWallStruggle() ? 64 : 0));
					break;
				
				case EventSmashWindow: 
					HashMap hashMap = player.getStateMachineParams(SmashWindowState.instance());
					if (hashMap.get(1) instanceof BaseVehicle && hashMap.get(2) instanceof VehiclePart) {
						BaseVehicle baseVehicle = (BaseVehicle)hashMap.get(1);
						VehiclePart vehiclePart = (VehiclePart)hashMap.get(2);
						this.flags = (short)(this.flags | 2048);
						this.type1 = String.valueOf(baseVehicle.getId());
						this.type2 = vehiclePart.getId();
					}

					break;
				
				case wasBumped: 
					this.type1 = player.getBumpType();
					this.type2 = player.getBumpFallType();
					this.flags = (short)(this.flags | (player.isBumpFall() ? 4 : 0));
					this.flags = (short)(this.flags | (player.isBumpStaggered() ? 8 : 0));
					if (player.getBumpedChr() != null) {
						this.type3 = String.valueOf(player.getBumpedChr().getOnlineID());
						this.type4 = player.getBumpedChr().getBumpType();
						this.flags = (short)(this.flags | (player.isHitFromBehind() ? 128 : 0));
						if (player.getBumpedChr() instanceof IsoZombie) {
							this.flags = (short)(this.flags | 256);
						}
					}

					break;
				
				case EventOverrideItem: 
					if (player.getNetworkCharacterAI().getAction() == null) {
						return false;
					}

					BaseAction baseAction = player.getNetworkCharacterAI().getAction();
					this.type1 = baseAction.getPrimaryHandItem() == null ? baseAction.getPrimaryHandMdl() : baseAction.getPrimaryHandItem().getStaticModel();
					this.type2 = baseAction.getSecondaryHandItem() == null ? baseAction.getSecondaryHandMdl() : baseAction.getSecondaryHandItem().getStaticModel();
					break;
				
				case Update: 
					this.flags = (short)(this.flags | (player.networkAI.isPressedMovement() ? 512 : 0));
					this.flags = (short)(this.flags | (player.networkAI.isPressedCancelAction() ? 1024 : 0));
					break;
				
				default: 
					DebugLog.Multiplayer.warn("[Event] unknown " + this.getDescription());
					return false;
				
				}

				boolean1 = !ClimbDownSheetRopeState.instance().equals(player.getCurrentState()) && !ClimbSheetRopeState.instance().equals(player.getCurrentState());
			}
		}

		return boolean1;
	}

	public static enum EventType {

		EventSetActivatedPrimary,
		EventSetActivatedSecondary,
		EventFishing,
		EventFitness,
		EventEmote,
		EventClimbFence,
		EventClimbDownRope,
		EventClimbRope,
		EventClimbWall,
		EventClimbWindow,
		EventOpenWindow,
		EventCloseWindow,
		EventSmashWindow,
		EventSitOnGround,
		wasBumped,
		collideWithWall,
		EventUpdateFitness,
		EventFallClimb,
		EventOverrideItem,
		ChargeSpearConnect,
		Update,
		Unknown;

		private static EventPacket.EventType[] $values() {
			return new EventPacket.EventType[]{EventSetActivatedPrimary, EventSetActivatedSecondary, EventFishing, EventFitness, EventEmote, EventClimbFence, EventClimbDownRope, EventClimbRope, EventClimbWall, EventClimbWindow, EventOpenWindow, EventCloseWindow, EventSmashWindow, EventSitOnGround, wasBumped, collideWithWall, EventUpdateFitness, EventFallClimb, EventOverrideItem, ChargeSpearConnect, Update, Unknown};
		}
	}
}
