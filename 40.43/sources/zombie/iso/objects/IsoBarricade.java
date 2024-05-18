package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SoundManager;
import zombie.WorldSoundManager;
import zombie.ai.states.ThumpState;
import zombie.characters.IsoGameCharacter;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.textures.ColorInfo;
import zombie.inventory.InventoryItem;
import zombie.inventory.InventoryItemFactory;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCamera;
import zombie.iso.IsoCell;
import zombie.iso.IsoDirections;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.LosUtil;
import zombie.iso.Vector2;
import zombie.iso.SpriteDetails.IsoObjectType;
import zombie.iso.objects.interfaces.BarricadeAble;
import zombie.iso.objects.interfaces.Thumpable;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameClient;
import zombie.network.GameServer;


public class IsoBarricade extends IsoObject implements Thumpable {
	public static final int MAX_PLANKS = 4;
	public static final int PLANK_HEALTH = 1000;
	private int[] plankHealth = new int[4];
	public static final int METAL_HEALTH = 5000;
	public static final int METAL_HEALTH_DAMAGED = 2500;
	private int metalHealth;
	public static final int METAL_BAR_HEALTH = 3000;
	private int metalBarHealth;

	public IsoBarricade(IsoCell cell) {
		super(cell);
	}

	public IsoBarricade(IsoCell cell, IsoGridSquare square, IsoDirections directions) {
		this.square = square;
		this.dir = directions;
	}

	public String getObjectName() {
		return "Barricade";
	}

	public void addPlank(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (this.canAddPlank()) {
			int int1 = 1000;
			if (inventoryItem != null) {
				int1 = (int)((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax() * 1000.0F);
			}

			if (gameCharacter != null) {
				int1 = (int)((float)int1 * gameCharacter.getBarricadeStrengthMod());
			}

			int int2;
			for (int2 = 0; int2 < 4; ++int2) {
				if (this.plankHealth[int2] <= 0) {
					this.plankHealth[int2] = int1;
					break;
				}
			}

			this.chooseSprite();
			if (!GameServer.bServer) {
				for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					LosUtil.cachecleared[int2] = true;
				}

				IsoGridSquare.setRecalcLightTime(-1);
				GameTime.instance.lightSourceUpdate = 100.0F;
			}

			if (this.square != null) {
				this.square.RecalcProperties();
			}
		}
	}

	public InventoryItem removePlank(IsoGameCharacter gameCharacter) {
		if (this.getNumPlanks() <= 0) {
			return null;
		} else {
			InventoryItem inventoryItem = null;
			int int1;
			for (int1 = 3; int1 >= 0; --int1) {
				if (this.plankHealth[int1] > 0) {
					float float1 = Math.min((float)this.plankHealth[int1] / 1000.0F, 1.0F);
					inventoryItem = InventoryItemFactory.CreateItem("Base.Plank");
					inventoryItem.setCondition((int)Math.max((float)inventoryItem.getConditionMax() * float1, 1.0F));
					this.plankHealth[int1] = 0;
					break;
				}
			}

			if (this.getNumPlanks() <= 0) {
				if (this.square != null) {
					if (GameServer.bServer) {
						this.square.transmitRemoveItemFromSquare(this);
					} else {
						this.square.RemoveTileObject(this);
					}
				}
			} else {
				this.chooseSprite();
				if (!GameServer.bServer) {
					for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						LosUtil.cachecleared[int1] = true;
					}

					IsoGridSquare.setRecalcLightTime(-1);
					GameTime.instance.lightSourceUpdate = 100.0F;
				}

				if (this.square != null) {
					this.square.RecalcProperties();
				}
			}

			return inventoryItem;
		}
	}

	public int getNumPlanks() {
		int int1 = 0;
		for (int int2 = 0; int2 < 4; ++int2) {
			if (this.plankHealth[int2] > 0) {
				++int1;
			}
		}

		return int1;
	}

	public boolean canAddPlank() {
		return !this.isMetal() && this.getNumPlanks() < 4 && !this.isMetalBar();
	}

	public void addMetalBar(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (this.getNumPlanks() <= 0) {
			if (this.metalHealth <= 0) {
				if (this.metalBarHealth <= 0) {
					this.metalBarHealth = 3000;
					if (inventoryItem != null) {
						this.metalBarHealth = (int)((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax() * 5000.0F);
					}

					if (gameCharacter != null) {
						this.metalBarHealth = (int)((float)this.metalBarHealth * gameCharacter.getMetalBarricadeStrengthMod());
					}

					this.chooseSprite();
					if (!GameServer.bServer) {
						for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
							LosUtil.cachecleared[int1] = true;
						}

						IsoGridSquare.setRecalcLightTime(-1);
						GameTime.instance.lightSourceUpdate = 100.0F;
					}

					if (this.square != null) {
						this.square.RecalcProperties();
					}
				}
			}
		}
	}

	public InventoryItem removeMetalBar(IsoGameCharacter gameCharacter) {
		if (this.metalBarHealth <= 0) {
			return null;
		} else {
			float float1 = Math.min((float)this.metalBarHealth / 3000.0F, 1.0F);
			this.metalBarHealth = 0;
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.MetalBar");
			inventoryItem.setCondition((int)Math.max((float)inventoryItem.getConditionMax() * float1, 1.0F));
			if (this.square != null) {
				if (GameServer.bServer) {
					this.square.transmitRemoveItemFromSquare(this);
				} else {
					this.square.RemoveTileObject(this);
				}
			}

			return inventoryItem;
		}
	}

	public void addMetal(IsoGameCharacter gameCharacter, InventoryItem inventoryItem) {
		if (this.getNumPlanks() <= 0) {
			if (this.metalHealth <= 0) {
				this.metalHealth = 5000;
				if (inventoryItem != null) {
					this.metalHealth = (int)((float)inventoryItem.getCondition() / (float)inventoryItem.getConditionMax() * 5000.0F);
				}

				if (gameCharacter != null) {
					this.metalHealth = (int)((float)this.metalHealth * gameCharacter.getMetalBarricadeStrengthMod());
				}

				this.chooseSprite();
				if (!GameServer.bServer) {
					for (int int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
						LosUtil.cachecleared[int1] = true;
					}

					IsoGridSquare.setRecalcLightTime(-1);
					GameTime.instance.lightSourceUpdate = 100.0F;
				}

				if (this.square != null) {
					this.square.RecalcProperties();
				}
			}
		}
	}

	public boolean isMetalBar() {
		return this.metalBarHealth > 0;
	}

	public InventoryItem removeMetal(IsoGameCharacter gameCharacter) {
		if (this.metalHealth <= 0) {
			return null;
		} else {
			float float1 = Math.min((float)this.metalHealth / 5000.0F, 1.0F);
			this.metalHealth = 0;
			InventoryItem inventoryItem = InventoryItemFactory.CreateItem("Base.SheetMetal");
			inventoryItem.setCondition((int)Math.max((float)inventoryItem.getConditionMax() * float1, 1.0F));
			if (this.square != null) {
				if (GameServer.bServer) {
					this.square.transmitRemoveItemFromSquare(this);
				} else {
					this.square.RemoveTileObject(this);
				}
			}

			return inventoryItem;
		}
	}

	public boolean isMetal() {
		return this.metalHealth > 0;
	}

	public boolean isBlockVision() {
		return this.isMetal() || this.getNumPlanks() > 2;
	}

	private void chooseSprite() {
		IsoSpriteManager spriteManager = IsoWorld.instance.spriteManager;
		int int1;
		String string;
		if (this.metalHealth > 0) {
			int1 = this.metalHealth <= 2500 ? 2 : 0;
			string = "constructedobjects_01";
			switch (this.dir) {
			case W: 
				this.sprite = spriteManager.getSprite(string + "_" + (24 + int1));
				break;
			
			case N: 
				this.sprite = spriteManager.getSprite(string + "_" + (25 + int1));
				break;
			
			case E: 
				this.sprite = spriteManager.getSprite(string + "_" + (28 + int1));
				break;
			
			case S: 
				this.sprite = spriteManager.getSprite(string + "_" + (29 + int1));
				break;
			
			default: 
				this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
			
			}
		} else if (this.metalBarHealth > 0) {
			String string2 = "constructedobjects_01";
			switch (this.dir) {
			case W: 
				this.sprite = spriteManager.getSprite(string2 + "_" + 55);
				break;
			
			case N: 
				this.sprite = spriteManager.getSprite(string2 + "_" + 53);
				break;
			
			case E: 
				this.sprite = spriteManager.getSprite(string2 + "_" + 52);
				break;
			
			case S: 
				this.sprite = spriteManager.getSprite(string2 + "_" + 54);
				break;
			
			default: 
				this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
			
			}
		} else {
			int1 = this.getNumPlanks();
			if (int1 <= 0) {
				this.sprite = spriteManager.getSprite("media/ui/missing-tile.png");
			} else {
				string = "carpentry_01";
				switch (this.dir) {
				case W: 
					this.sprite = spriteManager.getSprite(string + "_" + (8 + (int1 - 1) * 2));
					break;
				
				case N: 
					this.sprite = spriteManager.getSprite(string + "_" + (9 + (int1 - 1) * 2));
					break;
				
				case E: 
					this.sprite = spriteManager.getSprite(string + "_" + (0 + (int1 - 1) * 2));
					break;
				
				case S: 
					this.sprite = spriteManager.getSprite(string + "_" + (1 + (int1 - 1) * 2));
					break;
				
				default: 
					this.sprite.LoadFramesNoDirPageSimple("media/ui/missing-tile.png");
				
				}
			}
		}
	}

	public boolean isDestroyed() {
		return this.metalHealth <= 0 && this.getNumPlanks() <= 0 && this.metalBarHealth <= 0;
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square, IsoGridSquare square2) {
		return false;
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		if (this.metalHealth <= 0 && this.getNumPlanks() <= 2) {
			return IsoObject.VisionResult.NoEffect;
		} else {
			if (square == this.square) {
				if (this.dir == IsoDirections.N && square2.getY() < square.getY()) {
					return IsoObject.VisionResult.Blocked;
				}

				if (this.dir == IsoDirections.S && square2.getY() > square.getY()) {
					return IsoObject.VisionResult.Blocked;
				}

				if (this.dir == IsoDirections.W && square2.getX() < square.getX()) {
					return IsoObject.VisionResult.Blocked;
				}

				if (this.dir == IsoDirections.E && square2.getX() > square.getX()) {
					return IsoObject.VisionResult.Blocked;
				}
			} else if (square2 == this.square && square != this.square) {
				return this.TestVision(square2, square);
			}

			return IsoObject.VisionResult.NoEffect;
		}
	}

	public void Thump(IsoMovingObject movingObject) {
		if (!this.isDestroyed()) {
			if (movingObject instanceof IsoZombie) {
				int int1 = this.getNumPlanks();
				boolean boolean1 = this.metalHealth > 2500;
				int int2 = ThumpState.getFastForwardDamageMultiplier();
				this.Damage(((IsoZombie)movingObject).strength * int2);
				if (int1 != this.getNumPlanks()) {
					((IsoGameCharacter)((IsoGameCharacter)movingObject)).getEmitter().playSound("BreakBarricadePlank");
					if (GameServer.bServer) {
						GameServer.PlayWorldSoundServer("BreakBarricadePlank", false, movingObject.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
					}
				}

				if (this.isDestroyed()) {
					this.square.transmitRemoveItemFromSquare(this);
					if (!GameServer.bServer) {
						this.square.RemoveTileObject(this);
					}
				} else if ((int1 != this.getNumPlanks() || boolean1 && this.metalHealth < 2500) && GameServer.bServer) {
					this.sendObjectChange("state");
				}

				WorldSoundManager.instance.addSound(movingObject, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, true, 4.0F, 15.0F);
			}
		}
	}

	public Vector2 getFacingPosition(Vector2 vector2) {
		if (this.square == null) {
			return vector2.set(0.0F, 0.0F);
		} else if (this.dir == IsoDirections.N) {
			return vector2.set(this.getX() + 0.5F, this.getY());
		} else if (this.dir == IsoDirections.S) {
			return vector2.set(this.getX() + 0.5F, this.getY() + 1.0F);
		} else if (this.dir == IsoDirections.W) {
			return vector2.set(this.getX(), this.getY() + 0.5F);
		} else {
			return this.dir == IsoDirections.E ? vector2.set(this.getX() + 1.0F, this.getY() + 0.5F) : vector2.set(this.getX(), this.getY() + 0.5F);
		}
	}

	public void WeaponHit(IsoGameCharacter gameCharacter, HandWeapon handWeapon) {
		if (GameClient.bClient) {
			if (gameCharacter instanceof IsoPlayer) {
				GameClient.instance.sendWeaponHit((IsoPlayer)gameCharacter, handWeapon, this);
			}
		} else {
			String string = !this.isMetal() && !this.isMetalBar() ? "HitBarricadePlank" : "HitBarricadeMetal";
			SoundManager.instance.PlayWorldSound(string, false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
			if (GameServer.bServer) {
				GameServer.PlayWorldSoundServer(string, false, this.getSquare(), 1.0F, 20.0F, 2.0F, false);
			}

			if (handWeapon != null) {
				this.Damage(handWeapon.getDoorDamage() * 5);
			} else {
				this.Damage(100);
			}

			WorldSoundManager.instance.addSound(gameCharacter, this.square.getX(), this.square.getY(), this.square.getZ(), 20, 20, false, 0.0F, 15.0F);
			if (this.isDestroyed()) {
				if (gameCharacter != null) {
					String string2 = !this.isMetal() && !this.isMetalBar() ? "BreakBarricadePlank" : "BreakBarricadeMetal";
					gameCharacter.getEmitter().playSound(string2);
					if (GameServer.bServer) {
						GameServer.PlayWorldSoundServer(string2, false, gameCharacter.getCurrentSquare(), 0.2F, 20.0F, 1.1F, true);
					}
				}

				this.square.transmitRemoveItemFromSquare(this);
				if (!GameServer.bServer) {
					this.square.RemoveTileObject(this);
				}
			}
		}
	}

	public void Damage(int int1) {
		if (this.metalHealth > 0) {
			this.metalHealth -= int1;
			if (this.metalHealth <= 0) {
				this.metalHealth = 0;
				this.chooseSprite();
			}
		} else if (this.metalBarHealth > 0) {
			this.metalBarHealth -= int1;
			if (this.metalBarHealth <= 0) {
				this.metalBarHealth = 0;
				this.chooseSprite();
			}
		} else {
			for (int int2 = 3; int2 >= 0; --int2) {
				if (this.plankHealth[int2] > 0) {
					int[] intArray = this.plankHealth;
					intArray[int2] -= int1;
					if (this.plankHealth[int2] <= 0) {
						this.plankHealth[int2] = 0;
						this.chooseSprite();
					}

					break;
				}
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1) throws IOException {
		byte byte1 = byteBuffer.get();
		this.dir = IsoDirections.fromIndex(byte1);
		byte byte2 = byteBuffer.get();
		for (int int2 = 0; int2 < byte2; ++int2) {
			short short1 = byteBuffer.getShort();
			if (int2 < 4) {
				this.plankHealth[int2] = short1;
			}
		}

		this.metalHealth = byteBuffer.getShort();
		if (int1 >= 90) {
			this.metalBarHealth = byteBuffer.getShort();
		}

		this.chooseSprite();
	}

	public void save(ByteBuffer byteBuffer) throws IOException {
		byteBuffer.put((byte)1);
		byteBuffer.putInt(this.getObjectName().hashCode());
		byteBuffer.put((byte)this.dir.index());
		byteBuffer.put((byte)4);
		for (int int1 = 0; int1 < 4; ++int1) {
			byteBuffer.putShort((short)this.plankHealth[int1]);
		}

		byteBuffer.putShort((short)this.metalHealth);
		byteBuffer.putShort((short)this.metalBarHealth);
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			for (int int1 = 0; int1 < 4; ++int1) {
				byteBuffer.putShort((short)this.plankHealth[int1]);
			}

			byteBuffer.putShort((short)this.metalHealth);
			byteBuffer.putShort((short)this.metalBarHealth);
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		if ("state".equals(string)) {
			int int1;
			for (int1 = 0; int1 < 4; ++int1) {
				this.plankHealth[int1] = byteBuffer.getShort();
			}

			this.metalHealth = byteBuffer.getShort();
			this.metalBarHealth = byteBuffer.getShort();
			this.chooseSprite();
			if (this.square != null) {
				this.square.RecalcProperties();
			}

			for (int1 = 0; int1 < IsoPlayer.numPlayers; ++int1) {
				LosUtil.cachecleared[int1] = true;
			}

			IsoGridSquare.setRecalcLightTime(-1);
			GameTime.instance.lightSourceUpdate = 100.0F;
		}
	}

	public BarricadeAble getBarricadedObject() {
		int int1 = this.getSpecialObjectIndex();
		if (int1 == -1) {
			return null;
		} else {
			ArrayList arrayList = this.getSquare().getSpecialObjects();
			boolean boolean1;
			int int2;
			if (this.getDir() != IsoDirections.W && this.getDir() != IsoDirections.N) {
				if (this.getDir() == IsoDirections.E || this.getDir() == IsoDirections.S) {
					boolean1 = this.getDir() == IsoDirections.S;
					int2 = this.getSquare().getX() + (this.getDir() == IsoDirections.E ? 1 : 0);
					int int3 = this.getSquare().getY() + (this.getDir() == IsoDirections.S ? 1 : 0);
					IsoGridSquare square = this.getCell().getGridSquare((double)int2, (double)int3, (double)this.getZ());
					if (square != null) {
						arrayList = square.getSpecialObjects();
						for (int int4 = arrayList.size() - 1; int4 >= 0; --int4) {
							IsoObject object = (IsoObject)arrayList.get(int4);
							if (object instanceof BarricadeAble && boolean1 == ((BarricadeAble)object).getNorth()) {
								return (BarricadeAble)object;
							}
						}
					}
				}
			} else {
				boolean1 = this.getDir() == IsoDirections.N;
				for (int2 = int1 - 1; int2 >= 0; --int2) {
					IsoObject object2 = (IsoObject)arrayList.get(int2);
					if (object2 instanceof BarricadeAble && boolean1 == ((BarricadeAble)object2).getNorth()) {
						return (BarricadeAble)object2;
					}
				}
			}

			return null;
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1) {
		int int1 = IsoCamera.frameState.playerIndex;
		BarricadeAble barricadeAble = this.getBarricadedObject();
		if (barricadeAble != null && this.square.lighting[int1].targetDarkMulti() <= barricadeAble.getSquare().lighting[int1].targetDarkMulti()) {
			colorInfo = barricadeAble.getSquare().lighting[int1].lightInfo();
			this.targetAlpha[int1] = ((IsoObject)barricadeAble).targetAlpha[int1];
		}

		super.render(float1, float2, float3, colorInfo, boolean1);
	}

	public static IsoBarricade GetBarricadeOnSquare(IsoGridSquare square, IsoDirections directions) {
		if (square == null) {
			return null;
		} else {
			for (int int1 = 0; int1 < square.getSpecialObjects().size(); ++int1) {
				IsoObject object = (IsoObject)square.getSpecialObjects().get(int1);
				if (object instanceof IsoBarricade) {
					IsoBarricade barricade = (IsoBarricade)object;
					if (barricade.getDir() == directions) {
						return barricade;
					}
				}
			}

			return null;
		}
	}

	public static IsoBarricade GetBarricadeForCharacter(BarricadeAble barricadeAble, IsoGameCharacter gameCharacter) {
		if (barricadeAble != null && barricadeAble.getSquare() != null) {
			if (gameCharacter != null) {
				if (barricadeAble.getNorth()) {
					if (gameCharacter.getY() < (float)barricadeAble.getSquare().getY()) {
						return GetBarricadeOnSquare(barricadeAble.getOppositeSquare(), barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E);
					}
				} else if (gameCharacter.getX() < (float)barricadeAble.getSquare().getX()) {
					return GetBarricadeOnSquare(barricadeAble.getOppositeSquare(), barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E);
				}
			}

			return GetBarricadeOnSquare(barricadeAble.getSquare(), barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W);
		} else {
			return null;
		}
	}

	public static IsoBarricade GetBarricadeOppositeCharacter(BarricadeAble barricadeAble, IsoGameCharacter gameCharacter) {
		if (barricadeAble != null && barricadeAble.getSquare() != null) {
			if (gameCharacter != null) {
				if (barricadeAble.getNorth()) {
					if (gameCharacter.getY() < (float)barricadeAble.getSquare().getY()) {
						return GetBarricadeOnSquare(barricadeAble.getSquare(), barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W);
					}
				} else if (gameCharacter.getX() < (float)barricadeAble.getSquare().getX()) {
					return GetBarricadeOnSquare(barricadeAble.getSquare(), barricadeAble.getNorth() ? IsoDirections.N : IsoDirections.W);
				}
			}

			return GetBarricadeOnSquare(barricadeAble.getOppositeSquare(), barricadeAble.getNorth() ? IsoDirections.S : IsoDirections.E);
		} else {
			return null;
		}
	}

	public static IsoBarricade AddBarricadeToObject(BarricadeAble barricadeAble, boolean boolean1) {
		IsoGridSquare square = boolean1 ? barricadeAble.getOppositeSquare() : barricadeAble.getSquare();
		IsoDirections directions = null;
		if (barricadeAble.getNorth()) {
			directions = boolean1 ? IsoDirections.S : IsoDirections.N;
		} else {
			directions = boolean1 ? IsoDirections.E : IsoDirections.W;
		}

		if (square != null && directions != null) {
			IsoBarricade barricade = GetBarricadeOnSquare(square, directions);
			if (barricade != null) {
				return barricade;
			} else {
				barricade = new IsoBarricade(IsoWorld.instance.CurrentCell, square, directions);
				int int1 = -1;
				int int2;
				for (int2 = 0; int2 < square.getObjects().size(); ++int2) {
					IsoObject object = (IsoObject)square.getObjects().get(int2);
					if (object instanceof IsoCurtain) {
						IsoCurtain curtain = (IsoCurtain)object;
						if (curtain.getType() == IsoObjectType.curtainW && directions == IsoDirections.W) {
							int1 = int2;
						} else if (curtain.getType() == IsoObjectType.curtainN && directions == IsoDirections.N) {
							int1 = int2;
						} else if (curtain.getType() == IsoObjectType.curtainE && directions == IsoDirections.E) {
							int1 = int2;
						} else if (curtain.getType() == IsoObjectType.curtainS && directions == IsoDirections.S) {
							int1 = int2;
						}

						if (int1 != -1) {
							break;
						}
					}
				}

				square.AddSpecialObject(barricade, int1);
				for (int2 = 0; int2 < IsoPlayer.numPlayers; ++int2) {
					LosUtil.cachecleared[int2] = true;
				}

				IsoGridSquare.setRecalcLightTime(-1);
				GameTime.instance.lightSourceUpdate = 100.0F;
				return barricade;
			}
		} else {
			return null;
		}
	}

	public static IsoBarricade AddBarricadeToObject(BarricadeAble barricadeAble, IsoGameCharacter gameCharacter) {
		if (barricadeAble != null && barricadeAble.getSquare() != null && gameCharacter != null) {
			boolean boolean1;
			if (barricadeAble.getNorth()) {
				boolean1 = gameCharacter.getY() < (float)barricadeAble.getSquare().getY();
				return AddBarricadeToObject(barricadeAble, boolean1);
			} else {
				boolean1 = gameCharacter.getX() < (float)barricadeAble.getSquare().getX();
				return AddBarricadeToObject(barricadeAble, boolean1);
			}
		} else {
			return null;
		}
	}
}
