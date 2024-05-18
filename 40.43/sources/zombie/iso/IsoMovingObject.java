package zombie.iso;

import fmod.fmod.Audio;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Stack;
import zombie.CollisionManager;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.State;
import zombie.ai.astar.Mover;
import zombie.ai.states.AttackState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbOverFenceState2;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.ClimbThroughWindowState2;
import zombie.ai.states.FakeDeadZombieState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.StaggerBackDieState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieStandState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.core.Rand;
import zombie.core.bucket.BucketManager;
import zombie.inventory.types.HandWeapon;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.isoregion.MasterRegion;
import zombie.iso.objects.IsoDoor;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.popman.ZombiePopulationManager;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.ScriptCharacter;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;


public class IsoMovingObject extends IsoObject implements Mover {
	public static final long ExpectedChecksum = 270439210007L;
	private static int IDCount = 0;
	private boolean closeKilled = false;
	private float bspeed = 0.0F;
	public float bx;
	public float by;
	float bz;
	private int TimeSinceZombieAttack = 1000000;
	public boolean noDamage = false;
	private boolean collidedE = false;
	private boolean collidedN = false;
	private IsoObject CollidedObject = null;
	private boolean collidedS = false;
	private boolean collidedThisFrame = false;
	private boolean collidedW = false;
	private boolean CollidedWithDoor = false;
	private boolean collidedWithVehicle = false;
	protected IsoGridSquare current = null;
	private boolean destroyed = false;
	private boolean firstUpdate = true;
	protected Vector2 hitDir = new Vector2();
	private boolean AllowBehaviours = true;
	private float impulsex = 0.0F;
	private float impulsey = 0.0F;
	private float limpulsex = 0.0F;
	private float limpulsey = 0.0F;
	private float hitForce = 0.0F;
	private float hitFromAngle;
	protected int ID = 0;
	public IsoGridSquare last = null;
	protected IsoGridSquare movingSq = null;
	public float lx;
	public float ly;
	public float lz;
	public float nx;
	public float ny;
	private int PathFindIndex = -1;
	protected boolean solid = true;
	private float StateEventDelayTimer = 0.0F;
	private Thumpable thumpTarget = null;
	protected float width = 0.24F;
	public float x;
	public float y;
	public float z;
	private boolean bAltCollide = false;
	protected boolean shootable = true;
	private IsoZombie lastTargettedBy = null;
	protected boolean Collidable = true;
	protected float scriptnx = 0.0F;
	protected float scriptny = 0.0F;
	public Vector2 reqMovement = new Vector2();
	protected String ScriptModule = "none";
	protected String ScriptName = "none";
	private Stack ActiveInInstances = new Stack();
	public static IsoMovingObject.TreeSoundManager treeSoundMgr = new IsoMovingObject.TreeSoundManager();
	public IsoSpriteInstance def = null;
	protected Vector2 movementLastFrame = new Vector2();
	protected float weight = 1.0F;
	private static Vector2 tempo = new Vector2();
	private float feelersize = 0.5F;
	boolean bOnFloor = false;

	public static int getIDCount() {
		return IDCount;
	}

	public IsoBuilding getBuilding() {
		if (this.current == null) {
			return null;
		} else {
			IsoRoom room = this.current.getRoom();
			return room == null ? null : room.building;
		}
	}

	public MasterRegion getMasterRegion() {
		return this.current != null ? this.current.getMasterRegion() : null;
	}

	public static void setIDCount(int int1) {
		IDCount = int1;
	}

	public static Vector2 getTempo() {
		return tempo;
	}

	public static void setTempo(Vector2 vector2) {
		tempo = vector2;
	}

	public float getWeight() {
		return this.weight;
	}

	public float getWeight(float float1, float float2) {
		return this.weight;
	}

	public void onMouseRightClick(int int1, int int2) {
		if (this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && this.DistToProper(IsoPlayer.getInstance()) <= 2.0F) {
			IsoPlayer.instance.setDragObject(this);
		}
	}

	public String getObjectName() {
		return "IsoMovingObject";
	}

	public void onMouseRightReleased() {
	}

	public IsoMovingObject(IsoCell cell) {
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		if (cell != null) {
			this.ID = IDCount++;
			if (this.getCell().isSafeToAdd()) {
				this.getCell().getObjectList().add(this);
			} else {
				this.getCell().getAddList().add(this);
			}
		}
	}

	public IsoMovingObject(IsoCell cell, boolean boolean1) {
		this.ID = IDCount++;
		this.sprite = IsoSprite.CreateSprite(BucketManager.Shared().SpriteManager);
		if (boolean1) {
			if (this.getCell().isSafeToAdd()) {
				this.getCell().getObjectList().add(this);
			} else {
				this.getCell().getAddList().add(this);
			}
		}
	}

	public IsoMovingObject(IsoCell cell, IsoGridSquare square, IsoSprite sprite, boolean boolean1) {
		this.ID = IDCount++;
		this.sprite = sprite;
		if (boolean1) {
			if (this.getCell().isSafeToAdd()) {
				this.getCell().getObjectList().add(this);
			} else {
				this.getCell().getAddList().add(this);
			}
		}
	}

	public IsoMovingObject() {
		this.ID = IDCount++;
		this.getCell().getAddList().add(this);
	}

	public void collideCharacter() {
	}

	public void collideWith(IsoObject object) {
		if (this instanceof IsoGameCharacter && object instanceof IsoGameCharacter) {
			LuaEventManager.triggerEvent("OnCharacterCollide", (IsoGameCharacter)this, (IsoGameCharacter)object);
		} else {
			LuaEventManager.triggerEvent("OnObjectCollide", this, object);
		}
	}

	public void doStairs() {
		if (this.current != null) {
			if (this.last != null) {
				if (!(this instanceof IsoPhysicsObject)) {
					IsoGridSquare square = this.current;
					if (square.z > 0 && (square.Has(IsoObjectType.stairsTN) || square.Has(IsoObjectType.stairsTW)) && this.z - (float)((int)this.z) < 0.1F) {
						IsoGridSquare square2 = IsoWorld.instance.CurrentCell.getGridSquare(square.x, square.y, square.z - 1);
						if (square2 != null && (square2.Has(IsoObjectType.stairsTN) || square2.Has(IsoObjectType.stairsTW))) {
							square = square2;
						}
					}

					if (this instanceof IsoGameCharacter && (this.last.Has(IsoObjectType.stairsTN) || this.last.Has(IsoObjectType.stairsTW))) {
						this.z = (float)Math.round(this.z);
					}

					float float1 = this.z;
					if (square.Has(IsoObjectType.stairsTN)) {
						float1 = (float)square.getZ() + GameTime.getInstance().Lerp(0.6666F, 1.0F, 1.0F - (this.y - (float)square.getY()));
					} else if (square.Has(IsoObjectType.stairsTW)) {
						float1 = (float)square.getZ() + GameTime.getInstance().Lerp(0.6666F, 1.0F, 1.0F - (this.x - (float)square.getX()));
					} else if (square.Has(IsoObjectType.stairsMN)) {
						float1 = (float)square.getZ() + GameTime.getInstance().Lerp(0.3333F, 0.6666F, 1.0F - (this.y - (float)square.getY()));
					} else if (square.Has(IsoObjectType.stairsMW)) {
						float1 = (float)square.getZ() + GameTime.getInstance().Lerp(0.3333F, 0.6666F, 1.0F - (this.x - (float)square.getX()));
					} else if (square.Has(IsoObjectType.stairsBN)) {
						float1 = (float)square.getZ() + GameTime.getInstance().Lerp(0.01F, 0.3333F, 1.0F - (this.y - (float)square.getY()));
					} else if (square.Has(IsoObjectType.stairsBW)) {
						float1 = (float)square.getZ() + GameTime.getInstance().Lerp(0.01F, 0.3333F, 1.0F - (this.x - (float)square.getX()));
					}

					if (this instanceof IsoGameCharacter) {
						State state = ((IsoGameCharacter)this).getCurrentState();
						if (state == ClimbOverFenceState.instance() || state == ClimbThroughWindowState.instance()) {
							return;
						}

						if (state == ClimbOverFenceState2.instance() || state == ClimbThroughWindowState2.instance()) {
							if (square.HasStairs() && this.z > float1) {
								this.z = Math.max(float1, this.z - 0.075F * GameTime.getInstance().getMultiplier());
							}

							return;
						}
					}

					if (Math.abs(float1 - this.z) < 0.95F) {
						this.z = float1;
					}
				}
			}
		}
	}

	public int getID() {
		return this.ID;
	}

	public int getPathFindIndex() {
		return this.PathFindIndex;
	}

	public float getScreenX() {
		return IsoUtils.XToScreen(this.x, this.y, this.z, 0);
	}

	public float getScreenY() {
		return IsoUtils.YToScreen(this.x, this.y, this.z, 0);
	}

	public Thumpable getThumpTarget() {
		return this.thumpTarget;
	}

	public Vector2 getVectorFromDirection(Vector2 vector2) {
		vector2.x = 0.0F;
		vector2.y = 0.0F;
		switch (this.dir) {
		case S: 
			vector2.x = 0.0F;
			vector2.y = 1.0F;
			break;
		
		case N: 
			vector2.x = 0.0F;
			vector2.y = -1.0F;
			break;
		
		case E: 
			vector2.x = 1.0F;
			vector2.y = 0.0F;
			break;
		
		case W: 
			vector2.x = -1.0F;
			vector2.y = 0.0F;
			break;
		
		case NW: 
			vector2.x = -1.0F;
			vector2.y = -1.0F;
			break;
		
		case NE: 
			vector2.x = 1.0F;
			vector2.y = -1.0F;
			break;
		
		case SW: 
			vector2.x = -1.0F;
			vector2.y = 1.0F;
			break;
		
		case SE: 
			vector2.x = 1.0F;
			vector2.y = 1.0F;
		
		}
		vector2.normalize();
		return vector2;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public IsoGridSquare getSquare() {
		return this.current != null ? this.current : this.square;
	}

	public IsoBuilding getCurrentBuilding() {
		if (this.current == null) {
			return null;
		} else {
			return this.current.getRoom() == null ? null : this.current.getRoom().building;
		}
	}

	public void Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2) {
	}

	public void Move(Vector2 vector2) {
		this.nx += vector2.x * GameTime.instance.getMultiplier();
		this.ny += vector2.y * GameTime.instance.getMultiplier();
		this.reqMovement.x = vector2.x;
		this.reqMovement.y = vector2.y;
		if (this instanceof IsoPlayer) {
			if (vector2.x != 0.0F || vector2.y != 0.0F) {
				boolean boolean1 = false;
			}

			IsoGridSquare square = this.current;
			this.current = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)((int)this.z));
		}
	}

	public boolean isCharacter() {
		return this instanceof IsoGameCharacter;
	}

	public float DistTo(int int1, int int2) {
		return IsoUtils.DistanceManhatten((float)int1, (float)int2, this.x, this.y);
	}

	public float DistTo(IsoMovingObject movingObject) {
		return IsoUtils.DistanceManhatten(this.x, this.y, movingObject.x, movingObject.y);
	}

	public float DistToProper(IsoObject object) {
		return IsoUtils.DistanceTo(this.x, this.y, object.getX(), object.getY());
	}

	public float DistToSquared(IsoMovingObject movingObject) {
		return IsoUtils.DistanceToSquared(this.x, this.y, movingObject.x, movingObject.y);
	}

	public float DistToSquared(float float1, float float2) {
		return IsoUtils.DistanceToSquared(float1, float2, this.x, this.y);
	}

	public boolean getAllowBehaviours() {
		if (this instanceof IsoZombie) {
			return false;
		} else if (this.ScriptName.equals("none")) {
			return this.AllowBehaviours;
		} else {
			ScriptCharacter scriptCharacter = ScriptManager.instance.getCharacter(this.ScriptModule + "." + this.ScriptName);
			if (scriptCharacter == null) {
				return this.AllowBehaviours;
			} else {
				return this.AllowBehaviours && scriptCharacter.AllowBehaviours();
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		float float1 = byteBuffer.getFloat();
		float float2 = byteBuffer.getFloat();
		this.x = this.lx = this.nx = this.scriptnx = byteBuffer.getFloat() + (float)(IsoWorld.saveoffsetx * 300);
		this.y = this.ly = this.ny = this.scriptny = byteBuffer.getFloat() + (float)(IsoWorld.saveoffsety * 300);
		this.z = this.lz = byteBuffer.getFloat();
		this.dir = IsoDirections.fromIndex(byteBuffer.getInt());
		if (byteBuffer.get() != 0) {
			if (this.table == null) {
				this.table = LuaManager.platform.newTable();
			}

			this.table.load(byteBuffer, int1);
		}
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
		byteBuffer.putInt(this.getObjectName().hashCode());
		byteBuffer.putFloat(this.offsetX);
		byteBuffer.putFloat(this.offsetY);
		byteBuffer.putFloat(this.x);
		byteBuffer.putFloat(this.y);
		byteBuffer.putFloat(this.z);
		byteBuffer.putInt(this.dir.index());
		if (this.table != null && !this.table.isEmpty()) {
			byteBuffer.put((byte)1);
			this.table.save(byteBuffer);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void removeFromWorld() {
		IsoCell cell = this.getCell();
		if (cell.isSafeToAdd()) {
			cell.getObjectList().remove(this);
			cell.getRemoveList().remove(this);
		} else {
			cell.getRemoveList().add(this);
		}

		cell.getAddList().remove(this);
		super.removeFromWorld();
	}

	public void removeFromSquare() {
		if (this.current != null) {
			this.current.getMovingObjects().remove(this);
		}

		if (this.last != null) {
			this.last.getMovingObjects().remove(this);
		}

		if (this.movingSq != null) {
			this.movingSq.getMovingObjects().remove(this);
		}

		this.current = this.last = this.movingSq = null;
		if (this.square != null) {
			this.square.getStaticMovingObjects().remove(this);
		}

		super.removeFromSquare();
	}

	public IsoGridSquare getFuturWalkedSquare() {
		if (this.current != null) {
			IsoGridSquare square = this.getFeelerTile(this.feelersize);
			if (square != null && square != this.current) {
				return square;
			}
		}

		return null;
	}

	public float getGlobalMovementMod() {
		return this.getGlobalMovementMod(true);
	}

	public float getGlobalMovementMod(boolean boolean1) {
		if (this instanceof IsoPlayer && (((IsoPlayer)this).GhostMode || !((IsoPlayer)this).getAccessLevel().equals("None") || ((IsoPlayer)this).isNoClip())) {
			return 1.0F;
		} else {
			if (this.current != null && this.z - (float)((int)this.z) < 0.5F) {
				if (this.current.getProperties() != null && !this.current.getProperties().isBush && this.current.getProperties().Is("SpeedFactor")) {
					return Float.parseFloat(this.current.getProperties().Val("SpeedFactor")) / 100.0F;
				}

				if (this.current.Has(IsoObjectType.tree) || this.current.getProperties() != null && this.current.getProperties().isBush) {
					if (boolean1) {
						this.doTreeNoises();
					}

					for (int int1 = 1; int1 < this.current.getObjects().size(); ++int1) {
						IsoObject object = (IsoObject)this.current.getObjects().get(int1);
						if (object instanceof IsoTree) {
							object.setRenderEffect(RenderEffectType.Vegetation_Rustle);
							return ((IsoTree)object).getSlowFactor(this);
						}

						if (object.getSprite() != null && object.getSprite().isBush) {
							object.setRenderEffect(RenderEffectType.Vegetation_Rustle);
							return 0.6F;
						}
					}

					return 0.3F;
				}

				IsoGridSquare square = this.getFeelerTile(this.feelersize);
				if (square != null && square != this.current && (square.Has(IsoObjectType.tree) || square.getProperties() != null && square.getProperties().isBush)) {
					if (boolean1) {
						this.doTreeNoises();
					}

					for (int int2 = 1; int2 < square.getObjects().size(); ++int2) {
						IsoObject object2 = (IsoObject)square.getObjects().get(int2);
						if (object2 instanceof IsoTree) {
							object2.setRenderEffect(RenderEffectType.Vegetation_Rustle);
							return ((IsoTree)object2).getSlowFactor(this);
						}

						if (object2.getSprite() != null && object2.getSprite().isBush) {
							object2.setRenderEffect(RenderEffectType.Vegetation_Rustle);
							return 0.6F;
						}
					}

					return 0.3F;
				}
			}

			if (this.current != null && this.current.HasStairs()) {
				return 0.75F;
			} else {
				return 1.0F;
			}
		}
	}

	private void doTreeNoises() {
		if (!GameServer.bServer) {
			if (!(this instanceof IsoPhysicsObject)) {
				if (this.current != null) {
					if (Rand.Next(Rand.AdjustForFramerate(50)) == 0) {
						treeSoundMgr.addSquare(this.current);
					}
				}
			}
		}
	}

	public void postupdate() {
		boolean boolean1;
		if (this instanceof IsoZombie && GameServer.bServer && ((IsoZombie)this).getStateMachine().getCurrent() != ZombieStandState.instance()) {
			boolean1 = false;
		}

		if (this instanceof IsoPlayer && ((IsoPlayer)this).isLocalPlayer()) {
			IsoPlayer.instance = (IsoPlayer)this;
			IsoCamera.CamCharacter = (IsoPlayer)this;
		}

		float float1;
		if (GameClient.bClient && this instanceof IsoZombie) {
			if (this.bx == 0.0F) {
				this.bx = this.x;
				this.by = this.y;
			}

			tempo.x = this.x - this.bx;
			tempo.y = this.y - this.by;
			float1 = this.bspeed;
			if (this instanceof IsoZombie) {
				float1 *= GameTime.getInstance().getServerMultiplier();
			}

			if (tempo.getLength() > 2.0F) {
				float1 = tempo.getLength();
			}

			if (float1 > 0.001F && (tempo.x != 0.0F || tempo.y != 0.0F)) {
				if (float1 < tempo.getLength()) {
					tempo.setLength(float1);
				}

				this.bx += tempo.x;
				this.by += tempo.y;
			} else {
				this.bx = this.x;
				this.by = this.y;
			}

			if (this.movingSq != null) {
				this.movingSq.getMovingObjects().remove(this);
				this.movingSq = null;
			}

			this.last = this.current = this.square = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)this.z);
			if (this.current != null && !this.current.getMovingObjects().contains(this)) {
				this.current.getMovingObjects().add(this);
				this.movingSq = this.current;
			}

			this.getGlobalMovementMod();
		} else {
			this.ensureOnTile();
			if (this.lastTargettedBy != null && (this.lastTargettedBy.getHealth() <= 0.0F || this.lastTargettedBy.getBodyDamage().getHealth() <= 0.0F)) {
				this.lastTargettedBy = null;
			}

			if (this.lastTargettedBy != null && this.TimeSinceZombieAttack > 120) {
				this.lastTargettedBy = null;
			}

			++this.TimeSinceZombieAttack;
			if (this instanceof IsoPlayer) {
				boolean1 = false;
				((IsoPlayer)this).setLastCollidedW(this.collidedW);
				((IsoPlayer)this).setLastCollidedN(this.collidedN);
				IsoPlayer player = (IsoPlayer)this;
			}

			if (!this.destroyed) {
				if (!this.getAllowBehaviours() && this instanceof IsoSurvivor) {
					this.nx = this.scriptnx;
					this.ny = this.scriptny;
				}

				this.collidedThisFrame = false;
				this.collidedN = false;
				this.collidedS = false;
				this.collidedW = false;
				this.collidedE = false;
				this.CollidedWithDoor = false;
				this.last = this.current;
				this.CollidedObject = null;
				this.nx += this.impulsex;
				this.ny += this.impulsey;
				if (this.nx < 0.0F) {
					this.nx = 0.0F;
				}

				if (this.ny < 0.0F) {
					this.ny = 0.0F;
				}

				tempo.set(this.nx - this.x, this.ny - this.y);
				if (tempo.getLength() > 1.0F) {
					tempo.normalize();
					this.nx = this.x + tempo.getX();
					this.ny = this.y + tempo.getY();
				}

				this.impulsex = 0.0F;
				this.impulsey = 0.0F;
				if (!GameClient.bClient && this instanceof IsoZombie && (int)this.z == 0 && ((IsoZombie)this).getCurrentBuilding() == null && !this.isInLoadedArea((int)this.nx, (int)this.ny) && (((IsoZombie)this).getCurrentState() == PathFindState.instance() || ((IsoZombie)this).getCurrentState() == WalkTowardState.instance())) {
					ZombiePopulationManager.instance.virtualizeZombie((IsoZombie)this);
				} else {
					this.collidedWithVehicle = false;
					float float2;
					if (this instanceof IsoGameCharacter && !this.isOnFloor() && ((IsoGameCharacter)this).getVehicle() == null && (!(this instanceof IsoPlayer) || !((IsoPlayer)this).isNoClip())) {
						float1 = this.nx;
						float2 = this.ny;
						PolygonalMap2.instance.resolveCollision((IsoGameCharacter)this, this.nx, this.ny);
						if (float1 != this.nx || float2 != this.ny) {
							this.collidedWithVehicle = true;
						}
					}

					float1 = this.nx;
					float2 = this.ny;
					float float3 = 0.0F;
					boolean boolean2 = false;
					float float4;
					float float5;
					if (this.Collidable) {
						if (this.bAltCollide) {
							this.DoCollide(2);
						} else {
							this.DoCollide(1);
						}

						if (this.collidedN || this.collidedS) {
							this.ny = this.ly;
							this.DoCollideNorS();
						}

						if (this.collidedW || this.collidedE) {
							this.nx = this.lx;
							this.DoCollideWorE();
						}

						if (this.bAltCollide) {
							this.DoCollide(1);
						} else {
							this.DoCollide(2);
						}

						this.bAltCollide = !this.bAltCollide;
						if (this.collidedN || this.collidedS) {
							this.ny = this.ly;
							this.DoCollideNorS();
							boolean2 = true;
						}

						if (this.collidedW || this.collidedE) {
							this.nx = this.lx;
							this.DoCollideWorE();
							boolean2 = true;
						}

						float3 = Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly);
						float4 = this.nx;
						float5 = this.ny;
						this.nx = float1;
						this.ny = float2;
						if (this.Collidable && boolean2) {
							if (this.bAltCollide) {
								this.DoCollide(2);
							} else {
								this.DoCollide(1);
							}

							if (this.collidedN || this.collidedS) {
								this.ny = this.ly;
								this.DoCollideNorS();
							}

							if (this.collidedW || this.collidedE) {
								this.nx = this.lx;
								this.DoCollideWorE();
							}

							if (this.bAltCollide) {
								this.DoCollide(1);
							} else {
								this.DoCollide(2);
							}

							if (this.collidedN || this.collidedS) {
								this.ny = this.ly;
								this.DoCollideNorS();
								boolean2 = true;
							}

							if (this.collidedW || this.collidedE) {
								this.nx = this.lx;
								this.DoCollideWorE();
								boolean2 = true;
							}

							if (Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly) < float3) {
								this.nx = float4;
								this.ny = float5;
							}
						}
					}

					if (this.collidedThisFrame) {
						this.current = this.last;
					}

					float4 = this.nx - this.x;
					float5 = this.ny - this.y;
					if (Math.abs(float4) > 0.01F || Math.abs(float5) > 0.01F) {
						float4 *= this.getGlobalMovementMod();
						float5 *= this.getGlobalMovementMod();
					}

					this.x += float4;
					this.y += float5;
					if (GameClient.bClient) {
						if (this.bx == 0.0F) {
							this.bx = this.x;
							this.by = this.y;
						}

						tempo.x = this.x - this.bx;
						tempo.y = this.y - this.by;
						float float6 = this.bspeed;
						if (this instanceof IsoZombie) {
							float6 *= GameTime.getInstance().getServerMultiplier();
						}

						if (tempo.getLength() > 2.0F) {
							float6 = tempo.getLength();
						}

						if (!(float6 > 0.001F) || tempo.x == 0.0F && tempo.y == 0.0F) {
							this.bx = this.x;
							this.by = this.y;
						} else {
							if (float6 < tempo.getLength()) {
								tempo.setLength(float6);
							}

							this.bx += tempo.x;
							this.by += tempo.y;
						}
					}

					if (this instanceof IsoPlayer) {
						this.bx = this.x;
						this.by = this.y;
					}

					this.doStairs();
					this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
					if (this.current == null) {
						for (int int1 = (int)this.z; int1 >= 0; --int1) {
							this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, int1);
							if (this.current != null) {
								break;
							}
						}

						if (this.current == null && this.last != null) {
							this.current = this.last;
							this.x = this.nx = this.scriptnx = (float)this.current.getX() + 0.5F;
							this.y = this.ny = this.scriptny = (float)this.current.getY() + 0.5F;
						}
					}

					if (this.movingSq != null) {
						this.movingSq.getMovingObjects().remove(this);
						this.movingSq = null;
					}

					if (this.current != null && !this.current.getMovingObjects().contains(this)) {
						this.current.getMovingObjects().add(this);
						this.movingSq = this.current;
					}

					this.ensureOnTile();
					this.square = this.current;
					this.scriptnx = this.nx;
					this.scriptny = this.ny;
					this.firstUpdate = false;
				}
			}
		}
	}

	public void ensureOnTile() {
		if (this.current == null) {
			if (!(this instanceof IsoPlayer)) {
				if (this instanceof IsoSurvivor) {
					IsoWorld.instance.CurrentCell.Remove(this);
					IsoWorld.instance.CurrentCell.getSurvivorList().remove((IsoSurvivor)this);
				}

				return;
			}

			boolean boolean1 = true;
			boolean boolean2 = false;
			if (this.last != null && (this.last.Has(IsoObjectType.stairsTN) || this.last.Has(IsoObjectType.stairsTW))) {
				this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z + 1);
				boolean1 = false;
			}

			if (this.current == null) {
				this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
				return;
			}

			if (boolean1) {
				this.x = this.nx = this.scriptnx = (float)this.current.getX() + 0.5F;
				this.y = this.ny = this.scriptny = (float)this.current.getY() + 0.5F;
			}

			this.z = (float)this.current.getZ();
		}
	}

	public void preupdate() {
		this.nx = this.x;
		this.ny = this.y;
	}

	public void renderlast() {
	}

	public void spotted(IsoMovingObject movingObject, boolean boolean1) {
	}

	public void update() {
		if (this.def == null) {
			this.def = IsoSpriteInstance.get(this.sprite);
		}

		this.movementLastFrame.x = this.x - this.lx;
		this.movementLastFrame.y = this.y - this.ly;
		this.lx = this.x;
		this.ly = this.y;
		this.lz = this.z;
		this.square = this.current;
		if (this.sprite != null) {
			this.sprite.update(this.def);
		}

		this.StateEventDelayTimer -= GameTime.instance.getMultiplier();
	}

	private void Collided() {
		this.collidedThisFrame = true;
	}

	public int compareToY(IsoMovingObject movingObject) {
		if (this.sprite == null && movingObject.sprite == null) {
			return 0;
		} else if (this.sprite != null && movingObject.sprite == null) {
			return -1;
		} else if (this.sprite == null && movingObject.sprite != null) {
			return 1;
		} else {
			float float1 = IsoUtils.YToScreen(this.x, this.y, this.z, 0);
			float float2 = IsoUtils.YToScreen(movingObject.x, movingObject.y, movingObject.z, 0);
			double double1 = (double)float1;
			double double2 = (double)float2;
			if (double1 > double2) {
				return 1;
			} else {
				return double1 < double2 ? -1 : 0;
			}
		}
	}

	public float distToNearestCamCharacter() {
		float float1 = Float.MAX_VALUE;
		for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
			IsoPlayer player = IsoPlayer.players[int1];
			if (player != null) {
				float1 = Math.min(float1, this.DistTo(player));
			}
		}

		return float1;
	}

	public void seperate() {
		if (this.current != null) {
			if (this.solid) {
				if (!(this instanceof IsoPlayer) || !((IsoPlayer)this).GhostMode) {
					if (this instanceof IsoGameCharacter) {
						if (((IsoGameCharacter)this).getStateMachine().getCurrent() == StaggerBackDieState.instance() || ((IsoGameCharacter)this).getStateMachine().getCurrent() == FakeDeadZombieState.instance()) {
							return;
						}

						if (GameClient.bClient && this.isOnFloor()) {
							return;
						}
					}

					if (this.z < 0.0F) {
						this.z = 0.0F;
					}

					for (int int1 = -1; int1 <= 1; ++int1) {
						for (int int2 = -1; int2 <= 1; ++int2) {
							IsoGridSquare square = this.getCell().getGridSquare(this.current.getX() + int1, this.current.getY() + int2, (int)this.z);
							if (square != null) {
								for (int int3 = 0; int3 < square.getMovingObjects().size(); ++int3) {
									IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int3);
									if (!(movingObject instanceof IsoZombieGiblets) && movingObject != this && movingObject.solid && (!(movingObject instanceof IsoPlayer) || !((IsoPlayer)movingObject).GhostMode) && !movingObject.isOnFloor()) {
										float float1 = this.width + movingObject.width;
										if (tempo == null) {
											tempo = new Vector2(this.nx, this.ny);
										} else {
											tempo.x = this.nx;
											tempo.y = this.ny;
										}

										Vector2 vector2 = tempo;
										vector2.x -= movingObject.nx;
										vector2 = tempo;
										vector2.y -= movingObject.ny;
										if (!(Math.abs(this.z - movingObject.z) > 0.3F)) {
											float float2;
											if (this instanceof IsoGameCharacter && movingObject instanceof IsoGameCharacter) {
												float2 = tempo.getLength();
												if (float2 < float1 && (GameServer.bServer || this.distToNearestCamCharacter() < 60.0F) && float2 < float1) {
													tempo.setLength((float2 - float1) / 8.0F);
													if (((IsoGameCharacter)movingObject).compareMovePriority((IsoGameCharacter)this) >= 0) {
														this.nx -= tempo.x;
														this.ny -= tempo.y;
													}

													if (((IsoGameCharacter)this).compareMovePriority((IsoGameCharacter)movingObject) >= 0) {
														movingObject.nx += tempo.x;
														movingObject.ny += tempo.y;
													}

													this.collideWith(movingObject);
													movingObject.collideWith(this);
												}
											} else if (!(this instanceof IsoGameCharacter) || !(movingObject instanceof BaseVehicle)) {
												float2 = tempo.getLength();
												if (float2 < float1 * 1.0F) {
													CollisionManager.instance.AddContact(this, movingObject);
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	private boolean DoCollide(int int1) {
		this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
		int int2 = (int)this.z;
		int int3;
		int int4;
		int int5;
		if (this instanceof IsoMolotovCocktail) {
			for (int3 = (int)this.z; int3 > 0; --int3) {
				for (int4 = -1; int4 <= 1; ++int4) {
					for (int5 = -1; int5 <= 1; ++int5) {
						IsoGridSquare square = this.getCell().createNewGridSquare((int)this.nx + int5, (int)this.ny + int4, int3, false);
						if (square != null) {
							square.RecalcAllWithNeighbours(true);
						}
					}
				}
			}
		}

		if (this.current != null) {
			if (!this.current.TreatAsSolidFloor()) {
				this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
			}

			if (this.current == null) {
				return false;
			}

			this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
		}

		if (this.current != this.last && this.last != null && this.current != null) {
			if (this == IsoCamera.CamCharacter) {
				IsoWorld.instance.CurrentCell.lightUpdateCount = 10;
			}

			int3 = this.current.getX() - this.last.getX();
			int4 = this.current.getY() - this.last.getY();
			int5 = this.current.getZ() - this.last.getZ();
			boolean boolean1 = false;
			boolean boolean2 = false;
			if (int3 != 0 && int4 == 0) {
				boolean1 = true;
			}

			boolean boolean3 = false;
			if (this.last.testCollideAdjacent(this, int3, int4, int5) || this.current == null) {
				boolean3 = true;
			}

			if (boolean3) {
				if (this.last.getX() < this.current.getX()) {
					this.collidedE = true;
				}

				if (this.last.getX() > this.current.getX()) {
					this.collidedW = true;
				}

				if (this.last.getY() < this.current.getY()) {
					this.collidedS = true;
				}

				if (this.last.getY() > this.current.getY()) {
					this.collidedN = true;
				}

				if (this instanceof IsoZombie) {
					IsoZombie zombie;
					if (this.collidedW && !this.collidedN && !this.collidedS && this.last.Is(IsoFlagType.HoppableW)) {
						zombie = (IsoZombie)this;
						if (!zombie.bCrawling) {
							if (zombie.getCurrentState() != StaggerBackState.instance() && zombie.getCurrentState() != AttackState.instance()) {
								zombie.StateMachineParams.put(0, IsoDirections.W);
								zombie.getStateMachine().changeState(ClimbOverFenceState.instance());
							} else {
								zombie.getStateMachine().changeState(AttackState.instance());
							}
						}
					}

					if (this.collidedN && !this.collidedE && !this.collidedW && this.last.Is(IsoFlagType.HoppableN)) {
						zombie = (IsoZombie)this;
						if (!zombie.bCrawling) {
							if (zombie.getCurrentState() != StaggerBackState.instance() && zombie.getCurrentState() != AttackState.instance()) {
								zombie.StateMachineParams.put(0, IsoDirections.N);
								zombie.getStateMachine().changeState(ClimbOverFenceState.instance());
							} else {
								zombie.getStateMachine().changeState(AttackState.instance());
							}
						}
					}

					IsoZombie zombie2;
					IsoGridSquare square2;
					if (this.collidedS && !this.collidedE && !this.collidedW) {
						square2 = this.last.nav[IsoDirections.S.index()];
						if (square2 != null && square2.Is(IsoFlagType.HoppableN)) {
							zombie2 = (IsoZombie)this;
							if (!zombie2.bCrawling) {
								if (zombie2.getCurrentState() != StaggerBackState.instance() && zombie2.getCurrentState() != AttackState.instance()) {
									zombie2.StateMachineParams.put(0, IsoDirections.S);
									zombie2.getStateMachine().changeState(ClimbOverFenceState.instance());
								} else {
									zombie2.getStateMachine().changeState(AttackState.instance());
								}
							}
						}
					}

					if (this.collidedE && !this.collidedN && !this.collidedS) {
						square2 = this.last.nav[IsoDirections.E.index()];
						if (square2 != null && square2.Is(IsoFlagType.HoppableW)) {
							zombie2 = (IsoZombie)this;
							if (!zombie2.bCrawling) {
								if (zombie2.getCurrentState() != StaggerBackState.instance() && zombie2.getCurrentState() != AttackState.instance()) {
									zombie2.StateMachineParams.put(0, IsoDirections.E);
									zombie2.getStateMachine().changeState(ClimbOverFenceState.instance());
								} else {
									zombie2.getStateMachine().changeState(AttackState.instance());
								}
							}
						}
					}
				}

				if (int1 == 2) {
					if ((this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
						this.collidedS = false;
						this.collidedN = false;
					}
				} else if (int1 == 1 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
					this.collidedW = false;
					this.collidedE = false;
				}

				this.current = this.last;
				this.Collided();
				return true;
			}
		} else if (this.nx != this.lx || this.ny != this.ly) {
			if (this instanceof IsoPlayer) {
				boolean boolean4 = false;
			}

			if (this.current == null) {
				if (this.nx < this.lx) {
					this.collidedW = true;
				}

				if (this.nx > this.lx) {
					this.collidedE = true;
				}

				if (this.ny < this.ly) {
					this.collidedN = true;
				}

				if (this.ny > this.ly) {
					this.collidedS = true;
				}

				this.nx = this.lx;
				this.ny = this.ly;
				this.current = this.last;
				this.Collided();
				return true;
			}

			if (this instanceof IsoGameCharacter) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)this;
				if (gameCharacter.getPath2() != null) {
					PathFindBehavior2 pathFindBehavior2 = gameCharacter.getPathFindBehavior2();
					if ((int)pathFindBehavior2.getTargetX() == (int)this.x && (int)pathFindBehavior2.getTargetY() == (int)this.y && (int)pathFindBehavior2.getTargetZ() == (int)this.z) {
						return false;
					}
				}
			}

			IsoGridSquare square3 = this.getFeelerTile(this.feelersize);
			if (!GameServer.bServer && this instanceof IsoPlayer && square3 != this.current && ((IsoPlayer)this).TargetSpeed > 0.05F && !((IsoPlayer)this).JustMoved && square3 != null) {
				IsoObject object = this.current.getDoorTo(square3);
				if (object != null) {
					IsoGridSquare square4;
					if (object instanceof IsoDoor) {
						square4 = this.getFeelerTile(this.feelersize * 1.5F);
						if (!((IsoDoor)object).open && square4 == square3) {
							((IsoDoor)object).ToggleDoor((IsoPlayer)this);
						}
					} else if (object instanceof IsoThumpable) {
						square4 = this.getFeelerTile(this.feelersize * 1.5F);
						if (!((IsoThumpable)object).open && square4 == square3) {
							((IsoThumpable)object).ToggleDoor((IsoPlayer)this);
						}
					}
				}
			}

			if (this instanceof IsoGameCharacter && ((IsoGameCharacter)this).isClimbing()) {
				square3 = this.current;
			}

			if (square3 != null && square3 != this.current && this.current != null) {
				if (this == IsoCamera.CamCharacter) {
					boolean boolean5 = false;
				}

				if (this.current.testCollideAdjacent(this, square3.getX() - this.current.getX(), square3.getY() - this.current.getY(), square3.getZ() - this.current.getZ())) {
					if (this.last != null) {
						if (this.current.getX() < square3.getX()) {
							this.collidedE = true;
						}

						if (this.current.getX() > square3.getX()) {
							this.collidedW = true;
						}

						if (this.current.getY() < square3.getY()) {
							this.collidedS = true;
						}

						if (this.current.getY() > square3.getY()) {
							this.collidedN = true;
						}

						if (this instanceof IsoZombie) {
							IsoZombie zombie3;
							if (this.collidedW && !this.collidedN && !this.collidedS && this.current.Is(IsoFlagType.HoppableW)) {
								zombie3 = (IsoZombie)this;
								if (!zombie3.bCrawling) {
									if (zombie3.getCurrentState() != StaggerBackState.instance() && zombie3.getCurrentState() != AttackState.instance()) {
										zombie3.StateMachineParams.put(0, IsoDirections.W);
										zombie3.getStateMachine().changeState(ClimbOverFenceState.instance());
									} else {
										zombie3.getStateMachine().changeState(AttackState.instance());
									}
								}
							}

							if (this.collidedN && !this.collidedE && !this.collidedW && this.current.Is(IsoFlagType.HoppableN)) {
								zombie3 = (IsoZombie)this;
								if (!zombie3.bCrawling) {
									if (zombie3.getCurrentState() != StaggerBackState.instance() && zombie3.getCurrentState() != AttackState.instance()) {
										zombie3.StateMachineParams.put(0, IsoDirections.N);
										zombie3.getStateMachine().changeState(ClimbOverFenceState.instance());
									} else {
										zombie3.getStateMachine().changeState(AttackState.instance());
									}
								}
							}

							IsoGridSquare square5;
							IsoZombie zombie4;
							if (this.collidedS && !this.collidedE && !this.collidedW) {
								square5 = this.last.nav[IsoDirections.S.index()];
								if (square5 != null && square5.Is(IsoFlagType.HoppableN)) {
									zombie4 = (IsoZombie)this;
									if (!zombie4.bCrawling) {
										if (zombie4.getCurrentState() != StaggerBackState.instance() && zombie4.getCurrentState() != AttackState.instance()) {
											zombie4.StateMachineParams.put(0, IsoDirections.S);
											zombie4.getStateMachine().changeState(ClimbOverFenceState.instance());
										} else {
											zombie4.getStateMachine().changeState(AttackState.instance());
										}
									}
								}
							}

							if (this.collidedE && !this.collidedN && !this.collidedS) {
								square5 = this.last.nav[IsoDirections.E.index()];
								if (square5 != null && square5.Is(IsoFlagType.HoppableW)) {
									zombie4 = (IsoZombie)this;
									if (!zombie4.bCrawling) {
										if (zombie4.getCurrentState() != StaggerBackState.instance() && zombie4.getCurrentState() != AttackState.instance()) {
											zombie4.StateMachineParams.put(0, IsoDirections.E);
											zombie4.getStateMachine().changeState(ClimbOverFenceState.instance());
										} else {
											zombie4.getStateMachine().changeState(AttackState.instance());
										}
									}
								}
							}
						}

						if (int1 == 2 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
							this.collidedS = false;
							this.collidedN = false;
						}

						if (int1 == 1 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
							this.collidedW = false;
							this.collidedE = false;
						}
					}

					this.Collided();
					return true;
				}
			}
		}

		return false;
	}

	public IsoGridSquare getFeelerTile(float float1) {
		Vector2 vector2 = tempo;
		vector2.x = this.nx - this.lx;
		vector2.y = this.ny - this.ly;
		vector2.setLength(float1);
		return this.getCell().getGridSquare((int)(this.x + vector2.x), (int)(this.y + vector2.y), (int)this.z);
	}

	public void DoCollideNorS() {
		this.ny = this.ly;
	}

	public void DoCollideWorE() {
		this.nx = this.lx;
	}

	public int getTimeSinceZombieAttack() {
		return this.TimeSinceZombieAttack;
	}

	public void setTimeSinceZombieAttack(int int1) {
		this.TimeSinceZombieAttack = int1;
	}

	public boolean isCollidedE() {
		return this.collidedE;
	}

	public void setCollidedE(boolean boolean1) {
		this.collidedE = boolean1;
	}

	public boolean isCollidedN() {
		return this.collidedN;
	}

	public void setCollidedN(boolean boolean1) {
		this.collidedN = boolean1;
	}

	public IsoObject getCollidedObject() {
		return this.CollidedObject;
	}

	public void setCollidedObject(IsoObject object) {
		this.CollidedObject = object;
	}

	public boolean isCollidedS() {
		return this.collidedS;
	}

	public void setCollidedS(boolean boolean1) {
		this.collidedS = boolean1;
	}

	public boolean isCollidedThisFrame() {
		return this.collidedThisFrame;
	}

	public void setCollidedThisFrame(boolean boolean1) {
		this.collidedThisFrame = boolean1;
	}

	public boolean isCollidedW() {
		return this.collidedW;
	}

	public void setCollidedW(boolean boolean1) {
		this.collidedW = boolean1;
	}

	public boolean isCollidedWithDoor() {
		return this.CollidedWithDoor;
	}

	public void setCollidedWithDoor(boolean boolean1) {
		this.CollidedWithDoor = boolean1;
	}

	public boolean isCollidedWithVehicle() {
		return this.collidedWithVehicle;
	}

	public IsoGridSquare getCurrentSquare() {
		return this.current;
	}

	public IsoMetaGrid.Zone getCurrentZone() {
		return this.current != null ? this.current.getZone() : null;
	}

	public void setCurrent(IsoGridSquare square) {
		this.current = square;
	}

	public boolean isDestroyed() {
		return this.destroyed;
	}

	public void setDestroyed(boolean boolean1) {
		this.destroyed = boolean1;
	}

	public boolean isFirstUpdate() {
		return this.firstUpdate;
	}

	public void setFirstUpdate(boolean boolean1) {
		this.firstUpdate = boolean1;
	}

	public Vector2 getHitDir() {
		return this.hitDir;
	}

	public void setHitDir(Vector2 vector2) {
		this.hitDir.set(vector2);
	}

	public boolean isAllowBehaviours() {
		return this.AllowBehaviours;
	}

	public void setAllowBehaviours(boolean boolean1) {
		this.AllowBehaviours = boolean1;
	}

	public float getImpulsex() {
		return this.impulsex;
	}

	public void setImpulsex(float float1) {
		this.impulsex = float1;
	}

	public float getImpulsey() {
		return this.impulsey;
	}

	public void setImpulsey(float float1) {
		this.impulsey = float1;
	}

	public float getLimpulsex() {
		return this.limpulsex;
	}

	public void setLimpulsex(float float1) {
		this.limpulsex = float1;
	}

	public float getLimpulsey() {
		return this.limpulsey;
	}

	public void setLimpulsey(float float1) {
		this.limpulsey = float1;
	}

	public float getHitForce() {
		return this.hitForce;
	}

	public void setHitForce(float float1) {
		this.hitForce = float1;
	}

	public float getHitFromAngle() {
		return this.hitFromAngle;
	}

	public void setHitFromAngle(float float1) {
		this.hitFromAngle = float1;
	}

	public void setID(int int1) {
		this.ID = int1;
	}

	public IsoGridSquare getLastSquare() {
		return this.last;
	}

	public void setLast(IsoGridSquare square) {
		this.last = square;
	}

	public float getLx() {
		return this.lx;
	}

	public void setLx(float float1) {
		this.lx = float1;
	}

	public float getLy() {
		return this.ly;
	}

	public void setLy(float float1) {
		this.ly = float1;
	}

	public float getLz() {
		return this.lz;
	}

	public void setLz(float float1) {
		this.lz = float1;
	}

	public float getNx() {
		return this.nx;
	}

	public void setNx(float float1) {
		this.nx = float1;
	}

	public float getNy() {
		return this.ny;
	}

	public void setNy(float float1) {
		this.ny = float1;
	}

	public void setNoDamage(boolean boolean1) {
		this.noDamage = boolean1;
	}

	public boolean getNoDamage() {
		return this.noDamage;
	}

	public void setPathFindIndex(int int1) {
		this.PathFindIndex = int1;
	}

	public boolean isSolid() {
		return this.solid;
	}

	public void setSolid(boolean boolean1) {
		this.solid = boolean1;
	}

	public float getStateEventDelayTimer() {
		return this.StateEventDelayTimer;
	}

	public void setStateEventDelayTimer(float float1) {
		this.StateEventDelayTimer = float1;
	}

	public void setThumpTarget(Thumpable thumpable) {
		this.thumpTarget = thumpable;
	}

	public float getWidth() {
		return this.width;
	}

	public void setWidth(float float1) {
		this.width = float1;
	}

	public void setX(float float1) {
		this.x = float1;
		this.nx = float1;
		this.scriptnx = float1;
	}

	public void setY(float float1) {
		this.y = float1;
		this.ny = float1;
		this.scriptny = float1;
	}

	public void setZ(float float1) {
		this.z = float1;
		this.lz = float1;
		this.bz = float1;
	}

	public boolean isbAltCollide() {
		return this.bAltCollide;
	}

	public void setbAltCollide(boolean boolean1) {
		this.bAltCollide = boolean1;
	}

	public boolean isShootable() {
		return this.shootable;
	}

	public void setShootable(boolean boolean1) {
		this.shootable = boolean1;
	}

	public IsoZombie getLastTargettedBy() {
		return this.lastTargettedBy;
	}

	public void setLastTargettedBy(IsoZombie zombie) {
		this.lastTargettedBy = zombie;
	}

	public boolean isCollidable() {
		return this.Collidable;
	}

	public void setCollidable(boolean boolean1) {
		this.Collidable = boolean1;
	}

	public float getScriptnx() {
		return this.scriptnx;
	}

	public void setScriptnx(float float1) {
		this.scriptnx = float1;
	}

	public float getScriptny() {
		return this.scriptny;
	}

	public void setScriptny(float float1) {
		this.scriptny = float1;
	}

	public String getScriptModule() {
		return this.ScriptModule;
	}

	public void setScriptModule(String string) {
		this.ScriptModule = string;
	}

	public String getScriptName() {
		return this.ScriptName;
	}

	public void setScriptName(String string) {
		this.ScriptName = string;
	}

	public Stack getActiveInInstances() {
		return this.ActiveInInstances;
	}

	public void setActiveInInstances(Stack stack) {
		this.ActiveInInstances = stack;
	}

	public Vector2 getMovementLastFrame() {
		return this.movementLastFrame;
	}

	public void setMovementLastFrame(Vector2 vector2) {
		this.movementLastFrame = vector2;
	}

	public void setWeight(float float1) {
		this.weight = float1;
	}

	public float getFeelersize() {
		return this.feelersize;
	}

	public void setFeelersize(float float1) {
		this.feelersize = float1;
	}

	public void setOnFloor(boolean boolean1) {
		this.bOnFloor = boolean1;
	}

	public boolean isOnFloor() {
		return this.bOnFloor;
	}

	public void Despawn() {
	}

	public boolean isCloseKilled() {
		return this.closeKilled;
	}

	public void setCloseKilled(boolean boolean1) {
		this.closeKilled = boolean1;
	}

	public void setBlendSpeed(float float1) {
		this.bspeed = float1;
	}

	public Vector2 getFacingPosition(Vector2 vector2) {
		vector2.set(this.getX(), this.getY());
		return vector2;
	}

	private boolean isInLoadedArea(int int1, int int2) {
		int int3;
		if (GameServer.bServer) {
			for (int3 = 0; int3 < ServerMap.instance.LoadedCells.size(); ++int3) {
				ServerMap.ServerCell serverCell = (ServerMap.ServerCell)ServerMap.instance.LoadedCells.get(int3);
				if (int1 >= serverCell.WX * 70 && int1 < (serverCell.WX + 1) * 70 && int2 >= serverCell.WY * 70 && int2 < (serverCell.WY + 1) * 70) {
					return true;
				}
			}
		} else {
			for (int3 = 0; int3 < IsoPlayer.numPlayers; ++int3) {
				IsoChunkMap chunkMap = IsoWorld.instance.CurrentCell.ChunkMap[int3];
				if (!chunkMap.ignore && int1 >= chunkMap.getWorldXMinTiles() && int1 < chunkMap.getWorldXMaxTiles() && int2 >= chunkMap.getWorldYMinTiles() && int2 < chunkMap.getWorldYMaxTiles()) {
					return true;
				}
			}
		}

		return false;
	}

	public static class TreeSoundManager {
		private ArrayList squares = new ArrayList();
		private long[] soundTime = new long[6];
		private Comparator comp = new Comparator(){
    
    public int compare(IsoGridSquare var1, IsoGridSquare var2) {
        float var3 = TreeSoundManager.this.getClosestListener((float)var1.x + 0.5F, (float)var1.y + 0.5F, (float)var1.z);
        float var4 = TreeSoundManager.this.getClosestListener((float)var2.x + 0.5F, (float)var2.y + 0.5F, (float)var2.z);
        if (var3 > var4) {
            return 1;
        } else {
            return var3 < var4 ? -1 : 0;
        }
    }
};

		public void addSquare(IsoGridSquare square) {
			if (!this.squares.contains(square)) {
				this.squares.add(square);
			}
		}

		public void update() {
			if (!this.squares.isEmpty()) {
				Collections.sort(this.squares, this.comp);
				long long1 = System.currentTimeMillis();
				for (int int1 = 0; int1 < this.soundTime.length && int1 < this.squares.size(); ++int1) {
					IsoGridSquare square = (IsoGridSquare)this.squares.get(int1);
					if (!(this.getClosestListener((float)square.x + 0.5F, (float)square.y + 0.5F, (float)square.z) > 20.0F)) {
						int int2 = this.getFreeSoundSlot(long1);
						if (int2 == -1) {
							break;
						}

						Audio audio = null;
						float float1 = 0.05F;
						float float2 = 16.0F;
						float float3 = 0.29999998F;
						if (GameClient.bClient) {
							audio = SoundManager.instance.PlayWorldSoundImpl("Bushes", false, square.getX(), square.getY(), square.getZ(), float1, float2, float3, false);
						} else {
							BaseSoundEmitter baseSoundEmitter = IsoWorld.instance.getFreeEmitter((float)square.x + 0.5F, (float)square.y + 0.5F, (float)square.z);
							if (baseSoundEmitter.playSound("Bushes") != 0L) {
								this.soundTime[int2] = long1;
							}
						}

						if (audio != null) {
							this.soundTime[int2] = long1;
						}
					}
				}

				this.squares.clear();
			}
		}

		private float getClosestListener(float float1, float float2, float float3) {
			float float4 = Float.MAX_VALUE;
			for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				IsoPlayer player = IsoPlayer.players[int1];
				if (player != null && player.getCurrentSquare() != null) {
					float float5 = player.getX();
					float float6 = player.getY();
					float float7 = player.getZ();
					float float8 = IsoUtils.DistanceTo(float5, float6, float7 * 3.0F, float1, float2, float3 * 3.0F);
					if (player.HasTrait("HardOfHearing")) {
						float8 *= 4.5F;
					}

					if (float8 < float4) {
						float4 = float8;
					}
				}
			}

			return float4;
		}

		private int getFreeSoundSlot(long long1) {
			long long2 = Long.MAX_VALUE;
			int int1 = -1;
			for (int int2 = 0; int2 < this.soundTime.length; ++int2) {
				if (this.soundTime[int2] < long2) {
					long2 = this.soundTime[int2];
					int1 = int2;
				}
			}

			if (long1 - long2 < 1000L) {
				return -1;
			} else {
				return int1;
			}
		}
	}
}
