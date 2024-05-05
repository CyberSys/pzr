package zombie.characters;

import java.util.ArrayList;
import java.util.LinkedList;
import zombie.GameTime;
import zombie.SystemDisabler;
import zombie.ai.states.CollideWithWallState;
import zombie.core.Rand;
import zombie.core.math.PZMath;
import zombie.core.utils.UpdateTimer;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.input.GameKeyboard;
import zombie.iso.IsoDirections;
import zombie.iso.IsoMovingObject;
import zombie.iso.Vector2;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.NetworkVariables;
import zombie.network.packets.PlayerPacket;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;
import zombie.vehicles.VehicleManager;


public class NetworkPlayerAI extends NetworkCharacterAI {
	public final LinkedList events = new LinkedList();
	IsoPlayer player;
	private PathFindBehavior2 pfb2 = null;
	private final UpdateTimer timer = new UpdateTimer();
	private byte lastDirection = 0;
	private boolean needUpdate = false;
	private boolean blockUpdate = false;
	public boolean usePathFind = false;
	public float collidePointX;
	public float collidePointY;
	public float targetX = 0.0F;
	public float targetY = 0.0F;
	public int targetZ = 0;
	public boolean needToMovingUsingPathFinder = false;
	public boolean forcePathFinder = false;
	public Vector2 direction = new Vector2();
	public Vector2 distance = new Vector2();
	public boolean moving = false;
	public byte footstepSoundRadius = 0;
	public int lastBooleanVariables = 0;
	public float lastForwardDirection = 0.0F;
	public float lastPlayerMoveDirLen = 0.0F;
	private boolean pressedMovement = false;
	private boolean pressedCancelAction = false;
	public boolean climbFenceOutcomeFall = false;
	private long accessLevelTimestamp = 0L;
	boolean wasNonPvpZone = false;
	private Vector2 tempo = new Vector2();
	private static final int predictInterval = 1000;

	public NetworkPlayerAI(IsoGameCharacter gameCharacter) {
		super(gameCharacter);
		this.player = (IsoPlayer)gameCharacter;
		this.pfb2 = this.player.getPathFindBehavior2();
		gameCharacter.ulBeatenVehicle.Reset(200L);
		this.collidePointX = -1.0F;
		this.collidePointY = -1.0F;
		this.wasNonPvpZone = false;
	}

	public void needToUpdate() {
		this.needUpdate = true;
	}

	public void setBlockUpdate(boolean boolean1) {
		this.blockUpdate = boolean1;
	}

	public boolean isNeedToUpdate() {
		int int1 = NetworkPlayerVariables.getBooleanVariables(this.player);
		byte byte1 = (byte)((int)(this.player.playerMoveDir.getDirection() * 10.0F));
		if ((!this.timer.check() && int1 == this.lastBooleanVariables && this.lastDirection == byte1 || this.blockUpdate) && !this.needUpdate) {
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

	private void setUsingCollide(PlayerPacket playerPacket, int int1) {
		if (SystemDisabler.useNetworkCharacter) {
			this.player.networkCharacter.checkResetPlayer(int1);
		}

		playerPacket.x = (float)this.player.getCurrentSquare().getX();
		playerPacket.y = (float)this.player.getCurrentSquare().getY();
		playerPacket.z = (byte)this.player.getCurrentSquare().getZ();
		playerPacket.usePathFinder = false;
		playerPacket.moveType = NetworkVariables.PredictionTypes.Thump;
	}

	private void setUsingExtrapolation(PlayerPacket playerPacket, int int1, int int2) {
		Vector2 vector2 = this.player.dir.ToVector();
		if (SystemDisabler.useNetworkCharacter) {
			this.player.networkCharacter.checkResetPlayer(int1);
		}

		if (!this.player.isPlayerMoving()) {
			playerPacket.x = this.player.x;
			playerPacket.y = this.player.y;
			playerPacket.z = (byte)((int)this.player.z);
			playerPacket.usePathFinder = false;
			playerPacket.moveType = NetworkVariables.PredictionTypes.Static;
		} else {
			Vector2 vector22 = this.tempo;
			if (SystemDisabler.useNetworkCharacter) {
				NetworkCharacter.Transform transform = this.player.networkCharacter.predict(int2, int1, this.player.x, this.player.y, vector2.x, vector2.y);
				vector22.x = transform.position.x;
				vector22.y = transform.position.y;
			} else {
				this.player.getDeferredMovement(vector22);
				vector22.x = this.player.x + vector22.x * 0.03F * (float)int2;
				vector22.y = this.player.y + vector22.y * 0.03F * (float)int2;
			}

			if (this.player.z == this.pfb2.getTargetZ() && !PolygonalMap2.instance.lineClearCollide(this.player.x, this.player.y, vector22.x, vector22.y, (int)this.player.z, (IsoMovingObject)null)) {
				playerPacket.x = vector22.x;
				playerPacket.y = vector22.y;
				playerPacket.z = (byte)((int)this.pfb2.getTargetZ());
			} else {
				Vector2 vector23 = PolygonalMap2.instance.getCollidepoint(this.player.x, this.player.y, vector22.x, vector22.y, (int)this.player.z, (IsoMovingObject)null, 2);
				playerPacket.collidePointX = vector23.x;
				playerPacket.collidePointY = vector23.y;
				playerPacket.x = vector23.x + (this.player.dir != IsoDirections.N && this.player.dir != IsoDirections.S ? (this.player.dir.index() >= IsoDirections.NW.index() && this.player.dir.index() <= IsoDirections.SW.index() ? -1.0F : 1.0F) : 0.0F);
				playerPacket.y = vector23.y + (this.player.dir != IsoDirections.W && this.player.dir != IsoDirections.E ? (this.player.dir.index() >= IsoDirections.SW.index() && this.player.dir.index() <= IsoDirections.SE.index() ? 1.0F : -1.0F) : 0.0F);
				playerPacket.z = (byte)((int)this.player.z);
			}

			playerPacket.usePathFinder = false;
			playerPacket.moveType = NetworkVariables.PredictionTypes.Moving;
		}
	}

	private void setUsingPathFindState(PlayerPacket playerPacket, int int1) {
		if (SystemDisabler.useNetworkCharacter) {
			this.player.networkCharacter.checkResetPlayer(int1);
		}

		playerPacket.x = this.pfb2.pathNextX;
		playerPacket.y = this.pfb2.pathNextY;
		playerPacket.z = (byte)((int)this.player.z);
		playerPacket.usePathFinder = true;
		playerPacket.moveType = NetworkVariables.PredictionTypes.PathFind;
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
		playerPacket.collidePointX = -1.0F;
		playerPacket.collidePointY = -1.0F;
		if (boolean1) {
			this.setUpdateTimer(600.0F);
		}

		if (this.player.getCurrentState() == CollideWithWallState.instance()) {
			this.setUsingCollide(playerPacket, int1);
		} else if (this.pfb2.isMovingUsingPathFind()) {
			this.setUsingPathFindState(playerPacket, int1);
		} else {
			this.setUsingExtrapolation(playerPacket, int1, 1000);
		}

		boolean boolean2 = (double)this.player.playerMoveDir.getLength() < 0.01 && this.lastPlayerMoveDirLen > 0.01F;
		this.lastPlayerMoveDirLen = this.player.playerMoveDir.getLength();
		playerPacket.booleanVariables = NetworkPlayerVariables.getBooleanVariables(this.player);
		boolean boolean3 = this.lastBooleanVariables != playerPacket.booleanVariables;
		this.lastBooleanVariables = playerPacket.booleanVariables;
		playerPacket.direction = this.player.getForwardDirection().getDirection();
		boolean boolean4 = Math.abs(this.lastForwardDirection - playerPacket.direction) > 0.2F;
		this.lastForwardDirection = playerPacket.direction;
		playerPacket.footstepSoundRadius = this.footstepSoundRadius;
		return boolean1 || boolean3 || boolean4 || this.player.isJustMoved() || boolean2;
	}

	public void parse(PlayerPacket playerPacket) {
		if (!this.player.isTeleporting()) {
			this.targetX = PZMath.roundFromEdges(playerPacket.x);
			this.targetY = PZMath.roundFromEdges(playerPacket.y);
			this.targetZ = playerPacket.z;
			this.predictionType = playerPacket.moveType;
			this.needToMovingUsingPathFinder = playerPacket.usePathFinder;
			this.direction.set((float)Math.cos((double)playerPacket.direction), (float)Math.sin((double)playerPacket.direction));
			this.distance.set(playerPacket.x - this.player.x, playerPacket.y - this.player.y);
			if (this.usePathFind) {
				this.pfb2.pathToLocationF(playerPacket.x, playerPacket.y, (float)playerPacket.z);
				this.pfb2.walkingOnTheSpot.reset(this.player.x, this.player.y);
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
			if (GameServer.bServer) {
				this.player.setForwardDirection(this.direction);
			}

			this.collidePointX = playerPacket.collidePointX;
			this.collidePointY = playerPacket.collidePointY;
			playerPacket.variables.apply(this.player);
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
							baseVehicle.switchSeat(this.player, playerPacket.VehicleSeat);
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

			this.setPressedMovement(false);
			this.setPressedCancelAction(false);
		}
	}

	public boolean isPressedMovement() {
		return this.pressedMovement;
	}

	public void setPressedMovement(boolean boolean1) {
		boolean boolean2 = !this.pressedMovement && boolean1;
		this.pressedMovement = boolean1;
		if (this.player.isLocal() && boolean2) {
			GameClient.sendEvent(this.player, "Update");
		}
	}

	public boolean isPressedCancelAction() {
		return this.pressedCancelAction;
	}

	public void setPressedCancelAction(boolean boolean1) {
		boolean boolean2 = !this.pressedCancelAction && boolean1;
		this.pressedCancelAction = boolean1;
		if (this.player.isLocal() && boolean2) {
			GameClient.sendEvent(this.player, "Update");
		}
	}

	public void setCheckAccessLevelDelay(long long1) {
		this.accessLevelTimestamp = System.currentTimeMillis() + long1;
	}

	public boolean doCheckAccessLevel() {
		if (this.accessLevelTimestamp == 0L) {
			return true;
		} else if (System.currentTimeMillis() > this.accessLevelTimestamp) {
			this.accessLevelTimestamp = 0L;
			return true;
		} else {
			return false;
		}
	}

	public void update() {
		if (DebugOptions.instance.MultiplayerHotKey.getValue() && GameKeyboard.isKeyPressed(45) && GameKeyboard.isKeyDown(56)) {
			DebugLog.Multiplayer.noise("multiplayer hot key pressed");
			ArrayList arrayList = ScriptManager.instance.getAllRecipes();
			Recipe recipe = (Recipe)arrayList.get(Rand.Next(arrayList.size()));
			recipe.TimeToMake = (float)Rand.Next(32767);
			DebugLog.Multiplayer.debugln("Failed recipe \"%s\"", recipe.getOriginalname());
		}
	}

	public boolean isDismantleAllowed() {
		return true;
	}
}
