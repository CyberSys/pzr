package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import se.krka.kahlua.vm.KahluaTable;
import zombie.GameTime;
import zombie.SandboxOptions;
import zombie.Lua.LuaEventManager;
import zombie.core.Core;
import zombie.core.Rand;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoHeatSource;
import zombie.iso.IsoLightSource;
import zombie.iso.IsoMovingObject;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.SpriteDetails.IsoFlagType;
import zombie.iso.areas.SafeHouse;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteInstance;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.ui.TutorialManager;


public class IsoFire extends IsoObject {
	public int Age;
	public int Energy;
	public int Life;
	public int LifeStage;
	public int LifeStageDuration;
	public int LifeStageTimer;
	public int MaxLife;
	public int MinLife;
	public int SpreadDelay;
	public int SpreadTimer;
	public int numFlameParticles;
	public boolean perm;
	public boolean bSmoke;
	public IsoLightSource LightSource;
	public int LightRadius;
	public float LightOscillator;
	private IsoHeatSource heatSource;
	private float accum;

	public IsoFire(IsoCell cell) {
		super(cell);
		this.Age = 0;
		this.Energy = 0;
		this.MaxLife = 3000;
		this.MinLife = 800;
		this.perm = false;
		this.bSmoke = false;
		this.LightSource = null;
		this.LightRadius = 1;
		this.LightOscillator = 0.0F;
		this.accum = 0.0F;
	}

	public IsoFire(IsoCell cell, IsoGridSquare square) {
		super(cell);
		this.Age = 0;
		this.Energy = 0;
		this.MaxLife = 3000;
		this.MinLife = 800;
		this.perm = false;
		this.bSmoke = false;
		this.LightSource = null;
		this.LightRadius = 1;
		this.LightOscillator = 0.0F;
		this.accum = 0.0F;
		this.square = square;
		this.perm = true;
	}

	public String getObjectName() {
		return "Fire";
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		ArrayList arrayList = this.AttachedAnimSprite;
		this.AttachedAnimSprite = null;
		super.save(byteBuffer, boolean1);
		this.AttachedAnimSprite = arrayList;
		this.sprite = null;
		byteBuffer.putInt(this.Life);
		byteBuffer.putInt(this.SpreadDelay);
		byteBuffer.putInt(this.LifeStage - 1);
		byteBuffer.putInt(this.LifeStageTimer);
		byteBuffer.putInt(this.LifeStageDuration);
		byteBuffer.putInt(this.Energy);
		byteBuffer.putInt(this.numFlameParticles);
		byteBuffer.putInt(this.SpreadTimer);
		byteBuffer.putInt(this.Age);
		byteBuffer.put((byte)(this.perm ? 1 : 0));
		byteBuffer.put((byte)this.LightRadius);
		byteBuffer.put((byte)(this.bSmoke ? 1 : 0));
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.sprite = null;
		this.Life = byteBuffer.getInt();
		this.SpreadDelay = byteBuffer.getInt();
		this.LifeStage = byteBuffer.getInt();
		this.LifeStageTimer = byteBuffer.getInt();
		this.LifeStageDuration = byteBuffer.getInt();
		this.Energy = byteBuffer.getInt();
		this.numFlameParticles = byteBuffer.getInt();
		this.SpreadTimer = byteBuffer.getInt();
		this.Age = byteBuffer.getInt();
		this.perm = byteBuffer.get() == 1;
		this.LightRadius = byteBuffer.get() & 255;
		if (int1 >= 89) {
			this.bSmoke = byteBuffer.get() == 1;
		}

		if (this.perm) {
			this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -78, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
		} else {
			if (this.numFlameParticles == 0) {
				this.numFlameParticles = 1;
			}

			label48: switch (this.LifeStage) {
			case -1: 
				this.LifeStage = 0;
				int int2 = 0;
				while (true) {
					if (int2 >= this.numFlameParticles) {
						break label48;
					}

					this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16 + -16 + Rand.Next(32), -85 + -16 + Rand.Next(32), true, 0, false, 0.7F, IsoFireManager.FireTintMod);
					++int2;
				}

			
			case 0: 
				this.LifeStage = 1;
				this.LifeStageTimer = this.LifeStageDuration;
				this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				break;
			
			case 1: 
				this.LifeStage = 2;
				this.LifeStageTimer = this.LifeStageDuration;
				this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
				this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				break;
			
			case 2: 
				this.LifeStage = 3;
				this.LifeStageTimer = this.LifeStageDuration / 3;
				this.RemoveAttachedAnims();
				this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
				this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				break;
			
			case 3: 
				this.LifeStage = 4;
				this.LifeStageTimer = this.LifeStageDuration / 3;
				this.RemoveAttachedAnims();
				if (this.bSmoke) {
					this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
				} else {
					this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
					this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				}

				break;
			
			case 4: 
				this.LifeStage = 5;
				this.LifeStageTimer = this.LifeStageDuration / 3;
				this.RemoveAttachedAnims();
				this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
			
			}

			if (this.square != null) {
				if (this.LifeStage < 4) {
					this.square.getProperties().Set(IsoFlagType.burning);
				} else {
					this.square.getProperties().Set(IsoFlagType.smoke);
				}
			}
		}
	}

	public IsoFire(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1, int int2, boolean boolean2) {
		this.Age = 0;
		this.Energy = 0;
		this.MaxLife = 3000;
		this.MinLife = 800;
		this.perm = false;
		this.bSmoke = false;
		this.LightSource = null;
		this.LightRadius = 1;
		this.LightOscillator = 0.0F;
		this.accum = 0.0F;
		this.square = square;
		this.DirtySlice();
		this.square.getProperties().Set(IsoFlagType.smoke);
		this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
		this.Life = this.MinLife + Rand.Next(this.MaxLife - this.MinLife);
		if (int2 > 0) {
			this.Life = int2;
		}

		this.LifeStage = 4;
		this.LifeStageTimer = this.LifeStageDuration = this.Life / 4;
		this.Energy = int1;
		this.bSmoke = boolean2;
	}

	public IsoFire(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1, int int2) {
		this.Age = 0;
		this.Energy = 0;
		this.MaxLife = 3000;
		this.MinLife = 800;
		this.perm = false;
		this.bSmoke = false;
		this.LightSource = null;
		this.LightRadius = 1;
		this.LightOscillator = 0.0F;
		this.accum = 0.0F;
		this.square = square;
		this.DirtySlice();
		this.numFlameParticles = 2 + Rand.Next(2);
		for (int int3 = 0; int3 < this.numFlameParticles; ++int3) {
			this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16 + -16 + Rand.Next(32), -85 + -16 + Rand.Next(32), true, 0, false, 0.7F, IsoFireManager.FireTintMod);
		}

		this.Life = this.MinLife + Rand.Next(this.MaxLife - this.MinLife);
		if (int2 > 0) {
			this.Life = int2;
		}

		if (this.square.getProperties() != null && !this.square.getProperties().Is(IsoFlagType.vegitation) && this.square.getFloor() != null) {
			this.Life -= this.square.getFloor().getSprite().firerequirement * 100;
			if (this.Life < 600) {
				this.Life = Rand.Next(300, 600);
			}
		}

		this.SpreadDelay = this.SpreadTimer = Rand.Next(this.Life - this.Life / 2);
		this.LifeStage = 0;
		this.LifeStageTimer = this.LifeStageDuration = this.Life / 4;
		if (TutorialManager.instance.Active) {
			this.LifeStageDuration *= 2;
			this.Life *= 2;
		}

		if (TutorialManager.instance.Active) {
			this.SpreadDelay = this.SpreadTimer /= 4;
		}

		square.getProperties().Set(IsoFlagType.burning);
		this.Energy = int1;
		if (this.square.getProperties().Is(IsoFlagType.vegitation)) {
			this.Energy += 50;
		}

		LuaEventManager.triggerEvent("OnNewFire", this);
	}

	public IsoFire(IsoCell cell, IsoGridSquare square, boolean boolean1, int int1) {
		this(cell, square, boolean1, int1, 0);
	}

	public static boolean CanAddSmoke(IsoGridSquare square, boolean boolean1) {
		return CanAddFire(square, boolean1, true);
	}

	public static boolean CanAddFire(IsoGridSquare square, boolean boolean1) {
		return CanAddFire(square, boolean1, false);
	}

	public static boolean CanAddFire(IsoGridSquare square, boolean boolean1, boolean boolean2) {
		if (!boolean2 && (GameServer.bServer || GameClient.bClient) && ServerOptions.instance.NoFire.getValue()) {
			return false;
		} else if (square != null && !square.getObjects().isEmpty()) {
			if (square.Is(IsoFlagType.water)) {
				return false;
			} else if (!boolean1 && square.getProperties().Is(IsoFlagType.burntOut)) {
				return false;
			} else if (!square.getProperties().Is(IsoFlagType.burning) && !square.getProperties().Is(IsoFlagType.smoke)) {
				if (!boolean1 && !Fire_IsSquareFlamable(square)) {
					return false;
				} else {
					return boolean2 || !GameServer.bServer && !GameClient.bClient || SafeHouse.getSafeHouse(square) == null || ServerOptions.instance.SafehouseAllowFire.getValue();
				}
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean Fire_IsSquareFlamable(IsoGridSquare square) {
		return !square.getProperties().Is(IsoFlagType.unflamable);
	}

	public boolean HasTooltip() {
		return false;
	}

	public void Spread() {
		if (!GameClient.bClient) {
			if (SandboxOptions.instance.FireSpread.getValue()) {
				if (this.getCell() != null) {
					if (this.square != null) {
						if (this.LifeStage < 4) {
							IsoGridSquare square = null;
							int int1 = Rand.Next(3) + 1;
							if (Rand.Next(50) == 0) {
								int1 += 15;
							}

							if (TutorialManager.instance.Active) {
								int1 += 15;
							}

							for (int int2 = 0; int2 < int1; ++int2) {
								int int3 = Rand.Next(13);
								switch (int3) {
								case 0: 
									square = this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ());
									break;
								
								case 1: 
									square = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY() - 1, this.square.getZ());
									break;
								
								case 2: 
									square = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY(), this.square.getZ());
									break;
								
								case 3: 
									square = this.getCell().getGridSquare(this.square.getX() + 1, this.square.getY() + 1, this.square.getZ());
									break;
								
								case 4: 
									square = this.getCell().getGridSquare(this.square.getX(), this.square.getY() + 1, this.square.getZ());
									break;
								
								case 5: 
									square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() + 1, this.square.getZ());
									break;
								
								case 6: 
									square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ());
									break;
								
								case 7: 
									square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() - 1, this.square.getZ());
									break;
								
								case 8: 
									square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY() - 1, this.square.getZ() - 1);
									break;
								
								case 9: 
									square = this.getCell().getGridSquare(this.square.getX() - 1, this.square.getY(), this.square.getZ() - 1);
									break;
								
								case 10: 
									square = this.getCell().getGridSquare(this.square.getX(), this.square.getY() - 1, this.square.getZ() - 1);
									break;
								
								case 11: 
									square = this.getCell().getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ() - 1);
									break;
								
								case 12: 
									square = this.getCell().getGridSquare(this.square.getX(), this.square.getY(), this.square.getZ() + 1);
								
								}

								if (CanAddFire(square, false)) {
									int int4 = this.getSquaresEnergyRequirement(square);
									if (this.Energy >= int4) {
										this.Energy -= int4;
										if (GameServer.bServer) {
											this.sendObjectChange("Energy");
										}

										if (RainManager.isRaining()) {
											return;
										}

										int int5 = square.getProperties().Is(IsoFlagType.exterior) ? this.Energy : int4 * 2;
										IsoFireManager.StartFire(this.getCell(), square, false, int5);
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public boolean TestCollide(IsoMovingObject movingObject, IsoGridSquare square) {
		return this.square == square;
	}

	public IsoObject.VisionResult TestVision(IsoGridSquare square, IsoGridSquare square2) {
		return IsoObject.VisionResult.NoEffect;
	}

	public void update() {
		if (this.getObjectIndex() != -1) {
			if (!GameServer.bServer) {
				IsoFireManager.updateSound(this);
			}

			if (this.LifeStage < 4) {
				this.square.getProperties().Set(IsoFlagType.burning);
			} else {
				this.square.getProperties().Set(IsoFlagType.smoke);
			}

			if (!this.bSmoke && this.LifeStage < 5) {
				this.square.BurnTick();
			}

			int int1 = this.AttachedAnimSprite.size();
			for (int int2 = 0; int2 < int1; ++int2) {
				IsoSpriteInstance spriteInstance = (IsoSpriteInstance)this.AttachedAnimSprite.get(int2);
				IsoSprite sprite = spriteInstance.parentSprite;
				spriteInstance.update();
				float float1 = GameTime.instance.getMultipliedSecondsSinceLastUpdate() * 60.0F;
				spriteInstance.Frame += spriteInstance.AnimFrameIncrease * float1;
				if ((int)spriteInstance.Frame >= sprite.CurrentAnim.Frames.size() && sprite.Loop && spriteInstance.Looped) {
					spriteInstance.Frame = 0.0F;
				}
			}

			if (!this.bSmoke && !GameServer.bServer && this.LightSource == null) {
				this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61F, 0.165F, 0.0F, this.perm ? this.LightRadius : 5);
				IsoWorld.instance.CurrentCell.addLamppost(this.LightSource);
			}

			if (this.perm) {
				if (this.heatSource == null) {
					this.heatSource = new IsoHeatSource(this.square.x, this.square.y, this.square.z, this.LightRadius, 35);
					IsoWorld.instance.CurrentCell.addHeatSource(this.heatSource);
				} else {
					this.heatSource.setRadius(this.LightRadius);
				}
			} else {
				this.accum += GameTime.getInstance().getMultiplier() / 1.6F;
				while (this.accum > 1.0F) {
					--this.accum;
					++this.Age;
					if (this.LifeStageTimer > 0) {
						--this.LifeStageTimer;
						if (this.LifeStageTimer <= 0) {
							switch (this.LifeStage) {
							case 0: 
								this.LifeStage = 1;
								this.LifeStageTimer = this.LifeStageDuration;
								this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
								this.square.Burn();
								if (this.LightSource != null) {
									this.setLightRadius(5);
								}

								break;
							
							case 1: 
								this.LifeStage = 2;
								this.LifeStageTimer = this.LifeStageDuration;
								this.RemoveAttachedAnims();
								this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
								this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
								this.square.Burn();
								if (this.LightSource != null) {
									this.setLightRadius(8);
								}

								break;
							
							case 2: 
								this.LifeStage = 3;
								this.LifeStageTimer = this.LifeStageDuration / 3;
								this.RemoveAttachedAnims();
								this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
								this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
								if (this.LightSource != null) {
									this.setLightRadius(12);
								}

								break;
							
							case 3: 
								this.LifeStage = 4;
								this.LifeStageTimer = this.LifeStageDuration / 3;
								this.RemoveAttachedAnims();
								this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
								this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
								if (this.LightSource != null) {
									this.setLightRadius(8);
								}

								break;
							
							case 4: 
								this.LifeStage = 5;
								this.LifeStageTimer = this.LifeStageDuration / 3;
								this.RemoveAttachedAnims();
								this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
								if (this.LightSource != null) {
									this.setLightRadius(1);
								}

							
							}
						}
					}

					if (this.Life > 0) {
						--this.Life;
						if (this.LifeStage > 0 && this.SpreadTimer > 0) {
							--this.SpreadTimer;
							if (this.SpreadTimer <= 0) {
								if (this.LifeStage != 5) {
									this.Spread();
								}

								this.SpreadTimer = this.SpreadDelay;
							}
						}

						if (this.Energy > 0) {
							continue;
						}

						this.extinctFire();
						break;
					}

					this.extinctFire();
					break;
				}
			}
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		float1 += 0.5F;
		float2 += 0.5F;
		this.sx = 0.0F;
		this.offsetX = 0.0F;
		this.offsetY = 0.0F;
		float float4 = (float)Core.TileScale;
		for (int int1 = 0; int1 < this.AttachedAnimSprite.size(); ++int1) {
			IsoSprite sprite = ((IsoSpriteInstance)this.AttachedAnimSprite.get(int1)).parentSprite;
			if (sprite != null && sprite.CurrentAnim != null && sprite.def != null) {
				Texture texture = ((IsoDirectionFrame)sprite.CurrentAnim.Frames.get((int)sprite.def.Frame)).directions[this.dir.index()];
				if (texture != null) {
					sprite.soffX = (short)((int)(-((float)(texture.getWidthOrig() / 2) * float4)));
					sprite.soffY = (short)((int)(-((float)texture.getHeightOrig() * float4)));
					((IsoSpriteInstance)this.AttachedAnimSprite.get(int1)).setScale(float4, float4);
				}
			}
		}

		super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
		if (Core.bDebug) {
		}
	}

	public void extinctFire() {
		this.square.getProperties().UnSet(IsoFlagType.burning);
		this.square.getProperties().UnSet(IsoFlagType.smoke);
		this.RemoveAttachedAnims();
		this.square.getObjects().remove(this);
		this.square.RemoveTileObject(this);
		this.setLife(0);
		this.removeFromWorld();
	}

	int getSquaresEnergyRequirement(IsoGridSquare square) {
		int int1 = 30;
		if (square.getProperties().Is(IsoFlagType.vegitation)) {
			int1 = -15;
		}

		if (!square.getProperties().Is(IsoFlagType.exterior)) {
			int1 = 40;
		}

		if (square.getFloor() != null && square.getFloor().getSprite() != null) {
			int1 = square.getFloor().getSprite().firerequirement;
		}

		return TutorialManager.instance.Active ? int1 / 4 : int1;
	}

	public void setSpreadDelay(int int1) {
		this.SpreadDelay = int1;
	}

	public int getSpreadDelay() {
		return this.SpreadDelay;
	}

	public void setLife(int int1) {
		this.Life = int1;
	}

	public int getLife() {
		return this.Life;
	}

	public int getEnergy() {
		return this.Energy;
	}

	public boolean isPermanent() {
		return this.perm;
	}

	public void setLifeStage(int int1) {
		if (this.perm) {
			this.RemoveAttachedAnims();
			switch (int1) {
			case 0: 
				this.AttachAnim("Fire", "01", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				break;
			
			case 1: 
				this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
				this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -9, -52, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				break;
			
			case 2: 
				this.AttachAnim("Smoke", "03", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
				this.AttachAnim("Fire", "03", 4, IsoFireManager.FireAnimDelay, -16, -72, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				break;
			
			case 3: 
				this.AttachAnim("Smoke", "02", 4, IsoFireManager.SmokeAnimDelay, 0, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
				this.AttachAnim("Fire", "02", 4, IsoFireManager.FireAnimDelay, -16, -85, true, 0, false, 0.7F, IsoFireManager.FireTintMod);
				break;
			
			case 4: 
				this.AttachAnim("Smoke", "01", 4, IsoFireManager.SmokeAnimDelay, -9, 12, true, 0, false, 0.7F, IsoFireManager.SmokeTintMod);
			
			}
		}
	}

	public void setLightRadius(int int1) {
		this.LightRadius = int1;
		if (this.LightSource != null && int1 != this.LightSource.getRadius()) {
			this.getCell().removeLamppost(this.LightSource);
			this.LightSource = new IsoLightSource(this.square.getX(), this.square.getY(), this.square.getZ(), 0.61F, 0.165F, 0.0F, this.LightRadius);
			this.getCell().getLamppostPositions().add(this.LightSource);
			IsoGridSquare.RecalcLightTime = -1;
			GameTime.instance.lightSourceUpdate = 100.0F;
		}
	}

	public int getLightRadius() {
		return this.LightRadius;
	}

	public void addToWorld() {
		if (this.perm) {
			this.getCell().addToStaticUpdaterObjectList(this);
		} else {
			IsoFireManager.Add(this);
		}
	}

	public void removeFromWorld() {
		if (!this.perm) {
			IsoFireManager.Remove(this);
		}

		IsoFireManager.stopSound(this);
		if (this.LightSource != null) {
			this.getCell().removeLamppost(this.LightSource);
			this.LightSource = null;
		}

		if (this.heatSource != null) {
			this.getCell().removeHeatSource(this.heatSource);
			this.heatSource = null;
		}

		super.removeFromWorld();
	}

	public void saveChange(String string, KahluaTable kahluaTable, ByteBuffer byteBuffer) {
		super.saveChange(string, kahluaTable, byteBuffer);
		if ("Energy".equals(string)) {
			byteBuffer.putInt(this.Energy);
		} else if ("lightRadius".equals(string)) {
			byteBuffer.putInt(this.getLightRadius());
		}
	}

	public void loadChange(String string, ByteBuffer byteBuffer) {
		super.loadChange(string, byteBuffer);
		if ("Energy".equals(string)) {
			this.Energy = byteBuffer.getInt();
		}

		if ("lightRadius".equals(string)) {
			int int1 = byteBuffer.getInt();
			this.setLightRadius(int1);
		}
	}

	public boolean isCampfire() {
		if (this.getSquare() == null) {
			return false;
		} else {
			IsoObject[] objectArray = (IsoObject[])this.getSquare().getObjects().getElements();
			int int1 = 1;
			for (int int2 = this.getSquare().getObjects().size(); int1 < int2; ++int1) {
				IsoObject object = objectArray[int1];
				if (!(object instanceof IsoWorldInventoryObject) && "Campfire".equalsIgnoreCase(object.getName())) {
					return true;
				}
			}

			return false;
		}
	}
}
