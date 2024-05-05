package zombie.iso.objects;

import java.io.IOException;
import java.nio.ByteBuffer;
import zombie.GameTime;
import zombie.GameWindow;
import zombie.SandboxOptions;
import zombie.WorldSoundManager;
import zombie.audio.BaseSoundEmitter;
import zombie.characters.IsoPlayer;
import zombie.core.Core;
import zombie.core.PerformanceSettings;
import zombie.core.math.PZMath;
import zombie.core.opengl.Shader;
import zombie.core.textures.ColorInfo;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoCell;
import zombie.iso.IsoGridSquare;
import zombie.iso.IsoObject;
import zombie.iso.IsoWorld;
import zombie.iso.sprite.IsoDirectionFrame;
import zombie.iso.sprite.IsoSprite;
import zombie.iso.sprite.IsoSpriteManager;
import zombie.network.GameServer;
import zombie.util.StringUtils;


public class IsoTrap extends IsoObject {
	private int timerBeforeExplosion = 0;
	private int FPS;
	private int sensorRange = 0;
	private int firePower = 0;
	private int fireRange = 0;
	private int explosionPower = 0;
	private int explosionRange = 0;
	private int smokeRange = 0;
	private int noiseRange = 0;
	private int noiseDuration = 0;
	private float noiseStartTime = 0.0F;
	private float lastWorldSoundTime = 0.0F;
	private float extraDamage = 0.0F;
	private int remoteControlID = -1;
	private String countDownSound = null;
	private String explosionSound = null;
	private int lastBeep = 0;
	private HandWeapon weapon;
	private boolean instantExplosion;

	public IsoTrap(IsoCell cell) {
		super(cell);
		this.FPS = GameServer.bServer ? 10 : PerformanceSettings.getLockFPS();
	}

	public IsoTrap(HandWeapon handWeapon, IsoCell cell, IsoGridSquare square) {
		this.square = square;
		this.initSprite(handWeapon);
		this.setSensorRange(handWeapon.getSensorRange());
		this.setFireRange(handWeapon.getFireRange());
		this.setFirePower(handWeapon.getFirePower());
		this.setExplosionPower(handWeapon.getExplosionPower());
		this.setExplosionRange(handWeapon.getExplosionRange());
		this.setSmokeRange(handWeapon.getSmokeRange());
		this.setNoiseRange(handWeapon.getNoiseRange());
		this.setNoiseDuration(handWeapon.getNoiseDuration());
		this.setExtraDamage(handWeapon.getExtraDamage());
		this.setRemoteControlID(handWeapon.getRemoteControlID());
		this.setCountDownSound(handWeapon.getCountDownSound());
		this.setExplosionSound(handWeapon.getExplosionSound());
		this.FPS = GameServer.bServer ? 10 : PerformanceSettings.getLockFPS();
		if (handWeapon.getExplosionTimer() > 0) {
			this.timerBeforeExplosion = handWeapon.getExplosionTimer() * this.FPS - 1;
		} else if (!handWeapon.canBeRemote()) {
			this.timerBeforeExplosion = 1;
		}

		if (handWeapon.canBePlaced()) {
			this.weapon = handWeapon;
		}

		this.instantExplosion = handWeapon.isInstantExplosion();
	}

	private void initSprite(HandWeapon handWeapon) {
		if (handWeapon != null) {
			String string;
			if (handWeapon.getPlacedSprite() != null && !handWeapon.getPlacedSprite().isEmpty()) {
				string = handWeapon.getPlacedSprite();
			} else if (handWeapon.getTex() != null && handWeapon.getTex().getName() != null) {
				string = handWeapon.getTex().getName();
			} else {
				string = "media/inventory/world/WItem_Sack.png";
			}

			this.sprite = IsoSprite.CreateSprite(IsoSpriteManager.instance);
			Texture texture = this.sprite.LoadFrameExplicit(string);
			if (string.startsWith("Item_") && texture != null) {
				if (handWeapon.getScriptItem() == null) {
					this.sprite.def.scaleAspect((float)texture.getWidthOrig(), (float)texture.getHeightOrig(), (float)(16 * Core.TileScale), (float)(16 * Core.TileScale));
				} else {
					float float1 = (float)Core.TileScale;
					float float2 = handWeapon.getScriptItem().ScaleWorldIcon * (float1 / 2.0F);
					this.sprite.def.setScale(float2, float2);
				}
			}
		}
	}

	public void update() {
		if (this.timerBeforeExplosion > 0) {
			if (this.timerBeforeExplosion / this.FPS + 1 != this.lastBeep) {
				this.lastBeep = this.timerBeforeExplosion / this.FPS + 1;
				if (!GameServer.bServer && this.getObjectIndex() != -1) {
					this.getOrCreateEmitter();
					if (!StringUtils.isNullOrWhitespace(this.getCountDownSound())) {
						this.emitter.playSound(this.getCountDownSound());
					} else if (this.lastBeep == 1) {
						this.emitter.playSound("TrapTimerExpired");
					} else {
						this.emitter.playSound("TrapTimerLoop");
					}
				}
			}

			--this.timerBeforeExplosion;
			if (this.timerBeforeExplosion == 0) {
				this.triggerExplosion(this.getSensorRange() > 0);
			}
		}

		this.updateSounds();
	}

	private void updateSounds() {
		if (this.noiseStartTime > 0.0F) {
			float float1 = (float)GameTime.getInstance().getWorldAgeHours();
			this.noiseStartTime = PZMath.min(this.noiseStartTime, float1);
			this.lastWorldSoundTime = PZMath.min(this.lastWorldSoundTime, float1);
			float float2 = 60.0F / (float)SandboxOptions.getInstance().getDayLengthMinutes();
			float float3 = 60.0F;
			if (float1 - this.noiseStartTime > (float)this.getNoiseDuration() / float3 * float2) {
				this.noiseStartTime = 0.0F;
				if (this.emitter != null) {
					this.emitter.stopAll();
				}
			} else {
				if (!GameServer.bServer && (this.emitter == null || !this.emitter.isPlaying(this.getExplosionSound()))) {
					BaseSoundEmitter baseSoundEmitter = this.getOrCreateEmitter();
					if (baseSoundEmitter != null) {
						baseSoundEmitter.playSound(this.getExplosionSound());
					}
				}

				if (float1 - this.lastWorldSoundTime > 1.0F / float3 * float2 && this.getObjectIndex() != -1) {
					this.lastWorldSoundTime = float1;
					WorldSoundManager.instance.addSoundRepeating((Object)null, this.getSquare().getX(), this.getSquare().getY(), this.getSquare().getZ(), this.getNoiseRange(), 1, true);
				}
			}
		}

		if (this.emitter != null) {
			this.emitter.tick();
		}
	}

	public void render(float float1, float float2, float float3, ColorInfo colorInfo, boolean boolean1, boolean boolean2, Shader shader) {
		if (this.sprite.CurrentAnim != null && !this.sprite.CurrentAnim.Frames.isEmpty()) {
			Texture texture = ((IsoDirectionFrame)this.sprite.CurrentAnim.Frames.get(0)).getTexture(this.dir);
			if (texture != null) {
				if (texture.getName().startsWith("Item_")) {
					float float4 = (float)texture.getWidthOrig() * this.sprite.def.getScaleX() / 2.0F;
					float float5 = (float)texture.getHeightOrig() * this.sprite.def.getScaleY() * 3.0F / 4.0F;
					this.setAlphaAndTarget(1.0F);
					this.offsetX = 0.0F;
					this.offsetY = 0.0F;
					this.sx = 0.0F;
					this.sprite.render(this, float1 + 0.5F, float2 + 0.5F, float3, this.dir, this.offsetX + float4, this.offsetY + float5, colorInfo, true);
				} else {
					this.offsetX = (float)(32 * Core.TileScale);
					this.offsetY = (float)(96 * Core.TileScale);
					this.sx = 0.0F;
					super.render(float1, float2, float3, colorInfo, boolean1, boolean2, shader);
				}
			}
		}
	}

	public void triggerExplosion(boolean boolean1) {
		if (boolean1) {
			if (this.getSensorRange() > 0) {
				this.square.setTrapPositionX(this.square.getX());
				this.square.setTrapPositionY(this.square.getY());
				this.square.setTrapPositionZ(this.square.getZ());
				this.square.drawCircleExplosion(this.getSensorRange(), this, IsoTrap.ExplosionMode.Sensor);
			}
		} else {
			if (this.getExplosionSound() != null) {
				this.playExplosionSound();
			}

			if (this.getNoiseRange() > 0) {
				WorldSoundManager.instance.addSound((Object)null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), this.getNoiseRange(), 1);
			} else if (this.getExplosionSound() != null) {
				WorldSoundManager.instance.addSound((Object)null, (int)this.getX(), (int)this.getY(), (int)this.getZ(), 50, 1);
			}

			if (this.getExplosionRange() > 0) {
				this.square.drawCircleExplosion(this.getExplosionRange(), this, IsoTrap.ExplosionMode.Explosion);
			}

			if (this.getFireRange() > 0) {
				this.square.drawCircleExplosion(this.getFireRange(), this, IsoTrap.ExplosionMode.Fire);
			}

			if (this.getSmokeRange() > 0) {
				this.square.drawCircleExplosion(this.getSmokeRange(), this, IsoTrap.ExplosionMode.Smoke);
			}

			if (this.weapon == null || !this.weapon.canBeReused()) {
				if (GameServer.bServer) {
					GameServer.RemoveItemFromMap(this);
				} else {
					this.removeFromWorld();
					this.removeFromSquare();
				}
			}
		}
	}

	private BaseSoundEmitter getOrCreateEmitter() {
		if (this.getObjectIndex() == -1) {
			return null;
		} else {
			if (this.emitter == null) {
				this.emitter = IsoWorld.instance.getFreeEmitter(this.getX() + 0.5F, this.getY() + 0.5F, this.getZ());
				IsoWorld.instance.takeOwnershipOfEmitter(this.emitter);
			}

			return this.emitter;
		}
	}

	public void playExplosionSound() {
		if (!StringUtils.isNullOrWhitespace(this.getExplosionSound())) {
			if (this.getObjectIndex() != -1) {
				if (this.getNoiseRange() > 0 && (float)this.getNoiseDuration() > 0.0F) {
					this.noiseStartTime = (float)GameTime.getInstance().getWorldAgeHours();
				}

				if (!GameServer.bServer) {
					this.getOrCreateEmitter();
					if (!this.emitter.isPlaying(this.getExplosionSound())) {
						this.emitter.playSoundImpl(this.getExplosionSound(), (IsoObject)null);
					}
				}
			}
		}
	}

	public void load(ByteBuffer byteBuffer, int int1, boolean boolean1) throws IOException {
		super.load(byteBuffer, int1, boolean1);
		this.sensorRange = byteBuffer.getInt();
		this.firePower = byteBuffer.getInt();
		this.fireRange = byteBuffer.getInt();
		this.explosionPower = byteBuffer.getInt();
		this.explosionRange = byteBuffer.getInt();
		this.smokeRange = byteBuffer.getInt();
		this.noiseRange = byteBuffer.getInt();
		if (int1 >= 180) {
			this.noiseDuration = byteBuffer.getInt();
			this.noiseStartTime = byteBuffer.getFloat();
		}

		this.extraDamage = byteBuffer.getFloat();
		this.remoteControlID = byteBuffer.getInt();
		if (int1 >= 78) {
			this.timerBeforeExplosion = byteBuffer.getInt() * this.FPS;
			this.countDownSound = GameWindow.ReadStringUTF(byteBuffer);
			this.explosionSound = GameWindow.ReadStringUTF(byteBuffer);
			if ("bigExplosion".equals(this.explosionSound)) {
				this.explosionSound = "BigExplosion";
			}

			if ("smallExplosion".equals(this.explosionSound)) {
				this.explosionSound = "SmallExplosion";
			}

			if ("feedback".equals(this.explosionSound)) {
				this.explosionSound = "NoiseTrapExplosion";
			}
		}

		if (int1 >= 82) {
			boolean boolean2 = byteBuffer.get() == 1;
			if (boolean2) {
				InventoryItem inventoryItem = InventoryItem.loadItem(byteBuffer, int1);
				if (inventoryItem instanceof HandWeapon) {
					this.weapon = (HandWeapon)inventoryItem;
					this.initSprite(this.weapon);
				}
			}
		}
	}

	public void save(ByteBuffer byteBuffer, boolean boolean1) throws IOException {
		super.save(byteBuffer, boolean1);
		byteBuffer.putInt(this.sensorRange);
		byteBuffer.putInt(this.firePower);
		byteBuffer.putInt(this.fireRange);
		byteBuffer.putInt(this.explosionPower);
		byteBuffer.putInt(this.explosionRange);
		byteBuffer.putInt(this.smokeRange);
		byteBuffer.putInt(this.noiseRange);
		byteBuffer.putInt(this.noiseDuration);
		byteBuffer.putFloat(this.noiseStartTime);
		byteBuffer.putFloat(this.extraDamage);
		byteBuffer.putInt(this.remoteControlID);
		byteBuffer.putInt(this.timerBeforeExplosion > 1 ? Math.max(this.timerBeforeExplosion / this.FPS, 1) : 0);
		GameWindow.WriteStringUTF(byteBuffer, this.countDownSound);
		GameWindow.WriteStringUTF(byteBuffer, this.explosionSound);
		if (this.weapon != null) {
			byteBuffer.put((byte)1);
			this.weapon.saveWithSize(byteBuffer, false);
		} else {
			byteBuffer.put((byte)0);
		}
	}

	public void addToWorld() {
		this.getCell().addToProcessIsoObject(this);
	}

	public void removeFromWorld() {
		if (this.emitter != null) {
			if (this.noiseStartTime > 0.0F) {
				this.emitter.stopAll();
			}

			IsoWorld.instance.returnOwnershipOfEmitter(this.emitter);
			this.emitter = null;
		}

		super.removeFromWorld();
	}

	public int getTimerBeforeExplosion() {
		return this.timerBeforeExplosion;
	}

	public void setTimerBeforeExplosion(int int1) {
		this.timerBeforeExplosion = int1;
	}

	public int getSensorRange() {
		return this.sensorRange;
	}

	public void setSensorRange(int int1) {
		this.sensorRange = int1;
	}

	public int getFireRange() {
		return this.fireRange;
	}

	public void setFireRange(int int1) {
		this.fireRange = int1;
	}

	public int getFirePower() {
		return this.firePower;
	}

	public void setFirePower(int int1) {
		this.firePower = int1;
	}

	public int getExplosionPower() {
		return this.explosionPower;
	}

	public void setExplosionPower(int int1) {
		this.explosionPower = int1;
	}

	public int getNoiseDuration() {
		return this.noiseDuration;
	}

	public void setNoiseDuration(int int1) {
		this.noiseDuration = int1;
	}

	public int getNoiseRange() {
		return this.noiseRange;
	}

	public void setNoiseRange(int int1) {
		this.noiseRange = int1;
	}

	public int getExplosionRange() {
		return this.explosionRange;
	}

	public void setExplosionRange(int int1) {
		this.explosionRange = int1;
	}

	public int getSmokeRange() {
		return this.smokeRange;
	}

	public void setSmokeRange(int int1) {
		this.smokeRange = int1;
	}

	public float getExtraDamage() {
		return this.extraDamage;
	}

	public void setExtraDamage(float float1) {
		this.extraDamage = float1;
	}

	public String getObjectName() {
		return "IsoTrap";
	}

	public int getRemoteControlID() {
		return this.remoteControlID;
	}

	public void setRemoteControlID(int int1) {
		this.remoteControlID = int1;
	}

	public String getCountDownSound() {
		return this.countDownSound;
	}

	public void setCountDownSound(String string) {
		this.countDownSound = string;
	}

	public String getExplosionSound() {
		return this.explosionSound;
	}

	public void setExplosionSound(String string) {
		this.explosionSound = string;
	}

	public InventoryItem getItem() {
		return this.weapon;
	}

	public static void triggerRemote(IsoPlayer player, int int1, int int2) {
		int int3 = (int)player.getX();
		int int4 = (int)player.getY();
		int int5 = (int)player.getZ();
		int int6 = Math.max(int5 - int2 / 2, 0);
		int int7 = Math.min(int5 + int2 / 2, 8);
		IsoCell cell = IsoWorld.instance.CurrentCell;
		for (int int8 = int6; int8 < int7; ++int8) {
			for (int int9 = int4 - int2; int9 < int4 + int2; ++int9) {
				for (int int10 = int3 - int2; int10 < int3 + int2; ++int10) {
					IsoGridSquare square = cell.getGridSquare(int10, int9, int8);
					if (square != null) {
						for (int int11 = square.getObjects().size() - 1; int11 >= 0; --int11) {
							IsoObject object = (IsoObject)square.getObjects().get(int11);
							if (object instanceof IsoTrap && ((IsoTrap)object).getRemoteControlID() == int1) {
								((IsoTrap)object).triggerExplosion(false);
							}
						}
					}
				}
			}
		}
	}

	public boolean isInstantExplosion() {
		return this.instantExplosion;
	}

	public static enum ExplosionMode {

		Explosion,
		Fire,
		Smoke,
		Sensor;

		private static IsoTrap.ExplosionMode[] $values() {
			return new IsoTrap.ExplosionMode[]{Explosion, Fire, Smoke, Sensor};
		}
	}
}
