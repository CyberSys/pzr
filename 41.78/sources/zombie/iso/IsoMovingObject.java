package zombie.iso;

import fmod.fmod.Audio;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import org.joml.Vector2f;
import zombie.CollisionManager;
import zombie.GameTime;
import zombie.MovingObjectUpdateScheduler;
import zombie.SoundManager;
import zombie.Lua.LuaEventManager;
import zombie.Lua.LuaManager;
import zombie.ai.State;
import zombie.ai.astar.Mover;
import zombie.ai.states.AttackState;
import zombie.ai.states.ClimbOverFenceState;
import zombie.ai.states.ClimbThroughWindowState;
import zombie.ai.states.CollideWithWallState;
import zombie.ai.states.CrawlingZombieTurnState;
import zombie.ai.states.PathFindState;
import zombie.ai.states.StaggerBackState;
import zombie.ai.states.WalkTowardState;
import zombie.ai.states.ZombieFallDownState;
import zombie.ai.states.ZombieIdleState;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoSurvivor;
import zombie.characters.IsoZombie;
import zombie.characters.BodyDamage.BodyPart;
import zombie.characters.BodyDamage.BodyPartType;
import zombie.characters.skills.PerkFactory;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.skinnedmodel.model.Model;
import zombie.core.textures.ColorInfo;
import zombie.debug.DebugLog;
import zombie.debug.DebugOptions;
import zombie.debug.LineDrawer;
import zombie.inventory.types.HandWeapon;
import zombie.inventory.types.WeaponType;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.areas.IsoBuilding;
import zombie.iso.areas.IsoRoom;
import zombie.iso.areas.isoregion.regions.IWorldRegion;
import zombie.iso.objects.IsoMolotovCocktail;
import zombie.iso.objects.IsoThumpable;
import zombie.iso.objects.IsoTree;
import zombie.iso.objects.IsoZombieGiblets;
import zombie.iso.objects.RenderEffectType;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerMap;
import zombie.network.ServerOptions;
import zombie.popman.ZombiePopulationManager;
import zombie.util.StringUtils;
import zombie.util.Type;
import zombie.vehicles.BaseVehicle;
import zombie.vehicles.PathFindBehavior2;
import zombie.vehicles.PolygonalMap2;


public class IsoMovingObject extends IsoObject implements Mover {
	public static IsoMovingObject.TreeSoundManager treeSoundMgr = new IsoMovingObject.TreeSoundManager();
	public static final int MAX_ZOMBIES_EATING = 3;
	private static int IDCount = 0;
	private static final Vector2 tempo = new Vector2();
	public boolean noDamage = false;
	public IsoGridSquare last = null;
	public float lx;
	public float ly;
	public float lz;
	public float nx;
	public float ny;
	public float x;
	public float y;
	public float z;
	public IsoSpriteInstance def = null;
	protected IsoGridSquare current = null;
	protected Vector2 hitDir = new Vector2();
	protected int ID = 0;
	protected IsoGridSquare movingSq = null;
	protected boolean solid = true;
	protected float width = 0.24F;
	protected boolean shootable = true;
	protected boolean Collidable = true;
	protected float scriptnx = 0.0F;
	protected float scriptny = 0.0F;
	protected String ScriptModule = "none";
	protected Vector2 movementLastFrame = new Vector2();
	protected float weight = 1.0F;
	boolean bOnFloor = false;
	private boolean closeKilled = false;
	private String collideType = null;
	private float lastCollideTime = 0.0F;
	private int TimeSinceZombieAttack = 1000000;
	private boolean collidedE = false;
	private boolean collidedN = false;
	private IsoObject CollidedObject = null;
	private boolean collidedS = false;
	private boolean collidedThisFrame = false;
	private boolean collidedW = false;
	private boolean CollidedWithDoor = false;
	private boolean collidedWithVehicle = false;
	private boolean destroyed = false;
	private boolean firstUpdate = true;
	private float impulsex = 0.0F;
	private float impulsey = 0.0F;
	private float limpulsex = 0.0F;
	private float limpulsey = 0.0F;
	private float hitForce = 0.0F;
	private float hitFromAngle;
	private int PathFindIndex = -1;
	private float StateEventDelayTimer = 0.0F;
	private Thumpable thumpTarget = null;
	private boolean bAltCollide = false;
	private IsoZombie lastTargettedBy = null;
	private float feelersize = 0.5F;
	public final boolean[] bOutline = new boolean[4];
	public final ColorInfo[] outlineColor = new ColorInfo[4];
	private final ArrayList eatingZombies = new ArrayList();
	private boolean zombiesDontAttack = false;

	public IsoMovingObject(IsoCell cell) {
		this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
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
		this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
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

	public static int getIDCount() {
		return IDCount;
	}

	public static void setIDCount(int int1) {
		IDCount = int1;
	}

	public IsoBuilding getBuilding() {
		if (this.current == null) {
			return null;
		} else {
			IsoRoom room = this.current.getRoom();
			return room == null ? null : room.building;
		}
	}

	public IWorldRegion getMasterRegion() {
		return this.current != null ? this.current.getIsoWorldRegion() : null;
	}

	public float getWeight() {
		return this.weight;
	}

	public void setWeight(float float1) {
		this.weight = float1;
	}

	public float getWeight(float float1, float float2) {
		return this.weight;
	}

	public void onMouseRightClick(int int1, int int2) {
		if (this.square.getZ() == (int)IsoPlayer.getInstance().getZ() && this.DistToProper(IsoPlayer.getInstance()) <= 2.0F) {
			IsoPlayer.getInstance().setDragObject(this);
		}
	}

	public String getObjectName() {
		return "IsoMovingObject";
	}

	public void onMouseRightReleased() {
	}

	public void collideWith(IsoObject object) {
		if (this instanceof IsoGameCharacter && object instanceof IsoGameCharacter) {
			LuaEventManager.triggerEvent("OnCharacterCollide", this, object);
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
					if (square.HasStairs()) {
						float1 = square.getApparentZ(this.x - (float)square.getX(), this.y - (float)square.getY());
					}

					if (this instanceof IsoGameCharacter) {
						State state = ((IsoGameCharacter)this).getCurrentState();
						if (state == ClimbOverFenceState.instance() || state == ClimbThroughWindowState.instance()) {
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

	public void setID(int int1) {
		this.ID = int1;
	}

	public int getPathFindIndex() {
		return this.PathFindIndex;
	}

	public void setPathFindIndex(int int1) {
		this.PathFindIndex = int1;
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

	public void setThumpTarget(Thumpable thumpable) {
		this.thumpTarget = thumpable;
	}

	public Vector2 getVectorFromDirection(Vector2 vector2) {
		return getVectorFromDirection(vector2, this.dir);
	}

	public static Vector2 getVectorFromDirection(Vector2 vector2, IsoDirections directions) {
		if (vector2 == null) {
			DebugLog.General.warn("Supplied vector2 is null. Cannot be processed. Using fail-safe fallback.");
			vector2 = new Vector2();
		}

		vector2.x = 0.0F;
		vector2.y = 0.0F;
		switch (directions) {
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

	public Vector3 getPosition(Vector3 vector3) {
		vector3.set(this.getX(), this.getY(), this.getZ());
		return vector3;
	}

	public float getX() {
		return this.x;
	}

	public void setX(float float1) {
		this.x = float1;
		this.nx = float1;
		this.scriptnx = float1;
	}

	public float getY() {
		return this.y;
	}

	public void setY(float float1) {
		this.y = float1;
		this.ny = float1;
		this.scriptny = float1;
	}

	public float getZ() {
		return this.z;
	}

	public void setZ(float float1) {
		this.z = float1;
		this.lz = float1;
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

	public float Hit(HandWeapon handWeapon, IsoGameCharacter gameCharacter, float float1, boolean boolean1, float float2) {
		return 0.0F;
	}

	public void Move(Vector2 vector2) {
		this.nx += vector2.x * GameTime.instance.getMultiplier();
		this.ny += vector2.y * GameTime.instance.getMultiplier();
		if (this instanceof IsoPlayer) {
			this.current = IsoWorld.instance.CurrentCell.getGridSquare((double)this.x, (double)this.y, (double)((int)this.z));
		}
	}

	public void MoveUnmodded(Vector2 vector2) {
		this.nx += vector2.x;
		this.ny += vector2.y;
		if (this instanceof IsoPlayer) {
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

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
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

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		byteBuffer.put((byte)(this.Serialize() ? 1 : 0));
		byteBuffer.put(IsoObject.factoryGetClassID(this.getObjectName()));
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
		MovingObjectUpdateScheduler.instance.removeObject(this);
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
		if (this.current != null && this.z - (float)((int)this.z) < 0.5F) {
			if (this.current.Has(IsoObjectType.tree) || this.current.getProperties() != null && this.current.getProperties().Is("Bush")) {
				if (boolean1) {
					this.doTreeNoises();
				}

				for (int int1 = 1; int1 < this.current.getObjects().size(); ++int1) {
					IsoObject object = (IsoObject)this.current.getObjects().get(int1);
					if (object instanceof IsoTree) {
						object.setRenderEffect(RenderEffectType.Vegetation_Rustle);
					} else if (object.getProperties() != null && object.getProperties().Is("Bush")) {
						object.setRenderEffect(RenderEffectType.Vegetation_Rustle);
					}
				}
			}

			IsoGridSquare square = this.getFeelerTile(this.feelersize);
			if (square != null && square != this.current && (square.Has(IsoObjectType.tree) || square.getProperties() != null && square.getProperties().Is("Bush"))) {
				if (boolean1) {
					this.doTreeNoises();
				}

				for (int int2 = 1; int2 < square.getObjects().size(); ++int2) {
					IsoObject object2 = (IsoObject)square.getObjects().get(int2);
					if (object2 instanceof IsoTree) {
						object2.setRenderEffect(RenderEffectType.Vegetation_Rustle);
					} else if (object2.getSprite() != null && object2.getProperties().Is("Bush")) {
						object2.setRenderEffect(RenderEffectType.Vegetation_Rustle);
					}
				}
			}
		}

		return this.current != null && this.current.HasStairs() ? 0.75F : 1.0F;
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
		IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(this, IsoGameCharacter.class);
		IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(this, IsoZombie.class);
		this.slideAwayFromWalls();
		if (zombie != null && GameServer.bServer && !zombie.isCurrentState(ZombieIdleState.instance())) {
			boolean boolean1 = false;
		}

		if (player != null && player.isLocalPlayer()) {
			IsoPlayer.setInstance(player);
			IsoCamera.CamCharacter = player;
		}

		this.ensureOnTile();
		if (this.lastTargettedBy != null && this.lastTargettedBy.isDead()) {
			this.lastTargettedBy = null;
		}

		if (this.lastTargettedBy != null && this.TimeSinceZombieAttack > 120) {
			this.lastTargettedBy = null;
		}

		++this.TimeSinceZombieAttack;
		if (player != null) {
			player.setLastCollidedW(this.collidedW);
			player.setLastCollidedN(this.collidedN);
		}

		if (!this.destroyed) {
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
			if (zombie != null && (int)this.z == 0 && this.getCurrentBuilding() == null && !this.isInLoadedArea((int)this.nx, (int)this.ny) && (zombie.isCurrentState(PathFindState.instance()) || zombie.isCurrentState(WalkTowardState.instance()))) {
				ZombiePopulationManager.instance.virtualizeZombie(zombie);
			} else {
				float float1 = this.nx;
				float float2 = this.ny;
				this.collidedWithVehicle = false;
				if (gameCharacter != null && !this.isOnFloor() && gameCharacter.getVehicle() == null && this.isCollidable() && (player == null || !player.isNoClip())) {
					int int1 = (int)this.x;
					int int2 = (int)this.y;
					int int3 = (int)this.nx;
					int int4 = (int)this.ny;
					int int5 = (int)this.z;
					if (gameCharacter.getCurrentState() == null || !gameCharacter.getCurrentState().isIgnoreCollide(gameCharacter, int1, int2, int5, int3, int4, int5)) {
						Vector2f vector2f = PolygonalMap2.instance.resolveCollision(gameCharacter, this.nx, this.ny, IsoMovingObject.L_postUpdate.vector2f);
						if (vector2f.x != this.nx || vector2f.y != this.ny) {
							this.nx = vector2f.x;
							this.ny = vector2f.y;
							this.collidedWithVehicle = true;
						}
					}
				}

				float float3 = this.nx;
				float float4 = this.ny;
				float float5 = 0.0F;
				boolean boolean2 = false;
				float float6;
				float float7;
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

					float5 = Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly);
					float7 = this.nx;
					float6 = this.ny;
					this.nx = float3;
					this.ny = float4;
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

						if (Math.abs(this.nx - this.lx) + Math.abs(this.ny - this.ly) < float5) {
							this.nx = float7;
							this.ny = float6;
						}
					}
				}

				if (this.collidedThisFrame) {
					this.current = this.last;
				}

				this.checkHitWall();
				if (player != null && !player.isCurrentState(CollideWithWallState.instance()) && !this.collidedN && !this.collidedS && !this.collidedW && !this.collidedE) {
					this.setCollideType((String)null);
				}

				float7 = this.nx - this.x;
				float6 = this.ny - this.y;
				float float8 = !(Math.abs(float7) > 0.0F) && !(Math.abs(float6) > 0.0F) ? 0.0F : this.getGlobalMovementMod();
				if (Math.abs(float7) > 0.01F || Math.abs(float6) > 0.01F) {
					float7 *= float8;
					float6 *= float8;
				}

				this.x += float7;
				this.y += float6;
				this.doStairs();
				this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, (int)this.z);
				if (this.current == null) {
					for (int int6 = (int)this.z; int6 >= 0; --int6) {
						this.current = this.getCell().getGridSquare((int)this.x, (int)this.y, int6);
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

	public void ensureOnTile() {
		if (this.current == null) {
			if (!(this instanceof IsoPlayer)) {
				if (this instanceof IsoSurvivor) {
					IsoWorld.instance.CurrentCell.Remove(this);
					IsoWorld.instance.CurrentCell.getSurvivorList().remove(this);
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
		this.bOutline[IsoCamera.frameState.playerIndex] = false;
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
		} else if (this.sprite == null) {
			return 1;
		} else {
			float float1 = IsoUtils.YToScreen(this.x, this.y, this.z, 0);
			float float2 = IsoUtils.YToScreen(movingObject.x, movingObject.y, movingObject.z, 0);
			if ((double)float1 > (double)float2) {
				return 1;
			} else {
				return (double)float1 < (double)float2 ? -1 : 0;
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

	public boolean isSolidForSeparate() {
		if (this instanceof IsoZombieGiblets) {
			return false;
		} else if (this.current == null) {
			return false;
		} else if (!this.solid) {
			return false;
		} else {
			return !this.isOnFloor();
		}
	}

	public boolean isPushableForSeparate() {
		return true;
	}

	public boolean isPushedByForSeparate(IsoMovingObject movingObject) {
		return true;
	}

	public void separate() {
		if (this.isSolidForSeparate()) {
			if (this.isPushableForSeparate()) {
				IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(this, IsoGameCharacter.class);
				IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
				if (this.z < 0.0F) {
					this.z = 0.0F;
				}

				for (int int1 = 0; int1 <= 8; ++int1) {
					IsoGridSquare square = int1 == 8 ? this.current : this.current.nav[int1];
					if (square != null && !square.getMovingObjects().isEmpty() && (square == this.current || !this.current.isBlockedTo(square))) {
						float float1 = player != null && player.getPrimaryHandItem() instanceof HandWeapon ? ((HandWeapon)player.getPrimaryHandItem()).getMaxRange() : 0.3F;
						int int2 = 0;
						for (int int3 = square.getMovingObjects().size(); int2 < int3; ++int2) {
							IsoMovingObject movingObject = (IsoMovingObject)square.getMovingObjects().get(int2);
							if (movingObject != this && movingObject.isSolidForSeparate() && !(Math.abs(this.z - movingObject.z) > 0.3F)) {
								IsoGameCharacter gameCharacter2 = (IsoGameCharacter)Type.tryCastTo(movingObject, IsoGameCharacter.class);
								IsoPlayer player2 = (IsoPlayer)Type.tryCastTo(movingObject, IsoPlayer.class);
								float float2 = this.width + movingObject.width;
								Vector2 vector2 = tempo;
								vector2.x = this.nx - movingObject.nx;
								vector2.y = this.ny - movingObject.ny;
								float float3 = vector2.getLength();
								if (gameCharacter == null || gameCharacter2 == null && !(movingObject instanceof BaseVehicle)) {
									if (float3 < float2) {
										CollisionManager.instance.AddContact(this, movingObject);
									}

									return;
								}

								if (gameCharacter2 != null) {
									if (player != null && player.getBumpedChr() != movingObject && float3 < float2 + float1 && (double)player.getForwardDirection().angleBetween(vector2) > 2.6179938155736564 && player.getBeenSprintingFor() >= 70.0F && WeaponType.getWeaponType((IsoGameCharacter)player) == WeaponType.spear) {
										player.reportEvent("ChargeSpearConnect");
										player.setAttackType("charge");
										player.attackStarted = true;
										player.setVariable("StartedAttackWhileSprinting", true);
										player.setBeenSprintingFor(0.0F);
										return;
									}

									if (!(float3 >= float2)) {
										boolean boolean1 = false;
										if (player != null && player.getVariableFloat("WalkSpeed", 0.0F) > 0.2F && player.runningTime > 0.5F && player.getBumpedChr() != movingObject) {
											boolean1 = true;
										}

										if (GameClient.bClient && player != null && gameCharacter2 instanceof IsoPlayer && !ServerOptions.getInstance().PlayerBumpPlayer.getValue()) {
											boolean1 = false;
										}

										if (boolean1 && !"charge".equals(player.getAttackType())) {
											boolean boolean2 = !this.isOnFloor() && (gameCharacter.getBumpedChr() != null || (System.currentTimeMillis() - player.getLastBump()) / 100L < 15L || player.isSprinting()) && (player2 == null || !player2.isNPC());
											if (boolean2) {
												++gameCharacter.bumpNbr;
												int int4 = 10 - gameCharacter.bumpNbr * 3;
												int4 += gameCharacter.getPerkLevel(PerkFactory.Perks.Fitness);
												int4 += gameCharacter.getPerkLevel(PerkFactory.Perks.Strength);
												if (gameCharacter.Traits.Clumsy.isSet()) {
													int4 -= 5;
												}

												if (gameCharacter.Traits.Graceful.isSet()) {
													int4 += 5;
												}

												if (gameCharacter.Traits.VeryUnderweight.isSet()) {
													int4 -= 8;
												}

												if (gameCharacter.Traits.Underweight.isSet()) {
													int4 -= 4;
												}

												if (gameCharacter.Traits.Obese.isSet()) {
													int4 -= 8;
												}

												if (gameCharacter.Traits.Overweight.isSet()) {
													int4 -= 4;
												}

												BodyPart bodyPart = gameCharacter.getBodyDamage().getBodyPart(BodyPartType.Torso_Lower);
												if (bodyPart.getAdditionalPain(true) > 20.0F) {
													int4 = (int)((float)int4 - (bodyPart.getAdditionalPain(true) - 20.0F) / 20.0F);
												}

												int4 = Math.min(80, int4);
												int4 = Math.max(1, int4);
												if (Rand.Next(int4) == 0 || gameCharacter.isSprinting()) {
													gameCharacter.setVariable("BumpDone", false);
													gameCharacter.setBumpFall(true);
													gameCharacter.setVariable("TripObstacleType", "zombie");
												}
											} else {
												gameCharacter.bumpNbr = 0;
											}

											gameCharacter.setLastBump(System.currentTimeMillis());
											gameCharacter.setBumpedChr(gameCharacter2);
											gameCharacter.setBumpType(this.getBumpedType(gameCharacter2));
											boolean boolean3 = gameCharacter.isBehind(gameCharacter2);
											String string = gameCharacter.getBumpType();
											if (boolean3) {
												if (string.equals("left")) {
													string = "right";
												} else {
													string = "left";
												}
											}

											gameCharacter2.setBumpType(string);
											gameCharacter2.setHitFromBehind(boolean3);
											if (boolean2 | GameClient.bClient) {
												gameCharacter.actionContext.reportEvent("wasBumped");
											}
										}

										if (GameServer.bServer || this.distToNearestCamCharacter() < 60.0F) {
											if (this.isPushedByForSeparate(movingObject)) {
												vector2.setLength((float3 - float2) / 8.0F);
												this.nx -= vector2.x;
												this.ny -= vector2.y;
											}

											this.collideWith(movingObject);
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

	public String getBumpedType(IsoGameCharacter gameCharacter) {
		float float1 = this.x - gameCharacter.x;
		float float2 = this.y - gameCharacter.y;
		String string = "left";
		if (this.dir == IsoDirections.S || this.dir == IsoDirections.SE || this.dir == IsoDirections.SW) {
			if (float1 < 0.0F) {
				string = "left";
			} else {
				string = "right";
			}
		}

		if (this.dir == IsoDirections.N || this.dir == IsoDirections.NE || this.dir == IsoDirections.NW) {
			if (float1 > 0.0F) {
				string = "left";
			} else {
				string = "right";
			}
		}

		if (this.dir == IsoDirections.E) {
			if (float2 > 0.0F) {
				string = "left";
			} else {
				string = "right";
			}
		}

		if (this.dir == IsoDirections.W) {
			if (float2 < 0.0F) {
				string = "left";
			} else {
				string = "right";
			}
		}

		return string;
	}

	private void slideAwayFromWalls() {
		if (this.current != null) {
			IsoZombie zombie = (IsoZombie)Type.tryCastTo(this, IsoZombie.class);
			if (zombie != null && (this.isOnFloor() || zombie.isKnockedDown())) {
				if (!zombie.isCrawling() || zombie.getPath2() == null && !zombie.isMoving()) {
					if (!zombie.isCurrentState(ClimbOverFenceState.instance()) && !zombie.isCurrentState(ClimbThroughWindowState.instance())) {
						if (zombie.hasAnimationPlayer() && zombie.getAnimationPlayer().isReady()) {
							Vector3 vector3 = IsoMovingObject.L_slideAwayFromWalls.vector3;
							Model.BoneToWorldCoords((IsoGameCharacter)zombie, zombie.getAnimationPlayer().getSkinningBoneIndex("Bip01_Head", -1), vector3);
							if (Core.bDebug && DebugOptions.instance.CollideWithObstaclesRenderRadius.getValue()) {
								LineDrawer.DrawIsoCircle(vector3.x, vector3.y, this.z, 0.3F, 16, 1.0F, 1.0F, 0.0F, 1.0F);
							}

							Vector2 vector2 = IsoMovingObject.L_slideAwayFromWalls.vector2.set(vector3.x - this.x, vector3.y - this.y);
							vector2.normalize();
							vector3.x += vector2.x * 0.3F;
							vector3.y += vector2.y * 0.3F;
							float float1;
							if (zombie.isKnockedDown() && (zombie.isCurrentState(ZombieFallDownState.instance()) || zombie.isCurrentState(StaggerBackState.instance()))) {
								Vector2f vector2f = PolygonalMap2.instance.resolveCollision(zombie, vector3.x, vector3.y, IsoMovingObject.L_slideAwayFromWalls.vector2f);
								if (vector2f.x != vector3.x || vector2f.y != vector3.y) {
									float1 = GameTime.getInstance().getMultiplier() / 5.0F;
									this.nx += (vector2f.x - vector3.x) * float1;
									this.ny += (vector2f.y - vector3.y) * float1;
									return;
								}
							}

							if ((int)vector3.x != this.current.x || (int)vector3.y != this.current.y) {
								IsoGridSquare square = this.getCell().getGridSquare((int)vector3.x, (int)vector3.y, (int)this.z);
								if (square != null) {
									if (this.current.testCollideAdjacent(this, square.x - this.current.x, square.y - this.current.y, 0)) {
										float1 = GameTime.getInstance().getMultiplier() / 5.0F;
										if (square.x < this.current.x) {
											this.nx += ((float)this.current.x - vector3.x) * float1;
										} else if (square.x > this.current.x) {
											this.nx += ((float)square.x - vector3.x) * float1;
										}

										if (square.y < this.current.y) {
											this.ny += ((float)this.current.y - vector3.y) * float1;
										} else if (square.y > this.current.y) {
											this.ny += ((float)square.y - vector3.y) * float1;
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
		IsoGameCharacter gameCharacter = (IsoGameCharacter)Type.tryCastTo(this, IsoGameCharacter.class);
		this.current = this.getCell().getGridSquare((int)this.nx, (int)this.ny, (int)this.z);
		int int2;
		int int3;
		int int4;
		if (this instanceof IsoMolotovCocktail) {
			for (int2 = (int)this.z; int2 > 0; --int2) {
				for (int3 = -1; int3 <= 1; ++int3) {
					for (int4 = -1; int4 <= 1; ++int4) {
						IsoGridSquare square = this.getCell().createNewGridSquare((int)this.nx + int4, (int)this.ny + int3, int2, false);
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
			if (gameCharacter != null && gameCharacter.getCurrentState() != null && gameCharacter.getCurrentState().isIgnoreCollide(gameCharacter, this.last.x, this.last.y, this.last.z, this.current.x, this.current.y, this.current.z)) {
				return false;
			}

			if (this == IsoCamera.CamCharacter) {
				IsoWorld.instance.CurrentCell.lightUpdateCount = 10;
			}

			int2 = this.current.getX() - this.last.getX();
			int3 = this.current.getY() - this.last.getY();
			int4 = this.current.getZ() - this.last.getZ();
			boolean boolean1 = false;
			if (this.last.testCollideAdjacent(this, int2, int3, int4) || this.current == null) {
				boolean1 = true;
			}

			if (boolean1) {
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

				this.current = this.last;
				this.checkBreakHoppable();
				this.checkHitHoppable();
				if (int1 == 2) {
					if ((this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
						this.collidedS = false;
						this.collidedN = false;
					}
				} else if (int1 == 1 && (this.collidedS || this.collidedN) && (this.collidedE || this.collidedW)) {
					this.collidedW = false;
					this.collidedE = false;
				}

				this.Collided();
				return true;
			}
		} else if (this.nx != this.lx || this.ny != this.ly) {
			if (this instanceof IsoZombie && Core.GameMode.equals("Tutorial")) {
				return true;
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

			if (gameCharacter != null && gameCharacter.getPath2() != null) {
				PathFindBehavior2 pathFindBehavior2 = gameCharacter.getPathFindBehavior2();
				if ((int)pathFindBehavior2.getTargetX() == (int)this.x && (int)pathFindBehavior2.getTargetY() == (int)this.y && (int)pathFindBehavior2.getTargetZ() == (int)this.z) {
					return false;
				}
			}

			IsoGridSquare square2 = this.getFeelerTile(this.feelersize);
			if (gameCharacter != null) {
				if (gameCharacter.isClimbing()) {
					square2 = this.current;
				}

				if (square2 != null && square2 != this.current && gameCharacter.getPath2() != null && !gameCharacter.getPath2().crossesSquare(square2.x, square2.y, square2.z)) {
					square2 = this.current;
				}
			}

			if (square2 != null && square2 != this.current && this.current != null) {
				if (gameCharacter != null && gameCharacter.getCurrentState() != null && gameCharacter.getCurrentState().isIgnoreCollide(gameCharacter, this.current.x, this.current.y, this.current.z, square2.x, square2.y, square2.z)) {
					return false;
				}

				if (this.current.testCollideAdjacent(this, square2.getX() - this.current.getX(), square2.getY() - this.current.getY(), square2.getZ() - this.current.getZ())) {
					if (this.last != null) {
						if (this.current.getX() < square2.getX()) {
							this.collidedE = true;
						}

						if (this.current.getX() > square2.getX()) {
							this.collidedW = true;
						}

						if (this.current.getY() < square2.getY()) {
							this.collidedS = true;
						}

						if (this.current.getY() > square2.getY()) {
							this.collidedN = true;
						}

						this.checkBreakHoppable();
						this.checkHitHoppable();
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

	private void checkHitHoppable() {
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(this, IsoZombie.class);
		if (zombie != null && !zombie.bCrawling) {
			if (!zombie.isCurrentState(AttackState.instance()) && !zombie.isCurrentState(StaggerBackState.instance()) && !zombie.isCurrentState(ClimbOverFenceState.instance()) && !zombie.isCurrentState(ClimbThroughWindowState.instance())) {
				if (this.collidedW && !this.collidedN && !this.collidedS && this.last.Is(IsoFlagType.HoppableW)) {
					zombie.climbOverFence(IsoDirections.W);
				}

				if (this.collidedN && !this.collidedE && !this.collidedW && this.last.Is(IsoFlagType.HoppableN)) {
					zombie.climbOverFence(IsoDirections.N);
				}

				IsoGridSquare square;
				if (this.collidedS && !this.collidedE && !this.collidedW) {
					square = this.last.nav[IsoDirections.S.index()];
					if (square != null && square.Is(IsoFlagType.HoppableN)) {
						zombie.climbOverFence(IsoDirections.S);
					}
				}

				if (this.collidedE && !this.collidedN && !this.collidedS) {
					square = this.last.nav[IsoDirections.E.index()];
					if (square != null && square.Is(IsoFlagType.HoppableW)) {
						zombie.climbOverFence(IsoDirections.E);
					}
				}
			}
		}
	}

	private void checkBreakHoppable() {
		IsoZombie zombie = (IsoZombie)Type.tryCastTo(this, IsoZombie.class);
		if (zombie != null && zombie.bCrawling) {
			if (!zombie.isCurrentState(AttackState.instance()) && !zombie.isCurrentState(StaggerBackState.instance()) && !zombie.isCurrentState(CrawlingZombieTurnState.instance())) {
				IsoDirections directions = IsoDirections.Max;
				if (this.collidedW && !this.collidedN && !this.collidedS) {
					directions = IsoDirections.W;
				}

				if (this.collidedN && !this.collidedE && !this.collidedW) {
					directions = IsoDirections.N;
				}

				if (this.collidedS && !this.collidedE && !this.collidedW) {
					directions = IsoDirections.S;
				}

				if (this.collidedE && !this.collidedN && !this.collidedS) {
					directions = IsoDirections.E;
				}

				if (directions != IsoDirections.Max) {
					IsoObject object = this.last.getHoppableTo(this.last.getAdjacentSquare(directions));
					IsoThumpable thumpable = (IsoThumpable)Type.tryCastTo(object, IsoThumpable.class);
					if (thumpable != null && !thumpable.isThumpable()) {
						zombie.setThumpTarget(thumpable);
					} else if (object != null && object.getThumpableFor(zombie) != null) {
						zombie.setThumpTarget(object);
					}
				}
			}
		}
	}

	private void checkHitWall() {
		if (this.collidedN || this.collidedS || this.collidedE || this.collidedW) {
			if (this.current != null) {
				IsoPlayer player = (IsoPlayer)Type.tryCastTo(this, IsoPlayer.class);
				if (player != null) {
					if (StringUtils.isNullOrEmpty(this.getCollideType())) {
						boolean boolean1 = false;
						int int1 = this.current.getWallType();
						if ((int1 & 1) != 0 && this.collidedN && this.getDir() == IsoDirections.N) {
							boolean1 = true;
						}

						if ((int1 & 2) != 0 && this.collidedS && this.getDir() == IsoDirections.S) {
							boolean1 = true;
						}

						if ((int1 & 4) != 0 && this.collidedW && this.getDir() == IsoDirections.W) {
							boolean1 = true;
						}

						if ((int1 & 8) != 0 && this.collidedE && this.getDir() == IsoDirections.E) {
							boolean1 = true;
						}

						if (this.checkVaultOver()) {
							boolean1 = false;
						}

						if (boolean1 && player.isSprinting() && player.isLocalPlayer()) {
							this.setCollideType("wall");
							player.getActionContext().reportEvent("collideWithWall");
							this.lastCollideTime = 70.0F;
						}
					}
				}
			}
		}
	}

	private boolean checkVaultOver() {
		IsoPlayer player = (IsoPlayer)this;
		if (!player.isCurrentState(ClimbOverFenceState.instance()) && !player.isIgnoreAutoVault()) {
			if (!player.IsRunning() && !player.isSprinting() && player.isLocalPlayer()) {
				return false;
			} else {
				IsoDirections directions = this.getDir();
				IsoGridSquare square = this.current.getAdjacentSquare(IsoDirections.SE);
				if (directions == IsoDirections.SE && square != null && square.Is(IsoFlagType.HoppableN) && square.Is(IsoFlagType.HoppableW)) {
					return false;
				} else {
					IsoGridSquare square2 = this.current;
					if (this.collidedS) {
						square2 = this.current.getAdjacentSquare(IsoDirections.S);
					} else if (this.collidedE) {
						square2 = this.current.getAdjacentSquare(IsoDirections.E);
					}

					if (square2 == null) {
						return false;
					} else {
						boolean boolean1 = false;
						if (this.current.getProperties().Is(IsoFlagType.HoppableN) && this.collidedN && !this.collidedW && !this.collidedE && (directions == IsoDirections.NW || directions == IsoDirections.N || directions == IsoDirections.NE)) {
							directions = IsoDirections.N;
							boolean1 = true;
						}

						if (square2.getProperties().Is(IsoFlagType.HoppableN) && this.collidedS && !this.collidedW && !this.collidedE && (directions == IsoDirections.SW || directions == IsoDirections.S || directions == IsoDirections.SE)) {
							directions = IsoDirections.S;
							boolean1 = true;
						}

						if (this.current.getProperties().Is(IsoFlagType.HoppableW) && this.collidedW && !this.collidedN && !this.collidedS && (directions == IsoDirections.NW || directions == IsoDirections.W || directions == IsoDirections.SW)) {
							directions = IsoDirections.W;
							boolean1 = true;
						}

						if (square2.getProperties().Is(IsoFlagType.HoppableW) && this.collidedE && !this.collidedN && !this.collidedS && (directions == IsoDirections.NE || directions == IsoDirections.E || directions == IsoDirections.SE)) {
							directions = IsoDirections.E;
							boolean1 = true;
						}

						if (boolean1 && player.isSafeToClimbOver(directions)) {
							ClimbOverFenceState.instance().setParams(player, directions);
							player.getActionContext().reportEvent("EventClimbFence");
							return true;
						} else {
							return false;
						}
					}
				}
			}
		} else {
			return false;
		}
	}

	public void setMovingSquareNow() {
		if (this.movingSq != null) {
			this.movingSq.getMovingObjects().remove(this);
			this.movingSq = null;
		}

		if (this.current != null && !this.current.getMovingObjects().contains(this)) {
			this.current.getMovingObjects().add(this);
			this.movingSq = this.current;
		}
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

	public boolean getNoDamage() {
		return this.noDamage;
	}

	public void setNoDamage(boolean boolean1) {
		this.noDamage = boolean1;
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

	public float getWidth() {
		return this.width;
	}

	public void setWidth(float float1) {
		this.width = float1;
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

	public Vector2 getMovementLastFrame() {
		return this.movementLastFrame;
	}

	public void setMovementLastFrame(Vector2 vector2) {
		this.movementLastFrame = vector2;
	}

	public float getFeelersize() {
		return this.feelersize;
	}

	public void setFeelersize(float float1) {
		this.feelersize = float1;
	}

	public byte canHaveMultipleHits() {
		byte byte1 = 0;
		ArrayList arrayList = IsoWorld.instance.CurrentCell.getObjectList();
		for (int int1 = 0; int1 < arrayList.size(); ++int1) {
			IsoMovingObject movingObject = (IsoMovingObject)arrayList.get(int1);
			IsoPlayer player = (IsoPlayer)Type.tryCastTo(movingObject, IsoPlayer.class);
			if (player != null) {
				HandWeapon handWeapon = (HandWeapon)Type.tryCastTo(player.getPrimaryHandItem(), HandWeapon.class);
				if (handWeapon == null || player.bDoShove || player.isForceShove()) {
					handWeapon = player.bareHands;
				}

				float float1 = IsoUtils.DistanceTo(player.x, player.y, this.x, this.y);
				float float2 = handWeapon.getMaxRange() * handWeapon.getRangeMod(player) + 2.0F;
				if (!(float1 > float2)) {
					float float3 = player.getDotWithForwardDirection(this.x, this.y);
					if (!((double)float1 > 2.5) || !(float3 < 0.1F)) {
						LosUtil.TestResults testResults = LosUtil.lineClear(player.getCell(), (int)player.getX(), (int)player.getY(), (int)player.getZ(), (int)this.getX(), (int)this.getY(), (int)this.getZ(), false);
						if (testResults != LosUtil.TestResults.Blocked && testResults != LosUtil.TestResults.ClearThroughClosedDoor) {
							++byte1;
							if (byte1 >= 2) {
								return byte1;
							}
						}
					}
				}
			}
		}

		return byte1;
	}

	public boolean isOnFloor() {
		return this.bOnFloor;
	}

	public void setOnFloor(boolean boolean1) {
		this.bOnFloor = boolean1;
	}

	public void Despawn() {
	}

	public boolean isCloseKilled() {
		return this.closeKilled;
	}

	public void setCloseKilled(boolean boolean1) {
		this.closeKilled = boolean1;
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
				if (int1 >= serverCell.WX * 50 && int1 < (serverCell.WX + 1) * 50 && int2 >= serverCell.WY * 50 && int2 < (serverCell.WY + 1) * 50) {
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

	public boolean isCollided() {
		return !StringUtils.isNullOrWhitespace(this.getCollideType());
	}

	public String getCollideType() {
		return this.collideType;
	}

	public void setCollideType(String string) {
		this.collideType = string;
	}

	public float getLastCollideTime() {
		return this.lastCollideTime;
	}

	public void setLastCollideTime(float float1) {
		this.lastCollideTime = float1;
	}

	public ArrayList getEatingZombies() {
		return this.eatingZombies;
	}

	public void setEatingZombies(ArrayList arrayList) {
		this.eatingZombies.clear();
		this.eatingZombies.addAll(arrayList);
	}

	public boolean isEatingOther(IsoMovingObject movingObject) {
		return movingObject == null ? false : movingObject.eatingZombies.contains(this);
	}

	public float getDistanceSq(IsoMovingObject movingObject) {
		float float1 = this.x - movingObject.x;
		float float2 = this.y - movingObject.y;
		float1 *= float1;
		float2 *= float2;
		return float1 + float2;
	}

	public void setZombiesDontAttack(boolean boolean1) {
		this.zombiesDontAttack = boolean1;
	}

	public boolean isZombiesDontAttack() {
		return this.zombiesDontAttack;
	}

	public static class TreeSoundManager {
		private ArrayList squares = new ArrayList();
		private long[] soundTime = new long[6];
		private Comparator comp = (var1,var2)->{
    float var3 = this.getClosestListener((float)var1.x + 0.5F, (float)var1.y + 0.5F, (float)var1.z);
    float var4 = this.getClosestListener((float)var2.x + 0.5F, (float)var2.y + 0.5F, (float)var2.z);
    if (var3 > var4) {
        return 1;
    } else {
        return var3 < var4 ? -1 : 0;
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
					if (player.Traits.HardOfHearing.isSet()) {
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

	private static final class L_postUpdate {
		static final Vector2f vector2f = new Vector2f();
	}

	private static final class L_slideAwayFromWalls {
		static final Vector2f vector2f = new Vector2f();
		static final Vector2 vector2 = new Vector2();
		static final Vector3 vector3 = new Vector3();
	}
}
